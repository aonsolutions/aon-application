package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FsModel.FS_MODEL;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryBonus.SALARY_BONUS;
import static com.esferalia.aon.jooq.tables.SalaryCost.SALARY_COST;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryDeduction.SALARY_DEDUCTION;
import static com.esferalia.aon.jooq.tables.SalaryEmbargo.SALARY_EMBARGO;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo.AlcatrazPeriod;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo.AlcatrazTerritory;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.jooq.tables.records.AlcatrazRecord;
import com.esferalia.aon.jooq.tables.records.FsModelRecord;

public class JooqPayrollSalaries {

	private static Settings settings = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	private JooqPayrollSalaries() {
		super();
	}

	// --------------------------------------------------------------------------------------------
	//									SALARY METHODS
	// --------------------------------------------------------------------------------------------
	
	public static Period getSalariesDates(Connection connection, Integer domainId, Integer userId, SalaryInfoFilter filter) {
		return getSalariesDatesDB(DSL.using(connection, getDefaultSettings()), domainId, userId, filter);
	}
	
	public static List<SalaryInfo> getSalaries(Connection connection, Integer domainId, Integer userId, SalaryInfoFilter filter) {
		return getSalariesDB(DSL.using(connection, getDefaultSettings()), domainId, userId, filter);
	}
	
	public static SalaryInfo getSalariesDateEnd(Connection connection, Integer domainId, Integer userId, SalaryInfoFilter filter) {
		return getSalariesDateEnd(DSL.using(connection, getDefaultSettings()), domainId, userId, filter);
	}
	
	public static List<SalaryInfo> getSalariesByDocument(Connection connection, SalaryInfoFilter filter,  String document) {
		return getSalariesByDocumentDB(DSL.using(connection, getDefaultSettings()), filter, document);
	}
	
	// --------------------------------------------------------------------------------------------
	//									WORKPLACE METHODS
	// --------------------------------------------------------------------------------------------
	
	public static WorkplaceEmployees getWorkplaceActiveEmployees(Connection connection, Integer domainId, Integer userId, Integer workplaceId) {
		return getWorkplaceActiveEmployeesDB(DSL.using(connection, getDefaultSettings()), domainId, userId, workplaceId);
	}
	
	// --------------------------------------------------------------------------------------------
	//									ENTERPRISE METHODS
	// --------------------------------------------------------------------------------------------
	
	public static List<EmployeeInfo> getEnterpriseActiveEmployees(Connection connection, Integer domainId, Integer userId, Integer enterpriseId) {
		return getEnterpriseActiveEmployeesDB(DSL.using(connection, getDefaultSettings()), domainId, userId, enterpriseId);
	}
	
	// --------------------------------------------------------------------------------------------
	//									DELETE SALARY METHOD
	// --------------------------------------------------------------------------------------------

	public static void deleteSalaries(Connection connection, List<Integer> ids) {
		deleteSalariesDB(DSL.using(connection, getDefaultSettings()), ids);
	}

	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------
	
	// --------------------------------------------------------------------------------------------
	//									SALARY METHODS IMPL
	// --------------------------------------------------------------------------------------------
	
