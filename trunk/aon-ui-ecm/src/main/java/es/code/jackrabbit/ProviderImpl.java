/**
 * 
 * Copyright 1993-2006 obinary Ltd. (http://www.obinary.com) All rights reserved.
 */
package es.code.jackrabbit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import javax.jcr.NamespaceException;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeTypeManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.core.WorkspaceImpl;
import org.apache.jackrabbit.core.jndi.RegistryHelper;
import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.apache.jackrabbit.core.nodetype.NodeTypeDef;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.core.nodetype.xml.NodeTypeReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.client.ast.IDomain;

import es.code.cdr.CDRQName;
import es.code.cdr.core.ContentRepository;
import es.code.repository.IProvider;
import es.code.repository.RepositoryInfo;
import es.code.repository.RepositoryNotInitializedException;
import es.code.repository.util.Path;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 19/06/2007
 */

public class ProviderImpl implements IProvider {

	/** ProviderImpl class Logger */
	protected static final Logger LOGGER = LoggerFactory.getLogger( ProviderImpl.class.getName() );

	/** Repository properties */
	private RepositoryInfo ri;

	/** JCR repository instance */
	private Repository repository;

	/*(non-Javadoc)
	 * @see es.code.repository.IProvider#init(java.util.Properties)
	 */
	public void init(RepositoryInfo ri) throws IOException, RepositoryNotInitializedException {
		this.ri = ri;
		Properties props = this.ri.getProps();

		/* connect to repository */
		String configFile = props.getProperty( REPOSITORY_CONFIG_FILENAME_KEY );
		URL configURL = Path.getResource( configFile, REPOSITORY, REPOSITORY_NAME_KEY );
		configFile = Path.getAbsolutePath( configURL );
		String repositoryHome = props.getProperty( REPOSITORY_HOME_KEY );
		repositoryHome = Path.getAbsoluteFileSystemPath( repositoryHome ); 
    	if ( LOGGER.isDebugEnabled() )
    		LOGGER.debug( "Loading repository at {} (config file: {})", repositoryHome, configFile );
//		boolean addShutdownTask = false;
		final String repositoryName = props.getProperty( REPOSITORY_NAME_KEY );
		final Hashtable<String, String> env = new Hashtable<String, String>();
		env.put( Context.INITIAL_CONTEXT_FACTORY, props.getProperty( NAMING_FACTORY_CLASS_KEY ) );
		env.put( Context.PROVIDER_URL, props.getProperty( PROVIDER_URL_KEY ) );
		try {
			InitialContext ctx = new InitialContext(env);
			// first try to find the existing object if any
			try {
				this.repository = (Repository) ctx.lookup(repositoryName);
			} catch (NameNotFoundException ne) {
		    	if ( LOGGER.isDebugEnabled() )
		    		LOGGER.debug( "No JNDI bound Repository found with name {} , trying to initialize a new Repository", repositoryName );
				RegistryHelper.registerRepository(ctx, repositoryName, configFile, repositoryHome, true);
				this.repository = (Repository) ctx.lookup(repositoryName);
//				addShutdownTask = true;
			}
            validateWorkspaces();
		} catch (NamingException e) {
			LOGGER.error("Unable to initialize repository: " + e.getMessage(), e);
			throw new RepositoryNotInitializedException(e);
		} catch (RepositoryException e) {
			LOGGER.error("Unable to initialize repository: " + e.getMessage(), e);
			throw new RepositoryNotInitializedException(e);
		} catch (TransformerFactoryConfigurationError e) {
			LOGGER.error("Unable to initialize repository: " + e.getMessage(), e);
			throw new RepositoryNotInitializedException(e);
		}
//		if (addShutdownTask) {
//			ShutdownManager.addShutdownTask(new ShutdownTask() {
//
//				public boolean execute(com.magn.repository.context.Context context) {
//					LOGGER.info("Shutting down repository bound to '{}'", repositoryName);
//
//					try {
//						Context ctx = new InitialContext(env);
//						RegistryHelper.unregisterRepository(ctx, repositoryName);
//					} catch (NamingException ne) {
//						String msg = 
//							MessageFormat.format("Unable to shutdown repository {0}: {1} {2}", 
//									new Object[]{repositoryName, ne.getClass().getName(), ne.getMessage()}); 
//						LOGGER.warn( msg, ne );
//					} catch (Throwable e) {
//						String msg = 
//							MessageFormat.format("Failed to shutdown repository {0}: {1} {2}", 
//									new Object[]{repositoryName, e.getClass().getName(), e.getMessage()});
//						LOGGER.warn( msg, e);
//               		}
//					return true;
//				}
//			});
//		}
	}

