package es.code.ecm;

import java.io.IOException;
import java.util.Properties;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.code.ecm.repository.IProvider;
import es.code.ecm.repository.RepositoryInfo;
import es.code.ecm.repository.RepositoryNotInitializedException;
import es.code.ecm.repository.util.ClassUtil;
import es.code.ecm.security.acl.AclManager;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 02/07/2007
 *
 */
public final class ContentRepository {

	/** Default repository ID's. */
	public static final String DEFAULT_WORKSPACE = "default";

	/** ContentRepository Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger( ContentRepository.class.getName() );
    /** JCR provider. */
    private static IProvider provider;

    /**
     * Utility class, don't instantiate.
     */
    private ContentRepository() {
        // unused constructor
    }

    /**
     * Returns repository.
     * 
     * @throws RepositoryNotInitializedException 
     */
    public static Repository getRepository() throws RepositoryNotInitializedException {
        return (Repository) ContentRepository.provider.getUnderlineRepository();
    }

	/**
	 * Gets repository information.
	 * 
	 * @return
	 */
    public static RepositoryInfo getRi() {
        return ContentRepository.provider.getRi();
    }

    /**
     * Register a new workspace in the current repository using passed 
     * by paramenter credentials
     * 
     * @param workspaceName workspace name
     * @param sc credentials
     * @return <code>true</code> true if the workspace is registered now of <code>false</code> 
     * if it was already registered
     * @throws RepositoryException if any exception occours during registration
     */
    public static boolean registerWorkspace(SimpleCredentials sc, String workspaceName) 
    			throws RepositoryException {
        return ContentRepository.provider.registerWorkspace( sc, workspaceName );
    }

    /**
     * Gets the ACL manager.
     * 
     * @return
     */
    public static AclManager getAclManager() {
        return ContentRepository.provider.getAclManager();
    }

    /**
     * Gets default workspace name
     * 
     * @return default name if there are no workspaces defined or there is no workspace present with name "default",
     * otherwise return same name as repository name.
     */
    public static String getDefaultWorkspaceName(String repositoryId) {
        return DEFAULT_WORKSPACE;
    }

    /**
     * Gets bound Session.
     * 
     * @param workspaceId
     * @return
     * @throws RepositoryException
     */
	public static Session getSessionInstance(String workspaceId) throws RepositoryException {
		return getSessionInstance( getSimpleCredentials(), workspaceId );
	}

    /**
     * Gets bound Session.
     * 
     * @param sc
     * @param workspaceId
     * @return
     * @throws RepositoryException
     */
	public static Session getSessionInstance(SimpleCredentials sc, String workspaceId) throws RepositoryException {
		return ContentRepository.provider.getSessionInstance( sc, workspaceId );
	}

    /**
     * Load repository, as configured in bootstrap.properties.
     */
    public static void init(RepositoryInfo ri) {
        provider = null;
        try {
            loadRepository( ri );
        	if ( LOGGER.isDebugEnabled() )
        		LOGGER.debug( "System : JCR loaded" );
        }
        catch (Exception e) {
        	LOGGER.error("System : Failed to load JCR \"" + ri.getProps() + "\" " + e.getMessage(), e);
        }
    }

    /**
     * This method initializes the repository. You must not call this method twice.
     * 
     * @param ri
     * @throws RepositoryNotInitializedException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws IOException 
     */
    public static void loadRepository(RepositoryInfo ri) 
    		throws RepositoryNotInitializedException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
    	if ( LOGGER.isDebugEnabled() )
    		LOGGER.debug( "System : loading JCR {}", ri.getProps() );
    	ContentRepository.provider = 
        	(IProvider) ClassUtil.newInstance( ri.getProps().getProperty( IProvider.CUSTOM_PROVIDER_KEY ) );
    	ContentRepository.provider.init( ri );
    }

    /**
     * Re-load all configured repositories.
     * @see #init()
     */
    public static void reload() {
    	if ( LOGGER.isDebugEnabled() )
    		LOGGER.debug("System : reloading JCR");
        ContentRepository.init( ContentRepository.provider.getRi() );
    }

    /**
     * Return the repository namespace prefix besides the qname passed by parameter.
     * For example: aon:author
     * 
     * @param qname
     * @return
     */
    public static String getNodeName(ECMQName qname) {
		return ECMQName.NS_AON_PREFIX + ":" + qname.getLocalName();
    }    

    private static SimpleCredentials getSimpleCredentials() {
		Properties props = provider.getRi().getProps();
		String user = props.getProperty( IProvider.REPOSITORY_CONNECTION_USER );
		return new SimpleCredentials( user, props.getProperty( IProvider.REPOSITORY_CONNECTION_PSWD ).toCharArray() );

    }

}
