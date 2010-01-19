package com.code.aon.ui.warehouse.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.warehouse.dao.IWarehouseAlias;
import com.code.aon.warehouse.enumeration.DeliveryStatus;

public class DeliverySearchListener extends ControllerSearchListener {

	private Customer customer;

	private DeliveryStatus[] deliveryStatuses;

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
	
	@Override
	protected void init() throws ManagerBeanException {
		setCustomer(new Customer());
		DeliveryStatus[] defaultDeliveryStatus = {DeliveryStatus.PENDING};
		setDeliveryStatuses(defaultDeliveryStatus);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getCustomer() != null && getCustomer().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID), getCustomer().getId());			
		}
		if (!ArrayUtils.isEmpty(getDeliveryStatuses())) {
			String status = getController().resolveAlias(IWarehouseAlias.DELIVERY_STATUS);
			addEnumToCriteria(criteria, status, getDeliveryStatuses());
		}
	}	
}