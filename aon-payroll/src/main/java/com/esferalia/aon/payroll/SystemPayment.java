package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.SystemPaymentDB;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.salary.expression.ExpressionScope;

@Entity
@Table(name="system_payment")
@Heritable
public class SystemPayment extends SystemPaymentDB implements IContractPayment {
	
	private static final long serialVersionUID = 1L;

	@Override
	@Transient
	public double getAmount() {
		throw new UnsupportedOperationException();
	}
	@Override
	@Transient
	public String getName() {
		return getPaymentConcept()==null?null:getPaymentConcept().getCode();
	}
	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.SYSTEM;
	}
	@Transient
	public String getFullDescription() {
		return (getPaymentConcept() == null || StringUtils.isEmpty(getPaymentConcept().getCode()))?
				getDescription():
				getPaymentConcept().getCode()+ " - " + (StringUtils.isEmpty(getDescription())?getPaymentConcept().getDescription():
					getDescription());
	}
	@Override
	@Transient
	public boolean isReadOnly() {
		return false;
	}
	
}
