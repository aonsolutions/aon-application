package com.esferalia.aon.payroll.calculator.test;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCalculatorTest {

	protected final static Logger LOGGER = 
			LoggerFactory.getLogger(ContractSalaryCalculatorTest.class);

	protected Connection connection;


	@Before
	public void setUp() throws Exception {
		String url = "jdbc:mysql://127.0.0.1:3306/payroll-esferalia-org?autoReconnect=true";
		//String url = "jdbc:mysql://localhost:3306/aon-lanfisa-esferalia-net?autoReconnect=true";
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
