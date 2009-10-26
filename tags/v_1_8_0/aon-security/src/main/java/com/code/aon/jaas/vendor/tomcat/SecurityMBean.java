package com.code.aon.jaas.vendor.tomcat;

/**
 * MBean interface.
 * @since 1.0
 */
public interface SecurityMBean {

   // Constants -----------------------------------------------------

   /** ServiceController notification types corresponding to service lifecycle events */
   public static final String CREATE_EVENT  = "com.code.aon.jaas.vendor.tomcat.SecurityMBean.create";
   public static final String START_EVENT   = "com.code.aon.jaas.vendor.tomcat.SecurityMBean.start";
   public static final String STOP_EVENT    = "com.code.aon.jaas.vendor.tomcat.SecurityMBean.stop";
   public static final String DESTROY_EVENT = "com.code.aon.jaas.vendor.tomcat.SecurityMBean.destroy";

   public static final String[] states = {
      "Stopped", "Stopping", "Starting", "Started", "Failed",
      "Destroyed", "Created", "Unregistered", "Registered"
   };

   /** The Service.stop has completed */
   public static final int STOPPED  = 0;
   /** The Service.stop has been invoked */
   public static final int STOPPING = 1;
   /** The Service.start has been invoked */
   public static final int STARTING = 2;
   /** The Service.start has completed */
   public static final int STARTED  = 3;
   /** There has been an error during some operation */
   public static final int FAILED  = 4;
   /** The Service.destroy has completed */
   public static final int DESTROYED = 5;
   /** The Service.create has completed */
   public static final int CREATED = 6;
   /** The MBean has been created but has not completed MBeanRegistration.postRegister */
   public static final int UNREGISTERED = 7;
   /** The MBean has been created and has completed MBeanRegistration.postRegister */
   public static final int REGISTERED = 8;

   // Public --------------------------------------------------------
   
   String getName();
   int getState();
   String getStateString();
   
   /**
    * create the service, do expensive operations etc 
    */
   void create() throws Exception;
   
   /**
    * start the service, create is already called
    */
   void start() throws Exception;
   
   /**
    * stop the service
    */
   void stop();
   
   /**
    * destroy the service, tear down 
    */
   void destroy();
}
