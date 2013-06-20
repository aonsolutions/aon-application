package com.code.aon.warehouse;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.InventoryDB;

@Entity
@Table(name="inventory")
public class Inventory extends InventoryDB {
	
	private static final long serialVersionUID = 1L;

	private Set<InventoryDetail> details = new HashSet<InventoryDetail>();

	@OneToMany(mappedBy = "inventory", cascade={CascadeType.REMOVE})
	public Set<InventoryDetail> getDetails() {
		return details;
	}
	public void setDetails(Set<InventoryDetail> details) {
		this.details = details;
	}
}