	private static Period getSalariesDatesDB(DSLContext dslContext, Integer domainId, Integer userId, SalaryInfoFilter filter) {
		// SalaryType
		Condition salaryTypeCondition = getSalaryTypeCondition(filter);
		
		// Contracts
		Condition contractsCondition = getContractCondition(dslContext, domainId, userId, filter);
		
		Record1<Date> salaryMinDateRecord = dslContext.select(DSL.min(SALARY.END_DATE)).from(SALARY)
				.join(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.join(ENTERPRISE).on(ENTERPRISE.REGISTRY.eq(WORKPLACE.ENTERPRISE))
				.where(contractsCondition)
				.and(salaryTypeCondition)
				.fetchOne();
		
		Date salaryMinDate = salaryMinDateRecord.value1();
		
		Record1<Date> salaryMaxDateRecord = dslContext.select(DSL.max(SALARY.END_DATE)).from(SALARY)
				.join(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.join(ENTERPRISE).on(ENTERPRISE.REGISTRY.eq(WORKPLACE.ENTERPRISE))
				.where(contractsCondition)
				.and(salaryTypeCondition)
				.fetchOne();
		
		Date salaryMaxDate = salaryMaxDateRecord.value1();
		
		return new Period(parseStartToJavaDate(salaryMinDate), parseEndToJavaDate(salaryMaxDate));
	}
	
	private static java.util.Date parseStartToJavaDate(Date sqlDate) {
		return null == sqlDate ? DateUtils.getFirstDayOfYear() : new java.util.Date(sqlDate.getTime());
	}
	
	private static java.util.Date parseEndToJavaDate(Date sqlDate) {
		return null == sqlDate ? DateUtils.getLastDayOfYear(DateUtils.getFirstDayOfYear()) : new java.util.Date(sqlDate.getTime());
	}

	private static List<SalaryInfo> getSalariesDB(DSLContext dslContext, Integer domainId, Integer userId, SalaryInfoFilter filter) {
		List<SalaryInfo> salaries = new ArrayList<>();
		
		// SalaryType
		Condition salaryTypeCondition = getSalaryTypeCondition(filter);
		
		// Contracts
		Condition contractsCondition = getContractCondition(dslContext, domainId, userId, filter);
		
		// Dates
		Condition datesCondition = getDatesCondition(filter);
		
		// Get salaries
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.join(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.join(ENTERPRISE).on(ENTERPRISE.REGISTRY.eq(WORKPLACE.ENTERPRISE))
				.leftOuterJoin(FINANCE).on(FINANCE.SOURCE_ID.eq(SALARY.ID).and(FINANCE.DOMAIN.eq(SALARY.DOMAIN)))
				.where(SALARY.DOMAIN.eq(domainId))
				.and(contractsCondition)
				.and(salaryTypeCondition)
				.and(datesCondition)
				.orderBy(SALARY.END_DATE.desc())
				.fetch();
		
		for(Record salaryRecord : salaryRecords) {
			
			SalaryInfo salaryInfo = new SalaryInfo();
			salaryInfo.setId(salaryRecord.get(SALARY.ID));
			salaryInfo.setDomain(salaryRecord.get(SALARY.DOMAIN));
			salaryInfo.setContract(salaryRecord.get(SALARY.CONTRACT));
			salaryInfo.setStartDate(salaryRecord.get(SALARY.START_DATE));
			salaryInfo.setEndDate(salaryRecord.get(SALARY.END_DATE));
			salaryInfo.setType(Salary.Type.values()[salaryRecord.get(SALARY.TYPE)]);
			salaryInfo.setEnterpriseName(salaryRecord.get(SALARY.ENTERPRISE_NAME));
			salaryInfo.setEmployeeName(salaryRecord.get(SALARY.EMPLOYEE_NAME));
			salaryInfo.setTotalPayment(salaryRecord.get(SALARY.TOTAL_PAYMENT));
			salaryInfo.setTotalDeduction(salaryRecord.get(SALARY.TOTAL_DEDUCTION));
			salaryInfo.setTotalLiquid(salaryRecord.get(SALARY.TOTAL_LIQUID));
			
			Integer contractId = salaryRecord.get(SALARY.CONTRACT);

			Integer enterpriseId = getEnterpriseId(dslContext, contractId);
			
			Record workplaceRecord = getWorkplaceRecord(dslContext, contractId);
			
			String workplaceName = workplaceRecord.get(WORKPLACE.DESCRIPTION);
			Integer workplaceId =  workplaceRecord.get(WORKPLACE.ID);
			
			// Workplace
			salaryInfo.setWorkplaceName(workplaceName);
			salaryInfo.setWorkplaceId(workplaceId);
			
			// Enterprise ID
			salaryInfo.setEnterpriseId(enterpriseId);
			
			salaryInfo.setFinance(null != salaryRecord.get(FINANCE.ID));
			
			//Is Alcatraz
			Result<AlcatrazRecord> alcatrazRecords = dslContext.selectFrom(ALCATRAZ).where(ALCATRAZ.SALARY.eq(salaryInfo.getId())).fetch();
			if(!alcatrazRecords.isEmpty()) {
				salaryInfo.setAlcatraz(true);
				
				FsModelRecord fsModelRecord = dslContext.selectFrom(FS_MODEL).where(FS_MODEL.ID.eq(alcatrazRecords.get(0).getFsModel())).fetchOne();
				salaryInfo.setAlcatrazYear(fsModelRecord.getYear());
				salaryInfo.setAlcatrazPeriod(AlcatrazPeriod.values()[fsModelRecord.getPeriod()]);
				salaryInfo.setAlcatrazTerritory(AlcatrazTerritory.values()[fsModelRecord.getAdministration()]);
				
			} else salaryInfo.setAlcatraz(false);
			
			// Add to salaries list
			salaries.add(salaryInfo);

		}
		
		return salaries;
	}
	
	private static SalaryInfo getSalariesDateEnd(DSLContext dslContext, Integer domainId, Integer userId, SalaryInfoFilter filter) {
		
		// SalaryType
		Condition salaryTypeCondition = getSalaryTypeCondition(filter);
		
		// Contracts
		Condition contractsCondition = getContractCondition(dslContext, domainId, userId, filter);
		
		// Dates
		Condition datesCondition = getDatesCondition(filter);
		SalaryInfo salaryInfo = new SalaryInfo();
		// Get end salary
		 Record salaryRecord = dslContext.select().from(SALARY)
				.join(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.join(ENTERPRISE).on(ENTERPRISE.REGISTRY.eq(WORKPLACE.ENTERPRISE))
				.leftOuterJoin(FINANCE).on(FINANCE.SOURCE_ID.eq(SALARY.ID).and(FINANCE.DOMAIN.eq(SALARY.DOMAIN)))
				.where(SALARY.DOMAIN.eq(domainId))
				.and(contractsCondition)
				.and(salaryTypeCondition)
				.and(datesCondition)
				.orderBy(SALARY.END_DATE.desc())
				.limit(1)
				.fetchOne(); 
		if(salaryRecord!=null && salaryRecord.size() > 0) {
			salaryInfo.setId(salaryRecord.get(SALARY.ID));
			salaryInfo.setDomain(salaryRecord.get(SALARY.DOMAIN));
			salaryInfo.setContract(salaryRecord.get(SALARY.CONTRACT));
			salaryInfo.setStartDate(salaryRecord.get(SALARY.START_DATE));
			salaryInfo.setEndDate(salaryRecord.get(SALARY.END_DATE));
			salaryInfo.setType(Salary.Type.values()[salaryRecord.get(SALARY.TYPE)]);
			salaryInfo.setEnterpriseName(salaryRecord.get(SALARY.ENTERPRISE_NAME));
			salaryInfo.setEmployeeName(salaryRecord.get(SALARY.EMPLOYEE_NAME));
			salaryInfo.setTotalPayment(salaryRecord.get(SALARY.TOTAL_PAYMENT));
			salaryInfo.setTotalDeduction(salaryRecord.get(SALARY.TOTAL_DEDUCTION));
			salaryInfo.setTotalLiquid(salaryRecord.get(SALARY.TOTAL_LIQUID));
			
			salaryInfo.setFinance(null != salaryRecord.get(FINANCE.ID));
			
			//Is Alcatraz
			Result<AlcatrazRecord> alcatrazRecords = dslContext.selectFrom(ALCATRAZ).where(ALCATRAZ.SALARY.eq(salaryInfo.getId())).fetch();
			if(!alcatrazRecords.isEmpty()) {
				salaryInfo.setAlcatraz(true);
				
				FsModelRecord fsModelRecord = dslContext.selectFrom(FS_MODEL).where(FS_MODEL.ID.eq(alcatrazRecords.get(0).getFsModel())).fetchOne();
				salaryInfo.setAlcatrazYear(fsModelRecord.getYear());
				salaryInfo.setAlcatrazPeriod(AlcatrazPeriod.values()[fsModelRecord.getPeriod()]);
				salaryInfo.setAlcatrazTerritory(AlcatrazTerritory.values()[fsModelRecord.getAdministration()]);
				
			} else salaryInfo.setAlcatraz(false);
		}
		return salaryInfo;
	}
	
	
	private static List<SalaryInfo> getSalariesByDocumentDB(DSLContext dslContext, SalaryInfoFilter filter,  String document) {
		List<SalaryInfo> salaries = new ArrayList<>();
		// SalaryType
		Condition salaryTypeCondition = getSalaryTypeCondition(filter);
		// Dates
		Condition datesCondition = getDatesCondition(filter);
		
		// Get salaries
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.innerJoin(CONTRACT)
				.on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.innerJoin(REGISTRY)
				.on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.leftOuterJoin(FINANCE).on(FINANCE.SOURCE_ID.eq(SALARY.ID).and(FINANCE.DOMAIN.eq(SALARY.DOMAIN)))
				.where(REGISTRY.DOMAIN.eq(filter.getWorkplaceId()))
				.and(REGISTRY.DOCUMENT.eq(document))
				.and(salaryTypeCondition)
				.and(datesCondition)
				.orderBy(SALARY.END_DATE.desc())
				.fetch();
		
		// Enterprise Id
		Integer enterpriseId = null;
		
		if(salaryRecords.isNotEmpty()) {
			Integer contractId = salaryRecords.get(0).get(SALARY.CONTRACT);
			enterpriseId = getEnterpriseId(dslContext, contractId);
		}
		
		for(Record salaryRecord : salaryRecords) {
			
			SalaryInfo salaryInfo = new SalaryInfo();
			salaryInfo.setId(salaryRecord.get(SALARY.ID));
			salaryInfo.setDomain(salaryRecord.get(SALARY.DOMAIN));
			salaryInfo.setContract(salaryRecord.get(SALARY.CONTRACT));
			salaryInfo.setStartDate(salaryRecord.get(SALARY.START_DATE));
			salaryInfo.setEndDate(salaryRecord.get(SALARY.END_DATE));
			salaryInfo.setType(Salary.Type.values()[salaryRecord.get(SALARY.TYPE)]);
			salaryInfo.setEnterpriseName(salaryRecord.get(SALARY.ENTERPRISE_NAME));
			salaryInfo.setEmployeeName(salaryRecord.get(SALARY.EMPLOYEE_NAME));
			salaryInfo.setTotalPayment(salaryRecord.get(SALARY.TOTAL_PAYMENT));
			salaryInfo.setTotalDeduction(salaryRecord.get(SALARY.TOTAL_DEDUCTION));
			salaryInfo.setTotalLiquid(salaryRecord.get(SALARY.TOTAL_LIQUID));
			
			Integer contractId = salaryRecord.get(SALARY.CONTRACT);
			
			Record workplaceRecord = getWorkplaceRecord(dslContext, contractId);
			
			String workplaceName = workplaceRecord.get(WORKPLACE.DESCRIPTION);
			Integer workplaceId =  workplaceRecord.get(WORKPLACE.ID);
			
			// Workplace
			salaryInfo.setWorkplaceName(workplaceName);
			salaryInfo.setWorkplaceId(workplaceId);
			
			// Enterprise ID
			salaryInfo.setEnterpriseId(enterpriseId);
			
			salaryInfo.setFinance(null != salaryRecord.get(FINANCE.ID));
			
			//Is Alcatraz
			Result<AlcatrazRecord> alcatrazRecords = dslContext.selectFrom(ALCATRAZ).where(ALCATRAZ.SALARY.eq(salaryInfo.getId())).fetch();
			if(!alcatrazRecords.isEmpty()) {
				salaryInfo.setAlcatraz(true);
				
				FsModelRecord fsModelRecord = dslContext.selectFrom(FS_MODEL).where(FS_MODEL.ID.eq(alcatrazRecords.get(0).getFsModel())).fetchOne();
				salaryInfo.setAlcatrazYear(fsModelRecord.getYear());
				salaryInfo.setAlcatrazPeriod(AlcatrazPeriod.values()[fsModelRecord.getPeriod()]);
				salaryInfo.setAlcatrazTerritory(AlcatrazTerritory.values()[fsModelRecord.getAdministration()]);
				
			} else salaryInfo.setAlcatraz(false);
			
			
			// Add to salaries list
			salaries.add(salaryInfo);

		}
		
		return salaries;
	}
	
	// --------------------------------------------------------------------------------------------
	//									AUXLIAR METHODS
	// --------------------------------------------------------------------------------------------

	private static Condition getDatesCondition(SalaryInfoFilter filter) {
		Condition condition = DSL.noCondition();
		
		if(null != filter.getDateTillT()) {
			Date startDate = new Date(filter.getDateTillT().getTime());
			Date endDate = new Date(filter.getDateTTo().getTime());
			condition = SALARY.END_DATE.between(startDate, endDate);
		}
		
		return condition;
	}

	private static Condition getContractCondition(DSLContext dslContext, Integer domainId, Integer userId, SalaryInfoFilter filter) {
		Condition condition = DSL.noCondition();
		
		List<Integer> userScopes = dslContext
				.select(USER_SCOPE.SCOPE)
				.from(USER_SCOPE)
				.where(USER_SCOPE.USER_ID.eq(userId))
				.fetch(USER_SCOPE.SCOPE);
		
		// Load scopes for domain. Only if it's a child of user's domain.
		userScopes.addAll(
			dslContext
			.select(SCOPE.ID)
			.from(SCOPE)
			.innerJoin(DOMAIN).on(SCOPE.DOMAIN.eq(DOMAIN.ID))
			.where(SCOPE.DOMAIN.eq(domainId))
			.and(DOMAIN.PARENT.in( DSL.select(USER.DOMAIN).from(USER).where(USER.ID.eq(userId))))
			.fetch(SCOPE.ID));
		
		if(isNumberValid(filter.getEnterpriseId())) {
			condition = ENTERPRISE.REGISTRY.eq(filter.getEnterpriseId())
					.and(ENTERPRISE.SCOPE.isNull().or(ENTERPRISE.SCOPE.in(userScopes)));
		} else if(isNumberValid(filter.getWorkplaceId())) {
			condition = WORKPLACE.ID.eq(filter.getWorkplaceId())
					.and(WORKPLACE.SCOPE.isNull().or(WORKPLACE.SCOPE.in(userScopes)));
		} else if(isNumberValid(filter.getEmployeeId())) {
			condition = SALARY.CONTRACT.eq(filter.getEmployeeId());
		} else {
			List<Integer> domainChilds = dslContext.select(DOMAIN.ID).from(DOMAIN)
					.join(SCOPE).on(SCOPE.ID.eq(DOMAIN.SCOPE))
					.where(DOMAIN.PARENT.eq(domainId))
					.and(DOMAIN.SCOPE.in(userScopes))
					.fetch(DOMAIN.ID);
			condition = SALARY.DOMAIN.in(domainChilds);
		}
		
		return condition;
	}

	private static Condition getSalaryTypeCondition(SalaryInfoFilter filter) {
		Condition condition = SALARY.TYPE.lt((byte)Salary.Type.L00.ordinal()); //DSL.noCondition();
		
		if(null != filter.getSalaryTypes() && !filter.getSalaryTypes().isEmpty()) {
			List<Byte>  salaryTypes = new ArrayList<>();
			filter.getSalaryTypes().forEach(type -> salaryTypes.add((byte)type.intValue()));
			condition = SALARY.TYPE.in(salaryTypes);
		}
		
		return condition;
	}
	
	private static Record getWorkplaceRecord(DSLContext dslContext, Integer contractId) {
		return dslContext.select()
				.from(WORKPLACE)
				.where(WORKPLACE.ID.eq(
						dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
						.where(CONTRACT.ID.eq(contractId))))
				.fetchOne();
	}

	private static Integer getEnterpriseId(DSLContext dslContext, Integer contractId) {
		return dslContext.select(WORKPLACE.ENTERPRISE).from(WORKPLACE)
				.where(WORKPLACE.ID.eq(
						dslContext.select(CONTRACT.WORKPLACE).from(CONTRACT)
							.where(CONTRACT.ID.eq(contractId))
				)).fetchOne()
				.get(WORKPLACE.ENTERPRISE);
	}

	private static boolean isNumberValid(Integer number) {
		return number != null;
	}

	// --------------------------------------------------------------------------------------------
	//									WORKPLACE METHODS IMPL
	// --------------------------------------------------------------------------------------------
	
	private static WorkplaceEmployees getWorkplaceActiveEmployeesDB(DSLContext dslContext, Integer domainId, Integer userId, Integer workplaceId) {
		WorkplaceEmployees workplaceEmployees = new WorkplaceEmployees();
		
		List<Integer> userScopes = dslContext.select(USER_SCOPE.SCOPE).from(USER_SCOPE).where(USER_SCOPE.USER_ID.eq(userId)).fetch(USER_SCOPE.SCOPE);
		
		Result<Record> contractRecords = dslContext.select().from(CONTRACT)
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.where(CONTRACT.ID.gt(0))
				.and(CONTRACT.WORKPLACE.eq(workplaceId))
				.and(WORKPLACE.SCOPE.in(userScopes))
				.fetch();
		
		for(Record contractRecord : contractRecords){
			Record personRecord = dslContext.select().from(PERSON)
					.where(PERSON.REGISTRY.eq(contractRecord.get(CONTRACT.PERSON)))
					.fetchOne();
			
			Record registryRecord = dslContext.select().from(REGISTRY)
					.where(REGISTRY.ID.eq(contractRecord.get(CONTRACT.PERSON)))
					.fetchOne();
			
			workplaceEmployees.addEmployee(
					contractRecord.get(CONTRACT.ID), 
					personRecord.get(PERSON.NAME), 
					personRecord.get(PERSON.FIRST_SURNAME) + " " + personRecord.get(PERSON.SECOND_SURNAME), 
					registryRecord.get(REGISTRY.DOCUMENT), 
					personRecord.get(PERSON.SOCIAL_SECURITY_NUM));
		}
		
		return workplaceEmployees;
	}
	
	// --------------------------------------------------------------------------------------------
	//									ENTERPRISE METHODS IMPL
	// --------------------------------------------------------------------------------------------
	
	private static List<EmployeeInfo> getEnterpriseActiveEmployeesDB(DSLContext dslContext, Integer domainId, Integer userId, Integer enterpriseId) {
		List<EmployeeInfo> enterpriseEmployees = new ArrayList<>();
		
		List<Integer> userScopes = dslContext.select(USER_SCOPE.SCOPE).from(USER_SCOPE).where(USER_SCOPE.USER_ID.eq(userId)).fetch(USER_SCOPE.SCOPE);
		
		Result<Record> contractRecords = dslContext.select().from(CONTRACT)
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.join(ENTERPRISE).on(ENTERPRISE.REGISTRY.eq(WORKPLACE.ENTERPRISE))
				.where(CONTRACT.ID.gt(0))
				.and(WORKPLACE.ID.gt(0))
				.and(WORKPLACE.SCOPE.isNull().or(WORKPLACE.SCOPE.in(userScopes)))
				.and(ENTERPRISE.REGISTRY.eq(enterpriseId))
				.and(ENTERPRISE.SCOPE.isNull().or(ENTERPRISE.SCOPE.in(userScopes)))
				.fetch();
		
		for(Record contractRecord : contractRecords){
			Record personRecord = dslContext.select().from(PERSON)
					.where(PERSON.REGISTRY.eq(contractRecord.get(CONTRACT.PERSON)))
					.fetchOne();
			
			Record registryRecord = dslContext.select().from(REGISTRY)
					.where(REGISTRY.ID.eq(contractRecord.get(CONTRACT.PERSON)))
					.fetchOne();
			
			EmployeeInfo employee = new EmployeeInfo();
			employee.setEmployeeId(contractRecord.get(CONTRACT.ID));
			employee.setContractId(contractRecord.get(CONTRACT.ID));
			employee.setName(personRecord.get(PERSON.NAME));
			employee.setSurName(personRecord.get(PERSON.FIRST_SURNAME) + " " + personRecord.get(PERSON.SECOND_SURNAME));
			employee.setDocument(registryRecord.get(REGISTRY.DOCUMENT));
			employee.setSsNumber(personRecord.get(PERSON.SOCIAL_SECURITY_NUM));
			
			enterpriseEmployees.add(employee);
		}
		
		return enterpriseEmployees;
	}
	
	// --------------------------------------------------------------------------------------------
	//									DELETE SALARY METHOD IMPL
	// --------------------------------------------------------------------------------------------
	
	private static void deleteSalariesDB(DSLContext dslContext, List<Integer> ids) {
		// Delete SalaryData, SalaryBonus, SalaryCost, SalaryPayment, SalaryDeduction, SalaryEmbargo, Salary
		for(Integer id : ids) {		
			//Is Alcatraz
			Result<AlcatrazRecord> alcatrazRecords = dslContext.selectFrom(ALCATRAZ).where(ALCATRAZ.SALARY.eq(id)).fetch();
			
			if(alcatrazRecords.isEmpty())
				dslContext.transaction( t -> {
					DSLContext dsl = t.dsl();
					dsl.delete(SALARY_DATA).using(SALARY_DATA.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(id)).execute();
					dsl.delete(SALARY_COST).using(SALARY_COST.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(id)).execute();
					dsl.delete(SALARY_BONUS).using(SALARY_BONUS.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(id)).execute();
					dsl.delete(SALARY_EMBARGO).using(SALARY_EMBARGO.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(id)).execute();
					dsl.delete(SALARY_PAYMENT).using(SALARY_PAYMENT.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(id)).execute();
					dsl.delete(SALARY_DEDUCTION).using(SALARY_DEDUCTION.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(id)).execute();
					dsl.delete(SALARY).where(SALARY.ID.eq(id)).execute();
				});
		}
	}
	
	
}
