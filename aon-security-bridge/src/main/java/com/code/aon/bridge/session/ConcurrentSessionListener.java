/**
 * 
 */
package com.code.aon.bridge.session;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.jmx.mbean.ConsoleAdminFactoryManager;
import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 15/05/2007
 *
 */
public class ConcurrentSessionListener implements HttpSessionListener {

    /** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(ConcurrentSessionListener.class);

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent se) {
		LOGGER.debug( "Session Created: {}", se.getSession().getId() );
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent se) {
        Object[] params = { se.getSession().getId() };
        String[] sig = {String.class.getName()};
		try {
			IConsoleAdmin console = ConsoleAdminFactoryManager.createConsoleAdmin();
			String oname = console.getAonSessionManagerName();
			console.invoke( oname, IOperation.REMOVE_SESSION, params, sig );
			LOGGER.debug( "Session Destroyed: {}", se.getSession().getId() );
		} catch (DeploymentException e) {
			LOGGER.error( e.getMessage(), e );
		}
	}

}
