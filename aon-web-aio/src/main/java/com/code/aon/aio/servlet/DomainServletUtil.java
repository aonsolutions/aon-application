package com.code.aon.aio.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.AonException;
import com.code.aon.common.util.AdminUtil;

public class DomainServletUtil implements IDomainServletConstants{
	
	private String dbURL;
	private String dbHost;
	private String dbUser;
	private String dbPassword;
	private String dbName;
	private String user;
	private String domain;
	private String password;
	private String suffix;
	
	private String domainName;
	private String domainDescription;
	private String domainType;
	private String domainUser;
	private String domainPassword;
	private String domainMaxDefinedUsers;
	private String domainModules;

	private DomainServletUtil() {
		// Para el main y pruebas
	}
	
	public DomainServletUtil(HttpServletRequest request) throws AonException {
		try {
			initializeConnectionProperties();
			parseParameters(request);
		} catch (SAXException e) {
			throw new AonException(e);
		} catch (IOException e) {
			throw new AonException(e);
		} catch (ParserConfigurationException e) {
			throw new AonException(e);
		} catch (ClassNotFoundException e) {
			throw new AonException(e);
		} catch (SQLException e) {
			throw new AonException(e);
		} catch (URISyntaxException e) {
			throw new AonException(e);
		}
	}
	
	public String getDbURL() {
		return dbURL;
	}
	public void setDbURL(String dbURL) {
		this.dbURL = dbURL;
	}

	public String getDbHost() {
		return dbHost;
	}
	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainDescription() {
		return domainDescription;
	}
	public void setDomainDescription(String domainDescription) {
		this.domainDescription = domainDescription;
	}
	
	public String getDomainType() {
		return domainType;
	}
	public void setDomainType(String domainType) {
		this.domainType = domainType;
	}
	
	public String getDomainUser() {
		return domainUser;
	}
	public void setDomainUser(String domainUser) {
		this.domainUser = domainUser;
	}
	
	public String getDomainPassword() {
		return domainPassword;
	}
	public void setDomainPassword(String domainPassword) {
		this.domainPassword = domainPassword;
	}

	public String getDomainMaxDefinedUsers() {
		return domainMaxDefinedUsers;
	}
	public void setDomainMaxDefinedUsers(String domainMaxDefinedUsers) {
		this.domainMaxDefinedUsers = domainMaxDefinedUsers;
	}

	public String getDomainModules() {
		return domainModules;
	}
	public void setDomainModules(String domainModules) {
		this.domainModules = domainModules;
	}

	private void initializeConnectionProperties() throws SAXException, IOException, ParserConfigurationException, URISyntaxException  {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		File file = new File(DEPLOYED_XML);
		Document doc = builder.parse(file);
		NodeList list = doc.getElementsByTagName(DEPLOYED_OPTION_TAG);
		for (int i = 0; i < list.getLength(); i++ ) {
			Node node = list.item(i);
			NamedNodeMap map = node.getAttributes();
			Node attr  = map.getNamedItem(DEPLOYED_NAME_ATTR);
			if (DEPLOYED_URL_PROPERTY.equals(attr.getNodeValue())) {
				setDbURL( map.getNamedItem(DEPLOYED_VALUE_ATTR).getNodeValue() );
				setDbHost( getHost(getDbURL()) );
			}	
			if (DEPLOYED_USER_PROPERTY.equals(attr.getNodeValue())){
				setDbUser( map.getNamedItem(DEPLOYED_VALUE_ATTR).getNodeValue() );
			}
			if (DEPLOYED_PASSWORD_PROPERTY.equals(attr.getNodeValue())){
				setDbPassword( map.getNamedItem(DEPLOYED_VALUE_ATTR).getNodeValue() );
			}
		}
	}
	
	private String getHost(String url) throws URISyntaxException {
		String[] tokens = StringUtils.split(url,"//");
		String host = StringUtils.split(tokens[1],":")[0];
		host = StringUtils.split(host,",")[0];
		return host;
	}
	
