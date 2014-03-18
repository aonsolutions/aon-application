package com.code.aon.stat;

import java.io.Serializable;

import com.code.aon.AonVersion;


public class Stat implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer key;
	private String name;
	private double amount;
	private double numInvoice;
	private double productCount;
	private double averageAmount;
	

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getAverageAmount() {
		return averageAmount;
	}

	public void setAverageAmount(double averageAmount) {
		this.averageAmount = averageAmount;
	}

	public double getNumInvoice() {
		return numInvoice;
	}

	public void setNumInvoice(double d) {
		this.numInvoice = d;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getProductCount() {
		return productCount;
	}

	public void setProductCount(double productCount) {
		this.productCount = productCount;
	}

}
