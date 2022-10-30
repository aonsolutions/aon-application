package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;

public class NordigenUnlinkedBankAccount implements Serializable {

	private static final long serialVersionUID = -6197127936134641876L;
	
	private String bank;
	private String iban;
	
	public String getBank() {
		return bank;
	}
	public NordigenUnlinkedBankAccount setBank(String bank) {
		this.bank = bank;
		return this;
	}
	public String getIban() {
		return iban;
	}
	public NordigenUnlinkedBankAccount setIban(String iban) {
		this.iban = iban;
		return this;
	}
}
