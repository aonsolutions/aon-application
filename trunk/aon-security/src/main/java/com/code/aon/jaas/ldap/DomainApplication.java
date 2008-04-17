package com.code.aon.jaas.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;

public class DomainApplication implements IDomainApplication, ILdapConstants, ILdapSecurityConstants {

	private static final long serialVersionUID = 2524989244667666833L;

	/** Obtiene un logger apropiado. */
	private static final Log LOGGER = LogFactory.getLog( DomainApplication.class.getName() );
	
	/** Domain identifier. */
	private String id;
	
	private String domain;
	
	private String dataSource;

	private SecurityLdap ldap;
	
	public DomainApplication(SecurityLdap ldap, String domain) {
		this.ldap = ldap;
		this.domain = domain;
	}
	
	/**
     * Domain application identifier.
     * 
     * @param string
     */
	public void setId(String string) {
		this.id = string;
	}
	
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public IDataSourceMetaData getDataSourceMetaData() {
		return getDataSourceMetaData(this.dataSource);
	}

	@Override
	public IRelation getProfile(String name) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IRelation getUser(String name) {
		return ldap.getUserRelation(this.domain, this.id, name);
	}

	@Override
	public boolean isProfileInUsers(String profile) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Collection<IRelation> profiles() {
		List<IRelation> list = new ArrayList<IRelation>();
		list.addAll( getApplicationProfiles(this.id) );
		list.addAll( getDomainApplicationProfiles(this.domain,this.id) );
		return list;
	}

	@Override
	public void removeProfile(IRelation relation) {
		removeProfile(this.id, this.domain, relation);
	}

	@Override
	public void removeUser(IRelation relation) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public IRelation updateProfile(IRelation relation) {
		return updateProfile(this.id, this.domain, relation);
	}

	@Override
	public IRelation updateUser(IRelation relation) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public Collection<IRelation> users() {
		return getDomainApplicationUsers( this.domain, this.id );
	}

	@Override
	public void accept(INodeVisitor visitor) {
		throw new UnsupportedOperationException("Not supported!");
	}

	@Override
	public String getId() {
		return this.id;
	}

	public static DistinguishedName getParentDN( String domainName ) {
		return new DistinguishedName( APPLICATIONS_DN, Domain.getDN(domainName) );
	}
	
	public static DistinguishedName getDN( String domainName, String application ) {
		return new DistinguishedName( SecurityLdap.getCN(application), getParentDN(domainName) );
	}
		
	public static DomainApplication getObject( SecurityLdap ldap, Entry entry, String domain ) {
		DomainApplication domainApplication = new DomainApplication(ldap, domain);
		domainApplication.setId(entry.getAsString("cn"));
		domainApplication.setDataSource(entry.getAsString(DATA_SOURCE_ATTRIBUTE));
		return domainApplication;
	}
	
	public static IDomainApplication get( SecurityLdap ldap, String domainName, String application ) {
		LdapSession session = null;
		IDomainApplication domainApplication = null;
		try {
			session = ldap.getLdapSession();
			String objectClass = SecurityLdap.getObjectClass(DOMAIN_APPLICATION_OBJECT_CLASS);
			DistinguishedName dn = getDN(domainName, application);
			Entry entry = session.get( dn.toString(), objectClass );
			if ( entry != null ) {
				domainApplication = getObject( ldap, entry, domainName);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			ldap.closeSession(session);
		}
		return domainApplication;
	}

	public IDataSourceMetaData getDataSourceMetaData( String dn ) {
		LdapSession session = null;
		IDataSourceMetaData dsmd = null;
		try {
			session = this.ldap.getLdapSession();
			String objectClass = this.ldap.getObjectClass(DB_CONNECTION_OBJECT_CLASS);
			Entry entry = session.get( dn, objectClass );
			if ( entry != null ) {
				dsmd = this.ldap.getDataSourceMetaData(entry);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession(session);
		}
		return dsmd;
	}
	
	public Collection<IRelation> getApplicationProfiles( String application ) {
		LdapSession session = null;
		List<IRelation> profiles = new ArrayList<IRelation>();
		try {
			session = this.ldap.getLdapSession();
			String objectClass = this.ldap.getObjectClass(PROFILE_OBJECT_CLASS);
			DistinguishedName dn = this.ldap.getApplicationProfilesDN(application);
			List<Entry> list = session.search(dn.toString(), objectClass );
			for( Entry entry : list ) {
				IRelation profile = this.ldap.getRelation(entry);
				profiles.add(profile);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession(session);
		}
		return profiles;
	}	

	public Collection<IRelation> getDomainApplicationProfiles( String domainId, String application ) {
		LdapSession session = null;
		List<IRelation> profiles = new ArrayList<IRelation>();
		try {
			session = this.ldap.getLdapSession();
			String objectClass = this.ldap.getObjectClass(DOMAIN_APPLICATION_PROFILE_OBJECT_CLASS);
			DistinguishedName dn = this.ldap.getDomainApplicationProfilesDN(domainId, application);
			List<Entry> list = session.search(dn.toString(), objectClass );
			for( Entry entry : list ) {
				IRelation profile = this.ldap.getRelation(entry);
				profiles.add(profile);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession(session);
		}
		return profiles;
	}
	
	public Collection<IRelation> getDomainApplicationUsers( String domainName, String application ) {
		LdapSession session = null;
		List<IRelation> users = new ArrayList<IRelation>();
		try {
			session = this.ldap.getLdapSession();
			String objectClass = this.ldap.getObjectClass(DOMAIN_APPLICATION_USER_OBJECT_CLASS);
			DistinguishedName dn = this.ldap.getDomainApplicationUsersDN(domainName, application);
			List<Entry> list = session.search(dn.toString(), objectClass );
			for( Entry entry : list ) {
				IRelation user = this.ldap.getRelation(entry);
				users.add(user);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession(session);
		}
		return users;
	}
	
	public IRelation updateProfile(String appId, String domainId, IRelation relation) {
		LdapSession session = null;
		try {
			session = this.ldap.getLdapSession();
			String objectClass = SecurityLdap.getObjectClass(DOMAIN_APPLICATION_PROFILE_OBJECT_CLASS);
			DistinguishedName dn = SecurityLdap.getDomainApplicationProfileDN(domainId, appId, relation.getId());
			if ( session.exists(dn.toString(), objectClass) ) {
				ldap.updateProfile(dn, relation);
			} else {
				Entry entry = ldap.getDomainApplicationProfile( session, relation, dn);
				session.add(entry);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession(session);
		}
		return relation;
	}

	public void removeProfile(String appId, String domainId, IRelation relation) {
		DistinguishedName dn = SecurityLdap.getDomainApplicationProfileDN(domainId, appId, relation.getId());
		this.ldap.delete(dn);
	}
	
}
