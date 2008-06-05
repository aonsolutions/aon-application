package com.code.aon.ldap;

public class AonDN implements ILdapConstants {

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
	
	public static String userId( String uid ) {
		return USER_ID_ATTRIBUTE + "=" + uid;
	}
	
	public static String cn( String cn ) {
		return COMMON_NAME_ATTRIBUTE + "=" + cn;
	}

	public static String ou( String cn ) {
		return ORGANIZATIONAL_UNIT_NAME_ATTRIBUTE + "=" + cn;
	}

	public static String status( int status ) {
		return STATUS_ATTRIBUTE + "=" + status;
	}
	
	public static DistinguishedName getDomainDN( String domain ) {
		return new DistinguishedName( cn(domain), ou(DOMAINS) );
	}

	public static DistinguishedName getApplicationDN( String application ) {
		return new DistinguishedName( cn(application), ou(APPLICATIONS) );
	}
	
	public static DistinguishedName getDomainApplicationsDN( String domainName ) {
		return new DistinguishedName( ou(APPLICATIONS), getDomainDN(domainName) );
	}
	
	public static DistinguishedName getDomainApplicationDN( String domainName, String application ) {
		return new DistinguishedName( cn(application), getDomainApplicationsDN(domainName) );
	}
		
	public static DistinguishedName getUsersDN( String domain ) {
		return new DistinguishedName( ou(USERS), getDomainDN(domain) );
	}

	public static DistinguishedName getUserDN( String domain, String user ) {
		return new DistinguishedName( userId(user), getUsersDN(domain) );
	}

	public static DistinguishedName getUserAccountsDN( String domain, String user ) {
		return new DistinguishedName( ou(ACCOUNTS), getUserDN(domain, user) );
	}

	public static DistinguishedName getUserDefaultAccount( String domain, String user ) {
		return new DistinguishedName( cn(DEFAULT_MAIL_ACCOUNT_NAME), getUserAccountsDN(domain, user) );
	}
	
	public static DistinguishedName getUserSignaturesDN( String domain, String user ) {
		return new DistinguishedName( ou(SIGNATURES), getUserDN(domain, user) );
	}

	public static DistinguishedName getUserAddressBookDN( String domain, String user ) {
		return new DistinguishedName( ou(ADDRESS_BOOK), getUserDN(domain, user) );
	}
	
	public static DistinguishedName getDomainApplicationUsersDN( String domainName, String application ) {
		return new DistinguishedName( ou(USERS), getDomainApplicationDN(domainName, application) );
	}

	public static DistinguishedName getDomainApplicationUserDN( String domainName, String application, String user ) {
		return new DistinguishedName( cn(user), getDomainApplicationUsersDN(domainName, application) );
	}
	
	public static DistinguishedName getDomainApplicationProfilesDN( String domainName, String application ) {
		return new DistinguishedName( ou(PROFILES), getDomainApplicationDN(domainName, application) );
	}

	public static DistinguishedName getDomainApplicationProfileDN( String domainName, String application, String profile ) {
		return new DistinguishedName( cn(profile), getDomainApplicationProfilesDN(domainName, application) );
	}
	
	public static DistinguishedName getApplicationProfilesDN( String application ) {
		return new DistinguishedName( ou(PROFILES), getApplicationDN(application) );
	}

	public static DistinguishedName getApplicationProfileDN( String application, String profile ) {
		return new DistinguishedName( cn(profile), getApplicationProfilesDN(application) );
	}
	
	public static DistinguishedName getRolesDN( String application ) {
		return new DistinguishedName( ou(ROLES), getApplicationDN(application) );
	}

	public static DistinguishedName getRoleDN( String application, String role ) {
		return new DistinguishedName( cn(role), getRolesDN(application) );
	}

	public static DistinguishedName getMessageDN( int status ) {
		return new DistinguishedName( status(status), ou(MESSAGES) );
	}

	public static DistinguishedName getMessageDN( int status, String language ) {
		return new DistinguishedName( status(status), ou(language), ou(MESSAGES) );
	}
	
}
