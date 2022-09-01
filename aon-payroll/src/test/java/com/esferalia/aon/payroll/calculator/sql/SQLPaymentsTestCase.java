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
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;
import com.esferalia.aon.watson.util.AonDateUtils;

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

	

}
