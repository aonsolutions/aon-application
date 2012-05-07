package com.code.aon.account.bridge;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.Tax;
import com.esferalia.aon.entity.master.TaxAccountDB;

@Entity
@Table(name="tax_account")
public class TaxAccount extends TaxAccountDB implements IAccount {
	
	private static final long serialVersionUID = 1L;

	@Transient
	public ITransferObject getLinkedTo() {
		return getTax();
	}
	public void setLinkedTo(ITransferObject to) {
		setTax((Tax) to);
	}

	@Transient
	public String getAccountDescription() {
		return (getTax()==null) ? null : getTax().getName();
	}

}