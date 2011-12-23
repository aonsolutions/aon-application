package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.ContractDataDB;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;

@Entity
@Table(name="contract_data")
public class ContractData extends ContractDataDB implements IExpression, IVariableData {
	
	private static final long serialVersionUID = 1L;

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
	
}
