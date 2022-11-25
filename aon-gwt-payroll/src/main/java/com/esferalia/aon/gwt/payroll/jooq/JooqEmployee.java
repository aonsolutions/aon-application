package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractBonus.CONTRACT_BONUS;
import static com.esferalia.aon.jooq.tables.ContractClause.CONTRACT_CLAUSE;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractDeduction.CONTRACT_DEDUCTION;
import static com.esferalia.aon.jooq.tables.ContractEmbargo.CONTRACT_EMBARGO;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.ContractLeave.CONTRACT_LEAVE;
import static com.esferalia.aon.jooq.tables.ContractPayment.CONTRACT_PAYMENT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
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
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
import com.esferalia.aon.gwt.payroll.shared.BankEntities;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractSalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.JourneyDuration;
import com.esferalia.aon.gwt.payroll.shared.Municipalities;
import com.esferalia.aon.jooq.tables.records.ContractDataRecord;
import com.esferalia.aon.jooq.tables.records.ContractInfoRecord;
import com.esferalia.aon.jooq.tables.records.ContractRecord;
import com.esferalia.aon.jooq.tables.records.GeozoneRecord;
import com.esferalia.aon.jooq.tables.records.RaddressRecord;
import com.esferalia.aon.jooq.tables.records.RbankRecord;
import com.esferalia.aon.jooq.tables.records.RegistryRecord;
import com.esferalia.aon.jooq.tables.records.RmediaRecord;
import com.esferalia.aon.jooq.tables.records.RpaymethodRecord;
import com.esferalia.aon.jooq.tables.records.SalaryDataRecord;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqEmployee {

	private static Settings SETTINGS = null;
	private static SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static EmployeeContractInfo createEmployeeContract(Connection conn, EmployeeContractInfo employeeContractData) {
		return createEmployeeContractDB(DSL.using(conn, getDefaultSettings()), employeeContractData);
	}

	public static EmployeeContractInfo getEmployeeInfo(Connection conn, Integer contract) {
		return getEmployeeInfoDB(DSL.using(conn, getDefaultSettings()), contract);
	}
	
	public static EmployeeContractInfo setEmployeeInfo(Connection conn, EmployeeContractInfo newEmployeeInfo) {
		return setEmployeeInfoDB(DSL.using(conn, getDefaultSettings()), newEmployeeInfo);
	}
	
	public static String setEmployeeAFIChanges(Connection conn, Integer contractId, java.util.Date newDate,
			boolean isChangeContract, String tc2, boolean isQuoteContract, Integer quoteGroup,
			boolean isOcupationContract, String ocupation) {
		
		return setEmployeeAFIChangesDB(DSL.using(conn, getDefaultSettings()), contractId, newDate, isChangeContract, tc2,
				isQuoteContract, quoteGroup, isOcupationContract, ocupation);
	}
	
	public static Record getEmployeeRecord(Connection conn, Integer contractId) {
		return getEmployeeRecord(DSL.using(conn, getDefaultSettings()), contractId);
	}

	private static EmployeeContractInfo createEmployeeContractDB(DSLContext dslContext, EmployeeContractInfo employeeContractData) {
		
		System.out.println("GUARDANDO EN DB ...");
		
		ContractInfo contractData = employeeContractData.getContractInfo();
		EmployeeInfo employeeData = employeeContractData.getEmployeeInfo();
		
		Municipalities municipalities = new Municipalities();
		
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
				.set(REGISTRY.DOCUMENT_COUNTRY, AonStringUtils.isBlank(employeeData.getNationalityCode()) ? "ES" : employeeData.getNationalityCode())
				.set(REGISTRY.NATIONALITY, AonStringUtils.isBlank(employeeData.getNationalityCode())  ? "ES" : employeeData.getNationalityCode())
				.set(REGISTRY.NAME, employeeData.getFullName())
				.returning(REGISTRY.ID)
				.fetchOne();
			
			registryId = registryRecord.getId();
			
			dslContext.insertInto(PERSON)
				.set(PERSON.REGISTRY, registryId)
				.set(PERSON.DOMAIN, domain)
				.set(PERSON.BIRTH_DATE, parseDate(employeeData.getBirthdate()))
				.set(PERSON.GENDER, employeeData.getGender())
				.set(PERSON.SOCIAL_SECURITY_NUM, employeeData.getSsNumber())
				.set(PERSON.NAME, employeeData.getName())
				.set(PERSON.MARITAL_STATUS, employeeData.getCivilStatus())
				.set(PERSON.FIRST_SURNAME, employeeData.getSurName())
				.set(PERSON.SECOND_SURNAME, employeeData.getSecondSurName())
				.execute();
			
			Integer rAddressId = null;
			
			if(null != employeeData.getAddressProvinces()) {
				
				Result<Record> geozone = dslContext.select()
						.from(GEOZONE)
						.where(GEOZONE.CODE.eq(employeeData.getAddressProvinces()))
							.and(GEOZONE.DOMAIN.eq(domain)
									.or(GEOZONE.DOMAIN.eq(parentDomain)))
						.orderBy(GEOZONE.DOMAIN.desc())
						.fetch();
				
				Integer geozoneId = null;
				
				if(geozone == null){
					Result<Record1<String>> names = dslContext.select(GEOZONE.NAME)
						.from(GEOZONE)
						.where(GEOZONE.CODE.eq(employeeData.getAddressProvinces()))
						.fetch();
					
					if(!names.isEmpty()){
						GeozoneRecord geozoneRecord  = dslContext.insertInto(GEOZONE)
								.set(GEOZONE.DOMAIN, domain)
								.set(GEOZONE.NAME, names.get(0).value1())
								.set(GEOZONE.CODE, employeeData.getAddressProvinces())
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
					.set(RADDRESS.ADDRESS, AonStringUtils.isBlank(employeeData.getAddress()) ? "-" : employeeData.getAddress())
					.set(RADDRESS.ADDRESS2, employeeData.getAddressInfo())
					.set(RADDRESS.NUMBER, AonStringUtils.isBlank(employeeData.getAddresNum()) ? "-" : employeeData.getAddresNum())
					.set(RADDRESS.ZIP, employeeData.getAddressZip())
					.set(RADDRESS.CITY, municipalities.getMunicipalityByZip(employeeData.getAddressCity()))
					.set(RADDRESS.MUNICIPALITY_CODE, AonStringUtils.leftPad(employeeData.getAddressCity(), 5, '0'))
					.set(RADDRESS.GEOZONE, geozoneId)
					.returning(RADDRESS.ID, RADDRESS.GEOZONE)
					.fetchOne();
				
				rAddressId = rAddressRecord.getId();
				
				if(geozone == null && null != geozoneId){
					Integer rAddressGeozone = rAddressRecord.getGeozone();
					
					GeozoneRecord geozoneParentRecord  = dslContext.insertInto(GEOZONE)
							.set(GEOZONE.DOMAIN, domain)
							.set(GEOZONE.NAME, "ESPA" + String.valueOf("\u00D1") +"A")
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
			
			Integer payMethodId = employeeData.getPaymethodId();
			
			// No tiene forma de pago, pero ha podido introducir un banco
			if(null == payMethodId) {
				String account = employeeData.getAccount();
				if(!AonStringUtils.isBlank(account)) {
					account.trim();
					account.replaceAll(" ", "");
					
					String alias = AonStringUtils.isNotBlank(employeeData.getBankAlias()) ? employeeData.getBankAlias() : "CUENTA";
					if(account.length() > 8 && AonStringUtils.isBlank(employeeData.getBankAlias())) {
						String codeBank = account.substring(4, 8);
						alias = BankEntities.getBankEntity(codeBank);
					}
					
					dslContext.insertInto(RBANK)
							.set(RBANK.DOMAIN, domain)
							.set(RBANK.REGISTRY, registryId)
							.set(RBANK.BANK_ACCOUNT, account)
							.set(RBANK.BIC, employeeData.getBic())
							.set(RBANK.ALIAS, alias)
							.set(RBANK.ACTIVE, (byte) 1)
							.execute();
				}
			
			// Tiene forma de pago
			} else {
				Byte typePayMethod = dslContext.select(PAY_METHOD.TYPE).from(PAY_METHOD)
						.where(PAY_METHOD.ID.eq(payMethodId)).fetchOne(PAY_METHOD.TYPE);
				
				Integer rbankTableId = null;
				
				String account = employeeData.getAccount();
				if(!AonStringUtils.isBlank(account)) {
					account.trim();
					account.replaceAll(" ", "");
					
					String alias = AonStringUtils.isNotBlank(employeeData.getBankAlias()) ? employeeData.getBankAlias() : "CUENTA";
					if(account.length() > 8 && AonStringUtils.isBlank(employeeData.getBankAlias())) {
						String codeBank = account.substring(4, 8);
						alias = BankEntities.getBankEntity(codeBank);
					}
					
					RbankRecord rbankRecord = dslContext.insertInto(RBANK)
							.set(RBANK.DOMAIN, domain)
							.set(RBANK.REGISTRY, registryId)
							.set(RBANK.BANK_ACCOUNT, account)
							.set(RBANK.BIC, employeeData.getBic())
							.set(RBANK.ALIAS, alias)
							.set(RBANK.ACTIVE, (byte) 1)
							.returning(RBANK.ID)
							.fetchOne();
					
					// Si es transferencia (no se si giro tambien) se actualizar el rbankTableId para el rpaymethod
					if(typePayMethod == (byte)5)
						rbankTableId = rbankRecord.get(RBANK.ID); 
				}
				
				dslContext.insertInto(RPAYMETHOD)
				.set(RPAYMETHOD.DOMAIN, domain)
				.set(RPAYMETHOD.REGISTRY, registryId)
				.set(RPAYMETHOD.PAY_METHOD, payMethodId)
				.set(RPAYMETHOD.RBANK, rbankTableId)
				.set(RPAYMETHOD.NUMBER_OF_PYMNTS, (short) 1)
				.set(RPAYMETHOD.PYMNT_DAYS, "")
				.execute();
			}
			
		}else{ //EMPLEADO YA EXISTENTE
			
			registryId = employeeData.getEmployeeId();
				
		}
		
		// ------------------------------------------------------------------------------------------------------------------------
		// ------------------------------------------------ CONTRACT INFO ---------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------------------------
		
		Date contractStartDate =  parseDate(contractData.getStartDate());
		Date contractEndDate =  parseDate(contractData.getEndDate());
		
		ContractRecord contractRecord = dslContext.insertInto(CONTRACT)
			.set(CONTRACT.DOMAIN, domain)
			.set(CONTRACT.PERSON, registryId)
			.set(CONTRACT.WORKPLACE, contractData.getWorkplaceId())
			.set(CONTRACT.START_DATE, contractStartDate)
			.set(CONTRACT.END_DATE, contractEndDate)
			.set(CONTRACT.SENIORITY_DATE, parseDate(contractData.getSeniorityDate()) == null ? contractStartDate :  parseDate(contractData.getSeniorityDate()))
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
		
			if(null != contractData.getContractType()) {
				String contractType = AonStringUtils.leftPad(contractData.getContractType(), 3, "0");
				
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "TC2")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, parseContractTableStr(contractType))
					.set(CONTRACT_DATA.START_DATE, contractStartDate)
					.set(CONTRACT_DATA.END_DATE, contractEndDate)
					.execute();
			}
		
			/*ModelOption.values()[employeeContractData.getContract_model()].toString()*/
			if(null != contractData.getContractModel())
				dslContext.insertInto(CONTRACT_INFO)
					.set(CONTRACT_INFO.DOMAIN, domain)
					.set(CONTRACT_INFO.NAME, "OPCION_CONTRATO")
					.set(CONTRACT_INFO.CONTRACT, contractId)
					.set(CONTRACT_INFO.EXPRESSION, (contractData.getContractModel() == null) ? (String) null : "\""+ ModelOption.values()[contractData.getContractModel()].toString() +"\"")
					.set(CONTRACT_INFO.START_DATE, contractStartDate)
					.set(CONTRACT_INFO.END_DATE, contractEndDate)
					.execute();
		
			if(null != contractData.getQuoteGroup())
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "GRUPO_COTIZACION")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, parseContractTableStr(contractData.getQuoteGroup()))
					.set(CONTRACT_DATA.START_DATE, contractStartDate)
					.set(CONTRACT_DATA.END_DATE, contractEndDate)
					.execute();
			
			if(contractData.getQuoteGroupIdxMonth())
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "DIAS_MES")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, "30")
					.set(CONTRACT_DATA.START_DATE, contractStartDate)
					.set(CONTRACT_DATA.END_DATE, contractEndDate)
					.execute();
		
			if(null != contractData.getOcupation())
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "OCUPACION")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, parseContractTableStr(contractData.getOcupation()))
					.set(CONTRACT_DATA.START_DATE, contractStartDate)
					.set(CONTRACT_DATA.END_DATE, contractEndDate)
					.execute();	
				
		} else { //ES RETA
		
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "TIEMPO_COMPLETO")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, (contractData.getJourneyType() == 0) ? "false" : "true")
				.set(CONTRACT_DATA.START_DATE, contractStartDate)
				.set(CONTRACT_DATA.END_DATE, contractEndDate)
				.execute();
			
			dslContext.insertInto(CONTRACT_INFO)
				.set(CONTRACT_INFO.DOMAIN, domain)
				.set(CONTRACT_INFO.CONTRACT, contractId)
				.set(CONTRACT_INFO.NAME, "RETA")
				.set(CONTRACT_INFO.EXPRESSION, "true")
				.set(CONTRACT_INFO.START_DATE, contractStartDate)
				.set(CONTRACT_INFO.END_DATE, contractEndDate)
				.execute();
			
			dslContext.update(CONTRACT)
				.set(CONTRACT.ENTERPRISE_CCC, (Integer) null)
				.set(CONTRACT.ENTERPRISE_ACTIVITY, (Integer) null)
				.set(CONTRACT.SS_REGIME, (byte) 3)
				.where(CONTRACT.ID.eq(contractId))
				.execute();
			
			if(null != contractData.getQuoteGroup())
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "GRUPO_COTIZACION")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, parseContractTableStr(contractData.getQuoteGroup()))
					.set(CONTRACT_DATA.START_DATE, contractStartDate)
					.set(CONTRACT_DATA.END_DATE, contractEndDate)
					.execute();
			
			if(contractData.getQuoteGroupIdxMonth())
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "DIAS_MES")
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.EXPRESSION, "30")
					.set(CONTRACT_DATA.START_DATE, contractStartDate)
					.set(CONTRACT_DATA.END_DATE, contractEndDate)
					.execute();
			
		}
		
		if(null != contractData.getMdTBT())
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "TIPO_TRIBUTACION")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, parseContractTableStr(contractData.getMdTBT()+""))
				.set(CONTRACT_DATA.START_DATE, contractStartDate)
				.set(CONTRACT_DATA.END_DATE, contractEndDate)
				.execute();
		
		if(AonStringUtils.isNotBlank(contractData.getRlce()))
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "RLCE")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, parseContractTableStr(contractData.getRlce()))
				.set(CONTRACT_DATA.START_DATE, contractStartDate)
				.set(CONTRACT_DATA.END_DATE, contractEndDate)
				.execute();
		
		if(AonStringUtils.isNotBlank(contractData.getEmployeesColective()))
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "COLECTIVO_TRABAJADORES")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, parseContractTableStr(contractData.getEmployeesColective()))
				.set(CONTRACT_DATA.START_DATE, contractStartDate)
				.set(CONTRACT_DATA.END_DATE, contractEndDate)
				.execute();
		
		dslContext.insertInto(CONTRACT_INFO)
			.set(CONTRACT_INFO.DOMAIN, domain)
			.set(CONTRACT_INFO.CONTRACT, contractId)
			.set(CONTRACT_INFO.NAME, "SEPE_CONTRATO")
			.set(CONTRACT_INFO.EXPRESSION, "PENDING")
			.set(CONTRACT_INFO.START_DATE, contractStartDate)
			.set(CONTRACT_INFO.END_DATE, contractEndDate)
			.set(CONTRACT_INFO.CREATION_USER, "admin")
			.set(CONTRACT_INFO.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
			.execute();
		
		dslContext.insertInto(CONTRACT_INFO)
			.set(CONTRACT_INFO.DOMAIN, domain)
			.set(CONTRACT_INFO.CONTRACT, contractId)
			.set(CONTRACT_INFO.NAME, "SS_ALTA")
			.set(CONTRACT_INFO.EXPRESSION, "PENDING")
			.set(CONTRACT_INFO.START_DATE, contractStartDate)
			.set(CONTRACT_INFO.END_DATE, contractEndDate)
			.set(CONTRACT_INFO.CREATION_USER, "admin")
			.set(CONTRACT_INFO.CREATION_DATE, new Timestamp(new java.util.Date().getTime()))
			.execute();
		
		//ACTUALIZAR DURACION JORNADA
		TreeMap<java.util.Date, ArrayList<JourneyDuration>> contractJourneyDuration = contractData.getContractJourneyDuration().getContractJourneyDuration();
		if(null != contractJourneyDuration)
			for(Entry<java.util.Date, ArrayList<JourneyDuration>> entry : contractJourneyDuration.entrySet()) {
				for(JourneyDuration journey : entry.getValue()) {
					 java.util.Date endDateAux = journey.getEndDate();
					  
					 if(null != journey.getExpression() && "NL" != journey.getExpression())
						 dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
									CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
								.values(domain, journey.getName(), contractId, journey.getExpression(), 
										new Date(journey.getStartDate().getTime()), (null == endDateAux) ? null : new Date(endDateAux.getTime()))
								.execute();
					 else {
						 dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
									CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
								.values(domain, journey.getName(), contractId, null, 
										new Date(journey.getStartDate().getTime()), (null == endDateAux) ? null : new Date(endDateAux.getTime()))
								.execute();
					 }
				 }
			}
		
		if(null != contractData.getPartialityCoef()) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "COEFICIENTE_PARCIALIDAD")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, contractData.getPartialityCoef().toString())
				.set(CONTRACT_DATA.START_DATE, contractStartDate)
				.set(CONTRACT_DATA.END_DATE, contractEndDate)
				.execute();
		}
		
		if(null != contractData.getMdctz() && !AonStringUtils.equalsIgnoreCase(contractData.getMdctz(), "-1")) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "MODELO_COTIZACION_AGRARIO")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, contractData.getMdctz())
				.set(CONTRACT_DATA.START_DATE, contractStartDate)
				.set(CONTRACT_DATA.END_DATE, contractEndDate)
				.execute();
		}
		
		if(null != contractData.getJourneyType()){
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "TIEMPO_COMPLETO")
				.set(CONTRACT_DATA.CONTRACT, contractId)
				.set(CONTRACT_DATA.EXPRESSION, (contractData.getJourneyType() == 0) ? "false" : "true")
				.set(CONTRACT_DATA.START_DATE, contractStartDate)
				.set(CONTRACT_DATA.END_DATE, contractEndDate)
				.execute();
		}
		
		contractData.setContractId(contractId);
		employeeContractData.setContractInfo(contractData);
		
		downloadClausesToContract(dslContext, contractId, domain);
		
		System.out.println("GUARDADO -> CONTRACT : " + contractId);
		
		return employeeContractData;
	}
	
	private static void downloadClausesToContract(DSLContext dslContext, Integer contractId, Integer domainId) {
		Integer parentDomainId = dslContext.select(DOMAIN.PARENT).from(DOMAIN).where(DOMAIN.ID.eq(domainId)).fetchOne(DOMAIN.PARENT);
	
		Result<Record> clauseRecords = dslContext.select().from(CONTRACT_CLAUSE)
				.where(CONTRACT_CLAUSE.CONTRACT.isNull())
				.and(CONTRACT_CLAUSE.DOMAIN.eq(domainId)
					.or(CONTRACT_CLAUSE.DOMAIN.eq(parentDomainId))
				).and(CONTRACT_CLAUSE.GENERAL.eq((byte)1))
				.fetch();
		
		for(Record clauseRecord : clauseRecords) {
			dslContext.insertInto(CONTRACT_CLAUSE)
				.set(CONTRACT_CLAUSE.DOMAIN, domainId)
				.set(CONTRACT_CLAUSE.CONTRACT, contractId)
				.set(CONTRACT_CLAUSE.LINE, clauseRecord.get(CONTRACT_CLAUSE.LINE))
				.set(CONTRACT_CLAUSE.NAME, clauseRecord.get(CONTRACT_CLAUSE.NAME))
				.set(CONTRACT_CLAUSE.DESCRIPTION, clauseRecord.get(CONTRACT_CLAUSE.DESCRIPTION))
				.set(CONTRACT_CLAUSE.GENERAL, clauseRecord.get(CONTRACT_CLAUSE.GENERAL))
				.execute();
		}
	}

	private static Record getEmployeeRecord(DSLContext dslContext, Integer contractId) {
		return 
		dslContext
		.select()
		.from(CONTRACT)
		.innerJoin(PERSON).onKey()
		.innerJoin(ENTERPRISE_CCC).onKey()
		.where(CONTRACT.ID.eq(contractId))
		.fetchOne()
		;
		
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
		
		Integer employeeRegistryId = personTable.get(PERSON.REGISTRY);
		
		employeeData.setEmployeeId(personTable.get(PERSON.REGISTRY));
		employeeData.setDomain(personTable.get(PERSON.DOMAIN));
		employeeData.setBirthdate(personTable.get(PERSON.BIRTH_DATE));
		employeeData.setGender(personTable.get(PERSON.GENDER));
		employeeData.setCivilStatus(personTable.get(PERSON.MARITAL_STATUS));
		employeeData.setSsNumber(personTable.get(PERSON.SOCIAL_SECURITY_NUM));
		employeeData.setName(personTable.get(PERSON.NAME));
		employeeData.setSurName(personTable.get(PERSON.FIRST_SURNAME));
		employeeData.setSecondSurName(personTable.get(PERSON.SECOND_SURNAME));
		
		
		//REGISTRY TABLE
		Record registryTable = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(employeeRegistryId))
				.fetchOne();
		
		employeeData.setDocument(registryTable.get(REGISTRY.DOCUMENT));
		employeeData.setDocumentType(registryTable.get(REGISTRY.DOCUMENT_TYPE));
		employeeData.setNationality(registryTable.get(REGISTRY.NATIONALITY));
		
		//RADDRESS AND GEOZONE TABLE
		Result<Record> raddressTableRecords = dslContext.select().from(RADDRESS)
				.where(RADDRESS.REGISTRY.eq(employeeRegistryId))
				.fetch();
		
		if(raddressTableRecords.isNotEmpty()) {
			
			Record raddressTable = raddressTableRecords.get(0);
		
			employeeData.setRaddressId(raddressTable.get(RADDRESS.ID));
			employeeData.setStreetType(raddressTable.get(RADDRESS.STREET_TYPE));
			employeeData.setAddress(raddressTable.get(RADDRESS.ADDRESS));
			employeeData.setAddressInfo(raddressTable.get(RADDRESS.ADDRESS2));
			employeeData.setAddresNum(raddressTable.get(RADDRESS.NUMBER));
			employeeData.setAddressZip(raddressTable.get(RADDRESS.ZIP));
			employeeData.setAddressCity(raddressTable.get(RADDRESS.MUNICIPALITY_CODE));
			
			Integer raddressGeozoneId = raddressTable.get(RADDRESS.GEOZONE);
			
			if(null == raddressGeozoneId) {
				employeeData.setGeozoneId(null);
				employeeData.setAddressProvinces(null);
			}else {
				Record geozoneTable = dslContext.select().from(GEOZONE)
						.where(GEOZONE.ID.eq(raddressGeozoneId))
						.fetchOne();
				
				employeeData.setGeozoneId(geozoneTable.get(GEOZONE.ID));
				employeeData.setAddressProvinces(geozoneTable.get(GEOZONE.CODE));
			}
		}
		
		//RMEDIA TABLE
		Result<Record> rmediaTable = dslContext.select().from(RMEDIA)
				.where(RMEDIA.REGISTRY.eq(employeeRegistryId))
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
		Result<Record> rPayMethodRecords = dslContext.select().from(RPAYMETHOD)
			.where(RPAYMETHOD.REGISTRY.eq(employeeRegistryId))
			.fetch();
		
		if(rPayMethodRecords.isEmpty()) {
			employeeData.setRpaymethodId(null);
			
			employeeData.setPaymethodId(null);
			employeeData.setPayMethodType(null);
			
			employeeData.setRbankId(null);
			employeeData.setAccount(null);
			employeeData.setBic(null);
			
			Result<Record> rbankRecords = dslContext.select().from(RBANK)
					.where(RBANK.REGISTRY.eq(personTable.get(PERSON.REGISTRY)))
					.orderBy(RBANK.ID.desc())
					.fetch();
			
			if(rbankRecords.isNotEmpty()) {
				employeeData.setRbankId(rbankRecords.get(0).get(RBANK.ID));
				employeeData.setAccount(rbankRecords.get(0).get(RBANK.BANK_ACCOUNT));
				employeeData.setBic(rbankRecords.get(0).get(RBANK.BIC));
			}
		}else {
			Record rPayMethodRecord = rPayMethodRecords.get(0);
			
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
		
		//GET RBANKS
		Result<Record> rbankRecords = dslContext.select().from(RBANK)
				.where(RBANK.REGISTRY.eq(personTable.get(PERSON.REGISTRY)))
				.fetch();
		
		for(Record r: rbankRecords) {
			employeeData.addRbank(r.get(RBANK.ID), r.get(RBANK.BANK_ACCOUNT), r.get(RBANK.BIC), r.get(RBANK.ALIAS));
		}
		
		
		// ------------------------------------------------ CONTRACT INFO ---------------------------------------------------------
		System.out.println("CONTRACT = " + contract);
		System.err.println("CONTRACT ERR = " + contract);
		
		// HAS PAYROLL
		Result<Record> salaryRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contract))
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
			contractData.setContractSalariesInfo(createSalariesInfo(contractData, salaryRecords));
		}
		
		//CONTRACT TABLE
		Record contractTable = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contract))
				.fetchOne();
		
		contractData.setContractId(contract);
		contractData.setStartDate(contractTable.get(CONTRACT.START_DATE));
		// Set default old dates
		contractData.setOriginalStartDate(contractTable.get(CONTRACT.START_DATE));
		contractData.setOriginalEndDate(contractTable.get(CONTRACT.END_DATE));
		contractData.setEndDate(contractTable.get(CONTRACT.END_DATE));
		contractData.setSeniorityDate(contractTable.get(CONTRACT.SENIORITY_DATE));
		contractData.setAgreementCategory(contractTable.get(CONTRACT.CATEGORY_DESCRIPTION));
		contractData.setSsRegimen(contractTable.get(CONTRACT.SS_REGIME));
		
		Integer workplaceId = contractTable.get(CONTRACT.WORKPLACE);
		
		//WORKPLACE TABLE		
		contractData.setWorkplaceId(workplaceId);
		
		Record workplaceRecord = dslContext.select().from(WORKPLACE).where(WORKPLACE.ID.eq(workplaceId)).fetchOne();
		
		contractData.setWorkplaceName(workplaceRecord.get(WORKPLACE.DESCRIPTION));
		
		Record raddressRecord = dslContext.select().from(RADDRESS)
				.where(RADDRESS.ID.eq(
						dslContext.select(WORKPLACE.ADDRESS).from(WORKPLACE)
							.where(WORKPLACE.ID.eq(workplaceId))
							.fetchOne(WORKPLACE.ADDRESS)
				)).fetchOne();
		
		contractData.setWorkplaceZIP(raddressRecord.get(RADDRESS.MUNICIPALITY_CODE));
		contractData.setWorkplaceFullAddress(raddressRecord.get(RADDRESS.STREET_TYPE)+". "+raddressRecord.get(RADDRESS.ADDRESS)+" "+raddressRecord.get(RADDRESS.NUMBER));
		
		if(contractData.getSsRegimen() != 3){ //NO ES RETA
			//ENTERPRISE ACTIVITY TABLE
			Integer enterpriseActivityId = contractTable.get(CONTRACT.ENTERPRISE_ACTIVITY);
			contractData.setActivityId(enterpriseActivityId);
			
			//ENTERPRISE DATA
			String enterpriseDocument = dslContext.select(REGISTRY.DOCUMENT).from(REGISTRY)
					.where(REGISTRY.ID.eq(
							dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(employeeData.getDomain())).fetchOne(ENTERPRISE.REGISTRY)
					)).fetchOne(REGISTRY.DOCUMENT);
			
			contractData.setEnterpriseCIF(enterpriseDocument);
			
			//ENTERPRISE CCC TABLE
			Integer enterpriseCCCId = contractTable.get(CONTRACT.ENTERPRISE_CCC);
			
			if(null == enterpriseCCCId) {
				contractData.setCccId(null);
				contractData.setCccType(null);
				
			}else {
				Record enterpriseCCCTable = dslContext.select().from(ENTERPRISE_CCC)
						.where(ENTERPRISE_CCC.ID.eq(enterpriseCCCId))
						.fetchOne();
				
				contractData.setCccId(enterpriseCCCTable.get(ENTERPRISE_CCC.ID));
				contractData.setCccType(enterpriseCCCTable.get(ENTERPRISE_CCC.TYPE));
				contractData.setCompleteCCC(getCCCRegimeCode(enterpriseCCCTable.get(ENTERPRISE_CCC.TYPE)) + enterpriseCCCTable.get(ENTERPRISE_CCC.CCC));
			}
		}
		
		//AGREEMENT LEVEL TABLE
		Integer agreementLevelId = contractTable.get(CONTRACT.AGREEMENT_LEVEL);
		
		if(null == agreementLevelId) {
			contractData.setAgreementLevelId(null);
			contractData.setAgreementId(null);
		}else {
			Record agreementLevelTable = dslContext.select().from(AGREEMENT_LEVEL)
					.where(AGREEMENT_LEVEL.ID.eq(agreementLevelId))
					.fetchOne();
			try {
				contractData.setAgreementLevelId(agreementLevelTable.get(AGREEMENT_LEVEL.ID));
				
				//AGREEMENT TABLE
				Integer agreementId = agreementLevelTable.get(AGREEMENT_LEVEL.AGREEMENT);
				
				Record agreementTable = dslContext.select().from(AGREEMENT)
						.where(AGREEMENT.ID.eq(agreementId))
						.fetchOne();
				
				contractData.setAgreementId(agreementId);
				contractData.setAgreementColective(agreementTable.get(AGREEMENT.SS_NUMBER));
				
			} catch ( Throwable t ) {
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
					.where(CONTRACT_DATA.CONTRACT.eq(contract))
					.orderBy(CONTRACT_DATA.START_DATE)
					.fetch();
			}else {
				contractDataTable = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(contract))
						.and(CONTRACT_DATA.END_DATE.ge(currentDate).or(CONTRACT_DATA.END_DATE.isNull()))
						.fetch();
				
				if(contractDataTable.isEmpty())
					contractDataTable = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contract))
					.orderBy(CONTRACT_DATA.ID)
					.fetch();
			}
		}else {
			
			if(contractData.getStartDate().after(currentDate)) {
				contractDataTable = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(contract))
						.and(CONTRACT_DATA.START_DATE.le(new Date(contractData.getStartDate().getTime())))
						.and(CONTRACT_DATA.END_DATE.ge(new Date(contractData.getStartDate().getTime())).or(CONTRACT_DATA.END_DATE.isNull()))
						.fetch();
			}else
				contractDataTable = dslContext.select().from(CONTRACT_DATA)
						.where(CONTRACT_DATA.CONTRACT.eq(contract))
						.and(CONTRACT_DATA.END_DATE.isNull().or(CONTRACT_DATA.END_DATE.ge(currentDate)))
						.orderBy(CONTRACT_DATA.START_DATE.asc())
						.fetch();
			
			if(contractDataTable.isEmpty())
				contractDataTable = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contract))
				.orderBy(CONTRACT_DATA.ID)
				.fetch();
		}
			
		Map<String, String> contractDataMap = new HashMap<>();
		
		for(Record r : contractDataTable){
			contractDataMap.put(r.get(CONTRACT_DATA.NAME), r.get(CONTRACT_DATA.EXPRESSION));
			
			if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "TC2")) {
				contractData.setContracttypeId(r.get(CONTRACT_DATA.ID));
				contractData.setContractType(r.get(CONTRACT_DATA.EXPRESSION));
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "GRUPO_COTIZACION")) {
				contractData.setQuotegroupId(r.get(CONTRACT_DATA.ID));
				contractData.setQuoteGroup(r.get(CONTRACT_DATA.EXPRESSION));
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "DIAS_MES")) {
				if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.EXPRESSION), "30")) {
					contractData.setQuoteGroupIdxMonth(true);
					contractData.setQuoteGroupIdxMonthId(r.get(CONTRACT_DATA.ID));
				}
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "OCUPACION")) {
				contractData.setOcupationId(r.get(CONTRACT_DATA.ID));
				contractData.setOcupation(r.get(CONTRACT_DATA.EXPRESSION));
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "TIEMPO_COMPLETO")) {
				contractData.setJourneytypeId(r.get(CONTRACT_DATA.ID));
				contractData.setJourneyType(r.get(CONTRACT_DATA.EXPRESSION).equalsIgnoreCase("true") ? (byte) 1 : (byte) 0);
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "DISCONTINUOS")) {
				contractData.setDiscontinuos(r.get(CONTRACT_DATA.EXPRESSION).equalsIgnoreCase("true") ? true : false);
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "COEFICIENTE_PARCIALIDAD")) {
				contractData.setPartialityCoefId(r.get(CONTRACT_DATA.ID));
				String partiality = r.get(CONTRACT_DATA.EXPRESSION);
				partiality = partiality.replace(',', '.');
				contractData.setPartialityCoef(Double.parseDouble(partiality));
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "MODELO_COTIZACION_AGRARIO")) {
				contractData.setMdctzId(r.get(CONTRACT_DATA.ID));
				contractData.setMdctz(r.get(CONTRACT_DATA.EXPRESSION));
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "RLCE")) {
				contractData.setRlceId(r.get(CONTRACT_DATA.ID));
				contractData.setRlce(r.get(CONTRACT_DATA.EXPRESSION));
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "COLECTIVO_TRABAJADORES")) {
				contractData.setEmployeesColective(r.get(CONTRACT_DATA.EXPRESSION));
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "TIPO_TRIBUTACION")) {
				contractData.setMdTBT(Byte.parseByte(parseContractTable(r.get(CONTRACT_DATA.EXPRESSION))));
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "SEPE_ID")) {
				contractData.setSepeId(r.get(CONTRACT_DATA.EXPRESSION));
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "SEPE_EXTENSION_ID")) {
				contractData.setSepeExtensionId(r.get(CONTRACT_DATA.EXPRESSION));
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "SEPE_TRANSFORM_ID")) {
				contractData.setSepeTransformId(r.get(CONTRACT_DATA.EXPRESSION));
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "TRANSFORM_DATE")) {
				try {
					contractData.setTransformDate(formatDate.parse(r.get(CONTRACT_DATA.EXPRESSION)));
					if(null != contractData.getTransformDate()) contractData.setOriginalEndDate(DateUtils.addDays2Date(contractData.getTransformDate(), -1));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else if(AonStringUtils.equalsIgnoreCase(r.get(CONTRACT_DATA.NAME), "EXTENSION_DATE")) {
				contractData.setHasExtension(true);
				try {
					contractData.setExtensionDate(formatDate.parse(r.get(CONTRACT_DATA.EXPRESSION)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		//CONTRACT INFO TABLE
		Result<Record> contractInfoTableRecords = dslContext.select().from(CONTRACT_INFO)
			.where(CONTRACT_INFO.CONTRACT.eq(contract))
			.and(CONTRACT_INFO.NAME.eq("OPCION_CONTRATO"))
			.and(CONTRACT_INFO.START_DATE.le(currentDate))
			.and(CONTRACT_INFO.END_DATE.isNull().or(CONTRACT_INFO.END_DATE.ge(currentDate)))
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
				.where(CONTRACT_INFO.CONTRACT.eq(contract))
				.and(CONTRACT_INFO.NAME.eq("RETA"))
				.fetchOne();
		
		if(null == contractInfoTable)
			contractData.setRetaId(null);
		else
			contractData.setRetaId(contractInfoTable.get(CONTRACT_INFO.ID));
		
		ContractType contractTypeObj = new ContractType();
		ContractTypeRecord contractTypeRecord = null;
		try {
			contractTypeRecord = contractTypeObj.getContractType(Integer.parseInt(contractData.getContractType()));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(canHaveHoursAviable(contractData.getJourneyType(), contractTypeRecord)) {
		
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
		
		}
		
		// ---------------------------------------------- CheckSettle and Certifica2
		
		Result<Record> settlementRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contract))
				.and(SALARY.TYPE.eq((byte)2))
				.orderBy(SALARY.ID.desc())
				.fetch();
		
		contractData.setHasSettle(settlementRecords.isNotEmpty());
		if(settlementRecords.isNotEmpty()) {
			Record settlementRecord = settlementRecords.get(0);
			contractData.setHolidaysDate(settlementRecord.get(SALARY.END_DATE));
			
			Result<SalaryDataRecord> holidayRecords = dslContext.selectFrom(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(settlementRecord.get(SALARY.ID)))
					.and(SALARY_DATA.NAME.eq("DIAS_VACACIONES_NO_DISFRUTADOS"))
					.fetch();
			
			contractData.setSAA(holidayRecords.isNotEmpty() ? "001" : "015");
		}
		
		List<Integer> certifca2BatachIds = dslContext.select(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH)
				.from(CERTIFICA2_BATCH_DETAIL)
				.where(CERTIFICA2_BATCH_DETAIL.CONTRACT.eq(contract))
				.fetch(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH);
		
		if(certifca2BatachIds.isEmpty()) {
			contractData.setHasCertifica2(false);
		} else {
			Result<Record1<Integer>> sepeBatchAttachRecords = dslContext.select(SEPE_BATCH_ATTACH.ID).from(SEPE_BATCH_ATTACH)
				.where(SEPE_BATCH_ATTACH.SOURCE_BATCH.eq(certifca2BatachIds.get(0)))
				.and(SEPE_BATCH_ATTACH.MIMETYPE.eq((byte)5))
				.fetch();
			
			contractData.setHasCertifica2(sepeBatchAttachRecords.isNotEmpty());
		}
		
		// ---------------------------------------------- Sepe Id
		
		Result<Record> sepeIdRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.eq("SEPE_ID"))
				.and(CONTRACT_DATA.CONTRACT.eq(contract))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
		
		if(sepeIdRecords.isNotEmpty())
			contractData.setSepeId(sepeIdRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
		
		// ---------------------------------------------- Contract Extension
		
		Boolean hasExtension = false;
		
		try {
			Integer contractTypeValue = Integer.parseInt(contractData.getContractType());
			
			if(contractTypeValue >= 400) {
				Result<Record> extensionRecords = dslContext.select().from(CONTRACT_ATTACH).where(CONTRACT_ATTACH.CONTRACT.eq(contractData.getContractId())).and(CONTRACT_ATTACH.TYPE.eq((byte)13)).fetch();
				if(extensionRecords.isNotEmpty()) hasExtension = true;
			}
		} catch (NumberFormatException e) {}
		
		if(Boolean.FALSE.equals(contractData.isHasExtension())) contractData.setHasExtension(hasExtension);
		
		employeeContractInfo.setEmployeeInfo(employeeData);
		employeeContractInfo.setContractInfo(contractData);
		
		return employeeContractInfo;
	}

	private static boolean canHaveHoursAviable(Byte journeyType, ContractTypeRecord contractTypeRecord) {
		return (journeyType != null && journeyType == (byte) 0) || (null != contractTypeRecord && AonStringUtils.equalsIgnoreCase(contractTypeRecord.getJourneyType(), "P"));
	}

	private static ArrayList<ContractSalaryInfo> createSalariesInfo(ContractInfo contractData, Result<Record> salaryRecords) {
		ArrayList<ContractSalaryInfo> contractSalariesInfo = new ArrayList<com.esferalia.aon.gwt.payroll.shared.ContractSalaryInfo>();
		
		for(Record record : salaryRecords) {
			ContractSalaryInfo contractSalaryInfo = new ContractSalaryInfo();
			contractSalaryInfo.setType(getSalaryType(record.get(SALARY.TYPE)));
			contractSalaryInfo.setStart(record.get(SALARY.START_DATE));
			contractSalaryInfo.setEnd(record.get(SALARY.END_DATE));
			contractSalaryInfo.setTotalLiquid(record.get(SALARY.TOTAL_LIQUID));
			contractSalariesInfo.add(contractSalaryInfo);
		}
		
		return contractSalariesInfo;
	}
	
	private static String getSalaryType(Byte salaryType) {
		switch (salaryType) {
		case (byte)0:	
			return "N" + String.valueOf("\u00F3")  + "mina";
		case (byte)1:
			return "Extra";
		case (byte)2:
			return "Finiquito";
		case (byte)3:
			return "Atraso";
		default:
			return "N" + String.valueOf("\u00F3")  + "mina";
		}
	}
	
	private static EmployeeContractInfo setEmployeeInfoDB(DSLContext dslContext, EmployeeContractInfo employeeContractInfo) {
		
		ContractInfo contractData = employeeContractInfo.getContractInfo();
		EmployeeInfo employeeData = employeeContractInfo.getEmployeeInfo();
		Municipalities municipalities = new Municipalities();
		
		// ------------------------------------------------------------------------------------------------------------------------
		// ------------------------------------------------ EMPLOYEE INFO ---------------------------------------------------------
		// ------------------------------------------------------------------------------------------------------------------------
		
		dslContext.update(PERSON)
			.set(PERSON.BIRTH_DATE, (null == employeeData.getBirthdate()) ? null : new Date(employeeData.getBirthdate().getTime()))
			.set(PERSON.GENDER, employeeData.getGender())
			.set(PERSON.SOCIAL_SECURITY_NUM, employeeData.getSsNumber())
			.set(PERSON.NAME, employeeData.getName())
			.set(PERSON.FIRST_SURNAME, employeeData.getSurName())
			.set(PERSON.MARITAL_STATUS, employeeData.getCivilStatus())
			.set(PERSON.SECOND_SURNAME, employeeData.getSecondSurName())
			.where(PERSON.REGISTRY.eq(employeeData.getEmployeeId()))
			.execute();
		
		dslContext.update(REGISTRY)
			.set(REGISTRY.DOCUMENT, employeeData.getDocument())
			.set(REGISTRY.DOCUMENT_TYPE, employeeData.getDocumentType())
			.set(REGISTRY.DOCUMENT_COUNTRY,  AonStringUtils.isBlank(employeeData.getNationalityCode()) ? "ES" : employeeData.getNationalityCode())
			.set(REGISTRY.NATIONALITY,  AonStringUtils.isBlank(employeeData.getNationalityCode()) ? "ES" : employeeData.getNationalityCode())
			.set(REGISTRY.NAME, (null == employeeData.getSurName() ? "" :  employeeData.getSurName() + " ") + 
					(null == employeeData.getSecondSurName() ? "" : employeeData.getSecondSurName() + ", ") + 
					employeeData.getName())
			.where(REGISTRY.ID.eq(employeeData.getEmployeeId()))
			.execute();
		
		// Update salaries ssNumber and Document
		dslContext.update(SALARY)
			.set(SALARY.SOCIAL_SECURITY_NUMBER, employeeData.getSsNumber())
			.set(SALARY.EMPLOYEE_DOCUMENT, employeeData.getDocument())
			.where(SALARY.CONTRACT.eq(contractData.getContractId()))
			.execute();
		
		Integer domain = employeeData.getDomain();
		Integer registryId = employeeData.getEmployeeId();
		
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.eq(domain))
				.fetchOne();
		
		Integer parentDomain = domainRecord.get(DOMAIN.PARENT);
		Byte hasHeredity = domainRecord.get(DOMAIN.ENABLEHEREDITY);
		
		Integer rAddressId = null;
		
		if(null != employeeData.getAddressProvinces() && !AonStringUtils.equalsIgnoreCase(employeeData.getAddressProvinces(),"-1")) {
			
			Result<Record> geozone = null;
			
			if(hasHeredity == (byte)0)
				geozone = dslContext.select()
						.from(GEOZONE)
						.where(GEOZONE.CODE.eq(employeeData.getAddressProvinces()))
						.and(GEOZONE.DOMAIN.eq(domain))
						.fetch();
			else
				geozone = dslContext.select()
				.from(GEOZONE)
				.where(GEOZONE.CODE.eq(employeeData.getAddressProvinces()))
					.and(GEOZONE.DOMAIN.eq(domain)
							.or(GEOZONE.DOMAIN.eq(parentDomain)))
				.fetch();
			
			Integer geozoneId = null;
			
			if(geozone == null){
				Result<Record1<String>> names = dslContext.select(GEOZONE.NAME)
					.from(GEOZONE)
					.where(GEOZONE.CODE.eq(employeeData.getAddressProvinces()))
					.fetch();
				
				if(!names.isEmpty()){
					GeozoneRecord geozoneRecord  = dslContext.insertInto(GEOZONE)
							.set(GEOZONE.DOMAIN, domain)
							.set(GEOZONE.NAME, names.get(0).value1())
							.set(GEOZONE.CODE, employeeData.getAddressProvinces())
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
						.set(RADDRESS.ADDRESS2, employeeData.getAddressInfo())
						.set(RADDRESS.NUMBER, employeeData.getAddresNum())
						.set(RADDRESS.ZIP, employeeData.getAddressZip())
						.set(RADDRESS.CITY, municipalities.getMunicipalityByZip(employeeData.getAddressCity()))
						.set(RADDRESS.MUNICIPALITY_CODE, employeeData.getAddressCity())
						.set(RADDRESS.GEOZONE, geozoneId)
						.returning(RADDRESS.ID, RADDRESS.GEOZONE)
						.fetchOne();
				
				rAddressId = rAddressRecord.getId();
				employeeData.setRaddressId(rAddressId);
			}else{
				dslContext.update(RADDRESS)
						.set(RADDRESS.STREET_TYPE, employeeData.getStreetType())
						.set(RADDRESS.ADDRESS, employeeData.getAddress())
						.set(RADDRESS.ADDRESS2, employeeData.getAddressInfo())
						.set(RADDRESS.NUMBER, employeeData.getAddresNum())
						.set(RADDRESS.ZIP, employeeData.getAddressZip())
						.set(RADDRESS.CITY, municipalities.getMunicipalityByZip(employeeData.getAddressCity()))
						.set(RADDRESS.MUNICIPALITY_CODE, AonStringUtils.leftPad(employeeData.getAddressCity(), 5, '0'))
						.set(RADDRESS.GEOZONE, geozoneId)
						.where(RADDRESS.ID.eq(rAddressId))
						.execute();
			}
			
			if(geozone == null && null != geozoneId){
				Integer rAddressGeozone = geozoneId;
				
				GeozoneRecord geozoneParentRecord  = dslContext.insertInto(GEOZONE)
						.set(GEOZONE.DOMAIN, domain)
						.set(GEOZONE.NAME, "ESPA" + String.valueOf("\u00D1") + "A")
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
		} else {
			if(employeeData.getRaddressId() != null) {
				dslContext.delete(RADDRESS).where(RADDRESS.ID.eq(employeeData.getRaddressId())).execute();
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
		
		// Insertar RBank si lo precisa
		String account = employeeData.getAccount();
		Integer rbankTableId = null;
		if(!AonStringUtils.isBlank(account)) {
			
			Result<Record> findRBankRecord = dslContext.select().from(RBANK)
					.where(RBANK.BANK_ACCOUNT.eq(account))
					.and(RBANK.REGISTRY.eq(registryId))
					.and(RBANK.DOMAIN.eq(domain))
					.fetch();
			
			String alias = AonStringUtils.isNotBlank(account) ? employeeData.getBankAlias() : "CUENTA";
			
			if(null != account && account.length() > 8 && AonStringUtils.isBlank(alias)) {
				String codeBank = account.substring(4, 8);
				alias = BankEntities.getBankEntity(codeBank);
			}
			
			if(alias.length() > 25)
				alias = alias.substring(0, 24);
			
			if(!findRBankRecord.isEmpty()) {
				rbankTableId = findRBankRecord.get(0).get(RBANK.ID); 
				
				dslContext.update(RBANK)
					.set(RBANK.BIC, employeeData.getBic())
					.set(RBANK.ALIAS, alias)
					.where(RBANK.ID.eq(rbankTableId))
					.execute();
			} else {
				RbankRecord rbankRecord = dslContext.insertInto(RBANK)
						.set(RBANK.DOMAIN, domain)
						.set(RBANK.REGISTRY, registryId)
						.set(RBANK.BANK_ACCOUNT, null == account ? "" : account)
						.set(RBANK.BIC, employeeData.getBic())
						.set(RBANK.ALIAS, alias)
						.set(RBANK.ACTIVE, (byte) 1)
						.returning(RBANK.ID)
						.fetchOne();
				 
				rbankTableId = rbankRecord.get(RBANK.ID);
			}
			
			employeeData.setRbankId(rbankTableId);
		}
		
		// CheckPayMethod
		Integer payMethodId = employeeData.getPaymethodId();
		
		// No tiene metodo de pago por lo que hay que borrar rpaymethod si tiene
		if(null == payMethodId) {
			if(null != employeeData.getRpaymethodId()) {
				dslContext.delete(RPAYMETHOD)
					.where(RPAYMETHOD.ID.eq(employeeData.getRpaymethodId()))
					.execute();
				
				employeeData.setRpaymethodId(null);
			}
			
		// Si tiene payMethod hay que crear/actualizar rpaymethod y poner rbank si es tipo 5
		} else {
			Byte payMethodType = dslContext.select(PAY_METHOD.TYPE).from(PAY_METHOD)
					.where(PAY_METHOD.ID.eq(payMethodId)).fetchOne(PAY_METHOD.TYPE);
			
			// Creamos un rpaymethod
			if(null == employeeData.getRpaymethodId()) {
			
				RpaymethodRecord rpaymethodRecord = null;
				
				if(payMethodType == (byte) 5)
					rpaymethodRecord = dslContext.insertInto(RPAYMETHOD)
							.set(RPAYMETHOD.DOMAIN, domain)
							.set(RPAYMETHOD.REGISTRY, registryId)
							.set(RPAYMETHOD.PAY_METHOD, payMethodId)
							.set(RPAYMETHOD.RBANK, rbankTableId)
							.set(RPAYMETHOD.NUMBER_OF_PYMNTS, (short) 1)
							.set(RPAYMETHOD.PYMNT_DAYS, "")
							.returning(RPAYMETHOD.ID)
							.fetchOne();
				else {
					
					Integer rbank = null;
					
					rpaymethodRecord = dslContext.insertInto(RPAYMETHOD)
						.set(RPAYMETHOD.DOMAIN, domain)
						.set(RPAYMETHOD.REGISTRY, registryId)
						.set(RPAYMETHOD.PAY_METHOD, payMethodId)
						.set(RPAYMETHOD.RBANK, rbank)
						.set(RPAYMETHOD.NUMBER_OF_PYMNTS, (short) 1)
						.set(RPAYMETHOD.PYMNT_DAYS, "")
						.returning(RPAYMETHOD.ID)
						.fetchOne();
				}
					
				Integer rpayMethodTableId = rpaymethodRecord.get(RPAYMETHOD.ID);
				
				employeeData.setRpaymethodId(rpayMethodTableId);
				
			// Actualizamos el existente
			} else {
				
				if(AonStringUtils.isBlank(account)) {
					rbankTableId = null;
					employeeData.setRbankId(null);
				}
				
				// Se le asigna el banco independiemente del tipo de metodo de pago
				dslContext.update(RPAYMETHOD)
					.set(RPAYMETHOD.PAY_METHOD, payMethodId)
					.set(RPAYMETHOD.RBANK, rbankTableId)
					.where(RPAYMETHOD.ID.eq(employeeData.getRpaymethodId()))
					.execute();
				
//				if(payMethodType == (byte) 5)
//					dslContext.update(RPAYMETHOD)
//							.set(RPAYMETHOD.PAY_METHOD, payMethodId)
//							.set(RPAYMETHOD.RBANK, rbankTableId)
//							.where(RPAYMETHOD.ID.eq(employeeData.getRpaymethodId()))
//							.execute();
//				else {
//					
//					Integer rbank = null;
//					dslContext.update(RPAYMETHOD)
//						.set(RPAYMETHOD.PAY_METHOD, payMethodId)
//						.set(RPAYMETHOD.RBANK, rbank)
//						.where(RPAYMETHOD.ID.eq(employeeData.getRpaymethodId()))
//						.execute();
//				}
				
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
			ContractType contractType = new ContractType();
			ContractTypeRecord contractTypeRecord = contractType.getContractType(Integer.parseInt(contractData.getContractType()));
			
			dslContext.update(CONTRACT)
				.set(CONTRACT.ENTERPRISE_CCC, contractData.getCccId())
				.set(CONTRACT.ENTERPRISE_ACTIVITY, contractData.getActivityId())
				.set(CONTRACT.SS_REGIME, contractData.getSsRegimen())
				.where(CONTRACT.ID.eq(contractData.getContractId()))
				.execute();
			
			if(!contractData.hasPayroll()) {
				if(null == contractData.getContracttypeId()){
					if(null != contractData.getContractType()){
						String contractTypeStr = AonStringUtils.leftPad(contractData.getContractType(), 3, "0");
						ContractDataRecord tc2Record = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
								CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
							.values(contractData.getContracttypeId(), domain, "TC2", contractData.getContractId(), "\"" + contractTypeStr + "\"", 
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
						String contractTypeStr = AonStringUtils.leftPad(contractData.getContractType(), 3, "0");
						dslContext.update(CONTRACT_DATA)
							.set(CONTRACT_DATA.EXPRESSION, "\""+ contractTypeStr +"\"")
							.set(CONTRACT_DATA.START_DATE, startDate)
							.set(CONTRACT_DATA.END_DATE, endDate)
							.where(CONTRACT_DATA.ID.eq(contractData.getContracttypeId()))
							.execute();
					}
				}
					
				if(null == contractData.getQuotegroupId()){
					if(null != contractData.getQuoteGroup()){
						ContractDataRecord contizacionRecord = null;
						
						contizacionRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
								CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
							.values(contractData.getQuotegroupId(), domain, "GRUPO_COTIZACION", contractData.getContractId(), "\""+ contractData.getQuoteGroup()+"\"", 
									startDate, endDate)
							.returning(CONTRACT_DATA.ID)
							.fetchOne();
						
						contractData.setQuotegroupId(contizacionRecord.getId());
					}
				}else{
					if(null == contractData.getQuoteGroup()){
						dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getQuotegroupId())).execute();
						contractData.setQuotegroupId(null);
						contractData.setQuoteGroup(null);
					}else{
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
						
						ocupacionRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
								CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
							.values(contractData.getOcupationId(), domain, "OCUPACION", contractData.getContractId(), "\""+ contractData.getOcupation()+"\"", 
									startDate, endDate)
							.returning(CONTRACT_DATA.ID)
							.fetchOne();
						
						contractData.setOcupationId(ocupacionRecord.getId());
					}
				}else{
					if(null == contractData.getOcupation()){
						dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getOcupationId())).execute();
						contractData.setOcupationId(null);
						contractData.setOcupation(null);
					}else{
						dslContext.update(CONTRACT_DATA)
						.set(CONTRACT_DATA.EXPRESSION, "\""+ contractData.getOcupation()+"\"")
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.where(CONTRACT_DATA.ID.eq(contractData.getOcupationId()))
						.execute();
					}
				}
				
				if(null == contractData.getPartialityCoefId()){
					if(null != contractData.getPartialityCoef()){
						ContractDataRecord ocupacionRecord = null;
						
						ocupacionRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
								CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
							.values(contractData.getPartialityCoefId(), domain, "COEFICIENTE_PARCIALIDAD", contractData.getContractId(), contractData.getPartialityCoef().toString(), 
									startDate, endDate)
							.returning(CONTRACT_DATA.ID)
							.fetchOne();
						
						contractData.setPartialityCoefId(ocupacionRecord.getId());
					}
				}else{
					if(null == contractData.getPartialityCoef()){
						dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getPartialityCoefId())).execute();
						contractData.setPartialityCoefId(null);
						contractData.setPartialityCoef(null);
					}else{
						dslContext.update(CONTRACT_DATA)
						.set(CONTRACT_DATA.EXPRESSION, contractData.getPartialityCoef().toString())
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.where(CONTRACT_DATA.ID.eq(contractData.getPartialityCoefId()))
						.execute();
					}
				}
				
			}
			
			// I. Cotizacion mensual
			if(null == contractData.getQuoteGroupIdxMonthId()){
				if(contractData.getQuoteGroupIdxMonth()){
					ContractDataRecord contizacionRecord = null;
					
					contizacionRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
							CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
						.values(contractData.getQuoteGroupIdxMonthId(), domain, "DIAS_MES", contractData.getContractId(), "30", startDate, endDate)
						.returning(CONTRACT_DATA.ID)
						.fetchOne();
					
					contractData.setQuoteGroupIdxMonthId(contizacionRecord.getId());
				}
			}else{
				if(!contractData.getQuoteGroupIdxMonth()){
					dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getQuoteGroupIdxMonthId())).execute();
					contractData.setQuoteGroupIdxMonthId(null);
					contractData.setQuoteGroupIdxMonth(false);
				}
			}
			
			if(null == contractData.getMdctzId()){
				if(null != contractData.getMdctz() && !AonStringUtils.equalsIgnoreCase(contractData.getMdctz(), "-1")){
					ContractDataRecord mdCtzRecord = null;
					
					mdCtzRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
							CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
						.values(contractData.getMdctzId(), domain, "MODELO_COTIZACION_AGRARIO", contractData.getContractId(), contractData.getMdctz(), 
								startDate, endDate)
						.returning(CONTRACT_DATA.ID)
						.fetchOne();
					
					contractData.setMdctzId(mdCtzRecord.getId());
				}
			}else{
				if(null == contractData.getMdctz() || ("-1" == contractData.getMdctz() || "-1".equalsIgnoreCase(contractData.getMdctz()))){
					dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getMdctzId())).execute();
					contractData.setMdctzId(null);
					contractData.setMdctz(null);
				}else{
					dslContext.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.EXPRESSION, contractData.getMdctz())
					.set(CONTRACT_DATA.START_DATE, startDate)
					.set(CONTRACT_DATA.END_DATE, endDate)
					.where(CONTRACT_DATA.ID.eq(contractData.getMdctzId()))
					.execute();
				}
			}
			
			if(null == contractData.getRlceId()){
				if(AonStringUtils.isNotBlank(contractData.getRlce())){
					ContractDataRecord rlceRecord = null;
					
					rlceRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
							CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
						.values(contractData.getRlceId(), domain, "RLCE", contractData.getContractId(), "\""+ contractData.getRlce()+"\"", 
								startDate, endDate)
						.returning(CONTRACT_DATA.ID)
						.fetchOne();
					
					contractData.setRlceId(rlceRecord.getId());
				}
			}else{
				if(AonStringUtils.isBlank(contractData.getRlce())){
					dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getRlceId())).execute();
					contractData.setRlceId(null);
					contractData.setRlce(null);
				}else{
					dslContext.update(CONTRACT_DATA)
					.set(CONTRACT_DATA.EXPRESSION, "\""+ contractData.getRlce()+"\"")
					.set(CONTRACT_DATA.START_DATE, startDate)
					.set(CONTRACT_DATA.END_DATE, endDate)
					.where(CONTRACT_DATA.ID.eq(contractData.getRlceId()))
					.execute();
				}
			}
			
			// Employees Colective
			if(!contractTypeRecord.isTransform())
				dslContext.delete(CONTRACT_DATA)
					.where(CONTRACT_DATA.NAME.eq("COLECTIVO_TRABAJADORES"))
					.and(CONTRACT_DATA.CONTRACT.eq(contractData.getContractId()))
					.execute();
			
			if(AonStringUtils.isNotBlank(contractData.getEmployeesColective())) {
				dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domain)
					.set(CONTRACT_DATA.NAME, "COLECTIVO_TRABAJADORES")
					.set(CONTRACT_DATA.CONTRACT, contractData.getContractId())
					.set(CONTRACT_DATA.EXPRESSION, "\"" + contractData.getEmployeesColective() + "\"")
					.set(CONTRACT_DATA.START_DATE, startDate)
					.set(CONTRACT_DATA.END_DATE, endDate)
					.execute();
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
			
