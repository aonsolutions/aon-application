package com.code.aon.ui.project.controller.event;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.project.controller.ProjectController;

public class ProjectControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ProjectController pc = (ProjectController) event.getController();
		if (pc.getGraphTabId().equals(pc.getSelectedTab())) {
			pc.onGraphicTab(null);	
		}
		if (pc.getGanttTabId().equals(pc.getSelectedTab())) {
			pc.onProjectHistory(null);
		}
	}
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectController pc = (ProjectController) event.getController();
		if (pc.getGraphTabId().equals(pc.getSelectedTab())) {
			pc.onGraphicTab(null);	
		}
		if (pc.getGanttTabId().equals(pc.getSelectedTab())) {
			pc.onProjectHistory(null);
		}
	}
	
}