package com.code.aon.ldap;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;


public class BasicLdap {

	/**
	 * Obtain a suitable <code>Logger</code>.
	 */
	private static final Logger LOGGER = Logger.getLogger(BasicLdap.class.getName());
	
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
	
	@SuppressWarnings("unchecked")
	public static Properties getLdapProperties() {
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
				session = null;
			}
		} catch (LdapException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
	}
	
	public void delete( DistinguishedName dn ) throws LdapException {
		try {
			getLdapSession().delete(dn);
		} finally {
			closeSession();
		}
	}
	
	public boolean exists( String dn, String objectClass ) {
		boolean exists = false;
		try {
			exists = getLdapSession().exists( dn, LdapSession.getObjectClass(objectClass) );
		} catch ( LdapException e ) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		} finally {
			closeSession();
		}
		return exists;
	}

	public boolean exists( DistinguishedName dn, String objectClass ) {
		return exists( dn.toString(), objectClass );
	}

}
