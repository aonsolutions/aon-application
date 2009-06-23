package com.code.aon.ui.warehouse.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Series;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.faces.component.richfaces.lookup.LookupChangeEvent;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.report.OutputFormat;
import com.code.aon.ui.customer.util.CustomerValidationManager;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.Warehouse;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * Controller for Delivery.
 * 
 * @author Consulting & Development.
 */
public class DeliveryController extends BasicController {

	private List<SelectItem> addresses;

	private Warehouse warehouse;
	
	private IPriceStrategy priceStrategy;
	
	private CustomerValidationManager cvm;

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		Series series = SeriesNumberUtil.obtainSeries((String)event.getNewValue());
		if (this.getTo() != null) {
			((Delivery)this.getTo()).setNumber(SeriesNumberUtil.obtainNumber((String)event.getNewValue(), StringUtils.capitalize(this.getBeanName())));
			((Delivery)this.getTo()).setSecurityLevel((series!=null)?series.getSecurityLevel():null);
		}
	}

	public boolean isPending(){
		Delivery delivery = (Delivery)this.getTo();
		if (delivery.getStatus() != null) {
			return delivery.getStatus().equals(DeliveryStatus.PENDING);
		}
		return false;
	}

	public void customerData(LookupChangeEvent event) throws ManagerBeanException {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Customer customer = (Customer)event.getNewValue();
			isBlocked(customer); // Saca el mensaje de bloqueo.
			((Delivery)this.getTo()).setCustomer(customer);
			loadAddresses(customer.getId());
		} else {
			setAddresses(null);
		}
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

    public List<SelectItem> getAddresses() {
		return addresses;
	}
	
	public void setAddresses(List<SelectItem> addresses) {
		this.addresses = addresses;
	}
	
	public int getAddressCount() {
		if (addresses != null){
			return addresses.size();
		}
		return 0;
	}
	
    public Warehouse getWarehouse() {
		return warehouse;
	}
	
	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}
	
	public double getTaxableBase(){
		return getPriceStrategy().getTaxableBase((ICalculableContainer)getTo());
	}

	public double getTotalPrice(){
		return getPriceStrategy().getTotalPrice((ICalculableContainer)getTo(), ((Delivery)getTo()).getCustomer());
	}

	@SuppressWarnings("unused")
    public void onReport(ActionEvent event) {
        ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("sales");
        manager.setOutputFormat(OutputFormat.PDF);
    }

	/**
	 * A list of deliveries currently checked
	 */
	private ArrayList<Delivery> checks= new ArrayList<Delivery>();
	
	public boolean getRowChecked() {
		Delivery to = (Delivery) model.getRowData();
		return checks.contains( to );
	}
	
	/**
	 * Adds or removes a Delivery in the checks list
	 * 
	 * @param rowChecked true to add and false to remove
	 */
	public void setRowChecked(boolean rowChecked) {
		if ( rowChecked ) {
			Delivery to = (Delivery) model.getRowData();
			if (!checks.contains( to )) {
				checks.add( to );
			}
		} else {
			Delivery to = (Delivery) model.getRowData();
			if (checks.contains( to )) {
				checks.remove( to );
			}
		}
	}
	
	/**
	 * If the value changed sets the row to be checked or not
	 * true to add or false to remove
	 * 
	 * @param event the event that is launched by value change
	 */
	public void rowSelected(ValueChangeEvent event){
		if(event.getNewValue() != null){
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	/**
	 * Adds a transfer object to the checks
	 * 
	 * @param to the transfer object to be added
	 */
	public void addToCheckList(ITransferObject to){
		if(!checks.contains( to )){
			checks.add( (Delivery)to );
		}
	}
	
	/**
	 * Checks if this transfer object is the checks list
	 * 
	 * @param to transfer object to be analized
	 * @return true if exists
	 */
	public boolean isChecked(ITransferObject to){
		return checks.contains( to );
	}
	
	/**
	 * Returns the size of the checks list
	 * 
	 * @return quantity of objects
	 */
	public int getCheckListSize(){
		return checks.size();
	}
	
	/**
	 * Resets the checks list
	 */
	public void clearCheckList(){
		this.checks = new ArrayList<Delivery>();
	}
	
	/**
	 * When changes the value that set a delivery to be loaded calls loadDelivery
	 * 
	 * @param event the event that contains the ident to be loaded
	 * @throws ManagerBeanException
	 */
	public void loadDelivery(ValueChangeEvent event) throws ManagerBeanException {
		if(event.getNewValue() != null){
			loadDelivery((Integer)event.getNewValue());
		}
	}
	
	/**
	 * Loads a Delivery with this customer ident nad status pending 
	 * 
	 * @param customerId the ident of the customer
	 * @throws ManagerBeanException
	 */
	public void loadDelivery(Integer customerId) throws ManagerBeanException{
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getManagerBean().getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID),customerId);
		criteria.addEqualExpression(getManagerBean().getFieldName(IWarehouseAlias.DELIVERY_STATUS),DeliveryStatus.PENDING);
		this.setCriteria(criteria);
		super.onSearch(null);
	}
	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.BasicController#getCollection()
	 */
	@SuppressWarnings("unchecked")
	public Collection getCollection(){
		List<ITransferObject> l = new LinkedList<ITransferObject>();
		l.add(obtainDelivery(((Delivery)this.getTo()).getId()));
		return l;
	}

	/**
	 * Returns the Delivery searched with this delivery ident
	 * 
	 * @param deliveryId the ident of the delivery
	 * @return the delivery of this ident
	 */
	@SuppressWarnings("unchecked")
	private ITransferObject obtainDelivery(Integer deliveryId) {
		try {
			IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_ID), deliveryId);
			Iterator iter = deliveryBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Delivery)iter.next();
			}
		} catch (ManagerBeanException e) {
			//LOGGER.log(Level.SEVERE, "Error obtaining delivery with id= " + deliveryId, e);
		}
		return null;
	}

	private boolean isBlocked(Customer customer) {
		return getCustomerValidationManager().isBlocked(customer);
	}

	private CustomerValidationManager getCustomerValidationManager() {
		if (cvm == null) {
			cvm = new CustomerValidationManager(); 
		}
		return cvm;
	}

}