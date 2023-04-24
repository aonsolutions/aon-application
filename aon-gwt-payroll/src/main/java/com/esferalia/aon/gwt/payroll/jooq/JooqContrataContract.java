package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Cno.CNO;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.PayMethod.PAY_METHOD;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Raddress.RADDRESS;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Rmedia.RMEDIA;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.file.payroll.contract.pdf.ModelOption;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Attach;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractSalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractSpecificData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;

public class JooqContrataContract {
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	
	
	// ---------------------------------------------------- Constructor
	
	private JooqContrataContract() {
		super();
	}
	
	// ---------------------------------------------------- Settings

	private static Settings settings = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}

	
	// ---------------------------------------------------- Contract
	
	public static List<EmployeeContractInfo> getAllEmployeesInfo(Connection conn, Integer domainId, Boolean allEmployees) {
		return getAllEmployeesInfoDB(DSL.using(conn, getDefaultSettings()), domainId, allEmployees);
	}

	private static List<EmployeeContractInfo> getAllEmployeesInfoDB(DSLContext dslContext, Integer domainId, Boolean allEmployees) {
		List<EmployeeContractInfo> employeesInfo = new ArrayList<>();
		
		List<Integer> allContractIds = null;
		
		if(Boolean.TRUE.equals(allEmployees)) {
			// ------------------------------------------------ Get all contracts from domainId
			allContractIds = dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.DOMAIN.eq(domainId))
					.and(CONTRACT.ID.gt(0))
					.fetch(CONTRACT.ID);
		} else {
			// ------------------------------------------------ Get active contracts from domainId or ends in the last two months
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.MONTH, -1);
			
			Date contractEndDate = new Date(cal.getTimeInMillis());
			
			allContractIds = dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.DOMAIN.eq(domainId))
					.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(contractEndDate)))
					.and(CONTRACT.ID.gt(0))
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
			
			Integer employeeRegistry = personTable.get(PERSON.REGISTRY);
			
			// REGISTRY TABLE
			Record registryTable = dslContext.select().from(REGISTRY)
					.where(REGISTRY.ID.eq(employeeRegistry))
					.fetchOne();
			
			employeeData.setDocument(registryTable.get(REGISTRY.DOCUMENT));
			employeeData.setDocumentType(registryTable.get(REGISTRY.DOCUMENT_TYPE)); //Esto lo saco con el formato del documento, se podria obviar?
			employeeData.setNationality(registryTable.get(REGISTRY.NATIONALITY));
			
			// RADDRESS AND GEOZONE TABLE
			Record raddressTable = dslContext.select().from(RADDRESS)
					.where(RADDRESS.REGISTRY.eq(employeeRegistry))
					.fetchOne();
			
			if(null != raddressTable) {
			
				employeeData.setRaddressId(raddressTable.get(RADDRESS.ID));
				employeeData.setStreetType(raddressTable.get(RADDRESS.STREET_TYPE));
				employeeData.setAddress(raddressTable.get(RADDRESS.ADDRESS));
				employeeData.setAddressInfo(raddressTable.get(RADDRESS.ADDRESS2));
				employeeData.setAddresNum(raddressTable.get(RADDRESS.NUMBER));
				employeeData.setAddressZip(raddressTable.get(RADDRESS.ZIP));
				employeeData.setAddressCity(raddressTable.get(RADDRESS.MUNICIPALITY_CODE));
				employeeData.setAddressProvinces(raddressTable.get(RADDRESS.GEOZONE));
			}
			
			// RMEDIA TABLE
			Result<Record> rmediaRecords = dslContext.select().from(RMEDIA)
					.where(RMEDIA.REGISTRY.eq(employeeRegistry))
					.fetch();
			
			for(Record rmediaRecord : rmediaRecords){
				if(rmediaRecord.get(RMEDIA.MEDIA) == 1){
					employeeData.setPhoneId(rmediaRecord.get(RMEDIA.ID));
					employeeData.setPhone(rmediaRecord.get(RMEDIA.VALUE));
				}else if(rmediaRecord.get(RMEDIA.MEDIA) == 2){
					employeeData.setMobileId(rmediaRecord.get(RMEDIA.ID));
					employeeData.setMobile(rmediaRecord.get(RMEDIA.VALUE));
				}else if(rmediaRecord.get(RMEDIA.MEDIA) == 4){
					employeeData.setEmailId(rmediaRecord.get(RMEDIA.ID));
					employeeData.setEmail(rmediaRecord.get(RMEDIA.VALUE));
				}
			}
			
			// FIND RPAYMETHOD
			Record rPayMethodRecord = dslContext.select().from(RPAYMETHOD)
				.where(RPAYMETHOD.REGISTRY.eq(employeeRegistry))
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
				employeeData.addRbank(r.get(RBANK.ID), r.get(RBANK.BANK_ACCOUNT), r.get(RBANK.BIC), r.get(RBANK.ALIAS));
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
			
			Integer employeeWorkplaceId = contractTable.get(CONTRACT.WORKPLACE);
			
			// WORKPLACE TABLE		
			contractData.setWorkplaceId(employeeWorkplaceId);
			
			Record workplaceRecord = dslContext.select().from(WORKPLACE).where(WORKPLACE.ID.eq(employeeWorkplaceId)).fetchOne();
			
			contractData.setWorkplaceName(workplaceRecord.get(WORKPLACE.DESCRIPTION));
			
			Record raddressRecord = dslContext.select().from(RADDRESS)
					.where(RADDRESS.ID.eq(
							dslContext.select(WORKPLACE.ADDRESS).from(WORKPLACE)
								.where(WORKPLACE.ID.eq(employeeWorkplaceId))
								.fetchOne(WORKPLACE.ADDRESS)
					)).fetchOne();
			
			contractData.setWorkplaceZIP(raddressRecord.get(RADDRESS.MUNICIPALITY_CODE));
			contractData.setWorkplaceFullAddress(raddressRecord.get(RADDRESS.STREET_TYPE)+". "+raddressRecord.get(RADDRESS.ADDRESS)+" "+raddressRecord.get(RADDRESS.NUMBER));
			
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
					
					Record enterpriseRecord = dslContext.select().from(REGISTRY)
							.where(REGISTRY.ID.eq(enterpriseActivityTable.get(ENTERPRISE_ACTIVITY.ENTERPRISE)))
							.fetchOne();
					
					contractData.setEnterpriseCIF(enterpriseRecord.get(REGISTRY.DOCUMENT));
					contractData.setEnterpriseName(enterpriseRecord.get(REGISTRY.NAME));
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
					
					contractData.setCompleteCCC(getCCCRegimeCode(enterpriseCCCTable.get(ENTERPRISE_CCC.TYPE))+enterpriseCCCTable.get(ENTERPRISE_CCC.CCC));
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
					contractData.setAgreementColective(agreementTable.get(AGREEMENT.SS_NUMBER));
				} catch ( Exception t ) {
					contractData.setAgreementLevelId(null);
					contractData.setAgreementId(null);
				}
				
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
			Date currentDate = new Date(new java.util.Date().getTime());
			Result<Record> contractDataTable = null;
			
			if(null != contractData.getEndDate()) { //Para contratos finalizados
				if(currentDate.after(contractData.getEndDate())) {
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(contractId))
						.orderBy(CONTRACT_DATA.START_DATE)
						.fetch();
				}else {
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
							.where(CONTRACT_DATA.CONTRACT.eq(contractId))
							.and(CONTRACT_DATA.END_DATE.ge(currentDate).or(CONTRACT_DATA.END_DATE.isNull()))
							.fetch();
					
					if(contractDataTable.isEmpty())
						contractDataTable = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(contractId))
						.orderBy(CONTRACT_DATA.ID)
						.fetch();
				}
			}else {
				
				if(contractData.getStartDate().after(currentDate)) {
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
							.where(CONTRACT_DATA.CONTRACT.eq(contractId))
							.and(CONTRACT_DATA.START_DATE.le(new Date(contractData.getStartDate().getTime())))
							.and(CONTRACT_DATA.END_DATE.ge(new Date(contractData.getStartDate().getTime())).or(CONTRACT_DATA.END_DATE.isNull()))
							.fetch();
				}else
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
							.where(CONTRACT_DATA.CONTRACT.eq(contractId))
							.and(CONTRACT_DATA.END_DATE.isNull().or(CONTRACT_DATA.END_DATE.ge(currentDate)))
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
				}
				else if(r.get(CONTRACT_DATA.NAME).equals("GRUPO_COTIZACION")) {
					contractData.setQuotegroupId(r.get(CONTRACT_DATA.ID));
					contractData.setQuoteGroup(r.get(CONTRACT_DATA.EXPRESSION));
				}else if(r.get(CONTRACT_DATA.NAME).equals("OCUPACION")) {
					contractData.setOcupationId(r.get(CONTRACT_DATA.ID));
					contractData.setOcupation(r.get(CONTRACT_DATA.EXPRESSION));
				}else if(r.get(CONTRACT_DATA.NAME).equals("RLCE")) {
					contractData.setRlceId(r.get(CONTRACT_DATA.ID));
					contractData.setRlce(r.get(CONTRACT_DATA.EXPRESSION));
				}else if(r.get(CONTRACT_DATA.NAME).equals("TIEMPO_COMPLETO")) {
					contractData.setJourneytypeId(r.get(CONTRACT_DATA.ID));
					contractData.setJourneyType(r.get(CONTRACT_DATA.EXPRESSION).equalsIgnoreCase("TRUE") ? (byte) 0 : (byte) 1);
				}else if(r.get(CONTRACT_DATA.NAME).equals("COEFICIENTE_PARCIALIDAD")) {
					String expression = r.get(CONTRACT_DATA.EXPRESSION);
					if(null != expression && expression.contains("\""))
						expression = expression.split("\"")[1];
					
					Double partialityCoef = Double.parseDouble(expression);
					contractData.setPartialityCoefId(r.get(CONTRACT_DATA.ID));
					contractData.setPartialityCoef(partialityCoef);
				} else if(r.get(CONTRACT_DATA.NAME).equals("MODELO_COTIZACION_AGRARIO")) {
					contractData.setMdctzId(r.get(CONTRACT_DATA.ID));
					contractData.setMdctz(r.get(CONTRACT_DATA.EXPRESSION));
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
			
			employeeContractInfo.setEmployeeInfo(employeeData);
			employeeContractInfo.setContractInfo(contractData);
			
			// ------------------------------------------- CONTRACT SPECIFIC DATA -----------------------------------------------------
			
			ContractSpecificData contractSpecificData = new ContractSpecificData();
			
			// Get CNO
			Result<Record> cnoRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("CNO"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractData.getContractId()))
				.fetch();
			
			if(cnoRecords.isNotEmpty())
				contractSpecificData.setCno(parseContractTable(cnoRecords.get(0).get(CONTRACT_DATA.EXPRESSION)));
			
