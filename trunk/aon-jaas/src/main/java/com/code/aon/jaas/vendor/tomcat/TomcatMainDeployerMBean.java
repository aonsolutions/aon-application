/*
 * Generated file - Do not edit!
 */
package com.code.aon.jaas.vendor.tomcat;

import java.util.Properties;

/**
 * MBean interface.
 * 
 * @since 1.0
 */
public interface TomcatMainDeployerMBean {

	/**
	 * Gets the Data Source connection properties.
	 * 
	 * @param domainName
	 * @param application
	 * @return the DSMD properties
	 */
	Properties getConnectionProperties(String domainName, String application);

	String getDeployerInfo(String info);

}
