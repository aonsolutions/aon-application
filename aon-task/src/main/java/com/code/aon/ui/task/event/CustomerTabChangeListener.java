package com.code.aon.ui.task.event;

import javax.faces.event.AbortProcessingException;

import com.code.aon.ui.project.controller.DossierController;
import com.code.aon.ui.util.AonUtil;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.icesoft.faces.component.paneltabset.TabChangeListener;

public class CustomerTabChangeListener implements TabChangeListener {
	
	private static final String DOSSIER_CONTROLLER_NAME = "ICEDossier";

	public void processTabChange(TabChangeEvent tabChangeEvent) throws AbortProcessingException {
		if(tabChangeEvent.getNewTabIndex() == 1){
			DossierController dossierController = (DossierController)AonUtil.getController(DOSSIER_CONTROLLER_NAME);
			dossierController.onDossier(null);
		}
	}
}
