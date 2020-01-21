package com.code.aon.ui.commercial.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.stat.controller.ProjectStatEngineController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;

public class ProjectCommercialController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
	
	@Override
	public void onSearch(ActionEvent event) {
		fixProjectCommercial();
		super.onSearch(event);
	}

	public void onProjectHistory(ActionEvent event) {
		ProjectStatEngineController statController =(ProjectStatEngineController)AonUtil.getRegisteredBean("projectStat");
		statController.setProject(((ProjectCommercial)this.getTo()).getProject());
		statController.initializeProjectData();
	}

	public void fixProjectCommercial() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		String domainName = ds.getDomainNameURL();
		String user = ds.getCurrentUser();
		Integer domainId = ds.getDomainId();
		AON.fixProjectCommercial(domainName, domainId, user);
	}
}
