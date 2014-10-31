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
 * Servlet implementation class InicioVender
 */
public class InicioVender extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InicioVender() {
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
			ResultSet rs = st
					.executeQuery("select valores.idvalor,valores.nombre,tiposrentabilidad.nombre as tiporentabilidad,tiposvalor.nombre as tipovalor "
							+ ",precio,cantidad from portafolio,valores,tiposrentabilidad,tiposvalor where portafolio.IDVALOR=valores.idvalor "
							+ "and valores.tiporentabilidad=tiposRentabilidad.idtiporentabilidad and valores.tipovalor=tiposvalor.idtipovalor and "
							+ "portafolio.identidad="
							+ request.getSession().getAttribute("id"));

			request.setAttribute("tabla", rs);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.cerrar(conn);

		Connection cox = null;
		Statement st2 = this.crearConexion(cox);

		ResultSet rs2;
		try {
			rs2 = st2
					.executeQuery("select operaciones.idoperacion,operaciones.fecha"
							+ ",monto,tipooperacion,numerovalores,tipovalor from operaciones,pendientes where pendientes.idoperacion=operaciones.idoperacion "
							+ "and tipooperacion='VENTA' "
							+ "and entidad="
							+ request.getSession().getAttribute("id"));
			request.setAttribute("t2", rs2);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.cerrar(cox);

		String url = "/Vender.jsp"; // relative url for display jsp page
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
