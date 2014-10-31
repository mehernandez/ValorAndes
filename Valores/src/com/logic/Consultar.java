package com.logic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Consultar
 */
public class Consultar extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Consultar() {
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
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String tipoVenta = request.getParameter("tipoConsulta");

		// Primera consulta
		if (tipoVenta.equalsIgnoreCase("valores")) {
			System.out.println("ENTRE A CONSULTAR VALORES ------------------");
			String valor = request.getParameter("valor");

			String tipoValor = request.getParameter("tipoValor");
			String tipoRentabilidad = request.getParameter("tipoRentabilidad");
			// System.out.println("El tipo rentabilidad es :  "+tipoRentabilidad);
			String fechaExpiracion = request.getParameter("fechaExpiracion");
			String idOferente = request.getParameter("idOferente");
			String disponible = request.getParameter("estaDisponible");
			// String idIntermediario = request.getParameter("idIntermediario");
			String idInversionista = request.getParameter("idInversionista");
			String orden = request.getParameter("orden");
			String ordenTipo = request.getParameter("ordenTipo");

			String where = " WHERE 1=1 ";
			String order = "";

			if (!disponible.equalsIgnoreCase("N/A")) {
				if (disponible.equalsIgnoreCase("SI")) {
					where += " AND NEGOCIANDO>0 ";
				} else {
					where += " AND NEGOCIANDO=0 ";
				}
			}
			if (!valor.isEmpty()) {
				try {
					Double.parseDouble(valor);
					where += " AND precio = " + valor + " ";
				} catch (Exception e) {

				}

			}

			if (!tipoValor.equals("N/A")) {
				where += " AND tipovalor='" + tipoValor + "' ";
			}
			if (!tipoRentabilidad.equals("N/A")) {
				where += " AND tipoRentabilidad='" + tipoRentabilidad + "' ";
			}

			if (!fechaExpiracion.isEmpty()) {
				try {
					Date d = new SimpleDateFormat("dd/MM/YYYY")
							.parse(fechaExpiracion.trim());

					where += " AND FECHA='" + fechaExpiracion + "' ";
				} catch (Exception e) {
					// error en escritura de la fecha
				}

			}
			if (!idOferente.isEmpty()) {

				where += " AND idOferente=" + idOferente + " ";
			}

			// if(!idIntermediario.isEmpty()){
			//
			// where += " AND idIntermediario="+idIntermediario+" ";
			// }

			if (!idInversionista.isEmpty()) {

				where += " AND idInversionista=" + idInversionista + " ";
			}

			if (!orden.equalsIgnoreCase("N/A")) {

				if (orden.equalsIgnoreCase("Precio")) {
					orden = "precio";
				}

				order += " ORDER BY " + orden + " ";
				if (ordenTipo.equalsIgnoreCase("Descendentemente")) {

					order += " DESC";
				}
			}

			System.out.println("El order es :  " + order);

			Connection conn = null;
			Statement st = this.crearConexion(conn);
			try {
				ResultSet rs = st
						.executeQuery("select * from (select idvalor,valor,precio,fecha,idoferente,oferente,tipovalor,tiporentabilidad,idinversionista,inversionista , "
								+ "(select count(*) from operaciones,pendientes where operaciones.idoperacion=pendientes.idoperacion and "
								+ "operaciones.valor=idvalor) as negociando "
								+ "from (select valores.idvalor,valores.NOMBRE as valor, valores.PRECIO as precio,valores.EXPIRACION as fecha , oferentes.IDENTIDAD as idOferente "
								+ ", oferentes.NOMBRE as oferente , tiposValor.NOMBRE as tipovalor , tiposrentabilidad.NOMBRE as tiporentabilidad from valores, "
								+ "oferentes,tiposvalor,TIPOSRENTABILIDAD where valores.oferente=oferentes.IDENTIDAD and tiposvalor.IDTIPOVALOR=valores.TIPOVALOR and "
								+ "TIPOSRENTABILIDAD.IDTIPORENTABILIDAD=valores.TIPORENTABILIDAD) left join (select portafolio.idvalor as idv2 , "
								+ "inversionistas.IDENTIDAD as idinversionista,inversionistas.NOMBRE as inversionista from portafolio,inversionistas where "
								+ "portafolio.IDENTIDAD=inversionistas.identidad) on idvalor = idv2) "
								+ where + order);
				request.setAttribute("result", rs);
				// while(rs.next()){
				// System.out.println(rs.getString("nombre"));
				// }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.cerrar(conn);
		}
		// segunda consulta
		else if (tipoVenta.equalsIgnoreCase("operaciones")) {
			// String tipoUsuario = request.getParameter("tipoUsuario");
			String tipoOperacion = request.getParameter("tipoOperacion");
			String fechaDesde = request.getParameter("fechaDesde");
			String fechaHasta = request.getParameter("fechaHasta");
			String costo = request.getParameter("costo");
			// String tipoValor = request.getParameter("tipoValor");
			String tipoRentabilidad = request.getParameter("tipoRentabilidad");

			String where = " WHERE 1=1 ";

			// if(!tipoUsuario.equalsIgnoreCase("N/A")){
			// where += " AND tipoUsuario='"+tipoUsuario+"'";
			// }
			if (!tipoOperacion.equalsIgnoreCase("N/A")) {
				where += " AND tipoOperacion='" + tipoOperacion + "'";
			}
			if (!costo.isEmpty()) {
				where += " AND monto=" + costo;
			}
			// if(!tipoValor.equals("N/A")){
			// where += " AND tipoValor='"+tipoValor+"'";
			// }
			if (!tipoRentabilidad.equals("N/A")) {
				where += " AND tipoRentabilidad='" + tipoRentabilidad + "'";
			}
			if (!fechaDesde.isEmpty() && !fechaHasta.isEmpty()) {
				try {
					Date d = new SimpleDateFormat("dd/MM/YYYY")
							.parse(fechaDesde.trim());
					Date x = new SimpleDateFormat("dd/MM/YYYY")
							.parse(fechaHasta.trim());

					where += " AND fecha BETWEEN '" + fechaDesde + "' AND '"
							+ fechaHasta + "' ";
				} catch (Exception e) {
					// error en escritura de la fecha
				}

			}

			Connection conn = null;
			Statement st = this.crearConexion(conn);
			try {
				ResultSet rs = st
						.executeQuery("select * from ( select valores.NOMBRE as valor ,operaciones.TIPOOPERACION as tipooperacion, operaciones.FECHA as fecha "
								+ ", operaciones.MONTO as monto, tiposrentabilidad.NOMBRE as tiporentabilidad , operaciones.NUMEROVALORES as cantidad,operaciones.IDOPERACION from operaciones "
								+ ", valores ,adquirido, tiposrentabilidad where operaciones.IDOPERACION = adquirido.IDOPERACION "
								+ "and valores.IDVALOR = adquirido.IDVALOR and valores.TIPORENTABILIDAD= tiposrentabilidad.IDTIPORENTABILIDAD) "
								+ where);
				request.setAttribute("result", rs);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.cerrar(conn);

		}

		// Tercera consulta
		else if (tipoVenta.equalsIgnoreCase("mayorMovimiento")) {

			System.out.println("Entre a mayor movimiento");
			String fechaDesde = request.getParameter("fechaDesde");
			String fechaHasta = request.getParameter("fechaHasta");

			System.out.println("fecha desde: " + fechaDesde);
			System.out.println("Fecha hasta :  " + fechaHasta);
			String where = " ";

			if (!fechaDesde.isEmpty() && !fechaHasta.isEmpty()) {
				try {
					Date d = new SimpleDateFormat("dd/MM/YYYY")
							.parse(fechaDesde.trim());
					Date x = new SimpleDateFormat("dd/MM/YYYY")
							.parse(fechaHasta.trim());

					where += " AND fecha BETWEEN '" + fechaDesde + "' AND '"
							+ fechaHasta + "' ";
				} catch (Exception e) {
					// error en escritura de la fecha
				}

			}

			Connection conn = null;
			Statement st = this.crearConexion(conn);
			try {
				ResultSet rs = st
						.executeQuery("select * from(select idvalor,valor,tiporentabilidad, count(idvalor) as negociado,avg(preciovalor) as costopromedio  from( select valores.idvalor, "
								+ "valores.nombre as valor,preciovalor ,tiposrentabilidad.nombre as tiporentabilidad "
								+ "from operaciones,valores,tiposrentabilidad where operaciones.valor=idvalor and tiposrentabilidad.IDTIPORENTABILIDAD=valores.TIPORENTABILIDAD "
								+ where
								+ ") group by idvalor, valor,tiporentabilidad) where rownum <=20 order by negociado desc,idvalor,valor ");
				request.setAttribute("result", rs);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.cerrar(conn);

		}

		// Cuarta consulta
		else if (tipoVenta.equalsIgnoreCase("masActivo")) {
			String tipoValor = request.getParameter("tipoValor");
			String idValor = request.getParameter("idValor");

			String query = "SELECT * FROM INTERMEDIARIOS WHERE 1=2";

			if (!tipoValor.equalsIgnoreCase("N/A")) {

				query = "select * from (select nombre,numeroregistro,ciudad,direccion,telefono,count(nombre) as operaciones "
						+ "from(select intermediarios.nombre,numeroregistro,ciudad,direccion,telefono from operaciones,intermediarios "
						+ "where operaciones.intermediario=intermediarios.IDENTIDAD and tipovalor='"
						+ tipoValor
						+ "'  ) group by nombre, numeroregistro, ciudad, direccion, telefono) "
						+ "where operaciones=(select max(ops)from (select nombre,numeroregistro,ciudad,direccion,telefono,count(nombre) as ops "
						+ "from(select intermediarios.nombre,numeroregistro,ciudad,direccion,telefono from operaciones,intermediarios "
						+ "where operaciones.intermediario=intermediarios.IDENTIDAD and tipovalor='"
						+ tipoValor
						+ "' ) group by nombre, numeroregistro, ciudad, direccion, telefono)) ";
			} else if (!idValor.isEmpty()) {
				query = "select * from (select nombre,numeroregistro,ciudad,direccion,telefono,count(nombre) as operaciones "
						+ "from(select intermediarios.nombre,numeroregistro,ciudad,direccion,telefono from operaciones,intermediarios "
						+ "where operaciones.intermediario=intermediarios.IDENTIDAD and valor="
						+ idValor
						+ " ) group by nombre, numeroregistro, ciudad, direccion, telefono) "
						+ "where operaciones=(select max(ops)from (select nombre,numeroregistro,ciudad,direccion,telefono,count(nombre) as ops "
						+ "from(select intermediarios.nombre,numeroregistro,ciudad,direccion,telefono from operaciones,intermediarios "
						+ "where operaciones.intermediario=intermediarios.IDENTIDAD and valor="
						+ idValor
						+ " ) group by nombre, numeroregistro, ciudad, direccion, telefono)) ";
			} else {
				// no se selecciono nada , hay un error
			}

			Connection conn = null;
			Statement st = this.crearConexion(conn);
			try {
				// System.out.println("el query es :   "+query);
				ResultSet rs = st.executeQuery(query);
				request.setAttribute("result", rs);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("EXCEPCION SQL  :  " + e.getMessage());
			}
			this.cerrar(conn);
		}

		// request.setAttribute("clave", clave);

		String url = "/RespuestaConsulta.jsp"; // relative url for display jsp
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

}
