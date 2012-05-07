package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.AgreementDataDB;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;

@Entity
@Table(name="agreement_data")
public class AgreementData extends AgreementDataDB implements IExpression, IVariableData {
	
	private static final long serialVersionUID = 1L;

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
