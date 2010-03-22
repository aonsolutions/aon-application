/*
 * Created on 05-oct-2006
 *
 */
package com.code.aon.bridge.jmx.mbean;

public interface IOperation {

    static String MESSAGES_FILE = "com.code.aon.bridge.i18n.messages";

    /** AonMainDeployer operations */
    static final String CATALINA_HOME		= "getCatalinaHome",
						DEPLOY				= "deploy",
					    UNDEPLOY			= "undeploy",
					    ISDEPLOYED			= "isDeployed",
					    GET_DEPLOYER_INFO	= "getDeployerInfo";
    
    /** AonSecurity operations */
    static final String INIT_APPLICATION_DEPLOYED 	= "initApplicationDeployed",
    					APPLICATIONS_LIST 			= "applications",
    					USER_APPLICATIONS_LIST		= "getUserApplications",
    					GET_APPLICATION 			= "getApplication",
    					GET_APPLICATION4CTX			= "getApplication4Ctx",

    					GET_DOMAIN_NAMES_IMPORT		= "getDomainNames2Import",
    					GET_DOMAIN 					= "getDomain",
    					LOAD_DOMAIN					= "loadDomain",
						ADD_DOMAIN 					= "addDomain",
    					REMOVE_DOMAIN 				= "removeDomain",
						UPDATE_DOMAIN 				= "updateDomain",
						UPDATE_DOMAIN_ACCESSPOLICY 	= "updateAccessPolicy",
						UPDATE_DOMAIN_DSMD 			= "updateDSMD",
												
						REMOVE_RELATION 			= "removeRelation",
						UPDATE_RELATION 			= "updateRelation",
						
						REMOVE_PROFILE 				= "removeProfile",
						UPDATE_PROFILE 				= "updateProfile",
						
						GET_USER 					= "getUser",
    					LOAD_USERS					= "loadUsers",
						ADD_USER					= "addUser",
   						REMOVE_USER 				= "removeUser",
    					UPDATE_USER					= "updateUser";

    /** AonSessionManager operations */
    static final String AUTHENTICATE 				= "authenticate",
    					ENABLE_CONCURRENT_SESSIONS 	= "enableConcurrentSessions4User",
    					GET_SESSION_INFO 			= "getSessionInfo",
    					REGISTER_SESSION 			= "registerSession",
    					REMOVE_SESSION 				= "removeSession",
    					GET_LASTLOGIN_EXCEPTION		= "getLastLoginException",
    					FILL_LASTLOGIN_EXCEPTION	= "fillLastLoginException",
    					GET_ACTIVE_SESSIONS			= "getActiveSessions",
    					INVALIDATE					= "invalidate";
}
