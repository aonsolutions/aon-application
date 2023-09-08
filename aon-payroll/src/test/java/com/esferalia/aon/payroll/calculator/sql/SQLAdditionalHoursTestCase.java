/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.salary.ISalaryBuilder;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

/**
 * @author rtrepiana
 *
 */
public class SQLAdditionalHoursTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	@Test
	public void testBaseMin()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), 
				new HashMap<String, String>() {
					{
						put(ContextVariable.PARTIAL_FACTOR.getName(), "0.50");
						put(ContextVariable.TC2.getName(), ContractCode.C200.getValue());
						put(ContextVariable.CGP_BASE_MIN.getName(), ContextVariable.CGC_BASE_MIN.getName());
						put(ContextVariable.CGC_BASE_MIN.getName(),"1166.70 * 0.50" );
						put("BASE_CGC_MIN_HORA","7.03" );
						
					}
				});
		
		
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"SALARIO_BASE", 
				"100.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001, 
				SalaryType.SALARY);

		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"HORAS_COMPL", 
				"4.03 * HORAS_COMPLEMENTARIAS", 
				"_P", 
				"_P", 
				PaymentType.CRA_0057, 
				SalaryType.SALARY);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		addData(aonContext, contract, startDate, endDate, ContextVariable.ADDITIONAL_HOURS, 10.00);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>() ;
		calculator.setSalaryBuilder(getSalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		for ( com.esferalia.aon.payroll.SalaryPayment payment: salary.getSalaryPayments())
			System.out.println(payment.getExpression() + "= " + payment.getAmount() + "," + payment.getQuote());
		
		Assert.assertEquals(1166.70 * 0.5 + 7.03 * 10.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1166.70 * 0.5 + 7.03 * 10.00, salary.getProfessionalBase(), DELTA);
		//Assert.assertEquals(100.00 * 0.5 + 4.03 * 10.00, salary.getRawCommonBase(), DELTA);
		
		String cgcBase = salary.getSalaryData(ContextVariable.CGC_BASE.getName());
		Assert.assertEquals(1166.70 * 0.5 + 7.03 * 10.00, Double.parseDouble(cgcBase), DELTA);
		String cgpBase = salary.getSalaryData(ContextVariable.CGP_BASE.getName());
		Assert.assertEquals(1166.70 * 0.5 + 7.03 * 10.00, Double.parseDouble(cgpBase), DELTA);
//		
//		
//		String cgcBaseEnterprise = salary.getSalaryData(ContextVariable.CGC_BASE_ENTERPRISE.getName());
//		Assert.assertEquals(1000.00, Double.parseDouble(cgcBaseEnterprise), DELTA);
//		String cgpBaseEnterprise = salary.getSalaryData(ContextVariable.CGP_BASE_ENTERPRISE.getName());
//		Assert.assertEquals(1100.00, Double.parseDouble(cgpBaseEnterprise), DELTA);

	}
	
	@Test
	public void testBaseOK()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), 
				new HashMap<String, String>() {
					{
						put(ContextVariable.PARTIAL_FACTOR.getName(), "0.50");
						put(ContextVariable.TC2.getName(), ContractCode.C200.getValue());
						put(ContextVariable.CGP_BASE_MIN.getName(), ContextVariable.CGC_BASE_MIN.getName());
						put(ContextVariable.CGC_BASE_MIN.getName(),"1166.70 * 0.50" );
						put("BASE_CGC_MIN_HORA","7.03" );
						
					}
				});
		
		
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"SALARIO_BASE", 
				"100.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001, 
				SalaryType.SALARY);

		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"HORAS_COMPL", 
				"10.03 * HORAS_COMPLEMENTARIAS", 
				"_P", 
				"_P", 
				PaymentType.CRA_0057, 
				SalaryType.SALARY);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		addData(aonContext, contract, startDate, endDate, ContextVariable.ADDITIONAL_HOURS, 10.00);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>() ;
		calculator.setSalaryBuilder(getSalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		for ( com.esferalia.aon.payroll.SalaryPayment payment: salary.getSalaryPayments())
			System.out.println(payment.getExpression() + "= " + payment.getAmount() + "," + payment.getQuote());
		
		Assert.assertEquals(1166.70 * 0.5 + 10.03 * 10.00, salary.getCommonBase(), DELTA);
		Assert.assertEquals(1166.70 * 0.5 + 10.03 * 10.00, salary.getProfessionalBase(), DELTA);
		//Assert.assertEquals(100.00 * 0.5 + 4.03 * 10.00, salary.getRawCommonBase(), DELTA);
		
		String cgcBase = salary.getSalaryData(ContextVariable.CGC_BASE.getName());
		salary.getSalaryDatas().stream().filter( e -> e.getName().equals(ContextVariable.CGC_BASE.getName())).forEach( e -> System.out.println(e.getName() + " = " + e.getExpression() + ", " + e.getStartDate() +"," + e.getEndDate()));
		Assert.assertEquals(1166.70 * 0.5 + 10.03 * 10.00, Double.parseDouble(cgcBase), DELTA);

		String cgpBase = salary.getSalaryData(ContextVariable.CGP_BASE.getName());
		salary.getSalaryDatas().stream().filter( e -> e.getName().equals(ContextVariable.CGP_BASE.getName())).forEach( e -> System.out.println(e.getName() + " = " + e.getExpression() + ", " + e.getStartDate() +"," + e.getEndDate()));
		Assert.assertEquals(1166.70 * 0.5 + 10.03 * 10.00, Double.parseDouble(cgpBase), DELTA);

	}

	@Test
	public void testBaseMinRound()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), 
				new HashMap<String, String>() {
					{
						put(ContextVariable.MONTH_DAYS.getName(), "30.00");
						put(ContextVariable.TC2.getName(), ContractCode.C200.getValue());
//						put(ContextVariable.CGP_BASE_MIN.getName(), ContextVariable.CGC_BASE_MIN.getName());
//						put(ContextVariable.CGC_BASE_MIN.getName(),"1166.70 * DIAS_TRABAJADOS / DIAS_MES" );
						put("BASE_CGC_MIN_HORA","7.03" );
						
					}
				});
		
		
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"SALARIO_BASE", 
				"10.00 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001, 
				SalaryType.SALARY);

		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"HORAS_COMPL", 
				"8.03 * HORAS_COMPLEMENTARIAS", 
				"_P", 
				"_P", 
				PaymentType.CRA_0057, 
				SalaryType.SALARY);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		

		addData(aonContext, contract, contract.getStartDate(), add(startDate, Calendar.DAY_OF_MONTH, 1),
			ContextVariable.CGC_BASE_MIN.getName(), "1166.70 * 2 * 0.5 / DIAS_MES");
		addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH, 2), null,
			ContextVariable.CGC_BASE_MIN.getName(), "1166.70 * 28 * 0.75 / DIAS_MES");

		addData(aonContext, contract, contract.getStartDate(), add(startDate, Calendar.DAY_OF_MONTH, 1),
			ContextVariable.CGP_BASE_MIN.getName(), "1166.70 * 2 * 0.5 / DIAS_MES");
		addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH, 2), null,
			ContextVariable.CGP_BASE_MIN.getName(), "1166.70 * 28 * 0.75 / DIAS_MES");

		addData(aonContext, contract, contract.getStartDate(), add(startDate, Calendar.DAY_OF_MONTH, 1),
			ContextVariable.PARTIAL_FACTOR.getName(), "0.50");
		addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH, 2), null,
			ContextVariable.PARTIAL_FACTOR.getName(), "0.75");

		addData(aonContext, contract, startDate, add(startDate, Calendar.DAY_OF_MONTH, 1),
			"HORAS_COMPLEMENTARIAS", "10.00");
		
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);
		
		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder(connection) {
		    @Override
		    public void addPayment(Double amount, Double quote, Double tax, String description,
		            java.util.Date startDate, java.util.Date endDate, IPayment payment, Map context) {
			System.out.println(description + "= " + amount + "," + quote + "[ " + startDate + ".." + endDate +"]");
			context.forEach((k,v) -> System.out.println( "\t" + k + " : " + ((ITimedVariable)v).getValue(((ITimedVariable)v).getPeriod())));
		        super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
		    }
		};
		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>() ;
		calculator.setSalaryBuilder(new RoundSalaryBuilder<>(jooqSalaryBuilder,d -> d.setScale(3, RoundingMode.HALF_UP)));
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		com.esferalia.aon.occam.api.model.Salary salary = AON.getSalaries(aonContext, p -> p.getContractProperty().eq(contract.getId())).findAny().get();
		
