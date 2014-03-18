package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.SystemCostDB;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;

@Entity
@Table(name="system_cost")
public class SystemCost extends SystemCostDB implements IExpression{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.APPLICATION;
	}

	@Override
	@Transient
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transient
	public boolean isReadOnly() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
