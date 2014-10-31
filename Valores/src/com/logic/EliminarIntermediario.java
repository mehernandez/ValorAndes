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
 * Servlet implementation class EliminarIntermediario
 */
public class EliminarIntermediario extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EliminarIntermediario() {
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
		String idx = request.getParameter("idIntermediario");
		String[] ids = idx.split(":");
		String idIntermediario = ids[0];

		Connection conn = null;
		Statement st = this.crearConexion(conn);
		//Chambonada
		
		try {
			

			Class.forName("oracle.jdbc.OracleDriver");
			String dbURL = "jdbc:oracle:thin:@prod.oracle.virtual.uniandes.edu.co:1531:prod";
			String user = "ISIS2304161420";
			String pass = "entraros66151";
			conn = DriverManager.getConnection(dbURL, user, pass);
			if (conn != null) {
				 st = conn.createStatement();

			} else {
				System.out.println("VOY A RETORNAL NULLLLLLLLLLL");
				
			}
			
			

			conn.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		//
		String resp = "NO";

		try {
			ResultSet rs = st
					.executeQuery("select pendientes.idoperacion,pendientes.cantidad,operaciones.tipooperacion,operaciones.valor as idvalor,entidad as identidad "
							+ "from operaciones,pendientes where operaciones.idoperacion=pendientes.idoperacion and intermediario="
							+ idIntermediario);
			boolean ya = true;
			while (rs.next() && ya) {
				int idOp = rs.getInt("idoperacion");
				int cantidad = rs.getInt("cantidad");
				String tipoOp = rs.getString("tipooperacion");
				int idValor = rs.getInt("idvalor");
				int idEntidad = rs.getInt("identidad");

				if (tipoOp.equalsIgnoreCase("COMPRA")) {
					int f = st
							.executeUpdate("delete from pendientes where idoperacion="
									+ idOp);
					if (f == 0) {
						ya = false;
					}
				} else {
					int f = st
							.executeUpdate("update portafolio set cantidad=cantidad+"
									+ cantidad
									+ " where identidad="
									+ idEntidad + " and idvalor=" + idValor);
					if (f == 1) {
						
						//Chambonada
						Connection cox = null;
						Statement st2 = null;
						try {
							

							Class.forName("oracle.jdbc.OracleDriver");
							String dbURL = "jdbc:oracle:thin:@prod.oracle.virtual.uniandes.edu.co:1531:prod";
							String user = "ISIS2304161420";
							String pass = "entraros66151";
							cox = DriverManager.getConnection(dbURL, user, pass);
							if (cox != null) {
								 st2 = cox.createStatement();

							} else {
								System.out.println("VOY A RETORNAL NULLLLLLLLLLL");
								
							}
							
							

						} catch (Exception e) {
							e.printStackTrace();
						}
						
						
						st2.executeQuery("select * from pendientes where idoperacion="+idOp+" for update");
						
						//
						int g = st
								.executeUpdate("delete from pendientes where idoperacion="
										+ idOp);
						this.cerrar(cox);
						if (g == 0) {
							ya = false;
						}
					} else {
						ya = false;
					}
				}
			}

			if (ya) {

				int h = st
						.executeUpdate("delete from entidades where identidad="
								+ idIntermediario);
				int b = st
						.executeUpdate("delete from intermediarios where identidad="
								+ idIntermediario);
				int k = st.executeUpdate("delete from usuarios where entidad="
						+ idIntermediario);
				if (h == 1 && b == 1 && k == 1) {
                    conn.commit();
					resp = "si";
				} else {
					conn.rollback();
				}
			}
			else{
				conn.rollback();
			}

		} catch (Exception e) {
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
