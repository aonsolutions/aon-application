package com.code.aon.warehouse;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.InventoryDB;

@Entity
@Table(name="inventory")
public class Inventory extends InventoryDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<InventoryDetail> details = new HashSet<InventoryDetail>();

	@OneToMany(mappedBy = "inventory", cascade={CascadeType.REMOVE})
	public Set<InventoryDetail> getDetails() {
		return details;
	}
	public void setDetails(Set<InventoryDetail> details) {
		this.details = details;
	}
}