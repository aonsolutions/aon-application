package com.code.aon.ice.config;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.config.controller.UserWorkGroupController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICEUserWorkGroupController extends UserWorkGroupController {

	public void onSelect(RowSelectorEvent event){
		super.onSelect(new ActionEvent(event.getComponent()));
	}
}
