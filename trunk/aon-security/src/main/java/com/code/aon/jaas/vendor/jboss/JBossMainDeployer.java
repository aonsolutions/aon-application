package com.code.aon.jaas.vendor.jboss;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.net.URL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.ObjectName;

import javax.security.auth.login.AppConfigurationEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.system.ServiceMBeanSupport;

import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.client.ast.IOption;
import com.code.aon.jaas.client.ast.core.AstLoader;
import com.code.aon.jaas.client.xml.ApplicationsRenderer;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.jaas.deployment.DeploymentInfo;
import com.code.aon.jaas.deployment.IDeployer;
import com.code.aon.jaas.deployment.ast.AstException;
import com.code.aon.jaas.deployment.core.MainDeployer;
import com.code.aon.jaas.deployment.event.IDeployerListener;
import com.code.aon.jaas.storage.ApplicationsStorage;
import com.code.aon.jaas.storage.StorageManager;
import com.code.aon.jaas.vendor.VendorFactoryManager;
import com.code.aon.jaas.vendor.jboss.deployment.JBossFactory;

/**
 * JBoss security application deployer.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 11-nov-2004
 * @since 1.0
 *  
 * @jmx:mbean name="jboss.admin:service=AonMainDeployer" extends="org.jboss.system.ServiceMBean"
 */
