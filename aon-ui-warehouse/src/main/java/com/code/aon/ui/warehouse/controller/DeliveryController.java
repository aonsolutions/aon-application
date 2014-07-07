package com.code.aon.ui.warehouse.controller;

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

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.config.util.BankUtil;
import com.code.aon.config.util.SeriesUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.DeliveryInvoicingManager;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.bridge.DeliveryManager;
import com.code.aon.sales.bridge.SalesTransferManager;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.HeaderObjectController;
import com.code.aon.ui.customer.util.CustomerValidationManager;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.util.WarehouseEmailUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class DeliveryController extends HeaderObjectController implements IWarehouseConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryController.class);

	private List<SelectItem> addresses;
	private List<SelectItem> projects;
	private Warehouse warehouse;
	private Boolean defaultPayMethod;
	private IPriceStrategy priceStrategy;
	private RegistryValidationManager vm;
	private SalesTransferManager salesTransferManager;
	private boolean showSalesTransferWindow;
	private boolean showInvoiceWindow;
	private String invoiceSeries;
	private int invoiceNumber;
	private Date invoiceDate;
	private WarehouseEmailUtil emailUtil;
	private String selectedTab;
	private boolean shippingAlternativeAddress;
	
    public DeliveryController() {
    	this.emailUtil = new WarehouseEmailUtil();
    }

	public boolean isShippingAlternativeAddress() {
		return shippingAlternativeAddress;
	}

	public void setShippingAlternativeAddress(boolean shippingAlternativeAddress) {
		this.shippingAlternativeAddress = shippingAlternativeAddress;
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
	
    public Warehouse getWarehouse() {
		return warehouse;
	}
	
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	
	public Boolean getDefaultPayMethod() {
		return defaultPayMethod;
	}
	
	public void setDefaultPayMethod(Boolean defaultPayMethod) {
		this.defaultPayMethod = defaultPayMethod;
		if (defaultPayMethod != null && defaultPayMethod) {
			resetDeliveryPayMethod();
		}
	}
	
	public IPriceStrategy getPriceStrategy() {
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

	public SalesTransferManager getSalesTransferManager() {
		if (salesTransferManager == null) {
			salesTransferManager = new SalesTransferManager(); 
		}
		return salesTransferManager;
	}

	public void setSalesTransferManager(SalesTransferManager salesTransferManager) {
		this.salesTransferManager = salesTransferManager;
	}

	public boolean isShowSalesTransferWindow() {
		return showSalesTransferWindow;
	}

	public void setShowSalesTransferWindow(boolean value) {
		this.showSalesTransferWindow = value;
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
	
	public Double getDeliveriesTotalAmount() throws ManagerBeanException {
		double deliveriesTotalAmount = 0.0; 
		for(ITransferObject to: this.getWrappedList()){
			Delivery d = (Delivery) to;
			deliveriesTotalAmount += getDeliveryTotalPrice(d);
		}
		return deliveriesTotalAmount;
	}

	public boolean isCustomerReadOnly() {
		Delivery delivery = (Delivery)this.getTo();
		return (delivery.getProject() != null && delivery.getProject().getId() != null);
	}

	public boolean isPending(){
		Delivery delivery = (Delivery)this.getTo();
		return delivery.getStatus() == DeliveryStatus.PENDING;
	}

	public boolean isInvoiced(){
		Delivery delivery = (Delivery)this.getTo();
		return delivery.getStatus() == DeliveryStatus.INVOICED;
	}

	public Invoice getInvoice() throws ManagerBeanException {
		Delivery delivery = (Delivery)this.getTo();
		if (delivery != null && delivery.getId() != null) {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
			Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				DeliveryDetail deliveryDetail = (DeliveryDetail)iterator.next();

				IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DELIVERY);
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE_ID), deliveryDetail.getId());
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

	public void customerData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			isBlocked(customer);
			((Delivery)this.getTo()).setCustomer(customer);
			loadAddresses(customer.getId());
			loadProjects(customer.getId());
			loadDefaultPayMethod(customer.getId(), false);
		} else {
			setAddresses(null);
			setProjects(null);
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
	
	public void removeDeliveryProject(ActionEvent event) throws ManagerBeanException {
		Delivery to = (Delivery)this.getTo();
		to.setProject(null);
		getManagerBean().restoreNullSubPOJOs(to);
		getManagerBean().update(to);
		getManagerBean().initializePOJO(to);
	}

	public void loadDefaultPayMethod(Integer id, boolean forceDefault) throws ManagerBeanException {
		if (id != null) {
			if (((Delivery)this.getTo()).getPayMethod() != null && ((Delivery)this.getTo()).getPayMethod().getId() != null) {
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

	public void resetDeliveryPayMethod() {
		Delivery to = (Delivery)this.getTo();
		to.setPayMethod(new PayMethod());
		to.setNumberOfPayments(1);
		to.setDaysToFirstPayment(0);
		to.setDaysBetweenPayments(0);
		to.setPaymentDays("");
		to.setBankAccount(new BankAccount());
		to.setBankAlias(null);
		to.setBic(null);
	}

	public void onBankAccountData(ActionEvent event) {
		BankUtil.fillBankAccountData((Delivery)getTo());
	}

	public void onWorkPlaceChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			WorkPlace workPlace = (WorkPlace)event.getNewValue();
			((Delivery)this.getTo()).setWorkPlace(workPlace);
			((Delivery)this.getTo()).setScope(workPlace.getScope());
			setWarehouse(obtainWarehouse(((Delivery)this.getTo())));
		}
	}

	public Warehouse obtainWarehouse(Delivery delivery) throws ManagerBeanException {
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
		Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			return ((DeliveryDetail)iterator.next()).getWarehouse();
		} else if (delivery.getWorkPlace() != null) {
			IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
			criteria = new Criteria();
			criteria.addEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_ID), delivery.getWorkPlace().getId());
			criteria.addOrder(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_NAME));
			iterator = warehouseBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				return (Warehouse)iterator.next();
			} 
		}
		return null;
	}
	
	public List<SelectItem> getWarehouses() throws ManagerBeanException {
		WorkPlace workPlace = ((Delivery)this.getTo()).getWorkPlace();
		LinkedList<SelectItem> warehouses = new LinkedList<SelectItem>();
		if(workPlace!=null){
			IManagerBean warehouseBean = BeanManager.getManagerBean(Warehouse.class);
			Criteria criteria = new Criteria();
			Expression exp1 = ExpressionUtilities.getNullExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE));
			Expression exp2 = ExpressionUtilities.getEqualExpression(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_WORK_PLACE_ID), workPlace.getId());
			criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
			criteria.addOrder(warehouseBean.getFieldName(IEntityAlias.WAREHOUSE_NAME));
			List<ITransferObject> c = warehouseBean.getList(criteria);
			Iterator<ITransferObject> iter = c.iterator();
			while (iter.hasNext()) {
				Warehouse warehouse = (Warehouse) iter.next();
				SelectItem item = new SelectItem(warehouse, warehouse.getName());
				warehouses.add(item);
			}
		}
		return warehouses;
	}

	public double getTaxableBase(){
		return getPriceStrategy().getTaxableBase((ICalculableContainer)getTo());
	}

	public double getTotalPrice(){
		return getPriceStrategy().getTotalPrice((ICalculableContainer)getTo(), ((Delivery)getTo()).getCustomer());
	}

	public double getDeliveryTotalPrice() throws ManagerBeanException {
		Delivery delivery = (Delivery)this.getModel().getRowData();
		return getDeliveryTotalPrice(delivery) ;
	}
	
	private double getDeliveryTotalPrice(Delivery delivery) throws ManagerBeanException {
		return getPriceStrategy().getTotalPrice(delivery, delivery.getCustomer());
	}
	
	public double getTotalDetailQuantity(){
		try {
			IManagerBean detailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), ((Delivery)this.getTo()).getId());
			Projection projection = Projection.sum(detailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_QUANTITY));
			Object value = detailBean.getUniqueResult(projection, criteria);
			return (value != null) ? ((Double)value) : 0;
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Se ha producido un error al obtener la cantidad total de unidades");
		}
		return 0;
	}

	public void onSalesTransferShow(ActionEvent event) throws ManagerBeanException {
		Delivery to = (Delivery)this.getTo();

		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_CUSTOMER_ID), to.getCustomer().getId());
		if (to.getProject() != null && to.getProject().getId() != null) {
			criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_PROJECT_ID), to.getProject().getId());
		}
		if (to.getRegistryAddress() != null && to.getRegistryAddress().getId() != null) {
			criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_SHIPPING_ADDRESS_ID), to.getRegistryAddress().getId());
		}
		criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_STATUS), SalesStatus.PENDING);
		criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_SECURITY_LEVEL), to.getSecurityLevel());
		criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_WORK_PLACE_ID), to.getWorkPlace().getId());
		criteria.addOrder(salesBean.getFieldName(IEntityAlias.SALES_ISSUE_DATE));
		criteria.addOrder(salesBean.getFieldName(IEntityAlias.SALES_SERIES));
		criteria.addOrder(salesBean.getFieldName(IEntityAlias.SALES_NUMBER));
		getSalesTransferManager().setSalesList(salesBean.getList(criteria));

		// si el albaran no tiene address definido, se tienen tambien en cuenta los pedidos cuyo address = null
		if (to.getRegistryAddress() != null && to.getRegistryAddress().getId() != null) {
			criteria = new Criteria();
			criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_CUSTOMER_ID), to.getCustomer().getId());
			if (to.getProject() != null && to.getProject().getId() != null) {
				criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_PROJECT_ID), to.getProject().getId());
			}
			criteria.addNullExpression(salesBean.getFieldName(IEntityAlias.SALES_SHIPPING_ADDRESS));
			criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_STATUS), SalesStatus.PENDING);
			criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_SECURITY_LEVEL), to.getSecurityLevel());
			criteria.addEqualExpression(salesBean.getFieldName(IEntityAlias.SALES_WORK_PLACE_ID), to.getWorkPlace().getId());
			criteria.addOrder(salesBean.getFieldName(IEntityAlias.SALES_ISSUE_DATE));
			criteria.addOrder(salesBean.getFieldName(IEntityAlias.SALES_SERIES));
			criteria.addOrder(salesBean.getFieldName(IEntityAlias.SALES_NUMBER));
			getSalesTransferManager().getSalesList().addAll(salesBean.getList(criteria));
		}
	}

	public void onSalesTransfer(ActionEvent event) throws ManagerBeanException {
		Iterator<SalesDetail> iterator = getSalesTransferManager().getCheckedDetails().iterator();
		while (iterator.hasNext()) {
			SalesDetail salesDetail = iterator.next();
			if (salesDetail.getTransfered() > 0) {
				DeliveryManager deliveryManager = new DeliveryManager();
				deliveryManager.transferDeliveryDetail((Delivery)this.getTo(), salesDetail, getWarehouse());
			}
		}

		refresh(null);
		IController detailController = FormUtil.getController(DELIVERY_DETAIL_CONTROLLER_NAME);
		detailController.onSearch(null);
	}
	
	private SaleInvoiceController getSaleInvoiceController() {
		return (SaleInvoiceController) AonUtil.getRegisteredBean(IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME);
	}		

	public void onInvoiceShow(ActionEvent event) throws ManagerBeanException {
		Delivery to = (Delivery)this.getTo();
		SaleInvoiceController controller = getSaleInvoiceController();
		controller.onCancel(event);
		controller.initSeries(false);
		setInvoiceSeries(SeriesUtil.ensureInvoiceSeries(to.getSeries()));
		setInvoiceNumber(0);
		setInvoiceDate(new Date());
	}

	public void onInvoiceSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		if ( getSaleInvoiceController().isNumberEditable() ) {			
			updateInvoiceNumber((String)event.getNewValue());	
		}
	}
	
	public void onInvoiceNumberEditable(ActionEvent event) throws ManagerBeanException {
		updateInvoiceNumber(getInvoiceSeries());		
	}		

	private void updateInvoiceNumber(String seriesId) {
		setInvoiceNumber(getSaleInvoiceController().obtainMaxNumber(seriesId));
	}

	public void onInvoice(ActionEvent event) throws ManagerBeanException {
		Delivery to = (Delivery)this.getTo();
        if(getInvoiceNumber() == 0) {
        	updateInvoiceNumber(getInvoiceSeries());
		}															
		DeliveryInvoicingManager invoicingManager = new DeliveryInvoicingManager();
		Invoice invoice = invoicingManager.invoice(to, getInvoiceSeries(), getInvoiceNumber(), getInvoiceDate());

		IController invoiceController = FormUtil.getController(SALE_INVOICE_CONTROLLER_NAME);
		invoiceController.onEditSearch(event);
		invoiceController.getCriteria().addEqualExpression(invoiceController.getFieldName(IEntityAlias.INVOICE_ID), invoice.getId());
		invoiceController.onSearch(event);
		invoiceController.getModel().setRowIndex(0);
		invoiceController.onSelect(event);
	}

	public void onSendByEmail( ActionEvent event ) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {			
			try {			
				controller.onNewMessage(event);
				emailUtil.initMessageController(controller, (Delivery) getTo(), getReportTemplate());
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
			invoiceController.onLoad(event, invoice.getId(), DELIVERY_FORM_NAME, DELIVERY_CONTROLLER_NAME + ".refresh");
		}
	}
	
	public String getReportTemplate() throws ManagerBeanException {
		String value = AppParamUtil.getValue(AppParam.APP_DELIVERY_TEMPLATE_PARAM);
		return ( value != null) ? value : DELIVERY_CONTROLLER_NAME;
	}
	
	public boolean isShippingAlternativeAddressDefined() {
		Delivery delivery = (Delivery) this.getTo();
		if(delivery!=null){
			if( StringUtils.isNotBlank(delivery.getShippingAlternativeAddress())
				|| StringUtils.isNotBlank(delivery.getShippingAlternativeAddress2())
				|| StringUtils.isNotBlank(delivery.getShippingAlternativeZip())
				|| StringUtils.isNotBlank(delivery.getShippingAlternativeCity())
				|| StringUtils.isNotBlank(delivery.getShippingAlternativePhone())
				|| StringUtils.isNotBlank(delivery.getShippingAlternativeRecipient()) ){
				return true;
			}
		}
		return false;
	}
	
	public List<SelectItem> getHours() {
		List<SelectItem> hours = new LinkedList<SelectItem>();
		for(int i=0; i<24; i++){
			SelectItem item = new SelectItem(String.format("%02d", i));
			hours.add(item);
		}
		return hours;
	}
	
	public List<SelectItem> getMinutes() {
		List<SelectItem> minutes = new LinkedList<SelectItem>();
		for(int i=0; i<60; i++){
			SelectItem item = new SelectItem(String.format("%02d", i));
			minutes.add(item);
		}
		return minutes;
	}

	@Override
	public List<SelectItem> getSeriesCodes() throws ManagerBeanException {
		ConfigCollectionsController ccc = (ConfigCollectionsController) AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		return ccc.getDeliverySeriesIds();
	}
	
}