package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractLeaveDetail.CONTRACT_LEAVE_DETAIL;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.LeaveBatch.LEAVE_BATCH;
import static com.esferalia.aon.jooq.tables.LeaveBatchDetail.LEAVE_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jooq.Condition;
import org.jooq.Cursor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.esferalia.aon.gwt.payroll.shared.ItParams;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.jooq.tables.ContractData;
import com.esferalia.aon.jooq.tables.Registry;
import com.esferalia.aon.jooq.tables.records.ContractLeaveDetailRecord;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.jooq.tables.records.LeaveBatchRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.client.util.Objects;

public class JooqIT {

	private static Settings SETTINGS = null;
	
	private static Registry PERSON_REGISTRY = REGISTRY.as("person_registry");
	private static  Registry ENTERPRISE_REGISTRY = REGISTRY.as("enterprise_registry");
    
	private static  ContractData MOTIVO_MAT_PAT = CONTRACT_DATA.as("motivo_mat_pat");
	private static  ContractData BASE_REGULADORA = CONTRACT_DATA.as("base_reguladora");
    private static  ContractData INICIO_PAGO_DIRECTO = CONTRACT_DATA.as("inicio_pago_directo");
    private static  ContractData COEFICIENTE_PATERNIDAD = CONTRACT_DATA.as("coeficiente_paternidad");
    private static  ContractData COEFICIENTE_MATERNIDAD = CONTRACT_DATA.as("coeficiente_maternidad");
    private static  ContractData TIPO_SOLICITANTE_MAT_PAT = CONTRACT_DATA.as("tipo_solicitante_mat_pat");
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static List<ITEmployee> getEmployeesITInfo(Connection conn, Integer itIds []) {
		return getEmployeesITInfo(DSL.using(conn, getDefaultSettings()), CONTRACT_LEAVE.ID.in(itIds) );
	}

	public static List<ITEmployee> getEmployeesITInfo(AONContext aonContext, Collection<Integer> itIds) {
	    	return getEmployeesITInfo(aonContext.getDslContext(), CONTRACT_LEAVE.ID.in(itIds));
	}

	public static List<ITEmployee> getEmployeeItList(Connection conn, Integer domainId, ItParams params) {
		return getEmployeeItList(DSL.using(conn, getDefaultSettings()), domainId, params);
	}
	
	public static List<ITEmployee> getWorkplaceEmployeeITInfo(Connection conn, Integer workplaceId, Boolean allEmployees) {
	    	return getEmployeesITInfo(DSL.using(conn, getDefaultSettings()), CONTRACT.WORKPLACE.eq(workplaceId));
	}
	
	public static List<ITEmployee> getEmployeeITInfo(Connection conn, Integer contractId) {
	    	return getEmployeesITInfo(DSL.using(conn, getDefaultSettings()), CONTRACT.ID.eq(contractId));
	}
	
	public static String deleteIT(Connection conn, Integer domainId, Integer itId) {
		return deleteITDB(DSL.using(conn, getDefaultSettings()), domainId, itId);
	}

	public static String createUpdateITEmployee(Connection conn, Integer domainId, ITEmployee employeeITInfo) {
		return createUpdateITEmployeeDB(DSL.using(conn, getDefaultSettings()), domainId, employeeITInfo);
	}
	
	public static void setComunicationIT(Connection conn, Integer domainId, ITEmployee itEmployee, IT it) {
		setComunicationITDB(DSL.using(conn, getDefaultSettings()), domainId, itEmployee, it);
	}
	
