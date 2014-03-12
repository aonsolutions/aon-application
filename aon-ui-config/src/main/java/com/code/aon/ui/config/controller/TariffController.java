package com.code.aon.ui.config.controller;

import com.code.aon.common.AonVersion;
import com.code.aon.ui.form.BasicController;

public class TariffController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String selectedTab;
	private boolean showNewCatalogueWindow;
	private boolean showNewAddInfoWindow;

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public boolean isShowNewCatalogueWindow() {
		return showNewCatalogueWindow;
	}

	public void setShowNewCatalogueWindow(boolean showNewCatalogueWindow) {
		this.showNewCatalogueWindow = showNewCatalogueWindow;
	}

	public boolean isShowNewAddInfoWindow() {
		return showNewAddInfoWindow;
	}

	public void setShowNewAddInfoWindow(boolean showNewAddInfoWindow) {
		this.showNewAddInfoWindow = showNewAddInfoWindow;
	}

}