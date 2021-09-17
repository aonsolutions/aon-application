package com.esferalia.aon.occam.api.model.finance.checkit;

import java.io.Serializable;

public class CheckitUnlinkedBankAccount implements Serializable {

	private static final long serialVersionUID = -6197127936134641876L;
	
	private String bank;
	private String iban;
	private CheckItLoginFields login;
	private Integer bankId;
	
	public String getBank() {
		return bank;
	}
	public CheckitUnlinkedBankAccount setBank(String bank) {
		this.bank = bank;
		return this;
	}
	public String getIban() {
		return iban;
	}
	public CheckitUnlinkedBankAccount setIban(String iban) {
		this.iban = iban;
		return this;
	}
	public CheckItLoginFields getLogin() {
		return login;
	}
	public CheckitUnlinkedBankAccount setLogin(CheckItLoginFields login) {
		this.login = login;
		return this;
	}
	public Integer getBankId() {
		return bankId;
	}
	public CheckitUnlinkedBankAccount setBankId(Integer bankId) {
		this.bankId = bankId;
		return this;
	}
	
	
	
}
