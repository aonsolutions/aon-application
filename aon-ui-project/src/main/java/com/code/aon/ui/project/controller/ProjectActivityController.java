package com.code.aon.ui.project.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.Project;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class ProjectActivityController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public List<SelectItem> getActivityTypes() throws ManagerBeanException {
		ProjectCollectionsController pcc = (ProjectCollectionsController) 
			AonUtil.getRegisteredBean( IProjectConstants.PROJECT_COLLECTIONS_CONTROLLER_NAME );
		Project project = (Project) getMasterController().getTo();
		return pcc.getActivityTypes( project.getProjectType() == null?null:project.getProjectType().getId() );
	}
	
}
