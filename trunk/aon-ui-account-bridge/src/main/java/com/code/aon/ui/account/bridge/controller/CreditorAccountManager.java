package com.code.aon.ui.account.bridge.controller;


import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Creditor;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class CreditorAccountManager extends LinesController {

	public void creditorChanged(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Creditor creditor = (Creditor)event.getNewValue();
			((CreditorAccount) this.getTo()).setCreditor(creditor);
		}
	}

}
