package com.code.aon.ui.employee.controller;

import java.util.Collection;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.employee.Deductions;
import com.code.aon.employee.SalaryDeduction;
import com.code.aon.ui.form.LinesController;

public class SalaryDeductionController extends LinesController{
	
	private Deductions deductions;

	@SuppressWarnings("unchecked")
	public Deductions getDeductions() {
		try {
			if (deductions == null) {
				Collection<SalaryDeduction> list = (List<SalaryDeduction>) getModel().getWrappedData();
				deductions = new Deductions(list);
			}
		} catch (ManagerBeanException e) {
			
		}
		return deductions;
	}

	public void setDeductions(Deductions deductions) {
		this.deductions = deductions;
	}

}
