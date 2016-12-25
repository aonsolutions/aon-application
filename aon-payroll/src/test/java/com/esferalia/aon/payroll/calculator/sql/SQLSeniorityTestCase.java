/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfYear;
import static java.util.Calendar.YEAR;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

/**
 * @author rtrepiana
 *
 */
public class SQLSeniorityTestCase extends
		AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	@Test
	public void testSeniorityI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		// @formatter:off

		Date startContract = add(getFirstDayOfYear(getToday()), Calendar.YEAR, -3);
		
		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		addExtras(aonContext, agreement, startContract, 
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, }
				
		);


		ContractRecord contract = newContract(
				aonContext,
				startContract,
				Collections.emptyMap(),
				new String[] {
				"ANTIGÜEDAD(P_1 * 5 / 100, 2)" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, 
				category);
		//@formatter:on
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());

		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals( 1500.00 * 1.05, salary.getTotalPayment() );
		Assert.assertEquals( ( 1500.00 * 1.05 / 12 ) * 2, salary.getExtraPayProration() );
		
		
		
		startDate = getFirstDayOfYear(getToday());
		endDate = getLastDayOfYear(getToday());
		Date issueDate = add(getFirstDayOfMonth(endDate), Calendar.DAY_OF_MONTH, 14 );
		
		int year = get(issueDate,YEAR);
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals( 1500.00 * 1.05, salary.getTotalPayment() );


		startDate = add(add(getFirstDayOfYear(getToday()), Calendar.YEAR, -1), Calendar.MONTH,6); // 01/07 -1
		endDate = getLastDayOfMonth(add(getFirstDayOfYear(getToday()),Calendar.MONTH,5)); // 30/06
		issueDate = add(startDate, Calendar.YEAR, 1 );
		year = get(issueDate,YEAR);
		extra = getExtra(aonContext, agreement.getId(), "01/07");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals( 1500.00 * 1.05, salary.getTotalPayment() );
	}
		
	
	@Test
	public void testSeniorityII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		// @formatter:off

		Date startContract = add(getLastDayOfMonth(getToday()), Calendar.YEAR, -2);
		
		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		addExtras(aonContext, agreement, startContract, 
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1";
						this.month = Month.DECEMBER;
						this.start = "01/01";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, }
				
		);


		ContractRecord contract = newContract(
				aonContext,
				startContract,
				Collections.emptyMap(),
				new String[] {
				"ANTIGÜEDAD(P_1 * 5 / 100, 2)" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, 
				category);
		//@formatter:on
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());

		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals( 1500.00 * 1.00, salary.getTotalPayment() );
		Assert.assertEquals( ( 1500.00 * 1.00 / 12 ) * 2, salary.getExtraPayProration() );
		
		
		startDate = getFirstDayOfYear(getToday());
		endDate = getLastDayOfYear(getToday());
		Date issueDate = add(getFirstDayOfMonth(endDate), Calendar.DAY_OF_MONTH, 14 );
		int year = get(issueDate,YEAR);
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		int month = AonDateUtils.get(getToday(), Calendar.MONTH) +1 ; // +1 remember starts 14 
		
		//@formatter:off
		Assert.assertEquals( 
				(1500.00/12 * month) 
				+(1500.00 * 1.05 / 12 *(12-month)), 
				salary.getTotalPayment() );
		//@formatter:on

		startDate = add(add(getFirstDayOfYear(getToday()), Calendar.YEAR, -1), Calendar.MONTH,6); // 01/07 -1
		endDate = getLastDayOfMonth(add(getFirstDayOfYear(getToday()),Calendar.MONTH,5)); // 30/06
		issueDate = add(startDate, Calendar.YEAR, 2 );
		
		year = get(issueDate,YEAR);
		extra = getExtra(aonContext, agreement.getId(), "01/07");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		if ( month < 7 )
			Assert.assertEquals( 
					(1500.00/12 * (12 - ( 6-month )) 
					+(1500.00 * 1.05 / 12 * (6-month))), 
					salary.getTotalPayment() );
		else
			Assert.assertEquals( 
					(1500.00/12 * (month - 6 )) 
					+(1500.00 * 1.05 / 12 * ( 6  + 12 - month)), 
					salary.getTotalPayment() );
	}
	
	@Test
	public void testSeniorityIII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		// @formatter:off

		Date startContract = add(add(getToday(), Calendar.DAY_OF_MONTH, -1), Calendar.YEAR, -2);
		
		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		addExtras(aonContext, agreement, startContract, 
				new Extra[] { new Extra() {
					{
						this.expression = "P_0 + P_1";
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";
					}
				}, new Extra() {
					{
						this.expression = "P_0 + P_1";
						this.month = Month.JULY;
						this.start = "01/07 -1";
						this.end = "30/06";
						this.issue = "01/07";
					}
				}, }
				
		);
		
		addData(aonContext, agreement, startContract, 
				new HashMap<String, String>(){
			{
				put(ContextVariable.SENIORITY.getName(), "AÑO(FIN_NOMINA)-AÑO(INICIO_ANTIGUEDAD)");
			}
		});
		


		ContractRecord contract = newContract(
				aonContext,
				startContract,
				Collections.emptyMap(),
				new String[] {
				"ANTIGÜEDAD(P_1 * 5 / 100, 2)" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES",
				"TRACE('ANT.=%d',AÑO(FIN_NOMINA)-AÑO(INICIO_ANTIGUEDAD));0"
				}, 
				new String[] {
				}, 
				category);
		//@formatter:on
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());

		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals( 1500.00 * 1.05, salary.getTotalPayment() );
		Assert.assertEquals( ( 1500.00 * 1.05 / 12 ) * 2, salary.getExtraPayProration() );
		
		
		
		startDate = getFirstDayOfYear(getToday());
		endDate = getLastDayOfYear(getToday());
		Date issueDate = add(getFirstDayOfMonth(endDate), Calendar.DAY_OF_MONTH, 14 );
		int year = get(issueDate,YEAR);
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals( 1500.00 * 1.05, salary.getTotalPayment() );


		startDate = add(add(getFirstDayOfYear(getToday()), Calendar.YEAR, -1), Calendar.MONTH,6); // 01/07 -1
		endDate = getLastDayOfMonth(add(getFirstDayOfYear(getToday()),Calendar.MONTH,5)); // 30/06
		issueDate = add(startDate, Calendar.YEAR, 1 );
		year = get(issueDate,YEAR);
		extra = getExtra(aonContext, agreement.getId(), "01/07");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals( 1500.00 * 1.05, salary.getTotalPayment() );
	}
		
	
	// ------------------------------------------------------------------------

	public void testSeniorityIV() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		// @formatter:off

		Date startAgreement = add(getFirstDayOfYear(getToday()), Calendar.YEAR, -30);
		
		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
