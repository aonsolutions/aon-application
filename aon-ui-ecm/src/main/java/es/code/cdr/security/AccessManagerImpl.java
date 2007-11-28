/**
 * 
 */
package es.code.cdr.security;

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

/**
 * @author Consulting & Development. Iñaki Ayerbe - 04/07/2007
 *
 */
public class AccessManagerImpl implements AccessManager {

	/** Subject whose access rights this AccessManager should reflect */
	protected Subject subject;
	/** Hierarchy manager used for ACL-based access control model */
	protected HierarchyManager hierMgr;

	/** Tells if AccessManager instance has been initialized */
	private boolean initialized;
	/** Tells if accessing user is a system one */
	private boolean system;
	/** Tells if accessing user is an anonymous one */
	protected boolean anonymous;

    /* (non-Javadoc)
	 * @see org.apache.jackrabbit.core.security.AccessManager#canAccess(java.lang.String)
	 */
	public boolean canAccess(String workspaceName)
			throws NoSuchWorkspaceException, RepositoryException {
		// TODO check permission to access given workspace based on principals
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.jackrabbit.core.security.AccessManager#checkPermission(org.apache.jackrabbit.core.ItemId, int)
	 */
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

	/* (non-Javadoc)
	 * @see org.apache.jackrabbit.core.security.AccessManager#close()
	 */
	public void close() throws Exception {
		if (!initialized) {
			throw new IllegalStateException("not initialized");
		}
		initialized = false;
	}

	/* (non-Javadoc)
	 * @see org.apache.jackrabbit.core.security.AccessManager#init(org.apache.jackrabbit.core.security.AMContext)
	 */
	public void init(AMContext context) throws AccessDeniedException, Exception {
		if (initialized) {
			throw new IllegalStateException("already initialized");
		}

		subject = context.getSubject();
		hierMgr = context.getHierarchyManager();
		anonymous = !subject.getPrincipals( AnonymousPrincipal.class ).isEmpty();
		system = !subject.getPrincipals( SystemPrincipal.class ).isEmpty();

		// TODO check permission to access given workspace based on principals
		initialized = true;
	}

	/* (non-Javadoc)
	 * @see org.apache.jackrabbit.core.security.AccessManager#isGranted(org.apache.jackrabbit.core.ItemId, int)
	 */
	public boolean isGranted(ItemId id, int permissions)
			throws ItemNotFoundException, RepositoryException {
		if (!initialized) {
			throw new IllegalStateException("not initialized");
		}

		if (system) {
			// system has always all permissions
			return true;
		} else if (anonymous) {
			// anonymous is always denied WRITE & REMOVE premissions
			if ((permissions & WRITE) == WRITE || (permissions & REMOVE) == REMOVE) {
				return false;
			}
		}

		// TODO check permission based on principals
		return true;
	}

}
