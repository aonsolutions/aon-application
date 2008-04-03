package com.code.aon.jaas.vendor.jboss;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.management.ObjectName;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.auth.AuthInfoLdap;
import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.client.ast.IOption;

public class LdapLoginModule extends JBossLoginModule {
	
	private static final String JBOSS_SECURITY_DOMAIN = "jboss.security.security_domain";
	
    /** Obtiene un logger apropiado. */
	private static final Log LOGGER = LogFactory.getLog( LdapLoginModule.class.getName() );
	
	private Properties ldapProperties = new Properties();

	@SuppressWarnings("unchecked")
	public Map updateOptions( Map map ) {
		Map newOptions = new HashMap();
    	try {
    		String objectName = (String) map.get( IConstants.DEPLOYER_OBJECT_NAME );
    		ObjectName name = new ObjectName(objectName);
    		Map<String, IOption> options = 
    			(Map<String, IOption>) getMBeanServer().invoke( name, "getOptions",
    						new Object[] {},new String[] {} );
    		for( IOption option : options.values() ) {
    			if (! map.containsKey(option.getName()) ) {
    				newOptions.put( option.getName(), option.getValue() );
    			}
    		}
    		String securityDomain = (String) map.get(JBOSS_SECURITY_DOMAIN);
    		newOptions.put(IConstants.SECURITY_DOMAIN, securityDomain);
    	} catch (Exception e) {
    		LOGGER.fatal( "Error updating options. " + e.getMessage(), e );
        }
    	return newOptions;
	}
	
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {
		Map updatedOptions = updateOptions(options);
		super.initialize(subject, callbackHandler, sharedState, updatedOptions);
		this.ldapProperties = new Properties();
		for( Object o : updatedOptions.entrySet() ) {
			Map.Entry entry = (Map.Entry) o;
			String key = (String) entry.getKey();
			if ( key.startsWith("java.naming") ) {
				ldapProperties.put( key, entry.getValue() );
			}
		}
	}

	/**
     * Load Domain applications and users. 
	 * 
	 * @param domain
	 */
	@SuppressWarnings("unchecked")
	protected void load(String domain) {
        this.authInfo = new AuthInfoLdap( this.ldapProperties );
	}

}
