package com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill;

import java.util.Date;

public class EnterpriseBillEntry
{
	private String description;
	private int quantity;
	private double price;
	private int percent;
	private double amount;

	public EnterpriseBillEntry(String description, int quantity, double price, int percent, double amount) {
		this.description = description;
		this.quantity = quantity;
		this.price = price;
		this.percent = percent;
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getPrice() {
		return price;
	}

	public int getPercent() {
		return percent;
	}

	public double getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return "EnterpriseBillEntry{" +
				"description='" + description + '\'' +
				", quantity=" + quantity +
				", price=" + price +
				", percent=" + percent +
				", amount=" + amount +
				'}';
	}
}
