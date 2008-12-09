package com.code.aon.config;

import com.code.aon.config.Bank;

public interface IBankAccountContainer {

	public Bank getBank();

	public void setBank(Bank bank);

	public BankAccount getBankAccount();

	public void setBankAccount(BankAccount bankAccount);
	
}
