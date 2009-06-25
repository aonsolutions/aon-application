package com.code.aon.ui.accounting.event;

import com.code.aon.ui.accounting.controller.AccountBudgetController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountBudgetControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		((AccountBudgetController)getController()).calculateCreditDebitTotals();
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		((AccountBudgetController)getController()).buildDetailValueList();
	}
	
	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		((AccountBudgetController)getController()).setCredit(null);
		((AccountBudgetController)getController()).setDebit(null);
	}
	
	@Override
	public void beforeModelSearched(ControllerEvent event)
			throws ControllerListenerException {
		((AccountBudgetController)getController()).calculateCreditDebitLists();
	}
		
}