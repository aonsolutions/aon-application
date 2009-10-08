package com.code.aon.ui.accounting.controller;

import com.code.aon.account.Account;
import com.code.aon.accounting.Period;
import com.code.aon.common.enumeration.Month;
import com.code.aon.ui.form.BasicController;

public class AccountBudgetController extends BasicController {

	private Account account;
	private Month month;
	private Period period;
	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}
	
}