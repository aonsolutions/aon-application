package com.code.aon.stat;

import java.util.Date;

import com.code.aon.finance.enumeration.InvoiceType;

public class Invoice {

	private Integer id;
	private Integer type;
	private Date issueDate;
	private String reference;
	private String registryName;
	private double projectTaxableBase;
	private double invoiceTaxableBase;
	private double invoiceTotal;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getType() {
		return type;
	}
	public InvoiceType getInvoiceType() {
		return InvoiceType.values()[getType()];
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public String getRegistryName() {
		return registryName;
	}
	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}
	
	public double getProjectTaxableBase() {
		return projectTaxableBase;
	}
	public void setProjectTaxableBase(double projectTaxableBase) {
		this.projectTaxableBase = projectTaxableBase;
	}
	
	public double getInvoiceTaxableBase() {
		return invoiceTaxableBase;
	}
	public void setInvoiceTaxableBase(double invoiceTaxableBase) {
		this.invoiceTaxableBase = invoiceTaxableBase;
	}
	
	public double getInvoiceTotal() {
		return invoiceTotal;
	}
	public void setInvoiceTotal(double invoiceTotal) {
		this.invoiceTotal = invoiceTotal;
	}
}
