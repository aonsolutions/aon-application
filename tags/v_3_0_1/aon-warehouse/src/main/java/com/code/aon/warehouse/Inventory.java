package com.code.aon.warehouse;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	@Column(name="description")
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

	
}