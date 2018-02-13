package com.code.aon.ui.project.controller;

import java.sql.Connection;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.project.Project;
import com.code.aon.stat.engine.ProjectStatEngine;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.stat.controller.ProjectStatEngineController;
import com.code.aon.ui.util.AonUtil;

import net.aonsolutions.core.dbutils.DatabaseUtil;

public class ProjectController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String GANTT_TAB_ID  = "project_gantt_tab";
	private static final String GRAPH_TAB_ID  = "project_graph_tab";
	
	private String selectedTab;

	public String getSelectedTab() {
		return selectedTab;
	}
	public String getGanttTabId() {
		return GANTT_TAB_ID;	
	}
	public String getGraphTabId() {
		return GRAPH_TAB_ID;	
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public void onProjectHistory(ActionEvent event) {
		ProjectStatEngineController statController =(ProjectStatEngineController)AonUtil.getRegisteredBean("projectStat");
		statController.setProject( (Project) this.getTo());
		statController.initializeProjectData();
		statController.setSelectedTab(null);
		statController.setBackAction(IProjectConstants.PROJECT_FORM_PAGE);
	}

	public void onGraphicTab(ActionEvent event) {
		ProjectStatEngineController statController =(ProjectStatEngineController)AonUtil.getRegisteredBean("projectStat");
		Project project = (Project) this.getTo(); 
		Project statProject = statController.getProject();
		if (!project.equals(statProject)) {
			statController.setProject( (Project) this.getTo());
			statController.initializeProjectData();
		}
		statController.setBackAction(IProjectConstants.PROJECT_FORM_PAGE);
	}
	
	public String getLastAlias() {
		Connection c = null;
		String alias = "";
		try {
			c = DatabaseUtil.getConnection(AonUtil.getDomainName());
			ProjectStatEngine engine = new ProjectStatEngine();
			Project p = (Project) getTo();
			Integer pt = p.getProjectType() != null ? p.getProjectType().getId() : null;
			alias = engine.getLastProjectAlias(c, pt);
		}catch (Exception e) {
			e.printStackTrace();
		} 
		return isNevv() && alias != null && !"".equals(alias) && !" ".equals(alias)? "(Último expediente: " + alias + ")" : "";
	}
}