public class JBossMainDeployer extends ServiceMBeanSupport implements
        IDeployer, JBossMainDeployerMBean {

    /** JBossMainDeployer proper Logger. */
    private static final Log LOGGER = LogFactory.getLog( JBossMainDeployer.class.getName() );

    /** XMLLoginConfig ObjectName. */
	public static final ObjectName LOGIN_OBJECT_NAME = org.jboss.mx.util.ObjectNameFactory.create("jboss.security:service=XMLLoginConfig");

	/** Application Main deployer. */
	MainDeployer support = new MainDeployer();

	/**
	 * Constructor.
	 */
	public JBossMainDeployer() {
	}

	/**
	 * Add an <code>IDeployerListener</code>.
	 * 
	 * @jmx:managed-operation
	 */
	public void addDeployerListener(IDeployerListener l) {
		support.addDeployerListener(l);
	}

	/**
	 * Remove the <code>IDeployerListener</code>.
	 * 
	 * @jmx:managed-operation
	 */
	public void removeDeployerListener(IDeployerListener l) {
		support.removeDeployerListener(l);
	}

	/**
	 * Deploy application.
	 * 
	 * @jmx:managed-operation
	 */
	public DeploymentInfo deploy(URL url) throws DeploymentException {
		LOGGER.info( "deploy: " + url.getPath() );
		File file = new File( url.getPath() );
		if ( !file.canRead() ) {
			try {
				ObjectName oname = new ObjectName( "jboss.system:type=ServerConfig" );
				URL serverHomeURL = (URL) getServer().getAttribute( oname, "ServerHomeURL" );
				url = new File( serverHomeURL.getPath() + "farm/" + file.getName() ).toURL();
			} catch (Exception e) {
				LOGGER.fatal( "Error searching file: " + e.getMessage() );
				throw new DeploymentException( e.getMessage(), e );
			}
		}
		DeploymentInfo di = support.deploy( url );
		addLoginModule( di.securityDomain );
		return di;
	}

	/**
	 * Undeploy application.
	 * 
	 * @jmx:managed-operation
	 */
	public DeploymentInfo undeploy(URL url) throws DeploymentException {
		DeploymentInfo di = support.undeploy( url );
		removeLoginModule( di.securityDomain );
		return di;
	}

	/**
	 * Check if the application is already deployed.
	 * 
	 * @jmx:managed-operation
	 */
	public boolean isDeployed(URL url) throws DeploymentException {
		return support.isDeployed( url );
	}

//	/**
//	 * Returns application deployment information.
//	 * 
//	 * @jmx:managed-operation
//	 */
//	public DeploymentInfo getDeployment(URL url) {
//		return support.getDeployment( url );
//	}

	/**
     * Returns the default domain used for naming the MBean.
	 * 
	 * @jmx:managed-operation
	 */
	public String getDeployerInfo(String info) {
		return getServer().getDefaultDomain();
	}

	/**
	 * Returns the security configuration URL, where deployed applications are defined.
	 * 
	 * @jmx:managed-operation
	 */
	public URL getConfigResource(String resource) throws IOException {
		return support.getConfigResource( resource );
	}

	/**
	 * Set security configuration file name.
	 * 
	 * @jmx:managed-operation
	 */
	public void setConfigResource(String resourceName) throws IOException {
		support.setConfigResource( resourceName );
	}

	/**
	 * Add a new Login Module.
	 * 
	 * @jmx:managed-operation
	 */
	public void addLoginModule(String name) throws DeploymentException {
		try {
			Object[] params = { name, createLoginModule(name) };
			String[] sig = { String.class.getName(), AppConfigurationEntry[].class.getName() };
			getServer().invoke(LOGIN_OBJECT_NAME, "addAppConfig", params, sig);
		} catch (Exception e) {
			throw new DeploymentException( e.getMessage() );
		}
	}

	/**
	 * Remove a Lgin Module.
	 * 
	 * @jmx:managed-operation
	 */
	public void removeLoginModule(String name) throws DeploymentException {
		try {
			Object[] params = { new String[] { name } };
			String[] sig = { String[].class.getName() };
			getServer().invoke(LOGIN_OBJECT_NAME, "removeConfigs", params, sig);
		} catch (Exception e) {
			throw new DeploymentException( e.getMessage() );
		}
	}

	/*(non-Javadoc)
	 * @see org.jboss.system.ServiceMBeanSupport#startService()
	 */
	protected void startService() throws Exception {
		generateXMLFile();
		super.startService();
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
			writer.write("<option name=\"" + IConstants.UNAUTHENTICATED_IDENTITY + "\" value=\"anonymous\"></option>\n");
			writer.write("<option name=\"" + IConstants.DEPLOYER_OBJECT_NAME + "\" value=\"jboss.admin:service=AonSecurity\"></option>\n");
			writer.write("<option name=\"" + IConstants.SESSION_MANAGER_OBJECT_NAME + "\" value=\"jboss.admin:service=AonSessionManager\"></option>\n");
			writer.write("<option name=\"" + IConstants.ALGORITHM + "\" value=\"SHA-256\"></option>\n");
			writer.write("<option name=\"" + IConstants.ENCODING + "\" value=\"base64\"></option>\n");
			writer.write("</options>\n");
			writer.write("</applications>\n");
			writer.flush();
			writer.close();
		}
	}

	/**
	 * Create a Login Module with several defined options.
	 * 
	 * @param name
	 * @return
	 */
	private AppConfigurationEntry[] createLoginModule(String name) {
		AppConfigurationEntry[] entries = new AppConfigurationEntry[1];
		String code = JBossLoginModule.class.getName();
		AppConfigurationEntry.LoginModuleControlFlag flag = AppConfigurationEntry.LoginModuleControlFlag.REQUIRED;
		Map options = new HashMap();
		options.put( IConstants.SECURITY_DOMAIN, name );
		try {
			URL url = 
				StorageManager.getInstance( getConfigResource( null ), ApplicationsRenderer.getInstance() ).read();
			ApplicationsStorage	storage = 
				(ApplicationsStorage) AstLoader.getInstance().parse( 2, url.openStream() );
			Iterator iter = storage.options().values().iterator();
			while (iter.hasNext()) {
				IOption element = (IOption) iter.next();
				options.put( element.getName(), element.getValue() );
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (AstException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
		entries[0] = new AppConfigurationEntry(code, flag, options);
		return entries;
	}

	static {
		VendorFactoryManager.register(new JBossFactory());
	}

}