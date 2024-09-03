package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.GEROA;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;


public class SQLGeroaTestCase extends AbstractSQLTestCase {
	
	private static final String GEROA_PAYMENT = "BASE_CGC * PORCENTAGE_GEROA/100.00";
	private static final String GEROA_DEDUCTION = "(BASE_CGC - BASE_GEROA)*PORCENTAGE_GEROA/100.00";
	private static final double DELTA = 0.000001;
	
	
	private static class SuccessException extends Exception {
		
	}

	@Test
	public void testGeroaConcept() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		cleanSystemDeductions(aonContext);
		
		PaymentConceptRecord geroaConcept = addConcept(aonContext, GEROA);
	
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				geroaConcept, 
				PaymentType.CRA_0033,
				"/*read-only*//**/",
				GEROA_PAYMENT, 
				null, 
				SalaryType.SALARY);
		addSSRegimeDeduction(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				DeductionType.OTHER, 
				GEROA_DEDUCTION);
		addSSRegimeCost(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				"GEROA_E",
				DeductionType.OTHER, 
				GEROA_DEDUCTION);
		

		ContractRecord contract = newContract(aonContext,  
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put("PORCENTAGE_GEROA", "2.00");
					}
				}, new String[] { 
						"( P_1 + P_2 )* 0.10 ",
						"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
						"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
						}
				, new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05"
						
				}, null);
		
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		try {
			for ( SalaryPayment s : salary.getSalaryPayments() ) {
				if ( s.getExpression().equals("/*read-only*//**/"))  {
					assertEquals(s.getQuote(), 1750.00*1.10*2.00/100.00, DELTA );
					throw new SuccessException();
				}
			}
			fail("No GEROA payment!!!");
		} catch ( SuccessException e ) {
			
		}
		
		try {
			for ( SalaryDeduction d : salary.getSalaryDeductions() ) { 
				if ( d.getExpression().equals(GEROA_DEDUCTION))  {
					assertEquals(d.getAmount(), 1750.00*1.10*2.00/100.00, DELTA );
					throw new SuccessException();
				}
			}
			fail("No GEROA deduction!!!");
		} catch ( SuccessException e ) {
			
		}
		
		try {
			for ( SalaryCost d : salary.getSalaryCosts() ) { 
				if ( d.getName().equals("GEROA_E"))  {
					assertEquals(d.getAmount(), 1750.00*1.10*2.00/100.00, DELTA );
					throw new SuccessException();
				}
			}
			fail("No GEROA cost!!!");
		} catch ( SuccessException e ) {
			
		}

		assertEquals(1750.00 * 1.10,salary.getTotalPayment(), DELTA);
		assertEquals(1750.00 * 1.10 + 1750.00 * 1.10* 2.00/100.00,salary.getCommonBase(), DELTA);
		assertEquals(salary.getCommonBase() * 0.15 + 1750.00 * 1.10 * 2.00/100.00,salary.getTotalDeduction(), DELTA);
		assertEquals(1750.00 * 1.10 * 2.00/100.00,salary.getTotalEnterprise(), DELTA);
		
		
	}



}
