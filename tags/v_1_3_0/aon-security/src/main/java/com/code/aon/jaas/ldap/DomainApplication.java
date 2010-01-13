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
import com.code.aon.ldap.AonDN;
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

	private SecurityLdap ldap;
		
	private int status;
	
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
		
	public static DomainApplication getObject( SecurityLdap ldap, Entry entry, String domain ) {
		DomainApplication domainApplication = new DomainApplication(ldap, domain);
		domainApplication.setId(entry.getAsString("cn"));
		domainApplication.setDataSource(entry.getAsString(DATA_SOURCE_ATTRIBUTE));
		domainApplication.setStatus(entry.getAsInteger(STATUS_ATTRIBUTE));
		return domainApplication;
	}
	
	public static DomainApplication get( SecurityLdap ldap, String domainName, String application ) {
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
				dsmd = this.ldap.getDataSourceMetaData(entry);
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
				IRelation profile = this.ldap.getRelation(entry);
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
				IRelation profile = this.ldap.getRelation(entry);
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
				IRelation user = this.ldap.getRelation(entry);
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
				ldap.updateRelation(dn, relation);
			} else {
				Entry entry = ldap.getDomainApplicationProfile( session, relation, dn);
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
	
}
