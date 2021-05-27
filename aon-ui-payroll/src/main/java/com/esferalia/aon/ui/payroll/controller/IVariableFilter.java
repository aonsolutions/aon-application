package com.esferalia.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;


public interface IVariableFilter {
	

	public String getBeanName();
	
	public void reloadData(ActionEvent event);

	
}
