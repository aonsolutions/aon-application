package com.code.aon.ui.purchase.controller;


import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.PurchaseInvoicingManager;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.bridge.IncomeManager;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.purchase.util.IEmailControllerListener;
import com.code.aon.purchase.util.IEmailable;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.purchase.util.PurchaseEmailUtil;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.supplier.util.SupplierValidationManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseController extends BasicController implements IPurchaseConstants, IEmailable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseController.class.getName());

	private List<SelectItem> addresses;
	private Boolean defaultPayMethod;
	private IPriceStrategy priceStrategy;
	private RegistryValidationManager vm;
	private boolean showProjectWindow;
	private boolean showDetailProjectWindow;
	private boolean showIncomeWindow;
	private String incomeSeries;
	private int incomeNumber;
	private Date incomeDate;
	private Warehouse incomeWarehouse;
	private boolean showInvoiceWindow;
	private String invoiceRefCode;
	private Date invoiceDate;
	private PurchaseEmailUtil emailUtil;
	
	private List<String> moreRecipients;
	
	private List<IEmailControllerListener> emailControllerListenerClasses;

	
	public List<IEmailControllerListener> getEmailControllerListenerClasses() {
		return emailControllerListenerClasses;
	}

	public void setEmailControllerListenerClasses(
			List<IEmailControllerListener> emailControllerListenerClasses) {
		this.emailControllerListenerClasses = emailControllerListenerClasses;
	}

	@Override
	public List<String> getMoreRecipients() {
		if(moreRecipients==null){
			moreRecipients = new ArrayList<String>();
		}
		return moreRecipients;
	}

	public void setMoreRecipients(List<String> moreRecipients) {
		this.moreRecipients = moreRecipients;
	}
	
    public PurchaseController() {
    	this.emailUtil = new PurchaseEmailUtil();
    }

	public List<SelectItem> getAddresses() {
		return addresses;
	}
	
	public void setAddresses(List<SelectItem> addresses) {
		this.addresses = addresses;
	}
	
	public Boolean getDefaultPayMethod() {
		return defaultPayMethod;
	}

	public void setDefaultPayMethod(Boolean defaultPayMethod) {
		this.defaultPayMethod = defaultPayMethod;
		if (defaultPayMethod != null && defaultPayMethod) {
			resetPurchasePayMethod();
		}
	}
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	private RegistryValidationManager getRegistryValidationManager() {
		if (vm == null) {
			vm = new SupplierValidationManager(); 
		}
		return vm;
	}
	
	public boolean isShowProjectWindow() {
		return showProjectWindow;
	}

	public void setShowProjectWindow(boolean value) {
		this.showProjectWindow = value;
	}

	public boolean isShowDetailProjectWindow() {
		return showDetailProjectWindow;
	}

	public void setShowDetailProjectWindow(boolean value) {
		this.showDetailProjectWindow = value;
	}

	public boolean isShowIncomeWindow() {
		return showIncomeWindow;
	}

	public void setShowIncomeWindow(boolean value) {
		this.showIncomeWindow = value;
	}
	
	public String getIncomeSeries() {
		return incomeSeries;
	}

	public void setIncomeSeries(String incomeSeries) {
		this.incomeSeries = incomeSeries;
	}

	public int getIncomeNumber() {
		return incomeNumber;
	}

	public void setIncomeNumber(int incomeNumber) {
		this.incomeNumber = incomeNumber;
	}

	public Date getIncomeDate() {
		return incomeDate;
	}

	public void setIncomeDate(Date incomeDate) {
		this.incomeDate = incomeDate;
	}

	public Warehouse getIncomeWarehouse() {
		return incomeWarehouse;
	}

	public void setIncomeWarehouse(Warehouse incomeWarehouse) {
		this.incomeWarehouse = incomeWarehouse;
	}

	public boolean isShowInvoiceWindow() {
		return showInvoiceWindow;
	}

	public void setShowInvoiceWindow(boolean value) {
		this.showInvoiceWindow = value;
	}
	
	public String getInvoiceRefCode() {
		return invoiceRefCode;
	}

	public void setInvoiceRefCode(String invoiceRefCode) {
		this.invoiceRefCode = invoiceRefCode;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	
	public Double getPurchasesTotalAmount() throws ManagerBeanException {
		double purchasesTotalAmount = 0.0; 
		for(ITransferObject to: this.getWrappedList()){
			Purchase p = (Purchase) to;
			purchasesTotalAmount += getPurchaseTotalPrice(p);
		}
		return purchasesTotalAmount;
	}

	public boolean isInIncome() throws ManagerBeanException {
		Purchase purchase = (Purchase)this.getTo();
		return isInIncome(purchase);
	}

	private boolean isInIncome(Purchase purchase) throws ManagerBeanException {
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
		return incomeDetailBean.getCount(criteria) > 0;
	}

	public boolean isPending(){
		Purchase purchase = (Purchase)this.getTo();
		return purchase.getStatus() == PurchaseStatus.PENDING;
	}

	public boolean isBlocked(){
		Purchase purchase = (Purchase)this.getTo();
		return purchase.getStatus() == PurchaseStatus.BLOCKED;
	}

	public boolean isServed(){
		Purchase purchase = (Purchase)this.getTo();
		return purchase.getStatus() == PurchaseStatus.SERVED;
	}

	public boolean isClosed(){
		Purchase purchase = (Purchase)this.getTo();
		return purchase.getStatus() == PurchaseStatus.CLOSED;
	}

	public boolean isInvoiced(){
		Purchase purchase = (Purchase)this.getTo();
		return purchase.getStatus() == PurchaseStatus.INVOICED;
	}

	public Invoice getInvoice() throws ManagerBeanException {
		Purchase purchase = (Purchase)this.getTo();
		if (purchase != null && purchase.getId() != null) {
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
			Iterator<?> iterator = purchaseDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				PurchaseDetail purchaseDetail = (PurchaseDetail)iterator.next();

				IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.PURCHASE);
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE_ID), purchaseDetail.getId());
				Iterator<?> iter = invoiceDetailBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
					return invoiceDetail.getInvoice();
				}
			}
		}
    	return null;
	}

	public String getInvoiceCode() throws ManagerBeanException {
		Invoice invoice = getInvoice();
		return (invoice != null) ? invoice.getReferenceCode() : null;
	}

	public void supplierData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Supplier supplier = (Supplier)event.getNewValue();
			isBlocked(supplier);
			((Purchase)this.getTo()).setSupplier(supplier);
			loadAddresses(supplier.getId());
			loadDefaultPayMethod(supplier.getId(), false);
		} else {
			setAddresses(null);
		}
	}
	
	public void onWorkPlaceChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			WorkPlace workPlace = (WorkPlace)event.getNewValue();
			((Purchase)this.getTo()).setWorkPlace(workPlace);
			((Purchase)this.getTo()).setScope(workPlace.getScope());
		}
	}

	private boolean isBlocked(Supplier supplier) {
		return getRegistryValidationManager().isBlocked(supplier);
	}

	public void loadAddresses(Integer id) throws ManagerBeanException {
		List<SelectItem> addresses = new LinkedList<SelectItem>();
		if (id != null) {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
			Iterator<?> iter = rAddressBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryAddress address = (RegistryAddress)iter.next();
				String addressLabel = address.getFullAddress();
				addressLabel = ((addressLabel.length()>30)?addressLabel.substring(0,27)+"...":addressLabel) + " - " + address.getCity();
				addressLabel = ((addressLabel.length()>48)?addressLabel.substring(0,45)+"...":addressLabel);
				SelectItem item = new SelectItem(address, addressLabel);
				addresses.add(item);
			}
		}
		this.addresses = addresses;
	}

	public int getAddressCount() {
		if (addresses != null){
			return addresses.size();
		}
		return 0;
	}
	
	public void removePurchaseProject(ActionEvent event) throws ManagerBeanException {
		Purchase to = (Purchase)this.getTo();
		to.setProject(null);
		getManagerBean().restoreNullSubPOJOs(to);
		getManagerBean().update(to);
		getManagerBean().initializePOJO(to);

		removePurchaseDetailProject(); 
	}
	
	public void removePurchaseDetailProject() throws ManagerBeanException {
		Purchase purchase = (Purchase)this.getManagerBean().get(((Purchase)this.getTo()).getId());
		Project project = purchase.getProject();
		if(project!=null && project.getId()!=null ){
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
			criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PROJECT_ID), project.getId());
			for (ITransferObject ito : purchaseDetailBean.getList(criteria)) {
				PurchaseDetail purchaseDetail = (PurchaseDetail)ito;
				purchaseDetail.setProject(null);
				purchaseDetailBean.update(purchaseDetail);
			}
		}
		IController purchaseDetailController = FormUtil.getController(PURCHASE_DETAIL_CONTROLLER_NAME);
		purchaseDetailController.onSearch(null);
	}

	public void loadDefaultPayMethod(Integer id, boolean forceDefault) throws ManagerBeanException {
		if (id != null) {
			if (((Purchase)this.getTo()).getPayMethod() != null && ((Purchase)this.getTo()).getPayMethod().getId() != null) {
				setDefaultPayMethod(false);
			} else {
				if (forceDefault) {
					setDefaultPayMethod(true);
				} else {
					IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(rPayMethodBean.getFieldName(IEntityAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), id);
					Iterator<?> iter = rPayMethodBean.getList(criteria).iterator();
					setDefaultPayMethod(iter.hasNext());
				}
			}
		}
	}

	public void resetPurchasePayMethod() {
		Purchase to = (Purchase)this.getTo();
		to.setPayMethod(new PayMethod());
		to.setNumberOfPayments(1);
		to.setDaysToFirstPayment(0);
		to.setDaysBetweenPayments(0);
		to.setPaymentDays("");
		to.setBank(new Bank());
		to.setBankAccount(new BankAccount());
	}

	public double getTaxableBase(){
		return getPriceStrategy().getTaxableBase((ICalculableContainer)getTo());
	}

	public double getTotalPrice(){
		return getPriceStrategy().getTotalPrice((ICalculableContainer)getTo(), ((Purchase)getTo()).getSupplier());
	}

	public double getPurchaseTotalPrice() throws ManagerBeanException {
		Purchase purchase = (Purchase)this.getModel().getRowData();
		return getPurchaseTotalPrice(purchase);
	}

	public double getPurchaseTotalPrice(Purchase purchase) throws ManagerBeanException {
		return getPriceStrategy().getTotalPrice(purchase, purchase.getSupplier());
	}

	public void onPending(ActionEvent event) {
		Purchase to = (Purchase)this.getTo();
		to.setStatus(PurchaseStatus.PENDING);
		accept(event);
	}
	
	public void onClose(ActionEvent event) {
		Purchase to = (Purchase)this.getTo();
		to.setStatus(PurchaseStatus.CLOSED);
		accept(event);
	}

	public void onBlock(ActionEvent event) {
		Purchase to = (Purchase)this.getTo();
		to.setStatus(PurchaseStatus.BLOCKED);
		accept(event);
	}
	
	public void onUnblock(ActionEvent event) throws ManagerBeanException {
		Purchase to = (Purchase)this.getTo();
		to.setStatus(PurchaseStatus.PENDING);
		accept(event);
	}

	public void onIncomeShow(ActionEvent event) throws ManagerBeanException {
		Purchase to = (Purchase)this.getTo();
		setIncomeSeries(null);
		setIncomeNumber(0);
		setIncomeDate(new Date());
		setIncomeWarehouse(obtainDeliveryWarehouse(to.getWorkPlace()));
	}

	private Warehouse obtainDeliveryWarehouse(WorkPlace workPlace) throws ManagerBeanException {
		if (workPlace != null) {
			IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_ID), workPlace.getId());
			Iterator<?> iterator = warehouseBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				return (Warehouse)iterator.next();
			}
		}
		return null;
	}

	public void onIncome(ActionEvent event) throws ManagerBeanException {
		Purchase to = (Purchase)this.getTo();
		IncomeManager incomeManager = new IncomeManager();
		Income income = incomeManager.purchaseIncome(to, getIncomeSeries(), getIncomeNumber(), getIncomeDate(), getIncomeWarehouse());

		IController incomeController = FormUtil.getController(INCOME_CONTROLLER_NAME);
		incomeController.onEditSearch(event);
		incomeController.getCriteria().addEqualExpression(incomeController.getFieldName(IEntityAlias.INCOME_ID), income.getId());
		incomeController.onSearch(event);
		incomeController.getModel().setRowIndex(0);
		incomeController.onSelect(event);
	}

	public void onInvoiceShow(ActionEvent event) throws ManagerBeanException {
		setInvoiceRefCode(null);
		setInvoiceDate(new Date());
	}

	public void onInvoice(ActionEvent event) throws ManagerBeanException {
		Purchase to = (Purchase)this.getTo();
		PurchaseInvoicingManager invoicingManager = new PurchaseInvoicingManager();
		Invoice invoice = invoicingManager.invoice(to, getInvoiceRefCode(), getInvoiceDate());

		IController invoiceController = FormUtil.getController(PURCHASE_INVOICE_CONTROLLER_NAME);
		invoiceController.onEditSearch(event);
		invoiceController.getCriteria().addEqualExpression(invoiceController.getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
		invoiceController.onSearch(event);
		invoiceController.getModel().setRowIndex(0);
		invoiceController.onSelect(event);
	}
	
	public String getDescription(ITransferObject parent) {
		Purchase purchase = (Purchase) parent;
		return "purchase_" + purchase.getReferenceCode().replace("/", "-");
	}
	
	public PurchaseEmailUtil getEmailController() {
		return emailUtil;
	}

	public void onShowEmailMessage( ActionEvent event ) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {
			try {
				controller.onNewMessage(event);
				PurchaseReportManager purchaseReportManager = (PurchaseReportManager) AonUtil.getRegisteredBean(PURCHASE_REPORT_CONTROLLER_NAME);
				purchaseReportManager.setValued(true);
				fireBeforeEmailSend(event, getTo());
				emailUtil.initMessageController(controller, (Purchase) getTo(), getMoreRecipients());
			} catch ( Throwable e ) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);				
			}			
		}
	}	
	
	public void onEmailSend( ActionEvent event ) throws ManagerBeanException {
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		messageController.onSend(event);
		updatePurchaseCommunication((Purchase) this.getTo());
	}
	
	protected void updatePurchaseCommunication(Purchase purchase) throws ManagerBeanException{
		purchase.setEmailCommunication(true);
		IManagerBean bean = BeanManager.getManagerBean(Purchase.class);
		bean.restoreNullSubPOJOs(purchase);
		bean.update(purchase);
	}
	
	@SuppressWarnings("unchecked")
	public byte[] getPurchaseData(Purchase purchase) throws ManagerBeanException {
		ITransferObject to = purchase;
		try {
			ReportManager reportManager = new ReportManager();
			reportManager.setCollectionProvider( new SingleCollectionProvider(to) );
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			reportManager.execute( out, "purchaseForm");
			return out.toByteArray();
		} catch (Throwable e) {
			LOGGER.error(">>>> onReport " + e.getMessage());
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onLoadInvoice(ActionEvent event) throws ManagerBeanException {
		Invoice invoice = getInvoice();
		if (invoice != null) {
			BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(PURCHASE_INVOICE_CONTROLLER_NAME);
			invoiceController.onLoad(event, invoice.getId(), PURCHASE_FORM_NAME, PURCHASE_CONTROLLER_NAME + ".refresh");
		}
	}
	

	/*
	 * 
	 * EMAIL EVENTS LISTENERS
	 * 
	 */
	protected void fireBeforeEmailSend(ActionEvent event, ITransferObject to) throws ManagerBeanException {
		for(IEmailControllerListener l: getEmailControllerListenerClasses()){
			l.beforeEmailSend(to);
		}
	}	

}
