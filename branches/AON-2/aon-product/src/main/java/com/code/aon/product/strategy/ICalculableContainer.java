package com.code.aon.product.strategy;

import java.util.Date;
import java.util.List;

import com.code.aon.product.util.DiscountExpression;

/**
 * Contains ICalculable objects.
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public interface ICalculableContainer {

	/**
	 * Returns the list of ICalculable objects
	 * 
	 * @return ICalculable objects
	 */
	public List getDetailList();
	
	/**
	 * Returns the discaunt expression of this container
	 * 
	 * @return DiscountExpression
	 */
	public DiscountExpression getDiscountExpression();
	
	/**
	 * Returns the date
	 * 
	 * @return Date
	 */
	public Date getDate();
}
