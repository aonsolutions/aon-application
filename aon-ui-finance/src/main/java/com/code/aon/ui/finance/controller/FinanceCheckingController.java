package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_BUNDLE;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_CHECKING_MODULE_NO_FINANCE;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_CHECKING_MODULE_WRONG_FINANCE;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceCheckingController {

	private String reportName;
	private Date fromDate;
	private Date toDate;
	private List<Invoice> noFinanceInvoiceList;
	private List<Invoice> notEqualAmountInvoiceList;
	private IPriceStrategy priceStrategy;

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
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

	public List<Invoice> getNoFinanceInvoiceList() {
		return noFinanceInvoiceList;
	}

	public void setNoFinanceInvoiceList(List<Invoice> noFinanceInvoiceList) {
		this.noFinanceInvoiceList = noFinanceInvoiceList;
	}

	public List<Invoice> getNotEqualAmountInvoiceList() {
		return notEqualAmountInvoiceList;
	}

	public void setNotEqualAmountInvoiceList(List<Invoice> notEqualAmountInvoiceList) {
		this.notEqualAmountInvoiceList = notEqualAmountInvoiceList;
	}

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	@SuppressWarnings("unchecked")
	public void onNoFinanceList(ActionEvent event) {
		String dateCriteria = "";
		if (getFromDate() != null) {
			dateCriteria = "AND invoice.issueDate >= '" + new java.sql.Date(getFromDate().getTime()).toString() + "' ";
		}
		if (getToDate() != null) {
			dateCriteria = dateCriteria + "AND invoice.issueDate <= '" + new java.sql.Date(getToDate().getTime()).toString()+ "' ";
		}
		String select = "SELECT invoice " +
						"FROM Invoice invoice " +
						"WHERE " + DomainManager.getSQLWhereClause("invoice.domain") +
						" AND invoice.id NOT IN (SELECT finance.invoice.id FROM Finance finance WHERE finance.invoice IS NOT NULL) " +
						dateCriteria +
						" ORDER BY invoice.issueDate, invoice.referenceCode";
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query query = session.createQuery(select);
		noFinanceInvoiceList = query.list();

		setReportName(AonUtil.getMessage(FINANCE_BUNDLE, FINANCE_INVOICE_CHECKING_MODULE_NO_FINANCE));
		if (noFinanceInvoiceList.size() == 0) {
			AonUtil.addInfoMessage("No hay facturas sin vencimientos");
		}
	}

	public void onWrongFinanceList(ActionEvent event) throws ManagerBeanException {
		notEqualAmountInvoiceList = new LinkedList<Invoice>();

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_STATUS), InvoiceStatus.PENDING);
		if (getFromDate() != null) {
			criteria.addGreaterThanOrEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), getFromDate());
		}
		if (getToDate() != null) {
			criteria.addLessThanOrEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE), getToDate());
		}
		criteria.addOrder(invoiceBean.getFieldName(IEntityAlias.INVOICE_ISSUE_DATE));
		criteria.addOrder(invoiceBean.getFieldName(IEntityAlias.INVOICE_REFERENCE_CODE));
		Iterator<ITransferObject> invoiceIter = invoiceBean.getList(criteria).iterator();
		while (invoiceIter.hasNext()) {
			double invoiceTotal = 0;
			Invoice invoice = (Invoice)invoiceIter.next();
			if (InvoiceType.UNDEDUCTIBLE == invoice.getType()) {
				invoiceTotal = getPriceStrategy().getTaxableBase(invoice);
			} else {
				invoiceTotal = getPriceStrategy().getTotalPrice(invoice, invoice);
			}

			double financeTotal = 0;
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
			Iterator<ITransferObject> financeIter = financeBean.getList(criteria).iterator();
			while (financeIter.hasNext()) {
				Finance finance = (Finance)financeIter.next();
				financeTotal += finance.getAmount();
			}

			if (invoiceTotal != financeTotal) {
				notEqualAmountInvoiceList.add(invoice);
			}
		}

		setReportName(AonUtil.getMessage(FINANCE_BUNDLE, FINANCE_INVOICE_CHECKING_MODULE_WRONG_FINANCE));
		if (notEqualAmountInvoiceList.size() == 0) {
			AonUtil.addInfoMessage("No hay facturas con vencimientos erroneos");
		}
	}

}