package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ContractPaymentDB;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionScope;

@Entity
@Table(name="contract_payment")
public class ContractPayment extends ContractPaymentDB implements IContractPayment {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
	public Integer getConceptId() {
		return getPaymentConcept()==null?null:getPaymentConcept().getId();
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
	
	
	// TODO 
	private PaymentType paymentType;
	
	@Transient
	public PaymentType getPaymentType() {
		if(this.getType()!=null){
			paymentType = this.getType();
		}
		return paymentType;
	}
	public void setPaymentType(PaymentType paymentType) {
		this.paymentType = paymentType;
		this.setType(paymentType);
	}
	
	
}
