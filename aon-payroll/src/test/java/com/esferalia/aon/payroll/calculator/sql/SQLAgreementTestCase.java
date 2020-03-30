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
import com.esferalia.aon.payroll.enumeration.ContextVariable;
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

	@Test
	public void testFixedAmountsVIII() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		PaymentConceptRecord sbConcept = addConcept(aonContext, "SB");
		PaymentConceptRecord plConcept = addConcept(aonContext, "PL");

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
				this.concept = sbConcept.getId();
				this.expression = "SALARIO_MENSUAL";
			}
		}, new Payment() {
			{
				this.concept = plConcept.getId();
				this.expression = "PLUS_SALARIAL";
			}
		}, new Payment() {
			{
				this.concept = plConcept.getId();
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
				put("TIEMPO_COMPLETO", "FALSO()");
				put("COEFICIENTE_PARCIALIDAD", "0.75");
			}
		}, new String[] {}, new String[] { 
				"BASE_CGC * 4.70 / 100.00", 
				"BASE_CGP * 1.55 / 100.00",
				"BASE_CGP * 0.10 / 100.00", 
//				"BASE_IRPF * PORCENTAJE_IRPF / 100.00" 
				}, 
		category);
		Date endDate = getLastDayOfMonth(contractStartDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330 * 0.75) / 2, salary.getTotalPayment(), 0.00);
		org.junit.Assert.assertEquals((3300 / 6.0 * 0.75) / 2, salary.getExtraPayProration(), 0.00);
		
		//salary.getSalaryDatas()
		//.stream().filter(d -> ContextVariable.CGC_BASE.getName().equals(d.getName()))
		//.forEach( d -> System.out.println(d.getName() +" = " + d.getExpression()));
		
		org.junit.Assert.assertEquals(((3330 * 0.75)/2) + ((3300 / 6.0 * 0.75) / 2) , salary.getCommonBase(), 0.00);
	}

	@Test
	public void testFixedAmountsIX() throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		PaymentConceptRecord sbConcept = addConcept(aonContext, "SB");
		PaymentConceptRecord plConcept = addConcept(aonContext, "PL");

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
				this.concept = sbConcept.getId();
				this.expression = "SALARIO_MENSUAL";
			}
		}, new Payment() {
			{
				this.concept = plConcept.getId();
				this.expression = "PLUS_SALARIAL";
			}
		}, new Payment() {
			{
				this.concept = plConcept.getId();
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
						put("TIEMPO_COMPLETO", "FALSO()");
						put("COEFICIENTE_PARCIALIDAD", "0.25");
					}
				}, new String[] {}, new String[] { 
						"BASE_CGC * 4.70 / 100.00", 
						"BASE_CGP * 1.55 / 100.00",
						"BASE_CGP * 0.10 / 100.00", 
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00" 
				},
				category);
		Date endDate = getLastDayOfMonth(contractStartDate);
		
		Date startITDate = AonDateUtils.add(contractStartDate, Calendar.DAY_OF_MONTH, 15);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate, null, null);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		org.junit.Assert.assertEquals((3330 * 0.25) / 30 * 15, salary.getTotalPayment(), DELTA);
		org.junit.Assert.assertEquals((3300 / 6 * 0.25) / 30 * 15, salary.getExtraPayProration(), DELTA);

		org.junit.Assert.assertEquals(( (3330 * 0.25) + (3300 / 6 * 0.25) ) / 30 * 15, salary.getCommonBase(), DELTA);
	}

	@Test
	public void testFixedAmountsX() throws ExpressionException, SQLException, SalaryException {
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
				put("TIEMPO_COMPLETO", "false");
				put("COEFICIENTE_PARCIALIDAD", "0.50");
				
			}
		}, new String[] {}, new String[] { 
				"BASE_CGC * 4.70 / 100.00", 
				"BASE_CGP * 1.55 / 100.00",
				"BASE_CGP * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF / 100.00" }, category);
		Date endDate = getLastDayOfMonth(contractStartDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2 * 0.50, salary.getTotalPayment(), 0.00);
	}

	@Test
	public void testFixedAmountsXI() throws ExpressionException, SQLException, SalaryException {
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

		Date contractStartDate = getFirstDayOfYear(getToday());

		ContractRecord contract = newContract(aonContext, contractStartDate, new HashMap<String, String>() {
			{
				put("DIAS_MES", "30.00");
				put("TIEMPO_COMPLETO", "false");
				put("COEFICIENTE_PARCIALIDAD", "0.50");
				
			}
		}, new String[] {}, new String[] { 
				"BASE_CGC * 4.70 / 100.00", 
				"BASE_CGP * 1.55 / 100.00",
				"BASE_CGP * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF / 100.00" }, category);

		Date startDate = add(contractStartDate, Calendar.MONTH, 1);
		Date endDate = getLastDayOfMonth(startDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) * 0.50, salary.getTotalPayment(), 0.00);
	}

	@Test
	public void testFixedAmountsXII() throws ExpressionException, SQLException, SalaryException {
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
				}, new String[] {}, new String[] { 
						"BASE_CGC * 4.70 / 100.00", 
						"BASE_CGP * 1.55 / 100.00",
						"BASE_CGP * 0.10 / 100.00", 
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00" },
				category);
		Date endDate = getLastDayOfMonth(contractStartDate);
		
		Date startEREDate = AonDateUtils.add(contractStartDate, Calendar.DAY_OF_MONTH, 15);
		addData(aonContext, contract, startEREDate, null, ContextVariable.ERE_FACTOR, 1.00);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2, salary.getTotalPayment(), 0.00);
		org.junit.Assert.assertEquals((3300) / 12, salary.getExtraPayProration(), 0.00);
	}

	@Test
	public void testFixedAmountsXIII() throws ExpressionException, SQLException, SalaryException {
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
						put("TIEMPO_COMPLETO", "FALSO()");
						put("COEFICIENTE_PARCIALIDAD", "0.50");						
					}
				}, new String[] {}, new String[] { 
						"BASE_CGC * 4.70 / 100.00", 
						"BASE_CGP * 1.55 / 100.00",
						"BASE_CGP * 0.10 / 100.00", 
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00" },
				category);
		Date endDate = getLastDayOfMonth(contractStartDate);
		
		Date startEREDate = AonDateUtils.add(contractStartDate, Calendar.DAY_OF_MONTH, 15);
		addData(aonContext, contract, startEREDate, null, ContextVariable.ERE_FACTOR, 1.00);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330/2)*0.5, salary.getTotalPayment(), 0.00);
		org.junit.Assert.assertEquals((3300/12)*0.5, salary.getExtraPayProration(), 0.00);
		
	}

	@Test
	public void testFixedAmountsXIV() throws ExpressionException, SQLException, SalaryException {
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
				}, new String[] {}, new String[] { 
						"BASE_CGC * 4.70 / 100.00", 
						"BASE_CGP * 1.55 / 100.00",
						"BASE_CGP * 0.10 / 100.00", 
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00" },
				category);
		Date endDate = getLastDayOfMonth(contractStartDate);
		
		Date startEREDate = AonDateUtils.add(contractStartDate, Calendar.DAY_OF_MONTH, 15);
		addData(aonContext, contract, startEREDate, null, ContextVariable.ERE_FACTOR, 0.50);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals(3330/2.00 + 3330/4.00 , salary.getTotalPayment(), 0.00);
		org.junit.Assert.assertEquals(3300/12.00 + 3300/24.00, salary.getExtraPayProration(), 0.00);
	}
	
	@Test
	public void testRedefinedDataI() throws ExpressionException, SQLException, SalaryException {
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

		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		
		addData(aonContext, category, add(firstDayOfYear, Calendar.YEAR, -1), null, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
			}
		});

		Date contractStartDate = AonDateUtils.add(firstDayOfYear, Calendar.DAY_OF_MONTH, 16);

		ContractRecord contract = newContract(aonContext, contractStartDate, new HashMap<String, String>() {
			{
				put("DIAS_MES", "30.00");
			}
		}, new String[] {}, new String[] { 
				"BASE_CGC * 4.70 / 100.00", 
				"BASE_CGP * 1.55 / 100.00",
				"BASE_CGP * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF / 100.00" }, category);
		Date endDate = getLastDayOfMonth(contractStartDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2, salary.getTotalPayment(), 0.00);
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		addData(aonContext, agreement, firstDayOfYear, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "400.00");
				put("PLUS_TRANSPORTE", "40.00");
				put("SALARIO_MENSUAL", "4000.00");
			}
		});
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((4440) / 2, salary.getTotalPayment(), 0.00);
		
		
	}

	@Test
	public void testRedefinedDataII() throws ExpressionException, SQLException, SalaryException {
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

		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		
		addData(aonContext, category, add(firstDayOfYear, Calendar.YEAR, -1), null, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
			}
		});

		Date contractStartDate = AonDateUtils.add(firstDayOfYear, Calendar.DAY_OF_MONTH, 16);

		ContractRecord contract = newContract(aonContext, contractStartDate, new HashMap<String, String>() {
			{
				put("DIAS_MES", "30.00");
			}
		}, new String[] {}, new String[] { 
				"BASE_CGC * 4.70 / 100.00", 
				"BASE_CGP * 1.55 / 100.00",
				"BASE_CGP * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF / 100.00" }, category);
		Date endDate = getLastDayOfMonth(contractStartDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2, salary.getTotalPayment(), 0.00);
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		addData(aonContext, agreement, firstDayOfYear, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "PLUS_SALARIAL + 100.00");
				put("PLUS_TRANSPORTE", "PLUS_TRANSPORTE + 10.00");
				put("SALARIO_MENSUAL", "SALARIO_MENSUAL + 1000.00");
			}
		});
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((4440) / 2, salary.getTotalPayment(), 0.00);
		
		
	}

	@Test
	public void testRedefinedDataIII() throws ExpressionException, SQLException, SalaryException {
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

		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		
		addData(aonContext, category, add(firstDayOfYear, Calendar.YEAR, -2), null, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
			}
		});

		Date contractStartDate = AonDateUtils.add(firstDayOfYear, Calendar.DAY_OF_MONTH, 16);

		ContractRecord contract = newContract(aonContext, contractStartDate, new HashMap<String, String>() {
			{
				put("DIAS_MES", "30.00");
			}
		}, new String[] {}, new String[] { 
				"BASE_CGC * 4.70 / 100.00", 
				"BASE_CGP * 1.55 / 100.00",
				"BASE_CGP * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF / 100.00" }, category);
		Date endDate = getLastDayOfMonth(contractStartDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2, salary.getTotalPayment(), 0.00);
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		addData(aonContext, agreement, add(firstDayOfYear, Calendar.YEAR, -1), new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "PLUS_SALARIAL + 100.00");
				put("PLUS_TRANSPORTE", "PLUS_TRANSPORTE + 10.00");
				put("SALARIO_MENSUAL", "SALARIO_MENSUAL + 1000.00");
			}
		});
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((4440) / 2, salary.getTotalPayment(), 0.00);
		
		addData(aonContext, agreement, firstDayOfYear, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "PLUS_SALARIAL + 100.00");
				put("PLUS_TRANSPORTE", "PLUS_TRANSPORTE + 10.00");
				put("SALARIO_MENSUAL", "SALARIO_MENSUAL + 1000.00");
			}
		});
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((5550) / 2, salary.getTotalPayment(), 0.00);
		

	}

	@Test
	public void testRedefinedDataIV() throws ExpressionException, SQLException, SalaryException {
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

		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		
		addData(aonContext, category, add(firstDayOfYear, Calendar.YEAR, -2), null, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
			}
		});

		Date contractStartDate = AonDateUtils.add(firstDayOfYear, Calendar.DAY_OF_MONTH, 16);

		ContractRecord contract = newContract(aonContext, contractStartDate, new HashMap<String, String>() {
			{
				put("DIAS_MES", "30.00");
			}
		}, new String[] {}, new String[] { 
				"BASE_CGC * 4.70 / 100.00", 
				"BASE_CGP * 1.55 / 100.00",
				"BASE_CGP * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF / 100.00" }, category);
		Date endDate = getLastDayOfMonth(contractStartDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2, salary.getTotalPayment(), 0.00);
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		addData(aonContext, agreement, add(firstDayOfYear, Calendar.YEAR, -1), new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "PLUS_SALARIAL + 100.00");
				put("PLUS_TRANSPORTE", "PLUS_TRANSPORTE + 10.00");
				put("SALARIO_MENSUAL", "SALARIO_MENSUAL + 1000.00");
			}
		});
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((4440) / 2, salary.getTotalPayment(), 0.00);
		
		addData(aonContext, agreement, firstDayOfYear, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "PLUS_SALARIAL + 200.00");
				put("PLUS_TRANSPORTE", "PLUS_TRANSPORTE + 20.00");
				put("SALARIO_MENSUAL", "SALARIO_MENSUAL + 2000.00");
			}
		});
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((6660) / 2, salary.getTotalPayment(), 0.00);
		

	}
	
	@Test
	public void testRedefinedDataV() throws ExpressionException, SQLException, SalaryException {
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

		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		Date levelStartDate = add(firstDayOfYear, Calendar.YEAR, -2);
		Date levelEndDate = add(add(firstDayOfYear, Calendar.YEAR, -1), Calendar.DAY_OF_MONTH,-1);
		addData(aonContext, category, levelStartDate, levelEndDate, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
			}
		});

		Date contractStartDate = AonDateUtils.add(firstDayOfYear, Calendar.DAY_OF_MONTH, 16);

		ContractRecord contract = newContract(aonContext, contractStartDate, new HashMap<String, String>() {
			{
				put("DIAS_MES", "30.00");
			}
		}, new String[] {}, new String[] { 
				"BASE_CGC * 4.70 / 100.00", 
				"BASE_CGP * 1.55 / 100.00",
				"BASE_CGP * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF / 100.00" }, category);
		Date endDate = getLastDayOfMonth(contractStartDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2, salary.getTotalPayment(), 0.00);
		
		AgreementRecord agreement = getAgreement(aonContext, category.getAgreementLevel());
		
		addData(aonContext, agreement, add(firstDayOfYear, Calendar.YEAR, -1), add(firstDayOfYear, Calendar.DAY_OF_MONTH, -1 ) , new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "PLUS_SALARIAL + 100.00");
				put("PLUS_TRANSPORTE", "PLUS_TRANSPORTE + 10.00");
				put("SALARIO_MENSUAL", "SALARIO_MENSUAL + 1000.00");
			}
		});
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((4440) / 2, salary.getTotalPayment(), 0.00);
		
		addData(aonContext, agreement, firstDayOfYear, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "PLUS_SALARIAL + 200.00");
				put("PLUS_TRANSPORTE", "PLUS_TRANSPORTE + 20.00");
				put("SALARIO_MENSUAL", "SALARIO_MENSUAL + 2000.00");
			}
		});
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((6660) / 2, salary.getTotalPayment(), 0.00);
		

	}

	@Test
	public void testRedefinedDataVI() throws ExpressionException, SQLException, SalaryException {
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

		
		Date firstDayOfYear = getFirstDayOfYear(getToday());
		
		Date levelStartDate = add(firstDayOfYear, Calendar.YEAR, -2);
		Date levelEndDate = add(add(firstDayOfYear, Calendar.YEAR, -1), Calendar.DAY_OF_MONTH,-1);
		addData(aonContext, category, levelStartDate, levelEndDate, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "300.00");
				put("PLUS_TRANSPORTE", "30.00");
				put("SALARIO_MENSUAL", "3000.00");
			}
		});

		Date contractStartDate = AonDateUtils.add(firstDayOfYear, Calendar.DAY_OF_MONTH, 16);

		ContractRecord contract = newContract(aonContext, contractStartDate, new HashMap<String, String>() {
			{
				put("DIAS_MES", "30.00");
			}
		}, new String[] {}, new String[] { 
				"BASE_CGC * 4.70 / 100.00", 
				"BASE_CGP * 1.55 / 100.00",
				"BASE_CGP * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF / 100.00" }, category);
		Date endDate = getLastDayOfMonth(contractStartDate);

		Salary salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((3330) / 2, salary.getTotalPayment(), 0.00);

		addData(aonContext, category, add(firstDayOfYear, Calendar.YEAR, -1), add(firstDayOfYear, Calendar.DAY_OF_MONTH, -1 ), new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "400.00");
				put("PLUS_TRANSPORTE", "40.00");
				put("SALARIO_MENSUAL", "4000.00");
			}
		});
		
		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((4440) / 2, salary.getTotalPayment(), 0.00);
		
		
		addData(aonContext, category, firstDayOfYear, null, new HashMap<String, String>() {
			{
				put("PLUS_SALARIAL", "600.00");
				put("PLUS_TRANSPORTE", "60.00");
				put("SALARIO_MENSUAL", "6000.00");
			}
		});

		salary = new SmartContractSalaryCalculator<Salary>(new SalaryBuilder()).calculate(
				getContractSalaryCalculatorContext(connection, contractStartDate, endDate, endDate, contract))

		;
		org.junit.Assert.assertEquals((6660) / 2, salary.getTotalPayment(), 0.00);
		

	}
}