	@Override
	public RepositoryInfo getRi() {
		return ri;
	}

	@Override
	public Repository getUnderlineRepository() throws RepositoryNotInitializedException {
		if (this.repository == null) {
			throw new RepositoryNotInitializedException("Null repository"); //$NON-NLS-1$
		}
		return this.repository;
	}

	@Override
	public Session getSessionInstance(SimpleCredentials sc, String workspaceId) throws RepositoryException {
		String wsId = getDefaultWorkspaceName( workspaceId );
		Session jcrSession = this.repository.login( sc, wsId );
		InputStream xml = getNodeTypeDefinition( StringUtils.EMPTY );
		registerNamespace( CDRQName.NS_AON_PREFIX, CDRQName.NS_AON_URI, jcrSession.getWorkspace() );
		registerNodeTypes( jcrSession, xml );
		try {
			jcrSession.getRootNode().getNode( CDRQName.NS_AON_PREFIX + ":" + CDRQName.AON_CDR.getLocalName() );
		} catch (PathNotFoundException e) {
			jcrSession.getRootNode().addNode( CDRQName.NS_AON_PREFIX + ":" + CDRQName.AON_CDR.getLocalName() );
			jcrSession.getRootNode().save();
		}
		return jcrSession;
	}

	@Override
	public String getDefaultWorkspaceName(String workspaceId) {
//	Checks if workspaceId name equals to "localhost"
		return ( workspaceId.equals( IDomain.DEFAULT_DOMAIN_NAME ) )? 
				ContentRepository.DEFAULT_WORKSPACE: workspaceId;
	}

	@Override
	public void registerNamespace(String namespacePrefix, String uri, Workspace workspace) throws RepositoryException {
		try {
			workspace.getNamespaceRegistry().getURI(namespacePrefix);
		} catch (NamespaceException e) {
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("registering prefix [{}] with uri {}", namespacePrefix, uri );
			workspace.getNamespaceRegistry().registerNamespace(namespacePrefix, uri);
		}
	}

	@Override
	public void unregisterNamespace(String prefix, Workspace workspace) throws RepositoryException {
		workspace.getNamespaceRegistry().unregisterNamespace(prefix);
	}

	@Override
	public void registerNodeTypes() throws RepositoryException {
		registerNodeTypes( StringUtils.EMPTY );
	}

	@Override
	public void registerNodeTypes(String configuration) throws RepositoryException {
		if ( StringUtils.isEmpty( configuration ) ) {
			configuration = (String) this.ri.getProps().getProperty( CUSTOM_NODETYPES_KEY );
		}
		InputStream xml = getNodeTypeDefinition( configuration );
		registerNodeTypes( xml );
	}

	@Override
	public void registerNodeTypes(InputStream xmlStream) throws RepositoryException {
		String user = this.ri.getProps().getProperty( IProvider.REPOSITORY_CONNECTION_USER ) 
					+ this.ri.getProps().getProperty( IProvider.REPOSITORY_CONNECTION_CONTEXT );
		SimpleCredentials credentials = 
			new SimpleCredentials( user, this.ri.getProps().getProperty( IProvider.REPOSITORY_CONNECTION_PSWD ).toCharArray() );
		Session jcrSession = this.repository.login(credentials);
		registerNodeTypes( jcrSession, xmlStream );
	}

