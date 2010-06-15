package com.code.aon.ui.payroll.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.payroll.controller.EmbargoBasicController;
import com.code.aon.ui.payroll.controller.IPayrollConstants;

public class EmbargoLinesControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		
		EmbargoBasicController ebc = (EmbargoBasicController)FormUtil.getController(IPayrollConstants.EMBARGO_BASIC_CONTROLLER_NAME);
		try {
			ebc.setModel(this.getController().getModel());
			/*
			ebc.setCriteria(this.getController().getCriteria());
			ebc.onSearch(null);
			ebc.getModel().setRowIndex(this.getController().getModel().getRowIndex());
			*/
			ebc.onSelect(null);
			
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void afterBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		// TODO Auto-generated method stub
		super.afterBeanReset(event);
	}
}
