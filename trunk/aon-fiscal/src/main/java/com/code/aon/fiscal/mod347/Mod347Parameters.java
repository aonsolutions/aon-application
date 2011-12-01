package com.code.aon.fiscal.mod347;


import java.util.Date;

import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Mod347ReportOrder;
import com.code.aon.fiscal.enumeration.Mod347Type;

public class Mod347Parameters {

	private Date date;
	private Date fromDate;
	private Date toDate;
	private Date fromInvoiceDate;
	private Date toInvoiceDate;
	private Administration administration;
	private Integer period;
	private Mod347Type type;
	private Mod347ReportOrder order;
	private SecurityLevel securityLevel;
	private Double minimunAmount;

	public Mod347Parameters() {
		setDate(new Date());
		setPeriod(CommonUtil.getYear(getDate()));
		setFromDate(null);
		setToDate(null);
		setType(null);
		setOrder(Mod347ReportOrder.INVOICE_REGISTRY_DOCUMENT);
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

	public Administration getAdministration() {
		return administration;
	}

	public void setAdministration(Administration administration) {
		this.administration = administration;
	}

	public Integer getPeriod() {
		return period;
	}

	public void setPeriod(Integer period) {
		this.period = period;
	}

	public Mod347Type getType() {
		return type;
	}
	public void setType(Mod347Type type) {
		this.type = type;
	}

	public Mod347ReportOrder getOrder() {
		return order;
	}
	public void setOrder(Mod347ReportOrder order) {
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
