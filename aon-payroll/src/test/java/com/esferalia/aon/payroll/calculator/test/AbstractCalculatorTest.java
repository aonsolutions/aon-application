package com.esferalia.aon.payroll.calculator.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.sql.AbstractSQL.Salary;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLReader;
import com.esferalia.aon.salary.expression.ExpressionException;

public abstract class AbstractCalculatorTest {

	protected final static Logger LOGGER = 
			LoggerFactory.getLogger(ContractSalaryCalculatorTest.class);

	protected Connection connection;


	@Before
	public void setUp() throws Exception {
		String url = "jdbc:mysql://127.0.0.1:3306/payroll-esferalia-org?autoReconnect=true";
		String usr = "dbuser"; 
		String psw = "serubd2000";
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(url,usr ,psw );
	}

	@After
	public void tearDown() throws Exception {
		if ( connection != null ) {
			connection.close();
		}
	}


	public static void warn ( String format, Object ...args){
		LOGGER.warn(format, args);
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
	
	
	
}
