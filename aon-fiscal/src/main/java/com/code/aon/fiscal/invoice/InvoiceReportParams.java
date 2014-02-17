package com.code.aon.fiscal.invoice;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceReportParams {

	private int domain;
	private Date date;
	private Date fromTaxDate;
	private Date toTaxDate;
	private Date fromInvoiceDate;
	private Date toInvoiceDate;
	private SecurityLevel securityLevel;
    private TaxType taxType;
    private boolean accrualVatVisible;

	
	private List<InvoiceReportParamsDetail> details;
	
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	
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
	    setTaxType(null);
	    details = null;
	}
	
	public Criteria getCriteria() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new  Criteria();
		if (getFromInvoiceDate() != null) {
			criteria.addGreaterThanExpression(bean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), getFromInvoiceDate());
		}
		if (getToInvoiceDate() != null ) {
			criteria.addLessThanExpression(bean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), getToInvoiceDate());
		}
		if (getFromTaxDate() != null) {
			criteria.addGreaterThanExpression(bean.getFieldName(IEntityAlias.INVOICE_TAX_DATE), getFromTaxDate());
		}
		if (getToTaxDate() != null) {
			criteria.addLessThanExpression(bean.getFieldName(IEntityAlias.INVOICE_TAX_DATE), getToTaxDate());
		}
		if (getSecurityLevel() != null) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_SECURITY_LEVEL), getSecurityLevel());
		}
		return criteria;
	}
	
	public List<InvoiceReportParamsDetail> getDetails() {
		if (details == null) {
			setDetails( new LinkedList<InvoiceReportParamsDetail>());
		}
		return details;
	}
	public void setDetails(List<InvoiceReportParamsDetail> details) {
		this.details = details;
	}
	
	public void addDetail(InvoiceReportParamsDetail detail ) {
		getDetails().add(detail);
	}
	
	public TaxType getTaxType() {
		return taxType;
	}
	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
	}
	public boolean isAccrualVatVisible() {
		return accrualVatVisible;
	}
	public void setAccrualVatVisible(boolean accrualVatVisible) {
		this.accrualVatVisible = accrualVatVisible;
	}
}
