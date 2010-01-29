package com.code.aon.finance.invoicing;

import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Series;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;

public class InvoicingParameters {
	
	private Month month;
	
	private int year;
	
	private Series series;
	
	private int number;
	
	private Date invoiceDate;
	
	private Customer customer;
	
	private Item item;
	
	private SecurityLevel securityLevel;
	
	private WorkPlace workPlace;
	
	private boolean invoiceRecordable;
	
	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Series getSeries() {
		return series;
	}

	public void setSeries(Series series) {
		this.series = series;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public boolean isInvoiceRecordable() {
		return invoiceRecordable;
	}

	public void setInvoiceRecordable(boolean invoiceRecordable) {
		this.invoiceRecordable = invoiceRecordable;
	}
}