//			if(null != contractData.getJourneytypeId())
//				dslContext.delete(CONTRACT_DATA)
//					.where(CONTRACT_DATA.ID.eq(contractData.getJourneytypeId()))
//					.execute();
			
			if(null != contractData.getRetaId())
				dslContext.delete(CONTRACT_INFO)
				.where(CONTRACT_INFO.ID.eq(contractData.getRetaId()))
				.execute();
		
		} else {
			
			dslContext.update(CONTRACT)
			.set(CONTRACT.ENTERPRISE_CCC, (Integer) null)
			.set(CONTRACT.ENTERPRISE_ACTIVITY, (Integer) null)
			.set(CONTRACT.SS_REGIME, (byte) 3)
			.where(CONTRACT.ID.eq(contractData.getContractId()))
			.execute();
			
			if(null == contractData.getRetaId()){
				ContractInfoRecord retaRecord = dslContext.insertInto(CONTRACT_INFO, CONTRACT_INFO.ID, CONTRACT_INFO.DOMAIN, CONTRACT_INFO.CONTRACT, CONTRACT_INFO.NAME, CONTRACT_INFO.EXPRESSION, 
						CONTRACT_INFO.START_DATE, CONTRACT_INFO.END_DATE, CONTRACT_INFO.CREATION_USER, CONTRACT_INFO.CREATION_DATE, CONTRACT_INFO.MODIFICATION_USER,
						CONTRACT_INFO.MODIFICATION_DATE)
					.values(contractData.getRetaId(), domain, contractData.getContractId(), "RETA", "true", 
							startDate, endDate, (String) null, null, (String) null, null)
					.returning(CONTRACT_INFO.ID)
					.fetchOne();
				
				contractData.setRetaId(retaRecord.getId());
			}else{
				dslContext.update(CONTRACT_INFO)
				.set(CONTRACT_INFO.EXPRESSION, "true")
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
			
//			if(null != contractData.getQuotegroupId()){
//				dslContext.delete(CONTRACT_DATA)
//				.where(CONTRACT_DATA.ID.eq(contractData.getQuotegroupId()))
//				.execute();
//			
//				contractData.setQuotegroupId(null);
//				contractData.setQuoteGroup(null);
//			}
			
			if(null != contractData.getOcupationId()){
				dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.ID.eq(contractData.getOcupationId()))
				.execute();
			
				contractData.setOcupationId(null);
				contractData.setOcupation(null);
			}
			
			if(null != contractData.getPartialityCoefId()){
				dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.ID.eq(contractData.getPartialityCoefId()))
				.execute();
			
				contractData.setPartialityCoefId(null);
				contractData.setPartialityCoef(null);
			}
			
			if(null != contractData.getContractmodelId()){
				dslContext.delete(CONTRACT_INFO)
				.where(CONTRACT_INFO.ID.eq(contractData.getContractmodelId()))
				.execute();
			
				contractData.setContractmodelId(null);
				contractData.setContractModel(null);
			}
		}
		
		// Employees Colective
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.NAME.eq("TIPO_TRIBUTACION"))
			.and(CONTRACT_DATA.CONTRACT.eq(contractData.getContractId()))
			.execute();
		
		if(null != contractData.getMdTBT()) {
			dslContext.insertInto(CONTRACT_DATA)
				.set(CONTRACT_DATA.DOMAIN, domain)
				.set(CONTRACT_DATA.NAME, "TIPO_TRIBUTACION")
				.set(CONTRACT_DATA.CONTRACT, contractData.getContractId())
				.set(CONTRACT_DATA.EXPRESSION, parseContractTableStr(contractData.getMdTBT() + ""))
				.set(CONTRACT_DATA.START_DATE, startDate)
				.set(CONTRACT_DATA.END_DATE, endDate)
				.execute();
		}
		
		Integer contractType = AonStringUtils.isBlank(contractData.getContractType()) ? null : Integer.parseInt(contractData.getContractType());
		
		if(null == contractType || !isCompleteJourneyContract(contractType)) {
			
			if(null == contractData.getJourneytypeId()){
				if(null != contractData.getJourneyType()){
					ContractDataRecord journeyRecord = dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.ID, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
							CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
						.values(contractData.getJourneytypeId(), domain, "TIEMPO_COMPLETO", contractData.getContractId(), (contractData.getJourneyType() == 0) ? "false" : "true", 
								startDate, endDate)
						.returning(CONTRACT_DATA.ID)
						.fetchOne();
					
					contractData.setJourneytypeId(journeyRecord.getId());
				}
			} else{
				if(null == contractData.getJourneyType()){
					dslContext.delete(CONTRACT_DATA).where(CONTRACT_DATA.ID.eq(contractData.getJourneytypeId())).execute();
					contractData.setJourneytypeId(null);
					contractData.setJourneyType(null);
				}else{
					dslContext.update(CONTRACT_DATA)
						.set(CONTRACT_DATA.EXPRESSION, (contractData.getJourneyType() == 0) ? "false" : "true")
						.set(CONTRACT_DATA.START_DATE, startDate)
						.set(CONTRACT_DATA.END_DATE, endDate)
						.where(CONTRACT_DATA.ID.eq(contractData.getJourneytypeId()))
						.execute();
				}
			}
			
			TreeMap<java.util.Date, ArrayList<JourneyDuration>> contractJourneyDuration = contractData.getContractJourneyDuration().getContractJourneyDuration();
//			if(contractJourneyDuration.isEmpty()) {
			//ACTUALIZAR DURACION JORNADA
			dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.NAME.like("HORAS%"))
				.and(CONTRACT_DATA.CONTRACT.eq(contractData.getContractId()))
				.execute();
