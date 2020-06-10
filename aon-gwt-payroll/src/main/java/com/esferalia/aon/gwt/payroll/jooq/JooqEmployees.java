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
import static com.esferalia.aon.jooq.tables.ContrataBatchDetail.CONTRATA_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.FsModel190Detail.FS_MODEL190_DETAIL;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.IrpfDataAscendants.IRPF_DATA_ASCENDANTS;
import static com.esferalia.aon.jooq.tables.IrpfDataDescendients.IRPF_DATA_DESCENDIENTS;
import static com.esferalia.aon.jooq.tables.IrpfRegularization.IRPF_REGULARIZATION;
import static com.esferalia.aon.jooq.tables.IrpfResult.IRPF_RESULT;
import static com.esferalia.aon.jooq.tables.LeaveBatch.LEAVE_BATCH;
import static com.esferalia.aon.jooq.tables.LeaveBatchDetail.LEAVE_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
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
import org.jooq.SelectOnConditionStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Category;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.jooq.tables.Contract;
import com.esferalia.aon.jooq.tables.Person;
import com.esferalia.aon.jooq.tables.records.ContractBonusRecord;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractDeductionRecord;
import com.esferalia.aon.jooq.tables.records.ContractInfoRecord;
import com.esferalia.aon.jooq.tables.records.ContractPaymentRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RattachRecord;
import com.esferalia.aon.jooq.tables.records.RbankRecord;
import com.esferalia.aon.jooq.tables.records.RmediaRecord;
import com.esferalia.aon.jooq.tables.records.RpaymethodRecord;

public class JooqEmployees {

	private static Settings SETTINGS = null;

