/**
 * 
 */
package es.code.repository.security;

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
 * @author Consulting & Development. Iñaki Ayerbe - 02/07/2007
 *
 */
public class AccessManagerImpl implements AccessManager {

    /**
     * Subject whose access rights this AccessManager should reflect
     */
    protected Subject subject;

    /**
     * hierarchy manager used for ACL-based access control model
     */
    protected HierarchyManager hierMgr;

    private boolean initialized;

    protected boolean system;
    protected boolean anonymous;

    /**
     * Empty constructor
     */
    public AccessManagerImpl() {
        initialized = false;
        anonymous = false;
        system = false;
    }

    /* (non-Javadoc)
	 * @see org.apache.jackrabbit.core.security.AccessManager#canAccess(java.lang.String)
	 */
	public boolean canAccess(String workspaceName)
			throws NoSuchWorkspaceException, RepositoryException {
        // @todo check permission to access given workspace based on principals
        return true;
	}

	/* (non-Javadoc)
	 * @see org.apache.jackrabbit.core.security.AccessManager#checkPermission(org.apache.jackrabbit.core.ItemId, int)
	 */
	public void checkPermission(ItemId id, int permissions)
			throws AccessDeniedException, ItemNotFoundException,
			RepositoryException {
        if (!initialized) {
            throw new IllegalStateException("not initialized");
        }

        if (system) {
            // system has always all permissions
            return;
        } else if (anonymous) {
            // anonymous is always denied WRITE & REMOVE permissions
            if ((permissions & WRITE) == WRITE
                    || (permissions & REMOVE) == REMOVE) {
                throw new AccessDeniedException();
            }
        }
        // @todo check permission based on principals
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
        anonymous = !subject.getPrincipals(AnonymousPrincipal.class).isEmpty();
        system = !subject.getPrincipals(SystemPrincipal.class).isEmpty();

        // @todo check permission to access given workspace based on principals
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
            if ((permissions & WRITE) == WRITE
                    || (permissions & REMOVE) == REMOVE) {
                return false;
            }
        }

        // @todo check permission based on principals
        return true;
	}

}
