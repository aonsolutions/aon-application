package com.code.aon.aio.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.faces.controller.IRichConstants;
import com.code.aon.faces.controller.SelectedMenuController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.gwt.document.server.DocumentsServlet;

public class ServiConveniosController {
	public static Boolean serviConvenios;

	public static Boolean getServiConvenios() {
		return serviConvenios;
	}

	public static void setServiConvenios(Boolean serviConvenios) {
		SelectedMenuController smc = (SelectedMenuController) AonUtil.getRegisteredBean(IRichConstants.SELECTED_MENU_CONTROLLER_NAME);
		smc.setLastMenuAction("gwt_documents");
		DocumentsServlet.setServiconvenios(serviConvenios);
		ServiConveniosController.serviConvenios = serviConvenios;
	}
	
	public void onServiconvenios(ActionEvent event){
		setServiConvenios(true);
	}
	public void onNotServiconvenios(ActionEvent event){
		setServiConvenios(false);
	}
	
}
