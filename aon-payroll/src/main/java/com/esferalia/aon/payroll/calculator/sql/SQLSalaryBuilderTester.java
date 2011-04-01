package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

import com.esferalia.aon.payroll.sql.AbstractSQL;
import com.esferalia.aon.payroll.sql.SQLReader;
import com.esferalia.aon.payroll.sql.SQLReader.SalaryReader;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class SQLSalaryBuilderTester extends  AbstractSQLSalaryBuilder {

	
	private ISalaryBuilderListener listener;
	private static final String FORMAT = "[%s]: %s - %s";
	private static final String NO_SALARY_FORMAT  ="No hay nómina que calcular para: '%s' [%s,%s]";
	private static final String TOTAL_PAYMENT  ="[Total Devengado]";
	private static final String BASE_IRPF = "[Base I.R.P.F.]";
	private static final String BASE_CGC = "[Base C.G.C.]";
	private static final String EMPTY = "";
	private static final String SPACE = " ";
	private static final String LESSTHAN = "<";
	private static final String GREATHERTHAN = ">";
	private static final String WAITED = "Esperado: ";
	private static final String FOUND = " pero se encontró: ";
	
	private boolean testTotalPayment;
	private boolean testBaseIRPF;
	private boolean testBaseCGC;
	
	private double delta = 0.9;
	private SQLReader sqlReader ;
	
	private int contractCount;
	private int salaryCount;
	private int rightTestedsalariesCount;

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
			
			SalaryReader salaryReader = sqlReader.newSalaryReader();
			salaryReader.setStartDate(salary.getStartDate());
			salaryReader.setEndDate(salary.getEndDate());
			salaryReader.setContract(salary.getContract());
			salaryReader.setType(enum2short(SalaryType.SALARY));
			
			++contractCount;
			AbstractSQL.ISalary dbSalary = salaryReader.findSalary();
			if ( dbSalary == null ) {
				String msg = String.format(NO_SALARY_FORMAT, 
						salary.getEmployeeDocument(),
						salary.getStartDate(),
						salary.getEndDate());
				onWarning(msg);
				return null;
			}
			++salaryCount;
			if (testTotalPayment) {
				testEquals(TOTAL_PAYMENT,dbSalary.getTotalPayment(),salary.getTotalPayment(),delta);
			}
			if (testBaseIRPF) {
				testEquals( BASE_IRPF,dbSalary.getIrpfBase(),salary.getIrpfBase(),delta);
			}
			if (testBaseCGC) {
				testEquals(BASE_CGC,dbSalary.getRawCgcBase(),salary.getRawCgcBase(),delta);
			}
			++rightTestedsalariesCount;
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
	public int getRightTestedsalariesCount() {
		return rightTestedsalariesCount;
	}

	public void testEquals(String message, double expected, double actual, double delta) {
		if (Double.compare(expected, actual) == 0)
			return;
		if (!(Math.abs(expected - actual) <= delta))
			unExpectedValue(message, new Double(expected), new Double(actual));		
	}
	
	@Override
	public void setListener(ISalaryBuilderListener listener) {
		this.listener = listener;		
	}

	public static class UnExpectedValue extends Error{
		
		private static final long serialVersionUID = -3256215858427420045L;

		public UnExpectedValue(String message) {
			super(message);
		}
	}

	private static void unExpectedValue(String message, Object expected,Object actual) {
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

	private static String format(String message, Object expected, Object actual) {
		StringBuffer formatted = new StringBuffer(EMPTY);
		formatted.append(StringUtils.isEmpty(message)?EMPTY:message);
		formatted.append(StringUtils.isEmpty(message)?EMPTY:SPACE);
		String expectedString= String.valueOf(expected);
		String actualString= String.valueOf(actual);
		if (expectedString.equals(actualString)) {
			formatted.append(WAITED);
			formatted.append(formatClassAndValue(expected, expectedString));
			formatted.append(FOUND);
			formatted.append(formatClassAndValue(actual, actualString));
		} else {
			formatted.append(WAITED);
			formatted.append(LESSTHAN);
			formatted.append(expectedString);
			formatted.append(GREATHERTHAN);
			formatted.append(FOUND);
			formatted.append(LESSTHAN);
			formatted.append(actualString);
			formatted.append(GREATHERTHAN);
		}
		return formatted.toString(); 
	}
	
	private static String formatClassAndValue(Object value, String valueString) {
		String className= value == null ? "null" : value.getClass().getName();
		return className + LESSTHAN + valueString + GREATHERTHAN;
	}

	protected static Short enum2short(Enum<?> type) {
		return type == null ? null : (short ) type.ordinal();
	}

}
