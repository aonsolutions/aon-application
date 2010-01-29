package com.code.aon.finance.vat;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.VatReportType;
import com.code.aon.finance.enumeration.VatType;

public class Vat {
	InvoiceType invoiceType;
	double percent;
	double surcharge;
	double base;
	double vatQuota;
	double surchargeQuota;
	String series;
	int number;
	String reference;
	String document;
	String name;
	Date date;
	Date invoiceDate;
	int month;
	int year;
	int quarter;
	InvoiceTransactionType transactionType;
	VatType vatType;
	boolean investment;

	Calendar calendar;

	public InvoiceType getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
	}
	
	public VatType getVatType() {
		if (vatType == null) {
			if (invoiceType == InvoiceType.SALES) {
				setVatType( VatType.OUTPUT );
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
		if (transactionType == InvoiceTransactionType.INTRACOMUNNITARY) {
			return VatReportType.INTRACOMUNNITARY;
		} 
		if (transactionType == InvoiceTransactionType.EXTRACOMUNNITARY) {
			return VatReportType.EXTRACOMUNNITARY;
		}
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
		if ( StringUtils.isBlank(series) ) {
			return Integer.toString(number);
		} 
		return series + "/" + number;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
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

	
}
