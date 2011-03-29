/**
 * 
 */
package com.esferalia.aon.payroll.calculator.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilder;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.CustomerColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;

/**
 * @author rtrepiana
 *
 */
public class ContractSalaryCalculatorTest {
	
	final static Logger LOGGER = 
		LoggerFactory.getLogger(ContractSalaryCalculatorTest.class);
	
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
	 * Test method for {@link com.esferalia.aon.payroll.calculator.ContractSalaryCalculator#calculate(com.esferalia.aon.salary.calculator.ISalaryCalculatorContext)}.
	 * @throws com.code.aon.ql.util.ExpressionException 
	 */
	@Test
	public void testCalculate() throws SQLException, ExpressionException, SalaryException, ParseException, com.code.aon.ql.util.ExpressionException {
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
