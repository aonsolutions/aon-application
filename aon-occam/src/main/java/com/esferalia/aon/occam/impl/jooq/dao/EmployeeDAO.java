package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.BonusConcept.BONUS_CONCEPT;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractCost.CONTRACT_COST;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.DeductionConcept.DEDUCTION_CONCEPT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.PayrollWorkplace.PAYROLL_WORKPLACE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static java.util.Calendar.DAY_OF_MONTH;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SelectLimitPercentStep;
import org.jooq.Table;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.jooq.tables.records.ContractBonusRecord;
import com.esferalia.aon.jooq.tables.records.ContractCostRecord;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractDeductionRecord;
import com.esferalia.aon.jooq.tables.records.ContractInfoRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseActivityRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseCccRecord;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
import com.esferalia.aon.jooq.tables.records.PersonRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.WorkplaceRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.Cost;
import com.esferalia.aon.occam.api.model.Deduction;
import com.esferalia.aon.occam.api.model.Filter.ContractFilter;
import com.esferalia.aon.occam.api.model.Filter.EmployeeFilter;
import com.esferalia.aon.occam.api.model.HasEndDate;
import com.esferalia.aon.occam.api.model.HasStartDate;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.payroll.Employee;
import com.esferalia.aon.occam.api.model.payroll.TooManyEmployeesException;
import com.esferalia.aon.occam.api.model.type.BonusType;
import com.esferalia.aon.occam.api.model.type.DeductionType;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.Gender;
import com.esferalia.aon.occam.api.model.type.SSRegimeType;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractPropertiesDAO;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.EmployeePropertiesDAO;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class EmployeeDAO {
	
	public  static Registry ENTERPRISE_REGISTRY = REGISTRY.as("enterprise");
	

	private static String EMPLOYEE_DATA [] = {"TC2", "GRUPO_COTIZACION", "COEFICIENTE_PARCIALIDAD", "OCUPACION", "RLCE", "JORNADAS_REALES"};
	

	public  static Optional<Employee> getEmployee(AONContext aonContext, EmployeeFilter filter ) {
		List<Employee> employees = getEmployees(aonContext, filter).collect(Collectors.toList());
		
		if ( employees.isEmpty() )
			return Optional.empty();
		else if ( employees.size() == 1 )
			return Optional.of(employees.get(0));
		else 
			throw new TooManyEmployeesException();
	}

	public  static Stream<Employee> getEmployees(AONContext aonContext, EmployeeFilter filter ) {
		DSLContext dslContext = aonContext.getDslContext();
		
		Map<Employee, List<Object[]>> employeeDataMap = 
		dslContext
		.select()
		.from(CONTRACT)
		.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.innerJoin(REGISTRY).on(PERSON.REGISTRY.eq(REGISTRY.ID))
		.innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
		.innerJoin(ENTERPRISE_CCC).on(CONTRACT.ENTERPRISE_CCC.eq(ENTERPRISE_CCC.ID))
		.innerJoin(ENTERPRISE_ACTIVITY).on(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
		.innerJoin(ENTERPRISE_REGISTRY).on(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(ENTERPRISE_REGISTRY.ID))
		.leftJoin(CONTRACT_DATA).on(CONTRACT_DATA.CONTRACT.eq(CONTRACT.ID).and(CONTRACT_DATA.NAME.in(EMPLOYEE_DATA)))
		
		.where(new EmployeePropertiesDAO().getConditions(filter))
		
		.fetchGroups( r -> 
				new Employee()
				.setCcc(r.get(ENTERPRISE_CCC.CCC))
				.setCif(r.get(ENTERPRISE_REGISTRY.DOCUMENT))
		
				.setDni(r.get(REGISTRY.DOCUMENT))
				.setNaf(r.get(PERSON.SOCIAL_SECURITY_NUM))
				.setName(r.get(REGISTRY.NAME))
				.setSex(getSex(r.get(PERSON.GENDER)))
				.setBirthDate(r.get(PERSON.BIRTH_DATE))
				
				.setEmployeeId(r.get(CONTRACT.ID))
				.setStartDate(r.get(CONTRACT.START_DATE))
				.setEndDate(r.get(CONTRACT.END_DATE))
				.setCategory(r.get(CONTRACT.CATEGORY_DESCRIPTION))
				
				.setRegime(getSSRegimeCode(r.get(CONTRACT.SS_REGIME)))
				.setWorkplaceName(r.get(WORKPLACE.DESCRIPTION))
				.setWorkplaceId(r.get(WORKPLACE.ID))
				
				, r -> new Object [] {
						r.get(CONTRACT_DATA.NAME),
						r.get(CONTRACT_DATA.EXPRESSION),
						r.get(CONTRACT_DATA.START_DATE),
						r.get(CONTRACT_DATA.END_DATE)
				});
		
		employeeDataMap.forEach((employee, datas) -> datas.stream().filter( data -> data[0] != null ).forEach( data -> employee.addData((String)data[0], (String)data[1], (java.sql.Date)data[2], (java.sql.Date)data[3])));
		
		return employeeDataMap.keySet().stream();
			
	}

	public  static Employee addEmployee(AONContext aonContext, String domainName, Employee employee ) {
		DSLContext dslContext = aonContext.getDslContext();
		Integer domainId = 
		dslContext
		.select()
		.from(DOMAIN)
		.where(DOMAIN.NAME.eq(domainName))
		.fetchOptional(DOMAIN.ID)
		.orElseThrow(IllegalStateException::new);
		dslContext.transaction((config) -> { 
			ContractRecord contractRecord = addEmployee(dslContext, domainId, employee);
			employee.setEmployeeId(contractRecord.getId());
			employee.setWorkplaceId(contractRecord.getWorkplace());
		});
		
		return employee;
	}

	public static ContractRecord addEmployee(DSLContext dslContext, Integer domainId, Employee employee ) {
		
		EnterpriseCccRecord enterpriseCccRecord =
		getEnterpriseCCC(dslContext, domainId, employee );
		
		RegistryRecord personRecord = 
		getPerson(dslContext, domainId, employee);
		
		// Check person full name
		checkPersonName(dslContext, personRecord.get(REGISTRY.ID), employee);
		
		WorkplaceRecord workplaceRecord = 
		getWorpPlace(dslContext, domainId, enterpriseCccRecord.getEnterpriseActivity(), employee);
		
		InsertSetMoreStep<ContractRecord> insertContract = 
		dslContext
		.insertInto(CONTRACT)
		.set(CONTRACT.DOMAIN, domainId)
		.set(CONTRACT.PERSON, personRecord.getId())
		.set(CONTRACT.SS_REGIME, getSSRegime(employee))
		.set(CONTRACT.WORKPLACE, workplaceRecord.getId())
		.set(CONTRACT.ENTERPRISE_CCC, enterpriseCccRecord.getId())
		.set(CONTRACT.ENTERPRISE_ACTIVITY, enterpriseCccRecord.getEnterpriseActivity())
		.set(CONTRACT.START_DATE, toSql(employee.getStartDate()))
		.set(CONTRACT.SENIORITY_DATE, toSql(employee.getStartDate()));
		employee.getEndDate().ifPresent(endDate -> insertContract.set(CONTRACT.END_DATE, toSql(endDate)));
		employee.getCategory().ifPresent(category -> insertContract.set(CONTRACT.CATEGORY_DESCRIPTION, category));
		employee.getRegistration().ifPresent(registration -> insertContract.set(CONTRACT.REGISTRATION, registration));
		
		ContractRecord contractRecord = insertContract.returning().fetchOne();
		
		InsertSetMoreStep<ContractDataRecord> insertContractData = null ; 
		
		for ( Map.Entry<String, Collection<Employee.ExpressionData>> entry: employee.getDatas().entrySet() ) {
			for( Employee.ExpressionData data :  entry.getValue()) {
				insertContractData = 
				(insertContractData != null ? 
				insertContractData.newRecord():
				dslContext.insertInto(CONTRACT_DATA))
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.CONTRACT, contractRecord.getId())
				.set(CONTRACT_DATA.NAME, entry.getKey() )
				.set(CONTRACT_DATA.START_DATE, toSql(data.getStartDate()))
				.set(CONTRACT_DATA.END_DATE, toSql(data.getEndDate()))
				.set(CONTRACT_DATA.EXPRESSION, data.getExpression());
			}
		}

		InsertSetMoreStep<ContractInfoRecord> insertContractInfo = null ; 
		for ( Map.Entry<String, Collection<Employee.ExpressionData>> entry: employee.getInfos().entrySet() ) {
			for( Employee.ExpressionData data :  entry.getValue()) {
				insertContractInfo = 
				(insertContractInfo != null ? 
						insertContractInfo.newRecord():
				dslContext.insertInto(CONTRACT_INFO))
				.set(CONTRACT_DATA.DOMAIN, domainId)
				.set(CONTRACT_DATA.CONTRACT, contractRecord.getId())
				.set(CONTRACT_DATA.NAME, entry.getKey() )
				.set(CONTRACT_DATA.START_DATE, toSql(data.getStartDate()))
				.set(CONTRACT_DATA.END_DATE, toSql(data.getEndDate()))
				.set(CONTRACT_DATA.EXPRESSION, data.getExpression());
			}
		}
		if(insertContractInfo!=null)
			insertContractInfo.execute();
		
		
		insertContractData.execute();
		
		return contractRecord;
	}

	public static Bonus [] getBonuses(AONContext aonContext, String domainName, Integer contractId) {
		return getBonuses(aonContext.getDslContext(), domainName, contractId);
	}

	public static Bonus [] setBonuses(AONContext aonContext, String domainName, String ccc, String naf, Date startDate, Date endDate, Bonus ...bonuses) {
		return setBonuses(aonContext.getDslContext(), domainName, ccc, naf, toSql(startDate), toSql(endDate), bonuses);
	}

	public static Deduction [] getDeductions(AONContext aonContext, String domainName, Integer contractId) {
		return getDeductions(aonContext.getDslContext(), domainName, contractId);
	}

	public static Deduction [] setDeductions(AONContext aonContext, String domainName, String ccc, String naf, Date startDate, Date endDate, Deduction ...deductions) {
		return setDeductions(aonContext.getDslContext(), domainName, ccc, naf, toSql(startDate), toSql(endDate), deductions);
	}

	public static Cost [] getCosts(AONContext aonContext, String domainName, Integer contractId) {
		return getCosts(aonContext.getDslContext(), domainName, contractId);
	}

	public static Cost [] setCosts(AONContext aonContext, String domainName, String ccc, String naf, Date startDate, Date endDate, Cost ...costs) {
		return setCosts(aonContext.getDslContext(), domainName, ccc, naf, toSql(startDate), toSql(endDate), costs);
	}

	public static ContractData [] getData(AONContext aonContext, String domainName, Integer contractId) {
		return getData(aonContext.getDslContext(), domainName, contractId);
	}

	public static ContractData[] setData(AONContext aonContext, String domainName, String ccc, String naf, Date startDate, Date endDate, ContractData... contractDatas) {
		return setData(aonContext.getDslContext(), domainName, ccc, naf, toSql(startDate), toSql(endDate), contractDatas);
	}

	public static ContractData[] setContractData(AONContext aonContext, String domainName, ContractFilter filter, ContractData... contractDatas) {
		return setContractData(aonContext.getDslContext(), domainName, filter, contractDatas);
	}

	private static  EnterpriseCccRecord getEnterpriseCCC(DSLContext dslContext, Integer domainId, Employee employee) {
		return dslContext
		.select()
		.from(ENTERPRISE_CCC)
		.where(ENTERPRISE_CCC.DOMAIN.eq(domainId))
		.and(ENTERPRISE_CCC.CCC.eq(employee.getCcc()))
		.and(ENTERPRISE_CCC.TYPE.eq(getCCCType(employee)))
		.fetchOptionalInto(ENTERPRISE_CCC)
		.orElseGet(() -> {
			
			EnterpriseActivityRecord enterpriseActivityRecord = 
			getEnterpriseActivity(dslContext, domainId, employee);

			InsertSetMoreStep<EnterpriseCccRecord> insertEnterpriseCCCRecord = 
			dslContext
			.insertInto(ENTERPRISE_CCC)
			.set(ENTERPRISE_CCC.DOMAIN, domainId)
			.set(ENTERPRISE_CCC.CCC, employee.getCcc())
			.set(ENTERPRISE_CCC.TYPE, getCCCType(employee))
			.set(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY, enterpriseActivityRecord.getId());
			
			getGeozone(dslContext, domainId, employee.getCcc())
			.ifPresent( geozoneRecord ->  insertEnterpriseCCCRecord.set(ENTERPRISE_CCC.GEOZONE, geozoneRecord.getId()));
			
			return insertEnterpriseCCCRecord.returning().fetchOne();
		});
		
		
	}
	
	private static  WorkplaceRecord getWorpPlace(DSLContext dslContext, Integer domainId, Integer enterpriseActivityId, Employee employee)
	{
		Field<Integer> CONTRACTS_COUNT = DSL.field("contracts_count", Integer.class);
		Field<Integer> CONTRACTS_WORKPLACE = DSL.field("contracts_workplace", Integer.class);
		
		Table<Record2<Integer, Integer>> CONTRACTS = 
		DSL.select(
		DSL.count().as(CONTRACTS_COUNT)
		,CONTRACT.WORKPLACE.as(CONTRACTS_WORKPLACE))
		.from(CONTRACT).groupBy(CONTRACT.WORKPLACE).asTable("CONTRACTS");
	
		return 
		dslContext
		.select()
		.from(WORKPLACE)
		.innerJoin(PAYROLL_WORKPLACE).onKey()
		.innerJoin(RADDRESS).onKey()
		.innerJoin(GEOZONE).onKey()
		.leftJoin(CONTRACTS).on(WORKPLACE.ID.eq(CONTRACTS_WORKPLACE))
		.where(WORKPLACE.DOMAIN.eq(domainId))
		.and(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY.eq(enterpriseActivityId))
		.and(GEOZONE.CODE.eq(employee.getCcc().substring(0,2)))
		.orderBy(CONTRACTS_COUNT.desc())
		.fetchStreamInto(WORKPLACE).findFirst()
		.orElseGet(() ->
			dslContext
			.select()
			.from(WORKPLACE)
			.innerJoin(PAYROLL_WORKPLACE).onKey()
			.leftJoin(CONTRACTS).on(WORKPLACE.ID.eq(CONTRACTS_WORKPLACE))
			.where(WORKPLACE.DOMAIN.eq(domainId))
			.and(PAYROLL_WORKPLACE.ENTERPRISE_ACTIVITY.eq(enterpriseActivityId))
			.orderBy(CONTRACTS_COUNT.desc())
			.fetchStreamInto(WORKPLACE).findFirst()
			.orElseGet(() ->
				dslContext
				.select()
				.from(WORKPLACE)
				.innerJoin(PAYROLL_WORKPLACE).onKey()
				.leftJoin(CONTRACTS).on(WORKPLACE.ID.eq(CONTRACTS_WORKPLACE))
				.where(WORKPLACE.DOMAIN.eq(domainId))
				.orderBy(CONTRACTS_COUNT.desc())
				.fetchStreamInto(WORKPLACE).findFirst()
				.orElseGet(() -> 
					dslContext
					.select()
					.from(WORKPLACE)
					.where(WORKPLACE.DOMAIN.eq(domainId))
					.fetchStreamInto(WORKPLACE).findFirst()
					.orElseGet(() ->
						// No enterprise activity, return new one
						dslContext
						.insertInto(WORKPLACE)
						.set(WORKPLACE.DOMAIN, domainId)
						.set(WORKPLACE.ENTERPRISE, getEnterprise(dslContext, domainId).getRegistry())
						.set(WORKPLACE.DESCRIPTION, "CT AUTOGENERADO")
						.set(WORKPLACE.ADDRESS, getEnterpriseDefaultAddress(dslContext, domainId).get(0).get(RADDRESS.ID))
						.set(WORKPLACE.SCOPE, getEnterprise(dslContext, domainId).getScope())
						.returning()
						.fetchOne()
					)
				)
			)
		);
		
		
	}
	
	private static Result<Record> getEnterpriseDefaultAddress(DSLContext dslContext, Integer domainId) {
		Result<Record> raddresses = dslContext
		.select()
		.from(RADDRESS)
		.where(RADDRESS.REGISTRY.eq(getEnterprise(dslContext, domainId).getRegistry()))
		.fetch();
		
		if(raddresses.isEmpty())
			throw new IllegalArgumentException("No existe direcci\u00F3n para esta empresa");
		
		return raddresses;
	}

	private static Optional<GeozoneRecord> getEnScope(DSLContext dslContext, Integer domainId, String ccc) {
		String code = AonStringUtils.substring(ccc, 0, 2);
		return 
		dslContext
		.select()
		.from(GEOZONE)
		.where(GEOZONE.DOMAIN.eq(domainId))
		.and(GEOZONE.CODE.eq(code))
		.fetchOptionalInto(GEOZONE)
		.or(() ->
			dslContext
			.select()
			.from(GEOZONE)
			.where(GEOZONE.DOMAIN.eq(DSL.select(DOMAIN.PARENT).from(DOMAIN).where(DOMAIN.ID.eq(domainId))))
			.and(GEOZONE.CODE.eq(code))
			.fetchOptionalInto(GEOZONE)
		)
		;
	}

	private static Optional<GeozoneRecord> getGeozone(DSLContext dslContext, Integer domainId, String ccc) {
		String code = AonStringUtils.substring(ccc, 0, 2);
		return 
		dslContext
		.select()
		.from(GEOZONE)
		.where(GEOZONE.DOMAIN.eq(domainId))
		.and(GEOZONE.CODE.eq(code))
		.fetchOptionalInto(GEOZONE)
		.or(() ->
			dslContext
			.select()
			.from(GEOZONE)
			.where(GEOZONE.DOMAIN.eq(DSL.select(DOMAIN.PARENT).from(DOMAIN).where(DOMAIN.ID.eq(domainId))))
			.and(GEOZONE.CODE.eq(code))
			.fetchOptionalInto(GEOZONE)
		)
		;
	}
	

	private static EnterpriseRecord getEnterprise(DSLContext dslContext, Integer domainId) {
		return 
		dslContext
		.select()
		.from(ENTERPRISE)
		.where(ENTERPRISE.DOMAIN.eq(domainId))
		.fetchOptionalInto(ENTERPRISE)
		.orElseThrow(IllegalArgumentException::new)
		;
	}

	private static EnterpriseActivityRecord getEnterpriseActivity(DSLContext dslContext, Integer domainId, Employee employee) {
		return 
		dslContext
		.select()
		.from(ENTERPRISE_ACTIVITY)
		.innerJoin(ENTERPRISE_CCC).onKey()
		.where(ENTERPRISE_ACTIVITY.DOMAIN.eq(domainId))
		.and(ENTERPRISE_CCC.CCC.eq(employee.getCcc()))
		.limit(1)
		.fetchOptionalInto(ENTERPRISE_ACTIVITY)
		.orElseGet(() ->
			// No enterprise activity for ccc, return any 
			dslContext
			.select()
			.from(ENTERPRISE_ACTIVITY)
			.innerJoin(ENTERPRISE_CCC).onKey()
			.where(ENTERPRISE_ACTIVITY.DOMAIN.eq(domainId))
			.limit(1)
			.fetchOptionalInto(ENTERPRISE_ACTIVITY)
			.orElseGet(() -> 
				// No enterprise activity, return new one
				dslContext
				.insertInto(ENTERPRISE_ACTIVITY)
				
				.set(ENTERPRISE_ACTIVITY.TYPE, (byte)0)
				.set(ENTERPRISE_ACTIVITY.DESCRIPTION, "")
				
				.set(ENTERPRISE_ACTIVITY.DOMAIN, domainId)
				.set(ENTERPRISE_ACTIVITY.PRINCIPAL, (byte) 1)
				.set(ENTERPRISE_ACTIVITY.ENTERPRISE, getEnterprise(dslContext, domainId).getRegistry())
				.returning()
				.fetchOne()
			)
		);
	}
	
	private static RegistryRecord getPerson(DSLContext dslContext, Integer domainId, Employee employee) {
		return dslContext
		.select()
		.from(PERSON)
		.innerJoin(REGISTRY).onKey()
		.where(PERSON.SOCIAL_SECURITY_NUM.eq(employee.getNaf()))
		.and(PERSON.DOMAIN.eq(domainId))
//		.fetchOptionalInto(REGISTRY)
		.fetchStreamInto(REGISTRY)
		.sorted((r1,r2)-> comparePerson(r1, r2, employee))
		.findFirst()
		.orElseGet( () -> {
			
			String dni = trim(employee.getDni());
			
			InsertSetMoreStep<RegistryRecord> insertRegistry = 
			dslContext
			.insertInto(REGISTRY)
			.set(REGISTRY.TYPE,  (byte) 0 )
			.set(REGISTRY.DOMAIN, domainId)
			.set(REGISTRY.DOCUMENT, dni)
			.set(REGISTRY.DOCUMENT_TYPE, getDniType(dni))
			.set(REGISTRY.DOCUMENT_COUNTRY, getDniCountry(dni))
			.set(REGISTRY.NATIONALITY,  getDniCountry(dni));
			employee.getName().ifPresent(name -> insertRegistry.set(REGISTRY.NAME, name));
		
			RegistryRecord registryRecord = insertRegistry.returning().fetchOne();
			
			Integer geozoneId = getGeozone(dslContext, domainId, employee.getCcc()).map(GeozoneRecord::getId).orElse(null);
			
//			SelectConditionStep<Record1<Integer>> geozoneId = 
//			DSL
//			.select(GEOZONE.ID)
//			.from(GEOZONE)
//			.where(GEOZONE.DOMAIN.eq(domainId))
//			.and(GEOZONE.CODE.eq(AonStringUtils.substring(employee.getCcc(),0,2)))
//			;
			
			dslContext
			.insertInto(RADDRESS)
			.set(RADDRESS.DOMAIN, domainId)
			.set(RADDRESS.REGISTRY, registryRecord.getId())
			.set(RADDRESS.GEOZONE, geozoneId )
			.execute();
			;
			
			
			employee.getPhone().ifPresent(phone ->{
//				dslContext
//				.insertInto(RMEDIA)
//				.set(RMEDIA.DOMAIN, domainId)
//				.set(RMEDIA.REGISTRY, registryRecord.getId())
//				.set(RMEDIA.MEDIA, MediaType.CELLULAR.value())
//				.set(RMEDIA.VALUE, phone )
//				.execute();
//				;
			});
			
			InsertSetMoreStep<PersonRecord> insertPerson = 
			dslContext
			.insertInto(PERSON)
			.set(PERSON.DOMAIN, domainId)
			.set(PERSON.REGISTRY, registryRecord.getId())
			.set(PERSON.SOCIAL_SECURITY_NUM, employee.getNaf());
			
			employee.getName().ifPresent(name -> {
				getName(name).ifPresent(s -> insertPerson.set(PERSON.NAME,s));
				getFirstSurname(name).ifPresent( s -> insertPerson.set(PERSON.FIRST_SURNAME, s));
				getSecondSurname(name).ifPresent( s -> insertPerson.set(PERSON.SECOND_SURNAME, s));
			});
			
			employee.getBirthDate().ifPresent(
			birthDate -> insertPerson.set(PERSON.BIRTH_DATE, toSql(birthDate)));
			
			employee.getSex().map(EmployeeDAO::getGender).ifPresentOrElse(
			gender -> insertPerson.set(PERSON.GENDER, gender.value()), 
			() -> insertPerson.set(PERSON.GENDER, Gender.UNKNOWN.value()));
			

			insertPerson.execute();
			
			return registryRecord;

		});
	}
	
	private static void checkPersonName(DSLContext dslContext, Integer personRegistry, Employee employee) {
		PersonRecord personRecord = dslContext.selectFrom(PERSON).where(PERSON.REGISTRY.eq(personRegistry)).fetchOne();
		
		employee.getName().ifPresent(fullName -> {
			getName(fullName).ifPresent(name -> {
				if (!AonStringUtils.equalsIgnoreCase(personRecord.getName(), name))
					dslContext.update(PERSON).set(PERSON.NAME, name).where(PERSON.REGISTRY.eq(personRecord.get(PERSON.REGISTRY))).execute();
			});
			getFirstSurname(fullName).ifPresent(firstSurname -> {
				if (!AonStringUtils.equalsIgnoreCase(personRecord.getFirstSurname(), firstSurname))
					dslContext.update(PERSON).set(PERSON.FIRST_SURNAME, firstSurname).where(PERSON.REGISTRY.eq(personRecord.get(PERSON.REGISTRY))).execute();
			});
			getSecondSurname(fullName).ifPresent(secondSurname -> {
				if (!AonStringUtils.equalsIgnoreCase(personRecord.getSecondSurname(), secondSurname))
					dslContext.update(PERSON).set(PERSON.SECOND_SURNAME, secondSurname).where(PERSON.REGISTRY.eq(personRecord.get(PERSON.REGISTRY))).execute();
			});
		});
	}
	
	private static int comparePerson(RegistryRecord r1, RegistryRecord r2, Employee employee) {
		String dni = trim(employee.getDni());
		Optional<String> nameOpt = employee.getName();
		
		if(r1.getType()!=null && r2.getType()==null) {
			return 1;
		} else if(r2.getType()!=null && r1.getType()==null) {
			return -1;
		} else if(AonStringUtils.equals(dni, r1.getDocument()) && !AonStringUtils.equals(dni, r2.getDocument())) {
			return 1;
		} else if(AonStringUtils.equals(dni, r2.getDocument()) && !AonStringUtils.equals(dni, r1.getDocument())) {
			return -1;
		} else if(nameOpt.isPresent()) {
			if(AonStringUtils.getLevenshteinDistance(nameOpt.get(), r1.getName()) < AonStringUtils.getLevenshteinDistance(nameOpt.get(), r2.getName())) {
				return 1;
			} else {
				return -1;
			}
		}
		
		return 0;
	}

	private static Result<ContractRecord> getContracts(DSLContext dslContext, String domainName, String ccc, String naf,
			java.sql.Date startDate, java.sql.Date endDate) {
		return dslContext
		.select()
		.from(DOMAIN)
		.innerJoin(PERSON).on(PERSON.DOMAIN.eq(DOMAIN.ID))
		.innerJoin(CONTRACT).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.innerJoin(ENTERPRISE_CCC).on(ENTERPRISE_CCC.ID.eq(CONTRACT.ENTERPRISE_CCC))
		.where(DOMAIN.NAME.eq(domainName))
		.and(PERSON.SOCIAL_SECURITY_NUM.eq(naf))
		.and(ENTERPRISE_CCC.CCC.eq(ccc))
		.and(DSL.condition(endDate == null ).or(CONTRACT.START_DATE.le(endDate)))
		.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(startDate)))
		.fetchInto(CONTRACT);
	}


	private static Bonus [] setBonuses(DSLContext dslContext, String domainName, String ccc, String naf, java.sql.Date startDate, java.sql.Date endDate, Bonus ...bonuses) {
		
		List<ContractRecord> contractRecords = 
		getContracts(dslContext, domainName, ccc, naf, startDate, endDate)
		;
		
		for ( ContractRecord contractRecord : contractRecords  ) {
			Bonus contractBonuses [] = 
			Arrays.stream(bonuses)
			.filter( b -> intersects(contractRecord, b) )
			.toArray(Bonus[]::new);
			if ( contractBonuses.length >= 0 ) {
				setBonuses(dslContext, domainName, startDate, endDate, contractRecord, contractBonuses);
			}
			
		}
		
		return bonuses;
	}

	private static Deduction [] setDeductions(DSLContext dslContext, String domainName, String ccc, String naf, java.sql.Date startDate, java.sql.Date endDate, Deduction ...deductions) {
		
		List<ContractRecord> contractRecords = 
		getContracts(dslContext, domainName, ccc, naf, startDate, endDate)
		;
		
		for ( ContractRecord contractRecord : contractRecords  ) {
			Deduction contractDeductions [] = 
			Arrays.stream(deductions)
			.filter( b -> intersects(contractRecord, b) )
			.toArray(Deduction[]::new);
			if ( contractDeductions.length >= 0 ) {
				setDeductions(dslContext, domainName, startDate, endDate, contractRecord, contractDeductions);
			}
			
		}
		
		return deductions;
	}

	private static Cost [] setCosts(DSLContext dslContext, String domainName, String ccc, String naf, java.sql.Date startDate, java.sql.Date endDate, Cost ...costs) {
		
		List<ContractRecord> contractRecords = 
		getContracts(dslContext, domainName, ccc, naf, startDate, endDate)
		;
		
		for ( ContractRecord contractRecord : contractRecords  ) {
			Cost contractCosts [] = 
			Arrays.stream(costs)
			.filter( c -> intersects(contractRecord, c) )
			.toArray(Cost[]::new);
			if ( contractCosts.length >= 0 ) {
				setCosts(dslContext, domainName, startDate, endDate, contractRecord, contractCosts);
			}
			
		}
		
		return costs;
	}

	private static ContractData [] setData(DSLContext dslContext, String domainName, String ccc, String naf,java.sql.Date startDate, java.sql.Date endDate, ContractData ...datas) {
		
		List<ContractRecord> contractRecords = 
		getContracts(dslContext, domainName, ccc, naf, startDate, endDate)
		;
		
		for ( ContractRecord contractRecord : contractRecords  ) {
			ContractData contractDatas [] = 
			Arrays.stream(datas)
			.filter( b -> intersects(contractRecord, b) )
			.toArray(ContractData[]::new);
			if ( contractDatas.length >= 0 ) {
				setData(dslContext, domainName, startDate, endDate, contractRecord, contractDatas);
			}
			
		}
		
		return datas;
	}

	private static ContractData [] setContractData(DSLContext dslContext, String domainName, ContractFilter filter, ContractData ...contractDatas) {
		if ( contractDatas == null )
			return new ContractData[0];
		if ( contractDatas.length == 0 )
			return new ContractData[0];
			
		// TODO: Support multiple names ?
		
		Condition[] conditions = new ContractPropertiesDAO().getConditions(filter);
		Set<String> names = Arrays.stream(contractDatas).map(c -> c.getName()).collect(Collectors.toSet());
		
		// merge contiguous contract data
		ContractData mergedContractDatas [] = join(contractDatas);
		
		dslContext
		.select()
		.from(CONTRACT)
		.where(conditions)
		.fetchStreamInto(CONTRACT)
		.forEach(contractRecord -> {
			
			
			
			for (ContractData contractData : mergedContractDatas) {
				dslContext
				.select()
				.from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractRecord.getId()))
				.and(CONTRACT_DATA.NAME.eq(contractData.getName()))
				.and(CONTRACT_DATA.END_DATE.isNull().or(CONTRACT_DATA.END_DATE.ge(toSql(contractData.getStartDate()))))
				.and(DSL.condition(contractData.getEndDate()==null).or(CONTRACT_DATA.START_DATE.le(toSql(contractData.getEndDate()))))
				.fetchStreamInto(CONTRACT_DATA)
				.forEach(contractDataRecord -> {
					if ( ( compare(contractData.getStartDate(),  contractDataRecord.getStartDate()) <= 0 ) &&
						( compare(contractData.getEndDate(),  contractDataRecord.getEndDate()) >= 0 ) ) {
						contractDataRecord.delete();
					}else if ( compare(contractData.getStartDate(),  contractDataRecord.getStartDate()) <= 0 ) {
						if ( AonStringUtils.equals(contractData.getExpression(), contractDataRecord.getExpression())) {
							contractData.setEndDate(contractDataRecord.getEndDate());
							contractDataRecord.delete();
						} else {
							contractDataRecord.setStartDate(toSql(AonDateUtils.add(contractData.getEndDate(), DAY_OF_MONTH,1)));
							contractDataRecord.update();
						}
					} else if ( compare(contractData.getEndDate(),  contractDataRecord.getEndDate()) >= 0  ) {
						if ( AonStringUtils.equals(contractData.getExpression(), contractDataRecord.getExpression())) {
							contractData.setStartDate(contractDataRecord.getStartDate());
							contractDataRecord.delete();
							
						} else {
							contractDataRecord.setEndDate(toSql(AonDateUtils.add(contractData.getStartDate(), DAY_OF_MONTH,-1)));
							contractDataRecord.update();
						}
					}else {
						if ( AonStringUtils.equals(contractData.getExpression(), contractDataRecord.getExpression())) {
							contractData.setStartDate(contractDataRecord.getStartDate());
							contractData.setEndDate(contractDataRecord.getEndDate());
							contractDataRecord.delete();
						} else {
							ContractDataRecord newContractDataRecord = contractDataRecord.copy();
							newContractDataRecord.setEndDate(toSql(AonDateUtils.add(contractData.getStartDate(), DAY_OF_MONTH,-1)));
							newContractDataRecord.insert();
							contractDataRecord.setStartDate(toSql(AonDateUtils.add(contractData.getEndDate(), DAY_OF_MONTH,1)));
							contractDataRecord.update();
						}
					}
				});
				;

				dslContext
				.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.CONTRACT, contractRecord.getId())
				.set(CONTRACT_DATA.DOMAIN, contractRecord.getDomain())
				.set(CONTRACT_DATA.NAME, contractData.getName())
				.set(CONTRACT_DATA.EXPRESSION, contractData.getExpression())
				.set(CONTRACT_DATA.START_DATE, toSql(contractData.getStartDate()))
				.set(CONTRACT_DATA.END_DATE, toSql(contractData.getEndDate()))
				.execute();
				;
			
			}
		})
		;
		
		return contractDatas; // TODO: From data base perhaps ?
	}

	private static ContractData [] join(ContractData  contractDatas []) {
		Arrays.sort(contractDatas,  (d1,d2) -> d1.getStartDate().compareTo(d2.getStartDate()));
		LinkedList<ContractData> list = new LinkedList<ContractData>();
		list.add(copy(contractDatas[0]));
		
		for (int i = 0; i < contractDatas.length; i++) {
			ContractData last = list.getLast();
			ContractData next = contractDatas[i];
			Date startDate = AonDateUtils.add(next.getStartDate(), DAY_OF_MONTH, -1); 
			if ( AonStringUtils.equals(last.getExpression(), next.getExpression()) &&
					compare(startDate, last.getEndDate()) <= 0 ) {
				last.setEndDate(max(last.getEndDate(), next.getEndDate()));
			}
			else {
				list.add(copy(next));
			}
		}
		
		return list.toArray(ContractData[]::new);
	}
	
	private static ContractData copy(ContractData src) {
		return new ContractData()
				.setName(src.getName())
				.setExpression(src.getExpression())
				.setStartDate(src.getStartDate())
				.setEndDate(src.getEndDate());
	}
	
	
	private static <T  extends HasStartDate & HasEndDate >boolean intersects(ContractRecord r, T b) {
		return compare(max(r.getStartDate(), b.getStartDate()), min(r.getEndDate(), b.getEndDate())) <= 0;
	}
	
	
	private static Bonus [] getBonuses(DSLContext dslContext, String domainName, Integer contractId) {
		
		return 
		dslContext
		.select()
		.from(DOMAIN)
		.innerJoin(CONTRACT_BONUS).on(DOMAIN.ID.eq(CONTRACT_BONUS.DOMAIN))
		.innerJoin(CONTRACT).on(CONTRACT_BONUS.CONTRACT.eq(CONTRACT.ID))
		.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.innerJoin(ENTERPRISE_CCC).on(CONTRACT.ENTERPRISE_CCC.eq(ENTERPRISE_CCC.ID))
		.leftJoin(BONUS_CONCEPT).on(CONTRACT_BONUS.BONUS_CONCEPT.eq(BONUS_CONCEPT.ID))
		.where(DOMAIN.NAME.eq(domainName))
		.and(CONTRACT.ID.eq(contractId))
		.fetchStream()
		.map(record -> 
		new Bonus()
		.setId(get(record, CONTRACT_BONUS.ID))
		.setDomain(get(record, CONTRACT_BONUS.DOMAIN))
		.setEndDate(get(record, CONTRACT_BONUS.END_DATE))
		.setStartDate(get(record, CONTRACT_BONUS.START_DATE))
		.setExpression(get(record, CONTRACT_BONUS.EXPRESSION, BONUS_CONCEPT.EXPRESSION))
		.setDescription(get(record, CONTRACT_BONUS.DESCRIPTION, BONUS_CONCEPT.DESCRIPTION))
		.setType(valueOf(get(record, BONUS_CONCEPT.TYPE), BonusType.class))
		).toArray(Bonus[]::new);
		
	}



	private static Bonus [] setBonuses(DSLContext dslContext, String domainName, java.sql.Date startDate, java.sql.Date endDate, ContractRecord contractRecord, Bonus ...bonuses) {
		
		List<Bonus> bonusList = new ArrayList<Bonus>(bonuses.length);
		Arrays.stream(bonuses).forEach( bonus -> bonusList.add(bonus));
		
		dslContext
		.select()
		.from(CONTRACT_BONUS)
		.where(CONTRACT_BONUS.CONTRACT.eq(contractRecord.getId()))
		.and(DSL.condition(endDate == null ).or(CONTRACT_BONUS.START_DATE.le(endDate)))
		.and(CONTRACT_BONUS.END_DATE.isNull().or(CONTRACT_BONUS.END_DATE.ge(startDate)))
		.fetchStreamInto(CONTRACT_BONUS)
		.forEach( contractBonus -> {
			//if ( remove(bonusList, contractBonus) ) {
			//	return;
			//}
			
			if ( compare(contractBonus.getStartDate(), startDate) >= 0 ) { 
				if ( compare(contractBonus.getEndDate(), endDate ) <= 0 ) {
					// contract bonus starts after start date and ends before end date. So delete it.
					contractBonus.delete();
				}
				else { 
					// contract bonus ends after end date. So now starts just after end date. 
					contractBonus.setStartDate(AonDateUtils.add(endDate, Calendar.DAY_OF_MONTH, 1));
					contractBonus.update();
				}
			} else {
				if ( compare(contractBonus.getEndDate(), endDate ) <= 0 ) {
					// contract bonus starts before start date and ends before end date. So ends just before start date.
					contractBonus.setEndDate(AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, -1));
					contractBonus.update();
				} else {
					// contract bonus starts before start date and ends after end date. So we need to split it.
					ContractBonusRecord leftContractBonus = contractBonus;
					ContractBonusRecord rightContractBonus = contractBonus.copy();
					leftContractBonus.setEndDate(AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, -1));
					leftContractBonus.update();
					rightContractBonus.setStartDate(AonDateUtils.add(endDate, Calendar.DAY_OF_MONTH, 1));
					rightContractBonus.insert();
				}
			}
				
		});
		;
		
		for (Bonus bonus : bonusList) {
			dslContext
			.insertInto(CONTRACT_BONUS)
			.set(CONTRACT_BONUS.DOMAIN, contractRecord.getDomain())
			.set(CONTRACT_BONUS.CONTRACT, contractRecord.getId())
			.set(CONTRACT_BONUS.START_DATE, toSql(bonus.getStartDate()))
			.set(CONTRACT_BONUS.END_DATE, toSql(bonus.getEndDate()))
			.set(CONTRACT_BONUS.DESCRIPTION, bonus.getDescription())
			.set(CONTRACT_BONUS.EXPRESSION, bonus.getExpression())
			.execute()
			;
		}
		
		
		
		return bonuses;
		
	}
	
	private static Deduction [] getDeductions(DSLContext dslContext, String domainName, Integer contractId) {
		
		return 
		dslContext
		.select()
		.from(DOMAIN)
		.innerJoin(CONTRACT_DEDUCTION).on(DOMAIN.ID.eq(CONTRACT_DEDUCTION.DOMAIN))
		.innerJoin(CONTRACT).on(CONTRACT_DEDUCTION.CONTRACT.eq(CONTRACT.ID))
		.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.innerJoin(ENTERPRISE_CCC).on(CONTRACT.ENTERPRISE_CCC.eq(ENTERPRISE_CCC.ID))
		.leftJoin(DEDUCTION_CONCEPT).on(CONTRACT_DEDUCTION.DEDUCTION_CONCEPT.eq(DEDUCTION_CONCEPT.ID))
		.where(DOMAIN.NAME.eq(domainName))
		.and(CONTRACT.ID.eq(contractId))
		.fetchStream()
		.map(record -> 
		new Deduction()
		.setId(get(record, CONTRACT_DEDUCTION.ID))
		.setName(get(record, DEDUCTION_CONCEPT.CODE))
		.setDomain(get(record, CONTRACT_DEDUCTION.DOMAIN))
		.setEndDate(get(record, CONTRACT_DEDUCTION.END_DATE))
		.setStartDate(get(record, CONTRACT_DEDUCTION.START_DATE))
		.setExpression(get(record, CONTRACT_DEDUCTION.EXPRESSION, DEDUCTION_CONCEPT.EXPRESSION))
		.setDescription(get(record, CONTRACT_DEDUCTION.DESCRIPTION, DEDUCTION_CONCEPT.DESCRIPTION))
		.setType(valueOf(get(record, CONTRACT_DEDUCTION.TYPE, DEDUCTION_CONCEPT.TYPE),DeductionType.class))
		).toArray(Deduction[]::new);
		
	}

	private static Deduction [] setDeductions(DSLContext dslContext, String domainName, java.sql.Date startDate, java.sql.Date endDate, ContractRecord contractRecord, Deduction ...deductions) {
		
		List<Deduction> deductionsList = new ArrayList<Deduction>(deductions.length);
		Arrays.stream(deductions).forEach( deduction -> deductionsList.add(deduction));
		
		dslContext
		.select()
		.from(CONTRACT_DEDUCTION)
		.where(CONTRACT_DEDUCTION.CONTRACT.eq(contractRecord.getId()))
		.and(DSL.condition(endDate == null ).or(CONTRACT_DEDUCTION.START_DATE.le(endDate)))
		.and(CONTRACT_DEDUCTION.END_DATE.isNull().or(CONTRACT_DEDUCTION.END_DATE.ge(startDate)))
		.fetchStream()
		.filter( EmployeeDAO::filter )
		.forEach( r -> {
			
			//DeductionConceptRecord deductionConcept = r.into(DEDUCTION_CONCEPT); 
			ContractDeductionRecord contractDeduction = r.into(CONTRACT_DEDUCTION); 
			
			//if ( remove(deductionsList, contractDeduction) ) {
			//	return;
			//}
			
			if ( compare(contractDeduction.getStartDate(), startDate) >= 0 ) { 
				if ( compare(contractDeduction.getEndDate(), endDate ) <= 0 ) {
					// contract bonus starts after start date and ends before end date. So delete it.
					contractDeduction.delete();
				}
				else { 
					// contract bonus ends after end date. So now starts just after end date. 
					contractDeduction.setStartDate(AonDateUtils.add(endDate, Calendar.DAY_OF_MONTH, 1));
					contractDeduction.update();
				}
			} else {
				if ( compare(contractDeduction.getEndDate(), endDate ) <= 0 ) {
					// contract bonus starts before start date and ends before end date. So ends just before start date.
					contractDeduction.setEndDate(AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, -1));
					contractDeduction.update();
				} else {
					// contract bonus starts before start date and ends after end date. So we need to split it.
					ContractDeductionRecord leftContractDeduction = contractDeduction;
					ContractDeductionRecord rightContractDeduction = contractDeduction.copy();
					leftContractDeduction.setEndDate(AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, -1));
					leftContractDeduction.update();
					rightContractDeduction.setStartDate(AonDateUtils.add(endDate, Calendar.DAY_OF_MONTH, 1));
					rightContractDeduction.insert();
				}
			}
				
		});
		;
		
		for (Deduction deduction : deductionsList) {
			
			SelectLimitPercentStep<Record1<Integer>> deductionConcept = 
			dslContext.select(DEDUCTION_CONCEPT.ID)
			.from(DEDUCTION_CONCEPT)
			.where(DEDUCTION_CONCEPT.DOMAIN.eq(0))
			.and(DEDUCTION_CONCEPT.CODE.eq(deduction.getName()))
			.limit(1)
			;
			
			dslContext
			.insertInto(CONTRACT_DEDUCTION)
			.set(CONTRACT_DEDUCTION.DOMAIN, contractRecord.getDomain())
			.set(CONTRACT_DEDUCTION.CONTRACT, contractRecord.getId())
			.set(CONTRACT_DEDUCTION.START_DATE, toSql(deduction.getStartDate()))
			.set(CONTRACT_DEDUCTION.END_DATE, toSql(deduction.getEndDate()))
			.set(CONTRACT_DEDUCTION.DESCRIPTION, deduction.getDescription())
			.set(CONTRACT_DEDUCTION.EXPRESSION, deduction.getExpression())
			.set(CONTRACT_DEDUCTION.TYPE, valueOf(deduction.getType()))
			.set(CONTRACT_DEDUCTION.DEDUCTION_CONCEPT, deductionConcept)
			.execute()
			;
		}
		
		
		
		return deductions;
		
	}

	private static Cost [] getCosts(DSLContext dslContext, String domainName, Integer contractId) {
		
		return 
		dslContext
		.select()
		.from(DOMAIN)
		.innerJoin(CONTRACT_COST).on(DOMAIN.ID.eq(CONTRACT_COST.DOMAIN))
		.innerJoin(CONTRACT).on(CONTRACT_COST.CONTRACT.eq(CONTRACT.ID))
		.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.innerJoin(ENTERPRISE_CCC).on(CONTRACT.ENTERPRISE_CCC.eq(ENTERPRISE_CCC.ID))
		.where(DOMAIN.NAME.eq(domainName))
		.and(CONTRACT.ID.eq(contractId))
		.fetchStreamInto(CONTRACT_COST)
		.map(cost -> 
		new Cost()
		.setId(cost.getId())
		.setName(cost.getCode())
		.setDomain(cost.getDomain())
		.setEndDate(cost.getEndDate())
		.setStartDate(cost.getStartDate())
		.setExpression(cost.getExpression())
		.setDescription(cost.getDescription())
		.setType(valueOf(cost.getType(), DeductionType.class))
		).toArray(Cost[]::new);
		
	}

	private static Cost [] setCosts(DSLContext dslContext, String domainName, java.sql.Date startDate, java.sql.Date endDate, ContractRecord contractRecord, Cost ...costs) {
		
		List<Cost> costsList = new ArrayList<Cost>(costs.length);
		Arrays.stream(costs).forEach( deduction -> costsList.add(deduction));
		
		dslContext
		.select()
		.from(CONTRACT_COST)
		.where(CONTRACT_COST.CONTRACT.eq(contractRecord.getId()))
		.and(DSL.condition(endDate == null ).or(CONTRACT_COST.START_DATE.le(endDate)))
		.and(CONTRACT_COST.END_DATE.isNull().or(CONTRACT_COST.END_DATE.ge(startDate)))
		.fetchStream()
		.filter( EmployeeDAO::filter )
		.forEach( r -> {
			
			//DeductionConceptRecord deductionConcept = r.into(DEDUCTION_CONCEPT); 
			ContractCostRecord contractCost = r.into(CONTRACT_COST); 
			
			//if ( remove(costsList, contractCost) ) {
			//	return;
			//}
			
			if ( compare(contractCost.getStartDate(), startDate) >= 0 ) { 
				if ( compare(contractCost.getEndDate(), endDate ) <= 0 ) {
					// contract bonus starts after start date and ends before end date. So delete it.
					contractCost.delete();
				}
				else { 
					// contract bonus ends after end date. So now starts just after end date. 
					contractCost.setStartDate(AonDateUtils.add(endDate, Calendar.DAY_OF_MONTH, 1));
					contractCost.update();
				}
			} else {
				if ( compare(contractCost.getEndDate(), endDate ) <= 0 ) {
					// contract bonus starts before start date and ends before end date. So ends just before start date.
					contractCost.setEndDate(AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, -1));
					contractCost.update();
				} else {
					// contract bonus starts before start date and ends after end date. So we need to split it.
					ContractCostRecord leftContractCost = contractCost;
					ContractCostRecord rightContractCost = contractCost.copy();
					leftContractCost.setEndDate(AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, -1));
					leftContractCost.update();
					rightContractCost.setStartDate(AonDateUtils.add(endDate, Calendar.DAY_OF_MONTH, 1));
					rightContractCost.insert();
				}
			}
				
		});
		;
		
		for (Cost cost : costsList) {
			
			dslContext
			.insertInto(CONTRACT_COST)
			.set(CONTRACT_COST.DOMAIN, contractRecord.getDomain())
			.set(CONTRACT_COST.CONTRACT, contractRecord.getId())
			.set(CONTRACT_COST.START_DATE, toSql(cost.getStartDate()))
			.set(CONTRACT_COST.END_DATE, toSql(cost.getEndDate()))
			.set(CONTRACT_COST.DESCRIPTION, cost.getDescription())
			.set(CONTRACT_COST.EXPRESSION, cost.getExpression())
			.set(CONTRACT_COST.TYPE, valueOf(cost.getType()))
			.set(CONTRACT_COST.CODE, cost.getName())
			.execute()
			;
		}
		
		return costs;
		
	}
	
	private static ContractData [] getData(DSLContext dslContext, String domainName, Integer contractId) {
		
		return 
		dslContext
		.select()
		.from(DOMAIN)
		.innerJoin(CONTRACT_DATA).on(DOMAIN.ID.eq(CONTRACT_DATA.DOMAIN))
		.innerJoin(CONTRACT).on(CONTRACT_DATA.CONTRACT.eq(CONTRACT.ID))
		.innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
		.innerJoin(ENTERPRISE_CCC).on(CONTRACT.ENTERPRISE_CCC.eq(ENTERPRISE_CCC.ID))
		.where(DOMAIN.NAME.eq(domainName))
		.and(CONTRACT.ID.eq(contractId))
		.fetchStreamInto(CONTRACT_DATA)
		.map(data -> 
		new ContractData()
		.setId(data.getId())
		.setName(data.getName())
		.setDomain(data.getDomain())
		.setEndDate(data.getEndDate())
		.setStartDate(data.getStartDate())
		.setExpression(data.getExpression())
		).toArray(ContractData[]::new);
		
	}

	private static ContractData [] setData(DSLContext dslContext, String domainName, java.sql.Date startDate, java.sql.Date endDate, ContractRecord contractRecord, ContractData ...datas) {
		
		List<ContractData> datasList  = Arrays.asList(datas);
		
		String [] names = datasList.stream().map(ContractData::getName).toArray(String[]::new);
		
		dslContext
		.select()
		.from(CONTRACT_DATA)
		.where(CONTRACT_DATA.CONTRACT.eq(contractRecord.getId()))
		.and(CONTRACT_DATA.NAME.in(names))
		.and(DSL.condition(endDate == null ).or(CONTRACT_DATA.START_DATE.le(endDate)))
		.and(CONTRACT_DATA.END_DATE.isNull().or(CONTRACT_DATA.END_DATE.ge(startDate)))
		
		.fetchStream()
		.filter( EmployeeDAO::filter )
		.forEach( r -> {
			
			ContractDataRecord contractData = r.into(CONTRACT_DATA); 
			
			if ( compare(contractData.getStartDate(), startDate) >= 0 ) { 
				if ( compare(contractData.getEndDate(), endDate ) <= 0 ) {
					// contract data starts after start date and ends before end date. So delete it.
					contractData.delete();
				}
				else { 
					// contract data ends after end date. So now starts just after end date. 
					contractData.setStartDate(AonDateUtils.add(endDate, Calendar.DAY_OF_MONTH, 1));
					contractData.update();
				}
			} else {
				if ( compare(contractData.getEndDate(), endDate ) <= 0 ) {
					// contract data starts before start date and ends before end date. So ends just before start date.
					contractData.setEndDate(AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, -1));
					contractData.update();
				} else {
					// contract data starts before start date and ends after end date. So we need to split it.
					ContractDataRecord leftContractCost = contractData;
					ContractDataRecord rightContractCost = contractData.copy();
					leftContractCost.setEndDate(AonDateUtils.add(startDate, Calendar.DAY_OF_MONTH, -1));
					leftContractCost.update();
					rightContractCost.setStartDate(AonDateUtils.add(endDate, Calendar.DAY_OF_MONTH, 1));
					rightContractCost.insert();
				}
			}
				
		});
		;
		
		for (ContractData data : datasList) {
			
			dslContext
			.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, contractRecord.getDomain())
			.set(CONTRACT_DATA.CONTRACT, contractRecord.getId())
			.set(CONTRACT_DATA.START_DATE, toSql(data.getStartDate()))
			.set(CONTRACT_DATA.END_DATE, toSql(data.getEndDate()))
			.set(CONTRACT_DATA.NAME, data.getName())
			.set(CONTRACT_DATA.EXPRESSION, data.getExpression())
			.execute()
			;
		}
		
		return datas;
		
	}

	private static <T extends Enum<?>> Byte valueOf(T t) {
		if ( t == null )
			return null;
		
		return (byte) t.ordinal();
	}

	private static <T extends Enum<?>> T valueOf(Byte b, Class<T> clazz ) {
		if ( b == null )
			return null;
		if ( b < 0 )
			return null;
		
		T constants [] = clazz.getEnumConstants();
		
		if ( b >= constants.length )
			return null;
		
		return clazz.getEnumConstants()[b];
	}
	
	@SafeVarargs
	private static <T> T get(Record record, Field<T> ...fields) {
		for (Field<T> field : fields) {
			T t = record.get(field);
			if ( t != null )
				return t;
		}
		return null;
	}

	private static boolean filter( Record r ) {
		try {
			
			Byte type = r.get(CONTRACT_DEDUCTION.TYPE);
			if ( type == null )
				type = r.get(DEDUCTION_CONCEPT.TYPE);
			
			if ( type == null )
				return true;
			
			switch (DeductionType.values()[type]) {
			case IRPF:
			case ADVANCE_PAYMENT:
				return false;

			default:
				return true;
			}
		} catch ( Throwable t ) {
			return true;
		}
	}

	private static boolean remove( List<Deduction> list, ContractDeductionRecord r ) {
		for (int i = 0; i < list.size(); i++) {
			Deduction b = list.get(i);
			if ( !equals(b, r) )
				continue;
			list.remove(i);
			return true;
				
		}	
		return false; 
	}

	private static boolean remove( List<Bonus> list, ContractBonusRecord r ) {
		for (int i = 0; i < list.size(); i++) {
			Bonus b = list.get(i);
			if ( !equals(b, r) )
				continue;
			list.remove(i);
			return true;
				
		}	
		return false; 
	}
	
	private static boolean remove( List<Cost> list, ContractCostRecord r ) {
		for (int i = 0; i < list.size(); i++) {
			Cost c = list.get(i);
			if ( !equals(c, r) )
				continue;
			list.remove(i);
			return true;
				
		}	
		return false; 
	}

	private static boolean equals( Bonus bonus, ContractBonusRecord record) {
		if( equals(bonus.getStartDate(),record.getStartDate())
				&& equals(bonus.getEndDate(),record.getEndDate())
				&& AonStringUtils.equals(getExpression(bonus), getExpression(record)))
			return true;
		if (  AonStringUtils.equals(getExpression(bonus), getExpression(record)) ){
			if ((compare( record.getStartDate(), bonus.getStartDate()) <= 0 )
				&& (compare( record.getEndDate(), bonus.getEndDate()) >= 0 )
				&& (AonDateUtils.get(bonus.getStartDate(), Calendar.DAY_OF_MONTH) == 1 )
				&& (bonus.getEndDate() == null || AonDateUtils.get(bonus.getEndDate(), Calendar.DAY_OF_MONTH) == AonDateUtils.getMax(bonus.getEndDate(), Calendar.DAY_OF_MONTH) ))
				return true;
		}
		
		
		return false;
				
	}
	
	private static boolean equals( Deduction deduction, ContractDeductionRecord record) {
		if( equals(deduction.getStartDate(),record.getStartDate())
				&& equals(deduction.getEndDate(),record.getEndDate())
				&& AonStringUtils.equals(getExpression(deduction), getExpression(record)))
			return true;
		if (  AonStringUtils.equals(getExpression(deduction), getExpression(record)) ){
			if ((compare( record.getStartDate(), deduction.getStartDate()) <= 0 )
				&& (compare( record.getEndDate(), deduction.getEndDate()) >= 0 )
				&& (AonDateUtils.get(deduction.getStartDate(), Calendar.DAY_OF_MONTH) == 1 )
				&& (deduction.getEndDate() == null || AonDateUtils.get(deduction.getEndDate(), Calendar.DAY_OF_MONTH) == AonDateUtils.getMax(deduction.getEndDate(), Calendar.DAY_OF_MONTH) ))
				return true;
		}
		
		
		return false;
				
	}

	private static boolean equals( Cost cost, ContractCostRecord record) {
		if( equals(cost.getStartDate(),record.getStartDate())
				&& equals(cost.getEndDate(),record.getEndDate())
				&& AonStringUtils.equals(getExpression(cost), getExpression(record)))
			return true;
		if (  AonStringUtils.equals(getExpression(cost), getExpression(record)) ){
			if ((compare( record.getStartDate(), cost.getStartDate()) <= 0 )
				&& (compare( record.getEndDate(), cost.getEndDate()) >= 0 )
				&& (AonDateUtils.get(cost.getStartDate(), Calendar.DAY_OF_MONTH) == 1 )
				&& (cost.getEndDate() == null || AonDateUtils.get(cost.getEndDate(), Calendar.DAY_OF_MONTH) == AonDateUtils.getMax(cost.getEndDate(), Calendar.DAY_OF_MONTH) ))
				return true;
		}
		
		
		return false;
				
	}

	private static String getExpression(Bonus bonus) {
		return Optional.ofNullable(bonus.getExpression()).map( b -> b.replaceAll("/\\*[^\\*]*\\*/", "")).orElse("");
	}

	private static String getExpression(Deduction deduction) {
		return Optional.ofNullable(deduction.getExpression()).map( b -> b.replaceAll("/\\*[^\\*]*\\*/", "")).orElse("");
	}

	private static String getExpression(Cost cost) {
		return Optional.ofNullable(cost.getExpression()).map( b -> b.replaceAll("/\\*[^\\*]*\\*/", "")).orElse("");
	}

	private static String getExpression(ContractBonusRecord record) {
		return Optional.ofNullable(record.getExpression()).map( b -> b.replaceAll("/\\*[^\\*]*\\*/", "")).orElse("");
	}
	
	private static String getExpression(ContractDeductionRecord record) {
		return Optional.ofNullable(record.getExpression()).map( b -> b.replaceAll("/\\*[^\\*]*\\*/", "")).orElse("");
	}

	private static String getExpression(ContractCostRecord record) {
		return Optional.ofNullable(record.getExpression()).map( b -> b.replaceAll("/\\*[^\\*]*\\*/", "")).orElse("");
	}

	private static boolean equals ( Date d1, Date d2) {
		if ( d1 == d2 )
			return true;
		if ( d1 == null )
			return false;
		if ( d2 == null )
			return false;
		
		return  (AonDateUtils.get(d1, Calendar.YEAR) == AonDateUtils.get(d2, Calendar.YEAR))
				&& (AonDateUtils.get(d1, Calendar.MONTH) == AonDateUtils.get(d2, Calendar.MONTH))
				&& (AonDateUtils.get(d1, Calendar.DAY_OF_MONTH) == AonDateUtils.get(d2, Calendar.DAY_OF_MONTH));
				
	}
	
	private static String getSex( byte gender) {
		switch (Gender.safeValueOf(gender)) {
		case FEMALE:
			return "F";
		case MALE:
			return "M";
		default:
			return "U";
		}
	}

	private static Gender getGender( String sex ) {
		sex = AonStringUtils.trimToEmpty(sex);
		sex = AonStringUtils.upperCase(sex);
		switch (sex) {
		case "F":
			return Gender.FEMALE;
		case "M": case "V":
			return Gender.MALE;
		default:
			return Gender.UNKNOWN;
		}
	}
	
	private static Optional<String> getName(String name) {
		String names [] = name.split("\\s+");
		switch (names.length) {
		case 3:
			return Optional.of(names[0]);
		case 4:
			return Optional.of(String.format("%s %s", names[0], names[1]) );
		default:
			return Optional.of(name);
		}
	}
	
	private static Optional<String> getFirstSurname(String name) {
		String names [] = name.split("\\s+");
		switch (names.length) {
		case 3:
			return Optional.of(names[1]);
		case 4:
			return Optional.of(names[2]);
		default:
			return Optional.empty();
		}
	}

	private static Optional<String> getSecondSurname(String name) {
		String names [] = name.split("\\s+");
		switch (names.length) {
		case 3:
			return Optional.of(names[2]);
		case 4:
			return Optional.of(names[3]);
		default:
			return Optional.empty();
		}
	}

	private static String getDniCountry(String document) {
		return "ES";
	}

	private static String trim(String document) {
		return Document.trim(document);
	}

	private static byte getDniType(String document) {
		return Document.parse(document).value();
	}
	
	private static java.sql.Date toSql(Date date) {
		if ( date == null )
			return null;
		return new java.sql.Date(date.getTime());
	}

	private static java.sql.Date toSql(LocalDate date) {
		if ( date == null )
			return null;
		return java.sql.Date.valueOf(date);
	}
	
	private static byte getCCCType(Employee employee) {
		
		if(employee.getContractType().isPresent() && "000".equals(employee.getContractType().get()) && employee.getRlce().isPresent() && AonStringUtils.containsIgnoreCase(employee.getRlce().get(), "PRACT. NO LAB. EMP"))
			return 1; // TRAINING
		
		if (employee.getContractType().isPresent() && "000".equals(employee.getContractType().get()))
			return 5; // FELLOWS
		
		switch (getSSRegimeType(employee)) {
		case GENERAL:
			return 0; // PRINCIPAL
//		case :
//			return 1; // TRAINING
//		case :
//			return 2; // LEARNING
//		case :
//			return 3; // TRADE_REPRESENTATIVE
//		case :
//			return 4; // ASSIMILATEDS
//		case :
//			return 5; // FELLOWS
		case DOMESTIC_EMPLOYEES:
			return 6; // HOME_EMPLOYEES
		case AGRICULTURAL:
			return 7; // AGRICULTURAL
		case ARTIST:
			return 8; // ARTIST
		default :
			return 0; // PRINCIPAL

		}
	}
	
	private static byte getSSRegime(Employee employee) {
		return getSSRegimeType(employee).getValue();
	}
	private static SSRegimeType getSSRegimeType(Employee employee) {
		for (SSRegimeType type : SSRegimeType.values()) {
			if ( Objects.equals(type.getCode(),employee.getRegime())) 
				return type;
		}
		return SSRegimeType.GENERAL;
	}
	private static String getSSRegimeCode(Byte ordinal) {
		if ( ordinal == null )
			return null;
		if ( ordinal < 0 )
			return null;
		SSRegimeType types [] = SSRegimeType.values();
		if ( ordinal >= types.length )
			return null;
		
		return types[ordinal].getCode();
	}
	
	
	
	private static class Document {



		private static DocumentType parse(String str) {
			if (isCif(str)) {
				return DocumentType.CIF;
			}
			else if (isDni(str)) {
				return DocumentType.CIF;
			}
			else if (isNie(str)) {
				return DocumentType.NIE;
			}
			else if (isNif(str)) {
				return DocumentType.NIF;
			}
			return DocumentType.OTHER;
		}
		
		private static String trim(String str) {
			return str.length() > 9 ? str.replaceAll("^0+", ""): str;
		}

		private static boolean isDni(String str) {
			// Nif.is(str, /^(\d{8})([A-HJ-NP-TV-Z])$/)
			return is(str,"^(\\d{8})([A-HJ-NP-TV-Z])$");
		}


		private static boolean isNif(String str) {
			// Nif.is(str, /^[KLM](\d{7})([A-HJ-NP-TV-Z])$/)
			return is(str,"^[KLM](\\d{7})([A-HJ-NP-TV-Z])$");
		}

		private static boolean isNie(String str) {
			// const match: RegExpMatchArray | null = str.toUpperCase().match(/^([XYZ])(\d{7})([A-HJ-NP-TV-Z])$/);
			Matcher matcher = Pattern.compile("^([XYZ])(\\d{7})([A-HJ-NP-TV-Z])$").matcher(str.toUpperCase());
			if ( matcher.matches() ) {
				// const xyz = { X: 0, Y: 1, Z: 2 };
				Map<Character,Integer> xyz = new HashMap<Character, Integer>(){
					{
						put('X',0);
						put('Y',1);
						put('Z',2);
					}
				};
		        // return 'TRWAGMYFPDXBNJZSQVHLCKE'[+(xyz[match[1]] + match[2]) % 23] === match[3];
				char match1 = matcher.group(1).charAt(0);
				int match2 = Integer.parseInt(matcher.group(2));
				char match3 = matcher.group(3).charAt(0);
				return "TRWAGMYFPDXBNJZSQVHLCKE".charAt(xyz.get(match1) % 23 ) == match3;
			}
			return false;
		}

		private static boolean isCif(String str) {
			// let match: RegExpMatchArray | null = str.toUpperCase().match(/^[A-JUV](\d{7})([0-9])$/);
			Matcher matcher = Pattern.compile("^[A-JUV](\\d{7})([0-9])$").matcher(str.toUpperCase());
			if ( matcher.matches() ) {
				// return Nif.cifCtrlDigit(match[1]) === +match[2];
				return getCifCtrlDigit(matcher.group(1)) == Integer.parseInt(matcher.group(2));
			}
			matcher = Pattern.compile("^[N-SW](\\d{7})([A-J])$").matcher(str.toUpperCase());
			if ( matcher.matches() ) {
				// return 'JABCDEFGHI'[Nif.cifCtrlDigit(match[1])] === match[2];
				return "JABCDEFGHI".charAt(getCifCtrlDigit(matcher.group(1))) == matcher.group(2).charAt(0);
			}
			return false;
		}	

		private static boolean is(String str, String regex ) {
			Matcher matcher = Pattern.compile(regex).matcher(str.toUpperCase());
			if ( matcher.matches() ) {
		        // return 'TRWAGMYFPDXBNJZSQVHLCKE'[+match[1] % 23] === match[2];
				int match1 = Integer.parseInt(matcher.group(1));
				char match2 = matcher.group(2).charAt(0);
				return "TRWAGMYFPDXBNJZSQVHLCKE".charAt(match1 % 23 ) == match2;
			}
			return false;
		}

		private static int getCifCtrlDigit(String  str) {
			int digits [] = new int [str.length()];		
			for (int i = 0; i < str.length(); i++) {
				digits[i] = Integer.parseInt(String.valueOf(str.charAt(i)));
			}
			return getCifCtrlDigit(digits);
		}

		private static int getCifCtrlDigit(int  digits []) {
		    // const a = +digits[1] + +digits[3] + +digits[5];
			int a = digits[1] + digits[3] + digits[5];
			// const b1 = +digits[0] * 2;
			int b1 = digits[0] * 2;
			// const b3 = +digits[2] * 2;
			int b3 = digits[2] * 2;
			// const b5 = +digits[4] * 2;
			int b5 = digits[4] * 2;
			// const b7 = +digits[6] * 2;
			int b7 = digits[6] * 2;

			// const u1 = b1 % 10;
			int u1 = b1 % 10;
			// const d1 = (b1 - u1) / 10;
			int d1 = (b1 - u1) / 10;
			// const u3 = b3 % 10;
			int u3 = b3 % 10;
			// const d3 = (b3 - u3) / 10;
			int d3 = (b3 - u3) / 10;
			// const u5 = b5 % 10;
			int u5 = b5 % 10;
			// const d5 = (b5 - u5) / 10;
			int d5 = (b5 - u5) / 10;
			// const u7 = b7 % 10;
			int u7 = b7 % 10;
			// const d7 = (b7 - u7) / 10;
			int d7 = (b7 - u7) / 10;
			// const b = u1 + d1 + u3 + d3 + u5 + d5 + u7 + d7;
			int b = u1 + d1 + u3 + d3 + u5 + d5 + u7 + d7;

			// const c = a + b;
			int c = a + b;
			// const e = c % 10;
			int e = c % 10;

			// const d = e ? 10 - e : 0;
			int d = e != 0 ? 10 - e : 0;

			// return d;
			return d;

		}		
	}	

	public static Date max(Date a, Date b) {
		return compare(a, b) > 0 ? a : b;
	}

	public static Date min(Date a, Date b) {
		return compare(a, b) < 0 ? a : b;
	}


	public static int compare(Date a, Date b) {
		if (a == null) {
			return b == null ? 0 : 1;
		}
		return b == null ? -1 : a.compareTo(b);
	}

}
