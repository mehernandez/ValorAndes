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
 * Servlet implementation class ConsultarPortafolios
 */
public class ConsultarPortafolios extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConsultarPortafolios() {
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
		// TODO Auto-generated method stub
		String[] tipoValorx = request.getParameter("tipoValor").split(":");
		String tipoValor = tipoValorx[0];
		String cantidadValorS = request.getParameter("cantidadValor");
		String resp = "bien";
		
		
		// Aca se hace la logica
		Connection conn = null;
		Statement st = this.crearConexion(conn);
		try {
			int cantidadValor=Integer.parseInt(cantidadValorS);
			
			Long t1=System.currentTimeMillis();
			
			ResultSet rs = st
					.executeQuery("select * from (((select * from (select distinct entidad as identidad from operaciones where monto >"+cantidadValor+") natural join "+
"(select distinct identidad  from portafolio,Valores where portafolio.idvalor=valores.idvalor and  valores.tipoValor="+tipoValor+")) "+ 
"natural join (select identidad,portafolio.idvalor,nombre as valor,cantidad  from portafolio,valores where portafolio.idvalor = valores.idvalor)) "+
"natural join (select * from(select identidad , nombre as entidad from oferentes) union (select identidad , nombre as entidad from inversionistas)))");
			
			Long t2=System.currentTimeMillis();
			System.out.println("TIEMPO CONSULTA PORTAFOLIOS::::::::2"+(t2-t1));
			
			
			request.setAttribute("result", rs);
			request.setAttribute("tipo", "consultarPortafolios");


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp = "mal";
		}
		this.cerrar(conn);
		
		request.setAttribute("respuesta", resp);
		
		String url = "/RespuestaConsultasFinales.jsp"; // relative url for display jsp
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
