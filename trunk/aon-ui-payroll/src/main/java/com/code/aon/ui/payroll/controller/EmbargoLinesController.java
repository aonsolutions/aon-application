package com.code.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class EmbargoLinesController extends LinesController {

	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		
		updateBasicModel();
	}
	
	public void updateBasicModel(){
		EmbargoBasicController ebc = (EmbargoBasicController)FormUtil.getController(IPayrollConstants.EMBARGO_BASIC_CONTROLLER_NAME);
		try {
			ebc.setModel(this.getModel());
			ebc.onSelect(null);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
