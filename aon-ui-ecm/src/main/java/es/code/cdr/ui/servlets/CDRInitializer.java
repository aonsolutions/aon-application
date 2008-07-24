/**
 * 
 */
package es.code.cdr.ui.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.code.cdr.core.ContentRepository;
import es.code.cdr.ui.util.CDRUtils;
import es.code.repository.IProvider;
import es.code.repository.util.Path;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 29/06/2007
 *
 */
public class CDRInitializer implements ServletContextListener {

	/** CDRInitializer class Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger( CDRInitializer.class.getName() );

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
//        Log4jConfigurer.shutdownLogging(envProperties);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent context) {
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
//        Log4jConfigurer.initLogging( context );
		if ( StringUtils.isEmpty( System.getProperty( IProvider.JAAS_CONFIG_FILE_KEY ) ) ) {
			try {
				String jassPath = Path.getResource( "", IProvider.JAAS, "" ).getFile();
				System.setProperty( IProvider.JAAS_CONFIG_FILE_KEY, jassPath );
			} catch (SecurityException se) {
				LOGGER.error( "Failed to set " + IProvider.JAAS_CONFIG_FILE_KEY + ", check application server settings. Aborting startup", se );
				return;
			}
		} else {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info( "JAAS config file set by parent container or some other application"
						+ "\nConfig in use " + System.getProperty( IProvider.JAAS_CONFIG_FILE_KEY )
						+ "\nPlease make sure JAAS config has all necessary modules configured");
			}
		}
		if ( StringUtils.isEmpty( System.getProperty( IProvider.PROFILE_MAPPINGS_FILE_KEY ) ) ) {
			try {
				String mappings = Path.getResource( "", IProvider.PROFILE_MAPPINGS, "" ).getFile();
				System.setProperty( IProvider.PROFILE_MAPPINGS_FILE_KEY, mappings );
			} catch (SecurityException se) {
				LOGGER.error( "Failed to set " + IProvider.PROFILE_MAPPINGS_FILE_KEY + ", check application server settings. Aborting startup", se );
				return;
			}
		} else {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info( "Profile mappings file set by parent container or some other application"
						+ "\nProfile mappings in use " + System.getProperty( IProvider.PROFILE_MAPPINGS_FILE_KEY ) );
			}
		}
		
		ContentRepository.init( CDRUtils.getRepositoryInfo( bootstrap ) );
	}

}
