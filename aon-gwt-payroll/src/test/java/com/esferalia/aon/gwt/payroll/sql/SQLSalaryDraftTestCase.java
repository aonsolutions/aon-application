package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.MONTH_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.server.SalaryDraftBuilder;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.StringVariable;
import com.esferalia.aon.jooq.tables.records.BonusConceptRecord;
import com.esferalia.aon.jooq.tables.records.ContractBonusRecord;
import com.esferalia.aon.jooq.tables.records.ContractEmbargoRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.SalaryEmbargoRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.IContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.calculator.RoundSalaryBuilder;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import java.util.Calendar;

import junit.framework.Assert;

public class SQLSalaryDraftTestCase extends AbstractSQLTestCase {

	@Test
	public void testMonthDays() throws SQLException, ExpressionException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		addSystemData(
		aonContext, 
		AonDateUtils.getFirstDayOfYear(getToday()), 
		null, 
		Collections.singletonMap(MONTH_DAYS.getName(), "[ \"01\":30, \"02\":30, \"03\":30, \"04\":30, \"05\":30, \"06\":30, \"07\":30, \"08\": DIAS_NATURALES_MES, \"09\": DIAS_NATURALES_MES, \"10\": DIAS_NATURALES_MES, \"11\": DIAS_NATURALES_MES][GRUPO_COTIZACION]"));
		
		
		ContractRecord contract = newContract(aonContext, 
				AonDateUtils.getFirstDayOfYear(getToday())
				,new HashMap<String, String>(){
					{
						put(ContextVariable.QUOTE_GROUP.getName(),"\"10\"");
					}
				}
				,new String [] {
						"DIAS_MES",
//						"TRACE('GRUPO_COTIZACION =  %s  \r\n', GRUPO_COTIZACION ); 0.00",
//						"TRACE('M=%s\r\n',[\"10\":DIAS_NATURALES_MES][\"10\"]); 0.00",
//						"1000.00 * DIAS_TRABAJADOS / DIAS_MES"
				}
				,new String [] {}
				, null
				);
		
		Employee employee = new Employee();
		employee.setId(contract.getId());
		
		Date startDate = AonDateUtils.getFirstDayOfYear(getToday());
		Date endDate = getLastDayOfMonth(startDate);
		
		SalaryDraft salaryDraft = new SalaryDraft()
		.setEmployee(employee)
		.setStartDate(startDate)
		.setEndDate(endDate)
		.setIssueDate(endDate);
		
