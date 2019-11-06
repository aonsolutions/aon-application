package com.esferalia.aon.occam.api.model.finance.utilities;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.InvoiceType;

public class FinanceUtilitiesParams implements Serializable {

	private static final long serialVersionUID = 534612320441436788L;

	private InvoiceType invoiceType;
	private Date fromDate;
	private Date toDate;

	public InvoiceType getInvoiceType() {
		return invoiceType;
	}
	public FinanceUtilitiesParams setInvoiceType(InvoiceType invoiceType) {
		this.invoiceType = invoiceType;
		return this;
	}

	public Date getFromDate() {
		return fromDate;
	}
	public FinanceUtilitiesParams setFromDate(Date fromDate) {
		this.fromDate = fromDate;
		return this;
	}

	public Date getToDate() {
		return toDate;
	}
	public FinanceUtilitiesParams setToDate(Date toDate) {
		this.toDate = toDate;
		return this;
	}

}
