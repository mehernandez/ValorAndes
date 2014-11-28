package com.logic;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
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
 * Servlet implementation class ConsultarRangoFinal
 */
public class ConsultarRangoFinal extends HttpServlet implements IEscuchadorEventos {
	private static final long serialVersionUID = 1L;
	private Conector conector;
	
	private HttpServletRequest request;
	private HttpServletResponse response;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConsultarRangoFinal() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		JsonObject json = new JsonObject();
		
		json.addProperty("method", "Top20");
		json.addProperty("inicio", (String) request.getAttribute("fechaDesde"));
		json.addProperty("fin", (String) request.getAttribute("fechaHasta"));
		
		Gson gson = new GsonBuilder().create();
		
		String j = gson.toJson(json);
		
		
		//
		String resp = conector.preguntar(j);
		//
		
		JsonParser jsonParser = new JsonParser();
		JsonObject fullJson = jsonParser.parse(resp).getAsJsonObject();
		
		request.setAttribute("tabla2", fullJson);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		this.request = request;
		this.response = response;
		
		String tipoVenta = request.getParameter("tipoConsulta");

		if (tipoVenta.equalsIgnoreCase("mayorMovimiento")) {

			System.out.println("Entre a mayor movimiento");
			String fechaDesde = request.getParameter("fechaDesde");
			String fechaHasta = request.getParameter("fechaHasta");

			System.out.println("fecha desde: " + fechaDesde);
			System.out.println("Fecha hasta :  " + fechaHasta);
			String where = " ";

			if (!fechaDesde.isEmpty() && !fechaHasta.isEmpty()) {
				try {
					Date d = new SimpleDateFormat("dd/MM/YYYY")
							.parse(fechaDesde.trim());
					Date x = new SimpleDateFormat("dd/MM/YYYY")
							.parse(fechaHasta.trim());

					where += " AND fecha BETWEEN '" + fechaDesde + "' AND '"
							+ fechaHasta + "' ";
				} catch (Exception e) {
					// error en escritura de la fecha
				}
				
				

			}

			Connection conn = null;
			Statement st = this.crearConexion(conn);
			try {
				ResultSet rs = st
						.executeQuery("select * from(select idvalor,valor,tiporentabilidad, count(idvalor) as negociado,avg(preciovalor) as costopromedio  from( select valores.idvalor, "
								+ "valores.nombre as valor,preciovalor ,tiposrentabilidad.nombre as tiporentabilidad "
								+ "from operaciones,valores,tiposrentabilidad where operaciones.valor=idvalor and tiposrentabilidad.IDTIPORENTABILIDAD=valores.TIPORENTABILIDAD "
								+ where
								+ ") group by idvalor, valor,tiporentabilidad) where rownum <=20 order by negociado desc,idvalor,valor ");
				request.setAttribute("result", rs);

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.cerrar(conn);

			
			
			
			JsonObject json = new JsonObject();
			
			json.addProperty("method", "Top20");
	
			json.addProperty("inicio", (String) request.getParameter("fechaDesde"));
			json.addProperty("fin", (String) request.getParameter("fechaHasta"));
			
			Gson gson = new GsonBuilder().create();
			
			String j = gson.toJson(json);
			
			
			//
			
              conector.enviarPregunta(j);
			//
              
              String url = "/RespuestaRangoFinal.jsp"; // relative url for display jsp
      		// page
      		ServletContext sc = getServletContext();
      		
      		
      		RequestDispatcher rd = sc.getRequestDispatcher(url);
      		
      			
      	
			
			try {
				Thread.sleep(10000);
				} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			
			
			rd.forward(request, response);
		}
		
		
		
		
		
		
System.out.println("Termino el metodo POST");
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
		request.setAttribute("tabla2", mensaje);
		
		
		
		
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
