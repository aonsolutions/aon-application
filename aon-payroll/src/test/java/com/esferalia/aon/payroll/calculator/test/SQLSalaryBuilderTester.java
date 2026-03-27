package com.esferalia.aon.payroll.calculator.test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryProxy;
import com.esferalia.aon.salary.AbstractSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;



public class SQLSalaryBuilderTester<T extends ISalary> extends  AbstractSalaryBuilder<T> {

	private static final String SALARY_SQL = "SELECT *"
									+ " FROM " + SQLConstants.SALARY
									+ " WHERE " + SalaryColumns.CONTRACT + " = ? "
									+ " AND " + SalaryColumns.START_DATE + "  = ? "
									+ " AND " + SalaryColumns.END_DATE + " = ? "
									+ " AND " + SalaryColumns.TYPE + " =  ? " ;
	
	
	protected Connection connection;
	
	protected PreparedStatement salaryStmt;
	
	protected Date startDate;
	protected Date endDate;
	protected Date issueDate;
	protected Integer contract;
	
	
	private Map<String, Object> fields;
	
	
	private String employeeDocument;
	
	public SQLSalaryBuilderTester(Connection connection) 
	throws SQLException{
		this(connection, SalaryType.SALARY);
	}
	
	public SQLSalaryBuilderTester(Connection connection, SalaryType salaryType) 
	throws SQLException{	

		this.connection = connection;
		this.salaryStmt = 
			this.connection.prepareStatement(SALARY_SQL);
		this.salaryStmt.setInt(4, salaryType.ordinal());
		this.fields = new HashMap<String, Object>();
	}
	
	@Override
	public void setContract(Object contract) {
		this.contract = ( ( SQLSalaryProxy ) contract).getContractId();
	}

	@Override
	public void setCgcBase(Double cgcBase) {
		addField(SalaryColumns.CGC_BASE, cgcBase);
	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		addField(SalaryColumns.RAW_CGC_BASE, rawCgcBase);
	}


	@Override
	public void setCgpBase(Double professionalBase) {
		addField(SalaryColumns.CGP_BASE, professionalBase);
	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		addField(SalaryColumns.IRPF_BASE, irpfBase);
	}
	@Override
	public void setTotalLiquid(Double totalLiquid) {
		addField(SalaryColumns.TOTAL_LIQUID, totalLiquid);
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		addField(SalaryColumns.TOTAL_PAYMENT, totalPayment);
	}

	
	public void test() throws SQLException {
		ResultSet rs = null ;
		try {
			salaryStmt.setInt(1, this.contract);
			salaryStmt.setDate(2, new java.sql.Date(startDate.getTime() ));
			salaryStmt.setDate(3, new java.sql.Date(endDate.getTime() ));
			rs = salaryStmt.executeQuery();
			test(rs);
		}
		finally {
			if ( rs != null ){
				rs.close();
			}
		}
	}
	
	protected void test(ResultSet rs ) throws SQLException {
		if (  !rs.next() ){
			return;
		}
		assertDoubleField(SalaryColumns.TOTAL_PAYMENT, rs);
		assertDoubleField(SalaryColumns.IRPF_BASE, rs);
		//assertDoubleField(SalaryColumns.RAW_CGC_BASE, rs); 
		assertDoubleField(SalaryColumns.TOTAL_LIQUID, rs);
		//assertDoubleFieldIfNotZero(SalaryColumns.TOTAL_ENTERPRISE, rs); 
	}

	protected void assertDoubleField(String field, ResultSet rs  ) 
	throws SQLException {
		
		Double expected = rs.getDouble(field);
		Double  actual = ( Double ) fields.get(field);
		String msg = 
			String.format("[%s]:%s", 
					employeeDocument, field );
		assertEquals(msg, expected, CommonUtil.round(actual), (double) 0.9);
	}

	protected void assertDoubleFieldIfNotZero(String field, ResultSet rs  ) 
	throws SQLException {
		
		Double expected = rs.getDouble(field);
		if ( expected == 0.00 ) 
			return ;
		
		Double  actual = ( Double ) fields.get(field);
		String msg = 
			String.format("[%s]:%s", 
					employeeDocument, field );
		assertEquals(msg, expected, CommonUtil.round(actual), (double) 0.9);
	}

	private void addField(String field, Object value) {
		this.fields.put(field, value);
	}

	@Override
	public void setListener(ISalaryBuilderListener listener) {
		throw new UnsupportedOperationException("No implementado!");
	}

	
	
}
