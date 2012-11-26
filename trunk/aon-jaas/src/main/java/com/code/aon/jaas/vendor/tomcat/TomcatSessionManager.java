/**
 * 
 */
package com.code.aon.jaas.vendor.tomcat;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.auth.session.SessionAlreadyUsedLoginException;
import com.code.aon.jaas.auth.session.SessionInfo;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 23/05/2007
 *
 * @jmx:mbean name="Catalina:type=Security,name=AonSessionManager" extends="com.code.aon.jaas.vendor.tomcat.SecurityMBean"
 */
public class TomcatSessionManager implements TomcatSessionManagerMBean {

	/** TomcatSessionManager Logger instance. */
	private final static Logger LOGGER = LoggerFactory.getLogger(TomcatSessionManager.class);
	
	/** Last <code>LoginException</code> message. */
	private AuthenticationLoginException lastLoginException;

	/** Habilita el control de sesiones concurrentes por usuario. */
	private boolean enableConcurrentSessions4User;
	
	private Map<AuthPrincipal, Set<String>> principals = 
		Collections.synchronizedMap( new HashMap<AuthPrincipal, Set<String>>() );

	private Map<String, SessionInfo> sessionIds = 
		Collections.synchronizedMap( new HashMap<String, SessionInfo>() );

	/**
	 * Get <code>AuthenticationLoginException</code>.
     * 
	 * @jmx:managed-operation
     */
	@Override
	public AuthenticationLoginException getLastLoginException(String str) {
		return this.lastLoginException;
	}

	/**
	 * Set last <code>AuthenticationLoginException</code>.
	 *   
	 * @param lastLoginException
     * 
	 * @jmx:managed-operation
	 */
	@Override
	public void fillLastLoginException(AuthenticationLoginException lastLoginException) {
    	this.lastLoginException = lastLoginException;
	}

	/**
     * Problems with Tomcat forces me to implement this wrapper.
     * 
	 * @jmx:managed-operation
	 */
	@Override
	public void fillLastLoginException(String lastLoginException) {
    	LOGGER.info("Setting last login exception STRING [{}]", lastLoginException );
	}	
	
	/**
	 * Enable concurrent sessions per user.
	 * 
	 * @param enable
     * 
	 * @jmx:managed-operation
	 */
	@Override
	public void enableConcurrentSessions4User(Boolean enable) {
		this.enableConcurrentSessions4User = enable;
	}

	/**
	 * Get <code>SessionInfo</code> instance.
	 *   
	 * @param sessionId
	 * @return
	 * @throws LoginException
     * 
	 * @jmx:managed-operation
	 */
	@Override
	public SessionInfo getSessionInfo(String sessionId) {
		return sessionIds.get(sessionId);
	}
	
	/**
	 * Register a <code>HttpSession</code> and <code>AuthPrincipal</code> wrapped in a 
	 * <code>SessionInfo</code> instance.
	 *   
	 * @param sessionInfo
	 * @throws LoginException
     * 
	 * @jmx:managed-operation
	 */
	@Override
	public void registerSession(SessionInfo sessionInfo) throws LoginException {
		if ( getSessionInfo( sessionInfo.getSessionId() ) != null ) {
			fillLastLoginException( new SessionAlreadyUsedLoginException( "aon_login_err_3", sessionInfo.getSessionId() ) );
			throw lastLoginException;
		}
		sessionIds.put( sessionInfo.getSessionId(), sessionInfo );
		Set<String> sessionsUsedByPrincipal = (Set<String>) principals.get( sessionInfo.getPrincipal() );
		if (sessionsUsedByPrincipal == null) {
			sessionsUsedByPrincipal = Collections.synchronizedSet( new HashSet<String>() );
		}
		sessionsUsedByPrincipal.add( sessionInfo.getSessionId() );
		principals.put( sessionInfo.getPrincipal(), sessionsUsedByPrincipal );
	}
	
	/** 
     * Problems with Tomcat forces me to implement this wrapper around registerSession base method. 
     * 
	 * @jmx:managed-operation
	 */
	@Override
	public void registerSession(String sessionInfo) throws LoginException {
    	LOGGER.debug("Session Registration[{}]", sessionInfo );
	}

	/**
	 * Remove a <code>HttpSession</code>.
	 *   
	 * @param sessionId
	 * @throws LoginException
     * 
	 * @jmx:managed-operation
	 */
	@Override
	public void removeSession(String sessionId) throws LoginException {
		SessionInfo info = getSessionInfo( sessionId );
		if (info != null) {
			sessionIds.remove(sessionId);
			Set sessionsUsedByPrincipal = (Set) principals.get( info.getPrincipal() );
			if (sessionsUsedByPrincipal != null) {
				sessionsUsedByPrincipal.remove(sessionId);
				if (sessionsUsedByPrincipal.size() == 0) {
					// No need to keep AuthPrincipal in principals Map anymore 
					principals.remove( info.getPrincipal() );
				}
			}
		}
	}	

}