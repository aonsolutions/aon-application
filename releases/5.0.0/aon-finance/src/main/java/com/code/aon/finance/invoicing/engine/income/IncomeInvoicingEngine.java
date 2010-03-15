package com.code.aon.finance.invoicing.engine.income;

import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.IInvoicingFeedBack;
import com.code.aon.finance.invoicing.InvoicingParameters;
import com.code.aon.finance.invoicing.engine.IInvoicingDAO;
import com.code.aon.finance.invoicing.engine.IInvoicingEngine;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class IncomeInvoicingEngine implements IInvoicingEngine {
	
	private IInvoicingDAO invoicingDAO;
	
	private IInvoicingFeedBack invoicingFeedBack;
	
	public IInvoicingDAO getInvoicingDAO() {
		return invoicingDAO;
	}

	public IInvoicingFeedBack getInvoicingFeedBack() {
		return invoicingFeedBack;
	}

	public void setInvoicingDAO(IInvoicingDAO invoicingDAO) {
		this.invoicingDAO = invoicingDAO;
	}

	public void setInvoicingFeedBack(IInvoicingFeedBack invoicingFeedBack) {
		this.invoicingFeedBack = invoicingFeedBack; 
	}

	public void invoice(InvoicingParameters params) throws ManagerBeanException {
	}

	public void invoiceIncomeList(Invoice invoice, List<Income> incomeList) throws ManagerBeanException {
		for (Income income : incomeList) {
			createInvoiceDetails(invoice, income);
			getInvoicingDAO().updateSource(income);
		}
	}

	private void createInvoiceDetails(Invoice invoice, Income income) throws ManagerBeanException {
		int line = calculateMaxLine(invoice);

		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		criteria.addOrder(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_LINE));
		Iterator<?> iterator = incomeDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			IncomeDetail incomeDetail = (IncomeDetail)iterator.next();
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setLine(++line);
			invoiceDetail.setItem(incomeDetail.getItem());
			invoiceDetail.setDescription(incomeDetail.getDescription());
			invoiceDetail.setQuantity(incomeDetail.getQuantity());
			invoiceDetail.setPrice(incomeDetail.getPrice());
			invoiceDetail.setDiscountExpression(incomeDetail.getDiscountExpression());
			invoiceDetail.setWorkPlace(income.getWorkPlace());
			invoiceDetail.setSource(InvoiceSource.INCOME);
			invoiceDetail.setSourceId(incomeDetail.getId());
			getInvoicingDAO().insertInvoiceDetail(invoiceDetail);
			getInvoicingFeedBack().addMessage("\t \t" + "InvoiceDetail: " + invoiceDetail.getDescription() + " price= " + invoiceDetail.getTaxableBase());
		}
	}

	private	Integer calculateMaxLine(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Projection projection = Projection.max(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_LINE));
		Object value = invoiceDetailBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) : 0;
	}

}