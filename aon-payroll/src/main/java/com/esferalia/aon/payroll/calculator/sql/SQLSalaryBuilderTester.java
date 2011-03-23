package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.SQLException;

import com.esferalia.aon.payroll.sql.AbstractSQL;
import com.esferalia.aon.payroll.sql.SQLReader;
import com.esferalia.aon.payroll.sql.SQLReader.SalaryReader;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilderListener;

public class SQLSalaryBuilderTester extends  AbstractSQLSalaryBuilder {

	
	private ISalaryBuilderListener listener;
	private static final String FORMAT = "[%s]: %s - %s";
	private static final String NO_SALARY_FORMAT  ="No hay nómina que calcular para: '%s' [%s,%s]";
	private static final String TOTAL_PAYMENT  ="[Total Devengado]";
	private static final Object BASE_IRPF = "[Base I.R.P.F.]";
	private static final Object BASE_CGC = "[Base C.G.C.]";
	
	private boolean testTotalPayment;
	private boolean testBaseIRPF;
	private boolean testBaseCGC;
	
	public static class UnExpectedValue extends Error{
		
		private static final long serialVersionUID = -3256215858427420045L;

		public UnExpectedValue(String message) {
			super(message);
		}
		
	}
	
	public static class UnExpectedSalary extends Error{
		
		private static final long serialVersionUID = 1347570769647418339L;

		public UnExpectedSalary(String message) {
			super(message);
		}
		
	}
	

	private double delta = 0.01;
	private SQLReader sqlReader ;
	
	private int contractCount;
	private int salaryCount;
	
	public SQLSalaryBuilderTester(Connection connection) throws SQLException {
		sqlReader= new SQLReader(connection);
		testTotalPayment = true;
		testBaseIRPF = true;
		testBaseCGC = true;
		
	}
	
	
	public boolean isTestTotalPayment() {
		return testTotalPayment;
	}


	public void setTestTotalPayment(boolean testTotalPayment) {
		this.testTotalPayment = testTotalPayment;
	}


	public boolean isTestBaseIRPF() {
		return testBaseIRPF;
	}


	public void setTestBaseIRPF(boolean testBaseIRPF) {
		this.testBaseIRPF = testBaseIRPF;
	}


	public boolean isTestBaseCGC() {
		return testBaseCGC;
	}


	public void setTestBaseCGC(boolean testBaseCGC) {
		this.testBaseCGC = testBaseCGC;
	}


	public void setDelta(double delta) {
		this.delta = delta;
	}
	
	@Override
	public ISalary getSalary() {

		try {
			if (listener.isDebugEnabled()) {
				String msg = String.format(FORMAT, 
						salary.getEmployeeDocument(),
						salary.getEnterpriseName(),
						salary.getEmployeeName());
				listener.onDebug(msg);
			}
			
			SalaryReader salaryReader = 
				sqlReader.newSalaryReader();

			salaryReader.setStartDate(salary.getStartDate());
			salaryReader.setEndDate(salary.getEndDate());
			salaryReader.setContract(salary.getContract());
			++contractCount;
			AbstractSQL.ISalary dbSalary = salaryReader.findSalary();
			if ( dbSalary == null ) {
				String msg = String.format(NO_SALARY_FORMAT, 
						salary.getEmployeeDocument(),
						salary.getStartDate(),
						salary.getEndDate());
				onWarning(msg);
				// throw new UnExpectedSalary(msg);
				return null;
			}
			++salaryCount;
			
			String msg;			
			if (testTotalPayment) {
				msg = String.format("[%s]:%s",salary.getEmployeeDocument(),TOTAL_PAYMENT);
				testEquals(msg,dbSalary.getTotalPayment(),salary.getTotalPayment(),delta);
			}
			if (testBaseIRPF) {
				msg = String.format("[%s]:%s",salary.getEmployeeDocument(),BASE_IRPF);
				testEquals(msg,dbSalary.getIrpfBase(),salary.getIrpfBase(),delta);
			}
			if (testBaseCGC) {
				msg = String.format("[%s]:%s",salary.getEmployeeDocument(),BASE_CGC);
				testEquals(msg,dbSalary.getCgcBase(),salary.getCgcBase(),delta);
			}
		} catch (SQLException e) {
			onError(e.getLocalizedMessage());
		}
		
		return null;
	}
	
	
	public int getContractCount() {
		return contractCount;
	}


	public int getSalaryCount() {
		return salaryCount;
	}


	public void testEquals(String message, double expected,
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
	
	private void onError(String msg) {
		if (listener != null) {
			listener.onError(msg);
		}
	}
	private void onWarning(String msg) {
		if (listener != null) {
			listener.onWarning(msg);
		}
	}

	static String format(String message, Object expected, Object actual) {
		String formatted= "";
		if (message != null && !message.equals(""))
			formatted= message + " ";
		String expectedString= String.valueOf(expected);
		String actualString= String.valueOf(actual);
		if (expectedString.equals(actualString))
			return formatted + "Esperado: "
					+ formatClassAndValue(expected, expectedString)
					+ " pero se encontró: " + formatClassAndValue(actual, actualString);
		else
			return formatted + "Esperado: <" + expectedString + "> pero se encontró: <" + actualString + ">";
	}
	
	private static String formatClassAndValue(Object value, String valueString) {
		String className= value == null ? "null" : value.getClass().getName();
		return className + "<" + valueString + ">";
	}


	@Override
	public void setListener(ISalaryBuilderListener listener) {
		this.listener = listener;		
	}

}
