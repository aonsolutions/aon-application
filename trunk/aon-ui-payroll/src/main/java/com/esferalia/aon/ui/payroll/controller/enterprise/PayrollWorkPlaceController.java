package com.esferalia.aon.ui.payroll.controller.enterprise;

import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.payroll.PayrollWorkPlace;


public class PayrollWorkPlaceController extends LinesController {
	
	private PayrollWorkPlace payrollWorkPlace;

	public PayrollWorkPlace getPayrollWorkPlace() {
		return payrollWorkPlace;
	}

	public void setPayrollWorkPlace(PayrollWorkPlace payrollWorkPlace) {
		this.payrollWorkPlace = payrollWorkPlace;
	}
	    
	
}