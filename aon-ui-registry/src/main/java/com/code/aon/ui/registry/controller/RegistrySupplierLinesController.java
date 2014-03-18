package com.code.aon.ui.registry.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.config.util.BankUtil;
import com.code.aon.registry.RegistrySupplier;
import com.code.aon.ui.form.LinesController;

public class RegistrySupplierLinesController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	public void onBankAccountData(ActionEvent event) {
		BankUtil.fillBankAccountData((RegistrySupplier)getTo());
	}

}