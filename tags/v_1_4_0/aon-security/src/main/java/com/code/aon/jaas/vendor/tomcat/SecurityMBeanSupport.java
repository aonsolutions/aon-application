package com.code.aon.jaas.vendor.tomcat;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SecurityMBeanSupport extends NotificationBroadcasterSupport 
									implements SecurityMBean, MBeanRegistration {

	private static final Log LOGGER = LogFactory.getLog( SecurityMBeanSupport.class.getName() );

	/** Descriptive information about this component implementation. */
	protected static final String info =
		"com.code.aon.jaas.vendor.tomcat.SecurityMBeanSupport/1.0";

	/** Notification sequence number. */
	private long sequenceNumber = 0;

	/** The MBeanServer which we have been register with. */
	protected MBeanServer server;

	/** The object name which we are registsred under. */
	protected ObjectName serviceName;

	/** The current state this service is in. */
	private int state = UNREGISTERED;

	/** Returns the MBean server. */
	public MBeanServer getServer() {
		return this.server;
	}

	/** Returns the state. */
	public int getState() {
		return this.state;
	}

	/**
	 * Returns a String representation of the state.
	 */
	public String getStateString() {
		return states[state];
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.vendor.tomcat.SecurityMBean#getName()
	 */
	public String getName() {
		return SecurityMBeanSupport.class.getName();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.vendor.tomcat.SecurityMBean#create()
	 */
	public void create() throws Exception {
		LOGGER.debug("Creating " + serviceName);
		try {
			createService();
			this.state = CREATED;
		} catch (Exception e) {
			LOGGER.debug( "Initialization failed " + serviceName, e );
			throw e;
		}
		LOGGER.debug("Created " + serviceName);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.vendor.tomcat.SecurityMBean#start()
	 */
	public void start() throws Exception {
		if (state == STARTING || state == STARTED || state == STOPPING)
			return;
		if (state != CREATED && state != STOPPED && state != FAILED) {
			LOGGER.debug("Start requested before create, calling create now");
			create();
		}

		this.state = STARTING;
		sendStateChangeNotification(STOPPED, STARTING, getName() + " starting", null);
		LOGGER.debug("Starting " + serviceName);

		try {
			startService();
		} catch (Exception e) {
			this.state = FAILED;
			sendStateChangeNotification(STARTING, FAILED, getName() + " failed", e);
			LOGGER.debug( "Starting failed " + serviceName, e );
			throw e;
		}
		this.state = STARTED;
		sendStateChangeNotification(STARTING, STARTED, getName() + " started", null);
		LOGGER.debug("Started " + serviceName);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.vendor.tomcat.SecurityMBean#stop()
	 */
	public void stop() {
		if (state != STARTED)
			return;
	       
		state = STOPPING;
		sendStateChangeNotification(STARTED, STOPPING, getName() + " stopping", null);
		LOGGER.debug("Stopping " + serviceName);

		try {
			stopService();
		} catch (Throwable e) {
			state = FAILED;
			sendStateChangeNotification(STOPPING, FAILED, getName() + " failed", e);
			LOGGER.warn( "Stopping failed " + serviceName, e );
			return;
		}
	       
		state = STOPPED;
		sendStateChangeNotification(STOPPING, STOPPED, getName() + "stopped", null);
		LOGGER.debug("Stopped " + serviceName);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.vendor.tomcat.SecurityMBean#destroy()
	 */
	public void destroy() {
		if (this.state == DESTROYED)
			return;
	       
		if (this.state == STARTED) {
			LOGGER.debug("Destroy requested before stop, calling stop now");
			stop();
		}
		LOGGER.debug("Destroying " + serviceName);
		try {
			destroyService();
		} catch (Throwable t) {
			LOGGER.warn( "Destroying failed " + serviceName, t );
		}
		this.state = DESTROYED;
		LOGGER.debug("Destroyed " + serviceName);
	}

	///////////////////////////////////////////////////////////////////////////
	//                                JMX Hooks                              //
	///////////////////////////////////////////////////////////////////////////
	public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
		this.server = server;
		this.serviceName = getObjectName(server, name);
		return this.serviceName;
	}

	public void postRegister(Boolean done) {
		if (!done.booleanValue())
			destroy();
	}

	public void preDeregister() throws Exception {
		// TODO Auto-generated method stub

	}

	public void postDeregister() {
		destroy();
	}

	///////////////////////////////////////////////////////////////////////////
	//                       Concrete Service Overrides                      //
	///////////////////////////////////////////////////////////////////////////
	/**
	 * Sub-classes should override this method if they only need to set their
	 * object name during MBean pre-registration.
	 */
	protected ObjectName getObjectName(MBeanServer server, ObjectName name)
      			throws MalformedObjectNameException {
		return this.serviceName;
	}

	/**
	 * Sub-classes should override this method to provide
	 * custum 'create' logic.
	 *
	 * <p>This method is empty, and is provided for convenience
	 *    when concrete service classes do not need to perform
	 *    anything specific for this state change.
	 */
	protected void createService() throws Exception {}

	/**
	 * Sub-classes should override this method to provide
	 * custum 'start' logic.
	 * 
	 * <p>This method is empty, and is provided for convenience
	 *    when concrete service classes do not need to perform
	 *    anything specific for this state change.
	 */
	protected void startService() throws Exception {}
	   
	/**
	 * Sub-classes should override this method to provide
	 * custum 'stop' logic.
	 * 
	 * <p>This method is empty, and is provided for convenience
	 *    when concrete service classes do not need to perform
	 *    anything specific for this state change.
	 */
	protected void stopService() throws Exception {}

	/**
	 * Sub-classes should override this method to provide
	 * custum 'destroy' logic.
	 * 
	 * <p>This method is empty, and is provided for convenience
	 *    when concrete service classes do not need to perform
	 *    anything specific for this state change.
	 */
	protected void destroyService() throws Exception {}

	/**
	 * Helper for sending out state change notifications
	 */
	private void sendStateChangeNotification(int oldState, int newState, String msg, Throwable t) {
		long now = System.currentTimeMillis();
		AttributeChangeNotification stateChangeNotification = 
			new AttributeChangeNotification(this, sequenceNumber++, now, msg,
											"State", "java.lang.Integer",
											new Integer(oldState), new Integer(newState) );
		stateChangeNotification.setUserData(t);
		sendNotification(stateChangeNotification);      
	}
}
