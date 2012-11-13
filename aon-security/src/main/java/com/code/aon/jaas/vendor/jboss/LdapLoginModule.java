package com.code.aon.jaas.vendor.jboss;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.management.ObjectName;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.auth.spi.db.User;
import com.code.aon.jaas.auth.spi.db.Util;
import com.code.aon.jaas.client.ast.IOption;
import com.code.aon.jaas.ldap.AuthInfo;
import com.code.aon.jaas.ldap.SecurityLdap;

public class LdapLoginModule extends JBossLoginModule {
	
	private static final String JBOSS_SECURITY_DOMAIN = "jboss.security.security_domain";
	
    /** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(LdapLoginModule.class);

	@SuppressWarnings("unchecked")
	private Map updateOptions( Map map ) {
		Map newOptions = new HashMap();
    	try {
    		String objectName = (String) map.get( IConstants.DEPLOYER_OBJECT_NAME );
    		newOptions.put(IConstants.DEPLOYER_OBJECT_NAME, objectName);    		
    		String securityDomain = (String) map.get(JBOSS_SECURITY_DOMAIN);
    		newOptions.put(IConstants.SECURITY_DOMAIN, securityDomain);
    		
    		ObjectName name = new ObjectName(objectName);
    		Map<String, IOption> options = 
    			(Map<String, IOption>) getMBeanServer().invoke( name, "getOptions",
    						new Object[] {},new String[] {} );
    		
    		for( IOption option : options.values() ) {
    			if (! map.containsKey(option.getName()) ) {
    				newOptions.put( option.getName(), option.getValue() );
    			}
    		}
    	} catch (Throwable th) {
    		LOGGER.error( "Error updating options", th );
        }
    	return newOptions;
	}
	
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		Map updatedOptions = updateOptions(options);
		super.initialize(subject, callbackHandler, sharedState, updatedOptions);
	}

	/**
     * Load Domain applications and users. 
	 * 
	 * @param domain
	 */
	protected void load(String domain) {
		SecurityLdap ldap;
		try {
			ldap = (SecurityLdap) getMBeanServer().invoke( new ObjectName(this.objectName), "getSecurityLdap",
						new Object[] {},new String[] {} );
	        this.authInfo = new AuthInfo( ldap );			
		} catch (Throwable th) {
			LOGGER.error( "Error getting SecurityLdap", th );
		}
	}

	@Override
	protected void validateLoggedUsers() throws LoginException {
		super.validateLoggedUsers();
		AuthPrincipal principal = (AuthPrincipal) getIdentity();
		updatePrincipal(principal);
	}
	
	private void updatePrincipal( AuthPrincipal principal ) {
		Properties properties = null;
		String domainName = principal.getDomain();
		String applicationName = StringUtils.substringAfter( principal.getContext(), "/" );
		try {
			ObjectName oname = new ObjectName(this.objectName);
			Object[] params = { domainName, applicationName };
			String[] sig = { String.class.getName(), String.class.getName() };
			properties = (Properties) getMBeanServer().invoke(oname, "getConnectionProperties", params, sig);
		} catch (Throwable th) {
			LOGGER.error( "Error getting connection properties", th );
		}		
		if ( properties != null ) {
			Connection connection = null;
			try {
				Util dbUtil = new Util(properties);
				connection = dbUtil.createConnection(null);
				Integer domainId = dbUtil.getDomainId(domainName);
				if ( domainId != null ) {
					principal.setDomainId(domainId);
				} else {
					LOGGER.error("Domain {} not found", domainName);
				}
				User user = dbUtil.getUser(domainId, principal.getShortName());
				if ( user != null ) {
					principal.setUserId(user.getId());
					principal.setUserDomainId(user.getDomain());
				} else {
					LOGGER.error("User {} not found in domain {}", principal.getShortName(), domainId);
				}
				Integer applicationId = dbUtil.getApplicationId(applicationName);
				if ( applicationId != null ) {
					principal.setApplicationId(applicationId);
				} else {
					LOGGER.error("Application {} not found", applicationName);
				}
			} catch (SQLException ex) {
				LOGGER.error("Query failed", ex);
			} catch (ClassNotFoundException e) {
				LOGGER.error("JDBC driver not found", e);
			} finally {
				DbUtils.closeQuietly(connection);
			}
		}
	}
	
}
