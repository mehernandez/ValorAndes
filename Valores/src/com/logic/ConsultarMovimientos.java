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
 * Servlet implementation class ConsultarMovimientos
 */
public class ConsultarMovimientos extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ConsultarMovimientos() {
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

		String fechainicial = request.getParameter("fechaInicial");
		String fechaFinal = request.getParameter("fechaFinal");
		String[] tipoValorx = request.getParameter("tipoValor").split(":");
		String tipoValor = tipoValorx[0];
		String monto = request.getParameter("monto");
		String[] idIntermediariox = request.getParameter("idIntermediario").split(":");
		String idIntermediario = idIntermediariox[0];
		String incluir = request.getParameter("group1");

		//los que faltan

		String[] oferentex = request.getParameter("oferente").split(":");
		String oferente = oferentex[0];
		String[] inversionistax = request.getParameter("inversionista").split(":");
		String inversionista = inversionistax[0];


		String where = " where 1=1 ";

		String operador = "=";

		String resp = "mal";
		// Aca se hace la logica
		Connection conn = null;
		Statement st = this.crearConexion(conn);
		try {

			if(!fechainicial.isEmpty() && !fechaFinal.isEmpty() && !(!oferente.equals("N/A") && !inversionista.equals("N/A"))){



				if(incluir.equals("no")){
					operador = "<>";
				}
				if(!monto.equals("N/A")){
					String[] montox = monto.split("-");
					if(operador.equals("=")){
						where += " and monto >="+montox[0]+" and monto <="+montox[1]+" ";
					}
					else{
						where += " and (monto <"+montox[0]+" or monto >"+montox[1]+") ";	
					}
				}
				if(!inversionista.equals("N/A")){
					where += "and enti ="+inversionista+" ";
				}
				else if(!oferente.equals("N/A")){
					where += "and enti ="+oferente+" ";
				}
				if(!tipoValor.isEmpty()){
					where += " and tipVal"+operador+"'"+tipoValor+"' ";
				}
				if(!monto.isEmpty()){
					where += " and monto"+operador+monto+" ";
				}
				if(!idIntermediario.isEmpty()){
					where += "and intermediario"+operador+idIntermediario+" ";
				}



				ResultSet rs = st
						.executeQuery("select * from (SELECT fecha,monto, intermediario ,tipoOperacion,numerovalores,valores."
								+ "tipoValor as tipval,valores.nombre as valor "+
								", valores.tipoRentabilidad as tiporent , entidad as enti "+
								"FROM OPERACIONES , VALORES  where valor=idvalor  ) natural join "+
								"(select * from ((select identidad as enti , nombre as entidad from oferentes) union "+
								"(select identidad as enti , nombre as entidad from inversionistas))) "
								+ where + "and fecha between '"+fechainicial+"' and '"+fechaFinal+"'");
				request.setAttribute("result", rs);
				request.setAttribute("tipo", "consultarMovimientos");
				resp = "bien";
			}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
