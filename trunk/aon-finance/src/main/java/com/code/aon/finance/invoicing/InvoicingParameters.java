package com.code.aon.finance.invoicing;

import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Series;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.product.ProductCategory;

public class InvoicingParameters {
	
	private Customer customer;
	private Item item;
	private ProductCategory category;
	private Month month;
	private int year;
	private Series series;
	private Integer fromNumber;
	private Integer toNumber;
	private Date fromDate;
	private Date toDate;
	private boolean confidential;
	private WorkPlace workPlace;

	private Series invoiceSeries;
	private int invoiceNumber;
	private Date invoiceDate;
	private boolean invoiceRecordable;
	private String invoiceComments;
	
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

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

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

	public Integer getFromNumber() {
		return fromNumber;
	}

	public void setFromNumber(Integer fromNumber) {
		this.fromNumber = fromNumber;
	}
	
	public Integer getToNumber() {
		return toNumber;
	}

	public void setToNumber(Integer toNumber) {
		this.toNumber = toNumber;
	}
	
	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public boolean isConfidential() {
		return confidential;
	}

	public void setConfidential(boolean confidential) {
		this.confidential = confidential;
	}

	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public Series getInvoiceSeries() {
		return invoiceSeries;
	}

	public void setInvoiceSeries(Series invoiceSeries) {
		this.invoiceSeries = invoiceSeries;
	}

	public int getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(int invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public boolean isInvoiceRecordable() {
		return invoiceRecordable;
	}

	public void setInvoiceRecordable(boolean invoiceRecordable) {
		this.invoiceRecordable = invoiceRecordable;
	}

	public String getInvoiceComments() {
		return invoiceComments;
	}

	public void setInvoiceComments(String invoiceComments) {
		this.invoiceComments = invoiceComments;
	}

}