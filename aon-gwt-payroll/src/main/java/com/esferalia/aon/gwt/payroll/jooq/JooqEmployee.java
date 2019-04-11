package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geotree.GEOTREE;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractInfoRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
import com.esferalia.aon.jooq.tables.records.PayMethodRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RbankRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.RmediaRecord;
import com.esferalia.aon.jooq.tables.records.RpaymethodRecord;

public class JooqEmployee {

	private static Settings SETTINGS = null;

	public static EmployeeContractInfo getEmployeeInfo(Connection conn, Integer contract) {
		return getEmployeeInfoDB(DSL.using(conn, getDefaultSettings()), contract);
	}
	
	public static EmployeeContractInfo setEmployeeInfo(Connection conn, EmployeeContractInfo newEmployeeInfo) {
		return setEmployeeInfoDB(DSL.using(conn, getDefaultSettings()), newEmployeeInfo);
	}
	
	public static EmployeeContractInfo createEmployeeContract(Connection conn, EmployeeContractInfo employeeContractData) {
		return createEmployeeContractDB(DSL.using(conn, getDefaultSettings()), employeeContractData);
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	

	private static EmployeeContractInfo getEmployeeInfoDB(DSLContext dslContext, Integer contract) {
		
		// ------------------------------------------------ EMPLOYEE INFO ---------------------------------------------------------
		EmployeeContractInfo employeeContractInfo = new EmployeeContractInfo();
		ContractInfo contractData = new ContractInfo();
		EmployeeInfo employeeData = new EmployeeInfo();
		
		//PERSON TABLE
		Record personTable = dslContext.select().from(PERSON)
				.where(PERSON.REGISTRY.eq(
						dslContext.select(CONTRACT.PERSON).from(CONTRACT)
							.where(CONTRACT.ID.eq(contract))))
				.fetchOne();
	
		employeeData.setEmployeeId(personTable.get(PERSON.REGISTRY));
		employeeData.setDomain(personTable.get(PERSON.DOMAIN));
		employeeData.setBirthdate(personTable.get(PERSON.BIRTH_DATE));
		employeeData.setGender(personTable.get(PERSON.GENDER));
		employeeData.setSsNumber(personTable.get(PERSON.SOCIAL_SECURITY_NUM));
		employeeData.setName(personTable.get(PERSON.NAME));
		employeeData.setSurName(personTable.get(PERSON.FIRST_SURNAME));
		employeeData.setSecondSurName(personTable.get(PERSON.SECOND_SURNAME));
		
		
		Integer employee_registry = personTable.get(PERSON.REGISTRY);
		
		//REGISTRY TABLE
		Record registryTable = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(employee_registry))
				.fetchOne();
		
		employeeData.setDocument(registryTable.get(REGISTRY.DOCUMENT));
		employeeData.setDocumentType(registryTable.get(REGISTRY.DOCUMENT_TYPE)); //Esto lo saco con el formato del documento, �se podria obviar?
		employeeData.setNationality(registryTable.get(REGISTRY.NATIONALITY));
		
		//RADDRESS AND GEOZONE TABLE
		Record raddressTable = dslContext.select().from(RADDRESS)
				.where(RADDRESS.REGISTRY.eq(employee_registry))
				.fetchOne();
		
		if(null != raddressTable) {
		
			employeeData.setRaddressId(raddressTable.get(RADDRESS.ID));
			employeeData.setStreetType(raddressTable.get(RADDRESS.STREET_TYPE));
			employeeData.setAddress(raddressTable.get(RADDRESS.ADDRESS));
			employeeData.setAddresNum(raddressTable.get(RADDRESS.NUMBER));
			employeeData.setAddressZip(raddressTable.get(RADDRESS.ZIP));
			employeeData.setAddressCity(raddressTable.get(RADDRESS.CITY));
			
			Integer raddress_geozone = raddressTable.get(RADDRESS.GEOZONE);
			if(null == raddress_geozone) {
				employeeData.setGeozoneId(null);
				employeeData.setAddressProvinces(null);
			}else {
				Record geozoneTable = dslContext.select().from(GEOZONE)
						.where(GEOZONE.ID.eq(raddress_geozone))
						.fetchOne();
				
				employeeData.setGeozoneId(geozoneTable.get(GEOZONE.ID));
				employeeData.setAddressProvinces(geozoneTable.get(GEOZONE.NAME));
			}
		}
		
		//RMEDIA TABLE
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
		
		//FIND RPAYMETHOD
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
			
			employeeData.setPaymethodId(payMethodTable.get(PAY_METHOD.ID));
			employeeData.setPayMethodType(payMethodTable.get(PAY_METHOD.NAME));
			
			Integer rbank = rPayMethodRecord.get(RPAYMETHOD.RBANK);
			
			if(null == rbank) {
				employeeData.setRbankId(null);
				employeeData.setAccount(null);
				employeeData.setBic(null);
			}else {
				Record rBankTable = dslContext.select().from(RBANK)
						.where(RBANK.ID.eq(rbank))
						.fetchOne();
				
				employeeData.setRbankId(rBankTable.get(RBANK.ID));
				employeeData.setAccount(rBankTable.get(RBANK.BANK_ACCOUNT));
				employeeData.setBic(rBankTable.get(RBANK.BIC));
			}
			
		}		
		
		// ------------------------------------------------ CONTRACT INFO ---------------------------------------------------------
		System.out.println("******************************* CONTRACT = "+contract+" *******************************");
		
