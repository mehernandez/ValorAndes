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
 * Servlet implementation class IntroConsultas
 */
public class IntroConsultas extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public IntroConsultas() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		Connection conn = null;
		Statement st = this.crearConexion(conn);
		try {
			ResultSet rs = st
					.executeQuery("select nombre, identidad from intermediarios");
			request.setAttribute("intermediarios", rs);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.cerrar(conn);
		// Aca se hace la logica
		String url = "/ConsultasFinales.jsp"; // relative url for display jsp
		// page
		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(url);
		rd.forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
