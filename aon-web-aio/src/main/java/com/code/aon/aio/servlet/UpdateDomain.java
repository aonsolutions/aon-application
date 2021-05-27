package com.code.aon.aio.servlet;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.util.AdminUtil;

public class UpdateDomain extends HttpServlet {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String DEPLOYED_XML = "/var/lib/jbossas/server/default/conf/aon.workspace/deployed.xml";
//	private static final String DEPLOYED_XML = "/mnt/iNetServer.x86_64/var/lib/jbossas/server/default/conf/aon.workspace/deployed.xml";

	private static final String USER_PARAM = "user";
	private static final String PASSWORD_PARAM = "password";
	private static final String DOMAIN_NAME_PARAM = "domain-name";
	private static final String DOMAIN_DESCRIPTION_PARAM = "domain-description";
	private static final String DOMAIN_TYPE_PARAM = "domain-type";
    private static final String DOMAIN_USER_PARAM = "domain-user";
    private static final String DOMAIN_PASSWORD_PARAM = "domain-password";

	private static final String SCRIPT = "new_domain.py";

	private static final String SP_HOST = "--host=";
	private static final String SP_USER = "--user=";
	private static final String SP_PASSWD = "--passwd=";
	private static final String SP_DB = "--db=";

	private static final String SP_DOMAIN_NAME = "--domain-name=";
	private static final String SP_DOMAIN_DESCRIPTION = "--domain-description=";
	private static final String SP_DOMAIN_TYPE = "--domain-type=";
    private static final String SP_DOMAIN_USER = "--domain-user=";
    private static final String SP_DOMAIN_PASSWORD = "--domain-password=";


	private static final String DEPLOYED_OPTION_TAG = "option";
	private static final String DEPLOYED_URL_PROPERTY = "hibernate.connection.url";
	private static final String DEPLOYED_USER_PROPERTY = "hibernate.connection.username";
	private static final String DEPLOYED_PASSWORD_PROPERTY = "hibernate.connection.password";
	private static final String DEPLOYED_USESSL_PROPERTY = "hibernate.connection.usessl";
	private static final String DEPLOYED_TIMEZONE_PROPERTY = "hibernate.connection.timezone";

