/**
 * 
 */
package es.code.ecm.security.jaas.spi;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.apache.jackrabbit.core.security.SystemPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.plugin.DomainManager;
import com.code.aon.jaas.auth.AuthGroup;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IDomainApplication;
import com.code.aon.jaas.client.ast.IRelation;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 29/06/2007
 *
 */
public class ECMLoginModule extends AbstractLoginModule {

    /** Obtiene un logger apropiado. */
	protected static final Logger LOGGER = LoggerFactory.getLogger( ECMLoginModule.class.getName() );

	/** The login identity */
	private Principal identity;

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#login()
	 */
	@Override
	public boolean login() throws LoginException {
		if (this.callbackHandler == null) {
			throw new LoginException("Error: no CallbackHandler available for JCRModule");
		}
		String username = getUsername();
		if ( username != null ) {
			try {
				identity = createIdentity(username);
			} catch(Exception e) {
				LOGGER.warn( "Failed to create principal" );
				throw new LoginException("Failed to create principal: "+ e.getMessage());
			}
			super.loginOk = true;
		}
		if (!super.loginOk) {
			throw new LoginException( "failed to authenticate " + username );
		}
		return super.loginOk;
	}

	/**
	 * This method is called by <code>login()</code> and returns username and password.
	 * 
	 * @return String, username
	 * @exception LoginException, thrown if CallbackHandler is not set or fails.
	 */
	protected String getUsername() throws LoginException {
		//	prompt for a username and password
		if (callbackHandler == null) {
			throw new LoginException( "Error: no CallbackHandler available to collect authentication information" );
		}
		NameCallback nc = new NameCallback( "User name: ", "guest" );
		Callback[] callbacks = { nc };
		String username = null;
		try {
			callbackHandler.handle(callbacks);
			username = nc.getName();
		} catch (java.io.IOException ioe) {
			throw new LoginException(ioe.toString());
		} catch (UnsupportedCallbackException uce) {
			throw new LoginException( "CallbackHandler does not support: " + uce.getCallback() );
		}
		return username;
	}
	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#createIdentity(java.lang.String)
	 */
	@Override
	protected Principal createIdentity(String username) throws Exception {
		String system = new SystemPrincipal().getName();
//TODO Si existe el usuario en ECM y en LDAP --> AuthPrincipal, AnonimousPrincipal resto de casos.
		return ( username.equals( system ) )? new SystemPrincipal(): new AuthPrincipal( username );
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#getActiveUsers(java.lang.String, java.lang.String)
	 */
	@Override
	protected Integer getActiveUsers(String host, String context)
			throws LoginException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#getIdentity()
	 */
	@Override
	protected Principal getIdentity() {
		return identity;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#getRoleSets()
	 */
	@Override
	protected Group[] getRoleSets() throws LoginException {
        AuthGroup rolesGroup = new AuthGroup(ROLES_GROUP_NAME);
        List<Principal> groups = new LinkedList<Principal>();
        groups.add(rolesGroup);
        if ( getIdentity() instanceof AuthPrincipal ) {
	    	AuthPrincipal authPrincipal = (AuthPrincipal) getIdentity();
			DomainManager dm;
			try {
				dm = new DomainManager( authPrincipal );
				Iterator<IDomainApplication> iter = dm.getDomainApplications().iterator();
				while (iter.hasNext()) {
					IDomainApplication ido = iter.next();
					if ( ido.getId().equals( dm.getApplication().getId() ) ) {
						IRelation rel = ido.getUser( authPrincipal.getShortName() ) ;
						Iterator<IRelation> daprofiles = ido.profiles().iterator();
						while (daprofiles.hasNext()) {
							IRelation profile = daprofiles.next();
							if ( rel.getRelations().indexOf( profile.getId() ) > -1 ) {
								Iterator<String> roles = profile.relations().iterator();
								while (roles.hasNext()) {
									String elem = roles.next();
					                AuthPrincipal p = new AuthPrincipal( elem );
					                rolesGroup.addMember(p);
								}
							}
						}
					}
				}
			} catch (DeploymentException e) {
				throw new LoginException(e.getMessage());
			}
        }
        Group[] roleSets = new Group[groups.size()];
        groups.toArray(roleSets);
        return roleSets;
	}

	/* (non-Javadoc)
	 * @see com.code.aon.jaas.auth.spi.AbstractLoginModule#roles4Subject(java.util.Set)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void roles4Subject(Set<Principal> principals)
			throws LoginException {
		Group[] roleSets = getRoleSets();
		for (int g = 0; g < roleSets.length; g++) {
			Group group = roleSets[g];
			String name = group.getName();
			Group subjectGroup = createGroup(name, principals);
            // Copy the group members to the Subject group
			Enumeration members = group.members();
			while (members.hasMoreElements()) {
				Principal role = (Principal) members.nextElement();
				subjectGroup.addMember(role);
			}
		}
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
	@SuppressWarnings("unchecked")
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
