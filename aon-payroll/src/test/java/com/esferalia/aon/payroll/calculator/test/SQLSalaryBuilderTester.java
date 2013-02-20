package com.esferalia.aon.payroll.calculator.test;

import static org.junit.Assert.*;

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
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.ISalaryBuilderListener;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;



public class SQLSalaryBuilderTester implements ISalaryBuilder {

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
	public ISalary getSalary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createNewSalary() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContract(Object contract) {
		this.contract = ( ( SQLSalaryProxy ) contract).getContractId();
	}

	@Override
	public void setCcc(String ccc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnterpriseName(String enterpriseName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnterpriseAddress(String enterpriseAddress) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEnterpriseDocument(String enterpriseDocument) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRegistration(Integer registration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEmployeeName(String employeeName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEmployeeDocument(String employeeDocument) {
		this.employeeDocument = employeeDocument;
	}

	@Override
	public void setSocialSecurityNumber(String socialSecurityNumber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCategory(String category) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setQuoteGroup(String quoteGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSeniorityDate(Date seniorityDate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setType(SalaryType type) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void setChargeDate(Date issueDate) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void setIssueDate(Date issueDate) {
		// TODO Auto-generated method stub
		this.issueDate = issueDate;
	}

	@Override
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public void setTimeUnits(Integer timeUnits) {
		// TODO Auto-generated method stub

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
	public void setRemuneration(Double remuneration) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setProExtBase(Double extraPayProration) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setIrpfBase(Double irpfBase) {
		addField(SalaryColumns.IRPF_BASE, irpfBase);
	}

	@Override
	public void setNonHExtraBase(Double overtimeBase) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setItBase(Double itBase) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHExtraBase(Double hExtraBase) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTotalLiquid(Double totalLiquid) {
		addField(SalaryColumns.TOTAL_LIQUID, totalLiquid);
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		addField(SalaryColumns.TOTAL_PAYMENT, totalPayment);
	}

	@Override
	public void setTotalDeduction(Double totalDeduction) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSocialSecurityContributions(
			Double socialSecurityContributions) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTotalEnterprise(Double totalEnterprise) {
		addField(SalaryColumns.TOTAL_ENTERPRISE, totalEnterprise);
	}
	
	@Override
	public void addBonus(String concept, Double amount, String description) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addCost(DeductionType type, String concept, Double amount,
			String description) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addEmbargo(Integer embargo, Double amount, String description) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addPayment(PaymentType type, String concept, Double amount,
			String description, IPayment payment,
			Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDeduction(DeductionType type, String concept, Double amount,
			String description, IDeduction deduction, Map<String, ITimedVariable<?>> context) {
		// TODO Auto-generated method stub

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
