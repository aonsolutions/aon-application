package com.code.aon.ui.purchase.controller;

import static com.code.aon.ui.common.ICommonMessages.ITEM_SERIALIZABLE_REQUIRED_ERROR;
import static com.code.aon.ui.common.ICommonMessages.PURCHASE_RETURNED_IN_MSG;
import static com.code.aon.ui.common.ICommonMessages.PURCHASE_RETURN_OVER_MSG;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Finance;
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
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.purchase.util.IEmailControllerListener;
import com.code.aon.purchase.util.IEmailable;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.config.BankAccountHelper;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.HeaderObjectController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.purchase.util.PurchaseEmailUtil;
import com.code.aon.ui.purchase.util.PurchaseUtils;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.supplier.util.SupplierValidationManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.WarehouseCollectionsController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.carrier.Carrier;
import com.esferalia.aon.entity.IEntityAlias;

public class PurchaseController extends HeaderObjectController implements IPurchaseConstants, IEmailable, IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseController.class.getName());

	private List<SelectItem> addresses;
	private Boolean defaultPayMethod;
	private IPriceStrategy priceStrategy;
	private RegistryValidationManager vm;
	private boolean showProjectWindow;
	private boolean showDetailProjectWindow;
	private boolean showIncomeWindow;
	private String incomeReferenceCode;
	private Date incomeDate;
	private Warehouse incomeWarehouse;
	private boolean showInvoiceWindow;
	private String invoiceRefCode;
	private Date invoiceDate;
	private boolean showAuditInfoWindow;
	private Double listTotal;
	private PurchaseEmailUtil emailUtil;
	private boolean shippingAlternativeAddress;
	private boolean showShipmentWindow;
	private boolean showCommentsWindow;
	private boolean showRemarksWindow;
	private Purchase returnSourcePurchase;
	private BankAccountHelper accountHelper;
	private Date savedDeliveryDate;
	private Carrier savedCarrier;
	
	private List<String> moreRecipients;
	private List<IEmailControllerListener> emailControllerListenerClasses;
	
	public PurchaseController() {
    	this.emailUtil = new PurchaseEmailUtil();
    	this.accountHelper = new BankAccountHelper(this);
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
	
	public String getIncomeReferenceCode() {
		return incomeReferenceCode;
	}

	public void setIncomeReferenceCode(String incomeReferenceCode) {
		this.incomeReferenceCode = incomeReferenceCode;
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
	
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
	public Double getListTotal() {
		return listTotal;
	}

	public void setListTotal(Double listTotal) {
		this.listTotal = listTotal;
	}
	
	public boolean isShippingAlternativeAddress() {
		return shippingAlternativeAddress;
	}

	public void setShippingAlternativeAddress(boolean shippingAlternativeAddress) {
		this.shippingAlternativeAddress = shippingAlternativeAddress;
	}
	
	public boolean isShowShipmentWindow() {
		return showShipmentWindow;
	}

	public void setShowShipmentWindow(boolean showShipmentWindow) {
		this.showShipmentWindow = showShipmentWindow;
	}
	
	public boolean isShowCommentsWindow() {
		return showCommentsWindow;
	}

	public void setShowCommentsWindow(boolean showCommentsWindow) {
		this.showCommentsWindow = showCommentsWindow;
	}

	public boolean isShowRemarksWindow() {
		return showRemarksWindow;
	}

	public void setShowRemarksWindow(boolean showRemarksWindow) {
		this.showRemarksWindow = showRemarksWindow;
	}

	public BankAccountHelper getAccountHelper() {
		return accountHelper;
	}

	public Date getSavedDeliveryDate() {
		return savedDeliveryDate;
	}

	public void setSavedDeliveryDate(Date savedDeliveryDate) {
		this.savedDeliveryDate = savedDeliveryDate;
	}

	public Carrier getSavedCarrier() {
		return savedCarrier;
	}

	public void setSavedCarrier(Carrier savedCarrier) {
		this.savedCarrier = savedCarrier;
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
	
	public List<IEmailControllerListener> getEmailControllerListenerClasses() {
		return emailControllerListenerClasses;
	}

	public void setEmailControllerListenerClasses(
			List<IEmailControllerListener> emailControllerListenerClasses) {
		this.emailControllerListenerClasses = emailControllerListenerClasses;
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
			((Purchase)this.getTo()).setScope(supplier.getScope());
			loadAddresses(supplier.getId());
			loadDefaultPayMethod(supplier.getRegistry(), true);
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

	public void emptyAddresses() {
		setAddresses(null);
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

	public void loadDefaultPayMethod(Registry registry, boolean forceReset) throws ManagerBeanException {
		if (registry != null && registry.getId() != null) {
			if (forceReset) {
				resetPurchasePayMethod();
				setDefaultPayMethod(registry.getPayMethod() != null);
			} else {
				Purchase purchase = (Purchase)getTo();
				setDefaultPayMethod(purchase.getPayMethod() == null || purchase.getPayMethod().getId() == null);
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
		to.setBankAccount(new BankAccount());
		to.setBankAlias(null);
		to.setBic(null);
	}

	public void linkDeliveryDate(Purchase purchase) throws ManagerBeanException {
		if ((purchase.getDeliveryDate() != null && getSavedDeliveryDate() == null) || (purchase.getDeliveryDate() == null && getSavedDeliveryDate() != null) ||
				(!DateUtils.isSameDay(purchase.getDeliveryDate(), getSavedDeliveryDate()))) {
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
			if (purchase.getDeliveryDate() == null) {
				criteria.addNotNullExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_DELIVERY_DATE));
			} else {
				if (getSavedDeliveryDate() == null) {
					criteria.addNullExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_DELIVERY_DATE));
				} else {
					criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_DELIVERY_DATE), getSavedDeliveryDate());
				}
			}
			for (ITransferObject ito : purchaseDetailBean.getList(criteria)) {
				PurchaseDetail purchaseDetail = (PurchaseDetail)ito;
				purchaseDetail.setDeliveryDate(purchase.getDeliveryDate());
				purchaseDetailBean.update(purchaseDetail);
			}
		}
		setSavedDeliveryDate(purchase.getDeliveryDate());
	}

	public void linkCarrier(Purchase purchase) throws ManagerBeanException {
		Carrier carrier = (purchase.getCarrier() != null && purchase.getCarrier().getId() != null) ? purchase.getCarrier() : null;
		setSavedCarrier((getSavedCarrier() != null && getSavedCarrier().getId() != null) ? getSavedCarrier() : null);
		if ((carrier == null && getSavedCarrier() != null) || (carrier != null && !carrier.equals(getSavedCarrier()))) {
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
			if (carrier == null) {
				criteria.addNotNullExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_CARRIER));
			} else {
				if (getSavedCarrier() == null) {
					criteria.addNullExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_CARRIER));
				} else {
					criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_CARRIER_ID), getSavedCarrier().getId());
				}
			}
			for (ITransferObject ito : purchaseDetailBean.getList(criteria)) {
				PurchaseDetail purchaseDetail = (PurchaseDetail)ito;
				purchaseDetail.setCarrier(carrier);
				purchaseDetailBean.update(purchaseDetail);
			}
		}
		setSavedCarrier(purchase.getCarrier());
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
	
	public double getTotalDetailQuantity(){
		try {
			IManagerBean detailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), ((Purchase)this.getTo()).getId());
			Projection projection = Projection.sum(detailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_QUANTITY));
			Object value = detailBean.getUniqueResult(projection, criteria);
			return (value != null) ? ((Double)value) : 0;
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Se ha producido un error al obtener la cantidad total de unidades");
		}
		return 0;
	}

	public void obtainListTotals(ActionEvent event) {
		double listTotal = 0.0; 
		try {	
			for (ITransferObject ito : getManagerBean().getList(getCriteria())) {
				listTotal += getPurchaseTotalPrice((Purchase)ito);
			}
		} catch (ManagerBeanException e) {
			String message = "Imposible obtener el Total";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}		
		setListTotal(listTotal);
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
		validate(to);
		setIncomeReferenceCode(null);
		setIncomeDate(new Date());
		setIncomeWarehouse(to.getWarehouse());
	}
		
	private void validate(Purchase purchase) {
		try {
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
			criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_ITEM_PRODUCT_SERIALIZABLE), Boolean.TRUE);
			Expression serialNullExp = ExpressionUtilities.getNullExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_ITEM_SERIAL_NUMBER));
			Expression serialEmptyExp = ExpressionUtilities.getEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_ITEM_SERIAL_NUMBER), "");
			criteria.addExpression(ExpressionUtilities.getOrExpression(serialNullExp, serialEmptyExp));
			if (purchaseDetailBean.getCount(criteria) > 0) {
				String message = AonUtil.addErrorMessageFromBundle(ITEM_SERIALIZABLE_REQUIRED_ERROR);
				throw new AbortProcessingException(message);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage());
		}
	}

	public void onIncome(ActionEvent event) throws ManagerBeanException {
		Purchase to = (Purchase)this.getTo();
		try {
			IncomeManager incomeManager = new IncomeManager();
			Income income = incomeManager.purchaseIncome(to, getIncomeReferenceCode(), getIncomeDate(), getIncomeWarehouse());

			BasicController incomeController = (BasicController)AonUtil.getRegisteredBean(INCOME_CONTROLLER_NAME);
			incomeController.onLoad(event, income.getId(), PURCHASE_FORM_NAME, PURCHASE_CONTROLLER_NAME + ".refresh");		
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onInvoiceShow(ActionEvent event) {
		Purchase to = (Purchase)this.getTo();
		validate(to);
		setInvoiceRefCode(null);
		setInvoiceDate(new Date());
	}

	public void onInvoice(ActionEvent event) throws ManagerBeanException {
		Purchase to = (Purchase)this.getTo();
		try {
			PurchaseInvoicingManager invoicingManager = new PurchaseInvoicingManager();
			invoicingManager.invoice(to, getInvoiceRefCode(), getInvoiceDate());
			onLoadInvoice(event);
		} catch (Throwable ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
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
				this.refresh(event);
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
		bean.initializePOJO(purchase);
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
	
	public void onReturnRequest(ActionEvent event) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Finance.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			returnSourcePurchase = (Purchase) this.getTo();
			String comments = AonUtil.getMessage(PURCHASE_RETURN_OVER_MSG, returnSourcePurchase.getReferenceCode());
			comments += StringUtils.isBlank(returnSourcePurchase.getComments())?"":returnSourcePurchase.getComments();
			PurchaseUtils utils = new PurchaseUtils();
			Purchase purchase = utils.createPurchase(returnSourcePurchase.getSeries(),
					returnSourcePurchase.getSupplier(), returnSourcePurchase.getWorkPlace(),
					returnSourcePurchase.getWarehouse(),
							PurchaseDocumentType.ITEM_RETURN,
							comments, returnSourcePurchase.getRemarks(),
							returnSourcePurchase.getCarrier(),
							returnSourcePurchase.getPurchaseReference(),
							returnSourcePurchase.getShippingAlternativeAddress(),
							returnSourcePurchase.getShippingAlternativeAddress2(),
							returnSourcePurchase.getShippingAlternativeZip(),
							returnSourcePurchase.getShippingAlternativeCity(),
							returnSourcePurchase.getShippingAlternativePhone(),
							returnSourcePurchase.getShippingAlternativeRecipient(),
							returnSourcePurchase.getShippingContact(),
							returnSourcePurchase.getShippingPeriod());
			markSourcePurchaseAsReturned(purchase.getReferenceCode());
			PurchaseDetailController detailController = (PurchaseDetailController) AonUtil.getRegisteredBean(PURCHASE_DETAIL_CONTROLLER_NAME);
			for(ITransferObject to: detailController.getWrappedList()){
				PurchaseDetail detail = (PurchaseDetail) to;
				utils.createPurchaseDetail(purchase, detail.getItem(), detail.getProject(), detail.getProposalDetail(),
						detail.getLine(), detail.getDescription(), (-1)*detail.getQuantity(), detail.getPrice(), detail.getDiscountExpression(),
						detail.getTaxes(), PurchaseDetailStatus.PENDING, 0);
			}
			
			HibernateUtil.commitTransaction(sessionName);
			this.onLoad(event, purchase.getId(), PURCHASE_FORM_NAME, PURCHASE_CONTROLLER_NAME+".restoreSourcePurchase");
		} catch (Exception e) {
			String msg = "Error al crear la solicitud de devolucion.";
			AonUtil.addErrorMessage(msg  + e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private void markSourcePurchaseAsReturned(String referenceCode) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Purchase.class);
		String comments = AonUtil.getMessage(PURCHASE_RETURNED_IN_MSG, referenceCode);
		comments += StringUtils.isBlank(returnSourcePurchase.getComments())?"":returnSourcePurchase.getComments();
		returnSourcePurchase.setComments(comments);
		bean.restoreNullSubPOJOs(returnSourcePurchase);
		bean.update(returnSourcePurchase);
	}
	
	public void restoreSourcePurchase( ActionEvent event ) throws ManagerBeanException {
		this.select(event, returnSourcePurchase);
	}

	public boolean isShippingDataDefined() {
		Purchase purchase = (Purchase) this.getTo();
		if(purchase!=null){
			if( StringUtils.isNotBlank(purchase.getShippingContact())
					|| purchase.getShippingPeriod()!=null
					|| (purchase.getCarrier()!=null && purchase.getCarrier().getId()!=null)
					|| isShippingAlternativeAddressDefined() ){
				return true;
			}
		}
		return false;
	}
	
	public boolean isShippingAlternativeAddressDefined() {
		Purchase purchase = (Purchase) this.getTo();
		if(purchase!=null){
			if( StringUtils.isNotBlank(purchase.getShippingAlternativeAddress())
				|| StringUtils.isNotBlank(purchase.getShippingAlternativeAddress2())
				|| StringUtils.isNotBlank(purchase.getShippingAlternativeZip())
				|| StringUtils.isNotBlank(purchase.getShippingAlternativeCity())
				|| StringUtils.isNotBlank(purchase.getShippingAlternativePhone())
				|| StringUtils.isNotBlank(purchase.getShippingAlternativeRecipient()) ){
				return true;
			}
		}
		return false;
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

	@Override
	public List<SelectItem> getSeriesCodes() throws ManagerBeanException {
		ConfigCollectionsController ccc = (ConfigCollectionsController) AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		return ccc.getSalesSeriesIds();
	}

	private boolean hasDetails( Purchase purchase) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(PurchaseDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), purchase.getId());
			return bean.getCount(criteria) > 0;
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return false;
	}
	
	public void onMassiveClosure(ActionEvent event) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Finance.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			List<ITransferObject> list = getManagerBean().getList(getCriteria());
			for( ITransferObject to : list ) {
				Purchase purchase = (Purchase) to;
				if ( purchase.isPending() && hasDetails(purchase) ) {
					purchase.setStatus(PurchaseStatus.CLOSED);
					getManagerBean().update(to);					
				}
			}

			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			AonUtil.addErrorMessage(e.getMessage());
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				LOGGER.error("Unable to rollback transaction!", e);
			}
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
		initializeModel();
	}
	
	public List<SelectItem> getWarehouses() throws ManagerBeanException {
		Purchase purchase = (Purchase)this.getTo();
		return WarehouseCollectionsController.getWarehouses(purchase.getWorkPlace());
	}
	
}