package com.esferalia.aon.payroll.calculator.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Date;

import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;

public abstract class AbstractSalaryCalculatorTest extends AbstractCalculatorTest{
	
	
	
	protected static class ResultSetSalaryBuilderTester extends AbstractSQLSalaryBuilderTester {
		
		private ResultSet rs;
		
		public ResultSetSalaryBuilderTester(ResultSet rs) {
			super();
			this.rs = rs;
		}
		
		
		@Override
		protected Double getOtherDouble(String field) throws SQLException{
			return rs.getDouble(field);
		}
		
	}

	public void test() throws SQLException, ExpressionException, ParseException {
		
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String sql = 
				"SELECT salary.*"
				+", (SELECT sum(amount)"
				+"  FROM salary_payment"
				+"	WHERE salary = salary.id) AS amounts"
				+" FROM salary "
				;
			
			Criteria criteria = getCriteria();
			
			sql = CriteriaUtilities.toSQLString(criteria, sql);
			
			stmt = connection.createStatement();
			
			rs = stmt.executeQuery(sql);
			
			ContractSalaryCalculator calculator = 
					new ContractSalaryCalculator();
			ResultSetSalaryBuilderTester tester = 
				new ResultSetSalaryBuilderTester(rs);
			calculator.setSalaryBuilder(tester);
			
			int count = 0 ; 
			int success = 0;
			int warnings = 0;
			while ( rs.next() ) {
				count++;
				try {

					calculate(rs, calculator);
					
					tester.test(getFields());
					
					
					success++;
				} catch( AssertionError e ) {
					error(e.getMessage() + ", " 
							+ rs.getDate(SalaryColumns.START_DATE) + ".." 
							+ rs.getDate(SalaryColumns.END_DATE) );
				} catch (Exception e) {
					error(e.getMessage() + ", " 
							+ rs.getDate(SalaryColumns.START_DATE) + ".." 
							+ rs.getDate(SalaryColumns.END_DATE) );
				} 
			}
			info("delays {} : success {} , warnings {}",count, success, warnings );
			
		}
		finally {
			if ( rs != null ) {
				rs.close();
			}
			if ( stmt != null ) {
				stmt.close();
			}
		}
		
	}
	
	protected String [] getFields() {
		return new String [] {SalaryColumns.TOTAL_PAYMENT, SalaryColumns.TOTAL_LIQUID };
	}
	
	
	protected Criteria getCriteria() {
		Criteria criteria = new Criteria();
		
		criteria.addEqualExpression(SalaryColumns.TYPE, 
				getType().ordinal() );
		
		criteria.addGreaterThanOrEqualExpression(SalaryColumns.START_DATE, 
				"2010/07/01");
		
		return criteria;
	}
	
	protected SalaryType getType() {
		return null;
	}

	

	protected void calculate ( ResultSet rs, ContractSalaryCalculator calculator ) 
	throws SQLException, ExpressionException, SalaryException {
		
		int contract = rs.getInt(SalaryColumns.CONTRACT);
		Date startDate = rs.getDate(SalaryColumns.START_DATE);
		Date endDate = rs.getDate(SalaryColumns.END_DATE);
		Date chargeDate = rs.getDate(SalaryColumns.CHARGE_DATE);
		

		
		ISQLContractSalaryCalculatorContext ctx = 
			getSQLContractSalaryCalculatorContext(connection, contract, startDate, endDate, endDate, chargeDate);
		
		while ( ctx.next() ) {
			calculator.calculate( ctx);
		}
		
	}
	
	protected ISQLContractSalaryCalculatorContext getSQLContractSalaryCalculatorContext ( Connection connection,
			Integer contract, Date startDate, Date endDate, Date issueDate, Date chargeDate ) 
					throws SQLException, ExpressionException, SalaryException{
		return null;
	}

	
}
