/**
 * 
 */
package com.code.aon.jaas.vendor.jboss;

import java.net.URL;
import java.security.Principal;
import java.util.Map;
import java.util.Properties;

import javax.management.ObjectName;

import org.jboss.system.ServiceMBeanSupport;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IOption;
import com.code.aon.jaas.client.ast.core.AstLoader;
import com.code.aon.jaas.ldap.SecurityLdap;
import com.code.aon.jaas.storage.ApplicationsStorage;

/**
 * @author Consulting & Development. Aimar Tellitu - 03/04/2008
 *  
 * @jmx:mbean name="jboss.admin:service=AonLdap" extends="org.jboss.system.ServiceMBean"
 */
public class JBossLdap extends ServiceMBeanSupport implements JBossLdapMBean {

	private Map<String, IOption> options;
	
	private Properties ldapProperties;
	
	private SecurityLdap ldap;

	protected void startService() throws Exception {
		super.startService();
		ObjectName oname = new ObjectName(JBossMainDeployerMBean.OBJECT_NAME);
		URL config = 
			(URL) server.invoke(oname, "getConfigResource", new Object[] { null }, new String[] { String.class.getName() });
		ApplicationsStorage as = (ApplicationsStorage) AstLoader.getInstance().parse( 0, config.openStream() );
		this.options = as.options();
		this.ldapProperties = new Properties();
		for( IOption option : options.values() ) {
			if ( option.getName().startsWith("java.naming") ) {
				ldapProperties.put( option.getName(), option.getValue() );
			}
		}
		ldap = new SecurityLdap( this.ldapProperties );
	}
	
	protected void stopService() throws Exception {
		super.stopService();
	}

	public Map<String, IOption> getOptions() {
		return options;
	}

	public Properties getLdapProperties() {
		return ldapProperties;
	}
	
	public Properties getDSMDProperties(Principal principal) {
		AuthPrincipal p = (AuthPrincipal) principal;
		String domainName = p.getDomain();
		String application = ldap.getApplication( p.getContext() );
		IDataSourceMetaData dataSource = ldap.getDataSourceMetaData(domainName, application);
		if ( dataSource != null ) {
			return dataSource.getProperties();
		}
		return null;
	}
	
}