	public static void insert2Person(Connection connection, Integer domain, Integer registry,
			String document) {

		DSLContext dslContext = DSL.using(connection, getDefaultSettings());

		try {

			Record person = dslContext.select().from(REGISTRY)
					.rightOuterJoin(PERSON)
					.on(PERSON.REGISTRY.eq(REGISTRY.ID))
					.where(REGISTRY.DOCUMENT.eq(document))
					.and(PERSON.REGISTRY.eq(registry))
					.fetchOne();
			
			List<RaddressRecord> listRAddress = dslContext.selectFrom(RADDRESS)
							.where(RADDRESS.REGISTRY.eq(registry))
							.fetchInto(RADDRESS);
			
			List<RattachRecord> listRAttach = dslContext.selectFrom(RATTACH)
					.where(RATTACH.REGISTRY.eq(registry))
					.fetchInto(RATTACH);
			
			List<RbankRecord> listRBank = dslContext.selectFrom(RBANK)
					.where(RBANK.REGISTRY.eq(registry))
					.fetchInto(RBANK);
			
			

			if (person != null) {

				int registryId = dslContext
						.insertInto(REGISTRY)
						.set(REGISTRY.DOMAIN, domain)
						.set(REGISTRY.DOCUMENT,
								person.getValue(REGISTRY.DOCUMENT))
						.set(REGISTRY.DOCUMENT_TYPE,
								person.getValue(REGISTRY.DOCUMENT_TYPE))
						.set(REGISTRY.DOCUMENT_COUNTRY,
								person.getValue(REGISTRY.DOCUMENT_COUNTRY))
						.set(REGISTRY.NAME, person.getValue(REGISTRY.NAME))
						.set(REGISTRY.ALIAS, person.getValue(REGISTRY.ALIAS))
						.set(REGISTRY.TYPE, person.getValue(REGISTRY.TYPE))
						.set(REGISTRY.NATIONALITY,
								person.getValue(REGISTRY.NATIONALITY))
						.set(REGISTRY.SECURITY_LEVEL,
								person.getValue(REGISTRY.SECURITY_LEVEL))
						.returning(REGISTRY.ID).fetchOne().getId();
				
				for (RaddressRecord rAddress : listRAddress) {
					
					int rAddressID = dslContext.insertInto(RADDRESS)
							.set(RADDRESS.DOMAIN, domain)
							.set(RADDRESS.REGISTRY, registryId)
							.set(RADDRESS.TYPE, rAddress.getValue(RADDRESS.TYPE))
							.set(RADDRESS.RECIPIENT, rAddress.getValue(RADDRESS.RECIPIENT))
							.set(RADDRESS.STREET_TYPE, rAddress.getValue(RADDRESS.STREET_TYPE))
							.set(RADDRESS.ADDRESS, rAddress.getValue(RADDRESS.ADDRESS))
							.set(RADDRESS.NUMBER, rAddress.getValue(RADDRESS.NUMBER))
							.set(RADDRESS.ADDRESS2, rAddress.getValue(RADDRESS.ADDRESS2))
							.set(RADDRESS.ADDRESS3, rAddress.getValue(RADDRESS.ADDRESS3))
							.set(RADDRESS.ZIP, rAddress.getValue(RADDRESS.ZIP))
							.set(RADDRESS.CITY, rAddress.getValue(RADDRESS.CITY))
							.set(RADDRESS.GEOZONE, rAddress.getValue(RADDRESS.GEOZONE))
							.set(RADDRESS.ALIAS, rAddress.getValue(RADDRESS.ALIAS))
							.set(RADDRESS.MUNICIPALITY_CODE, rAddress.getValue(RADDRESS.MUNICIPALITY_CODE))
							.returning(RADDRESS.ID).fetchOne().getId();
					
					List<RmediaRecord> rmediaList = dslContext
							.selectFrom(RMEDIA)
							.where(RMEDIA.REGISTRY.eq(registry)
									.and(RMEDIA.RADDRESS.eq(rAddress.getValue(RADDRESS.ID))))
									.fetchInto(RMEDIA);
					
					for (RmediaRecord rMedia : rmediaList) {
						
						dslContext.insertInto(RMEDIA)
						.set(RMEDIA.DOMAIN, domain)
						.set(RMEDIA.REGISTRY, registryId)
						.set(RMEDIA.MEDIA, rMedia.getValue(RMEDIA.MEDIA))
						.set(RMEDIA.VALUE, rMedia.getValue(RMEDIA.VALUE))
						.set(RMEDIA.COMMENT, rMedia.getValue(RMEDIA.COMMENT))
						.set(RMEDIA.ADMINISTRATIVE, rMedia.getValue(RMEDIA.ADMINISTRATIVE))
						.set(RMEDIA.COMMERCIAL, rMedia.getValue(RMEDIA.COMMERCIAL))
						.set(RMEDIA.TECHNICAL, rMedia.getValue(RMEDIA.TECHNICAL))
						.set(RMEDIA.RADDRESS, rAddressID)
						.execute();
					}
				}
				
				
				for (RattachRecord rAttach : listRAttach) {
					
					dslContext.insertInto(RATTACH)
					.set(RATTACH.DOMAIN, domain)
					.set(RATTACH.REGISTRY, registryId)
					.set(RATTACH.CATEGORY, rAttach.getValue(RATTACH.CATEGORY))
					.set(RATTACH.MIMETYPE, rAttach.getValue(RATTACH.MIMETYPE))
					.set(RATTACH.DESCRIPTION, rAttach.getValue(RATTACH.DESCRIPTION))
					.set(RATTACH.DATA, rAttach.getValue(RATTACH.DATA))
					.set(RATTACH.TYPE, rAttach.getValue(RATTACH.TYPE))
					.set(RATTACH.SCOPE, rAttach.getValue(RATTACH.SCOPE))
					.set(RATTACH.SECURITY_LEVEL, rAttach.getValue(RATTACH.SECURITY_LEVEL))
					.set(RATTACH.ATTACH_DATE, rAttach.getValue(RATTACH.ATTACH_DATE))
					.set(RATTACH.DRIVE_ID, rAttach.getValue(RATTACH.DRIVE_ID))
					.set(RATTACH.DPARENT_ID, rAttach.getValue(RATTACH.DPARENT_ID))
					.set(RATTACH.CREATION_USER, rAttach.getValue(RATTACH.CREATION_USER))
					.set(RATTACH.CREATION_DATE, rAttach.getValue(RATTACH.CREATION_DATE))
					.set(RATTACH.MODIFICATION_USER, rAttach.getValue(RATTACH.MODIFICATION_USER))
					.set(RATTACH.MODIFICATION_DATE, rAttach.getValue(RATTACH.MODIFICATION_DATE))
					.execute();
				}
				
				for (RbankRecord rBank : listRBank) {
					
					int rBankId = dslContext.insertInto(RBANK)
							.set(RBANK.DOMAIN, domain)
							.set(RBANK.REGISTRY, registryId)
							.set(RBANK.BANK_ACCOUNT, rBank.getValue(RBANK.BANK_ACCOUNT))
							.set(RBANK.BIC, rBank.getValue(RBANK.BIC))
							.set(RBANK.SUFIX, rBank.getValue(RBANK.SUFIX))
							.set(RBANK.ALIAS, rBank.getValue(RBANK.ALIAS))
							.set(RBANK.ACTIVE, rBank.getValue(RBANK.ACTIVE))
							.set(RBANK.ACCOUNT, rBank.getValue(RBANK.ACCOUNT))
							.returning(RBANK.ID).fetchOne().getId();
					
					
					List<RpaymethodRecord> listRPayMethod = dslContext.selectFrom(RPAYMETHOD)
							.where(RPAYMETHOD.REGISTRY.eq(registry)
									.and(RPAYMETHOD.RBANK.eq(rBank.getValue(RBANK.ID))))
							.fetchInto(RPAYMETHOD);
					
					for (RpaymethodRecord payMethod : listRPayMethod) {
						
						dslContext.insertInto(RPAYMETHOD)
						.set(RPAYMETHOD.DOMAIN, domain)
						.set(RPAYMETHOD.REGISTRY, registryId)
						.set(RPAYMETHOD.PAY_METHOD, payMethod.getValue(RPAYMETHOD.PAY_METHOD))						
						.set(RPAYMETHOD.RBANK, rBankId)
						.set(RPAYMETHOD.NUMBER_OF_PYMNTS, payMethod.getValue(RPAYMETHOD.NUMBER_OF_PYMNTS))
						.set(RPAYMETHOD.DAYS_TO_FIRST_PYMNT, payMethod.getValue(RPAYMETHOD.DAYS_TO_FIRST_PYMNT))
						.set(RPAYMETHOD.DAYS_BETWEEN_PYMNTS, payMethod.getValue(RPAYMETHOD.DAYS_BETWEEN_PYMNTS))
						.set(RPAYMETHOD.PYMNT_DAYS, payMethod.getValue(RPAYMETHOD.PYMNT_DAYS))
						.execute();
						
					}
				}
				

				dslContext
						.insertInto(PERSON)
						.set(PERSON.REGISTRY, registryId)
						.set(PERSON.DOMAIN, domain)
						.set(PERSON.BIRTH_DATE,
								person.getValue(PERSON.BIRTH_DATE))
						.set(PERSON.GENDER, person.getValue(PERSON.GENDER))
						.set(PERSON.MARITAL_STATUS,
								person.getValue(PERSON.MARITAL_STATUS))
						.set(PERSON.SOCIAL_SECURITY_NUM,
								person.getValue(PERSON.SOCIAL_SECURITY_NUM))
						.set(PERSON.NAME, person.getValue(PERSON.NAME))
						.set(PERSON.FIRST_SURNAME,
								person.getValue(PERSON.FIRST_SURNAME))
						.set(PERSON.SECOND_SURNAME,
								person.getValue(PERSON.SECOND_SURNAME))
						.execute();

			}

		} catch (Exception ex) {
			throw new IllegalArgumentException();
		}
	}

