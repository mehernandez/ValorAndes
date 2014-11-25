package com.logic;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.JSON.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class ConsultaMultiClase
 */
public class ConsultaMultiClase extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Conector conector;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConsultaMultiClase() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		String url = "/ConsultaMultiFinal.jsp"; // relative url for display jsp
		// page
		ServletContext sc = getServletContext();
		RequestDispatcher rd = sc.getRequestDispatcher(url);
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try{
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		String tableName = request.getParameter("table_name");
		
		int start = Integer.parseInt(request.getParameter("start")) + 1;
		
		int length = Integer.parseInt(request.getParameter("length"));
		
		int columnOrder = Integer.parseInt(request.getParameter("order[0][column]"));
		
		String columnName = request.getParameter("columns[" + columnOrder + "][data]");
		
		String tipo = request.getParameter("order[0][dir]");
		
		String search = request.getParameter("search[value]");
		
		String data = request.getParameter("form");
		
		JSONArray obj = new JSONArray(data);
	
		
		
		if (tableName.equals("req1")){
			
			String fechaInicial=obj.getJSONObject(1).getString("value");
			String fechaFinal=obj.getJSONObject(2).getString("value");
			String bolsa = obj.getJSONObject(3).getString("value");
			
			if(bolsa.equals("medallo")){
			
		System.out.println("//////////////////FI: "+fechaInicial+" FF: "+fechaFinal);
			
			System.out.println("ESTA ES LA INFO:      "+data);
			
			response.setContentType("application/json");
			ArrayList<HashMap<String, String>> resultado = null;
			int conteo=0;
			int conteoSearch=0;
			try {
				resultado = darMovimientos(start, length, columnName, tipo, search,fechaInicial,fechaFinal);
				conteo = contarMovimientosTotal(fechaInicial,fechaFinal);
				conteoSearch = contarMovimientos(search,fechaInicial,fechaFinal);
				System.out.println("conteo " + conteo);
			} catch (Exception e) {
				e.printStackTrace();
			}

			DataTableObject dataTableObject = new DataTableObject();
			dataTableObject.setAaData(resultado);
			dataTableObject.setRecordsFiltered(conteoSearch);
			dataTableObject.setRecordsTotal(conteo);
			PrintWriter out = response.getWriter();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(dataTableObject);
			System.out.println(json);
			out.print(json);
			}
			
			else{
				
				System.out.println("//////////////////FI: "+fechaInicial+" FF: "+fechaFinal);
				
				System.out.println("ESTA ES LA INFO:      "+data);
				
				response.setContentType("application/json");
				
				PrintWriter out = response.getWriter();
				JsonObject json = new JsonObject();
				json.addProperty("method", "darValores");
				json.addProperty("start", start);
				json.addProperty("length", length);
				json.addProperty("columnOrder", columnOrder);
				json.addProperty("columnName", columnName);
				json.addProperty("tipo", tipo);
				json.addProperty("search", search);
				json.addProperty("inicio", fechaInicial);
				json.addProperty("fin", fechaFinal);
				
				
				Gson gson = new GsonBuilder().create();
				
				String j = gson.toJson(json);
				
				
				//
				String resp = conector.preguntar(j);
				//
				
				System.out.println(resp);
				out.print(resp);
				
			}

		} 
	}
	
	

	

	
	private int contarMovimientos(String search,String fechaInicial,String fechaFinal) throws SQLException {
		Connection conn=null;
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			String dbURL = "jdbc:oracle:thin:@prod.oracle.virtual.uniandes.edu.co:1531:prod";
			String user = "ISIS2304161420";
			String pass = "entraros66151";
			conn = DriverManager.getConnection(dbURL, user, pass);
			if (conn != null) {

			} else {
				System.out.println("VOY A RETORNAL NULLLLLLLLLLL");
				throw new Exception("La conexion es null");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		String query = "select count(*) as count from (select * from ((SELECT fecha,monto, intermediario ,tipoOperacion,numerovalores,valores.tipoValor as tipval,valores.nombre as valor, valores.tipoRentabilidad as tiporent , entidad as enti FROM OPERACIONES , VALORES  where valor=idvalor  ) natural join (select * from (select identidad as enti , nombre as entidad from oferentes) union (select identidad as enti , nombre as entidad from inversionistas))) WHERE fecha between '"+fechaInicial+"' and '"+fechaFinal+"') where VALOR like '" + search +"%' OR ENTIDAD like '" + search +"%'";
		PreparedStatement st = conn.prepareStatement(query);
		ResultSet set = st.executeQuery();
		set.next();
		int resultado = set.getInt("COUNT");
		set.close();
		st.close();
		conn.close();
		return resultado;	
	}

	private int contarMovimientosTotal(String fechaInicial,String fechaFinal) throws SQLException {
		Connection conn=null;
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			String dbURL = "jdbc:oracle:thin:@prod.oracle.virtual.uniandes.edu.co:1531:prod";
			String user = "ISIS2304161420";
			String pass = "entraros66151";
			conn = DriverManager.getConnection(dbURL, user, pass);
			if (conn != null) {

			} else {
				System.out.println("VOY A RETORNAL NULLLLLLLLLLL");
				throw new Exception("La conexion es null");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		String query = "select count(*) as count from (select * from ((SELECT fecha,monto, intermediario ,tipoOperacion,numerovalores,valores.tipoValor as tipval,valores.nombre as valor, valores.tipoRentabilidad as tiporent , entidad as enti FROM OPERACIONES , VALORES  where valor=idvalor  ) natural join (select * from (select identidad as enti , nombre as entidad from oferentes) union (select identidad as enti , nombre as entidad from inversionistas))) WHERE fecha between '"+fechaInicial+"' and '"+fechaFinal+"')";
		PreparedStatement st = conn.prepareStatement(query);
		ResultSet set = st.executeQuery();
		set.next();
		int resultado = set.getInt("COUNT");
		set.close();
		st.close();
		conn.close();
		return resultado;	
	}

	private ArrayList<HashMap<String, String>> darMovimientos(int start, int rows, String order, String tipo, String search,String fechaInicial, String fechaFinal) throws SQLException {
		
		if(order == null){
			order = "NOMBRE";
		}
		if(tipo == null){
			tipo = "asc";
		}
		Connection conn=null;
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			String dbURL = "jdbc:oracle:thin:@prod.oracle.virtual.uniandes.edu.co:1531:prod";
			String user = "ISIS2304161420";
			String pass = "entraros66151";
			conn = DriverManager.getConnection(dbURL, user, pass);
			if (conn != null) {

			} else {
				System.out.println("VOY A RETORNAL NULLLLLLLLLLL");
				throw new Exception("La conexion es null");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		String query = "select fecha, monto as cantidad , tipooperacion as tipo_mercado , valor as nombre_valor,tipval as tipo_valor, entidad as nombre_usuario, intermediario as nombre_corredor from ( select a.*, ROWNUM rnum from (select * from ((SELECT fecha,monto, intermediario ,tipoOperacion,numerovalores,valores.tipoValor as tipval,valores.nombre as valor, valores.tipoRentabilidad as tiporent , entidad as enti FROM OPERACIONES , VALORES  where valor=idvalor  ) natural join (select * from (select identidad as enti , nombre as entidad from oferentes) union (select identidad as enti , nombre as entidad from inversionistas))) WHERE fecha between '"+fechaInicial+"' and '"+fechaFinal+"' ORDER BY " +  order +" " +  tipo + ") a where ROWNUM <= ? AND (VALOR like '" + search +"%' OR ENTIDAD like '" + search +"%' )) where rnum  >= ?";
		PreparedStatement st = conn.prepareStatement(query);
		st.setInt(1, start + rows-1);
		st.setInt(2, start);
		ResultSet set = st.executeQuery();
		System.err.println("Ejecuto el query perro");
		ArrayList<HashMap<String, String>> resultado = darHola(set);
		set.close();
		st.close();
		conn.close();
		return resultado;	
		
	}
	
	public static ArrayList<HashMap<String, String>> darHola(ResultSet resSet) throws SQLException{
		ArrayList<HashMap<String, String>> finale = new ArrayList<HashMap<String,String>>();
		int j = 0;
		while(resSet.next()){
			Object[] str = new Object[resSet.getMetaData().getColumnCount()];
			HashMap<String, String> temp = new HashMap<String, String>();
			for (int i = 1; i <= str.length; i++) {
				String label = resSet.getMetaData().getColumnLabel(i);
				String obj = resSet.getString(i);
				temp.put(label, obj);
			}
			finale.add(temp);
			j++;
		}

		return finale;
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
