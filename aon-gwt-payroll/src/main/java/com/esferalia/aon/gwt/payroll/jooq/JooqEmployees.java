package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.payroll.calculator.jooq.JooqCommon.getDefaultSettings;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Category;
import com.esferalia.aon.gwt.payroll.shared.Employee;

public class JooqEmployees {

	public static List<Employee> getEmployees(Connection connection,
			Integer workplaceId, Date endDate, String pattern, int offset,
			int limit) throws SQLException {
		return getEmployees(DSL.using(connection, getDefaultSettings()),
				workplaceId, endDate, pattern, offset, limit);
	}

	private static List<Employee> getEmployees(DSLContext context,
			Integer workplaceId, Date endDate, String pattern, int offset,
			int limit) throws SQLException {
		Cursor<Record> cursor = null;

		try {
			//@formatter:off
			SelectConditionStep<Record> select = 
			context.select()
			.from(CONTRACT.join(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY)))
			.leftOuterJoin(AGREEMENT_LEVEL_CATEGORY.join(AGREEMENT_LEVEL.join(AGREEMENT)
						.on(AGREEMENT_LEVEL.AGREEMENT.eq(AGREEMENT.ID)))
					.on(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.eq(AGREEMENT_LEVEL.ID)))
				.on(CONTRACT.AGREEMENT_LEVEL_CATEGORY.eq(AGREEMENT_LEVEL_CATEGORY.ID))
			.where(CONTRACT.WORKPLACE.eq(workplaceId))
			.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.greaterOrEqual(new java.sql.Date(endDate.getTime()))));
			
			if ( !StringUtils.isBlank(pattern))
				select = select.and(DSL.concat(PERSON.FIRST_SURNAME, PERSON.SECOND_SURNAME, PERSON.NAME)
						.like("%"+pattern+"%"));
			
			
			cursor = select
			.orderBy(PERSON.FIRST_SURNAME.asc(), PERSON.SECOND_SURNAME.asc(), PERSON.NAME.asc(), CONTRACT.START_DATE.desc())
			.limit(offset, limit)
			.fetchLazy();
			//@formatter:on

			List<Employee> employees = new LinkedList<Employee>();

			for (Record record : cursor) {
				Employee employee = new Employee();

				employee.setId(record.getValue(CONTRACT.ID));
				employee.setStartDate(record.getValue(CONTRACT.START_DATE));
				employee.setEndDate(record.getValue(CONTRACT.END_DATE));

				employee.setPerson(record.getValue(PERSON.REGISTRY));
				employee.setName(record.getValue(PERSON.NAME));
				employee.setFirstSurname(record.getValue(PERSON.FIRST_SURNAME));
				employee.setSecondSurName(record
						.getValue(PERSON.SECOND_SURNAME));
				
				Integer categoryId = record.getValue(AGREEMENT_LEVEL_CATEGORY.ID);
				if ( categoryId != null ) {
					Category category = new Category();
					category.setId(categoryId);
					category.setLevelId(record.getValue(AGREEMENT_LEVEL.ID));
					category.setLevel(record.getValue(AGREEMENT_LEVEL.DESCRIPTION));
					category.setDescription(record
							.getValue(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION));
					
					Agreement agreement = new Agreement();
					agreement.setId(record.getValue(AGREEMENT.ID));
					agreement.setDescription(record.getValue(AGREEMENT.DESCRIPTION));

					category.setAgreement(agreement);
					employee.setCategory(category);
				}
				
				employees.add(employee);

			}

			return employees;

		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

}
