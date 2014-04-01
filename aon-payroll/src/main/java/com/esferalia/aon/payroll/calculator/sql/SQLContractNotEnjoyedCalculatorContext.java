package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLContractNotEnjoyedCalculatorContext 
	extends SQLContractSalaryCalculatorContext{

	
	
	
	public SQLContractNotEnjoyedCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate) throws SQLException,
			ExpressionException {
		this( connection , startDate, endDate, issueDate, null);
	}

	public SQLContractNotEnjoyedCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria) throws SQLException,
			ExpressionException {
		this( connection , startDate, endDate, issueDate, criteria, getPaymentsCriteria(SalaryType.NOT_ENJOYED_VACATIONS));
	}
	
	public SQLContractNotEnjoyedCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Criteria criteria, Criteria paymentsCriteria) throws SQLException,
			ExpressionException {
		this( connection , startDate, endDate, issueDate, null,criteria, paymentsCriteria );
	}

	public SQLContractNotEnjoyedCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate, Criteria criteria) throws SQLException,
			ExpressionException {
		super( connection , startDate, endDate, issueDate, chargeDate, criteria, getPaymentsCriteria(SalaryType.NOT_ENJOYED_VACATIONS) );
	}

	public SQLContractNotEnjoyedCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, Date chargeDate, Criteria criteria, Criteria paymentsCriteria) throws SQLException,
			ExpressionException {
		super( connection , startDate, endDate, issueDate, chargeDate, criteria, paymentsCriteria );
	}

	@Override
	public SalaryType getSalaryType() {
		return SalaryType.NOT_ENJOYED_VACATIONS;
	}
	
	
	// ------------------------------------------------------------------------
	
}