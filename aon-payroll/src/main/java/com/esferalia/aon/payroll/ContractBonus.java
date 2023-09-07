package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.ContractBonusDB;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.expression.ExpressionScope;

@Entity
@Table(name="contract_bonus")
public class ContractBonus extends ContractBonusDB implements IContractBonus{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	@Transient
	public String getName() {
		return null ;
	}

	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.CONTRACT;
	}

	@Override
	@Transient
	public boolean isReadOnly() {
		return false;
	}

	@Override
	@Transient
	public BonusType getType() {
		return getBonusConcept() == null ? null : getBonusConcept().getType(); 
	}

	@Override
	@Transient
	public double getAmount() {
		if (NumberUtils.isNumber(getExpression()) ) {
			return NumberUtils.toDouble(getExpression());	
		}
		return 0;
	}

	@Transient
	public String getFullDescription() {
		return (getBonusConcept() == null || getBonusConcept().getId()==null )?
				getDescription():
					getBonusConcept().getId()+ " - " + (StringUtils.isEmpty(getDescription())?getBonusConcept().getDescription():
					getDescription());
	}

	
}
