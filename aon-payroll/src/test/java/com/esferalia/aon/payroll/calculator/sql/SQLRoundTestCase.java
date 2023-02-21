/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.SystemDeduction.SYSTEM_DEDUCTION;
import static com.esferalia.aon.payroll.enumeration.SSRegimeType.GENERAL;
import static com.esferalia.aon.salary.enumeration.DeductionType.COMMON_CONTINGENCY;
import static com.esferalia.aon.salary.enumeration.DeductionType.FOGASA;
import static com.esferalia.aon.salary.enumeration.DeductionType.IRPF;
import static com.esferalia.aon.salary.enumeration.DeductionType.JOB_TRAINING;
import static com.esferalia.aon.salary.enumeration.DeductionType.NON_STRUCTURAL_OVERTIME;
import static com.esferalia.aon.salary.enumeration.DeductionType.PROFESSIONAL_CONTINGENCY;
import static com.esferalia.aon.salary.enumeration.DeductionType.STRUCTURAL_OVERTIME;
import static com.esferalia.aon.salary.enumeration.DeductionType.UNEMPLOYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DeductionConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionContext;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

/**
 * @author rtrepiana
 *
 */
public class SQLRoundTestCase extends AbstractSQLTestCase {

	private static final double FLOATING_POINT_ERROR = 0.0000000001;


