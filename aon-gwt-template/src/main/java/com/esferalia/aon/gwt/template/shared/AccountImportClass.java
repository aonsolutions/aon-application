package com.esferalia.aon.gwt.template.shared;

import com.esferalia.aon.occam.api.model.Account;
import com.google.gwt.user.client.rpc.IsSerializable;

public class AccountImportClass implements IsSerializable {

	private Account account;
	private Integer line;
	
	public AccountImportClass() {
		this.account = new Account()
				.setActive(true);
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}
}