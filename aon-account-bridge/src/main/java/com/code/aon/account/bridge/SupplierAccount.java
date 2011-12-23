package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.entity.master.SupplierAccountDB;

@Entity
@Table(name="supplier_account")
public class SupplierAccount extends SupplierAccountDB implements IAccount {
	
	private static final long serialVersionUID = 1L;

	@Transient
	public ITransferObject getLinkedTo() {
		return getSupplier();
	}
	public void setLinkedTo(ITransferObject to) {
		setSupplier((Supplier) to);
	}	

	@Transient
	public String getAccountDescription() {
		return (getSupplier()==null) ? null : getSupplier().getRegistry().getFullName();
	}

}