package com.code.aon.ui.config.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.config.BankAccount;
import com.code.aon.config.Bank;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class BankAccountValidationListener extends ControllerAdapter {

	private static final String CONFIG_BUNDLE = "configBundle";
	private static final String ERROR_MESSAGE = "config_invalid_bank_account";

	private boolean nullable;

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IBankAccountContainer bac = (IBankAccountContainer) event.getController().getTo();
		checkBankAccount(bac, nullable);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		IBankAccountContainer bac = (IBankAccountContainer) event.getController().getTo();
		checkBankAccount(bac, nullable);
	}

	public static void checkBankAccount(IBankAccountContainer bac, boolean nullable) throws ControllerListenerException {
		Bank bank = bac.getBank();
		BankAccount bankAccount = bac.getBankAccount();
		if (!nullable) {
			if (bank == null) {
				String msg = AonUtil.getMessage(CONFIG_BUNDLE, ERROR_MESSAGE);
				throw new ControllerListenerException(msg);
			}
			if (bankAccount == null) {
				String msg = AonUtil.getMessage(CONFIG_BUNDLE, ERROR_MESSAGE);
				throw new ControllerListenerException(msg);
			}
		}

		if (bank != null && bankAccount != null) {
			bankAccount.setEntity(bank.getCode());	
		}
		if (bankAccount != null && !StringUtils.isEmpty(bankAccount.getEntity()) && !bankAccount.isValid()) {
			String msg = AonUtil.getMessage(CONFIG_BUNDLE, ERROR_MESSAGE);
			throw new ControllerListenerException(msg);
		}
	}

}