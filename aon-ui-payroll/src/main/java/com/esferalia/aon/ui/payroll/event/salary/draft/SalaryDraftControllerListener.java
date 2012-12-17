package com.esferalia.aon.ui.payroll.event.salary.draft;

import java.util.Calendar;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.ui.payroll.controller.salary.draft.SalaryDraftController;

public class SalaryDraftControllerListener extends ControllerAdapter{
	
	@Override
	public void beforeEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		SalaryDraftController sc = (SalaryDraftController) event.getController();
		sc.setYear(Calendar.getInstance().get(Calendar.YEAR));
		sc.setMonth(Month.getMonthByValue(Calendar.getInstance().get(Calendar.MONTH)));
	}
    
}
