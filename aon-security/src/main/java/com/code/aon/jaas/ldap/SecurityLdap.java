package com.code.aon.jaas.ldap;

import java.util.Properties;

import javax.naming.NamingException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.client.ast.IDataSourceMetaData;
import com.code.aon.jaas.client.ast.core.AccessPolicy;
import com.code.aon.jaas.client.ast.core.DataSourceMetaData;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.LdapSession;

public class SecurityLdap implements ILdapConstants {
	
    /** Obtiene un logger apropiado. */
	private static final Log LOGGER = LogFactory.getLog( SecurityLdap.class.getName() );
	
	private Properties properties;

	public SecurityLdap( Properties properties ) {
		this.properties = properties;
	}
	
	private LdapSession getLdapSession() {
		LdapSession session = new LdapSession();
		try {
			session.open(this.properties);
		} catch (NamingException e) {
			LOGGER.error( "Error opening LDAP session. " + e.getMessage(), e );
		}
		return session;
	}
	
	private String getObjectClass( String objectClass ) {
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

	private DistinguishedName getDomainApplicationsDN( String domainName ) {
		return new DistinguishedName( APPLICATIONS_DN, getDomainDN(domainName) );
	}
	
	private DistinguishedName getDomainApplicationDN( String domainName, String application ) {
		return new DistinguishedName( getCN(application), getDomainApplicationsDN(domainName) );
	}
	
	private DistinguishedName getDomainApplicationUsersDN( String domainName, String application ) {
		return new DistinguishedName( USERS_DN, getDomainApplicationDN(domainName, application) );
	}

	private DistinguishedName getUsersDN( String domainName ) {
		return new DistinguishedName( USERS_DN, getDomainDN(domainName) );
	}

	private DistinguishedName getDomainApplicationProfilesDN( String domainName, String application ) {
		return new DistinguishedName( PROFILES_DN, getDomainApplicationDN(domainName, application) );
	}

	private DistinguishedName getProfilesDN( String application ) {
		return new DistinguishedName( PROFILES_DN, getCN(application), APPLICATIONS_DN );
	}
	
	public String getApplication( String context ) {
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
		LdapSession session = getLdapSession();
		String objectClass = getObjectClass(DOMAIN_OBJECT_CLASS);
		String cn = getCommonName( domainName );
		int count = session.getCount( DOMAINS_DN, getAndExpression(objectClass, cn) );
		session.close();
		return (count > 0);
	}

	public boolean hasUser( String domainName, String application, String user ) {
		LdapSession session = getLdapSession();
		String objectClass = getObjectClass(DOMAIN_APPLICATION_USER_OBJECT_CLASS);
		String cn = getCommonName(user);
		DistinguishedName dn = getDomainApplicationUsersDN(domainName, application);
		int count = session.getCount( dn.toString(), getAndExpression(objectClass, cn) );
		session.close();
		return (count > 0);
	}
	
	public Entry getUser( String domainName, String user ) {
		LdapSession session = getLdapSession();
		String objectClass = getObjectClass(USER_OBJECT_CLASS);
		String cn = getUserId(user);
		DistinguishedName dn = getUsersDN(domainName);
		Entry entry = session.searchOne( dn.toString(), getAndExpression(objectClass, cn) );
		session.close();
		return entry;
	}

	public Entry getDomainApplicationUser( String domainName, String application, String user ) {
		LdapSession session = getLdapSession();
		String objectClass = getObjectClass(DOMAIN_APPLICATION_USER_OBJECT_CLASS);
		String cn = getCommonName(user);
		DistinguishedName dn = getDomainApplicationUsersDN(domainName, application);
		Entry entry = session.searchOne( dn.toString(), getAndExpression(objectClass, cn) );
		session.close();
		return entry;
	}

	public Entry getApplicationProfile( String domainName, String application, String profileName ) {
		LdapSession session = getLdapSession();
		String objectClass = getObjectClass(PROFILE_OBJECT_CLASS);
		String cn = getCommonName(profileName);
		DistinguishedName dn = getProfilesDN(application);
		Entry entry = session.searchOne( dn.toString(), getAndExpression(objectClass, cn) );
		session.close();
		return entry;
	}

	public Entry getDomainApplicationProfile( String domainName, String application, String profileName ) {
		LdapSession session = getLdapSession();
		String objectClass = getObjectClass(DOMAIN_APPLICATION_PROFILE_OBJECT_CLASS);
		String cn = getCommonName(profileName);
		DistinguishedName dn = getDomainApplicationProfilesDN(domainName, application);
		Entry entry = session.searchOne( dn.toString(), getAndExpression(objectClass, cn) );
		session.close();
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
		accessPolicy.setId(entry.getAsString("cn"));
		accessPolicy.setExceptionThrowableIfMaximumExceeded(entry.getAsBoolean("exceptionThrowableIfMaximumExceeded"));
		accessPolicy.setMaxAllowedUsers(entry.getAsInteger("maxAllowedUsers"));
		accessPolicy.setMaxDefinedUsers(entry.getAsInteger("maxDefinedUsers"));
		accessPolicy.setMaxSessions4User(entry.getAsInteger("maxSessions4User"));
		return accessPolicy;
	}
	
	public IAccessPolicy getAccessPolicy( String domainName ) {
		LdapSession session = getLdapSession();
		String objectClass = getObjectClass(ACCESS_POLICY_OBJECT_CLASS);
		DistinguishedName dn = getDomainDN(domainName);
		Entry entry = session.searchOne( dn.toString(), objectClass );
		session.close();
		return ( entry != null ) ? getAccessPolicy(entry) : null;
	}

	public Entry getDomainApplication( String domainName, String application ) {
		LdapSession session = getLdapSession();
		String objectClass = getObjectClass(DOMAIN_APPLICATION_OBJECT_CLASS);
		String cn = getCommonName(application);
		DistinguishedName dn = getDomainApplicationsDN(domainName);
		Entry entry = session.searchOne( dn.toString(), getAndExpression(objectClass, cn) );
		session.close();
		return entry;
	}

	private IDataSourceMetaData getDataSourceMetaData( Entry entry ) {
		DataSourceMetaData dataSource = new DataSourceMetaData();
		dataSource.setUsername( entry.getAsString("uid") );
		dataSource.setPassword( entry.getAsString("userPassword") );
		dataSource.setConnectionURL( entry.getAsString("labeledURI") );
		dataSource.setDriverClass( entry.getAsString("driverClassName") );
		return dataSource;
	}
	
	public IDataSourceMetaData getDataSourceMetaData( String domainName, String application ) {
		Entry domainApplication = getDomainApplication(domainName, application);
		if ( domainApplication != null ) {
			String dn = domainApplication.getAsString(DATA_SOURCE_ATTRIBUTE);
			if (! StringUtils.isEmpty(dn) ) {
				LdapSession session = getLdapSession();
				String objectClass = getObjectClass(DB_CONNECTION_OBJECT_CLASS);
				Entry entry = session.get( dn, objectClass );
				session.close();
				return ( entry != null ) ? getDataSourceMetaData(entry) : null;
			}
		}
		return null;
	}
	
}
