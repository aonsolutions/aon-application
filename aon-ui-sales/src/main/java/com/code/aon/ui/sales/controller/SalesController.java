package com.code.aon.ui.sales.controller;

import static com.code.aon.ui.common.ICommonMessages.ITEM_SERIALIZABLE_REQUIRED_ERROR;
import static com.code.aon.ui.common.ICommonMessages.SALES_RETURNED_IN_MSG;
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
import com.code.aon.common.ProgressionState;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.util.SeriesUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistrySeller;
import com.code.aon.registry.enumeration.RegistrySellerStatus;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.bridge.DeliveryManager;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.seller.Seller;
import com.code.aon.ui.common.LongProcessThread;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.config.BankAccountHelper;
import com.code.aon.ui.config.controller.ConfigCollectionsController;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.HeaderObjectController;
import com.code.aon.ui.customer.util.CustomerValidationManager;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.sales.importer.edi.EdiSalesImporterHandler;
import com.code.aon.ui.sales.importer.edi.FtpSalesDownloadHandler;
import com.code.aon.ui.sales.udapa.SalesIngenetHandler;
import com.code.aon.ui.sales.util.PurchaseGeneratorManager;
import com.code.aon.ui.sales.util.SalesEmailUtil;
import com.code.aon.ui.sales.util.SalesUtils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.DeliveryController;
import com.code.aon.ui.warehouse.controller.IWarehouseConstants;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.carrier.Carrier;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.server.AonDateUtils;

