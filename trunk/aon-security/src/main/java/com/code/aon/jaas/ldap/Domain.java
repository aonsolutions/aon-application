package com.code.aon.jaas.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.UserAlreadyExistException;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;

public class Domain implements IDomain, ILdapSecurityConstants {
	
    /** Obtiene un logger apropiado. */
	private static final Log LOGGER = LogFactory.getLog( Domain.class.getName() );

	private static final long serialVersionUID = -8630396330009341954L;

	/** Domain identifier. */
	private String id;

	private SecurityLdap ldap;
	
	public Domain(SecurityLdap ldap) {
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
	public void add(IUser user) throws UserAlreadyExistException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Collection<IDomainApplication> applications() {
		return getDomainApplications(this.id);
	}

	@Override
	public IAccessPolicy getAccessPolicy() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IDataSourceMetaData getDataSourceMetaData() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IDomainApplication getDomainApplication(String name) {
		Entry domainApplication = ldap.getDomainApplication(this.id, name);
		if ( domainApplication != null ) {
			return ldap.getDomainApplication(domainApplication, this.id);
		}
		return null;
	}

	@Override
	public IUser getStandaloneUser(String name) {
		Entry user = ldap.getUser(this.id, name);
		if ( user != null ) {
			return ldap.getUser(user);
		}
		return null;
	}

	@Override
	public IDomainApplication remove(String appName) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void remove(IUser user) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void setAccessPolicy(IAccessPolicy access) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void setDataSourceMetaData(IDataSourceMetaData dsmt) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Map<String, IUser> standaloneUsers() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IUser update(IUser user, String oldUserId)
			throws UserAlreadyExistException {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public void accept(INodeVisitor visitor) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public String getId() {
		return this.id;
	}

	private Collection<IDomainApplication> getDomainApplications( String domainName ) {
		LdapSession session = null;
		List<IDomainApplication> applications = new ArrayList<IDomainApplication>();
		try {
			session = this.ldap.getLdapSession();
			String objectClass = this.ldap.getObjectClass(DOMAIN_APPLICATION_OBJECT_CLASS);
			DistinguishedName dn = this.ldap.getDomainApplicationsDN(domainName);
			List<Entry> list = session.search(dn.toString(), objectClass );
			for( Entry entry : list ) {
				IDomainApplication application = this.ldap.getDomainApplication(entry, domainName);
				applications.add(application);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession(session);
		}
		return applications;
	}
	
}
