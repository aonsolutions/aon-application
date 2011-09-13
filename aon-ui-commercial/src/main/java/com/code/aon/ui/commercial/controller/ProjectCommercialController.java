package com.code.aon.ui.commercial.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.stat.controller.ProjectStatEngineController;
import com.code.aon.ui.util.AonUtil;

public class ProjectCommercialController extends BasicController {

	public void onProjectHistory(ActionEvent event) {
		ProjectStatEngineController statController =(ProjectStatEngineController)AonUtil.getRegisteredBean("projectStat");
		statController.setProject(((ProjectCommercial)this.getTo()).getProject());
		statController.getProjectData();
	}

}
