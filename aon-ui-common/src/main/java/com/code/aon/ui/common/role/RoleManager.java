package com.code.aon.ui.common.role;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Clase que controla los roles habituales de las aplicaciones AON.
 * 
 * @author ecastellano
 * 
 */
public class RoleManager extends BasicRoleManager {
	
	/**
	 * @param role
	 *            The Role
	 * @return TRUE if user has role, false otherwise.
	 */
	public boolean isUserInRole(String role) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.isUserInRole(role);
	}

}
