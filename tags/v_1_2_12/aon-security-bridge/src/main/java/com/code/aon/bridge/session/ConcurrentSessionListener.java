/**
 * 
 */
package com.code.aon.bridge.session;

import java.io.File;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.bridge.jmx.mbean.ConsoleAdminFactoryManager;
import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.jaas.valves.BackDoorAuthenticationValve;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 15/05/2007
 *
 */
public class ConcurrentSessionListener implements HttpSessionListener {

    /** Obtiene un logger apropiado. */
	protected static final Log LOGGER = LogFactory.getLog( ConcurrentSessionListener.class.getName() );

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent se) {
		File file = new File ( BackDoorAuthenticationValve.RESOURCES_DEFAULT_DIR );
		if ( !file.exists() ) {
			file.mkdirs();
		}
		LOGGER.debug( "Session Created: " + se.getSession().getId() );
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
			BackDoorAuthenticationValve.remove( se.getSession().getId() );
			LOGGER.debug( "Session Destroyed: " + se.getSession().getId() );
		} catch (DeploymentException e) {
			LOGGER.fatal( e );
		}
	}

}
