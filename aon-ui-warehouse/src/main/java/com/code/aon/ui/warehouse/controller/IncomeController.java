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
import com.code.aon.company.WorkPlace;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.IncomeInvoicingManager;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.project.Project;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.bridge.IncomeManager;
import com.code.aon.purchase.bridge.PurchaseTransferManager;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryItemStatus;
import com.code.aon.registry.enumeration.RegistryMode;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.controller.IAuditableController;
import com.code.aon.ui.config.BankAccountHelper;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.product.controller.ItemTagPrintController;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.supplier.util.SupplierValidationManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.util.WarehouseEmailUtil;
import com.code.aon.ui.webmail.controller.MessageController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.enumeration.IncomeStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class IncomeController extends BasicController implements IWarehouseConstants, IAuditableController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(IncomeController.class);
	
	private List<SelectItem> addresses;
	private Warehouse warehouse;
	private Boolean defaultPayMethod;
	private IPriceStrategy priceStrategy;
	private RegistryValidationManager vm;
	private PurchaseTransferManager purchaseTransferManager;
	private boolean showPurchaseTransferWindow;
	private boolean showProjectWindow;
	private boolean showDetailProjectWindow;
	private boolean showInvoiceWindow;
	private String invoiceRefCode;
	private Date invoiceDate;
	private boolean showConfirmWindow;
	private boolean showPurchaseFilterWindow;
	private boolean showAuditInfoWindow;	
	private boolean showWarehouseChangeWindow;
	private WarehouseEmailUtil emailUtil;
	private Warehouse newWarehouse;
	private Double listTotal;
	private BankAccountHelper accountHelper;
	
    public IncomeController() {
    	this.emailUtil = new WarehouseEmailUtil();
    	this.accountHelper = new BankAccountHelper(this);
	}

	public List<SelectItem> getAddresses() {
		return addresses;
	}
	
	public void setAddresses(List<SelectItem> addresses) {
		this.addresses = addresses;
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
			resetIncomePayMethod();
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
			vm = new SupplierValidationManager(); 
		}
		return vm;
	}

	public PurchaseTransferManager getPurchaseTransferManager() {
		if (purchaseTransferManager == null) {
			purchaseTransferManager = new PurchaseTransferManager(); 
		}
		return purchaseTransferManager;
	}

	public void setPurchaseTransferManager(PurchaseTransferManager purchaseTransferManager) {
		this.purchaseTransferManager = purchaseTransferManager;
	}

	public boolean isShowPurchaseTransferWindow() {
		return showPurchaseTransferWindow;
	}

	public void setShowPurchaseTransferWindow(boolean value) {
		this.showPurchaseTransferWindow = value;
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

	public boolean isShowConfirmWindow() {
		return showConfirmWindow;
	}
	public void setShowConfirmWindow(boolean showConfirmWindow) {
		this.showConfirmWindow = showConfirmWindow;
	}

    public boolean isShowPurchaseFilterWindow() {
		return showPurchaseFilterWindow;
	}

    public void setShowPurchaseFilterWindow(boolean showPurchaseFilterWindow) {
		this.showPurchaseFilterWindow = showPurchaseFilterWindow;
	}
	
	public boolean isShowAuditInfoWindow() {
		return showAuditInfoWindow;
	}

	public void setShowAuditInfoWindow(boolean showAuditInfoWindow) {
		this.showAuditInfoWindow = showAuditInfoWindow;
	}
	
	public boolean isShowWarehouseChangeWindow() {
		return showWarehouseChangeWindow;
	}

	public void setShowWarehouseChangeWindow(boolean showWarehouseChangeWindow) {
		this.showWarehouseChangeWindow = showWarehouseChangeWindow;
	}

	public Warehouse getNewWarehouse() {
		return newWarehouse;
	}

	public void setNewWarehouse(Warehouse newWarehouse) {
		this.newWarehouse = newWarehouse;
	}

	public Double getListTotal() {
		return listTotal;
	}

	public void setListTotal(Double listTotal) {
		this.listTotal = listTotal;
	}

	public boolean isPending(){
		Income income = (Income)this.getTo();
		return income.getStatus() == IncomeStatus.PENDING;
	}

	public boolean isInvoiced(){
		Income income = (Income)this.getTo();
		return income.getStatus() == IncomeStatus.INVOICED;
	}

	public Invoice getInvoice() throws ManagerBeanException {
		Income income = (Income)this.getTo();
		if (income != null && income.getId() != null) {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
			Iterator<?> iterator = incomeDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				IncomeDetail incomeDetail = (IncomeDetail)iterator.next();

				IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.INCOME);
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_SOURCE_ID), incomeDetail.getId());
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
			((Income)this.getTo()).setSupplier(supplier);
			((Income)this.getTo()).setScope(supplier.getScope());
			loadAddresses(supplier.getId());
			loadDefaultPayMethod(supplier.getRegistry(), true);
		} else {
			setAddresses(null);
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
	
	public void removeIncomeProject(ActionEvent event) throws ManagerBeanException {
		Income to = (Income)this.getTo();
		to.setProject(null);
		getManagerBean().restoreNullSubPOJOs(to);
		getManagerBean().update(to);
		getManagerBean().initializePOJO(to);

		removeIncomeDetailProject(); 
	}
	
	public void removeIncomeDetailProject() throws ManagerBeanException {
		Income income = (Income)this.getManagerBean().get(((Income)this.getTo()).getId());
		Project project = income.getProject();
		if(project!=null && project.getId()!=null ){
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_PROJECT_ID), project.getId());
			for (ITransferObject ito : incomeDetailBean.getList(criteria)) {
				IncomeDetail incomeDetail = (IncomeDetail)ito;
				incomeDetail.setProject(null);
				incomeDetailBean.update(incomeDetail);
			}
		}
		IController incomeDetailController = FormUtil.getController(INCOME_DETAIL_CONTROLLER_NAME);
		incomeDetailController.onSearch(null);
	}

	public void loadDefaultPayMethod(Registry registry, boolean forceReset) throws ManagerBeanException {
		if (registry != null && registry.getId() != null) {
			if (forceReset) {
				resetIncomePayMethod();
				setDefaultPayMethod(registry.getPayMethod() != null);
			} else {
				Income income = (Income)getTo();
				setDefaultPayMethod(income.getPayMethod() == null || income.getPayMethod().getId() == null);
			}
		}
	}

	public void resetIncomePayMethod() {
		Income to = (Income)this.getTo();
		to.setPayMethod(new PayMethod());
		to.setNumberOfPayments(1);
		to.setDaysToFirstPayment(0);
		to.setDaysBetweenPayments(0);
		to.setPaymentDays("");
		to.setBankAccount(new BankAccount());
		to.setBankAlias(null);
		to.setBic(null);
	}

	public BankAccountHelper getAccountHelper() {
		return accountHelper;
	}

	public void onWorkPlaceChanged(ValueChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Income income = (Income)this.getTo();
			WorkPlace workPlace = (WorkPlace)event.getNewValue();
			income.setWorkPlace(workPlace);
			income.setScope(workPlace.getScope());
			updateWarehouse();
		}
	}

	public void updateWarehouse() throws ManagerBeanException {
		setWarehouse(null);
		Income income = (Income)this.getTo();
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		Iterator<?> iterator = incomeDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			IncomeDetail detail = (IncomeDetail) iterator.next();
			setWarehouse( detail.getWarehouse() );
		} else {
			List<Warehouse> warehouses = WarehouseCollectionsController.getWarehouseList(income.getWorkPlace());
			if ( warehouses.size() == 1 ) {
		        setWarehouse(warehouses.get(0));	
			}
		}
	}

	public List<SelectItem> getWarehouses() throws ManagerBeanException {
		Income income = (Income) getTo();
		if(income.getWorkPlace() != null && income.getWorkPlace().getId() != null) 
			return WarehouseCollectionsController.getWarehouses(income.getWorkPlace(), true);
		else return WarehouseCollectionsController.getWarehouses(income.getWorkPlace());
	}

	public double getTaxableBase(){
		return getPriceStrategy().getTaxableBase((ICalculableContainer)getTo());
	}

	public double getTotalPrice(){
		return getPriceStrategy().getTotalPrice((ICalculableContainer)getTo(), ((Income)getTo()).getSupplier());
	}

	public double getIncomeTotalPrice() throws ManagerBeanException {
		Income income = (Income)this.getModel().getRowData();
		return getIncomeTotalPrice(income);
	}

	public double getIncomeTotalPrice(Income income) throws ManagerBeanException {
		return getPriceStrategy().getTotalPrice(income, income.getSupplier());
	}
	
	public double getTotalDetailQuantity(){
		try {
			IManagerBean detailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(detailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), ((Income)this.getTo()).getId());
			Projection projection = Projection.sum(detailBean.getFieldName(IEntityAlias.INCOME_DETAIL_QUANTITY));
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
				listTotal += getIncomeTotalPrice((Income)ito);
			}
		} catch (ManagerBeanException e) {
			String message = "Imposible obtener el Total";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
		}		
		setListTotal(listTotal);
	}

	public void onPurchaseTransferShow(ActionEvent event) throws ManagerBeanException {
		getPurchaseTransferManager().setFilterParams(null);
		loadPurchaseTransferModel();
	}
	
	private Criteria getPurchaseTransferCriteria( IManagerBean bean, Income income, boolean addAddress ) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PURCHASE_SUPPLIER_ID), income.getSupplier().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PURCHASE_STATUS), PurchaseStatus.PENDING);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PURCHASE_SECURITY_LEVEL), income.getSecurityLevel());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PURCHASE_WORK_PLACE_ID), income.getWorkPlace().getId());

		Expression exp1 = ExpressionUtilities.getNullExpression(bean.getFieldName(IEntityAlias.PURCHASE_WAREHOUSE));
		if( (getWarehouse()!=null) && (getWarehouse().getId()!=null) ){
			String ljAlias = StringUtils.replace(bean.getFieldName(IEntityAlias.PURCHASE_WAREHOUSE_ID), ".id", "<id");
			Expression exp2 = ExpressionUtilities.getEqualExpression(ljAlias, getWarehouse().getId());
			criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
		} else {
			criteria.addExpression(exp1);
		}
		
		if ( addAddress ) {
			if (income.getRegistryAddress() != null && income.getRegistryAddress().getId() != null) {
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PURCHASE_REGISTRY_ADDRESS_ID), income.getRegistryAddress().getId());
			}			
		} else {
			criteria.addNullExpression(bean.getFieldName(IEntityAlias.PURCHASE_REGISTRY_ADDRESS));			
		}
		addFilterCriteria(criteria, bean);
		criteria.addOrder(bean.getFieldName(IEntityAlias.PURCHASE_ISSUE_DATE));
		criteria.addOrder(bean.getFieldName(IEntityAlias.PURCHASE_SERIES));
		criteria.addOrder(bean.getFieldName(IEntityAlias.PURCHASE_NUMBER));
		return criteria;
	}
	
	private void loadPurchaseTransferModel() throws ManagerBeanException {
		Income to = (Income)this.getTo();

		IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
		Criteria criteria = getPurchaseTransferCriteria(purchaseBean, to, true);
		getPurchaseTransferManager().setPurchaseList(purchaseBean.getList(criteria));

		// si el albaran no tiene address definido, se tienen tambien en cuenta los pedidos cuyo address = null
		if (to.getRegistryAddress() != null && to.getRegistryAddress().getId() != null) {
			criteria = getPurchaseTransferCriteria(purchaseBean, to, false);
			getPurchaseTransferManager().getPurchaseList().addAll(purchaseBean.getList(criteria));
		}
		getPurchaseTransferManager().setPurchaseModel(null);
	}
	
	private void addFilterCriteria(Criteria criteria, IManagerBean purchaseBean) throws ManagerBeanException {
		if(getPurchaseTransferManager().getFilterParams().getFromDate()!=null){
			criteria.addGreaterThanOrEqualExpression(purchaseBean.getFieldName(IEntityAlias.PURCHASE_ISSUE_DATE), getPurchaseTransferManager().getFilterParams().getFromDate());
		}
		if(getPurchaseTransferManager().getFilterParams().getToDate()!=null){
			criteria.addLessThanOrEqualExpression(purchaseBean.getFieldName(IEntityAlias.PURCHASE_ISSUE_DATE), getPurchaseTransferManager().getFilterParams().getToDate());
		}
		if(StringUtils.isNotBlank(getPurchaseTransferManager().getFilterParams().getSeries())){
			criteria.addExpression(ExpressionUtilities.getLikeExpression(
					purchaseBean.getFieldName(IEntityAlias.PURCHASE_SERIES), "%" + getPurchaseTransferManager().getFilterParams().getSeries() + "%"));
		}
		if(getPurchaseTransferManager().getFilterParams().getNumberFrom()!=null){
			criteria.addGreaterThanOrEqualExpression(purchaseBean.getFieldName(IEntityAlias.PURCHASE_NUMBER), getPurchaseTransferManager().getFilterParams().getNumberFrom());
		}
		if(getPurchaseTransferManager().getFilterParams().getNumberTo()!=null){
			criteria.addLessThanOrEqualExpression(purchaseBean.getFieldName(IEntityAlias.PURCHASE_NUMBER), getPurchaseTransferManager().getFilterParams().getNumberTo());
		}
	}
	public void onFilterTransferModel(ActionEvent event) throws ManagerBeanException {
		getPurchaseTransferManager().clearCheckedPurchase();
		getPurchaseTransferManager().setDetailList(null);
		getPurchaseTransferManager().setDetailModel(null);
		loadPurchaseTransferModel();
	}

	public boolean isTransferedGreatherThanPending() {
		Iterator<PurchaseDetail> iterator = getPurchaseTransferManager().getCheckedDetails().iterator();
		boolean transferedGreatherThanPending = false;
		while (iterator.hasNext() && !transferedGreatherThanPending) {
			transferedGreatherThanPending = getPurchaseTransferManager().isTransferedGreatherThanPending((PurchaseDetail)iterator.next());
		}
		return transferedGreatherThanPending;
	}

	public boolean isExistsDetailToCancel() {
		Iterator<PurchaseDetail> iterator = getPurchaseTransferManager().getCheckedDetails().iterator();
		while (iterator.hasNext()) {
			if(((PurchaseDetail)iterator.next()).isForcePendingQuantityCancel()){
				return true;
			}
		}
		return false;
	}
	
	private boolean checkConfirmWindowShow(){
		return isTransferedGreatherThanPending() || isExistsDetailToCancel();
	}
	
	public void onPurchaseTransfer(ActionEvent event) throws ManagerBeanException {
		if (checkConfirmWindowShow()) {
			setShowConfirmWindow(true);
		} else {
			confirmPurchaseTrasfer(event);
		}
	}

	public void confirmPurchaseTrasfer(ActionEvent event) throws ManagerBeanException {
		try {
			IncomeManager incomeManager = new IncomeManager();
			incomeManager.transferPurchaseDetails((Income)this.getTo(), getPurchaseTransferManager().getCheckedDetails(), getWarehouse());

			refresh(null);
			IController detailController = FormUtil.getController(INCOME_DETAIL_CONTROLLER_NAME);
			detailController.onSearch(null);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		} finally {
			setShowPurchaseTransferWindow(false);
			setPurchaseTransferManager(null);
		}
	}

	public void onInvoiceShow(ActionEvent event) {
		setInvoiceRefCode(null);
		setInvoiceDate(new Date());
	}

	public void onInvoice(ActionEvent event) {
		Income to = (Income)this.getTo();
		try {
			IncomeInvoicingManager invoicingManager = new IncomeInvoicingManager();
			invoicingManager.invoice(to, getInvoiceRefCode(), getInvoiceDate());
			onLoadInvoice(event);
		} catch (ManagerBeanException ex) {
			AonUtil.addErrorMessage(ex.getMessage());
			throw new AbortProcessingException(ex.getMessage(), ex);
		}
	}

	public void onLoadInvoice(ActionEvent event) throws ManagerBeanException {
		Invoice invoice = getInvoice();
		if (invoice != null) {
			BasicController invoiceController = (BasicController)AonUtil.getRegisteredBean(PURCHASE_INVOICE_CONTROLLER_NAME);
			invoiceController.onLoad(event, invoice.getId(), INCOME_FORM_NAME, INCOME_CONTROLLER_NAME + ".refresh");
		}
	}

	public void onWarehouseChangeShow(ActionEvent event) {
		setNewWarehouse(getWarehouse());
	}

	public void onWarehouseChange(ActionEvent event) throws ManagerBeanException {
		Income income = (Income)this.getTo();
		if (getNewWarehouse() != null && getWarehouse().getId() != getNewWarehouse().getId()) {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
			for (ITransferObject ito : incomeDetailBean.getList(criteria)) {
				IncomeDetail incomeDetail = (IncomeDetail)ito;
				incomeDetail.setWarehouse(getNewWarehouse());
				incomeDetailBean.update(incomeDetail);
			}
		}
		setWarehouse(getNewWarehouse());
		IController incomeDetailController = FormUtil.getController(INCOME_DETAIL_CONTROLLER_NAME);
		incomeDetailController.onSearch(null);
	}

	public void onSupplierPricesChange(ActionEvent event) throws ManagerBeanException {
		Income income = (Income)this.getTo();
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		for (ITransferObject ito : incomeDetailBean.getList(criteria)) {
			IncomeDetail incomeDetail = (IncomeDetail)ito;
			if (updateSupplierPrices(income.getWorkPlace(), income.getSupplier(), incomeDetail)) {
				incomeDetailBean.update(incomeDetail);
			}
		}

		IController incomeDetailController = FormUtil.getController(INCOME_DETAIL_CONTROLLER_NAME);
		incomeDetailController.onSearch(null);
	}

	private boolean updateSupplierPrices(WorkPlace workPlace, Supplier supplier, IncomeDetail incomeDetail) throws ManagerBeanException {
		IManagerBean rItemBean = BeanManager.getManagerBean(RegistryItem.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID), incomeDetail.getItem().getId());
		criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_REGISTRY_ID), supplier.getId());
		criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_STATUS), RegistryItemStatus.ACTIVE);
		criteria.addEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
		Expression wpExpr = ExpressionUtilities.getEqualExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_WORK_PLACE_ID), workPlace.getId());
		Expression wpNullExpr = ExpressionUtilities.getNullExpression(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_WORK_PLACE));
		criteria.addExpression(ExpressionUtilities.getOrExpression(wpExpr, wpNullExpr));
		criteria.addOrder(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_WORK_PLACE), false);
		criteria.addOrder(rItemBean.getFieldName(IEntityAlias.REGISTRY_ITEM_PRIORITY));
		List<ITransferObject> rItemList = rItemBean.getList(criteria);
		if (!rItemList.isEmpty()) {
			RegistryItem rItem = (RegistryItem)rItemList.get(0);
			incomeDetail.setPrice(rItem.getPrice());
			incomeDetail.setDiscountExpression(rItem.getDiscountExpression());
			return true;
		}
		return false;
	}
	
	public void onItemTagPrintShow(ActionEvent event) throws ManagerBeanException {
		List<Integer> idList = new LinkedList<Integer>();
		IController detailController = FormUtil.getController(IWarehouseConstants.INCOME_DETAIL_CONTROLLER_NAME);
		List<ITransferObject> list = detailController.getManagerBean().getList(detailController.getCriteria());
		list.forEach(to -> {idList.add(((IncomeDetail)to).getItem().getId());});
		ItemTagPrintController itemTagController = (ItemTagPrintController) FormUtil.getController(IItemConstants.ITEM_TAG_PRINT_CONTROLLER_NAME);
		itemTagController.onItemTagPrintShow(event, idList);
	}

	public void onSendByEmail( ActionEvent event ) {
		MessageController controller = (MessageController) AonUtil.getRegisteredBean(BEAN_MESSAGE);
		controller.onPrepareEmailWindow(event);
		if ( controller.isShowNewMessageWindow() ) {			
			try {			
				controller.onNewMessage(event);
				emailUtil.initMessageController(controller, (Income) getTo(), "incomeForm");
			} catch ( Throwable e ) {
				LOGGER.error(e.getMessage(), e);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e.getMessage(), e);				
			}				
		}
	}	
}