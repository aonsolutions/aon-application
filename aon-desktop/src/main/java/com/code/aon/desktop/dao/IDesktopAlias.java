package com.code.aon.desktop.dao;


/** 
* Interface for holding entity properties constants.
*/ 
public interface IDesktopAlias {

	/** 
	* Alias value: Domain_id
	* Hibernate value: Domain.id
	*/
	String  DOMAIN_ID = "Domain_id";

	/** 
	* Alias value: Domain_commonName
	* Hibernate value: Domain.commonName
	*/
	String  DOMAIN_COMMON_NAME = "Domain_commonName";

	/** 
	* Alias value: Domain_organizationName
	* Hibernate value: Domain.organizationName
	*/
	String  DOMAIN_ORGANIZATION_NAME = "Domain_organizationName";

	/** 
	* Alias value: Domain_parentDomain
	* Hibernate value: Domain.parentDomain
	*/
	String  DOMAIN_PARENT_DOMAIN = "Domain_parentDomain";
	
	/** 
	* Alias value: Domain_host
	* Hibernate value: Domain.host
	*/
	String  DOMAIN_HOST = "Domain_host";

	/** 
	* Alias value: Domain_mail
	* Hibernate value: Domain.mail
	*/
	String  DOMAIN_MAIL = "Domain_mail";
	
	/** 
	* Alias value: Domain_mobile
	* Hibernate value: Domain.mobile
	*/
	String  DOMAIN_MOBILE = "Domain_mobile";

	/** 
	* Alias value: Domain_status
	* Hibernate value: Domain.status
	*/
	String  DOMAIN_STATUS = "Domain_status";
	/** 
	* Alias value: Domain_dnsManagement
	* Hibernate value: Domain.dnsManagement
	*/
	String  DOMAIN_DNS_MANAGEMENT = "Domain_dnsManagement";

	/** 
	* Alias value: Domain_userManagement
	* Hibernate value: Domain.userManagement
	*/
	String  DOMAIN_USER_MANAGEMENT = "Domain_userManagement";
	
	/** 
	* Alias value: Domain_domainManagement
	* Hibernate value: Domain.domainManagement
	*/
	String  DOMAIN_DOMAIN_MANAGEMENT = "Domain_domainManagement";

	/** 
	* Alias value: Domain_jpegLogo
	* Hibernate value: Domain.jpegLogo
	*/
	String  DOMAIN_JPEG_LOGO = "Domain_jpegLogo";


	/** 
	* Alias value: AccessPolicy_id
	* Hibernate value: AccessPolicy.id
	*/
	String  ACCESS_POLICY_ID = "AccessPolicy_id";

	/** 
	* Alias value: AccessPolicy_commonName
	* Hibernate value: AccessPolicy.commonName
	*/
	String  ACCESS_POLICY_NAME = "AccessPolicy_commonName";

	/** 
	* Alias value: AccessPolicy_maxDefinedUsers
	* Hibernate value: AccessPolicy.maxDefinedUsers
	*/
	String  ACCESS_POLICY_MAX_DEFINED_USERS = "AccessPolicy_maxDefinedUsers";

	/** 
	* Alias value: AccessPolicy_maxAllowedUsers
	* Hibernate value: AccessPolicy.maxAllowedUsers
	*/
	String  ACCESS_POLICY_MAX_ALLOWD_USERS = "AccessPolicy_maxAllowedUsers";

	/** 
	* Alias value: AccessPolicy_maxSessions4User
	* Hibernate value: AccessPolicy.maxSessions4User
	*/
	String  ACCESS_POLICY_MAX_SESSIONS_4_USER = "AccessPolicy_maxSessions4User";

	/** 
	* Alias value: AccessPolicy_exceptionThrowableIfMaximumExceeded
	* Hibernate value: AccessPolicy.exceptionThrowableIfMaximumExceeded
	*/
	String  ACCESS_POLICY_EXCEPTION_THROWABLE_IF_MAXIMUM_EXCEEDED = "AccessPolicy_exceptionThrowableIfMaximumExceeded";

	
	/** 
	* Alias value: DBConnnection_id
	* Hibernate value: DBConnnection.id
	*/
	String  DB_CONNECTION_ID = "DBConnnection_id";

	/** 
	* Alias value: DBConnnection_commonName
	* Hibernate value: DBConnnection.commonName
	*/
	String  DB_CONNECTION_COMMON_NAME = "DBConnnection_commonName";
	
	/** 
	* Alias value: DBConnnection_driverClassName
	* Hibernate value: DBConnnection.driverClassName
	*/
	String  DB_CONNECTION_DRIVER_CLASS_NAME = "DBConnnection_driverClassName";

	/** 
	* Alias value: DBConnnection_labeledURI
	* Hibernate value: DBConnnection.labeledURI
	*/
	String  DB_CONNECTION_LABELED_URI = "DBConnnection_labeledURI";

	/** 
	* Alias value: DBConnnection_uid
	* Hibernate value: DBConnnection.uid
	*/
	String  DB_CONNECTION_UID = "DBConnnection_uid";

	/** 
	* Alias value: DBConnnection_userPassword
	* Hibernate value: DBConnnection.userPassword
	*/
	String  DB_CONNECTION_USER_PASSWORD = "DBConnnection_userPassword";
}