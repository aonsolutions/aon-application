package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.Quartet;
import com.esferalia.aon.gwt.payroll.client.Quintet;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class JooqEvents {

	private static Settings SETTINGS = null;
	
	public static WorkplaceEmployees getWorkplaceEmployees(Connection conn, Workplace workplace, Integer domainId) {
		return getWorkplaceEmployeesInformation(DSL.using(conn, getDefaultSettings()), workplace, domainId);
	}
	
	public static EventsWorkplace setWorkplaceEmployees(Connection conn, EventsWorkplace updateEventsWorkplace) {
		return setWorkplaceEmployeesInformation(DSL.using(conn, getDefaultSettings()), updateEventsWorkplace);
	}
	
	public static WorkplaceEmployees getWorkplaceEmployeesEvents(Connection conn, Integer workplaceId) {
		return getWorkplaceEmployeesEventsInformation(DSL.using(conn, getDefaultSettings()), workplaceId);
	}
	
	public static ArrayList<EventEmployee> setEventsDraft(Connection connection, ArrayList<EventEmployee> eventEmployees) {
		return setEventsDraftDB(DSL.using(connection, getDefaultSettings()), eventEmployees);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static WorkplaceEmployees getWorkplaceEmployeesEventsInformation(DSLContext dslContext, Integer workplaceId) {
		WorkplaceEmployees workplaceEmployees = new WorkplaceEmployees();
		System.out.println("Workplace :"+workplaceId);
		
		java.util.Date newDate = new java.util.Date();
		java.util.Date newDatePreviusMonth = DateUtils.getFirstDayOfMonth(newDate);
		System.out.println(newDatePreviusMonth);
		Date actualDate = new Date(newDatePreviusMonth.getTime());
		
		Result<Record> activeContractsRecords = dslContext.select().from(CONTRACT)
					.where(CONTRACT.WORKPLACE.eq(workplaceId))
					.and(CONTRACT.END_DATE.isNull()
							.or(CONTRACT.END_DATE.ge(actualDate)))
//					.and(CONTRACT.SS_REGIME.ne((byte)3))
					.orderBy(CONTRACT.ID.desc())
					.fetch();
		
		ArrayList<Integer> existingPerson = new ArrayList<>(); 
		
		for(Record c : activeContractsRecords) {
			if(existingPerson.isEmpty() || !existingPerson.contains(c.get(CONTRACT.PERSON))) {
				existingPerson.add(c.get(CONTRACT.PERSON));
				
				Record p = dslContext.select().from(PERSON)
						.where(PERSON.REGISTRY.eq(c.get(CONTRACT.PERSON)))
						.fetchOne();
				
				EmployeeInfo employee = new EmployeeInfo();
				
				Result<Record> contractRecords = dslContext.select().from(CONTRACT)
					.where(CONTRACT.PERSON.eq(p.get(PERSON.REGISTRY)))
					.orderBy(CONTRACT.START_DATE.desc())
					.fetch();
				
				Integer contractId = null;
				
				if(null != contractRecords && !contractRecords.isEmpty())
					contractId = contractRecords.get(0).get(CONTRACT.ID);
				
				if(null != contractId) {
					// ----------------------------------- FULL TIME JOURNEY
					
					String journeyTypeEmployee = "";
					
					Result<Record> journeyTypeRecords = dslContext.select()
							  .from(CONTRACT_DATA)
							  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
							  .and(CONTRACT_DATA.NAME.equal("TIEMPO_COMPLETO"))
							  .orderBy(CONTRACT_DATA.START_DATE.desc()) // If there is more than one contract
							  .fetch();
					
					if(!journeyTypeRecords.isEmpty())
						journeyTypeEmployee = journeyTypeRecords.get(0).get(CONTRACT_DATA.EXPRESSION);
					else {
						journeyTypeRecords = dslContext.select()
								  .from(CONTRACT_DATA)
								  .where(CONTRACT_DATA.CONTRACT.eq(contractId))
								  .and(CONTRACT_DATA.NAME.like(ContextVariable.TC2.getName()))
								  .orderBy(CONTRACT_DATA.START_DATE.desc())
								  .fetch();
						
						if(journeyTypeRecords.isEmpty())
							continue;
						else
							journeyTypeEmployee = journeyTypeRecords.get(0).get(CONTRACT_DATA.EXPRESSION);
					}
					
					employee.setIsFullTime(isFullTimeJourney(journeyTypeEmployee));
				}
				
				Record registryRecord = dslContext.select()
						.from(REGISTRY)
						.where(REGISTRY.ID.eq(p.get(PERSON.REGISTRY)))
						.fetchOne();
				
				//PERSON TABLE
				Integer id = p.get(PERSON.REGISTRY);
				String name = p.get(PERSON.NAME);
				String surName = p.get(PERSON.FIRST_SURNAME);
				String secondSurName = p.get(PERSON.SECOND_SURNAME);
				Date bithDate = p.get(PERSON.BIRTH_DATE);
				Byte gender = p.get(PERSON.GENDER);
				String ssNumber = p.get(PERSON.SOCIAL_SECURITY_NUM);
				
				//REGISTRY TABLE
				String document = registryRecord.get(REGISTRY.DOCUMENT);
				Byte documentType = registryRecord.get(REGISTRY.DOCUMENT_TYPE);
				String nationality = registryRecord.get(REGISTRY.NATIONALITY);
				
				Result<Record> raddressRecords = dslContext.select()
						.from(RADDRESS)
						.where(RADDRESS.REGISTRY.eq(p.get(PERSON.REGISTRY)))
						.fetch();
				
				//RADDRESS TABLE
				Integer raddressId = null;
				String streetType = null;
				String address = null;
				String addressNum = null;
				String addressZip = null;
				String addressCity = null;
				Integer geozoneId = null;
				String addressProvince = null;
				
				if(null != raddressRecords && !raddressRecords.isEmpty()) {
					if(null != raddressRecords.get(0).get(RADDRESS.GEOZONE)) {
						Record geozoneRecord = dslContext.select()
								.from(GEOZONE)
								.where(GEOZONE.ID.eq(raddressRecords.get(0).get(RADDRESS.GEOZONE)))
								.fetchOne();
						
						geozoneId = geozoneRecord.get(GEOZONE.ID);
						addressProvince = geozoneRecord.get(GEOZONE.NAME);
					}
					
					raddressId = raddressRecords.get(0).get(RADDRESS.ID);
					streetType = raddressRecords.get(0).get(RADDRESS.STREET_TYPE);
					address = raddressRecords.get(0).get(RADDRESS.ADDRESS);
					addressNum = raddressRecords.get(0).get(RADDRESS.NUMBER);
					addressZip = raddressRecords.get(0).get(RADDRESS.ZIP);
					addressCity = raddressRecords.get(0).get(RADDRESS.CITY);
				}
				
				//RMEDIA TABLE
				Result<Record> rmediaRecords = dslContext.select()
						.from(RMEDIA)
						.where(RMEDIA.REGISTRY.eq(p.get(PERSON.REGISTRY)))
						.fetch();
				
				Integer mobileId = null;
				String mobile = null;
				Integer phoneId = null;
				String phone = null;
				Integer emailId = null;
				String email = null;
				
				for(Record record : rmediaRecords) {
					if(1 == record.get(RMEDIA.MEDIA)) {
						phoneId = record.get(RMEDIA.ID);
						phone = record.get(RMEDIA.VALUE);
					}else if(2 == record.get(RMEDIA.MEDIA)) {
						mobileId = record.get(RMEDIA.ID);
						mobile = record.get(RMEDIA.VALUE);
					}else if(4 == record.get(RMEDIA.MEDIA)) {
						emailId = record.get(RMEDIA.ID);
						email = record.get(RMEDIA.VALUE);
					}
				}
				
				//PAY METHOD
				Result<Record> rpaymethodRecords = dslContext.select()
						.from(RPAYMETHOD)
						.where(RPAYMETHOD.REGISTRY.eq(p.get(PERSON.REGISTRY)))
						.fetch();
				
				Integer rpaymethodId = null;
				Integer paymethodId = null;
				String payMethod = null;
				Integer rbankId = null;
				String account = null;
				String bic = null;
				
				if(null != rpaymethodRecords && !rpaymethodRecords.isEmpty()) {
					rpaymethodId = rpaymethodRecords.get(0).get(RPAYMETHOD.ID);
					Record paymethodRecord = dslContext.select()
							.from(PAY_METHOD)
							.where(PAY_METHOD.ID.eq(rpaymethodRecords.get(0).get(RPAYMETHOD.PAY_METHOD)))
							.fetchOne();
					
					if(null != paymethodRecord) {
						paymethodId = paymethodRecord.get(PAY_METHOD.ID);
						payMethod = paymethodRecord.get(PAY_METHOD.NAME);
					}
					
					if(null != rpaymethodRecords.get(0).get(RPAYMETHOD.RBANK)) {
						Record rbankRecord = dslContext.select()
								.from(RBANK)
								.where(RBANK.ID.eq(rpaymethodRecords.get(0).get(RPAYMETHOD.RBANK)))
								.fetchOne();
						
						rbankId = rbankRecord.get(RBANK.ID);
						account = rbankRecord.get(RBANK.BANK_ACCOUNT);
						bic = rbankRecord.get(RBANK.BIC);
					}
				}
				
				//SET EMPLOYEE INFO
				employee.setContractId(contractId);
				
				employee.setEmployeeId(id);
				employee.setName(name);
				employee.setSurName(surName);
				employee.setSecondSurName(secondSurName);
				employee.setBirthdate(bithDate);
				employee.setGender(gender);
				employee.setSsNumber(ssNumber);
				
				employee.setDocument(document);
				employee.setDocumentType(documentType);
				employee.setNationality(nationality);
				
				employee.setRaddressId(raddressId);
				employee.setStreetType(streetType);
				employee.setAddress(address);
				employee.setAddresNum(addressNum);
				employee.setAddressZip(addressZip);
				employee.setAddressCity(addressCity);
				employee.setGeozoneId(geozoneId);
				employee.setAddressProvinces(addressProvince);
				
				employee.setMobileId(mobileId);
				employee.setMobile(mobile);
				employee.setPhoneId(phoneId);
				employee.setPhone(phone);
				employee.setEmailId(emailId);
				employee.setEmail(email);
				
				employee.setRpaymethodId(rpaymethodId);
				employee.setPaymethodId(paymethodId);
				employee.setPayMethodType(payMethod);
				employee.setRbankId(rbankId);
				employee.setAccount(account);
				employee.setBic(bic);
				
				workplaceEmployees.addEmployee(employee);
			}
		}
		
		return workplaceEmployees;
	}
	
	private static WorkplaceEmployees getWorkplaceEmployeesInformation(DSLContext dslContext, Workplace workplace, Integer domainId) {
		WorkplaceEmployees workplaceEmployees = new WorkplaceEmployees();
		
		List<Integer> enterprisesId = null;
		
		if(null != workplace) {
			enterprisesId = dslContext.select(WORKPLACE.ENTERPRISE)
					.from(WORKPLACE)
					.where(WORKPLACE.ID.eq(workplace.getId()))
					.fetch(WORKPLACE.ENTERPRISE);
		} else {
			enterprisesId = dslContext.select(WORKPLACE.ENTERPRISE)
					.from(WORKPLACE)
					.where(WORKPLACE.DOMAIN.eq(domainId))
					.fetch(WORKPLACE.ENTERPRISE);
		}
		
		Result<Record> personRecords = dslContext.select()
				.from(PERSON)
				.innerJoin(CONTRACT)
				.on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
				.innerJoin(WORKPLACE)
				.on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
				.where(WORKPLACE.ENTERPRISE.in(enterprisesId))
				.and(CONTRACT.ID.gt(0))
				.and(WORKPLACE.ACTIVE.eq((byte)1))
				.fetch();
		
		for(Record p : personRecords){
			EmployeeInfo employee = new EmployeeInfo();
			
			Result<Record> contractRecords = dslContext.select().from(CONTRACT)
				.where(CONTRACT.PERSON.eq(p.get(PERSON.REGISTRY)))
				.orderBy(CONTRACT.START_DATE.desc())
				.fetch();
			
			Integer contractId = null;
			Boolean isActive = false;
			
			if(null != contractRecords && !contractRecords.isEmpty()){
				contractId = contractRecords.get(0).get(CONTRACT.ID);
				Date endDate = contractRecords.get(0).get(CONTRACT.END_DATE);
				Date actualDate = new Date(new java.util.Date().getTime());
				if(null == endDate || endDate.after(actualDate))
					isActive = true;
			}
			
			Record registryRecord = dslContext.select()
					.from(REGISTRY)
					.where(REGISTRY.ID.eq(p.get(PERSON.REGISTRY)))
					.fetchOne();
			
			//PERSON TABLE
			Integer id = p.get(PERSON.REGISTRY);
			String name = p.get(PERSON.NAME);
			String surName = p.get(PERSON.FIRST_SURNAME);
			String secondSurName = p.get(PERSON.SECOND_SURNAME);
			Date bithDate = p.get(PERSON.BIRTH_DATE);
			Byte gender = p.get(PERSON.GENDER);
			String ssNumber = p.get(PERSON.SOCIAL_SECURITY_NUM);
			
			//REGISTRY TABLE
			String document = registryRecord.get(REGISTRY.DOCUMENT);
			Byte documentType = registryRecord.get(REGISTRY.DOCUMENT_TYPE);
			String nationality = registryRecord.get(REGISTRY.NATIONALITY);
			
			Result<Record> raddressRecords = dslContext.select()
					.from(RADDRESS)
					.where(RADDRESS.REGISTRY.eq(p.get(PERSON.REGISTRY)))
					.fetch();
			
			//RADDRESS TABLE
			Integer raddressId = null;
			String streetType = null;
			String address = null;
			String addressNum = null;
			String addressZip = null;
			String addressCity = null;
			Integer geozoneId = null;
			String addressProvince = null;
			
			if(null != raddressRecords && !raddressRecords.isEmpty()) {
				if(null != raddressRecords.get(0).get(RADDRESS.GEOZONE)) {
					Record geozoneRecord = dslContext.select()
							.from(GEOZONE)
							.where(GEOZONE.ID.eq(raddressRecords.get(0).get(RADDRESS.GEOZONE)))
							.fetchOne();
					
					geozoneId = geozoneRecord.get(GEOZONE.ID);
					addressProvince = geozoneRecord.get(GEOZONE.NAME);
				}
				
				raddressId = raddressRecords.get(0).get(RADDRESS.ID);
				streetType = raddressRecords.get(0).get(RADDRESS.STREET_TYPE);
				address = raddressRecords.get(0).get(RADDRESS.ADDRESS);
				addressNum = raddressRecords.get(0).get(RADDRESS.NUMBER);
				addressZip = raddressRecords.get(0).get(RADDRESS.ZIP);
				addressCity = raddressRecords.get(0).get(RADDRESS.CITY);
			}
			
			//RMEDIA TABLE
			Result<Record> rmediaRecords = dslContext.select()
					.from(RMEDIA)
					.where(RMEDIA.REGISTRY.eq(p.get(PERSON.REGISTRY)))
					.fetch();
			
			Integer mobileId = null;
			String mobile = null;
			Integer phoneId = null;
			String phone = null;
			Integer emailId = null;
			String email = null;
			
			for(Record record : rmediaRecords) {
				if(1 == record.get(RMEDIA.MEDIA)) {
					phoneId = record.get(RMEDIA.ID);
					phone = record.get(RMEDIA.VALUE);
				}else if(2 == record.get(RMEDIA.MEDIA)) {
					mobileId = record.get(RMEDIA.ID);
					mobile = record.get(RMEDIA.VALUE);
				}else if(4 == record.get(RMEDIA.MEDIA)) {
					emailId = record.get(RMEDIA.ID);
					email = record.get(RMEDIA.VALUE);
				}
			}
			
			//PAY METHOD
			Result<Record> rpaymethodRecords = dslContext.select()
					.from(RPAYMETHOD)
					.where(RPAYMETHOD.REGISTRY.eq(p.get(PERSON.REGISTRY)))
					.fetch();
			
			Integer rpaymethodId = null;
			Integer paymethodId = null;
			String payMethod = null;
			Integer rbankId = null;
			String account = null;
			String bic = null;
			
			if(null != rpaymethodRecords && !rpaymethodRecords.isEmpty()) {
				rpaymethodId = rpaymethodRecords.get(0).get(RPAYMETHOD.ID);
				Record paymethodRecord = dslContext.select()
						.from(PAY_METHOD)
						.where(PAY_METHOD.ID.eq(rpaymethodRecords.get(0).get(RPAYMETHOD.PAY_METHOD)))
						.fetchOne();
				
				paymethodId = paymethodRecord.get(PAY_METHOD.ID);
				payMethod = paymethodRecord.get(PAY_METHOD.NAME);
				
				if(null != rpaymethodRecords.get(0).get(RPAYMETHOD.RBANK)) {
					Record rbankRecord = dslContext.select()
							.from(RBANK)
							.where(RBANK.ID.eq(rpaymethodRecords.get(0).get(RPAYMETHOD.RBANK)))
							.fetchOne();
					
					rbankId = rbankRecord.get(RBANK.ID);
					account = rbankRecord.get(RBANK.BANK_ACCOUNT);
					bic = rbankRecord.get(RBANK.BIC);
				}
			}
			
			//SET EMPLOYEE INFO
			employee.setContractId(contractId);
			employee.setContractActive(isActive);
			
			employee.setEmployeeId(id);
			employee.setName(name);
			employee.setSurName(surName);
			employee.setSecondSurName(secondSurName);
			employee.setBirthdate(bithDate);
			employee.setGender(gender);
			employee.setSsNumber(ssNumber);
			
			employee.setDocument(document);
			employee.setDocumentType(documentType);
			employee.setNationality(nationality);
			
			employee.setRaddressId(raddressId);
			employee.setStreetType(streetType);
			employee.setAddress(address);
			employee.setAddresNum(addressNum);
			employee.setAddressZip(addressZip);
			employee.setAddressCity(addressCity);
			employee.setGeozoneId(geozoneId);
			employee.setAddressProvinces(addressProvince);
			
			employee.setMobileId(mobileId);
			employee.setMobile(mobile);
			employee.setPhoneId(phoneId);
			employee.setPhone(phone);
			employee.setEmailId(emailId);
			employee.setEmail(email);
			
			employee.setRpaymethodId(rpaymethodId);
			employee.setPaymethodId(paymethodId);
			employee.setPayMethodType(payMethod);
			employee.setRbankId(rbankId);
			employee.setAccount(account);
			employee.setBic(bic);
			
			workplaceEmployees.addEmployee(employee);
		}
		
		return workplaceEmployees;
	}
	
	
	private static EventsWorkplace setWorkplaceEmployeesInformation(DSLContext dslContext,
			EventsWorkplace updateEventsWorkplace) {
		
		ArrayList<String> varNotToUpdate = new ArrayList<>();
		varNotToUpdate.add("DIAS_ERE");
		varNotToUpdate.add("DIAS_HUELGA");
		varNotToUpdate.add("DIAS_VACACIONES");
		
		for(Quintet<Integer, String, java.util.Date, java.util.Date, String> varQuintet : updateEventsWorkplace.getUpdateEventsWorkplace()){
			Integer employeeId = varQuintet.getContractId();
			deleteEmployeeEventsVariables(dslContext, employeeId);
		}
		
		for(Quintet<Integer, String, java.util.Date, java.util.Date, String> varQuintet : updateEventsWorkplace.getUpdateEventsWorkplace()){
			Integer employeeId = varQuintet.getContractId();
			
			addEmployeeEventsVariable(
					dslContext, 
					employeeId, 
					varQuintet.getVarName(), 
					varQuintet.getStart_date(),
					varQuintet.getEnd_date(),
					varQuintet.getExpression(),
					varNotToUpdate);
		}
		
		return updateEventsWorkplace;
	}

	private static void deleteEmployeeEventsVariables(DSLContext dslContext, Integer employeeContractId) {
		Result<Record> contractRecord = dslContext.select()
				.from(CONTRACT)
				.where(CONTRACT.PERSON.eq(employeeContractId))
				.fetch();
		 
		Integer contract = contractRecord.get(0).get(CONTRACT.ID);
		
		dslContext.delete(CONTRACT_DATA)
		   .where(CONTRACT_DATA.CONTRACT.eq(contract))
		   .and(CONTRACT_DATA.NAME.in(
//				   ContextVariable.WORKED_DAYS.getName()
//				  ,ContextVariable.LEAVE_DAYS.getName()
//				  ,ContextVariable.WORKED_HOURS.getName()
//				  ,ContextVariable.REAL_DAYS.getName()
				  ContextVariable.EXTRA_HOURS.getName())
//				.or(CONTRACT_DATA.NAME.eq("DIAS_EFECTIVOS"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_COMPLEMENTARIAS"))
//				.or(CONTRACT_DATA.NAME.eq("DIAS_PECNORTA"))
//				.or(CONTRACT_DATA.NAME.eq("DIAS_MANUTENCION"))
//				.or(CONTRACT_DATA.NAME.eq("DIAS_PECNORTA_EXTRANJERO"))
//				.or(CONTRACT_DATA.NAME.eq("DIAS_MANUTENCION_EXTRANJERO"))
//				.or(CONTRACT_DATA.NAME.eq("KMS"))
//				.or(CONTRACT_DATA.NAME.eq("DIAS_VACACIONES"))
//				.or(CONTRACT_DATA.NAME.eq("IMPORTE_HORA_EXTRA"))
//				.or(CONTRACT_DATA.NAME.eq("VENTAS"))
//				.or(CONTRACT_DATA.NAME.eq("HORAS_EXTRAS_FZA"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_EXTRAS"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_FORMACION_PRESENCIAL"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_FORMACION_DISTANCIA"))
				.or(CONTRACT_DATA.NAME.eq("HORAS_TUTORIA"))
				.or(CONTRACT_DATA.NAME.eq("BONIFICACION_TUTORIA"))
				)
		   .execute();
		
	}
	
	private static void addEmployeeEventsVariable(DSLContext dslContext, Integer employeeContractId, String varName,
			java.util.Date start_date, java.util.Date end_date, String expression,
			ArrayList<String> varNotToUpdate) {
		
		Result<Record> contractRecord = dslContext.select()
				.from(CONTRACT)
				.where(CONTRACT.PERSON.eq(employeeContractId))
				.fetch();
		 
		Integer domain = contractRecord.get(0).get(CONTRACT.DOMAIN);
		Integer contract = contractRecord.get(0).get(CONTRACT.ID);
		
		if(!varNotToUpdate.contains(varName))
			if(null != expression){
				
				Date sqlStartDate = new Date(start_date.getTime());
				Date sqlEndDate = new Date(end_date.getTime());
				
				dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT,
						CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
						.values(domain, varName, contract, expression, 
								sqlStartDate, sqlEndDate)
						.execute();
			}
	}

	// Get employee calendar info from database
	private static Boolean isFullTimeJourney(String journeyType) {
		return ('1' == journeyType.charAt(1) || '4' == journeyType.charAt(1)|| "true" == journeyType) ? true : false;
	}

	private static ArrayList<EventEmployee> setEventsDraftDB(DSLContext dslContext, ArrayList<EventEmployee> eventEmployees) {
		for(EventEmployee eventEmployee : eventEmployees) {
			Integer contractId = eventEmployee.getContractId();
			
			Integer domain = dslContext.select(CONTRACT.DOMAIN)
					.from(CONTRACT)
					.where(CONTRACT.ID.eq(contractId))
					.fetchOne().value1();
			
			ArrayList<String> varsToUpdate = eventEmployee.getEmployeeEventsData().getVarsToUpdate();
			
			dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.in(varsToUpdate)).execute();
			
			for(Entry<String, ArrayList<Quartet<java.util.Date, java.util.Date, String, String>>> entry : eventEmployee.getEmployeeEventsData().getContractEventsList().entrySet()){
				if(varsToUpdate.contains(entry.getKey())) {
					for(Quartet<java.util.Date, java.util.Date, String, String> quartet : entry.getValue()) {
						if(null != quartet.getExpression())
							dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT,
									CONTRACT_DATA.EXPRESSION, CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
									.values(domain, quartet.getName(), contractId, quartet.getExpression(), 
											parseToSQLDate(quartet.getStartDate()), parseToSQLDate(quartet.getEndDate()))
									.execute();
					}
				}
			}
		}
		
		return null;
	}
	
	private static Date parseToSQLDate(java.util.Date date) {
		if(null == date)
			return null;
		
		DateUtils.resetTime(date);
		return new Date(date.getTime());
	}

	
}
