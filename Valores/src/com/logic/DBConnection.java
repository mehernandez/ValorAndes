package com.logic;

import java.sql.*;

public class DBConnection {
	private Connection conn;

	public static ResultSet ejecutar(String query) throws SQLException {
		Connection conn = null;
		ResultSet respuesta = null;
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
			respuesta = statement.executeQuery(query);

		} catch (Exception e) {
			e.printStackTrace();
		}
		conn.close();
		return respuesta;
	}

	public static int actualizar(String query) throws SQLException {
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

}
