package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractLeaveDetail.CONTRACT_LEAVE_DETAIL;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.jooq.tables.records.ContractLeaveRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.ibm.icu.util.Calendar;

public class JooqIT {

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static List<ITEmployee> getEmployeesITInfo(Connection conn, Integer itIds []) {
		return getEmployeesITInfoDB(DSL.using(conn, getDefaultSettings()), CONTRACT_LEAVE.ID.in(itIds) );
	}

	public static List<ITEmployee> getEmployeesITInfo(AONContext aonContext, Collection<Integer> itIds) {
		return getEmployeesITInfoDB(aonContext.getDslContext(), CONTRACT_LEAVE.ID.in(itIds) );
	}

	public static List<ITEmployee> getEmployeesITInfo(Connection conn, Integer domainId, Boolean allEmployees) {
		return getEmployeesITInfoDB(DSL.using(conn, getDefaultSettings()), domainId, allEmployees);
	}
	
	public static String deleteIT(Connection conn, Integer domainId, Integer itId) {
		return deleteITDB(DSL.using(conn, getDefaultSettings()), domainId, itId);
	}

	public static String createUpdateITEmployee(Connection conn, Integer domainId, ITEmployee employeeITInfo) {
		return createUpdateITEmployeeDB(DSL.using(conn, getDefaultSettings()), domainId, employeeITInfo);
	}
	
