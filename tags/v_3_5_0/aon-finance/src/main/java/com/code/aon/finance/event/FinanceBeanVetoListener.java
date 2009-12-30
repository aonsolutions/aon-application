package com.code.aon.finance.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.BankAccount;
import com.code.aon.finance.Finance;

public class FinanceBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Finance finance = (Finance)evt.getTo();
		checkFinance(finance);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Finance finance = (Finance)evt.getTo();
		checkFinance(finance);
	}
	
	private void checkFinance(Finance finance) throws ManagerBeanVetoListenerException {
		if(finance.getAmount() == 0){
			throw new ManagerBeanVetoListenerException("El importe del vencimiento no puede ser 0.0");
		}
		BankAccount bankAccount = finance.getBankAccount();
		if (bankAccount != null &&
			(bankAccount.getOffice() != null ||
			bankAccount.getEntity() != null ||
			bankAccount.getControl() != null ||
			bankAccount.getAccount() != null) &&
			!bankAccount.isValid()) {
			throw new ManagerBeanVetoListenerException("La cuenta bancaria del vencimiento no es válida. Los digitos de control no coinciden.");
		}
		if (finance.getBank() != null && finance.getBank().getId() == null) {
			finance.setBank(null);
		}
	}
}