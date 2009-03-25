package com.code.aon.warehouse;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents an Inventory.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
@Entity
@Table(name="inventory")
public class Inventory implements ITransferObject {
	
	private static final long serialVersionUID = 5398894889661223054L;

	/**
	 * Unique key
	 */
	private Integer id;
	
	/**
	 * The date of the inventory
	 */
	private Date inventoryDate;

	/**
	 * The warehouse where the inventory was done
	 */
	private Warehouse warehouse;

	/**
	 * A description to this inventory
	 */
	private String description;

	/** The details. */
	private Set<InventoryDetail> details = new HashSet<InventoryDetail>();
	
	/**
	 * Returns the unique key
	 * 
	 * @return unique key
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	/**
	 * Assigns the unique key
	 * 
	 * @param primaryKey
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Returns the inventory date
	 * 
	 * @return date
	 */
	@Column(name="inventory_date")
	public Date getInventoryDate() {
		return inventoryDate;
	}

	/**
	 * Assigns the date
	 * 
	 * @param inventoryDate the date
	 */
	public void setInventoryDate(Date inventoryDate) {
		this.inventoryDate = inventoryDate;
	}

	/**
	 * Returns the warehouse
	 * 
	 * @return the warehouse
	 */
	@ManyToOne
	@JoinColumn( name="warehouse",nullable=false )
	public Warehouse getWarehouse() {
		return warehouse;
	}

	/**
	 * Assigns the warehouse
	 * 
	 * @param warehouse The warehouse to set.
	 */
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	/**
	 * Returns the description.
	 * 
	 * @return the description.
	 */
	@Column(name="description", length=64)
	public String getDescription() {
		return description;
	}

	/**
	 * Assigns the description
	 * 
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the details.
	 * 
	 * @return the details
	 */
	@OneToMany(mappedBy = "inventory", cascade={CascadeType.REMOVE})
	public Set<InventoryDetail> getDetails() {
		return details;
	}

	/**
	 * Sets the details.
	 * 
	 * @param details the new details
	 */
	public void setDetails(Set<InventoryDetail> details) {
		this.details = details;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Inventory) {
			Inventory inventory = (Inventory) obj;
			if (ObjectUtils.equals(getId(), inventory.getId())) {
				return true;
			}
		}
		return false;
	}
	
}