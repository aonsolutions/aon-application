package com.code.aon.ui.sales.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.registry.controller.event.RegistrySearchListener;

public class SalesSearchListener extends RegistrySearchListener {

	private static final String REGISTRY_SEARCH_PREFFIX = "Sales_customer_registry_";

	private Customer customer;
	
	private SalesStatus[] salesStatuses;
	
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

	public SalesStatus[] getSalesStatuses() {
		return salesStatuses;
	}

	public void setSalesStatuses(SalesStatus[] salesStatuses) {
		this.salesStatuses = salesStatuses;
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
		SalesStatus[] defaultSalesStatus = {SalesStatus.PENDING};
		setSalesStatuses(defaultSalesStatus);
		setItem(new Item());
		getItem().setProduct(new Product());		
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		super.completeCriteria();
		Criteria criteria = getController().getCriteria();
		if (getCustomer() != null && getCustomer().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(ISalesAlias.SALES_CUSTOMER_ID), getCustomer().getId());			
		}
		if (!ArrayUtils.isEmpty(getSalesStatuses())) {
			String status = getController().resolveAlias(ISalesAlias.SALES_STATUS);
			addEnumToCriteria(criteria, status, getSalesStatuses());
		}
		if ((getItem() != null) && (getItem().getId() != null)) {
			criteria.addEqualExpression("Sales.lines.item.id", getItem().getId());
		}				
	}	
}