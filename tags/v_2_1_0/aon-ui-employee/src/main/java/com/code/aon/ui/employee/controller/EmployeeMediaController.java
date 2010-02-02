package com.code.aon.ui.employee.controller;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;

public class EmployeeMediaController extends BasicController {
	
    @Override
    public void onReset(ActionEvent event) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding("#{employeeAddress}");
        BasicController addressController = (BasicController)vb.getValue(ctx);
        addressController.onCancel(event);

        super.onReset(event);
    }

    @Override
    public void onSelect(ActionEvent event) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ValueBinding vb = ctx.getApplication().createValueBinding("#{employeeAddress}");
        BasicController addressController = (BasicController)vb.getValue(ctx);
        addressController.onCancel(event); 

        super.onSelect(event);
    }

}