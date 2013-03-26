package com.code.aon.ui.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.ConnectionProvider;
import com.code.aon.ui.common.controller.DomainResolver;

/**
 * Default implementation of the factory for creating Hibernate Configuration objects.
 */
public class DataSourceUtil {
	
	/**
	 * Gets the dB properties.
	 * 
	 * @return the dB properties
	 */
	public static Properties getDBProperties() {
		String server = AonUtil.getServerName();
		String context = AonUtil.getContextPath();
    	return DataSourceUtil.getDBProperties(server, context, AonUtil.isSkipLdap());
	}
	
	/**
	 * Gets the DB properties.
	 *
	 * @param server the server
	 * @param context the context
	 * @param skipLdap the skip ldap
	 * @return the DB properties
	 */
	public static Properties getDBProperties( String server, String context, boolean skipLdap ) {
    	String domain = DomainResolver.getDomain(server, skipLdap);
    	String application = DomainResolver.getApplication(context);
    	return ConnectionProvider.getDBProperties(domain, application);
	}	
 
	public static Integer getDomain( Connection connection, String host, boolean skipLdap ) throws SQLException {
		ResultSetHandler<Object> h = new ScalarHandler<Object>();
		QueryRunner run = new QueryRunner();
		Long count = (Long) run.query( connection, "SELECT count(id) FROM domain", h); 
		if ( count == 1 ) {
			return (Integer) run.query( connection, "SELECT id FROM domain", h);
		}
		String domainName = DomainResolver.getDomain(host, skipLdap);		
		Integer domainId = null;
		do {
			domainId = (Integer) run.query( connection, "SELECT id FROM domain WHERE name =?", h, domainName);
			if ( domainId == null ) {
				domainName = StringUtils.substringAfter(domainName, ".");	
			}
		} while ( (domainId == null) && StringUtils.contains(domainName, '.') );
		return domainId;
	}
	
}