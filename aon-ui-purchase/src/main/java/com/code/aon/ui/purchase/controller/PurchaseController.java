package com.code.aon.ui.purchase.controller;

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
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.bridge.invoicing.IncomeInvoicingManager;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.bridge.IncomeManager;
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
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.IncomeDetailType;

/**
 * Controller used in the purchase maintenance.
 */
public class PurchaseController extends BasicController {

	private final String INCOME_CONTROLLER = "income";
	private final String PURCHASE_INVOICE_CONTROLLER = "purchaseInvoice";

	private List<SelectItem> addresses;
	private Boolean defaultPayMethod;
	private IPriceStrategy priceStrategy;
	private RegistryValidationManager vm;
	private boolean showIncomeWindow;
	private String incomeSeries;
	private int incomeNumber;
	private Date incomeDate;
	private Warehouse incomeWarehouse;
	private boolean showInvoiceWindow;
	private String invoiceRefCode;
	private Date invoiceDate;
	private Warehouse invoiceWarehouse;
	
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

	public Warehouse getInvoiceWarehouse() {
		return invoiceWarehouse;
	}

	public void setInvoiceWarehouse(Warehouse invoiceWarehouse) {
		this.invoiceWarehouse = invoiceWarehouse;
	}

	public boolean isPending(){
		Purchase purchase = (Purchase)this.getTo();
		if (purchase.getStatus() != null) {
			return purchase.getStatus().equals(PurchaseStatus.PENDING);
		}
		return false;
	}

	public boolean isBlocked(){
		Purchase purchase = (Purchase)this.getTo();
		if (purchase.getStatus() != null) {
			return purchase.getStatus().equals(PurchaseStatus.BLOCKED);
		}
		return false;
	}

	public boolean isClosed(){
		Purchase purchase = (Purchase)this.getTo();
		if (purchase.getStatus() != null) {
			return purchase.getStatus().equals(PurchaseStatus.CLOSED);
		}
		return false;
	}

	public void supplierData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Supplier supplier = (Supplier)event.getNewValue();
			isBlocked(supplier);
			((Purchase)this.getTo()).setSupplier(supplier);
			((Purchase)this.getTo()).setScope(supplier.getScope());
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
			if (((Purchase)this.getTo()).getPayMethod() != null && ((Purchase)this.getTo()).getPayMethod().getId() != null) {
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
		return getPriceStrategy().getTotalPrice(purchase, purchase.getSupplier());
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
		setIncomeSeries(null);
		setIncomeNumber(0);
		setIncomeDate(new Date());
		setIncomeWarehouse(null);
	}

	public void onIncome(ActionEvent event) throws ManagerBeanException {
		setShowIncomeWindow(false);

		Purchase to = (Purchase)this.getTo();
		IncomeManager incomeManager = new IncomeManager();
		Income income = incomeManager.purchaseIncome(to, getIncomeSeries(), getIncomeNumber(), getIncomeDate(), getIncomeWarehouse(), IncomeDetailType.MANUAL);

		IController incomeController = FormUtil.getController(INCOME_CONTROLLER);
		incomeController.clearCriteria();
		incomeController.getCriteria().addEqualExpression(incomeController.getFieldName(IWarehouseAlias.INCOME_ID), income.getId());
		incomeController.onSearch(null);
		incomeController.getModel().setRowIndex(0);
		incomeController.onSelect(null);
	}

	public void onInvoiceShow(ActionEvent event) throws ManagerBeanException {
		setInvoiceRefCode(null);
		setInvoiceDate(new Date());
		setInvoiceWarehouse(null);
	}

	public void onInvoice(ActionEvent event) throws ManagerBeanException {
		setShowInvoiceWindow(false);

		Purchase to = (Purchase)this.getTo();
		String incomeSeries = to.getSeries();
		int incomeNumber = obtainMaxIncomeNumber(incomeSeries);
		IncomeManager incomeManager = new IncomeManager();
		Income income = incomeManager.purchaseIncome(to, incomeSeries, incomeNumber, getInvoiceDate(), getInvoiceWarehouse(), IncomeDetailType.AUTOMATIC);
		IncomeInvoicingManager invoicingManager = new IncomeInvoicingManager();
		Invoice invoice = invoicingManager.invoice(income, getInvoiceRefCode(), getInvoiceDate());

		IController invoiceController = FormUtil.getController(PURCHASE_INVOICE_CONTROLLER);
		invoiceController.clearCriteria();
		invoiceController.getCriteria().addEqualExpression(invoiceController.getFieldName(IFinanceAlias.INVOICE_ID), invoice.getId());
		invoiceController.onSearch(null);
		invoiceController.getModel().setRowIndex(0);
		invoiceController.onSelect(null);
	}

	private int obtainMaxIncomeNumber(String seriesId) {
		return SeriesNumberUtil.obtainNumber(seriesId, "Income");
	}

}
