package com.logic;

import java.io.IOException;
import java.sql.Connection;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class Preguntar
 */
public class Preguntar extends HttpServlet implements IEscuchadorEventos{
	private static final long serialVersionUID = 1L;
	private Conector conector;
	private String resp ;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Preguntar() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String transaccion = request.getParameter("transaccion");
		
		String[] t = transaccion.split(":");
		
		int cant = Integer.parseInt(t[1]);
		
JsonObject json = new JsonObject();


		
		json.addProperty("method", "compraVenta");
		
		json.addProperty("id", Integer.parseInt(t[0]));
		
		Connection con = DAO.conectar();
		try {
			Statement st = con.createStatement();
			
		ResultSet rs =	st.executeQuery("select cantidad from portafolioexterno where identidad ="+request.getSession().getAttribute("id")
					+  " and idvalor = "+t[0]);
		
		rs.next();
		
		int num = rs.getInt("cantidad");
		
		cant = cant - num;
		
		
		
		
	json.addProperty("cantidad", cant);

		
		Gson gson = new GsonBuilder().create();
		
		String j = gson.toJson(json);
		
		conector.enviarPregunta(j);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (resp.equals("si")){
			st.executeUpdate("update table portafolioexterno set cantidad = "+t[1]+" where identidad ="+request.getSession().getAttribute("id")
					+ " and idvalor = "+t[0]);
		}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DAO.cerrar(con);
	
		request.setAttribute("response", resp);
		
		  String url = "/RespuestaOperacion.jsp"; // relative url for display jsp
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
		String id = request.getParameter("idIntermediario");
		
		String[] str = id.split(":");
		
		JsonObject json = new JsonObject();
		
		json.addProperty("method", "retirar");
		
		json.addProperty("id", str[0]);
		


		
		Gson gson = new GsonBuilder().create();
		
		String j = gson.toJson(json);
		
		
			conector.enviarPregunta(j);
	
		
		try {
			Thread.sleep(10000);
			} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		
		
		request.setAttribute("response", resp);
		
		  String url = "/RespuestaOperacion.jsp"; // relative url for display jsp
    		// page
    		ServletContext sc = getServletContext();
    		
    		
    		RequestDispatcher rd = sc.getRequestDispatcher(url);
    		
    		
    		rd.forward(request, response);
		
		
	}
	
	public void manejarEvento(EventObject e)  {
		System.out.println("empezo evento");
		
		
		String mensaje = ((MiEvento)e).getElMensaje();
		System.out.println("mensaje recibido: " + mensaje);
		
		JsonParser jsonParser = new JsonParser();
		JsonObject fullJson = jsonParser.parse(mensaje).getAsJsonObject();
		
		if(fullJson.get("resultado").getAsBoolean()){
			resp ="si";
		}

		
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
		
		resp = "no";
	}

}
