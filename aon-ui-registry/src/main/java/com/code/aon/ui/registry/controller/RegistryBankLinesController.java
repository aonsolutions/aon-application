package com.code.aon.ui.registry.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.config.util.BankUtil;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.form.LinesController;

public class RegistryBankLinesController extends LinesController {

	public void onBankAccountData(ActionEvent event) {
		BankUtil.fillBankAccountData((RegistryBank)getTo());
	}

}