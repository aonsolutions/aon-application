package com.code.aon.ui.finance.event;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Bank;
import com.code.aon.finance.PayMethod;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.finance.controller.FinanceController;

public class FinanceSearchListener extends ControllerSearchListener {

	private Bank bank;
	
	private Customer customer;
	
	private Supplier supplier;
	
	private Date dueDateFrom;
	
	private Date dueDateTo;
	
	private Date invoiceIssueDateFrom;
	
	private Date invoiceIssueDateTo;
	
	private PayMethod payMethod;
	
	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

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

	public Date getInvoiceIssueDateFrom() {
		return invoiceIssueDateFrom;
	}

	public void setInvoiceIssueDateFrom(Date invoiceIssueDateFrom) {
		this.invoiceIssueDateFrom = invoiceIssueDateFrom;
	}

	public Date getInvoiceIssueDateTo() {
		return invoiceIssueDateTo;
	}

	public void setInvoiceIssueDateTo(Date invoiceIssueDateTo) {
		this.invoiceIssueDateTo = invoiceIssueDateTo;
	}

	public PayMethod getPayMethod() {
		return payMethod;
	}

	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setBank( new Bank() );
		setCustomer( new Customer() );
		setSupplier( new Supplier() );
		setDueDateFrom(null);
		setDueDateTo(null);
		setInvoiceIssueDateFrom(null);
		setInvoiceIssueDateTo(null);
		setPayMethod(null);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		FinanceController controller = (FinanceController) getController();
		Criteria criteria = controller.getCriteria();
		if ( (getBank() != null) && (!StringUtils.isEmpty(getBank().getCode())) ) {
			criteria.addEqualExpression("Finance.bank.code", getBank().getCode());			
		}
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
		if ( getInvoiceIssueDateFrom() != null ) {
			String field = controller.getFieldName(IFinanceAlias.FINANCE_INVOICE_ISSUE_DATE);
			criteria.addGreaterThanOrEqualExpression(field, getInvoiceIssueDateFrom());
		}
		if ( getInvoiceIssueDateTo() != null ) {
			String field = controller.getFieldName(IFinanceAlias.FINANCE_INVOICE_ISSUE_DATE);
			criteria.addLessThanOrEqualExpression(field, getInvoiceIssueDateTo());
		}
		if ( getPayMethod() != null ) {
			String field = controller.getFieldName(IFinanceAlias.FINANCE_PAY_METHOD_ID);
			criteria.addEqualExpression(field, getPayMethod().getId());
		}
	}	
}