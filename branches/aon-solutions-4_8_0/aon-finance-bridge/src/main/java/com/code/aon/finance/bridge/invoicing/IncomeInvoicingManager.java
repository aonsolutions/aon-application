package com.code.aon.finance.bridge.invoicing;

import java.util.Date;
import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

public class IncomeInvoicingManager {

	private IPriceStrategy priceStrategy;

	private FinanceGenerator financeGenerator;

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public FinanceGenerator getFinanceGenerator() {
		if (financeGenerator == null) {
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}

	public Invoice invoice(Income income, String referenceCode, Date issueDate) throws ManagerBeanException {
		updateIncomeStatus(income);
		Invoice invoice = createInvoice(income, referenceCode, issueDate);
		createInvoiceDetails(invoice, income);
		if (income.getPayMethod() != null && income.getPayMethod().getId() != null) {
			getFinanceGenerator().generateFinances(invoice, income, getPriceStrategy().getTotalPrice(invoice, invoice), true);
		} else {
			getFinanceGenerator().generateFinances(invoice, getPriceStrategy().getTotalPrice(invoice, invoice), true);
		}
		return invoice;
	}

	private void updateIncomeStatus(Income income) throws ManagerBeanException {
		IManagerBean incomeBean = BeanManager.getManagerBean(Income.class);
		income.setStatus(IncomeStatus.INVOICED);
		incomeBean.restoreNullSubPOJOs(income);
		incomeBean.update(income);
	}

	private Invoice createInvoice(Income income, String referenceCode, Date issueDate) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setReferenceCode(referenceCode);
		invoice.setRegistry(income.getSupplier().getRegistry());
		invoice.setRegistryDocument(income.getSupplier().getRegistry().getDocument());
		invoice.setRegistryName(income.getSupplier().getRegistry().getFullName());
		invoice.setRegistryAddress(income.getRegistryAddress());
		invoice.setIssueDate(issueDate);
		invoice.setTaxDate(issueDate);
		invoice.setSecurityLevel(income.getSecurityLevel());
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.PURCHASE);
		invoice.setScope(income.getScope());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private void createInvoiceDetails(Invoice invoice, Income income) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		criteria.addOrder(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_LINE));
		Iterator<?> iterator = incomeDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			IncomeDetail incomeDetail = (IncomeDetail)iterator.next();
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setLine(incomeDetail.getLine());
			invoiceDetail.setItem(incomeDetail.getItem());
			invoiceDetail.setDescription(incomeDetail.getDescription());
			invoiceDetail.setQuantity(incomeDetail.getQuantity());
			invoiceDetail.setPrice(incomeDetail.getPrice());
			invoiceDetail.setDiscountExpression(incomeDetail.getDiscountExpression());
			invoiceDetail.setWorkPlace(income.getWorkPlace());
			invoiceDetail.setSource(InvoiceSource.INCOME);
			invoiceDetail.setSourceId(incomeDetail.getId());
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetailBean.insert(invoiceDetail);
		}
	}

}
