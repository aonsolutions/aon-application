package com.code.aon.ui.accounting.event;

import com.code.aon.ui.accounting.controller.AccountBudgetController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountBudgetControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		((AccountBudgetController)getController()).initializeValueList();
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		((AccountBudgetController)getController()).calculateCreditDebitTotals();
	}
}