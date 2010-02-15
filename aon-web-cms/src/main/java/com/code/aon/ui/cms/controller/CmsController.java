package com.code.aon.ui.cms.controller;

import javax.faces.context.FacesContext;

public class CmsController {

	private boolean adminProfile = false;

	public void assignAdminProfile(){
		adminProfile = true;
	}
	
    public boolean isRoleDesigner() {
		if (adminProfile) return true;
    	return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(DESIGNER_ROLE);
    }

    public boolean isRoleEditor() {
		if (adminProfile) return true;
    	return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(EDITOR_ROLE);
    }

    public boolean isRoleRevisor() {
		if (adminProfile) return true;
    	return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(REVISOR_ROLE);
    }

    public boolean isRolePublisher() {
		if (adminProfile) return true;
    	return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(PUBLISHER_ROLE);
    }

    public boolean isAdministrator() {
		return adminProfile;
    }

    private static final String DESIGNER_ROLE = "Designer";
    private static final String EDITOR_ROLE = "Editor";
    private static final String REVISOR_ROLE = "Revisor";
    private static final String PUBLISHER_ROLE = "Publisher";
    
    
}
