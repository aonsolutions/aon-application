package com.code.aon.finance.bridge.invoicing;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.IProgression;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class SalesInvoicingManager {

	private IPriceStrategy priceStrategy;

	private FinanceGenerator financeGenerator;
	
	private IProgression progression;
	
	public void setProgression(IProgression progression) {
		this.progression = progression;
	}

	private void updateProgress( int current, int total ) {
		if ( this.progression != null ) {
			this.progression.setProgressionCurrentValue(Math.round((current * 100.0)/total));
		}
	}
	
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

	public Invoice invoice(Sales sales, String series, int number, Date issueDate) throws ManagerBeanException {
		updateSalesStatus(sales);
		Invoice invoice = createInvoice(sales, series, number, issueDate);
		createInvoiceDetails(invoice, sales);
		double invoiceTotal = getPriceStrategy().getTotalPrice(invoice, invoice);
		if (invoiceTotal != 0) {
			if (sales.getPayMethod() != null && sales.getPayMethod().getId() != null) {
				getFinanceGenerator().generateFinances(invoice, sales, invoiceTotal, true);
			} else {
				getFinanceGenerator().generateFinances(invoice, invoiceTotal, true);
			}
		}
		return invoice;
	}

	private void updateSalesStatus(Sales sales) throws ManagerBeanException {
		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		sales.setStatus(SalesStatus.INVOICED);
		salesBean.restoreNullSubPOJOs(sales);
		salesBean.update(sales);
	}

	private Invoice createInvoice(Sales sales, String series, int number, Date issueDate) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setProject(sales.getProject());
		invoice.setSeries(series);
		invoice.setNumber((number > 0) ? number : obtainMaxNumber(series));
		invoice.setRegistry(sales.getCustomer().getRegistry());
		invoice.setRegistryDocument(sales.getCustomer().getRegistry().getDocument());
		invoice.setRegistryDocumentType(sales.getCustomer().getRegistry().getDocumentType());
		invoice.setRegistryDocumentCountry(sales.getCustomer().getRegistry().getDocumentCountry());
		invoice.setRegistryName(sales.getCustomer().getRegistry().getFullName());
		invoice.setRegistryAddress(sales.getShippingAddress());
		invoice.setIssueDate(issueDate);
		invoice.setTaxDate(issueDate);
		invoice.setSecurityLevel(sales.getSecurityLevel());
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(sales.getScope());
		invoice.setComments(StringUtils.isNotBlank(sales.getPurchaseReference())?"Ref. compra: "+sales.getPurchaseReference():null);

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
    	return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	private void createInvoiceDetails(Invoice invoice, Sales sales) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
		criteria.addOrder(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_LINE));
		List<ITransferObject> list = salesDetailBean.getList(criteria);
		for( int i = 0; i < list.size(); i++ ) {
			SalesDetail salesDetail = (SalesDetail) list.get(i);
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setProject(sales.getProject());
			invoiceDetail.setLine(salesDetail.getLine());
			invoiceDetail.setItem(salesDetail.getItem());
			invoiceDetail.setDescription(salesDetail.getDescription());
			invoiceDetail.setQuantity(salesDetail.getQuantity());
			invoiceDetail.setPrice(salesDetail.getPrice());
			invoiceDetail.setDiscountExpression(salesDetail.getDiscountExpression());
			invoiceDetail.setWorkPlace(sales.getWorkPlace());
			invoiceDetail.setSource(InvoiceSource.SALES);
			invoiceDetail.setSourceId(salesDetail.getId());
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetailBean.insert(invoiceDetail);
			updateProgress(i+1, list.size());
		}
	}

}
