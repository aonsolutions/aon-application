package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ContractDeductionDB;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionScope;

@Entity
@Table(name="contract_deduction")
public class ContractDeduction extends ContractDeductionDB implements IContractDeduction{

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
		return getDeductionConcept()==null?null:getDeductionConcept().getCode();
	}

	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.CONTRACT;
	}

	@Transient
	public String getFullDescription() {
		return (getDeductionConcept() == null || StringUtils.isEmpty(getDeductionConcept().getCode()))?
				getDescription():
				getDeductionConcept().getCode()+ " - " + (StringUtils.isEmpty(getDescription())?getDeductionConcept().getDescription():
					getDescription());
	}

	@Override
	@Transient
	public boolean isReadOnly() {
		return false;
	}
	
	// TODO 
	private DeductionType deductionType;
	
	@Transient
	public DeductionType getDeductionType() {
		if(this.getType()!=null){
			deductionType = this.getType();
		}
		return deductionType;
	}
	public void setDeductionType(DeductionType deductionType) {
		this.deductionType = deductionType;
		this.setType(deductionType);
	}

}
