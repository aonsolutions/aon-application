/**
 * 
 */
package com.esferalia.aon.payroll.calculator.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.junit.jupiter.api.Test;

import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.CustomerColumns;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;

/**
 * @author rtrepiana
 *
 */
public class ContractSalaryCalculatorTest extends AbstractCalculatorTest{
	
	
	/**
	 * Test method for {@link com.esferalia.aon.payroll.calculator.ContractSalaryCalculator#calculate(com.esferalia.aon.salary.calculator.ISalaryCalculatorContext)}.
	 * @throws com.code.aon.ql.util.ExpressionException 
	 */
	public void testSalary() throws SQLException, ExpressionException, SalaryException, ParseException, com.code.aon.ql.util.ExpressionException {

		SQLSalaryBuilderTester<ISalary> salaryBuilderTester = 
			new SQLSalaryBuilderTester<ISalary>(connection);
		
		ContractSalaryCalculator<ISalary> calculator = 
			new ContractSalaryCalculator<ISalary>();
		calculator.setSalaryBuilder(salaryBuilderTester);
		
		
		SimpleDateFormat dateFormat = 
			new SimpleDateFormat("dd/MM/yyyy");
		Date start = dateFormat.parse("01/01/2011");
		Date end = dateFormat.parse("31/01/2011");
		
		Period period = new Period(start, end); //getStartAndEndDate();
		
		info("testSalary {}:{}",period.getStart(), period.getEnd());
		
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(SQLConstants.CUSTOMER + "." + CustomerColumns.STATUS, 
				CustomerStatus.ACTIVE );
		
		
		//criteria.addEqualExpression("person_registry.document", "51367458V");
		
		
		SQLContractSalaryCalculatorContext sqlCtx = 
			new SQLContractSalaryCalculatorContext(connection, 
					period.getStart(), 
					period.getEnd(),
					period.getEnd(), //Calendar.getInstance().getTime(),
					criteria, 
					ISQLContractSalaryCalculatorContext.OLDER);
		
		
		int count ;
		for ( count = 0;  sqlCtx.next() ; count++ ) {
			try {
				calculator.calculate(sqlCtx);
				debug("{} [{}] {}, {} ", 
						count,
						sqlCtx.getEmployeeDocument(),
						sqlCtx.getEnterpriseName(),
						sqlCtx.getEmployeeName());
				salaryBuilderTester.test();
			}
			catch ( NoSuchSalaryError err ){
				
			}
			catch( AssertionError err ) {
				error(err.getMessage());
			}
		}
		info("salarys {} ",count);
	}
	
	private Period getStartAndEndDate() 
	throws SQLException {
		
		ResultSet rs = null;
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			rs = 
				stmt.executeQuery("SELECT" +
							" start_date"+
							", end_date"+
							", count(*)"+
							" FROM salary"+
							" GROUP BY start_date, end_date"+
							" ORDER BY 3 DESC LIMIT 10");
			Random random = new Random();
			int end = 0; //random.nextInt(9);
			int start = 0;
			do {
				rs.next();
			} while ( start++ < end );
			
			return new Period(rs.getDate("start_date"), rs.getDate("end_date"));	
		} finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		} 
	}

}
