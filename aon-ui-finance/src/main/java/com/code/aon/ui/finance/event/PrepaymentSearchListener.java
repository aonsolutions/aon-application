package com.code.aon.ui.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class PrepaymentSearchListener extends ControllerSearchListener {

	private Creditor creditor;
	private Customer customer;
	
	public Creditor getCreditor() {
		return creditor;
	}

	public void setCreditor(Creditor creditor) {
		this.creditor = creditor;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setCreditor((Creditor)BeanManager.getManagerBean(Creditor.class).createNewTo());
		setCustomer((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		super.completeCriteria(criteria);
		if (getCreditor() != null && getCreditor().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PREPAYMENT_CREDITOR_ID), getCreditor().getId());			
		}
		if (getCustomer() != null && getCustomer().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PREPAYMENT_CUSTOMER_ID), getCustomer().getId());			
		}
	}

}