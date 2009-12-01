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
import com.code.aon.jaas.client.ast.core.AccessPolicy;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;

public class Domain implements IDomain, ILdapConstants, ILdapSecurityConstants, IAonObjectClasses {
	
    /** Obtiene un logger apropiado. */
	private static final Log LOGGER = LogFactory.getLog( Domain.class.getName() );

	private static final long serialVersionUID = -8630396330009341954L;
	
	public static final String OBJECT_CLASS = "aonDomain";

	/** Domain identifier. */
	private String id;

	private SecurityLdap ldap;
	
	private int status;
	
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
		return getAccessPolicy(this.id);
	}

	@Override
	public IDataSourceMetaData getDataSourceMetaData() {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IDomainApplication getDomainApplication(String name) {
		return DomainApplication.get(this.ldap, this.id, name);
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
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public static DistinguishedName getDN( String domainName ) {
		return AonDN.getDomainDN(domainName);
	}
	
	private static Domain getObject( SecurityLdap ldap, Entry entry ) {
		Domain domain = new Domain(ldap);
		domain.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		domain.setStatus(entry.getAsInteger(STATUS_ATTRIBUTE));
		return domain;
	}

	public static Domain get( SecurityLdap ldap, String domainId ) {
		Domain domain = null;
		try {
			String objectClass = LdapSession.getObjectClass(OBJECT_CLASS);
			DistinguishedName dn = getDN(domainId);
			Entry entry = ldap.getLdapSession().get( dn.toString(), objectClass );
			domain = getObject(ldap, entry);
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return domain;
	}
	
	private IAccessPolicy getAccessPolicy( Entry entry ) {
		AccessPolicy accessPolicy = new AccessPolicy();
		accessPolicy.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		accessPolicy.setExceptionThrowableIfMaximumExceeded(entry.getAsBoolean("exceptionThrowableIfMaximumExceeded"));
		accessPolicy.setMaxAllowedUsers(entry.getAsInteger("maxAllowedUsers"));
		accessPolicy.setMaxDefinedUsers(entry.getAsInteger("maxDefinedUsers"));
		accessPolicy.setMaxSessions4User(entry.getAsInteger("maxSessions4User"));
		return accessPolicy;
	}

	private IAccessPolicy getAccessPolicy( String domainName ) {
		IAccessPolicy accessPolicy = null;
		try {
			LdapSession session = ldap.getLdapSession();
			String objectClass = LdapSession.getObjectClass(ACCESS_POLICY);
			DistinguishedName dn = getDN(domainName);
			Entry entry = session.searchOne( dn.toString(), objectClass );
			if ( entry != null ) {
				accessPolicy = getAccessPolicy( entry );
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return accessPolicy;
	}

	private Collection<IDomainApplication> getDomainApplications( String domainName ) {
		List<IDomainApplication> applications = new ArrayList<IDomainApplication>();
		try {
			LdapSession session = this.ldap.getLdapSession();
			String objectClass = LdapSession.getObjectClass(DOMAIN_APPLICATION);
			DistinguishedName dn = DomainApplication.getParentDN(domainName);
			List<Entry> list = session.search(dn.toString(), objectClass );
			for( Entry entry : list ) {
				IDomainApplication application = DomainApplication.getObject(this.ldap, entry, domainName);
				applications.add(application);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession();
		}
		return applications;
	}
	
}
