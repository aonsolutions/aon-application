package com.code.aon.jaas.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.util.Util;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IRole;
import com.code.aon.jaas.client.ast.core.Role;
import com.code.aon.jaas.deployment.event.SubDeployerEvent;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.NameResolver;

public class Application implements IApplication, ILdapConstants, ILdapSecurityConstants, IAonObjectClasses {

	private static final long serialVersionUID = -7774786273060605086L;

	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(Application.class);
	
	/** Domain identifier. */
	private String id;

    /** Application description. */
    private String description;
    
    /** Application context. */
    private String context;
	
    /** 
     * Hash algorithm used to password generation. Default null.
     * SHA-1 (Secure Hash Algorithm 1) is slower than MD5, but the message digest is larger, 
     * which makes it more resistant to brute force attacks. Therefore, it is recommended 
     * that Secure Hash Algorithm is preferred to MD5 for all of your digest needs. Note, 
     * SHA-1 now has even higher strength brothers, SHA-256, SHA-384, and SHA-512 for 256, 
     * 384 and 512-bit digests respectively.
     */
	private String hashAlgorithm;

    /** Hash encoding format. Default BASE64. */
	private String hashEncoding = Util.BASE64_ENCODING;
    
	private BasicLdap ldap;
	
	public Application(BasicLdap ldap) {
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
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public void setContext(String context) {
		this.context = context;
	}
	
	public void setHashAlgorithm(String hashAlgorithm) {
		this.hashAlgorithm = hashAlgorithm;
	}

	public void setHashEncoding(String hashEncoding) {
		this.hashEncoding = hashEncoding;
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
		return this.hashAlgorithm;
	}

	@Override
	public String getHashEncoding() {
		return this.hashEncoding;
	}

	@Override
	public IRole getRole(String name) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public String getRoles() {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public String getSecurityDomain() {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public IDomain remove(IDomain domain) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public Collection<IRole> roles() {
		return getApplicationRoles(this.id);
	}

	@Override
	public void accept(INodeVisitor visitor) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void domainFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public void methodPermissionFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public void roleFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public void securityPermissionFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public void vendorDescriptorFound(SubDeployerEvent event) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	public static Name getDN( String application ) {
		return NameResolver.getApplicationDN(application);
	}
	
	private static Application getObject( SecurityLdap ldap, Entry entry ) {
		Application application = new Application(ldap);
		application.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		application.setDescription(entry.getAsString(DESCRIPTION_ATTRIBUTE));
		return application;
	}
	
	public static Application get( SecurityLdap ldap, String applicationId ) {
		Application application = null;
		Name dn = getDN(applicationId);
		Entry entry = ldap.get( dn, APPLICATION );
		if ( entry != null ) {
			application = getObject(ldap, entry);
		}
		return application;
	}
	
	public Collection<IRole> getApplicationRoles( String application ) {
		List<IRole> roles = new ArrayList<IRole>();
		try {
			LdapSession session = this.ldap.getLdapSession();
			String objectClass = NameResolver.getObjectClass(ROLE);
			Name dn = NameResolver.getApplicationRolesDN(application);
			List<Entry> list = session.search(dn, objectClass );
			for( Entry entry : list ) {
				IRole role = getRole(entry);
				roles.add(role);
			}
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			this.ldap.closeSession();
		}
		return roles;
	}
	
	public IRole getRole( Entry entry ) {
		Role role = new Role();
		role.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		return role;
	}	
	
}
