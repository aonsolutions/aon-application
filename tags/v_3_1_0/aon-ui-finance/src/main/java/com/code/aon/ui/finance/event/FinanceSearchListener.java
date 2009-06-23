package com.code.aon.ui.finance.event;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Bank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class FinanceSearchListener extends ControllerSearchListener {

	/** Determines if the finance is a payment or a charge. */
	private Boolean payment;
	
	private Bank bank;
	
	private Customer customer;
	
	private Supplier supplier;
	
	private Date dueDateFrom;
	
	private Date dueDateTo;
	
	private Date invoiceIssueDateFrom;
	
	private Date invoiceIssueDateTo;
	
	/**
	 * Gets if the finance is a payment or a charge.
	 * 
	 * @return true if the finance is a payment
	 */
	public Boolean getPayment() {
		return payment;
	}

	/**
	 * Sets if the finance is a payment or a charge.
	 * 
	 * @param payment true if the finance is a payment
	 */
	public void setPayment(Boolean payment) {
		this.payment = payment;
	}
		
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

	@Override
	protected void init() throws ManagerBeanException {
		setPayment(Boolean.FALSE);
		setBank( new Bank() );
		setCustomer( new Customer() );
		setSupplier( new Supplier() );
		setDueDateFrom(null);
		setDueDateTo(null);
		setInvoiceIssueDateFrom(null);
		setInvoiceIssueDateTo(null);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if ( (getBank() != null) && (!StringUtils.isEmpty(getBank().getCode())) ) {
			criteria.addEqualExpression("Finance.bank.code", getBank().getCode());			
		}
		if ( getPayment() != null ) {
			if ( getPayment() ) {
				if ( (getSupplier() != null) && (getSupplier().getId() != null) ) {
					String field = getController().getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID);
					criteria.addEqualExpression(field, getSupplier().getId());			
				}			
			} else {
				if ( (getCustomer() != null) && (getCustomer().getId() != null) ) {
					String field = getController().getFieldName(IFinanceAlias.FINANCE_REGISTRY_ID);
					criteria.addEqualExpression(field, getCustomer().getId());			
				}
			}
			String payment = getController().getFieldName(IFinanceAlias.FINANCE_PAYMENT);
			criteria.addEqualExpression(payment, getPayment());
		}
		if ( getDueDateFrom() != null ) {
			String field = getController().getFieldName(IFinanceAlias.FINANCE_DUE_DATE);
			criteria.addGreaterThanOrEqualExpression(field, getDueDateFrom());
		}
		if ( getDueDateTo() != null ) {
			String field = getController().getFieldName(IFinanceAlias.FINANCE_DUE_DATE);
			criteria.addLessThanOrEqualExpression(field, getDueDateTo());
		}
		if ( getInvoiceIssueDateFrom() != null ) {
			String field = getController().getFieldName(IFinanceAlias.FINANCE_INVOICE_ISSUE_DATE);
			criteria.addGreaterThanOrEqualExpression(field, getInvoiceIssueDateFrom());
		}
		if ( getInvoiceIssueDateTo() != null ) {
			String field = getController().getFieldName(IFinanceAlias.FINANCE_INVOICE_ISSUE_DATE);
			criteria.addLessThanOrEqualExpression(field, getInvoiceIssueDateTo());
		}
	}	
}