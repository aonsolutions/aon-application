package com.code.aon.jaas.vendor.tomcat;

import javax.security.auth.login.LoginException;

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.auth.session.SessionInfo;

/**
 * MBean interface.
 */
public interface TomcatSessionManagerMBean {

	/**
	 * Get <code>AuthenticationLoginException</code>.
	 */
	AuthenticationLoginException getLastLoginException(String str);

	/**
	 * Set last <code>AuthenticationLoginException</code>.
	 * 
	 * @param lastLoginException
	 */
	void fillLastLoginException(AuthenticationLoginException lastLoginException);

	/**
	 * Problems with Tomcat forces me to implement this wrapper.
	 */
	void fillLastLoginException(String lastLoginException);

	/**
	 * Enable concurrent sessions per user.
	 * 
	 * @param enable
	 */
	void enableConcurrentSessions4User(Boolean enable);

	/**
	 * Get <code>SessionInfo</code> instance.
	 * 
	 * @param sessionId
	 * @return
	 * @throws LoginException
	 */
	SessionInfo getSessionInfo(String sessionId);

	/**
	 * Register a <code>HttpSession</code> and <code>AuthPrincipal</code>
	 * wrapped in a <code>SessionInfo</code> instance.
	 * 
	 * @param sessionInfo
	 * @throws LoginException
	 */
	void registerSession(SessionInfo sessionInfo) throws LoginException;

	/**
	 * Problems with Tomcat forces me to implement this wrapper around
	 * registerSession base method.
	 */
	void registerSession(String sessionInfo) throws LoginException;

	/**
	 * Remove a <code>HttpSession</code>.
	 * 
	 * @param sessionId
	 * @throws LoginException
	 */
	void removeSession(String sessionId) throws LoginException;

}
