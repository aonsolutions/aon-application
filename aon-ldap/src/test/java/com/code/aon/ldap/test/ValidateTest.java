package com.code.aon.ldap.test;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.Name;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ldap.Scope;

public class ValidateTest implements IAonObjectClasses, ILdapConstants {

	private static Log LOGGER = LogFactory.getLog(ValidateTest.class.getName());
	
	private static final String HOST = "192.168.2.100";
	
	private static final String BASE_DN = "o=Esferalia-CODE,c=ES";
	
	private static final String TEST_USER = "cn=Manager," + BASE_DN;
	
	private static final String TEST_PASSWORD = "secret";
	
	private static BasicLdap ldap;

	@BeforeClass
	public static synchronized void runBeforeAllTests() {
		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, "ldap://" + HOST + "/" + BASE_DN);
		properties.put(Context.SECURITY_PRINCIPAL, TEST_USER);
		properties.put(Context.SECURITY_CREDENTIALS, TEST_PASSWORD);
		ldap = new BasicLdap( properties );
	}
	
	@After
	public synchronized void runAfterAllTests() {
		ldap.closeSession();
	}
	
	private List<Entry> getList( Name dn, String objectClass, String... attributes ) {
		List<Entry> list = null;
		try {
			String filter = NameResolver.getObjectClass(objectClass);
			list = ldap.getLdapSession().search(dn, filter, attributes );		
		} catch ( LdapException e ) {
			Assert.fail( e.getMessage() );
		} finally {
			ldap.closeSession();
		}		
		return list;
	}

	private List<Entry> getDeepList( Name dn, String expression, String objectClass, String... attributes ) {
		List<Entry> list = null;
		try {
			String exp1 = NameResolver.getObjectClass(objectClass);
			String filter = NameResolver.getAndExpression(exp1, expression);
			list = ldap.getLdapSession().search(dn, filter, Scope.SUBTREE_SCOPE, attributes );		
		} catch ( LdapException e ) {
			Assert.fail( e.getMessage() );
		} finally {
			ldap.closeSession();
		}		
		return list;
	}	
	
	private Name getFullDN( Name name ) {
		try {
			return ldap.getLdapSession().getFullDN(name);
		} catch ( LdapException e ) {
			Assert.fail( e.getMessage() );
		} finally {
			ldap.closeSession();
		}		
		return null;		
	}
	
	@Test
    public void testRoot() {
		Name applications = NameResolver.getApplicationsDN();
		Assert.assertTrue( ldap.exists(applications, ORGANIZATIONAL_UNIT) );
		Name domains = NameResolver.getDomainsDN();
		Assert.assertTrue( ldap.exists(domains, ORGANIZATIONAL_UNIT) );
		Name messages = NameResolver.getMessagesDN();
		Assert.assertTrue( ldap.exists(messages, ORGANIZATIONAL_UNIT) );

		Name fullApplications = getFullDN(applications);
		Assert.assertTrue( ldap.exists(fullApplications, ORGANIZATIONAL_UNIT) );
		Name fullDomains = getFullDN(domains);
		Assert.assertTrue( ldap.exists(fullDomains, ORGANIZATIONAL_UNIT) );
		Name fullMessages = getFullDN(messages);
		Assert.assertTrue( ldap.exists(fullMessages, ORGANIZATIONAL_UNIT) );
    }

	@Test
    public void testMessages() {
		Name message10 = NameResolver.getMessageDN(10);
		Assert.assertTrue( ldap.exists(message10, MESSAGE) );
		Name message100 = NameResolver.getMessageDN(100);
		Assert.assertTrue( ldap.exists(message100, MESSAGE) );

		Name message10_es = NameResolver.getMessageDN(10, "es");
		Assert.assertTrue( ldap.exists(message10_es, MESSAGE) );
		Name message100_es = NameResolver.getMessageDN(100, "es");
		Assert.assertTrue( ldap.exists(message100_es, MESSAGE) );
	}
	
	private void checkName( Name name, String objectClass, Name container, Name preffix ) {
		if ( ldap.exists(name, objectClass) ) {
			if (! name.startsWith(preffix) ) {
				LOGGER.error( objectClass + " " + name + " must have preffix  " + preffix + " in " + container );
			}
		} else {
			LOGGER.error( objectClass + " doesn't exist " + name + " in " + container );
		}
		
	}
	
    private void testApplicationProfile( Entry profile, Name applicationDN ) {
    	for( Object member : profile.get(MEMBER_ATTRIBUTE) ) {
    		Name memberName = NameResolver.getName( member.toString() );
    		checkName(memberName, ROLE, profile.getDN(), applicationDN);
    	}
    }

    private void testApplication( Entry application ) {
    	String name = application.getAsString(COMMON_NAME_ATTRIBUTE);
		Name profilesDN = NameResolver.getApplicationProfilesDN(name);
		Assert.assertTrue( ldap.exists(profilesDN, ORGANIZATIONAL_UNIT) );
		Name rolesDN = NameResolver.getApplicationRolesDN(name);
		Assert.assertTrue( ldap.exists(rolesDN, ORGANIZATIONAL_UNIT) );
		
		List<Entry> roles = getList(rolesDN, ROLE, OBJECT_CLASS_ATTRIBUTE);
		Assert.assertFalse( "Application " + name + " with roles empty",  roles.isEmpty() );

		List<Entry> profiles = getList(profilesDN, PROFILE, MEMBER_ATTRIBUTE);
		Assert.assertFalse( "Application " + name + " with profiles empty", profiles.isEmpty() );
		for( Entry profile : profiles ) {
			testApplicationProfile(profile, application.getDN());
		}
}
	
	@Test
    public void testApplications() {
		Name applications = NameResolver.getApplicationsDN();
		for( Entry application : getList(applications, APPLICATION,COMMON_NAME_ATTRIBUTE) ) {
			testApplication(application);
		}
	}

	private void testUser( Entry user, String domain ) {
		Name applicationsDN = NameResolver.getDomainApplicationsDN(domain);
		String name = user.getAsString(USER_ID_ATTRIBUTE);
		String filter = NameResolver.getCommonName(name);
		List<Entry> applications = getDeepList(applicationsDN, filter, DOMAIN_APPLICATION_USER);
		if ( applications.isEmpty() ) {
			LOGGER.warn( "User not registered in any application: " + user.getDN() );
		}
	}

	private void testDB( Entry db, String domain ) {
		Name applicationsDN = NameResolver.getDomainApplicationsDN(domain);
		String filter = NameResolver.getEqualExpression(DATA_SOURCE_ATTRIBUTE, db.getDN().toString());
		List<Entry> applications = getDeepList(applicationsDN, filter, DOMAIN_APPLICATION);
		if ( applications.isEmpty() ) {
			LOGGER.warn( "DBConnection not used in any application: " + db.getDN() );
		}
	}
	
	private void testDomainApplicationUser( Entry user, String application, String domain ) {
		String name = user.getAsString(COMMON_NAME_ATTRIBUTE);
		Name userDN = NameResolver.getUserDN(domain, name);
		Assert.assertTrue( "User " + userDN + " doesn't exist, buf referenced " + user.getDN(), ldap.exists(userDN, USER) );
		
		Name profilesDN = getFullDN(NameResolver.getApplicationProfilesDN(application));
    	for( Object member : user.get(MEMBER_ATTRIBUTE) ) {
    		Name memberName = NameResolver.getName( member.toString() );
    		checkName(memberName, PROFILE, user.getDN(), profilesDN);
    	}		
	}

	private void testDomainApplicationProfile( Entry profile, String application ) {
		Name rolesDN = NameResolver.getApplicationRolesDN(application);
    	for( Object member : profile.get(MEMBER_ATTRIBUTE) ) {
    		Name memberName = NameResolver.getName( member.toString() );
    		checkName(memberName, ROLE, profile.getDN(), rolesDN);
    	}		
	}
	
    private void testDomainApplication( Entry application, String domain ) {
    	String name = application.getAsString(COMMON_NAME_ATTRIBUTE);
		Name applicationDN = NameResolver.getApplicationDN(name);
		Assert.assertTrue( ldap.exists(applicationDN, APPLICATION) );
		Name profilesDN = NameResolver.getDomainApplicationProfilesDN(domain, name);
		Assert.assertTrue( ldap.exists(profilesDN, ORGANIZATIONAL_UNIT) );
		Name usersDN = NameResolver.getDomainApplicationUsersDN(domain, name);
		Assert.assertTrue( ldap.exists(usersDN, ORGANIZATIONAL_UNIT) );    	
		
		if ( application.containsKey(DATA_SOURCE_ATTRIBUTE) ) {
			Name dataSource = NameResolver.getName( application.getAsString(DATA_SOURCE_ATTRIBUTE) );
			Name bdsDN = getFullDN(NameResolver.getDomainBDsDN(domain));
			checkName(dataSource, DB_CONNECTION, application.getDN(), bdsDN);
		}
		
		List<Entry> users = getList(usersDN, DOMAIN_APPLICATION_USER);
		if ( users.isEmpty() ) {
			LOGGER.info( "DomainApplication " + application.getDN() + " with users empty" );	
		}
		for( Entry user : users ) {
			testDomainApplicationUser( user, name, domain );
		}
		
		List<Entry> profiles = getList(usersDN, DOMAIN_APPLICATION_PROFILE);
		for( Entry profile : profiles ) {
			testDomainApplicationProfile( profile, name );
		}
    }
	
    private void testDomain( Entry domain ) {
    	String name = domain.getAsString(COMMON_NAME_ATTRIBUTE);
		Name applicationsDN = NameResolver.getDomainApplicationsDN(name);
		Assert.assertTrue( ldap.exists(applicationsDN, ORGANIZATIONAL_UNIT) );
		Name bdsDN = NameResolver.getDomainBDsDN(name);
		Assert.assertTrue( ldap.exists(bdsDN, ORGANIZATIONAL_UNIT) );
		Name usersDN = NameResolver.getUsersDN(name);
		Assert.assertTrue( ldap.exists(usersDN, ORGANIZATIONAL_UNIT) );

		List<Entry> users = getList(usersDN, USER, USER_ID_ATTRIBUTE);
		Assert.assertFalse( "Domain " + name + " with users empty", users.isEmpty() );
		for( Entry user : users ) {
			testUser( user, name );
		}

		List<Entry> bds = getList(bdsDN, DB_CONNECTION, COMMON_NAME_ATTRIBUTE);
		Assert.assertFalse( "Domain " + name + " with bds empty", bds.isEmpty() );
		for( Entry bd : bds ) {
			testDB( bd, name );
		}		
		
		List<Entry> applications = getList(applicationsDN, DOMAIN_APPLICATION);
		Assert.assertFalse( "Domain " + name + " with applications empty", applications.isEmpty() );		
		for( Entry application : applications ) {
			testDomainApplication( application, name );
		}		
    }
	
	@Test
    public void testDomains() {
		Name domains = NameResolver.getDomainsDN();
		for( Entry domain : getList(domains, DOMAIN, COMMON_NAME_ATTRIBUTE) ) {
			testDomain(domain);
		}				
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ValidateTest.class);
	}

}