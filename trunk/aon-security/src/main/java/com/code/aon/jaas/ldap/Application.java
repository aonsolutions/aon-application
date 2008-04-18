package com.code.aon.jaas.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IRole;
import com.code.aon.jaas.deployment.event.SubDeployerEvent;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;

public class Application implements IApplication, ILdapConstants, ILdapSecurityConstants {

	private static final long serialVersionUID = -7774786273060605086L;

	/** Obtiene un logger apropiado. */
	private static final Log LOGGER = LogFactory.getLog( Application.class.getName() );
	
	/** Domain identifier. */
	private String id;

    /** Application description. */
    private String description;
    
    /** Application context. */
    private String context;
	
	private SecurityLdap ldap;
	
	public Application(SecurityLdap ldap) {
		this.ldap = ldap;
	}
	
    /**
     * Assigns domain identifier.
     * 
     * @param string
     */
	public void setId(String string) {
		this.id = string;
	}
	
	@Override
	public Collection<IDomain> domains() {
		throw new UnsupportedOperationException("Not supported!");
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setContext(String context) {
		this.context = context;
	}

	@Override
	public String getContext() {
		return ( this.context != null ) ? this.context : "/" + this.id;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public IDomain getDomain(String name) {
		return Domain.get(this.ldap, name);
	}

	@Override
	public String getHashAlgorithm() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public String getHashEncoding() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IRole getRole(String name) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public String getRoles() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public String getSecurityDomain() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IDomain remove(IDomain domain) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Collection<IRole> roles() {
		return getApplicationRoles(this.id);
	}

	@Override
	public void accept(INodeVisitor visitor) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void domainFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void methodPermissionFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void roleFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void securityPermissionFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void vendorDescriptorFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException("Not supported!");
	}

	public static DistinguishedName getDN( String application ) {
		return new DistinguishedName( SecurityLdap.getCN(application), APPLICATIONS_DN );
	}
	
	private static Application getObject( SecurityLdap ldap, Entry entry ) {
		Application application = new Application(ldap);
		application.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		application.setDescription(entry.getAsString(DESCRIPTION_ATTRIBUTE));
		return application;
	}
	
	public static Application get( SecurityLdap ldap, String applicationId ) {
		LdapSession session = null;
		Application application = null;
		try {
			session = ldap.getLdapSession();
			String objectClass = SecurityLdap.getObjectClass(APPLICATION_OBJECT_CLASS);
			DistinguishedName dn = getDN(applicationId);
			Entry entry = session.get( dn.toString(), objectClass );
			application = getObject(ldap, entry);
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			ldap.closeSession(session);
		}
		return application;
	}
	
	public Collection<IRole> getApplicationRoles( String application ) {
		LdapSession session = null;
		List<IRole> roles = new ArrayList<IRole>();
		try {
			session = this.ldap.getLdapSession();
			String objectClass = SecurityLdap.getObjectClass(ROLE_OBJECT_CLASS);
			DistinguishedName dn = this.ldap.getRolesDN(application);
			List<Entry> list = session.search(dn.toString(), objectClass );
			for( Entry entry : list ) {
				IRole role = this.ldap.getRole(entry);
				roles.add(role);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession(session);
		}
		return roles;
	}
	
}
