package com.code.aon.ui.employee.event;

import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.employee.ContractPayment;
import com.code.aon.ui.employee.controller.SalaryDraftController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.salary.ISalary;

public class SalaryDraftPaymentControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		LinesController l = (LinesController) event.getController();
		SalaryDraftController sc =  (SalaryDraftController) l.getMasterController();
		ISalary salary = sc.getSalary();
		Date issueDate = salary.getIssueDate();
		ContractPayment cp = (ContractPayment) l.getTo();
		cp.setStartDate(CommonUtil.getMonthFirstDay(issueDate));
		cp.setEndDate(CommonUtil.getMonthLastDay(issueDate));
		cp.setMonth(Month.getMonthByValue(CommonUtil.getMonth(issueDate)));
	}
	
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