//			Result<Record> contractSpecificDataRecords = dslContext.select().from(CONTRACT_ATTACH)
//					.where(CONTRACT_ATTACH.CONTRACT.eq(contractData.getContractId())
//							.or(CONTRACT_ATTACH.CONTRACT.isNull()))
//					.and(CONTRACT_ATTACH.DOMAIN.eq(employeeData.getDomain()))
//					.and(CONTRACT_ATTACH.TYPE.eq((byte)4))
//					.fetch();
//			
//			if(contractSpecificDataRecords.isNotEmpty()) {
//				byte[] data = contractSpecificDataRecords.get(0).get(CONTRACT_ATTACH.DATA);
//				
//			}
			
			employeeContractInfo.setContractSpecificData(contractSpecificData);
			
			// ---------------------------------------------- CONTRACT OTHER INFO -----------------------------------------------------
			
			Map<String, String> contractOtherInfoMap = new HashMap<>();
			employeeContractInfo.setContractOtherData(contractOtherInfoMap);
			
			// ------------------------------------------------ CONTRACT CLAUSE -------------------------------------------------------
			
			List<ContractClause> contractClauses = new ArrayList<>();
			employeeContractInfo.setContractClauses(contractClauses);

			// ------------------------------------------------ CONTRACT ATTACH -------------------------------------------------------
			
			List<Attach> contractAttachs = new ArrayList<>();
			employeeContractInfo.setContractAttachments(contractAttachs);
			
			// ------------------------------------------------ SCOPES -------------------------------------------------------
			
			Map<String, String> scopeMap = new HashMap<>();
			
			Result<Record> scopeRecords = dslContext.select().from(SCOPE)
				.where(SCOPE.DOMAIN.eq(employeeData.getDomain()))
				.fetch();
			
			scopeRecords.forEach(scopeRecord -> scopeMap.put(scopeRecord.get(SCOPE.DESCRIPTION), scopeRecord.get(SCOPE.ID).toString()));
			employeeContractInfo.setScopeMap(scopeMap);
			
			employeesInfo.add(employeeContractInfo);
		}
		
		return employeesInfo;
	}
	
	public static List<EmployeeContractInfo> getEmployeesInfo(Connection conn, Integer domainId, Boolean allEmployees) {
		return getEmployeesInfoDB(DSL.using(conn, getDefaultSettings()), domainId, allEmployees);
	}
	
	private static List<EmployeeContractInfo> getEmployeesInfoDB(DSLContext dslContext, Integer domainId, Boolean allEmployees) {
		List<EmployeeContractInfo> employeesInfo = new ArrayList<>();
		
		List<Integer> allContractIds = null;
		
		if(Boolean.TRUE.equals(allEmployees)) {
			// ------------------------------------------------ Get all contracts from domainId
			allContractIds = dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.DOMAIN.eq(domainId))
					.and(CONTRACT.ID.gt(0))
					.fetch(CONTRACT.ID);
		} else {
			// ------------------------------------------------ Get active contracts from domainId or ends in the last two months
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.MONTH, -1);
			
			Date contractEndDate = new Date(cal.getTimeInMillis());
			
			allContractIds = dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.DOMAIN.eq(domainId))
					.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(contractEndDate)))
					.and(CONTRACT.ID.gt(0))
					.fetch(CONTRACT.ID);
		}
		
		for(Integer contractId : allContractIds) {
			
			// --------------------------------------------- Init
			
			EmployeeContractInfo employeeContractInfo = new EmployeeContractInfo();
			ContractInfo contractData = new ContractInfo();
			EmployeeInfo employeeData = new EmployeeInfo();
			
			// --------------------------------------------- Employee Info
			
			Record contractPerRegWorkplaceRecord = dslContext.select().from(PERSON)
					.innerJoin(REGISTRY)
					.on(PERSON.REGISTRY.eq(REGISTRY.ID))
					.innerJoin(CONTRACT)
					.on(CONTRACT.PERSON.eq(PERSON.REGISTRY))
					.innerJoin(WORKPLACE)
					.on(WORKPLACE.ID.eq(CONTRACT.WORKPLACE))
					.where(CONTRACT.ID.eq(contractId))
					.fetchOne();
			
			employeeData.setEmployeeId(contractPerRegWorkplaceRecord.get(PERSON.REGISTRY));
			employeeData.setDomain(contractPerRegWorkplaceRecord.get(PERSON.DOMAIN));
			employeeData.setSsNumber(contractPerRegWorkplaceRecord.get(PERSON.SOCIAL_SECURITY_NUM));
			employeeData.setName(contractPerRegWorkplaceRecord.get(PERSON.NAME));
			employeeData.setSurName(contractPerRegWorkplaceRecord.get(PERSON.FIRST_SURNAME));
			employeeData.setSecondSurName(contractPerRegWorkplaceRecord.get(PERSON.SECOND_SURNAME));
			employeeData.setDocument(contractPerRegWorkplaceRecord.get(REGISTRY.DOCUMENT));
			
			// --------------------------------------------- Contract Info
			
			// HAS PAYROLL
			Integer payrollCount = dslContext.selectCount().from(SALARY)
					.where(SALARY.CONTRACT.eq(contractId))
					.fetchOne(0, int.class);
			
			contractData.setHasPayroll(payrollCount > 0);
			
			// CONTRACT TABLE
			contractData.setContractId(contractPerRegWorkplaceRecord.get(CONTRACT.ID));
			contractData.setStartDate(contractPerRegWorkplaceRecord.get(CONTRACT.START_DATE));
			contractData.setEndDate(contractPerRegWorkplaceRecord.get(CONTRACT.END_DATE));
			contractData.setSsRegimen(contractPerRegWorkplaceRecord.get(CONTRACT.SS_REGIME));
			contractData.setAgreementCategory(contractPerRegWorkplaceRecord.get(CONTRACT.CATEGORY_DESCRIPTION));
			
			// ENTERPRISE CCC
			Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.ID.eq(contractPerRegWorkplaceRecord.get(CONTRACT.ENTERPRISE_CCC)))
				.fetchOne();
			
			if(null!= enterpriseCCCRecord) {
				contractData.setCccId(enterpriseCCCRecord.get(ENTERPRISE_CCC.ID));
				contractData.setCccType(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE));
				contractData.setCompleteCCC(getCCCRegimeCode(contractData.getCccType())+enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC));	
			}
			
			// WORKPLACE TABLE		
			contractData.setWorkplaceId(contractPerRegWorkplaceRecord.get(WORKPLACE.ID));
			contractData.setWorkplaceName(contractPerRegWorkplaceRecord.get(WORKPLACE.DESCRIPTION));
			
			// TC2
			Result<Record> tc2Records = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("TC2"))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
			
			if(tc2Records.isNotEmpty()) {
				Record tc2Rocerd = tc2Records.get(0);
				contractData.setContracttypeId(tc2Rocerd.get(CONTRACT_DATA.ID));
				contractData.setContractType(tc2Rocerd.get(CONTRACT_DATA.EXPRESSION));
			}
			
			employeeContractInfo.setEmployeeInfo(employeeData);
			employeeContractInfo.setContractInfo(contractData);
			
			// --------------------------------------------- Add employeeContractInfo
						
			employeesInfo.add(employeeContractInfo);
			
		}
		
		return employeesInfo;
	}
	
	public static EmployeeContractInfo getEmployeeInfo(Connection conn, Integer contractId) {
		return getEmployeeInfoDB(DSL.using(conn, getDefaultSettings()), contractId);
	}

	private static EmployeeContractInfo getEmployeeInfoDB(DSLContext dslContext, Integer contractId) {
			
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
		
		Integer employeeRegistry = personTable.get(PERSON.REGISTRY);
		
		// REGISTRY TABLE
		Record registryTable = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(employeeRegistry))
				.fetchOne();
		
		employeeData.setDocument(registryTable.get(REGISTRY.DOCUMENT));
		employeeData.setDocumentType(registryTable.get(REGISTRY.DOCUMENT_TYPE)); //Esto lo saco con el formato del documento, se podria obviar?
		employeeData.setNationality(registryTable.get(REGISTRY.NATIONALITY));
		
		// RADDRESS AND GEOZONE TABLE
		Record raddressTable = dslContext.select().from(RADDRESS)
				.where(RADDRESS.REGISTRY.eq(employeeRegistry))
				.fetchOne();
		
		if(null != raddressTable) {
		
			employeeData.setRaddressId(raddressTable.get(RADDRESS.ID));
			employeeData.setStreetType(raddressTable.get(RADDRESS.STREET_TYPE));
			employeeData.setAddress(raddressTable.get(RADDRESS.ADDRESS));
			employeeData.setAddressInfo(raddressTable.get(RADDRESS.ADDRESS2));
			employeeData.setAddresNum(raddressTable.get(RADDRESS.NUMBER));
			employeeData.setAddressZip(raddressTable.get(RADDRESS.ZIP));
			employeeData.setAddressCity(raddressTable.get(RADDRESS.MUNICIPALITY_CODE));
			employeeData.setAddressProvinces(raddressTable.get(RADDRESS.GEOZONE));
		}
		
		// RMEDIA TABLE
		Result<Record> rmediaRecords = dslContext.select().from(RMEDIA)
				.where(RMEDIA.REGISTRY.eq(employeeRegistry))
				.fetch();
		
		for(Record rmediaRecord : rmediaRecords){
			if(rmediaRecord.get(RMEDIA.MEDIA) == 1){
				employeeData.setPhoneId(rmediaRecord.get(RMEDIA.ID));
				employeeData.setPhone(rmediaRecord.get(RMEDIA.VALUE));
			}else if(rmediaRecord.get(RMEDIA.MEDIA) == 2){
				employeeData.setMobileId(rmediaRecord.get(RMEDIA.ID));
				employeeData.setMobile(rmediaRecord.get(RMEDIA.VALUE));
			}else if(rmediaRecord.get(RMEDIA.MEDIA) == 4){
				employeeData.setEmailId(rmediaRecord.get(RMEDIA.ID));
				employeeData.setEmail(rmediaRecord.get(RMEDIA.VALUE));
			}
		}
		
		// FIND RPAYMETHOD
		Record rPayMethodRecord = dslContext.select().from(RPAYMETHOD)
			.where(RPAYMETHOD.REGISTRY.eq(employeeRegistry))
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
			employeeData.addRbank(r.get(RBANK.ID), r.get(RBANK.BANK_ACCOUNT), r.get(RBANK.BIC), r.get(RBANK.ALIAS));
		}
		
		// --------------------------------------------- Contract Info
		
		// HAS PAYROLL
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
					.orderBy(SALARY.END_DATE.desc())
					.fetch();
		
		if(salaryRecords.isEmpty()){
			contractData.setHasPayroll(false);
			contractData.setPayrollDate(null);
			contractData.setSalariesCount(0);
		}else{
			contractData.setHasPayroll(true);
			contractData.setPayrollDate(salaryRecords.get(0).get(SALARY.END_DATE));
			contractData.setSalariesCount(salaryRecords.size());
			contractData.setContractSalariesInfo(createSalariesInfo(salaryRecords));
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
		
		Integer employeWorkplaceId = contractTable.get(CONTRACT.WORKPLACE);
		
		// WORKPLACE TABLE		
		contractData.setWorkplaceId(employeWorkplaceId);
		
		Record workplaceRecord = dslContext.select().from(WORKPLACE).where(WORKPLACE.ID.eq(employeWorkplaceId)).fetchOne();
		
		contractData.setWorkplaceName(workplaceRecord.get(WORKPLACE.DESCRIPTION));
		
		Record raddressRecord = dslContext.select().from(RADDRESS)
				.where(RADDRESS.ID.eq(
						dslContext.select(WORKPLACE.ADDRESS).from(WORKPLACE)
							.where(WORKPLACE.ID.eq(employeWorkplaceId))
							.fetchOne(WORKPLACE.ADDRESS)
				)).fetchOne();
		
		contractData.setWorkplaceZIP(raddressRecord.get(RADDRESS.MUNICIPALITY_CODE));
		contractData.setWorkplaceFullAddress(raddressRecord.get(RADDRESS.STREET_TYPE)+". "+raddressRecord.get(RADDRESS.ADDRESS)+" "+raddressRecord.get(RADDRESS.NUMBER));
		
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
				
				Record enterpriseRecord = dslContext.select().from(REGISTRY)
						.where(REGISTRY.ID.eq(enterpriseActivityTable.get(ENTERPRISE_ACTIVITY.ENTERPRISE)))
						.fetchOne();
				
				contractData.setEnterpriseCIF(enterpriseRecord.get(REGISTRY.DOCUMENT));
				contractData.setEnterpriseName(enterpriseRecord.get(REGISTRY.NAME));
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
				
				contractData.setCompleteCCC(getCCCRegimeCode(enterpriseCCCTable.get(ENTERPRISE_CCC.TYPE))+enterpriseCCCTable.get(ENTERPRISE_CCC.CCC));
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
				contractData.setAgreementColective(agreementTable.get(AGREEMENT.SS_NUMBER));
			} catch ( Exception t ) {
				contractData.setAgreementLevelId(null);
				contractData.setAgreementId(null);
			}
			
		}
		
		contractData.setContracttypeId(null);
		contractData.setContractType(null);
		contractData.setQuotegroupId(null);
		contractData.setQuoteGroup(null);
		contractData.setOcupationId(null);
		contractData.setOcupation(null);
		contractData.setJourneytypeId(null);
		contractData.setJourneyType(null);
		contractData.setPartialityCoef(null);
		
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
			}else if(r.get(CONTRACT_DATA.NAME).equals("RLCE")) {
				contractData.setRlceId(r.get(CONTRACT_DATA.ID));
				contractData.setRlce(r.get(CONTRACT_DATA.EXPRESSION));
			}else if(r.get(CONTRACT_DATA.NAME).equals("TIEMPO_COMPLETO")) {
				contractData.setJourneytypeId(r.get(CONTRACT_DATA.ID));
				contractData.setJourneyType(r.get(CONTRACT_DATA.EXPRESSION).equalsIgnoreCase("true") ? (byte) 1 : (byte) 0);
			}else if(r.get(CONTRACT_DATA.NAME).equals("COEFICIENTE_PARCIALIDAD")) {
				String expression = r.get(CONTRACT_DATA.EXPRESSION);
				if(null != expression && expression.contains("\""))
					expression = expression.split("\"")[1];
				
				Double partialityCoef = Double.parseDouble(expression);
				contractData.setPartialityCoefId(r.get(CONTRACT_DATA.ID));
				contractData.setPartialityCoef(partialityCoef);
			} else if(r.get(CONTRACT_DATA.NAME).equals("MODELO_COTIZACION_AGRARIO")) {
				contractData.setMdctzId(r.get(CONTRACT_DATA.ID));
				contractData.setMdctz(r.get(CONTRACT_DATA.EXPRESSION));
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
		
		// ---------------------------------------------- CheckSettle and Certifica2
		
		Result<Record> settlementRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.TYPE.eq((byte)2))
				.orderBy(SALARY.ID.desc())
				.fetch();
		
		contractData.setHasSettle(settlementRecords.isNotEmpty());
		
		List<Integer> certifca2BatachIds = dslContext.select(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH)
				.from(CERTIFICA2_BATCH_DETAIL)
				.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.eq(contractId))
				.fetch(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH);
		
		if(certifca2BatachIds.isEmpty()) {
			contractData.setHasCertifica2(false);
		} else {
			Record1<Integer> sepeBatchAttachRecord = dslContext.select(SEPE_BATCH_ATTACH.ID).from(SEPE_BATCH_ATTACH)
				.where(SEPE_BATCH_ATTACH.SOURCE_BATCH.eq(certifca2BatachIds.get(0)))
				.and(SEPE_BATCH_ATTACH.MIMETYPE.eq((byte)5))
				.fetchOne();
			
			contractData.setHasCertifica2(sepeBatchAttachRecord != null);
		}
		
		// ---------------------------------------------- Contract Extension
		
		Integer contractTypeValue = Integer.parseInt(contractData.getContractType());
		Boolean hasExtension = false;
		if(contractTypeValue >= 400) {
			Result<Record> extensionRecords = dslContext.select().from(CONTRACT_ATTACH).where(CONTRACT_ATTACH.CONTRACT.eq(contractData.getContractId())).and(CONTRACT_ATTACH.TYPE.eq((byte)13)).fetch();
			if(extensionRecords.isNotEmpty()) hasExtension = true;
		}
		contractData.setHasExtension(hasExtension);
		
		employeeContractInfo.setEmployeeInfo(employeeData);
		employeeContractInfo.setContractInfo(contractData);
		
		// ------------------------------------------- CONTRACT SPECIFIC DATA -----------------------------------------------------
		
		ContractSpecificData contractSpecificData = new ContractSpecificData();
		
		// Get CNO
		Result<Record> cnoRecords = dslContext.select().from(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("CNO"))
			.and(CONTRACT_DATA.CONTRACT.eq(contractData.getContractId()))
			.fetch();
		
		if(cnoRecords.isNotEmpty())
			contractSpecificData.setCno(parseContractTable(cnoRecords.get(0).get(CONTRACT_DATA.EXPRESSION)));
		
