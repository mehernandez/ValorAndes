package com.tests;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

public class PruebasIteracion2 {
	
	private Statement statement;
	private Connection conn;
	
	public void setupEscenario1(){
		conn = null;
		ResultSet respuesta = null;
		try{
		statement = null;

		Class.forName("oracle.jdbc.OracleDriver");
		String dbURL = "jdbc:oracle:thin:@prod.oracle.virtual.uniandes.edu.co:1531:prod";
		String user = " ISIS2304121420";
		String pass = "alpendesf04b0";
		conn = DriverManager.getConnection(dbURL, user, pass);
		if (conn != null) {
		statement =conn.createStatement();
		}else{
		statement = null;
		}
		

		}catch(Exception e){
		e.printStackTrace();
		}
		
	}

	@Test
	public void testQueries() {
		try {
			String query="";
			statement.execute(query);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Test
	public void testInsertar(){
		try {
			//Este es la primera insertada
			String query="";
			assertEquals(1,statement.executeUpdate("insert into pendientes values(900,1,1)"));
			assertEquals(0,statement.executeUpdate("insert into pendientes values(900,3,0)"));
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
