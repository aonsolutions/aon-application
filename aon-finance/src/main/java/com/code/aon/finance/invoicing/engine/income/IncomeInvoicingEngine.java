package com.code.aon.finance.invoicing.engine.income;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.IInvoicingFeedBack;
import com.code.aon.finance.invoicing.InvoicingParameters;
import com.code.aon.finance.invoicing.engine.IInvoicingDAO;
import com.code.aon.finance.invoicing.engine.IInvoicingEngine;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class IncomeInvoicingEngine implements IInvoicingEngine, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private IInvoicingDAO invoicingDAO;
	private IInvoicingFeedBack invoicingFeedBack;
	private Session session;
	
	public IInvoicingDAO getInvoicingDAO() {
		return invoicingDAO;
	}

	public void setInvoicingDAO(IInvoicingDAO invoicingDAO) {
		this.invoicingDAO = invoicingDAO;
	}

	public IInvoicingFeedBack getInvoicingFeedBack() {
		return invoicingFeedBack;
	}

	public void setInvoicingFeedBack(IInvoicingFeedBack invoicingFeedBack) {
		this.invoicingFeedBack = invoicingFeedBack; 
	}

	public Session getHibernateSession() {
		return session;
	}

	public void setHibernateSession(Session session) {
		this.session = session; 
	}

	public void invoice(InvoicingParameters params) throws ManagerBeanException {
	}

	public void invoiceIncomeList(Invoice invoice, List<Income> incomeList) throws ManagerBeanException {
		for (Income income : incomeList) {
			List<?> incomeDetailList = income.getOrderedDetailList();
			if (incomeDetailList.size() > 0) {
				InvoiceDetail invoiceDetail = createInvoiceDetails(incomeDetailList, invoice);
				if (invoiceDetail != null) {
					income = (Income)getHibernateSession().merge(income);
					getInvoicingDAO().updateSource(income, null);
				}
			}
		}
	}

	private InvoiceDetail createInvoiceDetails(List<?> incomeDetailList, Invoice invoice) throws ManagerBeanException {
		InvoiceDetail invoiceDetail = null;
		int line = calculateMaxLine(invoice);
		for (Object obj : incomeDetailList) {
			IncomeDetail incomeDetail = (IncomeDetail)obj;
			invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(incomeDetail.getProject());
			invoiceDetail.setLine(++line);
			invoiceDetail.setItem(incomeDetail.getItem());
			invoiceDetail.setDescription(incomeDetail.getDescription());
			invoiceDetail.setQuantity(incomeDetail.getQuantity());
			invoiceDetail.setPrice(incomeDetail.getPrice());
			invoiceDetail.setDiscountExpression(incomeDetail.getDiscountExpression());
			invoiceDetail.setWorkPlace(incomeDetail.getIncome().getWorkPlace());
			invoiceDetail.setSource(InvoiceSource.INCOME);
			invoiceDetail.setSourceId(incomeDetail.getId());
			invoiceDetail.getInvoice().setUpdateEnabled(incomeDetailList.lastIndexOf(incomeDetail) == (incomeDetailList.size() - 1));

			getInvoicingDAO().insertInvoiceDetail(invoiceDetail);
			getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());
		}
		return invoiceDetail;
	}

	private	Integer calculateMaxLine(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Projection projection = Projection.max(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_LINE));
		Object value = invoiceDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) : 0;
	}

}