package com.code.aon.ui.finance.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.finance.Bank;
import com.code.aon.finance.BankAccount;
import com.code.aon.finance.IBankAccountContainer;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class BankAccountValidationListener extends ControllerAdapter {

	private final String FINANCE_BUNDLE = "financeBundle";
	private final String ERROR_MESSAGE = "finance_invalid_bank_account";
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IBankAccountContainer bac = (IBankAccountContainer) event.getController().getTo();
		checkBankAccount(bac);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		IBankAccountContainer bac = (IBankAccountContainer) event.getController().getTo();
		checkBankAccount(bac);
	}

	private void checkBankAccount(IBankAccountContainer bac) throws ControllerListenerException {
		BankAccount bankAccount = bac.getBankAccount();
		if (bankAccount == null) {
			String msg = AonUtil.getMessage(FINANCE_BUNDLE, ERROR_MESSAGE);
			throw new ControllerListenerException(msg);
		}
		if (StringUtils.isEmpty(bankAccount.getEntity())) {
			Bank bank = bac.getBank();
			if (bank == null) {
				String msg = AonUtil.getMessage(FINANCE_BUNDLE, ERROR_MESSAGE);
				throw new ControllerListenerException(msg);
			}
			bankAccount.setEntity( bank.getCode());	
		}
		if (!bankAccount.isValid()) {
			String msg = AonUtil.getMessage(FINANCE_BUNDLE, ERROR_MESSAGE);
			throw new ControllerListenerException(msg);
		}
	}


}