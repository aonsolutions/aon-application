package com.code.aon.aio.controller;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.faces.controller.IRichConstants;
import com.code.aon.faces.controller.SelectedMenuController;
import com.code.aon.ui.google.apis.controller.GoogleDriveController;
import com.code.aon.ui.util.AonUtil;


public class ServiConveniosController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static Boolean serviConvenios;

	public static Boolean getServiConvenios() {
		return serviConvenios;
	}

	public static void setServiConvenios(Boolean serviConvenios) {
		SelectedMenuController smc = (SelectedMenuController) AonUtil.getRegisteredBean(IRichConstants.SELECTED_MENU_CONTROLLER_NAME);
		smc.setLastMenuAction("gwt_documents");
		GoogleDriveController.setServiconvenios(serviConvenios);
		ServiConveniosController.serviConvenios = serviConvenios;
	}
	
	public void onServiconvenios(ActionEvent event){
		setServiConvenios(true);
	}
	public void onNotServiconvenios(ActionEvent event){
		setServiConvenios(false);
	}
	
	public void onIssues(ActionEvent event){
		SelectedMenuController smc = (SelectedMenuController) AonUtil.getRegisteredBean(IRichConstants.SELECTED_MENU_CONTROLLER_NAME);
		smc.setLastMenuAction("gwt_issues");
	}
	
}
