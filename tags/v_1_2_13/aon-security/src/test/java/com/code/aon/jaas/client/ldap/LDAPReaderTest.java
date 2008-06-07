package com.code.aon.jaas.client.ldap;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.auth.util.Util;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.client.ast.core.User;
import com.code.aon.jaas.ldap.AuthInfo;
import com.code.aon.jaas.ldap.SecurityLdap;
import com.code.aon.ldap.Entry;

public class LDAPReaderTest {

	private static Log LOGGER = LogFactory.getLog(LDAPReaderTest.class.getName());
	
	private static final String HOST = "192.168.2.100";
	
	private static final String BASE_DN = "o=Esferalia-CODE,c=ES";
	
	private static final String USER = "cn=Manager," + BASE_DN;
	
	private static final String PASSWORD = "secret";
	
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
		Assert.assertTrue( ldap.hasDomain( "aon.code.es") );
		Assert.assertTrue( ldap.hasDomain( "localhost") );
		Assert.assertTrue( ldap.hasUser("localhost", "aon-task", "atellitu") );
		Assert.assertFalse( ldap.hasUser("aon.code.es", "aon-nothing", "atellitu") );
		
		Entry user = ldap.getUser("localhost", "atellitu");
		Assert.assertNotNull( user );
		LOGGER.info( "User: " + user );
		
		Entry domainApplicationUser = ldap.getDomainApplicationUser("localhost", "aon-task", "atellitu");
		Assert.assertNotNull( domainApplicationUser );
		LOGGER.info( "Domain Application User: " + domainApplicationUser );
		
		List<String> applications = ldap.getUserApplications("localhost", "atellitu");
		Assert.assertNotNull( applications );
		LOGGER.info( "Applications: " + applications );
    }

	@Test
    public void changePassword() throws AuthenticationLoginException, NoSuchAlgorithmException {
		Entry entry = ldap.getUser("localhost", "atellitu");
		Assert.assertNotNull( entry );
		User user = ldap.getUser(entry);
		String newPassword = "at111276";
        byte[] hash = MessageDigest.getInstance("SHA").digest(newPassword.getBytes());
        String passwordHash = Util.encodeBase64(hash);		
        user.setPasswd( passwordHash );
		ldap.updateUser( "SHA", "localhost", user, null);
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(LDAPReaderTest.class);
	}

}