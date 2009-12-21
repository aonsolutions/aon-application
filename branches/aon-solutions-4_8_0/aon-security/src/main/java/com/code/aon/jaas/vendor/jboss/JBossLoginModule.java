package com.code.aon.jaas.vendor.jboss;

import java.lang.reflect.Constructor;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.security.auth.login.LoginException;

import org.jboss.mx.util.MBeanServerLocator;

import com.code.aon.jaas.auth.AuthGroup;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.auth.NestableGroup;
import com.code.aon.jaas.auth.spi.XMLLoginModule;

@SuppressWarnings("unchecked")
public class JBossLoginModule extends XMLLoginModule {

	@Override
	protected Principal createIdentity(String username) throws Exception {
		Principal p = null;
		if( principalClassName == null ) {
	         p = new AuthPrincipal(username);
		} else {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Class clazz = loader.loadClass(principalClassName);
			Class[] ctorSig = {String.class};
			Constructor ctor = clazz.getConstructor(ctorSig);
			Object[] ctorArgs = {username};
			p = (Principal) ctor.newInstance(ctorArgs);
		}
		return p;
	}

	@Override
	protected void roles4Subject(Set<Principal> principals) throws LoginException {
		Group[] roleSets = getRoleSets();
		for (int g = 0; g < roleSets.length; g++) {
			Group group = roleSets[g];
			String name = group.getName();
			Group subjectGroup = createGroup(name, principals);
			if (subjectGroup instanceof NestableGroup) {
                /*
                 * A NestableGroup only allows Groups to be added to it so we
                 * need to add a SimpleGroup to subjectRoles to contain the
                 * roles
                 */
				AuthGroup tmp = new AuthGroup(ROLES_GROUP_NAME);
				subjectGroup.addMember(tmp);
				subjectGroup = tmp;
			}
            // Copy the group members to the Subject group
			Enumeration members = group.members();
			while (members.hasMoreElements()) {
				Principal role = (Principal) members.nextElement();
				subjectGroup.addMember(role);
			}
		}
	}

	@Override
    protected Integer getActiveUsers(String host, String context) throws LoginException {
    	List list = new ArrayList();
    	try {
			ObjectName jaasMgr = new ObjectName("jboss.security:service=JaasSecurityManager");
			Object[] params = { super.securityDomain };
			String[] signature = { String.class.getName() };
			List allUsers = 
				(List) getMBeanServer().invoke( jaasMgr, "getAuthenticationCachePrincipals", params, signature );
			Iterator iter = allUsers.iterator();
			while (iter.hasNext()) {
				Principal principal = (Principal) iter.next();
				if (principal.getName().indexOf(host) > -1 && principal.getName().indexOf(context) > -1)
					list.add(principal);
			}
    	} catch (Throwable th) {
			throw new LoginException( "Error retrieving active users on [" + host + "] Host." + th.getMessage() );
    	}
		return list.size(); 
	}

	@Override
	protected MBeanServer getMBeanServer() {
		return MBeanServerLocator.locateJBoss();
	}

	/**
	 * Find or create a Group with the given name. Subclasses should use this
	 * method to locate the 'Roles' group or create additional types of groups.
	 * 
     * @param name
     * @param principals
	 * @return A named Group from the principals set.

     * 
     * @return el nombre del grupo del conjunto de principals.
     */
	private Group createGroup(String name, Set principals) {
		Group roles = null;
		Iterator iter = principals.iterator();
		while (iter.hasNext()) {
			Object next = iter.next();
			if (!(next instanceof Group)) {
				continue;
			}
			Group grp = (Group) next;
			if (grp.getName().equals(name)) {
				roles = grp;
				break;
			}
		}
//	If we did not find a group create one
		if (roles == null) {
			roles = new AuthGroup(name);
			principals.add(roles);
		}
		return roles;
	}

}
