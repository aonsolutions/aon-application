package com.code.aon.product.pricing;

import com.code.aon.config.Tax;

public interface IPriceable {
	
	public double getProfitablePrice();
	public double getProfitPercent();
	public void setProfitPercent(double value);
	public double getPrice();
	public void setPrice(double value);
	public double getSalesPrice(double price);
	public void setSalesPrice(double value);
	public Tax getVat();
	public Tax getRetention();

}
