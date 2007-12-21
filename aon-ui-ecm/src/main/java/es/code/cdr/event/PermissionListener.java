/**
 * 
 */
package es.code.cdr.event;

import java.util.EventListener;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 19/12/2007
 *
 */
public interface PermissionListener extends EventListener {

	void permissionAdded(PermissionEvent event);
}
