/*
 * Generated file - Do not edit!
 */
package com.code.aon.jaas.vendor.jboss;

/**
 * MBean interface.
 * @since 1.0
 */
public interface JBossSecurityMBean extends org.jboss.system.ServiceMBean {

   //default object name
   public static final java.lang.String OBJECT_NAME = "jboss.admin:service=AonSecurity";

   /**
    * Problems with Tomcat forces me to implement this wrapper around applications base method. Tomcat does not allow method calls without parameters.
    */
  java.util.Collection applications(java.lang.String apps) ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#applications()
    */
  java.util.Collection applications() ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#getApplication(java.lang.String)
    */
  com.code.aon.jaas.client.ast.IApplication getApplication(java.lang.String name) ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#getApplication4Ctx(java.lang.String)
    */
  com.code.aon.jaas.client.ast.IApplication getApplication4Ctx(java.lang.String ctx) ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#getSDApplications(java.lang.String)
    */
  java.util.Collection getSDApplications(java.lang.String securityDomain) ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#getUserApplications(java.lang.String, java.lang.String)
    */
  java.util.List getUserApplications(java.lang.String domainId,java.lang.String userId) ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#getDomainNames2Import(java.lang.String)
    */
  java.util.List getDomainNames2Import(java.lang.String appId) ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#getDSMDProperties(java.security.Principal)
    */
  java.util.Properties getDSMDProperties(java.security.Principal principal) ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#addDomain(java.lang.String, com.code.aon.jaas.client.ast.IDomain, Boolean)
    */
  void addDomain(java.lang.String appId,com.code.aon.jaas.client.ast.IDomain domain,java.lang.Boolean flag) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#loadDomain(com.code.aon.jaas.client.ast.IDomain)
    */
  void loadDomain(com.code.aon.jaas.client.ast.IDomain domain) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#getDomain(java.lang.String, java.lang.String)
    */
  com.code.aon.jaas.client.ast.IDomain getDomain(java.lang.String appContext,java.lang.String domainId) ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#loadUsers(com.code.aon.jaas.client.ast.IDomain)
    */
  java.util.List loadUsers(com.code.aon.jaas.client.ast.IDomain domain) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#addUser(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IUser, java.lang.String)
    */
  void addUser(java.lang.String appId,java.lang.String domainId,com.code.aon.jaas.client.ast.IUser user,java.lang.String oldUserId) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#getUser(java.security.Principal)
    */
  com.code.aon.jaas.client.ast.IUser getUser(java.security.Principal principal) ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#getUser(java.lang.String, java.lang.String, java.lang.String)
    */
  com.code.aon.jaas.client.ast.IUser getUser(java.lang.String appContext,java.lang.String domainId,java.lang.String userId) ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#initApplicationDeployed(java.lang.String, java.lang.String, java.lang.Boolean, java.lang.String, com.code.aon.jaas.client.ast.IAccessPolicy, com.code.aon.jaas.client.ast.IDataSourceMetaData)
    */
  void initApplicationDeployed(java.lang.String appId,java.lang.String domainId,java.lang.Boolean privileged,java.lang.String contextExtraInfo,com.code.aon.jaas.client.ast.IAccessPolicy accessPolicy,com.code.aon.jaas.client.ast.IDataSourceMetaData metadata) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#removeDomain(java.lang.String, com.code.aon.jaas.client.ast.IDomain)
    */
  com.code.aon.jaas.client.ast.IDomain removeDomain(java.lang.String appId,com.code.aon.jaas.client.ast.IDomain domain) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#removeProfile(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
    */
  com.code.aon.jaas.client.ast.IRelation removeProfile(java.lang.String appId,java.lang.String domainId,com.code.aon.jaas.client.ast.IRelation relation) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#removeRelation(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
    */
  com.code.aon.jaas.client.ast.IRelation removeRelation(java.lang.String appId,java.lang.String domainId,com.code.aon.jaas.client.ast.IRelation relation) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#removeUser(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IUser)
    */
  com.code.aon.jaas.client.ast.IUser removeUser(java.lang.String appId,java.lang.String domainId,com.code.aon.jaas.client.ast.IUser user) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#updateAccessPolicy(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IAccessPolicy)
    */
  void updateAccessPolicy(java.lang.String appId,java.lang.String domainId,com.code.aon.jaas.client.ast.IAccessPolicy accessPolicy) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#updateDSMD(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IDataSourceMetaData)
    */
  void updateDSMD(java.lang.String appId,java.lang.String domainId,com.code.aon.jaas.client.ast.IDataSourceMetaData metadata) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#updateDomain(java.lang.String, com.code.aon.jaas.client.ast.IDomain)
    */
  void updateDomain(java.lang.String appId,com.code.aon.jaas.client.ast.IDomain domain) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#updateProfile(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
    */
  com.code.aon.jaas.client.ast.IRelation updateProfile(java.lang.String appId,java.lang.String domainId,com.code.aon.jaas.client.ast.IRelation relation) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#updateRelation(java.lang.String, java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IRelation)
    */
  com.code.aon.jaas.client.ast.IRelation updateRelation(java.lang.String appId,java.lang.String domainId,com.code.aon.jaas.client.ast.IRelation relation) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#updateUser(java.lang.String, java.lang.String, com.code.aon.jaas.client.ast.IUser, java.lang.String)
    */
  com.code.aon.jaas.client.ast.IUser updateUser(java.lang.String appId,java.lang.String domainId,com.code.aon.jaas.client.ast.IUser user,java.lang.String oldUserId) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#read(com.code.aon.jaas.client.ast.IDomain)
    */
  java.net.URL read(com.code.aon.jaas.client.ast.IDomain domain) throws com.code.aon.jaas.storage.StorageException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.storage.IOperation#write(com.code.aon.jaas.client.ast.IDomain)
    */
  void write(com.code.aon.jaas.client.ast.IDomain domain) throws com.code.aon.jaas.storage.StorageException;

}
