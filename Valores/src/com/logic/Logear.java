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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class Logear
 */
public class Logear extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Logear() {
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
		System.out.println("/////////////ESTA ACA ANTES DE EXTRAER Y DEL IF");
		String tableName = request.getParameter("table_name");
		int start = Integer.parseInt(request.getParameter("start")) + 1;
		int length = Integer.parseInt(request.getParameter("length"));
		int columnOrder = Integer.parseInt(request.getParameter("order[0][column]"));
		String columnName = request.getParameter("columns[" + columnOrder + "][data]");
		String tipo = request.getParameter("order[0][dir]");
		String search = request.getParameter("search[value]");
		if (tableName.equals("intermediarios")){
			System.out.println("////////////ENTRO AL IF");
//			Enumeration enums = request.getParameterNames();
//			while(enums.hasMoreElements()){
//				String paramName = (String) enums.nextElement();
//				System.out.println(paramName + " - value : " + request.getParameter(paramName));
//			}
			ObjectMapper mapper = new ObjectMapper();
			response.setContentType("application/json");
			ArrayList<HashMap<String, String>> resultado = null;
			int conteo=0;
			int conteoSearch=0;
			try {
				resultado = darIntermediarios(start, length, columnName, tipo, search);
				conteo = contarIntermediariosTotal();
				conteoSearch = contarIntermediarios(search);
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
			out.print(json);

		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String usuario = request.getParameter("usuario");
		String clave = request.getParameter("clave");

		String url = "";
		Connection cox = null;
		boolean ya = false;
		Statement s = this.crearConexion(cox);
		try {
			ResultSet rs = s
					.executeQuery("select login,clave,identidad,tipo "
							+ "from usuarios,entidades where entidad=identidad and login='"
							+ usuario + "' and " + "clave='" + clave + "'");

			if (rs.next()) {
				request.getSession().setAttribute("login",
						rs.getString("login"));
				request.getSession().setAttribute("id", rs.getInt("identidad"));
				request.getSession().setAttribute("tipo", rs.getString("tipo"));
				url = "/HomeInversionista.jsp";
				ya = true;
			}
		} catch (SQLException e) {
			System.out.println("Error al hacer consulta para el login");
		}
		this.cerrar(cox);

		if (usuario.equals("admin") && clave.equals("admin")) {
			//ACA ME INTERESA HACER MODIFICACIONES
			url = "/HomeInversionista.jsp"; // relative url for display jsp page
			request.getSession().setAttribute("login", usuario);
			ya = true;
		}

		if (!ya) {
			url = "/Login.jsp";
			request.setAttribute("success", "no");
		} else {
			Connection conn = null;
			Statement st = this.crearConexion(conn);

			ArrayList oferentes = new ArrayList();
			ArrayList intermediarios = new ArrayList();
			ArrayList inversionistas = new ArrayList();
			ArrayList tiposValor = new ArrayList();
			ArrayList tiposRentabilidad = new ArrayList();
			ArrayList valores = new ArrayList();
			
			try {
				ResultSet rs = st.executeQuery("select nombre,identidad from oferentes");

				while (rs.next()) {
					oferentes.add(rs.getInt("identidad")+":"+rs.getString("nombre"));
				}
				ResultSet f = st
						.executeQuery("select nombre,identidad from intermediarios");
				while (f.next()) {
					intermediarios.add(f.getInt("identidad")+":"+f.getString("nombre"));
				}
				ResultSet k = st
						.executeQuery("select nombre, identidad from inversionistas");
				while (k.next()) {
					inversionistas.add(k.getInt("identidad")+":"+k.getString("nombre"));
				}
				
				ResultSet h = st
						.executeQuery("select nombre, idtipovalor from tiposvalor");
				while (h.next()) {
					tiposValor.add(h.getInt("idTipoValor")+":"+h.getString("nombre"));
				}

				ResultSet l = st
						.executeQuery("select nombre,idTipoRentabilidad from tiposRentabilidad");
				while (l.next()) {
					tiposRentabilidad.add(l.getInt("idTipoRentabilidad")+":"+l.getString("nombre"));
				}
				
				ResultSet v = st
						.executeQuery("select nombre, idvalor  from valores");
				while (v.next()) {
					valores.add((v.getInt("idValor"))+":"+(v.getString("nombre")));
				}
				
				
				request.getSession().setAttribute("oferentes", oferentes);
				request.getSession().setAttribute("intermediarios",
						intermediarios);
				request.getSession().setAttribute("inversionistas", inversionistas);
				request.getSession().setAttribute("tiposValor", tiposValor);
				request.getSession().setAttribute("tiposRentabilidad", tiposRentabilidad);
				request.getSession().setAttribute("valores", valores);
			} catch (SQLException e) {
				System.out
						.println("Error al ejecutar el query de obtener los nombres de los oferentes");
			}

			this.cerrar(conn);
		}

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
	
	public ArrayList<HashMap<String, String>> darIntermediarios(int start, int rows, String order, String tipo, String search) throws SQLException {
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
		String query = "select * from ( select a.*, ROWNUM rnum from (select idValor , nombre as nombreValor,precio,oferente,nombreOferente from valores natural join (select nombre as nombreOferente, identidad as oferente from oferentes ) ORDER BY " +  order +" " +  tipo + ") a where ROWNUM <= ? AND (NOMBREVALOR like '" + search +"%' OR NOMBREOFERENTE like '" + search +"%' )) where rnum  >= ?";
		PreparedStatement st = conn.prepareStatement(query);
		st.setInt(1, start + rows-1);
		st.setInt(2, start);
		ResultSet set = st.executeQuery();
		ArrayList<HashMap<String, String>> resultado = darHola(set);
		set.close();
		st.close();
		conn.close();
		return resultado;	
	}
	
	public int contarIntermediarios(String search) throws Exception{
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
		String query = "select count(*) as count from (select idValor , nombre as nombreValor,precio,oferente,nombreOferente from valores natural join (select nombre as nombreOferente, identidad as oferente from oferentes ) where NOMBREOFERENTE like '" + search +"%')";
		PreparedStatement st = conn.prepareStatement(query);
		ResultSet set = st.executeQuery();
		set.next();
		int resultado = set.getInt("COUNT");
		set.close();
		st.close();
		conn.close();
		return resultado;	
	}
	
	public int contarIntermediariosTotal()throws Exception{
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
		String query = "select count(*) as count from valores natural join (select nombre as nombreOferente, identidad as oferente from oferentes )";
		PreparedStatement st = conn.prepareStatement(query);
		ResultSet set = st.executeQuery();
		set.next();
		int resultado = set.getInt("COUNT");
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

}
