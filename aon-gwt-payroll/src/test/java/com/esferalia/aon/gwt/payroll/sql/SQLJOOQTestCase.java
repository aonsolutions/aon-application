/**
 * 
 */
package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;
import static com.esferalia.aon.watson.util.AonDateUtils.getLastDayOfMonth;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.gwt.payroll.jooq.JooqEmployees;
import com.esferalia.aon.gwt.payroll.jooq.JooqPayrollSalaries;
import com.esferalia.aon.jooq.tables.records.BonusConceptRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.calculator.SmartContractSalaryCalculator;
import com.esferalia.aon.payroll.calculator.jooq.JooqSalaryBuilder;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.enumeration.SSRegimeType;
import com.esferalia.aon.salary.SalaryException;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionException;

/**
 * @author rtrepiana
 *
 */
public class SQLJOOQTestCase extends AbstractSQLTestCase {

	private static final double DELTA = 0.000000001;

	@Test
	public void tesDeleteSalaries() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, getToday(), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'330'");
				put(ContextVariable.MONDAY_HOURS.getName(),"8");
				put(ContextVariable.TUESDAY_HOURS.getName(),"8");
				put(ContextVariable.WEDNESDAY_HOURS.getName(),"8");
				put(ContextVariable.THURSDAY_HOURS.getName(),"8");
				put(ContextVariable.FRIDAY_HOURS.getName(),"8");
			}
			},
			new String[] { 
					"500.00*DIAS_TRABAJADOS/DIAS_MES",
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
			}, 
			new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 1.00",
					},
			null
		);
		//@formatter:on

		addSSRegimeData(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_CGC_E", "23.60");
					}
				});

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY,
				"( BASE_CGC_E = BASE_CGC) * PORCENTAJE_CGC_E/100");

		//@formatter:off
		BonusConceptRecord concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY,
				"/*read-only*/5.00/**/");
		addBonus(aonContext, contract, concept, null);
		//@formatter:on
		
		addEmbargo(aonContext, contract, "666.66");

		DomainRecord domainRecord = getDomain(aonContext, contract.getDomain());
		Domain domain = new Domain()
		.setId(domainRecord.getId())
		.setName(domainRecord.getName())
		.setDescription(domainRecord.getDescription())
		.setActive(domainRecord.getActive() == (byte)1)
		;

		//@formatter:on
		Date startDate = getFirstDayOfMonth(getToday()); 
		for ( int i = 0 ; i < 12; i++) {
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1);
			
		}
		Stream<com.esferalia.aon.occam.api.model.Salary> salaries = 
		AON.getSalaries(domain, "login", p -> p.getContractProperty().eq(contract.getId()));
		salaries.forEach( salary -> {
			System.out.println(salary.getStartDate());
			salary.getPayments().forEach( p -> System.out.println("  P-."+ p.getDescription() + " = " + p.getAmount() ));
			salary.getDeductions().forEach( p -> System.out.println("  D-."+ p.getDescription() + " = " + p.getAmount() ));
			salary.getCosts().forEach( p -> System.out.println("  C-."+ p.getDescription() + " = " + p.getAmount() ));
			salary.getBonuses().forEach( p -> System.out.println("  B-."+ p.getDescription() + " = " + p.getAmount() ));
			salary.getEmbargos().forEach( p -> System.out.println("  E-."+ p.getDescription() + " = " + p.getAmount() ));
		});

		List<Integer> ids = AON.getSalaries(domain, "login", p -> p.getContractProperty().eq(contract.getId())).map(s -> s.getId()).collect(Collectors.toList());
		

		JooqPayrollSalaries.deleteSalaries(connection, domain.getId(), ids);

		AON.getSalaries(domain, "login", p -> p.getContractProperty().eq(contract.getId())).findAny()
		.ifPresent( c -> org.junit.fail("Contracts NOT deleted!!!!!!!!!!!!" ));

	}

	@Test
	public void tesDeleteContracts() throws ExpressionException, SQLException,
			SalaryException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		cleanSystemData(aonContext);
		cleanSystemCosts(aonContext);

		//@formatter:off
		ContractRecord contract = newContract(aonContext, getToday(), 
			new HashMap<String,String>(){
			{
				put(ContextVariable.TC2.getName(), "'330'");
				put(ContextVariable.MONDAY_HOURS.getName(),"8");
				put(ContextVariable.TUESDAY_HOURS.getName(),"8");
				put(ContextVariable.WEDNESDAY_HOURS.getName(),"8");
				put(ContextVariable.THURSDAY_HOURS.getName(),"8");
				put(ContextVariable.FRIDAY_HOURS.getName(),"8");
			}
			},
			new String[] { 
					"500.00*DIAS_TRABAJADOS/DIAS_MES",
					"1000.00 * DIAS_TRABAJADOS / DIAS_MES",
			}, 
			new String[] {
					"BASE_CGC * 0.10", 
					"BASE_CGP * 0.05",
					"BASE_IRPF * 1.00",
					},
			null
		);
		//@formatter:on

		addSSRegimeData(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), null,
				new HashMap<String, String>() {
					{
						put("PORCENTAJE_CGC_E", "23.60");
					}
				});

		addSSRegimeCost(aonContext, SSRegimeType.GENERAL,
				getFirstDayOfYear(getToday()), "CGC_E",
				DeductionType.COMMON_CONTINGENCY,
				"( BASE_CGC_E = BASE_CGC) * PORCENTAJE_CGC_E/100");

		//@formatter:off
		BonusConceptRecord concept = addBonusConcept(aonContext, 
				BonusType.SOCIAL_SECURITY,
				"/*read-only*/5.00/**/");
		addBonus(aonContext, contract, concept, null);
		//@formatter:on
		
		addEmbargo(aonContext, contract, "666.66");

		DomainRecord domainRecord = getDomain(aonContext, contract.getDomain());
		Domain domain = new Domain()
		.setId(domainRecord.getId())
		.setName(domainRecord.getName())
		.setDescription(domainRecord.getDescription())
		.setActive(domainRecord.getActive() == (byte)1)
		;

		//@formatter:on
		Date startDate = getFirstDayOfMonth(getToday()); 
		for ( int i = 0 ; i < 12; i++) {
			Date endDate = getLastDayOfMonth(startDate);
			JooqSalaryBuilder<Salary> jooqSalaryBuilder = new JooqSalaryBuilder<Salary>(connection);
			new SmartContractSalaryCalculator<Salary>(jooqSalaryBuilder)
			.calculate(getContractSalaryCalculatorContext(connection, startDate, endDate, endDate, contract));
			jooqSalaryBuilder.execute();
			startDate = add(startDate, Calendar.MONTH, 1);
			
		}
		Stream<com.esferalia.aon.occam.api.model.Salary> salaries = 
		AON.getSalaries(domain, "login", p -> p.getContractProperty().eq(contract.getId()));
		salaries.forEach( salary -> {
			System.out.println(salary.getStartDate());
			salary.getPayments().forEach( p -> System.out.println("  P-."+ p.getDescription() + " = " + p.getAmount() ));
			salary.getDeductions().forEach( p -> System.out.println("  D-."+ p.getDescription() + " = " + p.getAmount() ));
			salary.getCosts().forEach( p -> System.out.println("  C-."+ p.getDescription() + " = " + p.getAmount() ));
			salary.getBonuses().forEach( p -> System.out.println("  B-."+ p.getDescription() + " = " + p.getAmount() ));
			salary.getEmbargos().forEach( p -> System.out.println("  E-."+ p.getDescription() + " = " + p.getAmount() ));
		});

		JooqEmployees.delete(connection, contract.getId());
		
		PAYROLL.getContract(domain.getName(), domain.getId(), "login", p -> p.getIdProperty().eq(contract.getId()))
		.ifPresent( c -> org.junit.fail("Contracts NOT deleted!!!!!!!!!!!!" ));

		AON.getSalaries(domain, "login", p -> p.getContractProperty().eq(contract.getId())).findAny()
		.ifPresent( c -> org.junit.fail("Contracts NOT deleted!!!!!!!!!!!!" ));

	}

	// ------------------------------------------------------------------------



}
