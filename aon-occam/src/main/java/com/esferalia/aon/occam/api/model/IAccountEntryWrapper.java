package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.AccountEntry;

public interface IAccountEntryWrapper extends Serializable {

	AccountEntry getAccountEntry();
	void setAccountEntry(AccountEntry entry);
	
}
