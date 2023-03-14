package com.code.aon.jaas.auth;

/**
 * Login constants of <code>com.code.aon.jaas.auth.spi</code> 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-nov-2004
 * @since 1.0
 */
public interface IConstants {
	
	String AON_LOGIN_EXCEPTION = "aon_login_exception";
	
	String ADMIN_IDENTITY_SEPARATOR = "=";
	
	/** Separator between shortName and Domain. */
	String IDENTITY_SEPARATOR = "@";
	
	/** Separator between Domain and Context. */
	String CONTEXT_SEPARATOR = "/";

	/** Default role of any user. */
	String DEFAULT_ROLE = "User";
	
	/** Default domain IP. */
	//String DEFAULT_DOMAIN_IP = "127.0.0.1";
	
	/** Default domain name. */
	//String DEFAULT_DOMAIN_NAME = "localhost";

	/** Default context path. */
	String DEFAULT_CONTEXT_PATH = "/aon-aio";
	
	String BASE64_ENCODING = "BASE64";
	
	//String CONFIG_RESOURCE_NAME = "deployed.xml";
	
    /** Default Config Resource name. */
	//String DEFAULT_CONFIG_RESOURCE_NAME = "/conf/aon.workspace/" + CONFIG_RESOURCE_NAME;

}