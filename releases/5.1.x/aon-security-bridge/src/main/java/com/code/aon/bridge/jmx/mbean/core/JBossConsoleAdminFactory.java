package com.code.aon.bridge.jmx.mbean.core;

import com.code.aon.bridge.jmx.mbean.ConsoleAdminFactoryManager;
import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IConsoleAdminFactory;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * JBoss console administration factory.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-nov-2004
 * @since 1.0
 *  
 */
public class JBossConsoleAdminFactory implements IConsoleAdminFactory {

	/** Console Administration. */
	IConsoleAdmin console;

	/* (non-Javadoc)
	 * @see com.aon.jmx.adaptor.mbean.IConsoleAdminFactory#accept(java.lang.String)
	 */
	public boolean accept() throws DeploymentException {
		if (console == null) {
			console = new JBossConsoleAdmin();
		}
		return console.getServerInfo().equals("jboss");
	}

	/* (non-Javadoc)
	 * @see com.aon.jmx.adaptor.mbean.IConsoleAdminFactory#createConsoleAdmin()
	 */
	public IConsoleAdmin createConsoleAdmin() throws DeploymentException {
		return console;
	}

	/** Console administration factory. */
	static final IConsoleAdminFactory FACTORY = new JBossConsoleAdminFactory();

	static {
//	Registers JBoss Console Administration.
		ConsoleAdminFactoryManager.register(FACTORY);
	}

}