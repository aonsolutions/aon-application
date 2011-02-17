package com.esferalia.aon.payroll.calculator.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.SQLSalaryProxy;
import com.esferalia.aon.payroll.calculator.test.ContractSalaryCalculatorTestCase;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;

public class SalaryBuilderTester implements ISalaryBuilder {

	private static final String CGC_BASE 		= "cgc_base";
	private static final String CGP_BASE 		= "cgp_base";
	private static final String IRPF_BASE 		= "irpf_base";
	private static final String RAW_CGC_BASE 	= "raw_cgc_base";
	private static final String TOTAL_LIQUID 	= "total_liquid";
	private static final String TOTAL_PAYMENT 	= "total_payment";
	private static final String TOTAL_DEDUCTION = "total_deduction";
	
	private static final String SALARY_SQL = "SELECT *"
									+ " FROM salary"
									+ " WHERE contract = ? "
									+ " AND start_date = ? "
									+ " AND end_date = ? ";
	
	
	private Connection connection;
	
	private PreparedStatement salaryStmt;
	
	private Date startDate;
	private Date endDate;
	private Integer contract;
	
	
	private Map<String, Object> fields;
	
	
	private String employeeDocument;
	
	public SalaryBuilderTester(Connection connection) 
	throws SQLException{
		this.connection = connection;
		this.salaryStmt = 
			this.connection.prepareStatement(SALARY_SQL);
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
		this.contract = ( ( SQLSalaryProxy ) contract).getId();
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
	public void setIssueDate(Date issueDate) {
		// TODO Auto-generated method stub

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
		addField(CGC_BASE, cgcBase);
	}

	@Override
	public void setRawCgcBase(Double rawCgcBase) {
		addField(RAW_CGC_BASE, rawCgcBase);
	}


	@Override
	public void setCgpBase(Double professionalBase) {
		addField(CGP_BASE, professionalBase);
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
		addField(IRPF_BASE, irpfBase);
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
		addField(TOTAL_LIQUID, totalLiquid);
	}

	@Override
	public void setTotalPayment(Double totalPayment) {
		addField(TOTAL_PAYMENT, totalPayment);
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
	public void addPayment(PaymentType type, String concept, Double amount,
			String description, String expression) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addDeduction(DeductionType type, String concept, Double amount,
			String description, String expression) {
		// TODO Auto-generated method stub

	}
	
	
	public void test() throws SQLException {
		ResultSet rs = null ;
		try {
			salaryStmt.setInt(1, this.contract);
			salaryStmt.setDate(2, new java.sql.Date(startDate.getTime() ));
			salaryStmt.setDate(3, new java.sql.Date(endDate.getTime() ));
			rs = salaryStmt.executeQuery();
			if ( !rs.next() ){
				//fail();
				return;
			}
			
			assertDoubleField(TOTAL_PAYMENT, rs);
			assertDoubleField(IRPF_BASE, rs);
			assertDoubleField(RAW_CGC_BASE, rs); 

			//double sqlTotalLiquid = rs.getDouble("total_liquid");
			//assertEquals(sqlTotalLiquid, this.totalLiquid, 0.00);
		}
		catch ( AssertionError e ) {
			ContractSalaryCalculatorTestCase.error(e.getMessage() );			
		}
		finally {
			if ( rs != null ){
				rs.close();
			}
		}
	}
	
	private void assertField(String field, ResultSet rs  ) 
	throws SQLException {
		
		Object expected = rs.getObject(field);
		Object  actual = fields.get(field);
		String msg = 
			String.format("[%s]:%s", 
					employeeDocument, field );
		assertEquals(msg, expected, actual);
	}

	private void assertDoubleField(String field, ResultSet rs  ) 
	throws SQLException {
		
		Double expected = rs.getDouble(field);
		Double  actual = ( Double ) fields.get(field);
		String msg = 
			String.format("[%s]:%s", 
					employeeDocument, field );
		assertEquals(msg, expected, CommonUtil.round(actual), (double) 0.9);
	}

	private void addField(String field, Object value) {
		this.fields.put(field, value);
	}

	
	
}
