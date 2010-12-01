/*
 * Generated file - Do not edit!
 */
package com.code.aon.jaas.vendor.tomcat;

/**
 * MBean interface.
 */
public interface TomcatSessionManagerMBean extends com.code.aon.jaas.vendor.tomcat.SecurityMBean {

   //default object name
   public static final java.lang.String OBJECT_NAME = "Catalina:type=Security,name=AonSessionManager";

   /**
    * Authenticate principal checking the number of times the application has been accessed. If the concurrent sessions per user is enabled, checks it too.
    * @param principal
    * @param activeUsers
    * @param access
    * @throws LoginException
    */
  void authenticate(java.security.Principal principal,java.lang.Integer activeUsers,com.code.aon.jaas.client.ast.IAccessPolicy access) throws javax.security.auth.login.LoginException;

   /**
    * Problems with Tomcat forces me to implement this wrapper around authenticate base method.
    */
  void authenticate(java.lang.String principal,java.lang.String activeUsers,java.lang.String access) throws javax.security.auth.login.LoginException;

   /**
    * Enable concurrent sessions per user.
    * @param enable
    */
  void enableConcurrentSessions4User(java.lang.Boolean enable) ;

   /**
    * Get <code>SessionInfo</code> instance.
    * @param sessionId
    * @return 
    * @throws LoginException
    */
  com.code.aon.jaas.auth.session.SessionInfo getSessionInfo(java.lang.String sessionId) ;

   /**
    * Register a <code>HttpSession</code> and <code>AuthPrincipal</code> wrapped in a <code>SessionInfo</code> instance.
    * @param sessionInfo
    * @throws LoginException
    */
  void registerSession(com.code.aon.jaas.auth.session.SessionInfo sessionInfo) throws javax.security.auth.login.LoginException;

   /**
    * Problems with Tomcat forces me to implement this wrapper around registerSession base method.
    */
  void registerSession(java.lang.String sessionInfo) throws javax.security.auth.login.LoginException;

   /**
    * Remove a <code>HttpSession</code>.
    * @param sessionId
    * @throws LoginException
    */
  void removeSession(java.lang.String sessionId) throws javax.security.auth.login.LoginException;

   /**
    * Get <code>AuthenticationLoginException</code>.
    */
  com.code.aon.jaas.auth.session.AuthenticationLoginException getLastLoginException(java.lang.String str) ;

   /**
    * Set last <code>AuthenticationLoginException</code>.
    * @param lastLoginException
    */
  void fillLastLoginException(com.code.aon.jaas.auth.session.AuthenticationLoginException lastLoginException) ;

   /**
    * Return a list of <code>SessionInfo</code> instances, actives for the context passed by parameter.
    * @param context
    * @return 
    * @throws DeploymentException
    */
  java.util.List getActiveSessions(java.lang.String context) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * Invalidate session.
    * @param context
    * @param sessionId
    * @throws DeploymentException
    */
  void invalidate(java.lang.String context,java.lang.String sessionId) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * Problems with Tomcat forces me to implement this wrapper.
    */
  void fillLastLoginException(java.lang.String lastLoginException) ;

}