	@Override
	public boolean registerWorkspace(String workspaceName) throws RepositoryException {
		String user = this.ri.getProps().getProperty( IProvider.REPOSITORY_CONNECTION_USER ) 
					+ this.ri.getProps().getProperty( IProvider.REPOSITORY_CONNECTION_CONTEXT );
		SimpleCredentials credentials = 
				new SimpleCredentials( user, this.ri.getProps().getProperty( IProvider.REPOSITORY_CONNECTION_PSWD ).toCharArray() );
		return registerWorkspace( credentials, workspaceName );
	}

	@Override
	public boolean registerWorkspace(SimpleCredentials credentials, String workspaceName) throws RepositoryException {
		try {
			Session jcrSession = this.repository.login( credentials );
			String wsId = getDefaultWorkspaceName( workspaceName );
			WorkspaceImpl defaultWorkspace = (WorkspaceImpl) jcrSession.getWorkspace();
			String[] workspaceNames = defaultWorkspace.getAccessibleWorkspaceNames();

			boolean alreadyExists = ArrayUtils.contains( workspaceNames, wsId );
			if ( !alreadyExists ) { // check if workspace already exists
				defaultWorkspace.createWorkspace( wsId );
			}
			jcrSession.logout();
			return !alreadyExists;
		} catch (ClassCastException e) {
			// this could happen if the repository provider does not have proper Shared API for the
			// application server like at the moment in Jackrabbit
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Unable to register workspace, will continue", e);
		}
		return false;
	}

    /**
     * Checks if all workspaces are present according to the , 
     * creates any missing workspace
     * 
     */
	private void validateWorkspaces() {
		Iterator<String> names = this.ri.getWorkspaces().iterator();
		while ( names.hasNext() ) {
			try {
				registerWorkspace( names.next() );
			} catch (RepositoryException e) {
				LOGGER.error( "Unable to register, " + e.getMessage() + " will continue" );
			}
		}
	}

    /**
     * Node type registration is entirely dependent on the implementation. 
     * Refer JSR-170 specifications.
     * 
	 * @param jcrSession
	 * @param xmlStream
	 * @throws RepositoryException
	 */
	private void registerNodeTypes(Session jcrSession, InputStream xmlStream) throws RepositoryException {
		Workspace workspace = jcrSession.getWorkspace();

		// should never happen
		if (xmlStream == null) {
			throw new MissingNodetypesException();
		}

		NodeTypeDef[] types;
		try {
			types = NodeTypeReader.read(xmlStream);
		} catch (InvalidNodeTypeDefException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RepositoryException(e.getMessage(), e);
		} finally {
	        try {
	            if ( xmlStream != null ) {
	            	xmlStream.close();
	            }
	        } catch (IOException ioe) {
	            // ignore
	        }
		}

		NodeTypeManager ntMgr = workspace.getNodeTypeManager();
		NodeTypeRegistry ntReg;
		try {
			ntReg = ((NodeTypeManagerImpl) ntMgr).getNodeTypeRegistry();
		} catch (ClassCastException e) {
			// this could happen if the repository provider does not have proper Shared API for the
			// application server like at the moment in Jackrabbit
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Failed to get  NodeTypeRegistry",e);
			return;
		}

		for (int j = 0; j < types.length; j++) {
			NodeTypeDef def = types[j];

			try {
				ntReg.getNodeTypeDef(def.getName());
			} catch (NoSuchNodeTypeException nsne) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug( "registering nodetype {}", def.getName() );
				try {
					ntReg.registerNodeType(def);
				} catch (InvalidNodeTypeDefException e) {
					throw new RepositoryException(e.getMessage(), e);
				} catch (RepositoryException e) {
					throw new RepositoryException(e.getMessage(), e);
				}
			}

		}
	}

	/**
	 * @param configuration
	 * @return InputStream of node type definition file
	 */
	private InputStream getNodeTypeDefinition(String configuration) {
		URL resource = Path.getResource( configuration, NODETYPES, CUSTOM_NODETYPES_KEY );
		try {
			return resource.openStream();
		} catch (IOException e1) {
			// should never happen
			LOGGER.error( "Unable to open {} definition: {}", CUSTOM_NODETYPES_KEY, configuration );
		}
		return null;
	}

}