	private static List<ITEmployee> getEmployeesITInfo(DSLContext dslContext, Condition condition) {
	    
	    Registry PERSON_REGISTRY = REGISTRY.as("person_registry");
	    Registry ENTERPRISE_REGISTRY = REGISTRY.as("enterprise_registry");
	    
	    ContractData MOTIVO_MAT_PAT = CONTRACT_DATA.as("motivo_mat_pat");
	    ContractData BASE_REGULADORA = CONTRACT_DATA.as("base_reguladora");
	    ContractData INICIO_PAGO_DIRECTO = CONTRACT_DATA.as("inicio_pago_directo");
	    ContractData COEFICIENTE_PATERNIDAD = CONTRACT_DATA.as("coeficiente_paternidad");
	    ContractData COEFICIENTE_MATERNIDAD = CONTRACT_DATA.as("coeficiente_maternidad");
	    ContractData TIPO_SOLICITANTE_MAT_PAT = CONTRACT_DATA.as("tipo_solicitante_mat_pat");
	    
	    Date today = new Date(new java.util.Date().getTime());
	    
	    Cursor<Record> cursor =
	    dslContext
	    .select()
	    .from(CONTRACT)
	    .innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
	    .innerJoin(PERSON_REGISTRY).on(PERSON.REGISTRY.eq(PERSON_REGISTRY.ID))
	    .innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
	    .innerJoin(ENTERPRISE_REGISTRY).on(WORKPLACE.ENTERPRISE.eq(ENTERPRISE_REGISTRY.ID))
	    
	    .leftJoin(ENTERPRISE_CCC).on(CONTRACT.ENTERPRISE_CCC.eq(ENTERPRISE_CCC.ID))
	    
	    .leftJoin(CONTRACT_LEAVE).on(CONTRACT.ID.eq(CONTRACT_LEAVE.CONTRACT))

	    .leftJoin(MOTIVO_MAT_PAT).on(CONTRACT_LEAVE.CONTRACT.eq(MOTIVO_MAT_PAT.CONTRACT)
		    	.and(MOTIVO_MAT_PAT.NAME.eq("MOTIVO_MAT_PAT")
		    	.and(MOTIVO_MAT_PAT.START_DATE.eq(CONTRACT_LEAVE.START_DATE))))

	    .leftJoin(TIPO_SOLICITANTE_MAT_PAT).on(CONTRACT_LEAVE.CONTRACT.eq(TIPO_SOLICITANTE_MAT_PAT.CONTRACT)
		    	.and(TIPO_SOLICITANTE_MAT_PAT.NAME.eq("TIPO_SOLICITANTE_MAT_PAT")
		    	.and(TIPO_SOLICITANTE_MAT_PAT.START_DATE.eq(CONTRACT_LEAVE.START_DATE))))

	    .leftJoin(BASE_REGULADORA).on(CONTRACT_LEAVE.CONTRACT.eq(BASE_REGULADORA.CONTRACT)
		    	.and(BASE_REGULADORA.NAME.eq("BASE_REGULADORA")
		    	.and(BASE_REGULADORA.START_DATE.eq(CONTRACT_LEAVE.START_DATE))))

	    .leftJoin(INICIO_PAGO_DIRECTO).on(CONTRACT_LEAVE.CONTRACT.eq(INICIO_PAGO_DIRECTO.CONTRACT)
			.and(INICIO_PAGO_DIRECTO.NAME.eq("INICIO_PAGO_DIRECTO")
			.and(INICIO_PAGO_DIRECTO.START_DATE.eq(CONTRACT_LEAVE.START_DATE))))
	    
	    .leftJoin(COEFICIENTE_PATERNIDAD).on(CONTRACT_LEAVE.CONTRACT.eq(COEFICIENTE_PATERNIDAD.CONTRACT)
			.and(COEFICIENTE_PATERNIDAD.NAME.eq("COEFICIENTE_PATERNIDAD")
			.and(COEFICIENTE_PATERNIDAD.START_DATE.eq(CONTRACT_LEAVE.START_DATE))))

	    .leftJoin(COEFICIENTE_MATERNIDAD).on(CONTRACT_LEAVE.CONTRACT.eq(COEFICIENTE_MATERNIDAD.CONTRACT)
			.and(COEFICIENTE_PATERNIDAD.NAME.eq("COEFICIENTE_PATERNIDAD")
			.and(COEFICIENTE_PATERNIDAD.START_DATE.eq(CONTRACT_LEAVE.START_DATE))))

	    .leftJoin(CONTRACT_LEAVE_DETAIL).on(CONTRACT_LEAVE.ID.eq(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE))

	    .leftJoin(LEAVE_BATCH_DETAIL).on(CONTRACT_LEAVE_DETAIL.ID.eq(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL))
	    .leftJoin(LEAVE_BATCH).on(LEAVE_BATCH_DETAIL.LEAVE_BATCH.eq(LEAVE_BATCH.ID).and(LEAVE_BATCH.COMMUNICATION_ID.eq("COMUNICA")))

	    .where(condition)
	    .orderBy(CONTRACT.PERSON)
	    .fetchLazy();
	    
	    Map<Integer, Map<Integer,IT>> itsMap = new LinkedHashMap<>();
	    Map<Integer, ITEmployee> itEmployeesMap = new LinkedHashMap<>();
	    
	    while ( cursor.hasNext() ) {
		Record r = cursor.fetchNext();
		
		Integer contractId = r.get(CONTRACT.ID);

		itEmployeesMap.computeIfAbsent(contractId, id -> {
			ContractInfo contractInfo = new ContractInfo();
			contractInfo.setContractId(r.get(CONTRACT.ID));
			contractInfo.setSsRegimen(r.get(CONTRACT.SS_REGIME));
			contractInfo.setEndDate(r.get(CONTRACT.END_DATE));
			contractInfo.setStartDate(r.get(CONTRACT.START_DATE));
			contractInfo.setSeniorityDate(r.get(CONTRACT.SENIORITY_DATE));
			contractInfo.setEnterpriseName(r.get(ENTERPRISE_REGISTRY.NAME));
			contractInfo.setAgreementCategory(r.get(CONTRACT.CATEGORY_DESCRIPTION));
			
			//contractInfo.setEnterpriseName(r.get(ENTERPRISE_REGISTRY.NAME));
			//contractInfo.setEnterpriseCIF(r.get(ENTERPRISE_REGISTRY.DOCUMENT));
			
			Result<Record> contractTypeRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(r.get(CONTRACT.ID)))
				.and(CONTRACT_DATA.NAME.eq("TC2"))
				.and(CONTRACT_DATA.START_DATE.le(today))
				.and(CONTRACT_DATA.END_DATE.ge(today).or(CONTRACT_DATA.END_DATE.isNull()))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
			
			if(!contractTypeRecords.isEmpty())
				contractInfo.setContractType(contractTypeRecords.getFirst().get(CONTRACT_DATA.EXPRESSION));
			
			
			Optional.ofNullable(r.get(ENTERPRISE_CCC.ID))
			.ifPresent( contractInfo::setCccId);
			Optional.ofNullable(r.get(ENTERPRISE_CCC.TYPE))
			.ifPresent( contractInfo::setCccType);
			Optional.ofNullable(r.get(ENTERPRISE_CCC.TYPE))
			.ifPresent( cccType -> contractInfo.setCompleteCCC(getCCCRegimeCode(cccType)+r.get(ENTERPRISE_CCC.CCC)) );
			
			
			EmployeeInfo employeeInfo = new EmployeeInfo();
			employeeInfo.setEmployeeId(r.get(PERSON.REGISTRY));
			employeeInfo.setName(r.get(PERSON.NAME));
			employeeInfo.setSurName(r.get(PERSON.FIRST_SURNAME));
			employeeInfo.setSecondSurName(r.get(PERSON.SECOND_SURNAME));
			employeeInfo.setSsNumber(r.get(PERSON.SOCIAL_SECURITY_NUM));
			employeeInfo.setDocument(r.get(PERSON_REGISTRY.DOCUMENT));
			
			ITEmployee itEmployee = new ITEmployee() ;
			
			itEmployee.setContractInfo(contractInfo);
			itEmployee.setEmployeeInfo(employeeInfo);

			itEmployee.setStatus(r.get(CONTRACT_LEAVE.END_DATE) == null ? (byte)1 : (byte)0);
			
			return itEmployee;
		});
		
		Integer contractLeaveId = r.get(CONTRACT_LEAVE.ID);
		
		if ( r.get(CONTRACT_LEAVE.ID) == null )
		    continue;
		
		IT currentIt =
		itsMap.computeIfAbsent(contractId, id -> new HashMap<>())
		.computeIfAbsent(contractLeaveId, id -> {
		    IT it = new IT();
		    it.setIsParent(false);
		    it.setId(r.get(CONTRACT_LEAVE.ID));
		    it.setContract(r.get(CONTRACT.ID));
		    it.setDomain(r.get(CONTRACT_LEAVE.DOMAIN));
		    it.setTypeLowPart(r.get(CONTRACT_LEAVE.TYPE));
		    it.setContract(r.get(CONTRACT_LEAVE.CONTRACT));
		    it.setDescription(r.get(CONTRACT_LEAVE.DESCRIPTION));
		    it.setStartDate(r.get(CONTRACT_LEAVE.START_DATE));
		    it.setEndDate(r.get(CONTRACT_LEAVE.END_DATE));
		    it.setDailyCGCBase(r.get(CONTRACT_LEAVE.DAILY_CGC_BASE));
		    it.setDailyCGPBase(r.get(CONTRACT_LEAVE.DAILY_CGP_BASE));
		    it.setParent(r.get(CONTRACT_LEAVE.PARENT));
		    it.setDailyREGBase(r.get(CONTRACT_LEAVE.DAILY_REG_BASE));
		    it.setTypeHighPart(r.get(CONTRACT_LEAVE.DISCHARGE_CAUSE));

		    it.setFullName(r.get(PERSON_REGISTRY.NAME));
		    it.setContractStartDate(r.get(CONTRACT.START_DATE));
		    it.setContractEndDate(r.get(CONTRACT.END_DATE));
		    
    		    it.setComunicationDate(r.get(LEAVE_BATCH.DATE));
    		    Optional.ofNullable(r.get(LEAVE_BATCH.STATUS))
    		    .ifPresentOrElse( status -> it.setIsComunicate(status == (byte)1 ? true : false), () -> it.setIsComunicate(false)); 
		    
		    try {
    		    Optional.ofNullable(r.get(TIPO_SOLICITANTE_MAT_PAT.EXPRESSION))
    		    .ifPresent( expression -> it.setMaternityType(Byte.parseByte(expression)));
		    } catch (Exception e) {
			// Unknow expression, must be byte;
		    }

		    try {
        		    Optional.ofNullable(r.get(MOTIVO_MAT_PAT.EXPRESSION))
        		    .ifPresent( expression -> it.setMaternityReason(Byte.parseByte(expression)));
		    } catch (Exception e) {
			// Unknow expression, must be byte;
		    }

		    try {
			Optional.ofNullable(r.get(COEFICIENTE_MATERNIDAD.EXPRESSION))
				.ifPresent(expression -> it.setPartialityCoef(Double.parseDouble(expression)));
		    } catch (Exception e) {
			// Unknow expression, must be a double;
		    }

		    try {
			Optional.ofNullable(r.get(COEFICIENTE_PATERNIDAD.EXPRESSION))
				.ifPresent(expression -> it.setPartialityCoef(Double.parseDouble(expression)));
		    } catch (Exception e) {
			// Unknow expression, must be a double;
		    }
		    
		    try {
			Optional.ofNullable(r.get(INICIO_PAGO_DIRECTO.EXPRESSION)).ifPresent(expression -> it
				.setDirectPayDate(getDirectPayDateByExpression(r.get(INICIO_PAGO_DIRECTO.EXPRESSION))));
		    } catch (Exception e) {
			// Unknow expression, must be FECHA(yyyy,MM,dd);
		    }
		    
		    try {
				Optional.ofNullable(r.get(BASE_REGULADORA.EXPRESSION)).ifPresent(expression -> it.setRegulationBase(Double.parseDouble(expression)));
		    } catch (Exception e) {
		    	// Unknow expression, must be a double;
		    }

		    
		    Optional.ofNullable(itsMap.get(r.get(CONTRACT.ID)).get(r.get(CONTRACT_LEAVE.PARENT)))
		    .ifPresent(parentIt ->parentIt.setIsParent(true));

		    return it;
		});
		
		if ( r.get(CONTRACT_LEAVE_DETAIL.ID) == null )
		    continue;
		
		if ( currentIt.getITParts().stream()
		.anyMatch(itPart -> Objects.equal(itPart.getId(),r.get(CONTRACT_LEAVE_DETAIL.ID))))
		    continue;
		
		ITPart itPart = new ITPart();
		itPart.setIt(r.get(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE));
		itPart.setId(r.get(CONTRACT_LEAVE_DETAIL.ID));
		itPart.setDomain(r.get(CONTRACT_LEAVE_DETAIL.DOMAIN));
		itPart.setType(r.get(CONTRACT_LEAVE_DETAIL.TYPE));
		itPart.setCias(r.get(CONTRACT_LEAVE_DETAIL.CIAS));
		itPart.setDate(r.get(CONTRACT_LEAVE_DETAIL.DATE));
		itPart.setStatus(r.get(CONTRACT_LEAVE_DETAIL.STATUS));
		itPart.setCollegeNumber(r.get(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER));
		itPart.setConfirmOrderNumber(r.get(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER));
		
		itPart.setModify(false);
		itPart.setDelete(false);
		
		currentIt.addITPart(itPart);
		
	    }
	    
