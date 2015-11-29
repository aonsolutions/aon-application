package com.esferalia.aon.gwt.payroll.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.Level;
import com.esferalia.aon.jooq.tables.records.AgreementLevelCategoryRecord;
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
		AgreementLevelCategoryRecord category = newAgreementCategory(aonContext, agreement, "I", "Categoria 1");
		
		ContractRecord contract = newContract(aonContext, new String[]{}, new String[]{}, category);
		
		
		AgreementDraft draft = new AgreementDraft();
		draft.setId(agreement.getId());
		
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
	
}
