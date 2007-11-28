package es.code.cdr.core;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 02/07/2007
 *
 */
public class SessionManager {

	/** SessionsManager class Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger( SessionManager.class.getName() );

	/** Singleton instance */
	private static SessionManager instance;

	/**
	 * Get a <code>SessionManager</code> instance.
	 */
	public static SessionManager getInstance() {
		if( instance == null )
			instance = new SessionManager();
		return instance;
	}

	private Map<String, Session> sessions;

	/**
	 */
	private SessionManager() {
		sessions = new HashMap<String, Session>();
	}

	/**
	 * Put a <code>Session</code>
	 * 
	 * @param sessionId
	 * @param session
	 */
	public void put(String sessionId, Session session) {
		LOGGER.debug("put(" + sessionId + " ," + session + ")");
		sessions.put( sessionId, session );
    }

	/**
	 * Get a <code>Session</code>
	 * 
	 * @param sessionId
	 * @return
	 */
	public Session get(String sessionId) {
		LOGGER.debug("get(" + sessionId + ")");
		return (Session) sessions.get( sessionId );
	}

	/**
	 * Remove the <code>Session</code> bound to identifier passed by parameter.
	 * 
	 * @param sessionId
	 */
	public void remove(String sessionId) {
		LOGGER.debug("remove(" + sessionId + ")");
        sessions.remove( sessionId );
	}

}
