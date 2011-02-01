package com.code.aon.ui.employee.event;

import java.util.Date;

import com.code.aon.ui.employee.controller.SalaryDraftController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SalaryDraftControllerListener extends ControllerAdapter{
	

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		resetSalary(event);
	}
	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		resetSalary(event);
	}
	@Override
	public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
		resetSalary(event);
		SalaryDraftController sc = (SalaryDraftController) event.getController();
		sc.setIssueDate(new Date());
	}
	
    private void resetSalary(ControllerEvent event) {
    	SalaryDraftController sc = (SalaryDraftController) event.getController();
		sc.setSalary(null);
    }
    
}
