package com.code.aon.ice.registry;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.ui.config.controller.UserController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICEUserController extends UserController {

	public void onSelect(RowSelectorEvent event){
		super.onSelect(new ActionEvent(event.getComponent()));
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "user_form");
	}
}