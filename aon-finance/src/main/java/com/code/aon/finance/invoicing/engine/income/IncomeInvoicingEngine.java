package com.code.aon.finance.invoicing.engine.income;

import java.util.List;

import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
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

public class IncomeInvoicingEngine implements IInvoicingEngine {
	
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
			boolean lastIncome = (incomeList.indexOf(income) == (incomeList.size() - 1));
			createInvoiceDetails(invoice, income, lastIncome);
			getInvoicingDAO().updateSource(income, null);
		}
	}

	private void createInvoiceDetails(Invoice invoice, Income income, boolean lastIncome) throws ManagerBeanException {
		int line = calculateMaxLine(invoice);

		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		criteria.addOrder(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_LINE));
		List<ITransferObject> incomeDetailList = incomeDetailBean.getList(criteria);
		for (ITransferObject ito : incomeDetailList) {
			IncomeDetail incomeDetail = (IncomeDetail)ito;
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(incomeDetail.getProject());
			invoiceDetail.setLine(++line);
			invoiceDetail.setItem(incomeDetail.getItem());
			invoiceDetail.setDescription(incomeDetail.getDescription());
			invoiceDetail.setQuantity(incomeDetail.getQuantity());
			invoiceDetail.setPrice(incomeDetail.getPrice());
			invoiceDetail.setDiscountExpression(incomeDetail.getDiscountExpression());
			invoiceDetail.setWorkPlace(income.getWorkPlace());
			invoiceDetail.setSource(InvoiceSource.INCOME);
			invoiceDetail.setSourceId(incomeDetail.getId());
			invoiceDetail.getInvoice().setUpdateEnabled(lastIncome && ((incomeDetailList.lastIndexOf(incomeDetail) + 1) == incomeDetailList.size()));
			getInvoicingDAO().insertInvoiceDetail(invoiceDetail);
			getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());
		}
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