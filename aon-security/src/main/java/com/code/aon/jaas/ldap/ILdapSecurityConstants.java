package com.code.aon.jaas.ldap;

public interface ILdapSecurityConstants {

	String DOMAINS_DN = "ou=domains";
	
	String APPLICATIONS_DN = "ou=applications";
	
	String USERS_DN = "ou=users";
	
	String PROFILES_DN = "ou=profiles";
	
	String BDS_DN = "ou=bds";
	
	String APPLICATION_OBJECT_CLASS = "aonApplication";

	String DOMAIN_OBJECT_CLASS = "aonDomain";
	
	String DOMAIN_APPLICATION_OBJECT_CLASS = "aonDomainApplication";

	String DOMAIN_APPLICATION_USER_OBJECT_CLASS = "aonDomainApplicationUser";
	
	String DOMAIN_APPLICATION_PROFILE_OBJECT_CLASS = "aonDomainApplicationProfile";
	
	String ACCESS_POLICY_OBJECT_CLASS = "aonAccessPolicy";
	
	String DB_CONNECTION_OBJECT_CLASS = "aonDBConnection";
	
	String USER_OBJECT_CLASS = "aonUser";
	
	String PROFILE_OBJECT_CLASS = "aonProfile";
	
	String DESCRIPTION_ATTRIBUTE = "description";
	
	String DATA_SOURCE_ATTRIBUTE = "dataSource";
	
}
