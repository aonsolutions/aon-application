package com.code.aon.registry;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.registry.enumeration.RegistryItemStatus;
import com.code.aon.registry.enumeration.RegistryMode;
import com.esferalia.aon.entity.master.RegistryItemDB;

@Entity
@Table(name="ritem")
public class RegistryItem extends RegistryItemDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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