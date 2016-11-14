package com.code.aon.pool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

public class ConnectionInfo {

	public static final String DEFAULT_CONFIG_FILE = "/etc/aon-aio/connection";

	private static final String DRIVER_CLASS_PROPERTY = "driverClass";
	private static final String USER_PROPERTY = "user";
	private static final String PASSWORD_PROPERTY = "password";
	private static final String JDBC_URL_PROPERTY = "jdbcUrl";

	private static final String MYSQL_SCHEMA = "mysql";

	private static final String SELECT_SCHEMAS = "SELECT t.TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES as t WHERE t.TABLE_NAME = 'domain'";

	private String driverClass;
	private String url;
	private String user;
	private String password;

	private static ConnectionInfo DEFAULT_CONNNECTION;

	public ConnectionInfo()  {
	}

	public static final ConnectionInfo getDefaultConnectionInfo()
			throws AonConnectionException {
		synchronized (DEFAULT_CONFIG_FILE) {
			if (DEFAULT_CONNNECTION == null) {
				DEFAULT_CONNNECTION = new ConnectionInfo();
				DEFAULT_CONNNECTION.loadDefaulConfiguration();
			}
			return DEFAULT_CONNNECTION;
		}
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void load(InputStream in) throws IOException {
		Properties props = new Properties();
		props.load(in);
		setDriverClass(props.getProperty(DRIVER_CLASS_PROPERTY));
		setUser(props.getProperty(USER_PROPERTY));
		setPassword(props.getProperty(PASSWORD_PROPERTY));
		setUrl(props.getProperty(JDBC_URL_PROPERTY));
	}

	public void load(File propertiesfile) throws AonConnectionException {
		InputStream in = null;
		try {
			in = new FileInputStream(propertiesfile);
			load(in);
		} catch (IOException e) {
			throw new AonConnectionException(e.getMessage(),e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private void loadDefaulConfiguration() throws AonConnectionException {
		load(new File(DEFAULT_CONFIG_FILE));

	}

	public Connection getMetadataConnection() throws AonConnectionException {
		return getConnection(MYSQL_SCHEMA);
	}

	public Connection getDomainConnection(String domain) throws AonConnectionException {
		return getConnection(domain);
	}

	private Connection getConnection(String schema) throws AonConnectionException {
		try {
			Class.forName(getDriverClass());
			return DriverManager.getConnection(getSchemaUrl(schema), getUser(),getPassword());
		} catch (ClassNotFoundException e) {
			throw new AonConnectionException(e.getMessage(),e);
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);		}
		
	}

	public String getSchemaUrl(String schema) {
		String url = getUrl();
		if (url != null && !url.trim().endsWith("/")) {
			url = url + '/';
		}
		return url + schema;
	}

	public boolean testMetadataConnection() {
		Connection c = null;
		try {
			c = getMetadataConnection();
		} catch (AonConnectionException e) {
			return false;
		} finally {
			closeQuietly(c);
		}
		return true;
	}

	public boolean testDomainConnection(String domain) {
		Connection c = null;
		try {
			c = getDomainConnection(domain);
		} catch (AonConnectionException e) {
			return false;
		} finally {
			closeQuietly(c);
		}
		return true;
	}

	public List<String> getSchemas() throws AonConnectionException{
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			c = getMetadataConnection();
			ps = c.prepareStatement(SELECT_SCHEMAS,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			List<String> list = new Vector<String>();
			while (rs.next()) {
				list.add(rs.getString(1));
			}
			return list;
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
			closeQuietly(c);
		}	
	}
	
	public List<String> getSchemaDomains(String schema) throws AonConnectionException{
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			c = getMetadataConnection();
			String select = "SELECT name FROM `" + schema + "`.domain";
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			List<String> list = new Vector<String>();
			while (rs.next()) {
				list.add(rs.getString(1));
				
			}
			return list;
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
			closeQuietly(c);
		}	
	}
	
	public String getSchemaFirstDomain(String schema) throws AonConnectionException{
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			c = getMetadataConnection();
			String select = "SELECT name FROM `" + schema + "`.domain limit 1";
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			String s = "";
			if (rs.next()) {
				s = rs.getString(1);
				
			}
			return s;
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		} finally {
			closeQuietly(rs);
			closeQuietly(ps);
			closeQuietly(c);
		}	
	}
	
	public Map<String, String> getDomains() throws AonConnectionException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			c = getMetadataConnection();
			ps = c.prepareStatement(SELECT_SCHEMAS,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			Map<String, String> map = new Hashtable<String, String>();
			while (rs.next()) {
				String schema = rs.getString(1);
				String select = "SELECT name FROM `" + schema + "`.domain";
				ps1 = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				rs1 = ps1.executeQuery();
				while (rs1.next()) {
					String domain = rs1.getString(1);
					map.put(domain, schema);
				}
				rs1.close();
				ps1.close();
			}
			return map;
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		} finally {
			closeQuietly(rs1);
			closeQuietly(ps1);
			closeQuietly(rs);
			closeQuietly(ps);
			closeQuietly(c);
		}
	}
	
	public Map<String, Integer> getDomainMap() throws AonConnectionException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			c = getMetadataConnection();
			ps = c.prepareStatement(SELECT_SCHEMAS,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			Map<String, Integer> map = new Hashtable<String, Integer>();
			while (rs.next()) {
				String schema = rs.getString(1);
				String select = "SELECT name, id FROM `" + schema + "`.domain";
				ps1 = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				rs1 = ps1.executeQuery();
				while (rs1.next()) {
					String domain = rs1.getString(1);
					Integer id = rs1.getInt(2);
					map.put(domain, id);
				}
				rs1.close();
				ps1.close();
			}
			return map;
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		} finally {
			closeQuietly(rs1);
			closeQuietly(ps1);
			closeQuietly(rs);
			closeQuietly(ps);
			closeQuietly(c);
		}
	}

	public String getDomainDatabase(String domainName) throws AonConnectionException {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			c = getMetadataConnection();
			ps = c.prepareStatement(SELECT_SCHEMAS,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = ps.executeQuery();
			String returnedSchema = null;
			while (rs.next()) {
				String schema = rs.getString(1);
				String select = "SELECT name FROM `" + schema
						+ "`.domain WHERE name = '" + domainName + "'";
				ps1 = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				rs1 = ps1.executeQuery();
				if (rs1.next()) {
					returnedSchema = schema;
					break;
				}
				rs1.close();
				ps1.close();
			}
			return returnedSchema;
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		} finally {
			closeQuietly(rs1);
			closeQuietly(ps1);
			closeQuietly(rs);
			closeQuietly(ps);
			closeQuietly(c);
		}
	}

	private void closeQuietly(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
		}
	}

	private void closeQuietly(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
			}
		}
	}

	private void closeQuietly(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
			}
		}
	}

}
