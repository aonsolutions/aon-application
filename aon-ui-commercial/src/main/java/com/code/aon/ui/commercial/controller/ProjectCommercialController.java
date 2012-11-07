package com.code.aon.ui.commercial.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.stat.controller.ProjectStatEngineController;
import com.code.aon.ui.util.AonUtil;

public class ProjectCommercialController extends BasicController {

	private String selectedTab;
	private boolean showNewTrackingWindow;

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public boolean isShowNewTrackingWindow() {
		return showNewTrackingWindow;
	}

	public void setShowNewTrackingWindow(boolean value) {
		this.showNewTrackingWindow = value;
	}

	public void onProjectHistory(ActionEvent event) {
		ProjectStatEngineController statController =(ProjectStatEngineController)AonUtil.getRegisteredBean("projectStat");
		statController.setProject(((ProjectCommercial)this.getTo()).getProject());
		statController.initializeProjectData();
	}

}