//		addPayment(aonContext, agreement, startAgreement, new Payment(){
//			{
//				expression = "MAX(COCIENTE(1993-AÑO(INICIO_ANTIGUEDAD),3),0) * 19.93";
//			}
//		});
//
		addPayment(aonContext, agreement, startAgreement, new Payment(){
			{
				expression = "MAX("
								+ "COCIENTE("
								+ "RESTO(1993-AÑO(INICIO_ANTIGUEDAD),3)+(1996-MAX(1993,AÑO(INICIO_ANTIGUEDAD)))"
								+ ",3)"
							+ ",0) "
							+ "* 19.96";
			}
		});
		
		addPayment(aonContext, agreement, startAgreement, new Payment(){
			{
				expression ="MAX("
								+"COCIENTE("
								+"MAX(RESTO(1996-AÑO(INICIO_ANTIGUEDAD),3),0)+(AÑO(INICIO_NOMINA)-MAX(1996,AÑO(INICIO_ANTIGUEDAD)))"
								+ ",4)"
							+",0)"
							+ "*66.66";
			}
		});
		
		
		// 2000
		ContractRecord contract = newContract(
				aonContext,
				add(getToday(), YEAR, 2000 - get(getToday(), YEAR)),
				Collections.emptyMap(),
				new String[] {
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, 
				category);
		//@formatter:on
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());

		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		Assert.assertEquals( 1500.00 + (int)((get(getToday(), YEAR)-2000)/4) * 66.66, salary.getTotalPayment() );

		
		
		// 1994
		//@formatter:off
		contract = newContract(
				aonContext,
				add(getToday(), YEAR, 1994 - get(getToday(), YEAR)),
				Collections.emptyMap(),
				new String[] {
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, 
				category);
		ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals( 1500.00 + (int)((get(getToday(), YEAR)-1994)/4) * 66.66, salary.getTotalPayment() );


		// 1996
		//@formatter:off
		contract = newContract(
				aonContext,
				add(getToday(), YEAR, 1994 - get(getToday(), YEAR)),
				Collections.emptyMap(),
				new String[] {
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, 
				category);
		ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals( 1500.00 + (int)((get(getToday(), YEAR)-1996)/4) * 66.66, salary.getTotalPayment() );

		// 1993
		//@formatter:off
		contract = newContract(
				aonContext,
				add(getToday(), YEAR, 1993 - get(getToday(), YEAR)),
				Collections.emptyMap(),
				new String[] {
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, 
				category);
		ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals( 1500.00 + ((int)((get(getToday(), YEAR)-1996)/4) * 66.66) + 19.96, salary.getTotalPayment() );
	}
	
}