//		Result<Record> contractSpecificDataRecords = dslContext.select().from(CONTRACT_ATTACH)
//				.where(CONTRACT_ATTACH.CONTRACT.eq(contractData.getContractId())
//						.or(CONTRACT_ATTACH.CONTRACT.isNull()))
//				.and(CONTRACT_ATTACH.DOMAIN.eq(employeeData.getDomain()))
//				.and(CONTRACT_ATTACH.TYPE.eq((byte)4))
//				.fetch();
//		
//		if(contractSpecificDataRecords.isNotEmpty()) {
//			byte[] data = contractSpecificDataRecords.get(0).get(CONTRACT_ATTACH.DATA);
//			
//		}
		
		employeeContractInfo.setContractSpecificData(contractSpecificData);
		
		// ---------------------------------------------- CONTRACT OTHER INFO -----------------------------------------------------
		
		Map<String, String> contractOtherInfoMap = new HashMap<>();
		employeeContractInfo.setContractOtherData(contractOtherInfoMap);
		
		// ------------------------------------------------ CONTRACT CLAUSE -------------------------------------------------------
		
		List<ContractClause> contractClauses = new ArrayList<>();
		employeeContractInfo.setContractClauses(contractClauses);

		// ------------------------------------------------ CONTRACT ATTACH -------------------------------------------------------
		
		List<Attach> contractAttachs = new ArrayList<>();
		employeeContractInfo.setContractAttachments(contractAttachs);
		
		// ------------------------------------------------ SCOPES -------------------------------------------------------
		
		Map<String, String> scopeMap = new HashMap<>();
		
		Result<Record> scopeRecords = dslContext.select().from(SCOPE)
			.where(SCOPE.DOMAIN.eq(employeeData.getDomain()))
			.fetch();
		
		scopeRecords.forEach(scopeRecord -> scopeMap.put(scopeRecord.get(SCOPE.DESCRIPTION), scopeRecord.get(SCOPE.ID).toString()));
		
		employeeContractInfo.setScopeMap(scopeMap);
		
		System.out.println("GET CONTRACT : " + contractId + " -> TC2 : " + contractData.getContractType());
		
		return employeeContractInfo;
	}
	
	public static List<EmployeeInfo> getEmployeesByWorkplace(Connection conn, Integer workplaceId, Boolean allEmployees) {
		return getEmployeesByWorkplaceDB(DSL.using(conn, getDefaultSettings()), workplaceId, allEmployees);
	}

	private static List<EmployeeInfo> getEmployeesByWorkplaceDB(DSLContext dslContext, Integer workplaceId, Boolean allEmployees) {
		List<EmployeeInfo> employeeList = new ArrayList<>();

		SelectConditionStep<Record> condt = dslContext.select()
				.from(CONTRACT)
				.innerJoin(PERSON)
				.on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
				.where(CONTRACT.WORKPLACE.eq(workplaceId))
				.and(CONTRACT.ID.gt(0));
		
		if(Boolean.FALSE.equals(allEmployees)){
			// ------------------------------------------------ Get active contracts from domainId or ends in the last two months
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.MONTH, -1);
			Date contractEndDate = new Date(cal.getTimeInMillis());
			condt.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.ge(contractEndDate)));
		}
		
		Result<Record> queryEnd = condt.groupBy(PERSON.REGISTRY).fetch();

		for(Record r : queryEnd) {
			EmployeeInfo employeeData = new EmployeeInfo();
			employeeData.setEmployeeId(r.get(PERSON.REGISTRY));
			employeeData.setDomain(r.get(PERSON.DOMAIN));
			employeeData.setSsNumber(r.get(PERSON.SOCIAL_SECURITY_NUM));
			employeeData.setName(r.get(PERSON.NAME));
			employeeData.setSurName(r.get(PERSON.FIRST_SURNAME));
			employeeData.setSecondSurName(r.get(PERSON.SECOND_SURNAME));
			employeeData.setContractId(r.get(CONTRACT.ID));
			// REGISTRY TABLE
			Record registryTable = dslContext.select().from(REGISTRY).where(REGISTRY.ID.eq(r.get(PERSON.REGISTRY))).fetchOne();
		
			employeeData.setDocument(registryTable.get(REGISTRY.DOCUMENT));
			employeeData.setDocumentType(registryTable.get(REGISTRY.DOCUMENT_TYPE));
			employeeData.setNationality(registryTable.get(REGISTRY.NATIONALITY));
			
			employeeList.add(employeeData);
		}
		
		return employeeList;
	}
	
	private static ArrayList<ContractSalaryInfo> createSalariesInfo(Result<Record> salaryRecords) {
		ArrayList<ContractSalaryInfo> contractSalariesInfo = new ArrayList<>();
		
		for(Record salaryRecord : salaryRecords) {
			ContractSalaryInfo contractSalaryInfo = new ContractSalaryInfo();
			contractSalaryInfo.setType(getSalaryType(salaryRecord.get(SALARY.TYPE)));
			contractSalaryInfo.setStart(salaryRecord.get(SALARY.START_DATE));
			contractSalaryInfo.setEnd(salaryRecord.get(SALARY.END_DATE));
			contractSalaryInfo.setTotalLiquid(salaryRecord.get(SALARY.TOTAL_LIQUID));
			contractSalariesInfo.add(contractSalaryInfo);
		}
		
		return contractSalariesInfo;
	}

	private static String getSalaryType(Byte salaryType) {
		switch (salaryType) {
		case (byte)0:	
			return "N\u00F3mina";
		case (byte)1:
			return "Extra";
		case (byte)2:
			return "Finiquito";
		case (byte)3:
			return "Atraso";
		default:
			return "N\u00F3mina";
		}
	}
	
	// --------------------------------------- AUX METHODS -----------------------------

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
	// ---------------------------------------------------    AUXLIAR METHODS   --------------------------------------------------
	// ---------------------------------------------------------------------------------------------------------------------------	
	
	

	public static Map<String, CNO> getCNOs(Connection conn) {
		return getCNOsDB(DSL.using(conn, getDefaultSettings()));
	}

	public static Map<String, CNO> getCNOsDB(DSLContext dslContext) {
		Map<String, CNO> cnoMap = new HashMap<>();
		Result<Record> cnoRecords = dslContext.select().from(CNO).fetch();
		for(Record cnoRecord : cnoRecords) {
			CNO cno = new CNO();
			cno.setId(cnoRecord.get(CNO.ID));
			cno.setCode(cnoRecord.get(CNO.CODE));
			cno.setTitle(cnoRecord.get(CNO.TITLE));
					
			cnoMap.put(cnoRecord.get(CNO.CODE), cno);
		}
		return cnoMap;
	}
	
	
	
	// ------------------------------------------------------------------------------------------------------------------------
	// ------------------------------------------------ TRASH EMPLOYEES -------------------------------------------------------
	// ------------------------------------------------------------------------------------------------------------------------

	public static List<EmployeeContractInfo> getTrashEmployeesInfo(Connection conn, Integer domainId) {
		return getTrashEmployeesInfoDB(DSL.using(conn, getDefaultSettings()), domainId);
	}

	private static List<EmployeeContractInfo> getTrashEmployeesInfoDB(DSLContext dslContext, Integer domainId) {
		List<EmployeeContractInfo> employeesInfo = new ArrayList<>();
		
		List<Integer> trashContractIds = dslContext.select(CONTRACT.ID).from(CONTRACT)
				.where(CONTRACT.DOMAIN.eq(domainId))
				.and(CONTRACT.ID.lt(0))
				.fetch(CONTRACT.ID);
		
		for(Integer contractId : trashContractIds) {
			
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
			employeeData.setSsNumber(personTable.get(PERSON.SOCIAL_SECURITY_NUM));
			employeeData.setName(personTable.get(PERSON.NAME));
			employeeData.setSurName(personTable.get(PERSON.FIRST_SURNAME));
			employeeData.setSecondSurName(personTable.get(PERSON.SECOND_SURNAME));
			
			Integer employeeRegistry = personTable.get(PERSON.REGISTRY);
			
			// REGISTRY TABLE
			Record registryTable = dslContext.select().from(REGISTRY)
					.where(REGISTRY.ID.eq(employeeRegistry))
					.fetchOne();
			
			employeeData.setDocument(registryTable.get(REGISTRY.DOCUMENT));
			employeeData.setDocumentType(registryTable.get(REGISTRY.DOCUMENT_TYPE)); //Esto lo saco con el formato del documento, se podria obviar?
			employeeData.setNationality(registryTable.get(REGISTRY.NATIONALITY));
			
			// --------------------------------------------- Contract Info
			
			// HAS PAYROLL
			Result<Record> salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.CONTRACT.eq(contractId))
						.orderBy(SALARY.END_DATE.desc())
						.fetch();
			
			if(salaryRecords.isEmpty()){
				contractData.setHasPayroll(false);
				contractData.setPayrollDate(null);
				contractData.setSalariesCount(0);
			}else{
				contractData.setHasPayroll(true);
				contractData.setPayrollDate(salaryRecords.get(0).get(SALARY.END_DATE));
				contractData.setSalariesCount(salaryRecords.size());
			}
			
			// CONTRACT TABLE
			Record contractTable = dslContext.select().from(CONTRACT)
					.where(CONTRACT.ID.eq(contractId))
					.fetchOne();
			
			contractData.setContractId(contractId);
			contractData.setStartDate(contractTable.get(CONTRACT.START_DATE));
			contractData.setEndDate(contractTable.get(CONTRACT.END_DATE));
			contractData.setSsRegimen(contractTable.get(CONTRACT.SS_REGIME));
			
			Integer employeWorkplaceId = contractTable.get(CONTRACT.WORKPLACE);
			
			// WORKPLACE TABLE		
			contractData.setWorkplaceId(employeWorkplaceId);
			
			Record workplaceRecord = dslContext.select().from(WORKPLACE).where(WORKPLACE.ID.eq(employeWorkplaceId)).fetchOne();
			
			contractData.setWorkplaceName(workplaceRecord.get(WORKPLACE.DESCRIPTION));
			
			contractData.setContracttypeId(null);
			contractData.setContractType(null);
			contractData.setQuotegroupId(null);
			contractData.setQuoteGroup(null);
			contractData.setOcupationId(null);
			contractData.setOcupation(null);
			contractData.setJourneytypeId(null);
			contractData.setJourneyType(null);
			contractData.setPartialityCoef(null);
			
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
				}
				
			}
			
			employeeContractInfo.setEmployeeInfo(employeeData);
			employeeContractInfo.setContractInfo(contractData);
			
			// ------------------------------------------- CONTRACT SPECIFIC DATA -----------------------------------------------------
			
			ContractSpecificData contractSpecificData = new ContractSpecificData();
			employeeContractInfo.setContractSpecificData(contractSpecificData);
			
			// ---------------------------------------------- CONTRACT OTHER INFO -----------------------------------------------------
			
			Map<String, String> contractOtherInfoMap = new HashMap<>();
			employeeContractInfo.setContractOtherData(contractOtherInfoMap);
			
			// ------------------------------------------------ CONTRACT CLAUSE -------------------------------------------------------
			
			List<ContractClause> contractClauses = new ArrayList<>();
			employeeContractInfo.setContractClauses(contractClauses);

			// ------------------------------------------------ CONTRACT ATTACH -------------------------------------------------------
			
			List<Attach> contractAttachs = new ArrayList<>();
			employeeContractInfo.setContractAttachments(contractAttachs);
			
			
			employeesInfo.add(employeeContractInfo);
		}
		
		return employeesInfo;
	}

	public static void setSepeId(String domainName, Integer contractId, String ide) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			Integer domainId = AonServletUtils.getDomainID(domainName);
			
			Result<Record> sepeIdRecords = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.eq("SEPE_ID"))
					.fetch();
			
			if(sepeIdRecords.isEmpty()) {
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.NAME, "SEPE_ID")
					.set(CONTRACT_DATA.EXPRESSION, ide)
					.set(CONTRACT_DATA.START_DATE, new Date(new java.util.Date().getTime()))
					.set(CONTRACT_DATA.END_DATE, DSL.castNull(CONTRACT_DATA.END_DATE))
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.execute();
			
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.NAME, "COMUNICATION_DATE")
					.set(CONTRACT_DATA.EXPRESSION, dateFormat.format(new java.util.Date()))
					.set(CONTRACT_DATA.START_DATE, new Date(new java.util.Date().getTime()))
					.set(CONTRACT_DATA.END_DATE, DSL.castNull(CONTRACT_DATA.END_DATE))
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.execute();
			}
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}

	public static void removeSepeId(String domainName, Integer contractId, String sepeId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("SEPE_ID"))
				.and(CONTRACT_DATA.EXPRESSION.eq(sepeId))
				.execute();
			
			dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("COMUNICATION_DATE"))
				.execute();
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}
	
	public static void setSepeExtensionId(String domainName, Integer contractId, String extensionIde) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			Integer domainId = AonServletUtils.getDomainID(domainName);
			
			Result<Record> sepeIdRecords = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.eq("SEPE_EXTENSION_ID"))
					.fetch();
			
			if(sepeIdRecords.isEmpty()) {
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.NAME, "SEPE_EXTENSION_ID")
					.set(CONTRACT_DATA.EXPRESSION, extensionIde)
					.set(CONTRACT_DATA.START_DATE, new Date(new java.util.Date().getTime()))
					.set(CONTRACT_DATA.END_DATE, DSL.castNull(CONTRACT_DATA.END_DATE))
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.execute();
			
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.NAME, "COMUNICATION_EXTENSION_DATE")
					.set(CONTRACT_DATA.EXPRESSION, dateFormat.format(new java.util.Date()))
					.set(CONTRACT_DATA.START_DATE, new Date(new java.util.Date().getTime()))
					.set(CONTRACT_DATA.END_DATE, DSL.castNull(CONTRACT_DATA.END_DATE))
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.execute();
			}	
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}

	public static void removeSepeExtensionId(String domainName, Integer contractId, String sepeExtensionId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("SEPE_EXTENSION_ID"))
				.and(CONTRACT_DATA.EXPRESSION.eq(sepeExtensionId))
				.execute();
			
			dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("COMUNICATION_EXTENSION_DATE"))
				.execute();
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}

}
