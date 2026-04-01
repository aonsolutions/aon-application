package com.esferalia.aon.payroll.calculator.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.salary.AbstractSalaryBuilder;
import com.esferalia.aon.salary.ISalary;

public abstract class AbstractSQLSalaryBuilderTester<T extends ISalary> extends AbstractSalaryBuilder<T> {
	
	private String enterpriseDocument;
	private String employeeDocument;
	private Map<String, Double> values;
	
	public AbstractSQLSalaryBuilderTester() {
		this.values = new HashMap<String, Double>();
	}
	
	
	public String getEmployeeDocument() {
		return employeeDocument;
	}
	
	@Override
	public void setEmployeeDocument(String employeeDocument) {
		this.employeeDocument = employeeDocument;
	}
	
	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
	}
	
	@Override
	public void setTotalLiquid(Double totalLiquid) {
		values.put(SalaryColumns.TOTAL_LIQUID, totalLiquid);
	}
	
	@Override
	public void setTotalPayment(Double totalPayment) {
		values.put(SalaryColumns.TOTAL_PAYMENT, totalPayment);
	}
	
	@Override
	public void setTotalSS(
			Double socialSecurityContributions) {
		values.put(SalaryColumns.SOCIAL_SECURITY_CONTRIBUTIONS, socialSecurityContributions);
	}
	
	protected Double getDouble(String field) {
		return values.get(field);
	}
	
	protected void test() throws SQLException {
		for (String field : values.keySet()) {
			assertDoubleField(field);
		}
	}
	
	protected void test(String ...fields) throws SQLException {
		for (String field : fields) {
			assertDoubleField(field);
		}
	}

	protected void assertDoubleField(String field ) 
	throws SQLException {
		
		Double expected = getOtherDouble(field);
		Double  actual = ( Double ) values.get(field);
		String msg = 
			String.format("[%s][%s]: %s", 
					enterpriseDocument, employeeDocument, field  );
		assertEquals(expected, CommonUtil.round(actual), (double) 0.9, msg);
	}
	
	abstract protected Double getOtherDouble(String field) throws SQLException;
	
	
}
