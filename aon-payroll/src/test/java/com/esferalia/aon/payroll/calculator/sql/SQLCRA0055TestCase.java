package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.OCCUPATIONAL_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfYear;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;

import static org.junit.jupiter.api.Assertions.*;

public class SQLCRA0055TestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.019;
	
	@Test
	public void testCRA0055NoDay() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemPayments(aonContext);
		
		Date firstDayOfYear = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(aonContext,  
				firstDayOfYear,
				new HashMap<String,String>(){
					{
						put("DIAS_MES", "30"); // Monthly quote
					}
				}
				, new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
				}
				, new String[] {
				}, 
				null);
		//@formatter:on
		
		addPayment(aonContext, contract, contract.getStartDate(), contract.getEndDate(), "MEJORA", "BASE_REGULADORA * 0.60", "_P", null, PaymentType.CRA_0055, SalaryType.SALARY);
		
		addPrestIts(aonContext, contract);
		
		Date itDay = add(firstDayOfYear, Calendar.DAY_OF_MONTH, 9);
		
		addIT(aonContext, 
				contract, 
				LeaveType.COMMON_DISEASE, 
				itDay,
				itDay, 
				null);
		

		Date startDate = getFirstDayOfMonth(itDay);
		Date endDate = getLastDayOfMonth(startDate);
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
		
		Salary salary = new SmartContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		for ( SalaryPayment p: salary.getSalaryPayments())
			System.out.println(p.getExpression() + " = " + p.getAmount() );
				
		org.junit.assertEquals( 1000.00, salary.getCommonBase(), DELTA);
		org.junit.assertEquals( 1000.00, salary.getTotalPayment(), DELTA);
		
	}

	

	// ----------------------------------------------------------------------------------


	private static void addPrestIts(AONContext aonContext, ContractRecord contract) {
		PaymentConceptRecord prestIT = addConcept(aonContext, "PREST_IT");
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.60 * %s_4_15",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.60 * %s_16_20",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.75 * %s_21",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
	}

	// ----------------------------------------------------------------------------------

}
