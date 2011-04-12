package com.code.aon.ui.fiscal.controller;

import java.util.Calendar;
import java.util.Date;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;

public class InvoiceReportParams {

	private Date date;
	private Date fromTaxDate;
	private Date toTaxDate;
	private Date fromInvoiceDate;
	private Date toInvoiceDate;
	private SecurityLevel securityLevel;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public Date getFromTaxDate() {
		return fromTaxDate;
	}
	public void setFromTaxDate(Date fromTaxDate) {
		this.fromTaxDate = fromTaxDate;
	}

	public Date getToTaxDate() {
		return toTaxDate;
	}
	public void setToTaxDate(Date toTaxDate) {
		this.toTaxDate = toTaxDate;
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


	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	public void reset() {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		setDate(c.getTime());
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		setFromTaxDate(c.getTime());	
		setFromInvoiceDate(c.getTime());	
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DAY_OF_MONTH, 31);
		setToTaxDate(c.getTime());
		setToInvoiceDate(c.getTime());
		setSecurityLevel(AonUtil.getRoleManager().isConfidentiality()?null:SecurityLevel.OFFICIAL);
	}
	
	public Criteria getCriteria() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new  Criteria();
		if (getFromInvoiceDate() != null) {
			criteria.addGreaterThanExpression(bean.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), getFromInvoiceDate());
		}
		if (getToInvoiceDate() != null ) {
			criteria.addLessThanExpression(bean.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE), getToInvoiceDate());
		}
		if (getFromTaxDate() != null) {
			criteria.addGreaterThanExpression(bean.getFieldName(IFinanceAlias.INVOICE_TAX_DATE), getFromTaxDate());
		}
		if (getToTaxDate() != null) {
			criteria.addLessThanExpression(bean.getFieldName(IFinanceAlias.INVOICE_TAX_DATE), getToTaxDate());
		}
		if (getSecurityLevel() != null) {
			criteria.addEqualExpression(bean.getFieldName(IFinanceAlias.INVOICE_SECURITY_LEVEL), getSecurityLevel());
		}
		return criteria;
	}
}
