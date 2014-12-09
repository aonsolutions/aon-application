package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.payroll.calculator.jooq.JooqCommon.getDefaultSettings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.payroll.shared.Deduction;

public class JooqDeductions {

	public static List<Deduction> getConcepts(Connection connection,
			Integer domainId, Integer parentDomainId) throws SQLException {
		return getConcepts(DSL.using(connection, getDefaultSettings()),
				domainId, parentDomainId);
	}

	private static List<Deduction> getConcepts(DSLContext dslContext,
			Integer domainId, Integer parentDomainId) throws SQLException {
		Result<Record> result = dslContext.select()
				.from(DEDUCTION_CONCEPT).where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
				.fetch();
		
		List<Deduction> concepts = new  ArrayList<Deduction>(result.size());
		for(Record record: result ) {
			Deduction concept = new Deduction();
			concept.setId(record.getValue(DEDUCTION_CONCEPT.ID));
			concept.setName(record.getValue(DEDUCTION_CONCEPT.CODE));
			concept.setDescription(record.getValue(DEDUCTION_CONCEPT.DESCRIPTION));
			concept.setExpression(record.getValue(DEDUCTION_CONCEPT.EXPRESSION));
			concepts.add(concept);
			concept.setType(getDeductionType((int) record.getValue(DEDUCTION_CONCEPT.TYPE)));
		}
		return concepts;
	}
	
	private static Deduction.Type getDeductionType(int type) {
		Deduction.Type values [] = Deduction.Type.values();
		
		if ( type < -1  || type >= values.length )
			return Deduction.Type.OTHER;
		if ( type == -1 )
			return Deduction.Type.EMBARGO;
		
		return values[type];
		
	}

}
