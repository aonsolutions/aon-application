package com.code.aon.jaas.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IRole;
import com.code.aon.jaas.client.ast.IUser;
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
	
	public static String getObjectClass( String objectClass ) {
		return "(" + OBJECT_CLASS + "=" + objectClass +  ")";
	}

	public static String getCommonName( String cn ) {
		return "(" + getCN(cn) +  ")";
	}

	public static String getUserId( String uid ) {
		return "(" + USER_ID + "=" + uid +  ")";
	}
	
	public static String getCN( String cn ) {
		return COMMON_NAME + "=" + cn;
	}
	
	private static String getAndExpression( String expression1, String expression2 ) {
		return "(&" + expression1 + expression2 + ")";
	}

	public DistinguishedName getDomainApplicationUsersDN( String domainName, String application ) {
		return new DistinguishedName( USERS_DN, DomainApplication.getDN(domainName, application) );
	}

	private DistinguishedName getUsersDN( String domainName ) {
		return new DistinguishedName( USERS_DN, Domain.getDN(domainName) );
	}

	public DistinguishedName getDomainApplicationProfilesDN( String domainName, String application ) {
		return new DistinguishedName( PROFILES_DN, DomainApplication.getDN(domainName, application) );
	}

	public DistinguishedName getDomainApplicationProfileDN( String domainName, String application, String profile ) {
		return new DistinguishedName( getCN(profile), getDomainApplicationProfilesDN(domainName, application) );
	}
	
	public DistinguishedName getProfilesDN( String application ) {
		return new DistinguishedName( PROFILES_DN, Application.getDN(application) );
	}

	public DistinguishedName getRolesDN( String application ) {
		return new DistinguishedName( ROLES_DN, Application.getDN(application) );
	}

	public DistinguishedName getRoleDN( String application, String role ) {
		return new DistinguishedName( getCN(role), getRolesDN(application) );
	}
	
	public IRelation getProfile( Entry entry ) {
		Relation relation = new Relation();
		relation.setId(entry.getAsString(COMMON_NAME));
		Object value = entry.get( MEMBER );
		if ( value instanceof String ) {
			DistinguishedName member = new DistinguishedName( (String) value );
			relation.addRelation( member.getLevelValue(0) );
		} else {
			for( String profile : (List<String>) value ) {
				DistinguishedName dn = new DistinguishedName( profile );
				relation.addRelation( dn.getLevelValue(0) );
			}
		}
		return relation;
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
		return (Domain.get(this, domainName) != null);
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

	public IRelation getRelation( Entry entry ) {
		Relation relation = new Relation();
		relation.setId(entry.getAsString(COMMON_NAME));
		for( Object member : entry.get(MEMBER) ) {
			DistinguishedName dn = new DistinguishedName( (String) member );
			relation.addRelation( dn.getLevelValue(0) );
		}
		return relation;
	}

	public Entry getDomainApplicationProfile( LdapSession session, IRelation relation, DistinguishedName dn ) {
		Entry entry = new Entry(dn.toString());
		String appId = dn.getLevelValue(2);
		entry.addObjectClass("top", "groupOfNames", "aonProfile", "aonDomainApplicationProfile");
		for( String role : relation.relations() ) {
			DistinguishedName member = session.getFullDN( getRoleDN(appId, role) );
			entry.put( MEMBER, member.toString() );
		}
		return entry;
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
	
	public IDataSourceMetaData getDataSourceMetaData( Entry entry ) {
		DataSourceMetaData dataSource = new DataSourceMetaData();
		dataSource.setUsername( entry.getAsString("uid") );
		byte[] password = entry.getAsByteArray("userPassword");
		dataSource.setPassword( new String(password) );
		dataSource.setConnectionURL( entry.getAsString("labeledURI") );
		dataSource.setDriverClass( entry.getAsString("driverClassName") );
		return dataSource;
	}
	
	public IApplication getApplication4Ctx( String context ) {
		String applicationId = getApplicationId(context);
		IApplication application = Application.get(this, applicationId);
		if ( application != null ) {
			((Application) application).setContext(context);
		}
		return application;
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
			DistinguishedName dn = DomainApplication.getParentDN(domainId);
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
