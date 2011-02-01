package com.code.aon.file.tax.model.MOD340.data;


public class InvestmentInvoice extends Invoice {

	private int yearProrate;
	private int yearRegularization;
	private String deliveryInvoice;
	private double doneRegularization;
	private String investementDate;
	private String investementName;
	
	public int getYearProrate() {
		return yearProrate;
	}
	public void setYearProrate(int yearProrate) {
		this.yearProrate = yearProrate;
	}
	public int getYearRegularization() {
		return yearRegularization;
	}
	public void setYearRegularization(int yearRegularization) {
		this.yearRegularization = yearRegularization;
	}
	public String getDeliveryInvoice() {
		return deliveryInvoice;
	}
	public void setDeliveryInvoice(String deliveryInvoice) {
		this.deliveryInvoice = deliveryInvoice;
	}
	public double getDoneRegularization() {
		return doneRegularization;
	}
	public void setDoneRegularization(double doneRegularization) {
		this.doneRegularization = doneRegularization;
	}
	public String getInvestementDate() {
		return investementDate;
	}
	public void setInvestementDate(String investementDate) {
		this.investementDate = investementDate;
	}
	public String getInvestementName() {
		return investementName;
	}
	public void setInvestementName(String investementName) {
		this.investementName = investementName;
	}
	
}