		SalaryDraftBuilder salaryDraftBuilder = new SalaryDraftBuilder(salaryDraft);
		RoundSalaryBuilder<ISalary> roundSalaryBuilder = new RoundSalaryBuilder<ISalary>(salaryDraftBuilder,
				EmployeesServiceHelper.round(2));
		try {
			EmployeesServiceHelper.calculate(connection, salaryDraft, roundSalaryBuilder, salaryDraftBuilder, new SmartContractSalaryCalculator<ISalary>() {
				@Override
				protected void fillData(IContractSalaryCalculatorContext ctx) throws SalaryException {
					super.fillData(ctx);
					fillData(ctx, new ContextVariable[] { ContextVariable.SENIORITY });
					ctx.getExpressionContext().getVariables("DIAS_MES").forEach(v -> System.out.println("DIAS_MES:" + v.getValue(v.getPeriod()) ));
				}
				

			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		salaryDraft.getContext()
		.stream()
		//.peek( v -> System.out.println(v.getName() + " = " + v.getValue()))
		.filter(v -> AonStringUtils.equals(MONTH_DAYS.getName(), v.getName()))
		//.peek( v -> System.out.println("*" + v.getName() + " = " + v.getValue()))
		.map ( v -> AonNumberUtils.todouble(v.getValue()))
		.forEach( monthDays -> assertEquals(31.00, monthDays, 0.00) );
		;

		StringVariable monthDaysVar = new StringVariable();
		monthDaysVar.setImplicit(true);
		monthDaysVar.setStartDate(startDate);
		monthDaysVar.setEndDate(endDate);
		monthDaysVar.setExpression("30.00");
		monthDaysVar.setScope(Scope.SALARY);
		monthDaysVar.setName(MONTH_DAYS.getName());
		salaryDraft.addDraftVariable(monthDaysVar);
		

		salaryDraftBuilder = new SalaryDraftBuilder(salaryDraft);
		roundSalaryBuilder = new RoundSalaryBuilder<ISalary>(salaryDraftBuilder,
				EmployeesServiceHelper.round(2));
		try {
			EmployeesServiceHelper.calculate(connection, salaryDraft, roundSalaryBuilder, salaryDraftBuilder, new SmartContractSalaryCalculator<ISalary>() {
				@Override
				protected void fillData(IContractSalaryCalculatorContext ctx) throws SalaryException {
					super.fillData(ctx);
					fillData(ctx, new ContextVariable[] { ContextVariable.SENIORITY });
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		salaryDraft.getDraftContext()
		.stream()
		.filter(v -> AonStringUtils.equals(MONTH_DAYS.getName(), v.getName()))
		.findAny()
		.ifPresentOrElse(monthDays -> assertEquals("30.00", monthDays.getExpression()), org.junit.Assert::fail);
		;

		salaryDraft.getContext()
		.stream()
		.filter(v -> AonStringUtils.equals(MONTH_DAYS.getName(), v.getName()))
		.peek( v -> System.out.println("*" + v.getName() + " = " + v.getValue()))
		.map ( v -> AonNumberUtils.todouble(v.getValue()))
		.forEach( monthDays -> assertEquals(30.00, monthDays, 0.00) );
		;
		
	}
	

	@Test
	public void testSaveI() throws SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		ContractRecord contract = newContract(aonContext, getToday(),
				Collections.emptyMap());
		
		Employee employee = new Employee();
		employee.setId(contract.getId());
		
		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(getToday());
		
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(endDate);
		
		
		BonusConceptRecord concept = addBonusConcept(aonContext, null, "666.00");
//		addBonus(aonContext, contract, concept, "666.00");
		
		Bonus draftBonus = new Bonus();
		draftBonus.setConceptId(concept.getId());
		draftBonus.setExpression("666.00");
		draftBonus.setDescriptionTemplate("666.00");
		draftBonus.setStartDate(startDate);
		draftBonus.setEndDate(null);
		draft.addDraftBonus(draftBonus);
		
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		List<ContractBonusRecord>  bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(1, bonuses.size());
		Assert.assertEquals(CONTRACT_BONUS.DOMAIN.getName(), contract.getDomain(), bonuses.get(0).getDomain());
		Assert.assertEquals(CONTRACT_BONUS.BONUS_CONCEPT.getName(), concept.getId(), bonuses.get(0).getBonusConcept());
		Assert.assertEquals(CONTRACT_BONUS.START_DATE.getName(), startDate, bonuses.get(0).getStartDate());
		Assert.assertEquals(CONTRACT_BONUS.END_DATE.getName(), null, bonuses.get(0).getEndDate());
		Assert.assertEquals(CONTRACT_BONUS.DESCRIPTION.getName(), "666.00", bonuses.get(0).getDescription());
		Assert.assertEquals(CONTRACT_BONUS.EXPRESSION.getName(), null, bonuses.get(0).getExpression());
		
		draftBonus.setId(bonuses.get(0).getId());
		draftBonus.setDescriptionTemplate("Hello World!!!!");		
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(1, bonuses.size());
		Assert.assertEquals(CONTRACT_BONUS.DOMAIN.getName(), contract.getDomain(), bonuses.get(0).getDomain());
		Assert.assertEquals(CONTRACT_BONUS.BONUS_CONCEPT.getName(), concept.getId(), bonuses.get(0).getBonusConcept());
		Assert.assertEquals(CONTRACT_BONUS.START_DATE.getName(), startDate, bonuses.get(0).getStartDate());
		Assert.assertEquals(CONTRACT_BONUS.END_DATE.getName(), null, bonuses.get(0).getEndDate());
		Assert.assertEquals(CONTRACT_BONUS.DESCRIPTION.getName(), "Hello World!!!!", bonuses.get(0).getDescription());
		Assert.assertEquals(CONTRACT_BONUS.EXPRESSION.getName(), null, bonuses.get(0).getExpression());
		
		draftBonus.setId(bonuses.get(0).getId());
		draftBonus.setExpression("999");
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(1, bonuses.size());
		Assert.assertEquals(CONTRACT_BONUS.DOMAIN.getName(), contract.getDomain(), bonuses.get(0).getDomain());
		Assert.assertEquals(CONTRACT_BONUS.BONUS_CONCEPT.getName(), concept.getId(), bonuses.get(0).getBonusConcept());
		Assert.assertEquals(CONTRACT_BONUS.START_DATE.getName(), startDate, bonuses.get(0).getStartDate());
		Assert.assertEquals(CONTRACT_BONUS.END_DATE.getName(), null, bonuses.get(0).getEndDate());
		Assert.assertEquals(CONTRACT_BONUS.DESCRIPTION.getName(), "Hello World!!!!", bonuses.get(0).getDescription());
		Assert.assertEquals(CONTRACT_BONUS.EXPRESSION.getName(), "999", bonuses.get(0).getExpression());
		
		draftBonus.setId(bonuses.get(0).getId());
		draftBonus.setExpression("666.00");
		draftBonus.setDescriptionTemplate("666.00");
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(1, bonuses.size());
		Assert.assertEquals(CONTRACT_BONUS.DOMAIN.getName(), contract.getDomain(), bonuses.get(0).getDomain());
		Assert.assertEquals(CONTRACT_BONUS.BONUS_CONCEPT.getName(), concept.getId(), bonuses.get(0).getBonusConcept());
		Assert.assertEquals(CONTRACT_BONUS.START_DATE.getName(), startDate, bonuses.get(0).getStartDate());
		Assert.assertEquals(CONTRACT_BONUS.END_DATE.getName(), null, bonuses.get(0).getEndDate());
		Assert.assertEquals(CONTRACT_BONUS.DESCRIPTION.getName(),"666.00", bonuses.get(0).getDescription());
		Assert.assertEquals(CONTRACT_BONUS.EXPRESSION.getName(), null, bonuses.get(0).getExpression());
		
		draftBonus.setId(bonuses.get(0).getId());
		draftBonus.setExpression("REMOVE()");
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(0, bonuses.size());
	
		
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(0, bonuses.size());
		
		draftBonus.setExpression(null);
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(1, bonuses.size());
		Assert.assertEquals(CONTRACT_BONUS.DOMAIN.getName(), contract.getDomain(), bonuses.get(0).getDomain());
		Assert.assertEquals(CONTRACT_BONUS.BONUS_CONCEPT.getName(), concept.getId(), bonuses.get(0).getBonusConcept());
		Assert.assertEquals(CONTRACT_BONUS.START_DATE.getName(), startDate, bonuses.get(0).getStartDate());
		Assert.assertEquals(CONTRACT_BONUS.END_DATE.getName(), null, bonuses.get(0).getEndDate());
		Assert.assertEquals(CONTRACT_BONUS.DESCRIPTION.getName(),"666.00", bonuses.get(0).getDescription());
		Assert.assertEquals(CONTRACT_BONUS.EXPRESSION.getName(), null, bonuses.get(0).getExpression());
	
		draftBonus.setId(bonuses.get(0).getId());
		draftBonus.setExpression("555.00");
		draftBonus.setEndDate(getLastDayOfMonth(getToday()));
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(2, bonuses.size());
		Assert.assertEquals(CONTRACT_BONUS.DOMAIN.getName(), contract.getDomain(), bonuses.get(0).getDomain());
		Assert.assertEquals(CONTRACT_BONUS.BONUS_CONCEPT.getName(), concept.getId(), bonuses.get(0).getBonusConcept());
		Assert.assertEquals(CONTRACT_BONUS.START_DATE.getName(), startDate, bonuses.get(0).getStartDate());
		Assert.assertEquals(CONTRACT_BONUS.END_DATE.getName(), getLastDayOfMonth(getToday()), bonuses.get(0).getEndDate());
		Assert.assertEquals(CONTRACT_BONUS.DESCRIPTION.getName(),"666.00", bonuses.get(0).getDescription());
		Assert.assertEquals(CONTRACT_BONUS.EXPRESSION.getName(), "555.00", bonuses.get(0).getExpression());

		Assert.assertEquals(CONTRACT_BONUS.DOMAIN.getName(), contract.getDomain(), bonuses.get(1).getDomain());
		Assert.assertEquals(CONTRACT_BONUS.BONUS_CONCEPT.getName(), concept.getId(), bonuses.get(1).getBonusConcept());
		Assert.assertEquals(CONTRACT_BONUS.START_DATE.getName(), add(getLastDayOfMonth(getToday()), Calendar.DAY_OF_MONTH, 1), bonuses.get(1).getStartDate());
		Assert.assertEquals(CONTRACT_BONUS.END_DATE.getName(), null, bonuses.get(1).getEndDate());
		Assert.assertEquals(CONTRACT_BONUS.DESCRIPTION.getName(),"666.00", bonuses.get(0).getDescription());
		Assert.assertEquals(CONTRACT_BONUS.EXPRESSION.getName(), null, bonuses.get(1).getExpression());
		
	}
	
	
	@Test
	public void testUpdateEmbargoI() throws SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		java.sql.Date startYear = AonDateUtils.getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(aonContext, add(startYear, Calendar.YEAR, -1),
				Collections.emptyMap());
		
		Employee employee = new Employee();
		employee.setId(contract.getId());
		
		java.sql.Date startMonth = getFirstDayOfMonth(getToday());
		java.sql.Date endMonth = getLastDayOfMonth(getToday());
		
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startMonth);
		//draft.setEndDate(null); From this month
		draft.setIssueDate(endMonth);
		
		Deduction draftEmbargo = new Deduction();
		//draftEmbargo.setConceptId(); No concept
		draftEmbargo.setExpression("666.00");
		draftEmbargo.setDescriptionTemplate("EMBARGO 666.00");
		draftEmbargo.setStartDate(startMonth);
		draftEmbargo.setEndDate(null);
		draft.addDraftEmbargo(draftEmbargo);
		
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		
		List<ContractEmbargoRecord> embargos; 
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(1, embargos.size());
		ContractEmbargoRecord embargo = embargos.get(0); 
		Assert.assertEquals(embargo.getExpression(), draftEmbargo.getExpression());
		Assert.assertEquals(embargo.getDescription(), draftEmbargo.getDescriptionTemplate());
		Assert.assertEquals(embargo.getStartDate(), draftEmbargo.getStartDate());
		Assert.assertEquals(embargo.getEndDate(), draftEmbargo.getEndDate());
		
		// change expression & description...easy
		draftEmbargo.setId(embargo.getId());
		draftEmbargo.setExpression("999.00");
		draftEmbargo.setDescriptionTemplate("EMBARG0 999.00");

		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(1, embargos.size());
		embargo = embargos.get(0); 
		Assert.assertEquals(embargo.getExpression(), draftEmbargo.getExpression());
		Assert.assertEquals(embargo.getDescription(), draftEmbargo.getDescriptionTemplate());
		Assert.assertEquals(embargo.getStartDate(), draftEmbargo.getStartDate());
		Assert.assertEquals(embargo.getEndDate(), draftEmbargo.getEndDate());
		
		
		// change start date... previous month
		draftEmbargo.setId(embargo.getId());
		draftEmbargo.setStartDate(add(startMonth, Calendar.MONTH, -1));

		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(1, embargos.size());
		embargo = embargos.get(0); 
		Assert.assertEquals(embargo.getExpression(), draftEmbargo.getExpression());
		Assert.assertEquals(embargo.getDescription(), draftEmbargo.getDescriptionTemplate());
		Assert.assertEquals(embargo.getStartDate(), draftEmbargo.getStartDate());
		Assert.assertEquals(embargo.getEndDate(), draftEmbargo.getEndDate());
		
		// change end date & expression... 
		draftEmbargo.setId(embargo.getId());
		draftEmbargo.setExpression("888.00");
		draftEmbargo.setEndDate(add(startMonth, Calendar.MONTH, 10));

		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(2, embargos.size());
		embargo = embargos.get(0); 
		Assert.assertEquals(embargo.getExpression(), draftEmbargo.getExpression());
		Assert.assertEquals(embargo.getDescription(), draftEmbargo.getDescriptionTemplate());
		Assert.assertEquals(embargo.getStartDate(), draftEmbargo.getStartDate());
		Assert.assertEquals(embargo.getEndDate(), draftEmbargo.getEndDate());
	
		embargo = embargos.get(1); 
		Assert.assertEquals(embargo.getExpression(), "999.00");
		Assert.assertEquals(embargo.getDescription(), draftEmbargo.getDescriptionTemplate());
		Assert.assertEquals(embargo.getStartDate(), add(add(startMonth, Calendar.MONTH, 10),Calendar.DAY_OF_MONTH,1));
		Assert.assertEquals(embargo.getEndDate(), null);
		
		// Remove
		draftEmbargo.setId(embargos.get(0).getId());
		draftEmbargo.setExpression("REMOVE()");
		
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(1, embargos.size());
		embargo = embargos.get(0); 
		Assert.assertEquals(embargo.getExpression(), "999.00");
		Assert.assertEquals(embargo.getDescription(), draftEmbargo.getDescriptionTemplate());
		Assert.assertEquals(embargo.getStartDate(), add(add(startMonth, Calendar.MONTH, 10),Calendar.DAY_OF_MONTH,1));
		Assert.assertEquals(embargo.getEndDate(), null);
		
		// Remove
		draftEmbargo.setId(embargos.get(0).getId());
		draftEmbargo.setExpression("REMOVE()");
		draftEmbargo.setEndDate(null);
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(0, embargos.size());
		
	}

	@Test
	public void testUpdateEmbargoII() throws SQLException, ExpressionException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		java.sql.Date startYear = AonDateUtils.getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(aonContext, add(startYear, Calendar.YEAR, -1),
				Collections.emptyMap());
		
		Employee employee = new Employee();
		employee.setId(contract.getId());
		
		java.sql.Date startMonth = getFirstDayOfMonth(getToday());
		java.sql.Date endMonth = getLastDayOfMonth(getToday());
		
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startMonth);
		//draft.setEndDate(null); From this month
		draft.setIssueDate(endMonth);
		
		Deduction draftEmbargo = new Deduction();
		//draftEmbargo.setConceptId(); No concept
		draftEmbargo.setExpression("666.00");
		draftEmbargo.setDescriptionTemplate("EMBARGO 666.00");
		draftEmbargo.setStartDate(startMonth);
		draftEmbargo.setEndDate(null);
		draft.addDraftEmbargo(draftEmbargo);

		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		
		List<ContractEmbargoRecord> embargos; 
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(1, embargos.size());
		ContractEmbargoRecord embargo = embargos.get(0); 
		Assert.assertEquals(embargo.getExpression(), draftEmbargo.getExpression());
		Assert.assertEquals(embargo.getDescription(), draftEmbargo.getDescriptionTemplate());
		Assert.assertEquals(embargo.getStartDate(), draftEmbargo.getStartDate());
		Assert.assertEquals(embargo.getEndDate(), draftEmbargo.getEndDate());
		
		// Save a Salary
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startMonth, endMonth, endMonth, contract);
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		// Remove
		draftEmbargo.setId(embargos.get(0).getId());
		draftEmbargo.setExpression("REMOVE()");
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(0, embargos.size());

		
	}

