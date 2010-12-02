package com.code.aon.ui.employee.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractDeduction;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;

public class ContractDeductionController extends LinesController {
	
	// TODO este método de resolución de las deducciones es muy básico.
	// se recalcula la nomina entera cada vez, por lo que esta NO es manera  
	public double getAmount(){
		double amount = 0;
		Contract c  = (Contract) getMasterController().getTo();
		ISalary s;
		try {
			s = c.getSalary();
			ContractDeduction cd = (ContractDeduction) getModel().getRowData();
			if (NumberUtils.isNumber(cd.getExpression()) ) {
				amount = NumberUtils.toDouble(cd.getExpression()) ;	
			} else {
				if (StringUtils.endsWith(cd.getExpression(), "%")) {
					String func = StringUtils.stripEnd(cd.getExpression(), "%");
					if (NumberUtils.isNumber(func) ) {
						double percent = NumberUtils.toDouble(func);
						double totalPayments = s.getPayments().getTotal();
						amount = CommonUtil.round(totalPayments * percent / 100 ) ;
					}
				}
			}
		} catch (SalaryException e) {
			
		} catch (ManagerBeanException e) {
			
		}
		
		return amount;
	}

}
