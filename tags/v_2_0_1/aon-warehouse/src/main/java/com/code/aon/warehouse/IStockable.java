package com.code.aon.warehouse;

import com.code.aon.product.Item;

/**
 * Interface that must implement stockable classes
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public interface IStockable {

	/**
	 * Returns the evaluating item
	 * 
	 * @return item
	 */
	public Item getItem();
	
	/**
	 * Return the warehouse where the item is in
	 * 
	 * @return
	 */
	public Warehouse getWarehouse();
	
	/**
	 * Returns the quantity of items in this warehouse
	 * 
	 * @return
	 */
	public double getQuantity();
	
	/**
	 * Is an entry
	 * 
	 * @return is entry
	 */
	public boolean isEntry();
}
