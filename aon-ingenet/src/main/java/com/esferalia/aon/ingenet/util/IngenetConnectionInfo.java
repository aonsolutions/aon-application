package com.esferalia.aon.ingenet.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;


import net.aonsolutions.core.pool.AonConnectionException;

public class IngenetConnectionInfo {

	public static final String CONNECTION_NAME = "ingenet-connection";
	public static final String DEFAULT_CONFIG_FILE = "/etc/aon-aio/" + CONNECTION_NAME;

	private static final String DRIVER_CLASS_PROPERTY = "driverClass";
	private static final String USER_PROPERTY = "user";
	private static final String PASSWORD_PROPERTY = "password";
	private static final String JDBC_URL_PROPERTY = "jdbcUrl";

	private static final String MYSQL_SCHEMA = "udapa";

	private static final String SELECT_SCHEMAS = "SELECT t.TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES as t WHERE t.TABLE_NAME = 'domain'";

	private String driverClass;
	private String url;
	private String user;
	private String password;
	private String timeZone;
	private String useSSL;

	private static IngenetConnectionInfo DEFAULT_CONNNECTION;

	public IngenetConnectionInfo()  {
		this.useSSL = "false";
		this.timeZone = TimeZone.getDefault().getID();
	}

	public static final IngenetConnectionInfo getDefaultConnectionInfo()
			throws AonConnectionException {
		synchronized (DEFAULT_CONFIG_FILE) {
			if (DEFAULT_CONNNECTION == null) {
				DEFAULT_CONNNECTION = new IngenetConnectionInfo();
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

	public String getUseSSL() {
		return useSSL;
	}

	public void setUseSSL(String useSSL) {
		this.useSSL = useSSL;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
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
		if(Files.exists(Paths.get(DEFAULT_CONFIG_FILE))){
			load(new File(DEFAULT_CONFIG_FILE));
		} else {
			System.out.println("### Loading default connection info (File not found: " + DEFAULT_CONFIG_FILE +")");
			InputStream in = null;
			try {
				in = IngenetConnectionInfo.class.getClassLoader().getResourceAsStream("com/esferalia/aon/ingenet/" + CONNECTION_NAME);
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
			Properties properties = new Properties();
			properties.setProperty("user", getUser());
			properties.setProperty("password", getPassword());
			properties.setProperty("useSSL", getUseSSL());
			properties.setProperty("serverTimezone", getTimeZone());
			return DriverManager.getConnection(getSchemaUrl(schema), properties);
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

	public Map<String, Integer> getDomainMap() throws AonConnectionException {
		Connection c = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			c = getMetadataConnection();
			Map<String, Integer> map = new Hashtable<String, Integer>();
				String schema = "udapa";
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
			return map;
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		} finally {
			closeQuietly(rs1);
			closeQuietly(ps1);
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

	public int getDomainScope(int domainId) throws AonConnectionException {
		Connection c = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			c = getMetadataConnection();
			String schema = "udapa";
			String select = "SELECT id FROM `" + schema
					+ "`.scope WHERE domain = " + domainId;
			ps1 = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			rs1 = ps1.executeQuery();
			if (rs1.next()) {
				return rs1.getInt(1);
			}
			rs1.close();
			ps1.close();
			return -1;
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		} finally {
			closeQuietly(rs1);
			closeQuietly(ps1);
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
