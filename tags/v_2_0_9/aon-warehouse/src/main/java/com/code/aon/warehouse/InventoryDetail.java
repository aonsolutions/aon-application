package com.code.aon.warehouse;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.product.Item;

/**
 * Transfer Object that represents a Inventory Detail line.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
@Entity
@Table(name="inventory_detail")
public class InventoryDetail implements ITransferObject {
	
	/**
	 * Unique key
	 */
	private Integer id;
	
	/**
	 * The inventory header linked
	 */
	private Inventory inventory;
	
	/**
	 * Item referenced in this line
	 */
	private Item item;
	
	/**
	 * The quantity estimated
	 */
	private double actualQuantity;

	/**
	 * The real quantity
	 */
	private double realQuantity;

	/**
	 * The cost of this inventory detail at this moment
	 */
	private double cost;

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
	 * Returns the inventory header
	 * 
	 * @return inventory header
	 */
	@ManyToOne
    @JoinColumn(name="inventory", nullable = false, updatable = false)
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * Assigns the inventory header
	 * 
	 * @param inventory
	 */
	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	/**
	 * Returns the Item of this line
	 * 
	 * @return Item
	 */
	@ManyToOne
    @JoinColumn(name="item", updatable = false)
	public Item getItem() {
		return item;
	}

	/**
	 * Assigns the Item
	 * 
	 * @param item
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * Returns the actual quantity
	 * 
	 * @return actual quantity
	 */
	@Column(name="actual_quantity")
	public double getActualQuantity() {
		return actualQuantity;
	}

	/**
	 * Assigns the actual quantity
	 * 
	 * @param actual quantity
	 */
	public void setActualQuantity(double actualQuantity) {
		this.actualQuantity = actualQuantity;
	}
	
	/**
	 * Returns the real quantity
	 * 
	 * @return real quantity
	 */
	@Column(name="real_quantity")
	public double getRealQuantity() {
		return realQuantity;
	}

	/**
	 * Assigns the real quantity
	 * 
	 * @param real quantity
	 */
	public void setRealQuantity(double realQuantity) {
		this.realQuantity = realQuantity;
	}

	/**
	 * Returns the cost
	 * 
	 * @return cost
	 */
	@Column(name="cost")
	public double getCost() {
		return cost;
	}

	/**
	 * Assigns the cost
	 * 
	 * @param cost
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	
}