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
	
	@Override
	public void init() {
		super.init();
		setUserInRole(IAonRole.DOCUMENT, true);
	}

	@Override
	public boolean isUserInRole(String role) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.isUserInRole(role);
	}

}
