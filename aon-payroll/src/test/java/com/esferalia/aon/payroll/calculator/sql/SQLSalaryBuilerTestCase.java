package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.TC2;
import static com.esferalia.aon.payroll.enumeration.ContractCode.C100;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonNumberUtils;

import static org.junit.jupiter.api.Assertions.*;

public class SQLSalaryBuilerTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000001;

	// -------------------------------------------------------------------------
	@Test
	public void testSalaryBuilderI()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"1000.33333 * DIAS_TRABAJADOS / DIAS_MES" ,
				}, 
				new String[] {						
				"BASE_CGC * 4.60 / 100.00", 
				"BASE_CGC * 1.50 / 100.00", 
				"BASE_CGC * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF/100.00" 
				},
				null);
		//@formatter:on
		
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		int salaries = calculateAndSave(connection, ctx);

		// Only one salary saved to DB.
		assertEquals(1, salaries);
		
		AON.getSalaries(aonContext, 
		props->props.getContractProperty().eq(contract.getId()))
		.forEach(salary-> 
				{
					System.out.println("totalPayment : " + salary.getTotalPayment() );
					System.out.println("totalLiquid : " + salary.getTotalLiquid() );
					System.out.println("totalDeduction : " + salary.getTotalDeduction() );
					System.out.println("totalLiquid  + totalDeduction: " + ( salary.getTotalLiquid() + salary.getTotalDeduction()) );

					assertEquals(salary.getTotalPayment() , salary.getTotalLiquid() + salary.getTotalDeduction(), DELTA);

				}
		);

	}

	// -------------------------------------------------------------------------
	@Test
	public void testSalaryBuilderII()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, 
				getFirstDayOfYear(getToday()),
				new HashMap<String, String>() {
					{
						put(TC2.getName(), String.format("\"%s\"", C100.getValue()));
					}
				},
				new String[] {
				"1000.33333 * DIAS_TRABAJADOS / DIAS_MES" ,
				}, 
				new String[] {						
				"BASE_CGC * 4.60 / 100.00", 
				"BASE_CGC * 1.50 / 100.00", 
				"BASE_CGC * 0.10 / 100.00", 
				"BASE_IRPF * PORCENTAJE_IRPF/100.00" 
				},
				null);
		PaymentConceptRecord concept = addConcept(aonContext, "ESPECIE");
		addPayment(aonContext,
				contract, 
				concept,
				"0.33333", 
				"_P", 
				PaymentType.CRA_0019); 
		//@formatter:on
		
		

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);

		int salaries = calculateAndSave(connection, ctx);

		// Only one salary saved to DB.
		assertEquals(1, salaries);
		
		AON.getSalaries(aonContext, 
		props->props.getContractProperty().eq(contract.getId()))
		.forEach(salary-> 
				{

					System.out.println("irpfBase : " + salary.getIrpfBase() );
					System.out.println("moneyIrpfBase : " + salary.getMoneyIrpfBase() );
					System.out.println("inKindIrpfBase : " + salary.getInkindIrpfBase() );
					System.out.println("irpfBase  + moneyIrpfBase: " + ( BigDecimal.valueOf(salary.getMoneyIrpfBase()).add( BigDecimal.valueOf(salary.getInkindIrpfBase()))) );
					
					assertEquals(BigDecimal.valueOf(salary.getIrpfBase()) ,  BigDecimal.valueOf(salary.getMoneyIrpfBase()).add( BigDecimal.valueOf(salary.getInkindIrpfBase())) );
				}
		);

	}
	// -------------------------------------------------------------------------

	public static int calculateAndSave(Connection connection,
			ISQLContractSalaryCalculatorContext ctx) throws SalaryException {
		JooqSalaryBuilder<ISalary> jooqSalaryBuilder = new JooqSalaryBuilder<ISalary>(connection);
		RoundSalaryBuilder<ISalary> roundSalaryBuilder = new RoundSalaryBuilder<ISalary>(jooqSalaryBuilder,
				d -> d.setScale(2, RoundingMode.HALF_UP));
		
		new ContractSalaryCalculator<ISalary>(roundSalaryBuilder).calculate(ctx);
		return jooqSalaryBuilder.execute();
	}

}
