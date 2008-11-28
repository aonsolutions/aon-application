package es.code.repository.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NodeType;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.code.ecm.ContentRepository;
import es.code.ecm.repository.IProvider;
import es.code.ecm.repository.util.Path;
import es.code.ecm.util.ECMUtil;

public class BootstrapTest extends TestCase {

	/** BootstrapTest class Logger */
	protected static final Logger LOGGER = LoggerFactory.getLogger( BootstrapTest.class.getName() );

	/**
	 * Test bootstrap from files. This test can be easily used as a target for profiling the repository initialization
	 * process.
	 */
	public void testBootstrap() {
		InputStream is = null;
		Properties bootstrap = new Properties();
		try {
			is = getClass().getResourceAsStream( "/bootstrap.properties" );
			bootstrap.load(is);
		} catch (IOException e) {
			LOGGER.error( "Unable to load due to an IOException: {}", e.getMessage() );
		} finally {
			if ( is != null )
				try {
					is.close();
				} catch (IOException e) {
				}
		}

        if (StringUtils.isEmpty(System.getProperty("java.security.auth.login.config"))) { //$NON-NLS-1$
            try {
                System.setProperty("java.security.auth.login.config", Path.getResource( "", IProvider.JAAS, "").getFile());
            }
            catch (SecurityException se) {
            	LOGGER.error("Failed to set java.security.auth.login.config, check application server settings"); //$NON-NLS-1$
            	LOGGER.error(se.getMessage(), se);
            	LOGGER.info("Aborting startup");
                return;
            }
        }
        else {
            if (LOGGER.isInfoEnabled()) {
            	LOGGER.info("JAAS config file set by parent container or some other application"); //$NON-NLS-1$
            	LOGGER.info("Config in use " + System.getProperty("java.security.auth.login.config")); //$NON-NLS-1$ //$NON-NLS-2$
            	LOGGER.info("Please make sure JAAS config has all necessary modules (refer config/jaas.config) configured"); //$NON-NLS-1$
            }
        }
		ContentRepository.init( ECMUtil.getRepositoryInfo( bootstrap ) );
    }

	/**
	 * The first time nodes from aon:cdr to aon:resource, both included, are created.
	 */
//	public void testAdd2Explorer() {
//		boolean updated = false;
//		String username = ContentRepository.getProvider().getProps().getProperty( IProvider.REPOSITORY_CONNECTION_USER );
//		String passwd = ContentRepository.getProvider().getProps().getProperty( IProvider.REPOSITORY_CONNECTION_PSWD );
//		try {
//			Session jcrSession = 
//				ContentRepository.getSessionInstance( new SimpleCredentials( username, passwd.toCharArray() ) );
//			assertNotNull( "Repository not properly configured.", jcrSession );
//			printRepositoryProperties( jcrSession );
//			Node cdr = jcrSession.getRootNode().getNode( ContentRepository.getNodeName( CDRQName.AON_CDR ) );
//			
//			Folder folder;
//			try {
//				folder = new Folder( cdr.getNode( "framework" ) );
//				assertEquals( "framework", folder.getName() );
//			} catch (PathNotFoundException e) {
//				folder = new Folder( cdr.addNode( "framework", ContentRepository.getNodeName( CDRQName.AON_FOLDER ) ) );
//				folder.setAuthor( "Iñaki" );
//				folder.setEntryDate( Calendar.getInstance() );
//				folder.setRoles( new String[] {"Manager"} );
//				updated = true;
//			}
//
//			URL url = getClass().getResource( "/bootstrap.properties" );
//			File file = new File( url.getFile() );
//			Document document;
//			try {
//				document = new Document( folder.getNode( file.getName() ) );
//				assertEquals( file.getName(), document.getName() );
//			} catch (PathNotFoundException e) {
//				document = new Document( folder.getNode().addNode( file.getName(), ContentRepository.getNodeName( CDRQName.AON_DOCUMENT ) ) );
//				document.setAuthor( "Iñaki" );
//				document.setEntryDate( Calendar.getInstance() );
//				document.setCategory( "Prueba" );
//				document.setLanguage( "ES_es" );
//				document.setRoles( new String[] {"Manager"} );
//				document.addContent( url );
//				updated = true;
//			}
//
//			assertTrue( updated );
//			jcrSession.save();
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}	

	/**
	 * Test repository traversal access. 
	 */
