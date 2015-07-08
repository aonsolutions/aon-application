package com.code.aon.ui.groupware.controller;

import com.code.aon.AonVersion;


public class QuickIssueController extends NoticeController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public boolean showNewissueWindow;
	
	public boolean isShowNewissueWindow() {
		return showNewissueWindow;
	}

	public void setShowNewissueWindow(boolean showNewissueWindow) {
		this.showNewissueWindow = showNewissueWindow;
	}
	
	
}
