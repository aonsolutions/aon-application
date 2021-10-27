package net.aonsolutions.core.pool;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

final class NoSuchSchemaConnectionInfo extends ConnectionInfo {
	
	private final String schema;

	NoSuchSchemaConnectionInfo(String schema) {
		this.schema = schema;
	}

	@Override
	public String getUser(String schema) {
		return null;
	}

	@Override
	public String getUrl(String schema) {
		return null;
	}

	@Override
	public String getUseSSL(String schema) {
		return null;
	}

	@Override
	public String getTimeZone(String schema) {
		return null;
	}

	@Override
	public String getPassword(String schema) {
		return null;
	}

	@Override
	public String getSchemaUrl(String schema) {
		return null;
	}

	@Override
	public String getDriverClass(String schema) {
		return null;
	}

	@Override
	public List<String> getSchemas() throws AonConnectionException {
		throw new NoSuchShemaException(schema);
	}

	@Override
	public String getSchemaFirstDomain(String schema) throws AonConnectionException {
		throw new NoSuchShemaException(this.schema);
	}

	@Override
	public List<String> getSchemaDomains(String schema) throws AonConnectionException {
		throw new NoSuchShemaException(this.schema);
	}

	@Override
	public Connection getMetadataConnection(String schema) throws AonConnectionException {
		throw new NoSuchShemaException(this.schema);
	}

	@Override
	public Map<String, String> getDomains() throws AonConnectionException {
		throw new NoSuchShemaException(this.schema);
	}

	@Override
	public Map<String, Integer> getDomainMap() throws AonConnectionException {
		throw new NoSuchShemaException(this.schema);
	}

	@Override
	public String getDomainDatabase(String domainName) throws AonConnectionException {
		throw new NoSuchShemaException(this.schema);
	}

	@Override
	public Connection getDomainConnection(String domain) throws AonConnectionException {
		throw new NoSuchShemaException(this.schema);
	}

	@Override
	public Connection getConnection(String schema) throws AonConnectionException {
		throw new NoSuchShemaException(this.schema);
	}
}