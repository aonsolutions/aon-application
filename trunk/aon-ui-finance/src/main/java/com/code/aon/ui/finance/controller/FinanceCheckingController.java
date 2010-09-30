package com.code.aon.ui.finance.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.ui.util.AonUtil;

public class FinanceCheckingController {

	private List<Invoice> noFinanceInvoiceList;
	private List<Invoice> notEqualAmountInvoiceList;
	private Date fromDate;
	private Date toDate;
	private IPriceStrategy priceStrategy;
	private String reportName;
	private static final String bundle = "financeBundle";

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

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public void onWrongFinanceList(ActionEvent e) throws ManagerBeanException {

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean
				.getFieldName(IFinanceAlias.INVOICE_STATUS),
				InvoiceStatus.PENDING);
		if (this.getFromDate() != null) {
			criteria.addGreaterThanOrEqualExpression(invoiceBean
					.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE),
					this.fromDate);
		}
		if (this.getToDate() != null) {
			criteria.addLessThanOrEqualExpression(invoiceBean
					.getFieldName(IFinanceAlias.INVOICE_ISSUE_DATE),
					this.toDate);
		}
		Iterator<ITransferObject> iterator = invoiceBean.getList(criteria)
				.iterator();
		notEqualAmountInvoiceList = new LinkedList<Invoice>();
		while (iterator.hasNext()) {
			Double invoiceTotal = 0.0;
			Double financeTotal = 0.0;
			Invoice inv = (Invoice) iterator.next();
			if (InvoiceType.UNDEDUCTIBLE == inv.getType()) {
				invoiceTotal = getPriceStrategy().getTaxableBase(
						(ICalculableContainer) inv);
			} else {
				invoiceTotal = getPriceStrategy().getTotalPrice(
						(ICalculableContainer) inv, (ITaxInfo) inv);
			}
			IManagerBean financeBean = BeanManager
					.getManagerBean(Finance.class);
			Criteria cri = new Criteria();
			cri
					.addEqualExpression(
							financeBean
									.getFieldName(IFinanceAlias.FINANCE_INVOICE_REFERENCE_CODE),
							inv.getReferenceCode());
			Iterator<ITransferObject> iter = financeBean.getList(cri)
					.iterator();
			while (iter.hasNext()) {
				Finance finance = (Finance) iter.next();
				financeTotal += finance.getAmount();
			}
			if (!invoiceTotal.equals(financeTotal)) {
				notEqualAmountInvoiceList.add(inv);
			}
		}
		if (noFinanceInvoiceList.size() == 0) {
			String msg = "No hay facturas con vencimientos erroneos";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		setReportName(AonUtil.getMessage(bundle,
				"finance_invoice_checking_module_wrong_finance"));
	}

	@SuppressWarnings("unchecked")
	public void onNoFinanceList(ActionEvent e) {

		String dateCriteria = "";
		if (this.getFromDate() != null) {
			dateCriteria = " Invoice.issueDate >= '"
					+ new java.sql.Date(this.getFromDate().getTime())
							.toString() + "' AND";
		}
		if (this.getToDate() != null) {
			dateCriteria = dateCriteria + " Invoice.issueDate <= '"
					+ new java.sql.Date(this.getToDate().getTime()).toString()
					+ "' AND ";
		}
		String select = "select Invoice "
				+ "from Invoice as Invoice "
				+ "where "
				+ dateCriteria
				+ " Invoice.referenceCode not in (select Finance.invoice.referenceCode from Finance as Finance)";
		Session session = HibernateUtil.getSession(HibernateUtil
				.getSessionFactoryName());
		Query query = session.createQuery(select);
		noFinanceInvoiceList = query.list();

		setReportName(AonUtil.getMessage(bundle,
				"finance_invoice_checking_module_no_finance"));
		if (noFinanceInvoiceList.size() == 0) {
			String msg = "No hay facturas sin vencimientos";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public List<Invoice> getNotEqualAmountInvoiceList()
			throws ManagerBeanException {
		return notEqualAmountInvoiceList;
	}

	public void setNotEqualAmountInvoiceList(
			List<Invoice> notEqualAmountInvoiceList) {
		this.notEqualAmountInvoiceList = notEqualAmountInvoiceList;
	}

	public List<Invoice> getNoFinanceInvoiceList() {
		return noFinanceInvoiceList;
	}

	public void setNoFinanceInvoiceList(List<Invoice> noFinanceInvoiceList) {
		this.noFinanceInvoiceList = noFinanceInvoiceList;
	}

}