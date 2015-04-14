/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.jooq.tables.BonusConcept.BONUS_CONCEPT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.payroll.enumeration.ContextVariable.BONUS_DAYS;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;
import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static junit.framework.Assert.assertEquals;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.Test;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.jooq.tables.records.BonusConceptRecord;
import com.esferalia.aon.jooq.tables.records.ContractBonusRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.SalaryBuilder;
import com.esferalia.aon.payroll.calculator.ContractSalaryCalculator;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.expression.ExpressionException;
import com.esferalia.aon.watson.util.AonDateUtils;

/**
 * @author rtrepiana
 *
 */
public class SQLBonusTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	@Test
	public void testBonusDaysI() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
					}
				});

		Date bonusEnd = AonDateUtils.add(getToday(), YEAR,3);
		ContractBonusRecord bonus = addBonus(aonContext, contract,
				String.format("%s", BONUS_DAYS), getToday(),
				bonusEnd);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		int bonusDays = AonDateUtils.get(end, Calendar.DATE)
				- AonDateUtils.get(getToday(), Calendar.DATE) + 1;
		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder()).calculate(ctx);
		assertEquals(1, salary.getSalaryBonus().size());
		SalaryBonus salaryBonus = salary.getSalaryBonus().iterator().next();
		System.out.println(0 + "-." +start + "..."+ end + " = " + bonusDays  );
		assertEquals(bonusDays == AonDateUtils.getMax(start,DATE)? 30: bonusDays, salaryBonus.getAmount(), DELTA);
		
		for (int i = 1; i <= 24; i++) {
			start = getFirstDayOfMonth(add(start, MONTH, 1));
			end = getLastDayOfMonth(start);
			ctx = new SQLContractSalaryCalculatorContext(connection, start,
					end, end, criteria);
			ctx.next();
			bonusDays = AonDateUtils.get(end, Calendar.DATE)
					- AonDateUtils.get(start, Calendar.DATE) + 1;
			salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
					.calculate(ctx);
			assertEquals(1, salary.getSalaryBonus().size());
			salaryBonus = salary.getSalaryBonus().iterator().next();
			System.out.println(i + "-." +start + "..."+ end + " = " + bonusDays );
			assertEquals(bonusDays == AonDateUtils.getMax(start,DATE)? 30: bonusDays, salaryBonus.getAmount(),
					DELTA);
		}

		start = getFirstDayOfMonth(bonusEnd);
		end = getLastDayOfMonth(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start,
				end, end, criteria);
		ctx.next();
		bonusDays = AonDateUtils.get(bonusEnd, Calendar.DATE)
				- AonDateUtils.get(start, Calendar.DATE) + 1;
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		assertEquals(1, salary.getSalaryBonus().size());
		salaryBonus = salary.getSalaryBonus().iterator().next();
		System.out.println(start + "..."+ end + " = " + bonusDays );
		assertEquals(bonusDays == AonDateUtils.getMax(start,DATE)? 30: bonusDays, salaryBonus.getAmount(),
				DELTA);

		start = getFirstDayOfMonth(add(bonusEnd,MONTH,1));
		end = getLastDayOfMonth(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start,
				end, end, criteria);
		ctx.next();
		bonusDays = AonDateUtils.get(bonusEnd, Calendar.DATE)
				- AonDateUtils.get(start, Calendar.DATE) + 1;
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		assertEquals(0, salary.getSalaryBonus().size());
	}

	@Test
	public void testBonusDaysII() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		@SuppressWarnings("serial")
		ContractRecord contract = newContract(aonContext, getToday(),
				new HashMap<String, String>() {
					{
					}
				});

		Date bonusEnd = AonDateUtils.add(getToday(), YEAR,3);
		ContractBonusRecord bonus1 = addBonus(aonContext, contract,
				String.format("%s", BONUS_DAYS), getToday(),
				bonusEnd);
		addBonus(aonContext, contract,
				addBonusConcept(aonContext,String.format("%s * 2", BONUS_DAYS),BonusType.SOCIAL_SECURITY), getToday(),
				bonusEnd);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				CONTRACT.getName() + "." + CONTRACT.ID.getName(),
				contract.getId());

		Date start = getFirstDayOfMonth(getToday());
		Date end = getLastDayOfMonth(start);
		SQLContractSalaryCalculatorContext ctx = new SQLContractSalaryCalculatorContext(
				connection, start, end, end, criteria);
		ctx.next();
		int bonusDays = AonDateUtils.get(end, Calendar.DATE)
				- AonDateUtils.get(getToday(), Calendar.DATE) + 1;
		Salary salary = new ContractSalaryCalculator<Salary>(
				new SalaryBuilder()).calculate(ctx);
		assertEquals(2, salary.getSalaryBonus().size());
		System.out.println(0 + "-." +start + "..."+ end + " = " + bonusDays  );
		for (SalaryBonus salaryBonus : salary.getSalaryBonus()) 
			assertEquals((bonusDays == AonDateUtils.getMax(start,DATE)? 30: bonusDays) * (salaryBonus.getDescription().equals(bonus1.getDescription()) ? 1 : 2), salaryBonus.getAmount(), DELTA);
		
		for (int i = 1; i <= 24; i++) {
			start = getFirstDayOfMonth(add(start, MONTH, 1));
			end = getLastDayOfMonth(start);
			ctx = new SQLContractSalaryCalculatorContext(connection, start,
					end, end, criteria);
			ctx.next();
			bonusDays = AonDateUtils.get(end, Calendar.DATE)
					- AonDateUtils.get(start, Calendar.DATE) + 1;
			salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
					.calculate(ctx);
			assertEquals(2, salary.getSalaryBonus().size());
			System.out.println(i + "-." +start + "..."+ end + " = " + bonusDays );
			for (SalaryBonus salaryBonus : salary.getSalaryBonus()) 
				assertEquals((bonusDays == AonDateUtils.getMax(start,DATE)? 30: bonusDays) * (salaryBonus.getDescription().equals(bonus1.getDescription()) ? 1 : 2), salaryBonus.getAmount(), DELTA);
		}

		start = getFirstDayOfMonth(bonusEnd);
		end = getLastDayOfMonth(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start,
				end, end, criteria);
		ctx.next();
		bonusDays = AonDateUtils.get(bonusEnd, Calendar.DATE)
				- AonDateUtils.get(start, Calendar.DATE) + 1;
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		assertEquals(2, salary.getSalaryBonus().size());
		System.out.println(start + "..."+ end + " = " + bonusDays );
		for (SalaryBonus salaryBonus : salary.getSalaryBonus()) 
			assertEquals((bonusDays == AonDateUtils.getMax(start,DATE)? 30: bonusDays) * (salaryBonus.getDescription().equals(bonus1.getDescription()) ? 1 : 2), salaryBonus.getAmount(), DELTA);

		start = getFirstDayOfMonth(add(bonusEnd,MONTH,1));
		end = getLastDayOfMonth(start);
		ctx = new SQLContractSalaryCalculatorContext(connection, start,
				end, end, criteria);
		ctx.next();
		bonusDays = AonDateUtils.get(bonusEnd, Calendar.DATE)
				- AonDateUtils.get(start, Calendar.DATE) + 1;
		salary = new ContractSalaryCalculator<Salary>(new SalaryBuilder())
				.calculate(ctx);
		assertEquals(0, salary.getSalaryBonus().size());
	}
	// ------------------------------------------------------------------------

	protected final ContractBonusRecord addBonus(AONContext aonContext,
			ContractRecord contract, String expression, Date startDate,
			Date endDate) {
		return aonContext.getDslContext().insertInto(CONTRACT_BONUS)
				.set(CONTRACT_BONUS.DOMAIN, contract.getDomain())
				.set(CONTRACT_BONUS.CONTRACT, contract.getId())
				.set(CONTRACT_BONUS.START_DATE, startDate)
				.set(CONTRACT_BONUS.END_DATE, endDate)
				.set(CONTRACT_BONUS.DESCRIPTION, expression)
				.set(CONTRACT_BONUS.EXPRESSION, expression).returning()
				.fetchOne();

	}

	protected final BonusConceptRecord addBonusConcept(AONContext aonContext, String expression, BonusType type) {
		DomainRecord domain = newDomain(aonContext);
		return aonContext
				.getDslContext()
				.insertInto(BONUS_CONCEPT)
				.set(BONUS_CONCEPT.DOMAIN, domain.getId())
				.set(BONUS_CONCEPT.TYPE,
						(byte) type.ordinal())
				.set(BONUS_CONCEPT.EXPRESSION, expression)
				.set(BONUS_CONCEPT.DESCRIPTION, expression)
				.returning().fetchOne();
	
	}
	

	protected final ContractBonusRecord addBonus(AONContext aonContext,
			ContractRecord contract, BonusConceptRecord concept, Date startDate,
			Date endDate) {
		return aonContext.getDslContext().insertInto(CONTRACT_BONUS)
				.set(CONTRACT_BONUS.DOMAIN, contract.getDomain())
				.set(CONTRACT_BONUS.CONTRACT, contract.getId())
				.set(CONTRACT_BONUS.START_DATE, startDate)
				.set(CONTRACT_BONUS.END_DATE, endDate)
				.set(CONTRACT_BONUS.BONUS_CONCEPT, concept.getId())
				.returning()
				.fetchOne();

	}
}