	    itsMap.forEach((i1,map) -> map.forEach((i2,it) -> it.getITParts().sort( (part1,part2) -> part2.getDate().compareTo(part1.getDate()))));
	    
	    ArrayList<ITEmployee> itEmployees = new ArrayList<>();
	    itEmployeesMap.forEach((employeeId, itEmployee) ->{ 
		itsMap.getOrDefault(employeeId, Collections.emptyMap()).values().stream()
		.sorted( (it1,it2) -> it1.getStartDate().compareTo(it2.getStartDate())).forEach(itEmployee::addIT);
		itEmployees.add(itEmployee);
	    });	    
	    return itEmployees;
	}
	
	private static List<ITEmployee> getEmployeeItList(DSLContext dslContext, Integer domainId, ItParams params) {
		Condition condition = paramsToCondition(dslContext, domainId, params);
	    
	    Date today = new Date(new java.util.Date().getTime());
	    
	   SelectConditionStep<Record> select = dslContext
	    .select()
	    .from(CONTRACT)
	    .innerJoin(PERSON).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
	    .innerJoin(PERSON_REGISTRY).on(PERSON.REGISTRY.eq(PERSON_REGISTRY.ID))
	    .innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
	    .innerJoin(ENTERPRISE_REGISTRY).on(WORKPLACE.ENTERPRISE.eq(ENTERPRISE_REGISTRY.ID))
	    
	    .leftOuterJoin(ENTERPRISE_CCC).on(CONTRACT.ENTERPRISE_CCC.eq(ENTERPRISE_CCC.ID))
	    
	    .leftOuterJoin(CONTRACT_LEAVE).on(CONTRACT.ID.eq(CONTRACT_LEAVE.CONTRACT))

	    .leftOuterJoin(MOTIVO_MAT_PAT).on(CONTRACT_LEAVE.CONTRACT.eq(MOTIVO_MAT_PAT.CONTRACT)
		    	.and(MOTIVO_MAT_PAT.NAME.eq("MOTIVO_MAT_PAT")
		    	.and(MOTIVO_MAT_PAT.START_DATE.eq(CONTRACT_LEAVE.START_DATE))))

	    .leftOuterJoin(TIPO_SOLICITANTE_MAT_PAT).on(CONTRACT_LEAVE.CONTRACT.eq(TIPO_SOLICITANTE_MAT_PAT.CONTRACT)
		    	.and(TIPO_SOLICITANTE_MAT_PAT.NAME.eq("TIPO_SOLICITANTE_MAT_PAT")
		    	.and(TIPO_SOLICITANTE_MAT_PAT.START_DATE.eq(CONTRACT_LEAVE.START_DATE))))

	    .leftOuterJoin(BASE_REGULADORA).on(CONTRACT_LEAVE.CONTRACT.eq(BASE_REGULADORA.CONTRACT)
		    	.and(BASE_REGULADORA.NAME.eq("BASE_REGULADORA")
		    	.and(BASE_REGULADORA.START_DATE.eq(CONTRACT_LEAVE.START_DATE))))

	    .leftOuterJoin(INICIO_PAGO_DIRECTO).on(CONTRACT_LEAVE.CONTRACT.eq(INICIO_PAGO_DIRECTO.CONTRACT)
			.and(INICIO_PAGO_DIRECTO.NAME.eq("INICIO_PAGO_DIRECTO")
			.and(INICIO_PAGO_DIRECTO.START_DATE.eq(CONTRACT_LEAVE.START_DATE))))
	    
	    .leftOuterJoin(COEFICIENTE_PATERNIDAD).on(CONTRACT_LEAVE.CONTRACT.eq(COEFICIENTE_PATERNIDAD.CONTRACT)
			.and(COEFICIENTE_PATERNIDAD.NAME.eq("COEFICIENTE_PATERNIDAD")
			.and(COEFICIENTE_PATERNIDAD.START_DATE.eq(CONTRACT_LEAVE.START_DATE))))

	    .leftOuterJoin(COEFICIENTE_MATERNIDAD).on(CONTRACT_LEAVE.CONTRACT.eq(COEFICIENTE_MATERNIDAD.CONTRACT)
			.and(COEFICIENTE_PATERNIDAD.NAME.eq("COEFICIENTE_PATERNIDAD")
			.and(COEFICIENTE_PATERNIDAD.START_DATE.eq(CONTRACT_LEAVE.START_DATE))))

	    .leftOuterJoin(CONTRACT_LEAVE_DETAIL).on(CONTRACT_LEAVE.ID.eq(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE))

	    .leftOuterJoin(LEAVE_BATCH_DETAIL).on(CONTRACT_LEAVE_DETAIL.ID.eq(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL))
	    .leftOuterJoin(LEAVE_BATCH).on(LEAVE_BATCH_DETAIL.LEAVE_BATCH.eq(LEAVE_BATCH.ID).and(LEAVE_BATCH.COMMUNICATION_ID.eq("COMUNICA")))

	    .where(condition);
	    
	    if(params.isAsc()) {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(PERSON.FIRST_SURNAME);
			else if(AonStringUtils.equals(params.getOrderBy(), "start"))
				select.orderBy(CONTRACT_LEAVE.START_DATE);
			else if(AonStringUtils.equals(params.getOrderBy(), "end"))
				select.orderBy(CONTRACT_LEAVE.START_DATE);
			else if(AonStringUtils.equals(params.getOrderBy(), "low"))
				select.orderBy(CONTRACT_LEAVE.TYPE);
			else if(AonStringUtils.equals(params.getOrderBy(), "hight"))
				select.orderBy(CONTRACT_LEAVE.DISCHARGE_CAUSE);
			else if(AonStringUtils.equals(params.getOrderBy(), "startContract"))
				select.orderBy(CONTRACT.START_DATE);
			else if(AonStringUtils.equals(params.getOrderBy(), "endContract"))
				select.orderBy(CONTRACT.END_DATE);
		} else {
			if(AonStringUtils.equals(params.getOrderBy(), "name"))
				select.orderBy(PERSON.FIRST_SURNAME.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "start"))
				select.orderBy(CONTRACT_LEAVE.START_DATE.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "end"))
				select.orderBy(CONTRACT_LEAVE.START_DATE.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "low"))
				select.orderBy(CONTRACT_LEAVE.TYPE.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "hight"))
				select.orderBy(CONTRACT_LEAVE.DISCHARGE_CAUSE.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "startContract"))
				select.orderBy(CONTRACT.START_DATE.desc());
			else if(AonStringUtils.equals(params.getOrderBy(), "endContract"))
				select.orderBy(CONTRACT.END_DATE.desc());
		}
	    
	    System.out.println(select.getSQL(ParamType.INLINED).toString());
	    System.out.println("cursor size : " + select.limit(params.getOffset(), params.getLimit()).fetch().size());
	    
	    Cursor<Record> cursor = select
	    		.orderBy(CONTRACT.START_DATE.desc(), CONTRACT_LEAVE.START_DATE.desc())
	    		.limit(params.getOffset(), params.getLimit())
	    		.fetchLazy();
	    
	    Map<Integer, Map<Integer,IT>> itsMap = new LinkedHashMap<>();
	    Map<Integer, ITEmployee> itEmployeesMap = new LinkedHashMap<>();
	    
	    while ( cursor.hasNext() ) {
		Record r = cursor.fetchNext();
		
		Integer contractId = r.get(CONTRACT.ID);

		itEmployeesMap.computeIfAbsent(contractId, id -> {
			ContractInfo contractInfo = new ContractInfo();
			contractInfo.setContractId(r.get(CONTRACT.ID));
			contractInfo.setSsRegimen(r.get(CONTRACT.SS_REGIME));
			contractInfo.setEndDate(r.get(CONTRACT.END_DATE));
			contractInfo.setStartDate(r.get(CONTRACT.START_DATE));
			contractInfo.setSeniorityDate(r.get(CONTRACT.SENIORITY_DATE));
			contractInfo.setEnterpriseName(r.get(ENTERPRISE_REGISTRY.NAME));
			contractInfo.setAgreementCategory(r.get(CONTRACT.CATEGORY_DESCRIPTION));
			
			//contractInfo.setEnterpriseName(r.get(ENTERPRISE_REGISTRY.NAME));
			//contractInfo.setEnterpriseCIF(r.get(ENTERPRISE_REGISTRY.DOCUMENT));
			
			Result<Record> contractTypeRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(r.get(CONTRACT.ID)))
				.and(CONTRACT_DATA.NAME.eq("TC2"))
				.and(CONTRACT_DATA.START_DATE.le(today))
				.and(CONTRACT_DATA.END_DATE.ge(today).or(CONTRACT_DATA.END_DATE.isNull()))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
			
			if(!contractTypeRecords.isEmpty())
				contractInfo.setContractType(contractTypeRecords.getFirst().get(CONTRACT_DATA.EXPRESSION));
			
			Result<Record> fullTimeRecords = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(r.get(CONTRACT.ID)))
					.and(CONTRACT_DATA.NAME.eq("TIEMPO_COMPLETO"))
					.and(CONTRACT_DATA.START_DATE.le(today))
					.and(CONTRACT_DATA.END_DATE.ge(today).or(CONTRACT_DATA.END_DATE.isNull()))
					.orderBy(CONTRACT_DATA.START_DATE.desc())
					.fetch();
			
			if(!fullTimeRecords.isEmpty()) {
				String fullTimeRecord = fullTimeRecords.getFirst().get(CONTRACT_DATA.EXPRESSION);
				contractInfo.setFullTimePartialContracts(AonStringUtils.isNotBlank(fullTimeRecord) && Boolean.parseBoolean(fullTimeRecord));
			}
			
			Optional.ofNullable(r.get(ENTERPRISE_CCC.ID))
			.ifPresent( contractInfo::setCccId);
			Optional.ofNullable(r.get(ENTERPRISE_CCC.TYPE))
			.ifPresent( contractInfo::setCccType);
			Optional.ofNullable(r.get(ENTERPRISE_CCC.TYPE))
			.ifPresent( cccType -> contractInfo.setCompleteCCC(getCCCRegimeCode(cccType)+r.get(ENTERPRISE_CCC.CCC)) );
			
			
			EmployeeInfo employeeInfo = new EmployeeInfo();
			employeeInfo.setEmployeeId(r.get(PERSON.REGISTRY));
			employeeInfo.setName(r.get(PERSON.NAME));
			employeeInfo.setSurName(r.get(PERSON.FIRST_SURNAME));
			employeeInfo.setSecondSurName(r.get(PERSON.SECOND_SURNAME));
			employeeInfo.setSsNumber(r.get(PERSON.SOCIAL_SECURITY_NUM));
			employeeInfo.setDocument(r.get(PERSON_REGISTRY.DOCUMENT));
			
			ITEmployee itEmployee = new ITEmployee() ;
			
			itEmployee.setContractInfo(contractInfo);
			itEmployee.setEmployeeInfo(employeeInfo);

			itEmployee.setStatus(r.get(CONTRACT_LEAVE.END_DATE) == null ? (byte)1 : (byte)0);
			
			return itEmployee;
		});
		
		Integer contractLeaveId = r.get(CONTRACT_LEAVE.ID);
		
		if ( r.get(CONTRACT_LEAVE.ID) == null )
		    continue;
		
		IT currentIt =
		itsMap.computeIfAbsent(contractId, id -> new HashMap<>())
		.computeIfAbsent(contractLeaveId, id -> {
		    IT it = new IT();
		    it.setIsParent(false);
		    it.setId(r.get(CONTRACT_LEAVE.ID));
		    it.setContract(r.get(CONTRACT.ID));
		    it.setDomain(r.get(CONTRACT_LEAVE.DOMAIN));
		    it.setTypeLowPart(r.get(CONTRACT_LEAVE.TYPE));
		    it.setContract(r.get(CONTRACT_LEAVE.CONTRACT));
		    it.setDescription(r.get(CONTRACT_LEAVE.DESCRIPTION));
		    it.setStartDate(r.get(CONTRACT_LEAVE.START_DATE));
		    it.setEndDate(r.get(CONTRACT_LEAVE.END_DATE));
		    it.setDailyCGCBase(r.get(CONTRACT_LEAVE.DAILY_CGC_BASE));
		    it.setDailyCGPBase(r.get(CONTRACT_LEAVE.DAILY_CGP_BASE));
		    it.setParent(r.get(CONTRACT_LEAVE.PARENT));
		    it.setDailyREGBase(r.get(CONTRACT_LEAVE.DAILY_REG_BASE));
		    it.setTypeHighPart(r.get(CONTRACT_LEAVE.DISCHARGE_CAUSE));

		    it.setFullName(r.get(PERSON_REGISTRY.NAME));
		    it.setContractStartDate(r.get(CONTRACT.START_DATE));
		    it.setContractEndDate(r.get(CONTRACT.END_DATE));
		    
    		    it.setComunicationDate(r.get(LEAVE_BATCH.DATE));
    		    Optional.ofNullable(r.get(LEAVE_BATCH.STATUS))
    		    .ifPresentOrElse( status -> it.setIsComunicate(status == (byte)1 ? true : false), () -> it.setIsComunicate(false)); 
		    
		    try {
    		    Optional.ofNullable(r.get(TIPO_SOLICITANTE_MAT_PAT.EXPRESSION))
    		    .ifPresent( expression -> it.setMaternityType(Byte.parseByte(expression)));
		    } catch (Exception e) {
			// Unknow expression, must be byte;
		    }

		    try {
        		    Optional.ofNullable(r.get(MOTIVO_MAT_PAT.EXPRESSION))
        		    .ifPresent( expression -> it.setMaternityReason(Byte.parseByte(expression)));
		    } catch (Exception e) {
			// Unknow expression, must be byte;
		    }

		    try {
			Optional.ofNullable(r.get(COEFICIENTE_MATERNIDAD.EXPRESSION))
				.ifPresent(expression -> it.setPartialityCoef(Double.parseDouble(expression)));
		    } catch (Exception e) {
			// Unknow expression, must be a double;
		    }

		    try {
			Optional.ofNullable(r.get(COEFICIENTE_PATERNIDAD.EXPRESSION))
				.ifPresent(expression -> it.setPartialityCoef(Double.parseDouble(expression)));
		    } catch (Exception e) {
			// Unknow expression, must be a double;
		    }
		    
		    try {
			Optional.ofNullable(r.get(INICIO_PAGO_DIRECTO.EXPRESSION)).ifPresent(expression -> it
				.setDirectPayDate(getDirectPayDateByExpression(r.get(INICIO_PAGO_DIRECTO.EXPRESSION))));
		    } catch (Exception e) {
			// Unknow expression, must be FECHA(yyyy,MM,dd);
		    }
		    
		    try {
		    	Optional.ofNullable(r.get(BASE_REGULADORA.EXPRESSION))
				.ifPresent(expression -> it.setRegulationBase(Double.parseDouble(expression)));
		    } catch (Exception e) {
			// Unknow expression, must be a double;
		    }

		    
		    Optional.ofNullable(itsMap.get(r.get(CONTRACT.ID)).get(r.get(CONTRACT_LEAVE.PARENT)))
		    .ifPresent(parentIt ->parentIt.setIsParent(true));

		    return it;
		});
		
		if ( r.get(CONTRACT_LEAVE_DETAIL.ID) == null )
		    continue;
		
		if ( currentIt.getITParts().stream()
		.anyMatch(itPart -> Objects.equal(itPart.getId(),r.get(CONTRACT_LEAVE_DETAIL.ID))))
		    continue;
		
		ITPart itPart = new ITPart();
		itPart.setIt(r.get(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE));
		itPart.setId(r.get(CONTRACT_LEAVE_DETAIL.ID));
		itPart.setDomain(r.get(CONTRACT_LEAVE_DETAIL.DOMAIN));
		itPart.setType(r.get(CONTRACT_LEAVE_DETAIL.TYPE));
		itPart.setCias(r.get(CONTRACT_LEAVE_DETAIL.CIAS));
		itPart.setDate(r.get(CONTRACT_LEAVE_DETAIL.DATE));
		itPart.setStatus(r.get(CONTRACT_LEAVE_DETAIL.STATUS));
		itPart.setCollegeNumber(r.get(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER));
		itPart.setConfirmOrderNumber(r.get(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER));
		
		itPart.setModify(false);
		itPart.setDelete(false);
		
		currentIt.addITPart(itPart);
		
	    }
	    
	    itsMap.forEach((i1,map) -> map.forEach((i2,it) -> it.getITParts().sort( (part1,part2) -> part2.getDate().compareTo(part1.getDate()))));
	    
	    ArrayList<ITEmployee> itEmployees = new ArrayList<>();
	    itEmployeesMap.forEach((employeeId, itEmployee) ->{ 
		itsMap.getOrDefault(employeeId, Collections.emptyMap()).values().stream()
		.sorted( (it1,it2) -> it1.getStartDate().compareTo(it2.getStartDate())).forEach(itEmployee::addIT);
		itEmployees.add(itEmployee);
	    });	 
	    
	    System.out.println("itEmployees size : " + itEmployees.size());
	    itEmployees.forEach(ie -> {
	    	ie.getIts().forEach(it -> System.out.println("IT : " + it.getStartDate() + " - " + it.getEndDate()));
	    });
	    return itEmployees;
	}

	

	private static Condition paramsToCondition(DSLContext dslContext, Integer domainId, ItParams params) {
		Condition condition = CONTRACT.DOMAIN.eq(domainId);
		
		if(AonStringUtils.isNotBlank(params.getDescription())) {
			if(params.getDescription().contains("|")) {
				String[] descriptions = params.getDescription().split("\\|");
				Condition orCondition = DSL.noCondition();
			    for (String desc : descriptions) {
			    	orCondition = orCondition.or(
			           DSL.lower(PERSON_REGISTRY.NAME).like(DSL.lower("%" + desc.trim() + "%"))
			            .or(DSL.lower(PERSON_REGISTRY.DOCUMENT).like(DSL.lower("%" + desc.trim() + "%")))
			        );
			    }
			    condition = condition.and(orCondition);
			} else {
				condition = condition.and(DSL.lower(PERSON_REGISTRY.NAME).like(DSL.lower("%" + params.getDescription().trim() + "%"))
						.or(DSL.lower(PERSON_REGISTRY.DOCUMENT).like(DSL.lower("%" + params.getDescription().trim() + "%"))));
			}	
		}
		
		if(null != params.getStart())
			condition = condition.and(CONTRACT_LEAVE.START_DATE.ge(new Date(params.getStart().getTime())));
		
		if(null != params.getEnd())
			condition = condition.and(CONTRACT_LEAVE.END_DATE.isNotNull().and(CONTRACT_LEAVE.END_DATE.le(new Date(params.getEnd().getTime()))));
		
		if(null != params.getWorkplace())
			condition = condition.and(WORKPLACE.ID.eq(params.getWorkplace()));
			
		return condition;
	}

	private static void setEmployeeInfo(ITEmployee itEmployee, Integer contractId, DSLContext dslContext) {
		ContractInfo contractData = new ContractInfo();
		EmployeeInfo employeeData = new EmployeeInfo();
		
		// --------------------------------------------- Employee Info
		
		// PERSON TABLE
		Record personTable = dslContext.select().from(PERSON)
				.where(PERSON.REGISTRY.eq(
						dslContext.select(CONTRACT.PERSON).from(CONTRACT)
							.where(CONTRACT.ID.eq(contractId))))
				.fetchOne();
	
		employeeData.setEmployeeId(personTable.get(PERSON.REGISTRY));
		employeeData.setSsNumber(personTable.get(PERSON.SOCIAL_SECURITY_NUM));
		employeeData.setName(personTable.get(PERSON.NAME));
		employeeData.setSurName(personTable.get(PERSON.FIRST_SURNAME));
		employeeData.setSecondSurName(personTable.get(PERSON.SECOND_SURNAME));
		
		Integer employee_registry = personTable.get(PERSON.REGISTRY);
		
		// REGISTRY TABLE
		Record registryTable = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(employee_registry))
				.fetchOne();
		
		employeeData.setDocument(registryTable.get(REGISTRY.DOCUMENT));
		
		// --------------------------------------------- Contract Info
		
		// HAS PAYROLL
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
					.and(SALARY.TYPE.eq((byte)0))
					.orderBy(SALARY.END_DATE.desc())
					.fetch();
		
		if(salaryRecords.isEmpty()){
			contractData.setHasPayroll(false);
			contractData.setPayrollDate(null);
		}else{
			contractData.setHasPayroll(true);
			contractData.setPayrollDate(salaryRecords.get(0).get(SALARY.END_DATE));
		}
		
		// CONTRACT TABLE
		Record contractTable = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		contractData.setContractId(contractId);
		contractData.setStartDate(contractTable.get(CONTRACT.START_DATE));
		contractData.setEndDate(contractTable.get(CONTRACT.END_DATE));
		contractData.setSeniorityDate(contractTable.get(CONTRACT.SENIORITY_DATE));
		contractData.setSsRegimen(contractTable.get(CONTRACT.SS_REGIME));
		
		// FECHA ACTUAL
		java.util.Date actualJavaDate = new java.util.Date();
		Date actualSQLDate = new Date(actualJavaDate.getTime());
		
		contractData.setContracttypeId(null);
		contractData.setContractType(null);
		contractData.setQuotegroupId(null);
		contractData.setQuoteGroup(null);
		contractData.setOcupationId(null);
		contractData.setOcupation(null);
		contractData.setJourneytypeId(null);
		contractData.setJourneyType(null);
		
		//ENTERPRISE CCC TABLE
		Integer enterpriseCCC = contractTable.get(CONTRACT.ENTERPRISE_CCC);
		
		Record enterpriseRecord = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(
					dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(contractTable.get(CONTRACT.DOMAIN))).fetchOne(ENTERPRISE.REGISTRY)
				))
				.fetchOne();
		
		contractData.setEnterpriseCIF(enterpriseRecord.get(REGISTRY.DOCUMENT));
		contractData.setEnterpriseName(enterpriseRecord.get(REGISTRY.NAME));
		
		if(null == enterpriseCCC) {
			contractData.setCccId(null);
			contractData.setCccType(null);
			
		}else {
			Record enterpriseCCCTable = dslContext.select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.ID.eq(enterpriseCCC))
					.fetchOne();
			
			contractData.setCccId(enterpriseCCCTable.get(ENTERPRISE_CCC.ID));
			contractData.setCccType(enterpriseCCCTable.get(ENTERPRISE_CCC.TYPE));
			
			contractData.setCompleteCCC(getCCCRegimeCode(enterpriseCCCTable.get(ENTERPRISE_CCC.TYPE))+enterpriseCCCTable.get(ENTERPRISE_CCC.CCC));
		}
		
		// CONTRACT DATA TABLE
		Date currentDate = new Date(new java.util.Date().getTime());
		Result<Record> contractDataTable = null;
		
		if(null != contractData.getEndDate()) { //Para contratos finalizados
			if(currentDate.after( contractData.getEndDate())) {
				contractDataTable = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("TC2"))
				.orderBy(CONTRACT_DATA.START_DATE)
				.fetch();
			}else {
				contractDataTable = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(contractId))
						.and(CONTRACT_DATA.NAME.eq("TC2"))
						.and(CONTRACT_DATA.START_DATE.le(currentDate))
						.and(CONTRACT_DATA.END_DATE.ge(currentDate).or(CONTRACT_DATA.END_DATE.isNull()))
						.fetch();
				
				if(contractDataTable.isEmpty())
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.eq("TC2"))
					.orderBy(CONTRACT_DATA.ID)
					.fetch();
			}
			
		} else {
			
			if(contractData.getStartDate().after(currentDate)) {
				contractDataTable = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(contractId))
						.and(CONTRACT_DATA.NAME.eq("TC2"))
						.and(CONTRACT_DATA.START_DATE.le(new Date(contractData.getStartDate().getTime())))
						.and(CONTRACT_DATA.END_DATE.ge(new Date(contractData.getStartDate().getTime())).or(CONTRACT_DATA.END_DATE.isNull()))
						.fetch();
			}else
				contractDataTable = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(contractId))
						.and(CONTRACT_DATA.NAME.eq("TC2"))
						.orderBy(CONTRACT_DATA.START_DATE.asc())
						.fetch();
			
			if(contractDataTable.isEmpty())
				contractDataTable = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("TC2"))
				.orderBy(CONTRACT_DATA.ID)
				.fetch();
		}
			
		Map<String, String> contractDataMap = new HashMap<>();
		
		for(Record r : contractDataTable){
			contractDataMap.put(r.get(CONTRACT_DATA.NAME), r.get(CONTRACT_DATA.EXPRESSION));
			
			if(r.get(CONTRACT_DATA.NAME).equals("TC2")) {
				contractData.setContracttypeId(r.get(CONTRACT_DATA.ID));
				contractData.setContractType(r.get(CONTRACT_DATA.EXPRESSION));
			}
		}
		
		// CONTRACT INFO TABLE
		Record contractInfoTable = null;
		
		contractInfoTable = dslContext.select().from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("RETA"))
				.fetchOne();
		
		if(null == contractInfoTable)
			contractData.setRetaId(null);
		else
			contractData.setRetaId(contractInfoTable.get(CONTRACT_INFO.ID));
		
		itEmployee.setEmployeeInfo(employeeData);
		itEmployee.setContractInfo(contractData);
	}
	
	private static String createUpdateITEmployeeDB(DSLContext dslContext, Integer domainId, ITEmployee itEmployee) {
		
		Boolean anythingAdded = false;
		
		for(IT it : itEmployee.getIts()) {
			
			if(null == it.getId() || -1 == it.getId()) {	// NUEVO PARTE IT
				
				EmployeeInfo employeeInfo = itEmployee.getEmployeeInfo();
				ContractInfo contractInfo = itEmployee.getContractInfo();
				
				Date startDate = null == it.getStartDate() ? null : new Date(it.getStartDate().getTime());
				Date endDate = null == it.getEndDate() ? null : new Date(it.getEndDate().getTime());
				
				//String nss = employeeInfo.getSsNumber();
				Integer contractId = contractInfo.getContractId();

				SelectConditionStep<Record> query = dslContext
					.select()
					.from(REGISTRY)
					.innerJoin(PERSON).on(PERSON.REGISTRY.eq(REGISTRY.ID))
					.innerJoin(CONTRACT).on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
					.leftJoin(ENTERPRISE_CCC).onKey()
					//.where(PERSON.SOCIAL_SECURITY_NUM.eq(nss))
					.where(CONTRACT.ID.eq(contractId))
//					.and(CONTRACT.START_DATE.le(startDate))
//					.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(startDate)))
				;
				
				String completeCcc = contractInfo.getCompleteCCC();
				if(completeCcc!=null) {
					String ccc = completeCcc.substring(4, completeCcc.length());
					query.and(ENTERPRISE_CCC.DOMAIN.eq(domainId)).and(ENTERPRISE_CCC.CCC.eq(ccc));
				} else {
					query.and(CONTRACT.DOMAIN.eq(domainId));
				}
		
				Result<Record> contractRecords = query.orderBy(CONTRACT.ID.desc())
					.fetch();
				
				if(contractRecords.isEmpty())
					throw new IllegalArgumentException("No existe contrato activo para este trabajador en el periodo de la baja");
				
				//Integer contractId = contractRecords.get(0).get(CONTRACT.ID);
				
				ContractLeaveRecord contractLeaveRecord = dslContext.insertInto(CONTRACT_LEAVE)
					.set(CONTRACT_LEAVE.DOMAIN, domainId)
					.set(CONTRACT_LEAVE.TYPE, it.getTypeLowPart())
					.set(CONTRACT_LEAVE.CONTRACT, contractId)
					.set(CONTRACT_LEAVE.DESCRIPTION, it.getDescription())
					.set(CONTRACT_LEAVE.START_DATE, startDate)
					.set(CONTRACT_LEAVE.END_DATE, endDate)
					.set(CONTRACT_LEAVE.PARENT, it.getParent() == (byte)0 ? null : it.getParent())
					.set(CONTRACT_LEAVE.DISCHARGE_CAUSE, it.getTypeHighPart())
					.set(CONTRACT_LEAVE.DAILY_CGC_BASE, it.getDailyCGCBase())
					.set(CONTRACT_LEAVE.DAILY_CGP_BASE, it.getDailyCGPBase())
					.returning(CONTRACT_LEAVE.ID)
					.fetchOne();
				
				Integer contractLeaveId = contractLeaveRecord.get(CONTRACT_LEAVE.ID);
				
				for(ITPart itPart : it.getITParts()) {
					
					Date date = null == itPart.getDate() ? null : new Date(itPart.getDate().getTime());
					
					ContractLeaveDetailRecord contractLeaveDetail = dslContext.insertInto(CONTRACT_LEAVE_DETAIL)
						.set(CONTRACT_LEAVE_DETAIL.DOMAIN, domainId)
						.set(CONTRACT_LEAVE_DETAIL.TYPE, itPart.getType())
						.set(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE, contractLeaveId)
						.set(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER, itPart.getCollegeNumber())
						.set(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER, itPart.getConfirmOrderNumber())
						.set(CONTRACT_LEAVE_DETAIL.CIAS, itPart.getCias())
						.set(CONTRACT_LEAVE_DETAIL.DATE, date)
						.set(CONTRACT_LEAVE_DETAIL.STATUS, itPart.getStatus())
						.returning(CONTRACT_LEAVE_DETAIL.ID)
						.fetchOne();
				}
				
				Double baseReg = it.getRegulationBase();
				if(null != baseReg && 0.00 != baseReg) {
					dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domainId)
						.set(CONTRACT_DATA.NAME, "BASE_REGULADORA")
						.set(CONTRACT_DATA.CONTRACT, contractId)
						.set(CONTRACT_DATA.EXPRESSION, baseReg.toString())
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.execute();
				}
				
				if(null != it.getDirectPayDate()) {
					dslContext.insertInto(CONTRACT_DATA)
						.set(CONTRACT_DATA.DOMAIN, domainId)
						.set(CONTRACT_DATA.NAME, "INICIO_PAGO_DIRECTO")
						.set(CONTRACT_DATA.CONTRACT, contractId)
						.set(CONTRACT_DATA.EXPRESSION, getExpressionDirectPayDate(it.getDirectPayDate()))
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.execute();
				}
				
				if(it.getTypeLowPart() == (byte)2 || it.getTypeLowPart() == (byte)3) {	// MATERNIDAD || PATERNIDAD
					dslContext.insertInto(CONTRACT_DATA)
						.set(CONTRACT_DATA.DOMAIN, domainId)
						.set(CONTRACT_DATA.NAME, "TIPO_SOLICITANTE_MAT_PAT")
						.set(CONTRACT_DATA.CONTRACT, contractId)
						.set(CONTRACT_DATA.EXPRESSION, it.getMaternityType().toString())
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.execute();
					
					dslContext.insertInto(CONTRACT_DATA)
						.set(CONTRACT_DATA.DOMAIN, domainId)
						.set(CONTRACT_DATA.NAME, "MOTIVO_MAT_PAT")
						.set(CONTRACT_DATA.CONTRACT, contractId)
						.set(CONTRACT_DATA.EXPRESSION, it.getMaternityReason().toString())
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.execute();
					
					Double partialityCoef = it.getPartialityCoef();
					if(null != partialityCoef && 0.00 != partialityCoef) {
						if(it.getTypeLowPart() == (byte)2) {
							dslContext.insertInto(CONTRACT_DATA)
							.set(CONTRACT_DATA.DOMAIN, domainId)
								.set(CONTRACT_DATA.NAME, "COEFICIENTE_MATERNIDAD")
								.set(CONTRACT_DATA.CONTRACT, contractId)
								.set(CONTRACT_DATA.EXPRESSION, partialityCoef.toString())
								.set(CONTRACT_DATA.START_DATE, startDate)
								.set(CONTRACT_DATA.END_DATE, endDate)
								.execute();
						} else if(it.getTypeLowPart() == (byte)3) {
							dslContext.insertInto(CONTRACT_DATA)
							.set(CONTRACT_DATA.DOMAIN, domainId)
								.set(CONTRACT_DATA.NAME, "COEFICIENTE_PATERNIDAD")
								.set(CONTRACT_DATA.CONTRACT, contractId)
								.set(CONTRACT_DATA.EXPRESSION, partialityCoef.toString())
								.set(CONTRACT_DATA.START_DATE, startDate)
								.set(CONTRACT_DATA.END_DATE, endDate)
								.execute();
						}
					}
				}
				
				anythingAdded = true;
				
			} else {					// ACTUALIZAR PARTE IT
				
				Date startDate = null == it.getStartDate() ? null : new Date(it.getStartDate().getTime());
				Date endDate = null == it.getEndDate() ? null : new Date(it.getEndDate().getTime());
				
				dslContext.update(CONTRACT_LEAVE)
					.set(CONTRACT_LEAVE.TYPE, it.getTypeLowPart())
					.set(CONTRACT_LEAVE.DESCRIPTION, it.getDescription())
					.set(CONTRACT_LEAVE.START_DATE, startDate)
					.set(CONTRACT_LEAVE.END_DATE, endDate)
					.set(CONTRACT_LEAVE.PARENT, it.getParent() == (byte)0 ? null : it.getParent())
					.set(CONTRACT_LEAVE.DISCHARGE_CAUSE, it.getTypeHighPart())
					.set(CONTRACT_LEAVE.DAILY_CGC_BASE, it.getDailyCGCBase())
					.set(CONTRACT_LEAVE.DAILY_CGP_BASE, it.getDailyCGPBase())
					.where(CONTRACT_LEAVE.ID.eq(it.getId()))
					.execute();
				
//				dslContext.delete(CONTRACT_LEAVE_DETAIL)
//					.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(it.getId()))
//					.execute();
				
				for(ITPart itPart : it.getITParts()) {
					
					Date date = null == itPart.getDate() ? null : new Date(itPart.getDate().getTime());
					
					if(null != itPart.getId() && itPart.isDelete()) {
						dslContext.delete(CONTRACT_LEAVE_DETAIL)
						.where(CONTRACT_LEAVE_DETAIL.ID.eq(itPart.getId()))
						.execute();
					} else if(null != itPart.getId() && itPart.isModify()) {
						dslContext.update(CONTRACT_LEAVE_DETAIL)
						.set(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE, it.getId())
						.set(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER, itPart.getCollegeNumber())
						.set(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER, itPart.getConfirmOrderNumber())
						.set(CONTRACT_LEAVE_DETAIL.CIAS, itPart.getCias())
						.set(CONTRACT_LEAVE_DETAIL.DATE, date)
						.set(CONTRACT_LEAVE_DETAIL.STATUS, itPart.getStatus())
						.where(CONTRACT_LEAVE_DETAIL.ID.eq(itPart.getId()))
						.execute();	
					} else if(null == itPart.getId() && null!= date) {
						dslContext.insertInto(CONTRACT_LEAVE_DETAIL)
						.set(CONTRACT_LEAVE_DETAIL.DOMAIN, domainId)
						.set(CONTRACT_LEAVE_DETAIL.TYPE, itPart.getType())
						.set(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE, it.getId())
						.set(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER, itPart.getCollegeNumber())
						.set(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER, itPart.getConfirmOrderNumber())
						.set(CONTRACT_LEAVE_DETAIL.CIAS, itPart.getCias())
						.set(CONTRACT_LEAVE_DETAIL.DATE, date)
						.set(CONTRACT_LEAVE_DETAIL.STATUS, itPart.getStatus())
						.execute();
					}
						
				}
				
				Integer contractId = itEmployee.getContractInfo().getContractId();
				
				// Remove if is all ready exists
				dslContext.delete(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(
							CONTRACT_DATA.NAME.eq("TIPO_SOLICITANTE_MAT_PAT")
						.or(CONTRACT_DATA.NAME.eq("MOTIVO_MAT_PAT"))
						.or(CONTRACT_DATA.NAME.eq("BASE_REGULADORA"))
						.or(CONTRACT_DATA.NAME.eq("COEFICIENTE_MATERNIDAD"))
						.or(CONTRACT_DATA.NAME.eq("COEFICIENTE_PATERNIDAD"))
						.or(CONTRACT_DATA.NAME.eq("INICIO_PAGO_DIRECTO"))
					).and(CONTRACT_DATA.START_DATE.eq(startDate))
					.execute();
				
				if(null != it.getDirectPayDate()) {
					dslContext.insertInto(CONTRACT_DATA)
						.set(CONTRACT_DATA.DOMAIN, domainId)
						.set(CONTRACT_DATA.NAME, "INICIO_PAGO_DIRECTO")
						.set(CONTRACT_DATA.CONTRACT, contractId)
						.set(CONTRACT_DATA.EXPRESSION, getExpressionDirectPayDate(it.getDirectPayDate()))
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.execute();
				}
				
				Double baseReg = it.getRegulationBase();
				if(null != baseReg && 0.00 != baseReg) {
					dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domainId)
						.set(CONTRACT_DATA.NAME, "BASE_REGULADORA")
						.set(CONTRACT_DATA.CONTRACT, contractId)
						.set(CONTRACT_DATA.EXPRESSION, baseReg.toString())
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.execute();
				}
				
				if(it.getTypeLowPart() == (byte)2 || it.getTypeLowPart() == (byte)3) {	// MATERNIDAD || PATERNIDAD
					
					if(null != it.getMaternityType())
						dslContext.insertInto(CONTRACT_DATA)
							.set(CONTRACT_DATA.DOMAIN, domainId)
							.set(CONTRACT_DATA.NAME, "TIPO_SOLICITANTE_MAT_PAT")
							.set(CONTRACT_DATA.CONTRACT, contractId)
							.set(CONTRACT_DATA.EXPRESSION, it.getMaternityType().toString())
							.set(CONTRACT_DATA.START_DATE, startDate)
							.set(CONTRACT_DATA.END_DATE, endDate)
							.execute();
					
					if(null != it.getMaternityReason())
						dslContext.insertInto(CONTRACT_DATA)
						.set(CONTRACT_DATA.DOMAIN, domainId)
						.set(CONTRACT_DATA.NAME, "MOTIVO_MAT_PAT")
						.set(CONTRACT_DATA.CONTRACT, contractId)
						.set(CONTRACT_DATA.EXPRESSION, it.getMaternityReason().toString())
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.execute();
					
					Double partialityCoef = it.getPartialityCoef();
					if(null != partialityCoef && 0.00 != partialityCoef) {
						if(it.getTypeLowPart() == (byte)2) {
							dslContext.insertInto(CONTRACT_DATA)
							.set(CONTRACT_DATA.DOMAIN, domainId)
								.set(CONTRACT_DATA.NAME, "COEFICIENTE_MATERNIDAD")
								.set(CONTRACT_DATA.CONTRACT, contractId)
								.set(CONTRACT_DATA.EXPRESSION, partialityCoef.toString())
								.set(CONTRACT_DATA.START_DATE, startDate)
								.set(CONTRACT_DATA.END_DATE, endDate)
								.execute();
						} else if(it.getTypeLowPart() == (byte)3) {
							dslContext.insertInto(CONTRACT_DATA)
							.set(CONTRACT_DATA.DOMAIN, domainId)
								.set(CONTRACT_DATA.NAME, "COEFICIENTE_PATERNIDAD")
								.set(CONTRACT_DATA.CONTRACT, contractId)
								.set(CONTRACT_DATA.EXPRESSION, partialityCoef.toString())
								.set(CONTRACT_DATA.START_DATE, startDate)
								.set(CONTRACT_DATA.END_DATE, endDate)
								.execute();
						}
					}
				}
				
			}
		}
		
		if(anythingAdded)
			return "Parte IT creado.";
		else
			return "Parte IT actualizado.";
	}
	
	private static String getExpressionDirectPayDate(java.util.Date directPayDate) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(directPayDate);
		return "FECHA(" + cal.get(Calendar.YEAR) + "," + (cal.get(Calendar.MONTH) + 1) + "," + cal.get(Calendar.DAY_OF_MONTH) + ")";
	}
	
	private static java.util.Date getDirectPayDateByExpression(String directPayDateExpression) {
		Calendar cal = Calendar.getInstance();
		
		String date = directPayDateExpression.substring(6, directPayDateExpression.length() - 1);
		String[] directPayDateArr = date.split(",");
		
		cal.set(Calendar.YEAR, Integer.parseInt(directPayDateArr[0]));
		cal.set(Calendar.MONTH, (Integer.parseInt(directPayDateArr[1]) - 1));
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(directPayDateArr[2]));
		
		return cal.getTime();
	}

	private static void setComunicationITDB(DSLContext dslContext, Integer domainId, ITEmployee itEmployee, IT it) {
		
		Date startDate = null == it.getStartDate() ? null : new Date(it.getStartDate().getTime());
		Date endDate = null == it.getEndDate() ? null : new Date(it.getEndDate().getTime());
		
		Integer contractId = itEmployee.getContractInfo().getContractId();
		
		Record contractLeaveRecord = dslContext.select().from(CONTRACT_LEAVE)
			.where(CONTRACT_LEAVE.START_DATE.eq(startDate))
			.and(CONTRACT_LEAVE.CONTRACT.eq(contractId))
			.fetchOne();
		
		for(ITPart itPart : it.getITParts()) {
			Date date = null == itPart.getDate() ? null : new Date(itPart.getDate().getTime());
			
			if(itPart.getType() == (byte)0) {
				
				Record contractLeaveDetail = dslContext.select().from(CONTRACT_LEAVE_DETAIL)
						.where(CONTRACT_LEAVE_DETAIL.DATE.eq(date))
						.and(CONTRACT_LEAVE_DETAIL.TYPE.eq((byte)0))
						.and(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(
								contractLeaveRecord.get(CONTRACT_LEAVE.ID)
						)).fetchOne();
					
				LeaveBatchRecord leaveBatchRecord = dslContext.insertInto(LEAVE_BATCH)
					.set(LEAVE_BATCH.DOMAIN, domainId)
					.set(LEAVE_BATCH.DATE, new Timestamp(new java.util.Date().getTime()))
					.set(LEAVE_BATCH.STATUS, (byte)1)
					.set(LEAVE_BATCH.COMMUNICATION_ID, "COMUNICA")
					.set(LEAVE_BATCH.INCOME_FILE, (byte[]) null)
					.set(LEAVE_BATCH.OUTCOME_FILE, (byte[]) null)
					.returning(LEAVE_BATCH.ID).fetchOne();
				
				dslContext.insertInto(LEAVE_BATCH_DETAIL)
					.set(LEAVE_BATCH_DETAIL.DOMAIN, domainId)
					.set(LEAVE_BATCH_DETAIL.LEAVE_BATCH, leaveBatchRecord.getId())
					.set(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL, contractLeaveDetail.get(CONTRACT_LEAVE_DETAIL.ID))
					.execute();
			}
		}
	}


	private static String deleteITDB(DSLContext dslContext, Integer domainId, Integer itId) {
		
		Record contractLeaveRecord = dslContext.select().from(CONTRACT_LEAVE).where(CONTRACT_LEAVE.ID.eq(itId)).fetchOne();
		
		Date startDate = contractLeaveRecord.get(CONTRACT_LEAVE.START_DATE);
		Integer contractId = contractLeaveRecord.get(CONTRACT_LEAVE.CONTRACT);
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(
					CONTRACT_DATA.NAME.eq("INICIO_PAGO_DIRECTO")
				.or(CONTRACT_DATA.NAME.eq("TIPO_SOLICITANTE_MAT_PAT"))
				.or(CONTRACT_DATA.NAME.eq("MOTIVO_MAT_PAT"))
				.or(CONTRACT_DATA.NAME.eq("BASE_REGULADORA"))
				.or(CONTRACT_DATA.NAME.eq("COEFICIENTE_MATERNIDAD"))
				.or(CONTRACT_DATA.NAME.eq("COEFICIENTE_PATERNIDAD")))
			.and(CONTRACT_DATA.START_DATE.eq(startDate))
			.execute();
		
		dslContext.delete(LEAVE_BATCH_DETAIL)
		.where(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL.in(
				dslContext.select(CONTRACT_LEAVE_DETAIL.ID).from(CONTRACT_LEAVE_DETAIL)
					.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(itId))
					.fetch(CONTRACT_LEAVE_DETAIL.ID)
		)).execute();
		
		dslContext.delete(LEAVE_BATCH)
			.where(LEAVE_BATCH.ID.in(
					dslContext.select(LEAVE_BATCH_DETAIL.LEAVE_BATCH).from(LEAVE_BATCH_DETAIL)
						.where(LEAVE_BATCH_DETAIL.CONTRACT_LEAVE_DETAIL.in(
								dslContext.select(CONTRACT_LEAVE_DETAIL.ID).from(CONTRACT_LEAVE_DETAIL)
									.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(itId))
									.fetch(CONTRACT_LEAVE_DETAIL.ID)
						)).fetch(LEAVE_BATCH_DETAIL.LEAVE_BATCH)
			)).execute();
		
		dslContext.delete(CONTRACT_LEAVE_DETAIL)
			.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(itId))
			.execute();
		
		dslContext.delete(CONTRACT_LEAVE)
			.where(CONTRACT_LEAVE.ID.eq(itId))
				.execute();
		
		return "Parte IT eliminado correctamente";
	}

	

	

	// --------------------------------------- AUX METHODS -----------------------------
	
	public static Date parseDate(java.util.Date date) {
		if(null == date)
			return null;
		
		DateUtils.resetTime(date);
		return new Date(date.getTime());
	}
	
	public static String parseContractTableStr(String exp) {
		if(null == exp)
			return null;
		
		return "\""+ exp +"\"";
	}
	
	public static String parseContractTable(String exp) {
		if(null == exp)
			return null;
		
		return exp.split("\"")[1];
	}
	
	public static String getPaymentTypeName(byte type) {
		switch (type) {
		case (byte) 0:
			return "EFECTIVO";
		case (byte) 4:
			return "CHEQUE";
		case (byte) 5:
			return "TRANSFERENCIA";
		default:
			return "";
		}
	}
	
	private static ArrayList<JourneyDuration> orderByWeekDay(ArrayList<JourneyDuration> journeyList) {
		ArrayList<JourneyDuration> result = new ArrayList<>();
		
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_LUNES"))
				result.add(journey);
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_MARTES"))
				result.add(journey);
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_MIERCOLES"))
				result.add(journey);
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_JUEVES"))
				result.add(journey);
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_VIERNES"))
				result.add(journey);
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_SABADO"))
				result.add(journey);
		for(JourneyDuration journey : journeyList)
			if(journey.getName().equals("HORAS_DOMINGO"))
				result.add(journey);
		
		return result;
	}
	
	private static String getCCCRegimeCode(Byte cccRegime) {
		switch (cccRegime) {
		case 0:
			return "0111";
		case 1:
			return "0111";
		case 2:
			return "0111";
		case 3:
			return "0111";
		case 4:
			return "0111";
		case 5:
			return "0111";
		case 6:
			return "0138";
		case 7:
			return "0163";
		case 8:
			return "0112";
		default:
			return "0111";
		}
	}
	
