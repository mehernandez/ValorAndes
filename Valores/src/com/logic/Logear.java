package com.logic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
				ResultSet rs = st.executeQuery("select nombre from oferentes");

				while (rs.next()) {
					oferentes.add(rs.getString("nombre"));
				}
				ResultSet f = st
						.executeQuery("select nombre from intermediarios");
				while (f.next()) {
					intermediarios.add(f.getString("nombre"));
				}
				ResultSet k = st
						.executeQuery("select nombre from inversionistas");
				while (k.next()) {
					inversionistas.add(k.getString("nombre"));
				}
				
				ResultSet h = st
						.executeQuery("select nombre from tiposvalor");
				while (h.next()) {
					tiposValor.add(h.getString("nombre"));
				}

				ResultSet l = st
						.executeQuery("select nombre from tiposRentabilidad");
				while (l.next()) {
					tiposRentabilidad.add(l.getString("nombre"));
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

}
