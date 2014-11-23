package com.logic;

import java.sql.*;

public class DBConnection {
	
	
	private Connection conn;
	private Statement st;

	
	public DBConnection(){
		conn=null;
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
				conn=null;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public ResultSet ejecutar(String query) throws SQLException {
		st=crearConexion(conn);
		ResultSet respuesta = null;
		try {
			respuesta = st.executeQuery(query);

		} catch (Exception e) {
			e.printStackTrace();
		}
		cerrar(conn);
		return respuesta;
	}

	public int actualizar(String query) throws SQLException {
		Connection conn = null;
		int respuesta = 0;
		try {
			Statement statement = null;

			Class.forName("oracle.jdbc.OracleDriver");
			String dbURL = "jdbc:oracle:thin:@prod.oracle.virtual.uniandes.edu.co:1531:prod";
			String user = " ISIS2304121420";
			String pass = "alpendesf04b0";
			conn = DriverManager.getConnection(dbURL, user, pass);
			if (conn != null) {
				statement = conn.createStatement();
			} else {
				statement = null;
			}
			System.out.println(conn == null);
			respuesta = statement.executeUpdate(query);

		} catch (Exception e) {
			e.printStackTrace();
		}
		conn.close();
		return respuesta;
	}
	
	public Statement getStatement(){
		return st;
	}
	
	public Connection getConnection(){
		return conn;
	}

}
