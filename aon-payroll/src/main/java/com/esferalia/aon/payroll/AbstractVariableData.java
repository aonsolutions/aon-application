package com.esferalia.aon.payroll;

import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.payroll.enumeration.CNO;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.ContractVariables;
import com.esferalia.aon.payroll.enumeration.OccupationType;
import com.esferalia.aon.payroll.enumeration.QuoteGroup;
import com.esferalia.aon.salary.expression.IExpression;

public abstract class AbstractVariableData implements IExpression{
	
	@Transient
	public ContractVariables getVariable(){
		ContractVariables var = ContractVariables.getVariable(getName()!=null?getName().toUpperCase():null);
		if(var!=null){
			setName(getName().toUpperCase());
		}
		return var;
	}
	
	@Transient
	public Enum<?> getVariableEnum(){
		if(getName().equals(ContractVariables.CNO.getName())){
			return CNO.getCnoByValue(handleEditorExpression(getExpression()));
		} else if(getName().equals(ContractVariables.TC2.getName())){
			return ContractCode.getContractCodeByValue(handleEditorExpression(getExpression()));
		} else if(getName().equals(ContractVariables.QUOTE_GROUP.getName())){
			return QuoteGroup.getQuoteGroupByValue(handleEditorExpression(getExpression()));
		} else if(getName().equals(ContractVariables.OCCUPATION.getName())){
			return OccupationType.getOccupationTypeByValue(handleEditorExpression(getExpression()));
		} else if(getName().equals(ContractVariables.QUOTE_IT.getName())){
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
	
	public abstract void setName(String name);
	
	public abstract void setExpression(String expression);
	
}
