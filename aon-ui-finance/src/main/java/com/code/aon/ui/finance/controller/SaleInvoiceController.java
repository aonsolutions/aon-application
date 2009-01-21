package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.faces.component.richfaces.lookup.LookupChangeEvent;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.customer.util.CustomerValidationManager;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class SaleInvoiceController extends InvoiceController {
	
	private static final String SALE_INVOICE_ADDRESS_CONTROLLER_NAME = "saleInvoiceAddress";
	private static final String SALE_INVOICE_DETAIL_CONTROLLER_NAME = "saleInvoiceDetail";
	private static final String SALE_INVOICE_FINANCE_CONTROLLER_NAME = "saleInvoiceFinance";
	
	private IPriceStrategy priceStrategy;
	
	private FinanceGenerator financeGenerator;
	
	private AccountEntryInvoiceWriter accountWriter;
	
	private CustomerValidationManager cvm;

	private List<SelectItem> addresses;
	

	public IPriceStrategy getPriceStrategy(){
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

	public AccountEntryInvoiceWriter getAccountWriter() {
		if(accountWriter == null){
			accountWriter = new AccountEntryInvoiceWriter();
		}
		return accountWriter;
	}
	
	private CustomerValidationManager getCustomerValidationManager() {
		if (cvm == null) {
			cvm = new CustomerValidationManager(); 
		}
		return cvm;
	}
	
    public List<SelectItem> getAddresses() {
		return addresses;
	}
	
	public void setAddresses(List<SelectItem> addresses) {
		this.addresses = addresses;
	}
	
	public int getAddressCount() {
		if(addresses != null){
			return addresses.size();
		}
		return 0;
	}
	
	public void loadAddresses(Integer id) throws ManagerBeanException {
		List<SelectItem> addresses = new LinkedList<SelectItem>();
		if (id != null) {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
			Iterator<?> iter = rAddressBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryAddress address = (RegistryAddress)iter.next();
				String addressLabel = address.getAddress() + " " + address.getAddress2() + " " + address.getAddress3();
				addressLabel = ((addressLabel.length()>30)?addressLabel.substring(0,27)+"...":addressLabel) + " - " + address.getCity();
				addressLabel = ((addressLabel.length()>48)?addressLabel.substring(0,45)+"...":addressLabel);
				SelectItem item = new SelectItem(address.getId(), addressLabel);
				addresses.add(item);
			}
		}
		this.addresses = addresses;
	}

	public String getAddress() {
		RegistryAddress rAddress = ((Invoice)this.getTo()).getRegistryAddress();
		String address = (rAddress!=null)?rAddress.getAddress()+" "+rAddress.getAddress2()+" "+rAddress.getAddress3():"";
		BasicController addressController = (BasicController)FormUtil.getController(SALE_INVOICE_ADDRESS_CONTROLLER_NAME);
		if (addressController.getTo() != null && ((InvoiceAddress)addressController.getTo()).getId() != null) {
			InvoiceAddress invoiceAddress = (InvoiceAddress)addressController.getTo();
			address = invoiceAddress.getAddress() + " " + invoiceAddress.getAddress2();
		}
		return address;
	}

	public String getCity() {
		RegistryAddress rAddress = ((Invoice)this.getTo()).getRegistryAddress();
		String city = (rAddress!=null)?rAddress.getCity():"";
		BasicController addressController = (BasicController)FormUtil.getController(SALE_INVOICE_ADDRESS_CONTROLLER_NAME);
		if (addressController.getTo() != null && ((InvoiceAddress)addressController.getTo()).getId() != null) {
			InvoiceAddress invoiceAddress = (InvoiceAddress)addressController.getTo();
			city = invoiceAddress.getCity();
		}
		return city;
	}

	public void customerData(LookupChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null && !event.getNewValue().equals("")){
			Customer customer = (Customer) event.getNewValue();
			isBlocked(customer); // Saca el mensaje de bloqueo.
			((Invoice)this.getTo()).setRegistryName(customer.getRegistry().getFullName());
			((Invoice)this.getTo()).setRegistryDocument(customer.getRegistry().getDocument());
			((Invoice)this.getTo()).setRegistry(customer.getRegistry());
			loadAddresses(customer.getId());
		} else {
			setAddresses(null);	
		}
	}
	

	public double getToInvoiceTotalPrice(){
		Invoice invoice = (Invoice)this.getTo();
		return getPriceStrategy().getTotalPrice(invoice,invoice);
	}
	
	public double getInvoiceTotalPrice() throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(invoice,invoice);
	}

	public Customer getCustomer() throws ManagerBeanException{
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		if((Invoice)this.getTo() != null){
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_ID), ((Invoice)this.getTo()).getRegistry().getId());
			Iterator<?> iter = customerBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Customer)iter.next();
			}
			
		}
		return null;
	}

	public boolean isRemovable(){
		SaleInvoiceDetailController feeInvoicingDetailController = (SaleInvoiceDetailController)FormUtil.getController(SALE_INVOICE_DETAIL_CONTROLLER_NAME);
		Invoice invoice = (Invoice)this.getTo();
		if(feeInvoicingDetailController.getTo() == null && invoice.getStatus().equals(InvoiceStatus.PENDING)){
			return true;
		}
		return false;
	}
	
	public void generateFinances(ActionEvent event) throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getTo();
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			IController feeFinanceController = FormUtil.getController(SALE_INVOICE_FINANCE_CONTROLLER_NAME);
			List<?> financeList = (List<?>) feeFinanceController.getModel().getWrappedData();
			if(existFinanceTrackings(financeList)){
				AonUtil.addInfoMessageFromBundle(IFinanceMessages.BUNDLE_KEY,IFinanceMessages.VALIDATE_FINANCES_GENERATION_ERROR_KEY);
				throw new AbortProcessingException();
			}
			Iterator<?> iter = financeList.iterator();
			while(iter.hasNext()){
				Finance finance = (Finance)iter.next();
				financeBean.remove(finance);
			}
			getFinanceGenerator().generateFinances(invoice, invoice.getRegistry(), getPriceStrategy().getTotalPrice(invoice, invoice));
			feeFinanceController.onSearch(null);
		} catch (ManagerBeanException e) {
			String msg = AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY,IFinanceMessages.GENERATE_FINANCES_ERROR_KEY);
			throw new ManagerBeanException(msg,e);
		}
	}
	
	private boolean existFinanceTrackings(List<?> financeList) throws ManagerBeanException {
		IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Iterator<?> iter = financeList.iterator();
		while(iter.hasNext()){
			Criteria criteria = new Criteria();
			Finance finance = (Finance)iter.next();
			criteria.addEqualExpression(financeTrackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
			if(financeTrackingBean.getCount(criteria) > 0){
				return true;
			}
		}
		return false;
	}

	public void onRecordInvoice(ActionEvent event) throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getTo();
		getAccountWriter().recordAndUpdateInvoice(invoice, getPriceStrategy());
	}
	
	public void onUnrecordInvoice(ActionEvent event) throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getTo();
		getAccountWriter().unrecordAndUpdateInvoice(invoice);
	}

	public boolean isRecorded(){
		Invoice invoice = (Invoice)this.getTo();
		if(invoice.getStatus() != null){
			return invoice.getStatus().equals(InvoiceStatus.SCORED);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public Integer getAccountEntryId() throws ManagerBeanException {
    	Invoice invoice = (Invoice)this.getTo();
		if (invoice != null && invoice.getId() != null) {
			IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryInvoiceBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID), invoice.getId());
			Iterator iterator = accountEntryInvoiceBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				AccountEntryInvoice accountEntryInvoice = (AccountEntryInvoice)iterator.next();
				return accountEntryInvoice.getAccountEntry().getId();
			}
		}
    	return null;
	}
	
	private boolean isBlocked(Customer customer) {
		return getCustomerValidationManager().isBlocked(customer);
	}

}