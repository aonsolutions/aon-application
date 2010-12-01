package com.code.aon.file.tax.model.MOD340.data;

public class IssuedInvoice extends Invoice {

	private int invoiceCount;
	private int registerCount;
	private String firstInvoiceNumber;
	private String lastInvoiceNumber;
	private String correctedInvoiceNumber;
	private double surchargePercent;
	private double surchargeQuota;
	
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
	public String getCorrectedInvoiceNumber() {
		return correctedInvoiceNumber;
	}
	public void setCorrectedInvoiceNumber(String correctedInvoiceNumber) {
		this.correctedInvoiceNumber = correctedInvoiceNumber;
	}
	public double getSurchargePercent() {
		return surchargePercent;
	}
	public void setSurchargePercent(double surchargePercent) {
		this.surchargePercent = surchargePercent;
	}
	public double getSurchargeQuota() {
		return surchargeQuota;
	}
	public void setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
	}
}