	private static List<ITEmployee> getEmployeesITInfoDB(DSLContext dslContext, Integer domainId, Boolean allEmployees) {
		List<ITEmployee> itEmployees = new ArrayList<ITEmployee>();
		
		List<Integer> allContractIds = null;
		
		if(allEmployees) {
			// ------------------------------------------------ Get all contracts from domainId
			allContractIds = dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.DOMAIN.eq(domainId))
					.fetch(CONTRACT.ID);
		} else {
			// ------------------------------------------------ Get active contracts from domainId or ends in the last two months
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			
			Date contract_endDate = new Date(cal.getTimeInMillis());
			
			allContractIds = dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.DOMAIN.eq(domainId))
					.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(contract_endDate)))
					.fetch(CONTRACT.ID);
		}
		
		for(Integer contractId : allContractIds) {
			
			// --------------------------------------------- Init
			
			ITEmployee itEmployee = new ITEmployee();
			setEmployeeInfo(itEmployee, contractId, dslContext);
			
			Result<Record> contractLeaveRecords = dslContext.select().from(CONTRACT_LEAVE)
					.where(CONTRACT_LEAVE.CONTRACT.eq(contractId))
					.orderBy(CONTRACT_LEAVE.START_DATE.asc())
					.fetch();
			
			if(contractLeaveRecords.isEmpty())
				itEmployee.setStatus((byte)0);
			else {
				for(Record contractLeaveRecord : contractLeaveRecords) {
					IT it = new IT();
					
					Integer contractLeaveId = contractLeaveRecord.get(CONTRACT_LEAVE.ID);
					
					it.setId(contractLeaveId);
					it.setDomain(contractLeaveRecord.get(CONTRACT_LEAVE.DOMAIN));
					it.setTypeLowPart(contractLeaveRecord.get(CONTRACT_LEAVE.TYPE));
					it.setContract(contractLeaveRecord.get(CONTRACT_LEAVE.CONTRACT));
					it.setDescription(contractLeaveRecord.get(CONTRACT_LEAVE.DESCRIPTION));
					it.setStartDate(contractLeaveRecord.get(CONTRACT_LEAVE.START_DATE));
					it.setEndDate(contractLeaveRecord.get(CONTRACT_LEAVE.END_DATE));
					it.setDailyCGCBase(contractLeaveRecord.get(CONTRACT_LEAVE.DAILY_CGC_BASE));
					it.setDailyCGPBase(contractLeaveRecord.get(CONTRACT_LEAVE.DAILY_CGP_BASE));
					it.setParent(contractLeaveRecord.get(CONTRACT_LEAVE.PARENT));
					it.setDailyREGBase(contractLeaveRecord.get(CONTRACT_LEAVE.DAILY_REG_BASE));
					it.setTypeHighPart(contractLeaveRecord.get(CONTRACT_LEAVE.DISCHARGE_CAUSE));
					
					// Matenity
					if(it.getTypeLowPart() == (byte)2 || it.getTypeLowPart() == (byte)3) {
						Result<Record> contractDataMaternityRecords = dslContext.select().from(CONTRACT_DATA)
							.where(CONTRACT_DATA.NAME.eq("TIPO_SOLICITANTE_MAT_PAT").or(CONTRACT_DATA.NAME.eq("MOTIVO_MAT_PAT")))
							.and(CONTRACT_DATA.CONTRACT.eq(itEmployee.getContractInfo().getContractId()))
							.and(CONTRACT_DATA.START_DATE.eq(contractLeaveRecord.get(CONTRACT_LEAVE.START_DATE)))
							.fetch();
						
						for(Record record : contractDataMaternityRecords) {
							if(record.get(CONTRACT_DATA.NAME) == "TIPO_SOLICITANTE_MAT_PAT" || record.get(CONTRACT_DATA.NAME).equals("TIPO_SOLICITANTE_MAT_PAT"))
								it.setMaternityType(Byte.parseByte(record.get(CONTRACT_DATA.EXPRESSION)));
							if(record.get(CONTRACT_DATA.NAME) == "MOTIVO_MAT_PAT" || record.get(CONTRACT_DATA.NAME).equals("MOTIVO_MAT_PAT"))
								it.setMaternityReason(Byte.parseByte(record.get(CONTRACT_DATA.EXPRESSION)));
						}
							
					}
					
					// Is parent?
					Result<Record> contractLeaveParentRecords = dslContext.select().from(CONTRACT_LEAVE)
							.where(CONTRACT_LEAVE.PARENT.eq(contractLeaveId))
							.fetch();
					
					if(contractLeaveParentRecords.isEmpty())
						it.setIsParent(false);
					else
						it.setIsParent(true);
					
					Result<Record> contractLeaveDetailRecords = dslContext.select().from(CONTRACT_LEAVE_DETAIL)
							.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(contractLeaveId))
							.orderBy(CONTRACT_LEAVE_DETAIL.DATE.desc())
							.fetch();
					
					for(Record contractLeaveDetailRecord : contractLeaveDetailRecords) {
						ITPart itPart = new ITPart();
						
						itPart.setDomain(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.DOMAIN));
						itPart.setType(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.TYPE));
						itPart.setIt(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE));
						itPart.setCollegeNumber(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER));
						itPart.setConfirmOrderNumber(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER));
						itPart.setCias(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.CIAS));
						itPart.setDate(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.DATE));
						itPart.setStatus(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.STATUS));
						
						it.addITPart(itPart);
						
					}
					
					// Check status
					if(null == contractLeaveRecord.get(CONTRACT_LEAVE.END_DATE))
						itEmployee.setStatus((byte)1);
					else
						itEmployee.setStatus((byte)0);
					
					itEmployee.addIT(it);
					
				}
				
			}
			
			itEmployees.add(itEmployee);

		}
		
		return itEmployees;
	}
	
	private static List<ITEmployee> getEmployeesITInfoDB(DSLContext dslContext, Condition condition) {
		List<ITEmployee> itEmployees = new ArrayList<ITEmployee>();
		
		List<Integer> allContractIds = null;
		
		allContractIds = dslContext
				.select(CONTRACT.ID)
				.from(CONTRACT)
				.innerJoin(CONTRACT_LEAVE).onKey()
				.where(condition)
				.fetch(CONTRACT.ID);
		
		for(Integer contractId : allContractIds) {
			
			// --------------------------------------------- Init
			
			ITEmployee itEmployee = new ITEmployee();
			setEmployeeInfo(itEmployee, contractId, dslContext);
			
			Result<Record> contractLeaveRecords = 
					dslContext
					.select()
					.from(CONTRACT)
					.innerJoin(CONTRACT_LEAVE).onKey()
					.where(condition)
					.and(CONTRACT_LEAVE.CONTRACT.eq(contractId))
					.orderBy(CONTRACT_LEAVE.START_DATE.asc())
					.fetch();
			
			if(contractLeaveRecords.isEmpty())
				itEmployee.setStatus((byte)0);
			else {
				for(Record contractLeaveRecord : contractLeaveRecords) {
					IT it = new IT();
					
					Integer contractLeaveId = contractLeaveRecord.get(CONTRACT_LEAVE.ID);
					
					it.setId(contractLeaveId);
					it.setDomain(contractLeaveRecord.get(CONTRACT_LEAVE.DOMAIN));
					it.setTypeLowPart(contractLeaveRecord.get(CONTRACT_LEAVE.TYPE));
					it.setContract(contractLeaveRecord.get(CONTRACT_LEAVE.CONTRACT));
					it.setDescription(contractLeaveRecord.get(CONTRACT_LEAVE.DESCRIPTION));
					it.setStartDate(contractLeaveRecord.get(CONTRACT_LEAVE.START_DATE));
					it.setEndDate(contractLeaveRecord.get(CONTRACT_LEAVE.END_DATE));
					it.setDailyCGCBase(contractLeaveRecord.get(CONTRACT_LEAVE.DAILY_CGC_BASE));
					it.setDailyCGPBase(contractLeaveRecord.get(CONTRACT_LEAVE.DAILY_CGP_BASE));
					it.setParent(contractLeaveRecord.get(CONTRACT_LEAVE.PARENT));
					it.setDailyREGBase(contractLeaveRecord.get(CONTRACT_LEAVE.DAILY_REG_BASE));
					it.setTypeHighPart(contractLeaveRecord.get(CONTRACT_LEAVE.DISCHARGE_CAUSE));
					
					// Matenity
					if(it.getTypeLowPart() == (byte)2 || it.getTypeLowPart() == (byte)3) {
						Result<Record> contractDataMaternityRecords = dslContext.select().from(CONTRACT_DATA)
							.where(CONTRACT_DATA.NAME.eq("TIPO_SOLICITANTE_MAT_PAT").or(CONTRACT_DATA.NAME.eq("MOTIVO_MAT_PAT")))
							.and(CONTRACT_DATA.CONTRACT.eq(itEmployee.getContractInfo().getContractId()))
							.and(CONTRACT_DATA.START_DATE.eq(contractLeaveRecord.get(CONTRACT_LEAVE.START_DATE)))
							.fetch();
						
						for(Record record : contractDataMaternityRecords) {
							if(record.get(CONTRACT_DATA.NAME) == "TIPO_SOLICITANTE_MAT_PAT" || record.get(CONTRACT_DATA.NAME).equals("TIPO_SOLICITANTE_MAT_PAT"))
								it.setMaternityType(Byte.parseByte(record.get(CONTRACT_DATA.EXPRESSION)));
							if(record.get(CONTRACT_DATA.NAME) == "MOTIVO_MAT_PAT" || record.get(CONTRACT_DATA.NAME).equals("MOTIVO_MAT_PAT"))
								it.setMaternityReason(Byte.parseByte(record.get(CONTRACT_DATA.EXPRESSION)));
						}
							
					}
					
					// Is parent?
					Result<Record> contractLeaveParentRecords = dslContext.select().from(CONTRACT_LEAVE)
							.where(CONTRACT_LEAVE.PARENT.eq(contractLeaveId))
							.fetch();
					
					if(contractLeaveParentRecords.isEmpty())
						it.setIsParent(false);
					else
						it.setIsParent(true);
					
					Result<Record> contractLeaveDetailRecords = dslContext.select().from(CONTRACT_LEAVE_DETAIL)
							.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(contractLeaveId))
							.orderBy(CONTRACT_LEAVE_DETAIL.DATE.desc())
							.fetch();
					
					for(Record contractLeaveDetailRecord : contractLeaveDetailRecords) {
						ITPart itPart = new ITPart();
						
						itPart.setDomain(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.DOMAIN));
						itPart.setType(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.TYPE));
						itPart.setIt(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE));
						itPart.setCollegeNumber(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER));
						itPart.setConfirmOrderNumber(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER));
						itPart.setCias(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.CIAS));
						itPart.setDate(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.DATE));
						itPart.setStatus(contractLeaveDetailRecord.get(CONTRACT_LEAVE_DETAIL.STATUS));
						
						it.addITPart(itPart);
						
					}
					
					// Check status
					if(null == contractLeaveRecord.get(CONTRACT_LEAVE.END_DATE))
						itEmployee.setStatus((byte)1);
					else
						itEmployee.setStatus((byte)0);
					
					itEmployee.addIT(it);
					
				}
				
			}
			
			itEmployees.add(itEmployee);

		}
		
		return itEmployees;
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
		
		contractData.setOldStartDate(contractTable.get(CONTRACT.START_DATE));
		contractData.setOldEndDate(contractTable.get(CONTRACT.END_DATE));
		
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
				
				Date startDate = null == it.getStartDate() ? null : new Date(it.getStartDate().getTime());
				Date endDate = null == it.getEndDate() ? null : new Date(it.getEndDate().getTime());
				
				Integer contractId = itEmployee.getContractInfo().getContractId();
				
				dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
				
				ContractLeaveRecord contractLeaveRecord = dslContext.insertInto(CONTRACT_LEAVE)
					.set(CONTRACT_LEAVE.DOMAIN, domainId)
					.set(CONTRACT_LEAVE.TYPE, it.getTypeLowPart())
					.set(CONTRACT_LEAVE.CONTRACT, contractId)
					.set(CONTRACT_LEAVE.DESCRIPTION, it.getDescription())
					.set(CONTRACT_LEAVE.START_DATE, startDate)
					.set(CONTRACT_LEAVE.END_DATE, endDate)
					.set(CONTRACT_LEAVE.PARENT, it.getParent() == (byte)0 ? null : it.getParent())
					.set(CONTRACT_LEAVE.DISCHARGE_CAUSE, it.getTypeHighPart())
					.returning(CONTRACT_LEAVE.ID)
					.fetchOne();
				
				dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
				 
				Integer contractLeaveId = contractLeaveRecord.get(CONTRACT_LEAVE.ID);
				
				for(ITPart itPart : it.getITParts()) {
					
					Date date = null == itPart.getDate() ? null : new Date(itPart.getDate().getTime());
					
					dslContext.insertInto(CONTRACT_LEAVE_DETAIL)
						.set(CONTRACT_LEAVE_DETAIL.DOMAIN, domainId)
						.set(CONTRACT_LEAVE_DETAIL.TYPE, itPart.getType())
						.set(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE, contractLeaveId)
						.set(CONTRACT_LEAVE_DETAIL.COLLEGE_NUMBER, itPart.getCollegeNumber())
						.set(CONTRACT_LEAVE_DETAIL.CONFIRM_ORDER, itPart.getConfirmOrderNumber())
						.set(CONTRACT_LEAVE_DETAIL.CIAS, itPart.getCias())
						.set(CONTRACT_LEAVE_DETAIL.DATE, date)
						.set(CONTRACT_LEAVE_DETAIL.STATUS, itPart.getStatus())
						.execute();
				}
				
				if(it.getTypeLowPart() == (byte)2 || it.getTypeLowPart() == (byte)3) {	// MATERNIDAD || PATERNIDAD
					dslContext.insertInto(CONTRACT_DATA)
						.set(CONTRACT_DATA.DOMAIN, domainId)
						.set(CONTRACT_DATA.NAME, "TIPO_SOLICITANTE_MAT_PAT")
						.set(CONTRACT_DATA.CONTRACT, contractId)
						.set(CONTRACT_DATA.EXPRESSION, it.getMaternityType().toString())
						.set(CONTRACT_DATA.START_DATE, startDate)
						.execute();
					
					dslContext.insertInto(CONTRACT_DATA)
						.set(CONTRACT_DATA.DOMAIN, domainId)
						.set(CONTRACT_DATA.NAME, "MOTIVO_MAT_PAT")
						.set(CONTRACT_DATA.CONTRACT, contractId)
						.set(CONTRACT_DATA.EXPRESSION, it.getMaternityReason().toString())
						.set(CONTRACT_DATA.START_DATE, startDate)
						.execute();
				}
				
				anythingAdded = true;
				
			} else {					// ACTUALIZAR PARTE IT
				
				Date startDate = null == it.getStartDate() ? null : new Date(it.getStartDate().getTime());
				Date endDate = null == it.getEndDate() ? null : new Date(it.getEndDate().getTime());
				
				dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
				
				dslContext.update(CONTRACT_LEAVE)
					.set(CONTRACT_LEAVE.TYPE, it.getTypeLowPart())
					.set(CONTRACT_LEAVE.DESCRIPTION, it.getDescription())
					.set(CONTRACT_LEAVE.START_DATE, startDate)
					.set(CONTRACT_LEAVE.END_DATE, endDate)
					.set(CONTRACT_LEAVE.PARENT, it.getParent() == (byte)0 ? null : it.getParent())
					.set(CONTRACT_LEAVE.DISCHARGE_CAUSE, it.getTypeHighPart())
					.where(CONTRACT_LEAVE.ID.eq(it.getId()))
					.execute();
				
				dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
				
				dslContext.delete(CONTRACT_LEAVE_DETAIL)
					.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(it.getId()))
					.execute();
				
				for(ITPart itPart : it.getITParts()) {
					
					Date date = null == itPart.getDate() ? null : new Date(itPart.getDate().getTime());
					
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
				
				Integer contractId = itEmployee.getContractInfo().getContractId();
				
				// Remove if is all ready exists
				dslContext.delete(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.eq("TIPO_SOLICITANTE_MAT_PAT").or(CONTRACT_DATA.NAME.eq("MOTIVO_MAT_PAT")))
					.and(CONTRACT_DATA.START_DATE.eq(startDate))
					.execute();
				
				if(it.getTypeLowPart() == (byte)2 || it.getTypeLowPart() == (byte)3) {	// MATERNIDAD || PATERNIDAD
					
					if(null != it.getMaternityType())
						dslContext.insertInto(CONTRACT_DATA)
							.set(CONTRACT_DATA.DOMAIN, domainId)
							.set(CONTRACT_DATA.NAME, "TIPO_SOLICITANTE_MAT_PAT")
							.set(CONTRACT_DATA.CONTRACT, contractId)
							.set(CONTRACT_DATA.EXPRESSION, it.getMaternityType().toString())
							.set(CONTRACT_DATA.START_DATE, startDate)
							.execute();
					
					if(null != it.getMaternityReason())
						dslContext.insertInto(CONTRACT_DATA)
						.set(CONTRACT_DATA.DOMAIN, domainId)
						.set(CONTRACT_DATA.NAME, "MOTIVO_MAT_PAT")
						.set(CONTRACT_DATA.CONTRACT, contractId)
						.set(CONTRACT_DATA.EXPRESSION, it.getMaternityReason().toString())
						.set(CONTRACT_DATA.START_DATE, startDate)
						.execute();
				}
				
			}
		}
		
		if(anythingAdded)
			return "Parte IT creado.";
		else
			return "Parte IT actualizado.";
	}

	private static String deleteITDB(DSLContext dslContext, Integer domainId, Integer itId) {
		
		dslContext.execute("SET FOREIGN_KEY_CHECKS=0;");
		
		Record contractLeaveRecord = dslContext.select().from(CONTRACT_LEAVE).where(CONTRACT_LEAVE.ID.eq(itId)).fetchOne();
		Byte type = contractLeaveRecord.get(CONTRACT_LEAVE.TYPE);
		if(type == (byte)2 || type == (byte)3) {
			Date startDate = contractLeaveRecord.get(CONTRACT_LEAVE.START_DATE);
			Integer contractId = contractLeaveRecord.get(CONTRACT_LEAVE.CONTRACT);
			dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("TIPO_SOLICITANTE_MAT_PAT").or(CONTRACT_DATA.NAME.eq("MOTIVO_MAT_PAT")))
				.and(CONTRACT_DATA.START_DATE.eq(startDate))
				.execute();
		}
		
		dslContext.delete(CONTRACT_LEAVE_DETAIL)
			.where(CONTRACT_LEAVE_DETAIL.CONTRACT_LEAVE.eq(itId))
			.execute();
		
		dslContext.delete(CONTRACT_LEAVE)
			.where(CONTRACT_LEAVE.ID.eq(itId))
				.execute();
		
		dslContext.execute("SET FOREIGN_KEY_CHECKS=1;");
		
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

	

}
