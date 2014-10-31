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
 * Servlet implementation class Cancelar
 */
public class Cancelar extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Cancelar() {
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
		String id = request.getParameter("idOperacion");
		String tipo = request.getParameter("tipo");
		int myId = (Integer) request.getSession().getAttribute("id");
		
		boolean correcto = false;
		// chambonada
		Connection conn = null;
		Statement st = null;
		
		
		
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
			
			//st.executeQuery("select * from pendientes where idOperacion="+id+" for update");
			
			
			if (tipo.equalsIgnoreCase("venta")) {
				System.out.println("Entre a cancelar venta");
				ResultSet rf = st
						.executeQuery("select valor,pendientes.cantidad from "
								+ "operaciones,pendientes where operaciones.idoperacion=pendientes.idoperacion and pendientes.idoperacion="
								+ id);
				if (rf.next()) {
					int idValor = rf.getInt("valor");
					
					
					//Chambonada
//					Connection cox = null;
//					Statement st2 = null;
//					try {
//						
//
//						Class.forName("oracle.jdbc.OracleDriver");
//						String dbURL = "jdbc:oracle:thin:@prod.oracle.virtual.uniandes.edu.co:1531:prod";
//						String user = "ISIS2304161420";
//						String pass = "entraros66151";
//						cox = DriverManager.getConnection(dbURL, user, pass);
//						if (cox != null) {
//							 st2 = cox.createStatement();
//
//						} else {
//							System.out.println("VOY A RETORNAL NULLLLLLLLLLL");
//							
//						}
//						
//						
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					
//					
//					
//					st2.executeQuery("select cantidad from portafolio where idEntidad="+myId+" and idvalor="+idValor+" for update");
					
					//
					
					
					int r = st
							.executeUpdate("update portafolio set cantidad=cantidad+"
									+ rf.getInt("cantidad")
									+ " where identidad="
									+ myId
									+ " and idvalor=" + idValor);
					
					//this.cerrar(cox);
					if (r == 1) {
						int res = st
								.executeUpdate("delete from pendientes where idoperacion="
										+ id);
						if (res == 1) {
							resp = "si";
							conn.commit();
							correcto=true;
						}
					}
				}
			} else {
				System.out.println("Entre a cancelar compra");
				int res = st
						.executeUpdate("delete from pendientes where idoperacion="
								+ id);
				if (res == 1) {
					resp = "si";
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
		
		if(!correcto){
			try {
				conn.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
