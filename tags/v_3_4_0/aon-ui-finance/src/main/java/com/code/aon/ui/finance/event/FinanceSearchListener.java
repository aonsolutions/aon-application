package com.code.aon.ui.finance.event;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.finance.controller.FinanceController;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class FinanceSearchListener extends ControllerSearchListener {

	private Customer customer;

	private Supplier supplier;

	private Creditor creditor;

	private Bank bank;
	
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
	
	public Creditor getCreditor() {
		return creditor;
	}

	public void setCreditor(Creditor creditor) {
		this.creditor = creditor;
	}
	
	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setCustomer(new Customer());
		setSupplier(new Supplier());
		setCreditor(new Creditor());
		setBank(new Bank());
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		criteria.addEqualExpression(getController().getFieldName(IFinanceAlias.FINANCE_PAYMENT), ((FinanceController)getController()).isPayment());
		if ((getCustomer() != null) && (getCustomer().getId() != null)) {
			criteria.addEqualExpression(getController().getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID), getCustomer().getId());			
		}
		if ((getSupplier() != null) && (getSupplier().getId() != null)) {
			criteria.addEqualExpression(getController().getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID), getSupplier().getId());			
		}			
		if ((getCreditor() != null) && (getCreditor().getId() != null)) {
			criteria.addEqualExpression(getController().getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID), getCreditor().getId());			
		}			
		if ((getBank() != null) && (!StringUtils.isEmpty(getBank().getCode()))) {
			criteria.addEqualExpression(getController().getFieldName(IFinanceAlias.FINANCE_BANK_CODE), getBank().getCode());			
		}
	}

}