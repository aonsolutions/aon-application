package com.code.aon.ui.accounting.controller;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ui.form.LinesController;

public class AccountBudgetDetailController extends LinesController {
	
	private Double creditTotal;
	private Double debitTotal;
	
	/**
	 * Devuelve un array de nombres de mes
	 * @return
	 */
	public Month[] getMonthNames(){
		return Month.values();
	}

	public Double getCreditTotal() {
		return creditTotal;
	}

	public void setCreditTotal(Double creditTotal) {
		this.creditTotal = creditTotal;
	}

	public Double getDebitTotal() {
		return debitTotal;
	}

	public void setDebitTotal(Double debitTotal) {
		this.debitTotal = debitTotal;
	}
		
	
}