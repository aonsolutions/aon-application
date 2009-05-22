package com.code.aon.ui.accounting.event;

import com.code.aon.ui.accounting.controller.AccountBudgetController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountBudgetDetailControllerListener extends ControllerAdapter {

	private final String ACCOUNTBUDGET_CONTROLLER_NAME = "accountBudget";
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		((AccountBudgetController)FormUtil.getController(ACCOUNTBUDGET_CONTROLLER_NAME)).calculateCreditDebitTotals();
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		((AccountBudgetController)FormUtil.getController(ACCOUNTBUDGET_CONTROLLER_NAME)).calculateCreditDebitTotals();
	}
	
}