/**
 *
 * This source-code is licensed under the LGPL.
 * You may copy, adapt, and redistribute this file for commercial or non-commercial use.
 * When copying, adapting, or redistributing this document in keeping with the guidelines above,
 * you are required to provide proper attribution to co&de.
 * If you reproduce or distribute the document without making any substantive modifications to its content,
 * please use the following attribution line:
 *
 * Consulting and Development S.A. (http://www.co-de.com) All rights reserved.
 *
 */
package com.code.aon.bridge.plugin;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.core.JBossConsoleAdminFactory;
import com.code.aon.bridge.jndi.IJNDIConstants;
import com.code.aon.bridge.jndi.SecurityLocator;
import com.code.aon.bridge.jndi.SecurityLocatorException;
import com.code.aon.jaas.auth.AonGenericPrincipal;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * Utilities class.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 02/11/2007
 */
public class Utils {

	/**
	 * Gets the Security console.
	 * 
	 * @return
	 * @throws DeploymentException
	 */
	public static final IConsoleAdmin getSecurityConsole() throws DeploymentException {
		IConsoleAdmin console = null;
		try {
			console = SecurityLocator.getInstance().getConsole( IJNDIConstants.CONSOLE_FACTORY_CLASS );
		} catch (SecurityLocatorException e) {
			// TODO Search console using JNDI.
			JBossConsoleAdminFactory FACTORY = new JBossConsoleAdminFactory();
			if ( FACTORY.accept() )
				console = FACTORY.createConsoleAdmin();
    	}
		return console;
	}

	/**
	 * Return <code>AonGenericPrincipal</code> in the application server AonSessionManager MBean.
	 * 
	 * @param sessionId
	 * 
	 * @return
	 * @throws DeploymentException 
	 */
	public static final AonGenericPrincipal getSSOPrincipal(String sessionId) throws DeploymentException {
		IConsoleAdmin console = Utils.getSecurityConsole();
		Object[] params = { sessionId };
		String[] sig = { String.class.getName() };
		return (AonGenericPrincipal) console.invoke( console.getAonSessionManagerName(), "getSSOPrincipal", params, sig);
	}

}
