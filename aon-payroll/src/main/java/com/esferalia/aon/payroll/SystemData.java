package com.esferalia.aon.payroll;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.SystemDataDB;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;

@Entity
@Table(name="system_data")
@Heritable
public class SystemData extends SystemDataDB implements IExpression, IVariableData {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.APPLICATION;
	}
	
}
