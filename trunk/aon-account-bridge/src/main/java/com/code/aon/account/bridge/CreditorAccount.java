package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Creditor;
import com.esferalia.aon.entity.master.CreditorAccountDB;

@Entity
@Table(name="creditor_account")
public class CreditorAccount extends CreditorAccountDB implements IAccount {
	
	private static final long serialVersionUID = 1L;

	@Transient
	public ITransferObject getLinkedTo() {
		return getCreditor();
	}
	public void setLinkedTo(ITransferObject to) {
		setCreditor((Creditor) to);
	}	

	@Transient
	public String getAccountDescription() {
		return (getCreditor()==null) ? null : getCreditor().getRegistry().getFullName();
	}

}