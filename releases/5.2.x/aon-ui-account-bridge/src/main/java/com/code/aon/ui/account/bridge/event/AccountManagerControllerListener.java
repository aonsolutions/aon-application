package com.code.aon.ui.account.bridge.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.ui.account.bridge.controller.AccountManager;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountManagerControllerListener extends ControllerAdapter {
	private static final String CUSTOMER_TAB = "customer";
	private static final String CREDITOR_TAB = "creditor";
	private static final String SUPPLIER_TAB = "supplier";
	private static final String BANK_TAB = "bank";
	private static final String TAX_TAB = "tax";
	private static final String LOAN_TAB = "loan";

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		AccountManager am = (AccountManager) event.getController();
		Account account = (Account) am.getTo();
		String id = account.getId();
		if (StringUtils.startsWith(id, AccountConstants.CUSTOMER_ACCOUNT_PREFIX)) {
			am.setSelectedTab(CUSTOMER_TAB);
		} else if (StringUtils.startsWith(id, AccountConstants.CREDITOR_ACCOUNT_PREFIX)) {
			am.setSelectedTab(CREDITOR_TAB);
		} else if (StringUtils.startsWith(id, AccountConstants.SUPPLIER_ACCOUNT_PREFIX)) {
			am.setSelectedTab(SUPPLIER_TAB);
		} else if (StringUtils.startsWith(id, AccountConstants.BANK_ACCOUNT_PREFIX)) {
			am.setSelectedTab(BANK_TAB);
		} else if (StringUtils.startsWith(id, "47")) {
			am.setSelectedTab(TAX_TAB);
		} else if (StringUtils.startsWith(id, AccountConstants.SHORT_TERM_LOAN_ACCOUNT_PREFIX)) {
			am.setSelectedTab(LOAN_TAB);
		}
	}

}