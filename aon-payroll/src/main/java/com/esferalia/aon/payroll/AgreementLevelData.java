package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.AgreementLevelDataDB;
import com.esferalia.aon.payroll.enumeration.CNO;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;

@Entity
@Table(name="agreement_level_data")
@Heritable
public class AgreementLevelData extends AgreementLevelDataDB implements IExpression {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Transient
	public ContextVariable getVariable(){
		return ContextVariable.getVariableByName(getName());
	}
	
	@Transient
	public Enum<?> getVariableEnum(){
		if(getName().equals(ContextVariable.CNO.getName())){
			return CNO.getCnoByValue(handleEditorExpression(getExpression()));
		} else if(getName().equals(ContextVariable.TC2.getName())){
			return ContractCode.getContractCodeByValue(handleEditorExpression(getExpression()));
		} else if(getName().equals(ContextVariable.QUOTE_GROUP.getName())){
			return QuoteGroup.getQuoteGroupByValue(handleEditorExpression(getExpression()));
		} else if(getName().equals(ContextVariable.OCCUPATION.getName())){
			return OccupationType.getOccupationTypeByValue(handleEditorExpression(getExpression()));
		} else if(getName().equals(ContextVariable.QUOTE_IT.getName())){
			return null;
		}
		return null;
	}
	
	private String handleEditorExpression(String expression) {
		if( StringUtils.startsWith(expression, "\"") && StringUtils.endsWith(expression, "\"")){
			return expression = expression.substring(1, expression.length()-1);
		}
		return null;
	}
	
	@Transient
	public Double getDoubleExpression(){
		return Double.valueOf(getExpression());
	}
	
	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.AGREEMENT;
	}

	@Override
	@Transient
	public boolean isReadOnly() {
		return false;
	}
	
}
