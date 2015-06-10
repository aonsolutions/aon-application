package com.code.aon.ui.registry.controller;

import com.code.aon.AonVersion;
import com.code.aon.ui.config.BankAccountHelper;
import com.code.aon.ui.form.LinesController;

public class RegistryBankLinesController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private BankAccountHelper accountHelper;

	public RegistryBankLinesController() {
		this.accountHelper = new BankAccountHelper(this);
	}

	public BankAccountHelper getAccountHelper() {
		return accountHelper;
	}

}