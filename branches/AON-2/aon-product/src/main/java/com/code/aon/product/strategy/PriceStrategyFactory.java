package com.code.aon.product.strategy;

/**
 * Factory for the price strategy.
 * 
 * @author Consulting & Development. 
 * @since 1.0
 *
 */
public class PriceStrategyFactory {
	
	/**
	 * Creates a new instance of an object that implements IPriceStrategy
	 * 
	 * @return BasicPriceStrategy
	 */
	public static IPriceStrategy getPriceStrategy(){
		return new BasicPriceStrategy();
	}
}
