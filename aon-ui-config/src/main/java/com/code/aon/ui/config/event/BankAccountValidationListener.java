package com.code.aon.ui.config.event;

import static com.code.aon.ui.common.ICommonMessages.CONFIG_INCORRECT_BANK_ACCOUNT;
import static com.code.aon.ui.common.ICommonMessages.CONFIG_INCORRECT_IBAN;
import static com.code.aon.ui.common.ICommonMessages.CONFIG_INCORRECT_IBAN_LENGTH;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class BankAccountValidationListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
		if (StringUtils.isEmpty(bac.getBankAccount().getBban())) {
			bac.setBankAccount(null);
			bac.setBankAlias(null);
			bac.setBic(null);
		}

		if (!nullable && bac.getBankAccount() == null) {
			throw new ControllerListenerException(AonUtil.getMessage(CONFIG_INCORRECT_IBAN));
		}

		if (bac.getBankAccount() != null && !bac.getBankAccount().isValidBankAccount()) {
			if (!bac.getBankAccount().isValidIbanLength()) {
				throw new ControllerListenerException(AonUtil.getMessage(CONFIG_INCORRECT_IBAN_LENGTH));
			} else if (!bac.getBankAccount().isValidBban()) {
				throw new ControllerListenerException(AonUtil.getMessage(CONFIG_INCORRECT_BANK_ACCOUNT));
			} else {
				throw new ControllerListenerException(AonUtil.getMessage(CONFIG_INCORRECT_IBAN));
			}
		}
	}

}