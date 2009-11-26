package com.code.aon.ldap.test;

import java.util.List;
import java.util.Properties;

import javax.naming.Name;

import junit.framework.JUnit4TestAdapter;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.LdapSession;
import com.code.aon.ldap.NameResolver;

public class SearchTest implements IAonObjectClasses, ILdapConstants {

	private static final String BASE_DN = "o=aondirectory";
	
	private static final String USER = "cn=Manager," + BASE_DN;
	
	
	private static LdapSession session;

	@BeforeClass
	public static synchronized void runBeforeAllTests() {
		session = new LdapSession();
		Properties properties = ValidateTest.loadProperties();
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
			Name name = NameResolver.getDomainDN("code.es");
			List<Entry> list = session.search(name,NameResolver.getObjectClass(ACCESS_POLICY));
			Assert.assertFalse( list.isEmpty() );
		} catch (LdapException e) {
			Assert.fail( e.getMessage() );
		}
    }

	@Test
    public void testExists() {
		try {
			Name name = NameResolver.getApplicationDN("aon-desktop");
			boolean value = session.exists(name,NameResolver.getObjectClass(APPLICATION));
			Assert.assertTrue( value );
		} catch (LdapException e) {
			Assert.fail( e.getMessage() );
		}
    }
	
	@Test
    public void testCycle() {
		try {
			Name name = NameResolver.getDomainDN("deletable");
			Entry entry = new Entry(name);
			entry.addObjectClasses( new String[]{TOP, DOMAIN} );
			entry.put( "host", "127.0.0.1" );
			session.add(entry);
			Name newName = NameResolver.getDomainDN("borrable");
			session.rename(entry.getDN(), newName );			
			session.delete( newName );
		} catch (LdapException e) {
			Assert.fail( e.getMessage() );
		}
    }

	@Test
    public void testAttributes() {
		try {
			Name name = NameResolver.getUserDN("localhost", "deletable");
			Entry entry = new Entry(name);
			entry.addObjectClasses(new String[]{TOP, PERSON, USER, POSIX_ACCOUNT});
			entry.put( COMMON_NAME_ATTRIBUTE, "Deletable" );
			entry.put( SURNAME_ATTRIBUTE, "Deletable" );
			entry.put( HOME_DIRECTORY_ATTRIBUTE, "/home/deletable" );
			entry.put( GROUP_ID_NUMBER_ATTRIBUTE, 100 );
			entry.put( USER_ID_NUMBER_ATTRIBUTE, 100 );
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