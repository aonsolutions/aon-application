package com.code.aon.registry;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.registry.enumeration.RegistryItemStatus;
import com.code.aon.registry.enumeration.RegistryMode;
import com.esferalia.aon.entity.master.RegistryItemDB;

@Entity
@Table(name="ritem")
public class RegistryItem extends RegistryItemDB {
	
	private static final long serialVersionUID = 1L;

	public RegistryItem() {
		setStatus(RegistryItemStatus.ACTIVE);
	}

	@Transient
	public boolean isCommercialType() {
		return getType() == RegistryMode.TARGET;
	}

	@Transient
	public boolean isSaleType() {
		return getType() == RegistryMode.CUSTOMER;
	}

	@Transient
	public boolean isPurchaseType() {
		return getType() == RegistryMode.SUPPLIER;
	}

	@Transient
	public boolean isExpenseType() {
		return getType() == RegistryMode.CREDITOR;
	}

}