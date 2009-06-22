package com.code.aon.ui.warehouse.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class DeliverySearchListener extends ControllerSearchListener {

	private Date issueTimeFrom;
	
	private Date issueTimeTo;

	private Customer customer;

	public Date getIssueTimeFrom() {
		return issueTimeFrom;
	}

	public void setIssueTimeFrom(Date issueTimeFrom) {
		this.issueTimeFrom = issueTimeFrom;
	}

	public Date getIssueTimeTo() {
		return issueTimeTo;
	}

	public void setIssueTimeTo(Date issueTimeTo) {
		this.issueTimeTo = issueTimeTo;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setIssueTimeFrom(null);
		setIssueTimeTo(null);
		setCustomer(new Customer());
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getIssueTimeFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getController().getFieldName(IWarehouseAlias.DELIVERY_ISSUE_TIME), getIssueTimeFrom());
		}
		if (getIssueTimeTo() != null) {
			criteria.addLessThanOrEqualExpression(getController().getFieldName(IWarehouseAlias.DELIVERY_ISSUE_TIME), getIssueTimeTo());
		}
		if (getCustomer() != null && getCustomer().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(IWarehouseAlias.DELIVERY_CUSTOMER_ID), getCustomer().getId());			
		}
	}	
}