/*
 * Created on 08-sep-2005
 *
 */
package com.code.aon.jaas.vendor.tomcat;

import java.io.File;
import java.util.Properties;

import org.apache.catalina.util.ServerInfo;

import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.auth.spi.db.Util;

/**
 * Tomcat security application deployer.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 08-sep-2005
 * @since 1.0
 * 
 * @jmx:mbean name="Catalina:type=Security,name=AonMainDeployer"
 *            extends="com.code.aon.jaas.vendor.tomcat.SecurityMBean"
 */

public class TomcatMainDeployer implements TomcatMainDeployerMBean {

	private Properties properties;

	private Properties connectionProperties;

	public TomcatMainDeployer() {
		init();
	}

	public File getConfigFile() {
		return new File( System.getProperty( "catalina.home" ), IConstants.DEFAULT_CONFIG_RESOURCE_NAME );
	}

	private void init() {
		ConfigurationParser cp = new ConfigurationParser();
		this.properties = cp.parse(getConfigFile());
		this.connectionProperties = new Properties();
		if (properties.containsKey(Util.USER)) {
			this.connectionProperties.put(Util.USER, properties.get(Util.USER));
		}
		if (properties.containsKey(Util.PASSWORD)) {
			this.connectionProperties.put(Util.PASSWORD,
					properties.get(Util.PASSWORD));
		}
		if (properties.containsKey(Util.URL)) {
			this.connectionProperties.put(Util.URL, properties.get(Util.URL));
		}
		if (properties.containsKey(Util.DRIVER_CLASS)) {
			this.connectionProperties.put(Util.DRIVER_CLASS,
					properties.get(Util.DRIVER_CLASS));
		}
	}

	@Override
	public Properties getConnectionProperties(String domainName, String application) {
		Util util = new Util(connectionProperties);
		return util.getConnectionProperties(domainName);
	}

	public Properties getConnectionProperties() {
		return this.connectionProperties;
	}
	
	@Override
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

}