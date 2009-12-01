/*
 * Generated file - Do not edit!
 */
package com.code.aon.jaas.vendor.jboss;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.code.aon.jaas.client.ast.IApplication;
import com.code.aon.jaas.client.ast.IDomain;
import com.code.aon.jaas.client.ast.IOption;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.client.ast.IUser;
import com.code.aon.jaas.ldap.SecurityLdap;
import com.code.aon.jaas.storage.StorageException;

/**
 * MBean interface.
 */
public interface JBossLdapMBean extends org.jboss.system.ServiceMBean {

	//default object name
	String OBJECT_NAME = "jboss.admin:service=AonLdap";

	Map<String, IOption> getOptions();
	
	SecurityLdap getSecurityLdap();
	
	Properties getDSMDProperties(Principal principal);
	
	Properties getLdapProperties();
	
	IApplication getApplication4Ctx(String ctx);	
	
	IDomain getDomain(String appContext, String domainId);
	
	List getUserApplications(String domainId, String userId);
	
	IApplication getApplication(String name);

	IRelation removeProfile(String appId, String domainId, IRelation relation) throws StorageException;

	IRelation updateProfile(String appId, String domainId, IRelation relation) throws StorageException;
	
	IRelation updateRelation(String appId, String domainId, IRelation relation) throws StorageException;
	
	IRelation removeRelation(String appId, String domainId, IRelation relation) throws StorageException;
	
	IUser updateUser(String appId, String domainId, IUser user, String oldUserId) throws StorageException;
	
}
