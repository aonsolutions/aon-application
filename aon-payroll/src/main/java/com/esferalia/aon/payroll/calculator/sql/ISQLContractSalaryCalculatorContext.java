package com.esferalia.aon.payroll.calculator.sql;

import java.sql.SQLException;

import com.code.aon.ql.Order;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.salary.expression.ExpressionException;

public interface ISQLContractSalaryCalculatorContext 
	extends IContractSalaryCalculatorContext{
	
	public void close() throws SQLException ;

	boolean next() throws SQLException, ExpressionException;
	
	@SuppressWarnings("serial")
	public static final OrderByList OLDER = new OrderByList() {
		{
			add(new Order( ExpressionUtilities.getIdentifierExpression("IF(ISNULL(end_date),0,1)"), true));
			add(new Order( ExpressionUtilities.getIdentifierExpression("end_date"), false));
		}
	};
	
	@SuppressWarnings("serial")
	public static final OrderByList NEWER = new OrderByList() {
		{
			add(new Order( ExpressionUtilities.getIdentifierExpression("IF(ISNULL(end_date),1,0)"), true));
			add(new Order( ExpressionUtilities.getIdentifierExpression("end_date"), true));
		}
	};

}
