package com.code.aon.ui.warehouse.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class DeliverySearchListener extends RegistrySearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String REGISTRY_SEARCH_PREFFIX = "Delivery_customer_registry_";

	private Customer customer;

	private DeliveryStatus[] deliveryStatuses;

    private Item item;
    
    private Project project;
	
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
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setCustomer((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		DeliveryStatus[] defaultDeliveryStatus = {DeliveryStatus.PENDING};
		setDeliveryStatuses(defaultDeliveryStatus);
		setItem((Item)BeanManager.getManagerBean(Item.class).createNewTo());
		setProject((Project)BeanManager.getManagerBean(Project.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		super.completeCriteria(criteria);
		if (getCustomer() != null && getCustomer().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.DELIVERY_CUSTOMER_ID), getCustomer().getId());			
		}
		if (!ArrayUtils.isEmpty(getDeliveryStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.DELIVERY_STATUS);
			addEnumToCriteria(criteria, status, getDeliveryStatuses());
		}
		if ((getItem() != null) && (getItem().getId() != null)) {
			criteria.addEqualExpression("Delivery.lines.item.id", getItem().getId());
		}
		if ((getProject() != null) && (getProject().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.DELIVERY_PROJECT_ID), getProject().getId());			
		}						
	}	
}