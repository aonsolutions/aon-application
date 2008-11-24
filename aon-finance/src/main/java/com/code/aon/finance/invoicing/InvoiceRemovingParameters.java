package com.code.aon.finance.invoicing;

import java.util.Date;

import com.code.aon.registry.Registry;

public class InvoiceRemovingParameters {
	
	private Date fromDate;

	private Date toDate;
	
	private Integer fromNumber;
	
	private Integer toNumber;
	
	private Registry registry;

	public InvoiceRemovingParameters() {
		this.registry = new Registry();
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

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

}