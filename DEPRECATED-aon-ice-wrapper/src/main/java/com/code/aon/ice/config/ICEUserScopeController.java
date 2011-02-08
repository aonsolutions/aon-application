package com.code.aon.ice.config;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.config.controller.UserScopeController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICEUserScopeController extends UserScopeController {

	public void onSelect(RowSelectorEvent event){
		super.onSelect(new ActionEvent(event.getComponent()));
	}
}
