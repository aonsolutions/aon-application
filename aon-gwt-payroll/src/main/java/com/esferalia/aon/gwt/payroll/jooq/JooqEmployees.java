package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.Certifica2Batch.CERTIFICA2_BATCH;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractBatch.CONTRACT_BATCH;
import static com.esferalia.aon.jooq.tables.ContractBatchDetail.CONTRACT_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractCalendarEvent.CONTRACT_CALENDAR_EVENT;
import static com.esferalia.aon.jooq.tables.ContractClause.CONTRACT_CLAUSE;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractLeaveDetail.CONTRACT_LEAVE_DETAIL;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.IrpfDataAscendants.IRPF_DATA_ASCENDANTS;
import static com.esferalia.aon.jooq.tables.IrpfDataDescendients.IRPF_DATA_DESCENDIENTS;
import static com.esferalia.aon.jooq.tables.IrpfRegularization.IRPF_REGULARIZATION;
import static com.esferalia.aon.jooq.tables.IrpfResult.IRPF_RESULT;
import static com.esferalia.aon.jooq.tables.LeaveBatch.LEAVE_BATCH;
import static com.esferalia.aon.jooq.tables.LeaveBatchDetail.LEAVE_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Category;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.jooq.tables.Contract;
import com.esferalia.aon.jooq.tables.ContractInfo;
import com.esferalia.aon.jooq.tables.Person;
import com.esferalia.aon.jooq.tables.records.ContractBonusRecord;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractDeductionRecord;
import com.esferalia.aon.jooq.tables.records.ContractInfoRecord;
import com.esferalia.aon.jooq.tables.records.ContractPaymentRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;

public class JooqEmployees {

	private static Settings SETTINGS = null;

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
			// @formatter:off
			SelectConditionStep<Record> select = context
					.select()
					.from(CONTRACT.join(PERSON).on(
							CONTRACT.PERSON.eq(PERSON.REGISTRY)))
					.leftOuterJoin(
							AGREEMENT_LEVEL_CATEGORY.join(
									AGREEMENT_LEVEL.join(AGREEMENT).on(
											AGREEMENT_LEVEL.AGREEMENT
													.eq(AGREEMENT.ID))).on(
									AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL
											.eq(AGREEMENT_LEVEL.ID)))
					.on(CONTRACT.AGREEMENT_LEVEL_CATEGORY
							.eq(AGREEMENT_LEVEL_CATEGORY.ID))
					.where(CONTRACT.WORKPLACE.eq(workplaceId))
					.and(CONTRACT.END_DATE.isNull().or(
							CONTRACT.END_DATE.greaterOrEqual(new java.sql.Date(
									endDate.getTime()))));

			if (!StringUtils.isBlank(pattern))
				select = select.and(DSL.concat(PERSON.FIRST_SURNAME,
						PERSON.SECOND_SURNAME, PERSON.NAME).like(
						"%" + pattern + "%"));

