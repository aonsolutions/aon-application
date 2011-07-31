package com.esferalia.aon.ui.payroll.event.salary.draft;

import java.util.Calendar;
import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;

public class SalaryDraftControllerListener extends ControllerAdapter{
	

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		if(controller().getSalaryType()==null){
			controller().setSalaryType(SalaryType.SALARY);
		}
		controller().checkValidDraftPeriod();
		if(controller().isValidSalaryDraftPeriod()){
			searchSavedSalary(event);
		}
		resetSalary(event);
	}
	@Override
	public void afterBeanCanceled(ControllerEvent event) throws ControllerListenerException {
		resetSalary(event);
	}
	@Override
	public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
		resetSalary(event);
		controller().setIssueDate(new Date());
	}
	@Override
	public void beforeEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		controller().setYear(Calendar.getInstance().get(Calendar.YEAR));
		controller().setMonth(Month.getMonthByValue(Calendar.getInstance().get(Calendar.MONTH)));
	}
	
    private void resetSalary(ControllerEvent event) {
    	controller().setSalary(null);
    }
    private void searchSavedSalary(ControllerEvent event) {
    	controller().searchSavedDraftSalary();
    }
    
    private SalaryDraftController controller() {
    	return (SalaryDraftController) this.getController();
    }
    
    
}
