package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.ibm.icu.util.Calendar;

public class JooqContrataContract {

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static List<EmployeeContractInfo> getEmployeesInfo(Connection conn, Integer domainId, Boolean allEmployees) {
		return getEmployeesInfoDB(DSL.using(conn, getDefaultSettings()), domainId, allEmployees);
	}

	private static List<EmployeeContractInfo> getEmployeesInfoDB(DSLContext dslContext, Integer domainId, Boolean allEmployees) {
		List<EmployeeContractInfo> employeesInfo = new ArrayList<EmployeeContractInfo>();
		
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
			cal.add(Calendar.MONTH, -1);
			
			Date contract_endDate = new Date(cal.getTimeInMillis());
			
			allContractIds = dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.DOMAIN.eq(domainId))
					.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(contract_endDate)))
					.fetch(CONTRACT.ID);
		}
		
		for(Integer contractId : allContractIds) {
			
			// --------------------------------------------- Init
			
			EmployeeContractInfo employeeContractInfo = new EmployeeContractInfo();
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
			employeeData.setDomain(personTable.get(PERSON.DOMAIN));
			employeeData.setBirthdate(personTable.get(PERSON.BIRTH_DATE));
			employeeData.setGender(personTable.get(PERSON.GENDER));
			employeeData.setCivilStatus(personTable.get(PERSON.MARITAL_STATUS));
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
			employeeData.setDocumentType(registryTable.get(REGISTRY.DOCUMENT_TYPE)); //Esto lo saco con el formato del documento, se podria obviar?
			employeeData.setNationality(registryTable.get(REGISTRY.NATIONALITY));
			
			// RADDRESS AND GEOZONE TABLE
			Record raddressTable = dslContext.select().from(RADDRESS)
					.where(RADDRESS.REGISTRY.eq(employee_registry))
					.fetchOne();
			
			if(null != raddressTable) {
			
				employeeData.setRaddressId(raddressTable.get(RADDRESS.ID));
				employeeData.setStreetType(raddressTable.get(RADDRESS.STREET_TYPE));
				employeeData.setAddress(raddressTable.get(RADDRESS.ADDRESS));
				employeeData.setAddressInfo(raddressTable.get(RADDRESS.ADDRESS2));
				employeeData.setAddresNum(raddressTable.get(RADDRESS.NUMBER));
				employeeData.setAddressZip(raddressTable.get(RADDRESS.ZIP));
				employeeData.setAddressCity(raddressTable.get(RADDRESS.MUNICIPALITY_CODE));
				
				Integer raddress_geozone = raddressTable.get(RADDRESS.GEOZONE);
				if(null == raddress_geozone) {
					employeeData.setGeozoneId(null);
					employeeData.setAddressProvinces(null);
				}else {
					Record geozoneTable = dslContext.select().from(GEOZONE)
							.where(GEOZONE.ID.eq(raddress_geozone))
							.fetchOne();
					
					employeeData.setGeozoneId(geozoneTable.get(GEOZONE.ID));
					employeeData.setAddressProvinces(geozoneTable.get(GEOZONE.CODE));
				}
			}
			
			// RMEDIA TABLE
			Result<Record> rmediaTable = dslContext.select().from(RMEDIA)
					.where(RMEDIA.REGISTRY.eq(employee_registry))
					.fetch();
			
			for(Record record : rmediaTable){
				if(record.get(RMEDIA.MEDIA) == 1){
					employeeData.setPhoneId(record.get(RMEDIA.ID));
					employeeData.setPhone(record.get(RMEDIA.VALUE));
				}else if(record.get(RMEDIA.MEDIA) == 2){
					employeeData.setMobileId(record.get(RMEDIA.ID));
					employeeData.setMobile(record.get(RMEDIA.VALUE));
				}else if(record.get(RMEDIA.MEDIA) == 4){
					employeeData.setEmailId(record.get(RMEDIA.ID));
					employeeData.setEmail(record.get(RMEDIA.VALUE));
				}
			}
			
			// FIND RPAYMETHOD
			Record rPayMethodRecord = dslContext.select().from(RPAYMETHOD)
				.where(RPAYMETHOD.REGISTRY.eq(employee_registry))
				.fetchOne();
			
			if(null == rPayMethodRecord) {
				employeeData.setRpaymethodId(null);
				
				employeeData.setPaymethodId(null);
				employeeData.setPayMethodType(null);
				
				employeeData.setRbankId(null);
				employeeData.setAccount(null);
				employeeData.setBic(null);
			}else {
				employeeData.setRpaymethodId(rPayMethodRecord.get(RPAYMETHOD.ID));
				
				Record payMethodTable = dslContext.select().from(PAY_METHOD)
						.where(PAY_METHOD.ID.eq(rPayMethodRecord.get(RPAYMETHOD.PAY_METHOD)))
						.fetchOne();
				if(null != payMethodTable) {
					employeeData.setPaymethodId(payMethodTable.get(PAY_METHOD.ID));
					employeeData.setPayMethodType(payMethodTable.get(PAY_METHOD.NAME));
					employeeData.setPayMethodTypeB(payMethodTable.get(PAY_METHOD.TYPE));
				}
				
				Integer rbank = rPayMethodRecord.get(RPAYMETHOD.RBANK);
				
				employeeData.setRbankId(null);
				employeeData.setAccount(null);
				employeeData.setBic(null);
				
				if(null != rbank) {
					Record rBankTable = dslContext.select().from(RBANK)
							.where(RBANK.ID.eq(rbank))
							.fetchOne();
					
					if(null != rBankTable) {
						employeeData.setRbankId(rBankTable.get(RBANK.ID));
						employeeData.setAccount(rBankTable.get(RBANK.BANK_ACCOUNT));
						employeeData.setBic(rBankTable.get(RBANK.BIC));
					}
				}
				
			}
			
			// GET RBANKS
			Result<Record> rbankRecords = dslContext.select().from(RBANK)
					.where(RBANK.REGISTRY.eq(personTable.get(PERSON.REGISTRY)))
					.fetch();
			
			for(Record r: rbankRecords) {
				employeeData.addRbank(r.get(RBANK.ID), r.get(RBANK.BANK_ACCOUNT), r.get(RBANK.BIC));
			}
			
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
			contractData.setAgreementCategory(contractTable.get(CONTRACT.CATEGORY_DESCRIPTION));
			contractData.setSsRegimen(contractTable.get(CONTRACT.SS_REGIME));
			
			contractData.setOldStartDate(contractTable.get(CONTRACT.START_DATE));
			contractData.setOldEndDate(contractTable.get(CONTRACT.END_DATE));
			
			Integer employe_workplace_table_id = contractTable.get(CONTRACT.WORKPLACE);
			
			// WORKPLACE TABLE		
			contractData.setWorkplaceId(employe_workplace_table_id);
			
			if(contractData.getSsRegimen() != 3){ // NO ES RETA
				// ENTERPRISE ACTIVITY TABLE
				Integer enterpriseActivity = contractTable.get(CONTRACT.ENTERPRISE_ACTIVITY);
				
				if(null == enterpriseActivity) {
					contractData.setActivityId(null);
				}else {
					Record enterpriseActivityTable = dslContext.select().from(ENTERPRISE_ACTIVITY)
							.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseActivity))
							.fetchOne();
					
					contractData.setActivityId(enterpriseActivityTable.get(ENTERPRISE_ACTIVITY.ID));
				}
				
				//ENTERPRISE CCC TABLE
				Integer enterpriseCCC = contractTable.get(CONTRACT.ENTERPRISE_CCC);
				
				if(null == enterpriseCCC) {
					contractData.setCccId(null);
					contractData.setCccType(null);
					
				}else {
					Record enterpriseCCCTable = dslContext.select().from(ENTERPRISE_CCC)
							.where(ENTERPRISE_CCC.ID.eq(enterpriseCCC))
							.fetchOne();
					
					contractData.setCccId(enterpriseCCCTable.get(ENTERPRISE_CCC.ID));
					contractData.setCccType(enterpriseCCCTable.get(ENTERPRISE_CCC.TYPE));
				}
			}
			
			// AGREEMENT LEVEL TABLE
			Integer agreementLevel = contractTable.get(CONTRACT.AGREEMENT_LEVEL);
			
			if(null == agreementLevel) {
				contractData.setAgreementLevelId(null);
				contractData.setAgreementId(null);
			}else {
				Record agreementLevelTable = dslContext.select().from(AGREEMENT_LEVEL)
						.where(AGREEMENT_LEVEL.ID.eq(agreementLevel))
						.fetchOne();
				try {
					contractData.setAgreementLevelId(agreementLevelTable.get(AGREEMENT_LEVEL.ID));
					
					//AGREEMENT TABLE
					Integer agreement = agreementLevelTable.get(AGREEMENT_LEVEL.AGREEMENT);
					
					Record agreementTable = dslContext.select().from(AGREEMENT)
							.where(AGREEMENT.ID.eq(agreement))
							.fetchOne();
					
					contractData.setAgreementId(agreementTable.get(AGREEMENT.ID));
				} catch ( Throwable t ) {
					contractData.setAgreementLevelId(null);
					contractData.setAgreementId(null);
				}
				
			}
			
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
					.orderBy(CONTRACT_DATA.START_DATE)
					.fetch();
				}else {
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
							.where(CONTRACT_DATA.CONTRACT.eq(contractId))
							.and(CONTRACT_DATA.START_DATE.le(currentDate))
							.and(CONTRACT_DATA.END_DATE.ge(currentDate).or(CONTRACT_DATA.END_DATE.isNull()))
							.fetch();
					
					if(contractDataTable.isEmpty())
						contractDataTable = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(contractId))
						.orderBy(CONTRACT_DATA.ID)
						.fetch();
				}
				
			} else {
				
				if(contractData.getStartDate().after(currentDate)) {
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
							.where(CONTRACT_DATA.CONTRACT.eq(contractId))
							.and(CONTRACT_DATA.START_DATE.le(new Date(contractData.getStartDate().getTime())))
							.and(CONTRACT_DATA.END_DATE.ge(new Date(contractData.getStartDate().getTime())).or(CONTRACT_DATA.END_DATE.isNull()))
							.fetch();
				}else
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
							.where(CONTRACT_DATA.CONTRACT.eq(contractId))
							.orderBy(CONTRACT_DATA.START_DATE.asc())
							.fetch();
				
				if(contractDataTable.isEmpty())
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.orderBy(CONTRACT_DATA.ID)
					.fetch();
			}
				
			Map<String, String> contractDataMap = new HashMap<>();
			
			for(Record r : contractDataTable){
				contractDataMap.put(r.get(CONTRACT_DATA.NAME), r.get(CONTRACT_DATA.EXPRESSION));
				
				if(r.get(CONTRACT_DATA.NAME).equals("TC2")) {
					contractData.setContracttypeId(r.get(CONTRACT_DATA.ID));
					contractData.setContractType(r.get(CONTRACT_DATA.EXPRESSION));
				}else if(r.get(CONTRACT_DATA.NAME).equals("GRUPO_COTIZACION")) {
					contractData.setQuotegroupId(r.get(CONTRACT_DATA.ID));
					contractData.setQuoteGroup(r.get(CONTRACT_DATA.EXPRESSION));
				}else if(r.get(CONTRACT_DATA.NAME).equals("OCUPACION")) {
					contractData.setOcupationId(r.get(CONTRACT_DATA.ID));
					contractData.setOcupation(r.get(CONTRACT_DATA.EXPRESSION));
				}else if(r.get(CONTRACT_DATA.NAME).equals("TIEMPO_COMPLETO")) {
					contractData.setJourneytypeId(r.get(CONTRACT_DATA.ID));
					contractData.setJourneyType(r.get(CONTRACT_DATA.EXPRESSION).equalsIgnoreCase("TRUE") ? (byte) 0 : (byte) 1);
				}
			}
			
			// CONTRACT INFO TABLE
			Result<Record> contractInfoTableRecords = dslContext.select().from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("OPCION_CONTRATO"))
				.fetch();
			
			Record contractInfoTable = null;
			
			if(contractInfoTableRecords.isEmpty()) {
				contractData.setContractmodelId(null);
				contractData.setContractModel(null);
			}else {
				contractInfoTable = contractInfoTableRecords.get(0);
				
				if(null == contractInfoTable.get(CONTRACT_INFO.EXPRESSION)){
					contractData.setContractmodelId(contractInfoTable.get(CONTRACT_INFO.ID));
					contractData.setContractModel(null);
				}else {
					String contractType = contractInfoTable.get(CONTRACT_INFO.EXPRESSION);
					
					if(contractType.contains("\""))
						contractType = contractType.split("\"")[1];
					
					Integer ordinal = ModelOption.valueOf(contractType).ordinal();
					
					contractData.setContractmodelId(contractInfoTable.get(CONTRACT_INFO.ID));
					contractData.setContractModel(ordinal);	
				}
			}
			
			contractInfoTable = dslContext.select().from(CONTRACT_INFO)
					.where(CONTRACT_INFO.CONTRACT.eq(contractId))
					.and(CONTRACT_INFO.NAME.eq("RETA"))
					.fetchOne();
			
			if(null == contractInfoTable)
				contractData.setRetaId(null);
			else
				contractData.setRetaId(contractInfoTable.get(CONTRACT_INFO.ID));
			
			Result<Record> journiesDB = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.NAME.like("HORAS%"))
					.and(CONTRACT_DATA.CONTRACT.eq(contractId))
					.orderBy(CONTRACT_DATA.START_DATE)
					.fetch();
			
			Map<java.util.Date, ArrayList<JourneyDuration>> journies = new HashMap<>();
			
			if(null != journiesDB && !journiesDB.isEmpty()) {
				Date iterableDate = journiesDB.get(0).get(CONTRACT_DATA.START_DATE);
				ArrayList<JourneyDuration> journeyList = new ArrayList<>();
				for(Record r : journiesDB) {
					if(r.get(CONTRACT_DATA.START_DATE).equals(iterableDate)) {
						JourneyDuration journey = new JourneyDuration();
						journey.setStartDate(r.get(CONTRACT_DATA.START_DATE));
						journey.setEndDate(r.get(CONTRACT_DATA.END_DATE));
						journey.setName(r.get(CONTRACT_DATA.NAME));
						journey.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
						
						journeyList.add(journey);
					}else {
						journies.put(iterableDate, journeyList);
						journeyList = new ArrayList<>();
						
						iterableDate = r.get(CONTRACT_DATA.START_DATE);
						
						JourneyDuration journey = new JourneyDuration();
						journey.setStartDate(r.get(CONTRACT_DATA.START_DATE));
						journey.setEndDate(r.get(CONTRACT_DATA.END_DATE));
						journey.setName(r.get(CONTRACT_DATA.NAME));
						journey.setExpression(r.get(CONTRACT_DATA.EXPRESSION));
						
						journeyList.add(journey);
					}
				}
				journies.put(iterableDate, orderByWeekDay(journeyList));
			}
			
			contractData.setContractJourneyDuration(journies);
				
