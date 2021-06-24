package com.esferalia.aon.occam.api.model.accounting.utilities;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntryParams;

public class AccUtilitiesAccountChangeParams implements Serializable {
	
	private static final long serialVersionUID = -7875753310340952061L;
	
	private Integer domain;
	private Account oldAccount;
	private Account newAccount;
	private boolean changeInEntriesEnabled;
	private boolean changeInMastersEnabled;
	private AccountEntryParams params;
	
	
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesAccountChangeParams setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Account getOldAccount() {
		return oldAccount;
	}
	public AccUtilitiesAccountChangeParams setOldAccount(Account oldAccount) {
		this.oldAccount = oldAccount;
		return this;
	}
	
	public Account getNewAccount() {
		return newAccount;
	}
	public AccUtilitiesAccountChangeParams setNewAccount(Account newAccount) {
		this.newAccount = newAccount;
		return this;
	}
	
	public boolean isChangeInEntriesEnabled() {
		return changeInEntriesEnabled;
	}
	public AccUtilitiesAccountChangeParams setChangeInEntriesEnabled(boolean changeInEntriesEnabled) {
		this.changeInEntriesEnabled = changeInEntriesEnabled;
		return this;
	}
	
	public boolean isChangeInMastersEnabled() {
		return changeInMastersEnabled;
	}
	public AccUtilitiesAccountChangeParams setChangeInMastersEnabled(boolean changeInMastersEnabled) {
		this.changeInMastersEnabled = changeInMastersEnabled;
		return this;
	}
	
	public AccountEntryParams getAccountEntryParams() {
		return params;
	}
	public AccUtilitiesAccountChangeParams setAccountEntryParams(AccountEntryParams params) {
		this.params = params;
		return this;
	}
	
	
}
