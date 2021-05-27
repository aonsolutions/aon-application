package com.code.aon.ui.config.controller;

import com.code.aon.AonVersion;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.form.BasicController;

public class TaxController extends BasicController implements IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean showAuditInfoWindow;

	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}	

}
