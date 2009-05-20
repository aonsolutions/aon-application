package com.code.aon.sales;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.invoicing.FinanceGenerator;
import com.code.aon.finance.invoicing.IInvoicingDAO;
import com.code.aon.finance.invoicing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.sales.enumeration.BillingPeriod;

public class CustomerFeeInvoicingDAO implements IInvoicingDAO {
	
	private static final Logger LOGGER = Logger.getLogger(CustomerFeeInvoicingDAO.class.getName());
	
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
			LOGGER.log(Level.SEVERE, "Error inserting invoice wiht id=" + invoice.getId(), e);
		}
		return null;
	}

	public void insertInvoiceDetail(InvoiceDetail invoiceDetail) {
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetailBean.insert(invoiceDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting invoiceDetail with id=" + invoiceDetail.getId(), e);
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
				customerFeeBean.update(customerFee);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating customerFee with id=" + customerFee.getId(), e);
		}
	}

	public void insetFinance(Finance finance) {
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			double amount = getPriceStrategy().getTotalPrice(finance.getInvoice(), finance.getInvoice());
			if(amount != 0.0){
				finance.setAmount(amount);
				financeBean.insert(finance);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error inserting finance for invoice with id= " + finance.getInvoice().getId(), e);
		}
	}
	
	public void createFinances(Invoice invoice) throws ManagerBeanException {
		double amount = getPriceStrategy().getTotalPrice(invoice, invoice);
		if(amount != 0.0){
			getFinanceGenerator().generateFinances(invoice, invoice.getRegistry(), amount, true);
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
			LOGGER.log(Level.SEVERE, "Error obtaining address for registry with id= " + id, e);
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