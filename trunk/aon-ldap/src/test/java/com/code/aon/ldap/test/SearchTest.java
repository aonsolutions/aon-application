package com.code.aon.ldap.test;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.ldap.Entry;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;

public class SearchTest {

	private static Log LOGGER = LogFactory.getLog(SearchTest.class.getName());
	
	private static final String HOST = "192.168.2.100";
	
	private static final String BASE_DN = "o=Esferalia-CODE,c=ES";
	
	private static final String USER = "cn=Manager," + BASE_DN;
	
	private static final String PASSWORD = "secret";
	
	private static LdapSession session;

	@BeforeClass
	public static synchronized void runBeforeAllTests() {
		session = new LdapSession();
		Properties properties = new Properties();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, "ldap://" + HOST + "/" + BASE_DN);
		properties.put(Context.SECURITY_PRINCIPAL, USER);
		properties.put(Context.SECURITY_CREDENTIALS, PASSWORD);
		try {
			session.open( properties );
		} catch (LdapException e) {
			Assert.fail( e.getMessage() );
		}
	}
	
	@AfterClass
	public static synchronized void runAfterAllTests() {
		try {
			session.close();
		} catch (LdapException e) {
			Assert.fail( e.getMessage() );			
		}
	}
	
	@Test
    public void testSearch() {
		try {
			List<Entry> list = session.search("cn=code.es,ou=domains","(objectclass=aonAccessPolicy)");
			Assert.assertFalse( list.isEmpty() );
		} catch (LdapException e) {
			Assert.fail( e.getMessage() );
		}
    }

	@Test
    public void testExists() {
		try {
			boolean value = session.exists("cn=aon-desktop,ou=applications","(objectclass=aonApplication)");
			Assert.assertTrue( value );
		} catch (LdapException e) {
			Assert.fail( e.getMessage() );
		}
    }
	
	@Test
    public void testCycle() {
		try {
			Entry entry = new Entry("cn=deletable.es,ou=domains");
			entry.addObjectClasses( new String[]{"top", "aonDomain"} );
			entry.put( "host", "127.0.0.1" );
			session.add(entry);
			String newDN = "cn=borrable.es,ou=domains";
			session.rename(entry.getDN().toString(), newDN );			
			session.delete( newDN );
		} catch (LdapException e) {
			Assert.fail( e.getMessage() );
		}
    }

	@Test
    public void testAttributes() {
		try {
			Entry entry = new Entry("uid=deletable,ou=users,cn=localhost,ou=domains");
			entry.addObjectClasses(new String[]{"top", "person", "aonUser", "posixAccount"});
			entry.put( ILdapConstants.COMMON_NAME_ATTRIBUTE, "Deletable" );
			entry.put( ILdapConstants.SURNAME_ATTRIBUTE, "Deletable" );
			entry.put( "homeDirectory", "/home/deletable" );
			entry.put( "gidNumber", 100 );
			entry.put( "uidNumber", 100 );
			entry.put( "description", "Mierda descripcion" );
			session.add(entry);
			session.addAttribute(entry.getDN(), "description", "aimar" );
			session.replaceAttribute(entry.getDN(), "description", "tellitu" );
			session.removeAttributes(entry.getDN(), "description" );
			session.delete( entry.getDN() );
		} catch (LdapException e) {
			Assert.fail( e.getMessage() );
		}
    }
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(SearchTest.class);
	}

}