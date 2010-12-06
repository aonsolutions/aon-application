package com.code.aon.ui.employee.event;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.employee.ContractDeduction;
import com.code.aon.ui.employee.controller.SalaryDraftController;
import com.code.aon.ui.employee.controller.SalaryDraftDeductionController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SalaryDraftDeductionControllerListener extends ControllerAdapter{
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		SalaryDraftDeductionController c =  (SalaryDraftDeductionController) event.getController();
		SalaryDraftController master = (SalaryDraftController) c.getMasterController();
		ContractDeduction cp = (ContractDeduction) c.getTo();
		cp.setStartDate( master.getStartDate() );
		cp.setEndDate( master.getEndDate() );
		Month month = Month.getMonthByValue(CommonUtil.getMonth( master.getIssueDate()));
		cp.setMonth( month ); 
		c.reset(true);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		LinesController l = (LinesController) event.getController();
		SalaryDraftController sc =  (SalaryDraftController) l.getMasterController();
		ContractDeduction cp = (ContractDeduction) l.getTo();
		cp.setStartDate(sc.getStartDate());
		cp.setEndDate(sc.getEndDate());
		cp.setMonth(Month.getMonthByValue(CommonUtil.getMonth(sc.getIssueDate())));
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
