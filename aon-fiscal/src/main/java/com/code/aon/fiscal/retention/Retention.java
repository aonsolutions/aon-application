package com.code.aon.fiscal.retention;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.fiscal.enumeration.VatReportType;

public class Retention implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private InvoiceType invoiceType;
	private int count;
	private Double percent;
	private double base;
	private double quota;
	private String series;
	private int number;
	private String reference;
	private String document;
	private String name;
	private Date date;
	private Date invoiceDate;
	private int month;
	private int year;
	private int quarter;
	private InvoiceTransactionType transactionType;
	private WithholdingType withholdingType;
	private boolean investment;
	private boolean withholdingHidden;
	private boolean total;
	private boolean grandTotal;
	private Calendar calendar;

	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}

	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public void setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
	}

	public VatReportType getReportType() {
		if (transactionType == InvoiceTransactionType.INTRACOMMUNITY) {
			return VatReportType.INTRACOMMUNITY;
		} 
		if (transactionType == InvoiceTransactionType.EXTRACOMMUNITY) {
			return VatReportType.EXTRACOMMUNITY;
		}
		if (transactionType == InvoiceTransactionType.CAN_CEU_MEL) {
			return VatReportType.CAN_CEU_MEL;
		}
		return VatReportType.GENERAL;
	}

	public Double getPercent() {
		return percent;
	}

	public void setPercent(Double percent) {
		this.percent = percent;
	}

	public double getBase() {
		return base;
	}

	public void setBase(double base) {
		this.base = base;
	}

	public double getQuota() {
		return quota;
	}

	public void setQuota(double quota) {
		this.quota = quota;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getSeriesNumber() {
		return getDocumentNumber();
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getDocumentNumber() {
		String documentNumber = ((InvoiceType.SALES == getInvoiceType()) ? "E" : (InvoiceType.UNDEDUCTIBLE == getInvoiceType()) ? "G" : "R") + "-";
		if (!StringUtils.isEmpty(getSeries())) {
			documentNumber += getSeries() + "/";
		}
		documentNumber += StringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		return documentNumber;
	}


	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
		getCalendar().setTime(date);
		setMonth(getCalendar().get(Calendar.MONTH));
		setYear(getCalendar().get(Calendar.YEAR));
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
		setQuarter( ((month - 1) / 3) + 1);
	}

	public int getQuarter() {
		return quarter;
	}

	public void setQuarter(int quarter) {
		this.quarter = quarter;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Calendar getCalendar() {
		if (this.calendar == null) {
			calendar = Calendar.getInstance();
		}
		return this.calendar;
	}
	
	public InvoiceTransactionType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(InvoiceTransactionType transactionType) {
		this.transactionType = transactionType;
	}

	public boolean isInvestment() {
		return investment;
	}

	public void setInvestment(boolean investment) {
		this.investment = investment;
	}

	public String getVatConcept() {
		if (isInvestment()) {
			return "I";
		} 
		if (getInvoiceType() == InvoiceType.EXPENSES || getInvoiceType() == InvoiceType.UNDEDUCTIBLE) {
			return "G";
		}
		return "B";
	}
	
	public boolean isWithholdingHidden() {
		return withholdingHidden;
	}
	public void setWithholdingHidden(boolean withholdingHidden) {
		this.withholdingHidden = withholdingHidden;
	}

	public boolean isTotal() {
		return total;
	}
	public void setTotal(boolean total) {
		this.total = total;
	}

	public boolean isGrandTotal() {
		return grandTotal;
	}
	public void setGrandTotal(boolean grandTotal) {
		this.grandTotal = grandTotal;
	}
	
	public boolean isFirst() {
		return isGrandTotal() || (!isWithholdingHidden() && !isTotal());
	}
}
