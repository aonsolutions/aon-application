package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.COMMON_DISEASE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.TOTAL_PAYMENT;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

public class SQLAgreementTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	@Test
	public void testRedefinedPaymentsI() throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		AgreementRecord agreement = newAgreement(aonContext);
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement);

		PaymentConceptRecord conceptP = addConcept(aonContext, "P");

		addPayments(aonContext, agreement, getFirstDayOfYear(getToday()), new Payment[] { new Payment() {
			{
				this.concept = conceptP.getId();
				this.expression = "100";
			}
		}

		});

		ContractRecord contract = newContract(aonContext, getFirstDayOfMonth(getToday()),
				new HashMap<String, String>() {
				}, new String[] {}, new String[] {}, category);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(CONTRACT.getName() + "." + CONTRACT.ID.getName(), contract.getId());
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(connection, startDate, endDate,
				endDate, criteria);
		ctx.next();
		Salary salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(String.format("%s", TOTAL_PAYMENT), 100.00, salary.getTotalPayment());

		addPayments(aonContext, contract.getDomain(), agreement, getFirstDayOfYear(getToday()),
				new Payment[] { new Payment() {
					{
						this.concept = conceptP.getId();
						this.expression = "666";
					}
				}

				});

		// First of all ensure that agreement domain and contract domain are different.
		Assert.assertNotSame("DOMAIN", agreement.getDomain(), contract.getDomain());

		ctx = new SQLContractSalaryCalculatorContext(connection, startDate, endDate, endDate, criteria);
		ctx.next();
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(String.format("%s", TOTAL_PAYMENT), 666.00, salary.getTotalPayment());

		addPayments(aonContext, agreement, getFirstDayOfYear(getToday()), new Payment[] { new Payment() {
			{
				this.concept = conceptP.getId();
				this.expression = "200";
			}
		}

		});

		ctx = new SQLContractSalaryCalculatorContext(connection, startDate, endDate, endDate, criteria);
		ctx.next();
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(String.format("%s", TOTAL_PAYMENT), 666.00, salary.getTotalPayment());

		addPayments(aonContext, contract.getDomain(), agreement, getFirstDayOfYear(getToday()),
				new Payment[] { new Payment() {
					{
						this.concept = conceptP.getId();
						this.expression = "666";
					}
				}

				});

		ctx = new SQLContractSalaryCalculatorContext(connection, startDate, endDate, endDate, criteria);
		ctx.next();
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(String.format("%s", TOTAL_PAYMENT), 666.00 * 2, salary.getTotalPayment());

		addPayments(aonContext, contract.getDomain(), agreement, getFirstDayOfYear(getToday()),
				new Payment[] { new Payment() {
					{
						this.concept = conceptP.getId();
						this.expression = "666";
					}
				}

				});

		ctx = new SQLContractSalaryCalculatorContext(connection, startDate, endDate, endDate, criteria);
		ctx.next();
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(ctx);
		Assert.assertEquals(String.format("%s", TOTAL_PAYMENT), 666.00 * 3, salary.getTotalPayment());
	}

	@Test
	public void testFixedAmounts() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[] { new Extra() {
			{
				this.expression = "SALARIO_MENSUAL + PLUS_SALARIAL";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "01/07";
			}
		}, new Extra() {
			{
				this.expression = "SALARIO_MENSUAL + PLUS_SALARIAL";
				this.month = Month.DECEMBER;
				this.start = "01/01";
				this.end = "31/12";
				this.issue = "15/12";
			}
		}, }, new Payment[] { new Payment() {
			{
				this.expression = "SALARIO_MENSUAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_SALARIAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_TRANSPORTE";
			}
		} });

		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
			}
		});

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStartDate = AonDateUtils.add(firstDayOfYear, Calendar.DAY_OF_MONTH, 16);

		ContractRecord contract = newContract(aonContext, contractStartDate, new HashMap<String, String>() {
			{
				put("DIAS_MES", "30.00");
			}
		}, new String[] {}, new String[] { "BASE_CGC * 4.70 / 100.00", "BASE_CGP * 1.55 / 100.00",
				"BASE_CGP * 0.10 / 100.00", "BASE_IRPF * PORCENTAJE_IRPF / 100.00" }, category);
		Date endDate = getLastDayOfMonth(contractStartDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2, salary.getTotalPayment(), 0.00);
	}

	@Test
	public void testFixedAmountsII() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[] { new Extra() {
			{
				this.expression = "SALARIO_MENSUAL + PLUS_SALARIAL";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "01/07";
			}
		}, new Extra() {
			{
				this.expression = "SALARIO_MENSUAL + PLUS_SALARIAL";
				this.month = Month.DECEMBER;
				this.start = "01/01";
				this.end = "31/12";
				this.issue = "15/12";
			}
		}, }, new Payment[] { new Payment() {
			{
				this.expression = "SALARIO_MENSUAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_SALARIAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_TRANSPORTE";
			}
		} });

		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
			}
		});

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStartDate = firstDayOfYear;
		Date contractEndDate = AonDateUtils.add(firstDayOfYear, Calendar.DAY_OF_MONTH, 14);

		ContractRecord contract = newContract(aonContext, contractStartDate, contractEndDate,
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30.00");
					}
				}, new String[] {}, new String[] { "BASE_CGC * 4.70 / 100.00", "BASE_CGP * 1.55 / 100.00",
						"BASE_CGP * 0.10 / 100.00", "BASE_IRPF * PORCENTAJE_IRPF / 100.00" },
				category);
		Date endDate = getLastDayOfMonth(contractStartDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2, salary.getTotalPayment(), 0.00);
	}

	@Test
	public void testFixedAmountsIII() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[] { new Extra() {
			{
				this.expression = "SALARIO_MENSUAL + PLUS_SALARIAL";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "01/07";
			}
		}, new Extra() {
			{
				this.expression = "SALARIO_MENSUAL + PLUS_SALARIAL";
				this.month = Month.DECEMBER;
				this.start = "01/01";
				this.end = "31/12";
				this.issue = "15/12";
			}
		}, }, new Payment[] { new Payment() {
			{
				this.expression = "SALARIO_MENSUAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_SALARIAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_TRANSPORTE";
			}
		} });

		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
			}
		});

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStartDate = AonDateUtils.add(firstDayOfYear, Calendar.DAY_OF_MONTH, 9);
		Date contractEndDate = AonDateUtils.add(contractStartDate, Calendar.DAY_OF_MONTH, 14);

		ContractRecord contract = newContract(aonContext, contractStartDate, contractEndDate,
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30.00");
					}
				}, new String[] {}, new String[] { "BASE_CGC * 4.70 / 100.00", "BASE_CGP * 1.55 / 100.00",
						"BASE_CGP * 0.10 / 100.00", "BASE_IRPF * PORCENTAJE_IRPF / 100.00" },
				category);
		Date endDate = getLastDayOfMonth(contractStartDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2, salary.getTotalPayment(), 0.00);
	}


	@Test
	public void testFixedAmountsIV() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[] { new Extra() {
			{
				this.expression = "SALARIO_MENSUAL + PLUS_SALARIAL";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "01/07";
			}
		}, new Extra() {
			{
				this.expression = "SALARIO_MENSUAL + PLUS_SALARIAL";
				this.month = Month.DECEMBER;
				this.start = "01/01";
				this.end = "31/12";
				this.issue = "15/12";
			}
		}, }, new Payment[] { new Payment() {
			{
				this.expression = "SALARIO_MENSUAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_SALARIAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_TRANSPORTE";
			}
		} });

		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
			}
		});

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStartDate = firstDayOfYear;

		ContractRecord contract = newContract(aonContext, contractStartDate,
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30.00");
					}
				}, new String[] {}, new String[] { "BASE_CGC * 4.70 / 100.00", "BASE_CGP * 1.55 / 100.00",
						"BASE_CGP * 0.10 / 100.00", "BASE_IRPF * PORCENTAJE_IRPF / 100.00" },
				category);
		Date endDate = getLastDayOfMonth(contractStartDate);
		
		Date startITDate = AonDateUtils.add(contractStartDate, Calendar.DAY_OF_MONTH, 15);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2, salary.getTotalPayment(), 0.00);
	}

	@Test
	public void testFixedAmountsV() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext, new Extra[] { new Extra() {
			{
				this.expression = "SALARIO_MENSUAL + PLUS_SALARIAL";
				this.month = Month.JULY;
				this.start = "01/07 -1";
				this.end = "30/06";
				this.issue = "01/07";
			}
		}, new Extra() {
			{
				this.expression = "SALARIO_MENSUAL + PLUS_SALARIAL";
				this.month = Month.DECEMBER;
				this.start = "01/01";
				this.end = "31/12";
				this.issue = "15/12";
			}
		}, }, new Payment[] { new Payment() {
			{
				this.expression = "SALARIO_MENSUAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_SALARIAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_TRANSPORTE";
			}
		} });

		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
			}
		});

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStartDate = firstDayOfYear;

		ContractRecord contract = newContract(aonContext, contractStartDate,
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30.00");
					}
				}, new String[] {}, new String[] { "BASE_CGC * 4.70 / 100.00", "BASE_CGP * 1.55 / 100.00",
						"BASE_CGP * 0.10 / 100.00", "BASE_IRPF * PORCENTAJE_IRPF / 100.00" },
				category);
		Date endDate = getLastDayOfMonth(contractStartDate);
		
		Date startITDate = AonDateUtils.add(contractStartDate, Calendar.DAY_OF_MONTH, 5);
		Date endITDate = AonDateUtils.add(startITDate, Calendar.DAY_OF_MONTH, 15);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, endITDate, null);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2, salary.getTotalPayment(), 0.00);
	}

	@Test
	public void testFixedAmountsVI() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		PaymentConceptRecord paga = addConcept(aonContext, "PAGA", PaymentType.CRA_0004);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext, 
		new Extra[] {}, 
		new Payment[] { new Payment() {
			{
				this.expression = "SALARIO_MENSUAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_SALARIAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_TRANSPORTE";
			}
		}, new Payment() {
			{
				this.concept = paga.getId();
				this.expression = "PAGA_EXTRA";
			}
		}, new Payment() {
			{
				this.concept = paga.getId();
				this.expression = "PAGA_EXTRA";
			}
		} 
		});
		

		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String, String>() {
			{
				put("PAGA_EXTRA", "3000.00");
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
				put("BASE_CGC_MIN", "1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
			}
		});

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStartDate = firstDayOfYear;

		ContractRecord contract = newContract(aonContext, contractStartDate,
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30.00");
					}
				}, new String[] {}, new String[] { 
						"BASE_CGC * 4.70 / 100.00", "BASE_CGP * 1.55 / 100.00",
						"BASE_CGP * 0.10 / 100.00", "BASE_IRPF * PORCENTAJE_IRPF / 100.00" 
				},
				category);
		Date endDate = getLastDayOfMonth(contractStartDate);
		
		Date startITDate = AonDateUtils.add(contractStartDate, Calendar.DAY_OF_MONTH, 5);
		Date endITDate = AonDateUtils.add(startITDate, Calendar.DAY_OF_MONTH, 15);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, endITDate, null);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		
		for ( SalaryPayment p : salary.getSalaryPayments() ) {
			System.out.println(p.getExpression() + " = " + p.getAmount());
		}
		
		
		org.junit.Assert.assertEquals((3330 + 3000/6) /2 , salary.getTotalPayment(), 0.00);
	}


	@Test
	public void testFixedAmountsVII() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		PaymentConceptRecord paga = addConcept(aonContext, "PAGA", PaymentType.CRA_0004);
		PaymentConceptRecord garantizado = addConcept(aonContext, "GARANTIZADO", PaymentType.CRA_0055);

		// @formatter:on
		AgreementLevelCategoryRecord category = newAgreement(aonContext, 
		new Extra[] {}, 
		new Payment[] { new Payment() {
			{
				this.expression = "SALARIO_MENSUAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_SALARIAL";
			}
		}, new Payment() {
			{
				this.expression = "PLUS_TRANSPORTE";
			}
		}, new Payment() {
			{
				this.concept = paga.getId();
				this.expression = "PAGA_EXTRA";
			}
		}, new Payment() {
			{
				this.concept = paga.getId();
				this.expression = "PAGA_EXTRA";
			}
		}, new Payment() {
			{
				this.concept = garantizado.getId();
				this.expression = "SALARIO_MENSUAL + PLUS_SALARIAL + PLUS_TRANSPORTE + PAGA_EXTRA/6";
			}
		} 
		});
		

		addData(aonContext, category, getFirstDayOfYear(getToday()), null, new HashMap<String, String>() {
			{
				put("PAGA_EXTRA", "3000.00");
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
				put("BASE_CGC_MIN", "1050.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30)");
			}
		});

		Date firstDayOfYear = getFirstDayOfYear(getToday());
		Date contractStartDate = firstDayOfYear;

		ContractRecord contract = newContract(aonContext, contractStartDate,
				new HashMap<String, String>() {
					{
						put("DIAS_MES", "30.00");
					}
				}, new String[] {}, new String[] { 
						"BASE_CGC * 4.70 / 100.00", "BASE_CGP * 1.55 / 100.00",
						"BASE_CGP * 0.10 / 100.00", "BASE_IRPF * PORCENTAJE_IRPF / 100.00" 
				},
				category);
		
		PaymentConceptRecord prestIT = addConcept(aonContext, "PREST_IT");
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.00 * %s_1_3",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.00 * %s_4_15",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.00 * %s_16_20",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);
		addPayment(aonContext, contract, prestIT 
				,String.format("BASE_REGULADORA * 0.00 * %s_21",  COMMON_DISEASE_DAYS)
				,String.format("BASE_REGULADORA * 1.00 * %s",  QUOTE_DAYS)
				);

		Date endDate = getLastDayOfMonth(contractStartDate);
		

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))
		;
		
		for ( SalaryPayment p : salary.getSalaryPayments() ) {
			System.out.println(p.getExpression() + " = " + p.getAmount());
		}
		
		
		org.junit.Assert.assertEquals(3000 + 330 + 3000/6, salary.getTotalPayment(), 0.00);

		Date startITDate = AonDateUtils.add(contractStartDate, Calendar.DAY_OF_MONTH, 5);
		Date endITDate = AonDateUtils.add(startITDate, Calendar.DAY_OF_MONTH, 15);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, endITDate, null);

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))
		;
		
		for ( SalaryPayment p : salary.getSalaryPayments() ) {
			System.out.println(p.getExpression() + " = " + p.getAmount());
		}
		
		
		org.junit.Assert.assertEquals(3000 + 330 + 3000/6, salary.getTotalPayment(), 0.00);
	}
}
