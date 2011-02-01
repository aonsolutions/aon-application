package com.code.aon.jaas.auth;

/**
 * Login constants of <code>com.code.aon.jaas.auth.spi</code> 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-nov-2004
 * @since 1.0
 */
public interface IConstants {

    /** Indicates validating process state. */
    static final String INITIALIZE_STATE = "initialize";

    /** Indicates validating process state. */
    static final String LOGIN_STATE = "login";

    /** Indicates validating process state. */
    static final String ABORT_STATE = "abort";

    /** Indicates validating process state. */
    static final String LOGOUT_STATE = "logout";

    /** Indicates roles group name. */
    static final String ROLES_GROUP_NAME = "Roles";

    /** Indicates default group name the <code>Principal</code> belongs to. */
    static final String CALLERPRINCIPAL_GROUP_NAME = "CallerPrincipal";

    /** Indicates <b>Nominal</b> security strategy. */
    static final String NOMINAL_USER = "Nominal";

    /** Indicates <b>Concurrent</b> security strategy. */
    static final String CONCURRENT_USER = "Concurrent";

	/** Separator between shortName and Domain. */
	static final String IDENTITY_SEPARATOR = "@";

	/** Separator between Domain and Context. */
	static final String CONTEXT_SEPARATOR = "/";

	/** Unauthenticated identity tag. */
    static final String UNAUTHENTICATED_IDENTITY = "unauthenticatedIdentity";

	/** Security domain tag. */
    static final String SECURITY_DOMAIN = "securityDomain";

	/** JMX ObjectName tag. */
    static final String DEPLOYER_OBJECT_NAME = "objectName";

	/** JMX SessionManager ObjectName tag. */
    static final String SESSION_MANAGER_OBJECT_NAME = "sessionManagerObjectName";

	/** Hash Algorithm tag. */
    static final String ALGORITHM = "hashAlgorithm";

	/** Hash Encoding tag. */
    static final String ENCODING = "hashEncoding";

	/** Hash Charset tag. */
    static final String CHARSET = "hashCharset";

	/** Authentication username note. */
	static final String AUTH_USERNAME_NOTE = "com.code.aon.jaas.valves.USERNAME";
	
	/** Authentication password note. */
	static final String AUTH_PASSWORD_NOTE = "com.code.aon.jaas.valves.PASSWORD";
	
	/** Authentication methods for login configuration. */
	static final String AUTH_TYPE = "com.code.aon.jaas.valves.PROGRAMMATIC_WEB_LOGIN";
	
	/** Default directory for serialized application list. */
	static final String RESOURCES_DEFAULT_DIR = "/home/COMMON-RESOURCES/ENC/";
	
	/** Serialized session identifier name. */
	static final String SER_SESSION_ID = "serSessionId";

}