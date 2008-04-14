/**
 * 
 */
package com.code.aon.jaas.vendor.jboss;

import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.system.ServiceMBeanSupport;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IOption;
import com.code.aon.jaas.client.ast.core.Application;
import com.code.aon.jaas.client.ast.core.AstLoader;
import com.code.aon.jaas.ldap.SecurityLdap;
import com.code.aon.jaas.storage.ApplicationsStorage;

/**
 * @author Consulting & Development. Aimar Tellitu - 03/04/2008
 *  
 * @jmx:mbean name="jboss.admin:service=AonLdap" extends="org.jboss.system.ServiceMBean"
 */
public class JBossLdap extends ServiceMBeanSupport implements JBossLdapMBean {

	/** JBossSecurity Logger instance. */
	private static final Log LOGGER = LogFactory.getLog( JBossLdap.class.getName() );
	
	private Map<String, IOption> options;
	
	private SecurityLdap ldap;

	protected void startService() throws Exception {
		super.startService();
		ObjectName oname = new ObjectName(JBossMainDeployerMBean.OBJECT_NAME);
		URL config = 
			(URL) server.invoke(oname, "getConfigResource", new Object[] { null }, new String[] { String.class.getName() });
		ApplicationsStorage as = (ApplicationsStorage) AstLoader.getInstance().parse( 0, config.openStream() );
		this.options = as.options();
		Properties ldapProperties = new Properties();
		for( IOption option : options.values() ) {
			if ( option.getName().startsWith("java.naming") ) {
				ldapProperties.put( option.getName(), option.getValue() );
			}
		}
		this.ldap = new SecurityLdap( ldapProperties );
	}
	
	protected void stopService() throws Exception {
		super.stopService();
	}

	public Map<String, IOption> getOptions() {
		return options;
	}

	public SecurityLdap getSecurityLdap() {
		return ldap;
	}
	
	public Properties getDSMDProperties(Principal principal) {
		AuthPrincipal p = (AuthPrincipal) principal;
		String domainName = p.getDomain();
		String application = ldap.getApplicationId( p.getContext() );
		IDataSourceMetaData dataSource = ldap.getDataSourceMetaData(domainName, application);
		if ( dataSource != null ) {
			return dataSource.getProperties();
		}
		return null;
	}

	public IApplication getApplication4Ctx(String ctx) {
		if ( LOGGER.isDebugEnabled() ) {
			LOGGER.debug("Retrieving application for: CONTEXT[" + ctx + "]");
		}
		return this.ldap.getApplication4Ctx(ctx);
	}
	
	public IDomain getDomain(String appContext, String domainId) {
		if ( LOGGER.isDebugEnabled() ) {
			LOGGER.debug("Retrieving IDomain for: CONTEXT[" + appContext + "]" );
		}
		return this.ldap.getDomain(domainId);
	}

	public List getUserApplications(String domainId, String userId) {
		if ( LOGGER.isDebugEnabled() ) {
			LOGGER.debug("Retrieving user applications for: DOMAIN[" + domainId + "], USER [" + userId + "]" );
		}
		List<IApplication> applications = new ArrayList<IApplication>();
		for( String name : this.ldap.getUserApplications(domainId, userId) ) {
			applications.add( this.ldap.getApplication(name) );
		}
		return applications;
	}
	
}