public class SalesController extends HeaderObjectController implements ISalesConstants, IAuditableController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalesController.class);

	private List<SelectItem> addresses;
	private List<SelectItem> projects;
	private Boolean defaultPayMethod;
	private IPriceStrategy priceStrategy;
	private RegistryValidationManager vm;
	private PurchaseGeneratorManager purchaseGenerator;
	private boolean showPurchaseWindow;
	private boolean showDeliveryWindow;
	private boolean showElaborationWindow;
	private String deliverySeries;
	private int deliveryNumber;
	private Date deliveryDate;
	private Warehouse deliveryWarehouse;
	private boolean showInvoiceWindow;
	private String invoiceSeries;
	private int invoiceNumber;
	private Date invoiceDate;
	private String selectedTab;
	private boolean showAuditInfoWindow;
	private Double listTotal;
	private SalesEmailUtil emailUtil;
	private boolean shippingAlternativeAddress;
	private boolean showShipmentWindow;
	private ProgressionState progressionState;
	private Integer invoiceId;
	private BankAccountHelper accountHelper;
	private Date savedDeliveryDate;
	private Carrier savedCarrier;
	private boolean showPurchaseReferenceWindow;

	private EdiSalesImporterHandler ediImporter;
	@Deprecated
	private com.code.aon.ui.sales.udapa.EdiSalesImporterHandler udapaImporter;
	
	private FtpSalesDownloadHandler ftpEdiDownloader;
	private SalesIngenetHandler ingenetHandler;
	
	private SalesElaborationProcess elaborationProcess;
	
    public SalesController() {
    	this.emailUtil = new SalesEmailUtil();
    	this.accountHelper = new BankAccountHelper(this);
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

	public boolean isShowPurchaseWindow() {
		return showPurchaseWindow;
	}

	public void setShowPurchaseWindow(boolean showPurchaseWindow) {
		this.showPurchaseWindow = showPurchaseWindow;
	}
	
	public boolean isShowElaborationWindow() {
		return showElaborationWindow;
	}
	
	public void setShowElaborationWindow(boolean value) {
		this.showElaborationWindow = value;
	}

	public boolean isShowDeliveryWindow() {
		return showDeliveryWindow;
	}

	public void setShowDeliveryWindow(boolean value) {
		this.showDeliveryWindow = value;
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
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
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
		
	public ProgressionState getProgressionState() {
		return progressionState;
	}

	public void setProgressionState(ProgressionState progressionState) {
		this.progressionState = progressionState;
	}
	
	public Integer getInvoiceId() {
		return invoiceId;
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
	
	public boolean isShowPurchaseReferenceWindow() {
		return showPurchaseReferenceWindow;
	}

	public void setShowPurchaseReferenceWindow(boolean showPurchaseReferenceWindow) {
		this.showPurchaseReferenceWindow = showPurchaseReferenceWindow;
	}

	public EdiSalesImporterHandler getEdiImporter() {
		if(ediImporter==null){
			ediImporter = new EdiSalesImporterHandler(this);
		}
		return ediImporter;
	}

	@Deprecated
	public com.code.aon.ui.sales.udapa.EdiSalesImporterHandler getUdapaImporter() {
		if(udapaImporter==null){
			udapaImporter = new com.code.aon.ui.sales.udapa.EdiSalesImporterHandler(this);
		}
		return udapaImporter;
	}
	
	public FtpSalesDownloadHandler getFtpEdiDownloader() {
		if(ftpEdiDownloader==null){
			ftpEdiDownloader = new FtpSalesDownloadHandler(this);
		}
		return ftpEdiDownloader;
	}
	
	public SalesIngenetHandler getIngenetHandler() {
		if(ingenetHandler==null){
			ingenetHandler = new SalesIngenetHandler(this);
		}
		return ingenetHandler;
	}
	
	public void setIngenetHandler(SalesIngenetHandler ingenetHandler) {
		this.ingenetHandler = ingenetHandler;
	}
	
	public SalesElaborationProcess getElaborationProcess() {
		if(elaborationProcess==null){
			elaborationProcess = new SalesElaborationProcess(this);
		}
		return elaborationProcess;
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

	public boolean isManufacturable() throws ManagerBeanException {
		Sales sales = (Sales) this.getTo();
		try {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
			criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_ITEM_PRODUCT_MANUFACTURED), Boolean.TRUE);
			return salesDetailBean.getCount(criteria) > 0;
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage());
		}
	}

	public boolean isManufacturePending() throws ManagerBeanException {
		Sales sales = (Sales) this.getTo();
		try {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
			criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_ITEM_PRODUCT_MANUFACTURED), Boolean.TRUE);
			long pending = salesDetailBean.getList(criteria).stream()
				.map(to -> (SalesDetail)to)
				.filter(detail -> (detail.getQuantity() > detail.getDelivered()))
				.count();
			return pending > 0;
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage());
		}
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

	public void customerData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			isBlocked(customer);
			((Sales)this.getTo()).setCustomer(customer);
			((Sales)this.getTo()).setShippingAddress(null);
			((Sales)this.getTo()).setScope(customer.getScope());
			loadAddresses(customer.getId());
			loadProjects(customer.getId());
			loadCommercial(customer.getId());
			loadDefaultPayMethod(customer.getRegistry(), true);
		} else {
			setAddresses(null);
			setProjects(null);
		}
	}
	
	public boolean getExistPurchaseReference() {
		SalesUtils utils = new SalesUtils();
		Sales sales = (Sales)this.getTo();
		return StringUtils.isNotBlank(sales.getPurchaseReference()) && utils.getSalesRecord(sales.getPurchaseReference()).size()>(this.isNevv()?0:1);
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
			criteria.addEqualExpression(projectBean.getFieldName(IEntityAlias.PROJECT_ACTIVE), Boolean.TRUE);
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

	public void loadCommercial(Integer id) throws ManagerBeanException {
		if (id != null) {
			Sales sales = (Sales)this.getTo();
			IManagerBean registrySellerBean = BeanManager.getManagerBean(RegistrySeller.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_REGISTRY_ID), id);
			criteria.addEqualExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_STATUS), RegistrySellerStatus.ACTIVE);
			criteria.addLessThanOrEqualExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_START_DATE), sales.getIssueDate());
			Expression endDateExpr = ExpressionUtilities.getGreaterThanOrEqualExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_END_DATE), sales.getIssueDate());
			Expression endNullExpr = ExpressionUtilities.getNullExpression(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_END_DATE));
			criteria.addExpression(ExpressionUtilities.getOrExpression(endDateExpr, endNullExpr));
			criteria.addOrder(registrySellerBean.getFieldName(IEntityAlias.REGISTRY_SELLER_START_DATE));
			Iterator<ITransferObject> iter = registrySellerBean.getList(criteria).iterator();
			if (iter.hasNext()) {
				sales.setSeller(((RegistrySeller)iter.next()).getSeller());
			} else {
				sales.setSeller(new Seller());
				sales.getSeller().setRegistry(new Registry());
			}
		}
	}

	public void loadDefaultPayMethod(Registry registry, boolean forceReset) throws ManagerBeanException {
		if (registry != null && registry.getId() != null) {
			if (forceReset) {
				resetSalesPayMethod();
				setDefaultPayMethod(registry.getPayMethod() != null);
			} else {
				Sales sales = (Sales)getTo();
				setDefaultPayMethod(sales.getPayMethod() == null || sales.getPayMethod().getId() == null);
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
		to.setBankAccount(new BankAccount());
		to.setBankAlias(null);
		to.setBic(null);
	}

	public void linkDeliveryDate(Sales sales) throws ManagerBeanException {
		Date deliveryDate = sales.getDeliveryDate();
		if ((deliveryDate != null || getSavedDeliveryDate() != null) && !AonDateUtils.isSameDay(deliveryDate, getSavedDeliveryDate())) {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
			if (sales.getDeliveryDate() == null) {
				criteria.addNotNullExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_DELIVERY_DATE));
			} else {
				if (getSavedDeliveryDate() == null) {
					criteria.addNullExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_DELIVERY_DATE));
				} else {
					criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_DELIVERY_DATE), getSavedDeliveryDate());
				}
			}
			for (ITransferObject ito : salesDetailBean.getList(criteria)) {
				SalesDetail salesDetail = (SalesDetail)ito;
				salesDetail.setDeliveryDate(sales.getDeliveryDate());
				salesDetailBean.update(salesDetail);
			}
		}
		setSavedDeliveryDate(sales.getDeliveryDate());
	}

	public void linkCarrier(Sales sales) throws ManagerBeanException {
		Carrier carrier = (sales.getCarrier() != null && sales.getCarrier().getId() != null) ? sales.getCarrier() : null;
		setSavedCarrier((getSavedCarrier() != null && getSavedCarrier().getId() != null) ? getSavedCarrier() : null);
		if ((carrier == null && getSavedCarrier() != null) || (carrier != null && !carrier.equals(getSavedCarrier()))) {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
			if (carrier == null) {
				criteria.addNotNullExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_CARRIER));
			} else {
				if (getSavedCarrier() == null) {
					criteria.addNullExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_CARRIER));
				} else {
					criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_CARRIER_ID), getSavedCarrier().getId());
				}
			}
			for (ITransferObject ito : salesDetailBean.getList(criteria)) {
				SalesDetail salesDetail = (SalesDetail)ito;
				salesDetail.setCarrier(carrier);
				salesDetailBean.update(salesDetail);
			}
		}
		setSavedCarrier(sales.getCarrier());
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
		if ( getModel().isRowAvailable() ) {
			Sales sales = (Sales)this.getModel().getRowData();
			return getSalesTotalPrice(sales);			
		}
		return 0.0;
	}
	
	public double getSalesTotalPrice(Sales sales) throws ManagerBeanException {
		return getPriceStrategy().getTotalPrice(sales, sales.getCustomer());
	}
	
	public double getTotalDetailQuantity(){
		try {
			IManagerBean detailBean = BeanManager.getManagerBean(SalesDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), ((Sales)this.getTo()).getId());
			Projection projection = Projection.sum(detailBean.getFieldName(IEntityAlias.SALES_DETAIL_QUANTITY));
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
				listTotal += getSalesTotalPrice((Sales)ito);
			}
		} catch (ManagerBeanException e) {
			String message = "Imposible obtener el Total";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}		
		setListTotal(listTotal);
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

	protected DeliveryController getDeliveryController() {
		return (DeliveryController) AonUtil.getRegisteredBean(IWarehouseConstants.DELIVERY_CONTROLLER_NAME);
	}
	
	public void onDeliveryShow(ActionEvent event) throws ManagerBeanException {
		Sales to = (Sales) this.getTo();
		validate(to);
		getDeliveryController().initSeries(false);
		setDeliverySeries(SeriesUtil.ensureDeliverySeries(to.getSeries()));
		setDeliveryNumber(0);
		setDeliveryDate(new Date());
		setDeliveryWarehouse(obtainDeliveryWarehouse(to.getWorkPlace()));
	}
	
	private void validate(Sales sales) {
		try {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), sales.getId());
			criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_ITEM_PRODUCT_SERIALIZABLE), Boolean.TRUE);
			Expression serialNullExp = ExpressionUtilities.getNullExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_ITEM_SERIAL_NUMBER));
			Expression serialEmptyExp = ExpressionUtilities.getEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_ITEM_SERIAL_NUMBER), "");
			criteria.addExpression(ExpressionUtilities.getOrExpression(serialNullExp, serialEmptyExp));
			if (salesDetailBean.getCount(criteria) > 0) {
				String message = AonUtil.addErrorMessageFromBundle(ITEM_SERIALIZABLE_REQUIRED_ERROR);
				throw new AbortProcessingException(message);
			}
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage());
		}
	}

	public void onDeliverySeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		if ( getDeliveryController().isNumberEditable() ) {			
			updateDeliveryNumber((String)event.getNewValue());	
		}
	}

	protected int obtainMaxDeliveryNumber(String seriesId) {
		return getDeliveryController().obtainMaxNumber(seriesId);
	}
		
	private void updateDeliveryNumber(String seriesId) {
		setDeliveryNumber(obtainMaxDeliveryNumber(seriesId));
	}	
	
	public void onDeliveryNumberEditable(ActionEvent event) throws ManagerBeanException {
		updateDeliveryNumber(getDeliverySeries());		
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
		Sales to = (Sales)this.getTo();
		try {
			DeliveryManager deliveryManager = new DeliveryManager();
			Delivery delivery = deliveryManager.salesDelivery(to, getDeliverySeries(), getDeliveryNumber(), getDeliveryDate(), getDeliveryWarehouse());

			BasicController deliveryController = (BasicController)AonUtil.getRegisteredBean(DELIVERY_CONTROLLER_NAME);
			deliveryController.onLoad(event, delivery.getId(), SALES_FORM_NAME, SALES_CONTROLLER_NAME + ".refresh");		
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	private SaleInvoiceController getSaleInvoiceController() {
		return (SaleInvoiceController) AonUtil.getRegisteredBean(IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME);
	}		
	
	public void onInvoiceShow(ActionEvent event) throws ManagerBeanException {
		Sales to = (Sales)this.getTo();
		validate(to);
		SaleInvoiceController controller = getSaleInvoiceController();
		controller.onCancel(event);
		controller.initSeries(false);
		setInvoiceSeries(SeriesUtil.ensureInvoiceSeries(to.getSeries()));
		setInvoiceNumber(0);
		setInvoiceDate(new Date());
		setProgressionState(new ProgressionState());
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
			loadInvoice(event, invoice.getId());
		}		
	}
	
	private void loadInvoice(ActionEvent event, Integer id ) throws ManagerBeanException {
		BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(SALE_INVOICE_CONTROLLER_NAME);
		invoiceController.onLoad(event, id, SALES_FORM_NAME, SALES_CONTROLLER_NAME + ".refresh");		
	}
	
	public void loadElaborationMap() {
		SalesDetailController detailController = (SalesDetailController) AonUtil
				.getRegisteredBean(SALES_DETAIL_CONTROLLER_NAME);
		detailController.loadElaborationMap();
	}
	
	public void onElaborationShow(ActionEvent event) throws ManagerBeanException {
		getElaborationProcess().init();
	}
	
	public void onPurchaseGenerationShow(ActionEvent event) throws ManagerBeanException {
		setPurchaseGenerator(new PurchaseGeneratorManager((Sales) this.getTo()));
	}
	
	public boolean isShippingDataDefined() {
		Sales sales = (Sales) this.getTo();
		if(sales!=null){
			if( StringUtils.isNotBlank(sales.getShippingContact())
					|| sales.getShippingPeriod()!=null
					|| (sales.getCarrier()!=null && sales.getCarrier().getId()!=null)
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
	
	private Sales returnSourceSales;
	
	public void onReturnRequest(ActionEvent event) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Finance.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);
			HibernateUtil.beginTransaction(sessionName);
			
			returnSourceSales = (Sales) this.getTo();
//			String comments = AonUtil.getMessage(PURCHASE_RETURN_OVER_MSG, returnSourceSales.getReferenceCode());
			String comments = AonUtil.getMessage("sales_return_over", returnSourceSales.getReferenceCode());
			comments += StringUtils.isBlank(returnSourceSales.getComments())?"":returnSourceSales.getComments();
			SalesUtils utils = new SalesUtils();
			Sales sales = utils.createSales(returnSourceSales.getSeries(), returnSourceSales.getSeller(), 
					returnSourceSales.getCustomer(), returnSourceSales.getProject(), returnSourceSales.getWorkPlace(), 
					DocumentType.ITEM_RETURN, returnSourceSales.getIssueDate(), returnSourceSales.getDiscountExpression(), 
					returnSourceSales.getNumberOfPayments(), returnSourceSales.getDaysToFirstPayment(), returnSourceSales.getDaysBetweenPayments(), 
					returnSourceSales.getPaymentDays(), returnSourceSales.getPayMethod(), returnSourceSales.getBankAccount(), 
					returnSourceSales.getBankAlias(), returnSourceSales.getBic(), comments, returnSourceSales.getRemarks(), 
					returnSourceSales.getCarrier(), returnSourceSales.getPurchaseReference(),  
					returnSourceSales.getShippingAlternativeAddress(), returnSourceSales.getShippingAlternativeAddress2(), 
					returnSourceSales.getShippingAlternativeZip(), returnSourceSales.getShippingAlternativeCity(), returnSourceSales.getShippingAlternativePhone(), 
					returnSourceSales.getShippingAlternativeRecipient(), returnSourceSales.getShippingContact(), returnSourceSales.getShippingPeriod());
					
			markSourceSalesAsReturned(sales.getReferenceCode());
			SalesDetailController detailController = (SalesDetailController) AonUtil.getRegisteredBean(SALES_DETAIL_CONTROLLER_NAME);
			for(ITransferObject to: detailController.getWrappedList()){
				SalesDetail detail = (SalesDetail) to;
				utils.createSalesDetail(sales, detail.getItem(), detail.getLine(), detail.getDescription(), 
						detail.getOfferDetail(), SalesDetailStatus.PENDING, detail.getDiscountExpression(), 
						(-1)*detail.getQuantity(), detail.getPrice(), detail.getTaxes(), 0, 0);
			}
			
			HibernateUtil.commitTransaction(sessionName);
			this.onLoad(event, sales.getId(), SALES_FORM_NAME, SALES_CONTROLLER_NAME+".restoreSourceSales");
		} catch (Exception e) {
			String msg = "Error al crear la solicitud de devolucion. ";
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
	
	private void markSourceSalesAsReturned(String referenceCode) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Sales.class);
		String comments = AonUtil.getMessage(SALES_RETURNED_IN_MSG, referenceCode);
		comments += StringUtils.isBlank(returnSourceSales.getComments())?"":returnSourceSales.getComments();
		returnSourceSales.setComments(comments);
		bean.restoreNullSubPOJOs(returnSourceSales);
		bean.update(returnSourceSales);
	}
	
	public void restoreSourceSales( ActionEvent event ) throws ManagerBeanException {
		this.select(event, returnSourceSales);
	}

	@Override
	public List<SelectItem> getSeriesCodes() throws ManagerBeanException {
		ConfigCollectionsController ccc = (ConfigCollectionsController) AonUtil.getRegisteredBean(ConfigConstants.CONFIG_COLLECTIONS);
		return ccc.getSalesSeriesIds();
	}
	
	public String invoiceAction() {
		return (getInvoiceId() != null) ? IFinanceConstants.SALE_INVOICE_FORM_NAME : null;
	}

	public void onClosePanel(ActionEvent event) {
		if ( getProgressionState().isFinish() ) {
			if ( getInvoiceId() != null ) {
				try {
					loadInvoice(event, getInvoiceId());
				} catch (ManagerBeanException e) {
					AonUtil.addErrorMessage(e.getMessage());
					throw new AbortProcessingException(e.getMessage(),e);
				}				
			}
		}
		setShowInvoiceWindow(false);			
		getProgressionState().finish();
	}
	
	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public void onInvoice(ActionEvent event) {
		getProgressionState().start();
		SalesInvoiceProcess sip = new SalesInvoiceProcess(this);
		LongProcessThread thread = new LongProcessThread(sip); 
		thread.start();		
	}	
	
}
