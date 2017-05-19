package com.code.aon.aio.controller;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.faces.controller.IRichConstants;
import com.code.aon.faces.controller.SelectedMenuController;
import com.code.aon.ui.util.AonUtil;


public class SelectedMenuGwtController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static void onDocumental(ActionEvent event) {
		SelectedMenuController smc = (SelectedMenuController) AonUtil.getRegisteredBean(IRichConstants.SELECTED_MENU_CONTROLLER_NAME);
		smc.setLastMenuAction("gwt_documents");
	}
	
	public void onIssues(ActionEvent event){
		SelectedMenuController smc = (SelectedMenuController) AonUtil.getRegisteredBean(IRichConstants.SELECTED_MENU_CONTROLLER_NAME);
		smc.setLastMenuAction("gwt_issues");
	}
	
}
