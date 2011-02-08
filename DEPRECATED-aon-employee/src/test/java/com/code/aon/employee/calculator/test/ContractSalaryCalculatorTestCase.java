/**
 * 
 */
package com.code.aon.employee.calculator.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.lang.time.DateUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.employee.calculator.ContractSalaryCalculator;
import com.code.aon.employee.calculator.IContractSalaryCalculatorContext;
import com.code.aon.employee.calculator.sql.SQLContractSalaryCalculatorContext;
import com.code.aon.employee.calculator.sql.SQLSalaryBuilder;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;

/**
 * @author rtrepiana
 *
 */
public class ContractSalaryCalculatorTestCase {
	
	final static Logger LOGGER = 
		LoggerFactory.getLogger(ContractSalaryCalculatorTestCase.class);
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	public static void debug ( String format, Object ...args){
		LOGGER.debug(format, args);
	}
	public static void info ( String format, Object ...args){
		LOGGER.info(format, args);
	}
	public static void error ( String format, Object ...args){
		LOGGER.error(format, args);
	}
	
	private Connection connection;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		String url = "jdbc:mysql://localhost:3306/payroll-esferalia-org?autoReconnect=true";
		String usr = "dbuser"; 
		String psw = "serubd2000";
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(url,usr ,psw );
}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		connection.close();
	}

	/**
	 * Test method for {@link com.code.aon.employee.calculator.ContractSalaryCalculator#calculate(com.esferalia.aon.salary.calculator.ISalaryCalculatorContext)}.
	 */
	@Test
	public void testCalculate() throws SQLException, ExpressionException, SalaryException {
		
		SalaryBuilderTester salaryBuilderTester = 
			new SalaryBuilderTester(connection);
		
		ContractSalaryCalculator calculator = 
			new ContractSalaryCalculator();
		calculator.setSalaryBuilder(salaryBuilderTester);
		
		Date startAndEndDate [] = getStartAndEndDate();
		
		Date startDate = startAndEndDate[0];
		Date endDate = startAndEndDate[1];
		
		info("testCalculate {}:{}",startDate, endDate);
		
		Criteria criteria = new Criteria();
		//criteria.addEqualExpression("person_registry.document", "X2835073R");
		
		SQLContractSalaryCalculatorContext sqlCtx = 
			new SQLContractSalaryCalculatorContext(connection, 
					startDate, 
					endDate,
					Calendar.getInstance().getTime(),
					criteria );
		
		for ( int i = 0;  sqlCtx.next() ; i++ ) {
			try {
				calculator.calculate(sqlCtx);
				debug("{} [{}] {}, {} ", 
						i,
						sqlCtx.getEmployeeDocument(),
						sqlCtx.getEnterpriseName(),
						sqlCtx.getEmployeeName());
				salaryBuilderTester.test();
			}catch ( SalaryException e ) {
				error("{} [{}] {}, {} : {}",
						i,
						sqlCtx.getEmployeeDocument(),
						sqlCtx.getEnterpriseName(),
						sqlCtx.getEmployeeName(),
						e.getLocalizedMessage());
			}
	
		}
	}
	
	
	private Date [] getStartAndEndDate() 
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
			
			Date startAndEndDates[]={
				rs.getDate("start_date"),	
				rs.getDate("end_date")	
			};
			return startAndEndDates;	
		} finally {
			if ( rs != null )
				rs.close();
			if ( stmt != null )
				stmt.close();
		} 
	}

}
