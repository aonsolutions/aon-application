/*
 * Created on 27-may-2004
 *
 */
package com.code.aon.jaas.deployment.event;

import com.code.aon.jaas.deployment.DeploymentException;

/**
 * This interface receives events each time an application is deployed.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 27-may-2004
 * @since 1.0
 *  
 */
public interface IDeployerListener {

	/**
     * Whenever an application is deployed, this method is invoked.
	 * 
	 * @param event
	 * @throws DeploymentException 
	 */
	void applicationDeployed(DeployerEvent event) throws DeploymentException;

	/**
     * Whenever an application is undeployed, this method is invoked.
	 * 
	 * @param event
	 * @throws DeploymentException 
	 */
	void applicationUndeployed(DeployerEvent event) throws DeploymentException;
}