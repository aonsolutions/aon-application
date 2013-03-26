package com.esferalia.aon.payroll.calculator.test;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractDelayCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

public class ContractDelayCalculatorTest extends AbstractSalaryCalculatorTest{
	
	
	@Override
	protected SalaryType getType() {
		return SalaryType.DELAY;
	}
	
	
	@Override
	protected Criteria getCriteria() {
		Criteria criteria =  
				super.getCriteria();
//		criteria.addEqualExpression(SalaryColumns.EMPLOYEE_DOCUMENT, 
//				"00797949X");
		return criteria;
	}

	@Override
	protected String[] getFields() {
		return new String [] { SalaryColumns.TOTAL_PAYMENT, 
				SalaryColumns.SOCIAL_SECURITY_CONTRIBUTIONS };
	}
	

	@Override
	protected ISQLContractSalaryCalculatorContext getSQLContractSalaryCalculatorContext ( Connection connection,
			Integer contract, Date startDate, Date endDate, Date issueDate, Date chargeDate) 
					throws SQLException, ExpressionException, SalaryException {
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( SQLConstants.CONTRACT + "." + ContractColumns.ID, contract);

		return new SQLContractDelayCalculatorContext(connection, startDate, endDate, endDate, endDate, criteria){
			
			@Override
			public Date getIrpfDate() {
				Date chargeDate = getChargeDate();
				return add(chargeDate, Calendar.DATE, 1 );
			}

		};
	}
	
	private static Date add(Date date, int field, int amount ) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, 1);
		return calendar.getTime();
	}

}
