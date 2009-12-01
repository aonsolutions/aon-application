/**
 * 
 */
package com.code.aon.jaas.vendor.jboss;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.security.auth.login.LoginException;

import org.apache.catalina.Context;
import org.apache.catalina.Session;
import org.apache.catalina.core.StandardHost;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.mx.util.MBeanServerLocator;
import org.jboss.system.ServiceMBeanSupport;

import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.auth.session.AuthenticationManager;
import com.code.aon.jaas.auth.session.SessionInfo;
import com.code.aon.jaas.auth.session.event.ExpiredSessionEvent;
import com.code.aon.jaas.auth.session.event.ExpiredSessionListener;
import com.code.aon.jaas.client.ast.IAccessPolicy;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 23/05/2007
 *  
 * @jmx:mbean name="jboss.admin:service=AonSessionManager" extends="org.jboss.system.ServiceMBean"
 */
public class JBossSessionManager extends ServiceMBeanSupport 
		implements JBossSessionManagerMBean, ExpiredSessionListener {

	/** JBossSessionManager Logger instance. */
	private static final Log LOGGER = LogFactory.getLog( JBossSessionManager.class.getName() );
	/** Field STANDARD_HOST (value is ""jboss.web:type=Host,host=localhost"") */
	static final String STANDARD_HOST = "jboss.web:type=Host,host=localhost";
	/** Managed Resource Constant name */
	static final String MANAGED_RESOURCE = "managedResource";

	/** AuthenticationManager instance. */
	AuthenticationManager authenticationManager = new AuthenticationManager();

	/**
	 * Authenticate principal checking the number of times the application has been accessed. If
	 * the concurrent sessions per user is enabled, checks it too.
	 * 
	 * @param principal
	 * @param activeUsers
	 * @param access
	 * @throws LoginException
     * 
	 * @jmx:managed-operation
	 */
	public void authenticate(Principal principal, Integer activeUsers, IAccessPolicy access) throws LoginException {
		this.authenticationManager.authenticate( principal, activeUsers, access );
	}

	/**
	 * Enable concurrent sessions per user.
	 * 
	 * @param enable
     * 
	 * @jmx:managed-operation
	 */
	public void enableConcurrentSessions4User(Boolean enable) {
		this.authenticationManager.setEnableConcurrentSessions4User( enable );
	}

	/**
	 * Get <code>SessionInfo</code> instance.
	 *   
	 * @param sessionId
	 * @return
	 * @throws LoginException
     * 
	 * @jmx:managed-operation
	 */
	public SessionInfo getSessionInfo(String sessionId) {
		return this.authenticationManager.getSessionInformation( sessionId );
	}

	/**
	 * Register a <code>HttpSession</code> and <code>AuthPrincipal</code> wrapped in a 
	 * <code>SessionInfo</code> instance.
	 *   
	 * @param sessionInfo
	 * @throws LoginException
     * 
	 * @jmx:managed-operation
	 */
	public void registerSession(SessionInfo sessionInfo) throws LoginException {
		this.authenticationManager.registerSession( sessionInfo );
	}

	/**
	 * Remove a <code>HttpSession</code>.
	 *   
	 * @param sessionId
	 * @throws LoginException
     * 
	 * @jmx:managed-operation
	 */
	public void removeSession(String sessionId) throws LoginException {
		this.authenticationManager.removeSession( sessionId );
	}

	/**
	 * Get <code>AuthenticationLoginException</code>.
	 *   
	 * @param str
	 * @return
     * 
	 * @jmx:managed-operation
	 */
	public AuthenticationLoginException getLastLoginException(String str) {
		return this.authenticationManager.getLastLoginException();
	}

	/**
	 * Set last <code>AuthenticationLoginException</code>.
	 *   
	 * @param lastLoginException
     * 
	 * @jmx:managed-operation
	 */
	public void fillLastLoginException(AuthenticationLoginException lastLoginException) {
    	this.authenticationManager.setLastLoginException( lastLoginException );
	}

	/**
	 * Return a list of <code>SessionInfo</code> instances, actives for the context 
	 * passed by parameter.
	 * 
	 * @param context
	 * @return
	 * @throws DeploymentException
     * 
	 * @jmx:managed-operation
	 */
	public List getActiveSessions(String context) throws DeploymentException {
		List list = new ArrayList();
		try {
			MBeanServer server = MBeanServerLocator.locateJBoss();
//	Get MBean details for this object.
			ObjectName objectName = new ObjectName( STANDARD_HOST );
			StandardHost standardHost = 
				(StandardHost) server.getAttribute( objectName, MANAGED_RESOURCE );
			Context ctx = (Context) standardHost.findChild( context );
			if ( ctx != null ) {
				Session[] sessions = 
					ctx.getManager().findSessions();
				for (int i=0; i<sessions.length; i++) {
					if ( sessions[i].getPrincipal() != null ) {
						AuthPrincipal principal = 
							new AuthPrincipal( sessions[i].getPrincipal().getName() );
						list.add( 
							new SessionInfo( sessions[i].getId(), 
											sessions[i].getCreationTime(), 
											sessions[i].getLastAccessedTime(), 
											sessions[i].getMaxInactiveInterval(), 
											principal  ) 
							);
					}
					LOGGER.debug( "Active Sessions:" + sessions[i].getId() );
				}
			}
		} catch (Exception e) {
            throw new DeploymentException(e.getMessage(), e);
		}
		return list;
	}

	/**
	 * Invalidate session.
	 * 
	 * @param context
	 * @param sessionId
	 * @throws DeploymentException
     * 
	 * @jmx:managed-operation
	 */
	public void invalidate(String context, String sessionId) throws DeploymentException {
		try {
//	Get MBean details for this object.
			ObjectName objectName = new ObjectName( STANDARD_HOST );
			StandardHost standardHost = 
				(StandardHost) server.getAttribute( objectName, MANAGED_RESOURCE );
			Session[] sessions = 
				( (Context) standardHost.findChild( context ) ).getManager().findSessions();
			for (int i=0; i<sessions.length; i++) {
				if ( sessions[i].getId().equals( sessionId ) ) {
					sessions[i].getSession().invalidate();
					return;
				}
			}
		} catch (Exception e) {
            throw new DeploymentException(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.session.event.ExpiredSessionListener#expiredSession(com.code.aon.jaas.auth.session.event.ExpiredSessionEvent)
	 */
	public void expiredSession(ExpiredSessionEvent event) {
		SessionInfo sessionInfo = (SessionInfo) event.getSource();
		try {
			invalidate( sessionInfo.getPrincipal().getContext(), sessionInfo.getSessionId() );
		} catch (DeploymentException e) {
			LOGGER.fatal( e );
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.vendor.tomcat.SecurityMBeanSupport#startService()
	 */
	protected void startService() throws Exception {
		super.startService();
		this.authenticationManager.addExpiredSessionListener( this );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.vendor.tomcat.SecurityMBeanSupport#stopService()
	 */
	protected void stopService() throws Exception {
		super.stopService();
		this.authenticationManager.removeExpiredSessionListener( this );
	}

}