	private void initializeDatabaseInfo() throws ClassNotFoundException, SQLException, AonException {
		Class.forName("org.gjt.mm.mysql.Driver");
		Connection c = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement domainStmt = null;
		ResultSet domainRs = null;
		PreparedStatement userStmt = null;
		ResultSet userRs = null;
		try {
			c = DriverManager.getConnection(getDbURL(),getDbUser(),getDbPassword());
			
			stmt = c.prepareStatement(TABLE_SCHEMA_SENTENCE);
			rs = stmt.executeQuery();
			setDbName( null );
			setSuffix( null ); 
			while (rs.next()) {
				String db = rs.getString(1);
				String domainSql = "SELECT id,subDomainSuffix FROM `"+db+"`.`domain` WHERE name = '" + getDomain() + "'";
				domainStmt = c.prepareStatement(domainSql);
				domainRs = domainStmt.executeQuery();
				if (domainRs.next()) {
					int id = domainRs.getInt(1);
					
					String userSql = "SELECT `password` FROM `"+db+"`.`user` WHERE domain = "+id+" and login = '" + getUser() + "'";
					userStmt = c.prepareStatement(userSql);
					userRs = userStmt.executeQuery();
					if (userRs.next()) {
						String saved_passwd = userRs.getString(1);
						String sent_passwd  = AdminUtil.encodeSHA(getPassword());
						if (!StringUtils.equals(saved_passwd, sent_passwd)) {
							throw new AonException("La contraseña no es correcta.");
						}
					} else {
						throw new AonException("Usuario no registrado");
					}
					setDbName( db );
					setSuffix( domainRs.getString(2) ); 
					break;
				} 
				domainRs.close();
				domainStmt.close();
			}
			if (getDbName() == null) {
				throw new AonException("Usuario@Dominio no registrado");
			}
		} finally {
			if (userStmt != null) {try {userStmt.close();} catch (SQLException e) {}}
			if (userRs != null) {try {userRs.close();} catch (SQLException e) {}}
			if (domainStmt != null) {try {domainStmt.close();} catch (SQLException e) {}}
			if (domainRs != null) {try {domainRs.close();} catch (SQLException e) {}}
			if (stmt != null) {try {stmt.close();} catch (SQLException e) {}}
			if (rs != null) {try {rs.close();} catch (SQLException e) {}}
			if (c != null) {try {c.close();} catch (SQLException e) {}}
		}
	}

	private void parseParameters(HttpServletRequest request) throws AonException, ClassNotFoundException, SQLException {
		String userDomain= request.getParameter(USER_PARAM);
		if (StringUtils.isBlank(userDomain)) {
			throw new AonException("Usuario es un dato requerido");
		}
		if (!StringUtils.contains(userDomain, "@")) {
			throw new AonException("Formato de usuario incorrecto, debe ser 'usuario@dominio'");
		}
		String[] tok = StringUtils.split(userDomain,"@");
		if (tok.length != 2) {
			throw new AonException("Formato de usuario incorrecto, debe ser 'usuario@dominio'.");
		}
		setUser(tok[0]);
		setDomain(tok[1]);
		setPassword( request.getParameter(PASSWORD_PARAM) );
		
		initializeDatabaseInfo();
		
		setDomainName( request.getParameter(DOMAIN_NAME_PARAM) );
		if (StringUtils.isBlank(getDomainName())) {
			throw new AonException("Nombre del dominio es un dato requerido.");
		}
		if (!StringUtils.endsWith(getDomainName(), getSuffix())) {
			throw new AonException("Su usuario solo puede crear subdominios de '"+ getSuffix() +"'.");
		}
		
		setDomainUser( request.getParameter(DOMAIN_USER_PARAM));
		if (StringUtils.isBlank(getDomainUser())) {
			throw new AonException("El usuario del dominio es un dato requerido.");
		}
		setDomainPassword( request.getParameter(DOMAIN_PASSWORD_PARAM));
		if (StringUtils.isBlank(getDomainPassword())) {
			throw new AonException("La clave del usuario del dominio es un dato requerido.");
		}
		
		setDomainDescription( request.getParameter(DOMAIN_DESCRIPTION_PARAM));
		setDomainType( request.getParameter(DOMAIN_TYPE_PARAM));
		
		String definedUsers =request.getParameter(DOMAIN_MAX_DEFINED_USERS);
		if (StringUtils.isNotBlank(definedUsers)) {
			try {
				Integer.parseInt(definedUsers);
			} catch (NumberFormatException e) {
				throw new AonException("El número máximo de usuarios debe ser un valor numérico entero.");	
			}
			setDomainMaxDefinedUsers(definedUsers);
		}
		
		String modules = request.getParameter(DOMAIN_MODULES);
		if (StringUtils.isNotBlank(definedUsers)) {
			parseModules(modules);
			setDomainModules(modules);
		}
		
	}
	
