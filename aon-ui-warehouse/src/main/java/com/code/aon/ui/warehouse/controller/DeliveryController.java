package com.code.aon.ui.warehouse.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.commercial.Offer;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.tasCommercial.TasOffer;
import com.code.aon.tasCommercial.dao.ITasCommercialAlias;
import com.code.aon.tasDelivery.TasDelivery;
import com.code.aon.tasDelivery.dao.ITasDeliveryAlias;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.PageDataModel;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.report.OutputFormat;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

/**
 * Controller for Delivery.
 * 
 * @author Consulting & Development.
 */
public class DeliveryController extends BasicController {
	
	
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(DeliveryController.class.getName());
	
	/**
	 * Price calculator
	 */
	private IPriceStrategy priceStrategy;
	
    /**
     * The ident of the support order related to this delivery
     */
    private Integer supportOrderId;
    
    /**
     * The ident of the offer related to this delivery
     */
    private Integer offerId;
    
	/**
	 * The ident of the employee
	 */
	private Integer employeeId;
	
	/**
	 * Description
	 */
	private String description;
	
	/**
	 * The ident of the point of sale
	 */
	private Integer posId;
	
	/**
	 * The ident of the warehouse
	 */
	private Integer warehouseId;
	
	private Integer scopeId;
	
    /**
     * Returns the support order id
     * 
     * @return the support order id
     */
    public Integer getSupportOrderId() {
        return supportOrderId;
    }

    /**
     * Assigns the support order ident
     * 
     * @param supportOrderId the ident of the support order
     */
    public void setSupportOrderId(Integer supportOrderId) {
        this.supportOrderId = supportOrderId;
    }
    
    /**
     * Returns the offer id
     * 
     * @return the offer id
     */
    public Integer getOfferId() {
        return offerId;
    }

    /**
     * Assigns the offer ident
     * 
     * @param offerId the ident of the offer
     */
    public void setOfferId(Integer offerId) {
        this.offerId = offerId;
    }
    
	/**
	 * Returns the ident of the employee
	 * 
	 * @return employee ident
	 */
	public Integer getEmployeeId() {
		return employeeId;
	}

	/**
	 * Assigns the employee ident
	 * 
	 * @param employeeId the employee ident
	 */
	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	/**
	 * Returns the description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Assigns the desciption
	 * 
	 * @param description the description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the workplace ident
	 * 
	 * @return the workplace ident
	 */
	public Integer getPosId() {
		return posId;
	}

	/**
	 * Assigns the workplace ident
	 * 
	 * @param workPlaceId workplace ident
	 */
	public void setPosId(Integer workPlaceId) {
		this.posId = workPlaceId;
	}

	/**
	 * Returns the warehouse ident
	 * 
	 * @return the warehouse ident
	 */
	public Integer getWarehouseId() {
		return warehouseId;
	}

	/**
	 * Assigns the warehouse ident
	 * 
	 * @param warehouseId the warehouse ident
	 */
	public void setWarehouseId(Integer warehouseId) {
		this.warehouseId = warehouseId;
	}
	

	public Integer getScopeId() {
		return scopeId;
	}

	public void setScopeId(Integer scopeId) {
		this.scopeId = scopeId;
	}

