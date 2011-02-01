package com.code.aon.ldap.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.naming.Name;
import javax.naming.ldap.Rdn;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.LdapException;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ldap.Scope;

public class Util {

	private final static Logger LOGGER = LoggerFactory.getLogger(Util.class);
		
	private static final String LDAP_PROPERTIES = "ldap.properties";
	
	private BasicLdap ldap;

	public Util() {
		this.ldap = new BasicLdap( loadProperties() );
	}
	
	private Properties loadProperties() {
		Properties properties = new Properties();
		String path = System.getProperty(LDAP_PROPERTIES);
		try {
			if (! StringUtils.isEmpty(path) ) {
				FileInputStream fin = new FileInputStream( path ); 
				properties.load(fin);
				fin.close();
			} else {
				InputStream in = LdifTest.class.getResourceAsStream(LDAP_PROPERTIES);
				properties.load(in);
				in.close();
			}
		} catch ( IOException e ) {
			Assert.fail( e.getMessage() );
		}
		return properties;
	}
	
	public BasicLdap getLdap() {
		return ldap;
	}

	public void assertExist( Name dn, String objectClass ) {
		Assert.assertTrue( objectClass + " doesn't exist " + dn, ldap.exists(dn, objectClass) );
	}	
	
	public void assertNotExist( Name dn, String objectClass ) {
		Assert.assertFalse( objectClass + "  exist " + dn, ldap.exists(dn, objectClass) );
	}			
	
	public void addEntry( Entry entry ) {
		LOGGER.info( "Add: {}", entry.getDN() );
		if (! ldap.add(entry) ) {
			Assert.fail( "Error inserting " + entry.getDN() );
		}
		String objectClass = entry.getObjectClasses().get(0);
		if (! ldap.exists(entry.getDN(), objectClass) ) {
			Assert.fail( objectClass + ", " + entry.getDN() + " doesn't exist" );
		}
	}		

	public boolean exists( Name dn ) {
		boolean exists = false;
		try {
			Rdn rdn = NameResolver.getFirstRdn(dn);
			String filter = NameResolver.getEqualExpression(rdn.getType(), rdn.getValue().toString());
			exists = ldap.getLdapSession().exists(dn, filter, rdn.getType());
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			ldap.closeSession();
		}
		return exists;
	}
	
	public boolean exists( Name dn, String objectClass ) {
		return ldap.exists(dn, objectClass);
	}
	
	public void delete( Name dn, String objectClass ) {
		try {
			LOGGER.info( "Delete: {}", dn );
			ldap.getLdapSession().deleteDepth(dn, true);
		} catch ( LdapException e ) {
			Assert.fail(e.getMessage());			
			LOGGER.error(e.getMessage(), e);
		} finally {
			ldap.closeSession();
		}
		assertNotExist(dn, objectClass );
	}	
	
	public List<Entry> getList( Name dn, String objectClass, String... attributes ) {
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

	public List<Entry> getDeepList( Name dn, String expression, String objectClass, String... attributes ) {
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
	
	public Name getFullDN( Name name ) {
		try {
			return ldap.getLdapSession().getFullDN(name);
		} catch ( LdapException e ) {
			Assert.fail( e.getMessage() );
		} finally {
			ldap.closeSession();
		}		
		return null;		
	}

	public Name getBaseDN() {
		try {
			return ldap.getLdapSession().getBaseDN();
		} catch ( LdapException e ) {
			Assert.fail( e.getMessage() );
		} finally {
			ldap.closeSession();
		}		
		return null;		
	}
	
}
