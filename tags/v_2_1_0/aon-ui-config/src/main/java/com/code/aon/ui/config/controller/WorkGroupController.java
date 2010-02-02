package com.code.aon.ui.config.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

public class WorkGroupController extends BasicController {

	public void onSearch(MenuEvent event) {
		this.onSearch((ActionEvent)event);
	}
}
