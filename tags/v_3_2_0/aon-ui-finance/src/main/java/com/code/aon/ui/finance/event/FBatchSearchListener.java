package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class FBatchSearchListener extends ControllerSearchListener {

	/** Determines if the finance is a payment or a charge. */
	private Boolean payment;
	
	private RegistryBank registryBank;
	
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

	@Override
	protected void init() throws ManagerBeanException {
		setPayment(null);
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
	}	
}