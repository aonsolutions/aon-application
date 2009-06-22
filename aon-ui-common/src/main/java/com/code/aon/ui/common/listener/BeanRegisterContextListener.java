/*
 * Created on 28-jun-2005
 *
 */
package com.code.aon.ui.common.listener;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.bean.BeanConfigParser;

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
	private static Logger LOGGER = Logger.getLogger(BeanRegisterContextListener.class.getName());
	
	/** The Constant CONFIG_FILE_PARAM. */
	private static final String CONFIG_FILE_PARAM = "config-file";

	/**
	 * Parses the config file.
	 * 
	 * @param sce the ServletContextEvent
	 */
	public void contextInitialized(ServletContextEvent sce) {
		try {
			ServletContext ctx = sce.getServletContext();
			String configFile = ctx.getInitParameter(CONFIG_FILE_PARAM);
			InputStream is = ctx.getResourceAsStream(configFile);
			
			BeanConfigParser parser = BeanConfigParser.getInstance();
			parser.parse( is );
			
		} catch (ManagerBeanException e) {
			LOGGER.severe( e.getMessage() );
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
