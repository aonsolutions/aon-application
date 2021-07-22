package com.esferalia.aon.gwt.template.shared;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.google.gwt.user.client.rpc.IsSerializable;

public class AccountEntryImportClass implements IsSerializable {

	
	private AccountEntry entry;
	private Integer line;
	
	public AccountEntryImportClass() {
		this.entry = new AccountEntry();
	}

	public AccountEntry getEntry() {
		return entry;
	}

	public void setAccount(AccountEntry entry) {
		this.entry = entry;
	}

	public Integer getLine() {
		return line;
	}

	public void setLine(Integer line) {
		this.line = line;
	}
}
