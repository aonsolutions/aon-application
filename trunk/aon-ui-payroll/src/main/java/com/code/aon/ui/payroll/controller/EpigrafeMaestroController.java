package com.code.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;

public class EpigrafeMaestroController extends BasicController {

	public void onExit(ActionEvent event) {
		// TODO Auto-generated method stub
	}

	public void addDirectEqualExpression(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null) {
			String property = event.getComponent().getId().replace('_', '.');
			getCriteria().addEqualExpression(property, event.getNewValue());
		}
	}			
	
}
