/*
 * Generated file - Do not edit!
 */
package com.code.aon.jaas.vendor.jboss;

import java.security.Principal;
import java.util.Map;
import java.util.Properties;

import com.code.aon.jaas.client.ast.IOption;

/**
 * MBean interface.
 */
public interface JBossLdapMBean extends org.jboss.system.ServiceMBean {

	//default object name
	String OBJECT_NAME = "jboss.admin:service=AonLdap";

	Map<String, IOption> getOptions();
	
	Properties getLdapProperties();
	
	Properties getDSMDProperties(Principal principal);
	
}
