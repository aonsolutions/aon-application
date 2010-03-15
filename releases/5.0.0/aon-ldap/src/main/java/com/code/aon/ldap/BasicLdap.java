package com.code.aon.ldap;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BasicLdap {

	/**
	 * Obtain a suitable <code>Logger</code>.
	 */
	private final static Logger LOGGER = LoggerFactory.getLogger(BasicLdap.class);
	
	private Properties properties;
	
	private LdapSession session;

	public BasicLdap(Properties properties) {
		this.properties = properties;
	}

	public BasicLdap() {
		this( getLdapProperties() );
	}
	
	private static MBeanServer getMBeanServer() {
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
	
	public static Properties getLdapProperties() {
		Properties ldapProperties = null;
		MBeanServer server = getMBeanServer();
		try {
			ObjectName oname = new ObjectName("jboss.admin:service=AonLdap");
			ldapProperties = 
				(Properties) server.invoke( oname, "getLdapProperties", 
											new Object[] {}, new String[] {} );
		} catch (Throwable th) {
			LOGGER.error( "Error getting Ldap connection properties", th);
		}
		return ldapProperties;
	}
	
	public Properties getProperties() {
		return properties;
	}

	public LdapSession getLdapSession() throws LdapException {
		if ( this.session != null ) {
			closeSession();
		}
		this.session = new LdapSession();
		session.open(this.properties);
		return session;
	}
	
	public void closeSession() {
		try {
			if ( session != null ) {
				session.close();
			}
		} catch (Throwable th) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			session = null;
		}
	}
	
	public void delete( Name dn ) throws LdapException {
		try {
			getLdapSession().delete(dn);
		} finally {
			closeSession();
		}
	}
	
	public boolean exists( Name dn, String objectClass ) {
		boolean exists = false;
		try {
			exists = getLdapSession().exists( dn, NameResolver.getObjectClass(objectClass) );
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			closeSession();
		}
		return exists;
	}

	public Entry get( Name dn, String objectClass, String... attributes ) {
		Entry entry = null;
		try {
			entry = getLdapSession().get( dn, NameResolver.getObjectClass(objectClass), attributes );
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			closeSession();
		}
		return entry;
	}
	
}
