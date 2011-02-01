package com.code.aon.ui.finance.controller;

import com.code.aon.ui.form.BasicController;

public class FinanceMovementController extends BasicController implements IFinanceConstants {

	private Double balance;
	
	public Double getBalance() {
		return balance;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}
	
}