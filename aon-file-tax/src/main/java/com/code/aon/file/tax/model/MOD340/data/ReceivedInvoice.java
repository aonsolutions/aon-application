package com.code.aon.file.tax.model.MOD340.data;

public class ReceivedInvoice extends Invoice {

	private int invoiceCount;
	private int registerCount;
	private String firstInvoiceNumber;
	private String lastInvoiceNumber;
	private double deductibleQuota;
	
	public int getInvoiceCount() {
		return invoiceCount;
	}
	public void setInvoiceCount(int invoiceCount) {
		this.invoiceCount = invoiceCount;
	}
	public int getRegisterCount() {
		return registerCount;
	}
	public void setRegisterCount(int registerCount) {
		this.registerCount = registerCount;
	}
	public String getFirstInvoiceNumber() {
		return firstInvoiceNumber;
	}
	public void setFirstInvoiceNumber(String firstInvoiceNumber) {
		this.firstInvoiceNumber = firstInvoiceNumber;
	}
	public String getLastInvoiceNumber() {
		return lastInvoiceNumber;
	}
	public void setLastInvoiceNumber(String lastInvoiceNumber) {
		this.lastInvoiceNumber = lastInvoiceNumber;
	}
	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public void setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
	}
}
