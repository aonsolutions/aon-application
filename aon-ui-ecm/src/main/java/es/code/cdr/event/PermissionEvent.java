/**
 * 
 */
package es.code.cdr.event;

import java.util.EventObject;

import es.code.cdr.beans.Permission;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 19/12/2007
 *
 */
public class PermissionEvent extends EventObject {

	private static final long serialVersionUID = 193619937652322949L;

	/** New permission value. */
	private Permission permission;

	public PermissionEvent(Object source, Permission permission) {
		super(source);
		this.permission = permission;
	}

	/**
	 * @return the p
	 */
	public Permission getPermission() {
		return permission;
	}

}
