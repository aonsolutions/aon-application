package com.code.aon.ui.tas.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;

public class AppraiserController extends BasicController {
	
    @SuppressWarnings("unused")
    public void onReset(MenuEvent event) {
        this.onReset((ActionEvent)event);
    }
}