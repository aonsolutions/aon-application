package com.code.aon.ui.company.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;

public class WorkActivityController extends BasicController {
	
	public static final String MANAGER_BEAN_NAME = "workactivity";

    /*
     * (non-Javadoc)
     * @see com.code.aon.ui.form.BasicController#onAccept(javax.faces.event.ActionEvent)
     */
	public void onAccept(ActionEvent event) {
		accept(event);
	}

}