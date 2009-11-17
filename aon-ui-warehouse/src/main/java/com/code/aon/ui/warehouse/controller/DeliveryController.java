package com.code.aon.ui.warehouse.controller;

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
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.bridge.invoicing.DeliveryInvoicingManager;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.bridge.DeliveryManager;
import com.code.aon.sales.bridge.SalesTransferManager;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.customer.util.CustomerValidationManager;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.registry.util.RegistryValidationManager;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * Controller for Delivery.
 */
public class DeliveryController extends BasicController {

	private final String DELIVERY_DETAIL_CONTROLLER = "deliveryDetail";
	private final String SALE_INVOICE_CONTROLLER = "saleInvoice";

	private List<SelectItem> addresses;
	private Warehouse warehouse;
	private Boolean defaultPayMethod;
	private Boolean valuableDelivery;
	private IPriceStrategy priceStrategy;
	private RegistryValidationManager vm;
	private SalesTransferManager salesTransferManager;
	private boolean showSalesTransferWindow;
	private boolean showInvoiceWindow;
	private String invoiceSeries;
	private int invoiceNumber;
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
	
	public Boolean getValuableDelivery() {
		return valuableDelivery;
	}

	public void setValuableDelivery(Boolean valuableDelivery) {
		this.valuableDelivery = valuableDelivery;
	}

	public boolean isPending(){
		Delivery delivery = (Delivery)this.getTo();
		if (delivery.getStatus() != null) {
			return delivery.getStatus().equals(DeliveryStatus.PENDING);
		}
		return false;
	}

	public boolean isInvoiced(){
		Delivery delivery = (Delivery)this.getTo();
		if (delivery.getStatus() != null) {
			return delivery.getStatus().equals(DeliveryStatus.INVOICED);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public String getInvoiceCode() throws ManagerBeanException {
		Delivery delivery = (Delivery)this.getTo();
		if (delivery != null && delivery.getId() != null) {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_DELIVERY_ID), delivery.getId());
			Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				DeliveryDetail deliveryDetail = (DeliveryDetail)iterator.next();

				IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE), InvoiceSource.DELIVERY);
				criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_SOURCE_ID), deliveryDetail.getId());
				Iterator iter = invoiceDetailBean.getList(criteria).iterator();
				if (iter.hasNext()) {
					InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
					return invoiceDetail.getInvoice().getReferenceCode();
				}
			}
		}
    	return null;
	}

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		int number = obtainMaxNumber((String)event.getNewValue());
		SecurityLevel securityLevel = obtainSeriesSecurityLevel((String)event.getNewValue());
		if (this.getTo() != null) {
			((Delivery)this.getTo()).setNumber(number);
			((Delivery)this.getTo()).setSecurityLevel(securityLevel);
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
			((Delivery)this.getTo()).setCustomer(customer);
			((Delivery)this.getTo()).setScope(customer.getScope());
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
			if (((Delivery)this.getTo()).getPayMethod() != null && ((Delivery)this.getTo()).getPayMethod().getId() != null) {
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

	public void resetDeliveryPayMethod() {
		Delivery to = (Delivery)this.getTo();
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
		return getPriceStrategy().getTotalPrice((ICalculableContainer)getTo(), ((Delivery)getTo()).getCustomer());
	}

	public double getDeliveryTotalPrice() throws ManagerBeanException {
		Delivery delivery = (Delivery)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(delivery, delivery.getCustomer());
	}

	public void onSalesTransferShow(ActionEvent event) throws ManagerBeanException {
		Delivery to = (Delivery)this.getTo();

		IManagerBean salesBean = BeanManager.getManagerBean(Sales.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesBean.getFieldName(ISalesAlias.SALES_CUSTOMER_ID), to.getCustomer().getId());
		criteria.addEqualExpression(salesBean.getFieldName(ISalesAlias.SALES_SHIPPING_ADDRESS_ID), to.getRaddress().getId());
		criteria.addEqualExpression(salesBean.getFieldName(ISalesAlias.SALES_STATUS), SalesStatus.PENDING);
		criteria.addEqualExpression(salesBean.getFieldName(ISalesAlias.SALES_SECURITY_LEVEL), to.getSecurityLevel());
		criteria.addEqualExpression(salesBean.getFieldName(ISalesAlias.SALES_WORK_PLACE_ID), to.getWorkPlace().getId());
		criteria.addOrder(salesBean.getFieldName(ISalesAlias.SALES_ISSUE_DATE));
		criteria.addOrder(salesBean.getFieldName(ISalesAlias.SALES_SERIES));
		criteria.addOrder(salesBean.getFieldName(ISalesAlias.SALES_NUMBER));

		getSalesTransferManager().setSalesList(salesBean.getList(criteria));
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

		IController detailController = FormUtil.getController(DELIVERY_DETAIL_CONTROLLER);
		detailController.onSearch(null);
	}

	public void onInvoiceShow(ActionEvent event) throws ManagerBeanException {
		Delivery to = (Delivery)this.getTo();
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

		Delivery to = (Delivery)this.getTo();
		DeliveryInvoicingManager invoicingManager = new DeliveryInvoicingManager();
		Invoice invoice = invoicingManager.invoice(to, getInvoiceSeries(), getInvoiceNumber(), getInvoiceDate());

		IController invoiceController = FormUtil.getController(SALE_INVOICE_CONTROLLER);
		invoiceController.clearCriteria();
		invoiceController.getCriteria().addEqualExpression(invoiceController.getFieldName(IFinanceAlias.INVOICE_ID), invoice.getId());
		invoiceController.onSearch(null);
		invoiceController.getModel().setRowIndex(0);
		invoiceController.onSelect(null);
	}

}