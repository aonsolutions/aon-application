/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractDeductionRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator.Listener;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase.Extra;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase.Payment;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;


/**
 * @author rtrepiana
 *
 */
public class SQLEmbargoTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.01;

	private static class SuccessException extends RuntimeException {

	}

	@Test
	public void testSalaryEmbargoI()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		addEmbargarFunction(aonContext);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
					"1550.00 * DIAS_TRABAJADOS / DIAS_MES"						
				},
				new String[] {}
		);
		
		
		
		addEmbargo(aonContext, contract, "TRACE('PENDIENTE: %f\r\n', (IMPORTE_EMBARGO +EMBARGADO));EMBARGAR(IMPORTE_EMBARGO)");
		
		
		addData(aonContext, contract, 
				getFirstDayOfYear(getToday()), 
				null,
				new HashMap<String, String>() {
					{
						put("TC2", "'100'");
						put("IMPORTE_EMBARGO", "300.00");
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals(600.00*0.30,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals(300.00 - 600.00*0.30, amount,DELTA);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.fail();;
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.fail();;
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		start = getFirstDayOfMonth(getToday());
		end = getLastDayOfMonth(start);

		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);


		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals(600.00*0.30,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
	}

	@Test
	public void testSalaryEmbargoII()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		addEmbargarFunction(aonContext);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
					"1550.00 * DIAS_TRABAJADOS / DIAS_MES"						
				},
				new String[] {}
		);
		
		
		
		addEmbargo(aonContext, contract, "TRACE('%f\r\n', (EMBARGADO));EMBARGAR(IMPORTE_EMBARGO)");
		
		
		addData(aonContext, contract, 
				getFirstDayOfYear(getToday()), 
				null,
				new HashMap<String, String>() {
					{
						put("TC2", "'100'");
						put("IMPORTE_EMBARGO", "160.00");
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals(160.00,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();


	}

	@Test
	public void testSalaryEmbargoDescription()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		addEmbargarFunction(aonContext);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
					"1550.00 * DIAS_TRABAJADOS / DIAS_MES"						
				},
				new String[] {}
		);
		
		
		
		addEmbargo(aonContext, contract, "PTE. EMBARGAR: @{PENDIENTE} Eur., (EMBARGADO: @{EMBARGADO} Eur.)", "EMBARGAR(IMPORTE_EMBARGO)") ;
		
		
		addData(aonContext, contract, 
				getFirstDayOfYear(getToday()), 
				null,
				new HashMap<String, String>() {
					{
						put("TC2", "'100'");
						put("IMPORTE_EMBARGO", "300.00");
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals("PTE. EMBARGAR: 120.0 Eur., (EMBARGADO: 180.0 Eur.)", description);
				org.junit.Assert.assertEquals(600.00*0.30,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals("PTE. EMBARGAR: 0.0 Eur., (EMBARGADO: 300.0 Eur.)", description);
				org.junit.Assert.assertEquals(300.00 - 600.00*0.30, amount,DELTA);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.fail();;
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
	}

	@Test
	public void testSalaryEmbargoAdvance()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		addEmbargarFunction(aonContext);

		ContractRecord contract = newContract(aonContext,
				new String[] { 
					"1550.00 * DIAS_TRABAJADOS / DIAS_MES"						
				},
				new String[] {}
		);
		
		addEmbargo(aonContext, contract, "TRACE('%f\r\n', (EMBARGADO));EMBARGAR(IMPORTE_EMBARGO)");
		
		
		
		addData(aonContext, contract, 
				getFirstDayOfYear(getToday()), 
				null,
				new HashMap<String, String>() {
					{
						put("TC2", "'100'");
						put("IMPORTE_EMBARGO", "300.00");
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ContractDeductionRecord anticipo = 
		addDeduction(aonContext, contract, start, end, "750.00", "ANTICIPO...", "ANTICIPO");
		
		anticipo.setType((byte)DeductionType.ADVANCE_PAYMENT.ordinal());
		anticipo.update();
		

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals(600.00*0.30,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals(300.00 - 600.00*0.30, amount,DELTA);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.fail();;
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);
		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.fail();;
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		start = getFirstDayOfMonth(getToday());
		end = getLastDayOfMonth(start);

		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);


		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals(600.00*0.30,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
	}

	@Test
	public void testSalaryEmbargoExtraI()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		addEmbargarFunction(aonContext);
		
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JULY;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "01/07";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "550");
				put("SALARIO_MENSUAL", "1000.00");
			}
		});
		

		ContractRecord contract = newContract(aonContext,
				new String[] {},
				new String[] {},
				category
		);
		
		
		
		addEmbargo(aonContext, contract, "TRACE('EMBARGADO:%f\r\n', (EMBARGADO));EMBARGAR(IMPORTE_EMBARGO)");
		
		
		addData(aonContext, contract, 
				getFirstDayOfYear(getToday()), 
				null,
				new HashMap<String, String>() {
					{
						put("TC2", "'100'");
						put("IMPORTE_EMBARGO", "500.00");
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {

			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals((1550.00 - (950.00*12/14))*0.30  ,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals((1550.00 - (950.00*12/14))*0.30  ,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 15);
		Date issueDate = new Date(calendar.getTimeInMillis());
		int year = calendar.get(Calendar.YEAR);

		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		AgreementExtraRecord decemberExtra = getExtra(aonContext, agreement.getId(), "15/12");
		ISQLContractSalaryCalculatorContext extraCtx = getExtraSalaryCalculatorContext(connection, contract, decemberExtra, year, issueDate);
		SmartContractSalaryCalculator<ISalary> contractSalaryCalculator = new SmartContractSalaryCalculator<ISalary>(new JooqSalaryBuilder<ISalary>(connection){
				@Override
				public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
						Map<String, ITimedVariable<?>> context) {
					org.junit.Assert.assertEquals(500.00 - 2 * (1550.00 - (950.00*12/14))*0.30  ,amount,0.01);
					super.addEmbargo(id, amount, description, embargo, context);
				}
				
				@Override
				public void setTotalLiquid(Double totalLiquid) {
					org.junit.Assert.assertEquals(1550.00 - (500.00 - 2 * (1550.00 - (950.00*12/14))*0.30)  ,totalLiquid,0.01);
					super.setTotalLiquid(totalLiquid);
				}
		});
		contractSalaryCalculator.setListener(new Listener() {
		});
		contractSalaryCalculator.calculate(extraCtx);		

		
	}

	@Test
	public void testSalaryEmbargoSettleI()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();

		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		addEmbargarFunction(aonContext);
		
		PaymentConceptRecord conceptPagaExtra = addConcept(aonContext, "PAGA_EXTRA", PaymentType.CRA_0004);
		PaymentConceptRecord conceptSalarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord conceptPlusSalarial = addConcept(aonContext, "PLUS_SALARIAL");

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext,
				new Extra[] { 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "SALARIO_BASE + PLUS_SALARIAL";
							this.month = Month.JULY;
							this.start = "01/07 -1";
							this.end = "30/06";
							this.issue = "01/07";
						}
					}, 
					new Extra() {
						{
							this.concept = conceptPagaExtra.getId();
							this.expression = "(SALARIO_BASE + PLUS_SALARIAL)";
							this.month = Month.DECEMBER;
							this.start = "01/01";
							this.end = "31/12";
							this.issue = "15/12";
						}
					}, 
				},
				new Payment[] {
						new Payment() {
							{
								this.concept = conceptSalarioBase.getId();
								this.expression = "SALARIO_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						},
						new Payment() {
							{
								this.concept = conceptPlusSalarial.getId();
								this.expression = "PLUS_MENSUAL * DIAS_TRABAJADOS/DIAS_MES";
							}
						}
				});
		
		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String,String>(){
			{
				put("PLUS_MENSUAL", "550");
				put("SALARIO_MENSUAL", "1000.00");
			}
		});
		

		ContractRecord contract = newContract(aonContext,
				new String[] {},
				new String[] {},
				category
		);
		
		
		
		addEmbargo(aonContext, contract, "TRACE('EMBARGADO:%f\r\n', (EMBARGADO));EMBARGAR(IMPORTE_EMBARGO)");
		
		
		addData(aonContext, contract, 
				getFirstDayOfYear(getToday()), 
				null,
				new HashMap<String, String>() {
					{
						put("TC2", "'100'");
						put("IMPORTE_EMBARGO", "500.00");
						put("GRUPO_COTIZACION", "\"01\"");
					}
				});

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();

		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {

			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals((1550.00 - (950.00*12/14))*0.30  ,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		start = add(start, Calendar.MONTH,1);
		end = getLastDayOfMonth(start);
		ctx = getContractSalaryCalculatorContext(
				connection, start, end, end, contract);

		jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection) {
			@Override
			public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
					Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.assertEquals((1550.00 - (950.00*12/14))*0.30  ,amount,0.01);
				super.addEmbargo(id, amount, description, embargo, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();

		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(MONTH, Calendar.DECEMBER);
		calendar.set(DAY_OF_MONTH, 11);
		Date settleDate = new Date(calendar.getTimeInMillis());

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		ISQLContractSalaryCalculatorContext settleCtx = new SmartSQLContractSettleCalculatorContext(connection, contract.getStartDate(),
				settleDate, settleDate, criteria);
		settleCtx.next();
		
		SmartContractSalaryCalculator<ISalary> contractSalaryCalculator = new SmartContractSalaryCalculator<ISalary>(new JooqSalaryBuilder<ISalary>(connection){
				
				@Override
				public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
					System.out.println(description + " : " +amount );
					super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				}
			
				@Override
				public void addEmbargo(Integer id, Double amount, String description, IDeduction embargo,
						Map<String, ITimedVariable<?>> context) {
					org.junit.Assert.assertEquals(500.00 - 2 * (1550.00 - (950.00*12/14))*0.30  ,amount,0.01);
					super.addEmbargo(id, amount, description, embargo, context);
				}
				

		});
		contractSalaryCalculator.setListener(new Listener() {
		});
		contractSalaryCalculator.calculate(settleCtx);		

		
	}
	
	
	private void addEmbargarFunction(AONContext aonContext) {
		
		addSystemData(aonContext,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("SMI","950.00 * 12 / 14");
						put("PENDIENTE","0.00");
						put("PAGAS_PRORRATEADAS","FALSO()");
						put("MAX_EMBARGABLE", "def(total_liquido){ "
								+ "_smi = ( PAGAS_PRORRATEADAS ? SMI * 14 /12 : SMI);"
								+ " MAX(((total_liquido - _smi) * 0.30),0)"
								+ " + MAX(((total_liquido - 2 * _smi ) * 0.20),0)"
								+ " + MAX((( total_liquido - 3 * _smi ) * 0.10),0)"
								+ " + MAX((( total_liquido - 4 * _smi ) * 0.15),0)"
								+ " + MAX((( total_liquido - 5 * _smi ) * 0.15),0)"
								+ "}");
						put("EMBARGAR","def (EMBARGO){ PENDIENTE = ( EMBARGO + EMBARGADO ); E=((PENDIENTE > 0) ? MIN(MAX_EMBARGABLE(TOTAL_LIQUIDO), PENDIENTE ) : REMOVE()); SELF.addVariable('PENDIENTE',PENDIENTE - E); SELF.addVariable('EMBARGADO',-1*(EMBARGADO - E)); E;  }");
						
					}
				});
	}


}
