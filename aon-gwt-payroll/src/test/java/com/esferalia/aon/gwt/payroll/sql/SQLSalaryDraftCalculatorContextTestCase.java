package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.gwt.payroll.client.SalaryDraftObject;
import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.server.SalaryDraftBuilder;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.jooq.tables.records.BonusConceptRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.PaymentConceptRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.payroll.calculator.sql.ISQLContractSalaryCalculatorContext;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.expression.ExpressionException;

public class SQLSalaryDraftCalculatorContextTestCase extends
		AbstractSQLTestCase {

	@Test
	public void testDraftBonus() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, 
				new String[]{
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

		PaymentConceptRecord baseConcept = addConcept(aonContext,
				"SALARIO_BASE");
		addPayment(aonContext, contract, baseConcept,
				"1500.00 * DIAS_TRABAJADOS / DIAS_MES", "_P");

		PaymentConceptRecord plusConcept = addConcept(aonContext,
				"PLUS_SALARIAL");
		addPayment(aonContext, contract, plusConcept,
				"500.00 * DIAS_TRABAJADOS / DIAS_MES", "_P");

		PaymentConceptRecord seniorConcept = addConcept(aonContext,
				"ANTIGUEDAD");
		addPayment(aonContext, contract, seniorConcept,
				"( SALARIO_BASE + PLUS_SALARIAL ) * 0.10", "_P");

		Date startDate = getFirstDayOfMonth(getToday());
		Date endDate = getLastDayOfMonth(startDate);

		Employee employee = new Employee();
		employee.setId(contract.getId());
		SalaryDraft draft = new SalaryDraft();
		draft.setEmployee(employee);
		draft.setStartDate(startDate);
		draft.setEndDate(endDate);
		draft.setIssueDate(endDate);

		BonusConceptRecord continousConcept = addBonusConcept(aonContext,
				BonusType.CONTINUOUS_FORMATION, null);

		Bonus continousBonus = new Bonus();
		continousBonus.setConceptId(continousConcept.getId());
		continousBonus.setStartDate(startDate);
		continousBonus.setStartDate(endDate);
		continousBonus.setSalaryType(draft.getType());
		continousBonus.setExpression("666.999");
		draft.addDraftBonus(continousBonus);

		ISQLContractSalaryCalculatorContext ctx = EmployeesServiceHelper
				.getSalaryCalculatorContext(connection, draft, null);

		ISalary salary = new ContractSalaryCalculator<ISalary>(
				new SalaryDraftBuilder(draft)).calculate(ctx);
		
		assertEquals(1, draft.getBonuses().size());
		draft.getBonuses().forEach(bonus->assertEquals(666.999,bonus.getAmount()));
		
		
		draft.getBonuses().forEach(bonus-> {
			Bonus continousBonusI = new Bonus();
			continousBonusI.setId(bonus.getId());
			continousBonusI.setConceptId(continousConcept.getId());
			continousBonusI.setStartDate(startDate);
			continousBonusI.setStartDate(endDate);
			continousBonusI.setSalaryType(draft.getType());
			continousBonusI.setExpression("999.666");
			draft.addDraftBonus(continousBonusI);
			});

		ctx = EmployeesServiceHelper
				.getSalaryCalculatorContext(connection, draft, null);
		salary = new ContractSalaryCalculator<ISalary>(
				new SalaryDraftBuilder(draft)).calculate(ctx);
		
		assertEquals(1, draft.getBonuses().size());
		draft.getBonuses().forEach(bonus->assertEquals(999.666,bonus.getAmount()));
		
		draft.getBonuses().forEach(bonus-> {
			Bonus continousBonusI = new Bonus();
			continousBonusI.setId(bonus.getId());
			continousBonusI.setConceptId(continousConcept.getId());
			continousBonusI.setStartDate(startDate);
			continousBonusI.setStartDate(endDate);
			continousBonusI.setSalaryType(draft.getType());
			continousBonusI.setDescriptionTemplate("REDEFINED");
			continousBonusI.setExpression(bonus.getExpression());
			draft.addDraftBonus(continousBonusI);
			});

		ctx = EmployeesServiceHelper
				.getSalaryCalculatorContext(connection, draft, null);
		salary = new ContractSalaryCalculator<ISalary>(
				new SalaryDraftBuilder(draft)).calculate(ctx);

		assertEquals(1, draft.getBonuses().size());
		draft.getBonuses().forEach(bonus->assertEquals(true,bonus.getDescription().startsWith("REDEFINED")));
	
		draft.getBonuses().forEach(bonus-> {
			Bonus continousBonusI = new Bonus();
			continousBonusI.setId(bonus.getId());
			continousBonusI.setConceptId(continousConcept.getId());
			continousBonusI.setStartDate(startDate);
			continousBonusI.setStartDate(endDate);
			continousBonusI.setSalaryType(draft.getType());
			continousBonusI.setExpression("REMOVE()");
			draft.addDraftBonus(continousBonusI);
			});

		ctx = EmployeesServiceHelper
				.getSalaryCalculatorContext(connection, draft, null);
		salary = new ContractSalaryCalculator<ISalary>(
				new SalaryDraftBuilder(draft)).calculate(ctx);

		assertEquals(0, draft.getBonuses().size());
	}
	
	
	

}