		//HAS PAYROLL
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contract))
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
		
		//CONTRACT TABLE
		Record contractTable = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contract))
				.fetchOne();
		
		contractData.setContractId(contract);
		contractData.setStartDate(contractTable.get(CONTRACT.START_DATE));
		contractData.setEndDate(contractTable.get(CONTRACT.END_DATE));
		contractData.setSeniorityDate(contractTable.get(CONTRACT.SENIORITY_DATE));
		contractData.setAgreementCategory(contractTable.get(CONTRACT.CATEGORY_DESCRIPTION));
		contractData.setSsRegimen(contractTable.get(CONTRACT.SS_REGIME));
		
		contractData.setOldStartDate(contractTable.get(CONTRACT.START_DATE));
		contractData.setOldEndDate(contractTable.get(CONTRACT.END_DATE));
		
		Integer employe_workplace_table_id = contractTable.get(CONTRACT.WORKPLACE);
		
		//WORKPLACE TABLE		
		contractData.setWorkplaceId(employe_workplace_table_id);
		
		if(contractData.getSsRegimen() != 3){ //NO ES RETA
			//ENTERPRISE ACTIVITY TABLE
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
		
		//AGREEMENT LEVEL TABLE
		Integer agreementLevel = contractTable.get(CONTRACT.AGREEMENT_LEVEL);
		
		if(null == agreementLevel) {
			contractData.setAgreementLevelId(null);
			contractData.setAgreementId(null);
		}else {
			Record agreementLevelTable = dslContext.select().from(AGREEMENT_LEVEL)
					.where(AGREEMENT_LEVEL.ID.eq(agreementLevel))
					.fetchOne();
			
			contractData.setAgreementLevelId(agreementLevelTable.get(AGREEMENT_LEVEL.ID));
			//employee.setAgreement_level(agreementLevelTable.get(AGREEMENT_LEVEL.DESCRIPTION)); //Can be null
			
			//AGREEMENT TABLE
			Integer agreement = agreementLevelTable.get(AGREEMENT_LEVEL.AGREEMENT);
			
			Record agreementTable = dslContext.select().from(AGREEMENT)
					.where(AGREEMENT.ID.eq(agreement))
					.fetchOne();
			
			contractData.setAgreementId(agreementTable.get(AGREEMENT.ID));
			//employee.setAgreement(agreementTable.get(AGREEMENT.DESCRIPTION)); //Can be null	
			
		}
		
		//FECHA ACTUAL
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
		
		//CONTRACT DATA TABLE
		Result<Record> contractDataTable = dslContext.select().from(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contract))
			.orderBy(CONTRACT_DATA.START_DATE)
			.fetch();
		
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
		
		//CONTRACT INFO TABLE
		Record contractInfoTable = dslContext.select().from(CONTRACT_INFO)
			.where(CONTRACT_INFO.CONTRACT.eq(contract))
			.and(CONTRACT_INFO.NAME.eq("OPCION_CONTRATO"))
			.fetchOne();
		
		if(null == contractInfoTable) {
			contractData.setContractmodelId(null);
			contractData.setContractModel(null);
		}else if(null == contractInfoTable.get(CONTRACT_INFO.EXPRESSION)){
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
		
		contractInfoTable = dslContext.select().from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contract))
				.and(CONTRACT_INFO.NAME.eq("RETA"))
				.fetchOne();
		
		if(null == contractInfoTable)
			contractData.setRetaId(null);
		else
			contractData.setRetaId(contractInfoTable.get(CONTRACT_INFO.ID));
		
		Result<Record> journiesDB = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.like("HORAS%"))
				.and(CONTRACT_DATA.CONTRACT.eq(contract))
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
			
		System.out.println(employeeData.toString());
		System.out.println(contractData.toString());
		
		employeeContractInfo.setEmployeeInfo(employeeData);
		employeeContractInfo.setContractInfo(contractData);
		
		return employeeContractInfo;
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

	private static EmployeeContractInfo setEmployeeInfoDB(DSLContext dslContext, EmployeeContractInfo employeeContractInfo) {
		
		ContractInfo contractData = employeeContractInfo.getContractInfo();
		EmployeeInfo employeeData = employeeContractInfo.getEmployeeInfo();
		
		System.out.println(contractData.toString());
		System.out.println(employeeData.toString());
		
		// ------------------------------------------------------------------------------------------------------------------------
		// ------------------------------------------------ EMPLOYEE INFO ---------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------------------------
		
		dslContext.update(PERSON)
			.set(PERSON.BIRTH_DATE, (null == employeeData.getBirthdate()) ? null : new Date(employeeData.getBirthdate().getTime()))
			.set(PERSON.GENDER, employeeData.getGender())
			.set(PERSON.SOCIAL_SECURITY_NUM, employeeData.getSsNumber())
			.set(PERSON.NAME, employeeData.getName())
			.set(PERSON.FIRST_SURNAME, employeeData.getSurName())
			.set(PERSON.SECOND_SURNAME, employeeData.getSecondSurName())
			.where(PERSON.REGISTRY.eq(employeeData.getEmployeeId()))
			.execute();
		
		dslContext.update(REGISTRY)
			.set(REGISTRY.DOCUMENT, employeeData.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE, employeeData.getDocumentType())
			.set(REGISTRY.DOCUMENT_COUNTRY, employeeData.getNationality())
			.set(REGISTRY.NATIONALITY, employeeData.getNationality())
			.set(REGISTRY.NAME, (null == employeeData.getSurName() ? "" :  employeeData.getSurName() + " ") + 
					(null == employeeData.getSecondSurName() ? "" : employeeData.getSecondSurName() + ", ") + 
					employeeData.getName())
			.where(REGISTRY.ID.eq(employeeData.getEmployeeId()))
			.execute();
		
		Integer domain = employeeData.getDomain();
		Integer registryId = employeeData.getEmployeeId();
		
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.eq(domain))
				.fetchOne();
		
		Integer parentDomain = domainRecord.get(DOMAIN.PARENT);
		
		Integer rAddressId = null;
		
		if(null != employeeData.getAddressProvinces()) {
		
			Result<Record> geozone = dslContext.select()
					.from(GEOZONE)
					.where(GEOZONE.NAME.eq(employeeData.getAddressProvinces()))
						.and(GEOZONE.DOMAIN.eq(domain)
								.or(GEOZONE.DOMAIN.eq(parentDomain)))
					.fetch();
			
			Integer geozoneId = null;
			
			if(geozone == null){
				Result<Record1<String>> codes = dslContext.select(GEOZONE.CODE)
					.from(GEOZONE)
					.where(GEOZONE.NAME.like(employeeData.getAddressProvinces()+"%"))
					.fetch();
				
				if(!codes.isEmpty()){
					GeozoneRecord geozoneRecord  = dslContext.insertInto(GEOZONE)
							.set(GEOZONE.DOMAIN, domain)
							.set(GEOZONE.NAME, employeeData.getAddressProvinces())
							.set(GEOZONE.CODE, codes.get(0).value1())
							.returning(GEOZONE.ID)
							.fetchOne();
						
					geozoneId = geozoneRecord.getId();
				}
			}else
				geozoneId = geozone.get(0).get(GEOZONE.ID);
	
			rAddressId = employeeData.getRaddressId();
			
			if(null == rAddressId){
				RaddressRecord rAddressRecord = dslContext.insertInto(RADDRESS)
						.set(RADDRESS.DOMAIN, domain)
						.set(RADDRESS.REGISTRY, registryId)
						.set(RADDRESS.STREET_TYPE, employeeData.getStreetType())
						.set(RADDRESS.ADDRESS, employeeData.getAddress())
						.set(RADDRESS.NUMBER, employeeData.getAddresNum())
						.set(RADDRESS.ZIP, employeeData.getAddressZip())
						.set(RADDRESS.CITY, employeeData.getAddressCity())
						.set(RADDRESS.GEOZONE, geozoneId)
						.returning(RADDRESS.ID, RADDRESS.GEOZONE)
						.fetchOne();
				
				rAddressId = rAddressRecord.getId();
				employeeData.setRaddressId(rAddressId);
			}else{
				dslContext.update(RADDRESS)
						.set(RADDRESS.STREET_TYPE, employeeData.getStreetType())
						.set(RADDRESS.ADDRESS, employeeData.getAddress())
						.set(RADDRESS.NUMBER, employeeData.getAddresNum())
						.set(RADDRESS.ZIP, employeeData.getAddressZip())
						.set(RADDRESS.CITY, employeeData.getAddressCity())
						.set(RADDRESS.GEOZONE, geozoneId)
						.where(RADDRESS.ID.eq(rAddressId))
						.execute();
			}
			
			if(geozone == null && null != geozoneId){
				Integer rAddressGeozone = geozoneId;
				
				GeozoneRecord geozoneParentRecord  = dslContext.insertInto(GEOZONE)
						.set(GEOZONE.DOMAIN, domain)
						.set(GEOZONE.NAME, "ESPA�A")
						.set(GEOZONE.CODE, "ES")
						.set(GEOZONE.SYSTEM, (byte) 1)
						.returning(GEOZONE.ID)
						.fetchOne();
				
				Integer geozoneParentId = geozoneParentRecord.getId();
				
				dslContext.insertInto(GEOTREE)
				.set(GEOTREE.DOMAIN, domain)
				.set(GEOTREE.PARENT, geozoneParentId)
				.set(GEOTREE.CHILD, rAddressGeozone)
				.execute();
				
				dslContext.insertInto(GEOTREE)
				.set(GEOTREE.DOMAIN, domain)
				.set(GEOTREE.PARENT, (Integer) null)
				.set(GEOTREE.CHILD, geozoneParentId)
				.execute();
			}
		}
		
		if(null == employeeData.getPhoneId()){
			if(null != employeeData.getPhone()){
				RmediaRecord phoneRecord = dslContext.insertInto(RMEDIA, RMEDIA.ID, RMEDIA.DOMAIN, RMEDIA.REGISTRY, RMEDIA.MEDIA, RMEDIA.VALUE, RMEDIA.COMMENT, 
						  RMEDIA.ADMINISTRATIVE, RMEDIA.COMMERCIAL, RMEDIA.TECHNICAL, RMEDIA.RADDRESS)
					.values(employeeData.getPhoneId(), domain, registryId, (byte) 1, employeeData.getPhone(), (String) null, 
							(byte) 1, (byte) 1, (byte) 1, rAddressId)
					.returning(RMEDIA.ID)
					.fetchOne();
				
				employeeData.setPhoneId(phoneRecord.getId());
			}
		}else{
			if(null == employeeData.getPhone()){
				dslContext.delete(RMEDIA).where(RMEDIA.ID.eq(employeeData.getPhoneId())).execute();
				employeeData.setPhoneId(null);
				employeeData.setPhone(null);
			}else{
				dslContext.update(RMEDIA)
					.set(RMEDIA.VALUE, employeeData.getPhone())
					.set(RMEDIA.RADDRESS, rAddressId)
					.where(RMEDIA.ID.eq(employeeData.getPhoneId()))
					.execute();
			}
		}
		
		if(null == employeeData.getMobileId()){
			if(null != employeeData.getMobile()){
				RmediaRecord mobileRecord = dslContext.insertInto(RMEDIA, RMEDIA.ID, RMEDIA.DOMAIN, RMEDIA.REGISTRY, RMEDIA.MEDIA, RMEDIA.VALUE, RMEDIA.COMMENT, 
						  RMEDIA.ADMINISTRATIVE, RMEDIA.COMMERCIAL, RMEDIA.TECHNICAL, RMEDIA.RADDRESS)
					.values(employeeData.getMobileId(), domain, registryId, (byte) 2, employeeData.getMobile(), (String) null, 
							(byte) 1, (byte) 1, (byte) 1, rAddressId)
					.returning(RMEDIA.ID)
					.fetchOne();
				
				employeeData.setMobileId(mobileRecord.getId());
			}
		}else{
			if(null == employeeData.getMobile()){
				dslContext.delete(RMEDIA).where(RMEDIA.ID.eq(employeeData.getMobileId())).execute();
				employeeData.setMobileId(null);
				employeeData.setMobile(null);
			}else{
				dslContext.update(RMEDIA)
					.set(RMEDIA.VALUE, employeeData.getMobile())
					.set(RMEDIA.RADDRESS, rAddressId)
					.where(RMEDIA.ID.eq(employeeData.getMobileId()))
					.execute();
			}
		}
		
		if(null == employeeData.getEmailId()){
			if(null != employeeData.getEmail()){
				RmediaRecord emailRecord = dslContext.insertInto(RMEDIA, RMEDIA.ID, RMEDIA.DOMAIN, RMEDIA.REGISTRY, RMEDIA.MEDIA, RMEDIA.VALUE, RMEDIA.COMMENT, 
						  RMEDIA.ADMINISTRATIVE, RMEDIA.COMMERCIAL, RMEDIA.TECHNICAL, RMEDIA.RADDRESS)
					.values(employeeData.getEmailId(), domain, registryId, (byte) 4, employeeData.getEmail(), (String) null, 
							(byte) 1, (byte) 1, (byte) 1, rAddressId)
					.returning(RMEDIA.ID)
					.fetchOne();
				
				employeeData.setEmailId(emailRecord.getId());
			}
		}else{
			if(null == employeeData.getEmail()){
				dslContext.delete(RMEDIA).where(RMEDIA.ID.eq(employeeData.getEmailId())).execute();
				employeeData.setEmailId(null);
				employeeData.setEmail(null);
			}else{
				dslContext.update(RMEDIA)
					.set(RMEDIA.VALUE, employeeData.getEmail())
					.set(RMEDIA.RADDRESS, rAddressId)
					.where(RMEDIA.ID.eq(employeeData.getEmailId()))
					.execute();
			}
		}
		
		if(null == employeeData.getRpaymethodId()){
			if(employeeData.getPayMethodType() != null && employeeData.getPayMethodType() != ""){
				byte typePayMethod = getType(employeeData.getPayMethodType());
				if(-1 != typePayMethod){
					PayMethodRecord payMethodRecord = dslContext.insertInto(PAY_METHOD)
							.set(PAY_METHOD.DOMAIN, domain)
							.set(PAY_METHOD.NAME, employeeData.getPayMethodType())
							.set(PAY_METHOD.TYPE, typePayMethod)
							.returning(PAY_METHOD.ID)
							.fetchOne();
					
					Integer payMethodTableId = payMethodRecord.get(PAY_METHOD.ID);
					Integer rbankTableId = null;
					if(employeeData.getAccount() != null && employeeData.getAccount() != ""){
						RbankRecord rbankRecord = dslContext.insertInto(RBANK)
								.set(RBANK.DOMAIN, domain)
								.set(RBANK.REGISTRY, registryId)
								.set(RBANK.BANK_ACCOUNT, null == employeeData.getAccount() ? "" : employeeData.getAccount())
								.set(RBANK.BIC, employeeData.getBic())
								.set(RBANK.ALIAS, "CUENTA")
								.set(RBANK.ACTIVE, (byte) 1)
								.returning(RBANK.ID)
								.fetchOne();
						 
						 rbankTableId = rbankRecord.get(RBANK.ID); 
					}
					
					RpaymethodRecord rpaymethodRecord = dslContext.insertInto(RPAYMETHOD)
						.set(RPAYMETHOD.DOMAIN, domain)
						.set(RPAYMETHOD.REGISTRY, registryId)
						.set(RPAYMETHOD.PAY_METHOD, payMethodTableId)
						.set(RPAYMETHOD.RBANK, rbankTableId)
						.returning(RPAYMETHOD.ID)
						.fetchOne();
					
					Integer rpayMethodTableId = rpaymethodRecord.get(RPAYMETHOD.ID);
				
					//ACTUALIZAR CAMPOS
					employeeData.setPaymethodId(payMethodTableId);
					employeeData.setRpaymethodId(rpayMethodTableId);
					employeeData.setRbankId(rbankTableId);
				}
			}
		}else{
			if("TRANSFERENCIA".equals(employeeData.getPayMethodType())){
				Integer rbankTableId = employeeData.getRbankId();
				if(null == employeeData.getRbankId()  /*&& (employeeData.getAccount() != null && employeeData.getAccount() != "")*/){
					RbankRecord rbankRecord = dslContext.insertInto(RBANK)
							.set(RBANK.DOMAIN, domain)
							.set(RBANK.REGISTRY, registryId)
							.set(RBANK.BANK_ACCOUNT, null == employeeData.getAccount() ? "" : employeeData.getAccount())
							.set(RBANK.BIC, employeeData.getBic())
							.set(RBANK.ALIAS, "CUENTA")
							.set(RBANK.ACTIVE, (byte) 1)
							.returning(RBANK.ID)
							.fetchOne();
					 
					 rbankTableId = rbankRecord.get(RBANK.ID); 
				}else{
					dslContext.update(RBANK)
							.set(RBANK.BANK_ACCOUNT, null == employeeData.getAccount() ? "" : employeeData.getAccount())
							.set(RBANK.BIC, employeeData.getBic())
							.where(RBANK.ID.eq(rbankTableId))
							.execute();
				}
				
				byte typePayMethod = getType(employeeData.getPayMethodType());
				dslContext.update(PAY_METHOD)
						.set(PAY_METHOD.NAME, employeeData.getPayMethodType())
						.set(PAY_METHOD.TYPE, typePayMethod)
						.where(PAY_METHOD.ID.eq(employeeData.getPaymethodId()))
						.execute();
				
				dslContext.update(RPAYMETHOD)
				.set(RPAYMETHOD.RBANK, rbankTableId)
				.where(RPAYMETHOD.ID.eq(employeeData.getRpaymethodId()))
				.execute();
				
				//ACTUALIZAR CAMPOS
//				employeeData.setPaymethodId(payMethodTableId);
//				employeeData.setRpaymethodId(rpayMethodTableId);
				employeeData.setRbankId(rbankTableId);
				
			}else{
				byte typePayMethod = getType(employeeData.getPayMethodType());
				if(-1 == typePayMethod){
					dslContext.delete(RPAYMETHOD)
					.where(RPAYMETHOD.ID.eq(employeeData.getRpaymethodId()))
					.execute();
					
					dslContext.delete(RBANK)
						.where(RBANK.ID.eq(employeeData.getRbankId()))
						.execute();
					
					dslContext.delete(PAY_METHOD)
						.where(PAY_METHOD.ID.eq(employeeData.getPaymethodId()))
						.execute();
					
					//ACTUALIZAR CAMPOS
					employeeData.setPaymethodId(null);
					employeeData.setRpaymethodId(null);
					employeeData.setRbankId(null);
				}else{
					dslContext.update(PAY_METHOD)
						.set(PAY_METHOD.NAME, employeeData.getPayMethodType())
						.set(PAY_METHOD.TYPE, typePayMethod)
						.where(PAY_METHOD.ID.eq(employeeData.getPaymethodId()))
						.execute();
			
					if(null != employeeData.getRbankId()){
						dslContext.update(RPAYMETHOD)
							.set(RPAYMETHOD.RBANK, (Integer) null)
							.where(RPAYMETHOD.ID.eq(employeeData.getRpaymethodId()))
							.execute();
						
						dslContext.delete(RBANK)
							.where(RBANK.ID.eq(employeeData.getRbankId()))
							.execute();
						
						//ACTUALIZAR CAMPOS
						employeeData.setRbankId(null);
					}
				}
			}
		}
		
		// ------------------------------------------------------------------------------------------------------------------------
		// ------------------------------------------------ CONTRACT INFO ---------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------------------------
		
		Record contractTable = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractData.getContractId())).fetchOne();
		
		Date oldStartDate = contractTable.get(CONTRACT.START_DATE);
		Date oldEndDate = contractTable.get(CONTRACT.END_DATE);
		
		Date startDate = new Date(contractData.getStartDate().getTime());
		Date endDate = (null == contractData.getEndDate()) ? null : new Date(contractData.getEndDate().getTime());
		
		dslContext.update(CONTRACT)
			.set(CONTRACT.START_DATE, startDate)
			.set(CONTRACT.END_DATE, endDate)
			.set(CONTRACT.SENIORITY_DATE, (null == contractData.getSeniorityDate()) ? new Date(contractData.getStartDate().getTime()) : new Date(contractData.getSeniorityDate().getTime()))
			.set(CONTRACT.CATEGORY_DESCRIPTION, contractData.getAgreementCategory())
			.set(CONTRACT.AGREEMENT_LEVEL, contractData.getAgreementLevelId())
			.set(CONTRACT.WORKPLACE, contractData.getWorkplaceId())
			.where(CONTRACT.ID.eq(contractData.getContractId()))
			.execute();
		
		if(contractData.getSsRegimen() != 3){ //NO ES RETA
			
			dslContext.update(CONTRACT)
				.set(CONTRACT.ENTERPRISE_CCC, contractData.getCccId())
				.set(CONTRACT.ENTERPRISE_ACTIVITY, contractData.getActivityId())
				.set(CONTRACT.SS_REGIME, contractData.getSsRegimen())
				.where(CONTRACT.ID.eq(contractData.getContractId()))
				.execute();
			
			if(null == contractData.getContracttypeId()){
				if(null != contractData.getContractType()){
					ContractDataRecord tc2Record = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
							CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
						.values(contractData.getContracttypeId(), domain, "TC2", contractData.getContractId(), "\""+ contractData.getContractType()+"\"", 
								startDate, endDate)
						.returning(CONTRACT_DATA.ID)
						.fetchOne();
					
					contractData.setContractId(tc2Record.getId());
				}
			}else{
				if(null == contractData.getContractType()){
					dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getContracttypeId())).execute();
					contractData.setContractId(null);
					contractData.setContractType(null);
				}else{
					dslContext.update(CONTRACT_DATA)
						.set(CONTRACT_DATA.EXPRESSION, "\""+ contractData.getContractType()+"\"")
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.where(CONTRACT_DATA.ID.eq(contractData.getContracttypeId()))
						.execute();
				}
			}
			
			if(null == contractData.getQuotegroupId()){
				if(null != contractData.getQuoteGroup()){
					ContractDataRecord contizacionRecord = null;
					if(contractData.hasPayroll()){
						Date newEndDate = endDate; //Fecha fin contrato
						java.util.Date auxDate = DateUtils.copyDateOnly(contractData.getPayrollDate());
						DateUtils.addDays2Date(auxDate, 1);
						Date auxStartDate = new Date(auxDate.getTime()); //Fecha inicio nuevo tramo grupo cotizacion
						
						contizacionRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
								CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
							.values(null, domain, "GRUPO_COTIZACION", contractData.getContractId(), "\""+ contractData.getQuoteGroup()+"\"", 
									auxStartDate, newEndDate)
							.returning(CONTRACT_DATA.ID)
							.fetchOne();
					}else{
						contizacionRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
								CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
							.values(contractData.getQuotegroupId(), domain, "GRUPO_COTIZACION", contractData.getContractId(), "\""+ contractData.getQuoteGroup()+"\"", 
									startDate, endDate)
							.returning(CONTRACT_DATA.ID)
							.fetchOne();
					}
					contractData.setQuotegroupId(contizacionRecord.getId());
				}
			}else{
				if(null == contractData.getQuoteGroup()){
					if(contractData.hasPayroll()){
						java.util.Date auxDate = DateUtils.copyDateOnly(contractData.getPayrollDate());
						auxDate.setDate(auxDate.getDate()+1);
						Date auxStartDate = new Date(auxDate.getTime()); //Fecha inicio nuevo tramo grupo cotizacion
						
						Record auxRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getQuotegroupId())).fetchOne();
						if(auxStartDate.equals(auxRecord.get(CONTRACT_DATA.START_DATE))){
							dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getQuotegroupId())).execute();
							contractData.setQuotegroupId(null);
							contractData.setQuoteGroup(null);
						}else{
							Date auxEndDate = new Date(contractData.getPayrollDate().getTime()); //Fecha fin antigui ocupacion
							dslContext.update(CONTRACT_DATA).set(CONTRACT_DATA.END_DATE, auxEndDate).where(CONTRACT_DATA.ID.eq(contractData.getQuotegroupId())).execute();
							contractData.setQuotegroupId(null);
							contractData.setQuoteGroup(null);
						}
					}else{
						dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getQuotegroupId())).execute();
						contractData.setQuotegroupId(null);
						contractData.setQuoteGroup(null);
					}
				}else{
					if(contractData.hasPayroll()){
						Date newEndDate = endDate; //Fecha fin contrato
						Date auxEndDate = new Date(contractData.getPayrollDate().getTime()); //Fecha fin antigui grupo cotizacion
						java.util.Date auxDate = DateUtils.copyDateOnly(contractData.getPayrollDate());
						auxDate.setDate(auxDate.getDate()+1);
//						DateUtils.addDays(auxDate, 1);
						Date auxStartDate = new Date(auxDate.getTime()); //Fecha inicio nuevo tramo grupo cotizacion
						
						Record auxRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getQuotegroupId())).fetchOne();
						if(auxStartDate.equals(auxRecord.get(CONTRACT_DATA.START_DATE))){
							dslContext.update(CONTRACT_DATA)
							.set(CONTRACT_DATA.EXPRESSION, "\""+contractData.getQuoteGroup()+"\"")
							.where(CONTRACT_DATA.ID.eq(contractData.getQuotegroupId()))
							.execute();
						}else{
							dslContext.update(CONTRACT_DATA)
							.set(CONTRACT_DATA.END_DATE, auxEndDate)
							.where(CONTRACT_DATA.ID.eq(contractData.getQuotegroupId()))
							.execute();
						
							ContractDataRecord contizacionRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
									CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
								.values(null, domain, "GRUPO_COTIZACION", contractData.getContractId(), "\""+ contractData.getQuoteGroup()+"\"", 
										auxStartDate, newEndDate)
								.returning(CONTRACT_DATA.ID)
								.fetchOne();
							
							contractData.setQuotegroupId(contizacionRecord.getId());
						}
					}else
						dslContext.update(CONTRACT_DATA)
							.set(CONTRACT_DATA.EXPRESSION, "\""+contractData.getQuoteGroup()+"\"")
							.set(CONTRACT_DATA.START_DATE, startDate)
							.set(CONTRACT_DATA.END_DATE, endDate)
							.where(CONTRACT_DATA.ID.eq(contractData.getQuotegroupId()))
							.execute();
				}
			}
			
			if(null == contractData.getOcupationId()){
				if(null != contractData.getOcupation()){
					ContractDataRecord ocupacionRecord = null;
					if(contractData.hasPayroll()){
						Date newEndDate = endDate; //Fecha fin contrato
						java.util.Date auxDate = DateUtils.copyDateOnly(contractData.getPayrollDate());
						DateUtils.addDays2Date(auxDate, 1);
						Date auxStartDate = new Date(auxDate.getTime()); //Fecha inicio nuevo tramo ocupacion
						
						ocupacionRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
								CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
							.values(contractData.getOcupationId(), domain, "OCUPACION", contractData.getContractId(), "\""+ contractData.getOcupation()+"\"", 
									auxStartDate, newEndDate)
							.returning(CONTRACT_DATA.ID)
							.fetchOne();
					}else{
						ocupacionRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
								CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
							.values(contractData.getOcupationId(), domain, "OCUPACION", contractData.getContractId(), "\""+ contractData.getOcupation()+"\"", 
									startDate, endDate)
							.returning(CONTRACT_DATA.ID)
							.fetchOne();
					}
					contractData.setOcupationId(ocupacionRecord.getId());
				}
			}else{
				if(null == contractData.getOcupation()){
					if(contractData.hasPayroll()){
						java.util.Date auxDate = DateUtils.copyDateOnly(contractData.getPayrollDate());
						auxDate.setDate(auxDate.getDate()+1);
						Date auxStartDate = new Date(auxDate.getTime()); //Fecha inicio nuevo tramo grupo cotizacion
						
						Record auxRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getOcupationId())).fetchOne();
						if(auxStartDate.equals(auxRecord.get(CONTRACT_DATA.START_DATE))){
							dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getOcupationId())).execute();
							contractData.setOcupationId(null);
							contractData.setOcupation(null);
						}else{
							Date auxEndDate = new Date(contractData.getPayrollDate().getTime()); //Fecha fin antigui ocupacion
							dslContext.update(CONTRACT_DATA).set(CONTRACT_DATA.END_DATE, auxEndDate).where(CONTRACT_DATA.ID.eq(contractData.getOcupationId())).execute();
							contractData.setOcupationId(null);
							contractData.setOcupation(null);
						}
					}else{
						dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getOcupationId())).execute();
						contractData.setOcupationId(null);
						contractData.setOcupation(null);
					}	
				}else{
					if(contractData.hasPayroll()){
						Date newEndDate = endDate; //Fecha fin contrato
						Date auxEndDate = new Date(contractData.getPayrollDate().getTime()); //Fecha fin antigui grupo cotizacion
						java.util.Date auxDate = DateUtils.copyDateOnly(contractData.getPayrollDate());
						auxDate.setDate(auxDate.getDate()+1);
//						DateUtils.addDays(auxDate, 1);
						Date auxStartDate = new Date(auxDate.getTime()); //Fecha inicio nuevo tramo grupo cotizacion
						
						Record auxRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getOcupationId())).fetchOne();
						if(auxStartDate.equals(auxRecord.get(CONTRACT_DATA.START_DATE))){
							dslContext.update(CONTRACT_DATA)
							.set(CONTRACT_DATA.EXPRESSION, "\""+contractData.getOcupation()+"\"")
							.where(CONTRACT_DATA.ID.eq(contractData.getOcupationId()))
							.execute();
						}else{
							dslContext.update(CONTRACT_DATA)
								.set(CONTRACT_DATA.END_DATE, auxEndDate)
								.where(CONTRACT_DATA.ID.eq(contractData.getOcupationId()))
								.execute();
							
							ContractDataRecord contizacionRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
									CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
								.values(null, domain, "OCUPACION", contractData.getContractId(), "\""+ contractData.getOcupation()+"\"", 
										auxStartDate, newEndDate)
								.returning(CONTRACT_DATA.ID)
								.fetchOne();
							
							contractData.setQuotegroupId(contizacionRecord.getId());
						}
					}else
						dslContext.update(CONTRACT_DATA)
							.set(CONTRACT_DATA.EXPRESSION, "\""+ contractData.getOcupation()+"\"")
							.set(CONTRACT_DATA.START_DATE, startDate)
							.set(CONTRACT_DATA.END_DATE, endDate)
							.where(CONTRACT_DATA.ID.eq(contractData.getOcupationId()))
							.execute();
				}
			}
			
			if(null == contractData.getContractmodelId()){
				if(null != contractData.getContractModel()){
					ContractInfoRecord contractInfoRecord = dslContext.insertInto(CONTRACT_INFO, CONTRACT_INFO.ID, CONTRACT_INFO.DOMAIN, CONTRACT_INFO.CONTRACT, CONTRACT_INFO.NAME, CONTRACT_INFO.EXPRESSION, 
							CONTRACT_INFO.START_DATE, CONTRACT_INFO.END_DATE, CONTRACT_INFO.CREATION_USER, CONTRACT_INFO.CREATION_DATE, CONTRACT_INFO.MODIFICATION_USER,
							CONTRACT_INFO.MODIFICATION_DATE)
						.values(contractData.getContractmodelId(), domain, contractData.getContractId(), "OPCION_CONTRATO", "\""+ ModelOption.values()[contractData.getContractModel()].toString()+ "\"", 
								startDate, endDate, (String) null, null, (String) null, null)
						.returning(CONTRACT_INFO.ID)
						.fetchOne();
					
					contractData.setContractmodelId(contractInfoRecord.getId());
				}
			}else{
				if(null == contractData.getContractModel()){
					dslContext.delete(CONTRACT_INFO)
						.where(CONTRACT_INFO.ID.eq(contractData.getContractmodelId()))
						.execute();
				}else{
					dslContext.update(CONTRACT_INFO)
						.set(CONTRACT_INFO.EXPRESSION, "\""+ ModelOption.values()[contractData.getContractModel()].toString()+"\"")
						.set(CONTRACT_INFO.START_DATE, startDate)
						.set(CONTRACT_INFO.END_DATE, endDate)
						.where(CONTRACT_INFO.ID.eq(contractData.getContractmodelId()))
						.execute();
				}
			}
			
			
			if(null != contractData.getJourneytypeId())
				dslContext.delete(CONTRACT_DATA)
					.where(CONTRACT_DATA.ID.eq(contractData.getJourneytypeId()))
					.execute();
			
			if(null != contractData.getRetaId())
				dslContext.delete(CONTRACT_INFO)
				.where(CONTRACT_INFO.ID.eq(contractData.getRetaId()))
				.execute();
		
		}else {
			
			dslContext.update(CONTRACT)
			.set(CONTRACT.ENTERPRISE_CCC, (Integer) null)
			.set(CONTRACT.ENTERPRISE_ACTIVITY, (Integer) null)
			.set(CONTRACT.SS_REGIME, (byte) 3)
			.where(CONTRACT.ID.eq(contractData.getContractId()))
			.execute();
			
			if(null == contractData.getJourneytypeId()){
				if(null != contractData.getJourneyType()){
					ContractDataRecord journeyRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
							CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
						.values(contractData.getJourneytypeId(), domain, "TIEMPO_COMPLETO", contractData.getContractId(), (contractData.getJourneyType() == 0) ? "FALSE" : "TRUE", 
								startDate, endDate)
						.returning(CONTRACT_DATA.ID)
						.fetchOne();
					
					contractData.setJourneytypeId(journeyRecord.getId());
				}
			}else{
				if(null == contractData.getJourneyType()){
					dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getJourneytypeId())).execute();
					contractData.setJourneytypeId(null);
					contractData.setJourneyType(null);
				}else{
					dslContext.update(CONTRACT_DATA)
						.set(CONTRACT_DATA.EXPRESSION, (contractData.getJourneyType() == 0) ? "FALSE" : "TRUE")
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.where(CONTRACT_DATA.ID.eq(contractData.getJourneytypeId()))
						.execute();
				}
			}
			
			if(null == contractData.getRetaId()){
				ContractInfoRecord retaRecord = dslContext.insertInto(CONTRACT_INFO, CONTRACT_INFO.ID, CONTRACT_INFO.DOMAIN, CONTRACT_INFO.CONTRACT, CONTRACT_INFO.NAME, CONTRACT_INFO.EXPRESSION, 
						CONTRACT_INFO.START_DATE, CONTRACT_INFO.END_DATE, CONTRACT_INFO.CREATION_USER, CONTRACT_INFO.CREATION_DATE, CONTRACT_INFO.MODIFICATION_USER,
						CONTRACT_INFO.MODIFICATION_DATE)
					.values(contractData.getRetaId(), domain, contractData.getContractId(), "RETA", "TRUE", 
							startDate, endDate, (String) null, null, (String) null, null)
					.returning(CONTRACT_INFO.ID)
					.fetchOne();
				
				contractData.setRetaId(retaRecord.getId());
			}else{
				dslContext.update(CONTRACT_INFO)
				.set(CONTRACT_INFO.EXPRESSION, "TRUE")
				.set(CONTRACT_INFO.START_DATE, startDate)
				.set(CONTRACT_INFO.END_DATE, endDate)
				.where(CONTRACT_INFO.ID.eq(contractData.getRetaId()))
				.execute();
				
			}
			
			if(null != contractData.getContracttypeId()){
				dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.ID.eq(contractData.getContracttypeId()))
				.execute();
				
				contractData.setContracttypeId(null);
				contractData.setContractType(null);
			}
			
			if(null != contractData.getQuotegroupId()){
				dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.ID.eq(contractData.getQuotegroupId()))
				.execute();
			
				contractData.setQuotegroupId(null);
				contractData.setQuoteGroup(null);
			}
			
			if(null != contractData.getOcupationId()){
				dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.ID.eq(contractData.getOcupationId()))
				.execute();
			
				contractData.setOcupationId(null);
				contractData.setOcupation(null);
			}
			
			if(null != contractData.getContractmodelId()){
				dslContext.delete(CONTRACT_INFO)
				.where(CONTRACT_INFO.ID.eq(contractData.getContractmodelId()))
				.execute();
			
				contractData.setContractmodelId(null);
				contractData.setContractModel(null);
			}
		}
		
		TreeMap<java.util.Date, ArrayList<JourneyDuration>> contractJourneyDuration = contractData.getContractJourneyDuration().getContractJourneyDuration();
		 if(!contractJourneyDuration.isEmpty()){
		 
			//ACTUALIZAR DURACION JORNADA
			dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.like("HORAS%"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractData.getContractId()))
				.execute();
			 
			 for(Entry<java.util.Date, ArrayList<JourneyDuration>> entry : contractJourneyDuration.entrySet()) {
				 for(JourneyDuration journey : entry.getValue()) {
					 if(null != journey.getExpression())
						 dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
									CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
								.values(domain, journey.getName(), contractData.getContractId(), journey.getExpression(), 
										new Date(journey.getStartDate().getTime()), (null == journey.getEndDate()) ? null : new Date(journey.getEndDate().getTime()))
								.execute();
				 }
			 }
		 }else{
			//ACTUALIZAR DURACION JORNADA
			dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.like("HORAS%"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractData.getContractId()))
				.execute();
		 }

		//ACTUALIZAR FECHA INICIO Y FIN: contract, contract_data, contract_info, contract_bonus, contract_deduction, contract_embargo,
		// contract_leave, contract_payment
		
		dslContext.update(CONTRACT)
			.set(CONTRACT.START_DATE,  startDate)
			.where(CONTRACT.ID.eq(contractData.getContractId()))
			.and(CONTRACT.START_DATE.eq(oldStartDate))
			.execute();
		
		dslContext.update(CONTRACT)
			.set(CONTRACT.END_DATE, endDate)
			.where(CONTRACT.ID.eq(contractData.getContractId()))
			.and(CONTRACT.END_DATE.eq(oldEndDate))
			.execute();
		
		dslContext.update(CONTRACT_DATA)
			.set(CONTRACT_DATA.START_DATE,  startDate)
			.where(CONTRACT_DATA.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_DATA.START_DATE.eq(oldStartDate))
			.execute();
	
		dslContext.update(CONTRACT_DATA)
			.set(CONTRACT_DATA.END_DATE, endDate)
			.where(CONTRACT_DATA.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_DATA.END_DATE.eq(oldEndDate))
			.execute();
		
		dslContext.update(CONTRACT_INFO)
			.set(CONTRACT_INFO.START_DATE,  startDate)
			.where(CONTRACT_INFO.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_INFO.START_DATE.eq(oldStartDate))
			.execute();

		dslContext.update(CONTRACT_INFO)
			.set(CONTRACT_INFO.END_DATE, endDate)
			.where(CONTRACT_INFO.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_INFO.END_DATE.eq(oldEndDate))
			.execute();

		dslContext.update(CONTRACT_BONUS)
			.set(CONTRACT_BONUS.START_DATE,  startDate)
			.where(CONTRACT_BONUS.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_BONUS.START_DATE.eq(oldStartDate))
			.execute();

		dslContext.update(CONTRACT_BONUS)
			.set(CONTRACT_BONUS.END_DATE, endDate)
			.where(CONTRACT_BONUS.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_BONUS.END_DATE.eq(oldEndDate))
			.execute();

		dslContext.update(CONTRACT_DEDUCTION)
			.set(CONTRACT_DEDUCTION.START_DATE,  startDate)
			.where(CONTRACT_DEDUCTION.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_DEDUCTION.START_DATE.eq(oldStartDate))
			.execute();

		dslContext.update(CONTRACT_DEDUCTION)
			.set(CONTRACT_DEDUCTION.END_DATE, endDate)
			.where(CONTRACT_DEDUCTION.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_DEDUCTION.END_DATE.eq(oldEndDate))
			.execute();

		dslContext.update(CONTRACT_EMBARGO)
			.set(CONTRACT_EMBARGO.START_DATE,  startDate)
			.where(CONTRACT_EMBARGO.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_EMBARGO.START_DATE.eq(oldStartDate))
			.execute();

		dslContext.update(CONTRACT_EMBARGO)
			.set(CONTRACT_EMBARGO.END_DATE, endDate)
			.where(CONTRACT_EMBARGO.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_EMBARGO.END_DATE.eq(oldEndDate))
			.execute();

		dslContext.update(CONTRACT_LEAVE)
			.set(CONTRACT_LEAVE.START_DATE,  startDate)
			.where(CONTRACT_LEAVE.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_LEAVE.START_DATE.eq(oldStartDate))
			.execute();

		dslContext.update(CONTRACT_LEAVE)
			.set(CONTRACT_LEAVE.END_DATE, endDate)
			.where(CONTRACT_LEAVE.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_LEAVE.END_DATE.eq(oldEndDate))
			.execute();

		dslContext.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.START_DATE,  startDate)
			.where(CONTRACT_PAYMENT.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_PAYMENT.START_DATE.eq(oldStartDate))
			.execute();

		dslContext.update(CONTRACT_PAYMENT)
			.set(CONTRACT_PAYMENT.END_DATE, endDate)
			.where(CONTRACT_PAYMENT.CONTRACT.eq(contractData.getContractId()))
			.and(CONTRACT_PAYMENT.END_DATE.eq(oldEndDate))
			.execute();
				
		employeeContractInfo.setEmployeeInfo(employeeData);
		return employeeContractInfo;
	}

	private static byte getType(String typePayMethod) {
		switch (typePayMethod) {
		case "EFECTIVO":
			return (byte) 0;
		case "GIRO":
			return (byte) 1;
		case "CHEQUE":
			return (byte) 4;
		case "TRANSFERENCIA":
			return (byte) 5;
		default:
			return (byte) -1;
		}
	}

	private static EmployeeContractInfo createEmployeeContractDB(DSLContext dslContext, EmployeeContractInfo employeeContractData) {
		System.out.println("GUARDANDO EN DB ...");
		
		ContractInfo contractData = employeeContractData.getContractInfo();
		EmployeeInfo employeeData = employeeContractData.getEmployeeInfo();
		
//		System.out.println(contractData.toString());
//		System.out.println(employeeData.toString());
		
		// ------------------------------------------------------------------------------------------------------------------------
		// ------------------------------------------------ EMPLOYEE INFO ---------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------------------------		
		
		Record workplaceRecord = dslContext.select()
									.from(WORKPLACE)
									.where(WORKPLACE.ID.eq(contractData.getWorkplaceId()))
									.fetchOne();
		
		Integer domain = workplaceRecord.get(WORKPLACE.DOMAIN);
		Integer registryId = 0;
		
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.eq(domain))
				.fetchOne();
		
		Integer parentDomain = domainRecord.get(DOMAIN.PARENT);
		
		if(employeeData.getEmployeeId() == null){ //NUEVO EMPLEADO
			
			RegistryRecord registryRecord = dslContext.insertInto(REGISTRY)
				.set(REGISTRY.DOMAIN, domain)
				.set(REGISTRY.DOCUMENT, employeeData.getDocument())
				.set(REGISTRY.DOCUMENT_TYPE, null == employeeData.getDocumentType() ? (byte) 0 : employeeData.getDocumentType())
				.set(REGISTRY.DOCUMENT_COUNTRY, null == employeeData.getNationality() ? "ES" : employeeData.getNationality())
				.set(REGISTRY.NATIONALITY, null == employeeData.getNationality() ? "ES" : employeeData.getNationality())
				.set(REGISTRY.NAME, (null == employeeData.getSurName() ? "" :  employeeData.getSurName() + " ") + 
									(null == employeeData.getSecondSurName() ? "" : employeeData.getSecondSurName() + ", ") + 
									employeeData.getName())
				.returning(REGISTRY.ID)
				.fetchOne();
			
			registryId = registryRecord.getId();
			
			dslContext.insertInto(PERSON)
				.set(PERSON.REGISTRY, registryId)
				.set(PERSON.DOMAIN, domain)
				.set(PERSON.BIRTH_DATE, (employeeData.getBirthdate() == null) ? null : new Date(employeeData.getBirthdate().getTime()))
				.set(PERSON.GENDER, employeeData.getGender())
				.set(PERSON.SOCIAL_SECURITY_NUM, employeeData.getSsNumber())
				.set(PERSON.NAME, employeeData.getName())
				.set(PERSON.FIRST_SURNAME, null == employeeData.getSurName() ? "" : employeeData.getSurName())
				.set(PERSON.SECOND_SURNAME, null == employeeData.getSecondSurName() ? "" : employeeData.getSecondSurName())
				.execute();
			
			Integer rAddressId = null;
			
			if(null != employeeData.getAddressProvinces()) {
			
				Result<Record> geozone = dslContext.select()
						.from(GEOZONE)
						.where(GEOZONE.NAME.eq(employeeData.getAddressProvinces()))
							.and(GEOZONE.DOMAIN.eq(domain)
									.or(GEOZONE.DOMAIN.eq(parentDomain)))
						.fetch();
				
				Integer geozoneId = null;
				
				if(geozone == null){
					Result<Record1<String>> codes = dslContext.select(GEOZONE.CODE)
						.from(GEOZONE)
						.where(GEOZONE.NAME.like(employeeData.getAddressProvinces()+"%"))
						.fetch();
					
					if(!codes.isEmpty()){
						GeozoneRecord geozoneRecord  = dslContext.insertInto(GEOZONE)
								.set(GEOZONE.DOMAIN, domain)
								.set(GEOZONE.NAME, employeeData.getAddressProvinces())
								.set(GEOZONE.CODE, codes.get(0).value1())
								.returning(GEOZONE.ID)
								.fetchOne();
							
							geozoneId = geozoneRecord.getId();
					}
				}else
					geozoneId = geozone.get(0).get(GEOZONE.ID);
				
				RaddressRecord rAddressRecord = dslContext.insertInto(RADDRESS)
					.set(RADDRESS.DOMAIN, domain)
					.set(RADDRESS.REGISTRY, registryId)
					.set(RADDRESS.STREET_TYPE, employeeData.getStreetType())
					.set(RADDRESS.ADDRESS, employeeData.getAddress())
					.set(RADDRESS.NUMBER, employeeData.getAddresNum())
					.set(RADDRESS.ZIP, employeeData.getAddressZip())
					.set(RADDRESS.CITY, employeeData.getAddressCity())
					.set(RADDRESS.GEOZONE, geozoneId)
					.returning(RADDRESS.ID, RADDRESS.GEOZONE)
					.fetchOne();
				
				rAddressId = rAddressRecord.getId();
				
				if(geozone == null && null != geozoneId){
					Integer rAddressGeozone = rAddressRecord.getGeozone();
					
					GeozoneRecord geozoneParentRecord  = dslContext.insertInto(GEOZONE)
							.set(GEOZONE.DOMAIN, domain)
							.set(GEOZONE.NAME, "ESPA�A")
							.set(GEOZONE.CODE, "ES")
							.set(GEOZONE.SYSTEM, (byte) 1)
							.returning(GEOZONE.ID)
							.fetchOne();
					
					Integer geozoneParentId = geozoneParentRecord.getId();
					
					dslContext.insertInto(GEOTREE)
					.set(GEOTREE.DOMAIN, domain)
					.set(GEOTREE.PARENT, geozoneParentId)
					.set(GEOTREE.CHILD, rAddressGeozone)
					.execute();
					
					dslContext.insertInto(GEOTREE)
					.set(GEOTREE.DOMAIN, domain)
					.set(GEOTREE.PARENT, (Integer) null)
					.set(GEOTREE.CHILD, geozoneParentId)
					.execute();
				}
			}
			
			if(null != employeeData.getPhone())
				dslContext.insertInto(RMEDIA)
					.set(RMEDIA.DOMAIN, domain)
					.set(RMEDIA.REGISTRY, registryId)
					.set(RMEDIA.MEDIA, (byte) 1)
					.set(RMEDIA.VALUE, employeeData.getPhone())
					.set(RMEDIA.RADDRESS, rAddressId)
					.execute();
			
			if(null != employeeData.getMobile())
				dslContext.insertInto(RMEDIA)
					.set(RMEDIA.DOMAIN, domain)
					.set(RMEDIA.REGISTRY, registryId)
					.set(RMEDIA.MEDIA, (byte) 2)
					.set(RMEDIA.VALUE, employeeData.getMobile())
					.set(RMEDIA.RADDRESS, rAddressId)
					.execute();
			
			if(null != employeeData.getEmail())
				dslContext.insertInto(RMEDIA)
					.set(RMEDIA.DOMAIN, domain)
					.set(RMEDIA.REGISTRY, registryId)
					.set(RMEDIA.MEDIA, (byte) 4)
					.set(RMEDIA.VALUE, employeeData.getEmail())
					.set(RMEDIA.RADDRESS, rAddressId)
					.execute();
			
			if(employeeData.getPayMethodType() != null && employeeData.getPayMethodType() != ""){
				byte typePayMethod = getType(employeeData.getPayMethodType());
				if(-1 != typePayMethod){
					PayMethodRecord payMethodRecord = dslContext.insertInto(PAY_METHOD)
							.set(PAY_METHOD.DOMAIN, domain)
							.set(PAY_METHOD.NAME, employeeData.getPayMethodType())
							.set(PAY_METHOD.TYPE, typePayMethod)
							.returning(PAY_METHOD.ID)
							.fetchOne();
					
					Integer payMethodTableId = payMethodRecord.get(PAY_METHOD.ID);
					Integer rbankTableId = null;
					if(employeeData.getAccount() != null && employeeData.getAccount() != ""){
						RbankRecord rbankRecord = dslContext.insertInto(RBANK)
								.set(RBANK.DOMAIN, domain)
								.set(RBANK.REGISTRY, registryId)
								.set(RBANK.BANK_ACCOUNT, employeeData.getAccount())
								.set(RBANK.BIC, employeeData.getBic())
								.set(RBANK.ALIAS, "CUENTA")
								.set(RBANK.ACTIVE, (byte) 1)
								.returning(RBANK.ID)
								.fetchOne();
						 
						 rbankTableId = rbankRecord.get(RBANK.ID); 
					}
					
					dslContext.insertInto(RPAYMETHOD)
						.set(RPAYMETHOD.DOMAIN, domain)
						.set(RPAYMETHOD.REGISTRY, registryId)
						.set(RPAYMETHOD.PAY_METHOD, payMethodTableId)
						.set(RPAYMETHOD.RBANK, rbankTableId)
						.execute();
				}
			}
			
		}else{ //EMPLEADO YA EXISTENTE
			
			registryId = employeeData.getEmployeeId();
				
		}
		
		// ------------------------------------------------------------------------------------------------------------------------
		// ------------------------------------------------ CONTRACT INFO ---------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------------------------
		
		ContractRecord contractRecord = dslContext.insertInto(CONTRACT)
			.set(CONTRACT.DOMAIN, domain)
			.set(CONTRACT.PERSON, registryId)
			.set(CONTRACT.WORKPLACE, contractData.getWorkplaceId())
			.set(CONTRACT.START_DATE, new Date(contractData.getStartDate().getTime()))
			.set(CONTRACT.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
			.set(CONTRACT.SENIORITY_DATE, (contractData.getSeniorityDate() == null) ? new Date(contractData.getStartDate().getTime()) : new Date(contractData.getSeniorityDate().getTime()))
			.set(CONTRACT.CATEGORY_DESCRIPTION, (null == contractData.getAgreementCategory() || "" == contractData.getAgreementCategory()) ? null : contractData.getAgreementCategory())
			.set(CONTRACT.AGREEMENT_LEVEL, contractData.getAgreementLevelId())
			.returning(CONTRACT.ID)
			.fetchOne();
		
		Integer contractId = contractRecord.getId();
		
		if(contractData.getSsRegimen() != 3){ //NO ES RETA
			
			if(null != contractData.getActivityId())
				dslContext.update(CONTRACT)
				.set(CONTRACT.ENTERPRISE_CCC, contractData.getCccId())
				.set(CONTRACT.ENTERPRISE_ACTIVITY, contractData.getActivityId())
				.where(CONTRACT.ID.eq(contractId))
				.execute();
		
			if(null != contractData.getContractType())
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "TC2")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, (contractData.getContractType() == null) ? (String) null : "\""+contractData.getContractType()+"\"")
					.set(CONTRACT_DATA.START_DATE, new Date(contractData.getStartDate().getTime()))
					.set(CONTRACT_DATA.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
					.execute();
		
			/*ModelOption.values()[employeeContractData.getContract_model()].toString()*/
			if(null != contractData.getContractModel())
				dslContext.insertInto(CONTRACT_INFO)
					.set(CONTRACT_INFO.DOMAIN, domain)
					.set(CONTRACT_INFO.NAME, "OPCION_CONTRATO")
					.set(CONTRACT_INFO.CONTRACT, contractId)
					.set(CONTRACT_INFO.EXPRESSION, (contractData.getContractModel() == null) ? (String) null : "\""+ ModelOption.values()[contractData.getContractModel()].toString() +"\"")
					.set(CONTRACT_INFO.START_DATE, new Date(contractData.getStartDate().getTime()))
					.set(CONTRACT_INFO.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
					.execute();
		
			if(null != contractData.getQuoteGroup())
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "GRUPO_COTIZACION")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, (contractData.getQuoteGroup() == null) ? (String) null : "\""+ contractData.getQuoteGroup() +"\"")
					.set(CONTRACT_DATA.START_DATE, new Date(contractData.getStartDate().getTime()))
					.set(CONTRACT_DATA.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
					.execute();
		
			if(null != contractData.getOcupation())
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "OCUPACION")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, (contractData.getOcupation() == null) ? (String) null : "\""+ contractData.getOcupation() +"\"")
					.set(CONTRACT_DATA.START_DATE, new Date(contractData.getStartDate().getTime()))
					.set(CONTRACT_DATA.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
					.execute();	
				
		}else{//ES RETA
		
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "TIEMPO_COMPLETO")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, (contractData.getJourneyType() == 0) ? "FALSE" : "TRUE")
				.set(CONTRACT_DATA.START_DATE, new Date(contractData.getStartDate().getTime()))
				.set(CONTRACT_DATA.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
				.execute();
			
			dslContext.insertInto(CONTRACT_INFO)
				.set(CONTRACT_INFO.DOMAIN, domain)
				.set(CONTRACT_INFO.CONTRACT, contractId)
				.set(CONTRACT_INFO.NAME, "RETA")
				.set(CONTRACT_INFO.EXPRESSION, "true")
				.set(CONTRACT_INFO.START_DATE, new Date(contractData.getStartDate().getTime()))
				.set(CONTRACT_INFO.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
				.execute();
			
			dslContext.update(CONTRACT)
				.set(CONTRACT.ENTERPRISE_CCC, (Integer) null)
				.set(CONTRACT.ENTERPRISE_ACTIVITY, (Integer) null)
				.set(CONTRACT.SS_REGIME, (byte) 3)
				.where(CONTRACT.ID.eq(contractId))
				.execute();
			
		}
		
		dslContext.insertInto(CONTRACT_INFO)
			.set(CONTRACT_INFO.DOMAIN, domain)
			.set(CONTRACT_INFO.CONTRACT, contractId)
			.set(CONTRACT_INFO.NAME, "SEPE_CONTRATO")
			.set(CONTRACT_INFO.EXPRESSION, "PENDING")
			.set(CONTRACT_INFO.START_DATE, new Date(contractData.getStartDate().getTime()))
			.set(CONTRACT_INFO.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
			.set(CONTRACT_INFO.CREATION_USER, "admin")
			.set(CONTRACT_INFO.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
			.execute();
		
		dslContext.insertInto(CONTRACT_INFO)
			.set(CONTRACT_INFO.DOMAIN, domain)
			.set(CONTRACT_INFO.CONTRACT, contractId)
			.set(CONTRACT_INFO.NAME, "SS_ALTA")
			.set(CONTRACT_INFO.EXPRESSION, "PENDING")
			.set(CONTRACT_INFO.START_DATE, new Date(contractData.getStartDate().getTime()))
			.set(CONTRACT_INFO.END_DATE, (contractData.getEndDate() == null) ? null : new Date(contractData.getEndDate().getTime()))
			.set(CONTRACT_INFO.CREATION_USER, "admin")
			.set(CONTRACT_INFO.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
			.execute();
		
		//ACTUALIZAR DURACION JORNADA
		TreeMap<java.util.Date, ArrayList<JourneyDuration>> contractJourneyDuration = contractData.getContractJourneyDuration().getContractJourneyDuration();
		if(null != contractJourneyDuration)
			for(Entry<java.util.Date, ArrayList<JourneyDuration>> entry : contractJourneyDuration.entrySet()) {
				 for(JourneyDuration journey : entry.getValue()) {
					 dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
								CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
							.values(domain, journey.getName(), contractId, journey.getExpression(), 
									new Date(journey.getStartDate().getTime()), (null == journey.getEndDate()) ? null : new Date(journey.getEndDate().getTime()))
							.execute();
				 }
			}
		
		return null;
	}

}
