package com.code.aon.jaas.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IRole;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.AccessPolicy;
import com.code.aon.jaas.client.ast.core.DataSourceMetaData;
import com.code.aon.jaas.client.ast.core.Relation;
import com.code.aon.jaas.client.ast.core.Role;
import com.code.aon.jaas.client.ast.core.User;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.Scope;

public class SecurityLdap implements ILdapConstants, ILdapSecurityConstants {
	
    /** Obtiene un logger apropiado. */
	private static final Log LOGGER = LogFactory.getLog( SecurityLdap.class.getName() );
	
	private Properties properties;

	public SecurityLdap( Properties properties ) {
		this.properties = properties;
	}
	
	public LdapSession getLdapSession() throws LdapException {
		LdapSession session = new LdapSession();
		session.open(this.properties);
		return session;
	}
	
	public void closeSession( LdapSession session ) {
		try {
			if ( session != null ) {
				session.close();
			}
		} catch (LdapException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}
	
	public String getObjectClass( String objectClass ) {
		return "(" + OBJECT_CLASS + "=" + objectClass +  ")";
	}

	private String getCommonName( String cn ) {
		return "(" + getCN(cn) +  ")";
	}

	private String getUserId( String uid ) {
		return "(" + USER_ID + "=" + uid +  ")";
	}
	
	private String getCN( String cn ) {
		return COMMON_NAME + "=" + cn;
	}
	
	private String getAndExpression( String expression1, String expression2 ) {
		return "(&" + expression1 + expression2 + ")";
	}

	private DistinguishedName getDomainDN( String domainName ) {
		return new DistinguishedName( getCN(domainName), DOMAINS_DN );
	}

	public DistinguishedName getDomainApplicationsDN( String domainName ) {
		return new DistinguishedName( APPLICATIONS_DN, getDomainDN(domainName) );
	}
	
	private DistinguishedName getDomainApplicationDN( String domainName, String application ) {
		return new DistinguishedName( getCN(application), getDomainApplicationsDN(domainName) );
	}
	
	public DistinguishedName getDomainApplicationUsersDN( String domainName, String application ) {
		return new DistinguishedName( USERS_DN, getDomainApplicationDN(domainName, application) );
	}

	private DistinguishedName getUsersDN( String domainName ) {
		return new DistinguishedName( USERS_DN, getDomainDN(domainName) );
	}

	public DistinguishedName getDomainApplicationProfilesDN( String domainName, String application ) {
		return new DistinguishedName( PROFILES_DN, getDomainApplicationDN(domainName, application) );
	}

	private DistinguishedName getApplicationDN( String application ) {
		return new DistinguishedName( getCN(application), APPLICATIONS_DN );
	}
	
	public DistinguishedName getProfilesDN( String application ) {
		return new DistinguishedName( PROFILES_DN, getApplicationDN(application) );
	}

	public DistinguishedName getRolesDN( String application ) {
		return new DistinguishedName( ROLES_DN, getApplicationDN(application) );
	}
	
	public String getApplicationId( String context ) {
		String application = context;
		if ( application.startsWith("/") ) {
			application = application.substring(1);
		}
		int pos = application.lastIndexOf(".");
		if ( pos != -1 ) {
			application = application.substring(0, pos);
		}
		return application;
	}

	public boolean hasDomain( String domainName ) {
		LdapSession session = null;
		int count = -1;
		try {
			session = getLdapSession();
			String objectClass = getObjectClass(DOMAIN_OBJECT_CLASS);
			String cn = getCommonName( domainName );
			count = session.getCount( DOMAINS_DN, getAndExpression(objectClass, cn) );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession(session);
		}
		return (count > 0);
	}

	public boolean hasUser( String domainName, String application, String user ) {
		LdapSession session = null;
		int count = -1;
		try {
			session = getLdapSession();
			String objectClass = getObjectClass(DOMAIN_APPLICATION_USER_OBJECT_CLASS);
			String cn = getCommonName(user);
			DistinguishedName dn = getDomainApplicationUsersDN(domainName, application);
			count = session.getCount( dn.toString(), getAndExpression(objectClass, cn) );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession(session);
		}
		return (count > 0);
	}
	
	public Entry getUser( String domainName, String user ) {
		LdapSession session = null;
		Entry entry = null;
		try {
			session = getLdapSession();
			String objectClass = getObjectClass(USER_OBJECT_CLASS);
			String cn = getUserId(user);
			DistinguishedName dn = getUsersDN(domainName);
			entry = session.searchOne( dn.toString(), getAndExpression(objectClass, cn) );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession(session);
		}
		return entry;
	}

	public Entry getDomainApplicationUser( String domainName, String application, String user ) {
		LdapSession session = null;
		Entry entry = null;
		try {
			session = getLdapSession();
			String objectClass = getObjectClass(DOMAIN_APPLICATION_USER_OBJECT_CLASS);
			String cn = getCommonName(user);
			DistinguishedName dn = getDomainApplicationUsersDN(domainName, application);
			entry = session.searchOne( dn.toString(), getAndExpression(objectClass, cn) );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession(session);
		}
		return entry;
	}

	public Entry getApplicationProfile( String domainName, String application, String profileName ) {
		LdapSession session = null;
		Entry entry = null;
		try {
			session = getLdapSession();
			String objectClass = getObjectClass(PROFILE_OBJECT_CLASS);
			String cn = getCommonName(profileName);
			DistinguishedName dn = getProfilesDN(application);
			entry = session.searchOne( dn.toString(), getAndExpression(objectClass, cn) );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession(session);
		}
		return entry;
	}

	public Entry getDomainApplicationProfile( String domainName, String application, String profileName ) {
		LdapSession session = null;
		Entry entry = null;
		try {
			session = getLdapSession();
			String objectClass = getObjectClass(DOMAIN_APPLICATION_PROFILE_OBJECT_CLASS);
			String cn = getCommonName(profileName);
			DistinguishedName dn = getDomainApplicationProfilesDN(domainName, application);
			entry = session.searchOne( dn.toString(), getAndExpression(objectClass, cn) );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession(session);
		}
		return entry;
	}

	public Entry getProfile( String domainName, String application, String profileName ) {
		Entry profile = getApplicationProfile(domainName, application, profileName);
		if ( profile == null ) {
			profile = getDomainApplicationProfile(domainName, application, profileName);
		}
		return profile;
	}

	private IAccessPolicy getAccessPolicy( Entry entry ) {
		AccessPolicy accessPolicy = new AccessPolicy();
		accessPolicy.setId(entry.getAsString(COMMON_NAME));
		accessPolicy.setExceptionThrowableIfMaximumExceeded(entry.getAsBoolean("exceptionThrowableIfMaximumExceeded"));
		accessPolicy.setMaxAllowedUsers(entry.getAsInteger("maxAllowedUsers"));
		accessPolicy.setMaxDefinedUsers(entry.getAsInteger("maxDefinedUsers"));
		accessPolicy.setMaxSessions4User(entry.getAsInteger("maxSessions4User"));
		return accessPolicy;
	}

	private Application getApplication( Entry entry ) {
		Application application = new Application(this);
		application.setId(entry.getAsString(COMMON_NAME));
		application.setDescription(entry.getAsString(DESCRIPTION_ATTRIBUTE));
		return application;
	}

	private IDomain getDomain( Entry entry ) {
		Domain domain = new Domain(this);
		domain.setId(entry.getAsString(COMMON_NAME));
		return domain;
	}
	
	public IRelation getRelation( Entry entry ) {
		Relation relation = new Relation();
		relation.setId(entry.getAsString(COMMON_NAME));
		for( Object member : entry.get(MEMBER) ) {
			DistinguishedName dn = new DistinguishedName( (String) member );
			relation.addRelation( dn.getLevelValue(0) );
		}
		return relation;
	}

	public IRole getRole( Entry entry ) {
		Role role = new Role();
		role.setId(entry.getAsString(COMMON_NAME));
		return role;
	}
	
	public IUser getUser( Entry entry ) {
		User user = new User();
		user.setId(entry.getAsString(USER_ID));
		user.setName(entry.getAsString(COMMON_NAME));
		byte[] password = entry.getAsByteArray(USER_PASSWORD);		
		user.setPasswd(new String(password));
		if ( entry.containsKey(DESCRIPTION_ATTRIBUTE) ) {
			user.setDescription(entry.getAsString(DESCRIPTION_ATTRIBUTE));			
		}
		return user;
	}
	
	public IDomainApplication getDomainApplication( Entry entry, String domain ) {
		DomainApplication domainApplication = new DomainApplication(this, domain);
		domainApplication.setId(entry.getAsString("cn"));
		domainApplication.setDataSource(entry.getAsString(DATA_SOURCE_ATTRIBUTE));
		return domainApplication;
	}
	
	public IAccessPolicy getAccessPolicy( String domainName ) {
		LdapSession session = null;
		Entry entry = null;
		try {
			session = getLdapSession();
			String objectClass = getObjectClass(ACCESS_POLICY_OBJECT_CLASS);
			DistinguishedName dn = getDomainDN(domainName);
			entry = session.searchOne( dn.toString(), objectClass );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession(session);
		}
		return ( entry != null ) ? getAccessPolicy(entry) : null;
	}

	public Entry getDomainApplication( String domainName, String application ) {
		LdapSession session = null;
		Entry entry = null;
		try {
			session = getLdapSession();
			String objectClass = getObjectClass(DOMAIN_APPLICATION_OBJECT_CLASS);
			String cn = getCommonName(application);
			DistinguishedName dn = getDomainApplicationsDN(domainName);
			entry = session.searchOne( dn.toString(), getAndExpression(objectClass, cn) );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession(session);
		}
		return entry;
	}

	private IDataSourceMetaData getDataSourceMetaData( Entry entry ) {
		DataSourceMetaData dataSource = new DataSourceMetaData();
		dataSource.setUsername( entry.getAsString("uid") );
		byte[] password = entry.getAsByteArray("userPassword");
		dataSource.setPassword( new String(password) );
		dataSource.setConnectionURL( entry.getAsString("labeledURI") );
		dataSource.setDriverClass( entry.getAsString("driverClassName") );
		return dataSource;
	}
	
	public IDataSourceMetaData getDataSourceMetaData( String dn ) {
		LdapSession session = null;
		Entry entry = null;
		try {
			session = getLdapSession();
			String objectClass = getObjectClass(DB_CONNECTION_OBJECT_CLASS);
			entry = session.get( dn, objectClass );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession(session);
		}
		return ( entry != null ) ? getDataSourceMetaData(entry) : null;
	}
	
	
	public IDataSourceMetaData getDataSourceMetaData( String domainName, String application ) {
		Entry domainApplication = getDomainApplication(domainName, application);
		if ( domainApplication != null ) {
			String dn = domainApplication.getAsString(DATA_SOURCE_ATTRIBUTE);
			if ( dn != null ) {
				return getDataSourceMetaData( dn );
			}
		}
		return null;
	}

	public IApplication getApplication( String applicationId ) {
		LdapSession session = null;
		IApplication application = null;
		try {
			session = getLdapSession();
			String objectClass = getObjectClass(APPLICATION_OBJECT_CLASS);
			DistinguishedName dn = getApplicationDN(applicationId);
			Entry entry = session.get( dn.toString(), objectClass );
			application = getApplication(entry);
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession(session);
		}
		return application;
	}
	
	public IApplication getApplication4Ctx( String context ) {
		String applicationId = getApplicationId(context);
		IApplication application = getApplication(applicationId);
		if ( application != null ) {
			((Application) application).setContext(context);
		}
		return application;
	}
	
	public IDomain getDomain(String domainId) {
		LdapSession session = null;
		IDomain domain = null;
		try {
			session = getLdapSession();
			String objectClass = getObjectClass(DOMAIN_OBJECT_CLASS);
			DistinguishedName dn = getDomainDN(domainId);
			Entry entry = session.get( dn.toString(), objectClass );
			domain = getDomain(entry);
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession(session);
		}
		return domain;
	}
	
	public IRelation getUserRelation(String domainName, String application, String name) {
		Relation relation = null;
		Entry user = getDomainApplicationUser(domainName, application, name);
		if ( user != null ) {
			relation = new Relation(name);
			Object value = user.get( MEMBER );
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
	
	public List<String> getUserApplications(String domainId, String userId) {
		LdapSession session = null;
		List<String> applications = new ArrayList<String>();
		try {
			session = getLdapSession();
			String objectClass = getObjectClass(DOMAIN_APPLICATION_USER_OBJECT_CLASS);
			String cn = getCommonName(userId);
			DistinguishedName dn = getDomainApplicationsDN(domainId);
			List<Entry> list = session.search( dn.toString(), getAndExpression(objectClass, cn), Scope.SUBTREE_SCOPE, COMMON_NAME);
			for( Entry entry : list ) {
				String application = entry.getDN().getLevelValue( 2 );
				applications.add(application);
			}
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			closeSession(session);
		}
		return applications;
	}	

}
