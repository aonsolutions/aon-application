package com.code.aon.ice.project;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.project.controller.DossierController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICEDossierController extends DossierController {

	public void onSelect(RowSelectorEvent event){
		super.onSelect(new ActionEvent(event.getComponent()));
	}
}
