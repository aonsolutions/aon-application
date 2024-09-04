package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_GROUP;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.server.SalaryDraftBuilder;
import com.esferalia.aon.gwt.payroll.shared.CompositeDeduction;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.jooq.tables.records.SystemPaymentRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.google.gwt.editor.client.Editor.Ignore;

public class SQLSalaryDraftBuilderTestCase extends AbstractSQLTestCase {

	protected static final double DELTA = 0.004;
	

	@Test
	public void testCompositeDescription() throws ExpressionException, SQLException,
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
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<>();
		
		
		SalaryDraft salaryDraft = new SalaryDraft();
		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(salaryDraft) {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date start, java.util.Date end, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println(description + ": " + amount + "[" + start + "..." + end + "]");
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		};
		calculator.setSalaryBuilder(salaryDraftBuilder);
		calculator.calculate(ctx);
		
		salaryDraft.getPayments().forEach( p -> System.out.println(p.getDescription() +":" + p.getAmount()));
		salaryDraft.getPayments().forEach( p -> assertEquals("31 DÍAS DE IT", p.getDescription()) );
		

	}

	@Test
	public void testCompositeDescriptionII() throws ExpressionException, SQLException,
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
		"SALARIO BASE (@{SALARIO_DIARIO} x @{JORNADAS_REALES} )",
		"SALARIO_DIARIO * JORNADAS_REALES ",
		"_P", 
		"_P", 
		PaymentType.CRA_0001);
		
		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		addData(aonContext, contract, startDate, endDate, "SALARIO_DIARIO", "66.66");
		

		int i = 0;
		for( Date date = startDate;  date.compareTo(endDate) <= 0; date = add(date, Calendar.DAY_OF_MONTH, 5)) {
		    addData(aonContext, contract, date, date, "JORNADAS_REALES", "1.0");
		    i++;
		}
		
		int jornadasReales = i;
		
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<>();
		
		
		SalaryDraft salaryDraft = new SalaryDraft();
		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(salaryDraft) {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date start, java.util.Date end, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println(description + ": " + amount + "[" + start + "..." + end + "]");
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		};
		calculator.setSalaryBuilder(salaryDraftBuilder);
		calculator.calculate(ctx);
		
		salaryDraft.getPayments().forEach( p -> System.out.println(p.getDescription() +":" + p.getAmount()));
		salaryDraft.getPayments().forEach( p -> assertEquals("SALARIO BASE (66.66 x "+jornadasReales+" )", p.getDescription()) );
		

	}

	@Test
	public void testCompositeDescriptionIII() throws ExpressionException, SQLException,
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
		"DESCUENTO_DIAS_VACACIONES",
		"DIAS_NO_TRABAJADOS",
		"_P", 
		"_P", 
		PaymentType.CRA_0001);

		addPayment(aonContext, 
		contract, 
		contract.getStartDate(), 
		null,
		"SALARIO BASE ",
		"1000.00 * DIAS_TRABAJADOS ",
		"_P", 
		"_P", 
		PaymentType.CRA_0001);
		
		addPayment(aonContext, 
		contract, 
		contract.getStartDate(), 
		null,
		"VACACIONES (@{DIAS_VACACIONES} x @{SALARIO_DIA_VACACIONES} )",
		"DIAS_VACACIONES * SALARIO_DIA_VACACIONES",
		"_P", 
		"_P", 
		PaymentType.CRA_0001);

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		addData(aonContext, contract, startDate, endDate, "SALARIO_DIA_VACACIONES", "66.66");
		addData(aonContext, contract, startDate, endDate, "DIAS_NO_TRABAJADOS", "DIAS_VACACIONES");
		

		int i = 0;
		for( Date date = startDate;  date.compareTo(endDate) <= 0; date = add(date, Calendar.DAY_OF_MONTH, 5)) {
		    addData(aonContext, contract, date, date, "DIAS_VACACIONES", "1.0");
		    i++;
		}
		
		int jornadasReales = i;
		
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<>();
		
		
		SalaryDraft salaryDraft = new SalaryDraft();
		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(salaryDraft) {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date start, java.util.Date end, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println(description + ": " + amount + "[" + start + "..." + end + "]");
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		};
		calculator.setSalaryBuilder(salaryDraftBuilder);
		calculator.calculate(ctx);
		
		salaryDraft.getPayments().forEach( p -> System.out.println(p.getDescription() +":" + p.getAmount()));
		salaryDraft.getPayments().stream().filter( p -> p.getDescription().startsWith("VACACIONES")).forEach( p -> assertEquals("VACACIONES ("+jornadasReales + " x 66.66 )", p.getDescription()) );
		

	}

	@Test
	public void testStandardIrpf() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemDeductions(aonContext);
		
		PaymentConceptRecord irpfCtaEsp = 
		addConcept(aonContext, "IRPF_CTA_ESP");
		
		addSSRegimePayment(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		irpfCtaEsp, 
		PaymentType.CRA_0000,
		"isdef BASE_CTA_ESP ? ( BASE_CTA_ESP * PORCENTAJE_IRPF / 100.00 ) : HIDE()",
		null,
		null);
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IN_KIND, 
		"EN_ESPECIE",
		"Valor de los Productos Recibidos en Especie",
		"_EN_ESPECIE");
		

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Retribución Dineraria",
		"BASE_IRPF_DINERO * PORCENTAJE_IRPF/100.00");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Retribución en Especie",
		"_P=(BASE_IRPF_ESPECIE * PORCENTAJE_IRPF/100.00 - (isdef IRPF_CTA_ESP ? IRPF_CTA_ESP : 0.00)); (_P > 0.0049 ) ? _P : HIDE()");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Ingreso a Cuenta Especie a cargo de la Empresa",
		"isdef BASE_CTA_ESP ? IRPF_CTA_ESP : HIDE()");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.COMMON_CONTINGENCY, 
		"CGC",
		"4.70%",
		"BASE_CGC * PORCENTAJE_CGC/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.COMMON_CONTINGENCY, 
		"MEI",
		"0.10%",
		"BASE_CGC * PORCENTAJE_MEI/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.UNEMPLOYMENT, 
		"DESMPL",
		"@{PORCENTAJE_DESMPL} %",
		"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.JOB_TRAINING, 
		"FP",
		"0.10 %",
		"BASE_CGC * PORCENTAJE_FP/100");


		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {}, 
				null);
		//@formatter:on

		setData(aonContext, contract, "DIAS_MES", "30.00");
		setData(aonContext, contract, "PORCENTAJE_FP", "0.10");
		setData(aonContext, contract, "PORCENTAJE_MEI", "0.10");
		setData(aonContext, contract, "PORCENTAJE_CGC", "4.70");
		setData(aonContext, contract, "PORCENTAJE_IRPF", "10.00");
		setData(aonContext, contract, "PORCENTAJE_DESMPL", "1.55");

		addPayment(aonContext, 
		contract, 
		"SALARIO BASE",
		"1000.00 * DIAS_TRABAJADOS / DIAS_MES", 
		"_P", 
		"_P", 
		PaymentType.CRA_0001);
		
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		addData(aonContext, contract, startDate, add(startDate, Calendar.DAY_OF_MONTH, 14), QUOTE_GROUP, "\"05\"");
		addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH, 15), endDate, QUOTE_GROUP, "\"04\"");

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<>();
		
		
		SalaryDraft salaryDraft = new SalaryDraft();
		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(salaryDraft) {
		};
		calculator.setSalaryBuilder(salaryDraftBuilder);
		calculator.calculate(ctx);
		
		assertEquals(5, salaryDraft.getDeductions().size());
		salaryDraft.getDeductions().forEach( d -> {
		    System.out.println(d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
		});
		{
		Deduction irpfs [] =
		salaryDraft.getDeductions().stream()
		.filter( d -> "IRPF".equals(d.getName()))
		.toArray(Deduction[]::new);
		
		assertEquals(1, irpfs.length);
		assertFalse(irpfs[0] instanceof CompositeDeduction);
		
		assertEquals("IRPF", irpfs[0].getDescription());
		}
		
		for ( String name : new String [] {"CGC", "MEI", "FP", "DESMPL"} ) {
			Deduction deductions [] =
				salaryDraft.getDeductions().stream()
				.filter( d -> name.equals(d.getName()))
				.toArray(Deduction[]::new);
			assertEquals(1, deductions.length);
			assertTrue(deductions[0] instanceof CompositeDeduction);
			Collection<Deduction> childDeductions = 
			((CompositeDeduction) deductions[0] ).getChilds();
			assertEquals(2, childDeductions.size());
			
			childDeductions.forEach( d -> {
			    System.out.println(d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
			});
		}
		

	}

	@Test
	public void testCompositeIrpf() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemDeductions(aonContext);
		
		PaymentConceptRecord irpfCtaEsp = 
		addConcept(aonContext, "IRPF_CTA_ESP");
		
		addSSRegimePayment(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		irpfCtaEsp, 
		PaymentType.CRA_0000,
		"isdef BASE_CTA_ESP ? ( BASE_CTA_ESP * PORCENTAJE_IRPF / 100.00 ) : HIDE()",
		null,
		null);
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IN_KIND, 
		"EN_ESPECIE",
		"Valor de los Productos Recibidos en Especie",
		"_EN_ESPECIE");
		

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Retribución Dineraria",
		"BASE_IRPF_DINERO * PORCENTAJE_IRPF/100.00");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Retribución en Especie",
		"_P=(BASE_IRPF_ESPECIE * PORCENTAJE_IRPF/100.00 - (isdef IRPF_CTA_ESP ? IRPF_CTA_ESP : 0.00)); (_P > 0.0049 ) ? _P : HIDE()");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Ingreso a Cuenta Especie a cargo de la Empresa",
		"isdef BASE_CTA_ESP ? IRPF_CTA_ESP : HIDE()");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.COMMON_CONTINGENCY, 
		"CGC",
		"4.70%",
		"BASE_CGC * PORCENTAJE_CGC/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.COMMON_CONTINGENCY, 
		"MEI",
		"0.10%",
		"BASE_CGC * PORCENTAJE_MEI/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.UNEMPLOYMENT, 
		"DESMPL",
		"@{PORCENTAJE_DESMPL} %",
		"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.JOB_TRAINING, 
		"FP",
		"0.10 %",
		"BASE_CGC * PORCENTAJE_FP/100");


		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {}, 
				null);
		//@formatter:on

		setData(aonContext, contract, "DIAS_MES", "30.00");
		setData(aonContext, contract, "PORCENTAJE_FP", "0.10");
		setData(aonContext, contract, "PORCENTAJE_MEI", "0.10");
		setData(aonContext, contract, "PORCENTAJE_CGC", "4.70");
		setData(aonContext, contract, "PORCENTAJE_IRPF", "10.00");
		setData(aonContext, contract, "PORCENTAJE_DESMPL", "1.55");

		addPayment(aonContext, 
		contract, 
		"SALARIO BASE",
		"1000.00 * DIAS_TRABAJADOS / DIAS_MES", 
		"_P", 
		"_P", 
		PaymentType.CRA_0001);
		
		addPayment(aonContext, 
		contract, 
		"SALARIO EN ESPECIE", 
		"1.62 * DIAS_TRABAJADOS / DIAS_MES", 
		"_P", 
		"_P", 
		PaymentType.CRA_0013);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		addData(aonContext, contract, startDate, add(startDate, Calendar.DAY_OF_MONTH, 14), QUOTE_GROUP, "\"05\"");
		addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH, 15), endDate, QUOTE_GROUP, "\"04\"");

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<>();
		
		
		SalaryDraft salaryDraft = new SalaryDraft();
		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(salaryDraft) {
		};
		calculator.setSalaryBuilder(salaryDraftBuilder);
		calculator.calculate(ctx);
		
		assertEquals(6, salaryDraft.getDeductions().size());
		salaryDraft.getDeductions().forEach( d -> {
		    System.out.println(d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
		});
		{
		Deduction irpfs [] =
		salaryDraft.getDeductions().stream()
		.filter( d -> "IRPF".equals(d.getName()))
		.toArray(Deduction[]::new);
		
		assertEquals(1, irpfs.length);
		assertTrue(irpfs[0] instanceof CompositeDeduction);
		Collection<Deduction> irpfDeductions = 
		((CompositeDeduction) irpfs[0] ).getChilds();
		assertEquals(2, irpfDeductions.size());
		
		irpfDeductions.forEach( d -> {
		    System.out.println(d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
		});

		assertEquals("IRPF", irpfs[0].getDescription());
		}
		
		for ( String name : new String [] {"CGC", "MEI", "FP", "DESMPL"} ) {
			Deduction deductions [] =
				salaryDraft.getDeductions().stream()
				.filter( d -> name.equals(d.getName()))
				.toArray(Deduction[]::new);
			assertEquals(1, deductions.length);
			assertTrue(deductions[0] instanceof CompositeDeduction);
			Collection<Deduction> childDeductions = 
			((CompositeDeduction) deductions[0] ).getChilds();
			assertEquals(2, childDeductions.size());
			
			childDeductions.forEach( d -> {
			    System.out.println(d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
			});
		}
		

	}

	@Test
	public void testCompositeIrpfDraftPercent() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemDeductions(aonContext);
		
		PaymentConceptRecord irpfCtaEsp = 
		addConcept(aonContext, "IRPF_CTA_ESP");
		
		addSSRegimePayment(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		irpfCtaEsp, 
		PaymentType.CRA_0000,
		"isdef BASE_CTA_ESP ? ( BASE_CTA_ESP * PORCENTAJE_IRPF / 100.00 ) : HIDE()",
		null,
		null);
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IN_KIND, 
		"EN_ESPECIE",
		"Valor de los Productos Recibidos en Especie",
		"_EN_ESPECIE");
		

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Retribución Dineraria",
		"BASE_IRPF_DINERO * PORCENTAJE_IRPF/100.00");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Retribución en Especie",
		"_P=(BASE_IRPF_ESPECIE * PORCENTAJE_IRPF/100.00 - (isdef IRPF_CTA_ESP ? IRPF_CTA_ESP : 0.00)); (_P > 0.0049 ) ? _P : HIDE()");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Ingreso a Cuenta Especie a cargo de la Empresa",
		"isdef BASE_CTA_ESP ? IRPF_CTA_ESP : HIDE()");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.COMMON_CONTINGENCY, 
		"CGC",
		"4.70%",
		"BASE_CGC * PORCENTAJE_CGC/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.COMMON_CONTINGENCY, 
		"MEI",
		"0.10%",
		"BASE_CGC * PORCENTAJE_MEI/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.UNEMPLOYMENT, 
		"DESMPL",
		"@{PORCENTAJE_DESMPL} %",
		"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.JOB_TRAINING, 
		"FP",
		"0.10 %",
		"BASE_CGC * PORCENTAJE_FP/100");


		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {}, 
				null);
		//@formatter:on

		setData(aonContext, contract, "DIAS_MES", "30.00");
		setData(aonContext, contract, "PORCENTAJE_FP", "0.10");
		setData(aonContext, contract, "PORCENTAJE_MEI", "0.10");
		setData(aonContext, contract, "PORCENTAJE_CGC", "4.70");
		setData(aonContext, contract, "PORCENTAJE_DESMPL", "1.55");

		addPayment(aonContext, 
		contract, 
		"SALARIO BASE",
		"1000.00 * DIAS_TRABAJADOS / DIAS_MES", 
		"_P", 
		"_P", 
		PaymentType.CRA_0001);
		
		addPayment(aonContext, 
		contract, 
		"SALARIO EN ESPECIE", 
		"1.62 * DIAS_TRABAJADOS / DIAS_MES", 
		"_P", 
		"_P", 
		PaymentType.CRA_0013);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		addData(aonContext, contract, startDate, add(startDate, Calendar.DAY_OF_MONTH, 14), QUOTE_GROUP, "\"05\"");
		addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH, 15), endDate, QUOTE_GROUP, "\"04\"");

		
		
		Employee employee = new Employee();
		employee.setId(contract.getId());
		
		SalaryDraft salaryDraft = new SalaryDraft();
		salaryDraft.setEmployee(employee);
		salaryDraft.setStartDate(startDate);
		salaryDraft.setEndDate(endDate);
		salaryDraft.setIssueDate(endDate);
		salaryDraft.setType(Salary.Type.SALARY);

		NumberVariable irpfDraft = new NumberVariable();
		irpfDraft.setValue(10.00);
		irpfDraft.setEndDate(endDate);
		irpfDraft.setStartDate(startDate);
		irpfDraft.setName("PORCENTAJE_IRPF");
		salaryDraft.addDraftVariable(irpfDraft);

		ISQLContractSalaryCalculatorContext ctx = EmployeesServiceHelper.getSalaryCalculatorContext(
			connection, salaryDraft, null);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<>();

		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(salaryDraft) {
		};
		calculator.setSalaryBuilder(salaryDraftBuilder);
		calculator.calculate(ctx);
		
		assertEquals(6, salaryDraft.getDeductions().size());
		salaryDraft.getDeductions().forEach( d -> {
		    System.out.println(d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
		});
		{
		Deduction irpfs [] =
		salaryDraft.getDeductions().stream()
		.filter( d -> "IRPF".equals(d.getName()))
		.toArray(Deduction[]::new);
		
		assertEquals(1, irpfs.length);
		assertTrue(irpfs[0] instanceof CompositeDeduction);
		Collection<Deduction> irpfDeductions = 
		((CompositeDeduction) irpfs[0] ).getChilds();
		assertEquals(2, irpfDeductions.size());
		
		irpfDeductions.forEach( d -> {
		    System.out.println(d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
		});

		assertEquals("IRPF", irpfs[0].getDescription());
		}
		
		for ( String name : new String [] {"CGC", "MEI", "FP", "DESMPL"} ) {
			Deduction deductions [] =
				salaryDraft.getDeductions().stream()
				.filter( d -> name.equals(d.getName()))
				.toArray(Deduction[]::new);
			assertEquals(1, deductions.length);
			assertTrue(deductions[0] instanceof CompositeDeduction);
			Collection<Deduction> childDeductions = 
			((CompositeDeduction) deductions[0] ).getChilds();
			assertEquals(2, childDeductions.size());
			
			childDeductions.forEach( d -> {
			    System.out.println(d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
			});
		}
		

	}

	@Test
	public void testCompositeIrpfaCta() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemDeductions(aonContext);
		
		PaymentConceptRecord irpfCtaEsp = 
		addConcept(aonContext, "IRPF_CTA_ESP");
		
		addSSRegimePayment(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		irpfCtaEsp, 
		PaymentType.CRA_0000,
		"isdef BASE_CTA_ESP ? ( BASE_CTA_ESP * PORCENTAJE_IRPF / 100.00 ) : HIDE()",
		null,
		null);
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IN_KIND, 
		"EN_ESPECIE",
		"Valor de los Productos Recibidos en Especie",
		"_EN_ESPECIE");
		

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Retribución Dineraria",
		"BASE_IRPF_DINERO * PORCENTAJE_IRPF/100.00");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Retribución en Especie",
		"_P=(BASE_IRPF_ESPECIE * PORCENTAJE_IRPF/100.00 - (isdef IRPF_CTA_ESP ? IRPF_CTA_ESP : 0.00)); (_P > 0.0049 ) ? _P : HIDE()");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Ingreso a Cuenta Especie a cargo de la Empresa",
		"isdef BASE_CTA_ESP ? BASE_CTA_ESP : HIDE() ; IRPF_CTA_ESP");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.COMMON_CONTINGENCY, 
		"CGC",
		"4.70%",
		"BASE_CGC * PORCENTAJE_CGC/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.COMMON_CONTINGENCY, 
		"MEI",
		"0.10%",
		"BASE_CGC * PORCENTAJE_MEI/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.UNEMPLOYMENT, 
		"DESMPL",
		"@{PORCENTAJE_DESMPL} %",
		"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.JOB_TRAINING, 
		"FP",
		"0.10 %",
		"BASE_CGC * PORCENTAJE_FP/100");

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {}, 
				null);
		//@formatter:on

		setData(aonContext, contract, "DIAS_MES", "30.00");
		setData(aonContext, contract, "PORCENTAJE_FP", "0.10");
		setData(aonContext, contract, "PORCENTAJE_MEI", "0.10");
		setData(aonContext, contract, "PORCENTAJE_CGC", "4.70");
		setData(aonContext, contract, "PORCENTAJE_IRPF", "10.00");
		setData(aonContext, contract, "PORCENTAJE_DESMPL", "1.55");

		addPayment(aonContext, 
		contract, 
		"SALARIO BASE",
		"1000.00 * DIAS_TRABAJADOS / DIAS_MES", 
		"_P", 
		"_P", 
		PaymentType.CRA_0001);
		
		// A CTA 
		addPayment(aonContext, 
		contract, 
		"SALARIO EN ESPECIE", 
		"1.62 * DIAS_TRABAJADOS / DIAS_MES", 
		"BASE_CTA_ESP=_P", 
		"_P", 
		PaymentType.CRA_0013);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		addData(aonContext, contract, startDate, add(startDate, Calendar.DAY_OF_MONTH, 19), QUOTE_GROUP, "\"05\"");
		addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH, 20), endDate, QUOTE_GROUP, "\"04\"");

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<>();
		
		
		SalaryDraft salaryDraft = new SalaryDraft();
		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(salaryDraft) {
		};
		calculator.setSalaryBuilder(salaryDraftBuilder);
		calculator.calculate(ctx);
		
		System.out.println("DATA ==================================================================");
		salaryDraft.getContext().forEach( v -> {
		    System.out.println("\t" + v.getName() + " = " + v.getValue() +"" );
		});
		System.out.println("PAYMENTS ==================================================================");
		salaryDraft.getPayments().forEach( p -> {
		    System.out.println("\t" + p.getName() + " = " + p.getAmount() +"" );
		});
		System.out.println("DEDUCTIONS ================================================================");
		
		//assertEquals(6, salaryDraft.getDeductions().size());
		salaryDraft.getDeductions().forEach( d -> {
		    System.out.println("\t" + d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
		});
		System.out.println("IRPF ======================================================================");
		{
		Deduction irpfs [] =
		salaryDraft.getDeductions().stream()
		.filter( d -> "IRPF".equals(d.getName()))
		.toArray(Deduction[]::new);
		
		assertEquals(1, irpfs.length);
		assertTrue(irpfs[0] instanceof CompositeDeduction);
		Collection<Deduction> irpfDeductions = 
		((CompositeDeduction) irpfs[0] ).getChilds();
		assertEquals(2, irpfDeductions.size());
		
		irpfDeductions.forEach( d -> {
		    System.out.println("\t" + d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
		    assertTrue(d.getDescription().contains("IRPF "));
		});

		assertEquals("IRPF", irpfs[0].getDescription());
		}
		
		for ( String name : new String [] {"CGC", "MEI", "FP", "DESMPL"} ) {
			System.out.println(name +" ======================================================================");
			Deduction deductions [] =
				salaryDraft.getDeductions().stream()
				.filter( d -> name.equals(d.getName()))
				.toArray(Deduction[]::new);
			assertEquals(1, deductions.length);
			assertTrue(deductions[0] instanceof CompositeDeduction);
			Collection<Deduction> childDeductions = 
			((CompositeDeduction) deductions[0] ).getChilds();
			assertEquals(2, childDeductions.size());
			
			childDeductions.forEach( d -> {
			    System.out.println("\t" + d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
			});
		}

	}

	@Test
	public void testCompositeIrpfaCtaII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemDeductions(aonContext);
		
		PaymentConceptRecord irpfCtaEsp = 
		addConcept(aonContext, "IRPF_CTA_ESP");
		
		addSSRegimePayment(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		irpfCtaEsp, 
		PaymentType.CRA_0000,
		"isdef BASE_CTA_ESP ? ( BASE_CTA_ESP * PORCENTAJE_IRPF / 100.00 ) : HIDE()",
		null,
		null);
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IN_KIND, 
		"EN_ESPECIE",
		"Valor de los Productos Recibidos en Especie",
		"_EN_ESPECIE");
		

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Retribución Dineraria",
		"BASE_IRPF_DINERO * PORCENTAJE_IRPF/100.00");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Retribución en Especie",
		"_P=(BASE_IRPF_ESPECIE * PORCENTAJE_IRPF/100.00 - (isdef IRPF_CTA_ESP ? IRPF_CTA_ESP : 0.00)); (_P > 0.0049 ) ? _P : HIDE()");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"IRPF Ingreso a Cuenta Especie a cargo de la Empresa",
		"isdef BASE_CTA_ESP ? BASE_CTA_ESP : HIDE() ; IRPF_CTA_ESP");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.COMMON_CONTINGENCY, 
		"CGC",
		"4.70%",
		"BASE_CGC * PORCENTAJE_CGC/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.COMMON_CONTINGENCY, 
		"MEI",
		"0.10%",
		"BASE_CGC * PORCENTAJE_MEI/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.UNEMPLOYMENT, 
		"DESMPL",
		"@{PORCENTAJE_DESMPL} %",
		"BASE_CGP * ( isdef PORCENTAJE_DESMPL ? PORCENTAJE_DESMPL : PORCENTAJE_DESMPL=(INDEFINIDO ? 1.55 : 1.60 ))/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.JOB_TRAINING, 
		"FP",
		"0.10 %",
		"BASE_CGC * PORCENTAJE_FP/100");

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {}, 
				null);
		//@formatter:on

		setData(aonContext, contract, "DIAS_MES", "30.00");
		setData(aonContext, contract, "PORCENTAJE_FP", "0.10");
		setData(aonContext, contract, "PORCENTAJE_MEI", "0.10");
		setData(aonContext, contract, "PORCENTAJE_CGC", "4.70");
		setData(aonContext, contract, "PORCENTAJE_IRPF", "10.00");
		setData(aonContext, contract, "PORCENTAJE_DESMPL", "1.55");

		addPayment(aonContext, 
		contract, 
		"SALARIO BASE",
		"1000.00 * DIAS_TRABAJADOS / DIAS_MES", 
		"_P", 
		"_P", 
		PaymentType.CRA_0001);
		
		// A CTA 
		addPayment(aonContext, 
		contract, 
		"SALARIO EN ESPECIE", 
		"1.62 / 2.00 * DIAS_TRABAJADOS / DIAS_MES", 
		"BASE_CTA_ESP=( isdef BASE_CTA_ESP ? BASE_CTA_ESP : 0.00 ) + _P; _P", 
		"_P", 
		PaymentType.CRA_0013);
		
		addPayment(aonContext, 
		contract, 
		"SALARIO EN ESPECIE", 
		"1.62 / 2.00 * DIAS_TRABAJADOS / DIAS_MES", 
		"BASE_CTA_ESP=( isdef BASE_CTA_ESP ? BASE_CTA_ESP : 0.00 ) + _P; _P", 
		"_P", 
		PaymentType.CRA_0013);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		addData(aonContext, contract, startDate, add(startDate, Calendar.DAY_OF_MONTH, 19), QUOTE_GROUP, "\"05\"");
		addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH, 20), endDate, QUOTE_GROUP, "\"04\"");

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<>();
		
		
		SalaryDraft salaryDraft = new SalaryDraft();
		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(salaryDraft) {
		};
		calculator.setSalaryBuilder(salaryDraftBuilder);
		calculator.calculate(ctx);
		
		System.out.println("DATA ==================================================================");
		salaryDraft.getContext().forEach( v -> {
		    System.out.println("\t" + v.getName() + " = " + v.getValue() +"" + v.getStartDate() );
		});
		System.out.println("PAYMENTS ==================================================================");
		salaryDraft.getPayments().forEach( p -> {
		    System.out.println("\t" + p.getName() + " = " + p.getAmount() +"" );
		});
		System.out.println("DEDUCTIONS ================================================================");
		
		//assertEquals(6, salaryDraft.getDeductions().size());
		salaryDraft.getDeductions().forEach( d -> {
		    System.out.println("\t" + d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
		});
		System.out.println("IRPF ======================================================================");
		{
		Deduction irpfs [] =
		salaryDraft.getDeductions().stream()
		.filter( d -> "IRPF".equals(d.getName()))
		.toArray(Deduction[]::new);
		
		assertEquals(1, irpfs.length);
		assertTrue(irpfs[0] instanceof CompositeDeduction);
		Collection<Deduction> irpfDeductions = 
		((CompositeDeduction) irpfs[0] ).getChilds();
		irpfDeductions.forEach( d -> {
		    System.out.println("\t" + d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
		    assertTrue(d.getDescription().contains("IRPF "));
		});
		assertEquals(2, irpfDeductions.size());
		

		assertEquals("IRPF", irpfs[0].getDescription());
		}
		
		for ( String name : new String [] {"CGC", "MEI", "FP", "DESMPL"} ) {
			System.out.println(name +" ======================================================================");
			Deduction deductions [] =
				salaryDraft.getDeductions().stream()
				.filter( d -> name.equals(d.getName()))
				.toArray(Deduction[]::new);
			assertEquals(1, deductions.length);
			assertTrue(deductions[0] instanceof CompositeDeduction);
			Collection<Deduction> childDeductions = 
			((CompositeDeduction) deductions[0] ).getChilds();
			assertEquals(2, childDeductions.size());
			
			childDeductions.forEach( d -> {
			    System.out.println("\t" + d.getName() + " = " + d.getDescription() + "(" + d.getAmount() +")" );
			});
		}

	}

	@Test
	@Ignore
	public void testJoinContext() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		
		Date contractStartDate = add(getToday(), Calendar.YEAR, -10);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				contractStartDate,
				new String[] {}, 
				new String[] {}, 
				null);
		//@formatter:on
		
		SystemPaymentRecord systemPayment = 
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				add(getFirstDayOfYear(getToday()),Calendar.YEAR,-10),
				PaymentType.CRA_0054, 
				"(CAUSA_INDEMNIZACION == IMPROCEDENTE) ? 45 * AÑOS_TRABAJADOS * SALARIO_DIA : REMOVE()",
				null ,
				null, 
				SalaryType.SETTLE);
		systemPayment.setEndDate(add(add(getToday(),Calendar.YEAR,-5), DAY_OF_MONTH,-1));
		systemPayment.update();
		
		addSSRegimePayment(aonContext, 
				SSRegimeType.GENERAL, 
				add(getToday(),Calendar.YEAR,-5), 
				PaymentType.CRA_0054, 
				"(CAUSA_INDEMNIZACION == IMPROCEDENTE)? 33 * AÑOS_TRABAJADOS * SALARIO_DIA : REMOVE()",
				null ,
				null, 
				SalaryType.SETTLE);
		
		SalaryDraft salaryDraft = new SalaryDraft();
		Employee employee = new Employee();
		employee.setId(contract.getId());

		salaryDraft.setEmployee(employee);
		salaryDraft.setStartDate(contractStartDate);
		salaryDraft.setEndDate(getToday());
		salaryDraft.setIssueDate(getToday());
		salaryDraft.setType(com.esferalia.aon.gwt.payroll.shared.Salary.Type.SETTLE);
		
		Variable salarioDia = new NumberVariable();
		salarioDia.setName("SALARIO_DIA");
		salarioDia.setValue(100.00);
		salarioDia.setStartDate(contractStartDate);
		salarioDia.setEndDate(getToday());
		salaryDraft.addDraftVariable(salarioDia);

		Variable causaIndemnizacion = new StringVariable() ;
		causaIndemnizacion.setName("CAUSA_INDEMNIZACION");
		causaIndemnizacion.setValue("IMPROCEDENTE");
		causaIndemnizacion.setStartDate(contractStartDate);
		causaIndemnizacion.setEndDate(getToday());
		salaryDraft.addDraftVariable(causaIndemnizacion);

		ISQLContractSalaryCalculatorContext ctx = EmployeesServiceHelper.getSettleCalculatorContextImpl(connection, salaryDraft, null);;
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<>();
		
		
		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(salaryDraft);
		calculator.setSalaryBuilder(salaryDraftBuilder);
		calculator.calculate(ctx);
		
//		salaryDraft.getPayments().forEach( p -> System.out.println(p.getDescription() +":" + p.getAmount()));

//		salaryDraft.getContext().stream()
//		.filter(v -> v.getName().equals("CAUSA_INDEMNIZACION"))
//		.forEach( v -> System.out.println(v.getName() + " = '" + v.getValue() +"' " + v.getStartDate() + ".." + v.getEndDate()));

		long count =
		salaryDraft.getContext().stream()
		.filter(v -> v.getName().equals("CAUSA_INDEMNIZACION"))
		.peek( v -> System.out.println(v.getName() + " = '" + v.getValue() +"' " + v.getStartDate() + ".." + v.getEndDate()))
		.count();
		
		assertEquals(1, count);

		salaryDraft.getContext().stream()
		.filter(v -> v.getName().equals("CAUSA_INDEMNIZACION"))
		.forEach(v -> assertEquals(contractStartDate, v.getStartDate()) );

		salaryDraft.getContext().stream()
		.filter(v -> v.getName().equals("CAUSA_INDEMNIZACION"))
		.forEach(v -> assertEquals(getToday(), v.getEndDate()) );
}
	

}
