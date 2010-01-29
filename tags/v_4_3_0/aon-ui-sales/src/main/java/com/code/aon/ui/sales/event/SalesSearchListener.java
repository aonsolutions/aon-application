package com.code.aon.ui.sales.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.sales.enumeration.SalesStatus;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class SalesSearchListener extends ControllerSearchListener {

	private Customer customer;

	private SalesStatus[] salesStatuses;

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
	
	@Override
	protected void init() throws ManagerBeanException {
		setCustomer(new Customer());
		SalesStatus[] defaultSalesStatus = {SalesStatus.PENDING};
		setSalesStatuses(defaultSalesStatus);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getCustomer() != null && getCustomer().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(ISalesAlias.SALES_CUSTOMER_ID), getCustomer().getId());			
		}
		if (!ArrayUtils.isEmpty(getSalesStatuses())) {
			String status = getController().resolveAlias(ISalesAlias.SALES_STATUS);
			addEnumToCriteria(criteria, status, getSalesStatuses());
		}
	}	
}