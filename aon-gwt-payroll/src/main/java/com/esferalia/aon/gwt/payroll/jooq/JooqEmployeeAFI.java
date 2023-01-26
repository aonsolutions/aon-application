package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges.AFIChange;
import com.esferalia.aon.occam.api.model.type.ContractType;
import com.esferalia.aon.occam.api.model.type.ContractType.ContractTypeRecord;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqEmployeeAFI {
	
	// -------------------------------------------- Constructor
	
	protected JooqEmployeeAFI() {
		super();
	}
	
	// -------------------------------------------- Settings

	private static Settings settings = null;
	
	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}
	
	private static final String QUOTE_GROUP = "GRUPO_COTIZACION";
	private static final String TC2 = "TC2";
	private static final String PARTIALITY = "COEFICIENTE_PARCIALIDAD";
	private static final String OCUPATION = "OCUPACION";
	private static final String EMPLOYEECOLECTIVE = "COLECTIVO_TRABAJADORES";
	private static final String CNO = "CNO";
	
	public static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

	private static String stripDiacritics(String str) {
	    str = Normalizer.normalize(str, Normalizer.Form.NFD);
	    str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
	    return str;
	}
	
	// -------------------------------------------- Methods
	
	public static void setEmployeeAFI(Connection connection, Integer contractId, AFIChanges afiChangesMap) {
		setEmployeeAFIDB(DSL.using(connection, getDefaultSettings()), contractId, afiChangesMap);
	}
	
	public static AFIChanges getEmployeeAFI(Connection connection, Integer contractId) {
		return getEmployeeAFIDB(DSL.using(connection, getDefaultSettings()), contractId);
	}

	// -------------------------------------------- Methods.getEmployeeAFIInfo
	
	@SuppressWarnings("unchecked")
	public static JSONObject getEmployeeAFIInfo(
			String domainIdStr, 
			String domainName, 
			String contractIdStr,
			String fileName,
			Boolean isStartContract,
			Boolean isEndContract, 
			Boolean isChangeContract, 
			Boolean isQuoteContract, 
			Boolean isOcupationContract, 
			Boolean isPartialityCoefContract,
			String settleReason) {
		
		JSONObject employeeAFIJSON = new JSONObject();
		
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			Integer domainId = Integer.parseInt(domainIdStr);
			Integer contractId = Integer.parseInt(contractIdStr);
			
			Integer parentDomainId = dslContext.select(DOMAIN.PARENT).from(DOMAIN)
					.where(DOMAIN.ID.eq(domainId))
					.fetchOne(DOMAIN.PARENT);
			
			//AUTH_KEY
			String authKey = getAuthKey(dslContext, domainId, parentDomainId);
			
			//ETI
			JSONObject eti = new JSONObject();
			
			eti.put("authkey", authKey);			
			eti.put("payrollProvider", "498");	//Proveedor de nominas ESFERALIA NETWORKS, S.A.
			eti.put("fileName", fileName);
			eti.put("prorityCode", "N");
			employeeAFIJSON.put("ETI", eti);
			
			//EMP
			Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.ID.in(
							dslContext.select(CONTRACT.ENTERPRISE_CCC).from(CONTRACT)
								.where(CONTRACT.ID.eq(contractId))
								.fetchOne(CONTRACT.ENTERPRISE_CCC)
					)).fetchOne();
			
			Integer enterpriseActivityId = enterpriseCCCRecord.get(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY);
			
			String geozoneCCCCode = dslContext.select(GEOZONE.CODE).from(GEOZONE)
					.where(GEOZONE.ID.eq(enterpriseCCCRecord.get(ENTERPRISE_CCC.GEOZONE)))
					.fetchOne(GEOZONE.CODE);
			
			Result<Record> enterpriseCCCPrincipalRecords = dslContext.select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(enterpriseActivityId))
					.and(ENTERPRISE_CCC.TYPE.eq((byte)0))
					.orderBy(ENTERPRISE_CCC.ID)
					.fetch();
			
			Record geozoneCCCPrincipal = null;
			
			if(enterpriseCCCPrincipalRecords.isNotEmpty()) {
				Integer geozoneCCCPrincipalId = enterpriseCCCPrincipalRecords.get(0).get(ENTERPRISE_CCC.GEOZONE);
				geozoneCCCPrincipal = dslContext.select().from(GEOZONE)
					.where(GEOZONE.ID.eq(geozoneCCCPrincipalId))
					.fetchOne();
			}
			
			Record enterpriseRegistryRecord = dslContext.select().from(REGISTRY)
					.where(REGISTRY.ID.in(
						dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY)
							.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseActivityId))
							.fetchOne(ENTERPRISE_ACTIVITY.ENTERPRISE)
					)).fetchOne();
			
			
			String cccRegimeCode = getCCCRegimeCode(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE));
			String ccc =  parseCCC(enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC));
			String identType = getIndetType(enterpriseRegistryRecord.get(REGISTRY.DOCUMENT_TYPE));
			String ident = enterpriseRegistryRecord.get(REGISTRY.DOCUMENT);
			String cccProvincePrincipal = geozoneCCCPrincipal == null ? "00" : geozoneCCCPrincipal.get(GEOZONE.CODE);
			String cccPrincipal = parseCCC(enterpriseCCCPrincipalRecords.isEmpty() ? "000000000" : enterpriseCCCPrincipalRecords.get(0).get(ENTERPRISE_CCC.CCC));
			
			JSONObject emp = new JSONObject();
			emp.put("cccRegime", cccRegimeCode);
			emp.put("cccProvince", geozoneCCCCode);
			emp.put("ccc", ccc);
			emp.put("identType", identType);
			emp.put("country", "011");
			emp.put("ident", ident);
			emp.put("cccRegimePrincipal", "0111");
			emp.put("cccProvincePrincipal", cccProvincePrincipal);
			emp.put("cccPrincipal", cccPrincipal);
			employeeAFIJSON.put("EMP", emp);
			
			//RZS
			String rzsName = removeAccents(enterpriseRegistryRecord.get(REGISTRY.NAME));
			if(AonStringUtils.isNotBlank(rzsName))
				rzsName = stripDiacritics(rzsName);
			
			JSONObject rzsData = new JSONObject();
			rzsData.put("businessmanType", "2");
			rzsData.put("rzsName", rzsName);
			employeeAFIJSON.put("RZS", rzsData);
			
			//TRA
			employeeAFIJSON.put("TRA", getTRA(contractId, dslContext));
			
			//AYN
			employeeAFIJSON.put("AYN", getAYN(contractId, dslContext));
			
			// SEGMENTS
			Integer contSeg = 0;
			
			// Alta Contrato
			if(Boolean.TRUE.equals(isStartContract)) {
				contSeg++;
				employeeAFIJSON.put("MA", getSDC(contractId, dslContext));
			}
			
			// Baja Contrato
			if(Boolean.TRUE.equals(isEndContract)) {
				contSeg++;
				employeeAFIJSON.put("MB", getEDC(contractId, dslContext, settleReason));
			}
			
			// Movimientos Contrato
			if(Boolean.TRUE.equals(isChangeContract || isQuoteContract || isOcupationContract || isPartialityCoefContract)) {
				contSeg++;
				employeeAFIJSON.put("MC", getMC(contractId, dslContext));
			}
			
			//ETF
			JSONObject etf = new JSONObject();
			etf.put("authkey", authKey);
			etf.put("payrollProvider", "498");
			etf.put("fileName", fileName);
			etf.put("priorityCode", "N");
			employeeAFIJSON.put("ETF", etf);
			
			//CONFIG
			JSONObject conf = new JSONObject();
			conf.put("staticLines", "4");
			conf.put("employeeLines", "4");
			conf.put("numEmployees", contSeg+"");
			conf.put("settleHolidaysLine", "0");
			// Check if exist FCT line
			JSONObject mbObj = (JSONObject) employeeAFIJSON.get("MB");
			if(mbObj != null) {
				JSONObject fctObj = (JSONObject) ((JSONObject) employeeAFIJSON.get("MB")).get("FCT");
				if (null != fctObj) conf.put("settleHolidaysLine", "1");
			}
			employeeAFIJSON.put("CONF", conf);
			
		
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return employeeAFIJSON;
	}
	
	// -------------------------------------------- Auxiliar Methods.getEmployeeAFIInfo
	
	private static String getAuthKey(DSLContext dslContext, Integer domainId, Integer parentDomainId) {
		String authKey = "";
		
		Record appParamRecord = dslContext.select().from(APP_PARAM)
				.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY"))
				.and(APP_PARAM.DOMAIN.eq(domainId))
				.fetchOne();
		
		if(null == appParamRecord || null == appParamRecord.get(APP_PARAM.VALUE) || AonStringUtils.isBlank(appParamRecord.get(APP_PARAM.VALUE))) {
			appParamRecord = dslContext.select().from(APP_PARAM)
					.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY"))
					.and(APP_PARAM.DOMAIN.eq(parentDomainId))
					.fetchOne();
			
			if(null == appParamRecord || null == appParamRecord.get(APP_PARAM.VALUE)) 
				authKey = "00000";
			else
				authKey = appParamRecord.get(APP_PARAM.VALUE);
		} else
			authKey = appParamRecord.get(APP_PARAM.VALUE);
		
		return authKey;
	}
	
	// -------------------------------------------- getEmployeeAFIInfo. TRA

	@SuppressWarnings("unchecked")
	private static JSONObject getTRA(Integer contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		
		String numAfiliacion = dslContext.select(PERSON.SOCIAL_SECURITY_NUM).from(PERSON)
				.where(PERSON.REGISTRY.eq(
						dslContext.select(CONTRACT.PERSON).from(CONTRACT)
							.where(CONTRACT.ID.eq(contractId))
							.fetchOne(CONTRACT.PERSON)
				)).fetchOne(PERSON.SOCIAL_SECURITY_NUM);
			
		Record registryRecord = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(
						dslContext.select(CONTRACT.PERSON).from(CONTRACT)
							.where(CONTRACT.ID.eq(contractId))
							.fetchOne(CONTRACT.PERSON)
				)).fetchOne();
		
		String documentType = getDocumentType(registryRecord.get(REGISTRY.DOCUMENT));
		String documentCountry = getDocumentCountry(registryRecord.get(REGISTRY.DOCUMENT_COUNTRY));
		String document = registryRecord.get(REGISTRY.DOCUMENT);
		
		json.put("numAfiliacion", numAfiliacion);
		json.put("documentType", documentType);
		json.put("documentCountry", documentCountry);
		json.put("document", document);
		json.put("nationality", "724");	// registryRecord.get(REGISTRY.NATIONALITY)
		
		return json;
	}
	
	// -------------------------------------------- getEmployeeAFIInfo. TRA Auxiliar Methods

	private static String getDocumentType(String document) {
		Pattern dniPattern = Pattern.compile("[0-9]{7,8}[A-Z a-z]");
		Matcher dniMatcher = dniPattern.matcher(document);
		
		if(dniMatcher.matches())
			return "1";
		
		Pattern niePattern = Pattern.compile("[a-zA-Z0-9][0-9]{7}[a-zA-Z0-9]{1,2}");
		Matcher nieMatcher = niePattern.matcher(document);
		
		if(nieMatcher.matches())
			return "6";
		
		return "2";
	}
	
	private static String getDocumentCountry(String iso2) {
		if(AonStringUtils.isBlank(iso2))
			return null;
		
		Country country = Country.valueOf(iso2);
		return country.getIsoCode() + "";
	}
	
	// -------------------------------------------- getEmployeeAFIInfo. AYN

	@SuppressWarnings("unchecked")
	private static JSONObject getAYN(Integer contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		
		Record personRecord = dslContext.select().from(PERSON)
			.where(PERSON.REGISTRY.eq(
					dslContext.select(CONTRACT.PERSON).from(CONTRACT)
						.where(CONTRACT.ID.eq(contractId))
						.fetchOne(CONTRACT.PERSON)
			))
			.fetchOne();
		
		String firstSurname = personRecord.get(PERSON.FIRST_SURNAME);
		if(AonStringUtils.isNotBlank(firstSurname))
			firstSurname = stripDiacritics(firstSurname);
		
		String secondSurname = personRecord.get(PERSON.SECOND_SURNAME);
		if(AonStringUtils.isNotBlank(secondSurname))
			secondSurname = stripDiacritics(secondSurname);
		
		String name = personRecord.get(PERSON.NAME);
		if(AonStringUtils.isNotBlank(name))
			name = stripDiacritics(name);
		
		json.put("firstSurname", firstSurname);
		json.put("secondSurname", secondSurname);
		json.put("name", name);
		
		return json;
	}
	
	// -------------------------------------------- getEmployeeAFIInfo. SDC
	
	@SuppressWarnings("unchecked")
	private static JSONObject getSDC(int contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		JSONObject fab = new JSONObject();
		JSONObject odl = new JSONObject();
		JSONObject otd = new JSONObject();
		
		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		
		Result<Record> contractDataQuoteRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(QUOTE_GROUP))
				.orderBy(CONTRACT_DATA.ID.desc())
				.fetch();
		
		Result<Record> contractDataTC2Record = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(TC2))
				.orderBy(CONTRACT_DATA.ID.desc())
				.fetch();
		
		Result<Record> contractDataPCRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(PARTIALITY))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
		
		Result<Record> contractDataEmployeeColectiveRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(EMPLOYEECOLECTIVE))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
		
		Byte gender = dslContext.select(PERSON.GENDER).from(PERSON)
				.where(PERSON.REGISTRY.in(
						dslContext.select(CONTRACT.PERSON).from(CONTRACT)
							.where(CONTRACT.ID.eq(contractId))
							.fetchOne(CONTRACT.PERSON)
				)).fetchOne(PERSON.GENDER);
		
		Calendar startDateCalendar = Calendar.getInstance();
		startDateCalendar.setTime(contractRecord.get(CONTRACT.START_DATE));
		
		String quoteGruop = parseContractData(contractDataQuoteRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String tc2 = parseContractData(contractDataTC2Record.get(0).get(CONTRACT_DATA.EXPRESSION));
		String partiality = contractDataPCRecord.isEmpty() ? null : parseContractData(contractDataPCRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String employeeColective = contractDataEmployeeColectiveRecord.isEmpty() ? null : parseContractData(contractDataEmployeeColectiveRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		
		//FAB
		fab.put("action", "MA");
		fab.put("situation", "1");
		fab.put("day", startDateCalendar.get(Calendar.DAY_OF_MONTH));
		fab.put("month", startDateCalendar.get(Calendar.MONTH) + 1);
		fab.put("year", startDateCalendar.get(Calendar.YEAR));
		fab.put("quoteGroup", quoteGruop);
		fab.put("tc2", tc2);
		fab.put("partialityCoef", partiality);
		fab.put("employeeColective", employeeColective);
		fab.put("gender", gender);
		
		//ODL
		Result<Record> contractDataCnoRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(CNO))
				.orderBy(CONTRACT_DATA.ID.desc())
				.fetch();
		
		String employeeCno = contractDataCnoRecord.isEmpty() ? null : parseContractData(contractDataCnoRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		
		odl.put("cno", employeeCno);
		
		//OTD
		String endDate = null;
		Date contractEndDate = contractRecord.get(CONTRACT.END_DATE);
		if(null != contractEndDate) {
			Calendar endDateCalendar = Calendar.getInstance();
			endDateCalendar.setTime(contractRecord.get(CONTRACT.END_DATE));
			endDate = endDateCalendar.get(Calendar.YEAR) + (endDateCalendar.get(Calendar.MONTH) + 1) + endDateCalendar.get(Calendar.DAY_OF_MONTH) + "";
		}
		
		Integer agreementLevelId = dslContext.select(CONTRACT.AGREEMENT_LEVEL).from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne(CONTRACT.AGREEMENT_LEVEL);
		
		String agreementColective = null;
		
		if (null != agreementLevelId)
			agreementColective = dslContext.select(AGREEMENT.SS_NUMBER).from(AGREEMENT)
				.where(AGREEMENT.ID.in(
						dslContext.select(AGREEMENT_LEVEL.AGREEMENT).from(AGREEMENT_LEVEL)
							.where(AGREEMENT_LEVEL.ID.eq(agreementLevelId))
							.fetchOne(AGREEMENT_LEVEL.AGREEMENT)
				)).fetchOne(AGREEMENT.SS_NUMBER);

		otd.put("convCollective",  AonStringUtils.isBlank(agreementColective) ? "00000000000000" : agreementColective);
		otd.put("endDate", endDate);
		
		json.put("FAB", fab);
		json.put("ODL", odl);
		json.put("OTD", otd);
		
		return json;
	}
	
	// -------------------------------------------- getEmployeeAFIInfo. EDC
	
	@SuppressWarnings("unchecked")
	private static JSONObject getEDC(int contractId, DSLContext dslContext, String settleReason) {
		JSONObject json = new JSONObject();
		JSONObject fab = new JSONObject();
		JSONObject dam = new JSONObject();
		JSONObject fct = new JSONObject();
		
		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		
		Result<Record> contractDataQuoteRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(QUOTE_GROUP))
				.orderBy(CONTRACT_DATA.ID.desc())
				.fetch();
		
		Result<Record> contractDataTC2Record = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(TC2))
				.orderBy(CONTRACT_DATA.ID.desc())
				.fetch();
		
		Result<Record> contractDataPCRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(PARTIALITY))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
		
		Result<Record> contractDataEmployeeColectiveRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(EMPLOYEECOLECTIVE))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
		
		Byte gender = dslContext.select(PERSON.GENDER).from(PERSON)
				.where(PERSON.REGISTRY.eq(contractRecord.get(CONTRACT.PERSON)))
				.fetchOne(PERSON.GENDER);
		
		Result<Record> salaryDataRecords = dslContext.select().from(SALARY_DATA)
			.where(SALARY_DATA.NAME.eq("CAUSA_INDEMNIZACION"))
			.and(SALARY_DATA.SALARY.in(
					dslContext.select(SALARY.ID).from(SALARY)
						.where(SALARY.TYPE.eq((byte)2))
						.and(SALARY.CONTRACT.eq(contractId))
						.fetch(SALARY.ID))
			).fetch();
		
		Result<Record> holidaysDataRecords = dslContext.select().from(SALARY_DATA)
				.where(SALARY_DATA.NAME.eq("DIAS_VACACIONES_NO_DISFRUTADOS"))
				.and(SALARY_DATA.SALARY.in(
						dslContext.select(SALARY.ID).from(SALARY)
							.where(SALARY.TYPE.eq((byte)2))
							.and(SALARY.CONTRACT.eq(contractId))
							.fetch(SALARY.ID))
				).orderBy(SALARY_DATA.START_DATE.desc())
				.fetch();
		
		Calendar endDateCalendar = Calendar.getInstance();
		endDateCalendar.setTime(contractRecord.get(CONTRACT.END_DATE));
		
		String quoteGroup = parseContractData(contractDataQuoteRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String tc2 = parseContractData(contractDataTC2Record.get(0).get(CONTRACT_DATA.EXPRESSION));
		String partialityCoef = contractDataPCRecord.isEmpty() ? null : parseContractData(contractDataPCRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String employeeColective = contractDataEmployeeColectiveRecord.isEmpty() ? null : parseContractData(contractDataEmployeeColectiveRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		
		if(holidaysDataRecords.isNotEmpty()) {
			Calendar holidyaEndDateCalendar = Calendar.getInstance();
			holidyaEndDateCalendar.setTime(holidaysDataRecords.get(0).get(SALARY_DATA.END_DATE));
			fct.put("dayHoliday", holidyaEndDateCalendar.get(Calendar.DAY_OF_MONTH));
			fct.put("monthHoliday", holidyaEndDateCalendar.get(Calendar.MONTH) + 1);
			fct.put("yearHoliday", holidyaEndDateCalendar.get(Calendar.YEAR));
		}
		
		//FAB
		fab.put("action", "MB");
		
		//AVERIGUAR A TRAVES DEL FINIQUITO
		fab.put("situation", getCausaDespido(salaryDataRecords, settleReason));
		fab.put("day", endDateCalendar.get(Calendar.DAY_OF_MONTH));
		fab.put("month", endDateCalendar.get(Calendar.MONTH) + 1);
		fab.put("year", endDateCalendar.get(Calendar.YEAR));
		fab.put("quoteGroup", quoteGroup);
		fab.put("tc2", tc2);
		fab.put("partialityCoef", partialityCoef == null ? "" : parseCoefLengnt(partialityCoef));
		fab.put("employeeColective", employeeColective);
		fab.put("gender", gender);
		
		//DAM -> All reserved
		json.put("FAB", fab);
		json.put("DAM", dam);
		if(holidaysDataRecords.isNotEmpty())
			json.put("FCT", fct);
		
		return json;
	}
	
	private static String parseCoefLengnt(String partialityCoef) {
		if(partialityCoef.length() <= 3)
			return partialityCoef;
		return partialityCoef.length() <= 3 ? partialityCoef : partialityCoef.substring(partialityCoef.length()-3, partialityCoef.length());
	}

	// -------------------------------------------- getEmployeeAFIInfo. MC
	
	@SuppressWarnings("unchecked")
	private static JSONObject getMC(int contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		JSONObject fab = new JSONObject();
		JSONObject dam = new JSONObject();
		
		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		
		Result<Record> contractDataQuoteRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(QUOTE_GROUP))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
		
		Result<Record> contractDataTC2Record = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(TC2))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
		
		Result<Record> contractDataPCRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(PARTIALITY))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
		
		Result<Record> contractDataEmployeeColectiveRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(EMPLOYEECOLECTIVE))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
		
		Result<Record> contractDataOcupationRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(OCUPATION))
				.orderBy(CONTRACT_DATA.ID.desc())
				.fetch();
		
		Byte gender = dslContext.select(PERSON.GENDER).from(PERSON)
				.where(PERSON.REGISTRY.eq(contractRecord.get(CONTRACT.PERSON)))
				.fetchOne(PERSON.GENDER);
		
		
		ArrayList<Date> dates = new ArrayList<>();
		
		if(contractDataQuoteRecord.isNotEmpty())
			dates.add(contractDataQuoteRecord.get(0).get(CONTRACT_DATA.START_DATE));
		
		if(contractDataTC2Record.isNotEmpty())
			dates.add(contractDataTC2Record.get(0).get(CONTRACT_DATA.START_DATE));
		
		if(contractDataPCRecord.isNotEmpty())
			dates.add(contractDataPCRecord.get(0).get(CONTRACT_DATA.START_DATE));
		
		if(contractDataOcupationRecord.isNotEmpty())
			dates.add(contractDataOcupationRecord.get(0).get(CONTRACT_DATA.START_DATE));
		
		dates.sort((o1, o2) -> o1.compareTo(o2));
		
		Calendar dateCalendar = Calendar.getInstance();
		dateCalendar.setTime(dates.get(dates.size()-1));
		
		String quoteGroup =  contractDataQuoteRecord.isEmpty() ? null : parseContractData(contractDataQuoteRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String tc2 = parseContractData(contractDataTC2Record.get(0).get(CONTRACT_DATA.EXPRESSION));
		String partialityCoef = contractDataPCRecord.isEmpty() ? null : parseContractData(contractDataPCRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String employeeColective = contractDataEmployeeColectiveRecord.isEmpty() ? null : parseContractData(contractDataEmployeeColectiveRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		
		//FAB
		fab.put("action", "MC");
		fab.put("situation", "");
		fab.put("day", dateCalendar.get(Calendar.DAY_OF_MONTH));
		fab.put("month", dateCalendar.get(Calendar.MONTH) + 1);
		fab.put("year", dateCalendar.get(Calendar.YEAR));
		fab.put("quoteGroup", quoteGroup);
		fab.put("tc2", tc2);
		fab.put("partialityCoef", null == partialityCoef ? "" : parseCoefLengnt(partialityCoef));
		fab.put("employeeColective", employeeColective);
		fab.put("gender", gender);
		
		String ocupation = parseContractData(contractDataOcupationRecord.isEmpty() ? "" : contractDataOcupationRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		
		//DAM
		Calendar startDateCal = Calendar.getInstance();
		startDateCal.setTimeInMillis(contractRecord.get(CONTRACT.START_DATE).getTime());
		String startDate = dateCalendar.get(Calendar.DAY_OF_MONTH) + "";
		startDate += AonStringUtils.leftPad((dateCalendar.get(Calendar.MONTH) + 1) + "", 2, '0');
		startDate += dateCalendar.get(Calendar.YEAR) + "";
		
		dam.put("ocupation", ocupation);
		dam.put("startDate", AonStringUtils.isBlank(ocupation) ? "" : startDate);
		
		json.put("FAB", fab);
		json.put("DAM", dam);
		
		return json;
	}
	
	// -------------------------------------------- getEmployeeAFIInfo. Auxiliar Methods
	
	private static String removeAccents(String cadena) {
	    return cadena.replace("Á", "A")
	            .replace("É", "E")
	            .replace("Í", "I")
	            .replace("Ó", "O")
	            .replace("Ú", "U")
	            .replace("á", "a")
	            .replace("é", "e")
	            .replace("í", "i")
	            .replace("ó", "o")
	            .replace("ú", "u");
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
		default:
			return "0111";
		}
	}
	
	private static String getIndetType(Byte identType) {
		switch (identType) {
		case 0:
			return "1";
		case 1:
			return "9";
		case 2:
			return "6";
		default:
			return "L";
		}
	}

	private static String getCausaDespido(Result<Record> records, String settleReason) {

		if(records.isEmpty())
			return settleReason;
		
		switch (records.get(0).get(SALARY_DATA.EXPRESSION)) {
		case "UNFAIR":
			return "93";
		case "TEMP_END":
			return "93";
		case "WORK_END": //FIN_OBRA
			return "93";
		case "DEFINITE_END":
			return "93";
		case "OBJECTIVE":
			return "91";
		default: //"CONDITIONS_CHANGE":
			return settleReason;
		}
	}
	
	private static String parseContractData( String data ) {
		if(null != data)
			data = data.replace(".", "");
		if(null != data && data.contains("\""))
			return data.split("\"")[1];
		else
			return data;
	}
	
	private static String parseCCC(String ccc) {
		if(ccc.length() == 11) {
			return ccc.substring(2);
		}else
			return ccc;
		
	}
	
	// -------------------------------------------- setEmployeeAFIDB
	
	private static void setEmployeeAFIDB(DSLContext dslContext, Integer contractId, AFIChanges afiChangesMap) {
		
		//Delete existing info
		ArrayList<String> contractDataVars = new ArrayList<>();
		contractDataVars.add(TC2);
		contractDataVars.add(QUOTE_GROUP);
		contractDataVars.add(OCUPATION);
		contractDataVars.add(PARTIALITY);
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.in(contractDataVars))
			.execute();
		
		//Get contract startDate and endDate
		Record contractRecord = dslContext.select().from(CONTRACT)
					.where(CONTRACT.ID.eq(contractId))
					.fetchOne();
		
		Integer domainId = contractRecord.get(CONTRACT.DOMAIN);
		Date contractEndDate = contractRecord.get(CONTRACT.END_DATE);
		
		ArrayList<java.util.Date> dateList = new ArrayList<>();
		dateList.addAll(afiChangesMap.getAFIChanges().keySet());
		
		//Insert info : OPCION 1
		Integer nextIt = 1;
		
		for(Entry<java.util.Date, ArrayList<AFIChange>> entry : afiChangesMap.getAFIChanges().entrySet()) {
			Date startDate = new Date(entry.getKey().getTime());
			Date endDate = getEndDate(dateList, nextIt, contractEndDate);
			
			for(AFIChange afiChange: afiChangesMap.getAFIChanges().get(entry.getKey())) {
				if(null != afiChange.getValue()) {
					if(AonStringUtils.equals(afiChange.getName(), PARTIALITY)) {
						insertPartiality(dslContext, domainId, contractId, afiChange, startDate, endDate);
					} else {
						insertContractData(dslContext, domainId, contractId, afiChange, startDate, endDate);
					}
				}
			}
			
			nextIt++;
		}
		
		checkPartialHours(dslContext, contractId, afiChangesMap, dateList);
	}

	// -------------------------------------------- setEmployeeAFIDB. Auxiliar Methods
	
	private static void checkPartialHours(DSLContext dslContext, Integer contractId, AFIChanges afiChangesMap, ArrayList<java.util.Date> dateList) {
		if(dateList.isEmpty() || dateList.size() < 2) return;
		
		dateList.sort((o1, o2) -> o1.compareTo(o2));
		java.util.Date lastDate = dateList.get(dateList.size()-1);
		ArrayList<AFIChange> afiChanges = afiChangesMap.getAFIChangessByDate(lastDate);
		
		Integer contractType = getContractType(afiChanges);
		
		if(null == contractType) return;
		
		java.util.Date newEndDate = DateUtils.addDays2Date(lastDate, -1);
		
		if(isCompleteJourneyContract(contractType))
			dslContext.update(CONTRACT_DATA)
				.set(CONTRACT_DATA.END_DATE, new Date(newEndDate.getTime()))
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.END_DATE.isNull().or(CONTRACT_DATA.END_DATE.ge(new Date(newEndDate.getTime()))))
				.and(CONTRACT_DATA.NAME.eq("HORAS_LUNES")
					.or(CONTRACT_DATA.NAME.eq("HORAS_MARTES"))
					.or(CONTRACT_DATA.NAME.eq("HORAS_MIERCOLES"))
					.or(CONTRACT_DATA.NAME.eq("HORAS_JUEVES"))
					.or(CONTRACT_DATA.NAME.eq("HORAS_VIERNES"))
					.or(CONTRACT_DATA.NAME.eq("HORAS_SABADO"))
					.or(CONTRACT_DATA.NAME.eq("HORAS_DOMINGO"))
					.or(CONTRACT_DATA.NAME.eq("COEFICIENTE_PARCIALIDAD"))
					.or(CONTRACT_DATA.NAME.eq("TIEMPO_COMPLETO"))
				).execute();
		
	}
	
	private static boolean isCompleteJourneyContract(Integer contractTypeCode) {
		ContractType contractTypeObj = new ContractType();
		ContractTypeRecord contractType = contractTypeObj.getContractType(contractTypeCode);
		return AonStringUtils.equalsIgnoreCase(contractType.getJourneyType(), "C");
	}

	private static Integer getContractType(ArrayList<AFIChange> afiChanges) {
		for(AFIChange afiChange : afiChanges)
			if(AonStringUtils.equalsIgnoreCase(afiChange.getName(), "TC2"))
				return Integer.parseInt(afiChange.getValue());
		
		return null;
	}

	private static Date getEndDate(ArrayList<java.util.Date> dateList, Integer nextIt, Date contractEndDate) {
		Date endDate = null;
		
		if(nextIt < dateList.size()) {
			endDate = new Date(DateUtils.copyDateOnly(dateList.get(nextIt)).getTime());
			DateUtils.addDays2Date(endDate, -1);
		} else
			endDate = contractEndDate;
		
		return endDate;
	}
	
	private static void insertPartiality(DSLContext dslContext, Integer domainId, Integer contractId, AFIChange afiChange, Date startDate, Date endDate) {
		dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, domainId)
			.set(CONTRACT_DATA.CONTRACT, contractId)
			.set(CONTRACT_DATA.NAME, afiChange.getName())
			.set(CONTRACT_DATA.EXPRESSION, afiChange.getValue().contains("\"") ? AonStringUtils.replace(afiChange.getValue(), "\"", "") : afiChange.getValue())
			.set(CONTRACT_DATA.START_DATE, startDate)
			.set(CONTRACT_DATA.END_DATE, endDate)
			.execute();
	}

	private static void insertContractData(DSLContext dslContext, Integer domainId, Integer contractId, AFIChange afiChange, Date startDate, Date endDate) {
		dslContext.insertInto(CONTRACT_DATA)
			.set(CONTRACT_DATA.DOMAIN, domainId)
			.set(CONTRACT_DATA.CONTRACT, contractId)
			.set(CONTRACT_DATA.NAME, afiChange.getName())
			.set(CONTRACT_DATA.EXPRESSION, afiChange.getValue().contains("\"") ? afiChange.getValue() : "\"" + afiChange.getValue() + "\"")
			.set(CONTRACT_DATA.START_DATE, startDate)
			.set(CONTRACT_DATA.END_DATE, endDate)
			.execute();
	}

	// -------------------------------------------- getEmployeeAFIDB

	private static AFIChanges getEmployeeAFIDB(DSLContext dslContext, Integer contractId) {
		AFIChanges afiChanges = new AFIChanges();
		
		ArrayList<String> contractDataVars = new ArrayList<>();
		contractDataVars.add(TC2);
		contractDataVars.add(QUOTE_GROUP);
		contractDataVars.add(OCUPATION);
		contractDataVars.add(PARTIALITY);
		
		Result<Record> contractDataRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.in(contractDataVars))
				.orderBy(CONTRACT_DATA.START_DATE.asc())
				.fetch();
		
		if(!contractDataRecords.isEmpty()) {
			ArrayList<AFIChange> afiChangeList = new ArrayList<>();
			Date dateAux = contractDataRecords.get(0).get(CONTRACT_DATA.START_DATE);
			
			for(Record r : contractDataRecords) {
				if(r.get(CONTRACT_DATA.START_DATE).equals(dateAux)) {
					afiChangeList.add(new AFIChange(r.get(CONTRACT_DATA.NAME), r.get(CONTRACT_DATA.EXPRESSION)));
				}else {
					java.util.Date javaDate = new Date(dateAux.getTime());
					afiChanges.getAFIChanges().put(javaDate, afiChangeList);
					dateAux = r.get(CONTRACT_DATA.START_DATE);
					afiChangeList = new ArrayList<>();
					afiChangeList.add(new AFIChange(r.get(CONTRACT_DATA.NAME), r.get(CONTRACT_DATA.EXPRESSION)));
				}
			}
			
			if(!afiChangeList.isEmpty()) {
				java.util.Date javaDate = new Date(dateAux.getTime());
				afiChanges.getAFIChanges().put(javaDate, afiChangeList);
			}
			
		}
		
		return afiChanges;
	}

}
