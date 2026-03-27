package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.Period;
import com.esferalia.aon.watson.util.AonDateUtils;

import static org.junit.jupiter.api.Assertions.*;

public class SQLInKindPaymentTestCase extends AbstractSQLTestCase {

	protected static final double DELTA = 0.004;
	
	@Test
	public void testSalaryInKindDeduction() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemDeductions(aonContext);
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IN_KIND, 
		"_EN_ESPECIE");
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {
				}, 
				null);
		//@formatter:on

		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord antiguedad = addConcept(aonContext, "ANTIGUEDAD");
		PaymentConceptRecord paga = addConcept(aonContext, "PAGA");
		
		addPayment(aonContext, contract, salarioBase, "1000.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, plusSalarial, "PLUS * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, antiguedad, "ANTIGUEDAD * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, paga, "(SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD)/12");
		//setData(aonContext, contract, "DIAS_MES", "30.00");
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		addData(aonContext, contract, startDate, endDate, "PLUS", "100.00");
		addData(aonContext, contract, startDate, endDate, "ANTIGUEDAD", "50.00");

		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getName() + " = " + p.getAmount() ));

		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )), (double) salary.getTotalPayment(), DELTA);
		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )), (double) salary.getRemuneration(), DELTA);
		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )), (double) salary.getTotalLiquid(), DELTA);


		addPayment(aonContext, contract, "SALARIO EN ESPECIE", "1.62 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0013);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder());

		salary = calculator.calculate(ctx);
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getName() + " = " + p.getAmount() ));
		

		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )) + 1.62 , (double) salary.getTotalPayment(), DELTA);
		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )) , (double) salary.getRemuneration(), DELTA);
		
		
		assertEquals( 1.62, (double) salary.getTotalDeduction(), DELTA);
		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )) , (double) salary.getTotalLiquid(), DELTA);

		addPayment(aonContext, contract, "SALARIO EN ESPECIE", "33.33 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0013);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder());

		salary = calculator.calculate(ctx);
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getName() + " = " + p.getAmount() ));
		

		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )) + 1.62 + 33.33 , (double) salary.getTotalPayment(), DELTA);
		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )) , (double) salary.getRemuneration(), DELTA);
		
		
		assertEquals( 1.62 + 33.33, (double) salary.getTotalDeduction(), DELTA);
		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )) , (double) salary.getTotalLiquid(), DELTA);

	}


	@Test
	public void testSalaryInKindDeductionIT() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemDeductions(aonContext);
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IN_KIND, 
		"_EN_ESPECIE");
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {
				}, 
				null);
		//@formatter:on

		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord antiguedad = addConcept(aonContext, "ANTIGUEDAD");
		PaymentConceptRecord paga = addConcept(aonContext, "PAGA");
		
		addPayment(aonContext, contract, salarioBase, "1000.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, plusSalarial, "PLUS * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, antiguedad, "ANTIGUEDAD * DIAS_TRABAJADOS / DIAS_MES");
		//addPayment(aonContext, contract, paga, "(SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD)/12"); ???
		setData(aonContext, contract, "DIAS_MES", "30.00");
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		addData(aonContext, contract, startDate, endDate, "PLUS", "100.00");
		addData(aonContext, contract, startDate, endDate, "ANTIGUEDAD", "50.00");
		
		Date startIt = add(startDate, Calendar.DAY_OF_MONTH, 5 );
		Date endIt = add(startIt, Calendar.DAY_OF_MONTH, 14 );
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, endIt, null);

		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getName() + " = " + p.getAmount() ));
		
		int days = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH) - 15  ; 
		
		assertEquals( (double) ( 1150.00 ) * days / 30, (double) salary.getTotalPayment(), DELTA);
		assertEquals( (double) ( 1150.00 ) * days / 30, (double) salary.getRemuneration(), DELTA);
		assertEquals( (double) ( 1150.00 ) * days / 30, (double) salary.getTotalLiquid(), DELTA);


		addPayment(aonContext, contract, "SALARIO EN ESPECIE", "1.62 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0013);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder());

		salary = calculator.calculate(ctx);
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getName() + " = " + p.getAmount() ));
		

		assertEquals( (double) ( 1150.00 + 1.62 ) * days / 30, (double) salary.getTotalPayment(), DELTA);
		assertEquals( (double) ( 1150.00 ) * days / 30, (double) salary.getRemuneration(), DELTA);
		
		
		assertEquals( 1.62 * days / 30, (double) salary.getTotalDeduction(), DELTA);
		assertEquals( (double) ( 1150.00 ) * days / 30 , (double) salary.getTotalLiquid(), DELTA);

		addPayment(aonContext, contract, "SALARIO EN ESPECIE", "33.33 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0013);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder());

		salary = calculator.calculate(ctx);
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getName() + " = " + p.getAmount() ));
		

		assertEquals( (double) ( 1150.00 + 1.62 + 33.33 ) * days / 30, (double) salary.getTotalPayment(), DELTA);
		assertEquals( (double) ( 1150.00 ) * days / 30, (double) salary.getRemuneration(), DELTA);
		
		
		assertEquals( ( 1.62 + 33.33 ) * days / 30, (double) salary.getTotalDeduction(), DELTA);
		assertEquals( (double) ( 1150.00 ) * days / 30, (double) salary.getTotalLiquid(), DELTA);

	}

	@Test
	public void testSalaryInKindIrpf() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemDeductions(aonContext);
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IN_KIND, 
		"_EN_ESPECIE");
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IN_KIND, 
		"IRPF",
		"Retribuci�n Dineraria",
		"BASE_IRPF_DINERO * PORCENTAJE_IRPF/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IN_KIND, 
		"IRPF",
		"Retribuci�n en Especie",
		"BASE_IRPF_ESPECIE * PORCENTAJE_IRPF/100");

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {
				}, 
				null);
		//@formatter:on

		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord antiguedad = addConcept(aonContext, "ANTIGUEDAD");
		PaymentConceptRecord paga = addConcept(aonContext, "PAGA");
		
		addPayment(aonContext, contract, salarioBase, "1000.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, plusSalarial, "PLUS * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, antiguedad, "ANTIGUEDAD * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, paga, "(SALARIO_BASE + PLUS_SALARIAL + ANTIGUEDAD)/12");
		addPayment(aonContext, contract, "SALARIO EN ESPECIE", "1.62 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0013);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		addData(aonContext, contract, startDate, endDate, "PLUS", "100.00");
		addData(aonContext, contract, startDate, endDate, "ANTIGUEDAD", "50.00");
		addData(aonContext, contract, startDate, endDate, "PORCENTAJE_IRPF", "10.00");

		
		
		ISQLContractSalaryCalculatorContext  ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<Salary> calculator = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);
		
		//salary.getSalaryPayments().forEach(p -> System.out.println(p.getName() + " = " + p.getAmount() ));
		salary.getSalaryDeductions().forEach(d -> System.out.println(d.getExpression() +" = " + d.getAmount() ));
		

		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )) + 1.62 , (double) salary.getTotalPayment(), DELTA);
		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )) , (double) salary.getRemuneration(), DELTA);
		
		
		assertEquals( 1.62 + (double) ((( 1150.00 * ( 1.00 + 1.00/12 )) + 1.62 ) * 0.10) , (double) salary.getTotalDeduction(), DELTA);
		
		assertEquals(  (double) ( 1150.00 * ( 1.00 + 1.00/12 )) 
			- ((( 1150.00 * ( 1.00 + 1.00/12 )) + 1.62 ) * 0.10) , (double) salary.getTotalLiquid(), DELTA);


	}

	@Test
	public void testSalaryInKindIrpfWithPeriods() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemDeductions(aonContext);
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IN_KIND, 
		ContextVariable.IN_KIND.getName(),
		"RETRIBUCI�N EN ESPECIE",
		"isdef _EN_ESPECIE ? SUM(_EN_ESPECIE) : HIDE()");
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"Retribuci�n Dineraria",
		"BASE_IRPF_DINERO * PORCENTAJE_IRPF/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"Retribuci�n en Especie",
		"BASE_IRPF_ESPECIE * PORCENTAJE_IRPF/100");

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {
				}, 
				null);
		//@formatter:on

		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord paga = addConcept(aonContext, "PAGA");
		
		addPayment(aonContext, contract, salarioBase, "1000.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, plusSalarial, "PLUS * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, paga, "(SALARIO_BASE + PLUS_SALARIAL )/12");
		addPayment(aonContext, contract, "SALARIO EN ESPECIE", "FRACCIONAR(100.00)", "_P", "_P", PaymentType.CRA_0013);
		addPayment(aonContext, contract, "SALARIO EN ESPECIE", "FRACCIONAR(62.00)", "_P", "_P", PaymentType.CRA_0013);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		addData(aonContext, contract, startDate, endDate, "PLUS", "100.00");
		addData(aonContext, contract, startDate, endDate, "PORCENTAJE_IRPF", "10.00");

		
		Date startIt = add(startDate, Calendar.DAY_OF_MONTH, 5 );
		Date endIt = add(startIt, Calendar.DAY_OF_MONTH, 14 );
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startIt, endIt, null);
		
		ISQLContractSalaryCalculatorContext  ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, add(endDate, Calendar.DAY_OF_MONTH, 10 ), contract);
		SmartContractSalaryCalculator<Salary> calculator = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getName() + " = " + p.getAmount() ));
		salary.getSalaryDeductions().forEach(d -> System.out.println(d.getExpression() +" = " + d.getAmount() ));
		
		int monthDays = AonDateUtils.get(endDate, Calendar.DAY_OF_MONTH);
		int activeDays = monthDays - (int) new Period(startIt, endIt).getDays();
		
		assertEquals( (double) (( 1100.00 * ( 1.00 + 1.00/12 ))) * activeDays / monthDays + 162.00 , (double) salary.getTotalPayment(), DELTA);
		assertEquals( (double) ( 1100.00 * ( 1.00 + 1.00/12 )) * activeDays / monthDays   , (double) salary.getRemuneration(), DELTA);
		
		
		assertEquals( 162.00 + (double) ((( 1100.00 * ( 1.00 + 1.00/12 )) * activeDays / monthDays + 162.00 ) * 0.10) , (double) salary.getTotalDeduction(), DELTA);
		
