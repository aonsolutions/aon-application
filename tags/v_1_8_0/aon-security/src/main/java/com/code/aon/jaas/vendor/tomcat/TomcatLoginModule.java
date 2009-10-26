package com.code.aon.jaas.vendor.tomcat;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.security.auth.login.LoginException;

import com.code.aon.jaas.auth.AuthGroup;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.spi.XMLLoginModule;

import org.apache.catalina.Context;
import org.apache.catalina.core.StandardHost;

/**
 * Tomcat Login Module.
 * 
 * @author Consulting & Development. Iñaki Ayerbe - 19-jul-2006
 * @since 1.0
 *  
 */

public class TomcatLoginModule extends XMLLoginModule {

	/*(non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#createIdentity(java.lang.String)
	 */
	protected Principal createIdentity(String username) throws Exception {
		return new AuthPrincipal(username);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.XMLLoginModule#roles4Subject(java.util.Set)
	 */
	@SuppressWarnings("unchecked")
	protected void roles4Subject(Set<Principal> principals) throws LoginException {
		Group[] roleSets = getRoleSets();
		for (int g = 0; g < roleSets.length; g++) {
			Group group = roleSets[g];
            // Copy the group members to the principals
			Enumeration members = group.members();
			while (members.hasMoreElements()) {
				Principal role = (Principal) members.nextElement();
				principals.add( new AuthGroup( role.getName() ) );
			}
		}
	}

    /* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#getActiveUsers(java.lang.String)
	 */
	protected Integer getActiveUsers(String host, String context) throws LoginException {
		try {
    		ObjectName objectName = new ObjectName( "Catalina:host=" + host + ",type=Host" );
    		StandardHost standardHost = 
    			(StandardHost) getMBeanServer().getAttribute( objectName, "managedResource" );
    		Context ctx = (Context) standardHost.findChild( context );
    		return ctx.getManager().getActiveSessions();
		} catch(Exception e) {
			throw new LoginException( "Error retrieving active users on [" + host + "] Host." + e.getMessage() );
		}
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#getMBeanServer()
	 */
	@Override
	protected MBeanServer getMBeanServer() {
		return (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
	}

//	/**
//	 * Recupera la lista con los usuarios activos de un dominio en el host pasado
//     * por parámetro.
//	 * 
//     * @param host
//	 * @return
//	 * @throws MalformedObjectNameException
//	 * @throws InstanceNotFoundException
//	 * @throws MBeanException
//	 * @throws ReflectionException
//	 */
//    protected List getActiveUsers(String host) {
//    	List<Principal> list = new ArrayList<Principal>();
//    	try {
//			MBeanServer server = getMBeanServer();
//			ObjectName jaasMgr = new ObjectName("Catalina:host=localhost,path=/aon-security,type=Manager");
//			LOGGER.info( "********************************************************************" );
//			LOGGER.info( "********************************************************************" );
//			getStandardHost( server );
////			getManager( server );
//			LOGGER.info( "********************************************************************" );
//			LOGGER.info( "********************************************************************" );
//			getManagerDetails( server, jaasMgr );
//			LOGGER.info( "********************************************************************" );
//			LOGGER.info( "********************************************************************" );
//    	} catch(Exception e) {
//    		LOGGER.fatal( "Error loading active users on [" + host + "] Host." + e.getMessage() );
//    	}
//		return list; 
//	}
//
//	// Tomcat Server Component Methods
//    public StandardHost getStandardHost(MBeanServer mBeanServer) {
//    	StandardHost standardHost = null;
//    	try {
//    		String objName = "Catalina:host=localhost,type=Host";
//    		// Get MBean details for this object.
//    		ObjectName objectName = new ObjectName(objName);
//    		Object managedResource = mBeanServer.getAttribute(objectName, "managedResource");
//    		standardHost = (StandardHost)managedResource;
//    		Context context = (Context) standardHost.findChild( "/aon-security" );
//    		LOGGER.info( "getStandardHost-->" + context.getManager().getActiveSessions() );
//    	   	Session[] sessions = context.getManager().findSessions();
//    	   	for (int i=0; i<sessions.length; i++) {
//             	LOGGER.info("CreationTime : " + sessions[i].getCreationTime());
//             	LOGGER.info("Id : " + sessions[i].getId());
//             	LOGGER.info("LastAccessedTime : " + sessions[i].getLastAccessedTime());
//    	   	}
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}
//    	return standardHost;
//    }
//
//    public Manager getManager(MBeanServer mBeanServer) {
//       	Manager manager = null;
//       	try {
//    	   	String objName = "Catalina:j2eeType=WebModule,name=//localhost/aon-security,J2EEApplication=none,J2EEServer=none";
//       		//String objName = "Catalina:type=Manager,path=/balancer,host=localhost";
//    	   	ObjectName contextObjectName = new ObjectName(objName);
//    	   	Object contextManagedResource = mBeanServer.getAttribute(contextObjectName, "managedResource");
//    	   	StandardContext standardContext = (StandardContext)contextManagedResource;
//
//    	   	manager = standardContext.getManager();
//    	   	Session[] sessions = manager.findSessions();
////    	   	// Put sessions into a List;
////    	   	HashMap sessionList = new HashMap();
//    	   	for (int i=0; i<sessions.length; i++) {
//             	LOGGER.info("CreationTime : " + sessions[i].getCreationTime());
//             	LOGGER.info("Id : " + sessions[i].getId());
//             	LOGGER.info("LastAccessedTime : " + sessions[i].getLastAccessedTime());
////    	   		sessionList.put(sessions[i].getId(), sessions[i]);
//    	   	}
//       	} catch (Exception e) {
//       		e.printStackTrace();
//       	}
//       	return manager;
//       }
//
//    public void getManagerDetails(MBeanServer mBeanServer, ObjectName objectName) {
//          try {
//        	  String  attributeName = "activeSessions";
//              int activeSessions = ((Integer)mBeanServer.getAttribute(objectName, attributeName)).intValue();
//           	LOGGER.info( "Active Sessions : " + Integer.toString(activeSessions) );
//
//           	attributeName = "maxActiveSessions";
//           	Integer maxActiveSessions = (Integer) mBeanServer.getAttribute(objectName, attributeName);
//           	LOGGER.info( "maxActiveSessions : " + maxActiveSessions );
//
//           	try{
//           	attributeName = "listSessionIds";
//           	String listSessionIds = (String) mBeanServer.getAttribute(objectName, attributeName);
//           	LOGGER.info( "listSessionIds : " + listSessionIds );
//           	} catch(Exception e) {
//           		LOGGER.info( "SEVERE listSessionIds: " + e.getMessage() );
//           	}
//
//           	attributeName = "sessions";
//             java.util.HashMap attributeList = (java.util.HashMap)mBeanServer.invoke(objectName, attributeName,null,null);
//
//
//          	Iterator valueListIterator = attributeList.values().iterator();
//             while (valueListIterator.hasNext()) {
//             	Session listElement = (Session)valueListIterator.next();
//             	LOGGER.info("CreationTime : " + listElement.getCreationTime());
//             	LOGGER.info("Id : " + listElement.getId());
//             	LOGGER.info("LastAccessedTime : " + listElement.getLastAccessedTime());
//
//             	String prefix = "Session " + listElement.getId() + ": ";
//
//             	HttpSession httpSession = listElement.getSession();
//
//             	Enumeration attrNames = httpSession.getAttributeNames();
//             	while (attrNames.hasMoreElements()) {
//             		String attrName = (String)attrNames.nextElement();
//             		String attrValue = (String)httpSession.getAttribute(attrName);
//             		LOGGER.info( "HttpSession Attribute: " + attrName +","+ attrValue);
//             	}
//             }
//          } catch (Exception e) {
//             e.printStackTrace();
//          }
//       }
}
