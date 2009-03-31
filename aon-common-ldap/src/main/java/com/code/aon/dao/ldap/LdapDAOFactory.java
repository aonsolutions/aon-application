package com.code.aon.dao.ldap;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import com.code.aon.common.bean.BeanConfig;
import com.code.aon.common.dao.IDAO;

public class LdapDAOFactory {
	
	/**
	 * Obtain a suitable <code>Logger</code>.
	 */
	private static final Logger LOGGER = Logger.getLogger(LdapDAOFactory.class.getName());
	
	private Properties ldapProperties;
	
	protected LdapDAOFactory() {
		this.ldapProperties = retrieveLdapProperties();		
	}
	
	private MBeanServer getMBeanServer() {
		MBeanServer server = null;
		List servers = MBeanServerFactory.findMBeanServer(null);
		if (servers.size() > 0) {
			if (servers.size() > 1) {
				//	Iterates over servers list untill AonMainDeployerMBean is found.
				// TODO Isolate application server. 
				Iterator it = servers.iterator();
				while (it.hasNext()) {
					server = (MBeanServer) it.next();
					try {
						ObjectName jbossname = new ObjectName( "jboss.admin:service=AonMainDeployer" );
						server.getObjectInstance( jbossname );
						break;
					} catch(Exception e) {
					}
				}
			} else {
				server = (MBeanServer) servers.get(0);
			}
		}
		return server;
	}
	
	@SuppressWarnings("unchecked")
	private Properties retrieveLdapProperties() {
		Properties ldapProperties = null;
		MBeanServer server = getMBeanServer();
		try {
			ObjectName oname = new ObjectName("jboss.admin:service=AonLdap");
			ldapProperties = 
				(Properties) server.invoke( oname, "getLdapProperties", 
											new Object[] {}, new String[] {} );
		} catch (Throwable th) {
			LOGGER.log( Level.SEVERE, "Error getting Ldap connection properties", th);
		}
		return ldapProperties;
	}
	
	/**
	 * Gets the ldap properties.
	 * 
	 * @return the ldap properties
	 */
	public Properties getLdapProperties() {
		return ldapProperties;
	}

	/**
	 * Gets the dAO.
	 * 
	 * @param config the config
	 * 
	 * @return the dAO
	 */
	public LdapDAO getDAO( BeanConfig config ) {
		LdapDAO dao = new LdapDAO(this.ldapProperties, config.getPojoClass());
		return dao;
	}

}
