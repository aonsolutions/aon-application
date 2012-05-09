package com.code.aon.jaas.auth;

/**
 * Login constants of <code>com.code.aon.jaas.auth.spi</code> 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-nov-2004
 * @since 1.0
 */
public interface IConstants {

	/** Default domain IP. */
	String DEFAULT_DOMAIN_IP = "127.0.0.1";
	/** Default domain name. */
	String DEFAULT_DOMAIN_NAME = "localhost";

	/** Indicates validating process state. */
	String INITIALIZE_STATE = "initialize";

	/** Indicates validating process state. */
	String LOGIN_STATE = "login";
	
	/** Indicates validating process state. */
	String ABORT_STATE = "abort";
	
	/** Indicates validating process state. */
	String LOGOUT_STATE = "logout";
	
	/** Indicates roles group name. */
	String ROLES_GROUP_NAME = "Roles";
	
	/** Indicates default group name the <code>Principal</code> belongs to. */
	String CALLERPRINCIPAL_GROUP_NAME = "CallerPrincipal";
	
	/** Indicates <b>Nominal</b> security strategy. */
	String NOMINAL_USER = "Nominal";
	
	/** Indicates <b>Concurrent</b> security strategy. */
	String CONCURRENT_USER = "Concurrent";
	
	/** Separator between shortName and Domain. */
	String IDENTITY_SEPARATOR = "@";
	
	/** Separator between Domain and Context. */
	String CONTEXT_SEPARATOR = "/";
	
	/** Unauthenticated identity tag. */
	String UNAUTHENTICATED_IDENTITY = "unauthenticatedIdentity";
	
	/** Security domain tag. */
	String SECURITY_DOMAIN = "securityDomain";
	
	/** JMX ObjectName tag. */
	String DEPLOYER_OBJECT_NAME = "objectName";
	
	/** JMX SessionManager ObjectName tag. */
	String SESSION_MANAGER_OBJECT_NAME = "sessionManagerObjectName";
	
	/** Hash Algorithm tag. */
	String ALGORITHM = "hashAlgorithm";
	
	/** Hash Encoding tag. */
	String ENCODING = "hashEncoding";
	
	/** Hash Charset tag. */
	String CHARSET = "hashCharset";
	
	/** Authentication username note. */
	String AUTH_USERNAME_NOTE = "com.code.aon.jaas.valves.USERNAME";
	
	/** Authentication password note. */
	String AUTH_PASSWORD_NOTE = "com.code.aon.jaas.valves.PASSWORD";
	
	/** Authentication methods for login configuration. */
	String AUTH_TYPE = "com.code.aon.jaas.valves.PROGRAMMATIC_WEB_LOGIN";
	
	/** Default directory for serialized application list. */
	String RESOURCES_DEFAULT_DIR = "/home/COMMON-RESOURCES/ENC/";
	
	/** Serialized session identifier name. */
	String SER_SESSION_ID = "serSessionId";

}