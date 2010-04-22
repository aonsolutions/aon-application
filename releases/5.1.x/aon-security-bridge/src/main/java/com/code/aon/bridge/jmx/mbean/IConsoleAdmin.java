package com.code.aon.bridge.jmx.mbean;

import java.net.URL;
import java.security.Principal;
import java.util.List;

import javax.security.auth.Subject;

import com.code.aon.jaas.deployment.DeploymentException;

/**
 * This interface connects to application server MBeans. 
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 16-nov-2004
 */
public interface IConsoleAdmin {

	/** Indicate an empty string */
	String EMPTY_STRING = "";

	/**
	 * Return AonMainDeployer name.
	 * 
	 * @return
	 */
	String getAonMainDeployerName();

	/**
	 * Return AonSecurity name.
	 * 
	 * @return
	 */
	String getAonSecurityName();

	/**
	 * Return AonSessionManager name.
	 * 
	 * @return
	 */
	String getAonSessionManagerName();

	/**
     * Return the list of Application Server or Servlet Container deployed applications. 
     * 
     * @return String
     * @throws DeploymentException
     */
	String listDeployedAsString() throws DeploymentException;

	/**
	 * Check if the applications is deployed.
	 * 
	 * @param name String
	 * 
	 * @return boolean
	 * @throws DeploymentException
	 */
	boolean isDeployed(String name) throws DeploymentException;

	/**
	 * Return Application Server home directory.
	 * 
	 * @return URL
	 * @throws DeploymentException
	 */
	URL getServerHomeURL() throws DeploymentException;

	/**
	 * Flush authenticated principal.
	 * 
	 * @param domain
	 * @param principal
	 * @throws DeploymentException
	 */
	void flushAuthenticationCache(String domain, Principal principal) throws DeploymentException;

	/**
	 * Get the currently authenticated Subject.
	 * 
	 * @throws DeploymentException
	 */
	Subject getActiveSubject() throws DeploymentException;

	/**
	 * Return Deployer home directory where applications are deployed.
	 * 
	 * @return String
	 * @throws DeploymentException
	 */
	String getDeployerHome() throws DeploymentException;

	/**
	 * Return Application Server or Servlet Container features.
	 * 
	 * @return String
	 * @throws DeploymentException
	 */
	String getServerInfo() throws DeploymentException;

	 /**
	 * Deploy a new application.
	 * 
	 * @param name String
	 * 
	 * @throws DeploymentException
	 */
	void deploy(String name) throws DeploymentException;

	/**
	 * Invoke a MBean method.
	 * 
	 * @param oname String
	 * @param method String
	 * @param params Object[]
	 * @param sig String[]
	 * 
	 * @return Object
	 * @throws DeploymentException
	 */
	Object invoke(String oname, String method, Object[] params, String[] sig) throws DeploymentException;

	/**
	 * Return the list of <code>IMBeanInfo</code> depending on the <code>MBeanFilter</code> 
	 * that can be deployed.
	 * 
	 * @param filter MBeanFilter
	 * @return List
	 */
	List<IMBeanInfo> getMBeans(MBeanFilter filter);

	/**
	 * Load the list of <code>IMBeanInfo</code> that are deployed by the Application Server or 
	 * Servlet Container that are not deployed by aon-security project.
	 * 
	 * @throws DeploymentException
	 */
	void load() throws DeploymentException;

}