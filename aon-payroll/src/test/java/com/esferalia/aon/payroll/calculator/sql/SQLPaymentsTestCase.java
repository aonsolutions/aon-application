package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;

import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
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


	

}
