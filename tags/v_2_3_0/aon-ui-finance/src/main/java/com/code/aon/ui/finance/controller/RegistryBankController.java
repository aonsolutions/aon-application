package com.code.aon.ui.finance.controller;

import com.code.aon.ui.finance.IBankAccountContainer;
import com.code.aon.ui.form.LinesController;

public class RegistryBankController extends LinesController implements IBankAccountContainer{

	private String entity;
	
	private String office;
	
	private String control;
	
	private String account;

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getControl() {
		return control;
	}

	public void setControl(String control) {
		this.control = control;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
}