package es.code.cdr.core;

import es.code.cdr.CDRQName;
import es.code.repository.IProvider;
import es.code.repository.RepositoryNotInitializedException;

import java.io.IOException;
import java.util.Properties;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.code.repository.util.ClassUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 02/07/2007
 *
 */
public final class ContentRepository {

	/** ContentRepository class Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger( ContentRepository.class.getName() );

	/**
     * default repository ID's.
     */
	public static final String DEFAULT_WORKSPACE = "default";
	
    /** JCR provider. */
    private static IProvider provider;

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
     * @param sc
     * @return
     * @throws RepositoryException
     */
	public static Session getSessionInstance(SimpleCredentials sc) throws RepositoryException {
        return ContentRepository.provider.getSessionInstance( sc );
	}

    /**
     * Load repository, as configured in bootstrap.properties.
     */
    public static void init(Properties props) {
        provider = null;
        try {
            loadRepository( props );
        	if ( LOGGER.isDebugEnabled() )
        		LOGGER.debug( "System : JCR loaded" );
        }
        catch (Exception e) {
        	LOGGER.error("System : Failed to load JCR \"" + props + "\" " + e.getMessage(), e);
        }
    }

    /**
     * This method initializes the repository. You must not call this method twice.
     * 
     * @param props
     * @throws RepositoryNotInitializedException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     * @throws IOException 
     */
    public static void loadRepository(Properties props) 
    		throws RepositoryNotInitializedException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
    	if ( LOGGER.isDebugEnabled() )
    		LOGGER.debug( "System : loading JCR {}", props );
    	ContentRepository.provider = 
        	(IProvider) ClassUtil.newInstance( props.getProperty( IProvider.CUSTOM_PROVIDER_KEY ) );
    	ContentRepository.provider.init( props );
    }

    /**
     * Re-load all configured repositories.
     * @see #init()
     */
    public static void reload() {
    	if ( LOGGER.isDebugEnabled() )
    		LOGGER.debug("System : reloading JCR");
        ContentRepository.init( ContentRepository.provider.getProps() );
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
}