	/**
	 * Returns if the delivery is updatable or not
	 * if the delivery is closed returns false
	 * 
	 * @return is the delivery data editable
	 */
	public boolean isEditable(){
		if((Delivery)this.getTo() != null && ((Delivery)this.getTo()).getStatus() != null){
			if(((Delivery)this.getTo()).getStatus().equals(DeliveryStatus.CLOSED)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns if the delivery is closed or not
	 * 
	 * @return is the delivery closed
	 * @throws ManagerBeanException
	 */
	public boolean isClosed() throws ManagerBeanException{
		return ((Delivery)this.getModel().getRowData()).getStatus().equals(DeliveryStatus.CLOSED);
	}
	
	/**
	 * Reset and initializes the controller
	 * Called from the menu
	 * 
	 * @param event a menu event
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unused")
	public void onReset(MenuEvent event) throws ManagerBeanException{
        setSupportOrderId(null);
        setOfferId(null);
		this.setModel(new PageDataModel(this,0,20));
		this.clearCriteria();
		super.onReset(null);
	}
	
	/**
	 * Reset and initializes the controller
	 * 
	 * @param event an action event
	 */
	@Override
	public void onReset(ActionEvent event) {
		try {
            setSupportOrderId(null);
            setOfferId(null);
			this.setModel(new PageDataModel(this,0,20));
			this.clearCriteria();
			super.onReset(null);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error getting model onSelect", e);
		}
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
	
	/**
	 * Calculates the base price instead of the price strategy
	 * 
	 * @return the base price
	 * @throws ManagerBeanException
	 */
	public double getDeliveryBasePrice() throws ManagerBeanException{
		return getPriceStrategy().getTaxableBase((ICalculableContainer)this.getModel().getRowData());
	}
	
	/**
	 * Calculates the total price instead of the price strategy
	 * 
	 * @return total price
	 * @throws ManagerBeanException
	 */
	public double getDeliveryTotalPrice() throws ManagerBeanException{
		Delivery delivery = (Delivery)this.getModel().getRowData();
		return getPriceStrategy().getTotalPrice(delivery,delivery.getCustomer());
	}
	
	/**
	 * Calculates the total tax rate instead of the price strategy
	 * 
	 * @return total tax rate
	 * @throws ManagerBeanException
	 */
	public double getDeliveryTotalTaxRate() throws ManagerBeanException {
		Delivery delivery = (Delivery)this.getModel().getRowData();
		Iterator<TaxBreakDown> iter = getPriceStrategy().getTaxBreakDowns(delivery,delivery.getCustomer()).iterator();
		double total = 0;
		while(iter.hasNext()){
			total += iter.next().getTaxQuota();
		}
		return total;
	}
	
	/**
	 * Returns total delivery surcharge quota instead of the price strategy
	 * 
	 * @return total surcharge quota
	 * @throws ManagerBeanException
	 */
	public double getDeliveryTotalSurchargeRate() throws ManagerBeanException {
		Delivery delivery = (Delivery)this.getModel().getRowData();
		Iterator<TaxBreakDown> iter = getPriceStrategy().getTaxBreakDowns(delivery,delivery.getCustomer()).iterator();
		double total = 0;
		while(iter.hasNext()){
			total += iter.next().getSurchargeQuota();
		}
		return total;
	}

	public String getSupportOrderData() throws ManagerBeanException {
		TasDelivery tasDelivery = obtainTasDelivery(((Delivery)this.getModel().getRowData()).getId());
		String supportOrderData = "";
		if (tasDelivery != null) {
			supportOrderData = tasDelivery.getSupportOrder().getTasItem().getPublicCode() + " / " + tasDelivery.getSupportOrder().getTasItem().getModel().getMake().getName()+ " " + tasDelivery.getSupportOrder().getTasItem().getModel().getName();			
		}
		return supportOrderData;
	}

	/**
	 * Searches and assigns a customer instead of the value of the event
	 * that is customer's ident
	 * 
	 * @param event the event that contains customer ident
	 * @throws ManagerBeanException
	 */
	@SuppressWarnings("unchecked")
	public void customerData(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(customerBean.getFieldName(ICustomerAlias.CUSTOMER_ID),event.getNewValue());
			Iterator iter = customerBean.getList(criteria).iterator();
			if(iter.hasNext()){
				Customer customer = (Customer)iter.next();
				((Delivery)this.getTo()).setCustomer(customer);
			}
		}
	}
	
	/**
	 * Returns the price strategy
	 * 
	 * @return the price strategy
	 */
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
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
			LOGGER.log(Level.SEVERE, "Error obtaining delivery with id= " + deliveryId, e);
		}
		return null;
	}
	
	/**
	 * Returns the TasDelivery related to the Delivery of the controller 
	 * 
	 * @return the tasdelivery
	 */
	public TasDelivery obtainTasDelivery(){
		return obtainTasDelivery(((Delivery)this.getTo()).getId());
	}
	
	@SuppressWarnings("unchecked")
	private TasDelivery obtainTasDelivery(Integer deliveryId) {
		try {
			IManagerBean tasDeliveryBean = BeanManager.getManagerBean(TasDelivery.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(tasDeliveryBean.getFieldName(ITasDeliveryAlias.TAS_DELIVERY_DELIVERY_ID), deliveryId);
			Iterator iter = tasDeliveryBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (TasDelivery)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining associated tasDelivery", e);
		}
		return null;
	}

	/**
	 * Adds a criteria greater than or equal to the issue date
	 * 
	 * @param event contains the value
	 * @throws ManagerBeanException
	 */
	public void addIssueDateGreaterThanExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addGreaterThanOrEqualExpression(getFieldName(IWarehouseAlias.DELIVERY_ISSUE_TIME), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Adds a criteria less than or equal to the issue date
	 * 
	 * @param event contains the value
	 * @throws ManagerBeanException
	 */
	public void addIssueDateLessThanExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addLessThanOrEqualExpression(getFieldName(IWarehouseAlias.DELIVERY_ISSUE_TIME), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Searchs instead of the customer's document
	 * 
	 * @param event contains the customer document
	 * @throws ManagerBeanException
	 * @throws ExpressionException
	 */
	@SuppressWarnings("unchecked")
	public void addTargetExpression(ValueChangeEvent event)
		throws ManagerBeanException, ExpressionException {
	    if ((event.getNewValue() != null)
	    		&& (!"".equals(event.getNewValue().toString().trim()))
	    		) {
			Criteria criteria = new Criteria();
			IManagerBean bean = BeanManager.getManagerBean(Customer.class);
			String identifier = bean.getFieldName(ICustomerAlias.CUSTOMER_REGISTRY_DOCUMENT);
			criteria.addEqualExpression(identifier, event.getNewValue());
			List list = bean.getList(criteria);
			Iterator iter = list.iterator();
	    	Criteria c = getCriteria();
	    	if (iter.hasNext()){
				while (iter.hasNext()){
					Customer t = (Customer)iter.next();
					Object value = t.getId(); 
					c.addEqualExpression(getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID), value);
					setCriteria(c);
				}
	    	}else{
				Object value = new Integer(-1); 
				c.addEqualExpression(getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID), value);
				setCriteria(c);
	    	}
		}
	}
	
	/**
	 * Adds equal expression
	 * 
	 * @param event constains the value
	 * @throws ManagerBeanException
	 */
	public void addEqualExpression(ValueChangeEvent event)
		throws ManagerBeanException {
	    if (event.getNewValue() != null) {
	    	Criteria c = getCriteria();
			Object value = event.getNewValue();
			c.addEqualExpression(getFieldName(event.getComponent().getId()), value);
			setCriteria(c);
		}
	}
	
	/**
	 * Searchs instead of the customer's ident
	 * 
	 * @param event contains the customer ident
	 * @throws ManagerBeanException
	 */
	public void addCustomerExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null && !((String)event.getNewValue()).trim().equals("")){
			try{
				Integer id = new Integer((String)event.getNewValue());
				Criteria criteria = getCriteria();
				criteria.addEqualExpression(getManagerBean().getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID), id);
				setCriteria(criteria);
			} catch (Exception e) {
			}
		}
	}

    /**
     * Recovers all offers related with supportOrderId
     * 
     * @return offers List
     * @throws ManagerBeanException
     */
	@SuppressWarnings("unchecked")
    public List<SelectItem> getRelatedTASOffers() throws ManagerBeanException {
        LinkedList<SelectItem> offers = new LinkedList<SelectItem>();
        offerId = null;
        if (supportOrderId != null) {
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Criteria criteria = new Criteria();
            IManagerBean tasOfferBean = BeanManager.getManagerBean(TasOffer.class);
            criteria.addEqualExpression(tasOfferBean.getFieldName(ITasCommercialAlias.TAS_OFFER_SUPPORT_ORDER_ID), supportOrderId);
            Iterator iter = tasOfferBean.getList(criteria).iterator();
            while(iter.hasNext()){
                TasOffer tasOffer = (TasOffer)iter.next();
                Offer offer = tasOffer.getOffer();
                SelectItem item = new SelectItem(offer.getId(), (offer.getSeries()==null?"":offer.getSeries()+"/")+offer.getNumber()+ " - " +formatter.format(offer.getIssueDate()));
                offers.add(item);
            }
        }
        return offers;
    }

    /**
     * Sets default parameters to report.
     * 
     * @param event that launched report
     */
	@SuppressWarnings("unused")
    public void onReport(ActionEvent event) {
        ReportManager manager = (ReportManager)AonUtil.getRegisteredBean("report");
        manager.setReportKey("delivery");
        manager.setOutputFormat(OutputFormat.PDF);
    }
	
	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException{
		if (event.getPhaseId() == PhaseId.ANY_PHASE) {
			event.setPhaseId(PhaseId.INVOKE_APPLICATION );
			event.queue();
		}
		if (event.getPhaseId() == PhaseId.INVOKE_APPLICATION) {
			int number = obtainMaxNumber((String)event.getNewValue());
			if(this.getTo() != null){
				((Delivery)this.getTo()).setNumber(number);	
			}
		}
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
		IManagerBean deliveryBean = BeanManager.getManagerBean(Delivery.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_SERIES), seriesId);
		Projection projection = Projection.max(deliveryBean.getFieldName(IWarehouseAlias.DELIVERY_NUMBER));
		Object value = deliveryBean.getUniqueResult(projection, criteria);
		if(value != null){
			return ((Integer)value).intValue() + 1;
		}
		return 1;
	}

}