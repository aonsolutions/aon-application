package com.code.aon.ice.groupware;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.ui.groupware.controller.NoteController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICENoteController extends NoteController {

	public void onSelect(RowSelectorEvent event){
		this.onSelect(new ActionEvent(event.getComponent()));
		FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "note_form");
	}
}