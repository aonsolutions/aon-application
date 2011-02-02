package com.code.aon.jaas.vendor.jboss;

import java.util.HashMap;
import java.util.Map;

import javax.management.ObjectName;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.client.ast.IOption;
import com.code.aon.jaas.ldap.AuthInfo;
import com.code.aon.jaas.ldap.SecurityLdap;

public class LdapLoginModule extends JBossLoginModule {
	
	private static final String JBOSS_SECURITY_DOMAIN = "jboss.security.security_domain";
	
    /** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(LdapLoginModule.class);

	@SuppressWarnings("unchecked")
	public Map updateOptions( Map map ) {
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
	@SuppressWarnings("unchecked")
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

}
