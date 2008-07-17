package com.code.aon.ui.cms.controller;

import javax.faces.context.FacesContext;

public class CmsController {

    public boolean isRoleDesigner() {
    	return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(DESIGNER_ROLE);
    }

    public boolean isRoleEditor() {
    	return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(EDITOR_ROLE);
    }

    public boolean isRoleRevisor() {
    	return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(REVISOR_ROLE);
    }

    public boolean isRolePublisher() {
    	return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(PUBLISHER_ROLE);
    }

    private static final String DESIGNER_ROLE = "Designer";
    private static final String EDITOR_ROLE = "Editor";
    private static final String REVISOR_ROLE = "Revisor";
    private static final String PUBLISHER_ROLE = "Publisher";
    
    
}
