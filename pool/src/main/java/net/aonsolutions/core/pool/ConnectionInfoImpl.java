package net.aonsolutions.core.pool;

import static net.aonsolutions.core.pool.AonDataSource.CONFIGURATION_PATH;

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
import java.util.TimeZone;
import java.util.Vector;


class ConnectionInfoImpl extends ConnectionInfo{


	static final String USER_PROPERTY = "user";
	static final String USESSL_PROPERTY = "useSSL";
	static final String PASSWORD_PROPERTY = "password";
	static final String JDBC_URL_PROPERTY = "jdbcUrl";
	static final String TIMEZONE_PROPERTY = "timezone";
	static final String DRIVER_CLASS_PROPERTY = "driverClass";
	static final String DEFAULT_CONFIG_FILE = CONFIGURATION_PATH + "/connection";
	static final String MYSQL_SCHEMA = "information_schema";

	private static final String SELECT_SCHEMAS = "SELECT t.TABLE_SCHEMA FROM INFORMATION_SCHEMA.TABLES as t WHERE t.TABLE_NAME = 'domain'";

	private static ConnectionInfoImpl defaultConnection;

	public static final ConnectionInfoImpl getDefaultConnectionInfoImpl()
			throws AonConnectionException {
		synchronized (DEFAULT_CONFIG_FILE) {
			if (defaultConnection == null) {
				defaultConnection = new ConnectionInfoImpl();
				defaultConnection.loadDefaulConfiguration();
			}
			return defaultConnection;
		}
	}

	private String url;
	private String user;
	private String password;
	private String useSSL;
	private String timeZone;
	private String driverClass;
	
	private Connection metadaConnection;
	
	@Override
	public String getUrl(String schema) {
		return url;
	}

	@Override
	public String getUser(String schema) {
		return user;
	}

	@Override
	public String getPassword(String schema) {
		return password;
	}

	@Override
	public String getUseSSL(String schema) {
		return useSSL;
	}

	@Override
	public String getTimeZone(String schema) {
		return timeZone;
	}

	@Override
	public String getDriverClass(String schema) {
		return driverClass;
	}

	@Override
	public Connection getDomainConnection(String domain) throws AonConnectionException {
		return getConnection(domain);
	}

	@Override
	public String getSchemaUrl(String schema) {
		String url = getUrl(schema);
		if (url != null && !url.trim().endsWith("/")) {
			url = url + '/';
		}
		return url + schema;
	}

	@Override
	public List<String> getSchemas() throws AonConnectionException{
		PreparedStatement preparedStmt = null;
		ResultSet resultSet = null;

		try {
			Connection connection = getMetadataConnection();
			preparedStmt = connection.prepareStatement(SELECT_SCHEMAS,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			resultSet = preparedStmt.executeQuery();
			List<String> list = new Vector<String>();
			while (resultSet.next()) {
				String schema = resultSet.getString(1);
				list.add(schema);
			}
			return list;
		} catch (SQLException e) {
			throw new AonConnectionException(e.getMessage(),e);
		} finally {
			closeQuietly(resultSet);
			closeQuietly(preparedStmt);
		}
	}

	@Override
	public Connection getMetadataConnection(String schema) throws AonConnectionException {
		return newMetadataConnection();
	}

	@Override
	public Connection getConnection(String schema) throws AonConnectionException {
		return AonDataSource.getInstance().getDatabaseConnection(schema);
	}

	@Override
	public List<String> getSchemaDomains(String schema) throws AonConnectionException{
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			Connection c = getMetadataConnection();
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
		}
	}

	@Override
	public String getSchemaFirstDomain(String schema) throws AonConnectionException{
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			Connection c = getMetadataConnection();
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
		}
	}

	@Override
	public Map<String, String> getDomains() throws AonConnectionException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			Connection c = getMetadataConnection();
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
		}
	}

	@Override
	public Map<String, Integer> getDomainMap() throws AonConnectionException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			Connection c = getMetadataConnection();
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
		}
	}

	@Override
	public String getDomainDatabase(String domainName) throws AonConnectionException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			Connection c = getMetadataConnection();
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
		}
	}
	
	// ------------------------------------------------------------------------

	ConnectionInfoImpl load(Properties props
			,String driverClassProperty
			,String userProperty
			,String passwordProperty
			,String jdbcUrlProperty
			,String useSSLProperty
			,String timeZoneProperty
			) {
		driverClass = props.getProperty(driverClassProperty, props.getProperty(DRIVER_CLASS_PROPERTY));
		user = props.getProperty(userProperty, props.getProperty(USER_PROPERTY));
		password = props.getProperty(passwordProperty, props.getProperty(PASSWORD_PROPERTY));
		url = props.getProperty(jdbcUrlProperty, props.getProperty(JDBC_URL_PROPERTY));
		useSSL = props.getProperty(useSSLProperty, props.getProperty(USESSL_PROPERTY,"false"));
		timeZone = props.getProperty(timeZoneProperty, props.getProperty(TIMEZONE_PROPERTY,TimeZone.getDefault().getID()));
		return this;
	}

	ConnectionInfoImpl load(Properties props) {
		driverClass = props.getProperty(DRIVER_CLASS_PROPERTY);
		user = props.getProperty(USER_PROPERTY);
		password = props.getProperty(PASSWORD_PROPERTY);
		url = props.getProperty(JDBC_URL_PROPERTY);
		useSSL = props.getProperty(USESSL_PROPERTY, "false");
		timeZone = props.getProperty(TIMEZONE_PROPERTY, TimeZone.getDefault().getID());
		return this;
	}

	ConnectionInfoImpl load(InputStream in) throws IOException {
		Properties props = new Properties();
		props.load(in);
		load(props);
		return this;
	}

	ConnectionInfoImpl load(File propertiesfile) throws AonConnectionException {
		InputStream in = null;
		try {
			in = new FileInputStream(propertiesfile);
			load(in);
			return this;
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


	// ---------------------------------------------------------------- private

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

	private void loadDefaulConfiguration() throws AonConnectionException {
		load(new File(DEFAULT_CONFIG_FILE));

	}
	
	private Connection getMetadataConnection() throws AonConnectionException{
		
		if ( metadaConnection != null ) {
			try {
				if ( metadaConnection.isValid(0))
					return metadaConnection;
			} catch (SQLException e) {
				metadaConnection = null;
			}
		}
		
		metadaConnection = newMetadataConnection();
		return metadaConnection;
	}
	

	private Connection newMetadataConnection() throws AonConnectionException{
		try {
			Class.forName(driverClass);
		} catch ( ClassNotFoundException e) {
			throw new AonConnectionException(e);
		}
		
		Properties properties = new Properties();
		properties.put(USER_PROPERTY, user);
		properties.put(PASSWORD_PROPERTY, password);
		properties.put(USESSL_PROPERTY, useSSL);
		properties.put("serverTimezone", timeZone);
		
		try {
			return  DriverManager.getConnection(url, properties);
		} catch ( SQLException e ) {
			throw new AonConnectionException(e);
		}
	}
}