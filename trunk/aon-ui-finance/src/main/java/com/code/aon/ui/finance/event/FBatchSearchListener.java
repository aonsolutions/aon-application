package com.code.aon.ui.finance.event;

import java.util.Date;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class FBatchSearchListener extends ControllerSearchListener {

	/** Determines if the finance is a payment or a charge. */
	private Boolean payment;
	
	private RegistryBank registryBank;
	
	private Date issueDateFrom;
	
	private Date issueDateTo;
	
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
		
	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

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

	@Override
	protected void init() throws ManagerBeanException {
		setPayment(null);
		setIssueDateFrom(null);
		setIssueDateTo(null);
		setRegistryBank(null);
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if ( getPayment() != null ) {
			String payment = getController().getFieldName(IFinanceAlias.FINANCE_PAYMENT);
			criteria.addEqualExpression(payment, getPayment());
		}
		if ( getRegistryBank() != null ) {
			String field = getController().getFieldName(IFinanceAlias.FINANCE_BATCH_REGISTRY_BANK_ID);
			criteria.addEqualExpression(field, getRegistryBank().getId());
		}
		if ( getIssueDateFrom() != null ) {
			String field = getController().getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE);
			criteria.addGreaterThanOrEqualExpression(field, getIssueDateFrom());
		}
		if ( getIssueDateTo() != null ) {
			String field = getController().getFieldName(IFinanceAlias.FINANCE_BATCH_ISSUE_DATE);
			criteria.addLessThanOrEqualExpression(field, getIssueDateTo());
		}
	}	
}