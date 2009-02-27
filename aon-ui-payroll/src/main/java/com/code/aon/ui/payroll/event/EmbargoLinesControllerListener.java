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
			ebc.setCriteria(this.getController().getCriteria());
			ebc.onSelect(null);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
