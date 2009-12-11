package com.code.aon.finance.bridge.invoicing;

import java.util.Date;
import java.util.Iterator;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.bridge.util.SalesBridgeUtil;

public class OfferInvoicingManager {

	private SalesBridgeUtil salesBridgeUtil;

	private IPriceStrategy priceStrategy;

	private FinanceGenerator financeGenerator;

	public SalesBridgeUtil getSalesBridgeUtil() {
		if (salesBridgeUtil == null) {
			salesBridgeUtil = new SalesBridgeUtil();
		}
		return salesBridgeUtil;
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

	public Invoice invoice(Offer offer, String series, int number, Date issueDate) throws ManagerBeanException {
		Invoice invoice = createInvoice(offer, series, number, issueDate);
		createInvoiceDetails(invoice, offer);
		if (offer.getPayMethod() != null && offer.getPayMethod().getId() != null) {
			getFinanceGenerator().generateFinances(invoice, offer, getPriceStrategy().getTotalPrice(invoice, invoice), true);
		} else {
			getFinanceGenerator().generateFinances(invoice, getPriceStrategy().getTotalPrice(invoice, invoice), true);
		}
		updateOfferStatus(offer);

		return invoice;
	}

	private Invoice createInvoice(Offer offer, String series, int number, Date issueDate) throws ManagerBeanException {
		Customer customer = getSalesBridgeUtil().obtainCustomer(offer);

		Invoice invoice = new Invoice();
		invoice.setSeries(series);
		invoice.setNumber((number > 0) ? number : obtainMaxNumber(series));
		invoice.setRegistry(customer.getRegistry());
		invoice.setRegistryDocument(customer.getRegistry().getDocument());
		invoice.setRegistryName(customer.getRegistry().getFullName());
		invoice.setRegistryAddress(offer.getAddress());
		invoice.setIssueDate(issueDate);
		invoice.setTaxDate(issueDate);
		invoice.setSecurityLevel(offer.getSecurityLevel());
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(offer.getScope());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
    	return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	private void createInvoiceDetails(Invoice invoice, Offer offer) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_OFFER_ID), offer.getId());
		criteria.addNotNullExpression(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_ITEM_ID));
		criteria.addOrder(offerDetailBean.getFieldName(ICommercialAlias.OFFER_DETAIL_LINE));
		Iterator<?> iterator = offerDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			OfferDetail offerDetail = (OfferDetail)iterator.next();
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setItem(offerDetail.getItem());
			invoiceDetail.setDescription(offerDetail.getDescription());
			invoiceDetail.setQuantity(offerDetail.getQuantity());
			invoiceDetail.setPrice(offerDetail.getPrice());
			invoiceDetail.setDiscountExpression(offerDetail.getDiscountExpression());
			invoiceDetail.setWorkPlace(offer.getWorkPlace());
			invoiceDetail.setSource(InvoiceSource.OFFER);
			invoiceDetail.setSourceId(offerDetail.getId());
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetailBean.insert(invoiceDetail);

			offerDetail.setStatus(OfferDetailStatus.ON_INVOICE);
			offerDetailBean.update(offerDetail);
		}
	}

	private void updateOfferStatus(Offer offer) throws ManagerBeanException {
		IManagerBean offerBean = BeanManager.getManagerBean(Offer.class);
		offer.setStatus(OfferStatus.INVOICED);
		offerBean.restoreNullSubPOJOs(offer);
		offerBean.update(offer);
	}

}
