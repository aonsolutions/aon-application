package com.code.aon.finance.bridge.invoicing;

import java.util.Date;
import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;

public class RectificationInvoicingManager {

	private IPriceStrategy priceStrategy;

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}

	public Invoice rectifyInvoice(Invoice invoice, String series, int number, Date issueDate, String cause) throws ManagerBeanException {
		Invoice rectifier = createRectifierInvoice(invoice, series, number, issueDate, cause);
		createRectifierInvoiceDetails(rectifier, invoice);
		createRectifierInvoiceFinances(rectifier, invoice);
		updateRectifiedInvoice(rectifier, invoice);
		return rectifier;
	}

	private Invoice createRectifierInvoice(Invoice invoice, String series, int number, Date issueDate, String cause) throws ManagerBeanException {
		Invoice rectifier = new Invoice();
		rectifier.setSeries(series);
		rectifier.setNumber((number > 0) ? number : obtainMaxNumber(series));
		rectifier.setRegistry(invoice.getRegistry());
		rectifier.setRegistryDocument(invoice.getRegistryDocument());
		rectifier.setRegistryDocumentType(invoice.getRegistryDocumentType());
		rectifier.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
		rectifier.setRegistryName(invoice.getRegistryName());
		rectifier.setRegistryAddress(invoice.getRegistryAddress());
		rectifier.setIssueDate(issueDate);
		rectifier.setTaxDate(issueDate);
		rectifier.setSecurityLevel(invoice.getSecurityLevel());
		rectifier.setComments(cause);
		rectifier.setRectificationType(RectificationType.NORMAL_RECTIFIER);
		rectifier.setRectificationInvoice(invoice);
		rectifier.setService(invoice.isService());
		rectifier.setStatus(InvoiceStatus.PENDING);
		rectifier.setType(invoice.getType());
		rectifier.setScope(invoice.getScope());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(rectifier);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
    	return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	private void createRectifierInvoiceDetails(Invoice rectifier, Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		criteria.addOrder(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_LINE));
		Iterator<?> iterator = invoiceDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
			invoiceDetail.setId(null);
			invoiceDetail.setInvoice(rectifier);
			invoiceDetail.setQuantity(CommonUtil.round(0 - invoiceDetail.getQuantity(), 3));
			invoiceDetail.setSource(obtainRectifierSource(invoiceDetail.getSource()));
			invoiceDetail.setSourceId(null);
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetailBean.insert(invoiceDetail);
		}
	}

	private InvoiceSource obtainRectifierSource(InvoiceSource source) {
		if (source == InvoiceSource.DELIVERY) {
			source = InvoiceSource.DIRECT_SALES;
		} else if (source == InvoiceSource.INCOME) {
			source = InvoiceSource.DIRECT_PURCHASE;
		} else if (source == InvoiceSource.OFFER) {
			source = InvoiceSource.DIRECT_INVOICE;
		} else if (source == InvoiceSource.FEE) {
			source = InvoiceSource.DIRECT_INVOICE;
		}
		return source;
	}

	private void createRectifierInvoiceFinances(Invoice rectifier, Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		criteria.addOrder(financeBean.getFieldName(IFinanceAlias.FINANCE_ID));
		Iterator<?> iterator = financeBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			finance.setId(null);
			finance.setInvoice(rectifier);
			finance.setConcept(finance.getInvoice().getDocumentNumber()); 
			finance.setAmount(CommonUtil.round(0 - finance.getTotalAmount()));
			finance.setFinanceStatus(FinanceStatus.PENDING);
			financeBean.insert(finance);
		}
	}

	private void updateRectifiedInvoice(Invoice rectifier, Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoice.setRectificationInvoice(invoice.getRectificationType() == RectificationType.NONE ? rectifier : null);
		invoice.setRectificationType(RectificationType.RECTIFIED);
		invoiceBean.restoreNullSubPOJOs(invoice);
		invoiceBean.update(invoice);
	}

}
