package com.code.aon.ice.project;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.ui.project.controller.DossierTypeController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICEDossierTypeController extends DossierTypeController {

	public void onSelect(RowSelectorEvent event) {
		super.onSelect(new ActionEvent(event.getComponent()));
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "dossier_type_form");
	}
}
