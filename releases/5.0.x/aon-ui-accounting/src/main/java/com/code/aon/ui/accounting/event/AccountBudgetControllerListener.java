package com.code.aon.ui.accounting.event;

import com.code.aon.accounting.AccountBudget;
import com.code.aon.accounting.Period;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.accounting.controller.AccountBudgetController;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
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
		AccountBudgetController c = (AccountBudgetController) getController(); 
		c.setCredit(null);
		c.setDebit(null);
		AccountBudget ab = (AccountBudget) c.getTo(); 
		try {
			Period period;
			period = AccountingPeriodUtil.getDefaultPeriod();
			if (period != null) {
				ab.setPeriod(period.getId());
			}
		} catch (ManagerBeanException e) {
			ab.setPeriod(null);
		}
	}
	
	@Override
	public void beforeModelSearched(ControllerEvent event)
			throws ControllerListenerException {
		//((AccountBudgetController)getController()).calculateCreditDebitLists();
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		((AccountBudgetController)getController()).calculateCreditDebitLists();
	}
	
	@Override
	public void beforeEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		((AccountBudgetController)getController()).setAccount(null);
	}
		
}