/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfYear;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.YEAR;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

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
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		assertEquals( 1500.00 * 1.05, salary.getTotalPayment() , DELTA);
		assertEquals( ( 1500.00 * 1.05 / 12 ) * 2, salary.getExtraPayProration(), DELTA);
		
		
		
		startDate = getFirstDayOfYear(getToday());
		endDate = getLastDayOfYear(getToday());
		Date issueDate = add(getFirstDayOfMonth(endDate), Calendar.DAY_OF_MONTH, 14 );
		
		int year = get(issueDate,YEAR);
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		assertEquals( 1500.00 * 1.05, salary.getTotalPayment(), DELTA);


		startDate = add(add(getFirstDayOfYear(getToday()), Calendar.YEAR, -1), Calendar.MONTH,6); // 01/07 -1
		endDate = getLastDayOfMonth(add(getFirstDayOfYear(getToday()),Calendar.MONTH,5)); // 30/06
		issueDate = add(startDate, Calendar.YEAR, 1 );
		year = get(issueDate,YEAR);
		extra = getExtra(aonContext, agreement.getId(), "01/07");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		assertEquals( 1500.00 * 1.05, salary.getTotalPayment(), DELTA);
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
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		assertEquals( 1500.00 * 1.00, salary.getTotalPayment(), DELTA);
		assertEquals( ( 1500.00 * 1.00 / 12 ) * 2, salary.getExtraPayProration(), DELTA);
		
		
		startDate = getFirstDayOfYear(getToday());
		endDate = getLastDayOfYear(getToday());
		Date issueDate = add(getFirstDayOfMonth(endDate), Calendar.DAY_OF_MONTH, 14 );
		int year = get(issueDate,YEAR);
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		int month = AonDateUtils.get(getToday(), Calendar.MONTH) +1 ; // +1 remember starts at end 
		
		//@formatter:off
		assertEquals( 
				(1500.00/12 * month) 
				+(1500.00 * 1.05 / 12 *(12-month)), 
				salary.getTotalPayment()
				, DELTA);
		//@formatter:on

		startDate = add(add(getFirstDayOfYear(getToday()), Calendar.YEAR, -1), Calendar.MONTH,6); // 01/07 -1
		endDate = getLastDayOfMonth(add(getFirstDayOfYear(getToday()),Calendar.MONTH,5)); // 30/06
		issueDate = add(startDate, Calendar.YEAR, 1 );
		
		year = get(issueDate,YEAR);
		extra = getExtra(aonContext, agreement.getId(), "01/07");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		if ( month < 7 )
			assertEquals( 
					(1500.00/12 * (12 - ( 6-month  )) 		// without seniority  
					+(1500.00 * 1.05 / 12 * (6-month))), 	// with seniority
					salary.getTotalPayment() 
					, DELTA);
		else
			assertEquals( 
					(1500.00) 
//					(1500.00/12 * (month - 6 )) 
//					+(1500.00/ 12 * ( 6  + 12 - month))
					, 
					salary.getTotalPayment() 
					, DELTA);
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
				//"TRACE('ANT.=%d',AÑO(FIN_NOMINA)-AÑO(INICIO_ANTIGUEDAD));0"
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
		
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		assertEquals( 1500.00 * 1.05, salary.getTotalPayment(), DELTA);
		assertEquals( ( 1500.00 * 1.05 / 12 ) * 2, salary.getExtraPayProration(), DELTA);
		
		
		
		startDate = getFirstDayOfYear(getToday());
		endDate = getLastDayOfYear(getToday());
		Date issueDate = add(getFirstDayOfMonth(endDate), Calendar.DAY_OF_MONTH, 14 );
		int year = get(issueDate,YEAR);
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		assertEquals( 1500.00 * 1.05, salary.getTotalPayment(), DELTA);


		startDate = add(add(getFirstDayOfYear(getToday()), Calendar.YEAR, -1), Calendar.MONTH,6); // 01/07 -1
		endDate = getLastDayOfMonth(add(getFirstDayOfYear(getToday()),Calendar.MONTH,5)); // 30/06
		issueDate = add(startDate, Calendar.YEAR, 1 );
		year = get(issueDate,YEAR);
		extra = getExtra(aonContext, agreement.getId(), "01/07");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		assertEquals( 1500.00 * 1.05, salary.getTotalPayment(), DELTA);
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
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		assertEquals( 1500.00 + (int)((get(getToday(), YEAR)-2000)/4) * 66.66, salary.getTotalPayment(), DELTA);

		
		
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
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		assertEquals( 1500.00 + (int)((get(getToday(), YEAR)-1994)/4) * 66.66, salary.getTotalPayment(), DELTA);


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
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		assertEquals( 1500.00 + (int)((get(getToday(), YEAR)-1996)/4) * 66.66, salary.getTotalPayment(), DELTA);

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
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		assertEquals( 1500.00 + ((int)((get(getToday(), YEAR)-1996)/4) * 66.66) + 19.96, salary.getTotalPayment(), DELTA);
	}
	@Test
	public void testSeniorityV() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		// @formatter:off

		Date startAgreement = add(getFirstDayOfYear(getToday()), Calendar.YEAR, -50);
		
		AgreementRecord agreement = newAgreement(aonContext);
		
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);
		
		addPayment(aonContext, agreement, startAgreement, new Payment(){
			{
				expression = "["
				+ "1 : 7.36" 
				+ ",2 : 14.65" 
				+ ",3 : 22.02" 
				+ ",4 : 29.3" 
				+ ",5 : 36.61" 
				+ ",5 : 36.61" 
				+ ",6 : 43.63" 
				+ ",7 : 50.32" 
				+ ",8 : 56.66" 
				+ ",9 : 62.73" 
				+ ",10 : 68.44" 
				+ ",11 : 73.86" 
				+ ",12 : 79" 
				+ ",13 : 83.75" 
				+ ",14 : 88.25" 
				+ ",15 : 92.62" 
				+ ",16 : 97.05" 
				+ ",17 : 101.48" 
				+ ",18 : 105.95" 
				+ ",19 : 110.4" 
				+ ",20 : 114.8" 
				+ ",21 : 119.28" 
				+ ",22 : 123.7" 
				+ ",23 : 128.15" 
				+ ",24 : 132.57" 
				+ ",25 : 137" 
				+ ",26 : 141.42" 
				+ ",27 : 145.89" 
				+ ",28 : 150.33" 
				+ ",29 : 154.74" 
				+ ",30 : 159.19" 
				+"][AÑOS_ANTIGUEDAD >= 30 ? 30 : AÑOS_ANTIGUEDAD]"
				+"* DIAS_TRABAJADOS / DIAS_MES";
			}
		});
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		Date yesterday = add(startDate, DAY_OF_MONTH, -1);
		
		double payments[] = {
				7.36,
				14.65,
				22.02,
				29.3,
				36.61,
				43.63,
				50.32,
				56.66,
				62.73,
				68.44,
				73.86,
				79,
				83.75,
				88.25,
				92.62,
				97.05,
				101.48,
				105.95,
				110.4,
				114.8,
				119.28,
				123.7,
				128.15,
				132.57,
				137,
				141.42,
				145.89,
				150.33,
				154.74,
				159.19,
				159.19,
				159.19,
				159.19,
				159.19,
				159.19,
				159.19,
		};

		for ( int i = 1 ; i <= payments.length ; i++ )  {
			//@formatter:off
			ContractRecord contract = newContract(
					aonContext,
					add(yesterday, YEAR, -1 * i),
					Collections.emptyMap(),
					new String[] {
							"TRACE('AÑOS_ANTIGUEDAD=%f\r\n', MIN(AÑOS_ANTIGUEDAD,30.00)); 0.00"
					}, 
					new String[] {
					}, 
					category);
			//@formatter:on
			
			//@formatter:off
			ISQLContractSalaryCalculatorContext ctx = 
					getContractSalaryCalculatorContext(connection, 
					startDate, 
					endDate, 
					endDate, 
					contract);
			//@formatter:on
			Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
			
			assertEquals( payments[i-1], salary.getTotalPayment(), DELTA );
		}

		
		
	}
	
	@Test
	public void testSeniorityVI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		// @formatter:off

		Date startContract = add(getToday(), Calendar.YEAR, -3);
		
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
		
		addData(aonContext, agreement, startContract, new HashMap<String, String>(){
			{
				put("INICIO_ANTIGUEDAD" , "INICIO_MES(INICIO_ANTIGUEDAD)");
			}
		});


		ContractRecord contract = newContract(
				aonContext,
				startContract,
				Collections.emptyMap(),
				new String[] {
				"ANTIGÜEDAD(P_1 * 5 / 100, 3)" ,
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
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		assertEquals( 1500.00 * 1.05, salary.getTotalPayment(), DELTA);
		assertEquals( ( 1500.00 * 1.05 / 12 ) * 2, salary.getExtraPayProration(), DELTA);
		
		
		
		startDate = getFirstDayOfMonth(getToday());
		endDate = getLastDayOfMonth(startDate);
		Date issueDate = add(getFirstDayOfMonth(endDate), Calendar.DAY_OF_MONTH, 14 );
		
		int year = get(issueDate,YEAR);
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		int month = get(getToday(), Calendar.MONTH);
		assertEquals( 1500.00 * 1.05 / 12.00 * ( 12 - month ) + 1500.00 * 1.00 / 12.00 * month, salary.getTotalPayment(), DELTA);


//		startDate = add(add(getFirstDayOfMonth(getToday()), Calendar.YEAR, -1), Calendar.MONTH,6); // 01/07 -1
//		endDate = getLastDayOfMonth(add(getFirstDayOfMonth(getToday()),Calendar.MONTH,5)); // 30/06
//		issueDate = add(startDate, Calendar.YEAR, 1 );
//		year = get(issueDate,YEAR);
//		extra = getExtra(aonContext, agreement.getId(), "01/07");
//		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
//		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
//		assertEquals( 1500.00 * 1.05, salary.getTotalPayment() );
	}
	
	@Test
	public void testSeniorityVII() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		// @formatter:off

		Date startContract = add(getToday(), Calendar.YEAR, -3);
		
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
		
		addData(aonContext, agreement, startContract, new HashMap<String, String>(){
			{
				put("INICIO_ANTIGUEDAD" , "INICIO_AÑO(INICIO_ANTIGUEDAD)");
			}
		});


		ContractRecord contract = newContract(
				aonContext,
				startContract,
				Collections.emptyMap(),
				new String[] {
				"ANTIGÜEDAD(P_1 * 5 / 100, 3)" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, 
				category);
		//@formatter:on
		
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		//@formatter:off
		ISQLContractSalaryCalculatorContext ctx = 
				getContractSalaryCalculatorContext(connection, 
				startDate, 
				endDate, 
				endDate, 
				contract);
		//@formatter:on
		
		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		
		assertEquals( 1500.00 * 1.05, salary.getTotalPayment(), DELTA);
		assertEquals( ( 1500.00 * 1.05 / 12 ) * 2, salary.getExtraPayProration(), DELTA);
		
		
		
		startDate = getFirstDayOfMonth(getToday());
		endDate = getLastDayOfMonth(startDate);
		Date issueDate = add(getFirstDayOfMonth(endDate), Calendar.DAY_OF_MONTH, 14 );
		
		int year = get(issueDate,YEAR);
		AgreementExtraRecord extra = getExtra(aonContext, agreement.getId(), "15/12");
		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		int month = get(getToday(), Calendar.MONTH);
		assertEquals( 1500.00 * 1.05 , salary.getTotalPayment(), DELTA);


//		startDate = add(add(getFirstDayOfMonth(getToday()), Calendar.YEAR, -1), Calendar.MONTH,6); // 01/07 -1
//		endDate = getLastDayOfMonth(add(getFirstDayOfMonth(getToday()),Calendar.MONTH,5)); // 30/06
//		issueDate = add(startDate, Calendar.YEAR, 1 );
//		year = get(issueDate,YEAR);
//		extra = getExtra(aonContext, agreement.getId(), "01/07");
//		ctx = getExtraSalaryCalculatorContext(connection, contract, extra, year, issueDate);
//		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
//		assertEquals( 1500.00 * 1.05, salary.getTotalPayment() );
	}
}
