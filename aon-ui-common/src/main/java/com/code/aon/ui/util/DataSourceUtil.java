package com.code.aon.ui.util;

import java.util.Properties;

import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.common.util.ConnectionProvider;
import com.code.aon.jaas.auth.AuthPrincipal;
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
    	String domain = DomainResolver.getDomain(server);
    	String application = DomainResolver.getApplication(context);
    	BasicPrincipal bp = new BasicPrincipal(domain, application);
    	return ConnectionProvider.getDBProperties(new AuthPrincipal(bp.getName()));
	}	
    
}
