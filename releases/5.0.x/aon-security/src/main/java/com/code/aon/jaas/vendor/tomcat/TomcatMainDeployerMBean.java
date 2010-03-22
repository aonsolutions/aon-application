/*
 * Generated file - Do not edit!
 */
package com.code.aon.jaas.vendor.tomcat;

/**
 * MBean interface.
 * @since 1.0
 */
public interface TomcatMainDeployerMBean extends com.code.aon.jaas.vendor.tomcat.SecurityMBean {

   //default object name
   public static final java.lang.String OBJECT_NAME = "Catalina:type=Security,name=AonMainDeployer";

   /**
    * Problems with Tomcat forces me to implement this wrapper around addDeployerListener base method.
    */
  void addDeployerListener(java.lang.String l) ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.deployment.IDeployer#addDeployerListener(com.code.aon.jaas.deployment.event.IDeployerListener)
    */
  void addDeployerListener(com.code.aon.jaas.deployment.event.IDeployerListener l) ;

   /**
    * Problems with Tomcat forces me to implement this wrapper around removeDeployerListener base method.
    */
  void removeDeployerListener(java.lang.String l) ;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.deployment.IDeployer#removeDeployerListener(com.code.aon.jaas.deployment.event.IDeployerListener)
    */
  void removeDeployerListener(com.code.aon.jaas.deployment.event.IDeployerListener l) ;

   /**
    * Problems with Tomcat forces me to implement this wrapper around deploy base method.
    */
  void deploy(java.lang.String url,java.lang.String deploy) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.deployment.IDeployer#deploy(java.net.URL)
    */
  com.code.aon.jaas.deployment.DeploymentInfo deploy(java.net.URL url) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * (non-Javadoc)
    */
  void undeploy(java.lang.String name) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.deployment.IDeployer#undeploy(java.net.URL)
    */
  com.code.aon.jaas.deployment.DeploymentInfo undeploy(java.net.URL url) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * (non-Javadoc)
    */
  void isDeployed(java.lang.String name) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.deployment.IDeployer#isDeployed(java.net.URL)
    */
  boolean isDeployed(java.net.URL url) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * Return Catalina home.
    * @return 
    * @throws DeploymentException
    * @deprecated in future versions this method will change to <code>getCatalinaHome()</code>, without parameters.    */
  java.lang.String getCatalinaHome(java.lang.String home) throws com.code.aon.jaas.deployment.DeploymentException;

   /**
    * (non-Javadoc)
    * @see com.code.aon.jaas.deployment.IDeployer#getDeployerInfo(java.lang.String)
    * @deprecated     */
  java.lang.String getDeployerInfo(java.lang.String info) ;

   /**
    * Devuelve el fichero de configuración, donde se define la seguridad de cada una de las aplicaciones desplegadas.
    * @deprecated     */
  java.net.URL getConfigResource(java.lang.String resource) throws java.io.IOException;

}