//	public static void main(String[] args) {
//	    
//	    try ( CloseableAONContext aonContext = AONContext.getAONContext("b72384936-ayudat.aonsolutions.org", "aon") ){
//		Integer domainId = aonContext.getDomainId();
//		DSLContext dslContext = aonContext.getDslContext();
//
//		List<ITEmployee> oldEmployeesIT = getEmployeesITInfoDB(dslContext, domainId, true);
//		List<ITEmployee> newEmployeesIT = getEmployeesITInfo(dslContext, CONTRACT.DOMAIN.eq(domainId).and(CONTRACT.ID.gt(0)));
//		
//		for (int i = 0; i < newEmployeesIT.size(); i++) {
//		    ITEmployee newEmployeeIT = newEmployeesIT.get(i);
//		    ITEmployee oldEmployeeIT = oldEmployeesIT.get(i);
//		    
//		    if ( !EqualsBuilder.reflectionEquals(newEmployeeIT, oldEmployeeIT, true) ) {
//			
//			    EmployeeInfo newEmployeeInfo = newEmployeeIT.getEmployeeInfo();
//			    EmployeeInfo oldEmployeeInfo = oldEmployeeIT.getEmployeeInfo();
//			    
//			    if ( !EqualsBuilder.reflectionEquals(newEmployeeInfo, oldEmployeeInfo, true)) {
//				System.err.println("ERROR [EmployeeInfo] : " + newEmployeeInfo.getFullName());
//			    }
//			    
//			    List<IT> newIts = newEmployeeIT.getIts();
//			    List<IT> oldIts = oldEmployeeIT.getIts();
//			    
//			    for ( int j = 0; j < newIts.size() ; j++ ) {
//				IT newIt = newIts.get(j);
//				IT oldIt = oldIts.get(j);
//				if (!EqualsBuilder.reflectionEquals(newIt, oldIt, true, null, "itParts")) {
//				    System.err.println("ERROR [IT] : " + newIt.getFullName() +", " + newIt.getStartDate() + " (" + newIt.getTypeLowPart() +")" );
//				}				
//				
//				List<ITPart> newItParts = newIt.getITParts();
//				List<ITPart> oldItParts = oldIt.getITParts();
//				for (int z = 0; z < newItParts.size(); z++) {
//				    ITPart newItPart = newItParts.get(z);
//				    ITPart oldItPart = oldItParts.get(z);
//					if (!EqualsBuilder.reflectionEquals(newItPart, oldItPart, true, null, "itParts")) {
//					    System.err.println("ERROR [ITPART] : " + newIt.getFullName() +", " + newIt.getStartDate() + " (" + newItPart.getConfirmOrderNumber() +")" );
//					}				
//				}
//				
//			    }
//			    
//			    ContractInfo newContractInfo = newEmployeeIT.getContractInfo();
//			    ContractInfo oldContractInfo = oldEmployeeIT.getContractInfo();
//
//		    }
//		    
//		}
//		
//	    }
//	}

}
