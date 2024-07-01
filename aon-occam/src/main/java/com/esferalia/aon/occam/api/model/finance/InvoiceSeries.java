package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

public class InvoiceSeries implements Serializable {
	
	private static final long serialVersionUID = -129893698643793652L;
	
	private String description;
	private boolean sales;
	private int fromNumber;
	private int toNumber;
	private int count;

	public String getDescription() {
		return description;
	}
	public InvoiceSeries setDescription(String description) {
		this.description = description;
		return this;
	}

	public boolean isSales() {
		return sales;
	}
	public InvoiceSeries setSales(boolean sales) {
		this.sales = sales;
		return this;
	}
	public int getFromNumber() {
		return fromNumber;
	}
	public InvoiceSeries setFromNumber(int fromNumber) {
		this.fromNumber = fromNumber;
		return this;
	}
	public int getToNumber() {
		return toNumber;
	}
	public InvoiceSeries setToNumber(int toNumber) {
		this.toNumber = toNumber;
		return this;
	}
	public int getCount() {
		return count;
	}
	public InvoiceSeries setCount(int count) {
		this.count = count;
		return this;
	}
	
}
