package com.code.aon.ui.sales.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Series;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.bridge.invoicing.DeliveryInvoicingManager;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.sales.Sales;
import com.code.aon.sales.bridge.DeliveryManager;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.seller.Seller;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.customer.util.CustomerValidationManager;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryDetailType;

/**
 * Controller used in the sales maintenance.
 */
public class SalesController extends BasicController {

	private final String DELIVERY_CONTROLLER = "delivery";
	private final String SALE_INVOICE_CONTROLLER = "saleInvoice";

	private List<SelectItem> addresses;
	private Boolean defaultPayMethod;
	private IPriceStrategy priceStrategy;
	private RegistryValidationManager vm;
	private boolean showDeliveryWindow;
	private String deliverySeries;
	private int deliveryNumber;
	private Date deliveryDate;
	private Warehouse deliveryWarehouse;
	private boolean showInvoiceWindow;
	private String invoiceSeries;
	private int invoiceNumber;
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

	public Warehouse getInvoiceWarehouse() {
		return invoiceWarehouse;
	}

	public void setInvoiceWarehouse(Warehouse invoiceWarehouse) {
		this.invoiceWarehouse = invoiceWarehouse;
	}

	public boolean isPending(){
		Sales sales = (Sales)this.getTo();
		if (sales.getStatus() != null) {
			return sales.getStatus().equals(SalesStatus.PENDING);
		}
		return false;
	}

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		int number = obtainMaxNumber((String)event.getNewValue());
		SecurityLevel securityLevel = obtainSeriesSecurityLevel((String)event.getNewValue());
		if (this.getTo() != null) {
			((Sales)this.getTo()).setNumber(number);
			((Sales)this.getTo()).setSecurityLevel(securityLevel);
		}
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
    	return SeriesNumberUtil.obtainNumber(seriesId, StringUtils.capitalize(this.getBeanName()));
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

	public void customerData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			isBlocked(customer);
			((Sales)this.getTo()).setCustomer(customer);
			((Sales)this.getTo()).setScope(customer.getScope());
			loadAddresses(customer.getId());
			loadDefaultPayMethod(customer.getId(), false);
		} else {
			setAddresses(null);
		}
	}

	private boolean isBlocked(Customer customer) {
		return getRegistryValidationManager().isBlocked(customer);
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
			if (((Sales)this.getTo()).getPayMethod() != null && ((Sales)this.getTo()).getPayMethod().getId() != null) {
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
		return getPriceStrategy().getTotalPrice(sales, sales.getCustomer());
	}

	public void onDeliveryShow(ActionEvent event) throws ManagerBeanException {
		Sales to = (Sales)this.getTo();
		setDeliverySeries(to.getSeries());
		setDeliveryNumber(obtainMaxDeliveryNumber(to.getSeries()));
		setDeliveryDate(new Date());
	}

	public void onDeliverySeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		setDeliveryNumber(obtainMaxDeliveryNumber((String)event.getNewValue()));
	}

	private int obtainMaxDeliveryNumber(String seriesId) {
		return SeriesNumberUtil.obtainNumber(seriesId, "Delivery");
	}

	public void onDelivery(ActionEvent event) throws ManagerBeanException {
		setShowDeliveryWindow(false);

		Sales to = (Sales)this.getTo();
		DeliveryManager deliveryManager = new DeliveryManager();
		Delivery delivery = deliveryManager.salesDelivery(to, getDeliverySeries(), getDeliveryNumber(), getDeliveryDate(), getDeliveryWarehouse(), DeliveryDetailType.MANUAL);

		IController deliveryController = FormUtil.getController(DELIVERY_CONTROLLER);
		deliveryController.clearCriteria();
		deliveryController.getCriteria().addEqualExpression(deliveryController.getFieldName(IWarehouseAlias.DELIVERY_ID), delivery.getId());
		deliveryController.onSearch(null);
		deliveryController.getModel().setRowIndex(0);
		deliveryController.onSelect(null);
	}

	public void onInvoiceShow(ActionEvent event) throws ManagerBeanException {
		Sales to = (Sales)this.getTo();
		setInvoiceSeries(to.getSeries());
		setInvoiceNumber(obtainMaxInvoiceNumber(to.getSeries()));
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

	public void onInvoice(ActionEvent event) throws ManagerBeanException {
		setShowInvoiceWindow(false);

		Sales to = (Sales)this.getTo();
		int deliveryNumber = obtainMaxDeliveryNumber(getInvoiceSeries());
		DeliveryManager deliveryManager = new DeliveryManager();
		Delivery delivery = deliveryManager.salesDelivery(to, getInvoiceSeries(), deliveryNumber, getInvoiceDate(), getInvoiceWarehouse(), DeliveryDetailType.AUTOMATIC);
		DeliveryInvoicingManager invoicingManager = new DeliveryInvoicingManager();
		Invoice invoice = invoicingManager.invoice(delivery, getInvoiceSeries(), getInvoiceNumber(), getInvoiceDate());

		IController invoiceController = FormUtil.getController(SALE_INVOICE_CONTROLLER);
		invoiceController.clearCriteria();
		invoiceController.getCriteria().addEqualExpression(invoiceController.getFieldName(IFinanceAlias.INVOICE_ID), invoice.getId());
		invoiceController.onSearch(null);
		invoiceController.getModel().setRowIndex(0);
		invoiceController.onSelect(null);
	}

}
