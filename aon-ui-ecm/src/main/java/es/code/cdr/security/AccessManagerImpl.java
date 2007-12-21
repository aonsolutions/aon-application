/**
 * 
 */
package es.code.cdr.security;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;
import javax.security.auth.Subject;

import org.apache.jackrabbit.core.ItemId;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.core.security.AnonymousPrincipal;
import org.apache.jackrabbit.core.security.SystemPrincipal;

import com.code.aon.bridge.plugin.DomainManager;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.client.ast.IRelation;

import es.code.cdr.beans.Permission;
import es.code.cdr.event.PermissionEvent;
import es.code.cdr.event.PermissionListener;
import es.code.repository.IProvider;
import es.code.repository.util.Path;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 04/07/2007
 *
 */
public class AccessManagerImpl implements AccessManager, PermissionListener {

	/** Profiles whose access rights this AccessManager should reflect */
	protected Collection<IRelation> profiles;
	/** Check permission based on principals loaded from a role mappings properties file. */
	Properties profilemappings = new Properties();

	/** Tells if AccessManager instance has been initialized */
	private boolean initialized;
	/** Tells if accessing user is a system one */
	private boolean system;
	/** Tells if accessing user is an anonymous one */
	protected boolean anonymous;

	@Override
	public boolean canAccess(String workspaceName)
			throws NoSuchWorkspaceException, RepositoryException {
		// TODO check permission to access given workspace based on principals
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
		anonymous = !subject.getPrincipals( AnonymousPrincipal.class ).isEmpty();
		system = !subject.getPrincipals( SystemPrincipal.class ).isEmpty();
		//	Check permission to access given workspace based on principals
		AuthPrincipal principal = null;
		Iterator<AuthPrincipal> it = subject.getPrincipals( AuthPrincipal.class ).iterator();
		while (it.hasNext()) {
			principal = it.next();
			if ( principal.getContext() != null )
				break;
		}
		
		if ( principal != null && principal.getContext() != null ) {
			this.profiles = new DomainManager( principal ).getProfiles( null );
			InputStream is = Path.getResource( System.getProperty( IProvider.PROFILE_MAPPINGS_FILE_KEY ), IProvider.PROFILE_MAPPINGS, "" ).openStream();
			profilemappings.load( is );
			initialized = true;
		} else {
			throw new AccessDeniedException( "Unable to access repository:" + subject );
		}
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
		//	Check permission based on principals
		Iterator<IRelation> it = this.profiles.iterator();
		while (it.hasNext()) {
			IRelation profile = it.next();
            String perms = profilemappings.getProperty( profile.getId() );
        	if ( perms.indexOf( Integer.toString( permissions ) ) > -1 )
        		return true;
		}
		return false;
	}

	@Override
	public void permissionAdded(PermissionEvent event) {
		Permission p = event.getPermission();
		profilemappings.put( p.getName(), p.getPermissions() );
	}

}