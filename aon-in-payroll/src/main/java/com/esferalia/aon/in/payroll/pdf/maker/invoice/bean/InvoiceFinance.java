package com.esferalia.aon.in.payroll.pdf.maker.invoice.bean;

import java.util.Date;

public class InvoiceFinance {

	private Date   dueDate;
	private String paymethod;
	private String iban;
	private double amount;

	public InvoiceFinance(Date dueDate, String paymethod, String iban, double amount) {
		this.dueDate   = dueDate;
		this.paymethod = paymethod;
		this.iban	   = splitIban(iban);
		this.amount	   = amount;
	}

	public static String splitIban(String iban) {
		return iban.substring(0, 4) + "." + iban.substring(4, 8) + "." + iban.substring(8, 12) + "."
				+ iban.substring(12, 14) + "." + iban.substring(14);
	}

	public Date getDueDate() {
		return dueDate;
	}

	public String getPaymethod() {
		return paymethod;
	}

	public String getIban() {
		return iban;
	}

	public double getAmount() {
		return amount;
	}

}
