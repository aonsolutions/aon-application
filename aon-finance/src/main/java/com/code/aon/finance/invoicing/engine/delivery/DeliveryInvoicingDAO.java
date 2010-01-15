package com.code.aon.finance.invoicing.engine.delivery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.IPayMethod;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.invoicing.engine.IInvoicingDAO;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

public class DeliveryInvoicingDAO implements IInvoicingDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryInvoicingDAO.class.getName());
	
	private List<Invoice> invoicingCollection;
	
	private IPriceStrategy priceStrategy;
	
	private FinanceGenerator financeGenerator;
	
	public DeliveryInvoicingDAO(){
		invoicingCollection = new ArrayList<Invoice>();
	}

	public Invoice insertInvoice(Invoice invoice) {
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			invoice = (Invoice)invoiceBean.insert(invoice);
			invoicingCollection.add(invoice);
			return invoice;
		} catch (ManagerBeanException e) {
			LOGGER.error("Error inserting invoice wiht id=" + invoice.getId(), e);
		}
		return null;
	}

	public void insertInvoiceDetail(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetailBean.insert(invoiceDetail);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error inserting invoiceDetail with id=" + invoiceDetail.getId(), e);
		}
	}
	
	public void updateSource(ITransferObject to) {
		Delivery delivery = (Delivery)to;

		try {
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			delivery.setStatus(DeliveryStatus.INVOICED);
			deliveryBean.update(delivery);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error updating delivery with id=" + delivery.getId(), e);
		}
	}

	public void createFinances(Invoice invoice, IPayMethod payMethod) throws ManagerBeanException {
		double amount = getPriceStrategy().getTotalPrice(invoice, invoice);
		if (amount != 0.0) {
			if (payMethod.getPayment() != null && payMethod.getPayment().getId() != null) {
				getFinanceGenerator().generateFinances(invoice, payMethod, amount, true);
			} else {
				getFinanceGenerator().generateFinances(invoice, amount, true);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		return invoicingCollection;
	}
	
	private IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}
	
	public FinanceGenerator getFinanceGenerator() {
		if(financeGenerator == null){
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}

}