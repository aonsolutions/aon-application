package com.esferalia.aon.occam.api.model.finance.nordigen;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.registry.RegistryBank;

public class NordigenBankAccount implements Serializable {

	private static final long serialVersionUID = 1175684863512830855L;

	private RegistryBank rbank;
	private NordigenAccountMetadata metadata;
	private NordigenAccountDetails details;
	private NordigenAccountBalance balance;
	private NordigenRequisition requisition;
	private NordigenInstitution institution;
	
	private boolean isLinked;
	private String iban;
	private String bankAlias;
	
	public RegistryBank getRbank() {
		return rbank;
	}
	public NordigenBankAccount setRbank(RegistryBank rbank) {
		this.rbank = rbank;
		return this;
	}
	public NordigenAccountMetadata getMetadata() {
		return metadata;
	}
	public NordigenBankAccount setMetadata(NordigenAccountMetadata metadata) {
		this.metadata = metadata;
		return this;
	}
	public NordigenAccountDetails getDetails() {
		return details;
	}
	public NordigenBankAccount setDetails(NordigenAccountDetails details) {
		this.details = details;
		return this;
	}
	public NordigenAccountBalance getBalance() {
		return balance;
	}
	public NordigenBankAccount setBalance(NordigenAccountBalance balance) {
		this.balance = balance;
		return this;
	}
	public NordigenRequisition getRequisition() {
		return requisition;
	}
	public NordigenBankAccount setRequisition(NordigenRequisition requisition) {
		this.requisition = requisition;
		return this;
	}
	public NordigenInstitution getInstitution() {
		return institution;
	}
	public NordigenBankAccount setInstitution(NordigenInstitution institution) {
		this.institution = institution;
		return this;
	}
	public boolean isLinked() {
		return isLinked;
	}
	public NordigenBankAccount setLinked(boolean isLinked) {
		this.isLinked = isLinked;
		return this;
	}
	public String getIban() {
		return iban;
	}
	public NordigenBankAccount setIban(String iban) {
		this.iban = iban;
		return this;
	}
	public String getBankAlias() {
		return bankAlias;
	}
	public NordigenBankAccount setBankAlias(String bankAlias) {
		this.bankAlias = bankAlias;
		return this;
	}

	
	
}
