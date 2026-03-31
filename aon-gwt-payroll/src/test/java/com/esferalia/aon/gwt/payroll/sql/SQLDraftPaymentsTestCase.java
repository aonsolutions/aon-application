package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.HOLIDAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.esferalia.aon.gwt.payroll.server.SalaryDraftBuilder;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

public class SQLDraftPaymentsTestCase extends AbstractSQLTestCase {

	protected static final double DELTA = 0.004;
	

	@Test
	public void testCompositeDescriptionDraft() throws ExpressionException, SQLException,
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
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		
		Employee employee = new Employee();
		employee.setId(contract.getId());

		SalaryDraft salaryDraft = new SalaryDraft();
		salaryDraft.setEmployee(employee);
		salaryDraft.setStartDate(startDate);
		salaryDraft.setEndDate(endDate);
		salaryDraft.setIssueDate(endDate);

		SalaryDraftBuilder draftSalaryBuilder = new SalaryDraftBuilder(salaryDraft) {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date start, java.util.Date end, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println(description + ": " + amount + "[" + start + "..." + end + "]");
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		};
		calculator.setSalaryBuilder(draftSalaryBuilder);
		calculator.calculate(ctx);
		
		salaryDraft.getPayments().forEach( p -> System.out.println(p.getDescription() +":" + p.getAmount()));
		salaryDraft.getPayments().forEach( p -> assertEquals("31 DÍAS DE IT", p.getDescription()) );
		

	}

	
	@Test
	public void testCompositeDescriptionDraftII() throws ExpressionException, SQLException,
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
		"VACACIONES @{DIAS_VACACIONES} DÍAS", 
		"/*read-only*/DIAS_VACACIONES * 66.66/**/", 
		"_P", 
		"_P", 
		PaymentType.CRA_0001);

		Date startDate = getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Date startHolidaysDate = add(startDate, Calendar.DAY_OF_MONTH, 5 );
		for ( int i = 0; i < 3 ; i++ ) {
			Date holidaysDate = add(startHolidaysDate, Calendar.DAY_OF_MONTH,i);
			addData(aonContext, contract, holidaysDate, holidaysDate, HOLIDAYS, "1");
		}
		
		startHolidaysDate = add(startDate, Calendar.DAY_OF_MONTH, 15 );
		for ( int i = 0; i < 5 ; i++ ) {
			Date holidaysDate = add(startHolidaysDate, Calendar.DAY_OF_MONTH,i);
			addData(aonContext, contract, holidaysDate, holidaysDate, HOLIDAYS, "1");
		}

		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(
				connection, startDate, endDate, endDate, contract);
		SmartContractSalaryCalculator<ISalary> calculator = new SmartContractSalaryCalculator<ISalary>();
		
		Employee employee = new Employee();
		employee.setId(contract.getId());

		SalaryDraft salaryDraft = new SalaryDraft();
		salaryDraft.setEmployee(employee);
		salaryDraft.setStartDate(startDate);
		salaryDraft.setEndDate(endDate);
		salaryDraft.setIssueDate(endDate);

		SalaryDraftBuilder draftSalaryBuilder = new SalaryDraftBuilder(salaryDraft) {
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date start, java.util.Date end, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				System.out.println(description + ": " + amount + "[" + start + "..." + end + "]");
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
			}
		};
		calculator.setSalaryBuilder(draftSalaryBuilder);
		calculator.calculate(ctx);
		
		salaryDraft.getPayments().forEach( p -> System.out.println(p.getDescription() +":" + p.getAmount()));
		salaryDraft.getPayments().forEach( p -> {
			if ( p.getExpression().contains("DIAS_VACACIONES"))
				assertEquals("VACACIONES 8 DÍAS", p.getDescription());
		} );
		

	}
	

}
