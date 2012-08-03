package com.code.aon.common.util;

import java.util.Iterator;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;

public class BeanServerUtil {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BeanServerUtil.class.getName());

	public static final String MAIN_DEPLOYER_REF = "jboss.admin:service=AonMainDeployer";
	
	public static final ObjectName MAIN_DEPLOYER = getObjectName(MAIN_DEPLOYER_REF);

	public static final String SESSION_MANAGER_REF = "jboss.admin:service=AonSessionManager";
	
	public static final ObjectName SESSION_MANAGER = getObjectName(SESSION_MANAGER_REF);
	
	public static final String LDAP_SERVICE_REF = "jboss.admin:service=AonLdap";
	
	public static final ObjectName LDAP_SERVICE = getObjectName(LDAP_SERVICE_REF);
	
	public static final String AON_SECURITY_DOMAIN = "aon-login";
	
	public static final String JAAS_SECURITY = "jboss.security:service=JaasSecurityManager";
	
	private static ObjectName getObjectName( String ref ) {
		try {
			return new ObjectName( ref );
		} catch (MalformedObjectNameException e) {
			LOGGER.error( e.getMessage(), e );
		}		
		return null;
	}
	
	public static MBeanServer getMBeanServer() {
		MBeanServer server = null;
		List<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
		if (servers.size() > 0) {
			if (servers.size() > 1) {
				//	Iterates over servers list untill AonMainDeployerMBean is found.
				// TODO Isolate application server. 
				Iterator<MBeanServer> it = servers.iterator();
				while (it.hasNext()) {
					server = it.next();
					try {
						server.getObjectInstance( MAIN_DEPLOYER );
						break;
					} catch (Exception e) {
						LOGGER.warn( e.getMessage(), e );
					}
				}
			} else {
				server = (MBeanServer) servers.get(0);
			}
		}
		return server;
	}

	public static void flushAuthenticationCache(String domain) throws AonException {
		try {
			ObjectName name = getObjectName(JAAS_SECURITY);
			Object[] params = { domain };
			String[] sig = { String.class.getName() };
			getMBeanServer().invoke(name, "flushAuthenticationCache", params, sig);
		} catch (Throwable e) {
			throw new AonException( e.getMessage(), e );
		}
	}
	
}