//	public void testResourceAccess() {
//		String username = ContentRepository.getProvider().getProps().getProperty( IProvider.REPOSITORY_CONNECTION_USER );
//		String passwd = ContentRepository.getProvider().getProps().getProperty( IProvider.REPOSITORY_CONNECTION_PSWD );
//		try {
//			Session jcrSession = 
//				ContentRepository.getSessionInstance( new SimpleCredentials( username, passwd.toCharArray() ) );
//			assertNotNull( "Repository not properly configured.", jcrSession );
//			Node cdr = jcrSession.getRootNode().getNode( ContentRepository.getNodeName( CDRQName.AON_CDR ) );
//
//			// Gets Node from CDR --> Folder/Document/Content
//			Node resNode = 
//				cdr.getNode( "framework/bootstrap.properties/" + ContentRepository.getNodeName( CDRQName.AON_CONTENT ) );
//			InputStream is = resNode.getProperty( QName.NS_JCR_PREFIX + ":" + QName.JCR_DATA.getLocalName() ).getStream();
//			LineNumberReader lnr = new LineNumberReader( new InputStreamReader( is ) );
//			try {
//				String line = lnr.readLine();
//				while ( line != null ) {
//					System.out.println( line );
//					line = lnr.readLine();
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * XPATH query.
	 */
	public void testGetContent() {
//		String username = ContentRepository.getProvider().getRi().getProps().getProperty( IProvider.REPOSITORY_CONNECTION_USER );
//		String passwd = ContentRepository.getProvider().getRi().getProps().getProperty( IProvider.REPOSITORY_CONNECTION_PSWD );
//		try {
//			Session jcrSession = 
//				ContentRepository.getSessionInstance( new SimpleCredentials( username, passwd.toCharArray() ) );
//			assertNotNull( "Repository not properly configured.", jcrSession );
//			try {
//				QueryParameters params = new QueryParameters();
//				params.setBycontent( "subsecuente" );
//				QueryResult result = 
//					QueryManager.getInstance().execute( jcrSession, params, Query.XPATH );
//				NodeIterator it = result.getNodes();
//				while (it.hasNext()) {
//					Node n = it.nextNode();
//					System.out.println( n.getName() + " " + n.getPath() );
//				}
//			} catch (RepositoryException e) {
//				e.printStackTrace();
//			} catch (InvalidStatementException e) {
//				e.printStackTrace();
//			}
//		} catch (RepositoryException e) {
//			e.printStackTrace();
//		}
	}

	private void printRepositoryProperties(Session jcrSession) {
		try {
			System.out.println( jcrSession.getWorkspace().getName() + " Prefixes" );
			String[] prefixes = jcrSession.getWorkspace().getNamespaceRegistry().getPrefixes();
			for (int i = 0; i < prefixes.length; i++) {
				String string = prefixes[i];
				System.out.println( string );
			}
			System.out.println( jcrSession.getWorkspace().getName() + " URIs" );
			String[] uris = jcrSession.getWorkspace().getNamespaceRegistry().getURIs();
			for (int i = 0; i < uris.length; i++) {
				String string = uris[i];
				System.out.println( string );
			}
		} catch (RepositoryException e) {
			fail("Exception caught: " + e.getMessage());
		}
		try {
			System.out.println( jcrSession.getWorkspace().getName() + " AllNodeTypes");
			Iterator iter = jcrSession.getWorkspace().getNodeTypeManager().getAllNodeTypes();
			while ( iter.hasNext() ) {
				NodeType element = (NodeType) iter.next();
				System.out.println( element.getName() + " "  + element.getPrimaryItemName() + " " + element.getPropertyDefinitions() );
			}
			System.out.println( jcrSession.getUserID() 
					+ " " + jcrSession.getRootNode().getPath() 
					+ " " + jcrSession.getRootNode().getName()
					+ " " + jcrSession.getRootNode().isNodeType("mix:referenceable")
					+ " " + jcrSession.getRootNode().getPrimaryNodeType().getName() 
								);
		} catch (RepositoryException e) {
			fail("Exception caught: " + e.getMessage());
		}		
	}

}
