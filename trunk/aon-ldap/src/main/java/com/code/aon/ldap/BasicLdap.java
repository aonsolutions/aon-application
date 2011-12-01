package com.code.aon.ldap;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ldap.IAonObjectClasses.TOP;

import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.naming.Name;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicLdap {

	private static final String SHA_ALGORITHM = "SHA";

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
	
	public boolean delete( Name dn ) {
		boolean ok = false;		
		try {
			getLdapSession().delete(dn);
			ok = true;
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			closeSession();
		}
		return ok;
	}

	
	public boolean deleteDepth( Name dn, boolean selfDelete ) {
		boolean ok = false;		
		try {
			getLdapSession().deleteDepth(dn, selfDelete);
			ok = true;
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			closeSession();
		}
		return ok;
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
	
	public boolean add( Entry entry ) {
		boolean ok = false;
		try {
			getLdapSession().add(entry);
			ok = true;
		} catch ( Throwable th ) {
			LOGGER.error( th.getMessage(), th );
		} finally {
			closeSession();
		}
		return ok;
	}	
	
	public Entry addOrganizationUnit( Name dn ) {
		Entry entry = null;
		try {
			entry = new Entry(dn);
			entry.addObjectClasses(new String[]{TOP, ORGANIZATIONAL_UNIT});
			entry.put( ILdapConstants.ORGANIZATIONAL_UNIT_NAME_ATTRIBUTE, NameResolver.getFirstValue(dn) );
			LOGGER.info( "Add Organization Unit entry: {}", dn );
			getLdapSession().add(entry);
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e);
		} finally {
			closeSession();
		}
		return entry;
	}		
	
	public List<Entry> getList( Name base, String filter, String... attributes ) {
		List<Entry> list = null;
		try {
			list = getLdapSession().search(base, filter, attributes );
		} catch ( LdapException e ) {
			LOGGER.error( e.getMessage(), e);
		} finally {
			closeSession();
		}
		return list;		
	}

	public static String encode( String value, String algorithm ) {
		String shaPassword = null;
		try {
			byte[] hash = MessageDigest.getInstance(algorithm).digest(value.getBytes());
			shaPassword = "{" + algorithm + "}" + new String(Base64.encodeBase64(hash));
		} catch (Throwable e) {
			LOGGER.error(e.getMessage(), e);
		}        		
		return shaPassword;
	}	
	
	public static String encodeSHA( String value ) {
		return encode(value, SHA_ALGORITHM);
	}
	
}
