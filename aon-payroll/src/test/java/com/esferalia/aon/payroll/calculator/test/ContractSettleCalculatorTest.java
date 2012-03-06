package com.esferalia.aon.payroll.calculator.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSettleCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

public class ContractSettleCalculatorTest extends AbstractSalaryCalculatorTest{
	

	
	@Override
	protected SalaryType getType() {
		return SalaryType.SETTLE;
	}
	
	
	@Override
	protected ISQLContractSalaryCalculatorContext getSQLContractSalaryCalculatorContext ( Connection connection,
			Integer contract, Date startDate, Date endDate, Date issueDate, Date chargeDate) 
					throws SQLException, ExpressionException, SalaryException {
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( SQLConstants.CONTRACT + "." + ContractColumns.ID, contract);
		
		return new SQLContractSettleCalculatorContext(connection, startDate, endDate, endDate, chargeDate, criteria);
	}
	
	

}
