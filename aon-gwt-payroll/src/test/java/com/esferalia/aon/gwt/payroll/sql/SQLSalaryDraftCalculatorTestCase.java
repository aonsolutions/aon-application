package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import org.junit.Test;

import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.server.SalaryDraftBuilder;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.BonusEvent;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.DeductionEvent;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.jooq.tables.records.ContractBonusRecord;
import com.esferalia.aon.jooq.tables.records.ContractEmbargoRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.expression.ExpressionException;

import junit.framework.Assert;

public class SQLSalaryDraftCalculatorTestCase extends AbstractSQLTestCase {

	@Test
	public void testDraftSyntaxErrorEmbargoI()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[]{
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				},
				new String[]{
				"BASE_CGC * 0.10",
				"BASE_CGP * 0.05",
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100"
				}
				);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Employee employee = new Employee();
		employee.setId(contract.getId());
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(endDate);
		draft.setType(Type.SALARY);

		Deduction embargo = new Deduction();
		embargo.setStartDate(startDate);
		embargo.setExpression("100.00(");
		// embargo.setType(Deduction.Type.EMBARGO);
		draft.addDraftEmbargo(embargo);

		ISQLContractSalaryCalculatorContext ctx = EmployeesServiceHelper
				.getSalaryCalculatorContext(connection, draft, null);

		SalaryDraftBuilder builder = new SalaryDraftBuilder(draft);
		ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>(
				builder);
		calculator.setListener(builder);
		calculator.calculate(ctx);
		Assert.assertEquals(1, draft.getEvents().size());
		DeductionEvent event = (DeductionEvent) draft.getEvents().get(0);
		Assert.assertEquals(embargo, event.getDeduction());
		Assert.assertEquals(Deduction.Type.EMBARGO,
				event.getDeduction().getType());

	}

	@Test
	public void testDraftSyntaxErrorEmbargoII()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[]{
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				},
				new String[]{
				"BASE_CGC * 0.10",
				"BASE_CGP * 0.05",
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20",
				"BASE_IRPF * 0.00/100"
				}
				);
		//@formatter:on

		ContractEmbargoRecord embargo = addEmbargo(aonContext, contract,
				"100.00(");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Employee employee = new Employee();
		employee.setId(contract.getId());
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(endDate);
		draft.setType(Type.SALARY);

		ISQLContractSalaryCalculatorContext ctx = EmployeesServiceHelper
				.getSalaryCalculatorContext(connection, draft, null);

		SalaryDraftBuilder builder = new SalaryDraftBuilder(draft);
		ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>(
				builder);
		calculator.setListener(builder);
		calculator.calculate(ctx);
		Assert.assertEquals(1, draft.getEvents().size());
		DeductionEvent event = (DeductionEvent) draft.getEvents().get(0);
		Assert.assertEquals(embargo.getId(), event.getDeduction().getId());
		Assert.assertEquals(Deduction.Type.EMBARGO,
				event.getDeduction().getType());

		Deduction draftEmbargo = new Deduction();
		draftEmbargo.setId(event.getDeduction().getId());
		draftEmbargo.setStartDate(startDate);
		draftEmbargo.setExpression("100.00");
		draftEmbargo.setDescriptionTemplate("EMBARGO");

		draft.addDraftEmbargo(draftEmbargo);

		ctx = EmployeesServiceHelper
				.getSalaryCalculatorContext(connection, draft, null);
		builder = new SalaryDraftBuilder(draft);
		calculator = new ContractSalaryCalculator<ISalary>(builder);
		calculator.setListener(builder);
		calculator.calculate(ctx);

		Assert.assertEquals(0, draft.getEvents().size());
		Assert.assertEquals(1, draft.getEmbargos().size());

	}

	@Test
	public void testDraftSyntaxErrorBonusI()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[]{
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				},
				new String[]{
				"BASE_CGC * 0.10",
				"BASE_CGP * 0.05",
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100"
				}
				);
		//@formatter:on

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Employee employee = new Employee();
		employee.setId(contract.getId());
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(endDate);
		draft.setType(Type.SALARY);

		Bonus bonus = new Bonus();
		bonus.setStartDate(startDate);
		bonus.setExpression("100.00(");
		draft.addDraftBonus(bonus);

		ISQLContractSalaryCalculatorContext ctx = EmployeesServiceHelper
				.getSalaryCalculatorContext(connection, draft, null);

		SalaryDraftBuilder builder = new SalaryDraftBuilder(draft);
		ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>(
				builder);
		calculator.setListener(builder);
		calculator.calculate(ctx);

		Assert.assertEquals(1, draft.getEvents().size());
		BonusEvent event = (BonusEvent) draft.getEvents().get(0);
		Assert.assertEquals(bonus, event.getBonus());
		Assert.assertEquals(bonus.getId(), event.getBonus().getId());

	}

	@Test
	public void testDraftSyntaxErrorBnusII()
			throws ExpressionException, SQLException, SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[]{
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES"
				},
				new String[]{
				"BASE_CGC * 0.10",
				"BASE_CGP * 0.05",
				"BASE_ESTR * 0.10",
				"BASE_NESTR * 0.20",
				"BASE_IRPF * PORCENTAJE_IRPF/100"
				}
				);
		//@formatter:on

		ContractBonusRecord bonus = addBonus(aonContext, contract,
				addBonusConcept(aonContext, BonusType.CONTINUOUS_FORMATION, ""),
				"100.00(");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Employee employee = new Employee();
		employee.setId(contract.getId());
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(endDate);
		draft.setType(Type.SALARY);

		ISQLContractSalaryCalculatorContext ctx = EmployeesServiceHelper
				.getSalaryCalculatorContext(connection, draft, null);

		SalaryDraftBuilder builder = new SalaryDraftBuilder(draft);
		ContractSalaryCalculator<ISalary> calculator = new ContractSalaryCalculator<ISalary>(
				builder);
		calculator.setListener(builder);
		calculator.calculate(ctx);
		Assert.assertEquals(1, draft.getEvents().size());
		BonusEvent event = (BonusEvent) draft.getEvents().get(0);
		Assert.assertEquals(bonus.getId(), event.getBonus().getId());

		Bonus draftBonus = new Bonus();
		draftBonus.setId(event.getBonus().getId());
		draftBonus.setStartDate(startDate);
		draftBonus.setExpression("100.00");
		draftBonus.setDescriptionTemplate("BONUS");
		draft.addDraftBonus(draftBonus);

		ctx = EmployeesServiceHelper
				.getSalaryCalculatorContext(connection, draft, null);
		builder = new SalaryDraftBuilder(draft);
		calculator = new ContractSalaryCalculator<ISalary>(builder);
		calculator.setListener(builder);
		calculator.calculate(ctx);

		Assert.assertEquals(0, draft.getEvents().size());
		Assert.assertEquals(1, draft.getBonuses().size());

	}

}
