package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper.getAvailableBonuses;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;
import com.esferalia.aon.salary.enumeration.BonusType;

public class EmployeesServiceHelperTest extends AbstractSQLTestCase {

	@Test
	public void testGetAvailableBonusesI() {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		DomainRecord domainI = newDomain(aonContext);

		for (BonusType bonusType : BonusType.values())
			addBonusConcept(aonContext, domainI.getId(), bonusType,
					String.format("%d", bonusType.ordinal()));

		List<Bonus> availableBonuses = getAvailableBonuses(connection, 0,
				domainI.getId());

		assertEquals(BonusType.values().length,availableBonuses.size());
		System.out.println(availableBonuses.size());

		for (Bonus bonus : availableBonuses) {
			assertEquals(bonus.getType().ordinal(),
					Integer.parseInt(bonus.getExpression()));
			System.out.println(bonus.getType().ordinal() + "-." + bonus.getDescription() + " = '" + bonus.getExpression() + "'");
		}

	}

	@Test
	public void testGetAvailableBonusesII() {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);
		
		Integer domains  []= new Integer [BonusType.values().length];
		
		for (int i = 0 ; i<  BonusType.values().length; i++ )
			domains[i]=addBonusConcept(aonContext, BonusType.values()[i],
					String.format("%d", BonusType.values()[i].ordinal())).getDomain();
		
		List<Bonus> availableBonuses = getAvailableBonuses(connection, 0,
				domains);

		assertEquals(BonusType.values().length, availableBonuses.size());
		System.out.println(availableBonuses.size());

		for (Bonus bonus : availableBonuses) {
			assertEquals(bonus.getType().ordinal(),
					Integer.parseInt(bonus.getExpression()));
			System.out.println(bonus.getType().ordinal() + "-." + bonus.getDescription() + " = '" + bonus.getExpression() + "'");
		}

	}
}
