package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.customer.Customer;
import com.esferalia.aon.entity.master.CustomerAccountDB;

@Entity
@Table(name="customer_account")
public class CustomerAccount extends CustomerAccountDB implements IAccount {
	
	private static final long serialVersionUID = 5965646601798660054L;

	@Transient
	public ITransferObject getLinkedTo() {
		return getCustomer();
	}
	public void setLinkedTo(ITransferObject to) {
		setCustomer((Customer) to);
	}	

	@Transient
	public String getAccountDescription() {
		return (getCustomer()==null) ? null : getCustomer().getRegistry().getFullName();
	}

}