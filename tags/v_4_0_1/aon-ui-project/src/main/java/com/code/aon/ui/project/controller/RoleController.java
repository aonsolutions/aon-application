package com.code.aon.ui.project.controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

public class RoleController {

	public static final String MONITOR_ROLE = "MonitorTareas";
	public static final String MANAGER_ROLE = "Manager";
	public static final String USER_ROLE = "User";

	public boolean isMonitorRole() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.isUserInRole(MONITOR_ROLE);
	}

	public boolean isManagerRole() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.isUserInRole(MANAGER_ROLE);
	}

	public boolean isUserRole() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		return ec.isUserInRole(USER_ROLE);
	}

}
