package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.IRPF_PERCENT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.WORKED_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.server.AonDateUtils;


public class SQLAgreementSalaryCalculatorContextTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	public void testRedefinedPaymentsI() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		

		Date startYear  = getFirstDayOfYear(getToday());
		
		cleanSystemDeductions(aonContext);
		
		//formatter:off
		addSSRegimeDeduction(aonContext, 
				SSRegimeType.GENERAL, 
				startYear, 
				DeductionType.COMMON_CONTINGENCY, 
				String.format("%s * 4.7/100.00", CGC_BASE));
		addSSRegimeDeduction(aonContext, 
				SSRegimeType.GENERAL, 
				startYear, 
				DeductionType.JOB_TRAINING, 
				String.format("%s * 1.55/100.00", CGC_BASE));
		addSSRegimeDeduction(aonContext, 
				SSRegimeType.GENERAL, 
				startYear, 
				DeductionType.UNEMPLOYMENT, 
				String.format("%s * 0.1/100.00", CGC_BASE));
		addSSRegimeDeduction(aonContext, 
				SSRegimeType.GENERAL, 
				startYear, 
				DeductionType.IRPF, 
				String.format("%s * %s/100.00", CGC_BASE, IRPF_PERCENT));
		//formatter:on

		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord categoryI = newAgreementCategory(aonContext, agreement);
		addData(aonContext, categoryI, startYear, null, new HashMap<String,String>() {
			{
				put("SALARIO_BASE", "2000.00");
				put("PLUS_TRANSPORTE", "10.00");
			}
		});
		
		

		PaymentConceptRecord conceptI = addConcept(aonContext, "I");
		PaymentConceptRecord conceptII = addConcept(aonContext, "II");

		addPayments(aonContext, agreement, getFirstDayOfYear(getToday()), 
				new Payment [] {
			
			new Payment(){
				{
					this.concept = conceptI.getId();
					this.expression = String.format("SALARIO_BASE * %s / %s ", WORKED_DAYS , MONTH_DAYS );
				}
			},
			
			new Payment(){
				{
					this.concept = conceptII.getId();
					this.expression = String.format("PLUS_TRANSPORTE * %s", ContextVariable.ACTUAL_DAYS );
				}
			}
			
		});
		
		java.util.Date startDate = AonDateUtils.getMonthFirstDay(getToday());
		java.util.Date endDate = AonDateUtils.getMonthLastDay(startDate);
		
		SQLAgreementSalaryCalculatorContext ctx = new SQLAgreementSalaryCalculatorContext(connection, startDate, endDate, categoryI.getAgreementLevel());
		ctx.next();
		
		Salary salary = new ContractSalaryCalculator<Salary>( new SalaryBuilder()).calculate(ctx);
		
		cleanSystemDeductions(aonContext);
		
		assertTrue( salary.getTotalIrpf() > 0.00 );
		
		
	}

}
