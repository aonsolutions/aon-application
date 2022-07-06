package net.aonsolutions.core.pool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;


class CompositeConnectionInfo extends ConnectionInfo {

	
	private static CompositeConnectionInfo defaultConnectionInfo;

	static final CompositeConnectionInfo getDefaultCompositeConnectionInfo()
			throws AonConnectionException{

		synchronized (ConnectionInfoImpl.DEFAULT_CONFIG_FILE) {
			if (defaultConnectionInfo == null) {
				defaultConnectionInfo = newConnectionInfo(new File(ConnectionInfoImpl.DEFAULT_CONFIG_FILE));
			}
			
			return defaultConnectionInfo;
		}
	}

	static final CompositeConnectionInfo getCompositeConnectionInfo(File configFile)
			throws AonConnectionException{
		return newConnectionInfo(configFile);
	}

	private Map<String, String> domainsMap;
	private Map<String, ConnectionInfo> schemasMap ;

	private List<ConnectionInfo> connectionInfos = new LinkedList<ConnectionInfo>();
	
	@Override
	public String getUrl(String schema) {
		return getConnectionInfo(schema).getUrl(schema);
	}

	@Override
	public String getUser(String schema) {
		return getConnectionInfo(schema).getUser(schema);
	}

	@Override
	public String getPassword(String schema) {
		return getConnectionInfo(schema).getPassword(schema);
	}

	@Override
	public String getUseSSL(String schema) {
		return getConnectionInfo(schema).getUseSSL(schema);
	}

	@Override
	public String getTimeZone(String schema) {
		return getConnectionInfo(schema).getTimeZone(schema);
	}

	@Override
	public String getSchemaUrl(String schema) {
		return getConnectionInfo(schema).getSchemaUrl(schema);
	}

	@Override
	public String getDriverClass(String schema) {
		return getConnectionInfo(schema).getDriverClass(schema);
	}

	@Override
	public List<String> getSchemas() throws AonConnectionException {
		return getSchemasMap().keySet().stream().collect(Collectors.toList());
	}

	@Override
	public Map<String, String> getDomains() throws AonConnectionException {
		if ( domainsMap == null ) { 
			domainsMap = getDomainsMap();
		}
		return domainsMap;
	}

	@Override
	public Connection getMetadataConnection(String schema) throws AonConnectionException {
		return getConnectionInfo(schema).getMetadataConnection(schema);
	}

	@Override
	public Map<String, Integer> getDomainMap() throws AonConnectionException {
		
		Map<String, Integer> domainMap = new HashMap<>();
		for (ConnectionInfo connectionInfo : connectionInfos) {
			try {
				domainMap.putAll(connectionInfo.getDomainMap());
			} catch ( AonConnectionException e) {
				
			}
		}
		return domainMap;

	}

	@Override
	public Connection getConnection(String schema) throws AonConnectionException {
		return getConnectionInfo(schema).getConnection(schema);
	}

	@Override
	public String getSchemaFirstDomain(String schema) throws AonConnectionException {
		return getConnectionInfo(schema).getSchemaFirstDomain(schema);
	}

	@Override
	public String getDomainDatabase(String domainName) throws AonConnectionException {
		String dataBase = getDomains().get(domainName);
		if ( dataBase != null  ) {
			return dataBase;
		}
		reloadDomains();
		return getDomains().get(domainName);
	}

	@Override
	public List<String> getSchemaDomains(String schema) throws AonConnectionException {
		return getConnectionInfo(schema).getSchemaDomains(schema);
	}

	@Override
	public Connection getDomainConnection(String domain) throws AonConnectionException {
		return getConnectionInfo(domain).getConnection(domain);
		//return getConnection(getDomains().get(domain));
	}
	
	private void reloadDomains() throws AonConnectionException {
		domainsMap = getDomainsMap();
	}

	private Map<String, String> getDomainsMap() throws AonConnectionException {
		Map<String, String> domains = new HashMap<>();
		for (ConnectionInfo connectionInfo : connectionInfos) {
			try {
				domains.putAll(connectionInfo.getDomains());
			} catch ( AonConnectionException e) {
			}
		}
		return domains;
	}
	
	
	// 
	
	private Map<String, ConnectionInfo> getSchemasMap() {
		if ( schemasMap == null) {
			// HashMap is non synchronized. It is not-thread safe and can't be 
			// shared between many threads without proper synchronization code
			schemasMap = new Hashtable<>();
			for( ConnectionInfo connectionInfo: connectionInfos ) {
				try {
					connectionInfo.getSchemas().forEach(schema -> schemasMap.putIfAbsent(schema, connectionInfo));
				} catch ( AonConnectionException e ) {
				}
			}
			//schemasMap.forEach((schema, ci) -> System.out.println(schema + " {user:" + ci.getUser(schema) + ", url:"+ ci.getUrl(schema) +"}"));
		}
		
		return schemasMap;
	}
	private ConnectionInfo getConnectionInfo(String schema) {
		return getSchemasMap().getOrDefault(schema, new NoSuchSchemaConnectionInfo(schema));
	}
	

	private CompositeConnectionInfo load(Properties props) throws AonConnectionException {
		connectionInfos = 
		props.keySet().stream()
		.filter(Objects::nonNull)
		.filter(prop -> prop.toString().startsWith(ConnectionInfoImpl.JDBC_URL_PROPERTY))
		.map( prop -> prop.toString().substring(ConnectionInfoImpl.JDBC_URL_PROPERTY.length()))
		.map(suffix -> newConnectionInfo(props, suffix))
		.collect(Collectors.toList());
		return this;
	}
	
	private ConnectionInfo newConnectionInfo(Properties props, String suffix) {
		return new ConnectionInfoImpl().load(
		props, 
		ConnectionInfoImpl.DRIVER_CLASS_PROPERTY + suffix, 
		ConnectionInfoImpl.USER_PROPERTY + suffix, 
		ConnectionInfoImpl.PASSWORD_PROPERTY + suffix, 
		ConnectionInfoImpl.JDBC_URL_PROPERTY + suffix, 
		ConnectionInfoImpl.USESSL_PROPERTY + suffix, 
		ConnectionInfoImpl.TIMEZONE_PROPERTY + suffix);
	}
	
	private static CompositeConnectionInfo newConnectionInfo(File file) throws AonConnectionException {
		try(FileInputStream is = new FileInputStream(file)) {
			return getConnectionInfo(is);
		} catch ( IOException e ) {
			throw new AonConnectionException(e);
		}
	}

	private static CompositeConnectionInfo getConnectionInfo(InputStream is) throws IOException, AonConnectionException {
		Properties props = new Properties();
		props.load(is);
		return getConnectionInfo(props);
	}

	private static CompositeConnectionInfo getConnectionInfo(Properties props) throws AonConnectionException {
		return new CompositeConnectionInfo().load(props);
		
	}
}
