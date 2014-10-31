package com.logic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Comprar
 */
public class Comprar extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Comprar() {
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String resp = "n";
		int id = (Integer) request.getSession().getAttribute("id");

		String sCantidad = request.getParameter("cantidad");
		String idValor = request.getParameter("idValor");
		String sPrecio = request.getParameter("precio");
		String nIntermediario = request.getParameter("intermediario");

		if (!idValor.isEmpty()) {
			if (!sCantidad.isEmpty() && !sPrecio.isEmpty()) {

			} else if (!sCantidad.isEmpty()) {

				Connection conn = null;
				Statement st = this.crearConexion(conn);
				try {
					ResultSet g = st
							.executeQuery("select identidad from intermediarios where nombre='"
									+ nIntermediario + "'");
					int rs1 = 0;
					int rs2 = 0;
					if (g.next()) {
						int inter = g.getInt("identidad");

						ResultSet v = st
								.executeQuery("select idvalor,tipovalor,precio ,sum(cantidad) as suma "
										+ "from (select valores.idvalor ,valores.nombre,tiposvalor.nombre as tipovalor"
										+ ",pendientes.cantidad ,operaciones.idoperacion,activa,operaciones.entidad as inter"
										+ ",precio from valores,operaciones,pendientes,tiposvalor where valores.idvalor=valor "
										+ "and pendientes.idoperacion=operaciones.idoperacion and activa=1 and tiposvalor.idtipovalor=valores.tipovalor "
										+ "and tipooperacion='VENTA' and entidad <>"
										+ id
										+ " and idvalor="
										+ idValor
										+ ") group by idvalor, tipovalor,precio ");

						if (v.next()) {
							int cantDisp = v.getInt("suma");
							int cantidad = Integer.parseInt(sCantidad);
							if (cantDisp >= cantidad && cantidad > 0) {
								String tipoValor = v.getString("tipovalor");
								int prec = v.getInt("precio");

								rs1 = st.executeUpdate("INSERT INTO OPERACIONES "
										+ " VALUES (SEQOPERACIONES.NEXTVAL,SYSDATE ,"
										+ (prec * cantidad)
										+ ","
										+ id
										+ ","
										+ inter
										+ ",'COMPRA',"
										+ cantidad
										+ ","
										+ prec
										+ ",'"
										+ tipoValor
										+ "',"
										+ idValor + " )");

								if (rs1 == 1) {

									rs2 = st.executeUpdate("INSERT INTO pendientes "
											+ " VALUES (SEQOPERACIONES.CURRVAL,"
											+ cantidad + ",0)");

								}

							}
						}
					}
					this.cerrar(conn);
					if (rs1 == 1 && rs2 == 1) {
						resp = "si";
						System.out.println("La operacion acabo con exito !");
					}
				} catch (SQLException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}

			} else if (!sPrecio.isEmpty()) {
				System.out.println("Entre a comprar por dinero");
				Connection conn = null;
				Statement st = this.crearConexion(conn);
				try {
					ResultSet g = st
							.executeQuery("select identidad from intermediarios where nombre='"
									+ nIntermediario + "'");
					int rs1 = 0;
					int rs2 = 0;
					if (g.next()) {
						int inter = g.getInt("identidad");

						ResultSet v = st
								.executeQuery("select idvalor,tipovalor,precio ,sum(cantidad) as suma "
										+ "from (select valores.idvalor ,valores.nombre,tiposvalor.nombre as tipovalor"
										+ ",pendientes.cantidad ,operaciones.idoperacion,activa,operaciones.entidad as inter"
										+ ",precio from valores,operaciones,pendientes,tiposvalor where valores.idvalor=valor "
										+ "and pendientes.idoperacion=operaciones.idoperacion and activa=1 and tiposvalor.idtipovalor=valores.tipovalor "
										+ "and tipooperacion='VENTA' and entidad <>"
										+ id
										+ " and idvalor="
										+ idValor
										+ ") group by idvalor, tipovalor,precio ");

						if (v.next()) {
							int cantDisp = v.getInt("suma");
							int preciox = Integer.parseInt(sPrecio);
							int prec = v.getInt("precio");
							int cantidad = preciox / prec;
							System.out.println("Cantidad es :" + cantidad);
							if (cantDisp >= cantidad && cantidad > 0) {
								String tipoValor = v.getString("tipovalor");

								rs1 = st.executeUpdate("INSERT INTO OPERACIONES "
										+ " VALUES (SEQOPERACIONES.NEXTVAL,SYSDATE ,"
										+ (prec * cantidad)
										+ ","
										+ id
										+ ","
										+ inter
										+ ",'COMPRA',"
										+ cantidad
										+ ","
										+ prec
										+ ",'"
										+ tipoValor
										+ "',"
										+ idValor + " )");

								if (rs1 == 1) {

									rs2 = st.executeUpdate("INSERT INTO pendientes "
											+ " VALUES (SEQOPERACIONES.CURRVAL,"
											+ cantidad + ",0)");

								}

							}
						}
					}
					this.cerrar(conn);
					if (rs1 == 1 && rs2 == 1) {
						resp = "si";
						System.out.println("La operacion acabo con exito !");
					}
				} catch (SQLException e) {
					System.out.println(e.getMessage());
					e.printStackTrace();
				}

			}
		}

		request.setAttribute("response", resp);
		String url = "/RespuestaOperacion.jsp";
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

}
