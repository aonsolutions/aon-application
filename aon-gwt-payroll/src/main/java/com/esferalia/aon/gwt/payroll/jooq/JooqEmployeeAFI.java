package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges.AFIChange;
import com.esferalia.aon.jooq.tables.records.PersonRecord;
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
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

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
			Boolean isCnoContract,
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
			
			String fullCcc = cccRegimeCode + AonStringUtils.leftPad(geozoneCCCCode, 2, '0') + AonStringUtils.leftPad(ccc, 9, '0');
			String fullPrincipalCcc = "0111" + AonStringUtils.leftPad(cccProvincePrincipal, 2, '0') + AonStringUtils.leftPad(cccPrincipal, 9, '0');
			
			JSONObject emp = new JSONObject();
			emp.put("fullCcc", fullCcc);
			emp.put("identType", identType);
			emp.put("country", "011");
			emp.put("ident", ident);
			emp.put("fullPrincipalCcc", fullPrincipalCcc);
			employeeAFIJSON.put("EMP", emp);
			
			//RZS
			String rzsName = removeAccents(enterpriseRegistryRecord.get(REGISTRY.NAME));
			if(AonStringUtils.isNotBlank(rzsName))
				rzsName = stripDiacritics(rzsName);
			
			JSONObject rzsData = new JSONObject();
			rzsData.put("rzsName", rzsName);
			employeeAFIJSON.put("RZS", rzsData);
			
			//TRA
			employeeAFIJSON.put("TRA", getTRA(contractId, dslContext));
			
			//AYN
			employeeAFIJSON.put("AYN", getAYN(contractId, dslContext));
			
			// SEGMENTS
			Integer contSeg = 0;
			
			//CONFIG
			JSONObject conf = new JSONObject();
			conf.put("staticLines", "4");
			conf.put("employeeLines", "4");
			conf.put("settleHolidaysLine", "0");
			
			// Alta Contrato
			if(Boolean.TRUE.equals(isStartContract)) {
				contSeg++;
				JSONObject ma = getSDC(contractId, dslContext);
				employeeAFIJSON.put("MA", ma);
				conf.put("employeeLines", ma.size() + 2); // TRA y AYN
			}
			
			// Baja Contrato
			if(Boolean.TRUE.equals(isEndContract)) {
				contSeg++;
				JSONObject mb = getEDC(contractId, dslContext, settleReason);
				employeeAFIJSON.put("MB", mb);
				conf.put("employeeLines", mb.size() + 2); // TRA y AYN
			}
			
			// Movimientos Contrato
			if(Boolean.TRUE.equals(isChangeContract || isQuoteContract || isOcupationContract || isPartialityCoefContract || isCnoContract)) {
				contSeg++;
				JSONObject mc = getMC(contractId, dslContext);
				employeeAFIJSON.put("MC", mc);
				conf.put("employeeLines", mc.size() + 2); // TRA y AYN
			}
			
			conf.put("numEmployees", contSeg+"");
			
			//ETF
			JSONObject etf = new JSONObject();
			etf.put("authkey", authKey);
			etf.put("payrollProvider", "498");
			etf.put("fileName", fileName);
			employeeAFIJSON.put("ETF", etf);
			
			
			employeeAFIJSON.put("CONF", conf);
		
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return employeeAFIJSON;
	}
	
	// -------------------------------------------- Auxiliar Methods.getEmployeeAFIInfo
	
	public static String getAuthKey(Connection connection, Integer domainId, Integer parentDomainId) {
		return getAuthKey(DSL.using(connection, getDefaultSettings()), domainId, parentDomainId);
	}
	
	
	public static String getAuthKey(DSLContext dslContext, Integer domainId, Integer parentDomainId) {
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
		
		PersonRecord personRecord = dslContext.selectFrom(PERSON)
			.where(PERSON.REGISTRY.in(
				dslContext.select(CONTRACT.PERSON).from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne(CONTRACT.PERSON)
			)).fetchOne();
		
		String quoteGruop = parseContractData(contractDataQuoteRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String tc2 = parseContractData(contractDataTC2Record.get(0).get(CONTRACT_DATA.EXPRESSION));
		String partiality = contractDataPCRecord.isEmpty() ? null : parseContractData(contractDataPCRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String employeeColective = contractDataEmployeeColectiveRecord.isEmpty() ? null : parseContractData(contractDataEmployeeColectiveRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		
		//FAB
		fab.put("action", "MA");
		fab.put("situation", "1");
		fab.put("realDate", dateFormat.format(contractRecord.get(CONTRACT.START_DATE)));
		fab.put("quoteGroup", quoteGruop);
		fab.put("tc2", tc2);
		fab.put("partialityCoef", partiality == null ? "" : parseCoefLengnt(partiality));
		fab.put("employeeColective", employeeColective);
		fab.put("birthDate", personRecord.getBirthDate() == null ? "" : dateFormat.format(personRecord.getBirthDate()));
		fab.put("gender", personRecord.getGender());
		
		//ODL
		Result<Record> contractDataCnoRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(CNO))
				.orderBy(CONTRACT_DATA.ID.desc())
				.fetch();
		
		String employeeCno = contractDataCnoRecord.isEmpty() ? null : parseContractData(contractDataCnoRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		
		Integer agreementLevelId = dslContext.select(CONTRACT.AGREEMENT_LEVEL).from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne(CONTRACT.AGREEMENT_LEVEL);
		
		String agreementColective = "";
		
		if (null != agreementLevelId)
			agreementColective = dslContext.select(AGREEMENT.SS_NUMBER).from(AGREEMENT)
				.where(AGREEMENT.ID.in(
						dslContext.select(AGREEMENT_LEVEL.AGREEMENT).from(AGREEMENT_LEVEL)
							.where(AGREEMENT_LEVEL.ID.eq(agreementLevelId))
							.fetchOne(AGREEMENT_LEVEL.AGREEMENT)
				)).fetchOne(AGREEMENT.SS_NUMBER);

		odl.put("convCollective",  AonStringUtils.leftPad(agreementColective, 14, '0'));
		odl.put("cno", employeeCno);
		
		//OTD
		Date contractEndDate = contractRecord.get(CONTRACT.END_DATE);
		if(null != contractEndDate) {
			otd.put("endDate", contractEndDate != null ? dateFormat.format(contractEndDate) : "");
			json.put("OTD", otd);
		}
		
		
		json.put("FAB", fab);
		json.put("ODL", odl);
		
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
		
		String quoteGroup = parseContractData(contractDataQuoteRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String tc2 = parseContractData(contractDataTC2Record.get(0).get(CONTRACT_DATA.EXPRESSION));
		String partialityCoef = contractDataPCRecord.isEmpty() ? null : parseContractData(contractDataPCRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String employeeColective = contractDataEmployeeColectiveRecord.isEmpty() ? null : parseContractData(contractDataEmployeeColectiveRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		
		if(holidaysDataRecords.isNotEmpty()) {
			fct.put("holidayDate", dateFormat.format(holidaysDataRecords.get(0).get(SALARY_DATA.END_DATE)));
			fct.put("saa", "001");
		}
		
		//FAB
		fab.put("action", "MB");
		
		//AVERIGUAR A TRAVES DEL FINIQUITO
		fab.put("situation", getCausaDespido(salaryDataRecords, settleReason));
		fab.put("realDate", dateFormat.format(contractRecord.get(CONTRACT.END_DATE)));
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
		if(AonStringUtils.isBlank(partialityCoef) || AonStringUtils.equalsIgnoreCase(partialityCoef, "1")) return "";
		
		try {
			Double coef = Double.parseDouble(partialityCoef);
			coef = coef * 1000;
			return (int) Math.floor(coef) + "";
		} catch (Exception e) {
			return "";
		}
	}

	// -------------------------------------------- getEmployeeAFIInfo. MC
	
	@SuppressWarnings("unchecked")
	private static JSONObject getMC(int contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		JSONObject fab = new JSONObject();
		JSONObject dam = new JSONObject();
		JSONObject odl = new JSONObject();
		
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
		
		Result<Record> contractDataCnoRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(CNO))
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
		
		if(contractDataCnoRecord.isNotEmpty())
			dates.add(contractDataCnoRecord.get(0).get(CONTRACT_DATA.START_DATE));
		
		dates.sort((o1, o2) -> o1.compareTo(o2));
		
		String quoteGroup =  contractDataQuoteRecord.isEmpty() ? null : parseContractData(contractDataQuoteRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String tc2 = parseContractData(contractDataTC2Record.get(0).get(CONTRACT_DATA.EXPRESSION));
		String partialityCoef = contractDataPCRecord.isEmpty() ? null : parseContractData(contractDataPCRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String employeeColective = contractDataEmployeeColectiveRecord.isEmpty() ? null : parseContractData(contractDataEmployeeColectiveRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String ocupation = parseContractData(contractDataOcupationRecord.isEmpty() ? "" : contractDataOcupationRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String cno = parseContractData(contractDataCnoRecord.isEmpty() ? "" : contractDataCnoRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		
		//FAB
		fab.put("action", "MC");
		fab.put("situation", "");
		fab.put("realDate", dateFormat.format(dates.get(dates.size()-1)));
		fab.put("quoteGroup", quoteGroup);
		fab.put("tc2", tc2);
		fab.put("partialityCoef", null == partialityCoef ? "" : parseCoefLengnt(partialityCoef));
		fab.put("employeeColective", employeeColective);
		fab.put("gender", gender);
		
		//DAM
		dam.put("ocupation", ocupation);
		
		//ODL
		odl.put("convCollective",  AonStringUtils.leftPad(employeeColective, 14, '0'));
		odl.put("cno", cno);
		
		json.put("FAB", fab);
		json.put("DAM", dam);
		json.put("ODL", odl);
		
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
		contractDataVars.add(CNO);
		
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
		if(AonStringUtils.isNotBlank(afiChange.getValue()))
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
		if(AonStringUtils.isNotBlank(afiChange.getValue()))
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
		contractDataVars.add(CNO);
		
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

	@SuppressWarnings("unchecked")
	public static JSONObject getEmployeesCNOAFIInfo(Integer domainId, String domainName, String fileName) {
		JSONObject employeesCNOAFIJSON = new JSONObject();
		// SEGMENTS
		Integer contSeg = 0;
		
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			Integer parentDomainId = dslContext.select(DOMAIN.PARENT).from(DOMAIN)
					.where(DOMAIN.ID.eq(domainId))
					.fetchOne(DOMAIN.PARENT);
			
			//AUTH_KEY
			String authKey = getAuthKey(dslContext, domainId, parentDomainId);
			
			//ETI
			JSONObject eti = new JSONObject();
			
			eti.put("authkey", authKey);		
			// Proveedor ESFERALIA NETWORKS, S.A.
			eti.put("payrollProvider", "498");	
			eti.put("fileName", fileName);
			eti.put("prorityCode", "N");
			employeesCNOAFIJSON.put("ETI", eti);
			
			Calendar currentDate = Calendar.getInstance();
			
			// 01-01-2023
			Calendar startDate = Calendar.getInstance();
			startDate.set(Calendar.YEAR, 2023);
			startDate.set(Calendar.MONTH, 0);
			startDate.set(Calendar.DAY_OF_MONTH, 1);
			
			Result<Record> contracts = dslContext.select().from(CONTRACT)
				.join(ENTERPRISE_CCC)
				.on(CONTRACT.ENTERPRISE_CCC.eq(ENTERPRISE_CCC.ID))
				.join(GEOZONE)
				.on(ENTERPRISE_CCC.GEOZONE.eq(GEOZONE.ID))
				.join(ENTERPRISE_ACTIVITY)
				.on(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(ENTERPRISE_ACTIVITY.ID))
				.join(ENTERPRISE)
				.on(ENTERPRISE_ACTIVITY.ENTERPRISE.eq(ENTERPRISE.REGISTRY))
				.join(REGISTRY)
				.on(ENTERPRISE.REGISTRY.eq(REGISTRY.ID))
				.where(CONTRACT.DOMAIN.eq(domainId))
				.and(CONTRACT.SS_REGIME.ne((byte)3))	// Autonmos
				.and(ENTERPRISE_CCC.TYPE.ne((byte)6)) 	// Emplead@s de hogar
				.and(ENTERPRISE_CCC.TYPE.ne((byte)8)) 	// Artistas
				.and(CONTRACT.END_DATE.isNull().or(CONTRACT.END_DATE.gt(new Date(startDate.getTime().getTime()))))
				.and(CONTRACT.START_DATE.le(new Date(currentDate.getTimeInMillis())))
				.fetch();
			
			JSONArray contractsArr = new JSONArray();
			
			for(Record contract : contracts) {
				JSONObject contractObj = new JSONObject();
				
				Result<Record> principalCCCs = dslContext.select().from(ENTERPRISE_CCC)
						.join(GEOZONE)
						.on(ENTERPRISE_CCC.GEOZONE.eq(GEOZONE.ID))
						.where(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(contract.get(ENTERPRISE_ACTIVITY.ID)))
						.and(ENTERPRISE_CCC.TYPE.eq((byte)0))
						.orderBy(ENTERPRISE_CCC.ID)
						.fetch();
				
				String cccRegimeCode = getCCCRegimeCode(contract.get(ENTERPRISE_CCC.TYPE));
				String ccc =  parseCCC(contract.get(ENTERPRISE_CCC.CCC));
				String identType = getIndetType(contract.get(REGISTRY.DOCUMENT_TYPE));
				String ident = contract.get(REGISTRY.DOCUMENT);
				String cccProvincePrincipal = principalCCCs.get(0) == null ? "00" : principalCCCs.get(0).get(GEOZONE.CODE);
				String cccPrincipal = parseCCC(principalCCCs.get(0) == null ? "000000000" : principalCCCs.get(0).get(ENTERPRISE_CCC.CCC));
				
				String fullCcc = cccRegimeCode + AonStringUtils.leftPad(contract.get(GEOZONE.CODE), 2, '0') + AonStringUtils.leftPad(ccc, 9, '0');
				String fullPrincipalCcc = "0111" + AonStringUtils.leftPad(cccProvincePrincipal, 2, '0') + AonStringUtils.leftPad(cccPrincipal, 9, '0');
				
				JSONObject emp = new JSONObject();
				emp.put("fullCcc", fullCcc);
				emp.put("identType", identType);
				emp.put("country", "011");
				emp.put("ident", ident);
				emp.put("fullPrincipalCcc", fullPrincipalCcc);
				contractObj.put("EMP", emp);
				
				//RZS
				String rzsName = removeAccents(contract.get(REGISTRY.NAME));
				if(AonStringUtils.isNotBlank(rzsName))
					rzsName = stripDiacritics(rzsName);
				
				JSONObject rzsData = new JSONObject();
				rzsData.put("rzsName", rzsName);
				contractObj.put("RZS", rzsData);
				
				//TRA
				contractObj.put("TRA", getTRA(contract.get(CONTRACT.ID), dslContext));
				
				//AYN
				contractObj.put("AYN", getAYN(contract.get(CONTRACT.ID), dslContext));
								
				// CNOs
				contSeg++;
				JSONObject moc = getMOC(contract.get(CONTRACT.ID), dslContext);
				contractObj.put("MOC", moc);
				
				contractsArr.add(contractObj);
				
			}
			
			employeesCNOAFIJSON.put("CONTRACTS", contractsArr);
			
			//ETF
			JSONObject etf = new JSONObject();
			etf.put("authkey", authKey);
			etf.put("payrollProvider", "498");
			etf.put("fileName", fileName);
			etf.put("employees", contracts.size());
			etf.put("totalLines", contracts.size() * 6 + 2); // ETI, ETF
			employeesCNOAFIJSON.put("ETF", etf);
				
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return employeesCNOAFIJSON;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject getMOC(int contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		JSONObject fab = new JSONObject();
		JSONObject odl = new JSONObject();
		
		Calendar currentDate = Calendar.getInstance();
		
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
				.and(
					CONTRACT_DATA.END_DATE.isNull()
					.or(CONTRACT_DATA.END_DATE.ge(new Date(currentDate.getTimeInMillis())))
					.or(CONTRACT_DATA.END_DATE.eq(contractRecord.get(CONTRACT.END_DATE)))
				).orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
		
		Result<Record> contractDataEmployeeColectiveRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(EMPLOYEECOLECTIVE))
				.orderBy(CONTRACT_DATA.START_DATE.desc())
				.fetch();
		
		Result<Record> contractDataCnoRecord = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq(CNO))
				.orderBy(CONTRACT_DATA.ID.desc())
				.fetch();
		
		Byte gender = dslContext.select(PERSON.GENDER).from(PERSON)
				.where(PERSON.REGISTRY.eq(contractRecord.get(CONTRACT.PERSON)))
				.fetchOne(PERSON.GENDER);
		
		String quoteGroup =  contractDataQuoteRecord.isEmpty() ? null : parseContractData(contractDataQuoteRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String tc2 = parseContractData(contractDataTC2Record.get(0).get(CONTRACT_DATA.EXPRESSION));
		String partialityCoef = contractDataPCRecord.isEmpty() ? null : parseContractData(contractDataPCRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String employeeColective = contractDataEmployeeColectiveRecord.isEmpty() ? null : parseContractData(contractDataEmployeeColectiveRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		String cno = parseContractData(contractDataCnoRecord.isEmpty() ? "" : contractDataCnoRecord.get(0).get(CONTRACT_DATA.EXPRESSION));
		
		// 01-01-2023
		Calendar cnoStartDate = Calendar.getInstance();
		cnoStartDate.set(Calendar.YEAR, 2023);
		cnoStartDate.set(Calendar.MONTH, 0);
		cnoStartDate.set(Calendar.DAY_OF_MONTH, 2);
		
		Date realDate = contractRecord.get(CONTRACT.START_DATE).before(new Date(cnoStartDate.getTimeInMillis())) ? new Date(cnoStartDate.getTimeInMillis()) : contractRecord.get(CONTRACT.START_DATE);
		
		//FAB
		fab.put("action", "MOC");
		fab.put("situation", "");
		fab.put("realDate", dateFormat.format(realDate));
		fab.put("quoteGroup", quoteGroup);
		fab.put("tc2", tc2);
		fab.put("partialityCoef", null == partialityCoef ? "" : parseCoefLengnt(partialityCoef));
		fab.put("employeeColective", employeeColective);
		fab.put("gender", gender);
		
		//ODL
		odl.put("convCollective",  AonStringUtils.leftPad(employeeColective, 14, '0'));
		odl.put("cno", cno);
		
		json.put("FAB", fab);
		json.put("ODL", odl);
		
		return json;
	}
	
	// ------------------------------------------------ Pension Plan AFI

	public static String checkPensionPlanAFI(Connection connection, long date, List<Integer> cccIdList) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		// Given findingDate set start and end date
		Calendar startDate = Calendar.getInstance();
		startDate.setTimeInMillis(date);
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(date);
		endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Date startDateSQL = new Date(startDate.getTimeInMillis());
		Date endDateSQL = new Date(endDate.getTimeInMillis());
		
		System.out.println(startDateSQL);
		System.out.println(endDateSQL);
		System.out.println(cccIdList);
		
		Result<Record> pensionPlanPayments = dslContext.select().from(SALARY_PAYMENT)
			.join(SALARY)
			.on(SALARY.ID.eq(SALARY_PAYMENT.SALARY))
			.join(CONTRACT)
			.on(CONTRACT.ID.eq(SALARY.CONTRACT))
			.join(ENTERPRISE_CCC)
			.on(ENTERPRISE_CCC.ID.eq(CONTRACT.ENTERPRISE_CCC))
			.where(SALARY_PAYMENT.PAYMENT_CONCEPT.eq("PPE"))
			.and(SALARY.START_DATE.ge(startDateSQL))
			.and(SALARY.END_DATE.le(endDateSQL))
			.and(ENTERPRISE_CCC.ID.in(cccIdList))
			.fetch();
		
		if(!hasAuthKeyFromDomain(dslContext, cccIdList)) return "No existe c\u00f3digo de autorizaci\u00f3n. Por favor rellenelo desde el apartado de Empresa de Laboral > Integeral de N\u00f3minas";
		if(!hasSSMutualFromDomain(dslContext, cccIdList)) return "No existe entidad gestora del plan de pensiones. Por favor rellenelo desde el apartado de Empresa de Laboral > Integeral de N\u00f3minas";
		if(pensionPlanPayments.isEmpty()) return "La(s) cuenta(s) de cotizaci\u00F3n seleccionada(s) no tiene(n) devengos con CRA 0000 - APORTACION EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO";
		
		return null;
	}
	
	private static boolean hasAuthKeyFromDomain(DSLContext dslContext, List<Integer> cccIdList) {
		
		// Prepare aunthKey
		Result<Record> domainRecords = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.in(
						dslContext.select(ENTERPRISE_CCC.DOMAIN).from(ENTERPRISE_CCC)
							.where(ENTERPRISE_CCC.ID.in(cccIdList))
				))
				.fetch();
		
		Integer domainId = domainRecords.get(0).get(DOMAIN.ID);
		
		Record enterpriseDataRecord = dslContext.select().from(ENTERPRISE_DATA)
				.where(ENTERPRISE_DATA.NAME.eq("PAY_authorization_key_PAY"))
				.and(ENTERPRISE_DATA.DOMAIN.eq(domainId))
				.fetchOne();
		
		return null != enterpriseDataRecord && AonStringUtils.isNotBlank(enterpriseDataRecord.get(ENTERPRISE_DATA.EXPRESSION));
	}
	
	private static boolean hasSSMutualFromDomain(DSLContext dslContext, List<Integer> cccIdList) {
		Result<Record> domainRecords = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.in(
						dslContext.select(ENTERPRISE_CCC.DOMAIN).from(ENTERPRISE_CCC)
							.where(ENTERPRISE_CCC.ID.in(cccIdList))
				))
				.fetch();
		
		Integer domainId = domainRecords.get(0).get(DOMAIN.ID);
		
		Record enterpriseDataRecord = dslContext.select().from(ENTERPRISE_DATA)
				.where(ENTERPRISE_DATA.NAME.eq("PAY_ss_pension_plan_mutual_PAY"))
				.and(ENTERPRISE_DATA.DOMAIN.eq(domainId))
				.fetchOne();
		
		return null != enterpriseDataRecord && AonStringUtils.isNotBlank(enterpriseDataRecord.get(ENTERPRISE_DATA.EXPRESSION));
		
	}

}
