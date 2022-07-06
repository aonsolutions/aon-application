package net.aonsolutions.core.pool;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public abstract  class ConnectionInfo {
	
	public static final ConnectionInfo getDefaultConnectionInfo()
			throws AonConnectionException {
		return CompositeConnectionInfo.getDefaultCompositeConnectionInfo();
	}

	public static final ConnectionInfo getConnectionInfo(File configFile)
			throws AonConnectionException {
		return CompositeConnectionInfo.getCompositeConnectionInfo(configFile);
	}

	public abstract String getUrl(String schema) ;

	public abstract String getUser(String schema) ;

	public abstract String getPassword(String schema);

	public abstract String getUseSSL(String schema);

	public abstract String getTimeZone(String schema);

	public abstract String getSchemaUrl(String schema);

	public abstract String getDriverClass(String schema) ;

	public abstract List<String> getSchemas() throws AonConnectionException;

	public abstract Map<String, String> getDomains() throws AonConnectionException ;

	public abstract Map<String, Integer> getDomainMap() throws AonConnectionException ;

	public abstract Connection getConnection(String schema) throws AonConnectionException ;

	public abstract String getSchemaFirstDomain(String schema) throws AonConnectionException;

	public abstract String getDomainDatabase(String domainName) throws AonConnectionException ;
	
	public abstract List<String> getSchemaDomains(String schema) throws AonConnectionException;

	public abstract Connection getDomainConnection(String domain) throws AonConnectionException;
	
	public abstract Connection getMetadataConnection(String schema) throws AonConnectionException;
}
