package com.code.aon.jaas.client.ldap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;

import junit.framework.JUnit4TestAdapter;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.auth.util.Util;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.User;
import com.code.aon.jaas.ldap.Domain;
import com.code.aon.jaas.ldap.DomainApplication;
import com.code.aon.jaas.ldap.SecurityLdap;

public class LDAPReaderTest {

	private final static Logger LOGGER = LoggerFactory.getLogger(LDAPReaderTest.class);
	
	private static final String HOST = "127.0.0.1";
	
	private static final String BASE_DN = "o=aondirectory";
	
	private static final String USER = "cn=Manager," + BASE_DN;
	
	private static final String PASSWORD = "GeForce";
	
	private static SecurityLdap ldap;

	@BeforeClass
	public static synchronized void runBeforeAllTests() {
		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, "ldap://" + HOST + "/" + BASE_DN);
		properties.put(Context.SECURITY_PRINCIPAL, USER);
		properties.put(Context.SECURITY_CREDENTIALS, PASSWORD);
		ldap = new SecurityLdap( properties );			
	}
	
	@Test
    public void testGetApplications() throws AuthenticationLoginException {
		Assert.assertFalse( ldap.hasDomain( "aon.code.es") );
		Assert.assertTrue( ldap.hasDomain( "localhost") );
		Assert.assertTrue( ldap.hasUser("localhost", "aon-desktop", "admin") );
		Assert.assertFalse( ldap.hasUser("localhost", "aon-nothing", "admin") );
		
		Domain domain = Domain.get(ldap, "localhost" );
		IUser user = domain.getStandaloneUser("admin");
		Assert.assertNotNull( user );
		LOGGER.info( "User: {}", user );
		
		DomainApplication domainApplication = DomainApplication.get(ldap, "localhost", "aon-desktop");
		IRelation domainApplicationUser = domainApplication.getUser("admin");
		Assert.assertNotNull( domainApplicationUser );
		LOGGER.info( "Domain Application User: {}", domainApplicationUser );
		
		List<String> applications = ldap.getUserApplications("localhost", "admin");
		Assert.assertNotNull( applications );
		LOGGER.info( "Applications: {}", applications );
    }

	@Test
    public void changePassword() throws AuthenticationLoginException, NoSuchAlgorithmException {
		Domain domain = Domain.get(ldap, "localhost" );
		User user = (User) domain.getStandaloneUser("admin");		
		String newPassword = "demo";
        byte[] hash = MessageDigest.getInstance("SHA").digest(newPassword.getBytes());
        String passwordHash = Util.encodeBase64(hash);		
        user.setPasswd( passwordHash );
		ldap.updateUser( "SHA", "localhost", user, null);
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(LDAPReaderTest.class);
	}

}