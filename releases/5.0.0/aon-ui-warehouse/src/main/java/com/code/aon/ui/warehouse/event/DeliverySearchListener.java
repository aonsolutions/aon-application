package com.code.aon.ui.warehouse.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

public class DeliverySearchListener extends RegistrySearchListener {
	
	private static final String REGISTRY_SEARCH_PREFFIX = "Delivery_customer_registry_";

	private Customer customer;

	private DeliveryStatus[] deliveryStatuses;

    private Item item;
	
	public String getPreffix() throws ManagerBeanException {
		return REGISTRY_SEARCH_PREFFIX;
	}	
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public DeliveryStatus[] getDeliveryStatuses() {
		return deliveryStatuses;
	}

	public void setDeliveryStatuses(DeliveryStatus[] deliveryStatuses) {
		this.deliveryStatuses = deliveryStatuses;
	}
	
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setCustomer(new Customer());
		DeliveryStatus[] defaultDeliveryStatus = {DeliveryStatus.PENDING};
		setDeliveryStatuses(defaultDeliveryStatus);
		setItem(new Item());
		getItem().setProduct(new Product());		
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		super.completeCriteria();
		Criteria criteria = getController().getCriteria();
		if (getCustomer() != null && getCustomer().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID), getCustomer().getId());			
		}
		if (!ArrayUtils.isEmpty(getDeliveryStatuses())) {
			String status = getController().resolveAlias(IWarehouseAlias.DELIVERY_STATUS);
			addEnumToCriteria(criteria, status, getDeliveryStatuses());
		}
		if ((getItem() != null) && (getItem().getId() != null)) {
			criteria.addEqualExpression("Delivery.lines.item.id", getItem().getId());
		}						
	}	
}