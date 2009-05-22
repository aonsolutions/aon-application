package com.code.aon.ldap;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.commons.lang.StringUtils;

public class NameResolver implements ILdapConstants {

	private static final Logger LOGGER = Logger.getLogger(NameResolver.class.getName());
	
	public static final String DOMAINS = "domains";
	
	public static final String APPLICATIONS = "applications";
	
	public static final String USERS = "users";
	
	public static final String ADDRESS_BOOK = "addressbook";
	
	public static final String ACCOUNTS = "accounts";
	
	public static final String SIGNATURES = "signatures";
	
	public static final String PROFILES = "profiles";
	
	public static final String ROLES = "roles";
	
	public static final String BDS = "bds";
	
	public static final String MESSAGES = "messages";
	
	public static final String DEFAULT_MAIL_ACCOUNT_NAME = "default";
	
	public static Rdn getRdn( String type, Object value ) {
		try {
			return new Rdn(type, value);
		} catch (InvalidNameException e) {
			LOGGER.log( Level.SEVERE, "Error creating Rdn " + type + ": " + value, e);			
		}
		return null;		
	}

	public static Name getName( Rdn ... rdns ) {
		return new LdapName( Arrays.asList(rdns) );
	}
	
	public static Name getName( Name preffix, Name suffix ) {
		try {
			Name newName = (Name) suffix.clone();
			return newName.addAll(preffix);
		} catch (InvalidNameException e) {
			LOGGER.log( Level.SEVERE, "Error creating Name with " + preffix + " and " + suffix, e);			
		}
		return null;		
	}	
	
	public static Name getName( Rdn rdn, Name suffix ) {
		try {
			Name name = (Name) suffix.clone();
			return name.addAll(getName(rdn));
		} catch (InvalidNameException e) {
			LOGGER.log( Level.SEVERE, "Error creating Name with " + rdn + " and " + suffix, e);			
		}
		return null;		
	}

	public static Name getName( String name ) {
		try {		
			if ( !StringUtils.isBlank(name) ) {
				if ( (name.charAt(0) == '\"') && (name.charAt(name.length()-1) == '\"') ) {
					return new LdapName( name.substring(1, name.length()-1) );
				}
			}
			return new LdapName( name );
		} catch (InvalidNameException e) {
			LOGGER.log( Level.SEVERE, "Error creating Name: " + name, e);			
		}
		return null;		
	}

	public static String getValue( Name name, int pos ) {
		try {	
			LdapName newName = null;
			if ( LdapName.class.isAssignableFrom(name.getClass()) ) {
				newName = (LdapName) name;
			} else {
				newName = new LdapName( name.toString() );
			}
			int index = newName.size()-1-pos;
			return newName.getRdn(index).getValue().toString();
		} catch (InvalidNameException e) {
			LOGGER.log( Level.SEVERE, "Error getting value " + pos + " of Name: " + name, e);			
		}
		return null;		
	}

	public static String getFirstValue( Name name ) {
		return getValue(name, 0);
	}

	public static Name getParent( Name name ) {
		int pos = name.size()-1;
		return name.getPrefix(pos);
	}
	
	public static Rdn userId( String uid ) {
		return getRdn(USER_ID_ATTRIBUTE, uid);
	}
	
	public static Rdn cn( String cn ) {
		return getRdn(COMMON_NAME_ATTRIBUTE, cn);
	}

	public static Rdn ou( String ou ) {
		return getRdn(ORGANIZATIONAL_UNIT_NAME_ATTRIBUTE, ou);		
	}

	public static Rdn status( int status ) {
		return getRdn(STATUS_ATTRIBUTE, String.valueOf(status));
	}
	
	public static String getEqualExpression( String attribute, String value ) {
		return "(" + attribute + "=" + value +  ")";
	}

	public static String getAndExpression( String expression1, String expression2 ) {
		return "(&" + expression1 + expression2 + ")";
	}
		
	public static String getObjectClass( String objectClass ) {
		return getEqualExpression(OBJECT_CLASS_ATTRIBUTE, objectClass);
	}

	public static String getCommonName( String cn ) {
		return getEqualExpression(COMMON_NAME_ATTRIBUTE, cn);
	}	

	public static Name getDomainsDN() {
		return getName( ou(DOMAINS) );
	}
	
	public static Name getDomainDN( String domain ) {
		return getName( ou(DOMAINS), cn(domain) );
	}

	public static Name getApplicationsDN() {
		return getName( ou(APPLICATIONS) );
	}
	
	public static Name getApplicationDN( String application ) {
		return getName( ou(APPLICATIONS), cn(application) );
	}
	
	public static Name getDomainBDsDN( String domain ) {
		return getName( ou(BDS), getDomainDN(domain) );
	}
	
	public static Name getDomainApplicationsDN( String domainName ) {
		return getName( ou(APPLICATIONS), getDomainDN(domainName) );
	}
	
	public static Name getDomainApplicationDN( String domainName, String application ) {
		return getName( cn(application), getDomainApplicationsDN(domainName) );
	}
		
	public static Name getUsersDN( String domain ) {
		return getName( ou(USERS), getDomainDN(domain) );
	}

	public static Name getUserDN( String domain, String user ) {
		return getName( userId(user), getUsersDN(domain) );
	}

	public static Name getUserAccountsDN( String domain, String user ) {
		return getName( ou(ACCOUNTS), getUserDN(domain, user) );
	}

	public static Name getUserDefaultAccount( String domain, String user ) {
		return getName( cn(DEFAULT_MAIL_ACCOUNT_NAME), getUserAccountsDN(domain, user) );
	}
	
	public static Name getUserSignaturesDN( String domain, String user ) {
		return getName( ou(SIGNATURES), getUserDN(domain, user) );
	}

	public static Name getUserAddressBookDN( String domain, String user ) {
		return getName( ou(ADDRESS_BOOK), getUserDN(domain, user) );
	}
	
	public static Name getDomainApplicationUsersDN( String domainName, String application ) {
		return getName( ou(USERS), getDomainApplicationDN(domainName, application) );
	}

	public static Name getDomainApplicationUserDN( String domainName, String application, String user ) {
		return getName( cn(user), getDomainApplicationUsersDN(domainName, application) );
	}
	
	public static Name getDomainApplicationProfilesDN( String domainName, String application ) {
		return getName( ou(PROFILES), getDomainApplicationDN(domainName, application) );
	}

	public static Name getDomainApplicationProfileDN( String domainName, String application, String profile ) {
		return getName( cn(profile), getDomainApplicationProfilesDN(domainName, application) );
	}
	
	public static Name getApplicationProfilesDN( String application ) {
		return getName( ou(PROFILES), getApplicationDN(application) );
	}

	public static Name getApplicationProfileDN( String application, String profile ) {
		return getName( cn(profile), getApplicationProfilesDN(application) );
	}
	
	public static Name getApplicationRolesDN( String application ) {
		return getName( ou(ROLES), getApplicationDN(application) );
	}

	public static Name getApplicationRoleDN( String application, String role ) {
		return getName( cn(role), getApplicationRolesDN(application) );
	}

	public static Name getMessagesDN() {
		return getName( ou(MESSAGES) );
	}
	
	public static Name getMessageDN( int status ) {
		return getName( ou(MESSAGES), status(status) );
	}

	public static Name getMessageDN( int status, String language ) {
		return getName( ou(MESSAGES), ou(language), status(status) );
	}
	
}
