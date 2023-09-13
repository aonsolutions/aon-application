package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ContractEmbargoDB;
import com.esferalia.aon.payroll.calculator.IContractEmbargo;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionScope;

@Entity
@Table(name="contract_embargo")
public class ContractEmbargo extends ContractEmbargoDB implements IContractEmbargo {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean fixedAmount;
	
	
	@Transient
	public boolean isFixedAmount() {
		if(NumberUtils.isNumber(getExpression())){
			fixedAmount = true;
		}
		return fixedAmount;
	}
	public void setFixedAmount(boolean fixedAmount) {
		this.fixedAmount = fixedAmount;
		if( !fixedAmount ){
			setExpression("MAX_EMBARGABLE()");
		}
	}
	
	@Transient
	public Double getFee(){
		if(NumberUtils.isNumber(getExpression())){
			return NumberUtils.toDouble(getExpression());
		}
		return null;
	}

	public void setFee(Double fee){
		setExpression(fee.toString());
	}
	
	
	// ------------------------------------------------------------------------
	
	@Override
	@Transient
	public String getName() {
		return null;
	}

	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.CONTRACT;
	}
	
	@Override
	@Transient
	public DeductionType getType() {
		return DeductionType.EMBARGO;
	}

	@Override
	@Transient
	public boolean isReadOnly() {
		return false;
	}

	@Override
	@Transient
	public Integer getEmbargo() {
		return getId();
	}

	@Override
	@Transient
	public double getAmount() {
		if (NumberUtils.isNumber(getExpression()) ) {
			return NumberUtils.toDouble(getExpression());	
		}
		return 0;
	}
	
}
