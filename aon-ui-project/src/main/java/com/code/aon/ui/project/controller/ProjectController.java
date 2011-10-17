package com.code.aon.ui.project.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.project.Project;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.stat.controller.ProjectStatEngineController;
import com.code.aon.ui.util.AonUtil;

public class ProjectController extends BasicController {

	public void onProjectHistory(ActionEvent event) {
		ProjectStatEngineController statController =(ProjectStatEngineController)AonUtil.getRegisteredBean("projectStat");
		statController.setProject( (Project) this.getTo());
		statController.getProjectData();
		statController.setBackAction(IProjectConstants.PROJECT_FORM_PAGE);
	}
	
}
