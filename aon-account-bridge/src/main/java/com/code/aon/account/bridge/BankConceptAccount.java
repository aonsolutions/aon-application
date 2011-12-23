package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.BankConcept;
import com.esferalia.aon.entity.master.BankConceptAccountDB;

@Entity
@Table(name="bank_concept_account")
public class BankConceptAccount extends BankConceptAccountDB implements IAccount {

	private static final long serialVersionUID = 1L;

	@Transient
	public ITransferObject getLinkedTo() {
		return getBankConcept();
	}
	public void setLinkedTo(ITransferObject to) {
		setBankConcept((BankConcept) to);
	}

	@Transient
	public String getAccountDescription() {
		return (getBankConcept()==null) ? null : getBankConcept().getName();
	}
}