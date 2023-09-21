package com.code.aon.warehouse;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.warehouse.enumeration.InventoryStatus;
import com.esferalia.aon.entity.master.InventoryDB;

@Entity
@Table(name="inventory")
public class Inventory extends InventoryDB implements IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<InventoryDetail> details = new HashSet<InventoryDetail>();

	@OneToMany(mappedBy = "inventory", cascade={CascadeType.REMOVE})
	public Set<InventoryDetail> getDetails() {
		return details;
	}
	public void setDetails(Set<InventoryDetail> details) {
		this.details = details;
	}
	
	@Transient
	public boolean isClosed() {
		return getStatus() == InventoryStatus.PROCESSED;
	}
	
}