package com.code.aon.jaas.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.Name;

import org.apache.commons.lang.ArrayUtils;
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
import com.code.aon.jaas.client.ast.core.User;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ldap.Scope;

public class Domain implements IDomain, ILdapConstants, ILdapSecurityConstants, IAonObjectClasses {
	
	/** Obtiene un logger apropiado. */
	private static final Log LOGGER = LogFactory.getLog( Domain.class.getName() );

	private static final long serialVersionUID = -8630396330009341954L;

	/** Domain identifier. */
	private String id;

	private BasicLdap ldap;
	
	private int status;
	
	public Domain(BasicLdap ldap) {
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
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
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
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public IDomainApplication getDomainApplication(String name) {
		return DomainApplication.get(this.ldap, this.id, name);
	}

	@Override
	public IUser getStandaloneUser(String name) {
		Entry entry = getUser(this.ldap, this.id, name);
		if ( entry != null ) {
			return getUser(entry);
		}
		return null;
	}

	@Override
	public IDomainApplication remove(String appName) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public void remove(IUser user) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public void setAccessPolicy(IAccessPolicy access) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public void setDataSourceMetaData(IDataSourceMetaData dsmt) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public Map<String, IUser> standaloneUsers() {
		Map<String,IUser> map = new HashMap<String, IUser>();
		for( IUser user : getUsers(this.id) ) {
			map.put( user.getId(), user);
		}
		return map;
	}

	@Override
	public IUser update(IUser user, String oldUserId)
			throws UserAlreadyExistException {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public void accept(INodeVisitor visitor) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
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

	public static Name getDN( String domainName ) {
		return NameResolver.getDomainDN(domainName);
	}
	
	private static Domain getObject( BasicLdap ldap, Entry entry ) {
		Domain domain = new Domain(ldap);
		domain.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		domain.setStatus(entry.getAsInteger(STATUS_ATTRIBUTE));
		return domain;
	}

	public static Domain get( BasicLdap ldap, String domainId ) {
		Domain domain = null;
		Name dn = getDN(domainId);
		Entry entry = ldap.get( dn, DOMAIN );
		if ( entry != null ) {
			domain = getObject(ldap, entry);	
		}
		return domain;
	}
	
	private IAccessPolicy getAccessPolicy( Entry entry ) {
		AccessPolicy accessPolicy = new AccessPolicy();
		accessPolicy.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		accessPolicy.setExceptionThrowableIfMaximumExceeded(entry.getAsBoolean(EXCEPTION_THROWABLE_IF_MAXIMUM_EXCEEDED_ATTRIBUTE));
		accessPolicy.setMaxAllowedUsers(entry.getAsInteger(MAX_ALLOWED_USERS_ATTRIBUTE));
		accessPolicy.setMaxDefinedUsers(entry.getAsInteger(MAX_DEFINED_USERS_ATTRIBUTE));
		accessPolicy.setMaxSessions4User(entry.getAsInteger(MAX_SESSIONS4_USER_ATTRIBUTE));
		return accessPolicy;
	}

	private IAccessPolicy getAccessPolicy( String domainName ) {
		IAccessPolicy accessPolicy = null;
		try {
			LdapSession session = ldap.getLdapSession();
			String objectClass = NameResolver.getObjectClass(ACCESS_POLICY);
			Name dn = getDN(domainName);
			Entry entry = session.searchOne( dn, objectClass );
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
			String objectClass = NameResolver.getObjectClass(DOMAIN_APPLICATION);
			Name dn = DomainApplication.getParentDN(domainName);
			List<Entry> list = session.search(dn, objectClass );
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
	
	public static Entry getUser( BasicLdap ldap, String domainId, String userId ) {
		Name dn = NameResolver.getUserDN(domainId, userId);
		return ldap.get( dn, USER );
	}	
	
	private static User getUser( Entry entry ) {
		User user = new User();
		user.setId(entry.getAsString(USER_ID_ATTRIBUTE));
		user.setName(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		byte[] password = entry.getAsByteArray(USER_PASSWORD_ATTRIBUTE);		
		int offset = ArrayUtils.indexOf( password, (byte) '}' ) + 1;
		user.setPasswd( new String(password, offset, password.length-offset) );
		if ( entry.containsKey(DESCRIPTION_ATTRIBUTE) ) {
			user.setDescription(entry.getAsString(DESCRIPTION_ATTRIBUTE));			
		}
		return user;
	}
		
	
	private List<IUser> getUsers( String domainName ) {
		List<IUser> users = new LinkedList<IUser>();
		try {
			String objectClass = NameResolver.getObjectClass(USER);
			Name dn = NameResolver.getUsersDN( domainName );
			List<Entry> list = ldap.getLdapSession().search( dn, objectClass, Scope.SUBTREE_SCOPE );
			for( Entry entry : list ) {
				IUser user = getUser(entry);
				users.add(user);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return users;
	}
	
}
