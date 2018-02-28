package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.OrderByList;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.calculator.ISalaryCalculatorContext;
import com.esferalia.aon.salary.expression.ExpressionException;

public interface ISQLContractSalaryCalculatorContext extends
		IContractSalaryCalculatorContext {

	int getId();

	void close() throws SQLException;

	Connection getConnection();

	Date getDate(String table, String column);

	String getString(String table, String column);

	Integer getInt(String table, String column);

	Object getObject(String table, String column);

	double getIrpf() ;

	Object liquid(double liquid, Date start, Date end) 
			throws ExpressionException, SQLException, SalaryException;

	boolean next() throws SQLException, ExpressionException;
	
	
	/*
	ISalaryCalculatorContext getLiquidCalculatorContext(
			Connection conn, Date startDate, Date endDate, Date issueDate,
			Criteria criteria, final double x);
			*/

	@SuppressWarnings("serial")
	static final OrderByList OLDER = new OrderByList() {
		{
			add(new Order(
					ExpressionUtilities
							.getIdentifierExpression("IF(ISNULL(end_date),0,1)"),
					true));
			add(new Order(
					ExpressionUtilities.getIdentifierExpression("end_date"),
					false));
			add(new Order(
					ExpressionUtilities.getIdentifierExpression("start_date"),
					false));
		}
	};

	@SuppressWarnings("serial")
	static final OrderByList NEWER = new OrderByList() {
		{
			add(new Order(
					ExpressionUtilities
							.getIdentifierExpression("IF(ISNULL(end_date),1,0)"),
					true));
			add(new Order(
					ExpressionUtilities.getIdentifierExpression("end_date"),
					true));
			add(new Order(
					ExpressionUtilities.getIdentifierExpression("start_date"),
					true));
		}
	};

}