			cursor = select
					.orderBy(PERSON.FIRST_SURNAME.asc(),
							PERSON.SECOND_SURNAME.asc(), PERSON.NAME.asc(),
							CONTRACT.START_DATE.desc()).limit(offset, limit)
					.fetchLazy();
			// @formatter:on

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
				Integer categoryId = record
						.getValue(AGREEMENT_LEVEL_CATEGORY.ID);
				if (categoryId != null) {
					Category category = new Category();
					category.setId(categoryId);
					category.setLevelId(record.getValue(AGREEMENT_LEVEL.ID));
					category.setLevel(record
							.getValue(AGREEMENT_LEVEL.DESCRIPTION));
					category.setDescription(record
							.getValue(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION));

					Agreement agreement = new Agreement();
					agreement.setId(record.getValue(AGREEMENT.ID));
					agreement.setDescription(record
							.getValue(AGREEMENT.DESCRIPTION));

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
	
	public static List<Employee> getTrashEmployees(Connection conn, int workplaceId) {
		return getTrashEmployees(DSL.using(
				conn, getDefaultSettings()), 
				workplaceId);
	}
	
	private static List<Employee> getTrashEmployees(DSLContext context, int workplaceId) {
		
		Cursor<Record> cursor = null;
		
		try {
			// @formatter:off
			SelectConditionStep<Record> select = context
					.select()
					.from(CONTRACT.join(PERSON).on(
							CONTRACT.PERSON.eq(PERSON.REGISTRY)))
					.leftOuterJoin(
							AGREEMENT_LEVEL_CATEGORY.join(
									AGREEMENT_LEVEL.join(AGREEMENT).on(
											AGREEMENT_LEVEL.AGREEMENT
													.eq(AGREEMENT.ID))).on(
									AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL
											.eq(AGREEMENT_LEVEL.ID)))
					.on(CONTRACT.AGREEMENT_LEVEL_CATEGORY
							.eq(AGREEMENT_LEVEL_CATEGORY.ID))
					.where(CONTRACT.WORKPLACE.eq(workplaceId))
					.and(CONTRACT.ID.lessThan(0));

			cursor = select
					.orderBy(PERSON.FIRST_SURNAME.asc(),
							PERSON.SECOND_SURNAME.asc(), PERSON.NAME.asc(),
							CONTRACT.START_DATE.desc())
					.fetchLazy();
			// @formatter:on

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
				Integer categoryId = record
						.getValue(AGREEMENT_LEVEL_CATEGORY.ID);
				if (categoryId != null) {
					Category category = new Category();
					category.setId(categoryId);
					category.setLevelId(record.getValue(AGREEMENT_LEVEL.ID));
					category.setLevel(record
							.getValue(AGREEMENT_LEVEL.DESCRIPTION));
					category.setDescription(record
							.getValue(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION));

					Agreement agreement = new Agreement();
					agreement.setId(record.getValue(AGREEMENT.ID));
					agreement.setDescription(record
							.getValue(AGREEMENT.DESCRIPTION));

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

	public static Map<String, String> getAvaiableEmployees(Connection conn,
			Integer domain, Integer parent) throws SQLException {

		return getAvaiableEmployees(
				DSL.using(conn, SQLDialect.MYSQL, getDefaultSettings()),
				domain, parent);

	}

	private static Map<String, String> getAvaiableEmployees(DSLContext context,
			Integer domain, Integer parent) throws SQLException {

		Cursor<Record> cursor = null;

		try {

			// @formatter:off
			SelectConditionStep<Record> select = context.select().from(PERSON)
					.join(REGISTRY).on(PERSON.REGISTRY.eq(REGISTRY.ID))
					.where(PERSON.DOMAIN.eq(domain));

			if (parent != null)
				select = select.or(PERSON.DOMAIN.eq(parent));
			// @formatter:on

			cursor = select.orderBy(PERSON.FIRST_SURNAME.asc(),
					PERSON.SECOND_SURNAME.asc(), PERSON.NAME.asc()).fetchLazy();

			Map<String, String> employees = new HashMap<String, String>();

			for (Record person : cursor) {
				Employee employee = new Employee();
				employee.setName(person.getValue(PERSON.NAME));
				employee.setFirstSurname(person.getValue(PERSON.FIRST_SURNAME));
				employee.setSecondSurName(person
						.getValue(PERSON.SECOND_SURNAME));

				employees.put(person.getValue(REGISTRY.DOCUMENT),
						employee.getFullname());
			}

			return employees;

		} finally {

			if (cursor != null)
				cursor.close();
		}
	}

	private static Employee getDataEmployee(DSLContext create, String document,
			Date startDate, Date endDate) {

		// @formatter:off
		Record person = create.select().from(Person.PERSON).join(REGISTRY)
				.on(PERSON.REGISTRY.eq(REGISTRY.ID))
				.where(REGISTRY.DOCUMENT.eq(document)).fetchOne();
		// @formatter:on

		Employee employee = new Employee();

		employee.setStartDate(startDate);
		employee.setEndDate(endDate);
		employee.setDocument(person.getValue(REGISTRY.DOCUMENT));
		employee.setPerson(person.getValue(PERSON.REGISTRY));
		employee.setName(person.getValue(PERSON.NAME));
		employee.setFirstSurname(person.getValue(PERSON.FIRST_SURNAME));
		employee.setSecondSurName(person.getValue(PERSON.SECOND_SURNAME));

		return employee;

	}

	public static Employee paste(Connection conn, int domain, int workplaceId,
			int contractId, String document, Date startDate, Date endDate,
			boolean check) throws SQLException {

		DSLContext create = DSL.using(conn, SQLDialect.MYSQL,
				getDefaultSettings());

		Employee employee = getDataEmployee(create, document, startDate,
				endDate);

		int newContractId = insert2Contract(create, contractId, workplaceId,
				employee);
		insert2ContractBonus(create, contractId, newContractId, startDate,
				endDate);
		insert2ContractData(create, contractId, newContractId, startDate,
				endDate);
		insert2ContractInfo(create, contractId, newContractId, startDate,
				endDate);

		if (check) {
			/**
			 * ContractDeduction ContractPayment
			 */
			insert2ContractDeduction(create, contractId, newContractId,
					startDate, endDate);
			insert2ContractPayment(create, contractId, newContractId,
					startDate, endDate);

		}

		employee.setId(newContractId);

		return employee;

	}

	protected static int insert2Contract(DSLContext create, int contractId,
			int workplaceId, Employee employee) throws SQLException {

		// @formatter:off
		Record contractRecord = create
				.select()
				.from(CONTRACT)
				.leftOuterJoin(
						AGREEMENT_LEVEL_CATEGORY.join(
								AGREEMENT_LEVEL.join(AGREEMENT).on(
										AGREEMENT_LEVEL.AGREEMENT
												.eq(AGREEMENT.ID))).on(
								AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL
										.eq(AGREEMENT_LEVEL.ID)))
				.on(CONTRACT.AGREEMENT_LEVEL_CATEGORY
						.eq(AGREEMENT_LEVEL_CATEGORY.ID))
				.where(CONTRACT.ID.eq(contractId)).fetchOne();
		// @formatter:on

		Integer categoryId = contractRecord
				.getValue(AGREEMENT_LEVEL_CATEGORY.ID);
		if (categoryId != null) {
			Category category = new Category();
			category.setId(categoryId);
			category.setLevelId(contractRecord.getValue(AGREEMENT_LEVEL.ID));
			category.setLevel(contractRecord
					.getValue(AGREEMENT_LEVEL.DESCRIPTION));
			category.setDescription(contractRecord
					.getValue(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION));

			Agreement agreement = new Agreement();
			agreement.setId(contractRecord.getValue(AGREEMENT.ID));
			agreement.setDescription(contractRecord
					.getValue(AGREEMENT.DESCRIPTION));

			category.setAgreement(agreement);
			employee.setCategory(category);
		}

		// @formatter:off
		InsertSetMoreStep<ContractRecord> insertContract = create
				.insertInto(CONTRACT)

				.set(CONTRACT.DOMAIN, contractRecord.getValue(CONTRACT.DOMAIN))
				.set(CONTRACT.PERSON, employee.getPerson())
				.set(CONTRACT.WORKPLACE, workplaceId)
				.set(CONTRACT.ENTERPRISE_CCC,
						contractRecord.getValue(CONTRACT.ENTERPRISE_CCC))
				.set(CONTRACT.START_DATE,
						new java.sql.Date(employee.getStartDate().getTime()))
				.set(CONTRACT.CALENDAR,
						contractRecord.getValue(CONTRACT.CALENDAR))
				.set(CONTRACT.DESCRIPTION,
						contractRecord.getValue(CONTRACT.DESCRIPTION))
				.set(CONTRACT.SEPE_STATUS,
						contractRecord.getValue(CONTRACT.SEPE_STATUS))
				.set(CONTRACT.REGISTRATION,
						contractRecord.getValue(CONTRACT.REGISTRATION))
				.set(CONTRACT.SENIORITY_DATE,
						contractRecord.getValue(CONTRACT.SENIORITY_DATE))
				.set(CONTRACT.ENTERPRISE_ACTIVITY,
						contractRecord.getValue(CONTRACT.ENTERPRISE_ACTIVITY))
				.set(CONTRACT.SS_REGIME,
						contractRecord.getValue(CONTRACT.SS_REGIME))
				.set(CONTRACT.AGREEMENT_LEVEL_CATEGORY,
						contractRecord
								.getValue(CONTRACT.AGREEMENT_LEVEL_CATEGORY))
				.set(CONTRACT.MODEL, contractRecord.getValue(CONTRACT.MODEL))
				.set(CONTRACT.CATEGORY_DESCRIPTION,
						contractRecord.getValue(CONTRACT.CATEGORY_DESCRIPTION))
				.set(CONTRACT.SS_STATUS,
						contractRecord.getValue(CONTRACT.SS_STATUS));
		// @formatter:on

		if (employee.getEndDate() != null)
			insertContract = insertContract.set(Contract.CONTRACT.END_DATE,
					new java.sql.Date(employee.getEndDate().getTime()));

		Integer newContractId = insertContract.returning(CONTRACT.ID)
				.fetchOne().getId();
		employee.setId(newContractId);

		return newContractId;
	}

	protected static void insert2ContractBonus(DSLContext create,
			int oldContractId, int newContractId, Date startDate, Date endDate)
			throws SQLException {

		InsertSetMoreStep<ContractBonusRecord> insert;

		// @formatter:off
		List<ContractBonusRecord> bonuses = create.selectFrom(CONTRACT_BONUS)
				.where(CONTRACT_BONUS.CONTRACT.eq(oldContractId))
				.fetchInto(CONTRACT_BONUS);
		// @formatter:on

		if (bonuses != null) {

			for (ContractBonusRecord bonus : bonuses) {
				insert = create
						.insertInto(CONTRACT_BONUS)

						.set(CONTRACT_BONUS.DOMAIN, bonus.getDomain())
						.set(CONTRACT_BONUS.CONTRACT, newContractId)
						.set(CONTRACT_BONUS.DESCRIPTION, bonus.getDescription())
						.set(CONTRACT_BONUS.EXPRESSION, bonus.getExpression())
						.set(CONTRACT_BONUS.START_DATE,
								new java.sql.Date(startDate.getTime()));

				if (endDate != null)
					insert = insert.set(CONTRACT_BONUS.END_DATE,
							new java.sql.Date(endDate.getTime()));

				insert.execute();
			}
		}
	}

	protected static void insert2ContractData(DSLContext create,
			int contractId, int newContractId, Date startDate, Date endDate)
			throws SQLException {

		InsertSetMoreStep<ContractDataRecord> insert;

		// @formatter:off
		List<ContractDataRecord> datas = create.selectFrom(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.fetchInto(CONTRACT_DATA);
		// @formatter:on

		if (datas != null) {

			for (ContractDataRecord contractData : datas) {

				insert = create
						.insertInto(CONTRACT_DATA)
						.set(CONTRACT_DATA.DOMAIN, contractData.getDomain())
						.set(CONTRACT_DATA.NAME, contractData.getName())
						.set(CONTRACT_DATA.CONTRACT, newContractId)
						.set(CONTRACT_DATA.EXPRESSION,
								contractData.getExpression())
						.set(CONTRACT_DATA.START_DATE,
								new java.sql.Date(startDate.getTime()));

				if (endDate != null)
					insert = insert.set(CONTRACT_DATA.END_DATE,
							new java.sql.Date(endDate.getTime()));

				insert.execute();
			}
		}
	}

	protected static void insert2ContractInfo(DSLContext create,
			int contractId, int newContractId, Date startDate, Date endDate) {

		InsertSetMoreStep<ContractInfoRecord> insert;

		List<ContractInfoRecord> infos = create.selectFrom(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.fetchInto(CONTRACT_INFO);

		if (infos != null) {

			for (ContractInfoRecord info : infos) {

				insert = create
						.insertInto(CONTRACT_INFO)
						.set(CONTRACT_INFO.DOMAIN, info.getDomain())
						.set(CONTRACT_INFO.CONTRACT, newContractId)
						.set(CONTRACT_INFO.NAME,
								info.getValue(CONTRACT_INFO.NAME))
						.set(CONTRACT_INFO.EXPRESSION,
								info.getValue(CONTRACT_INFO.EXPRESSION))
						.set(CONTRACT_INFO.START_DATE,
								new java.sql.Date(startDate.getTime()))
						.set(CONTRACT_INFO.CREATION_USER,
								info.getValue(CONTRACT_INFO.CREATION_USER))
						.set(CONTRACT_INFO.CREATION_DATE,
								info.getValue(CONTRACT_INFO.CREATION_DATE))
						.set(CONTRACT_INFO.MODIFICATION_USER,
								info.getValue(CONTRACT_INFO.MODIFICATION_USER))
						.set(CONTRACT_INFO.MODIFICATION_DATE,
								info.getValue(CONTRACT_INFO.MODIFICATION_DATE));

				if (endDate != null)
					insert = insert.set(CONTRACT_INFO.END_DATE,
							new java.sql.Date(endDate.getTime()));

				insert.execute();
			}

		}

	}

	protected static void insert2ContractDeduction(DSLContext create,
			int contractId, int newContractId, Date startDate, Date endDate)
			throws SQLException {

		InsertSetMoreStep<ContractDeductionRecord> insert;

		// @formatter:off
		List<ContractDeductionRecord> deductions = create
				.selectFrom(CONTRACT_DEDUCTION)
				.where(CONTRACT_DEDUCTION.CONTRACT.eq(contractId))
				.fetchInto(CONTRACT_DEDUCTION);
		// @formatter:on

		if (deductions != null) {

			for (ContractDeductionRecord deduction : deductions) {

				insert = create
						.insertInto(CONTRACT_DEDUCTION)

						.set(CONTRACT_DEDUCTION.DOMAIN, deduction.getDomain())
						.set(CONTRACT_DEDUCTION.TYPE, deduction.getType())
						.set(CONTRACT_DEDUCTION.DEDUCTION_CONCEPT,
								deduction.getDeductionConcept())
						.set(CONTRACT_DEDUCTION.CONTRACT, newContractId)
						.set(CONTRACT_DEDUCTION.DESCRIPTION,
								deduction.getDescription())
						.set(CONTRACT_DEDUCTION.DESCRIPTION_DECORABLE,
								deduction.getDescriptionDecorable())
						.set(CONTRACT_DEDUCTION.EXPRESSION,
								deduction.getExpression())
						.set(CONTRACT_DEDUCTION.START_DATE,
								new java.sql.Date(startDate.getTime()));

				if (endDate != null)
					insert = insert.set(CONTRACT_DEDUCTION.END_DATE,
							new java.sql.Date(endDate.getTime()));

				insert.execute();
			}
		}
	}

	protected static void insert2ContractPayment(DSLContext create,
			int contractId, int newContractId, Date startDate, Date endDate)
			throws SQLException {

		InsertSetMoreStep<ContractPaymentRecord> insert;

		// @formatter:off
		List<ContractPaymentRecord> payments = create
				.selectFrom(CONTRACT_PAYMENT)
				.where(CONTRACT_PAYMENT.CONTRACT.eq(contractId))
				.fetchInto(CONTRACT_PAYMENT);
		// @formatter:off

		if (payments != null) {

			for (ContractPaymentRecord payment : payments) {
				insert = create
						.insertInto(CONTRACT_PAYMENT)

						.set(CONTRACT_PAYMENT.DOMAIN, payment.getDomain())
						.set(CONTRACT_PAYMENT.TYPE, payment.getType())
						.set(CONTRACT_PAYMENT.CONTRACT, newContractId)
						.set(CONTRACT_PAYMENT.PAYMENT_CONCEPT,
								payment.getPaymentConcept())
						.set(CONTRACT_PAYMENT.DESCRIPTION,
								payment.getDescription())
						.set(CONTRACT_PAYMENT.DESCRIPTION_DECORABLE,
								payment.getDescriptionDecorable())
						.set(CONTRACT_PAYMENT.EXPRESSION,
								payment.getExpression())
						.set(CONTRACT_PAYMENT.IRPF_EXPRESSION,
								payment.getIrpfExpression())
						.set(CONTRACT_PAYMENT.QUOTE_EXPRESSION,
								payment.getQuoteExpression())
						.set(CONTRACT_PAYMENT.START_DATE,
								new java.sql.Date(startDate.getTime()))
						.set(CONTRACT_PAYMENT.MONTH, payment.getMonth())
						.set(CONTRACT_PAYMENT.SALARY_TYPE,
								payment.getSalaryType());

				if (endDate != null)
					insert = insert.set(CONTRACT_PAYMENT.END_DATE,
							new java.sql.Date(endDate.getTime()));

				insert.execute();
			}
		}
	}

	public static void delete(Connection conn, int personId)
			throws SQLException {

		try {

			deleteContract(conn, personId);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static final String SET_FOREIGN_KEY_CHECKS_0 = "SET FOREIGN_KEY_CHECKS=0;";
	private static final String SET_FOREIGN_KEY_CHECKS_1 = "SET FOREIGN_KEY_CHECKS=1;";

	public static void moveContractId(Connection conn, Integer personId) {

		try {
			Statement sOpen = conn.createStatement();
			sOpen.execute(SET_FOREIGN_KEY_CHECKS_0);
			System.out.println("Claves referenciales desactivadas");

			// ------------------------------------------------------

			DSLContext dslContext = DSL.using(conn, SQLDialect.MYSQL,
					getDefaultSettings());

			dslContext.update(IRPF_DATA).set(IRPF_DATA.CONTRACT, -(personId))
					.where(IRPF_DATA.CONTRACT.eq(personId)).execute();

			dslContext.update(IRPF_REGULARIZATION)
					.set(IRPF_REGULARIZATION.CONTRACT, -(personId))
					.where(IRPF_REGULARIZATION.CONTRACT.eq(personId)).execute();

			dslContext.update(IRPF_RESULT)
					.set(IRPF_RESULT.CONTRACT, -(personId))
					.where(IRPF_RESULT.CONTRACT.eq(personId)).execute();

			dslContext.update(SALARY).set(SALARY.CONTRACT, -(personId))
					.where(SALARY.CONTRACT.eq(personId)).execute();

			dslContext.update(CERTIFICA2_BATCH_DETAIL)
					.set(CERTIFICA2_BATCH_DETAIL.CONTRACT, -(personId))
					.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.eq(personId))
					.execute();

			dslContext.update(CONTRACT_LEAVE)
					.set(CONTRACT_LEAVE.CONTRACT, -(personId))
					.where(CONTRACT_LEAVE.CONTRACT.eq(personId)).execute();

			dslContext.update(CONTRACT_ATTACH)
					.set(CONTRACT_ATTACH.CONTRACT, -(personId))
					.where(CONTRACT_ATTACH.CONTRACT.eq(personId)).execute();

			dslContext.update(CONTRACT_BATCH_DETAIL)
					.set(CONTRACT_BATCH_DETAIL.CONTRACT, -(personId))
					.where(CONTRACT_BATCH_DETAIL.CONTRACT.eq(personId))
					.execute();

			dslContext.update(CONTRACT_BONUS)
					.set(CONTRACT_BONUS.CONTRACT, -(personId))
					.where(CONTRACT_BONUS.CONTRACT.eq(personId)).execute();

			dslContext.update(CONTRACT_CALENDAR_EVENT)
					.set(CONTRACT_CALENDAR_EVENT.CONTRACT, -(personId))
					.where(CONTRACT_CALENDAR_EVENT.CONTRACT.eq(personId))
					.execute();

			dslContext.update(CONTRACT_CLAUSE)
					.set(CONTRACT_CLAUSE.CONTRACT, -(personId))
					.where(CONTRACT_CLAUSE.CONTRACT.eq(personId)).execute();

			dslContext.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.CONTRACT, -(personId))
					.where(CONTRACT_DATA.CONTRACT.eq(personId)).execute();

			dslContext.update(CONTRACT_DEDUCTION)
					.set(CONTRACT_DEDUCTION.CONTRACT, -(personId))
					.where(CONTRACT_DEDUCTION.CONTRACT.eq(personId)).execute();

			dslContext.update(CONTRACT_EMBARGO)
					.set(CONTRACT_EMBARGO.CONTRACT, -(personId))
					.where(CONTRACT_EMBARGO.CONTRACT.eq(personId)).execute();

			dslContext.update(CONTRACT_INFO)
					.set(CONTRACT_INFO.CONTRACT, -(personId))
					.where(CONTRACT_INFO.CONTRACT.eq(personId)).execute();

			dslContext.update(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.CONTRACT, -(personId))
					.where(CONTRACT_PAYMENT.CONTRACT.eq(personId)).execute();

			dslContext.update(CONTRACT).set(CONTRACT.ID, -(personId))
					.where(CONTRACT.ID.eq(personId)).execute();

			// ------------------------------------------------------

			Statement sClose = conn.createStatement();
			sClose.execute(SET_FOREIGN_KEY_CHECKS_1);
			System.out.println("Claves referenciales activadas");

		} catch (SQLException ex) {
			throw new IllegalArgumentException();
		} catch (Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	private static void deleteContract(Connection conn, Integer... personIds)
			throws Exception {

		DSLContext create = DSL.using(conn, SQLDialect.MYSQL,
				getDefaultSettings());

		// ----------------------------------IRPF-------------------------------------------

		SelectConditionStep<Record1<Integer>> irpfSelect = create
				// formatter:off
				.select(IRPF_DATA.ID).from(IRPF_DATA)
				.where(IRPF_DATA.CONTRACT.in(personIds));
		// formatter:on

		create.delete(IRPF_DATA_ASCENDANTS)
				.where(IRPF_DATA_ASCENDANTS.IRPF_DATA.in(irpfSelect)).execute();
		create.delete(IRPF_DATA_DESCENDIENTS)
				.where(IRPF_DATA_DESCENDIENTS.IRPF_DATA.in(irpfSelect))
				.execute();

		create.delete(IRPF_REGULARIZATION)
				.where(IRPF_REGULARIZATION.CONTRACT.in(personIds)).execute();
		create.delete(IRPF_RESULT).where(IRPF_RESULT.CONTRACT.in(personIds))
				.execute();
		create.delete(IRPF_DATA).where(IRPF_DATA.CONTRACT.in(personIds))
				.execute();

		// ----------------------------------Salary------------------------------------------

		SelectConditionStep<Record1<Integer>> salariesSelect = create
				// formatter:off
				.select(SALARY.ID).from(SALARY)
				.where(SALARY.CONTRACT.in(personIds));
		// formatter:on

		create.delete(SALARY_PAYMENT)
				.where(SALARY_PAYMENT.SALARY.in(salariesSelect)).execute();
		create.delete(SALARY_EMBARGO)
				.where(SALARY_EMBARGO.SALARY.in(salariesSelect)).execute();
		create.delete(SALARY_DEDUCTION)
				.where(SALARY_DEDUCTION.SALARY.in(salariesSelect)).execute();
		create.delete(SALARY_DATA).where(SALARY_DATA.SALARY.in(salariesSelect))
				.execute();
		create.delete(SALARY_COST).where(SALARY_COST.SALARY.in(salariesSelect))
				.execute();
		create.delete(SALARY_BONUS)
				.where(SALARY_BONUS.SALARY.in(salariesSelect)).execute();

		create.delete(SALARY).where(SALARY.CONTRACT.in(personIds)).execute();

		// --------------------------------CERTIFICA2_BATCH------------------------------------

		Result<Record1<Integer>> certifica2DetailSelect = create
				// formatter:off
				.select(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH)
				.from(CERTIFICA2_BATCH_DETAIL)
				.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.in(personIds)).fetch();
		// formatter:on

		create.delete(CERTIFICA2_BATCH_DETAIL)
				.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.in(personIds))
				.execute();
		create.delete(CERTIFICA2_BATCH)
				.where(CERTIFICA2_BATCH_DETAIL.ID.in(certifica2DetailSelect))
				.execute();

		// ----------------------------------LEAVE_BATCH--------------------------------------

		Result<Record1<Integer>> leaveBatchDetail = create
				.select(LEAVE_BATCH_DETAIL.LEAVE_BATCH)
				.from(LEAVE_BATCH_DETAIL
						.join(CONTRACT_LEAVE_DETAIL)
						.on(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL
								.eq(CONTRACT_LEAVE_DETAIL.ID))
						.join(CONTRACT_LEAVE)
						.on(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.in(personIds)))
				.fetch();

		create.delete(LEAVE_BATCH_DETAIL)
				.where(LEAVE_BATCH_DETAIL.LEAVE_BATCH.in(leaveBatchDetail))
				.execute();
		create.delete(LEAVE_BATCH).where(LEAVE_BATCH.ID.in(leaveBatchDetail))
				.execute();

		// ----------------------------------Contract------------------------------------------

		create.delete(CONTRACT_ATTACH)
				.where(CONTRACT_ATTACH.CONTRACT.in(personIds)).execute();

		Result<Record1<Integer>> batchDetailSelect = create
				.select(CONTRACT_BATCH_DETAIL.CONTRACT_BATCH)
				.from(CONTRACT_BATCH_DETAIL)
				.where(CONTRACT_BATCH_DETAIL.CONTRACT.in(personIds)).fetch();

		create.delete(CONTRACT_BATCH_DETAIL)
				.where(CONTRACT_BATCH_DETAIL.CONTRACT.in(personIds)).execute();
		create.delete(CONTRACT_BATCH)
				.where(CONTRACT_BATCH.ID.in(batchDetailSelect)).execute();

		create.delete(CONTRACT_BONUS)
				.where(CONTRACT_BONUS.CONTRACT.in(personIds)).execute();
		create.delete(CONTRACT_CALENDAR_EVENT)
				.where(CONTRACT_CALENDAR_EVENT.CONTRACT.in(personIds))
				.execute();
		create.delete(CONTRACT_CLAUSE)
				.where(CONTRACT_CLAUSE.CONTRACT.in(personIds)).execute();
		create.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.in(personIds)).execute();
		create.delete(CONTRACT_DEDUCTION)
				.where(CONTRACT_DEDUCTION.CONTRACT.in(personIds)).execute();
		create.delete(CONTRACT_EMBARGO)
				.where(CONTRACT_EMBARGO.CONTRACT.in(personIds)).execute();
		create.delete(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.in(personIds)).execute();

		SelectConditionStep<Record1<Integer>> leaveDetailSelect = create
				.select(CONTRACT_LEAVE.ID).from(CONTRACT_LEAVE)
				.where(CONTRACT_LEAVE.CONTRACT.in(personIds));

		create.delete(CONTRACT_LEAVE_DETAIL)
				.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE
						.in(leaveDetailSelect)).execute();
		create.delete(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.in(personIds)).execute();

		create.delete(CONTRACT_PAYMENT)
				.where(CONTRACT_PAYMENT.CONTRACT.in(personIds)).execute();

		create.delete(CONTRACT).where(Contract.CONTRACT.ID.in(personIds))
				.execute();

		// ----------------------------------------------------------------------------------

	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}

}
