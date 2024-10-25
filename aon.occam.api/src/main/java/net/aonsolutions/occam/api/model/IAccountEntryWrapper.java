package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;

public interface IAccountEntryWrapper extends Serializable {

	AccountEntry getAccountEntry();
	void setAccountEntry(AccountEntry entry);
	
	public LinkedList<AccountEntry> getAccountEntries();
	
}