//			}
			
			if(!contractJourneyDuration.isEmpty() && (null != contractData.getJourneyType() && contractData.getJourneyType() == 0)){
			 
				for(Entry<java.util.Date, ArrayList<JourneyDuration>> entry : contractJourneyDuration.entrySet()) {
					 for(JourneyDuration journey : entry.getValue()) {
						 java.util.Date endDateAux = journey.getEndDate();
						  
						 if(null != journey.getExpression() && "NL" != journey.getExpression()) {
							 String expression = journey.getExpression();
							 expression = expression.replace(",", ".");
							
							 dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
										CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
									.values(domain, journey.getName(), contractData.getContractId(), expression, 
											new Date(journey.getStartDate().getTime()), (null == endDateAux) ? null : new Date(endDateAux.getTime()))
									.execute();
						 } else {
							 dslContext.insertInto(CONTRACT_DATA, CONTRACT_DATA.DOMAIN, CONTRACT_DATA.NAME, CONTRACT_DATA.CONTRACT, CONTRACT_DATA.EXPRESSION, 
										CONTRACT_DATA.START_DATE, CONTRACT_DATA.END_DATE)
									.values(domain, journey.getName(), contractData.getContractId(), null, 
											new Date(journey.getStartDate().getTime()), (null == endDateAux) ? null : new Date(endDateAux.getTime()))
									.execute();
						 }
					 }
				}
			}
			
		}
		
		checkPartialHours(dslContext, contractData.getContractId(), contractData.getContractType(), contractData.getEndDate());
		

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
			
		//UPDATE AGREEMENT SS NUMBER
		Integer agreementId = employeeContractInfo.getContractInfo().getAgreementId();
		if(null != agreementId) {
			String agreementSSNumber = dslContext.select(AGREEMENT.SS_NUMBER).from(AGREEMENT).where(AGREEMENT.ID.eq(agreementId)).fetchOne(AGREEMENT.SS_NUMBER);
			employeeContractInfo.getContractInfo().setAgreementColective(agreementSSNumber);
		}
		
		employeeContractInfo.setEmployeeInfo(employeeData);
		employeeContractInfo.setContractInfo(contractData);
		return employeeContractInfo;
	}

	// --------------------------------------- AUX METHODS -----------------------------
	
	private static void checkPartialHours(DSLContext dslContext, Integer contractId, String contractTypeValue, java.util.Date contractEndDate) {
		Integer contractType = AonStringUtils.isBlank(contractTypeValue) ? null : Integer.parseInt(contractTypeValue);
		
		if(null == contractType) return;
		
		Date endDateSQL = null == contractEndDate ? null : new Date(contractEndDate.getTime());
		
		if(isCompleteJourneyContract(contractType))
			dslContext.delete(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("HORAS_LUNES")
					.or(CONTRACT_DATA.NAME.eq("HORAS_MARTES"))
					.or(CONTRACT_DATA.NAME.eq("HORAS_MIERCOLES"))
					.or(CONTRACT_DATA.NAME.eq("HORAS_JUEVES"))
					.or(CONTRACT_DATA.NAME.eq("HORAS_VIERNES"))
					.or(CONTRACT_DATA.NAME.eq("HORAS_SABADO"))
					.or(CONTRACT_DATA.NAME.eq("HORAS_DOMINGO"))
					.or(CONTRACT_DATA.NAME.eq("COEFICIENTE_PARCIALIDAD"))
					.or(CONTRACT_DATA.NAME.eq("TIEMPO_COMPLETO"))
				).and(CONTRACT_DATA.END_DATE.isNull().or(CONTRACT_DATA.END_DATE.eq(endDateSQL)))
				.execute();
		
	}
	
	private static boolean isCompleteJourneyContract(Integer contractTypeCode) {
		ContractType contractTypeObj = new ContractType();
		ContractTypeRecord contractType = contractTypeObj.getContractType(contractTypeCode);
		return AonStringUtils.equalsIgnoreCase(contractType.getJourneyType(), "C");
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
	
	private static String getTypeDescription(byte typePay) {
		switch (typePay) {
		case (byte) 0:
			return "EFECTIVO";
		case (byte) 1:
			return "GIRO";
		case  (byte) 4:
			return "CHEQUE";
		case (byte) 5:
			return "TRANSFERENCIA";
		default:
			return "";
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
		try {
			exp = exp.split("\"")[1];
			return exp;
		}catch (Exception e) {
			return exp;
		}
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
		
	private static List<String> getContractOtherDataListNames(){
		List<String> contractOtherDataNames = new ArrayList<String>();
		
		// ------------------------------------------------------- Indefinite Table
		
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
		
		// ------------------------------------------------------- Temporal Table
		
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
		
		// ------------------------------------------------------- Formation Table
		
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
		
		// ------------------------------------------------------- Practice Table
		
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

	public static float getBaseCC(Connection conn, String docNum, java.util.Date dateFrom) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateFrom);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Result<Record> salaryData = dslContext.select().from(SALARY_DATA)
			.where(SALARY_DATA.NAME.eq("BASE_CGC"))
			.and(SALARY_DATA.SALARY.eq(
					dslContext.select(SALARY.ID).from(SALARY)
						.where(SALARY.EMPLOYEE_DOCUMENT.eq(docNum))
						.and(SALARY.END_DATE.eq(new Date(cal.getTimeInMillis())))
			)).fetch();
		
		if(salaryData.isNotEmpty())
			return Float.parseFloat(salaryData.get(0).get(SALARY_DATA.EXPRESSION));
		
		return 0;
	}

	public static float getBaseCP(Connection conn, String docNum, java.util.Date dateFrom) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateFrom);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Result<Record> salaryData = dslContext.select().from(SALARY_DATA)
			.where(SALARY_DATA.NAME.eq("BASE_CGP"))
			.and(SALARY_DATA.SALARY.eq(
					dslContext.select(SALARY.ID).from(SALARY)
						.where(SALARY.EMPLOYEE_DOCUMENT.eq(docNum))
						.and(SALARY.END_DATE.eq(new Date(cal.getTimeInMillis())))
			)).fetch();
		
		if(salaryData.isNotEmpty())
			return Float.parseFloat(salaryData.get(0).get(SALARY_DATA.EXPRESSION));
		
		return 0;
	}

	public static int getDays(Connection conn, String docNum, java.util.Date dateFrom) {
		DSLContext dslContext = DSL.using(conn, getDefaultSettings());
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateFrom);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Result<Record> salaryData = dslContext.select().from(SALARY_DATA)
			.where(SALARY_DATA.NAME.eq("DIAS_NOMINA"))
			.and(SALARY_DATA.SALARY.eq(
					dslContext.select(SALARY.ID).from(SALARY)
						.where(SALARY.EMPLOYEE_DOCUMENT.eq(docNum))
						.and(SALARY.END_DATE.eq(new Date(cal.getTimeInMillis())))
			)).fetch();
		
		if(salaryData.isNotEmpty()) {
			Double newDays = Double.parseDouble(salaryData.get(0).get(SALARY_DATA.EXPRESSION));
			return newDays.intValue();
		}
		
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

}
