package com.code.aon.ui.employee.controller;

import java.util.Collection;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.employee.Payments;
import com.code.aon.employee.SalaryPayment;
import com.code.aon.ui.form.LinesController;

public class SalaryPaymentController extends LinesController{
	
	private Payments payments;

	@SuppressWarnings("unchecked")
	public Payments getPayments() {
		try {
			if (payments == null) {
				Collection<SalaryPayment> list = (List<SalaryPayment>) getModel().getWrappedData();
				payments = new Payments(list);
			}
		} catch (ManagerBeanException e) {
			
		}
		return payments;
	}

	public void setPayments(Payments payments) {
		this.payments = payments;
	}

}
