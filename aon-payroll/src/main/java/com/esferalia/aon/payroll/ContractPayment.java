package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.esferalia.aon.entity.master.ContractPaymentDB;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.salary.expression.ExpressionScope;

@Entity
@Table(name="contract_payment")
public class ContractPayment extends ContractPaymentDB implements IContractPayment {
	
	private static final long serialVersionUID = 1L;

	@Override
	@Transient
	public double getAmount() {
		if (NumberUtils.isNumber(getExpression()) ) {
			return NumberUtils.toDouble(getExpression());	
		}
		return 0;
	}
	
	@Override
	@Transient
	public String getName() {
		return getPaymentConcept()==null?null:getPaymentConcept().getCode();
	}

	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.CONTRACT;
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
