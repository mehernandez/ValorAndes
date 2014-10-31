package com.logic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class PortafolioProcesar
 */
public class PortafolioProcesar extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PortafolioProcesar() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		Connection conn = null;
		Statement st = this.crearConexion(conn);
		try {
			System.out.println("/////////////////ESTE ES EL ID: "+request.getSession().getAttribute("id")+"//////////////////");
			ResultSet rs = st
					.executeQuery("select idvalor,nombre,cantidad from valores natural join portafolio where identidad="+request.getSession().getAttribute("id"));
			request.setAttribute("result", rs);

			//			ResultSet c=st.executeQuery("select sum(cantidad) as total from valores natural join portafolio where identidad="+request.getSession().getAttribute("id"));
			//			c.next();
			//			int cantidad= c.getInt("total");
			//			System.out.println("////////////////CANTIDAD: "+cantidad);
			//			request.setAttribute("cantidadTotal", cantidad);
			// while(rs.next()){
			// System.out.println(rs.getString("nombre"));
			// }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.cerrar(conn);





		String url = "/Portafolio.jsp"; // relative url for display jsp page
		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(url);
		rd.forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String pedido = request.getParameter("transaccion");
		boolean estado = false;
		int idvalor = Integer.parseInt(pedido.split(":")[0]);
		int cantidadNueva = Integer.parseInt(pedido.split(":")[1]);

		// query que pida la informacion del valor con ese id, especificamente
		// la cantidad actual



		Connection conn = this.conectar();
		Statement st = null;
		try {
			st = conn.createStatement();
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int cantidadActual = 0;

		try {
			ResultSet d = st.executeQuery("select cantidad from portafolio where idvalor="+idvalor
					+" and identidad="+(Integer)request.getSession().getAttribute("id"));
			if(d.next()){
				cantidadActual = d.getInt("cantidad");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.cerrar(conn);

		try {

			if ((cantidadNueva - cantidadActual) > 0) {
				System.out.println("debo comprar "+(cantidadNueva - cantidadActual));
				// Debo comprar! TRANSACCION
				estado =comprar((Integer)request.getSession().getAttribute("id"),idvalor,(cantidadNueva - cantidadActual));

			} else if ((cantidadNueva - cantidadActual) < 0) {
				// Debo vender ! TRANSACCION
				estado =vender((Integer)request.getSession().getAttribute("id"),idvalor,(cantidadActual - cantidadNueva));
			}
			
			
		} catch (Exception e) {
			// Si hay un error en la transaccion es fallido y no se pudo
			// modificar el portafolio
		}

		//prepararRequest(request, 0);
		String resp ="n";
		if(estado){
			
			resp="si";
		}
		
		request.setAttribute("response", resp);
		
		String url = "/RespuestaOperacion.jsp"; // relative url for display jsp
		// page
		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(url);
		rd.forward(request, response);
	}

	public Statement crearConexion(Connection conn) {

		try {
			Statement st = null;

			Class.forName("oracle.jdbc.OracleDriver");
			String dbURL = "jdbc:oracle:thin:@prod.oracle.virtual.uniandes.edu.co:1531:prod";
			String user = "ISIS2304161420";
			String pass = "entraros66151";
			conn = DriverManager.getConnection(dbURL, user, pass);
			if (conn != null) {
				return st = conn.createStatement();

			} else {
				System.out.println("VOY A RETORNAL NULLLLLLLLLLL");
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void cerrar(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public ArrayList<String> calcularPorcentajes(ResultSet result,int nCantidadTotal)
			throws NumberFormatException, SQLException {

		int cantidadTotal = nCantidadTotal;
		System.out.println("////////////CANTIDAD TOTAL: "+cantidadTotal);
		ArrayList<String> porcentajes = new ArrayList<String>();
		// Recorrido para asignar porcentajes al arraylist

		while (result.next()) {
			System.out.println(result.getString("cantidad"));
			porcentajes.add((((result.getInt("cantidad")) / cantidadTotal) * 100) + "");

		}
		return porcentajes;

	}

	public int darCantidadTotal(ResultSet result) throws NumberFormatException,
	SQLException {
		int cantidadTotal = 0;
		// Recorrido para calcular el total

		cantidadTotal += Integer.parseInt(result.getString("cantidad"));
		while (result.next()) {
			cantidadTotal += Integer.parseInt(result.getString("cantidad"));
		}
		return cantidadTotal;
	}

	//	public void prepararRequest(HttpServletRequest request, int idValor) {
	//
	//		// sesion
	//		int id = 0;
	//
	//		ArrayList<String> porcentajes = new ArrayList<String>();
	//
	//		Connection conn = null;
	//		Statement st = this.crearConexion(conn);
	//		try {
	//			ResultSet rs = st
	//					.executeQuery("SELECT valores.idvalor,valores.nombre,cantidad from valores join portafolio on valores.idvalor=portafolio.idvalor and identidad="
	//							+ id + " and valores.idvalor=" + idValor);
	//			porcentajes = calcularPorcentajes(rs);
	//			request.setAttribute("result", rs);
	//
	//		} catch (SQLException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		this.cerrar(conn);
	//
	//		request.setAttribute("porcentajes", porcentajes);
	//	}

	public boolean comprar(int id,int idvalor,int cantidad) {

		int numValores = cantidad;
		Connection conn = null;
		Statement st = this.crearConexion(conn);
		boolean resp = false;

		try{
			ResultSet v = st.executeQuery("select idvalor,tipovalor,precio ,sum(cantidad) as suma "
					+ "from (select valores.idvalor ,valores.nombre,tiposvalor.nombre as tipovalor"
					+ ",pendientes.cantidad ,operaciones.idoperacion,activa,operaciones.entidad as inter"
					+ ",precio from valores,operaciones,pendientes,tiposvalor where valores.idvalor=valor "+
					"and pendientes.idoperacion=operaciones.idoperacion and activa=1 and tiposvalor.idtipovalor=valores.tipovalor "
					+ "and tipooperacion='VENTA' and entidad <>"+id+" and idvalor="+idvalor+") group by idvalor, tipovalor,precio ");

			if(v.next()){
				int cantDisp = v.getInt("suma");
				System.out.println("La cantidad disponible es: "+cantDisp +" y numero de valores es :"+cantidad);
				if(cantDisp >= cantidad){

					ResultSet ch = st.executeQuery("select pendientes.idoperacion,pendientes.cantidad from operaciones,pendientes where operaciones.idoperacion=pendientes.idoperacion "+
							"and tipooperacion='VENTA' and activa=1 and valor="+idvalor);

					while(ch.next() && numValores >=1){
						System.out.println("Entre al segundo filtro");
						int cuant = ch.getInt("cantidad");
						int idOpp = ch.getInt("idoperacion");
						if(cuant > numValores ){
							int a = st.executeUpdate("update pendientes set cantidad=cantidad-"+numValores+" where "
									+ "idoperacion="+idOpp);
							if(a==1){

								numValores=0;

							}
						}
						else if(cuant == numValores){
							int a = st.executeUpdate("delete from pendientes where idoperacion="+idOpp);
							if(a==1){

								numValores=0;

							}

						}
						else if(cuant < numValores){

							int a = st.executeUpdate("delete from pendientes where idoperacion="+idOpp);
							if(a==1){
								numValores -= cuant;
							}

						}
					}
					if(numValores==0){
						int d = st.executeUpdate("update portafolio set cantidad=cantidad+"+cantidad+"where identidad="+id
								+" and idvalor="+idvalor);
						if(d==0){
							d = st.executeUpdate("insert into portafolio values("+id+",+"+idvalor+","+cantidad+")");
						}
						if(d==1){
							System.out.println("La operacion termino con exito");
							resp=true;
						}
					}

				}
			}} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		if(resp){
			try {
				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				conn.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		this.cerrar(conn);
		return resp;

	}

	public boolean vender(int id,int idvalor,int cantidad) {


		Connection conn = this.conectar();
		Statement st = null;
		try {
			st = conn.createStatement();
			conn.setAutoCommit(false);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean resp = false;
		try {
			ResultSet rs = st
					.executeQuery("select cantidad ,precio,tiposvalor.nombre as tipovalor from portafolio,valores,tiposrentabilidad,"
							+ "tiposvalor where portafolio.IDVALOR=valores.idvalor "
							+ "and valores.tiporentabilidad=tiposRentabilidad.idtiporentabilidad and valores.tipovalor=tiposvalor.idtipovalor "
							+ "and valores.idvalor="
							+ idvalor
							+ " and portafolio.identidad=" + id);

			if (rs.next()) {
				int cant = rs.getInt("cantidad");
				int prec = rs.getInt("precio");
				String tipVal = rs.getString("tipovalor");
				if (cant >= cantidad) {

					int r1 = 0;

					r1 = st.executeUpdate("insert into operaciones values(seqoperaciones.nextval,"
							+ "sysdate,"
							+ (prec * cantidad)
							+ ","
							+ id
							+ ","
							+ 1
							+ ",'VENTA',"
							+ cantidad
							+ ","
							+ prec
							+ ",'"
							+ tipVal
							+ "',"
							+ idvalor + ")");

					int r2 = 0;
					if (r1 == 1) {
						r2 = st.executeUpdate("insert into pendientes values (seqoperaciones.CURRVAL,"
								+ cantidad + ",1)");

						if (r2 == 1) {
							ResultSet f = st
									.executeQuery("select seqoperaciones.currval from dual");
							f.next();

							int r3 = st
									.executeUpdate("update portafolio set cantidad=cantidad-"
											+ cantidad
											+ " where"
											+ " identidad="
											+ id
											+ " and idvalor=" + idvalor);
							if (r3 == 1) {
								resp = true;
								System.out
								.println("La operacion acabo con exito");
							}
						}
					}



				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(resp){
			try {
				conn.commit();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			try {
				conn.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.cerrar(conn);
		return resp;
	}
	
	public Connection conectar(){
	     Connection conn = null;
			try{
				
				Statement st = null;

				Class.forName("oracle.jdbc.OracleDriver");
				String dbURL = "jdbc:oracle:thin:@prod.oracle.virtual.uniandes.edu.co:1531:prod";
				String user = "ISIS2304161420";
				String pass = "entraros66151";
				conn = DriverManager.getConnection(dbURL, user, pass);
				if (conn != null) {
					return conn;

				}else{
					System.out.println("VOY A RETORNAL NULLLLLLLLLLL");
					return null;
				}

			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}

}
