package com.code.aon.finance.bridge.invoicing;

import java.util.Date;
import java.util.Iterator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
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
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

public class DeliveryInvoicingManager {

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

	public Invoice invoice(Delivery delivery, String series, int number, Date issueDate) throws ManagerBeanException {
		updateDeliveryStatus(delivery);
		Invoice invoice = createInvoice(delivery, series, number, issueDate);
		createInvoiceDetails(invoice, delivery);
		if (delivery.getPayMethod() != null && delivery.getPayMethod().getId() != null) {
			getFinanceGenerator().generateFinances(invoice, delivery, getPriceStrategy().getTotalPrice(invoice, invoice), true);
		} else {
			getFinanceGenerator().generateFinances(invoice, getPriceStrategy().getTotalPrice(invoice, invoice), true);
		}
		return invoice;
	}

	private void updateDeliveryStatus(Delivery delivery) throws ManagerBeanException {
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		delivery.setStatus(DeliveryStatus.INVOICED);
		deliveryBean.restoreNullSubPOJOs(delivery);
		deliveryBean.update(delivery);
	}

	private Invoice createInvoice(Delivery delivery, String series, int number, Date issueDate) throws ManagerBeanException {
		Invoice invoice = new Invoice();
		invoice.setSeries(series);
		invoice.setNumber((number > 0) ? number : obtainMaxNumber(series));
		invoice.setRegistry(delivery.getCustomer().getRegistry());
		invoice.setRegistryDocument(delivery.getCustomer().getRegistry().getDocument());
		invoice.setRegistryName(delivery.getCustomer().getRegistry().getFullName());
		invoice.setRegistryAddress(delivery.getRegistryAddress());
		invoice.setIssueDate(issueDate);
		invoice.setTaxDate(issueDate);
		invoice.setSecurityLevel(delivery.getSecurityLevel());
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setType(InvoiceType.SALES);
		invoice.setScope(delivery.getScope());

		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		return (Invoice)invoiceBean.insert(invoice);
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
    	return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	private void createInvoiceDetails(Invoice invoice, Delivery delivery) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		criteria.addOrder(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_LINE));
		Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)iterator.next();
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setLine(deliveryDetail.getLine());
			invoiceDetail.setItem(deliveryDetail.getItem());
			invoiceDetail.setDescription(deliveryDetail.getDescription());
			invoiceDetail.setQuantity(deliveryDetail.getQuantity());
			invoiceDetail.setPrice(deliveryDetail.getPrice());
			invoiceDetail.setDiscountExpression(deliveryDetail.getDiscountExpression());
			invoiceDetail.setWorkPlace(delivery.getWorkPlace());
			invoiceDetail.setSource(InvoiceSource.DELIVERY);
			invoiceDetail.setSourceId(deliveryDetail.getId());
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetailBean.insert(invoiceDetail);
		}
	}

}
