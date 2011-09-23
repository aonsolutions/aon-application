package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

public interface IVariablesHandler {
	
	public void initializeVariables(ActionEvent event);
	public List<?> expressionContext(Object suggest); 
	public IManagerBean getVariableManagerBean() throws ManagerBeanException;
	public void resetVariable();


}
