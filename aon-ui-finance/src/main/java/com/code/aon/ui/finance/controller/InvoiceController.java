package com.code.aon.ui.finance.controller;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IAttachment;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
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
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.util.FinanceEmailUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.sign.controller.ISignatureController;
import com.code.aon.ui.sign.controller.SignerController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.webmail.SecurityInfo;

public class InvoiceController extends BasicController implements ISignatureController {

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceController.class.getName());
	
	private String invoiceAddressControllerName;
	private String invoiceDetailControllerName;
	private String invoiceFinanceControllerName;
	private IPriceStrategy priceStrategy;
	private FinanceGenerator financeGenerator;
	private AccountEntryInvoiceWriter accountWriter;
	private List<SelectItem> addresses;
	private boolean showInvoiceAddressWindow;
	private FinanceEmailUtil emailController;
	private String backAction;

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
		if (InvoiceType.UNDEDUCTIBLE == getInvoice().getType()) {
			return getTaxableBase();
		}
		return getPriceStrategy().getTotalPrice((ICalculableContainer)getTo(), (ITaxInfo)getTo());
	}
	
	public double getInvoiceTotalPrice() throws ManagerBeanException {
		Invoice invoice = (Invoice)this.getModel().getRowData();
		if (InvoiceType.UNDEDUCTIBLE == invoice.getType()) {
			return getPriceStrategy().getTaxableBase(invoice);
		}
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
		return CommonUtil.round(financeTotal);
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
			getFinanceGenerator().generateFinances(invoice, getToInvoiceTotalPrice());
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
		double invoiceTotal = getToInvoiceTotalPrice();
		double financeTotal = getToInvoiceFinanceTotal();
		if (financeTotal != 0 && invoiceTotal != financeTotal) {
			String message = AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.UNABLE_RECORD_INACCURACY_ERROR_KEY);
			throw new AbortProcessingException(message);
		}

		getAccountWriter().recordAndUpdateInvoice(getInvoice());
	}
	
	public void onUnrecordInvoice(ActionEvent event) throws ManagerBeanException{
		getAccountWriter().unrecordAndUpdateInvoice(getInvoice());
	}

	public boolean isRecorded() {
		return getInvoice().isRecorded();
	}

	public boolean isReadOnly() {
		return (getInvoice().isRecorded() || getInvoice().isSigned());
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

	@Override
	public IManagerBean getAttachmentBean() {
		try {
			return BeanManager.getManagerBean(InvoiceAttachment.class);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public String getAttachmentMimeTypeAlias() {
		return IFinanceAlias.INVOICE_ATTACHMENT_MIME_TYPE;
	}

	@Override
	public String getAttachmentParentAlias() {
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
	
	@Override
	public IAttachment generateReportAttachment(ITransferObject to) {
		return getSignerController().getReport(to);
	}
	
	@Override
	public IAttachment getUnsignedAttachment(ITransferObject to) {
		return generateReportAttachment(to);
	}	
	
	@Override
	public String getDescription(ITransferObject parent) {
		Invoice invoice = (Invoice) parent;
		return "invoice_" + invoice.getReferenceCode().replace("/", "-");
	}
	
	public FinanceEmailUtil getEmailController() {
		return emailController;
	}
	
	public SignerController getSignerController() {
		return (SignerController) AonUtil.getRegisteredBean(IFinanceConstants.SALE_INVOICE_SIGNER_CONTROLLER_NAME);
	}
	
	public IAttachment getInvoiceData( Invoice invoice ) throws ManagerBeanException {
		SignerController signer = getSignerController();
		IAttachment attach = null;
		if ( invoice.isSigned() ) {
			attach = signer.getSignedAttachment(invoice.getId());
		} else {
			attach = getUnsignedAttachment(invoice);
		}
		return attach;		
	}
	
	public void onSendInvoiceByEmail( ActionEvent event ) throws ManagerBeanException, IOException {
		sendInvoiceByEmail( null, true );
	}

	private void sendInvoiceByEmail( SecurityInfo securyInfo, boolean facturae ) throws ManagerBeanException, IOException {
		Invoice invoice = getInvoice();
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(WebMailConstants.BEAN_MESSAGE);
		messageController.initNewMessage();
		IAttachment attach = getInvoiceData(invoice);
		emailController.initMessageController(messageController, invoice, attach, facturae);
		messageController.setShowNewMessageWindow(true);
		messageController.setSecurityInfo( securyInfo );
	}
	
	public String getBackAction() {
		return backAction;
	}
	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}
	
	public String backAction() {
		String b = backAction;
		if (b == null) {
			b =  getBeanName() + "_list";
		}
		setBackAction(null); 
		return b;
	}
	
	public void onSelectTo(ActionEvent event) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		String alias = getIdAlias();
		criteria.addEqualExpression(this.getManagerBean().getFieldName(alias),getManagerBean().getId( getTo() ));
		this.setCriteria(criteria);
		this.onSearch(event);
		this.onSelectFirst(event);
	}
	
	private String getIdAlias() {
		String factoryName = HibernateUtil.getSessionFactoryName();
		SessionFactory factory = HibernateUtil.getSessionFactory(factoryName);
		ClassMetadata cm = factory.getClassMetadata(getPojo());
		String id = getPojoShortName() + "_" + cm.getIdentifierPropertyName();
		return id;
	}
	
	public InvoiceController() {
		this.emailController = new FinanceEmailUtil();
	}

}
