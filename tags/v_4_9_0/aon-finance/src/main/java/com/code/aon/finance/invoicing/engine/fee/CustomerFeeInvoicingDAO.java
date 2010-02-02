package com.code.aon.finance.invoicing.engine.fee;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
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
import com.code.aon.registry.dao.IRegistryAlias;

public class CustomerFeeInvoicingDAO implements IInvoicingDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerFeeInvoicingDAO.class.getName());
	
	private List<Invoice> invoicingCollection;
	
	private IPriceStrategy priceStrategy;
	
	private FinanceGenerator financeGenerator;
	
	public CustomerFeeInvoicingDAO(){
		invoicingCollection = new ArrayList<Invoice>();
	}

	public Invoice insertInvoice(Invoice invoice) {
		try {
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			invoice.setRegistryAddress(obtainAddress(invoice.getRegistry().getId()));
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
		CustomerFee customerFee = (CustomerFee)to;
		try {
			IManagerBean customerFeeBean = BeanManager.getManagerBean(CustomerFee.class);
			if(customerFee.getPeriod().equals(BillingPeriod.NO_PERIOD)){
				customerFeeBean.remove(customerFee);
			}else{
				Calendar billingCalendar = new GregorianCalendar();
				billingCalendar.setTime(customerFee.getBillingDate());
				billingCalendar.add(Calendar.MONTH, customerFee.getPeriod().getValue());
				customerFee.setBillingDate(billingCalendar.getTime());
				if (customerFee.getFinalDate() != null && customerFee.getBillingDate().after(customerFee.getFinalDate())) {
					customerFeeBean.remove(customerFee);
				} else {
					customerFeeBean.update(customerFee);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error updating customerFee with id=" + customerFee.getId(), e);
		}
	}

	public void createFinances(Invoice invoice) throws ManagerBeanException {
		double amount = getPriceStrategy().getTotalPrice(invoice, invoice);
		if(amount != 0.0){
			getFinanceGenerator().generateFinances(invoice, amount, true);
		}
	}

	@SuppressWarnings("unchecked")
	private RegistryAddress obtainAddress(Integer id) {
		try {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
			criteria.addOrder(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), true);
			Iterator iter = rAddressBean.getList(criteria, 0, 1).iterator();
			if(iter.hasNext()){
				return (RegistryAddress)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining address for registry with id= " + id, e);
		}
		return null;
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