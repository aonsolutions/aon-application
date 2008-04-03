/**
 * 
 */
package com.code.aon.jaas.vendor.jboss;

import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import com.code.aon.jaas.client.ast.IOption;
import com.code.aon.jaas.client.ast.core.AstLoader;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.jaas.storage.ApplicationsStorage;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 23/05/2007
 *  
 * @jmx:mbean name="jboss.admin:service=AonLdap" extends="org.jboss.system.ServiceMBean"
 */
public class JBossLdap extends ServiceMBeanSupport implements JBossLdapMBean {

	/** JBossSessionManager Logger instance. */
	private static final Log LOGGER = LogFactory.getLog( JBossLdap.class.getName() );
	
	private Map<String, IOption> options;

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.vendor.tomcat.SecurityMBeanSupport#startService()
	 */
	protected void startService() throws Exception {
		super.startService();
		ObjectName oname = new ObjectName(JBossMainDeployerMBean.OBJECT_NAME);
		URL config = 
			(URL) server.invoke(oname, "getConfigResource", new Object[] { null }, new String[] { String.class.getName() });
		ApplicationsStorage as = (ApplicationsStorage) AstLoader.getInstance().parse( 0, config.openStream() );
		this.options = as.options();
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.vendor.tomcat.SecurityMBeanSupport#stopService()
	 */
	protected void stopService() throws Exception {
		super.stopService();
	}

	public Map<String, IOption> getOptions() {
		return options;
	}

	public Properties getDSMDProperties(Principal principal) {
		Properties properties = new Properties();
		return properties;
	}
	
}
