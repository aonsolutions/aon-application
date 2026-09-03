package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Alcatraz.ALCATRAZ;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Finance.FINANCE;
import static com.esferalia.aon.jooq.tables.FinanceTracking.FINANCE_TRACKING;
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
import org.jooq.SelectConditionStep;
import org.jooq.Table;
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
import com.esferalia.aon.gwt.payroll.shared.SalaryParams;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.jooq.tables.records.AlcatrazRecord;
import com.esferalia.aon.jooq.tables.records.FinanceRecord;
import com.esferalia.aon.jooq.tables.records.FsModelRecord;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

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
	
	public static List<SalaryInfo> getSalaries(Connection connection, Integer domainId, Integer parentDomainId, Integer userId, SalaryParams params) {
		return getSalariesDB(DSL.using(connection, getDefaultSettings()), domainId, parentDomainId, userId, params);
	}
	
	public static SalaryInfo getSalariesDateEnd(Connection connection, Integer domainId, Integer userId, SalaryInfoFilter filter) {
		return getSalariesDateEnd(DSL.using(connection, getDefaultSettings()), domainId, userId, filter);
	}
	
	public static List<SalaryInfo> getSalariesByDocument(Connection connection, SalaryInfoFilter filter,  String document) {
		return getSalariesByDocumentDB(DSL.using(connection, getDefaultSettings()), filter, document);
	}
	
	public static List<SalaryInfo> getLastSalaryByDocument(Connection connection, SalaryInfoFilter filter,  String document) {
		return getLastSalaryByDocumentDB(DSL.using(connection, getDefaultSettings()), filter, document);
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

	public static void deleteSalaries(Connection connection, Integer domainId, List<Integer> ids) {
		deleteSalariesDB(DSL.using(connection, getDefaultSettings()), domainId, ids);
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
		Table<FinanceRecord> filteredFinance = DSL.selectFrom(FINANCE)
			    .where(FINANCE.DOMAIN.eq(domainId)
			    .and(FINANCE.PAYROLL.eq((byte)1)))
			    .asTable("filtered_finance");
		
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.join(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.join(ENTERPRISE).on(ENTERPRISE.REGISTRY.eq(WORKPLACE.ENTERPRISE))
				.leftJoin(filteredFinance).on(filteredFinance.field(FINANCE.SOURCE_ID).eq(SALARY.ID))
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
			salaryInfo.setChargeDate(salaryRecord.get(SALARY.CHARGE_DATE));
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
			
			Byte financeStatus = salaryRecord.get(FINANCE.STATUS);
			salaryInfo.setFinance(financeStatus != null && financeStatus != (byte) 0);
			
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
		
		// Get salaries
		Table<FinanceRecord> filteredFinance = DSL.selectFrom(FINANCE)
			    .where(FINANCE.DOMAIN.eq(domainId)
			    .and(FINANCE.PAYROLL.eq((byte)1)))
			    .asTable("filtered_finance");
		
		// Get end salary
		 Record salaryRecord = dslContext.select().from(SALARY)
				.join(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.join(ENTERPRISE).on(ENTERPRISE.REGISTRY.eq(WORKPLACE.ENTERPRISE))
				.leftJoin(filteredFinance).on(filteredFinance.field(FINANCE.SOURCE_ID).eq(SALARY.ID))
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
			salaryInfo.setChargeDate(salaryRecord.get(SALARY.CHARGE_DATE));
			salaryInfo.setType(Salary.Type.values()[salaryRecord.get(SALARY.TYPE)]);
			salaryInfo.setEnterpriseName(salaryRecord.get(SALARY.ENTERPRISE_NAME));
			salaryInfo.setEmployeeName(salaryRecord.get(SALARY.EMPLOYEE_NAME));
			salaryInfo.setTotalPayment(salaryRecord.get(SALARY.TOTAL_PAYMENT));
			salaryInfo.setTotalDeduction(salaryRecord.get(SALARY.TOTAL_DEDUCTION));
			salaryInfo.setTotalLiquid(salaryRecord.get(SALARY.TOTAL_LIQUID));
			
			Byte financeStatus = salaryRecord.get(FINANCE.STATUS);
			salaryInfo.setFinance(financeStatus != null && financeStatus != (byte) 0);
			
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
		Table<FinanceRecord> filteredFinance = DSL.selectFrom(FINANCE)
			    .where(FINANCE.DOMAIN.eq(filter.getWorkplaceId())
			    .and(FINANCE.PAYROLL.eq((byte)1)))
			    .asTable();
		
		// Get salaries
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.innerJoin(CONTRACT)
				.on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.innerJoin(REGISTRY)
				.on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.leftJoin(filteredFinance).on(filteredFinance.field(FINANCE.SOURCE_ID).eq(SALARY.ID))
				.where(REGISTRY.DOMAIN.eq(filter.getWorkplaceId()))
				.and(REGISTRY.DOCUMENT.eq(document))
				.and(salaryTypeCondition)
				.and(datesCondition)
				.groupBy(SALARY.ID)
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
			salaryInfo.setChargeDate(salaryRecord.get(SALARY.CHARGE_DATE));
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
			
			Byte financeStatus = salaryRecord.get(FINANCE.STATUS);
			salaryInfo.setFinance(financeStatus != null && financeStatus != (byte) 0);
			
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

	private static List<SalaryInfo> getLastSalaryByDocumentDB(DSLContext dslContext, SalaryInfoFilter filter,  String document) {
		List<SalaryInfo> salaries = new ArrayList<>();
		// SalaryType
		Condition salaryTypeCondition = getSalaryTypeCondition(filter);
		// Dates
		Condition datesCondition = getDatesCondition(filter);
		
		// Get salaries
		Table<FinanceRecord> filteredFinance = DSL.selectFrom(FINANCE)
			    .where(FINANCE.DOMAIN.eq(filter.getWorkplaceId())
			    .and(FINANCE.PAYROLL.eq((byte)1)))
			    .asTable();
		
		// Get salaries
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.innerJoin(CONTRACT)
				.on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.innerJoin(REGISTRY)
				.on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.leftJoin(filteredFinance).on(filteredFinance.field(FINANCE.SOURCE_ID).eq(SALARY.ID))
				.where(REGISTRY.DOMAIN.eq(filter.getWorkplaceId()))
				.and(REGISTRY.DOCUMENT.eq(document))
				.and(salaryTypeCondition)
				.and(datesCondition)
				.orderBy(SALARY.END_DATE.desc())
				.limit(1)
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
			salaryInfo.setChargeDate(salaryRecord.get(SALARY.CHARGE_DATE));
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
			
			Byte financeStatus = salaryRecord.get(FINANCE.STATUS);
			salaryInfo.setFinance(financeStatus != null && financeStatus != (byte) 0);
			
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
				.join(PERSON).on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
				.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.where(CONTRACT.ID.gt(0))
				.and(CONTRACT.WORKPLACE.eq(workplaceId))
				.and(WORKPLACE.SCOPE.in(userScopes))
				.fetch();
		
		for(Record contractRecord : contractRecords){
			workplaceEmployees.addEmployee(
					contractRecord.get(CONTRACT.ID), 
					contractRecord.get(PERSON.NAME), 
					contractRecord.get(PERSON.FIRST_SURNAME) + " " + contractRecord.get(PERSON.SECOND_SURNAME), 
					contractRecord.get(REGISTRY.DOCUMENT), 
					contractRecord.get(PERSON.SOCIAL_SECURITY_NUM));
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
				.join(PERSON).on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
				.join(REGISTRY).on(REGISTRY.ID.eq(CONTRACT.PERSON))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.join(ENTERPRISE).on(ENTERPRISE.REGISTRY.eq(WORKPLACE.ENTERPRISE))
				.where(CONTRACT.ID.gt(0))
				.and(WORKPLACE.ID.gt(0))
				.and(WORKPLACE.SCOPE.isNull().or(WORKPLACE.SCOPE.in(userScopes)))
				.and(ENTERPRISE.REGISTRY.eq(enterpriseId))
				.and(ENTERPRISE.SCOPE.isNull().or(ENTERPRISE.SCOPE.in(userScopes)))
				.fetch();
		
		for(Record contractRecord : contractRecords){
			EmployeeInfo employee = new EmployeeInfo();
			employee.setEmployeeId(contractRecord.get(CONTRACT.ID));
			employee.setContractId(contractRecord.get(CONTRACT.ID));
			employee.setName(contractRecord.get(PERSON.NAME));
			employee.setSurName(contractRecord.get(PERSON.FIRST_SURNAME) + " " + contractRecord.get(PERSON.SECOND_SURNAME));
			employee.setDocument(contractRecord.get(REGISTRY.DOCUMENT));
			employee.setSsNumber(contractRecord.get(PERSON.SOCIAL_SECURITY_NUM));
			
			enterpriseEmployees.add(employee);
		}
		
		return enterpriseEmployees;
	}
	
	// --------------------------------------------------------------------------------------------
	//									DELETE SALARY METHOD IMPL
	// --------------------------------------------------------------------------------------------
	
	private static void deleteSalariesDB(DSLContext dslContext, Integer domainId, List<Integer> ids) {
		// Delete SalaryData, SalaryBonus, SalaryCost, SalaryPayment, SalaryDeduction, SalaryEmbargo, Salary
		for(Integer id : ids) {		
			//Is Alcatraz
			Result<AlcatrazRecord> alcatrazRecords = dslContext.selectFrom(ALCATRAZ).where(ALCATRAZ.SALARY.eq(id)).fetch();
			
			if(alcatrazRecords.isEmpty())
				dslContext.transaction( t -> {
					DSLContext dsl = t.dsl();
					dsl.delete(SALARY_DATA).using(SALARY_DATA.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(id)).and(SALARY.DOMAIN.eq(domainId)).execute();
					dsl.delete(SALARY_COST).using(SALARY_COST.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(id)).and(SALARY.DOMAIN.eq(domainId)).execute();
					dsl.delete(SALARY_BONUS).using(SALARY_BONUS.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(id)).and(SALARY.DOMAIN.eq(domainId)).execute();
					dsl.delete(SALARY_EMBARGO).using(SALARY_EMBARGO.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(id)).and(SALARY.DOMAIN.eq(domainId)).execute();
					dsl.delete(SALARY_PAYMENT).using(SALARY_PAYMENT.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(id)).and(SALARY.DOMAIN.eq(domainId)).execute();
					dsl.delete(SALARY_DEDUCTION).using(SALARY_DEDUCTION.innerJoin(SALARY).onKey()).where(SALARY.ID.eq(id)).and(SALARY.DOMAIN.eq(domainId)).execute();
					dsl.delete(FINANCE_TRACKING).using(FINANCE_TRACKING.innerJoin(FINANCE).onKey()).where(FINANCE.PAYROLL.eq((byte)1)).and(FINANCE.SOURCE_ID.eq(id)).and(FINANCE.STATUS.eq((byte)0)).and(FINANCE.DOMAIN.eq(domainId)).execute();
					dsl.delete(FINANCE).where(FINANCE.PAYROLL.eq((byte)1)).and(FINANCE.SOURCE_ID.eq(id)).and(FINANCE.STATUS.eq((byte)0)).and(FINANCE.DOMAIN.eq(domainId)).execute();
					dsl.delete(SALARY).where(SALARY.ID.eq(id)).and(SALARY.DOMAIN.eq(domainId)).execute();
				});
		}
	}
	
	// --------------------------------------------------------------------------------------------
	//									GET SALARYES METHOD IMPL
	// --------------------------------------------------------------------------------------------
	
	private static List<SalaryInfo> getSalariesDB(DSLContext dslContext, Integer domainId, Integer parentDomainId, Integer userId, SalaryParams params) {
		Condition condition = paramsToCondition(dslContext, domainId, parentDomainId, userId, params);
		
		List<SalaryInfo> salaries = new ArrayList<>();
		
		// Get salaries
		Table<FinanceRecord> filteredFinance = DSL.selectFrom(FINANCE)
			    .where(FINANCE.DOMAIN.eq(domainId)
			    .and(FINANCE.PAYROLL.eq((byte)1)))
			    .groupBy(FINANCE.SOURCE_ID)
			    .asTable("filtered_finance");
		
		SelectConditionStep<Record> select = dslContext.select().from(SALARY)
				.join(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT))
				.join(WORKPLACE).on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
				.join(ENTERPRISE).on(ENTERPRISE.REGISTRY.eq(WORKPLACE.ENTERPRISE))
				.leftJoin(filteredFinance).on(filteredFinance.field(FINANCE.SOURCE_ID).eq(SALARY.ID))
				.where(condition);
		
		if(params.isAsc()) {
			if(AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "enterprise"))
				select.orderBy(SALARY.ENTERPRISE_NAME);
			else if(AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "workplace"))
				select.orderBy(WORKPLACE.DESCRIPTION);
			else if(AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "employee"))
				select.orderBy(SALARY.EMPLOYEE_NAME);
			else if(AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "type"))
				select.orderBy(SALARY.TYPE);
			else if(AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "start"))
				select.orderBy(SALARY.START_DATE);
			else if(AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "end"))
				select.orderBy(SALARY.END_DATE);
		} else {
			if(AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "enterprise"))
				select.orderBy(SALARY.ENTERPRISE_NAME.desc());
			else if(AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "workplace"))
				select.orderBy(WORKPLACE.DESCRIPTION.desc());
			else if(AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "employee"))
				select.orderBy(SALARY.EMPLOYEE_NAME.desc());
			else if(AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "type"))
				select.orderBy(SALARY.TYPE.desc());
			else if(AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "start"))
				select.orderBy(SALARY.START_DATE.desc());
			else if(AonStringUtils.equalsIgnoreCase(params.getOrderBy(), "end"))
				select.orderBy(SALARY.END_DATE.desc());
		}
		
		Result<Record> salaryRecords = select.limit(params.getOffset(), params.getLimit())
				.fetch();
		
		System.out.println("getSalariesDB -> offset : " + params.getOffset() + ", limit : " + params.getLimit() + ", results : " + salaryRecords.size());
		
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
			
			Byte financeStatus = salaryRecord.get(FINANCE.STATUS);
			salaryInfo.setFinance(financeStatus != null && financeStatus != (byte) 0);
			
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

	private static Condition paramsToCondition(DSLContext dslContext, Integer domainId, Integer parentDomainId, Integer userId, SalaryParams params) {
		Condition condition = DSL.noCondition();
		
		if(AonStringUtils.isNotBlank(params.getDescription()))
			condition = condition.and(
					SALARY.EMPLOYEE_NAME.like("%" + params.getDescription() + "%")
					.or(WORKPLACE.DESCRIPTION.like("%" + params.getDescription() + "%"))
					.or(SALARY.ENTERPRISE_NAME.like("%" + params.getDescription() + "%"))
			);
		
		List<Byte> salaryTypes = new ArrayList<Byte>();
		if(params.isSalary()) salaryTypes.add((byte)Salary.Type.SALARY.ordinal());
		if(params.isExtra()) salaryTypes.add((byte)Salary.Type.EXTRA.ordinal());
		if(params.isSettle()) salaryTypes.add((byte)Salary.Type.SETTLE.ordinal());
		if(params.isDelay()) salaryTypes.add((byte)Salary.Type.DELAY.ordinal());
		if(params.isProcedural()) salaryTypes.add((byte)Salary.Type.PROCEDURAL.ordinal());
		
		if(params.isLiquidations()) salaryTypes.add((byte)Salary.Type.L00.ordinal());
		if(params.isLiquidations()) salaryTypes.add((byte)Salary.Type.L02.ordinal());
		if(params.isLiquidations()) salaryTypes.add((byte)Salary.Type.L03.ordinal());
		if(params.isLiquidations()) salaryTypes.add((byte)Salary.Type.L13.ordinal());
		
		condition = condition.and(SALARY.TYPE.in(salaryTypes));
		
		List<Integer> userScopes = dslContext
				.select(USER_SCOPE.SCOPE)
				.from(USER_SCOPE)
				.where(USER_SCOPE.USER_ID.eq(userId))
				.fetch(USER_SCOPE.SCOPE);
		
		// DomainFilter
		if(parentDomainId == null) {
			// Load scopes for domain. Only if it's a child of user's domain.
			userScopes.addAll(
					dslContext
				.select(SCOPE.ID)
				.from(SCOPE)
				.innerJoin(DOMAIN).on(SCOPE.DOMAIN.eq(DOMAIN.ID))
				.where(SCOPE.DOMAIN.eq(domainId))
				.and(DOMAIN.PARENT.in( DSL.select(USER.DOMAIN).from(USER).where(USER.ID.eq(userId))))
				.fetch(SCOPE.ID));
			
			List<Integer> domainChilds = dslContext.select(DOMAIN.ID).from(DOMAIN)
					.leftOuterJoin(SCOPE).on(SCOPE.ID.eq(DOMAIN.SCOPE))
					.where(DOMAIN.PARENT.eq(domainId))
					.and(DOMAIN.SCOPE.in(userScopes).or(DOMAIN.SCOPE.isNull()))
					.fetch(DOMAIN.ID);
			
			if(!domainChilds.isEmpty())
				condition = condition.and( SALARY.DOMAIN.in(domainChilds) );
			else
				condition = condition.and( SALARY.DOMAIN.eq(domainId) );
			
		} else {
			condition = condition.and( SALARY.DOMAIN.eq(domainId) );
		}
			
		
		if(isNumberValid(params.getWorkplace())) {
			condition = condition.and( WORKPLACE.ID.eq(params.getWorkplace()) );
		} else if(isNumberValid(params.getContract())) {
			condition = condition.and( SALARY.CONTRACT.eq(params.getContract()) );
		}
		
		
		if(null != params.getStart())
			condition = condition.and(
					(
							SALARY.TYPE.ne(com.esferalia.aon.occam.api.model.type.SalaryType.DELAY.value()).and(
							SALARY.ISSUE_DATE.ge(AonDateUtils.toSql(params.getStart())))
					).or(
							SALARY.TYPE.eq(com.esferalia.aon.occam.api.model.type.SalaryType.DELAY.value()).and(
							SALARY.CHARGE_DATE.ge(AonDateUtils.toSql(params.getStart())))
					)
			);
		
		if(null != params.getEnd())
			condition = condition.and(
					(
							SALARY.TYPE.ne(com.esferalia.aon.occam.api.model.type.SalaryType.DELAY.value()).and(
							SALARY.ISSUE_DATE.le(AonDateUtils.toSql(params.getEnd())))
					).or(
							SALARY.TYPE.eq(com.esferalia.aon.occam.api.model.type.SalaryType.DELAY.value()).and(
							SALARY.CHARGE_DATE.le(AonDateUtils.toSql(params.getEnd())))
					)
			);
		
		return condition;
	}
	
	
}