	private void parseModules(String modules) throws AonException {
		String[] mods = StringUtils.split(modules,',');
		for (String mod : mods) {
			try {
				Module.valueOf(mod);
			} catch (IllegalArgumentException e) {
				StringBuilder buf = new StringBuilder();
				buf.append("El módulo '");
				buf.append(mod);
				buf.append("' no es un módulo válido, debe ser uno de los siguientes:");
				for (Module m:Module.values()) {
					buf.append(" '");
					buf.append(m.getName());
					buf.append("'");
				}
				throw new AonException( buf.toString() );
			}
			
		}
		
	}

	public String[] getNewDomainCommand() {
		return getCommand(NEW_SCRIPT);
	}
	public String[] getUpdateDomainCommand() {
		return getCommand(UPDATE_SCRIPT);
	}

	private String[] getCommand(String script) {
		List<String> commandLine = new LinkedList<String>();
		commandLine.add(script);
		commandLine.add(SP_HOST + getDbHost());
		commandLine.add(SP_USER + getDbUser());
		commandLine.add(SP_PASSWD + getDbPassword());
		commandLine.add(SP_DB + getDbName());
		commandLine.add(SP_DOMAIN_NAME + getDomainName());

		if (StringUtils.isNotBlank(getDomainDescription())) {
			commandLine.add(SP_DOMAIN_DESCRIPTION + getDomainDescription());
		}

		if (StringUtils.isNotBlank(getDomainType())) {
			commandLine.add(SP_DOMAIN_TYPE + getDomainType());
		}

		if (StringUtils.isNotBlank(getDomainUser())) {
			commandLine.add(SP_DOMAIN_USER + getDomainUser());
		}
		
		if (StringUtils.isNotBlank(getDomainPassword())) {
			commandLine.add(SP_DOMAIN_PASSWORD + getDomainPassword());
		}

		if (StringUtils.isNotBlank(getDomainMaxDefinedUsers())) {
			commandLine.add(SP_DOMAIN_MAX_DEFINED_USERS + getDomainMaxDefinedUsers());
		}
		
		if (StringUtils.isNotBlank(getDomainModules())) {
			commandLine.add(SP_DOMAIN_MODULES + getDomainModules());
		}

		return commandLine.toArray(new String[commandLine.size()]);
	}
	
	private String parseLine(String line) {
		line = StringUtils.replace(line, "[91m", "<p style='color: red;'>");
		line = StringUtils.replace(line, "[32m", "<p style='color: green;'>");
		line = StringUtils.replace(line, "[1m", "<p style='font-weight: bold;'>");
		line = StringUtils.replace(line, "[0m", "</p>");
		return line;
	}

	public void doResponse(HttpServletResponse response, BufferedReader stdInput, BufferedReader stdError, int exitVal) throws AonException {
		try {
			response.setContentType("text/html");
			response.getWriter().println("<html><head><head><body>");
			String line = "";
			while ((line = stdInput.readLine()) != null) {
				response.getWriter().println(parseLine(line));
			}
			while ((line = stdError.readLine()) != null) {
				response.getWriter().println(parseLine(line));
			}
			response.getWriter().println("</body></html>");
			response.getWriter().flush();
		} catch (IOException e) {
			throw new AonException(e);
		}
		
	}
	
	public static void main(String[] args) throws AonException {
		DomainServletUtil d = new DomainServletUtil();
		d.parseModules("dddd");
		
	}
	
	
}
