package com.esferalia.aon.in.payroll.pdf.creators.invoice.beans;

public class InvoiceEntry
{
	private String description;
	private double quantity;
	private double price;
	private double percent;
	private double amount;

	public InvoiceEntry(String description, double quantity, double price, double percent, double amount) {
		this.description = description;
		this.quantity = quantity;
		this.price = price;
		this.percent = percent;
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public double getQuantity() {
		return quantity;
	}

	public double getPrice() {
		return price;
	}

	public double getPercent() {
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
