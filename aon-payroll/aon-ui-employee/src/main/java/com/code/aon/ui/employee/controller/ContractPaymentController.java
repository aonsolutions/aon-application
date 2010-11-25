package com.code.aon.ui.employee.controller;

import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.employee.ContractPayment;
import com.code.aon.ui.form.LinesController;

public class ContractPaymentController extends LinesController {
	
	// TODO este método de resolución de las percepciones es muy básico.
	// se recalcula la nomina entera cada vez, por lo que esta NO es manera
	public double getAmount(){
		double amount = 0;
		try {
			ContractPayment cp = (ContractPayment) getModel().getRowData();
			if (NumberUtils.isNumber(cp.getFunction()) ) {
				amount = NumberUtils.toDouble(cp.getFunction()) ;	
			} else {
				amount = 0.0;
			}
		} catch (ManagerBeanException e) {
			
		}
		return amount;
	}

}
