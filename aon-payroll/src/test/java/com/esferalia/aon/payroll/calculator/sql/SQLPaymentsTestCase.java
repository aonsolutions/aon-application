package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator.Listener;
import com.esferalia.aon.payroll.calculator.GenericContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractBonus;
import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionContext.RemovedExpressionVariable;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

import junit.framework.Assert;

public class SQLPaymentsTestCase extends AbstractSQLTestCase {

	protected static final double DELTA = 0.004;
	
	@Test
	public void testCommonDiseaseITI() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {}, 
				null);
		//@formatter:on

		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord antiguedad = addConcept(aonContext, "ANTIGUEDAD");
		PaymentConceptRecord paga = addConcept(aonContext, "PAGA");
		
		
		addPayment(aonContext, contract, salarioBase, "1000.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, plusSalarial, "PLUS * DIAS_TRABAJADOS");
		addPayment(aonContext, contract, antiguedad, "ANTIGUEDAD * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, paga, "(SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD)/12");
		setData(aonContext, contract, "DIAS_MES", "30.00");
		
		
		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				null, null);

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		addData(aonContext, contract, startDate, startDate, "PLUS", "100.00");
		addData(aonContext, contract, add(startDate,DAY_OF_MONTH,1), endDate, "ANTIGUEDAD", "50.00");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date start, java.util.Date end, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println(payment.getName()+ ": " + amount + "[" + start + "..." + end + "]");
				if ( payment.getName().equals("PAGA")) {
					if ( startDate.equals(start))
						Assert.assertEquals(startDate, end);
					else { 
						Assert.assertEquals(add(startDate,DAY_OF_MONTH,1), start);
						Assert.assertEquals(add(startITDate,DAY_OF_MONTH,-1), end);
					}
				}
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		});
		Salary salary = calculator.calculate(ctx);
		
		//Assert.assertEquals((1000.00 * 10 / 30.00 + 50.00 + 100.00 * 9/12) * ( 1 + 1/12 ) , salary.getTotalPayment());

		//Assert.assertEquals(4, salary.getSalaryPayments().size());

	}

	@Test
	public void testCompositeDescriptionJOOQ() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {}, 
				null);
		//@formatter:on

		
		
		setData(aonContext, contract, "DIAS_MES", "30.00");

		addPayment(aonContext, 
		contract, 
		contract.getStartDate(), 
		null,
		"SALARIO BASE",
		"1000.00 * DIAS_TRABAJADOS / DIAS_MES", 
		"_P", 
		"_P", 
		PaymentType.CRA_0001);
		
		addPayment(aonContext, contract, 
		contract.getStartDate(), 
		null, 
		"@{DIAS_ENFERMEDAD_COMUN_21} DÍAS DE IT", 
		" /*read-only*/DIAS_ENFERMEDAD_COMUN_21 * BASE_REGULADORA * 0.75 * (isdef COEFICIENTE_IT ? COEFICIENTE_IT : 1.00)/**/", 
		"_P", 
		"DIAS_COTIZADOS * (isdef COEFICIENTE_IT ? COEFICIENTE_IT : 1.00) * BASE_REGULADORA", 
		PaymentType.CRA_0001);
		
		
		Date startITDate = getFirstDayOfYear(getToday());
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,null, null);
		

		Date startDate = add(startITDate, Calendar.MONTH, 2);
		Date endDate = getLastDayOfMonth(startDate);
		
		addData(aonContext, contract, startDate, add(startDate,DAY_OF_MONTH,10), "BASE_REGULADORA", "100.00");
		addData(aonContext, contract, add(startDate,DAY_OF_MONTH,11), endDate, "BASE_REGULADORA", "100.00");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection) {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date start, java.util.Date end, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println(description + ": " + amount + "[" + start + "..." + end + "]");
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		};
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		Stream<com.esferalia.aon.occam.api.model.Salary> salaries = 
		AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId()));
		
		salaries.forEach( salary -> {
			salary.getPayments().forEach( p -> System.out.println(p.getDescription() +":" + p.getAmount()));
			salary.getPayments().forEach( p -> org.junit.Assert.assertEquals("31 DÍAS DE IT", p.getDescription()) );
		});
		
		

	}

	
	@Test
	public void testSalaryInKindIRPF() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new String[] {}, 
				new String[] {
						"BASE_CGC * PORCENTAJE_CGC / 100.00",
						"BASE_CGP * PORCENTAJE_FP / 100.00",
						"BASE_CGP * PORCENTAJE_DESMPL / 100.00",
						
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00"
				}, 
				null);
		//@formatter:on

		PaymentConceptRecord paga = addConcept(aonContext, "PAGA");
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		
		
		addPayment(aonContext, contract, salarioBase, "1000.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, paga, "(SALARIO_BASE )/12");
		addPayment(aonContext, contract, 
				"SALARIO EN ESPECIE", 
				"1200.00", 
				"BASE_CTA_ESP=_P", 
				"_P", PaymentType.CRA_0013);
		
		PaymentConceptRecord ingrCtaEsp = addConcept(aonContext, "IRPF_CTA_ESP");

		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), 
				ingrCtaEsp,
				PaymentType.CRA_0000, 
				//"INGRESO A CUENTA ESPECIE A CARGO DE LA EMPRESA" ,
				"/*read-only*/isdef BASE_CTA_ESP ? BASE_CTA_ESP * PORCENTAJE_IRPF / 100.00 : HIDE()/**/", 
				"0.00", 
				"0.00",
				SalaryType.SALARY);
		
		addSSRegimeDeduction(
				aonContext, 
				SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()),
				DeductionType.OTHER,
				"IRPF_CTA_ESP");
		
		setData(aonContext, contract, "DIAS_MES", "30.00");
		
		setData(aonContext, contract, "PORCENTAJE_CGC", "4.70");
		setData(aonContext, contract, "PORCENTAJE_FP", "0.10");
		setData(aonContext, contract, "PORCENTAJE_DESMPL", "1.55");
		setData(aonContext, contract, "PORCENTAJE_IRPF", "9.55");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		
		salary.getSalaryPayments().forEach( p -> {
			System.out.println( p.getDescription() + ": " + p.getAmount() +", " + p.getIrpf());
		});
		
		
		
		org.junit.Assert.assertEquals( 1000.00 * 13.00/12.00  + 1200.00, salary.getIrpfBase(), 0.001);
		org.junit.Assert.assertEquals( 1000.00 * 13.00/12.00  + 1200.00 + (1200.00 * 9.55 / 100.00), salary.getTotalPayment(), 0.001);
		
		salary.getSalaryDeductions().forEach( p -> {
			System.out.println( p.getDescription() + ": " + p.getAmount() );
		});

		org.junit.Assert.assertEquals( 5, salary.getSalaryDeductions().size(), 0.001);
		
		org.junit.Assert.assertEquals( salary.getCommonBase() * ( 4.7 + 0.10 + 1.55 ) / 100.00 + salary.getIrpfBase() * 9.55 / 100.00 + 1200.00 * 9.55 / 100.00 , 
				salary.getSalaryDeductions().stream().collect(Collectors.summingDouble(d -> d.getAmount() )), 
				0.001);
		
		org.junit.Assert.assertEquals( 1000.00 * 13/12, salary.getRemuneration(), 0.001);

	}

	@Test
	public void testSalaryInKindIRPFII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new String[] {}, 
				new String[] {
						"BASE_CGC * PORCENTAJE_CGC / 100.00",
						"BASE_CGP * PORCENTAJE_FP / 100.00",
						"BASE_CGP * PORCENTAJE_DESMPL / 100.00",
						
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00"
				}, 
				null);
		//@formatter:on

		PaymentConceptRecord paga = addConcept(aonContext, "PAGA");
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		
		
		addPayment(aonContext, contract, salarioBase, "1000.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, paga, "(SALARIO_BASE )/12");
		addPayment(aonContext, contract, 
				"SALARIO EN ESPECIE", 
				"1200.00", 
				"BASE_CTA_ESP=_P", 
				"_P", PaymentType.CRA_0013);
		
		
		
		PaymentConceptRecord ingrCtaEsp = addConcept(aonContext, "IRPF_CTA_ESP");

		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), 
				ingrCtaEsp,
				PaymentType.CRA_0000, 
				//"INGRESO A CUENTA ESPECIE A CARGO DE LA EMPRESA" ,
				"/*read-only*/isdef BASE_CTA_ESP ? BASE_CTA_ESP * PORCENTAJE_IRPF / 100.00 : HIDE()/**/", 
				"0.00", 
				"0.00",
				SalaryType.SALARY);
		
		addSSRegimeDeduction(
				aonContext, 
				SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()),
				DeductionType.OTHER,
				"IRPF_CTA_ESP");
		
		setData(aonContext, contract, "DIAS_MES", "30.00");
		
		setData(aonContext, contract, "PORCENTAJE_CGC", "4.70");
		setData(aonContext, contract, "PORCENTAJE_FP", "0.10");
		setData(aonContext, contract, "PORCENTAJE_DESMPL", "1.55");
		//setData(aonContext, contract, "PORCENTAJE_IRPF", "9.55");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				if ( "INGR_CTA_ESP".equals(payment.getName())) {
					ITimedVariable<?> baseCtaEsp = context.get("BASE_CTA_ESP");
					org.junit.Assert.assertEquals(1200.00, baseCtaEsp.getValue(baseCtaEsp.getPeriod()));
				}
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		});
		Salary salary = calculator.calculate(ctx);
		
		salary.getSalaryPayments().forEach( p -> {
			System.out.println( p.getDescription() + ": " + p.getAmount() +", " + p.getIrpf());
		});
		
		salary.getSalaryDatas().forEach(d -> System.out.println(d.getName() + " = " + d.getExpression() ));
		
		//double irpf = Double.parseDouble(salary.getSalaryData("PORCENTAJE_IRPF"));
		double irpf = salary.getTotalIrpf() / salary.getIrpfBase() * 100.00;
		
		org.junit.Assert.assertEquals( 1000.00 * 13.00/12.00  + 1200.00, salary.getIrpfBase(), 0.001);
		org.junit.Assert.assertEquals( 1000.00 * 13.00/12.00  + 1200.00 + (1200.00 * irpf / 100.00), salary.getTotalPayment(), 0.001);
		
		salary.getSalaryDeductions().forEach( p -> {
			System.out.println( p.getDescription() + ": " + p.getAmount() );
		});

		org.junit.Assert.assertEquals( 5, salary.getSalaryDeductions().size(), 0.001);
		
		org.junit.Assert.assertEquals( salary.getCommonBase() * ( 4.7 + 0.10 + 1.55 ) / 100.00 + salary.getIrpfBase() * irpf / 100.00 + 1200.00 * irpf / 100.00 , 
				salary.getSalaryDeductions().stream().collect(Collectors.summingDouble(d -> d.getAmount() )), 
				0.001);
		
		
		org.junit.Assert.assertEquals( 1000.00 * 13/12, salary.getRemuneration(), 0.001);

	}

	@Test
	public void testSalaryInKindIRPFIII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new String[] {}, 
				new String[] {
						"BASE_CGC * PORCENTAJE_CGC / 100.00",
						"BASE_CGP * PORCENTAJE_FP / 100.00",
						"BASE_CGP * PORCENTAJE_DESMPL / 100.00",
						
						"BASE_IRPF * PORCENTAJE_IRPF / 100.00"
				}, 
				null);
		//@formatter:on

		PaymentConceptRecord paga = addConcept(aonContext, "PAGA");
		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		
		
		addPayment(aonContext, contract, salarioBase, "1000.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, paga, "(SALARIO_BASE )/12");
		addPayment(aonContext, contract, 
				"SALARIO EN ESPECIE", 
				"1200.00", 
				"_P", 
				"_P", PaymentType.CRA_0013);
		
		
		
		PaymentConceptRecord ingrCtaEsp = addConcept(aonContext, "INGR_CTA_ESP");

		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), 
				ingrCtaEsp,
				PaymentType.CRA_0000, 
				//"INGRESO A CUENTA ESPECIE A CARGO DE LA EMPRESA" ,
				"/*read-only*/isdef BASE_CTA_ESP ? BASE_CTA_ESP * PORCENTAJE_IRPF / 100.00 : HIDE()/**/", 
				"0.00", 
				"0.00",
				SalaryType.SALARY);
		
		addSSRegimeDeduction(
				aonContext, 
				SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()),
				DeductionType.OTHER,
				"INGR_CTA_ESP");
		
		setData(aonContext, contract, "DIAS_MES", "30.00");
		
		setData(aonContext, contract, "PORCENTAJE_CGC", "4.70");
		setData(aonContext, contract, "PORCENTAJE_FP", "0.10");
		setData(aonContext, contract, "PORCENTAJE_DESMPL", "1.55");
		//setData(aonContext, contract, "PORCENTAJE_IRPF", "9.55");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder() {
			@Override
			public void addZeroPayment(Double quote, Double tax, java.util.Date startDate, java.util.Date endDate,
					IPayment payment, Map<String, ITimedVariable<?>> context) {
				org.junit.Assert.fail();
			}
		});
		calculator.setListener(new GenericContractSalaryCalculator.Listener() {
			
			@Override
			public void onUndefinedData(IContractDeduction deduction, String variableName, String message) {
				org.junit.Assert.fail();
			}
			
			@Override
			public void onUndefinedData(IContractDeduction deduction, RemovedExpressionVariable<?> var) {
				org.junit.Assert.fail();
			}
			
			@Override
			public void onUndefinedData(IContractPayment payment, String variableName, String message) {
				org.junit.Assert.fail();
			}
			
			@Override
			public void onUndefinedData(IContractPayment payment, RemovedExpressionVariable<?> var) {
				org.junit.Assert.fail();
			}
			
			@Override
			public void onRemove(IContractPayment payment) {
				org.junit.Assert.fail();
			}
			
			@Override
			public void onRemove(IContractDeduction payment) {
				org.junit.Assert.fail();
			}
			
			@Override
			public void onRemove(IContractBonus bonus) {
				org.junit.Assert.fail();
			}
			
			@Override
			public void onInvalidData(IContractBonus bonus, String variableName, String message) {
				org.junit.Assert.fail();
			}
			
			@Override
			public void onInvalidData(IContractDeduction deduction, String variableName, String message) {
				org.junit.Assert.fail();
			}
			
			@Override
			public void onInvalidData(IContractPayment payment, String variableName, String message) {
				org.junit.Assert.fail();
			}
			
		});
		Salary salary = calculator.calculate(ctx);
		
		
		salary.getSalaryPayments().forEach( p -> {
			System.out.println( p.getDescription() + ": " + p.getAmount() +", " + p.getIrpf());
		});
		
		double irpf = salary.getTotalIrpf() / salary.getIrpfBase() * 100.00;
		
		org.junit.Assert.assertEquals( 1000.00 * 13.00/12.00  + 1200.00, salary.getIrpfBase(), 0.001);
		org.junit.Assert.assertEquals( 1000.00 * 13.00/12.00  + 1200.00 , salary.getTotalPayment(), 0.001);
		
		salary.getSalaryDeductions().forEach( p -> {
			System.out.println( p.getDescription() + ": " + p.getAmount() );
		});

		org.junit.Assert.assertEquals( 4, salary.getSalaryDeductions().size(), 0.001);
		
		org.junit.Assert.assertEquals( salary.getCommonBase() * ( 4.7 + 0.10 + 1.55 ) / 100.00 + salary.getIrpfBase() * irpf / 100.00  , 
				salary.getSalaryDeductions().stream().collect(Collectors.summingDouble(d -> d.getAmount() )), 
				0.001);

	}
}
