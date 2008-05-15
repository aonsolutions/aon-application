package es.code.cdr.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;

import sun.awt.EventListenerAggregate;

import com.code.aon.bridge.plugin.DomainManager;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.deployment.DeploymentException;

import es.code.cdr.beans.Permission;
import es.code.cdr.event.PermissionEvent;
import es.code.cdr.event.PermissionListener;
import es.code.repository.IProvider;
import es.code.repository.util.Path;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 20/12/2007
 *
 */
public class PermissionsManager implements Serializable {

	private static final long serialVersionUID = 3304323707292777241L;

	/** PermissionsManager Logger instance. */
	static final Logger LOGGER = Logger.getLogger( PermissionsManager.class.getName() );

	// Manages the listener list.
	private EventListenerAggregate listeners;
	/** Permission list. */
	private List<Permission> permissions = new ArrayList<Permission>();

	/**
	 * Constructor.
	 * 
	 * @throws DeploymentException 
	 */
	public PermissionsManager(Principal principal) {
		try {
			load( Utils.getAuthPrincipal( principal ) );
		} catch (DeploymentException e) {
			LOGGER.severe( e.getMessage() );
			throw new AbortProcessingException( e );
		}
	}

	/**
	 * Add a PermissionListener to the listener list.
	 * 
	 * @param l
	 */
	public void addPermissionsListener(PermissionListener l) {
		if (l == null) {
		    return;
		}
		if (listeners == null) {
			listeners = new EventListenerAggregate( PermissionListener.class );
		}
		listeners.add( l );
	}

	/**
	 * Remove a PermissionListener from the listener list.
	 * 
	 * @param l
	 */
	public void removePermissionsListener(PermissionListener l) {
		if (l == null) {
		    return;
		}
		if (listeners == null) {
			return;
		}
		listeners.remove( l );
	}

	/**
	 * Gets the permission list.
	 * 
	 * @return
	 */
	public List<Permission> getPermissions() {
		return this.permissions;		
	}

	/**
     * Fire a PermissionEvent to any registered listeners.
	 */
	public void firePermissionSelected(Permission permission) {
		if ( listeners != null ) {
			Object[] list = listeners.getListenersInternal();
			for (int i = 0; i < list.length; i++) {
				PermissionListener l = (PermissionListener)list[i];
				l.permissionAdded( new PermissionEvent( this, permission ) );
			}
		}
	}

	/**
	 * Loads profile mappings properties file.
	 *  
	 * @param principal
	 * @throws DeploymentException 
	 */
	private void load(AuthPrincipal principal) throws DeploymentException {
		Properties profilemappings = new Properties();
		try {
			DomainManager dm = new DomainManager( principal );
			InputStream is = 
				Path.getResource( System.getProperty( IProvider.PROFILE_MAPPINGS_FILE_KEY ), IProvider.PROFILE_MAPPINGS, "" ).openStream();
			profilemappings.load( is );
			Enumeration<Object> e = profilemappings.keys();
			while (e.hasMoreElements()) {
				Object key = e.nextElement();
				String users = dm.getUsers( null, (String) key );
				Permission p = new Permission( key, (String) profilemappings.get( key ), users );
				this.permissions.add( p );
			}
		} catch (IOException e) {
		}
	}
}
