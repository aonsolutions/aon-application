package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class IrpfBreakdown implements Serializable{
	
	private static final long serialVersionUID = 8600299724576523607L;
	
	private String document;
	private String name;
	private Date issueDate;
	
	// ------------------- facturas
	private InvoiceType invoiceType;
	private Integer invoice;
	private String series;
	private Integer number;
	private String referenceCode;
	private Date taxDate;
	private WithholdingType withholdingType;
	private double base;
	private double percent;
	private double quota;
	
	// ------------------- Nominas
	private double moneyBase;
	private double moneyQuota;
	private double inKindBase;
	private double inKindQuota;
	
	public String getDocument() {
		return document;
	}
	public IrpfBreakdown setDocument(String document) {
		this.document = document;
		return this;
	}
	public String getName() {
		return name;
	}
	public IrpfBreakdown setName(String name) {
		this.name = name;
		return this;
	}
	public Date getIssueDate() {
		return issueDate;
	}
	public IrpfBreakdown setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	// ----------------------------------------------------------------
	public InvoiceType getInvoiceType() {
		return invoiceType;
	}
	public IrpfBreakdown setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
		return this;
	}
	public Integer getInvoice() {
		return invoice;
	}
	public IrpfBreakdown setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}
	public String getSeries() {
		return series;
	}
	public IrpfBreakdown setSeries(String series) {
		this.series = series;
		return this;
	}
	public Integer getNumber() {
		return number;
	}
	public IrpfBreakdown setNumber(Integer number) {
		this.number = number;
		return this;
	}
	public String getReferenceCode() {
		return referenceCode;
	}
	public IrpfBreakdown setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
		return this;
	}
	public Date getTaxDate() {
		return taxDate;
	}
	public IrpfBreakdown setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
		return this;
	}
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public IrpfBreakdown setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
		return this;
	}
	public double getBase() {
		return base;
	}
	public IrpfBreakdown setBase(double base) {
		this.base = base;
		return this;
	}
	public double getPercent() {
		return percent;
	}
	public IrpfBreakdown setPercent(double percent) {
		this.percent = percent;
		return this;
	}
	public double getQuota() {
		return quota;
	}
	public IrpfBreakdown setQuota(double quota) {
		this.quota = quota;
		return this;
	}
	// ----------------------------------------------------------------
	public double getMoneyBase() {
		return moneyBase;
	}
	public IrpfBreakdown setMoneyBase(double moneyBase) {
		this.moneyBase = moneyBase;
		return this;
	}
	public double getMoneyQuota() {
		return moneyQuota;
	}
	public IrpfBreakdown setMoneyQuota(double moneyQuota) {
		this.moneyQuota = moneyQuota;
		return this;
	}
	public double getInKindBase() {
		return inKindBase;
	}
	public IrpfBreakdown setInKindBase(double inKindBase) {
		this.inKindBase = inKindBase;
		return this;
	}
	public double getInKindQuota() {
		return inKindQuota;
	}
	public IrpfBreakdown setInKindQuota(double inKindQuota) {
		this.inKindQuota = inKindQuota;
		return this;
	}
	public boolean isMoneyRetention() {
		return (AonMathUtils.isNotZero( moneyBase) || AonMathUtils.isNotZero( moneyQuota));
	}
	public boolean isInKindRetention() {
		return (AonMathUtils.isNotZero( inKindBase) || AonMathUtils.isNotZero( inKindQuota));
	}
	
	public String getDocumentNumber() {
		String documentNumber = ((InvoiceType.SALES == getInvoiceType()) ? 
				"E" 
				: (InvoiceType.UNDEDUCTIBLE == getInvoiceType()) 
				? "G" 
				: "R") + "-";
		if (!AonStringUtils.isEmpty(getSeries())) {
			documentNumber += series + "/";
		}
		documentNumber += AonStringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		return documentNumber;
	}
	
}
