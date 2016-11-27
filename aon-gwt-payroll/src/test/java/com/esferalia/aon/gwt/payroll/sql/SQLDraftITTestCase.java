package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext.IListener;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLITTestCase;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

import junit.framework.Assert;

public class SQLDraftITTestCase extends SQLITTestCase {

	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(
			Connection connection, Date startDate, Date endDate,
			java.sql.Date issueDate, ContractRecord contract)
			throws ExpressionException, SQLException {

		Employee employee = new Employee();
		employee.setId(contract.getId());

		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(issueDate);

		return EmployeesServiceHelper.getSalaryCalculatorContext(connection,
				draft, null);
	};


	@Test
	public void testPaternityITII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		
		addSystemData(aonContext, getFirstDayOfYear(getToday()), null, 
				new HashMap<String,String>(){
			{
				put(CGC_BASE_MIN.getName(), "(1000.00 * (DIAS_NOMINA == DIAS_MES ? 1 : DIAS_NOMINA/30))");
			}
		});

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"}, 
				new String[] {						
				"BASE_CGC * 0.10", 
				"BASE_CGP * 0.05",
				"BASE_IRPF * PORCENTAJE_IRPF/100" 
				}, null);
		//@formatter:on
		
		PaymentConceptRecord maternity = addConcept(aonContext, ContextVariable.MATERNITY.getName());
		addPayment(aonContext, contract, maternity, "DIAS_PATERNIDAD * 0" , "DIAS_PATERNIDAD * BASE_REGULADORA");
		
		Date startITDate = getToday();
		addIT(aonContext, contract, LeaveType.PATERNITY, startITDate,
				null, 100.00);

		
		Date startDate = add(getFirstDayOfMonth(getToday()),MONTH,1);
		Date endDate = getLastDayOfMonth(startDate);
		
		Employee employee = new Employee();
		employee.setId(contract.getId());

		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(endDate);
		
		NumberVariable factorVariable = new NumberVariable();
		factorVariable.setName(ContextVariable.PATERNITY_FACTOR.getName());
		factorVariable.setValue(0.5);
		factorVariable.setScope(Scope.SALARY);
		factorVariable.setStartDate(startITDate);
		factorVariable.setEndDate(null);
		
		draft.addDraftVariable(factorVariable);

		ISQLContractSalaryCalculatorContext ctx = EmployeesServiceHelper.getSalaryCalculatorContext(connection,
				draft, null);
		ctx.setListener( new IListener() {
			
			@Override
			public void onIrpf(IrpfOutcome irpfOutcome) {
			}
			
			@Override
			public void onRedefinedImplicit(String name, ITimedVariable<?> redefined, ITimedVariable<?> implicit) {
				Assert.fail(name);
			}
		});
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setListener(new ContractSalaryCalculator.Listener(){
			@Override
			public void onCheckError(IContractPayment payment, String message) {
				Assert.fail(message);
				super.onCheckError(payment, message);
			}
			
			
			
		});
		calculator.setSalaryBuilder(new SalaryBuilder(){
			@Override
			public void addPayment(Double amount, Double quote, Double tax, String description,
					java.util.Date startDate, java.util.Date endDate, IPayment payment,
					Map<String, ITimedVariable<?>> context) {
				super.addPayment(amount, quote, tax, description, startDate, endDate, payment, context);
				ITimedVariable<?> factor = context.get(ContextVariable.PATERNITY_FACTOR.getName() );
				if ( factor == null )
					return;
				Assert.assertEquals(ExpressionScope.SALARY,((IExpressionVariable<?>) factor).getExpression().getScope());
			}
		});
		Salary salary = calculator.calculate(ctx);

		Assert.assertEquals(1750.00 * 1/2, salary.getTotalPayment());
		Assert.assertEquals(get(endDate, DAY_OF_MONTH) * 100.00 * 0.50 + 1750.00 * 1/2 , salary.getCommonBase());
		
		// Cret@ 
		ctx = EmployeesServiceHelper.getSalaryCalculatorContext(connection,
				draft, null);
		calculator = new ContractSalaryCalculator.ContractSalaryCalculator4Dummies<Salary>();

		JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId()))
				.forEach(s -> {

					// 535 Base de contingencias comunes.
					List<ContextData> datas = s.getContextData()
							.get(CGC_BASE.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					Assert.assertEquals(1750.00 * 0.5,
							Double.parseDouble(datas.get(0).getExpression()));

					// 635 o 634 Base de Accidentes de Trabajo.
					datas = s.getContextData().get(CGP_BASE.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					Assert.assertEquals(1750.00 * 0.5,
							Double.parseDouble(datas.get(0).getExpression()));

					datas = s.getContextData()
							.get(MATERNITY_BASE.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startDate, datas.get(0).getStartDate());
					Assert.assertEquals(endDate, datas.get(0).getEndDate());
					Assert.assertEquals(100.00 * get(endDate, DAY_OF_MONTH) * 0.5,
							Double.parseDouble(datas.get(0).getExpression()));
				});
		;

		draft.setStartDate(getFirstDayOfMonth(startITDate));
		draft.setEndDate(getLastDayOfMonth(startITDate));
		draft.setIssueDate(getLastDayOfMonth(startITDate));
		// Cret@ 
		ctx = EmployeesServiceHelper.getSalaryCalculatorContext(connection,
				draft, null);
		calculator = new ContractSalaryCalculator<Salary>();

		jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		AON.getSalaryData(aonContext,
				props -> props.getContractProperty().eq(contract.getId())
						.and(props.getStartDateProperty().eq(getFirstDayOfMonth(startITDate)))
						)
				.forEach(s -> {
					Date start  =getFirstDayOfMonth(startITDate);
					Date end  =getLastDayOfMonth(startITDate);
					int monthDays = get(end, DAY_OF_MONTH);
					int workDays = get(startITDate, DAY_OF_MONTH)-1;
					int itDays = monthDays - workDays;
					// 500 Base de contingencias comunes.
					List<ContextData> datas = s.getContextData()
							.get(CGC_BASE.getName());
					Assert.assertEquals(2, datas.size());
					
					Assert.assertEquals(start, datas.get(0).getStartDate());
					Assert.assertEquals(add(startITDate, DAY_OF_MONTH, -1), datas.get(0).getEndDate());
					Assert.assertEquals(1750.00 * workDays / monthDays,
							Double.parseDouble(datas.get(0).getExpression()),DELTA);

					Assert.assertEquals(startITDate, datas.get(1).getStartDate());
					Assert.assertEquals(end, datas.get(1).getEndDate());
					Assert.assertEquals(1750.00 * itDays / monthDays * 0.5,
							Double.parseDouble(datas.get(1).getExpression()));
					

					// 601 o 611 Base de Accidentes de Trabajo.
					datas = s.getContextData().get(CGP_BASE.getName());
					Assert.assertEquals(2, datas.size());
					Assert.assertEquals(2, datas.size());
					
					Assert.assertEquals(start, datas.get(0).getStartDate());
					Assert.assertEquals(add(startITDate, DAY_OF_MONTH, -1), datas.get(0).getEndDate());
					Assert.assertEquals(1750.00 * workDays / monthDays,
							Double.parseDouble(datas.get(0).getExpression()));

					Assert.assertEquals(startITDate, datas.get(1).getStartDate());
					Assert.assertEquals(end, datas.get(1).getEndDate());
					Assert.assertEquals(1750.00 * itDays / monthDays * 0.5,
							Double.parseDouble(datas.get(1).getExpression()));

					datas = s.getContextData()
							.get(MATERNITY_BASE.getName());
					Assert.assertEquals(1, datas.size());
					Assert.assertEquals(startITDate, datas.get(0).getStartDate());
					Assert.assertEquals(end, datas.get(0).getEndDate());
					Assert.assertEquals(100.00 * itDays * 0.5,
							Double.parseDouble(datas.get(0).getExpression()));
				});
		;
	}

}
