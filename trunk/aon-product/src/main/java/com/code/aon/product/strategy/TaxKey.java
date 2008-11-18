package com.code.aon.product.strategy;

import com.code.aon.config.enumeration.TaxType;

public class TaxKey {
	
	private TaxType type;
	
	private double percent;

	public double getPercent() {
		return percent;
	}

	public void setType(TaxType type) {
		this.type = type;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public TaxType getType() {
		return type;
	}
	
	@Override
	public boolean equals(Object obj) {
		TaxKey key = (TaxKey)obj;
		return (getType().equals(key.getType()) && getPercent() == key.getPercent()); 
	}

	@Override
	public int hashCode() {
		return getType().hashCode() + new Double(getPercent()).hashCode();
	}
	
	
}