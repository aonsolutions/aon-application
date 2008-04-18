/**
 * 
 */
package com.code.aon.jaas.vendor.jboss;

import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.system.ServiceMBeanSupport;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.IOption;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.AstLoader;
import com.code.aon.jaas.ldap.Application;
import com.code.aon.jaas.ldap.Domain;
import com.code.aon.jaas.ldap.DomainApplication;
import com.code.aon.jaas.ldap.ILdapSecurityConstants;
import com.code.aon.jaas.ldap.SecurityLdap;
import com.code.aon.jaas.storage.ApplicationsStorage;
import com.code.aon.jaas.storage.StorageException;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.ILdapConstants;

/**
 * @author Consulting & Development. Aimar Tellitu - 03/04/2008
 *  
 * @jmx:mbean name="jboss.admin:service=AonLdap" extends="org.jboss.system.ServiceMBean"
 */
public class JBossLdap extends ServiceMBeanSupport implements JBossLdapMBean, ILdapConstants, ILdapSecurityConstants {

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
		IDomainApplication domainApplication = DomainApplication.get(this.ldap, domainName, application);
		if ( domainApplication != null ) {
			return domainApplication.getDataSourceMetaData().getProperties();
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
		return Domain.get(this.ldap, domainId);
	}

	public List getUserApplications(String domainId, String userId) {
		if ( LOGGER.isDebugEnabled() ) {
			LOGGER.debug("Retrieving user applications for: DOMAIN[" + domainId + "], USER [" + userId + "]" );
		}
		List<IApplication> applications = new ArrayList<IApplication>();
		for( String name : this.ldap.getUserApplications(domainId, userId) ) {
			applications.add( Application.get(this.ldap, name) );
		}
		return applications;
	}
	
    public IApplication getApplication(String name) {
		if ( LOGGER.isDebugEnabled() ) {
			LOGGER.debug("Retrieving application for: NAME[" + name + "]" );
		}
		return Application.get(this.ldap, name);   
	}

	public IRelation removeProfile(String appId, String domainId, IRelation relation) throws StorageException {
		if ( LOGGER.isDebugEnabled() ) {
			LOGGER.debug("Removing profile relation from: DOMAIN[" + domainId + "], APPLICATION[" + appId + "]" );
		}
		DistinguishedName dn = SecurityLdap.getApplicationProfileDN(appId, relation.getId());
		if ( this.ldap.exists(dn, PROFILE_OBJECT_CLASS) ) {
			this.ldap.delete(dn);
		} else {
			IDomainApplication domainApplication = DomainApplication.get(this.ldap, domainId, appId);
			domainApplication.removeProfile(relation);
		}
		return relation;
	}

	public IRelation updateProfile(String appId, String domainId, IRelation relation) throws StorageException {
		if ( LOGGER.isDebugEnabled() ) {
			LOGGER.debug("Updating profile relation for: DOMAIN[" + domainId + "], APPLICATION[" + appId + "]" );
		}
		DistinguishedName dn = SecurityLdap.getApplicationProfileDN(appId, relation.getId());
		if ( this.ldap.exists(dn, PROFILE_OBJECT_CLASS) ) {
			ldap.updateRelation( dn, relation );
		} else {
			IDomainApplication domainApplication = DomainApplication.get(this.ldap, domainId, appId);
			domainApplication.updateProfile(relation);
		}
		return relation;
	}
	
	public IRelation updateRelation(String appId, String domainId, IRelation relation) throws StorageException {
		if ( LOGGER.isDebugEnabled() ) {
			LOGGER.debug("Updating user relation for: DOMAIN[" + domainId + "], APPLICATION[" + appId + "]" );
		}
		DistinguishedName dn = SecurityLdap.getDomainApplicationUserDN(domainId, appId, relation.getId());
		if ( this.ldap.exists(dn, DOMAIN_APPLICATION_USER_OBJECT_CLASS) ) {
			this.ldap.updateRelation(dn, relation);
		}
		return relation;
	}
	
	public IRelation removeRelation(String appId, String domainId, IRelation relation) throws StorageException {
		if ( LOGGER.isDebugEnabled() ) {
			LOGGER.debug("Removing user relation from: DOMAIN[" + domainId + "], APPLICATION[" + appId + "]" );
		}
		DistinguishedName dn = SecurityLdap.getDomainApplicationUserDN(domainId, appId, relation.getId());
		if ( this.ldap.exists(dn, DOMAIN_APPLICATION_USER_OBJECT_CLASS) ) {
			this.ldap.deleteAttribute(dn, MEMBER_ATTRIBUTE);
		}
		return relation;
	}
	
}
