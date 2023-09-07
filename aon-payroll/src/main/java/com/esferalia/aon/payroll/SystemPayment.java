package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.SystemPaymentDB;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionScope;

@Entity
@Table(name="system_payment")
@Heritable
public class SystemPayment extends SystemPaymentDB implements IContractPayment {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
	public Integer getConceptId() {
		return getPaymentConcept()==null?null:getPaymentConcept().getId();
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
	@Transient
	public PaymentType getResolvedType() {
		PaymentType type = getPaymentType();
		if (type != null)
			return type;
		PaymentConcept concept = getPaymentConcept();
		if (concept == null)
			return null;
		return concept.getType();

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
