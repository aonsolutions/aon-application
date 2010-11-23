package com.code.aon.product.strategy;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;

/**
 * To operate with an object in price strategy must implement this interface.
 * This insure that the object has all data needed by price strategy objects.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public interface ICalculable {
	
	/**
	 * Returns the Item
	 * 
	 * @return the item
	 */
	public Item getItem();
	
	/**
	 * Returns the discount expression
	 * 
	 * @return the discount expression
	 */
	public DiscountExpression getDiscountExpression();
	
	/**
	 * Returns the quantity of items
	 * 
	 * @return the quantity
	 */
	public double getQuantity();
	
	/**
	 * Returns the price to operate
	 * 
	 * @return the price
	 */
	public double getPrice();
	
	public double getTaxes() throws ManagerBeanException;
}
