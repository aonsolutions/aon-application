package com.code.aon.ldap.test;

import java.util.List;
import java.util.Random;

import javax.naming.Name;

import junit.framework.JUnit4TestAdapter;

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

public class ValidateTest implements IAonObjectClasses, ILdapConstants {

	public static final String AON_WEBMAIL = "aon-webmail";
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ValidateTest.class);
	
	private static Util util;
	
	@BeforeClass
	public static synchronized void runBeforeAllTests() {
		util = new Util();
	}
	
	@AfterClass
	public static synchronized void runAfterAllTests() {
		util.getLdap().closeSession();
	}

	private void ensureOrganizationalUnit( Name dn ) {
		if (! util.exists(dn, ORGANIZATIONAL_UNIT) ) {
			LOGGER.warn(  "{} {} doesn't exist", ORGANIZATIONAL_UNIT, dn );
			util.getLdap().addOrganizationUnit(dn);
		}
	}
	
	@Test
    public void testRoot() {
		Name applications = NameResolver.getApplicationsDN();
		util.assertExist(applications, ORGANIZATIONAL_UNIT );
		Name domains = NameResolver.getDomainsDN();
		util.assertExist(domains, ORGANIZATIONAL_UNIT );
		Name messages = NameResolver.getMessagesDN();
		util.assertExist(messages, ORGANIZATIONAL_UNIT );

		Name fullApplications = util.getFullDN(applications);
		util.assertExist(fullApplications, ORGANIZATIONAL_UNIT );
		Name fullDomains = util.getFullDN(domains);
		util.assertExist(fullDomains, ORGANIZATIONAL_UNIT );
		Name fullMessages = util.getFullDN(messages);
		util.assertExist(fullMessages, ORGANIZATIONAL_UNIT );
    }

	@Test
    public void testMessages() {
		Name message10 = NameResolver.getMessageDN(10);
		util.assertExist(message10, MESSAGE );
		Name message100 = NameResolver.getMessageDN(100);
		util.assertExist(message100, MESSAGE );

		Name message10_es = NameResolver.getMessageDN(10, "es");
		util.assertExist(message10_es, MESSAGE );
		Name message100_es = NameResolver.getMessageDN(100, "es");
		util.assertExist(message100_es, MESSAGE );
	}
	
	private void checkName( Name name, String objectClass, Name container, Name preffix ) {
		if ( util.exists(name, objectClass) ) {
			if (! name.startsWith(preffix) ) {
				LOGGER.error( "{} {} must have preffix {} in {}", new Object[]{objectClass, name, preffix, container} );
			}
		} else {
			LOGGER.error( "{} doesn't exist {} in {}", new Object[] {objectClass, name, container} );
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
    	LOGGER.info( "Start validate application {}", name );    	
		Name profilesDN = NameResolver.getApplicationProfilesDN(name);
		ensureOrganizationalUnit( profilesDN );
		Name rolesDN = NameResolver.getApplicationRolesDN(name);
		ensureOrganizationalUnit( rolesDN );
		
		List<Entry> roles = util.getList(rolesDN, ROLE, OBJECT_CLASS_ATTRIBUTE);
		Assert.assertFalse( "Application " + name + " with roles empty",  roles.isEmpty() );

		List<Entry> profiles = util.getList(profilesDN, PROFILE, MEMBER_ATTRIBUTE);
		Assert.assertFalse( "Application " + name + " with profiles empty", profiles.isEmpty() );
		for( Entry profile : profiles ) {
			testApplicationProfile(profile, application.getDN());
		}
		LOGGER.info( "End validate application {}", name );
    }
	
	@Test
    public void testApplications() {
		Name applications = NameResolver.getApplicationsDN();
		for( Entry application : util.getList(applications, APPLICATION,COMMON_NAME_ATTRIBUTE) ) {
			testApplication(application);
		}
	}

	private void testUser( Entry user, String domain ) {
		Name applicationsDN = NameResolver.getDomainApplicationsDN(domain);
		String name = user.getAsString(USER_ID_ATTRIBUTE);
		String filter = NameResolver.getCommonName(name);
		List<Entry> applications = util.getDeepList(applicationsDN, filter, DOMAIN_APPLICATION_USER);
		if ( applications.isEmpty() ) {
			LOGGER.warn( "User not registered in any application: {}", user.getDN() );
		}
		testWebmail(user, domain);
	}

	private void testDB( Entry db, String domain ) {
		Name applicationsDN = NameResolver.getDomainApplicationsDN(domain);
		String filter = NameResolver.getEqualExpression(DATA_SOURCE_ATTRIBUTE, db.getDN().toString());
		List<Entry> applications = util.getDeepList(applicationsDN, filter, DOMAIN_APPLICATION);
		if ( applications.isEmpty() ) {
			LOGGER.warn( "DBConnection not used in any application: {}", db.getDN() );
		}
	}
	
	private void testDomainApplicationUser( Entry user, String application, String domain ) {
		String name = user.getAsString(COMMON_NAME_ATTRIBUTE);
		Name userDN = NameResolver.getUserDN(domain, name);
		Assert.assertTrue( "User " + userDN + " doesn't exist, buf referenced " + user.getDN(), util.exists(userDN, USER) );
		
		Name profilesDN = util.getFullDN(NameResolver.getApplicationProfilesDN(application));
		Name domainProfilesDN = util.getFullDN(NameResolver.getDomainApplicationProfilesDN(domain, application));
    	for( Object member : user.get(MEMBER_ATTRIBUTE) ) {
    		Name memberName = NameResolver.getName( member.toString() );
    		if ( memberName.startsWith(profilesDN) ) {
        		checkName(memberName, PROFILE, user.getDN(), profilesDN);	
    		} else if ( memberName.startsWith(domainProfilesDN) ) {
    			checkName(memberName, PROFILE, user.getDN(), domainProfilesDN);
    		} else {
    			checkName(memberName, PROFILE, user.getDN(), util.getBaseDN());
    		}
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
		util.assertExist(applicationDN, APPLICATION );
		Name profilesDN = NameResolver.getDomainApplicationProfilesDN(domain, name);
		util.assertExist(profilesDN, ORGANIZATIONAL_UNIT );
		Name usersDN = NameResolver.getDomainApplicationUsersDN(domain, name);
		util.assertExist(usersDN, ORGANIZATIONAL_UNIT );    	
		
		if ( application.containsKey(DATA_SOURCE_ATTRIBUTE) ) {
			Name dataSource = NameResolver.getName( application.getAsString(DATA_SOURCE_ATTRIBUTE) );
			Name bdsDN = util.getFullDN(NameResolver.getDomainBDsDN(domain));
			checkName(dataSource, DB_CONNECTION, application.getDN(), bdsDN);
		}
		
		List<Entry> users = util.getList(usersDN, DOMAIN_APPLICATION_USER);
		if ( users.isEmpty() ) {
			LOGGER.info( "DomainApplication {} with users empty", application.getDN() );	
		}
		for( Entry user : users ) {
			testDomainApplicationUser( user, name, domain );
		}
		
		List<Entry> profiles = util.getList(usersDN, DOMAIN_APPLICATION_PROFILE);
		for( Entry profile : profiles ) {
			testDomainApplicationProfile( profile, name );
		}
    }
    
    private void testMailAccount( Entry mailAccount, Name signaturesDN ) {
    	if ( mailAccount.containsKey(SIGNATURE_MEMBER_ATTRIBUTE) ) {
    		String member = mailAccount.getAsString(SIGNATURE_MEMBER_ATTRIBUTE);
    		Name memberName = NameResolver.getName( member );
    		checkName(memberName, SIGNATURE, mailAccount.getDN(), signaturesDN);    	
    	}
    }

    private void testContact( Entry contact, Name addressbookDN ) {
    	if ( contact.containsKey(MEMBER_ATTRIBUTE) ) {
        	for( Object member : contact.get(MEMBER_ATTRIBUTE) ) {
        		Name memberName = NameResolver.getName( member.toString() );
        		checkName(memberName, CONTACT, contact.getDN(), addressbookDN);
        	}
    	}
    }
    
    private void testWebmail( Entry user, String domain ) {
    	String name = user.getAsString(USER_ID_ATTRIBUTE);

		Name webmail = NameResolver.getDomainApplicationUserDN(domain, AON_WEBMAIL, name);
		boolean registered = util.exists(webmail, DOMAIN_APPLICATION_USER);
    	
		Name addressbook = NameResolver.getUserAddressBookDN(domain, name);
		if ( registered ) {
			ensureOrganizationalUnit( addressbook );
		}
		if ( util.exists(addressbook, ORGANIZATIONAL_UNIT) ) {
			List<Entry> contacts = util.getList(addressbook, CONTACT);
			for( Entry contact : contacts ) {
				testContact(contact, util.getFullDN(addressbook));
			}	
		}
		
		Name signaturesDN = NameResolver.getUserSignaturesDN(domain, name);
		if ( registered ) {
			ensureOrganizationalUnit( signaturesDN );
		}
		if ( util.exists(signaturesDN, ORGANIZATIONAL_UNIT) ) {
			List<Entry> signatures = util.getList(signaturesDN, SIGNATURE);
			if ( signatures.isEmpty() && registered ) {
				LOGGER.error( "User {} with signatures empty", user.getDN() );	
			}			
		}
		
		Name accounts = NameResolver.getUserAccountsDN(domain, name);
		if ( registered ) {
			ensureOrganizationalUnit( accounts );
		}
		if ( util.exists(accounts, ORGANIZATIONAL_UNIT) ) {
			List<Entry> mailAccounts = util.getList(accounts, MAIL_ACCOUNT);
			if ( mailAccounts.isEmpty() && registered ) {
				LOGGER.error( "User {} with mail accounts empty", user.getDN() );	
			} else {
				for( Entry mailAccount : mailAccounts ) {
					testMailAccount(mailAccount, util.getFullDN(signaturesDN));
				}	
			}
		}
    }
    
    private void testDomain( Entry domain ) {
    	String name = domain.getAsString(COMMON_NAME_ATTRIBUTE);
    	LOGGER.info( "Start validate domain {}", name );
		Name applicationsDN = NameResolver.getDomainApplicationsDN(name);
		util.assertExist(applicationsDN, ORGANIZATIONAL_UNIT );
		Name bdsDN = NameResolver.getDomainBDsDN(name);
		util.assertExist(bdsDN, ORGANIZATIONAL_UNIT );
		Name usersDN = NameResolver.getUsersDN(name);
		util.assertExist(usersDN, ORGANIZATIONAL_UNIT );

		List<Entry> users = util.getList(usersDN, USER, USER_ID_ATTRIBUTE);
		Assert.assertFalse( "Domain " + name + " with users empty", users.isEmpty() );
		for( Entry user : users ) {
			testUser( user, name );
		}

		List<Entry> bds = util.getList(bdsDN, DB_CONNECTION, COMMON_NAME_ATTRIBUTE);
		Assert.assertFalse( "Domain " + name + " with bds empty", bds.isEmpty() );
		for( Entry bd : bds ) {
			testDB( bd, name );
		}		
		
		List<Entry> applications = util.getList(applicationsDN, DOMAIN_APPLICATION);
		Assert.assertFalse( "Domain " + name + " with applications empty", applications.isEmpty() );		
		for( Entry application : applications ) {
			testDomainApplication( application, name );
		}		

		LOGGER.info( "End validate domain {}", name );
    }
	
	@Test
    public void testDomain() {
    	StopWatch sw = new StopWatch();
    	sw.start();
	    	
		Name domainsDN = NameResolver.getDomainsDN();
		List<Entry> domains = util.getList(domainsDN, DOMAIN);
		
		Random random = new Random();
		int domainIndex = random.nextInt(domains.size());
		Entry domain = domains.get(domainIndex);

		testDomain(domain);
		
		sw.stop();
		LOGGER.info( "Total time: " + sw );
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ValidateTest.class);
	}

}