//			System.out.println(employeeData.toString());
//			System.out.println(contractData.toString());
			
			employeeContractInfo.setEmployeeInfo(employeeData);
			employeeContractInfo.setContractInfo(contractData);
			
			// ------------------------------------------------------------------------------------------------------------------------
			// ---------------------------------------------- CONTRACT OTHER INFO -----------------------------------------------------
			// ------------------------------------------------------------------------------------------------------------------------

			List<String> contractOtherDataNames = getContractOtherDataListNames(contractData.getContractType());
			
			// Variables globales
			
			Result<Record> contractOtherDataRecords = dslContext.select().from(CONTRACT_INFO)
					.where(CONTRACT_INFO.NAME.in(contractOtherDataNames))
					.and(CONTRACT_INFO.CONTRACT.isNull())
					.orderBy(CONTRACT_INFO.START_DATE.desc())
					.fetch();
			
			for(Record contractOtherDataRecord : contractOtherDataRecords) {
				String name = contractOtherDataRecord.get(CONTRACT_INFO.NAME);
				String value = contractOtherDataRecord.get(CONTRACT_INFO.EXPRESSION);
				employeeContractInfo.addContractOtherData(name, value);
			}
			
			// Variables contrato
			
			contractOtherDataRecords = dslContext.select().from(CONTRACT_INFO)
					.where(CONTRACT_INFO.NAME.in(contractOtherDataNames))
					.and(CONTRACT_INFO.CONTRACT.eq(contractData.getContractId()))
					.orderBy(CONTRACT_INFO.START_DATE.desc())
					.fetch();
			
			for(Record contractOtherDataRecord : contractOtherDataRecords) {
				String name = contractOtherDataRecord.get(CONTRACT_INFO.NAME);
				String value = contractOtherDataRecord.get(CONTRACT_INFO.EXPRESSION);
				employeeContractInfo.addContractOtherData(name, value);
			}
			
			employeesInfo.add(employeeContractInfo);
			
		}
		
		return employeesInfo;
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

	// ---------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------    AFI CHANGES   ----------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------------	
	
	private static String setEmployeeAFIChangesDB(DSLContext dslContext, Integer contractId, java.util.Date newDate,
			boolean isChangeContract, String tc2, boolean isQuoteContract, Integer quoteGroup,
			boolean isOcupationContract, String ocupation) {
		
		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		Integer domain = contractRecord.get(CONTRACT.DOMAIN);
		Date newEndDate = contractRecord.get(CONTRACT.END_DATE);
		
		
		if(null != newDate) {
			java.util.Date previusDate = DateUtils.copyDateOnly(newDate);
			previusDate = DateUtils.addDays2Date(previusDate, -1);
			
			if(isChangeContract) {
				Result<Record> tc2Records = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("TC2")).orderBy(CONTRACT_DATA.ID.desc()).fetch();
				if(!tc2Records.isEmpty()) {
					dslContext.update(CONTRACT_DATA).set(CONTRACT_DATA.END_DATE, new Date(previusDate.getTime())).where(CONTRACT_DATA.ID.eq(tc2Records.get(0).get(CONTRACT_DATA.ID))).execute();
				}
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "TC2")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, (tc2 == null) ? (String) null : "\""+tc2+"\"")
					.set(CONTRACT_DATA.START_DATE, new Date(newDate.getTime()))
					.set(CONTRACT_DATA.END_DATE, newEndDate)
					.execute();
			}
			
			if(isQuoteContract) {
				Result<Record> quoteRecords = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION")).orderBy(CONTRACT_DATA.ID.desc()).fetch();
				if(!quoteRecords.isEmpty()) {
					dslContext.update(CONTRACT_DATA).set(CONTRACT_DATA.END_DATE, new Date(previusDate.getTime())).where(CONTRACT_DATA.ID.eq(quoteRecords.get(0).get(CONTRACT_DATA.ID))).execute();
				}
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "GRUPO_COTIZACION")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, (quoteGroup == null) ? (String) null : "\""+quoteGroup.toString()+"\"")
					.set(CONTRACT_DATA.START_DATE, new Date(newDate.getTime()))
					.set(CONTRACT_DATA.END_DATE, newEndDate)
					.execute();
			}
			
			if(isOcupationContract) {
				Result<Record> ocupationRecords = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("OCUPACION")).orderBy(CONTRACT_DATA.ID.desc()).fetch();
				if(!ocupationRecords.isEmpty()) {
					dslContext.update(CONTRACT_DATA).set(CONTRACT_DATA.END_DATE, new Date(previusDate.getTime())).where(CONTRACT_DATA.ID.eq(ocupationRecords.get(0).get(CONTRACT_DATA.ID))).execute();
				}
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "OCUPACION")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, ocupation)
					.set(CONTRACT_DATA.START_DATE, new Date(newDate.getTime()))
					.set(CONTRACT_DATA.END_DATE, newEndDate)
					.execute();
			}
		}
		
		return "";
	}
	
	// ---------------------------------------------------------------------------------------------------------------------------
	// ---------------------------------------------------    AUXLIAR METHODS   --------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------------	
	
	private static List<String> getContractOtherDataListNames(String contractTypeStr) {
		Integer contractType = Integer.parseInt(contractTypeStr);
		
		if(contractType >= 100 && contractType <= 400) {
			return getContractOtherDataIndefiniteListNames();
		} else if (contractType == 421) {
			return getContractOtherDataFormationListNames();
		} else if (contractType == 420 || contractType == 520) {
			return getContractOtherDataPracticeListNames();
		} else
			return getContractOtherDataTempListNames();
	}
	
	private static List<String> getContractOtherDataIndefiniteListNames(){
		List<String> contractOtherDataNames = new ArrayList<String>();
		
		contractOtherDataNames.add("ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("FUNCTIONS");
		contractOtherDataNames.add("EMPLOYEE_CONTRACT_DISTANCE");
		contractOtherDataNames.add("EMPLOYEE_CONTRACT_DIST_ADDR");
		contractOtherDataNames.add("DISC_WORK_DESCRIPTION");
		contractOtherDataNames.add("DISC_WORK_ACTIVITY");
		contractOtherDataNames.add("DISC_WORK_DURATION");
		contractOtherDataNames.add("DISC_WORK_ESTIMATED_DURATION");
		contractOtherDataNames.add("DISC_WORK_ESTIM_JOURNAL_HOURS");
		contractOtherDataNames.add("DISC_WORK_ESTIM_JOURNAL_PERIOD");
		contractOtherDataNames.add("DISC_WORK_ESTIM_SCHEDULE");
		contractOtherDataNames.add("DISC_AGREEMENT_COLLECTIVE");
		contractOtherDataNames.add("FULL_TIME_WEEK_HOURS");
		contractOtherDataNames.add("FULL_TIME_START_TIME");
		contractOtherDataNames.add("FULL_TIME_END_TIME");
		contractOtherDataNames.add("PARTIALLY_TIME_HOURS");
		contractOtherDataNames.add("DEFAULT_JOURNAL_HOURS");
		contractOtherDataNames.add("COMPLEMENTARY_HOURS");
		contractOtherDataNames.add("TRIAL_DURATION");
		contractOtherDataNames.add("SALARY_AMOUNT");
		contractOtherDataNames.add("SALARY_PERIOD");
		contractOtherDataNames.add("SALARY_CONCEPT");
		contractOtherDataNames.add("HOLIDAYS");
		contractOtherDataNames.add("SEPE_MUNICIPALITY");
		contractOtherDataNames.add("I_OPT2_SEPE_MUNICIPALITY");
		contractOtherDataNames.add("I_OPT2_DISABILITY_NO_SEVERE");
		contractOtherDataNames.add("I_OPT2_DISABILITY_SEVERE");
		contractOtherDataNames.add("I_OPT2_REDUCTION");
		contractOtherDataNames.add("I_OPT5_BONUS_ART4_RDL3_2012");
		contractOtherDataNames.add("I_OPT5_UNEMPLOYED_BT_16_30");
		contractOtherDataNames.add("I_OPT5_UNEMPLOYED_GT_45");
		contractOtherDataNames.add("I_OPT5_UNEMPL_3_MONTH_BENEFIT");
		contractOtherDataNames.add("I_OPT5_FIRST_EMPLOYEE_AND_LT_30");
		contractOtherDataNames.add("I_OPT6_AGE");
		contractOtherDataNames.add("I_OPT6_AGREEMENT_COLLECTIVE1");
		contractOtherDataNames.add("I_OPT6_AGREEMENT_COLLECTIVE2");
		contractOtherDataNames.add("I_OPT15_ONSITE_HOURS");
		contractOtherDataNames.add("I_OPT15_ONSITE_WEEK_HOURS");
		contractOtherDataNames.add("I_OPT15_SALARY");
		contractOtherDataNames.add("I_OPT15_OVERNIGHT");
		contractOtherDataNames.add("I_OPT15_OVERNIGHT_WEEK_DAYS");
		contractOtherDataNames.add("I_OPT17_FULL_TIME_QUOTE_BONUS");
		contractOtherDataNames.add("I_OPT17_DISCONT_TIME_QUOTE_BONUS");
		contractOtherDataNames.add("I_OPT17_SRC_CONTRACT_SEPE_MUNIC");
		
		return contractOtherDataNames;
	}
	
	private static List<String> getContractOtherDataTempListNames(){
		List<String> contractOtherDataNames = new ArrayList<String>();
		
		contractOtherDataNames.add("T_ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("T_ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("T_ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("T_LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("T_LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("T_LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("T_FUNCTIONS");
		contractOtherDataNames.add("T_EMPLOYEE_CONTRACT_DISTANCE");
		contractOtherDataNames.add("T_EMPLOYEE_CONTRACT_DIST_ADDR");
		contractOtherDataNames.add("T_FULL_TIME_WEEK_HOURS");
		contractOtherDataNames.add("T_FULL_TIME_START_TIME");
		contractOtherDataNames.add("T_FULL_TIME_END_TIME");
		contractOtherDataNames.add("T_PARTIALLY_TIME_JOB_LOWER_THAN");
		contractOtherDataNames.add("T_PARTIALLY_TIME_JOB_DISTRIB");
		contractOtherDataNames.add("T_END_DATE_TEXT");
		contractOtherDataNames.add("T_TRIAL_DURATION");
		contractOtherDataNames.add("T_GREATER_DURATION_AGREEMENT_COL");
		contractOtherDataNames.add("T_SALARY_AMOUNT");
		contractOtherDataNames.add("T_SALARY_PERIOD");
		contractOtherDataNames.add("T_SALARY_CONCEPT");
		contractOtherDataNames.add("T_HOLIDAYS");
		contractOtherDataNames.add("T_SEPE_MUNICIPALITY");
		contractOtherDataNames.add("T_OPT1_WORK_DESCRIPTION1");
		contractOtherDataNames.add("T_OPT1_WORK_DESCRIPTION2");
		contractOtherDataNames.add("T_OPT2_WORK_DESCRIPTION1");
		contractOtherDataNames.add("T_OPT2_WORK_DESCRIPTION2");
		contractOtherDataNames.add("T_OPT3_REPLACED_WORKER_NAME");
		contractOtherDataNames.add("T_OPT10_REQUIREMENTS_OPT");
		contractOtherDataNames.add("T_OPT10_FORMATION_OPT");
		contractOtherDataNames.add("T_OPT10_FORMATION_TYPE_OPT");
		contractOtherDataNames.add("T_OPT10_FORMATION_TYPE_OPT1_TEXT");
		contractOtherDataNames.add("T_OPT10_FORMATION_TYPE_OPT2_TEXT");
		contractOtherDataNames.add("T_OPT12_ONSITE_HOURS");
		contractOtherDataNames.add("T_OPT12_ONSITE_WEEK_HOURS");
		contractOtherDataNames.add("T_OPT12_ONSITE_HOURS_DISTRIB");
		contractOtherDataNames.add("T_OPT12_SALARY_OPT");
		contractOtherDataNames.add("T_OPT12_OVERNIGHT");
		contractOtherDataNames.add("T_OPT12_OVERNIGHT_WEEK_DAYS");
		contractOtherDataNames.add("T_OPT13_DISABILITY_ISSUED_BY");
		contractOtherDataNames.add("T_OPT13_DISABILITY");
		contractOtherDataNames.add("T_OPT13_SEVERE_DISABILITY");
		contractOtherDataNames.add("T_OPT14_TRIAL_PERIOD");
		contractOtherDataNames.add("T_OPT14_TRIAL_TERMS");
		contractOtherDataNames.add("T_OPT14_PROFESSION");
		contractOtherDataNames.add("T_OPT14_DISTANCE_ADJUSTMENT");
		contractOtherDataNames.add("T_OPT14_DISTANCE_ADJUSTMENT_MORE");
		contractOtherDataNames.add("T_OPT14_COLLECTIVE_AGREEMENT");
		
		return contractOtherDataNames;
	}
	
	private static List<String> getContractOtherDataFormationListNames(){
		List<String> contractOtherDataNames = new ArrayList<String>();
		
		contractOtherDataNames.add("L_ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("L_ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("L_ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("L_LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("L_LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("L_LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("L_QUOTE_BONUS");
		contractOtherDataNames.add("L_EMPLOYEE_OPT");
		contractOtherDataNames.add("L_CONTRACT_WORKPLACE_ADDRESS");
		contractOtherDataNames.add("L_FORMATION_TEACHER");
		contractOtherDataNames.add("L_HORARIO_LABORAL");
		contractOtherDataNames.add("L_HORARIO_LECTIVO");
		contractOtherDataNames.add("L_TRIAL_DURATION");
		contractOtherDataNames.add("L_TRIAL_DURATION_INCREASE");
		contractOtherDataNames.add("L_SALARY_AMOUNT");
		contractOtherDataNames.add("L_SALARY_PERIOD");
		contractOtherDataNames.add("L_HOLIDAYS");
		contractOtherDataNames.add("L_ANNEX_I_CHECK");
		contractOtherDataNames.add("L_ANNEX_II_CHECK");
		
		return contractOtherDataNames;
	}
	
	private static List<String> getContractOtherDataPracticeListNames(){
		List<String> contractOtherDataNames = new ArrayList<String>();
		
		contractOtherDataNames.add("P_ENTERPRISE_DIR_STAFF_NAME");
		contractOtherDataNames.add("P_ENTERPRISE_DIR_STAFF_NIF");
		contractOtherDataNames.add("P_ENTERPRISE_DIR_STAFF_CHARGE");
		contractOtherDataNames.add("P_LEGAL_REPRESENTATIVE_NAME");
		contractOtherDataNames.add("P_LEGAL_REPRESENTATIVE_NIF");
		contractOtherDataNames.add("P_LEGAL_REPRESENTATIVE_CHARGE");
		contractOtherDataNames.add("P_PROFESSIONAL_CERT");
		contractOtherDataNames.add("P_PROFESSIONAL_CERT_OBTAIN_DATE");
		contractOtherDataNames.add("P_DISABILITY_ISSUE_ENTITY");
		contractOtherDataNames.add("P_DISABILITY_ISSUE_ENTITY_MORE");
		contractOtherDataNames.add("P_FIRST_CONTRACT");
		contractOtherDataNames.add("P_FULL_TIME_WEEK_HOURS");
		contractOtherDataNames.add("P_FULL_TIME_START_TIME");
		contractOtherDataNames.add("P_FULL_TIME_END_TIME");
		contractOtherDataNames.add("P_JOB_TIME_DISTRIBUTION2");
		contractOtherDataNames.add("P_TRIAL_DURATION");
		contractOtherDataNames.add("P_SALARY_AMOUNT");
		contractOtherDataNames.add("P_SALARY_PERIOD");
		contractOtherDataNames.add("P_SALARY_CONCEPT");
		contractOtherDataNames.add("P_HOLIDAYS");
		contractOtherDataNames.add("P_SEPE_START_COMMUNICATION");
		contractOtherDataNames.add("P_SEPE_END_COMMUNICATION");
		contractOtherDataNames.add("P_OPT3_UNEMPLOYMENT");
		contractOtherDataNames.add("P_OPT4_TRIAL_DURATION");
		contractOtherDataNames.add("P_OPT4_TRIAL_DURATION_CONDITIONS");
		contractOtherDataNames.add("P_OPT4_WORK_PLACE_ADAPTATIONS");
		contractOtherDataNames.add("P_OPT4_STAFF_ADJUSTMENT");
		contractOtherDataNames.add("P_OPT4_STAFF_ADJUSTMENT_MORE");
		contractOtherDataNames.add("P_OPT5_MOTIVATION");
		contractOtherDataNames.add("P_OPT5_EMPLOYER");
		
		return contractOtherDataNames;
	}

}
