package com.code.aon.ui.finance.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.finance.controller.FinanceReturnController;

public class FinanceReturnSearchListener extends ControllerSearchListener {

	private Customer customer;
	
	private Supplier supplier;
	
	private Date dueDateFrom;
	
	private Date dueDateTo;
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
	public Date getDueDateFrom() {
		return dueDateFrom;
	}

	public void setDueDateFrom(Date dueDateFrom) {
		this.dueDateFrom = dueDateFrom;
	}

	public Date getDueDateTo() {
		return dueDateTo;
	}

	public void setDueDateTo(Date dueDateTo) {
		this.dueDateTo = dueDateTo;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setCustomer( new Customer() );
		setSupplier( new Supplier() );
		setDueDateFrom(null);
		setDueDateTo(null);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		FinanceReturnController controller = (FinanceReturnController) getController();
		Criteria criteria = controller.getCriteria();
		if ( controller.getPayment() ) {
			if ( (getSupplier() != null) && (getSupplier().getId() != null) ) {
				String field = controller.getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID);
				criteria.addEqualExpression(field, getSupplier().getId());			
			}			
		} else {
			if ( (getCustomer() != null) && (getCustomer().getId() != null) ) {
				String field = controller.getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID);
				criteria.addEqualExpression(field, getCustomer().getId());			
			}			
		}
		if ( getDueDateFrom() != null ) {
			String field = controller.getFieldName(IFinanceAlias.FINANCE_DUE_DATE);
			criteria.addGreaterThanOrEqualExpression(field, getDueDateFrom());
		}
		if ( getDueDateTo() != null ) {
			String field = controller.getFieldName(IFinanceAlias.FINANCE_DUE_DATE);
			criteria.addLessThanOrEqualExpression(field, getDueDateTo());
		}
	}
	
}