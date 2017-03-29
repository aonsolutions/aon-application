package com.code.aon.fiscal.vat;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.fiscal.enumeration.VatReportType;
import com.code.aon.fiscal.enumeration.VatType;

public class Vat implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private InvoiceType invoiceType;
	private double percent;
	private double surcharge;
	private double base;
	private double vatQuota;
	private double surchargeQuota;
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
	private VatType vatType;
	private boolean investment;
	private Integer invoiceId;
	private boolean vatAccrualPayment;

	Calendar calendar;

	public Integer getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}
	
	public VatType getVatType() {
		if (vatType == null) {
			if (invoiceType == InvoiceType.SALES) {
				setVatType( isInvestment()?VatType.INVESTMENT:VatType.OUTPUT );
			} else {
				setVatType( isInvestment()?VatType.INVESTMENT:VatType.INPUT);
			}
		}
		return vatType;
	}
	public void setVatType(VatType vatType) {
		this.vatType = vatType;
	}

	public VatReportType getReportType() {
		for (VatReportType type : VatReportType.values()) {
			if (type.accept(transactionType)) {
				return type;
			}
		}
		// By default
		return VatReportType.GENERAL;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public double getSurcharge() {
		return surcharge;
	}

	public void setSurcharge(double surcharge) {
		this.surcharge = surcharge;
	}

	public double getBase() {
		return base;
	}

	public void setBase(double base) {
		this.base = base;
	}

	public double getVatQuota() {
		return vatQuota;
	}

	public void setVatQuota(double vatQuota) {
		this.vatQuota = vatQuota;
	}

	public double getSurchargeQuota() {
		return surchargeQuota;
	}

	public void setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
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

	public double getTotal() {
		return CommonUtil.round(getBase() + getVatQuota() + getSurchargeQuota());
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

	public String getNature() {
		return (getVatType() == VatType.OUTPUT)?"R":"S";
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
	public boolean isVatAccrualPayment() {
		return vatAccrualPayment;
	}
	public void setVatAccrualPayment(boolean vatAccrualPayment) {
		this.vatAccrualPayment = vatAccrualPayment;
	}
	
}
