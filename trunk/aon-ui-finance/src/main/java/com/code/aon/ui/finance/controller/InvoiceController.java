package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class InvoiceController extends BasicController {

	private String invoiceAddressControllerName;
	private String invoiceDetailControllerName;
	private String invoiceFinanceControllerName;
	private IPriceStrategy priceStrategy;
	private FinanceGenerator financeGenerator;
	private AccountEntryInvoiceWriter accountWriter;
	private List<SelectItem> addresses;
	private boolean showInvoiceAddressWindow;

	public String getInvoiceAddressControllerName() {
		return invoiceAddressControllerName;
	}

	public void setInvoiceAddressControllerName(String invoiceAddressControllerName) {
		this.invoiceAddressControllerName = invoiceAddressControllerName;
	}

	public String getInvoiceDetailControllerName() {
		return invoiceDetailControllerName;
	}

	public void setInvoiceDetailControllerName(String invoiceDetailControllerName) {
		this.invoiceDetailControllerName = invoiceDetailControllerName;
	}

	public String getInvoiceFinanceControllerName() {
		return invoiceFinanceControllerName;
	}

	public void setInvoiceFinanceControllerName(String invoiceFinanceControllerName) {
		this.invoiceFinanceControllerName = invoiceFinanceControllerName;
	}

	public Invoice getInvoice() {
		return (Invoice)getTo();
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

	public AccountEntryInvoiceWriter getAccountWriter() {
		if (accountWriter == null) {
			accountWriter = new AccountEntryInvoiceWriter();
		}
		return accountWriter;
	}
	
    public List<SelectItem> getAddresses() {
		return addresses;
	}
	
	public void setAddresses(List<SelectItem> addresses) {
		this.addresses = addresses;
	}
	
	public int getAddressCount() {
		if (addresses != null) {
			return addresses.size();
		}
		return 0;
	}
	
	public void loadAddresses(Integer id) throws ManagerBeanException {
		this.addresses = new LinkedList<SelectItem>();
		if (id != null) {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
			Iterator<?> iter = rAddressBean.getList(criteria).iterator();
			while(iter.hasNext()) {
				RegistryAddress address = (RegistryAddress)iter.next();
				String addressLabel = StringUtils.join( new String[]{address.getAddress(),address.getAddress2(),address.getAddress3()}, " ");
				addressLabel = StringUtils.abbreviate(StringUtils.trim(addressLabel),30) + " - " + address.getCity();
				addressLabel = StringUtils.abbreviate(addressLabel, 50);
				SelectItem item = new SelectItem(address.getId(), addressLabel);
				addresses.add(item);
			}
		}
	}

	public String getAddress() {
		RegistryAddress rAddress = getInvoice().getRegistryAddress();
		String address = (rAddress!=null)?rAddress.getAddress()+" "+rAddress.getAddress2()+" "+rAddress.getAddress3():"";
		BasicController addressController = (BasicController)FormUtil.getController(invoiceAddressControllerName);
		if (addressController.getTo() != null && ((InvoiceAddress)addressController.getTo()).getId() != null) {
			InvoiceAddress invoiceAddress = (InvoiceAddress)addressController.getTo();
			address = invoiceAddress.getAddress() + " " + invoiceAddress.getAddress2();
		}
		return address;
	}

	public String getCity() {
		RegistryAddress rAddress = getInvoice().getRegistryAddress();
		String city = (rAddress!=null)?rAddress.getCity():"";
		BasicController addressController = (BasicController)FormUtil.getController(invoiceAddressControllerName);
		if (addressController.getTo() != null && ((InvoiceAddress)addressController.getTo()).getId() != null) {
			InvoiceAddress invoiceAddress = (InvoiceAddress)addressController.getTo();
			city = invoiceAddress.getCity();
		}
		return city;
	}

	public double getTaxableBase(){
		return getPriceStrategy().getTaxableBase((ICalculableContainer)getTo());
	}

	public double getToInvoiceTotalPrice() {
		return getPriceStrategy().getTotalPrice((ICalculableContainer)getTo(), (ITaxInfo)getTo());
	}
	
	public double getInvoiceTotalPrice() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}

	public double getToInvoiceFinanceTotal() throws ManagerBeanException {
		double financeTotal = 0;
		IController feeFinanceController = FormUtil.getController(invoiceFinanceControllerName);
		Iterator<?> iterator = feeFinanceController.getManagerBean().getList(feeFinanceController.getCriteria()).iterator();
		while(iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			financeTotal += finance.getAmount();
		}
		return financeTotal;
	}

	public String getPayMethod() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getModel().getRowData();
		String payMethodName = null;
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iterator = financeBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			if (payMethodName == null) {
				payMethodName = (finance.getPayMethod() != null) ? finance.getPayMethod().getName() : null;
			}
			if (finance.getPayMethod() != null && !finance.getPayMethod().getName().equals(payMethodName)) {
				return "MULTIPLE";
			}
		}
		return payMethodName;
	}

	public FinanceStatus getFinanceStatus() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getModel().getRowData();
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iterator = financeBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			Finance finance = (Finance)iterator.next();
			if (FinanceStatus.PAID != finance.getFinanceStatus() && FinanceStatus.SETTLED != finance.getFinanceStatus()) {
				return FinanceStatus.PENDING;
			}
		}
		return (financeBean.getCount(criteria) == 0) ? FinanceStatus.PENDING : FinanceStatus.PAID;
	}

	public boolean isRemovable() {
		InvoiceDetailController invoiceDetailController = (InvoiceDetailController)FormUtil.getController(invoiceDetailControllerName);
		return (invoiceDetailController.getTo() == null && InvoiceStatus.PENDING == getInvoice().getStatus());
	}
	
	public boolean isAccountSource() throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), getInvoice().getId());
		Iterator<ITransferObject> iterator = invoiceDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
			return InvoiceSource.ACCOUNT == invoiceDetail.getSource();
		}
		return false;
	}

	public void generateFinances(ActionEvent event) throws ManagerBeanException{
		Invoice invoice = getInvoice();
		try {
			IController invoiceFinanceController = FormUtil.getController(invoiceFinanceControllerName);
			List<?> financeList = invoiceFinanceController.getManagerBean().getList(invoiceFinanceController.getCriteria());
			if (existFinanceTrackings(financeList)) {
				AonUtil.addInfoMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.VALIDATE_FINANCES_GENERATION_ERROR_KEY);
				throw new AbortProcessingException();
			}
			Iterator<?> iter = financeList.iterator();
			while(iter.hasNext()) {
				Finance finance = (Finance)iter.next();
				invoiceFinanceController.getManagerBean().remove(finance);
			}
			getFinanceGenerator().generateFinances(invoice, getPriceStrategy().getTotalPrice(invoice, invoice));
			invoiceFinanceController.onSearch(null);
		} catch (ManagerBeanException e) {
			String msg = AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.GENERATE_FINANCES_ERROR_KEY);
			throw new ManagerBeanException(msg,e);
		}
	}
	
	private boolean existFinanceTrackings(List<?> financeList) throws ManagerBeanException {
		IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Iterator<?> iter = financeList.iterator();
		while(iter.hasNext()) {
			Criteria criteria = new Criteria();
			Finance finance = (Finance)iter.next();
			criteria.addEqualExpression(financeTrackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
			if (financeTrackingBean.getCount(criteria) > 0) {
				return true;
			}
		}
		return false;
	}

	public void onRecordInvoice(ActionEvent event) throws ManagerBeanException{
		if (getToInvoiceFinanceTotal() != 0 && getToInvoiceTotalPrice() != getToInvoiceFinanceTotal()) {
			String message = AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.UNABLE_RECORD_INACCURACY_ERROR_KEY);
			throw new AbortProcessingException(message);
		}

		getAccountWriter().recordAndUpdateInvoice(getInvoice());
	}
	
	public void onUnrecordInvoice(ActionEvent event) throws ManagerBeanException{
		getAccountWriter().unrecordAndUpdateInvoice(getInvoice());
	}

	public boolean isRecorded() {
		return InvoiceStatus.SCORED == getInvoice().getStatus();
	}

	public boolean isReadOnly() {
		return (isRecorded() || getInvoice().isSigned());
	}
	
	@SuppressWarnings("unchecked")
	public Integer getAccountEntryId() throws ManagerBeanException {
    	Invoice invoice = getInvoice();
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
	
	public boolean isShowInvoiceAddressWindow() {
		return showInvoiceAddressWindow;
	}

	public void setShowInvoiceAddressWindow(boolean value) {
		this.showInvoiceAddressWindow = value;
	}

	public void onInvoiceAddressShow( ActionEvent event ) {
		BasicController addressController = (BasicController)FormUtil.getController(invoiceAddressControllerName);
		ITransferObject to = addressController.getTo();
		if ( to == null ) {
			addressController.onReset(event);
		}
	}

}
