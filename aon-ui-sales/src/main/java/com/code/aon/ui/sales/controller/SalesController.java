package com.code.aon.ui.sales.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MESSAGE;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.config.util.SeriesUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.SalesInvoicingManager;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.bridge.DeliveryManager;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.seller.Seller;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.customer.util.CustomerValidationManager;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.sales.util.PurchaseGeneratorManager;
import com.code.aon.ui.sales.util.SalesEmailUtil;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.entity.IEntityAlias;

public class SalesController extends BasicController implements ISalesConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalesController.class);

	private List<SelectItem> addresses;
	private List<SelectItem> projects;
	private Boolean defaultPayMethod;
	private IPriceStrategy priceStrategy;
	private RegistryValidationManager vm;
	private PurchaseGeneratorManager purchaseGenerator;
	private boolean showDeliveryWindow;
	private boolean showPurchaseWindow;
	private String deliverySeries;
	private int deliveryNumber;
	private Date deliveryDate;
	private Warehouse deliveryWarehouse;
	private boolean showInvoiceWindow;
	private String invoiceSeries;
	private int invoiceNumber;
	private Date invoiceDate;
	private Double salesTotalAmount;
	private SalesEmailUtil emailUtil;
	private String selectedTab;
	private boolean shippingAlternativeAddress;
	private boolean showShipmentWindow;
	
    public SalesController() {
    	this.emailUtil = new SalesEmailUtil();
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

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public List<SelectItem> getAddresses() {
		return addresses;
	}
	
	public void setAddresses(List<SelectItem> addresses) {
		this.addresses = addresses;
	}
	
    public List<SelectItem> getProjects() {
		return projects;
	}
	
	public void setProjects(List<SelectItem> projects) {
		this.projects = projects;
	}
	
	public Boolean getDefaultPayMethod() {
		return defaultPayMethod;
	}

	public void setDefaultPayMethod(Boolean defaultPayMethod) {
		this.defaultPayMethod = defaultPayMethod;
		if (defaultPayMethod != null && defaultPayMethod) {
			resetSalesPayMethod();
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
			vm = new CustomerValidationManager(); 
		}
		return vm;
	}
	
	public PurchaseGeneratorManager getPurchaseGenerator() {
		return purchaseGenerator;
	}

	public void setPurchaseGenerator(PurchaseGeneratorManager purchaseGenerator) {
		this.purchaseGenerator = purchaseGenerator;
	}

	public boolean isShowDeliveryWindow() {
		return showDeliveryWindow;
	}

	public void setShowDeliveryWindow(boolean value) {
		this.showDeliveryWindow = value;
	}
	
	public boolean isShowPurchaseWindow() {
		return showPurchaseWindow;
	}

	public void setShowPurchaseWindow(boolean showPurchaseWindow) {
		this.showPurchaseWindow = showPurchaseWindow;
	}

	public String getDeliverySeries() {
		return deliverySeries;
	}

	public void setDeliverySeries(String deliverySeries) {
		this.deliverySeries = deliverySeries;
	}

	public int getDeliveryNumber() {
		return deliveryNumber;
	}

	public void setDeliveryNumber(int deliveryNumber) {
		this.deliveryNumber = deliveryNumber;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Warehouse getDeliveryWarehouse() {
		return deliveryWarehouse;
	}

	public void setDeliveryWarehouse(Warehouse deliveryWarehouse) {
		this.deliveryWarehouse = deliveryWarehouse;
	}

	public boolean isShowInvoiceWindow() {
		return showInvoiceWindow;
	}

	public void setShowInvoiceWindow(boolean value) {
		this.showInvoiceWindow = value;
	}
	
	public String getInvoiceSeries() {
		return invoiceSeries;
	}

	public void setInvoiceSeries(String invoiceSeries) {
		this.invoiceSeries = invoiceSeries;
	}

	public int getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(int invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	
	public Double getSalesTotalAmount() {
		return salesTotalAmount;
	}

	public void setSalesTotalAmount(Double salesTotalAmount) {
		this.salesTotalAmount = salesTotalAmount;
	}
	
	public void getSelectionTotalAmount(ActionEvent event) {
		try {	
			IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
			List<ITransferObject> list = salesBean.getList(getCriteria());
			salesTotalAmount = 0.0;
			for(ITransferObject to: list){
				salesTotalAmount += getSalesTotalPrice((Sales) to);
			}
		} catch (ManagerBeanException e) {
			String message = "Imposible obtener el total";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}		
	}

	public boolean isCustomerReadOnly() throws ManagerBeanException {
		Sales sales = (Sales)this.getTo();
		if (sales.getProject() != null && sales.getProject().getId() != null) {
			return true;
		}
		return isInDelivery(sales);
	}

	public boolean isInDelivery() throws ManagerBeanException {
		Sales sales = (Sales)this.getTo();
		return isInDelivery(sales);
	}

	private boolean isInDelivery(Sales sales) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_SALES_DETAIL_SALES_ID), sales.getId());
		return deliveryDetailBean.getCount(criteria) > 0;
	}

	public boolean isPending(){
		Sales sales = (Sales)this.getTo();
		return sales.getStatus() == SalesStatus.PENDING;
	}

	public boolean isBlocked(){
		Sales sales = (Sales)this.getTo();
		return sales.getStatus() == SalesStatus.BLOCKED;
	}

	public boolean isServed (){
		Sales sales = (Sales)this.getTo();
		return sales.getStatus() == SalesStatus.SERVED;
	}

	public boolean isClosed(){
		Sales sales = (Sales)this.getTo();
		return sales.getStatus() == SalesStatus.CLOSED;
	}

	public boolean isInvoiced(){
		Sales sales = (Sales)this.getTo();
		return sales.getStatus() == SalesStatus.INVOICED;
	}

	public Invoice getInvoice() throws ManagerBeanException {
		Sales sales = (Sales)this.getTo();
		if (sales != null && sales.getId() != null) {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
			Iterator<?> iterator = salesDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				SalesDetail salesDetail = (SalesDetail)iterator.next();

				IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.SALES);
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE_ID), salesDetail.getId());
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

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		int number = obtainMaxNumber((String)event.getNewValue());
		SecurityLevel securityLevel = SeriesUtil.getSeriesSecurityLevel((String)event.getNewValue());
		if (this.getTo() != null) {
			((Sales)this.getTo()).setNumber(number);
			((Sales)this.getTo()).setSecurityLevel(securityLevel);
		}
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	return SeriesNumberUtil.obtainNumber(seriesId, StringUtils.capitalize(this.getBeanName()));
	}

	public void customerData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			isBlocked(customer);
			((Sales)this.getTo()).setCustomer(customer);
			loadAddresses(customer.getId());
			loadProjects(customer.getId());
			loadDefaultPayMethod(customer.getId(), false);
		} else {
			setAddresses(null);
			setProjects(null);
		}
	}
	
	public void onWorkPlaceChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			WorkPlace workPlace = (WorkPlace)event.getNewValue();
			((Sales)this.getTo()).setWorkPlace(workPlace);
			((Sales)this.getTo()).setScope(workPlace.getScope());
		}
	}

	private boolean isBlocked(Customer customer) {
		return getRegistryValidationManager().isBlocked(customer);
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
	
	public void loadProjects(Integer id) throws ManagerBeanException {
		this.projects = new LinkedList<SelectItem>();
		if (id != null) {
			IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_REGISTRY_ID), id);
			criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_ACTIVE), new Boolean(true));
			criteria.addOrder(projectBean.getFieldName(IEntityAlias.PROJECT_NAME));
			Iterator<?> iterator = projectBean.getList(criteria).iterator();
			while(iterator.hasNext()) {
				Project project = (Project)iterator.next();
				SelectItem item = new SelectItem(project, project.getName());
				projects.add(item);
			}
		}
	}

	public int getProjectCount() {
		if (projects != null) {
			return projects.size();
		}
		return 0;
	}
	
	public void removeSalesProject(ActionEvent event) throws ManagerBeanException {
		Sales to = (Sales)this.getTo();
		to.setProject(null);
		getManagerBean().restoreNullSubPOJOs(to);
		getManagerBean().update(to);
		getManagerBean().initializePOJO(to);
	}

	public void loadDefaultPayMethod(Integer id, boolean forceDefault) throws ManagerBeanException {
		if (id != null) {
			if (((Sales)this.getTo()).getPayMethod() != null && ((Sales)this.getTo()).getPayMethod().getId() != null) {
				setDefaultPayMethod(false);
			} else {
				if (forceDefault) {
					setDefaultPayMethod(true);
				} else {
					IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(rPayMethodBean.getFieldName(IEntityAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), id);
					Iterator<ITransferObject> iter = rPayMethodBean.getList(criteria).iterator();
					setDefaultPayMethod(iter.hasNext());
				}
			}
		}
	}

	public void resetSalesPayMethod() {
		Sales to = (Sales)this.getTo();
		to.setPayMethod(new PayMethod());
		to.setNumberOfPayments(1);
		to.setDaysToFirstPayment(0);
		to.setDaysBetweenPayments(0);
		to.setPaymentDays("");
		to.setBank(new Bank());
		to.setBankAccount(new BankAccount());
	}

	public void sellerData(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Seller seller = (Seller)event.getNewValue();
			((Sales)this.getTo()).setSeller(seller);
		}
	}

	public double getTaxableBase(){
		return getPriceStrategy().getTaxableBase((ICalculableContainer)getTo());
	}

	public double getTotalPrice(){
		return getPriceStrategy().getTotalPrice((ICalculableContainer)getTo(), ((Sales)getTo()).getCustomer());
	}

	public double getSalesTotalPrice() throws ManagerBeanException {
		Sales sales = (Sales)this.getModel().getRowData();
		return getSalesTotalPrice(sales);
	}
	
	public double getSalesTotalPrice(Sales sales) throws ManagerBeanException {
		return getPriceStrategy().getTotalPrice(sales, sales.getCustomer());
	}

	public void onPending(ActionEvent event) {
		Sales to = (Sales)this.getTo();
		to.setStatus(SalesStatus.PENDING);
		accept(event);
	}
	
	public void onClose(ActionEvent event) {
		Sales to = (Sales)this.getTo();
		to.setStatus(SalesStatus.CLOSED);
		accept(event);
	}

	public void onBlock(ActionEvent event) {
		Sales to = (Sales)this.getTo();
		to.setStatus(SalesStatus.BLOCKED);
		accept(event);
	}
	
	public void onUnblock(ActionEvent event) throws ManagerBeanException {
		Sales to = (Sales)this.getTo();
		to.setStatus(SalesStatus.PENDING);
		accept(event);
	}

	public void onDeliveryShow(ActionEvent event) throws ManagerBeanException {
		Sales to = (Sales)this.getTo();
		setDeliverySeries(SeriesUtil.ensureDeliverySeries(to.getSeries()));
		setDeliveryNumber(obtainMaxDeliveryNumber(getDeliverySeries()));
		setDeliveryDate(new Date());
		setDeliveryWarehouse(obtainDeliveryWarehouse(to.getWorkPlace()));
	}

	public void onDeliverySeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		setDeliveryNumber(obtainMaxDeliveryNumber((String)event.getNewValue()));
	}

	private int obtainMaxDeliveryNumber(String seriesId) {
		return SeriesNumberUtil.obtainNumber(seriesId, "Delivery");
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

	public void onDelivery(ActionEvent event) {
		try {
			Sales to = (Sales)this.getTo();
			DeliveryManager deliveryManager = new DeliveryManager();
			Delivery delivery = deliveryManager.salesDelivery(to, getDeliverySeries(), getDeliveryNumber(), getDeliveryDate(), getDeliveryWarehouse());

			IController deliveryController = FormUtil.getController(DELIVERY_CONTROLLER_NAME);
			deliveryController.onEditSearch(event);
			deliveryController.getCriteria().addEqualExpression(deliveryController.getFieldName(IEntityAlias.DELIVERY_ID), delivery.getId());
			deliveryController.onSearch(event);
			deliveryController.getModel().setRowIndex(0);
			deliveryController.onSelect(event);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo grabar el albarán. (" + e.getMessage()+ ")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void onInvoiceShow(ActionEvent event) throws ManagerBeanException {
		Sales to = (Sales)this.getTo();
		setInvoiceSeries(SeriesUtil.ensureInvoiceSeries(to.getSeries()));
		setInvoiceNumber(obtainMaxInvoiceNumber(getInvoiceSeries()));
		setInvoiceDate(new Date());
	}

	public void onInvoiceSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		setInvoiceNumber(obtainMaxInvoiceNumber((String)event.getNewValue()));
	}

	private int obtainMaxInvoiceNumber(String seriesId) {
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		return SeriesNumberUtil.obtainNumber(seriesId, "Invoice", criteria);
	}

	public void onInvoice(ActionEvent event) {
		try {
			Sales to = (Sales)this.getTo();
			SalesInvoicingManager invoicingManager = new SalesInvoicingManager();
			Invoice invoice = invoicingManager.invoice(to, getInvoiceSeries(), getInvoiceNumber(), getInvoiceDate());

			IController invoiceController = FormUtil.getController(SALE_INVOICE_CONTROLLER_NAME);
			invoiceController.onEditSearch(event);
			invoiceController.getCriteria().addEqualExpression(invoiceController.getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
			invoiceController.onSearch(event);
			invoiceController.getModel().setRowIndex(0);
			invoiceController.onSelect(event);
		} catch (ManagerBeanException e) {
			String msg = "No se pudo grabar la factura. (" + e.getMessage()+ ")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

	public void onSendByEmail( ActionEvent event ) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {			
			try {
				controller.onNewMessage(event);
				emailUtil.initMessageController(controller, (Sales) getTo());	
			} catch ( Throwable e ) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);				
			}
		}
	}	

	public void onLoadInvoice(ActionEvent event) throws ManagerBeanException {
		Invoice invoice = getInvoice();
		if (invoice != null) {
			BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
			invoiceController.onLoad(event, invoice.getId(), SALES_FORM_NAME, SALES_CONTROLLER_NAME + ".refresh");
		}
	}
	
	public void onPurchaseGenerationShow(ActionEvent event) throws ManagerBeanException {
		setPurchaseGenerator(new PurchaseGeneratorManager((Sales) this.getTo()));
	}
	
	public boolean isShippingDataDefined() {
		Sales sales = (Sales) this.getTo();
		if(sales!=null){
			if( StringUtils.isNotBlank(sales.getShippingContact())
					|| sales.getShippingPeriod()!=null
					|| isShippingAlternativeAddressDefined() ){
				return true;
			}
		}
		return false;
	}
	
	public boolean isShippingAlternativeAddressDefined() {
		Sales sales = (Sales) this.getTo();
		if(sales!=null){
			if( StringUtils.isNotBlank(sales.getShippingAlternativeAddress())
				|| StringUtils.isNotBlank(sales.getShippingAlternativeAddress2())
				|| StringUtils.isNotBlank(sales.getShippingAlternativeZip())
				|| StringUtils.isNotBlank(sales.getShippingAlternativeCity())
				|| StringUtils.isNotBlank(sales.getShippingAlternativePhone())
				|| StringUtils.isNotBlank(sales.getShippingAlternativeRecipient()) ){
				return true;
			}
		}
		return false;
	}

}
