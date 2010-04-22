package com.code.aon.ui.cms.controller;

import java.io.File;

import javax.faces.context.FacesContext;

import com.code.aon.ui.cms.util.ControllerUtil;

public class CmsController {

    private static final String DESIGNER_ROLE = "Designer";
    private static final String EDITOR_ROLE = "Editor";
    private static final String REVISOR_ROLE = "Revisor";
    private static final String PUBLISHER_ROLE = "Publisher";
    	
	private boolean adminProfile;
	
	private boolean fileManager;

	public void assignAdminProfile( boolean fileManager ) {
		this.adminProfile = true;
		this.fileManager = fileManager;
	}
	
    public boolean isAdministrator() {
		return adminProfile;
    }
    
    public boolean isFileManager() {
		return fileManager;
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
 
	public File getCurrentTemplatePath() {
		return ControllerUtil.getCurrentTemplatePath();
	}	
}