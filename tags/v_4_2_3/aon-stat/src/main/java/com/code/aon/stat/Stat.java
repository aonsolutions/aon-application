package com.code.aon.stat;


public class Stat {

	private Integer key;
	private String name;
	private double amount;
	private double numInvoice;
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

}
