package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class InvoicingGroupSearchListener extends ControllerSearchListener {

	private Customer customer;
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setCustomer( new Customer() );
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if ( (getCustomer() != null) && (getCustomer().getId() != null) ) {
			String field = getController().getFieldName(IFinanceAlias.INVOICING_GROUP_PARENT_ID);
			criteria.addEqualExpression(field, getCustomer().getId());			
		}
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		setCustomer( new Customer() );
	}
	
}