/*
 * Created on 28-jun-2005
 *
 */
package com.code.aon.ui.common.listener;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.bean.BeanConfigParser;
import com.code.aon.common.util.Classpath;

/**
 * BeanRegisterContextListener is used to parse the configuration file bean-config.xml. 
 * ManagerBeanListeners are defined within this file. BeanRegisterContextListener must be 
 * defined in web.xml as follows: 
 * 
 *<pre>
 * &lt;istener&gt;
 *      &lt;listener-class&gt;
 *          com.code.aon.ui.common.listener.BeanRegisterContextListener
 *      &lt;/listener-class&gt;
 * &lt;/listener&gt;
 * 
 *  &lt;context-param&gt;
 *      &lt;param-name&gt;config-file&lt;/param-name&gt;
 *      &lt;param-value&gt;/WEB-INF/conf/bean_config.xml&lt;/param-value&gt;
 *   &lt;/context-param&gt;
 *</pre> 
 * @author Consulting & Development. Aimar Tellitu - 26-ene-2006
 * @since 1.0
 */
public class BeanRegisterContextListener implements ServletContextListener {

	/** Gets a suitable <code>Logger</code>. */
	private final static Logger LOGGER = LoggerFactory.getLogger(BeanRegisterContextListener.class);
	
	/** The Constant CONFIG_FILE_PARAM. */
	private static final String CONFIG_FILE = "bean-config.xml";

	private void addBeanConfig( URL resource ) throws IOException {
		try {
			InputStream is = resource.openStream();
			
			BeanConfigParser parser = BeanConfigParser.getInstance();
			parser.parse( is );
			
			is.close();
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}		
	}
	
	/**
	 * Parses the config file.
	 * 
	 * @param sce the ServletContextEvent
	 */
	public void contextInitialized(ServletContextEvent sce) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
	        URL[] urls = Classpath.search(cl, "META-INF/", CONFIG_FILE);
	        for (int i = 0; i < urls.length; i++) {
	            try {
	            	addBeanConfig( urls[i] );
	            	LOGGER.info("Added Bean Config from: " + urls[i]);
	            } catch (Exception e) {
	                LOGGER.error( "Error Loading Library: " + urls[i], e);
	            }
	        }
		} catch (IOException e) {
        	LOGGER.error( "Error searching files: " + CONFIG_FILE, e);
        }		
	}

	/**
	 * Context destroyed.
	 * 
	 * @param sce the sce
	 */
	public void contextDestroyed(ServletContextEvent sce) {
	}
	
}
