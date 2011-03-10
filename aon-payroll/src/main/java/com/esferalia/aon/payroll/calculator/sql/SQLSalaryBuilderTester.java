package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.EventListener;
import java.util.EventObject;

import com.esferalia.aon.master.sql.AbstractSQL;
import com.esferalia.aon.master.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.master.sql.SQLReader;
import com.esferalia.aon.master.sql.SQLReader.SalaryReader;
import com.esferalia.aon.salary.ISalary;

public class SQLSalaryBuilderTester extends  AbstractSQLSalaryBuilder {

	
	
	public static class UnExpectedValue extends Error{
		
		public UnExpectedValue(String message) {
			super(message);
		}
		
	}
	
	public static class UnExpectedSalary extends Error{
		
		public UnExpectedSalary(String message) {
			super(message);
		}
		
	}
	

	private double delta = 0.01;
	private SQLReader sqlReader ;
	
	public SQLSalaryBuilderTester(Connection connection) 
	throws SQLException {
		sqlReader= new SQLReader(connection);
	}
	
	
	public void setDelta(double delta) {
		this.delta = delta;
	}
	
	@Override
	public ISalary getSalary() {

		try {
			SalaryReader salaryReader = 
				sqlReader.newSalaryReader();

			salaryReader.setStartDate(salary.getStartDate());
			salaryReader.setEndDate(salary.getEndDate());
			salaryReader.setContract(salary.getContract());
			
			AbstractSQL.ISalary dbSalary = salaryReader.findSalary();
			if ( dbSalary == null ) {
				String msg = String.format("No salary found for '%s' [%s,%s]", 
						salary.getEmployeeDocument(),
						salary.getStartDate(),
						salary.getEndDate());
				throw new UnExpectedSalary(msg);
			}
			
			String msg = String.format("[%s]:%s", 
					salary.getEmployeeDocument(), 
					SalaryColumns.TOTAL_PAYMENT);
			testEquals(msg,
						dbSalary.getTotalPayment(),
						salary.getTotalPayment(),
						delta);
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	static public void testEquals(String message, double expected,
			double actual, double delta) {
		if (Double.compare(expected, actual) == 0)
			return;
		if (!(Math.abs(expected - actual) <= delta))
			unExpectedValue(message, new Double(expected), new Double(actual));
	}
	
	static private void unExpectedValue(String message, Object expected,
			Object actual) {
		throw new UnExpectedValue(format(message, expected, actual));
	}
	
	static String format(String message, Object expected, Object actual) {
		String formatted= "";
		if (message != null && !message.equals(""))
			formatted= message + " ";
		String expectedString= String.valueOf(expected);
		String actualString= String.valueOf(actual);
		if (expectedString.equals(actualString))
			return formatted + "expected: "
					+ formatClassAndValue(expected, expectedString)
					+ " but was: " + formatClassAndValue(actual, actualString);
		else
			return formatted + "expected:<" + expectedString + "> but was:<"
					+ actualString + ">";
	}
	
	private static String formatClassAndValue(Object value, String valueString) {
		String className= value == null ? "null" : value.getClass().getName();
		return className + "<" + valueString + ">";
	}

}
