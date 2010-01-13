/**
 * 
 */
package com.code.aon.jaas.auth.session;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.session.event.ExpiredSessionEvent;
import com.code.aon.jaas.auth.session.event.ExpiredSessionListener;
import com.code.aon.jaas.client.ast.IAccessPolicy;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 16/05/2007
 *
 */
public class AuthenticationManager {

    /** AuthenticationManager Logger class. */
	protected static final Log LOGGER = LogFactory.getLog( AuthenticationManager.class.getName() );
	/** */
	private Map<AuthPrincipal, Set<String>> principals = 
		Collections.synchronizedMap( new HashMap<AuthPrincipal, Set<String>>() );
	/** */
	private Map<String, SessionInfo> sessionIds = 
		Collections.synchronizedMap( new HashMap<String, SessionInfo>() );
	/** Habilita el control de sesiones concurrentes por usuario. */
	private boolean enableConcurrentSessions4User;
	/** Last <code>LoginException</code> message. */
	private AuthenticationLoginException lastLoginException;
	private List<ExpiredSessionListener> listeners = new ArrayList<ExpiredSessionListener>();

	/**
     * Add a ExpiredSessionListener to the listener list
     * 
     * @param l - The ExpiredSessionListener to be added
     */
    public void addExpiredSessionListener(ExpiredSessionListener l) {
        if (!listeners.contains(l))
        	listeners.add(l);
    }

    /**
     * Remove a ExpiredSessionListener from the listener list
     * 
     * @param l - The ExpiredSessionListener to be added
     */
    public void removeExpiredSessionListener(ExpiredSessionListener l) {
    	listeners.remove(l);
    }

    /**
	 * @return the enableConcurrentSessions4User
	 */
	public boolean isEnableConcurrentSessions4User() {
		return enableConcurrentSessions4User;
	}

	/**
	 * @param enableConcurrentSessions4User the enableConcurrentSessions4User to set
	 */
	public void setEnableConcurrentSessions4User(boolean enableConcurrentSessions4User) {
		this.enableConcurrentSessions4User = enableConcurrentSessions4User;
	}

	/**
	 * @return the lastLoginException
	 */
	public AuthenticationLoginException getLastLoginException() {
		return lastLoginException;
	}

	/**
	 * @param lastLoginException the lastLoginException to set
	 */
	public void setLastLoginException(AuthenticationLoginException lastLoginException) {
		this.lastLoginException = lastLoginException;
	}

	/**
	 * Attempts to authenticate the passed {@link AuthPrincipal} object.
	 * 
	 * @param principal
	 * @throws LoginException
	 */
	public void authenticate(Principal principal, Integer activeUsers, IAccessPolicy access) 
			throws LoginException {
//	Check if maximum number of allowed users has reached.
		checkAuthenticationAllowed( (AuthPrincipal) principal, activeUsers, access );
//	Check if multiple sessions per user is allowed.
		if ( isEnableConcurrentSessions4User() ) {
			checkConcurrentAuthenticationAllowed( (AuthPrincipal) principal, access );
		}
	}

	/**
	 * Devuelve las sesiones abiertas para un usuario.
	 * 
	 * @param principal
	 * @return
	 */
	public List<SessionInfo> getAllSessions(AuthPrincipal principal) {
		List<SessionInfo> list = new ArrayList<SessionInfo>();
		Set<String> sessionsUsedByPrincipal = (Set<String>) principals.get(principal);
		if (sessionsUsedByPrincipal == null) {
			return list;
		}
		Iterator<String> iter = sessionsUsedByPrincipal.iterator();
		while (iter.hasNext()) {
			String sessionId = iter.next();
			list.add( getSessionInformation( sessionId ) );
		}
		return list;
	}

	/**
	 * Devuelve las sesiones abiertas para un dominio.
	 * 
	 * @param domain
	 * @return
	 */
	public List<SessionInfo> getAllSessions(String domain) {
		List<SessionInfo> list = new ArrayList<SessionInfo>();
		Iterator<AuthPrincipal> iter = principals.keySet().iterator();
		while (iter.hasNext()) {
			AuthPrincipal principal = iter.next();
			if ( principal.getDomain().equals( domain ) ) {
				list.addAll( getAllSessions( principal ) );
			}
		}
		return list;
	}

	public SessionInfo getSessionInformation(String sessionId) {
		return sessionIds.get(sessionId);
	}

	/**
	 * Register a <code>SessionInfo</code> for concurrent session authentication.
	 * 
	 * @param sessionInfo
	 * @throws LoginException
	 */
	public void registerSession(SessionInfo sessionInfo) throws LoginException {
		if ( getSessionInformation( sessionInfo.getSessionId() ) != null ) {
			setLastLoginException( new SessionAlreadyUsedLoginException( "aon_login_err_3", sessionInfo.getSessionId() ) );
			throw getLastLoginException();
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
	 * Remove session.
	 * 
	 * @param sessionId
	 */
	@SuppressWarnings("unchecked")
	public void removeSession(String sessionId) {
		SessionInfo info = getSessionInformation( sessionId );
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

	protected void checkAuthenticationAllowed(AuthPrincipal principal, int activeUsers, IAccessPolicy access) 
			throws LoginException {
		int sessionCount = activeUsers;
		if ( isEnableConcurrentSessions4User() ) {
			SessionInfo[] sessions = getAllSessions( principal.getDomain() ).toArray( new SessionInfo[] {} );
//A user could have more than one session, so activeUsers != sessions.length
			sessionCount = sessions.length;
		}
		int allowableSessions = access.getMaxAllowedUsers();
		if ( allowableSessions == -1 ) {
			return;
		}
		if ( sessionCount < allowableSessions ) {
			return;
		}
		setLastLoginException( new MaximumLoginException( "aon_login_err_4", Integer.toString( allowableSessions ) ) );
		throw getLastLoginException();
	}

	protected void checkConcurrentAuthenticationAllowed(AuthPrincipal principal, IAccessPolicy access) 
			throws LoginException {
		SessionInfo[] sessions = getAllSessions( principal ).toArray( new SessionInfo[] {} );
		int sessionCount = sessions.length;
		int allowableSessions = access.getMaxSessions4User();
		if (sessionCount < allowableSessions || allowableSessions == -1) {
			return;
		}
		if ( access.isExceptionThrowableIfMaximumExceeded() || (sessions == null)) {
			setLastLoginException( new ConcurrentLoginException( "aon_login_err_5", Integer.toString( allowableSessions ) ) );
			throw getLastLoginException();
		}
		//Determine least recently used session, and mark it for invalidation
		SessionInfo leastRecentlyUsed = null;
		for (int i = 0; i < sessions.length; i++) {
		    if ( (leastRecentlyUsed == null) || 
		    		sessions[i].getLastAccessedTime() < leastRecentlyUsed.getLastAccessedTime() ) {
		        leastRecentlyUsed = sessions[i];
		    }
		}
		if ( leastRecentlyUsed != null )
			fireExpiredSession( new ExpiredSessionEvent( leastRecentlyUsed ) );
	}

    /**
     * Fire an <code>ExpiredSessionEvent</code>.
     * 
     * @param event
     */
    protected void fireExpiredSession(ExpiredSessionEvent event) {
        Iterator<ExpiredSessionListener> iter = listeners.iterator();
        while (iter.hasNext()) {
        	ExpiredSessionListener l = iter.next();
            l.expiredSession( event );
        }
    }

}