	@Test
	public void testRoundDeductionsAndCostsI()
			throws ExpressionException, SQLException, SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		try {

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemDeductions(aonContext);
		Salary salary = calculate(new String[] { "1562.19" }, connection, aonContext);
		
		salary.getSalaryDeductions().forEach(d -> System.out.println(d.getType() + " : " + d.getAmount() ));
		salary.getSalaryCosts().forEach(d -> System.out.println(d.getType() + " : " + d.getAmount() ));
		
		assertTotalPayment(salary);
		assertCommanBase(salary);
		
		Assert.assertEquals(99.19 , salary.getSocialSecurityContributions(),0.00);
		Assert.assertEquals(490.54 , salary.getTotalEnterprise(),0.00);
		
		salary.getSalaryDeductions().forEach(d -> {
			switch (d.getType()) {
			case COMMON_CONTINGENCY:
				Assert.assertEquals(73.42, d.getAmount(),0.00);
				break;
			case UNEMPLOYMENT:
				Assert.assertEquals(24.21, d.getAmount(),0.00);
				break;
			case JOB_TRAINING:
				Assert.assertEquals(1.56, d.getAmount(),0.00);
				break;
			case IRPF:
				break;
			default:
				Assert.fail("Unknown deduction " + d.getType() + ", " + d.getDescription());
			}
		});

		salary.getSalaryCosts().forEach(c -> {
			switch (c.getType()) {
			case COMMON_CONTINGENCY:
				Assert.assertEquals(368.68, c.getAmount(),0.00);
				break;
			case UNEMPLOYMENT:
				Assert.assertEquals(85.92, c.getAmount(),0.00);
				break;
			case JOB_TRAINING:
				Assert.assertEquals(9.38, c.getAmount(),0.00);
				break;
			case FOGASA:
				Assert.assertEquals(3.12, c.getAmount(),0.00);
				break;
			case PROFESSIONAL_CONTINGENCY:
				if ( c.getName().equals("IT_E"))
					Assert.assertEquals(12.50, c.getAmount(),0.00);
				else if ( c.getName().equals("IMS_E"))
					Assert.assertEquals(10.94, c.getAmount(),0.00);
				break;
			default:
				Assert.fail("Unknown deduction " + c.getType() + ", " + c.getDescription());
			}
		});
		} finally {
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemDeductions(aonContext);
		}
	}

	@Test
	public void testRoundDeductionsAndCostsII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		try {

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemDeductions(aonContext);
		
		Salary salary = calculate(new String[] {"2455.354345238095"}, connection, aonContext);
		
		salary.getSalaryDeductions().forEach(d -> System.out.println(d.getType() + " : " + d.getAmount() ));
		salary.getSalaryCosts().forEach(d -> System.out.println(d.getType() + " : " + d.getAmount() ));
		
		assertTotalPayment(salary);
		assertCommanBase(salary);
		//Assert.assertEquals(99.19 , salary.getSocialSecurityContributions(),0.00);
		//Assert.assertEquals(490.54 , salary.getTotalEnterprise(),0.00);
		
		salary.getSalaryDeductions().forEach(d -> {
			switch (d.getType()) {
			case COMMON_CONTINGENCY:
				Assert.assertEquals(115.40, d.getAmount(),0.00);
				break;
			case UNEMPLOYMENT:
				Assert.assertEquals(38.06, d.getAmount(),0.00);
				break;
			case JOB_TRAINING:
				Assert.assertEquals(2.46, d.getAmount(),0.00);
				break;
			case IRPF:
				break;
			default:
				Assert.fail("Unknown deduction " + d.getType() + ", " + d.getDescription());
			}
		});

		salary.getSalaryCosts().forEach(c -> {
			switch (c.getType()) {
			case COMMON_CONTINGENCY:
				Assert.assertEquals(579.46, c.getAmount(),0.00);
				break;
			case UNEMPLOYMENT:
				Assert.assertEquals(135.04, c.getAmount(),0.00);
				break;
			case JOB_TRAINING:
				Assert.assertEquals(14.73, c.getAmount(),0.00);
				break;
			case FOGASA:
				Assert.assertEquals(4.91, c.getAmount(),0.00);
				break;
			case PROFESSIONAL_CONTINGENCY:
				if ( c.getName().equals("IT_E"))
					Assert.assertEquals(19.64, c.getAmount(),0.00);
				else if ( c.getName().equals("IMS_E"))
					Assert.assertEquals(17.19, c.getAmount(),0.00);
				break;
			default:
				Assert.fail("Unknown deduction " + c.getType() + ", " + c.getDescription());
			}
		});
		} 
		finally {
			cleanSystemData(aonContext);
			cleanSystemCosts(aonContext);
			cleanSystemDeductions(aonContext);
		}
	}

	@Test
	public void testRoundDeductionsAndCostsIII()
			throws ExpressionException, SQLException, SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		try {

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemDeductions(aonContext);
		
		Salary salary = calculate(new String[] {
				"2000.306000000000",
				 "455.048345238095"
		}, connection, aonContext);
		
		salary.getSalaryDeductions().forEach(d -> System.out.println(d.getType() + " : " + d.getAmount() ));
		salary.getSalaryCosts().forEach(d -> System.out.println(d.getType() + " : " + d.getAmount() ));
		
		assertTotalPayment(salary);
		assertCommanBase(salary);
		
		Assert.assertEquals(2455.35 , salary.getCommonBase(),0.00);
		//Assert.assertEquals(99.19 , salary.getSocialSecurityContributions(),0.00);
		//Assert.assertEquals(490.54 , salary.getTotalEnterprise(),0.00);
		
		salary.getSalaryDeductions().forEach(d -> {
			switch (d.getType()) {
			case COMMON_CONTINGENCY:
				Assert.assertEquals(115.40, d.getAmount(),0.00);
				break;
			case UNEMPLOYMENT:
				Assert.assertEquals(38.06, d.getAmount(),0.00);
				break;
			case JOB_TRAINING:
				Assert.assertEquals(2.46, d.getAmount(),0.00);
				break;
			case IRPF:
				break;
			default:
				Assert.fail("Unknown deduction " + d.getType() + ", " + d.getDescription());
			}
		});

		salary.getSalaryCosts().forEach(c -> {
			switch (c.getType()) {
			case COMMON_CONTINGENCY:
				Assert.assertEquals(579.46, c.getAmount(),0.00);
				break;
			case UNEMPLOYMENT:
				Assert.assertEquals(135.04, c.getAmount(),0.00);
				break;
			case JOB_TRAINING:
				Assert.assertEquals(14.73, c.getAmount(),0.00);
				break;
			case FOGASA:
				Assert.assertEquals(4.91, c.getAmount(),0.00);
				break;
			case PROFESSIONAL_CONTINGENCY:
				if ( c.getName().equals("IT_E"))
					Assert.assertEquals(19.64, c.getAmount(),0.00);
				else if ( c.getName().equals("IMS_E"))
					Assert.assertEquals(17.19, c.getAmount(),0.00);
				break;
			default:
				Assert.fail("Unknown deduction " + c.getType() + ", " + c.getDescription());
			}
		});
		} finally {
			cleanSystemData(aonContext);
			cleanSystemCosts(aonContext);
			cleanSystemDeductions(aonContext);
		}
	}

	@Test
	public void testRoundLiquidAndTotalDeduction()
			throws ExpressionException, SQLException, SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		try {

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemDeductions(aonContext);
		
		Salary salary = calculate(new String[] {
				"2000.306000000000",
				 "455.048345238095"
		}, connection, aonContext);
		
		salary.getSalaryDeductions().forEach(d -> System.out.println(d.getType() + " : " + d.getAmount() ));
		salary.getSalaryCosts().forEach(d -> System.out.println(d.getType() + " : " + d.getAmount() ));
		
		assertTotalPayment(salary);
		assertCommanBase(salary);
		
		Assert.assertEquals(2455.35 , salary.getCommonBase(),0.00);
		//Assert.assertEquals(99.19 , salary.getSocialSecurityContributions(),0.00);
		//Assert.assertEquals(490.54 , salary.getTotalEnterprise(),0.00);
		
		double deductions = salary.getSalaryDeductions().stream().collect(Collectors.summingDouble(d->d.getAmount()));
		Assert.assertEquals(deductions, salary.getTotalDeduction(),0.00);

		
		
		Assert.assertEquals(salary.getTotalPayment() - deductions, salary.getTotalLiquid(),FLOATING_POINT_ERROR);

		} finally {
			cleanSystemData(aonContext);
			cleanSystemCosts(aonContext);
			cleanSystemDeductions(aonContext);
		}
	}

	@Test
	public void testRoundLiquidAndTotalDeductionWithEmbargos()
			throws ExpressionException, SQLException, SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		try {

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemDeductions(aonContext);
		
		Salary salary = 
		calculate(
		new String[] {
		"2000.306000000000",
		"455.048345238095"
		}, 
		new String[] {
		"20.0666666666666",
		"20.0466666666666",
		}, 
		connection, 
		aonContext);
		

		salary.getSalaryEmbargos().forEach(d -> System.out.println(d.getType() + " : " + d.getAmount() ));
		salary.getSalaryDeductions().forEach(d -> System.out.println(d.getType() + " : " + d.getAmount() ));
		salary.getSalaryCosts().forEach(d -> System.out.println(d.getType() + " : " + d.getAmount() ));
		
		assertTotalPayment(salary);
		assertCommanBase(salary);

		Assert.assertEquals(2455.35 , salary.getCommonBase(),0.00);
		//Assert.assertEquals(99.19 , salary.getSocialSecurityContributions(),0.00);
		//Assert.assertEquals(490.54 , salary.getTotalEnterprise(),0.00);
		
		double deductions = salary.getSalaryDeductions().stream().collect(Collectors.summingDouble(d->d.getAmount()));
		Assert.assertEquals(deductions, salary.getTotalDeduction(),0.00);

		double embargos = salary.getSalaryEmbargos().stream().collect(Collectors.summingDouble(d->d.getAmount()));
		Assert.assertEquals(20.07+ 20.05, embargos ,0.00);
		
		System.out.println("Embargos : " + embargos);
		System.out.println("Deductions : " + deductions);
		System.out.println("Payment : " + salary.getTotalPayment());
		
		Assert.assertEquals(salary.getTotalPayment() - deductions - 40.12 , salary.getTotalLiquid(),FLOATING_POINT_ERROR);

		} finally {
			cleanSystemData(aonContext);
			cleanSystemCosts(aonContext);
			cleanSystemDeductions(aonContext);
		}
	}

	void assertTotalPayment(Salary salary) {
	    double totalPayment = 
	    salary.getSalaryPayments().stream().map( p -> Math.round(p.getAmount()*100)/100.00 ).map( d -> BigDecimal.valueOf(d)).reduce(BigDecimal.ZERO, (d1,d2) -> d1.add(d2)).doubleValue();
	    Assert.assertEquals(totalPayment, salary.getTotalPayment(),0.00);
	}

	void assertCommanBase(Salary salary) {
	    double commonBase = 
	    salary.getSalaryPayments().stream().map( p -> Math.round(p.getQuote()*100)/100.00 ).map( d -> BigDecimal.valueOf(d)).reduce(BigDecimal.ZERO, (d1,d2) -> d1.add(d2)).doubleValue();
	    Assert.assertEquals(commonBase, salary.getCommonBase(),0.00);
	}

	@Test
	public void testIrpfQuotasI()
			throws ExpressionException, SQLException, SalaryException {
		testIrpfQuotas(
			new Payment() { 
				{ 
					type = PaymentType.CRA_0000; 
					description = "SALARIO BASE"; 
					expression = "1111.23456681 * DIAS_TRABAJADOS / DIAS_MES";
				}
			}
		);
	}

	@Test
	public void testIrpfQuotasII()
			throws ExpressionException, SQLException, SalaryException {
		testIrpfQuotas(
			new Payment() { 
				{ 
					type = PaymentType.CRA_0001; 
					description = "SALARIO BASE"; 
					expression = "1111.1111111234 * DIAS_TRABAJADOS / DIAS_MES";
				}
			},
			new Payment() { 
				{ 
					description = "PLUS TRANSPORTE Y DISTANCIA"; 
					expression =  "33.33334567 * DIAS_TRABAJADOS / DIAS_MES"; 
					type =  PaymentType.CRA_0032;
				}
			},
			new Payment() { 
				{ 
					description = "PRORRATEO PAGA EXTRAORDINARIA"; 
					expression =  "SALARIO_BASE / 12.00 * 2.00"; 
					type =  PaymentType.CRA_0004;
				}
			},
			new Payment() { 
				{ 
					description = "RETRIBUCIÓN EN ESPECIE"; 
					expression =  "SALARIO_BASE * 0.10"; 
					type =  PaymentType.CRA_0013;
				}
			},
			new Payment() { 
				{ 
					description = "HORAS COMPLEMENTARIAS PACTADAS"; 
					expression =  "22.22335445546"; 
					type =  PaymentType.CRA_0016;
				}
			},
			new Payment() { 
				{ 
					description = "GASTOS DE LOCMOCIÓN Y DISTANCIA"; 
					expression =  "69.6969696"; 
					type =  PaymentType.CRA_0042;
				}
			}
		);
	}

	@Test
	public void testIrpfQuotasIII()
			throws ExpressionException, SQLException, SalaryException {
		testIrpfQuotas(
			new Payment() { 
				{ 
					type = PaymentType.CRA_0000; 
					description = "SALARIO BASE"; 
					expression = "0.00 * DIAS_TRABAJADOS / DIAS_MES";
				}
			}
		);
	}

	@Test
	public void testIrpfNoQuotas()
			throws ExpressionException, SQLException, SalaryException {
		testIrpfQuotas(
				Collections.singletonMap(ContextVariable.IRPF_PERCENT.getName(), "0.00"),
				new Payment() { 
					{ 
						type = PaymentType.CRA_0001; 
						description = "SALARIO BASE"; 
						expression = "1111.1111111234 * DIAS_TRABAJADOS / DIAS_MES";
					}
				},
				new Payment() { 
					{ 
						description = "PLUS TRANSPORTE Y DISTANCIA"; 
						expression =  "33.33334567 * DIAS_TRABAJADOS / DIAS_MES"; 
						type =  PaymentType.CRA_0032;
					}
				},
				new Payment() { 
					{ 
						description = "PRORRATEO PAGA EXTRAORDINARIA"; 
						expression =  "SALARIO_BASE / 12.00 * 2.00"; 
						type =  PaymentType.CRA_0004;
					}
				},
				new Payment() { 
					{ 
						description = "RETRIBUCIÓN EN ESPECIE"; 
						expression =  "SALARIO_BASE * 0.10"; 
						type =  PaymentType.CRA_0013;
					}
				},
				new Payment() { 
					{ 
						description = "HORAS COMPLEMENTARIAS PACTADAS"; 
						expression =  "22.22335445546"; 
						type =  PaymentType.CRA_0016;
					}
				},
				new Payment() { 
					{ 
						description = "GASTOS DE LOCMOCIÓN Y DISTANCIA"; 
						expression =  "69.6969696"; 
						type =  PaymentType.CRA_0042;
					}
				}
		);
	}

	public void testIrpfQuotas(Payment ...payments)
			throws ExpressionException, SQLException, SalaryException {
		testIrpfQuotas(Collections.EMPTY_MAP, payments);
	}

	public void testIrpfQuotas(Map<String,String> datas, Payment ...payments)
			throws ExpressionException, SQLException, SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		try {

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemDeductions(aonContext);
		
		ContractRecord contract = newContract(aonContext, getFirstDayOfMonth(getToday()), datas);
		
		for (Payment payment : payments)
			addPayment(aonContext, contract, payment.description, payment.expression, "_P", "_P", payment.type);
		
		Salary salary = calculate(connection, aonContext, contract);
		
		salary.getSalaryPayments().forEach( p -> System.out.println( "[" + p.getType().name() + "] " + p.getDescription() + "\t\t:" + p.getIrpf() ));
		
		System.out.println( salary.getIrpfBase() + " = " + salary.getInMoneyIrpfBase() + "+" + salary.getInkindIrpfBase());
		
		Assert.assertEquals(salary.getIrpfBase(), salary.getInMoneyIrpfBase() + salary.getInkindIrpfBase() ,FLOATING_POINT_ERROR);
		
		Map<String, Double> irpfQuotas =
		salary.getSalaryDatas()
		.stream()
		.filter(d -> d.getName().startsWith("CRA"))
		.filter(d -> d.getName().endsWith("_IRPF"))
		.peek(d -> System.out.println(d.getName() + " = " + d.getExpression() ))
		.collect(Collectors.toMap( d -> d.getName(), d -> {
			try {
				return Double.parseDouble(d.getExpression());
			} catch ( Throwable t) {
				return 0.00;
			}
		} ))
		;
		
		double totalIrpf = irpfQuotas.values().stream().collect( Collectors.summingDouble( d -> d ));
		Assert.assertEquals(salary.getTotalIrpf(), totalIrpf ,FLOATING_POINT_ERROR);
		
		Map<String, Double> irpfBases =
		salary.getSalaryDatas()
		.stream()
		.filter(d -> d.getName().startsWith("CRA"))
		.filter(d -> d.getName().endsWith("_BASE"))
		.peek(d -> System.out.println(d.getName() + " = " + d.getExpression() ))
		.collect(Collectors.toMap( d -> d.getName(), d -> {
			try {
				return Double.parseDouble(d.getExpression());
			} catch ( Throwable t) {
				return 0.00;
			}
		} ))
		;

		double irpfBase = irpfBases.values().stream().collect( Collectors.summingDouble( d -> d ));
		Assert.assertEquals(salary.getIrpfBase(), irpfBase ,FLOATING_POINT_ERROR);

		assertTotalPayment(salary);

		} finally {
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemDeductions(aonContext);
		}
	}

	@Test
	public void testRoundPaymentsI()
			throws ExpressionException, SQLException, SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		try {

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemDeductions(aonContext);
		
		Salary salary = calculate(new String[] { "974.72", "974.72/12.00", "974.72/12", "974.72/12", "199.65 / 12" }, connection, aonContext);
		
		assertTotalPayment(salary);
		assertCommanBase(salary);

		} finally {
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemDeductions(aonContext);
		}
	}

	@Test
	public void testRoundPaymentsII()
			throws ExpressionException, SQLException, SalaryException {
		
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		try {

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemDeductions(aonContext);
		
		//Salary salary = calculate(new String[] { "974.72", "974.72/12.00", "974.72/12", "974.72/12", "199.65 / 12"}, connection, aonContext);
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"947.72 * DIAS_TRABAJADOS / DIAS_MES" ,
				"P_0 / 12.00",
				"P_0 / 12.00",
				"P_0 / 12.00",
				"196.65 / 12.00  * DIAS_TRABAJADOS / DIAS_MES",
				}, 
				new String[] {}, null);
		//@formatter:on
		
		addPayment(aonContext, contract, "DIAS_ENFERMEDAD_COMUN_1_3 * 0.00", "DIAS_ENFERMEDAD_COMUN_1_3 * BASE_REGULADORA");
		addPayment(aonContext, contract, "DIAS_ENFERMEDAD_COMUN_4_15 * BASE_REGULADORA * 0.60", "DIAS_ENFERMEDAD_COMUN_4_15 * BASE_REGULADORA");

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		Date endITDate = add(startITDate, DAY_OF_MONTH, 10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				endITDate, null);

		Salary salary = calculate(connection, aonContext, contract);
		
		assertTotalPayment(salary);
		assertCommanBase(salary);
		
		salary.getSalaryPayments().forEach( p -> Assert.assertFalse(p.getAmount() == 0.01 ));
		salary.getSalaryPayments().forEach( p -> System.out.println(p.getDescription() + " = " + p.getAmount()));

		} finally {
		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);
		cleanSystemDeductions(aonContext);
		}
	}

	private Salary calculate(
			String[] payments,
			Connection connection, 
			AONContext aonContext)
			throws SalaryException, ExpressionException, SQLException {
		return calculate(payments, new String [] {}, connection, aonContext);
	}

	private Salary calculate(
			String[] payments,
			String [] embargos,
			Connection connection, 
			AONContext aonContext)
			throws SalaryException, ExpressionException, SQLException {
		ContractRecord contract = newContract(aonContext,
				payments,
				new String[] {
				}
		);
		
		for (String embargo : embargos) {
			addEmbargo(aonContext, contract, embargo);
		}
		
		return calculate(connection, aonContext, contract);
	}

	private Salary calculate(
			Connection connection, 
			AONContext aonContext,
			ContractRecord contract)
			throws SalaryException, ExpressionException, SQLException {
		
		Date firstDayOfYear = AonDateUtils.getFirstDayOfYear(contract.getStartDate());
		
		addSSRegimeData(aonContext, GENERAL, firstDayOfYear, null , new HashMap<String, String>(){
			{
				put("PORCENTAJE_CGC", "4.70");
				put("PORCENTAJE_EXTR", "2.00");
				put("PORCENTAJE_NEXTR", "4.70");
				put("PORCENTAJE_DESMPL", "1.55");
				put("PORCENTAJE_FP", "0.10");

				put("PORCENTAJE_CGC_E", "23.60");
				put("PORCENTAJE_EXTR_E", "12.00");
				put("PORCENTAJE_NEXTR_E", "23.60");
				put("PORCENTAJE_DESMPL_E", "5.50");
				put("PORCENTAJE_FP_E", "0.60");
				put("PORCENTAJE_IT", "0.80");
				put("PORCENTAJE_IMS", "0.70");
				put("PORCENTAJE_FOGASA", "0.20");

				put("PORCENTAJE_IRPF", "1.10");
			}
		});
		
		addSSRegimeDeduction(aonContext, GENERAL, firstDayOfYear, "CGC", COMMON_CONTINGENCY, "BASE_CGC * PORCENTAJE_CGC/100");
		addSSRegimeDeduction(aonContext, GENERAL, firstDayOfYear, "EXTR", STRUCTURAL_OVERTIME, "BASE_ESTR * PORCENTAJE_EXTR/100");
		addSSRegimeDeduction(aonContext, GENERAL, firstDayOfYear, "NEXTR", NON_STRUCTURAL_OVERTIME, "BASE_NESTR * PORCENTAJE_NEXTR/100");
		addSSRegimeDeduction(aonContext, GENERAL, firstDayOfYear, "DESMPL", UNEMPLOYMENT, "BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100 ");
		addSSRegimeDeduction(aonContext, GENERAL, firstDayOfYear, "FP", JOB_TRAINING, "BASE_CGP * PORCENTAJE_FP/100");

		addSSRegimeDeduction(aonContext, GENERAL, firstDayOfYear, "IRPF", IRPF, "BASE_IRPF * PORCENTAJE_IRPF/100");

		addSSRegimeCost(aonContext, GENERAL, firstDayOfYear, "CGC_E", COMMON_CONTINGENCY, "BASE_CGC_E * PORCENTAJE_CGC_E/100");
		addSSRegimeCost(aonContext, GENERAL, firstDayOfYear, "EXTR_E", STRUCTURAL_OVERTIME, "BASE_ESTR * PORCENTAJE_EXTR_E/100");
		addSSRegimeCost(aonContext, GENERAL, firstDayOfYear, "NEXT_E", NON_STRUCTURAL_OVERTIME, "BASE_ESTR * PORCENTAJE_NEXTR_E/100");
		addSSRegimeCost(aonContext, GENERAL, firstDayOfYear, "IT_E", PROFESSIONAL_CONTINGENCY, "BASE_CGP_E * (isdef PORCENTAJE_IT ? PORCENTAJE_IT : (PORCENTAJE_IT=( isdef OCUPACION ? OCUPACION_IT[OCUPACION] : TARIFA_IT)))/100");
		addSSRegimeCost(aonContext, GENERAL, firstDayOfYear, "IMS_E", PROFESSIONAL_CONTINGENCY, "BASE_CGP_E * (isdef PORCENTAJE_IMS ? PORCENTAJE_IMS : (PORCENTAJE_IMS=( isdef OCUPACION ? OCUPACION_IMS[OCUPACION] : TARIFA_IMS)))/100");
		addSSRegimeCost(aonContext, GENERAL, firstDayOfYear, "DESMPL_E", UNEMPLOYMENT, "(PORCENTAJE_DESMPL == 0) ? 0.00 : ( BASE_CGP_E * ( isdef PORCENTAJE_DESMPL_E ? PORCENTAJE_DESMPL_E : PORCENTAJE_DESMPL_E=(INDEFINIDO ? 5.50 : (TIEMPO_COMPLETO ? 6.70 : 7.70)))/100)");
		addSSRegimeCost(aonContext, GENERAL, firstDayOfYear, "FOGASA_E", FOGASA, "BASE_CGP_E * PORCENTAJE_FOGASA / 100");
		addSSRegimeCost(aonContext, GENERAL, firstDayOfYear, "FP_E", JOB_TRAINING, "BASE_CGP_E * PORCENTAJE_FP_E/100");
		
		Date firstDayOfMonth = getFirstDayOfMonth(getToday());
		Date lastDayOfMonth = getLastDayOfMonth(firstDayOfMonth);
		
		IContractSalaryCalculatorContext contractSalaryCalculatorContext = 
		getContractSalaryCalculatorContext(connection, firstDayOfMonth, lastDayOfMonth, lastDayOfMonth, contract);
		
		ExpressionContext expressionContext = contractSalaryCalculatorContext.getExpressionContext();
		
		Salary salary = 
		new SmartContractSalaryCalculator<Salary>( new RoundSalaryBuilder<Salary>(new SalaryBuilder(), d -> d.setScale(2, RoundingMode.HALF_UP) ))
		.calculate(getContractSalaryCalculatorContext(connection, firstDayOfMonth, lastDayOfMonth, lastDayOfMonth, contract))
		;
		return salary;
	}

	
	// 2.455,35
	
	
	protected final void addSSRegimeDeduction(AONContext aonContext, SSRegimeType ssRegimetype, Date startDate,
			String code, DeductionType deductionType, String expression) {
		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=0");

		DeductionConceptRecord deductionConcept =
		aonContext
		.getDslContext()
		.insertInto(DEDUCTION_CONCEPT)
		.set(DEDUCTION_CONCEPT.CODE, code)
		.set(DEDUCTION_CONCEPT.DOMAIN, (-1) * ssRegimetype.ordinal())
		.set(DEDUCTION_CONCEPT.TYPE,
		(byte) (deductionType != null ? deductionType.ordinal() : DeductionType.OTHER.ordinal()))
		.returning().fetchOne();

		aonContext
		.getDslContext()
		.insertInto(SYSTEM_DEDUCTION)
		.set(SYSTEM_DEDUCTION.START_DATE, startDate)
		.set(SYSTEM_DEDUCTION.EXPRESSION, expression)
		.set(SYSTEM_DEDUCTION.DOMAIN, (-1) * ssRegimetype.ordinal())
		.set(SYSTEM_DEDUCTION.DEDUCTION_CONCEPT, deductionConcept.getId())
		.execute();

		aonContext.getDslContext().execute("SET FOREIGN_KEY_CHECKS=1");
	}
	

}
