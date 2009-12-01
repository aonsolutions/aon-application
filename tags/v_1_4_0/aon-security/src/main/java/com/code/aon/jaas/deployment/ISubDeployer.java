package com.code.aon.jaas.deployment;

import com.code.aon.jaas.deployment.event.ISubDeployerListener;

/**
 * This interface initializes and starts the application deployment.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 18-may-2004
 * @since 1.0
 *  
 */
public interface ISubDeployer {

	/**
	 * Add a ISubDeployerListener to the listener list.
	 *
	 * @param l  The ISubDeployerListener to be added
	 */
	void addSubDeployerListener(ISubDeployerListener l);

	/**
	 * Remove a ISubDeployerListener from the listener list.
	 *
	 * @param l The ISubDeployerListener to be removed
	 */
	void removeSubDeployerListener(ISubDeployerListener l);

	/**
	 * Initialize <code>ISubDeployer</code>
	 * 
	 * @param di
	 * @throws DeploymentException
	 */
	void init(DeploymentInfo di) throws DeploymentException;

	/**
	 * Start <code>ISubDeployer</code> deployment.
	 * 
	 * @param di
	 * @throws DeploymentException
	 */
	void start(DeploymentInfo di) throws DeploymentException;

}