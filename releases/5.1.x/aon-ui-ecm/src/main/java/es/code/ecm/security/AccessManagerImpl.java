/**
 * 
 */
package es.code.ecm.security;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;
import javax.security.auth.Subject;

import org.apache.jackrabbit.core.HierarchyManager;
import org.apache.jackrabbit.core.ItemId;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.core.security.AnonymousPrincipal;
import org.apache.jackrabbit.core.security.SystemPrincipal;

import com.code.aon.jaas.auth.AuthGroup;
import com.code.aon.jaas.auth.AuthPrincipal;

import es.code.ecm.ContentRepository;
import es.code.ecm.event.PermissionEvent;
import es.code.ecm.event.PermissionListener;
import es.code.ecm.nodes.User;
import es.code.ecm.security.acl.AclEntry;
import es.code.ecm.security.acl.AclObjectIdentity;
import es.code.ecm.security.acl.core.NodeObjectIdentity;
import es.code.ecm.util.JCRUtils;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 04/07/2007
 *
 */
public class AccessManagerImpl implements AccessManager, PermissionListener {

	/** Tells if AccessManager instance has been initialized */
	private boolean initialized;
	/** Tells if accessing user is a system one */
	private boolean system;
	/** Tells if accessing user is an anonymous one */
	private boolean anonymous;
	/** Principal */
	private AuthPrincipal principal;
	/** Principal roles. */
	private String principalRoles;
	/** Principal ACLEntry */
	private AclEntry principalEntry;

	HierarchyManager hm ;

	public AuthPrincipal getPrincipal() {
		return principal;
	}

	public String getPrincipalRoles() {
		return principalRoles;
	}

	@Override
	public void close() throws Exception {
		if (!initialized) {
			throw new IllegalStateException("not initialized");
		}
		initialized = false;
	}

	@Override
	public void init(AMContext context) throws AccessDeniedException, Exception {
		if (initialized) {
			throw new IllegalStateException("already initialized");
		}

		Subject subject = context.getSubject();
		hm = context.getHierarchyManager();
		anonymous = !subject.getPrincipals( AnonymousPrincipal.class ).isEmpty();
		system = !subject.getPrincipals( SystemPrincipal.class ).isEmpty();
		if ( !system ) {
			//	Check permission to access given workspace based on principals
			principal = null;
			Iterator<AuthPrincipal> it = subject.getPrincipals( AuthPrincipal.class ).iterator();
			while (it.hasNext()) {
				principal = it.next();
				if ( principal.getContext() != null )
					break;
			}
			if ( principal != null && principal.getContext() != null ) {
				findPrincipalEntry();
				if ( principalEntry == null ) {
					Set<AuthGroup> set = subject.getPrincipals( AuthGroup.class );
					if ( !set.isEmpty() ) {
						principalRoles = set.iterator().next().toString();
						anonymous = principalRoles.indexOf( "Anonymous" ) > -1;
					}
				}
			} else {
				throw new AccessDeniedException( "Unable to access repository:" + subject );
			}
		}
		initialized = true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isGranted(ItemId id, int permissions)
			throws ItemNotFoundException, RepositoryException {
		if (!initialized) {
			throw new IllegalStateException("not initialized");
		}
		if (system) {
			// system has always all permissions
			return true;
		} else if (anonymous) {
			// anonymous is always denied WRITE & REMOVE permissions
			if ((permissions & WRITE) == WRITE || (permissions & REMOVE) == REMOVE) {
				return false;
			}
		}
		// Check if the node relative path is aon:folder or aon:category, otherwise do nothing.
		String nodeType = JCRUtils.getNodeType( hm.getPath( id ).toString() );
		if (  nodeType != null ) {
			AclObjectIdentity aclObjectIdentity = new NodeObjectIdentity( nodeType, id.toString() );
			AclEntry entry = getAclEntry( aclObjectIdentity );
			if ( entry != null ) {
				Long accessLevel = entry.getAclPermission().getAccessLevel();
				if ( anonymous && accessLevel == 0 ) {
					return true;
				} else {
					if ( principalEntry == null ) // Finds principal AclEntry if exist.
						findPrincipalEntry();
					if ( !anonymous && accessLevel <= principalEntry.getAclPermission().getAccessLevel() )
						return true;
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canAccess(String workspaceName)
			throws NoSuchWorkspaceException, RepositoryException {
		if ( workspaceName.equals( ContentRepository.DEFAULT_WORKSPACE ) && system )
			return true;

		if ( workspaceName.equals( principal.getDomain() ) )
			return true;

		return false;
	}

	@Override
	public void checkPermission(ItemId id, int permissions)
			throws AccessDeniedException, ItemNotFoundException, RepositoryException {
		if (!initialized) {
			throw new IllegalStateException("not initialized");
		}

		if (system) {
			// system has always all permissions
			return;
		} else if (anonymous) {
			// anonymous is always denied WRITE & REMOVE permissions
			if ((permissions & WRITE) == WRITE || (permissions & REMOVE) == REMOVE) {
				throw new AccessDeniedException();
			}
		}
		// TODO check permission based on principals
	}

	@Override
	public void permissionAdded(PermissionEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void permissionChanged(PermissionEvent event) {
		findPrincipalEntry();
	}

	/**
	 * Finds AclEntry bound to principal.
	 */
	private void findPrincipalEntry() {
		AclObjectIdentity aclObjectIdentity = 
			new NodeObjectIdentity( User.getNodeType(), principal.getShortName() );
		principalEntry = getAclEntry( aclObjectIdentity );
	}

	/**
	 * Gets ACL entry.
	 * 
	 * @param aclObjectIdentity
	 * @return
	 */
	private AclEntry getAclEntry(AclObjectIdentity aclObjectIdentity) {
		List<AclEntry> l = ContentRepository.getAclManager().getAcls( aclObjectIdentity );
		if ( l != null ) {
			Iterator<AclEntry> iter = l.iterator();
			while (iter.hasNext()) {
				AclEntry entry = iter.next();
				if ( entry.getAclObjectIdentity().equals( aclObjectIdentity ) ) {
					return entry;
				}
			}
		}
		return null;
	}

}