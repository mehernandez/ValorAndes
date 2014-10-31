package com.logic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Intermediar
 */
public class Intermediar extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Intermediar() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		int id = (Integer) request.getSession().getAttribute("id");
		String idx = request.getParameter("idOperacion");
		String[] ids = idx.split(":");
		String idOperacion = ids[0];
		String resp = "n";


		Connection conn = this.conectar();
		Statement st = null;
		try {
			st = conn.createStatement();
			conn.setAutoCommit(false);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		

		try {
			ResultSet rx = st.executeQuery("select entidad,tipooperacion,numerovalores,valor from operaciones where idoperacion="+idOperacion);
			rx.next();

			int idEntidad = rx.getInt("entidad");
			String tipo = rx.getString("tipooperacion");
			int numerox = rx.getInt("numerovalores");
			int numValores = numerox;
			int idValor = rx.getInt("valor");



			if(tipo.equalsIgnoreCase("venta")){
				int rs = st.executeUpdate("update pendientes set activa=1 where activa=0 and idoperacion="+idOperacion);
				if(rs==1){
					resp="si";
					conn.commit();
				}
			}
			else{
				ResultSet v = st.executeQuery("select idvalor,tipovalor,precio ,sum(cantidad) as suma "
						+ "from (select valores.idvalor ,valores.nombre,tiposvalor.nombre as tipovalor"
						+ ",pendientes.cantidad ,operaciones.idoperacion,activa,operaciones.entidad as inter"
						+ ",precio from valores,operaciones,pendientes,tiposvalor where valores.idvalor=valor "+
						"and pendientes.idoperacion=operaciones.idoperacion and activa=1 and tiposvalor.idtipovalor=valores.tipovalor "
						+ "and tipooperacion='VENTA' and entidad <>"+idEntidad+" and idvalor="+idValor+") group by idvalor, tipovalor,precio ");

				if(v.next()){
					int cantDisp = v.getInt("suma");
					System.out.println("La cantidad disponible es: "+cantDisp +" y numero de valores es :"+numValores);
					if(cantDisp >= numValores){

						ResultSet ch = st.executeQuery("select pendientes.idoperacion,pendientes.cantidad from operaciones,pendientes where operaciones.idoperacion=pendientes.idoperacion "+
								"and tipooperacion='VENTA' and activa=1 and valor="+idValor);
					     
						while(ch.next() && numValores >=1){
							System.out.println("Entre al segundo filtro");
							int cuant = ch.getInt("cantidad");
							int idOpp = ch.getInt("idoperacion");
							
//							Connection cox = this.conectar();
//							Statement st2 = null;
//							try {
//								st2 = conn.createStatement();
//								
//							} catch (SQLException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}
//							st2.executeQuery("select cantidad from pendientes where idoperacion="+idOpp+" for update");
							
							if(cuant > numValores ){
								int a = st.executeUpdate("update pendientes set cantidad=cantidad-"+numValores+" where "
										+ "idoperacion="+idOpp);
								if(a==1){
									int b = st.executeUpdate("delete from pendientes where idoperacion="+idOperacion);
									if(b==1){
										numValores=0;
									}
								}
							}
							else if(cuant == numValores){
								int a = st.executeUpdate("delete from pendientes where idoperacion="+idOpp);
								if(a==1){
									int b = st.executeUpdate("delete from pendientes where idoperacion="+idOperacion);
									if(b==1){
										numValores=0;
									}
								}
								
							}
							else if(cuant < numValores){
								
								int a = st.executeUpdate("delete from pendientes where idoperacion="+idOpp);
								if(a==1){
									numValores -= cuant;
								}
								
							}
//							this.cerrar(cox);
						}
						if(numValores==0){
							
							
							
							int d = st.executeUpdate("update portafolio set cantidad=cantidad+"+numerox+"where identidad="+idEntidad
									+" and idvalor="+idValor);
							if(d==0){
								d = st.executeUpdate("insert into portafolio values("+idEntidad+",+"+idValor+","+numerox+")");
							}
							if(d==1){
								System.out.println("La operacion termino con exito");
							resp="si";
							conn.commit();
							}else{
								conn.rollback();
							}
						}else{
							conn.rollback();
						}

					}
				}
				

			}
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		this.cerrar(conn);

		request.setAttribute("response", resp);
		String url = "/RespuestaOperacion.jsp";
		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(url);
		rd.forward(request, response);

	}

	public Statement crearConexion(Connection conn){

		try{
			Statement st = null;

			Class.forName("oracle.jdbc.OracleDriver");
			String dbURL = "jdbc:oracle:thin:@prod.oracle.virtual.uniandes.edu.co:1531:prod";
			String user = "ISIS2304161420";
			String pass = "entraros66151";
			conn = DriverManager.getConnection(dbURL, user, pass);
			if (conn != null) {
				return st =conn.createStatement();

			}else{
				System.out.println("VOY A RETORNAL NULLLLLLLLLLL");
				return null;
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public void cerrar(Connection conn){
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
