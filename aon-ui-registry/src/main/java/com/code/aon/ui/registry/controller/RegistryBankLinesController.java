package com.code.aon.ui.registry.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.registry.RegistryBank;
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
	
	@Override
	public void onAccept(ActionEvent event) {
		RegistryBank bank = (RegistryBank) getTo();
		// Si tiene fecha de balance es que ha sido vinculado y los bancos vinculados no deberian editarse porque se generan errores cuando modifican el IBAN
		if(bank.getBalanceDate() != null) {			
			addMessage("No se puede editar un banco vinculado");
			return;
		}
		super.onAccept(event);
	}

}