	@Test
	public void testUpdateEmbargoIII() throws SQLException, ExpressionException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		java.sql.Date startYear = AonDateUtils.getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(aonContext, add(startYear, Calendar.YEAR, -1),
				Collections.emptyMap());
		
		Employee employee = new Employee();
		employee.setId(contract.getId());
		
		java.sql.Date startMonth = getFirstDayOfMonth(getToday());
		java.sql.Date endMonth = getLastDayOfMonth(getToday());
		
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startMonth);
		//draft.setEndDate(null); From this month
		draft.setIssueDate(endMonth);
		
		Deduction draftEmbargo = new Deduction();
		//draftEmbargo.setConceptId(); No concept
		draftEmbargo.setExpression("666.00");
		draftEmbargo.setDescriptionTemplate("EMBARGO 666.00");
		draftEmbargo.setStartDate(startMonth);
		draftEmbargo.setEndDate(null);
		draft.addDraftEmbargo(draftEmbargo);

		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		
		List<ContractEmbargoRecord> embargos; 
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(1, embargos.size());
		ContractEmbargoRecord embargo = embargos.get(0); 
		Assert.assertEquals(embargo.getExpression(), draftEmbargo.getExpression());
		Assert.assertEquals(embargo.getDescription(), draftEmbargo.getDescriptionTemplate());
		Assert.assertEquals(embargo.getStartDate(), draftEmbargo.getStartDate());
		Assert.assertEquals(embargo.getEndDate(), draftEmbargo.getEndDate());
		
		// Save a Salary
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startMonth, endMonth, endMonth, contract);
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		// Remove
		draftEmbargo.setId(embargos.get(0).getId());
		draftEmbargo.setExpression("777.00");
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(1, embargos.size());
		
		List<SalaryEmbargoRecord> salaryEmbargos = getSalaryEmbargo(aonContext, contract);
		Assert.assertEquals(1, salaryEmbargos.size());
		Assert.assertEquals(-666, (int)salaryEmbargos.get(0).getContractEmbargo());
		

		
	}

	
	@Test
	public void testUpdateEmbargoIV() throws SQLException, ExpressionException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		java.sql.Date startYear = AonDateUtils.getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(aonContext, add(startYear, Calendar.YEAR, -1),
				Collections.emptyMap());
		
		Employee employee = new Employee();
		employee.setId(contract.getId());
		
		java.sql.Date startMonth = getFirstDayOfMonth(getToday());
		java.sql.Date endMonth = getLastDayOfMonth(getToday());
		
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startMonth);
		//draft.setEndDate(null); From this month
		draft.setIssueDate(endMonth);
		
		Deduction draftEmbargo = new Deduction();
		//draftEmbargo.setConceptId(); No concept
		draftEmbargo.setExpression("666.00");
		draftEmbargo.setDescriptionTemplate("EMBARGO 666.00");
		draftEmbargo.setStartDate(startMonth);
		draftEmbargo.setEndDate(null);
		draft.addDraftEmbargo(draftEmbargo);

		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		
		List<ContractEmbargoRecord> embargos; 
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(1, embargos.size());
		ContractEmbargoRecord embargo = embargos.get(0); 
		Assert.assertEquals(embargo.getExpression(), draftEmbargo.getExpression());
		Assert.assertEquals(embargo.getDescription(), draftEmbargo.getDescriptionTemplate());
		Assert.assertEquals(embargo.getStartDate(), draftEmbargo.getStartDate());
		Assert.assertEquals(embargo.getEndDate(), draftEmbargo.getEndDate());
		
		// Save a Salaries
		ISQLContractSalaryCalculatorContext ctx = getContractSalaryCalculatorContext(connection, startMonth, endMonth, endMonth, contract);
		JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
		ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();
		calculator.setSalaryBuilder(jooqSalaryBuilder);
		calculator.calculate(ctx);
		jooqSalaryBuilder.execute();
		
		// Change from next month
		draftEmbargo.setId(embargos.get(0).getId());
		draftEmbargo.setExpression("777.00");
		draftEmbargo.setStartDate(add(startMonth, Calendar.MONTH,1));
		
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(2, embargos.size());
		
		List<SalaryEmbargoRecord> salaryEmbargos = getSalaryEmbargo(aonContext, contract);
		Assert.assertEquals(1, salaryEmbargos.size());
		Assert.assertEquals(embargos.get(0).getId(), salaryEmbargos.get(0).getContractEmbargo());
		

		
	}

	
	@Test
	public void testUpdateEmbargoV() throws SQLException, ExpressionException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		java.sql.Date startYear = AonDateUtils.getFirstDayOfYear(getToday());
		java.sql.Date startContract = add(startYear, Calendar.YEAR, -1);
		
		ContractRecord contract = newContract(aonContext, startContract,
				Collections.emptyMap());
		
		Employee employee = new Employee();
		employee.setId(contract.getId());
		
		java.sql.Date startMonth = getFirstDayOfMonth(getToday());
		java.sql.Date endMonth = getLastDayOfMonth(getToday());
		
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startMonth);
		//draft.setEndDate(null); From this month
		draft.setIssueDate(endMonth);
		
		Deduction draftEmbargo = new Deduction();
		//draftEmbargo.setConceptId(); No concept
		draftEmbargo.setExpression("666.00");
		draftEmbargo.setDescriptionTemplate("EMBARGO 666.00");
		draftEmbargo.setStartDate(startMonth);
		draftEmbargo.setEndDate(null);
		draft.addDraftEmbargo(draftEmbargo);

		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		
		List<ContractEmbargoRecord> embargos; 
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(1, embargos.size());
		ContractEmbargoRecord embargo = embargos.get(0); 
		Assert.assertEquals(embargo.getExpression(), draftEmbargo.getExpression());
		Assert.assertEquals(embargo.getDescription(), draftEmbargo.getDescriptionTemplate());
		Assert.assertEquals(embargo.getStartDate(), draftEmbargo.getStartDate());
		Assert.assertEquals(embargo.getEndDate(), draftEmbargo.getEndDate());
		
		// Save a Salaries
		for ( int i = 0; i < (12*4); i++) {
			java.sql.Date startDate = add( startContract, Calendar.MONTH, i);
			java.sql.Date endDate = getLastDayOfMonth(startDate);
			ISQLContractSalaryCalculatorContext ctx = 
					getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract);
			JooqSalaryBuilder jooqSalaryBuilder = new JooqSalaryBuilder(connection);
			ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>();
			calculator.setSalaryBuilder(jooqSalaryBuilder);
			calculator.calculate(ctx);
			jooqSalaryBuilder.execute();
		}
		
		// Change from next month
		draftEmbargo.setId(embargos.get(0).getId());
		draftEmbargo.setExpression("777.00");
		draftEmbargo.setStartDate(add(startMonth, Calendar.MONTH, 12));
		
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(2, embargos.size());
		
		Assert.assertEquals(embargos.get(0).getExpression(), "666.00");
		Assert.assertEquals(embargos.get(0).getStartDate(), startMonth);
		Assert.assertEquals(embargos.get(0).getEndDate(), add(add(startMonth, Calendar.MONTH, 12), Calendar.DAY_OF_MONTH,-1));
		
		Assert.assertEquals(embargos.get(1).getExpression(), draftEmbargo.getExpression());
		Assert.assertEquals(embargos.get(1).getStartDate(), add(startMonth, Calendar.MONTH, 12));
		Assert.assertEquals(embargos.get(1).getEndDate(), null);

		List<SalaryEmbargoRecord> salaryEmbargos = getSalaryEmbargo(aonContext, contract);
		for ( int i = 0 ; i < 12; i++)
			Assert.assertEquals(embargos.get(0).getId(), salaryEmbargos.get(i).getContractEmbargo());
		for ( int i = 12 ; i < embargos.size(); i++)
			Assert.assertEquals(-666, (int) salaryEmbargos.get(i).getContractEmbargo());
		
		draftEmbargo.setId(embargos.get(0).getId());
		draftEmbargo.setExpression("333.00");
		draftEmbargo.setStartDate(add(startMonth, Calendar.MONTH, 6));
		draftEmbargo.setEndDate(getLastDayOfMonth(add(startMonth, Calendar.MONTH, 7)));

		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(4, embargos.size());
		salaryEmbargos = getSalaryEmbargo(aonContext, contract);

		for ( int i = 0 ; i < 6; i++)
			Assert.assertEquals(embargos.get(0).getId(), salaryEmbargos.get(i).getContractEmbargo());
		for ( int i = 6 ; i < 8; i++)
			Assert.assertEquals(-666, (int)salaryEmbargos.get(i).getContractEmbargo());
		for ( int i = 8 ; i < 12; i++)
			Assert.assertEquals(embargos.get(2).getId(), salaryEmbargos.get(i).getContractEmbargo());
		for ( int i = 12 ; i < embargos.size(); i++)
			Assert.assertEquals(-666, (int) salaryEmbargos.get(i).getContractEmbargo());
		
		draftEmbargo.setId(embargos.get(1).getId());
		draftEmbargo.setExpression("REMOVE()");
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		
		embargos = getEmbargo(aonContext, contract);
		Assert.assertEquals(3, embargos.size());
		salaryEmbargos = getSalaryEmbargo(aonContext, contract);
		for ( int i = 0 ; i < 6; i++)
			Assert.assertEquals(embargos.get(0).getId(), salaryEmbargos.get(i).getContractEmbargo());
		for ( int i = 6 ; i < 8; i++)
			Assert.assertEquals(-666, (int)salaryEmbargos.get(i).getContractEmbargo());
		for ( int i = 8 ; i < 12; i++)
			Assert.assertEquals(embargos.get(1).getId(), salaryEmbargos.get(i).getContractEmbargo());
		for ( int i = 12 ; i < embargos.size(); i++)
			Assert.assertEquals(-666, (int) salaryEmbargos.get(i).getContractEmbargo());

	}

	
	// ------------------------------------------------------------------------
	@Test
	public void testUpdateBonusI() throws SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		java.sql.Date startYear = AonDateUtils.getFirstDayOfYear(getToday());
		
		ContractRecord contract = newContract(aonContext, add(startYear, Calendar.YEAR, -1),
				Collections.emptyMap());
		
		Employee employee = new Employee();
		employee.setId(contract.getId());
		
		java.sql.Date startMonth = getFirstDayOfMonth(getToday());
		java.sql.Date endMonth = getLastDayOfMonth(getToday());
		
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startMonth);
		//draft.setEndDate(null); From this month
		draft.setIssueDate(endMonth);
		
		BonusConceptRecord concept = addBonusConcept(aonContext, null, "666.00");
		
		Bonus draftBonus = new Bonus();
		draftBonus.setConceptId(concept.getId());
		//draftEmbargo.setConceptId(); No concept
		draftBonus.setExpression("666.00");
		draftBonus.setDescriptionTemplate("BONIF 666.00");
		draftBonus.setStartDate(startMonth);
		draftBonus.setEndDate(null);
		draft.addDraftBonus(draftBonus);
		
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		
		List<ContractBonusRecord> bonuses; 
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(1, bonuses.size());
		ContractBonusRecord bonus = bonuses.get(0); 
		Assert.assertEquals(bonus.getExpression(), draftBonus.getExpression());
		Assert.assertEquals(bonus.getDescription(), draftBonus.getDescriptionTemplate());
		Assert.assertEquals(bonus.getStartDate(), draftBonus.getStartDate());
		Assert.assertEquals(bonus.getEndDate(), draftBonus.getEndDate());
		
		// change expression & description...easy
		draftBonus.setId(bonus.getId());
		draftBonus.setExpression("999.00");
		draftBonus.setDescriptionTemplate("BONIF 999.00");

		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(1, bonuses.size());
		bonus = bonuses.get(0); 
		Assert.assertEquals(bonus.getExpression(), draftBonus.getExpression());
		Assert.assertEquals(bonus.getDescription(), draftBonus.getDescriptionTemplate());
		Assert.assertEquals(bonus.getStartDate(), draftBonus.getStartDate());
		Assert.assertEquals(bonus.getEndDate(), draftBonus.getEndDate());
		
		
		// change start date... previous month
		draftBonus.setId(bonus.getId());
		draftBonus.setStartDate(add(startMonth, Calendar.MONTH, -1));

		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(1, bonuses.size());
		bonus = bonuses.get(0); 
		Assert.assertEquals(bonus.getExpression(), draftBonus.getExpression());
		Assert.assertEquals(bonus.getDescription(), draftBonus.getDescriptionTemplate());
		Assert.assertEquals(bonus.getStartDate(), draftBonus.getStartDate());
		Assert.assertEquals(bonus.getEndDate(), draftBonus.getEndDate());
		
		// change end date & expression... 
		draftBonus.setId(bonus.getId());
		draftBonus.setExpression("888.00");
		draftBonus.setEndDate(add(startMonth, Calendar.MONTH, 10));

		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(2, bonuses.size());
		bonus = bonuses.get(0); 
		Assert.assertEquals(bonus.getExpression(), draftBonus.getExpression());
		Assert.assertEquals(bonus.getDescription(), draftBonus.getDescriptionTemplate());
		Assert.assertEquals(bonus.getStartDate(), draftBonus.getStartDate());
		Assert.assertEquals(bonus.getEndDate(), draftBonus.getEndDate());
	
		bonus = bonuses.get(1); 
		Assert.assertEquals(bonus.getExpression(), "999.00");
		Assert.assertEquals(bonus.getDescription(), draftBonus.getDescriptionTemplate());
		Assert.assertEquals(bonus.getStartDate(), add(add(startMonth, Calendar.MONTH, 10),Calendar.DAY_OF_MONTH,1));
		Assert.assertEquals(bonus.getEndDate(), null);
		
		// Remove
		draftBonus.setId(bonuses.get(0).getId());
		draftBonus.setExpression("REMOVE()");
		
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(1, bonuses.size());
		bonus = bonuses.get(0); 
		Assert.assertEquals(bonus.getExpression(), "999.00");
		Assert.assertEquals(bonus.getDescription(), draftBonus.getDescriptionTemplate());
		Assert.assertEquals(bonus.getStartDate(), add(add(startMonth, Calendar.MONTH, 10),Calendar.DAY_OF_MONTH,1));
		Assert.assertEquals(bonus.getEndDate(), null);
		
		// Remove
		draftBonus.setId(bonuses.get(0).getId());
		draftBonus.setExpression("REMOVE()");
		draftBonus.setEndDate(null);
		SQLSalaryDraft.save(connection, draft, contract.getDomain(), null);
		bonuses = getBonus(aonContext, contract);
		Assert.assertEquals(0, bonuses.size());
		
	}

	
	public List<ContractBonusRecord> getBonus(AONContext aonContext, ContractRecord contract) {
		//@formatter:off
		return aonContext.getDslContext()
		.select()
		.from(CONTRACT_BONUS)
		.where(CONTRACT_BONUS.CONTRACT.eq(contract.getId()))
		.orderBy(CONTRACT_BONUS.START_DATE)
		.fetchInto(CONTRACT_BONUS)
		;
		//@formatter:off
	}

	public List<ContractEmbargoRecord> getEmbargo(AONContext aonContext, ContractRecord contract) {
		//@formatter:off
		return aonContext.getDslContext()
		.select()
		.from(CONTRACT_EMBARGO)
		.where(CONTRACT_EMBARGO.CONTRACT.eq(contract.getId()))
		.orderBy(CONTRACT_EMBARGO.START_DATE)
		.fetchInto(CONTRACT_EMBARGO)
		;
		//@formatter:off
	}

	public List<SalaryEmbargoRecord> getSalaryEmbargo(AONContext aonContext, ContractRecord contract) {
		//@formatter:off
		return aonContext.getDslContext()
		.select()
		.from(SALARY_EMBARGO)
		.join(SALARY)
		.on(SALARY_EMBARGO.SALARY.eq(SALARY.ID))
		.where(SALARY.CONTRACT.eq(contract.getId()))
		.orderBy(SALARY.START_DATE)
		.fetchInto(SALARY_EMBARGO)
		;
		//@formatter:off
	}
}
