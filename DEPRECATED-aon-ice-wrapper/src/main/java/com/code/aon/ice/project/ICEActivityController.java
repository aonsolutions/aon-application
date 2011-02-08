package com.code.aon.ice.project;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.project.controller.ActivityController;
import com.icesoft.faces.component.ext.RowSelectorEvent;

public class ICEActivityController extends ActivityController {

	public void onSelect(RowSelectorEvent event) {
		super.onSelect(new ActionEvent(event.getComponent()));
	}
}
