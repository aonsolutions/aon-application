package com.code.aon.ui.employee.event;

import com.code.aon.ui.employee.controller.SalaryDraftController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.listener.LinesControllerListener;

public class SalaryDraftPaymentControllerListener extends LinesControllerListener{
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		resetSalary(event);
	}
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		resetSalary(event);
	}
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		resetSalary(event);
	}
    private void resetSalary(ControllerEvent event) {
		LinesController l = (LinesController) event.getController();
		SalaryDraftController sc =  (SalaryDraftController) l.getMasterController();
		sc.setSalary(null);
    }
    
}
