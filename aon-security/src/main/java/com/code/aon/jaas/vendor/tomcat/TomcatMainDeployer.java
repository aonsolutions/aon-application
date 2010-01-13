/*
 * Created on 08-sep-2005
 *
 */
package com.code.aon.jaas.vendor.tomcat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.management.MBeanException;
import javax.management.RuntimeOperationsException;

import org.apache.catalina.util.ServerInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.jaas.deployment.DeploymentInfo;
import com.code.aon.jaas.deployment.IDeployer;

import com.code.aon.jaas.deployment.core.MainDeployer;

import com.code.aon.jaas.deployment.event.IDeployerListener;

import com.code.aon.jaas.vendor.VendorFactoryManager;

import com.code.aon.jaas.vendor.tomcat.deployment.TomcatFactory;

/**
 * Tomcat security application deployer.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 08-sep-2005
 * @since 1.0
 *
 * @jmx:mbean name="Catalina:type=Security,name=AonMainDeployer" extends="com.code.aon.jaas.vendor.tomcat.SecurityMBean"
 */

public class TomcatMainDeployer extends SecurityMBeanSupport 
		implements IDeployer, TomcatMainDeployerMBean {

	/** TomcatMainDeployer Logger instance. */
	private static final Log LOGGER = LogFactory.getLog( TomcatMainDeployer.class.getName() );

	/** Application Main deployer. */
	MainDeployer support = new MainDeployer();

	/**
	 * Constructor.
	 * @throws MBeanException
	 * @throws RuntimeOperationsException
	 */
	public TomcatMainDeployer() throws RuntimeOperationsException, MBeanException {
		super();
	}

    /**
     * Problems with Tomcat forces me to implement this wrapper around addDeployerListener base method.
     * 
 	 * @jmx:managed-operation
     */
	public void addDeployerListener(String l) {
		LOGGER.debug("addDeployerListener String:" + l);
	}

    /** (non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#addDeployerListener(com.code.aon.jaas.deployment.event.IDeployerListener)
     * 
 	 * @jmx:managed-operation
     */
	public void addDeployerListener(IDeployerListener l) {
		support.addDeployerListener(l);
	}

    /**
     * Problems with Tomcat forces me to implement this wrapper around removeDeployerListener base method.
     * 
 	 * @jmx:managed-operation
     */
	public void removeDeployerListener(String l) {
		LOGGER.debug("removeDeployerListener String:" + l);
	}

    /** (non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#removeDeployerListener(com.code.aon.jaas.deployment.event.IDeployerListener)
     * 
 	 * @jmx:managed-operation
     */
	public void removeDeployerListener(IDeployerListener l) {
		support.removeDeployerListener(l);
	}

    /**
     * Problems with Tomcat forces me to implement this wrapper around deploy base method.
     * 
 	 * @jmx:managed-operation
     */
	public void deploy(String url, String deploy) throws DeploymentException {
		LOGGER.debug( "TomcatMainDeployer deploying: String[" + url +"]" );
		try {
			deploy( new URL( "file:" + getCatalinaBase() + url ) );
		} catch (MalformedURLException e) {
			throw new DeploymentException( e.getMessage(), e );
		}
	}

    /** (non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#deploy(java.net.URL)
     * 
 	 * @jmx:managed-operation
     */
	public DeploymentInfo deploy(URL url) throws DeploymentException {
		LOGGER.debug( "TomcatMainDeployer deploying: URL[" + url + "]" );
		return support.deploy( url );
	}

    /** (non-Javadoc)
 	 * @jmx:managed-operation
     */
	public void undeploy(String name) throws DeploymentException {
		try {
			support.undeploy( new URL( "file:" + getCatalinaBase() + name ) );
		} catch (MalformedURLException e) {
			throw new DeploymentException( e.getMessage(), e );
		}
	}

    /** (non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#undeploy(java.net.URL)
     * 
 	 * @jmx:managed-operation
     */
	public DeploymentInfo undeploy(URL url) throws DeploymentException {
		return support.undeploy( url );
	}

    /** (non-Javadoc)
 	 * @jmx:managed-operation
     */
	public void isDeployed(String name) throws DeploymentException {
		try {
			support.undeploy( new URL( "file:" + getCatalinaBase() + name ) );
		} catch (MalformedURLException e) {
			throw new DeploymentException( e.getMessage(), e );
		}
	}

    /** (non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#isDeployed(java.net.URL)
     * 
 	 * @jmx:managed-operation
     */
	public boolean isDeployed(URL url) throws DeploymentException {
		return support.isDeployed( url );
	}

    /**
     * Return Catalina home.
     *  
     * @return
     * @throws DeploymentException
     * 
 	 * @jmx:managed-operation
     * @deprecated in future versions this method will change to
     *             <code>getCatalinaHome()</code>, without parameters.
     */
	public String getCatalinaHome(String home) throws DeploymentException {
		return System.getProperty( "catalina.home" );
	}
    
    /** (non-Javadoc)
     * @see com.code.aon.jaas.deployment.IDeployer#getDeployerInfo(java.lang.String)
     * 
 	 * @jmx:managed-operation
     * @deprecated
     */
	public String getDeployerInfo(String info) {
		StringBuffer props = new StringBuffer();
		props.append("OK - Server info");
		props.append("\nTomcat Version: ");
		props.append( ServerInfo.getServerInfo() );
		props.append("\nOS Name: ");
		props.append(System.getProperty("os.name"));
		props.append("\nOS Version: ");
		props.append(System.getProperty("os.version"));
		props.append("\nOS Architecture: ");
		props.append(System.getProperty("os.arch"));
		props.append("\nJVM Version: ");
		props.append(System.getProperty("java.runtime.version"));
		props.append("\nJVM Vendor: ");
		props.append(System.getProperty("java.vm.vendor"));
		return props.toString();
	}

    /**
     * Asigna el nombre del fichero de configuración.
     * 
     */
	public void setConfigResource(String resourceName) throws IOException {
		support.setConfigResource(resourceName);
	}

    /**
     * Devuelve el fichero de configuración, donde se define la seguridad de cada 
     * una de las aplicaciones desplegadas.
     * 
 	 * @jmx:managed-operation
     * @deprecated
     */
	public URL getConfigResource(String resource) throws IOException {
		return support.getConfigResource(resource);
	}

    /*(non-Javadoc)
     * @see org.jboss.system.ServiceMBeanSupport#startService()
     */
	protected void startService() throws Exception {
		generateXMLFile();
	}

    /**
     * Return Catalina base.
     *  
     * @return
     * @throws DeploymentException
     */
	protected String getCatalinaBase() throws DeploymentException {
		return getCatalinaHome("") + "/webapps/";
	}

    /**
	 * Create a XML deployer file, if it does not exist.
     * 
     * @throws IOException
     */
	private void generateXMLFile() throws IOException {
		File file = new File( getConfigResource( null ).getFile() );
		if ( !file.getParentFile().exists() ) {
			file.getParentFile().mkdirs();
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writer.write("<?xml version='1.0' encoding='ISO-8859-1' ?>\n");
			writer.write("<applications>\n");
			writer.write("<options>\n");
//            writer.write("<option name=\"" + IConstants.UNAUTHENTICATED_IDENTITY + "\" value=\"anonymous\"></option>\n");
//            writer.write("<option name=\"" + IConstants.DEPLOYER_OBJECT_NAME + "\" value=\"Catalina:type=Security,name=AonSecurity\"></option>\n");
//            writer.write("<option name=\"" + IConstants.SESSION_MANAGER_OBJECT_NAME + "\" value=\"Catalina:type=Security,name=AonSessionManager\"></option>\n");
			writer.write("<option name=\"" + IConstants.ALGORITHM + "\" value=\"SHA-256\"></option>\n");
			writer.write("<option name=\"" + IConstants.ENCODING + "\" value=\"base64\"></option>\n");
			writer.write("</options>\n");
			writer.write("</applications>\n");
			writer.flush();
			writer.close();
		}
	}

	static {
		VendorFactoryManager.register(new TomcatFactory());
	}

}
