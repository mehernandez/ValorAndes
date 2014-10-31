package com.tests;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

public class TestsIteracion3 {

	public Connection conectar(){
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
	
	//@Test
	public void testConsultas() throws SQLException{
		Connection conn=conectar();
		Statement statement=conn.createStatement();
		//PROBAR QUE LAS CONSULTAS NO ESTAN SIENDO NULAS
		//Intermediarios
		assertTrue(statement.execute("select nombre from intermediarios"));
		//assertNotNull(statement.executeQuery(""));
		//Oferentes
		assertTrue(statement.execute("select nombre from oferentes"));
		//assertNotNull(statement.executeQuery(""));
		//Inversionistas
		assertTrue(statement.execute("select nombre from inversionistas"));
		//assertNotNull(statement.executeQuery(""));
		conn.close();
	}
	
	@Test
	public void testEliminarIntermediario() throws SQLException{
		Connection conn=conectar();
		Statement statement=conn.createStatement();
		
		//Primero se ingresa un intermediario
		
		//int g = statement.executeUpdate("insert into intermediarios values (600,'hola','6786060760','Neiva','Direccion Salitre','427008989')");
		assertEquals(1, statement.executeUpdate("insert into intermediarios values (600,'hola','6786060760','Neiva','Direccion Salitre','427008989')"));
		assertEquals(1,statement.executeUpdate("insert into pendientes values (1000,1000,1)"));
		//Luego se elimina
		assertEquals(1,statement.executeUpdate("delete from intermediarios where identidad=600"));
		assertEquals(1,statement.executeUpdate("delete from pendientes where idoperacion=1000"));
		//Luego se busca y verifica si esta
		ResultSet rs1= statement.executeQuery("select nombre from intermediarios where identidad=600");
		assertEquals(false,rs1.next());
		ResultSet rs2= statement.executeQuery("select idoperacion from pendientes where idoperacion=1000");
		assertEquals(false,rs2.next());
		
		conn.close();
	}
	
	
	public void reorganizarPortafolio() throws SQLException{
		Connection conn=conectar();
		Statement statement=conn.createStatement();
		//CASO DE COMPRA
		ResultSet cliente=statement.executeQuery("");
		//El cliente va a ser el primero
		cliente.next();
		String clienteS=cliente.getString("nombre");
		//Ahora se tiene que obtener una accion de su portafolio
		ResultSet accion=statement.executeQuery("");
		accion.next();
		String nombreAccion=accion.getString("nombre");
		//ya con el nombre del cliente y la accion que pertenence a su portafolio se puede proceder a hacer la transaccion
		//donde estan los 0 de cuantas accionesMercado y cuantasAccionesCliente se calcula con un qurery cuantas acciones esa accion tiene y
		//en la de acciones se pone el valor nuevo
		int cuantasAccionesMercado=0;
		int cuantasAccionesCliente=0;
		int acciones=0;
		//TRANSACCION se toma cualquier numero que este dentro de lo que hay en el mercado
		if(cuantasAccionesCliente>acciones){
			//EN ESTE CASO SE HACE LA TRANSACCION CORRESPONDIENTE A VENDER
			
			//Luego de calcular se mira cuantas acciones quedaron en el mercado(donde esta el 0 es el nuevo calculo de acciones)
			assertEquals(cuantasAccionesMercado+(cuantasAccionesCliente-acciones),0);
			//Luego de calcular se mira cuantas acciones quedaron en el cliente(donde esta el 0 es el nuevo calculo de acciones)
			assertEquals(acciones,0);
		}else{
			//EN ESTE CASO SE HACE LA TRANSACCION CORRESPONDIENTE A COMPRAR
			
			//Luego de calcular se mira cuantas acciones quedaron en el mercado(donde esta el 0 es el nuevo calculo de acciones)
			assertEquals(cuantasAccionesMercado-(acciones-cuantasAccionesCliente),0);
			//Luego de calcular se mira cuantas acciones quedaron en el cliente(donde esta el 0 es el nuevo calculo de acciones)
			assertEquals(acciones,0);
		}
	}
	

}
