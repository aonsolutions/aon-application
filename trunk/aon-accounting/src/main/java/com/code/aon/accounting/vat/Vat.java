package com.code.aon.accounting.vat;

import java.util.Calendar;
import java.util.Date;

import com.code.aon.config.enumeration.TaxType;

public class Vat {
	TaxType taxType;
	double percent;
	double surcharge;
	double base;
	String series;
	int number;
	String reference;
	String document;
	String name;
	Date date;
	int month;
	int year;

	int quarter;
	Calendar calendar;

	public TaxType getTaxType() {
		return taxType;
	}

	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
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

	public double getVatQuota() {
		return round(getBase() * (getPercent() + getSurcharge()) / 100);
	}

	public double getTotal() {
		return round(getBase() + getVatQuota());
	}
	
	
	public Calendar getCalendar() {
		if (this.calendar == null) {
			calendar = Calendar.getInstance();
		}
		return this.calendar;
	}
	
	private double round(double value) {
		double decimal = Math.pow(10, 2);
		return Math.round(decimal * value) / decimal;
	}
}
