package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;

public class AccountEntryWrapper implements IAccountEntryWrapper, Serializable {
	
	private static final long serialVersionUID = -1975602092083364156L;
	
	private AccountEntry ae;
	
	public AccountEntryWrapper(AccountEntry ae) {
		this.ae = ae;
	}
	
	@Override
	public AccountEntry getAccountEntry() {
		return ae;
	}

	@Override
	public void setAccountEntry(AccountEntry entry) {
		this.ae = entry;
	}

	@Override
	public LinkedList<AccountEntry> getAccountEntries() {
		LinkedList<AccountEntry> list = new LinkedList<AccountEntry>();
		list.add(getAccountEntry());
		return list;
	}

}