//		assertEquals(  (double) ( 1150.00 * ( 1.00 + 1.00/12 )) 
//			- ((( 1150.00 * ( 1.00 + 1.00/12 )) + 1.62 ) * 0.10) , (double) salary.getTotalLiquid(), DELTA);

	}
	

	@Test
	public void testSalaryInKindIrpfWithIssueDate() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemDeductions(aonContext);
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IN_KIND, 
		ContextVariable.IN_KIND.getName(),
		"RETRIBUCI�N EN ESPECIE",
		"isdef _EN_ESPECIE ? SUM(_EN_ESPECIE) : HIDE()");
		
		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"Retribuci�n Dineraria",
		"BASE_IRPF_DINERO * PORCENTAJE_IRPF/100");

		addSSRegimeDeduction(
		aonContext, 
		SSRegimeType.GENERAL, 
		AonDateUtils.getFirstDayOfYear(getToday()),
		DeductionType.IRPF, 
		"IRPF",
		"Retribuci�n en Especie",
		"BASE_IRPF_ESPECIE * PORCENTAJE_IRPF/100");

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {}, 
				new String[] {
				}, 
				null);
		//@formatter:on

		PaymentConceptRecord salarioBase = addConcept(aonContext, "SALARIO_BASE");
		PaymentConceptRecord plusSalarial = addConcept(aonContext, "PLUS_SALARIAL");
		PaymentConceptRecord paga = addConcept(aonContext, "PAGA");
		
		addPayment(aonContext, contract, salarioBase, "1000.00 * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, plusSalarial, "PLUS * DIAS_TRABAJADOS / DIAS_MES");
		addPayment(aonContext, contract, paga, "(SALARIO_BASE + PLUS_SALARIAL )/12");
		addPayment(aonContext, contract, "SALARIO EN ESPECIE", "FRACCIONAR(100.00)", "_P", "_P", PaymentType.CRA_0013);
		addPayment(aonContext, contract, "SALARIO EN ESPECIE", "FRACCIONAR(62.00)", "_P", "_P", PaymentType.CRA_0013);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		addData(aonContext, contract, startDate, endDate, "PLUS", "100.00");
		addData(aonContext, contract, startDate, endDate, "PORCENTAJE_IRPF", "10.00");

		
		ISQLContractSalaryCalculatorContext  ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, add(endDate, Calendar.DAY_OF_MONTH, 10 ), add(endDate, Calendar.DAY_OF_MONTH, 10 ), contract);
		SmartContractSalaryCalculator<Salary> calculator = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder());

		Salary salary = calculator.calculate(ctx);
		
		salary.getSalaryDeductions().forEach(d -> System.out.println(d.getExpression() +" = " + d.getAmount() ));
		
		
		
		assertEquals( 162.00 + (double) (( 1100.00 * ( 1.00 + 1.00/12 ))  + 162.00 ) * 0.10 , (double) salary.getTotalDeduction(), DELTA);
		

	}
	
}
