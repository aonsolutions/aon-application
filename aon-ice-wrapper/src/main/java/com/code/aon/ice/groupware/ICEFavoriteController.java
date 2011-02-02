package com.code.aon.ice.groupware;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.ui.groupware.controller.FavoriteController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICEFavoriteController extends FavoriteController {
	
	private boolean disableOnSelect;

	public void onSelect(RowSelectorEvent event){
		if(!this.disableOnSelect){
			this.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "favorite_form");
		}
		this.disableOnSelect = false;
	}
	
	@SuppressWarnings("unused")
	public void onSelectUrl(ActionEvent event){
		this.disableOnSelect = true;
	}
}
