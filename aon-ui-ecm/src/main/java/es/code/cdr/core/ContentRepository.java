package es.code.cdr.core;

import java.io.IOException;
import java.util.Properties;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.code.cdr.CDRQName;
import es.code.repository.IProvider;
import es.code.repository.RepositoryInfo;
import es.code.repository.RepositoryNotInitializedException;
import es.code.repository.util.ClassUtil;

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
//    /** Predefined Hierarchy managers. */
//	private static Map<String, HierarchyManager> hierarchies = new HashMap<String, HierarchyManager>();

    /**
     * Utility class, don't instantiate.
     */
    private ContentRepository() {
        // unused constructor
    }

    /**
     * Returns repository provider.
     */
    public static IProvider getProvider() {
        return ContentRepository.provider;
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
    	/*
    	// load hierarchy managers for each workspace
        Iterator<String> workspaces = ri.getWorkspaces().iterator();
        while (workspaces.hasNext()) {
            loadHierarchyManager( ContentRepository.provider, workspaces.next() );
        }
        */
    }

//    /**
//     * Load hierarchy manager for the specified workspace.
//     * 
//     * @param workspaceId
//     */
//    private static void loadHierarchyManager(IProvider provider, String workspaceId) {
//        try {
//        	SimpleCredentials sc = getSimpleCredentials();
//            Session jcrSession = provider.getSessionInstance( sc, workspaceId );
//    		HierarchyManager hm = new HierarchyManager( sc.getUserID() );
//    		hm.init( jcrSession.getRootNode() );
//            ContentRepository.hierarchies.put( workspaceId, hm );
//            hm.setQueryManager( QueryManager.getInstance() );
//        } catch (RepositoryException re) {
//        	LOGGER.error( "System : Failed to initialize hierarchy manager for JCR; " + re.getMessage(), re);
//        }
//    }
//
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
    public static String getNodeName(CDRQName qname) {
		return CDRQName.NS_AON_PREFIX + ":" + qname.getLocalName();
    }    

    private static SimpleCredentials getSimpleCredentials() {
		Properties props = provider.getRi().getProps();
		String user = props.getProperty( IProvider.REPOSITORY_CONNECTION_USER ) + props.getProperty( IProvider.REPOSITORY_CONNECTION_CONTEXT );
		return new SimpleCredentials( user, props.getProperty( IProvider.REPOSITORY_CONNECTION_PSWD ).toCharArray() );

    }

}
