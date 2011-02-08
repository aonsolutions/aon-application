package com.code.aon.ice.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICEController extends BasicController {

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event){
		super.onSelect(new ActionEvent(event.getComponent()));
	}
}
