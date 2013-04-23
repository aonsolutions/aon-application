package com.code.aon.finance.invoicing.engine.fee;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.IPayMethod;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.BillingPeriod;
import com.code.aon.finance.invoicing.engine.IInvoicingDAO;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerFeeInvoicingDAO implements IInvoicingDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerFeeInvoicingDAO.class.getName());
	
	private List<Invoice> invoicingCollection;
	private IPriceStrategy priceStrategy;
	private FinanceGenerator financeGenerator;
	
	public Collection<Invoice> getCollection() {
		return invoicingCollection;
	}
	
	private IPriceStrategy getPriceStrategy() {
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

	public CustomerFeeInvoicingDAO() {
		invoicingCollection = new ArrayList<Invoice>();
	}

	public Invoice insertInvoice(Invoice invoice) {
		try {
			if (invoice.getRegistryAddress() == null) {
				invoice.setRegistryAddress(obtainAddress(invoice.getRegistry().getId()));
			}
			invoice = (Invoice)BeanManager.getManagerBean(Invoice.class).insert(invoice);
			invoicingCollection.add(invoice);
			return invoice;
		} catch (ManagerBeanException e) {
			LOGGER.error("Error inserting invoice wiht id=" + invoice.getId(), e);
		}
		return null;
	}

	public void insertInvoiceDetail(InvoiceDetail invoiceDetail) {
		try {
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetail = (InvoiceDetail)BeanManager.getManagerBean(InvoiceDetail.class).insert(invoiceDetail);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error inserting invoiceDetail with id=" + invoiceDetail.getId(), e);
		}
	}
	
	public void updateSource(ITransferObject to) {
		CustomerFee customerFee = (CustomerFee)to;
		try {
			if (customerFee.getPeriod().equals(BillingPeriod.NO_PERIOD)) {
				BeanManager.getManagerBean(CustomerFee.class).remove(customerFee);
			} else {
				Calendar billingCalendar = new GregorianCalendar();
				billingCalendar.setTime(customerFee.getBillingDate());
				billingCalendar.add(Calendar.MONTH, customerFee.getPeriod().getValue());
				customerFee.setBillingDate(billingCalendar.getTime());
				if (customerFee.getFinalDate() != null && customerFee.getBillingDate().after(customerFee.getFinalDate())) {
					BeanManager.getManagerBean(CustomerFee.class).remove(customerFee);
				} else {
					BeanManager.getManagerBean(CustomerFee.class).update(customerFee);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error updating customerFee with id=" + customerFee.getId(), e);
		}
	}

	public void createFinances(Invoice invoice, IPayMethod payMethod) throws ManagerBeanException {
		double amount = getPriceStrategy().getTotalPrice(invoice, invoice);
		if (amount != 0.0) {
			if (payMethod != null && payMethod.getPayment() != null && payMethod.getPayment().getId() != null) {
				getFinanceGenerator().generateFinances(invoice, payMethod, amount, true);
			} else {
				getFinanceGenerator().generateFinances(invoice, amount, true);
			}
		}
	}

	private RegistryAddress obtainAddress(Integer id) {
		try {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
			criteria.addOrder(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), true);
			for (ITransferObject ito : rAddressBean.getList(criteria, 0, 1)) {
				return (RegistryAddress)ito;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining address for registry with id= " + id, e);
		}
		return null;
	}

}