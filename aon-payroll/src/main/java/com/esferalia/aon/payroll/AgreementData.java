package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.AgreementDataDB;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;

@Entity
@Table(name="agreement_data")
@Heritable
public class AgreementData extends AgreementDataDB implements IExpression {
	
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
