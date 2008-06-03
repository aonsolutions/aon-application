package com.code.aon.ui.employee.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class EmployeeMediaController extends BasicController {
	
	private static final long serialVersionUID = -6505795941584003674L;
	static final String MANAGER_BEAN_NAME = "employeeMedia";

	@Override
    public void onReset(ActionEvent event) {
        IController addressController = AonUtil.getController( EmployeeAddressController.MANAGER_BEAN_NAME );
        addressController.onCancel(event);
        super.onReset(event);
    }

    @Override
    public void onSelect(ActionEvent event) {
        IController addressController = AonUtil.getController( EmployeeAddressController.MANAGER_BEAN_NAME );
        addressController.onCancel(event); 
        super.onSelect(event);
    }

}