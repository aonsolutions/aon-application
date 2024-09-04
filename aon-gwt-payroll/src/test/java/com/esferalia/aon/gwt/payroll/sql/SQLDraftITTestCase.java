package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGC_BASE_MIN;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.CGP_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MATERNITY_BASE;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.PREST_IT;
import static com.esferalia.aon.watson.util.AonDateUtils.get;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.server.SalaryDraftCalculatorContext;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.NumberVariable;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Salary.ContextData;
import com.esferalia.aon.payroll.IrpfOutcome;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator4Dummies;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext.IListener;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.sql.SQLITTestCase;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.LeaveType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpressionVariable;
import com.esferalia.aon.salary.expression.ITimedVariable;
import com.esferalia.aon.salary.payment.IPayment;

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

	protected ISQLContractSalaryCalculatorContext getContractSalaryCalculatorContext(Connection connection,
			Date startDate, Date endDate, Date issueDate, ContractRecord contract,
			IContractSalaryCalculatorContext.IListener listener) throws ExpressionException, SQLException {

		Employee employee = new Employee();
		employee.setId(contract.getId());

		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(issueDate);
		
		return EmployeesServiceHelper.getSalaryCalculatorContext(connection,
				draft, listener);
		
		
	}

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
		
		int dayOfIt = (int ) (Math.floor(Math.random() * (25 - 2)) + 2);
		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH, dayOfIt);
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
				fail(name);
			}
		});
		ContractSalaryCalculator<Salary> calculator = new ContractSalaryCalculator<Salary>();
		calculator.setListener(new ContractSalaryCalculator.Listener(){
			@Override
			public void onCheckError(IContractPayment payment, String message) {
				fail(message);
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
				assertEquals(ExpressionScope.SALARY,((IExpressionVariable<?>) factor).getExpression().getScope());
			}
		});
		Salary salary = calculator.calculate(ctx);

		assertEquals(1750.00 * 1/2, salary.getTotalPayment(), DELTA);
		assertEquals(get(endDate, DAY_OF_MONTH) * 100.00 * 0.50 + 1750.00 * 1/2 , salary.getCommonBase(), DELTA);
		
		// Cret@ 
		ctx = EmployeesServiceHelper.getSalaryCalculatorContext(connection,
				draft, null);
		calculator = new ContractSalaryCalculator4Dummies<Salary>();

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
					assertEquals(1, datas.size());
					assertEquals(startDate, datas.get(0).getStartDate());
					assertEquals(endDate, datas.get(0).getEndDate());
					assertEquals(1750.00 * 0.5,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					// 635 o 634 Base de Accidentes de Trabajo.
					datas = s.getContextData().get(CGP_BASE.getName());
					assertEquals(1, datas.size());
					assertEquals(startDate, datas.get(0).getStartDate());
					assertEquals(endDate, datas.get(0).getEndDate());
					assertEquals(1750.00 * 0.5,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);

					datas = s.getContextData()
							.get(MATERNITY_BASE.getName());
					assertEquals(1, datas.size());
					assertEquals(startDate, datas.get(0).getStartDate());
					assertEquals(endDate, datas.get(0).getEndDate());
					assertEquals(100.00 * get(endDate, DAY_OF_MONTH) * 0.5,
							Double.parseDouble(datas.get(0).getExpression()), DELTA);
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
					assertEquals(2, datas.size());
					
					assertEquals(start, datas.get(0).getStartDate());
					assertEquals(add(startITDate, DAY_OF_MONTH, -1), datas.get(0).getEndDate());
					assertEquals(1750.00 * workDays / monthDays,
							Double.parseDouble(datas.get(0).getExpression()),DELTA);

					assertEquals(startITDate, datas.get(1).getStartDate());
					assertEquals(end, datas.get(1).getEndDate());
					assertEquals(1750.00 * itDays / monthDays * 0.5,
							Double.parseDouble(datas.get(1).getExpression()), DELTA);
					

					// 601 o 611 Base de Accidentes de Trabajo.
					datas = s.getContextData().get(CGP_BASE.getName());
					assertEquals(2, datas.size());
					assertEquals(2, datas.size());
					
					assertEquals(start, datas.get(0).getStartDate());
					assertEquals(add(startITDate, DAY_OF_MONTH, -1), datas.get(0).getEndDate());
					assertEquals(1750.00 * workDays / monthDays,
							Double.parseDouble(datas.get(0).getExpression()),DELTA);

					assertEquals(startITDate, datas.get(1).getStartDate());
					assertEquals(end, datas.get(1).getEndDate());
					assertEquals(1750.00 * itDays / monthDays * 0.5,
							Double.parseDouble(datas.get(1).getExpression()), DELTA);

					datas = s.getContextData()
							.get(MATERNITY_BASE.getName());
					assertEquals(1, datas.size());
					assertEquals(startITDate, datas.get(0).getStartDate());
					assertEquals(end, datas.get(0).getEndDate());
					assertEquals(100.00 * itDays * 0.5,
							Double.parseDouble(datas.get(0).getExpression()),DELTA);
				});
		;
	}

	@Test
	public void testCommonDiseaseIT365RedefinedII() throws ExpressionException, SQLException,
			SalaryException {
		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		cleanSystemPayments(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[] {
				"250.00 * DIAS_TRABAJADOS / DIAS_MES" ,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				}, 
				new String[] {
				}, null);
		//@formatter:on

		addPrestITs(aonContext, contract);

		Date startITDate = add(getFirstDayOfMonth(getToday()), DAY_OF_MONTH,10);
		addIT(aonContext, contract, LeaveType.COMMON_DISEASE, startITDate,
				startITDate, null);
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		Employee employee = new Employee();
		employee.setId(contract.getId());

		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(endDate);

		StringVariable directPayStartVariable = new StringVariable();
		directPayStartVariable.setName(ContextVariable.DIRECT_PAY_START.getName());
		directPayStartVariable.setScope(Scope.SALARY);
		directPayStartVariable.setStartDate(startITDate);
		directPayStartVariable.setEndDate(null);
		directPayStartVariable.setValue(String.format("FECHA(%d,%d,%d)", get(startITDate, YEAR),get(startITDate, MONTH)+1, get(startITDate, DAY_OF_MONTH) ));
		draft.addDraftVariable(directPayStartVariable);
		
		
		SalaryDraftCalculatorContext<SQLContractSalaryCalculatorContext> draftCtx = 
				EmployeesServiceHelper.getSalaryCalculatorContext(connection,draft, null);
		


		SmartContractSalaryCalculator<Salary> calculator = new SmartContractSalaryCalculator<Salary>();

		calculator.setSalaryBuilder(new SalaryBuilder());
		Salary salary = calculator.calculate(draftCtx);

		for (com.esferalia.aon.payroll.SalaryPayment payment : salary
				.getSalaryPayments()) {
			System.out.println(payment.getName() + " = " + payment.getAmount()
					+ " (" + payment.getExpression() + ")");
			if ( PREST_IT.equals(payment.getName() ))
					assertEquals("DIAS_ENFERMEDAD_COMUN_366 * 0.00", payment.getExpression());	
		}

		assertEquals(5, salary.getSalaryPayments().size());

	}
	
}
