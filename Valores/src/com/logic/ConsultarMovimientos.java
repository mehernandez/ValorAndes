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
		
		
		String tipoValor = request.getParameter("tipoValor");
		String monto = request.getParameter("monto");
		
		String idIntermediario = request.getParameter("idIntermediario");
		String incluir = request.getParameter("group1");

		//los que faltan

		
		String oferente = request.getParameter("oferente");
		
		String inversionista = request.getParameter("inversionista");


		String where = " where 1=1 ";

		String operador = "=";

		String resp = "mal";
		// Aca se hace la logica
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
					
					String[] str = inversionista.split(":");
					
					where += "and enti "+operador+str[0]+" ";
				}
				else if(!oferente.equals("N/A")){
					
					String[] str = oferente.split(":"); 
					where += "and enti "+operador+str[0]+" ";
				}
				if(!tipoValor.equals("N/A")){
					
					String[] str = tipoValor.split(":");
					
					
					where += " and tipVal"+operador+str[0]+" ";
				}
				
				if(!idIntermediario.equals("N/A")){
					
					String[] str = idIntermediario.split(":");
					where += "and intermediario"+operador+str[0]+" ";
				}
				
				Long t1=System.currentTimeMillis();

				DBConnection db = new DBConnection();
				
				String query="select * from (SELECT fecha,monto, intermediario ,tipoOperacion,numerovalores,valores."
								+ "tipoValor as tipval,valores.nombre as valor "+
								", valores.tipoRentabilidad as tiporent , entidad as enti "+
								"FROM OPERACIONES , VALORES  where valor=idvalor  ) natural join "+
								"(select * from ((select identidad as enti , nombre as entidad from oferentes) union "+
								"(select identidad as enti , nombre as entidad from inversionistas))) "
								+ where + "and fecha between '"+fechainicial+"' and '"+fechaFinal+"'";
				
				

				ResultSet rs = db.ejecutar(query);
				
				Long t2=System.currentTimeMillis();
				System.out.println("TIEMPO CONSULTA MOVIMIENTOS::::::::2"+(t2-t1));
				request.setAttribute("result", rs);
				request.setAttribute("tipo", "consultarMovimientos");
				resp = "bien";
			}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		request.setAttribute("respuesta", resp);

		String url = "/RespuestaConsultasFinales.jsp"; // relative url for display jsp
		// page
		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(url);
		rd.forward(request, response);

	}
	
	

}