	public static Employee getEmployee(Connection connection,
			Integer employeeId ) throws SQLException {
		return getEmployee(DSL.using(connection, getDefaultSettings()),
				employeeId);
	}

	public static List<Employee> getEmployees(Connection connection,
			Integer workplaceId, Date endDate, String pattern, int offset,
			int limit) throws SQLException {
		return getEmployees(DSL.using(connection, getDefaultSettings()),
				workplaceId, endDate, pattern, offset, limit);
	}

	private static Employee getEmployee(DSLContext context,
			Integer employeeId) throws SQLException {
		Cursor<Record> cursor = null;

		try {
			// @formatter:off
			SelectConditionStep<Record> select = 
					getEmployeeSelect(context)
					.where(CONTRACT.ID.eq(employeeId))
					
					;


			cursor = select
					.fetchLazy();
			// @formatter:on

			for (Record record : cursor) 
				return newEmployee(record, context);

			return null;

		} finally {
			if (cursor != null)
				cursor.close();
		}
	}


	private static List<Employee> getEmployees(DSLContext context,
			Integer workplaceId, Date endDate, String pattern, int offset,
			int limit) throws SQLException {
		Cursor<Record> cursor = null;

		try {
			Integer month = endDate.getMonth();
			Integer year = endDate.getYear();
			month--;
			if(month < 0) {
				month = 11;
				year--;
			}
			Date newEndDate = new Date(year, month, 1);
			
			// @formatter:off
			SelectConditionStep<Record> select = 
					getEmployeeSelect(context)
//					.where(CONTRACT.ID.ge(0))
					.where(CONTRACT.WORKPLACE.eq(workplaceId))
					.and(CONTRACT.END_DATE.isNull().or(
							CONTRACT.END_DATE.greaterOrEqual(new java.sql.Date(
									newEndDate.getTime()))));

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
				employees.add(newEmployee(record, context));
			}
			
			// Check if has Salaries
			for(Employee employee : employees) {
				hasPayroll(context, employee);
			}

			return employees;

		} finally {
			if (cursor != null)
				cursor.close();
		}
	}
	
	private static void hasPayroll(DSLContext context, Employee employee) {
		Result<Record> salaryRecords = context.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(employee.getId()))
				.fetch();
		
		if(salaryRecords.isEmpty())
			employee.setHasSalaries(false);
		else
			employee.setHasSalaries(true);
	}

	private static SelectOnConditionStep<Record> getEmployeeSelect(DSLContext context) {
		return context
				.select()
				.from(CONTRACT.join(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY)))
				.leftOuterJoin(REGISTRY).on(REGISTRY.ID.eq(PERSON.REGISTRY))
				.leftOuterJoin(AGREEMENT_LEVEL.join(AGREEMENT).on(AGREEMENT_LEVEL.AGREEMENT.eq(AGREEMENT.ID))).on(CONTRACT.AGREEMENT_LEVEL.eq(AGREEMENT_LEVEL.ID));

