package com.code.aon.ui.finance.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Series;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceAttachment;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.report.ReportException;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.customer.util.CustomerValidationManager;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.util.EmailUtilController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.sign.controller.ISignatureController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.SecurityInfo;

public class SaleInvoiceController extends InvoiceController implements ISignatureController, IFinanceConstants, IFinanceMessages {
	
	private static final Logger LOGGER = Logger.getLogger(SaleInvoiceController.class.getName());
	
	private IPriceStrategy priceStrategy;
	
	private FinanceGenerator financeGenerator;
	
	private AccountEntryInvoiceWriter accountWriter;
	
	private CustomerValidationManager cvm;

	private List<SelectItem> addresses;
	
	private boolean showInvoiceAddressWindow;

	public Invoice getInvoice() {
		return (Invoice) getTo();
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

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		int number = obtainMaxNumber((String)event.getNewValue());
		SecurityLevel securityLevel = obtainSeriesSecurityLevel((String)event.getNewValue());
		if (this.getTo() != null) {
			((Invoice)this.getTo()).setNumber(number);
			((Invoice)this.getTo()).setSecurityLevel(securityLevel);
		}
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
    	return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	@SuppressWarnings("unchecked")
	private SecurityLevel obtainSeriesSecurityLevel(String seriesId) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_ID), seriesId);
		Iterator iter = seriesBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			Series series = (Series)iter.next(); 
			if (series.getSecurityLevel() != null) {
				return series.getSecurityLevel();
			}
		}
		return null;
	}

	public void customerData(LookupChangeEvent event) throws ManagerBeanException{
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer) event.getNewValue();
			isBlocked(customer); // Saca el mensaje de bloqueo.
			Invoice invoice = (Invoice) getTo();
			invoice.setRegistryName(customer.getRegistry().getFullName());
			invoice.setRegistryDocument(customer.getRegistry().getDocument());
			invoice.setRegistry(customer.getRegistry());
			loadAddresses(customer.getId());
		} else {
			setAddresses(null);	
		}
	}

	public double getToInvoiceTotalPrice() {
		Invoice invoice = (Invoice)this.getTo();
		return getPriceStrategy().getTotalPrice(invoice,invoice);
	}
	
	public double getInvoiceTotalPrice() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}

	public double getToInvoiceFinanceTotal() throws ManagerBeanException {
		double financeTotal = 0;
		IController feeFinanceController = FormUtil.getController(SALE_INVOICE_FINANCE_CONTROLLER_NAME);
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
			if (!FinanceStatus.PAID.equals(finance.getFinanceStatus()) && !FinanceStatus.SETTLED.equals(finance.getFinanceStatus())) {
				return FinanceStatus.PENDING;
			}
		}
		return FinanceStatus.PAID;
	}

	public Customer getCustomer() throws ManagerBeanException{
		IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		if ((Invoice)this.getTo() != null) {
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_ID), ((Invoice)this.getTo()).getRegistry().getId());
			Iterator<?> iter = customerBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				return (Customer)iter.next();
			}
		}
		return null;
	}

	public boolean isRemovable() {
		SaleInvoiceDetailController saleInvoiceDetailController = (SaleInvoiceDetailController)FormUtil.getController(SALE_INVOICE_DETAIL_CONTROLLER_NAME);
		Invoice invoice = (Invoice)this.getTo();
		if (saleInvoiceDetailController.getTo() == null && InvoiceStatus.PENDING.equals(invoice.getStatus())) {
			return true;
		}
		return false;
	}
	
	public boolean isAccountSource() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getTo();
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iterator = invoiceDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)iterator.next();
			return invoiceDetail.getSource().equals(InvoiceSource.ACCOUNT);
		}
		return false;
	}

	public void generateFinances(ActionEvent event) throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getTo();
		try {
			IController feeFinanceController = FormUtil.getController(SALE_INVOICE_FINANCE_CONTROLLER_NAME);
			List<?> financeList = feeFinanceController.getManagerBean().getList(feeFinanceController.getCriteria());
			if (existFinanceTrackings(financeList)) {
				AonUtil.addInfoMessageFromBundle(IFinanceMessages.BUNDLE_KEY,IFinanceMessages.VALIDATE_FINANCES_GENERATION_ERROR_KEY);
				throw new AbortProcessingException();
			}
			Iterator<?> iter = financeList.iterator();
			while(iter.hasNext()) {
				Finance finance = (Finance)iter.next();
				feeFinanceController.getManagerBean().remove(finance);
			}
			getFinanceGenerator().generateFinances(invoice, getPriceStrategy().getTotalPrice(invoice, invoice));
			feeFinanceController.onSearch(null);
		} catch (ManagerBeanException e) {
			String msg = AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY,IFinanceMessages.GENERATE_FINANCES_ERROR_KEY);
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

		Invoice invoice = (Invoice)this.getTo();
		getAccountWriter().recordAndUpdateInvoice(invoice);
	}
	
	public void onUnrecordInvoice(ActionEvent event) throws ManagerBeanException{
		Invoice invoice = (Invoice)this.getTo();
		getAccountWriter().unrecordAndUpdateInvoice(invoice);
	}

	public boolean isRecorded() {
		Invoice invoice = (Invoice)this.getTo();
		if (invoice.getStatus() != null) {
			return invoice.getStatus().equals(InvoiceStatus.SCORED);
		}
		return false;
	}

	public boolean isReadOnly() {
		Invoice invoice = (Invoice)this.getTo();
		if ( invoice.isSigned() ) {
			return true;
		}
		if ( invoice.getStatus() != null ) {
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

	public boolean isShowInvoiceAddressWindow() {
		return showInvoiceAddressWindow;
	}

	public void setShowInvoiceAddressWindow(boolean value) {
		this.showInvoiceAddressWindow = value;
	}
	
	public void onInvoiceAddressShow( ActionEvent event ) {
		BasicController addressController = (BasicController)FormUtil.getController(SALE_INVOICE_ADDRESS_CONTROLLER_NAME);
		ITransferObject to = addressController.getTo();
		if ( to == null ) {
			addressController.onReset(event);
		}
	}
	
	public void onSendInvoiceByEmail( ActionEvent event ) throws ManagerBeanException, ReportException, IOException, SAXException {
		sendInvoiceByEmail( null );
	}

	public void sendInvoiceByEmail( SecurityInfo securyInfo ) throws ManagerBeanException, ReportException, IOException, SAXException {
		Invoice invoice = getInvoice();
		EmailUtilController emailController = (EmailUtilController) AonUtil.getRegisteredBean(EMAIL_UTIL_CONTROLLER_NAME);
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(WebMailConstants.BEAN_MESSAGE);
		messageController.initNewMessage();
		String[] emails = emailController.getEmails(invoice);
		if (! ArrayUtils.isEmpty(emails) ) {
			messageController.setRecipientsTo( emails[0] );
			if ( emails.length > 1 ) { 
				String recipientsCc = StringUtils.join( emails, ',', 1, emails.length );
				messageController.setRecipientsCc( recipientsCc );
			}
		}
		messageController.setSubject( emailController.getEmailSubject(invoice) );
		messageController.setContent( emailController.getEmailBody(invoice) );
		messageController.addAttachment( emailController.getInvoiceFile(invoice) );
		messageController.addAttachment( emailController.getInvoiceXml(invoice) );
		messageController.setShowNewMessageWindow(true);
		messageController.setSecurityInfo( securyInfo );
	}

	@Override
	public IManagerBean getAttachmentBean() {
		try {
			return BeanManager.getManagerBean(InvoiceAttachment.class);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String getAttchmentMimeTypeAlias() {
		return IFinanceAlias.INVOICE_ATTACHMENT_MIME_TYPE;
	}

	@Override
	public String getAttchmentParentAlias() {
		return IFinanceAlias.INVOICE_ATTACHMENT_INVOICE_ID;
	}

	@Override
	public boolean isSigned(ITransferObject to) {
		return ((Invoice) to).isSigned();
	}

	@Override
	public IAttachment newAttachment(ITransferObject parent) {
		InvoiceAttachment attachment = new InvoiceAttachment();
		attachment.setInvoice( (Invoice) parent );
		return attachment;
	}

	@Override
	public void setSigned(ITransferObject to, boolean value) {
		((Invoice) to).setSigned(value);
	}
	
}