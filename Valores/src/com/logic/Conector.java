package com.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Conector extends Thread{

	public final static String NOMBRE = "VALORANDES";

	/**
	 * La direccion del servidor
	 */
	 public final static String HOST = "54.148.172.137";    // amazon
	
	//public final static String HOST = "186.28.34.3";  // Felipe

	/**
	 * El puerto a la conexion de pregunta
	 */
	public final static int PUERTO_PREGUNTA = 12345;
	
//	public final static int PUERTO_PREGUNTA = 12347;   // auxiliar
	

	/**
	 * El puerto a la conexion de respuesta
	 */
	public final static int PUERTO_RESPUESTA = 12346;

	/**
	 * El socket de la conexion
	 */
	private Socket socketPregunta;

	/**
	 * El escritor de la conexion
	 */
	private PrintWriter outPregunta;

	/**
	 * El lector de la linea ingresada
	 */
	private BufferedReader inPregunta;

	/**
	 * El lector del sistema 
	 */
	private BufferedReader stdInPregunta;

	//-----------------------------------------------------------

	/**
	 * El socket de la conexion
	 */
	private Socket socketRespuesta;

	/**
	 * El escritor de la conexion
	 */
	private PrintWriter outRespuesta;

	/**
	 * El lector de la linea ingresada
	 */
	private BufferedReader inRespuesta;

	/**
	 * El lector del sistema 
	 */
	private BufferedReader stdInRespuesta;

	/**
	 * La instancia del conector
	 */
	private static Conector instancia = null;

	/**
	 * Las clases que escuchan el evento
	 */
	private List _listeners = new ArrayList();

	//-----------------------------------------------------------
	// CONSTRUCTOR
	//-----------------------------------------------------------

	/**
	 * Construye un nuevo conector con dos conexion stateless y dos activas
	 * @param params Los parametros del conector
	 * @throws IOException En caso de error
	 * <pos> Las dos conexiones stateless se han podido manejar
	 */
	private Conector() throws Exception{
		System.out.println("=============================INICIANDO CONECTOR===================================");
		openConnectionPregunta();

		outPregunta.write("REGISTRAR-" + NOMBRE);
		outPregunta.flush();
		String resp = inPregunta.readLine();
		String[] resps = resp.split("-");
		if(resps[0].equals("TODO OK")){
			System.out.println("Conexion y registro con la cola de pregunta fue exitosa");
		}else{
			closeConnectionPregunta();
			throw new Exception("No se pudo establecer la conexion con la cola pregunta");
		}

		closeConnectionPregunta();

		//Registrar con respuesta

		openConnectionRespuesta();

		outRespuesta.write("REGISTRAR-" + NOMBRE);
		outRespuesta.flush();
		String resp1 = inRespuesta.readLine();
		String[] resps1 = resp1.split("-");
		if(resps1[0].equals("TODO OK")){
			System.out.println("Conexion y registro con la cola de respuesta fue exitosa");
		}else{
			closeConnectionRespuesta();
			throw new Exception("No se pudo establecer la conexion con la cola respuesta");
		}

		closeConnectionRespuesta();

		start();
	}

	public static Conector getInstance() throws Exception{
		if(instancia == null)
			instancia = new Conector();
		return instancia;
	}

	//-----------------------------------------------------------
	// CONEXIONES
	//-----------------------------------------------------------

	public void run(){
		System.out.println("======================================================================");
		System.out.println("Monitoreando preguntas y respuestas");
		System.out.println("======================================================================");
		
//		try {
//			this.enviarRespuesta("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOAOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOBOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOCOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOD");
//		} catch (IOException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
		
		while(true){
			try {
				openConnectionPregunta();
				outPregunta.write("PEDIR");
				outPregunta.flush();
				String preg = inPregunta.readLine();
				String[] params = preg.split("-");
				if(params[0].equals("ERROR")){
					//System.out.println("No hay preguntas");
				}else{
					System.out.println("Pregunta recibida: " + params[0]);
					//Atender pregunta!!
					//					String resp = Dao.pregunta(params[0]);

					String par = params[0];

					JsonParser jsonParser = new JsonParser();
					JsonObject fullJson = jsonParser.parse(par).getAsJsonObject();

					String tipo = fullJson.get("method").getAsString();

					

					if(tipo.equals("Top20")){
						System.out.println("Entre a "+tipo);
						this.retornarTop20(fullJson.get("inicial").getAsString(), fullJson.get("fin").getAsString());
					}
					else if(tipo.equals("darValores")){
						
						System.out.println("Entre a "+ tipo);
						this.darConsultaMovimientos(fullJson.get("inicio").getAsString(), fullJson.get("fin").getAsString(),
								fullJson.get("start").getAsInt(),fullJson.get("length").getAsInt()  , fullJson.get("columnName").getAsString(),
								fullJson.get("tipo").getAsString(), fullJson.get("search").getAsString());
					}
					else if (tipo.equals("darIntermediarios")){
						System.out.println("Entre a "+ tipo);
						this.darIntermediarios();
					}
					else if(tipo.equals("retirar")){
						System.out.println("Entre a "+ tipo);
						this.eliminarIntermediario(fullJson.get("id").getAsString());
					}

					//String temp = "{\"recordsTotal\": 200,\"recordsFiltered\": 20,\"data\": [{\"NOMBRE\": \"Certificado44\",\"CANTIDAD\": \"2\",\"PROMEDIO\": \"259.34\"}],\"draw\":0}";
					//enviarRespuesta(temp);
					//					enviarRespuesta(resp);
				}
				closeConnectionPregunta();

			} catch (Exception e) {
				try {
					closeConnectionPregunta();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			//			System.out.println("Pidiendo respuestas");

			try {
				openConnectionRespuesta();
				outRespuesta.write("PEDIR");
				outRespuesta.flush();
				String preg = inRespuesta.readLine();
				String[] params = preg.split("-");
				if(params[0].equals("ERROR")){
					//System.out.println("No hay preguntas");
				}else{
					//Atender respuesta!!
					System.out.println("Respuesta: "+ params[0]);
					//enviarRespuesta("For trying to reach the things that i cant see");
					fireEvent(params[0]);
				}
				closeConnectionRespuesta();

			} catch (Exception e) {
				try {
					closeConnectionRespuesta();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * Inicia la conexion con el socket de pregunta
	 */
	private void openConnectionPregunta(){
		try {

			socketPregunta = new Socket(HOST, PUERTO_PREGUNTA);

			outPregunta = new PrintWriter(socketPregunta.getOutputStream(), true);

			inPregunta = new BufferedReader(new InputStreamReader(
					socketPregunta.getInputStream()));    

			stdInPregunta = new BufferedReader(new InputStreamReader(System.in));


		} catch (UnknownHostException e) {
			System.err.println("Unknown Host.");
			// System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't get I/O for "
					+ "the connection.");
			//  System.exit(1);
		}
	}

	/**
	 * Cierra las conexiones con el socket de respuesta
	 * @throws IOException
	 */
	private void closeConnectionRespuesta() throws IOException{
		outRespuesta.close();
		inRespuesta.close();
		socketRespuesta.close();
		stdInRespuesta.close();
	}

	/**
	 * Inicia la conexion con el socket de pregunta
	 */
	private void openConnectionRespuesta(){
		try {

			socketRespuesta = new Socket(HOST, PUERTO_RESPUESTA);

			outRespuesta = new PrintWriter(socketRespuesta.getOutputStream(), true);

			inRespuesta = new BufferedReader(new InputStreamReader(
					socketRespuesta.getInputStream()));    

			stdInRespuesta = new BufferedReader(new InputStreamReader(System.in));

		} catch (UnknownHostException e) {
			System.err.println("Unknown Host.");
			// System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Couldn't get I/O for "
					+ "the connection.");
			//  System.exit(1);
		}
	}

	/**
	 * Cierra las conexiones con el socket de pregunta
	 * @throws IOException
	 */
	private void closeConnectionPregunta() throws IOException{
		outPregunta.close();
		inPregunta.close();
		socketPregunta.close();
		stdInPregunta.close();
	}

	/**
	 * Llamado para terminar todas las conexiones activas que tienen todos los sockets
	 * @throws IOException 
	 */
	public void terminateAll() throws IOException{
		socketPregunta.close();
		outPregunta.close();
		inPregunta.close();
		stdInPregunta.close();

		System.out.println("Conexion a socket pregunta cerrada -stateless-");

		socketRespuesta.close();
		outRespuesta.close();
		inRespuesta.close();
		stdInRespuesta.close(); 

		System.out.println("Conexion a socket respuesta cerrada -stateless-");

		instancia.stop();
	}

	//-----------------------------------------------------------
	// METODOS
	//-----------------------------------------------------------

	public void enviarPregunta(String pregunta) throws IOException{
		openConnectionPregunta();

		outPregunta.write("PUBLICAR-" + pregunta);
		outPregunta.flush();

		closeConnectionPregunta();
	}

	public void enviarRespuesta(String respuesta) throws IOException{
		
		
		openConnectionRespuesta();

		outRespuesta.write("PUBLICAR-" + respuesta);
		outRespuesta.flush();

		closeConnectionRespuesta();
	}

	//-----------------------------------------------------------
	// EVENTOS
	//-----------------------------------------------------------

	public synchronized void addEventListener(IEscuchadorEventos listener)  {
		_listeners.add(listener);
	}

	public synchronized void removeEventListener(IEscuchadorEventos listener)   {
		_listeners.remove(listener);
	}

	private synchronized void fireEvent(String hola) {
		MiEvento event = new MiEvento(this, hola);
		Iterator i = _listeners.iterator();
		while(i.hasNext())  {
			((IEscuchadorEventos) i.next()).manejarEvento(event);
		}
	}

	//-----------------------------------------------------------
	// MAIN
	//-----------------------------------------------------------

	public static void main(String[] args) {
		try{
			Conector nicky = new Conector();
			//nicky.sleep(10000);
			//String temp = "{\"recordsTotal\": 200,\"recordsFiltered\": 20,\"data\": [{\"NOMBRE\": \"Certificado44\",\"CANTIDAD\": \"2\",\"PROMEDIO\": \"259.34\"}],\"draw\":0}";
			//nicky.enviarRespuesta(temp);
		}catch(Exception e){
			e.printStackTrace();
		}
		//nicky.start();
	}


	public String preguntar

	(String pre){


		// TODO

		return pre;

	}


	public void responder(String resp){

		// TODO

	}





	public void retornarTop20(String fechaDesde , String fechaHasta) throws Exception{
		Connection con =  DAO.conectar();		
		try {

			Statement st = con.createStatement();
			String where = " ";
			if (!fechaDesde.isEmpty() && !fechaHasta.isEmpty()) {

				try {

					String[] des = fechaDesde.split("/");
					fechaDesde = des[1]+"/"+des[0]+"/"+des[2];
					
					String[] has = fechaHasta.split("/");
					fechaHasta = has[1]+"/"+has[0]+"/"+has[2];
					
					System.out.println(fechaDesde + "     "+ fechaHasta );



					where += " AND fecha BETWEEN '" + fechaDesde + "' AND '"

	+ fechaHasta + "' ";

				} catch (Exception e) {

					e.printStackTrace();

				}
			}
			ResultSet rs = st

					.executeQuery("select valor as nombre, negociado as cantidad, costopromedio as promedio from(select idvalor,valor,tiporentabilidad, count(idvalor) as negociado,round(avg(preciovalor),0) as costopromedio  from( select valores.idvalor, "

	+ "valores.nombre as valor,preciovalor ,tiposrentabilidad.nombre as tiporentabilidad "

	+ "from operaciones,valores,tiposrentabilidad where operaciones.valor=idvalor and tiposrentabilidad.IDTIPORENTABILIDAD=valores.TIPORENTABILIDAD "

	+ where

	+ ") group by idvalor, valor,tiporentabilidad) where rownum <=20 order by negociado desc,idvalor,valor ");


			ArrayList<HashMap<String, String>> result = darHola(rs);


			String json = darJson(result, 0, 0);


			
			this.enviarRespuesta(json);
			
		System.out.println("Respondi el top 20  !!!!!");
		System.out.println(json);
			//responder(json);



		} catch (SQLException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

		DAO.cerrar(con);



	}



	public void darConsultaMovimientos(String fechaInicial , String fechaFinal, int start, int length,

			String columnName,String tipo , String search){


		ArrayList<HashMap<String, String>> resultado = null;

		int conteo=0;

		int conteoSearch=0;

		try {
			
			String[] in = fechaInicial.split("/");
			fechaInicial = in[1]+"/"+in[0]+"/"+in[2];
			
			String[] fin = fechaFinal.split("/");
			fechaFinal = fin[1]+"/"+fin[0]+"/"+fin[2];

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



		Gson gson = new GsonBuilder().create();

		String json = gson.toJson(dataTableObject);


		try {
			json= json.replace("-", "/");
			this.enviarRespuesta(json);   // responder
			
			System.out.println("Respondi !");
			System.out.println(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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



	public String darJson(ArrayList<HashMap<String, String>> resultado,int conteo, int conteoSearch){


		DataTableObject dataTableObject = new DataTableObject();

		dataTableObject.setAaData(resultado);

		dataTableObject.setRecordsFiltered(conteoSearch);

		dataTableObject.setRecordsTotal(conteo);

		Gson gson = new GsonBuilder().create();

		return gson.toJson(dataTableObject);

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

	public String transformFromB64(String toConvert) {
		return new String(Base64.decodeBase64(toConvert));
		}
	
	public String encodeToB64(String toConvert){
		byte[] encodedBytes = Base64.encodeBase64(toConvert.getBytes());
		return new String(encodedBytes);
		}
	
	
	public void darIntermediarios(){
		
		
		
		Connection con = DAO.conectar();
		
		try{
		
		Statement st = con.createStatement();
		
		ResultSet rs = st

				.executeQuery("select nombre , numeroRegistro as numero_registro , identidad as id from intermediarios where rownum <= 5");


		ArrayList<HashMap<String, String>> result = darHola(rs);


		String json = darJson(result, 0, 0);

       json = json.replace("-", "_");
		
		this.enviarRespuesta(json);
		
	System.out.println("Respondi el dar Intermediarios  !!!!!");
	System.out.println(json);
		//responder(json);



	} catch (Exception e) {

		// TODO Auto-generated catch block

		e.printStackTrace();

	}

	DAO.cerrar(con);



}
	
	public boolean eliminarIntermediario(String idIntermediario){
		
		
		
	Connection conn = null;

	
		boolean resp = false;

		try {
			
			 conn = DAO.conectar();
			Statement st = conn.createStatement();
			//Chambonada

				conn.setAutoCommit(false);
					
			//
			
			ResultSet rs = st
					.executeQuery("select pendientes.idoperacion,pendientes.cantidad,operaciones.tipooperacion,operaciones.valor as idvalor,entidad as identidad "
							+ "from operaciones,pendientes where operaciones.idoperacion=pendientes.idoperacion and intermediario="
							+ idIntermediario);
			boolean ya = true;
			while (rs.next() && ya) {
				int idOp = rs.getInt("idoperacion");
				int cantidad = rs.getInt("cantidad");
				String tipoOp = rs.getString("tipooperacion");
				int idValor = rs.getInt("idvalor");
				int idEntidad = rs.getInt("identidad");

				if (tipoOp.equalsIgnoreCase("COMPRA")) {
					int f = st
							.executeUpdate("delete from pendientes where idoperacion="
									+ idOp);
					if (f == 0) {
						ya = false;
					}
				} else {
					int f = st
							.executeUpdate("update portafolio set cantidad=cantidad+"
									+ cantidad
									+ " where identidad="
									+ idEntidad + " and idvalor=" + idValor);
					if (f == 1) {
						
						//Chambonada
						Connection cox = DAO.conectar();
						Statement st2 = cox.createStatement();
				
										
						st2.executeQuery("select * from pendientes where idoperacion="+idOp+" for update");
						
						//
						int g = st
								.executeUpdate("delete from pendientes where idoperacion="
										+ idOp);
						DAO.cerrar(cox);
						if (g == 0) {
							ya = false;
						}
					} else {
						ya = false;
					}
				}
			}

			if (ya) {

				int h = st
						.executeUpdate("delete from entidades where identidad="
								+ idIntermediario);
				int b = st
						.executeUpdate("delete from intermediarios where identidad="
								+ idIntermediario);
				int k = st.executeUpdate("delete from usuarios where entidad="
						+ idIntermediario);
				if (h == 1 && b == 1 && k == 1) {
                    conn.commit();
					resp = true;
					
					System.out.println("Se elimino el intermediario "+ idIntermediario);
					
					
				} else {
					conn.rollback();
				}
			}
			else{
				conn.rollback();
			}

		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		DAO.cerrar(conn);

		return resp;
		
	}

	
}
