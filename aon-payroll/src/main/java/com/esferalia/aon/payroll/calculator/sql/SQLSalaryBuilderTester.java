package com.esferalia.aon.payroll.calculator.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.sql.AbstractSQL;
import com.esferalia.aon.payroll.sql.SQLReader;
import com.esferalia.aon.payroll.sql.SQLReader.SalaryReader;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilderListener;

import static org.apache.commons.lang.StringEscapeUtils.escapeHtml;

public class SQLSalaryBuilderTester extends  AbstractSQLSalaryBuilder {

	
	private ISalaryBuilderListener listener;
	private static final String FORMAT = "[%s]: %s - %s";
	private static final String NO_SALARY_FORMAT  ="No hay n&oacute;mina para [%s]: %s - %s (%s...%s)";
	private static final String TOTAL_LIQUID  ="L&iacute;quido Total a Percibir";
	private static final String TOTAL_ENTERPRISE  ="Cuota de empresa";
	
	private boolean testTotalLiquid;
	private boolean testEnterpriseCost;
	
	private double delta = 0.9;
	private SQLReader sqlReader ;
	
	private int contractCount;
	private int salaryCount;
	private int rightTestedsalariesCount;
	
	private Date testEndDate = null; 
	private Date testStartDate = null; 

	AbstractSQL.ISalary dbSalary = null;
	
	public SQLSalaryBuilderTester(Connection connection) throws SQLException {
		sqlReader= new SQLReader(connection);
		testTotalLiquid = false;
		testEnterpriseCost = false;
		
	}
	
	public void setTestEndDate(Date testDate) {
		this.testEndDate = testDate;
	}
	
	public void setTestStartDate(Date testStartDate) {
		this.testStartDate = testStartDate;
	}
	
	public AbstractSQL.ISalary getSalaryDraft()  {
		return super.salary;
	}

	public AbstractSQL.ISalary getDBSalary()  {
		return dbSalary;
	}
	
	public boolean isTestTotalLiquid() {
		return testTotalLiquid;
	}


	public void setTestTotalLiquid(boolean testTotalPayment) {
		this.testTotalLiquid = testTotalPayment;
	}

	
	public void setTestEnterpriseCost(boolean testEnterpriseCost) {
		this.testEnterpriseCost = testEnterpriseCost;
	}
	
	public void setDelta(double delta) {
		this.delta = delta;
	}
	
	@Override
	public ISalary getSalary() {

		try {
			if (listener != null && listener.isDebugEnabled()) {
				String msg = String.format(FORMAT, 
						salary.getEmployeeDocument(),
						escapeHtml(salary.getEnterpriseName()),
						escapeHtml(salary.getEmployeeName())
						);
				//listener.onDebug(msg);
			}
			
			SalaryReader salaryReader = sqlReader.newSalaryReader();
			salaryReader.setStartDate(getStartDate());
			salaryReader.setEndDate(getEndDate());
			salaryReader.setContract(salary.getContract());
			salaryReader.setType(salary.getType());
			
			++contractCount;
			this.dbSalary = salaryReader.findSalary();
			if ( dbSalary == null ) {
				String msg = String.format(NO_SALARY_FORMAT, 
						salary.getEmployeeDocument(),
						escapeHtml(salary.getEnterpriseName()),
						escapeHtml(salary.getEmployeeName()),
						salary.getStartDate(),
						salary.getEndDate());
				onWarning(msg);
				return null;
			}
			++salaryCount;
			
			if ( testTotalLiquid ) {
				testEquals(TOTAL_LIQUID,dbSalary.getTotalLiquid(),salary.getTotalLiquid(),delta);
			}
			if ( testEnterpriseCost ) {
				testEquals(TOTAL_ENTERPRISE,dbSalary.getTotalEnterprise(),salary.getTotalEnterprise(),delta);
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
			unExpectedValue(message, expected, actual);		
	}
	
	@Override
	public void setListener(ISalaryBuilderListener listener) {
		this.listener = listener;		
	}
	
	
	
	// ------------------------------------------------------------------------
	
	private Date getEndDate() {
		return testEndDate == null ? salary.getEndDate() : testEndDate;
	}
	
	private Date getStartDate() {
		return testStartDate == null ? salary.getStartDate() : testStartDate;
	}

	public static class UnExpectedValue extends Error{
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		public UnExpectedValue(String message) {
			super(message);
		}
	}

	private static void unExpectedValue(String message, double expected,double actual) {
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

	private static String format(String message, double expected, double actual) {
			return String.format("%s diferente. En la n&oacute;mina '%.2f', en el borrador '%.2f'", 
					message, CommonUtil.round(expected), CommonUtil.round(actual));
	}
	
	protected static Short enum2short(Enum<?> type) {
		return type == null ? null : (short ) type.ordinal();
	}

}
