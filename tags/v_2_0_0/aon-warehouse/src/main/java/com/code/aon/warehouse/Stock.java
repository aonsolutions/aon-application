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
 * Transfer Object that represents a Stock line.
 * 
 * @author igayarre
 *
 */
@Entity
@Table(name="stock")
public class Stock implements ITransferObject{
	
	/**
	 * Unique key
	 */
	private Integer id;
	
	/**
	 * Item of the stock line
	 */
	private Item item;
	
	/**
	 * Warehouse of the stock analisys
	 */
	private Warehouse warehouse;
	
	/**
	 * Quantity of items in this warehouse
	 */
	private Double quantity;
	
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
	public void setId(Integer primaryKey) {
		this.id = primaryKey;
	}
	
	/**
	 * Returns the Item 
	 * 
	 * @return Returns the item.
	 */
	@ManyToOne
	@JoinColumn( name="item",nullable=false )
	public Item getItem() {
		return item;
	}
	/**
	 * Assgins a Item to this detail
	 * 
	 * @param item The item to set.
	 */
	public void setItem(Item item) {
		this.item = item;
	}
	
	/**
	 * Returns the quantity
	 * 
	 * @return Returns the quantity.
	 */
	public Double getQuantity() {
		return quantity;
	}
	/**
	 * Assigns the quantity of Items
	 * 
	 * @param quantity The quantity to set.
	 */
	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * Returns the warehouse
	 * 
	 * @return Returns the warehouse.
	 */
	@ManyToOne
	@JoinColumn( name="warehouse",nullable=false )
	public Warehouse getWarehouse() {
		return warehouse;
	}
	/**
	 * Assigns the warehouse
	 * 
	 * @param warehouse The warehouse to be set.
	 */
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
}