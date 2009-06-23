package com.code.aon.ui.sales.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class SalesSearchListener extends ControllerSearchListener {

	private Date issueDateFrom;
	
	private Date issueDateTo;

	private Customer customer;

	public Date getIssueDateFrom() {
		return issueDateFrom;
	}

	public void setIssueDateFrom(Date issueDateFrom) {
		this.issueDateFrom = issueDateFrom;
	}

	public Date getIssueDateTo() {
		return issueDateTo;
	}

	public void setIssueDateTo(Date issueDateTo) {
		this.issueDateTo = issueDateTo;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setIssueDateFrom(null);
		setIssueDateTo(null);
		setCustomer(new Customer());
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if (getIssueDateFrom() != null) {
			criteria.addGreaterThanOrEqualExpression(getController().getFieldName(ISalesAlias.SALES_ISSUE_DATE), getIssueDateFrom());
		}
		if (getIssueDateTo() != null) {
			criteria.addLessThanOrEqualExpression(getController().getFieldName(ISalesAlias.SALES_ISSUE_DATE), getIssueDateTo());
		}
		if (getCustomer() != null && getCustomer().getId() != null) {
			criteria.addEqualExpression(getController().getFieldName(ISalesAlias.SALES_CUSTOMER_ID), getCustomer().getId());			
		}
	}	
}