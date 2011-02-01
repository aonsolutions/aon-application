package com.code.aon.jaas.deployment;

import java.io.IOException;
import java.net.URL;

import com.code.aon.jaas.deployment.event.IDeployerListener;

/**
 * Security application deployer interfaz.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 13-may-2004
 * @since 1.0
 *  
 */

public interface IDeployer {

    /** Resource name. */
	static final String RESOURCE_NAME = "deployed.xml";
    /** Default Config Resource name. */
	static final String DEFAULT_CONFIG_RESOURCE_NAME = "aon.workspace/" + RESOURCE_NAME;

	/**
	 * Add an <code>IDeployerListener</code>.
	 * 
	 * @param l
	 */
	void addDeployerListener(IDeployerListener l);

	/**
	 * Remove a DeployerListener from the listener list.
	 * 
	 * @param l
	 */
	void removeDeployerListener(IDeployerListener l);

	/**
	 * Tell you if a packaged identified by a URL is deployed.
	 * 
	 * @param url
	 * @return
	 * @throws DeploymentException
	 */
	boolean isDeployed(URL url) throws DeploymentException;

	/**
	 * Deploy a package identified by a URL
	 * 
	 * @param url
	 * @throws DeploymentException
	 */
	DeploymentInfo deploy(URL url) throws DeploymentException;

	/**
	 * Undeploy a package identified by a URL
	 * 
	 * @param url
	 * @throws DeploymentException
	 */
	DeploymentInfo undeploy(URL url) throws DeploymentException;

	/**
	 * Returns the application server or servlets container name/version, 
	 * where this application is running.
	 * 
	 * @param info
	 * 
	 * @return
	 * @deprecated in future versions this method will change to
	 *             <code>getDeployerInfo()</code>, without parameters. This
	 *             method is used within jboss-3.2.x versions, in jboss-4.0.0
	 *             and greater versions the parameter will not be necesary.
	 */
	String getDeployerInfo(String info);

	/**
	 * Returns the security-config.xml URL
	 * 
	 * @param resource
	 * 
	 * @return URL
	 * @throws IOException
	 * @deprecated in future versions this method will change to
	 *             <code>getConfigResource()</code>, without parameters. This
	 *             method is used within jboss-3.2.x versions, in jboss-4.0.0
	 *             and greater versions the parameter will not be necesary.
	 */
	URL getConfigResource(String resource) throws IOException;

}