//		for ( com.esferalia.aon.occam.api.model.Salary.Payment payment: salary.getPayments())
//			System.out.println(payment.getExpression() + "= " + payment.getAmount() + "," + payment.getQuote());
		
		Assert.assertEquals(1166.70 * 2 * 0.5 / 30.00 + 1166.70 * 28 / 30.00 * 0.75 + 8.03 * 10.00, salary.getCommonContingenciesBase(), DELTA);
		Assert.assertEquals(1166.70 * 2 * 0.5 / 30.00 + 1166.70 * 28 / 30.00 * 0.75 + 8.03 * 10.00, salary.getProfessionalContingenciesBase(), DELTA);
		//Assert.assertEquals(100.00 * 0.5 + 4.03 * 10.00, salary.getRawCommonBase(), DELTA);
		
		Double cgcBase = salary.getContextData(ContextVariable.CGC_BASE.getName(), Collectors.summingDouble( Double::parseDouble ));
		Assert.assertEquals(1166.70 * 2 / 30.00 * 0.5 + 1166.70 * 28 / 30.00 * 0.75 + 8.03 * 10.00, cgcBase, DELTA);
		Double cgpBase = salary.getContextData(ContextVariable.CGP_BASE.getName(), Collectors.summingDouble( Double::parseDouble ));
		Assert.assertEquals(1166.70 * 2 / 30.00 * 0.5 + 1166.70 * 28 / 30.00 * 0.75 + 8.03 * 10.00, cgpBase, DELTA);
		Double additionalBase = salary.getContextData(ContextVariable.ADDITIONAL_BASE.getName(), Collectors.summingDouble( Double::parseDouble ));
		Assert.assertEquals(8.03 * 10.00, additionalBase, DELTA);

		salary.getContextData().get(ContextVariable.CGC_BASE.getName())
		.forEach(d -> System.out.println(d.getExpression() +" [ " + d.getStartDate() + ".." + d.getEndDate() +" ]"));

		salary.getContextData().get(ContextVariable.ADDITIONAL_BASE.getName())
		.forEach(d -> System.out.println(d.getExpression() +" [ " + d.getStartDate() + ".." + d.getEndDate() +" ]"));

		 List<ContextData> cgcBaseData = salary.getContextData(ContextVariable.CGC_BASE.getName(), startDate, add(startDate, Calendar.DAY_OF_MONTH, 1));
		 Assert.assertEquals(1166.70 * 2 / 30.00 * 0.5 + 8.03 * 10  , Double.parseDouble(cgcBaseData.get(0).getExpression()), DELTA);
		
		 List<ContextData> additionalBaseData = salary.getContextData(ContextVariable.ADDITIONAL_BASE.getName(), startDate, add(startDate, Calendar.DAY_OF_MONTH, 1));
		 Assert.assertEquals(8.03 * 10  , Double.parseDouble(additionalBaseData.get(0).getExpression()), DELTA);
	}
	
	@Test
	public void testNoFraccionateOK()
			throws ExpressionException, SQLException, SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext,
				getFirstDayOfMonth(getToday()), 
				new HashMap<String, String>() {
					{
						//put(ContextVariable.TC2.getName(), "\"100\"");
					    	//put(ContextVariable.CGP_BASE_MIN.getName(), ContextVariable.CGC_BASE_MIN.getName());
					    	//put(ContextVariable.CGC_BASE_MIN.getName(),"1166.70" );
					    	//put("BASE_CGC_MIN_HORA","7.03" );
						
					}
				});
		
		
		
		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"SALARIO_BASE", 
				"1066.70 * DIAS_TRABAJADOS / DIAS_MES", 
				"_P", 
				"_P", 
				PaymentType.CRA_0001, 
				SalaryType.SALARY);

		addPayment(aonContext, 
				contract, 
				contract.getStartDate(), 
				contract.getEndDate(), 
				"HORAS_COMPL", 
				"10.00 * HORAS_COMPLEMENTARIAS", 
				"_P", 
				"_P", 
				PaymentType.CRA_0057, 
				SalaryType.SALARY);
		
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		Date issueDate = endDate;
		
		addData(aonContext, contract, startDate ,add(startDate, Calendar.DAY_OF_MONTH, 5),  ContextVariable.QUOTE_GROUP.getName(), "\"05\"");
		addData(aonContext, contract, add(startDate, Calendar.DAY_OF_MONTH, 6),endDate,  ContextVariable.QUOTE_GROUP.getName(), "\"04\"");

		addData(aonContext, contract, startDate, startDate, ContextVariable.ADDITIONAL_HOURS, 5.00);
		addData(aonContext, contract, endDate, endDate, ContextVariable.ADDITIONAL_HOURS, 5.00);

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, issueDate, contract);

		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>() ;
		calculator.setSalaryBuilder(getSalaryBuilder());
		Salary salary = calculator.calculate(ctx);
		for ( com.esferalia.aon.payroll.SalaryPayment payment: salary.getSalaryPayments())
			System.out.println(payment.getDescription() + "= " + payment.getAmount() + "," + payment.getQuote());
		

	}

	protected ISalaryBuilder<Salary> getSalaryBuilder() {
		return new SalaryBuilder();
	}

}
