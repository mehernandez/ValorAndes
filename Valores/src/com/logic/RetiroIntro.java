package com.logic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EventObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class RetiroIntro
 */
public class RetiroIntro extends HttpServlet implements IEscuchadorEventos{
	private static final long serialVersionUID = 1L;
	private HttpServletRequest request;
	
	Conector conector;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RetiroIntro() {
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
		
		this.request = request;
		Connection conn = null;
		Statement st = this.crearConexion(conn);
		try {
			ResultSet rs = st
					.executeQuery("SELECT IDENTIDAD AS ID,NOMBRE,DIRECCION,CIUDAD,TELEFONO FROM INTERMEDIARIOS");
			request.setAttribute("result", rs);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.cerrar(conn);
		
		JsonObject json = new JsonObject();
		
		json.addProperty("method", "darIntermediarios");
		
		String j = new GsonBuilder().create().toJson(json);
		
		conector.enviarPregunta(j);
		
		try {
			Thread.sleep(10000);
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		
		

		String url = "/Retiro.jsp"; // relative url for display jsp page
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
	
	
	public void manejarEvento(EventObject e)  {
		System.out.println("empezo evento");
		
		
		String mensaje = ((MiEvento)e).getElMensaje();
		System.out.println("mensaje recibido: " + mensaje);
		request.setAttribute("externos", mensaje);
		
		
		
		
		System.out.println("Termino evento");
		
		Thread.currentThread().interrupt();
		}


	public void init(){
		try {
			conector = Conector.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conector.addEventListener(this);
	}

}
