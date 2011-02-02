package com.code.ui.gbp.stats;

import java.math.BigDecimal;
import java.util.Date;

import com.code.gbp.enumeration.SupplierStatus;

public class InvoiceControl {

	private Integer campaignCode;

	private String campaignName;

	private String supplierType;

	private String supplierName;

	private String invoiceConcept;

	private String number;

	private Double amount;
	
	private Date invoiceDate;
	
	private Date paymentDate;

	private SupplierStatus supplierStatus;
	
	public InvoiceControl(){
	}
	
	public InvoiceControl	(
			Integer campaignCode,
			String campaignName,
			String supplierType,
			String supplierName,
			SupplierStatus supplierStatus,
			String invoiceConcept,
			String number,
			BigDecimal amount,
			Date invoiceDate,
			Date paymentDate
			){
		this.campaignCode = campaignCode;
		this.campaignName = campaignName;
		this.supplierType = supplierType;
		this.supplierName = supplierName;
		this.supplierStatus = supplierStatus;
		this.invoiceConcept = invoiceConcept;
		this.number = number;
		this.amount = amount==null?new Double(0):new Double(amount.doubleValue());
		this.invoiceDate = invoiceDate;
		this.paymentDate = paymentDate;
	}

	public Integer getCampaignCode() {
		return campaignCode;
	}

	public void setCampaignCode(Integer campaignCode) {
		this.campaignCode = campaignCode;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getSupplierType() {
		return supplierType;
	}

	public void setSupplierType(String supplierType) {
		this.supplierType = supplierType;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getInvoiceConcept() {
		return invoiceConcept;
	}

	public void setInvoiceConcept(String invoiceConcept) {
		this.invoiceConcept = invoiceConcept;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public SupplierStatus getSupplierStatus() {
		return supplierStatus;
	}

	public void setSupplierStatus(SupplierStatus supplierStatus) {
		this.supplierStatus = supplierStatus;
	}
	
	
	
}
