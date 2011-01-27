package com.code.aon.ldap.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import javax.naming.Name;

import junit.framework.JUnit4TestAdapter;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.StopWatch;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ldap.ldif.LDIF;

public class LdifTest implements IAonObjectClasses, ILdapConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(LdifTest.class);
	
	private static final String[] DN_ATTRIBUTES = new String[] {
		MEMBER_ATTRIBUTE, DATA_SOURCE_ATTRIBUTE, SIGNATURE_MEMBER_ATTRIBUTE
	};
	
	private final static String[] SUPPORTED_OBJECT_CLASSES = new String[] {
		"organization", "organizationalUnit", "aonContact",
		"aonApplication", "aonMessage", "aonRole", "aonDomain",
		"aonDomainApplication", "aonDBConnection", "aonDBConnection",
		"aonAccessPolicy", "aonProfile", "aonUser", "aonSignature",
		"aonMailAccount", "aonDomainApplicationUser", "aonDomainApplicationProfile"
	};
	
	private static final String LDIF_RESOURCE = "aondirectory.ldif";
	
	private static Util util;
	
	@BeforeClass
	public static synchronized void runBeforeAllTests() {
		util = new Util();
	}
	
	@AfterClass
	public static synchronized void runAfterAllTests() {
		util.getLdap().closeSession();
	}
	
	private boolean isSupported( Entry entry ) {
		for( String objectClass : entry.getObjectClasses() ) {
			if ( ArrayUtils.contains(SUPPORTED_OBJECT_CLASSES, objectClass) ) {
				return true;
			}
		}
		return false;
	}
    
    private void cleanTree() {
		Name domains = NameResolver.getDomainsDN();
		if ( util.exists(domains, ORGANIZATIONAL_UNIT) ) {
			util.delete(domains, ORGANIZATIONAL_UNIT);
		}
		Name applications = NameResolver.getApplicationsDN();
		if ( util.exists(applications, ORGANIZATIONAL_UNIT) ) {
			util.delete(applications, ORGANIZATIONAL_UNIT);
		}
		Name messages = NameResolver.getMessagesDN();
		if ( util.exists(messages, ORGANIZATIONAL_UNIT) ) {
			util.delete(messages, ORGANIZATIONAL_UNIT);
		}
    }
    
    private boolean isForSecondPass( Entry entry, String attribute ) {
    	if ( entry.containsKey(attribute) ) {
    		for( Object value : entry.get(attribute) ) {
				Name name = NameResolver.getName( (String) value );
				if (! util.exists(name) ) {
					return true;	
				}    			
    		}
    	}
    	return false;
    }
    
    private boolean isForSecondPass( Entry entry ) {
    	for( String attribute : DN_ATTRIBUTES ) {
    		if ( isForSecondPass(entry, attribute) ) {
    			return true;
    		}
    	}
    	return false;
    }
   
    public void testLdif() {
    	StopWatch sw = new StopWatch();
    	sw.start();
		try {
			InputStream in = LdifTest.class.getResourceAsStream(LDIF_RESOURCE);
			LDIF ldif = new LDIF(in);
			List<Entry> entries = ldif.getList();
			Assert.assertEquals( 679, entries.size() );
			sw.stop();
			LOGGER.info( "Total time: " + sw );
		} catch (IOException e) {
			Assert.fail(e.getMessage());			
			LOGGER.error(e.getMessage(), e);
		}
    }

    public void testCleanTree() {
    	StopWatch sw = new StopWatch();
    	sw.start();
    	cleanTree();
		sw.stop();
		LOGGER.info( "Total time: " + sw );
    }

    @Test
    public void importLDIF() {
    	StopWatch sw = new StopWatch();
    	cleanTree();
		InputStream in = LdifTest.class.getResourceAsStream(LDIF_RESOURCE);
		try {
			LDIF ldif = new LDIF(in);
			List<Entry> list = new LinkedList<Entry>();
			List<Entry> entries = ldif.getList();
	    	sw.start();
	    	LOGGER.info( "First pass ..." );
			for( Entry entry : entries ) {
				if ( isSupported(entry) ) {
					Name parent = NameResolver.getParent( entry.getDN() );
					if ( ! util.exists(parent) ) {
						LOGGER.info( "For second pass (no parent): {}", entry.getDN() );
						list.add(entry);
					} else if ( isForSecondPass(entry) ) {
						LOGGER.info( "For second pass (no created dn): {}", entry.getDN() );
						list.add(entry);
					} else {
						util.addEntry( entry );	
					}
				}
			}
			LOGGER.info( "Second pass ..." );
			for( Entry entry2 : list ) {
				Name parent = NameResolver.getParent( entry2.getDN() );
				if ( util.exists(parent) ) {
					util.addEntry( entry2 );	
				} else {
					LOGGER.warn( "Skipped: {}", entry2.getDN() );
				}				
			}
			sw.stop();
			LOGGER.info( "Total time: " + sw );
		} catch (IOException e) {
			Assert.fail(e.getMessage());			
			LOGGER.error(e.getMessage(), e);
		}
    }    
    
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(LdifTest.class);
	}

}