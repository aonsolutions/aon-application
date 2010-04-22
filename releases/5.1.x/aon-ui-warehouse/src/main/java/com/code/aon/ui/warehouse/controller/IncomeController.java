package com.code.aon.ui.warehouse.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.IncomeInvoicingManager;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.purchase.bridge.IncomeManager;
import com.code.aon.purchase.bridge.PurchaseTransferManager;
import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.ui.supplier.util.SupplierValidationManager;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeStatus;

/**
 * Controller for Income.
 */
public class IncomeController extends BasicController {

	private final String INCOME_DETAIL_CONTROLLER = "incomeDetail";
	private final String PURCHASE_INVOICE_CONTROLLER = "purchaseInvoice";

	private List<SelectItem> addresses;
	private Warehouse warehouse;
	private Boolean defaultPayMethod;
	private IPriceStrategy priceStrategy;
	private RegistryValidationManager vm;
	private PurchaseTransferManager purchaseTransferManager;
	private boolean showPurchaseTransferWindow;
	private boolean showInvoiceWindow;
	private String invoiceRefCode;
	private Date invoiceDate;

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

	public boolean isPending(){
		Income income = (Income)this.getTo();
		if (income.getStatus() != null) {
			return income.getStatus().equals(IncomeStatus.PENDING);
		}
		return false;
	}

	public boolean isInvoiced(){
		Income income = (Income)this.getTo();
		if (income.getStatus() != null) {
			return income.getStatus().equals(IncomeStatus.INVOICED);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public String getInvoiceCode() throws ManagerBeanException {
		Income income = (Income)this.getTo();
		if (income != null && income.getId() != null) {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IWarehouseAlias.INCOME_DETAIL_INCOME_ID), income.getId());
			Iterator<?> iterator = incomeDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				IncomeDetail incomeDetail = (IncomeDetail)iterator.next();

				IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.INCOME);
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE_ID), incomeDetail.getId());
				Iterator iter = invoiceDetailBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
					return invoiceDetail.getInvoice().getReferenceCode();
				}
			}
		}
    	return null;
	}

	public void supplierData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Supplier supplier = (Supplier)event.getNewValue();
			isBlocked(supplier);
			((Income)this.getTo()).setSupplier(supplier);
			((Income)this.getTo()).setScope(supplier.getScope());
			loadAddresses(supplier.getId());
			loadDefaultPayMethod(supplier.getId(), false);
		} else {
			setAddresses(null);
		}
	}
	
	private boolean isBlocked(Supplier supplier) {
		return getRegistryValidationManager().isBlocked(supplier);
	}

	@SuppressWarnings("unchecked")
	public void loadAddresses(Integer id) throws ManagerBeanException {
		List<SelectItem> addresses = new LinkedList<SelectItem>();
		if (id != null) {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), id);
			Iterator iter = rAddressBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryAddress address = (RegistryAddress)iter.next();
				String addressLabel = address.getAddress() + " " + address.getAddress2() + " " + address.getAddress3();
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
	
	@SuppressWarnings("unchecked")
	public void loadDefaultPayMethod(Integer id, boolean forceDefault) throws ManagerBeanException {
		if (id != null) {
			if (((Income)this.getTo()).getPayMethod() != null && ((Income)this.getTo()).getPayMethod().getId() != null) {
				setDefaultPayMethod(false);
			} else {
				if (forceDefault) {
					setDefaultPayMethod(true);
				} else {
					IManagerBean rPayMethodBean = BeanManager.getManagerBean(RegistryPayMethod.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(rPayMethodBean.getFieldName(IRegistryAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), id);
					Iterator iter = rPayMethodBean.getList(criteria).iterator();
					setDefaultPayMethod(iter.hasNext());
				}
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
		to.setBank(new Bank());
		to.setBankAccount(new BankAccount());
	}

	public double getTaxableBase(){
		return getPriceStrategy().getTaxableBase((ICalculableContainer)getTo());
	}

	public double getTotalPrice(){
		return getPriceStrategy().getTotalPrice((ICalculableContainer)getTo(), ((Income)getTo()).getSupplier());
	}

	public double getIncomeTotalPrice() throws ManagerBeanException {
		Income income = (Income)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(income, income.getSupplier());
	}

	public void onPurchaseTransferShow(ActionEvent event) throws ManagerBeanException {
		Income to = (Income)this.getTo();

		IManagerBean purchaseBean = BeanManager.getManagerBean(Purchase.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(purchaseBean.getFieldName(IPurchaseAlias.PURCHASE_SUPPLIER_ID), to.getSupplier().getId());
		if (to.getRegistryAddress() != null && to.getRegistryAddress().getId() != null) {
			criteria.addEqualExpression(purchaseBean.getFieldName(IPurchaseAlias.PURCHASE_REGISTRY_ADDRESS_ID), to.getRegistryAddress().getId());
		}
		criteria.addEqualExpression(purchaseBean.getFieldName(IPurchaseAlias.PURCHASE_STATUS), PurchaseStatus.PENDING);
		criteria.addEqualExpression(purchaseBean.getFieldName(IPurchaseAlias.PURCHASE_SECURITY_LEVEL), to.getSecurityLevel());
		criteria.addEqualExpression(purchaseBean.getFieldName(IPurchaseAlias.PURCHASE_WORK_PLACE_ID), to.getWorkPlace().getId());
		criteria.addOrder(purchaseBean.getFieldName(IPurchaseAlias.PURCHASE_ISSUE_DATE));
		criteria.addOrder(purchaseBean.getFieldName(IPurchaseAlias.PURCHASE_SERIES));
		criteria.addOrder(purchaseBean.getFieldName(IPurchaseAlias.PURCHASE_NUMBER));

		getPurchaseTransferManager().setPurchaseList(purchaseBean.getList(criteria));
	}

	public void onPurchaseTransfer(ActionEvent event) throws ManagerBeanException {
		Iterator<PurchaseDetail> iterator = getPurchaseTransferManager().getCheckedDetails().iterator();
		while (iterator.hasNext()) {
			PurchaseDetail purchaseDetail = iterator.next();
			if (purchaseDetail.getTransfered() > 0) {
				IncomeManager incomeManager = new IncomeManager();
				incomeManager.transferIncomeDetail((Income)this.getTo(), purchaseDetail, getWarehouse());
			}
		}

		IController detailController = FormUtil.getController(INCOME_DETAIL_CONTROLLER);
		detailController.onSearch(null);
	}

	public void onInvoiceShow(ActionEvent event) throws ManagerBeanException {
		setInvoiceRefCode(null);
		setInvoiceDate(new Date());
	}

	public void onInvoice(ActionEvent event) throws ManagerBeanException {
		setShowInvoiceWindow(false);

		Income to = (Income)this.getTo();
		IncomeInvoicingManager invoicingManager = new IncomeInvoicingManager();
		Invoice invoice = invoicingManager.invoice(to, getInvoiceRefCode(), getInvoiceDate());

		IController invoiceController = FormUtil.getController(PURCHASE_INVOICE_CONTROLLER);
		invoiceController.clearCriteria();
		invoiceController.getCriteria().addEqualExpression(invoiceController.getFieldName(IFinanceAlias.INVOICE_ID), invoice.getId());
		invoiceController.onSearch(null);
		invoiceController.getModel().setRowIndex(0);
		invoiceController.onSelect(null);
	}

}