package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.registry.RegistryBank;
import com.esferalia.aon.entity.master.RegistryBankAccountDB;

@Entity
@Table(name="rbank_account")
public class RegistryBankAccount extends RegistryBankAccountDB implements IAccount {
	
	private static final long serialVersionUID = -327952223428600895L;
	@Transient
	public ITransferObject getLinkedTo() {
		return getRegistryBank();
	}
	public void setLinkedTo(ITransferObject to) {
		setRegistryBank((RegistryBank) to);
	}	

	@Transient
	public String getAccountDescription() {
		return (getRegistryBank()==null) ? null : getRegistryBank().getFullName();
	}

}