package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

import junit.framework.Assert;

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
		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )) + 1.62 , (double) salary.getRemuneration(), DELTA);
		
		
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
		assertEquals( (double) ( 1150.00 * ( 1.00 + 1.00/12 )) + 1.62 + 33.33, (double) salary.getRemuneration(), DELTA);
		
		
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

		assertEquals( (double) ( 1150.00 ) /2, (double) salary.getTotalPayment(), DELTA);
		assertEquals( (double) ( 1150.00 ) /2, (double) salary.getRemuneration(), DELTA);
		assertEquals( (double) ( 1150.00 ) /2, (double) salary.getTotalLiquid(), DELTA);


		addPayment(aonContext, contract, "SALARIO EN ESPECIE", "1.62 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0013);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder());

		salary = calculator.calculate(ctx);
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getName() + " = " + p.getAmount() ));
		

		assertEquals( (double) ( 1150.00 + 1.62 ) / 2, (double) salary.getTotalPayment(), DELTA);
		assertEquals( (double) ( 1150.00 + 1.62 ) / 2, (double) salary.getRemuneration(), DELTA);
		
		
		assertEquals( 1.62 / 2, (double) salary.getTotalDeduction(), DELTA);
		assertEquals( (double) ( 1150.00 ) / 2 , (double) salary.getTotalLiquid(), DELTA);

		addPayment(aonContext, contract, "SALARIO EN ESPECIE", "33.33 * DIAS_TRABAJADOS / DIAS_MES", "_P", "_P", PaymentType.CRA_0013);
		
		ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		calculator = 
		new SmartContractSalaryCalculator<Salary>( new SalaryBuilder());

		salary = calculator.calculate(ctx);
		
		salary.getSalaryPayments().forEach(p -> System.out.println(p.getName() + " = " + p.getAmount() ));
		

		assertEquals( (double) ( 1150.00 + 1.62 + 33.33 ) / 2, (double) salary.getTotalPayment(), DELTA);
		assertEquals( (double) ( 1150.00 + 1.62 + 33.33 ) / 2, (double) salary.getRemuneration(), DELTA);
		
		
		assertEquals( ( 1.62 + 33.33 ) / 2, (double) salary.getTotalDeduction(), DELTA);
		assertEquals( (double) ( 1150.00 ) / 2 , (double) salary.getTotalLiquid(), DELTA);

	}

	

}
