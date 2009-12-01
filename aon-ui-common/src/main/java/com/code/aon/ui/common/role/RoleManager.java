package com.code.aon.ui.common.role;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * Clase que controla los roles habituales de las aplicaciones AON.
 * @author ecastellano
 *
 */
public class RoleManager {

	/**
	 * @return TRUE if user has IAonRole.TASK_MOPNITORING role, false otherwise.
	 */
	public boolean isTaskMonitor() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.isUserInRole(IAonRole.TASK_MONITORING);
	}

	/**
	 * @return TRUE if user has IAonRole.ADMIN role, false otherwise.
	 */
	public boolean isAdmin() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.isUserInRole(IAonRole.ADMIN);
	}

	/**
	 * @return TRUE if user has IAonRole.USER role, false otherwise.
	 */
	public boolean isUser() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.isUserInRole(IAonRole.USER);
	}

	/**
	 * @return TRUE if user has IAonRole.ACCOUNTING role, false otherwise.
	 */
	public boolean isAccounting() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.isUserInRole(IAonRole.ACCOUNTING);
	}
	
	/**
	 * @return TRUE if user has IAonRole.INVOICING role, false otherwise.
	 */
	public boolean isInvoicing() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.isUserInRole(IAonRole.INVOICING);
	}

	/**
	 * @return TRUE if user has IAonRole.CONFIDENTIALITY role, false otherwise.
	 */
	public boolean isConfidentiality() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.isUserInRole(IAonRole.CONFIDENTIALITY);
	}
	
}
