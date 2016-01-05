package com.esferalia.aon.gwt.payroll.sql;

import static com.esferalia.aon.watson.util.AonDateUtils.getFirstDayOfYear;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.gwt.payroll.server.EmployeesServiceHelper;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;
import com.esferalia.aon.jooq.tables.records.AgreementExtraRecord;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
import com.esferalia.aon.jooq.tables.records.AgreementPaymentRecord;
import com.esferalia.aon.jooq.tables.records.AgreementRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.payroll.calculator.sql.AbstractSQLTestCase;

public class SQLAgreementDraftTestCase extends AbstractSQLTestCase {

	@Test
	public void testUpdateCategories() throws SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		AgreementRecord agreement = newAgreement(aonContext);
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext,
				agreement, "I", "Categoria 1");

		ContractRecord contract = newContract(aonContext, new String[] {},
				new String[] {}, category);

		AgreementDraft draft = new AgreementDraft();
		draft.setId(agreement.getId());

		EmployeesServiceHelper.calculate(connection,
				draft, agreement.getDomain(), null);

		Set<String> categories = new HashSet<String>();
		categories.add(category.getDescription());
		categories.add("Categoria 2");
		categories.add("Categoria 3");
		categories.add("Categoria 4");

		Level level = new Level();
		level.setId(category.getAgreementLevel());

		draft.addDraftLevel(level);
		draft.addDraftCategories(level, categories);

		SQLAgreementDraft.save(connection, draft, agreement.getDomain(), null);
	}

	@Test
	public void testRemoveExtras() throws SQLException {

		Connection connection = getConnection();
		AONContext aonContext = new AONContext(connection);

		Date startDate = getFirstDayOfYear(getToday());

		AgreementRecord agreement = newAgreement(aonContext);
		AgreementPaymentRecord payment = addPayment(aonContext, agreement,
				startDate, new Payment() {
					{
						expression = "P_0";
					}
				});
		AgreementExtraRecord extra = addExtra(aonContext, payment, startDate,
				new Extra() {
					{
						this.month = Month.DECEMBER;
						this.start = "01/12";
						this.end = "31/12";
						this.issue = "15/12";

					}
				});

		AgreementDraft draft = new AgreementDraft();
		draft.setId(agreement.getId());

		com.esferalia.aon.gwt.payroll.shared.Extra draftExtra = new com.esferalia.aon.gwt.payroll.shared.Extra();
		draftExtra.setId(extra.getId());
		draftExtra.setIssueDate("REMOVE()");
		draft.addDraftExtra(draftExtra);

		SQLAgreementDraft.save(connection, draft, agreement.getDomain(), null);
	}
}