//				.leftOuterJoin(AGREEMENT_LEVEL_CATEGORY.join(AGREEMENT_LEVEL.join(AGREEMENT).on(AGREEMENT_LEVEL.AGREEMENT.eq(AGREEMENT.ID))).on(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL.eq(AGREEMENT_LEVEL.ID))).on(CONTRACT.AGREEMENT_LEVEL.eq(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL));
	}

	private static Employee newEmployee(Record record, DSLContext dslContext) {
		Employee employee = new Employee();

		employee.setId(record.getValue(CONTRACT.ID));
		employee.setStartDate(record.getValue(CONTRACT.START_DATE));
		employee.setEndDate(record.getValue(CONTRACT.END_DATE));
		employee.setSeniorityDate(record.getValue(CONTRACT.SENIORITY_DATE));

		employee.setPerson(record.getValue(PERSON.REGISTRY));
		employee.setName(record.getValue(PERSON.NAME));
		employee.setFirstSurname(record.getValue(PERSON.FIRST_SURNAME));
		employee.setSecondSurName(record
				.getValue(PERSON.SECOND_SURNAME));
		employee.setDocument(record.getValue(REGISTRY.DOCUMENT));
		Integer levelId = record
				.getValue(AGREEMENT_LEVEL.ID);
		if (levelId != null) {
			Category category = new Category();
			//category.setId(categoryId);
			category.setLevelId(levelId);
			category.setLevel(record
					.getValue(AGREEMENT_LEVEL.DESCRIPTION));
			category.setDescription(record
					.getValue(CONTRACT.CATEGORY_DESCRIPTION));

			Agreement agreement = new Agreement();
			agreement.setId(record.getValue(AGREEMENT.ID));
			agreement.setDomain(record
					.getValue(AGREEMENT.DOMAIN));
			agreement.setDescription(record
					.getValue(AGREEMENT.DESCRIPTION));

			category.setAgreement(agreement);
			employee.setCategory(category);
		}
		
		Result<Record> contractDataRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(record.getValue(CONTRACT.ID)))
				.and(CONTRACT_DATA.NAME.eq("TC2")).orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
		if(contractDataRecords.isEmpty())
			employee.setContractType(null);
		else {
			String contractType = contractDataRecords.get(0).get(CONTRACT_DATA.EXPRESSION);
			employee.setContractType(contractType.contains("\"") ? contractType.split("\"")[1] : contractType);
		}
		
		return employee;
	}

	public static List<Employee> getTrashEmployees(Connection conn,
			int workplaceId) {
		return getTrashEmployees(DSL.using(conn, getDefaultSettings()),
				workplaceId);
	}

	private static List<Employee> getTrashEmployees(DSLContext context,
			int workplaceId) {

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
					.on(CONTRACT.AGREEMENT_LEVEL
							.eq(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL))
					.where(CONTRACT.WORKPLACE.eq(workplaceId))
					.and(CONTRACT.ID.lessThan(0));

			cursor = select.orderBy(PERSON.FIRST_SURNAME.asc(),
					PERSON.SECOND_SURNAME.asc(), PERSON.NAME.asc(),
					CONTRACT.START_DATE.desc()).fetchLazy();
			// @formatter:on

			List<Employee> employees = new LinkedList<Employee>();

			for (Record record : cursor) {
				Employee employee = new Employee();

				employee.setId(record.getValue(CONTRACT.ID));
				employee.setStartDate(record.getValue(CONTRACT.START_DATE));
				employee.setEndDate(record.getValue(CONTRACT.END_DATE));
				employee.setSeniorityDate(record.getValue(CONTRACT.SENIORITY_DATE));

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

	private static Employee getDataEmployee(DSLContext create, Integer domain, String document,
			Date startDate, Date endDate) {

		// @formatter:off
		 Result<Record> person = create.select().from(Person.PERSON).join(REGISTRY)
				.on(PERSON.REGISTRY.eq(REGISTRY.ID))
				.where(REGISTRY.DOCUMENT.eq(document))
				.and(PERSON.DOMAIN.eq(domain))
				.fetch();
		// @formatter:on

		Employee employee = new Employee();

		employee.setStartDate(startDate);
		employee.setEndDate(endDate);
		employee.setDocument(person.get(0).getValue(REGISTRY.DOCUMENT));
		employee.setPerson(person.get(0).getValue(PERSON.REGISTRY));
		employee.setName(person.get(0).getValue(PERSON.NAME));
		employee.setFirstSurname(person.get(0).getValue(PERSON.FIRST_SURNAME));
		employee.setSecondSurName(person.get(0).getValue(PERSON.SECOND_SURNAME));

		return employee;

	}

	public static Employee paste(Connection conn, int domain, int workplaceId,
			int contractId, String document, Date startDate, Date endDate,
			boolean check) throws SQLException {

		DSLContext create = DSL.using(conn, SQLDialect.MYSQL,
				getDefaultSettings());

		Employee employee = getDataEmployee(create, domain, document, startDate,
				endDate);

		int newContractId = insert2Contract(create, domain, contractId, workplaceId,
				employee);
		insert2ContractBonus(create, domain, contractId, newContractId, startDate,
				endDate);
		insert2ContractData(create, domain, contractId, newContractId, startDate,
				endDate);
		insert2ContractInfo(create, domain, contractId, newContractId, startDate,
				endDate);

		if (check) {
			/**
			 * ContractDeduction ContractPayment
			 */
			insert2ContractDeduction(create, domain, contractId, newContractId,
					startDate, endDate);
			insert2ContractPayment(create, domain, contractId, newContractId,
					startDate, endDate);

		}

		employee.setId(newContractId);

		return employee;

	}

	protected static int insert2Contract(DSLContext create, Integer domain, int contractId,
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
				.on(CONTRACT.AGREEMENT_LEVEL
						.eq(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL))
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

				.set(CONTRACT.DOMAIN, domain)
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
				.set(CONTRACT.AGREEMENT_LEVEL,
						contractRecord
								.getValue(CONTRACT.AGREEMENT_LEVEL))
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

	protected static void insert2ContractBonus(DSLContext create, Integer domain,
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

						.set(CONTRACT_BONUS.DOMAIN, domain)
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

	protected static void insert2ContractData(DSLContext create, Integer domain,
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
						.set(CONTRACT_DATA.DOMAIN, domain)
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

	protected static void insert2ContractInfo(DSLContext create, Integer domain,
			int contractId, int newContractId, Date startDate, Date endDate) {

		InsertSetMoreStep<ContractInfoRecord> insert;

		List<ContractInfoRecord> infos = create.selectFrom(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.fetchInto(CONTRACT_INFO);

		if (infos != null) {

			for (ContractInfoRecord info : infos) {

				insert = create
						.insertInto(CONTRACT_INFO)
						.set(CONTRACT_INFO.DOMAIN, domain)
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

	protected static void insert2ContractDeduction(DSLContext create, Integer domain,
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

						.set(CONTRACT_DEDUCTION.DOMAIN, domain)
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

	protected static void insert2ContractPayment(DSLContext create, Integer domain,
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

						.set(CONTRACT_PAYMENT.DOMAIN, domain)
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

	public static void moveContractId(Connection conn, final Integer personId) {

		try {
			Statement sOpen = conn.createStatement();
			sOpen.execute(SET_FOREIGN_KEY_CHECKS_0);
			System.out.println("Claves referenciales desactivadas");

			// ------------------------------------------------------

			DSLContext dslContext = DSL.using(conn, SQLDialect.MYSQL,
					getDefaultSettings());

			dslContext
					.update(CERTIFICA2_BATCH_DETAIL)
					.set(CERTIFICA2_BATCH_DETAIL.ID,
							CERTIFICA2_BATCH_DETAIL.ID.mul(-1))
					.set(CERTIFICA2_BATCH_DETAIL.CONTRACT,
							CERTIFICA2_BATCH_DETAIL.CONTRACT.mul(-1))
					.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.eq(personId))
					.execute();

			dslContext
					.update(CONTRACT_ATTACH)
					.set(CONTRACT_ATTACH.ID, CONTRACT_ATTACH.ID.mul(-1))
					.set(CONTRACT_ATTACH.CONTRACT,
							CONTRACT_ATTACH.CONTRACT.mul(-1))
					.where(CONTRACT_ATTACH.CONTRACT.eq(personId)).execute();

			dslContext
					.update(CONTRACT_BATCH_DETAIL)
					.set(CONTRACT_BATCH_DETAIL.ID,
							CONTRACT_BATCH_DETAIL.ID.mul(-1))
					.set(CONTRACT_BATCH_DETAIL.CONTRACT,
							CONTRACT_BATCH_DETAIL.CONTRACT.mul(-1))
					.where(CONTRACT_BATCH_DETAIL.CONTRACT.eq(personId))
					.execute();

			dslContext
					.update(CONTRACT_BONUS)
					.set(CONTRACT_BONUS.ID, CONTRACT_BONUS.ID.mul(-1))
					.set(CONTRACT_BONUS.CONTRACT,
							CONTRACT_BONUS.CONTRACT.mul(-1))
					.where(CONTRACT_BONUS.CONTRACT.eq(personId)).execute();

			dslContext
					.update(CONTRACT_CALENDAR_EVENT)
					.set(CONTRACT_CALENDAR_EVENT.ID,
							CONTRACT_CALENDAR_EVENT.ID.mul(-1))
					.set(CONTRACT_CALENDAR_EVENT.CONTRACT,
							CONTRACT_CALENDAR_EVENT.CONTRACT.mul(-1))
					.where(CONTRACT_CALENDAR_EVENT.CONTRACT.eq(personId))
					.execute();

			dslContext
					.update(CONTRACT_CLAUSE)
					.set(CONTRACT_CLAUSE.ID, CONTRACT_CLAUSE.ID.mul(-1))

					.set(CONTRACT_CLAUSE.CONTRACT,
							(CONTRACT_CLAUSE.CONTRACT != null) ? CONTRACT_CLAUSE.CONTRACT
									.mul(-1) : CONTRACT_CLAUSE.CONTRACT)

					.where(CONTRACT_CLAUSE.CONTRACT.eq(personId)).execute();

			dslContext
					.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.ID, CONTRACT_DATA.ID.mul(-1))
					.set(CONTRACT_DATA.CONTRACT, CONTRACT_DATA.CONTRACT.mul(-1))
					.where(CONTRACT_DATA.CONTRACT.eq(personId)).execute();

			dslContext
					.update(CONTRACT_DEDUCTION)
					.set(CONTRACT_DEDUCTION.ID, CONTRACT_DEDUCTION.ID.mul(-1))
					.set(CONTRACT_DEDUCTION.CONTRACT,
							CONTRACT_DEDUCTION.CONTRACT.mul(-1))
					.where(CONTRACT_DEDUCTION.CONTRACT.eq(personId)).execute();

			// ------------------------------------------- SALARY TABLES

			SelectConditionStep<Record1<Integer>> salaryId = dslContext
					.select(SALARY.ID).from(SALARY)
					.where(SALARY.CONTRACT.eq(personId));

			dslContext.update(SALARY_BONUS)
					.set(SALARY_BONUS.ID, SALARY_BONUS.ID.mul(-1))
					.set(SALARY_BONUS.SALARY, SALARY_BONUS.SALARY.mul(-1))
					.where(SALARY_BONUS.SALARY.in(salaryId)).execute();

			dslContext.update(SALARY_COST)
					.set(SALARY_COST.ID, SALARY_COST.ID.mul(-1))
					.set(SALARY_COST.SALARY, SALARY_COST.SALARY.mul(-1))
					.where(SALARY_COST.SALARY.in(salaryId)).execute();

			dslContext.update(SALARY_DATA)
					.set(SALARY_DATA.ID, SALARY_DATA.ID.mul(-1))
					.set(SALARY_DATA.SALARY, SALARY_DATA.SALARY.mul(-1))
					.where(SALARY_DATA.SALARY.in(salaryId)).execute();

			dslContext
					.update(SALARY_DEDUCTION)
					.set(SALARY_DEDUCTION.ID, SALARY_DEDUCTION.ID.mul(-1))
					.set(SALARY_DEDUCTION.SALARY,
							SALARY_DEDUCTION.SALARY.mul(-1))
					.where(SALARY_DEDUCTION.SALARY.in(salaryId)).execute();

			dslContext.update(SALARY_EMBARGO)
					.set(SALARY_EMBARGO.ID, SALARY_EMBARGO.ID.mul(-1))
					.set(SALARY_EMBARGO.SALARY, SALARY_EMBARGO.SALARY.mul(-1))
					.where(SALARY_EMBARGO.SALARY.in(salaryId)).execute();

			dslContext.update(SALARY_PAYMENT)
					.set(SALARY_PAYMENT.ID, SALARY_PAYMENT.ID.mul(-1))
					.set(SALARY_PAYMENT.SALARY, SALARY_PAYMENT.SALARY.mul(-1))
					.where(SALARY_PAYMENT.SALARY.in(salaryId)).execute();

			dslContext.update(SALARY).set(SALARY.ID, SALARY.ID.mul(-1))
					.set(SALARY.CONTRACT, SALARY.CONTRACT.mul(-1))
					.where(SALARY.CONTRACT.eq(personId)).execute();

			// -----------------------------------------------------------

			dslContext
					.update(CONTRACT_EMBARGO)
					.set(CONTRACT_EMBARGO.ID, CONTRACT_EMBARGO.ID.mul(-1))
					.set(CONTRACT_EMBARGO.CONTRACT,
							CONTRACT_EMBARGO.CONTRACT.mul(-1))
					.where(CONTRACT_EMBARGO.CONTRACT.eq(personId)).execute();

			dslContext
					.update(CONTRACT_INFO)
					.set(CONTRACT_INFO.ID, CONTRACT_INFO.ID.mul(-1))

					.set(CONTRACT_INFO.CONTRACT,
							(CONTRACT_INFO.CONTRACT != null) ? CONTRACT_INFO.CONTRACT
									.mul(-1) : CONTRACT_INFO.CONTRACT)

					.where(CONTRACT_INFO.CONTRACT.eq(personId)).execute();

			SelectConditionStep<Record1<Integer>> contractLeaveId = dslContext
					.select(CONTRACT_LEAVE.ID).from(CONTRACT_LEAVE)
					.where(CONTRACT_LEAVE.CONTRACT.eq(personId));

			SelectConditionStep<Record1<Integer>> contractLeaveDetailId = dslContext
					.select(CONTRACT_LEAVE_DETAIL.ID)
					.from(CONTRACT_LEAVE_DETAIL)
					.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE
							.eq(contractLeaveId));

			dslContext
					.update(LEAVE_BATCH_DETAIL)
					.set(LEAVE_BATCH_DETAIL.ID, LEAVE_BATCH_DETAIL.ID.mul(-1))
					.set(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL,
							LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL.mul(-1))
					.where(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL
							.in(contractLeaveDetailId)).execute();

			dslContext
					.update(CONTRACT_LEAVE_DETAIL)
					.set(CONTRACT_LEAVE_DETAIL.ID,
							CONTRACT_LEAVE_DETAIL.ID.mul(-1))
					.set(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE,
							CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.mul(-1))
					.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE
							.in(contractLeaveId)).execute();

			dslContext
					.update(CONTRACT_LEAVE)
					.set(CONTRACT_LEAVE.ID, CONTRACT_LEAVE.ID.mul(-1))
					.set(CONTRACT_LEAVE.CONTRACT,
							CONTRACT_LEAVE.CONTRACT.mul(-1))
					.where(CONTRACT_LEAVE.CONTRACT.eq(personId)).execute();

			dslContext
					.update(CONTRACT_PAYMENT)
					.set(CONTRACT_PAYMENT.ID, CONTRACT_PAYMENT.ID.mul(-1))
					.set(CONTRACT_PAYMENT.CONTRACT,
							CONTRACT_PAYMENT.CONTRACT.mul(-1))
					.where(CONTRACT_PAYMENT.CONTRACT.eq(personId)).execute();

			dslContext
					.update(CONTRATA_BATCH_DETAIL)
					.set(CONTRATA_BATCH_DETAIL.ID,
							CONTRATA_BATCH_DETAIL.ID.mul(-1))
					.set(CONTRATA_BATCH_DETAIL.CONTRACT,
							CONTRATA_BATCH_DETAIL.CONTRACT.mul(-1))
					.where(CONTRATA_BATCH_DETAIL.CONTRACT.eq(personId))
					.execute();

			dslContext
					.update(FS_MODEL190_DETAIL)
					.set(FS_MODEL190_DETAIL.ID, FS_MODEL190_DETAIL.ID.mul(-1))
					.set(FS_MODEL190_DETAIL.CONTRACT,
							FS_MODEL190_DETAIL.CONTRACT.mul(-1))
					.where(FS_MODEL190_DETAIL.CONTRACT.eq(personId.byteValue()))
					.execute();

			SelectConditionStep<Record1<Integer>> irpfDataId = dslContext
					.select(IRPF_DATA.ID).from(IRPF_DATA)
					.where(IRPF_DATA.CONTRACT.eq(personId));

			dslContext
					.update(IRPF_DATA_ASCENDANTS)
					.set(IRPF_DATA_ASCENDANTS.ID,
							IRPF_DATA_ASCENDANTS.ID.mul(-1))
					.set(IRPF_DATA_ASCENDANTS.IRPF_DATA,
							IRPF_DATA_ASCENDANTS.IRPF_DATA.mul(-1))
					.where(IRPF_DATA_ASCENDANTS.IRPF_DATA.in(irpfDataId))
					.execute();

			dslContext
					.update(IRPF_DATA_DESCENDIENTS)
					.set(IRPF_DATA_DESCENDIENTS.ID,
							IRPF_DATA_DESCENDIENTS.ID.mul(-1))
					.set(IRPF_DATA_DESCENDIENTS.IRPF_DATA,
							IRPF_DATA_DESCENDIENTS.IRPF_DATA.mul(-1))
					.where(IRPF_DATA_DESCENDIENTS.IRPF_DATA.in(irpfDataId))
					.execute();

			dslContext.update(IRPF_DATA)
					.set(IRPF_DATA.ID, IRPF_DATA.ID.mul(-1))
					.set(IRPF_DATA.CONTRACT, IRPF_DATA.CONTRACT.mul(-1))
					.where(IRPF_DATA.CONTRACT.eq(personId)).execute();

			dslContext
					.update(IRPF_REGULARIZATION)
					.set(IRPF_REGULARIZATION.ID, IRPF_REGULARIZATION.ID.mul(-1))
					.set(IRPF_REGULARIZATION.CONTRACT,
							IRPF_REGULARIZATION.CONTRACT.mul(-1))
					.where(IRPF_REGULARIZATION.CONTRACT.eq(personId)).execute();

			dslContext.update(IRPF_RESULT)
					.set(IRPF_RESULT.ID, IRPF_RESULT.ID.mul(-1))
					.set(IRPF_RESULT.CONTRACT, IRPF_RESULT.CONTRACT.mul(-1))
					.where(IRPF_RESULT.CONTRACT.eq(personId)).execute();

			dslContext.update(CONTRACT).set(CONTRACT.ID, CONTRACT.ID.mul(-1))
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
