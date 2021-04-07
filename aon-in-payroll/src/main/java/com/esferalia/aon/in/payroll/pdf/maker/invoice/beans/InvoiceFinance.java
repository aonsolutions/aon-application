package com.esferalia.aon.in.payroll.pdf.maker.invoice.beans;

import java.util.Date;

public class InvoiceFinance {

	private Date due_date;
	private String paymethod;
	private String iban;
	private double amount;

	public InvoiceFinance(Date due_date, String paymethod, String iban, double amount) {
		this.due_date = due_date;
		this.paymethod = paymethod;
		this.iban = split_iban(iban);
		this.amount = amount;
	}

	public static String split_iban(String iban){
		return	  iban.substring(0,4) 	+ "."
				+ iban.substring(4,8) 	+ "."
				+ iban.substring(8,12) 	+ "."
				+ iban.substring(12,14) + "."
				+ iban.substring(14);
	}

	public Date getDue_date() {
		return due_date;
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

	@Override
	public String toString() {
		return "EnterpriseBillFinance{" +
				"due_date=" + due_date +
				", paymethod='" + paymethod + '\'' +
				", iban='" + iban + '\'' +
				", amount=" + amount +
				'}';
	}
}
