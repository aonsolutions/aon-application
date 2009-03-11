package com.code.aon.jaas.ldap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.INodeVisitor;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.core.DataSourceMetaData;
import com.code.aon.jaas.client.ast.core.Relation;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;

public class DomainApplication implements IDomainApplication, ILdapConstants, ILdapSecurityConstants, IAonObjectClasses {

	private static final long serialVersionUID = 2524989244667666833L;

	/** Obtiene un logger apropiado. */
	private static final Log LOGGER = LogFactory.getLog( DomainApplication.class.getName() );
	
	/** Domain identifier. */
	private String id;
	
	private String domain;
	
	private String dataSource;

	private BasicLdap ldap;
		
	private int status;
	
	public DomainApplication(BasicLdap ldap, String domain) {
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
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public IRelation getUser(String userId) {
		return getUserRelation(userId);
	}

	@Override
	public boolean isProfileInUsers(String profile) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
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
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public IRelation updateProfile(IRelation relation) {
		return updateProfile(this.id, this.domain, relation);
	}

	@Override
	public IRelation updateUser(IRelation relation) {
		throw new UnsupportedOperationException(SecurityLdap.NOT_SUPPORTED);
	}

	@Override
	public Collection<IRelation> users() {
		return getDomainApplicationUsers( this.domain, this.id );
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
	
	public static DistinguishedName getParentDN( String domainName ) {
		return AonDN.getDomainApplicationsDN(domainName);
	}
	
	public static DistinguishedName getDN( String domainName, String application ) {
		return AonDN.getDomainApplicationDN(domainName, application);
	}
		
	public static DomainApplication getObject( BasicLdap ldap, Entry entry, String domain ) {
		DomainApplication domainApplication = new DomainApplication(ldap, domain);
		domainApplication.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		domainApplication.setDataSource(entry.getAsString(DATA_SOURCE_ATTRIBUTE));
		domainApplication.setStatus(entry.getAsInteger(STATUS_ATTRIBUTE));
		return domainApplication;
	}
	
	public static DomainApplication get( BasicLdap ldap, String domainName, String application ) {
		DomainApplication domainApplication = null;
		try {
			String objectClass = LdapSession.getObjectClass(DOMAIN_APPLICATION);
			DistinguishedName dn = getDN(domainName, application);
			Entry entry = ldap.getLdapSession().get( dn.toString(), objectClass );
			if ( entry != null ) {
				domainApplication = getObject( ldap, entry, domainName);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return domainApplication;
	}

	public IDataSourceMetaData getDataSourceMetaData( String dn ) {
		IDataSourceMetaData dsmd = null;
		try {
			String objectClass = LdapSession.getObjectClass(DB_CONNECTION);
			Entry entry = this.ldap.getLdapSession().get( dn, objectClass );
			if ( entry != null ) {
				dsmd = getDataSourceMetaData(entry);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession();
		}
		return dsmd;
	}
	
	public Collection<IRelation> getApplicationProfiles( String application ) {
		List<IRelation> profiles = new ArrayList<IRelation>();
		try {
			String objectClass = LdapSession.getObjectClass(PROFILE);
			DistinguishedName dn = AonDN.getApplicationProfilesDN(application);
			List<Entry> list = this.ldap.getLdapSession().search(dn.toString(), objectClass );
			for( Entry entry : list ) {
				IRelation profile = getRelation(entry);
				profiles.add(profile);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession();
		}
		return profiles;
	}	

	public Collection<IRelation> getDomainApplicationProfiles( String domainId, String application ) {
		List<IRelation> profiles = new ArrayList<IRelation>();
		try {
			String objectClass = LdapSession.getObjectClass(DOMAIN_APPLICATION_PROFILE);
			DistinguishedName dn = AonDN.getDomainApplicationProfilesDN(domainId, application);
			List<Entry> list = this.ldap.getLdapSession().search(dn.toString(), objectClass );
			for( Entry entry : list ) {
				IRelation profile = getRelation(entry);
				profiles.add(profile);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession();
		}
		return profiles;
	}
	
	public Collection<IRelation> getDomainApplicationUsers( String domainName, String application ) {
		List<IRelation> users = new ArrayList<IRelation>();
		try {
			String objectClass = LdapSession.getObjectClass(DOMAIN_APPLICATION_USER);
			DistinguishedName dn = AonDN.getDomainApplicationUsersDN(domainName, application);
			List<Entry> list = this.ldap.getLdapSession().search(dn.toString(), objectClass );
			for( Entry entry : list ) {
				IRelation user = getRelation(entry);
				users.add(user);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession();
		}
		return users;
	}
	
	public IRelation updateProfile(String appId, String domainId, IRelation relation) {
		try {
			LdapSession session = this.ldap.getLdapSession();
			String objectClass = LdapSession.getObjectClass(DOMAIN_APPLICATION_PROFILE);
			DistinguishedName dn = AonDN.getDomainApplicationProfileDN(domainId, appId, relation.getId());
			if ( session.exists(dn.toString(), objectClass) ) {
				updateRelation(this.ldap, dn, relation);
			} else {
				Entry entry = getDomainApplicationProfile( session, relation, dn);
				session.add(entry);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			this.ldap.closeSession();
		}
		return relation;
	}

	public void removeProfile(String appId, String domainId, IRelation relation) {
		DistinguishedName dn = AonDN.getDomainApplicationProfileDN(domainId, appId, relation.getId());
		try {
			this.ldap.delete(dn);
		} catch (LdapException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}
	
	private IDataSourceMetaData getDataSourceMetaData( Entry entry ) {
		DataSourceMetaData dataSource = new DataSourceMetaData();
		dataSource.setUsername( entry.getAsString(USER_ID_ATTRIBUTE) );
		byte[] password = entry.getAsByteArray(USER_PASSWORD_ATTRIBUTE);
		dataSource.setPassword( new String(password) );
		dataSource.setConnectionURL( entry.getAsString(LABELED_URI_ATTRIBUTE) );
		dataSource.setDriverClass( entry.getAsString(DRIVER_CLASS_NAME_ATTRIBUTE) );
		return dataSource;
	}	
	

	private IRelation getRelation( Entry entry ) {
		Relation relation = new Relation();
		relation.setId(entry.getAsString(COMMON_NAME_ATTRIBUTE));
		for( Object member : entry.get(MEMBER_ATTRIBUTE) ) {
			DistinguishedName dn = new DistinguishedName( (String) member );
			relation.addRelation( dn.getLevelValue(0) );
		}
		return relation;
	}

	private Entry getDomainApplicationProfile( LdapSession session, IRelation relation, DistinguishedName dn ) {
		Entry entry = new Entry(dn.toString());
		String appId = dn.getLevelValue(2);
		String[] objectClasses = new String[] {TOP, GROUP_OF_NAMES, DOMAIN_APPLICATION_PROFILE};
		entry.addObjectClasses( objectClasses );
		for( String role : relation.relations() ) {
			DistinguishedName member = session.getFullDN( AonDN.getApplicationProfileDN(appId, role) );
			entry.put( MEMBER_ATTRIBUTE, member.toString() );
		}
		return entry;
	}	
	
	public Entry getDomainApplicationUser( String userId ) {
		Entry entry = null;
		try {
			String objectClass = LdapSession.getObjectClass(DOMAIN_APPLICATION_USER);
			DistinguishedName dn = AonDN.getDomainApplicationUserDN(this.domain, this.id, userId);
			entry = ldap.getLdapSession().get( dn.toString(), objectClass );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
		return entry;
	}
	
	@SuppressWarnings("unchecked")
	private IRelation getUserRelation(String userId) {
		Relation relation = null;
		Entry user = getDomainApplicationUser(userId);
		if ( user != null ) {
			relation = new Relation(userId);
			Object value = user.get( MEMBER_ATTRIBUTE );
			if ( value instanceof String ) {
				DistinguishedName member = new DistinguishedName( (String) value );
				relation.addRelation( member.getLevelValue(0) );
			} else {
				for( String profile : (List<String>) value ) {
					DistinguishedName dn = new DistinguishedName( profile );
					relation.addRelation( dn.getLevelValue(0) );
				}
			}
		}
		return relation;
	}		
	
	public static void updateRelation( BasicLdap ldap, DistinguishedName dn, IRelation relation ) {
		try {
			LdapSession session = ldap.getLdapSession();
			String appId = dn.getLevelValue(2);
			List<Object> members = new LinkedList<Object>();
			for( String role : relation.relations() ) {
				DistinguishedName member = session.getFullDN( AonDN.getApplicationProfileDN(appId, role) );
				members.add( member.toString() );
			}
			session.replaceAttribute(dn, MEMBER_ATTRIBUTE, members);
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			ldap.closeSession();
		}
	}	
	
}
