/*
 * Generated file - Do not edit!
 */
package com.code.aon.jaas.vendor.jboss;

/**
 * MBean interface.
 * @since 1.0
 */
public interface JBossMainDeployerMBean extends org.jboss.system.ServiceMBean {

   //default object name
   public static final java.lang.String OBJECT_NAME = "jboss.admin:service=AonMainDeployer";

   /**
    * Add an <code>IDeployerListener</code>.
    */
  void addDeployerListener(com.code.aon.jaas.deployment.event.IDeployerListener l) ;

   /**
    * Remove the <code>IDeployerListener</code>.
    */
  void removeDeployerListener(com.code.aon.jaas.deployment.event.IDeployerListener l) ;

   /**
    * Deploy application.
    */
  com.code.aon.jaas.deployment.DeploymentInfo deploy(java.net.URL url) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * Undeploy application.
    */
  com.code.aon.jaas.deployment.DeploymentInfo undeploy(java.net.URL url) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * Check if the application is already deployed.
    */
  boolean isDeployed(java.net.URL url) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * Returns the default domain used for naming the MBean.
    */
  java.lang.String getDeployerInfo(java.lang.String info) ;

   /**
    * Returns the security configuration URL, where deployed applications are defined.
    */
  java.net.URL getConfigResource(java.lang.String resource) throws java.io.IOException;

   /**
    * Set security configuration file name.
    */
  void setConfigResource(java.lang.String resourceName) throws java.io.IOException;

   /**
    * Add a new Login Module.
    */
  void addLoginModule(java.lang.String name) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * Remove a Lgin Module.
    */
  void removeLoginModule(java.lang.String name) throws com.code.aon.jaas.deployment.DeploymentException;

}
