package com.code.aon.finance.model347;

import java.util.Date;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.enumeration.Model347ReportOrder;
import com.code.aon.finance.enumeration.Model347Type;

public class Model347Parameters {

	private Date date;
	private Date fromDate;
	private Date toDate;
	private Date fromInvoiceDate;
	private Date toInvoiceDate;
	private Model347Type type;
	private Model347ReportOrder order;
	private SecurityLevel securityLevel;
	private Double minimunAmount;

	public Model347Parameters() {
		setDate(new Date());
		setFromDate(null);
		setToDate(null);
		setType(Model347Type.ALL);
		setOrder(Model347ReportOrder.INVOICE_REGISTRY_DOCUMENT);
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
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

	public Date getFromInvoiceDate() {
		return fromInvoiceDate;
	}
	public void setFromInvoiceDate(Date fromInvoiceDate) {
		this.fromInvoiceDate = fromInvoiceDate;
	}

	public Date getToInvoiceDate() {
		return toInvoiceDate;
	}
	public void setToInvoiceDate(Date toInvoiceDate) {
		this.toInvoiceDate = toInvoiceDate;
	}

	public Model347Type getType() {
		return type;
	}
	public void setType(Model347Type type) {
		this.type = type;
	}

	public Model347ReportOrder getOrder() {
		return order;
	}
	public void setOrder(Model347ReportOrder order) {
		this.order = order;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public Double getMinimunAmount() {
		return minimunAmount;
	}
	public void setMinimunAmount(Double minimunAmount) {
		this.minimunAmount = minimunAmount;
	}
}
