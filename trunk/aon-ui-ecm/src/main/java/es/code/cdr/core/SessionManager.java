package es.code.cdr.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.plugin.UserManager;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.jaas.valves.BackDoorPrincipal;

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

	/** User manager. */
	private UserManager userManager = new UserManager();
	/** Hierarchies. */
	private Map<String, HierarchyManager> hierarchies = new HashMap<String, HierarchyManager>();

	/**
	 */
	private SessionManager() {
	}

	/**
	 * Returns the authenticated user name.
	 * 
	 * @return
	 */
	public String getUserName() {
		return userManager.getUserFromPrincipal().getName();
	}

	/**
	 * Returns the list of domains this context belongs to.
	 * 
	 * @param ctx
	 * @return
	 * @throws DeploymentException
	 */
	public List<String> getApplicationDomains(String ctx) throws DeploymentException {
		return  userManager.getApplicationDomains( ctx );
	}

	/**
	 * Get a <code>HierarchyManager</code>
	 * 
	 * @param sessionId
	 * @return
	 */
	public HierarchyManager getHierarchyManager(String sessionId) {
		return (HierarchyManager) hierarchies.get( sessionId );
	}

	/**
	 * Gets hierarchy manager for the repository using JCR session. 
	 * Creates a new JCR session and hierarchy manager if not exist.
	 * 
	 * @param sessionId
	 * @param bdp
	 * @param workspaceId
	 */
	public HierarchyManager getHierarchyManager(
			String sessionId, BackDoorPrincipal bdp, String workspaceId) {
		HierarchyManager hm = getHierarchyManager( sessionId );
		if ( hm == null ) {
			hm = new HierarchyManager( bdp.getPrincipal().getName() );
			try {
				hm.init( getSession( sessionId, bdp, workspaceId ).getRootNode() );
				hierarchies.put( sessionId, hm );
			} catch (RepositoryException e) {
				LOGGER.error( e.getMessage(), e );
			}
		}
		return hm;
	}

    /**
     * Gets access controlled query manager. Use MgnlContext if possible.
     * @param request
     * @param repositoryID
     * @param workspaceID
     * @throws RepositoryException
     */
    protected QueryManager getQueryManager() throws RepositoryException {
    	return null;
    }

	/**
	 * Gets the JCR session while login, creates a new JCR session if not existing.
	 * 
	 * @param sessionId
	 * @param bdp
	 * @param workspaceId
	 * 
	 * @throws LoginException
	 * @throws RepositoryException
	 */
	private Session getSession(String sessionId, BackDoorPrincipal bdp, String workspaceId)
			throws LoginException, RepositoryException {
		return getRepositorySession( sessionId, bdp, workspaceId );
	}

	/**
	 * Get repository session.
	 * 
	 * @param sessionId
	 * @param bdp
	 * @param workspaceId
	 * 
	 * @throws LoginException
	 * @throws RepositoryException
	 */
	private Session getRepositorySession(String sessionId, BackDoorPrincipal bdp, String workspaceId)
			throws LoginException, RepositoryException {
		HierarchyManager hm = getHierarchyManager( sessionId );
        if ( hm == null ) {
    		SimpleCredentials sc = new SimpleCredentials( bdp.getPrincipal().getName() , bdp.getPassword().toCharArray() );
        	return ContentRepository.getSessionInstance( sc, workspaceId );
        }
        return hm.getWorkspace().getSession();
    }

}