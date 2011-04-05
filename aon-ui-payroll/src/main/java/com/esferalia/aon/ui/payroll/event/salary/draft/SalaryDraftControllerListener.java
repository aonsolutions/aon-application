package com.esferalia.aon.ui.payroll.event.salary.draft;

import java.util.Date;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;

public class SalaryDraftControllerListener extends ControllerAdapter{
	

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		resetSalary(event);
		searchSavedSalary(event);
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
    private void searchSavedSalary(ControllerEvent event) {
    	SalaryDraftController sc = (SalaryDraftController) event.getController();
		sc.searchSavedDraftSalary();
    }
    
}