	public UpdateDomain() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}

	private void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Properties props = getConnectionProperties();
			String dburl = props.getProperty(DEPLOYED_URL_PROPERTY);
			String dbhost = getHost(dburl);
			String dbuser = props.getProperty(DEPLOYED_USER_PROPERTY);
			String dbpassword = props.getProperty(DEPLOYED_PASSWORD_PROPERTY);
			String dbusessl = props.getProperty(DEPLOYED_USESSL_PROPERTY, "false");
			String dbtimezone = props.getProperty(DEPLOYED_TIMEZONE_PROPERTY, TimeZone.getDefault().getID());

			String user = request.getParameter(USER_PARAM);
			if (StringUtils.isBlank(user)) {
				throw new AonException("Usuario es un dato requerido");
			}
			if (!StringUtils.contains(user, "@")) {
				throw new AonException("Formato de usuario incorrecto, debe ser 'usuario@dominio'");
			}
			String[] tok = StringUtils.split(user,"@");
			if (tok.length != 2) {
				throw new AonException("Formato de usuario incorrecto, debe ser 'usuario@dominio'.");
			}
			String domain = tok[1];
			user = tok[0];
			String password = request.getParameter(PASSWORD_PARAM);

			String[] databaseInfo = getDatabaseInfo(dburl,dbuser,dbpassword, dbusessl, dbtimezone, domain, user, password );
			String database = databaseInfo[0];
			String suffix = databaseInfo[1];
			String domainName = request.getParameter(DOMAIN_NAME_PARAM);
			if (StringUtils.isBlank(domainName)) {
				throw new AonException("Nombre del dominio es un dato requerido.");
			}
			if (!StringUtils.endsWith(domainName, suffix)) {
				throw new AonException("Su usuario solo puede crear subdominios de '"+ suffix +"'.");
			}

			String domainUser = request.getParameter(DOMAIN_USER_PARAM);
			if (StringUtils.isBlank(domainUser)) {
				throw new AonException("El usuario del dominio es un dato requerido.");
			}
			String domainPassword = request.getParameter(DOMAIN_PASSWORD_PARAM);
			if (StringUtils.isBlank(domainPassword)) {
				throw new AonException("La clave del usuario del dominio es un dato requerido.");
			}

			String domainDescription = request.getParameter(DOMAIN_DESCRIPTION_PARAM);
			String domainType = request.getParameter(DOMAIN_TYPE_PARAM);

			List<String> commandLine = new LinkedList<String>();
			commandLine.add(SCRIPT);
			commandLine.add(SP_HOST + dbhost);
			commandLine.add(SP_USER + dbuser);
			commandLine.add(SP_PASSWD + dbpassword);
			commandLine.add(SP_DB+ database);
			commandLine.add(SP_DOMAIN_NAME+ domainName);

			if (StringUtils.isNotBlank(domainDescription)) {
				commandLine.add(SP_DOMAIN_DESCRIPTION + domainDescription);
			}

			if (StringUtils.isNotBlank(domainType)) {
				commandLine.add(SP_DOMAIN_TYPE + domainType);
			}

			if (StringUtils.isNotBlank(domainUser)) {
				commandLine.add(SP_DOMAIN_USER + domainUser);
			}

			if (StringUtils.isNotBlank(domainPassword)) {
				commandLine.add(SP_DOMAIN_PASSWORD + domainPassword);
			}

			String[] command = commandLine.toArray(new String[commandLine.size()]);
			for (String t : command) {
				System.out.print( t );
			}

			Runtime r = Runtime.getRuntime();
			Process p = r.exec(command);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			response.setContentType("text/html");
			response.getWriter().println("<html><head><head><body>");
			String line = "";
			while ((line = stdInput.readLine()) != null) {
				response.getWriter().println(parseLine(line));
			}
			while ((line = stdError.readLine()) != null) {
				response.getWriter().println(parseLine(line));
			}
			p.waitFor();
			response.getWriter().println("</body></html>");
			response.getWriter().flush();
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.setContentType("text/html");
			response.getWriter().print("<html><head><head><body>");
			response.getWriter().print("Se ha producido un error interno. [" + e.getMessage()+ "]");
			response.getWriter().print("</body></html>");
			response.getWriter().flush();

			response.getWriter().flush();
			e.printStackTrace();
		}

	}

	private String parseLine(String line) {
		line = StringUtils.replace(line, "[91m", "<p style='color: red;'>");
		line = StringUtils.replace(line, "[32m", "<p style='color: green;'>");
		line = StringUtils.replace(line, "[1m", "<p style='font-weight: bold;'>");
		line = StringUtils.replace(line, "[0m", "</p>");
		return line;
	}

	private Properties getConnectionProperties() throws SAXException, IOException, ParserConfigurationException  {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		File file = new File(DEPLOYED_XML);
		Document doc = builder.parse(file);
		NodeList list = doc.getElementsByTagName(DEPLOYED_OPTION_TAG);
		Properties props = new Properties();
		for (int i = 0; i < list.getLength(); i++ ) {
			Node node = list.item(i);
			NamedNodeMap map = node.getAttributes();
			Node attr  = map.getNamedItem("name");

			if (DEPLOYED_URL_PROPERTY.equals(attr.getNodeValue())
				|| DEPLOYED_USER_PROPERTY.equals(attr.getNodeValue())
				|| DEPLOYED_PASSWORD_PROPERTY.equals(attr.getNodeValue())) {

				Node value = map.getNamedItem("value");
				props.put(attr.getNodeValue(), value.getNodeValue());
			}
		}
		return props;
	}

	private String getHost(String url) throws URISyntaxException {
		String[] tokens = StringUtils.split(url,"//");
		String host = StringUtils.split(tokens[1],":")[0];
		host = StringUtils.split(host,",")[0];
		return host;
	}

	private String[] getDatabaseInfo(String dburl, String dbuser, String dbpassword, String dbusessl, String dbtimezone, String domain, String user, String password) throws ClassNotFoundException, SQLException, AonException {
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement domainStmt = null;
		ResultSet domainRs = null;
		PreparedStatement userStmt = null;
		ResultSet userRs = null;
		try {
			Properties properties = new Properties();
			properties.setProperty("user", dbuser);
			properties.setProperty("password", dbpassword);
			properties.setProperty("useSSL", dbusessl);
			properties.setProperty("serverTimezone", dbtimezone);
			c = DriverManager.getConnection(dburl,properties);

			String sql = "SELECT T.TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES as T WHERE T.TABLE_NAME = 'domain'";
			stmt = c.prepareStatement(sql);
			rs = stmt.executeQuery();
			String database = null;
			String suffix = null;
			while (rs.next()) {
				String db = rs.getString(1);
				String domainSql = "SELECT `id`,`subDomainSuffix` FROM `"+db+"`.`domain` WHERE name = '" + domain + "'";
				domainStmt = c.prepareStatement(domainSql);
				domainRs = domainStmt.executeQuery();
				if (domainRs.next()) {
					int id = domainRs.getInt(1);

					String userSql = "SELECT `password` FROM `"+db+"`.`user` WHERE domain = "+id+" and login = '" + user + "'";
					userStmt = c.prepareStatement(userSql);
					userRs = userStmt.executeQuery();
					if (userRs.next()) {
						String saved_passwd = userRs.getString(1);
						String sent_passwd  = AdminUtil.encodeSHA(password);
						if (!StringUtils.equals(saved_passwd, sent_passwd)) {
							throw new AonException("La contrase�a no es correcta.");
						}
					} else {
						throw new AonException("Usuario no registrado");
					}
					database = db;
					suffix = domainRs.getString(2);
					break;
				}
				domainRs.close();
				domainStmt.close();
			}
			if (database == null) {
				throw new AonException("Usuario@Dominio no registrado");
			}
			return new String[]{database,suffix};

		} finally {
			DbUtils.closeQuietly(userStmt);
			DbUtils.closeQuietly(userRs);
			DbUtils.closeQuietly(domainStmt);
			DbUtils.closeQuietly(domainRs);
			DbUtils.closeQuietly(stmt);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(c);
		}
	}

//	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, URISyntaxException {
//		NewDomain d = new NewDomain();
//		System.out.println( d.getHost("jdbc:mysql://127.0.0.1:3306") );
//		System.out.println( d.getHost("jdbc:mysql:replication://192.168.3.110,192.168.3.111,192.168.3.112:3306/aon-mac-asesores-es?autoReconnect=true mac") );
//		System.out.println(  );
//	}
}
