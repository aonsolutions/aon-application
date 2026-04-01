/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.BonusConcept.BONUS_CONCEPT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BONUS_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.REGULATORY_BASE;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getMax;
import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.SalaryDeduction;
import com.esferalia.aon.jooq.tables.records.BonusConceptRecord;
import com.esferalia.aon.jooq.tables.records.ContractBonusRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator.Listener;
import com.esferalia.aon.payroll.calculator.GenericContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author rtrepiana
 *
 */
public class SQLBonusTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	@Test
	public void tesTarifaReducida() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, getToday(), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'330'");
				put(ContextVariable.MONDAY_HOURS.getName(),"8");
				put(ContextVariable.TUESDAY_HOURS.getName(),"8");
				put(ContextVariable.WEDNESDAY_HOURS.getName(),"8");
				put(ContextVariable.THURSDAY_HOURS.getName(),"8");
				put(ContextVariable.FRIDAY_HOURS.getName(),"8");
			}
			},
			new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"500.00*DIAS_TRABAJADOS/DIAS_MES",
					"TRACE('DIAS_TRABAJADOS=%f\r\n',DIAS_TRABAJADOS);0.00"
			}, 
			new String[] {
					"TRACE('BASE_CGC = %f\r\n', BASE_CGC );BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					},
			null
		);
		//@formatter:on

		addSSRegimeData(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_CGC_E", "23.60");
					}
				});

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY,
				"( BASE_CGC_E = BASE_CGC) * PORCENTAJE_CGC_E/100");

		//@formatter:off
		BonusConceptRecord concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY,
				"/*read-only*/"+
				"CHECK(COEFICIENTE_PARCIALIDAD>=0.50,'La jornada debe ser al menos del 50%');"+
				"TC2S='100,200,300,150,250,350,130,230,330';"+
				"CHECK(TC2S.indexOf(TC2)>=0,'El contrato debe ser %s', TC2S);"+
				"FIN_BONIF=DIA(AÑO(INICIO_CONTRATO,2),-1);"+
				"CHECK(INICIO_NOMINA <= FIN_BONIF, 'La Tarifa Reducida finalizo el %1$td/%1$tm/%1$tY', FIN_BONIF);"+
				"DIAS_NO_BONIF=MAX(0,DIAS(FIN,FIN_BONIF)); "+
				"500.00  * (DIAS_COTIZADOS - DIAS_NO_BONIF) * COEFICIENTE_PARCIALIDAD / DIAS_MES * PORCENTAJE_CGC_E/100"+
				"/**/");
		addBonus(aonContext, contract, concept, null);
		//@formatter:on

		//@formatter:off
		concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY, 
				"/*read-only*/"+
				"CHECK((C=COEFICIENTE_PARCIALIDAD)>=0.50,'La jornada debe ser al menos del 50%');"+
				"T='100,200,300,150,250,350,130,230,330';"+
				"CHECK(T.indexOf(TC2)>=0,'El contrato debe ser %s',T);"+
				"I=AÑO((O=INICIO_CONTRATO),2);"+
				"CHECK(FIN_NOMINA>=I,'La Tarifa Reducida (<10) comienza el %1$td/%1$tm/%1$tY',I);"+
				"N=MAX(0,DIAS(I,(A=INICIO)));"+
				"F=DIA(AÑO(O,3),-1);"+
				"CHECK(A<=F,'La Tarifa Reducida (<10) finalizo el %1$td/%1$tm/%1$tY',F);"+
				"N+=MAX(0,DIAS(FIN,F));"+
				"250.00*(DIAS_COTIZADOS-N)*C/DIAS_MES*PORCENTAJE_CGC_E/100"+
				"/**/");
		addBonus(aonContext, contract, concept, null);
		//@formatter:on

		// Current month. Not all days worked.
		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(getToday()),
						getLastDayOfMonth(getToday()),
						getLastDayOfMonth(getToday()), contract));

		int monthDays = getMax(getToday(), Calendar.DAY_OF_MONTH);
		long workDays = new Period(getToday(), getLastDayOfMonth(getToday())).daysStream()
				.filter(d -> d.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY )
				.filter(d -> d.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY ).count();
		workDays = workDays > 0 ? monthDays - (get(getToday(), Calendar.DAY_OF_MONTH) - 1) : workDays;
		
		assertEquals((1000.00 * workDays / monthDays * 23.60 / 100),
				salary.getTotalEnterprise(), DELTA);

		// Next month. 500 bonus full filled.
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), MONTH, 1)),
						getLastDayOfMonth(add(getToday(), MONTH, 1)),
						getLastDayOfMonth(add(getToday(), MONTH, 1)), contract));

		assertEquals((1000.00 * 23.60 / 100),
				salary.getTotalEnterprise(), DELTA);

		addIT(aonContext,
				contract,
				LeaveType.COMMON_DISEASE,
				add(getFirstDayOfMonth(add(getToday(), MONTH, 1)),
						DAY_OF_MONTH, 10),
				add(getFirstDayOfMonth(add(getToday(), MONTH, 1)),
						DAY_OF_MONTH, 12), null);
		PaymentConceptRecord prestIT = addConcept(aonContext,
				PREST_IT);
		addPayment(aonContext, contract, prestIT, String.format(
				"%s * 0.00 * %s_1_3", REGULATORY_BASE, COMMON_DISEASE_DAYS),
				String.format(
						"%s * %s_1_3", REGULATORY_BASE, COMMON_DISEASE_DAYS));

		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), MONTH, 1)),
						getLastDayOfMonth(add(getToday(), MONTH, 1)),
						getLastDayOfMonth(add(getToday(), MONTH, 1)), contract));

		monthDays = getMax(add(getToday(), MONTH, 1), Calendar.DAY_OF_MONTH);
		assertEquals((1500.00 * ( monthDays - 3 ) / monthDays),
				salary.getTotalPayment(), DELTA);
		assertEquals((1000.00 * 23.60 / 100),
				salary.getTotalEnterprise(), DELTA);

		// Next year. 500 bonus full filled.
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), YEAR, 1)),
						getLastDayOfMonth(add(getToday(), YEAR, 1)),
						getLastDayOfMonth(add(getToday(), YEAR, 1)), contract));

		assertEquals((1000.00 * 23.60 / 100),
				salary.getTotalEnterprise(), DELTA);

		// After two years. 500 bonus partial filled & 250 too. Yes two bonus.
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), YEAR, 2)),
						getLastDayOfMonth(add(getToday(), YEAR, 2)),
						getLastDayOfMonth(add(getToday(), YEAR, 2)), contract));

		monthDays = getMax(add(getToday(), YEAR, 2), Calendar.DAY_OF_MONTH);
		int bonusIDays = get(add(getToday(), YEAR, 2), DAY_OF_MONTH) - 1;
		int bonusIIDays = monthDays - bonusIDays;
		assertEquals(
				((1500.00 - 500.00 * bonusIDays / monthDays) * 23.60 / 100)
						- (250.00 * bonusIIDays / monthDays * 23.60 / 100),
				salary.getTotalEnterprise(), DELTA);

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder());
		calculator.setListener(new Listener() {

			@Override
			public void onCheckError(IContractBonus bonus, String message) {
				System.out.println(message);
			}
		});

		// After two years and one month. 250 bonus full filled
		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection,
				getFirstDayOfMonth(add(add(getToday(), YEAR, 2), MONTH, 1)),
				getLastDayOfMonth(add(add(getToday(), YEAR, 2), MONTH, 1)),
				getLastDayOfMonth(add(add(getToday(), YEAR, 2), MONTH, 1)),
				contract));

		assertEquals((1250.00) * 23.60 / 100,
				salary.getTotalEnterprise(), DELTA);

		calculator = new ContractSalaryCalculator<Salary>(new SalaryBuilder());
		calculator.setListener(new Listener() {

			@Override
			public void onCheckError(IContractBonus bonus, String message) {
				System.out.println(message);
			}
		});

		// After three years . 250 bonus last momth
		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection, getFirstDayOfMonth(add(getToday(), YEAR, 3)),
				getLastDayOfMonth(add(getToday(), YEAR, 3)),
				getLastDayOfMonth(add(getToday(), YEAR, 3)), contract));
		bonusIIDays = get(add(getToday(), YEAR, 3), DAY_OF_MONTH) - 1;
		monthDays = getMax(add(getToday(), YEAR, 3), Calendar.DAY_OF_MONTH);
		
		assertEquals(
				(1500.00 - 250.00 * bonusIIDays / monthDays) * 23.60 / 100,
				salary.getTotalEnterprise(), DELTA);

		calculator = new ContractSalaryCalculator<Salary>(new SalaryBuilder());
		calculator.setListener(new Listener() {

			@Override
			public void onCheckError(IContractBonus bonus, String message) {
				System.out.println(message);
			}
		});

		// After three years and one month . None bonus
		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection,
				getFirstDayOfMonth(add(add(getToday(), YEAR, 3), MONTH, 1)),
				getLastDayOfMonth(add(add(getToday(), YEAR, 3), MONTH, 1)),
				getLastDayOfMonth(add(add(getToday(), YEAR, 3), MONTH, 1)),
				contract));

		assertEquals((1500.00) * 23.60 / 100,
				salary.getTotalEnterprise(), DELTA);

		// TC2 Invalid '110'
		addData(aonContext, contract, getToday(), null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), "'110'");
					}
				});

		calculator = new ContractSalaryCalculator<Salary>(new SalaryBuilder());
		calculator.setListener(new Listener() {

			@Override
			public void onCheckError(IContractBonus bonus, String message) {
				System.out.println(message);
			}
		});

		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection, getFirstDayOfMonth(add(getToday(), MONTH, 1)),
				getLastDayOfMonth(add(getToday(), MONTH, 1)),
				getLastDayOfMonth(add(getToday(), MONTH, 1)), contract));
		assertEquals((1500.00) * 23.60 / 100,
				salary.getTotalEnterprise(), DELTA);

		// COEFICIENTE_PARCIALIDAD too low.
		addData(aonContext, contract, getToday(), null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.PARTIAL_FACTOR.getName(), "0.25");
					}
				});

		calculator = new ContractSalaryCalculator<Salary>(new SalaryBuilder());
		calculator.setListener(new Listener() {

			@Override
			public void onCheckError(IContractBonus bonus, String message) {
				System.out.println(message);
			}
		});

		// 50% time work
		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection, getFirstDayOfMonth(add(getToday(), MONTH, 1)),
				getLastDayOfMonth(add(getToday(), MONTH, 1)),
				getLastDayOfMonth(add(getToday(), MONTH, 1)), contract));
		assertEquals((1500.00) * 23.60 / 100 * 0.25,
				salary.getTotalEnterprise(), DELTA);
		

		addData(aonContext, contract, getToday(), null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), "'200'");
						put(ContextVariable.PARTIAL_FACTOR.getName(), "0.50");
					}
				});

		calculator = new ContractSalaryCalculator<Salary>(new SalaryBuilder());
		calculator.setListener(new Listener() {

			@Override
			public void onCheckError(IContractBonus bonus, String message) {
				System.out.println(message);
			}
		});
		
		ISQLContractSalaryCalculatorContext ctx =getContractSalaryCalculatorContext(
				connection, getFirstDayOfMonth(getToday()),
				getLastDayOfMonth(getToday()), getLastDayOfMonth(getToday()),
				contract);
		salary = calculator.calculate(ctx);

		monthDays = getMax(getToday(), Calendar.DAY_OF_MONTH);
		workDays = monthDays - (get(getToday(), Calendar.DAY_OF_MONTH) - 1);
		assertEquals((750.00 - 250.00) * workDays / monthDays * 23.60
				/ 100, salary.getTotalEnterprise(), DELTA);

		// Second month . Half of 500 bonus...
		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection, getFirstDayOfMonth(add(getToday(), MONTH, 1)),
				getLastDayOfMonth(add(getToday(), MONTH, 1)),
				getLastDayOfMonth(add(getToday(), MONTH, 1)), contract));
		assertEquals((750.00 - 250.00) * 23.60 / 100,
				salary.getTotalEnterprise(), DELTA);

		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection, getFirstDayOfMonth(getToday()),
				getLastDayOfMonth(getToday()), getLastDayOfMonth(getToday()),
				contract));
		assertEquals((750.00 - 250.00) * ((double) workDays / monthDays)
				* 23.60 / 100, salary.getTotalEnterprise(), DELTA);

		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), YEAR, 2)),
						getLastDayOfMonth(add(getToday(), YEAR, 2)),
						getLastDayOfMonth(add(getToday(), YEAR, 2)), contract));
		bonusIDays = get(add(getToday(), YEAR, 2), DAY_OF_MONTH) - 1;
		monthDays = getMax(add(getToday(), YEAR, 2), Calendar.DAY_OF_MONTH);
		bonusIIDays = monthDays - bonusIDays;
		assertEquals((750.00 * 23.60 / 100)
				- (250.00 * bonusIDays / monthDays * 23.60 / 100)
				- (125.00 * bonusIIDays / monthDays * 23.60 / 100),
				salary.getTotalEnterprise(), DELTA);

		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), YEAR, 3)),
						getLastDayOfMonth(add(getToday(), YEAR, 3)),
						getLastDayOfMonth(add(getToday(), YEAR, 3)), contract));
		monthDays = getMax(add(getToday(), YEAR, 3), Calendar.DAY_OF_MONTH);
		bonusIIDays = get(add(getToday(), YEAR, 3), DAY_OF_MONTH) - 1;
		;
		assertEquals((750.00 * 23.60 / 100)
				- (125.00 * bonusIIDays / monthDays * 23.60 / 100),
				salary.getTotalEnterprise(), DELTA);

		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), YEAR, 4)),
						getLastDayOfMonth(add(getToday(), YEAR, 4)),
						getLastDayOfMonth(add(getToday(), YEAR, 4)), contract));
		monthDays = getMax(add(getToday(), YEAR, 2), Calendar.DAY_OF_MONTH);
		assertEquals((750.00 * 23.60 / 100),
				salary.getTotalEnterprise(), DELTA);

	}

	@Test
	public void tesTarifaPlana() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, getToday(), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'100'");
			}
			},
			new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"500.00*DIAS_TRABAJADOS/DIAS_MES",
					"TRACE('DIAS_TRABAJADOS=%f\r\n',DIAS_TRABAJADOS);0.00",
					"TRACE('DIAS_COTIZADOS=%f\r\n',DIAS_COTIZADOS);0.00"
					}, 
			new String[] {
					"TRACE('BASE_CGC = %f\r\n', BASE_CGC );BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					},
			null
		);
		//@formatter:on

		addSSRegimeData(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_CGC_E", "23.60");
					}
				});

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY,
				"( BASE_CGC_E = BASE_CGC) * PORCENTAJE_CGC_E/100");

		//@formatter:off
		BonusConceptRecord concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY,
				"/*read-only*/"+
				"CHECK((C=COEFICIENTE_PARCIALIDAD)>=0.50,'La jornada debe ser al menos del 50%');"+
				"TC2S='100,200,300';"+
				"CHECK(TC2S.indexOf(TC2)>=0,'El contrato debe ser %s', TC2S);"+
				"FIN_BONIF=DIA(AÑO(INICIO_CONTRATO,2),-1);"+
				"CHECK(INICIO_NOMINA <= FIN_BONIF, 'La Tarifa Plana finalizo el %1$td/%1$tm/%1$tY', FIN_BONIF);"+
				"N=MAX(0,DIAS(FIN,FIN_BONIF)); "+
				"D = (C < 1.00 ? ( C < 75.00 ? 50.00 : 75.00 ) : 100.00);"+
				"MAX(0,(CGC_E * ((DIAS_COTIZADOS-N)/DIAS_COTIZADOS)-(D * (DIAS_COTIZADOS-N)/DIAS_MES )))"+
				"/**/");
		addBonus(aonContext, contract, concept, null);
		//@formatter:on

		//@formatter:off
		concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY, 
				"/*read-only*/"+
				"CHECK((C=COEFICIENTE_PARCIALIDAD)>=0.50,'La jornada debe ser al menos del 50%');"+
				"T='100,200,300';"+
				"CHECK(T.indexOf(TC2)>=0,'El contrato debe ser %s',T);"+
				"I=AÑO(INICIO_CONTRATO,2);"+
				"CHECK(FIN_NOMINA>=I,'La Tarifa Plana (<10) comienza el %1$td/%1$tm/%1$tY',I);"+
				"N=MAX(0,DIAS(I,(A=INICIO)));"+
				"F=DIA(AÑO(INICIO_CONTRATO,3),-1);"+
				"CHECK(INICIO_NOMINA<=F,'La Tarifa Plana (<10) finalizo el %1$td/%1$tm/%1$tY',F);"+
				"N+=MAX(0,DIAS(FIN,F));"+
				"TRACE('N=%f\r\n', N);"+
				"CGC_E * ((DIAS_COTIZADOS-N)/DIAS_COTIZADOS) * 0.50 "+
				"/**/");
		addBonus(aonContext, contract, concept, null);
		//@formatter:on

		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder());
		calculator.setListener(new Listener() {

			@Override
			public void onCheckError(IContractBonus bonus, String message) {
				System.out.println(message);
			}
		});


		// Current month. Not all days worked.
		Salary salary = calculator
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(getToday()),
						getLastDayOfMonth(getToday()),
						getLastDayOfMonth(getToday()), contract));

		int monthDays = getMax(getToday(), Calendar.DAY_OF_MONTH);
		int workDays = monthDays - (get(getToday(), Calendar.DAY_OF_MONTH) - 1);
		
		assertEquals(
				100.00 * workDays / monthDays,
				salary.getTotalEnterprise(), DELTA);

		// Next month. 100 bonus full filled.
		salary = calculator
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), MONTH, 1)),
						getLastDayOfMonth(add(getToday(), MONTH, 1)),
						getLastDayOfMonth(add(getToday(), MONTH, 1)), contract));

		assertEquals( 
				100.00,
				salary.getTotalEnterprise(), DELTA);

		addIT(aonContext,
				contract,
				LeaveType.COMMON_DISEASE,
				add(getFirstDayOfMonth(add(getToday(), MONTH, 1)),
						DAY_OF_MONTH, 10),
				add(getFirstDayOfMonth(add(getToday(), MONTH, 1)),
						DAY_OF_MONTH, 12), null);
		PaymentConceptRecord prestIT = addConcept(aonContext,
				PREST_IT);
		addPayment(aonContext, contract, prestIT, String.format(
				"%s * 0.00 * %s_1_3", REGULATORY_BASE, COMMON_DISEASE_DAYS),
				String.format(
						"%s * %s_1_3", REGULATORY_BASE, COMMON_DISEASE_DAYS));

		salary = calculator
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), MONTH, 1)),
						getLastDayOfMonth(add(getToday(), MONTH, 1)),
						getLastDayOfMonth(add(getToday(), MONTH, 1)), contract));

		monthDays = getMax(add(getToday(), MONTH, 1), Calendar.DAY_OF_MONTH);
		assertEquals((1500.00 * ( monthDays - 3 ) / monthDays),
				salary.getTotalPayment(), DELTA);
		assertEquals(100.00,
				salary.getTotalEnterprise(), DELTA);

		// Next year. 100 bonus full filled.
		salary = calculator
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), YEAR, 1)),
						getLastDayOfMonth(add(getToday(), YEAR, 1)),
						getLastDayOfMonth(add(getToday(), YEAR, 1)), contract));

		assertEquals(100.00,
				salary.getTotalEnterprise(), DELTA);

		// After two years. 100 bonus partial filled & 50% too. Yes two bonus.
		salary = calculator
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), YEAR, 2)),
						getLastDayOfMonth(add(getToday(), YEAR, 2)),
						getLastDayOfMonth(add(getToday(), YEAR, 2)), contract));

		monthDays = getMax(add(getToday(), YEAR, 2), Calendar.DAY_OF_MONTH);
		int bonusIDays = get(add(getToday(), YEAR, 2), DAY_OF_MONTH) - 1;
		int bonusIIDays = monthDays - bonusIDays;
		assertEquals(( 100.00 * bonusIDays / monthDays )
				+ (1500.00 * bonusIIDays / monthDays * 23.6 / 100 * 0.50),
				salary.getTotalEnterprise(), DELTA);

		// After two years and one month. 50% bonus full filled
		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection,
				getFirstDayOfMonth(add(add(getToday(), YEAR, 2), MONTH, 1)),
				getLastDayOfMonth(add(add(getToday(), YEAR, 2), MONTH, 1)),
				getLastDayOfMonth(add(add(getToday(), YEAR, 2), MONTH, 1)),
				contract));
		assertEquals(750.00 * 23.60 / 100,
				salary.getTotalEnterprise(), DELTA);


		// After three years . 50% bonus last month
		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection, getFirstDayOfMonth(add(getToday(), YEAR, 3)),
				getLastDayOfMonth(add(getToday(), YEAR, 3)),
				getLastDayOfMonth(add(getToday(), YEAR, 3)), contract));
		monthDays = getMax(add(getToday(), YEAR, 3), DAY_OF_MONTH);
		bonusIIDays = get(add(getToday(), YEAR, 3), DAY_OF_MONTH) - 1;
		assertEquals(1500.00 * 23.60 / 100  -  750.00 * 23.60 / 100 * bonusIIDays / monthDays,
				salary.getTotalEnterprise(), DELTA);

		// After three years and one month . None bonus
		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection,
				getFirstDayOfMonth(add(add(getToday(), YEAR, 3), MONTH, 1)),
				getLastDayOfMonth(add(add(getToday(), YEAR, 3), MONTH, 1)),
				getLastDayOfMonth(add(add(getToday(), YEAR, 3), MONTH, 1)),
				contract));

		assertEquals((1500.00) * 23.60 / 100,
				salary.getTotalEnterprise(), DELTA);

		// TC2 Invalid '110'
		addData(aonContext, contract, getToday(), null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), "'110'");
					}
				});

		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection, getFirstDayOfMonth(add(getToday(), MONTH, 1)),
				getLastDayOfMonth(add(getToday(), MONTH, 1)),
				getLastDayOfMonth(add(getToday(), MONTH, 1)), contract));
		assertEquals((1500.00) * 23.60 / 100,
				salary.getTotalEnterprise(), DELTA);

		// COEFICIENTE_PARCIALIDAD too low.
		addData(aonContext, contract, getToday(), null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.PARTIAL_FACTOR.getName(), "0.25");
					}
				});

		// 50% time work
		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection, getFirstDayOfMonth(add(getToday(), MONTH, 1)),
				getLastDayOfMonth(add(getToday(), MONTH, 1)),
				getLastDayOfMonth(add(getToday(), MONTH, 1)), contract));
		assertEquals((1500.00) * 23.60 / 100 * 0.25,
				salary.getTotalEnterprise(), DELTA);

		addData(aonContext, contract, getToday(), null,
				new HashMap<String, String>() {
					{
						put(ContextVariable.TC2.getName(), "'200'");
						put(ContextVariable.PARTIAL_FACTOR.getName(), "0.50");
					}
				});

		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection, 
				getFirstDayOfMonth(getToday()),
				getLastDayOfMonth(getToday()), 
				getLastDayOfMonth(getToday()),
				contract));
		monthDays = getMax(getToday(), Calendar.DAY_OF_MONTH);
		workDays = monthDays - (get(getToday(), Calendar.DAY_OF_MONTH) - 1);
		assertEquals(50.00 * workDays / monthDays, salary.getTotalEnterprise(), DELTA);

		// Second month . 50 bonus...
		salary = calculator.calculate(getContractSalaryCalculatorContext(
				connection, 
				getFirstDayOfMonth(add(getToday(), MONTH, 1)),
				getLastDayOfMonth(add(getToday(), MONTH, 1)),
				getLastDayOfMonth(add(getToday(), MONTH, 1)), contract));
		assertEquals(50.00,
				salary.getTotalEnterprise(), DELTA);

		
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), YEAR, 2)),
						getLastDayOfMonth(add(getToday(), YEAR, 2)),
						getLastDayOfMonth(add(getToday(), YEAR, 2)), contract));
		monthDays = getMax(add(getToday(), YEAR, 2), Calendar.DAY_OF_MONTH);
		bonusIDays = get(add(getToday(), YEAR, 2), DAY_OF_MONTH) - 1;
		bonusIIDays = monthDays - bonusIDays;
		assertEquals(50.00 * bonusIDays / monthDays
				+ ( 750.00 * 23.60 / 100.00 * bonusIIDays / monthDays * 0.50),
				salary.getTotalEnterprise(), DELTA);

		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(add(getToday(), YEAR, 2),MONTH, 1)),
						getLastDayOfMonth(add(add(getToday(), YEAR, 2),MONTH, 1)),
						getLastDayOfMonth(add(add(getToday(), YEAR, 2),MONTH, 1)), contract));
		assertEquals((750.00 * 0.50 * 23.60 / 100),
				salary.getTotalEnterprise(), DELTA);

		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), YEAR, 3)),
						getLastDayOfMonth(add(getToday(), YEAR, 3)),
						getLastDayOfMonth(add(getToday(), YEAR, 3)), contract));
		bonusIIDays = get(add(getToday(), YEAR, 3), DAY_OF_MONTH) - 1;
		monthDays = getMax(add(getToday(), YEAR, 3), Calendar.DAY_OF_MONTH);
		assertEquals(750.00 * 23.60 / 100 - (750.00 * 0.50 * 23.60 / 100 * bonusIDays / monthDays),
				salary.getTotalEnterprise(), DELTA);

		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(add(getToday(), YEAR, 3), MONTH,1)),
						getLastDayOfMonth(add(add(getToday(), YEAR, 3), MONTH,1)),
						getLastDayOfMonth(add(add(getToday(), YEAR, 3), MONTH,1)), contract));
		assertEquals((750.00 * 23.60 / 100),
				salary.getTotalEnterprise(), DELTA);

		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(add(getToday(), YEAR, 4)),
						getLastDayOfMonth(add(getToday(), YEAR, 4)),
						getLastDayOfMonth(add(getToday(), YEAR, 4)), contract));
		assertEquals((750.00 * 23.60 / 100),
				salary.getTotalEnterprise(), DELTA);

	}

	@Test
	public void testBonusDaysI() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
					}
				});

		Date bonusEnd = AonDateUtils.add(getToday(), YEAR, 3);
		ContractBonusRecord bonus = addBonus(aonContext, contract,
				String.format("%s", BONUS_DAYS), getToday(), bonusEnd);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		int bonusDays = AonDateUtils.get(end, Calendar.DATE)
				- AonDateUtils.get(getToday(), Calendar.DATE) + 1;
		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder()).calculate(ctx);
		assertEquals(1, salary.getSalaryBonus().size());
		SalaryBonus salaryBonus = salary.getSalaryBonus().iterator().next();
		System.out.println(0 + "-." + start + "..." + end + " = " + bonusDays);
		assertEquals(bonusDays == AonDateUtils.getMax(start, DATE) ? 30
				: bonusDays, salaryBonus.getAmount(), DELTA);

		for (int i = 1; i <= 24; i++) {
			start = getFirstDayOfMonth(add(start, MONTH, 1));
			end = getLastDayOfMonth(start);
			ctx = new SQLContractSalaryCalculatorContext(connection, start,
					end, end, criteria);
			ctx.next();
			bonusDays = AonDateUtils.get(end, Calendar.DATE)
					- AonDateUtils.get(start, Calendar.DATE) + 1;
			salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
					.calculate(ctx);
			assertEquals(1, salary.getSalaryBonus().size());
			salaryBonus = salary.getSalaryBonus().iterator().next();
			System.out.println(i + "-." + start + "..." + end + " = "
					+ bonusDays);
			assertEquals(bonusDays == AonDateUtils.getMax(start, DATE) ? 30
					: bonusDays, salaryBonus.getAmount(), DELTA);
		}

		start = getFirstDayOfMonth(bonusEnd);
		end = getLastDayOfMonth(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		bonusDays = AonDateUtils.get(bonusEnd, Calendar.DATE)
				- AonDateUtils.get(start, Calendar.DATE) + 1;
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		assertEquals(1, salary.getSalaryBonus().size());
		salaryBonus = salary.getSalaryBonus().iterator().next();
		System.out.println(start + "..." + end + " = " + bonusDays);
		assertEquals(bonusDays == AonDateUtils.getMax(start, DATE) ? 30
				: bonusDays, salaryBonus.getAmount(), DELTA);

		start = getFirstDayOfMonth(add(bonusEnd, MONTH, 1));
		end = getLastDayOfMonth(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		bonusDays = AonDateUtils.get(bonusEnd, Calendar.DATE)
				- AonDateUtils.get(start, Calendar.DATE) + 1;
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		assertEquals(0, salary.getSalaryBonus().size());
	}

	@Test
	public void testBonusDaysII() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
					}
				});

		Date bonusEnd = AonDateUtils.add(getToday(), YEAR, 3);
		ContractBonusRecord bonus1 = addBonus(aonContext, contract,
				String.format("%s", BONUS_DAYS), getToday(), bonusEnd);
		addBonus(
				aonContext,
				contract,
				addBonusConcept(aonContext,
						String.format("%s * 2", BONUS_DAYS),
						BonusType.SOCIAL_SECURITY), getToday(), bonusEnd);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		int bonusDays = AonDateUtils.get(end, Calendar.DATE)
				- AonDateUtils.get(getToday(), Calendar.DATE) + 1;
		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder()).calculate(ctx);
		assertEquals(2, salary.getSalaryBonus().size());
		System.out.println(0 + "-." + start + "..." + end + " = " + bonusDays);
		for (SalaryBonus salaryBonus : salary.getSalaryBonus())
			assertEquals(
					(bonusDays == AonDateUtils.getMax(start, DATE) ? 30
							: bonusDays)
							* (salaryBonus.getDescription().equals(
									bonus1.getDescription()) ? 1 : 2),
					salaryBonus.getAmount(), DELTA);

		for (int i = 1; i <= 24; i++) {
			start = getFirstDayOfMonth(add(start, MONTH, 1));
			end = getLastDayOfMonth(start);
			ctx = new SQLContractSalaryCalculatorContext(connection, start,
					end, end, criteria);
			ctx.next();
			bonusDays = AonDateUtils.get(end, Calendar.DATE)
					- AonDateUtils.get(start, Calendar.DATE) + 1;
			salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
					.calculate(ctx);
			assertEquals(2, salary.getSalaryBonus().size());
			System.out.println(i + "-." + start + "..." + end + " = "
					+ bonusDays);
			for (SalaryBonus salaryBonus : salary.getSalaryBonus())
				assertEquals(
						(bonusDays == AonDateUtils.getMax(start, DATE) ? 30
								: bonusDays)
								* (salaryBonus.getDescription().equals(
										bonus1.getDescription()) ? 1 : 2),
						salaryBonus.getAmount(), DELTA);
		}

		start = getFirstDayOfMonth(bonusEnd);
		end = getLastDayOfMonth(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		bonusDays = AonDateUtils.get(bonusEnd, Calendar.DATE)
				- AonDateUtils.get(start, Calendar.DATE) + 1;
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		assertEquals(2, salary.getSalaryBonus().size());
		System.out.println(start + "..." + end + " = " + bonusDays);
		for (SalaryBonus salaryBonus : salary.getSalaryBonus())
			assertEquals(
					(bonusDays == AonDateUtils.getMax(start, DATE) ? 30
							: bonusDays)
							* (salaryBonus.getDescription().equals(
									bonus1.getDescription()) ? 1 : 2),
					salaryBonus.getAmount(), DELTA);

		start = getFirstDayOfMonth(add(bonusEnd, MONTH, 1));
		end = getLastDayOfMonth(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start, end,
				end, criteria);
		ctx.next();
		bonusDays = AonDateUtils.get(bonusEnd, Calendar.DATE)
				- AonDateUtils.get(start, Calendar.DATE) + 1;
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		assertEquals(0, salary.getSalaryBonus().size());
	}

	@Test
	public void tesAutoStartSection() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		
		Date startContract = add(getToday(), Calendar.DAY_OF_MONTH, -100);
		
		//@formatter:off
		ContractRecord contract = newContract(
			aonContext, 
			startContract, 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'100'");
				put(ContextVariable.MONTH_DAYS.getName(), "30.00");
			}
			},
			new String[] { 
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
					"500.00*DIAS_TRABAJADOS/DIAS_MES",
					"TRACE('DIAS_MES=%f\r\n',DIAS_MES);0.00",
					"TRACE('DIAS_TRABAJADOS=%f\r\n',DIAS_TRABAJADOS);0.00",
					"TRACE('DIAS_COTIZADOS=%f\r\n',DIAS_COTIZADOS);0.00"
					}, 
			new String[] {
					"TRACE('BASE_CGC = %f\r\n', BASE_CGC );BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					},
			null
		);
		//@formatter:on

		addSSRegimeData(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_CGC_E", "23.60");
					}
				});

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY,
				"( BASE_CGC_E = BASE_CGC) * PORCENTAJE_CGC_E/100");

		Date startBonus = add(getFirstDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 10);
		//@formatter:off
		BonusConceptRecord concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY,"/*read-only*/666/**/");
		addBonus(aonContext, contract, startBonus, concept);
		//@formatter:on



		// Current month. Not all days worked.
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection,
						getFirstDayOfMonth(getToday()),
						getLastDayOfMonth(getToday()),
						getLastDayOfMonth(getToday()), contract);

		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder).calculate(ctx);
		jooqSalaryBuilder.execute();

		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		Date prevBonus = add(startBonus, DAY_OF_MONTH, -1);
		Date lastDayOfMonth = getLastDayOfMonth(getToday());
		int monthDays = get(lastDayOfMonth, DAY_OF_MONTH);
		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()))
				.forEach(salary -> {

					// 500 Base de contingencias comunes.
					List<ContextData> datas = salary.getContextData()
							.get(CGC_BASE.getName());
					assertEquals(2, datas.size());
					assertEquals(firstDayOfMonth, datas.get(0).getStartDate());
					assertEquals(prevBonus, datas.get(0).getEndDate());
					assertEquals(1500.00 * 10.00 / monthDays,
							Double.parseDouble(datas.get(0).getExpression())
							, DELTA);
					assertEquals(startBonus, datas.get(1).getStartDate());
					assertEquals(lastDayOfMonth, datas.get(1).getEndDate());
					assertEquals(1500.00 *(monthDays-10)/ monthDays,
							Double.parseDouble(datas.get(1).getExpression())
							,DELTA);

					// 501 Base de Horas Extras Fuerza Mayor
//					datas = salary.getContextData()
//							.get(STRUCTURAL_OVERTIME_BASE.getName());
//					assertEquals(2, datas.size());
//					assertEquals(firstDayOfMonth, datas.get(0).getStartDate());
//					assertEquals(prevBonus, datas.get(0).getEndDate());
//					assertEquals(startBonus, datas.get(1).getStartDate());
//					assertEquals(lastDayOfMonth, datas.get(1).getEndDate());
//					assertEquals(0.00,
//							Double.parseDouble(datas.get(0).getExpression()));

					// 502 Base de Horas Extras
//					datas = salary.getContextData()
//							.get(NON_STRUCTURAL_OVERTIME_BASE.getName());
//					assertEquals(2, datas.size());
//					assertEquals(firstDayOfMonth, datas.get(0).getStartDate());
//					assertEquals(prevBonus, datas.get(0).getEndDate());
//					assertEquals(startBonus, datas.get(1).getStartDate());
//					assertEquals(lastDayOfMonth, datas.get(1).getEndDate());
//					assertEquals(0.00,
//							Double.parseDouble(datas.get(0).getExpression()));

					// 601 o 611 Base de Accidentes de Trabajo.
					datas = salary.getContextData().get(CGP_BASE.getName());
					assertEquals(2, datas.size());
					assertEquals(firstDayOfMonth, datas.get(0).getStartDate());
					assertEquals(prevBonus, datas.get(0).getEndDate());
					assertEquals(1500.00 * 10.00 / monthDays,
							Double.parseDouble(datas.get(0).getExpression())
							, DELTA);
					assertEquals(startBonus, datas.get(1).getStartDate());
					assertEquals(lastDayOfMonth, datas.get(1).getEndDate());
					assertEquals(1500.00 *(monthDays-10) / monthDays,
							Double.parseDouble(datas.get(1).getExpression())
							,DELTA);

				});
		;

	}

	@Test
	public void testBonusI() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemCosts(aonContext);
		
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY, "50");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY, "40");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_P",
				DeductionType.COMMON_CONTINGENCY, "10");
		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "NESTR",
				DeductionType.COMMON_CONTINGENCY, "0");
		
		ContractRecord contract = newContract(
				aonContext, 
				getFirstDayOfYear(getToday()), 
				new HashMap<String,String>(){
				{
					put(ContextVariable.TC2.getName(), "'100'");
					put(ContextVariable.MONTH_DAYS.getName(), "30.00");
				}
				},
				new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00*DIAS_TRABAJADOS/DIAS_MES",
						"TRACE('DIAS_MES=%f\r\n',DIAS_MES);0.00",
						"TRACE('DIAS_TRABAJADOS=%f\r\n',DIAS_TRABAJADOS);0.00",
						"TRACE('DIAS_COTIZADOS=%f\r\n',DIAS_COTIZADOS);0.00"
						}, 
				new String[] {
						"TRACE('BASE_CGC = %f\r\n', BASE_CGC );BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						},
				null
			);
		
		
		addBonus(aonContext, contract, "CUOTA_EMPRESARIAL* 0.60", contract.getStartDate(), null);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Salary salary =
		new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		assertEquals(1, salary.getSalaryBonus().size());
	
		for (SalaryBonus bonus : salary.getSalaryBonus()) {
			assertEquals(60.00, bonus.getAmount(), DELTA);
		}
		

		

	}

	@Test
	public void testTutoriaBonus() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemPayments(aonContext);
		
		addFormacionAndTutoriaBonus(aonContext);



		ContractRecord contract = newContract(
				aonContext, 
				getFirstDayOfYear(getToday()), 
				new HashMap<String,String>(){
				{
					put(ContextVariable.TC2.getName(), "'100'");
					put(ContextVariable.MONTH_DAYS.getName(), "30.00");
					
					// TUTORIA
					put(ContextVariable.SLD_H06.getName(), "10.00");
					put(ContextVariable.SLD_C737.getName(), "60.00");

				}
				},
				new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS/DIAS_MES",
						//"TRACE('HORAS_FORMACION_DISTANCIA=%f\r\n',HORAS_FORMACION_DISTANCIA);0.00",
						//"TRACE('HORAS_FORMACION_DISTANCIA=%f\r\n',HORAS_FORMACION_DISTANCIA);0.00",
						//"TRACE('DIAS_COTIZADOS=%f\r\n',DIAS_COTIZADOS);0.00"
						}, 
				new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						},
				null
			);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		calculator.setListener(new GenericContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(String message) {
				fail(message);
			}
		});

		Salary salary =
		calculator.calculate(
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		assertEquals(1, salary.getSalaryBonus().size());
	
		
		for (SalaryBonus bonus : salary.getSalaryBonus()) {
			System.out.println(bonus.getDescription() +" = " + bonus.getAmount() );
			if (AonStringUtils.equals(bonus.getDescription(), "BONIFICACI\u00D3N TUTORIA"))
				assertEquals(60.00, bonus.getAmount(), DELTA);
			else 
				fail("Unknown '" + bonus.getDescription() +"'");
		}
		
		calculateAndSave(connection, getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		Stream<com.esferalia.aon.occam.api.model.Salary> salaries = 
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()));
		
		salaries.forEach( s -> {
		    assertEquals(1, s.getBonuses().size());
		});

	}

	@Test
	public void testTutoriaWithNoHoursBonus() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemPayments(aonContext);
		
		addFormacionAndTutoriaBonus(aonContext);



		ContractRecord contract = newContract(
				aonContext, 
				getFirstDayOfYear(getToday()), 
				new HashMap<String,String>(){
				{
					put(ContextVariable.TC2.getName(), "'100'");
					put(ContextVariable.MONTH_DAYS.getName(), "30.00");
					
					// TUTORIA
					put(ContextVariable.SLD_C737.getName(), "60.00");

				}
				},
				new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}, 
				new String[] {
						},
				null
			);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		calculator.setListener(new GenericContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(String message) {
				throw new RuntimeException(message);
			}
		});
		
		try {
			Salary salary =
			calculator.calculate(
			getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
			
			assertEquals(0, salary.getSalaryBonus().size());
		
			
			fail("No hours message");
		} catch ( RuntimeException e ) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testFormacionDistanciaBonus() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemPayments(aonContext);
		
		addFormacionAndTutoriaBonus(aonContext);

		ContractRecord contract = newContract(
				aonContext, 
				getFirstDayOfYear(getToday()), 
				new HashMap<String,String>(){
				{
					put(ContextVariable.TC2.getName(), "'100'");
					put(ContextVariable.MONTH_DAYS.getName(), "30.00");
					
					put(ContextVariable.SLD_H04.getName(), "10.00");
					put(ContextVariable.SLD_C763.getName(), "60.00");

				}
				},
				new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS/DIAS_MES",
						}, 
				new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						},
				null
			);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		calculator.setListener(new GenericContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(String message) {
				fail(message);
			}
		});

		Salary salary =
		calculator.calculate(
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		assertEquals(1, salary.getSalaryBonus().size());
	
		
		for (SalaryBonus bonus : salary.getSalaryBonus()) {
			System.out.println(bonus.getDescription() +" = " + bonus.getAmount() );
			if (AonStringUtils.equals(bonus.getDescription(), "BONIF FORM. T.DISTAN"))
				assertEquals(60.00, bonus.getAmount(), DELTA);
			else 
				fail("Unknown '" + bonus.getDescription() +"'");
		}
		
		calculateAndSave(connection, getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		Stream<com.esferalia.aon.occam.api.model.Salary> salaries = 
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()));
		
		salaries.forEach( s -> {
		    assertEquals(1, s.getBonuses().size());
		});

		

	}

	@Test
	public void testFormacionPresencialBonus() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemPayments(aonContext);
		
		addFormacionAndTutoriaBonus(aonContext);

		ContractRecord contract = newContract(
				aonContext, 
				getFirstDayOfYear(getToday()), 
				new HashMap<String,String>(){
				{
					put(ContextVariable.TC2.getName(), "'100'");
					put(ContextVariable.MONTH_DAYS.getName(), "30.00");
					
					put(ContextVariable.SLD_H03.getName(), "10.00");
					put(ContextVariable.SLD_C763.getName(), "60.00");

				}
				},
				new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS/DIAS_MES",
						}, 
				new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						},
				null
			);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		calculator.setListener(new GenericContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(String message) {
				fail(message);
			}
		});

		Salary salary =
		calculator.calculate(
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		assertEquals(1, salary.getSalaryBonus().size());
	
		
		for (SalaryBonus bonus : salary.getSalaryBonus()) {
			System.out.println(bonus.getDescription() +" = " + bonus.getAmount() );
			if (AonStringUtils.equals(bonus.getDescription(), "BONIF FORM. T.PRESEN"))
				assertEquals(60.00, bonus.getAmount(), DELTA);
			else 
				fail("Unknown '" + bonus.getDescription() +"'");
		}
		

		

	}

	
	@Test
	public void testTutoriaAnFormacionBonus() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemPayments(aonContext);
		
		addFormacionAndTutoriaBonus(aonContext);



		ContractRecord contract = newContract(
				aonContext, 
				getFirstDayOfYear(getToday()), 
				new HashMap<String,String>(){
				{
					put(ContextVariable.TC2.getName(), "'100'");
					put(ContextVariable.MONTH_DAYS.getName(), "30.00");
					
					// TUTORIA
					put(ContextVariable.SLD_H06.getName(), "10.00");
					put(ContextVariable.SLD_C737.getName(), "60.00");

					put(ContextVariable.SLD_H04.getName(), "10.00");
					put(ContextVariable.SLD_C763.getName(), "60.00");

				}
				},
				new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS/DIAS_MES",
						//"TRACE('HORAS_FORMACION_DISTANCIA=%f\r\n',HORAS_FORMACION_DISTANCIA);0.00",
						//"TRACE('HORAS_FORMACION_DISTANCIA=%f\r\n',HORAS_FORMACION_DISTANCIA);0.00",
						//"TRACE('DIAS_COTIZADOS=%f\r\n',DIAS_COTIZADOS);0.00"
						}, 
				new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						},
				null
			);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		calculator.setListener(new GenericContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(String message) {
				fail(message);
			}
		});

		Salary salary =
		calculator.calculate(
		getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		assertEquals(2, salary.getSalaryBonus().size());
	
		
		for (SalaryBonus bonus : salary.getSalaryBonus()) {
			System.out.println(bonus.getDescription() +" = " + bonus.getAmount() );
			if (AonStringUtils.equals(bonus.getDescription(), "BONIFICACI\u00D3N TUTORIA"))
				assertEquals(60.00, bonus.getAmount(), DELTA);
			else if (AonStringUtils.equals(bonus.getDescription(), "BONIF FORM. T.DISTAN"))
				assertEquals(60.00, bonus.getAmount(), DELTA);
			else 
				fail("Unknown '" + bonus.getDescription() +"'");
		}
		

		

	}

	@Test
	public void testFormacionWithNoHoursBonus() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemPayments(aonContext);
		
		addFormacionAndTutoriaBonus(aonContext);



		ContractRecord contract = newContract(
				aonContext, 
				getFirstDayOfYear(getToday()), 
				new HashMap<String,String>(){
				{
					put(ContextVariable.TC2.getName(), "'100'");
					put(ContextVariable.MONTH_DAYS.getName(), "30.00");
					
					// TUTORIA
					put(ContextVariable.SLD_C763.getName(), "60.00");

				}
				},
				new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						}, 
				new String[] {
						},
				null
			);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		calculator.setListener(new GenericContractSalaryCalculator.Listener() {
			@Override
			public void onCheckError(String message) {
				throw new RuntimeException(message);
			}
		});
		
		try {
			Salary salary =
			calculator.calculate(
			getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
			
			assertEquals(0, salary.getSalaryBonus().size());
		
			
			fail("No hours message");
		} catch ( RuntimeException e ) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testDeductionBonus() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemPayments(aonContext);
		
		addFormacionAndTutoriaBonus(aonContext);



		ContractRecord contract = newContract(
				aonContext, 
				getFirstDayOfYear(getToday()), 
				new HashMap<String,String>(){
				{
					put(ContextVariable.TC2.getName(), "'100'");
					put(ContextVariable.MONTH_DAYS.getName(), "30.00");
					
				}
				},
				new String[] { 
						"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
						"500.00 * DIAS_TRABAJADOS/DIAS_MES",
						}, 
				new String[] {
						"BASE_CGC * 0.10", 
						"BASE_CGP * 0.05",
						},
				null
			);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		addBonus(aonContext, contract, startDate, endDate, 
			"/*pec:16, quota:08*/"
			+ "/*read.only*/"
			+ "CHECK(\"100\".indexOf(TC2) >= 0,  \"El contrato de ser del tipo 100\");"
			+ "SELF.addDeduction(\"BONIF\", \"BONIF. TEST DEDUCTION BONUS\", \"(-MIN(1000.00, CUOTA_TRABAJADOR))\");"
			+ "CUOTA_EMPRESA"
			+ "/**/" , 
			"BONIF. TEST DEDUCTION BONUS");
		
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder());
		Salary salary = calculator.calculate( getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
		
		for (com.esferalia.aon.payroll.SalaryDeduction deduction : salary.getSalaryDeductions()) {
			System.out.println(deduction.getDescription() +" = " + deduction.getAmount() );
		}
		

		assertEquals(3, salary.getSalaryDeductions().size());
		assertEquals(0.00, salary.getSocialSecurityContributions(), 0.00);

		

	}


	// ------------------------------------------------------------------------

	protected final ContractBonusRecord addBonus(AONContext aonContext,
			ContractRecord contract, String expression, Date startDate,
			Date endDate) {
		return aonContext.getDslContext().insertInto(CONTRACT_BONUS)
				.set(CONTRACT_BONUS.DOMAIN, contract.getDomain())
				.set(CONTRACT_BONUS.CONTRACT, contract.getId())
				.set(CONTRACT_BONUS.START_DATE, startDate)
				.set(CONTRACT_BONUS.END_DATE, endDate)
				.set(CONTRACT_BONUS.DESCRIPTION, expression)
				.set(CONTRACT_BONUS.EXPRESSION, expression).returning()
				.fetchOne();

	}

	protected final BonusConceptRecord addBonusConcept(AONContext aonContext,
			String expression, BonusType type) {
		DomainRecord domain = newDomain(aonContext);
		return aonContext.getDslContext().insertInto(BONUS_CONCEPT)
				.set(BONUS_CONCEPT.DOMAIN, domain.getId())
				.set(BONUS_CONCEPT.TYPE, (byte) type.ordinal())
				.set(BONUS_CONCEPT.EXPRESSION, expression)
				.set(BONUS_CONCEPT.DESCRIPTION, expression).returning()
				.fetchOne();

	}

	protected final ContractBonusRecord addBonus(AONContext aonContext,
			ContractRecord contract, BonusConceptRecord concept,
			Date startDate, Date endDate) {
		return aonContext.getDslContext().insertInto(CONTRACT_BONUS)
				.set(CONTRACT_BONUS.DOMAIN, contract.getDomain())
				.set(CONTRACT_BONUS.CONTRACT, contract.getId())
				.set(CONTRACT_BONUS.START_DATE, startDate)
				.set(CONTRACT_BONUS.END_DATE, endDate)
				.set(CONTRACT_BONUS.BONUS_CONCEPT, concept.getId()).returning()
				.fetchOne();

	}

	protected final void addFormacionAndTutoriaBonus(AONContext aonContext) {
		
		addSSRegimeData(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				null, 
				new HashMap<String, String>(){
					{
						put("HORAS_TUTORIA_MSG", 
							"\"<div>Para completar la <b>BONIFICACI\u00D3N TUTORIA</b> es necesario indicar las horas de tutoria ( HORAS_TUTORIA ).</div>"
							+"<div>&nbsp;</div>"
							+ "<div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\"");
						put("HORAS_FORM_DISTAN_MSG", 
							"\"<div>Para completar la <b>BONIF FORM. T.DISTAN</b> es necesario indicar las horas de formaci\u00f3n ( HORAS_FORMACION_DISTANCIA ).</div>"
							+"<div>&nbsp;</div>"
							+ "<div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\"");
						put("HORAS_FORM_PRESEN_MSG",
							"\"<div>Para completar la <b>BONIF FORM. T.PRESEN</b> es necesario indicar las horas de formaci\u00f3n ( HORAS_FORMACION_PRESENCIAL ).</div>"
							+"<div>&nbsp;</div>"
							+ "<div class='aon-text-right'><span class='aon-icon aon-icon-logo' />aon Solutions</div>\"");
								
					}
				});
		
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				PaymentType.CRA_0001, 
				"BONIFICACION_FORMACION_CONTINUA; "
				+ "isdef HORAS_FORMACION_DISTANCIA ? "
				+ "SELF.addBonus('BONIF FORM. T.DISTAN','HORAS_FORMACION_DISTANCIA; BONIFICACION_FORMACION_CONTINUA');HIDE() "
				+ ": isdef HORAS_FORMACION_PRESENCIAL ? HIDE() : HIDE(HORAS_FORM_DISTAN_MSG)"
				);
		
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				PaymentType.CRA_0001, 
				"BONIFICACION_FORMACION_CONTINUA; "
				+ "isdef HORAS_FORMACION_PRESENCIAL ? "
				+ "SELF.addBonus('BONIF FORM. T.PRESEN','HORAS_FORMACION_PRESENCIAL; BONIFICACION_FORMACION_CONTINUA');HIDE() "
				+ ": isdef HORAS_FORMACION_DISTANCIA ? HIDE() : HIDE(HORAS_FORM_PRESEN_MSG)"
				);

		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				getFirstDayOfYear(getToday()), 
				PaymentType.CRA_0001, 
				"BONIFICACION_TUTORIA; "
				+ "isdef HORAS_TUTORIA ? "
				+ "SELF.addBonus('BONIFICACI\u00D3N TUTORIA','HORAS_TUTORIA; BONIFICACION_TUTORIA');HIDE() "
				+ ": HIDE(HORAS_TUTORIA_MSG)"
				);
	}
	
	
}
