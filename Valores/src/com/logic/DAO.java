package com.logic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DAO {

	public static Connection conectar(){
	     Connection conn = null;
			try{
				
				Statement st = null;

				Class.forName("oracle.jdbc.OracleDriver");
				String dbURL = "jdbc:oracle:thin:@prod.oracle.virtual.uniandes.edu.co:1531:prod";
				String user = "ISIS2304161420";
				String pass = "entraros66151";
				conn = DriverManager.getConnection(dbURL, user, pass);
				if (conn != null) {
					return conn;

				}else{
					System.out.println("VOY A RETORNAL NULLLLLLLLLLL");
					return null;
				}

			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
	
	public static void cerrar(Connection conn){
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
