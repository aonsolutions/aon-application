package com.code.aon.ui.fiscal.controller;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.finance.enumeration.InvoiceType;

public class InvoiceReport {
	
	private static String OUTPUT_INVOICE = "Emit.";
	private static String INPUT_INVOICE = "Recb.";

	private static String INVESTMENT = "Bien Inver.";
	private static String EXPENSE    = "Gasto";
	private static String STANDARD   = "Bien Corriente";
	
	private InvoiceType invoiceType;
	private InvoiceTransactionType transaction;
	private boolean investment;
	private Date taxDate;
	private Date issueDate;
	private String referenceCode;
	private String series;
	private Integer number;
	private String registryDocument;
	private String registryName;
	private TaxType taxType;
	private boolean surcharge;
	private VatDeductionType vatDeductionType;
	private WithholdingType withholdingType;
	private double taxableBase;
	private double percentage;
	private double quota;
	private double deductibleQuota;
	public InvoiceType getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public void setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
	}
	public boolean isInvestment() {
		return investment;
	}
	public void setInvestment(boolean investment) {
		this.investment = investment;
	}
	public Date getTaxDate() {
		return taxDate;
	}
	public void setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getRegistryDocument() {
		return registryDocument;
	}
	public void setRegistryDocument(String registryDocument) {
		this.registryDocument = registryDocument;
	}
	public String getRegistryName() {
		return registryName;
	}
	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}
	public TaxType getTaxType() {
		return taxType;
	}
	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}
	public boolean isSurcharge() {
		return surcharge;
	}
	public void setSurcharge(boolean surcharge) {
		this.surcharge = surcharge;
	}
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}
	public void setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
	}
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public void setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
	}
	public double getTaxableBase() {
		return taxableBase;
	}
	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public double getQuota() {
		return quota;
	}
	public void setQuota(double quota) {
		this.quota = quota;
	}
	public double getDeductibleQuota() {
		if (deductibleQuota == 0) {
			return getQuota();
		}
		return deductibleQuota;
	}
	public void setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
	}

	public String getDocumentNumber() {
		String documentNumber = ((InvoiceType.SALES == getInvoiceType()) ? "E" : (InvoiceType.UNDEDUCTIBLE == getInvoiceType()) ? "G" : "R") + "-";
		if (!StringUtils.isEmpty(getSeries())) {
			documentNumber += getSeries() + "/";
		}
		documentNumber += StringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		return documentNumber;
	}

	// Método creado para no tener que definir todos los campos que se utilizan en la plantilla de report. 
	public InvoiceReport getInvoice() {
		return this;
	}
	
	public boolean isRetention() {
		return getTaxType() == TaxType.RETENTION;
	}
	public boolean isVat() {
		return getTaxType() == TaxType.VAT;
	}
	
	public String getAbbreviatedInvoiceType() {
		return (getInvoiceType() == InvoiceType.SALES)?OUTPUT_INVOICE:INPUT_INVOICE; 
	}
	public String getConceptNature() {
		if (getInvoiceType() == InvoiceType.SALES) {
			return null;
		}
		if (isInvestment()) {
			return INVESTMENT;
		} 
		if (getInvoiceType() == InvoiceType.EXPENSES || getInvoiceType() == InvoiceType.UNDEDUCTIBLE) {
			return EXPENSE;
		}
		return STANDARD;
	}

	public InvoiceReport clone() {
		InvoiceReport cloned = new InvoiceReport();
		cloned.setInvoiceType(getInvoiceType());
		cloned.setTransaction(getTransaction());
		cloned.setInvestment(isInvestment());
		cloned.setTaxDate(getTaxDate());
		cloned.setIssueDate(getIssueDate());
		cloned.setReferenceCode(getReferenceCode());
		cloned.setSeries(getSeries());
		cloned.setNumber(getNumber());
		cloned.setRegistryDocument(getRegistryDocument());
		cloned.setRegistryName(getRegistryName());
		cloned.setTaxType(getTaxType());
		cloned.setSurcharge(isSurcharge());
		cloned.setVatDeductionType(getVatDeductionType());
		cloned.setWithholdingType(getWithholdingType());
		cloned.setTaxableBase(getTaxableBase());
		cloned.setPercentage(getPercentage());
		cloned.setQuota(getQuota());
		cloned.setDeductibleQuota(getDeductibleQuota());
		return cloned;
		
	}
}
