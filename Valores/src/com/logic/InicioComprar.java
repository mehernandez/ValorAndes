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
 * Servlet implementation class InicioComprar
 */
public class InicioComprar extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InicioComprar() {
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
			System.out
					.println("query : "
							+ "select valores.idvalor ,valores.nombre,tiposrentabilidad.nombre as tiporentabilidad,pendientes.cantidad,tiposvalor.nombre as tipovalor,precio from valores,"
							+ "operaciones,pendientes,tiposrentabilidad,tiposvalor where valores.idvalor=valor "
							+ "and pendientes.idoperacion=operaciones.idoperacion and activa=1 and tiposrentabilidad.idtiporentabilidad=valores.tiporentabilidad "
							+ "and tiposvalor.idtipovalor=valores.tipovalor and tipooperacion='VENTA' and operaciones.entidad<>"
							+ request.getSession().getAttribute("id"));
			ResultSet rs = st
					.executeQuery("select valores.idvalor ,valores.nombre,tiposrentabilidad.nombre as tiporentabilidad,pendientes.cantidad,tiposvalor.nombre as tipovalor,precio from valores,"
							+ "operaciones,pendientes,tiposrentabilidad,tiposvalor where valores.idvalor=valor "
							+ "and pendientes.idoperacion=operaciones.idoperacion and activa=1 and tiposrentabilidad.idtiporentabilidad=valores.tiporentabilidad "
							+ "and tiposvalor.idtipovalor=valores.tipovalor and tipooperacion='VENTA' and operaciones.entidad<>"
							+ request.getSession().getAttribute("id"));

			request.setAttribute("t1", rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.cerrar(conn);

		Connection cox = null;
		Statement st2 = this.crearConexion(cox);

		try {
			ResultSet rs2 = st2
					.executeQuery("select operaciones.idoperacion,operaciones.fecha"
							+ ",monto,tipooperacion,numerovalores,tipovalor from operaciones,pendientes where pendientes.idoperacion=operaciones.idoperacion "
							+ "and tipooperacion='COMPRA' "
							+ "and entidad="
							+ request.getSession().getAttribute("id"));
			request.setAttribute("t2", rs2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.cerrar(cox);

		String url = "/Comprar.jsp"; // relative url for display jsp page
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
