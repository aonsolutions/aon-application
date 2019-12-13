package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountEntry;

public interface IAccountEntryWrapper extends Serializable {

	AccountEntry getAccountEntry();
	void setAccountEntry(AccountEntry entry);
	public LinkedList<AccountEntry> getAccountEntries();
	
}
