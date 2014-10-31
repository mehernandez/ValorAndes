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
 * Servlet implementation class Vender
 */
public class Vender extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Vender() {
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
		String idvalor = request.getParameter("idvalor");
		int cantidad = Integer.parseInt(request.getParameter("cantidad"));
		String intermediario = request.getParameter("intermediario");
		int id = (Integer) request.getSession().getAttribute("id");

		Connection conn = null;
		Statement st = this.crearConexion(conn);
		String resp = "n";
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
					ResultSet g = st
							.executeQuery("select identidad from intermediarios where nombre='"
									+ intermediario + "'");
					if (g.next()) {

						int r1 = 0;
						System.out
								.println("insert into operaciones values(seqoperaciones.nextval,"
										+ "sysdate,"
										+ (prec * cantidad)
										+ ","
										+ id
										+ ","
										+ g.getInt("identidad")
										+ ",'VENTA',"
										+ cantidad
										+ ","
										+ prec
										+ ",'" + tipVal + "'," + idvalor + ")");
						r1 = st.executeUpdate("insert into operaciones values(seqoperaciones.nextval,"
								+ "sysdate,"
								+ (prec * cantidad)
								+ ","
								+ id
								+ ","
								+ g.getInt("identidad")
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
									+ cantidad + ",0)");

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
									resp = "si";
									System.out
											.println("La operacion acabo con exito");
								}
							}
						}

					}

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.cerrar(conn);
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

}
