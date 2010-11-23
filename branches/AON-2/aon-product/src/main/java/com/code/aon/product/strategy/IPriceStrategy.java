package com.code.aon.product.strategy;

import java.util.List;

import com.code.aon.product.Tariff;
import com.code.aon.registry.ITaxInfo;

/**
 * Manages ICalculable objects to obtain the prices.
 * Prices included in price strategy are with or without taxes,
 * and allows to parametrice tariffs. 
 * 
 * @author Consulting & Development. 
 * @since 1.0
 *
 */
public interface IPriceStrategy {
	
	/**
	 * Returns the unit price of the ICalculable object.
	 * 
	 * @param calc ICalculable object
	 * @return the unit price
	 */
	public double getUnitPrice(ICalculable calc);
	
	/**
	 * Returns the unit price of the ICalculable object
	 * appling a concrete tariff.
	 * 
	 * @param calc ICalculable object
	 * @param tariff the tariff to be applied to the ICalculabe object
	 * @return the unit price
	 */
	public double getUnitPrice(ICalculable calc, Tariff tariff);
	
	/**
	 * Returns the base price of the ICalculable object.
	 * 
	 * @param calc ICalculable object
	 * @return the base price
	 */
	public double getBasePrice(ICalculable calc);
	
	/**
	 * Returns the base price of the ICalculable object.
	 * 
	 * @param calc ICalculable object
	 * @param forceUnitPrice option to obtain the price from the
	 *				price strategy or directly from the ICalculable object
	 * @return the base price
	 */
	public double getBasePrice(ICalculable calc, boolean forceUnitPrice);
	
	/**
	 * Returns the taxable base for this ICalculableContainer
	 * 
	 * @param icc ICalculableContainer a list of ICalculables
	 * @return the taxable base
	 */
	public double getTaxableBase(ICalculableContainer icc);
	
	/**
	 * Returns a list of TaxBreakDown for this ICalculableContainer 
	 * instead of the tax info
	 * 
	 * @param icc ICalculableContainer a list of ICalculables
	 * @param iti tax aplicability info
	 * @return
	 */
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti);
	
	/**
	 * Returns the total price for this ICalculableContainer
	 * 
	 * @param icc ICalculableContainer a list of ICalculables
	 * @param iti tax aplicability info
	 * @return the total price
	 */
	public double getTotalPrice(ICalculableContainer icc, ITaxInfo iti);

}
