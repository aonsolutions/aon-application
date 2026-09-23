package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Certifica2Batch.CERTIFICA2_BATCH;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Cno.CNO;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Certifica2Info;
import com.esferalia.aon.gwt.payroll.shared.Certifica2Info.Certifica2Period;
import com.esferalia.aon.jooq.tables.records.Certifica2BatchRecord;
import com.esferalia.aon.jooq.tables.records.DomainRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.payroll.sepe.certifica.CertificaFill;
import com.esferalia.aon.payroll.tgss.cra.StringUtils;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.COTIZACIONREATYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.COTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CUENTACOTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CertificadoEmpresa;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.EMPRESATYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.REPRESENTANTETYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE.DatosVacacionesCotizadas;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE.DatosVacacionesCotizadasREA;
import com.esferalia.aon.watson.util.AonStringUtils;

import aon.sepe.objects.Certificates;
import aon.sepe.objects.Certificates.CertificatesBuilder;
import aon.sepe.objects.Certificates.TypeDuration;
import aon.sepe.objects.QuoteData;
import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class JooqCertifica2 {

	// ---------------------------------------------------------- Constructor

	private JooqCertifica2() {
		super();
	}

	// ---------------------------------------------------------- Variables

	private static Settings settings = null;

	protected static Settings getDefaultSettings() {
		if (settings == null) {
			settings = new Settings();
			settings.setRenderSchema(false);
		}
		return settings;
	}

	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static SimpleDateFormat monthKeyFormat = new SimpleDateFormat("yyyyMM");
	
	private static SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat monthDateFormat = new SimpleDateFormat("MM");
	private static SimpleDateFormat monthStrDateFormat = new SimpleDateFormat("MMMM");
	private static SimpleDateFormat dayDateFormat = new SimpleDateFormat("dd");

	private static DecimalFormat decimalFormat = new DecimalFormat("0000000.00");

	// -----------------------------------------------------
	// createCertifica2DB@2Info

	public static void createCertifica2DBServlet(String domainName, String user, Integer contractId, String suspensionReason, String ereCode, java.util.Date ereEnd) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			createCertifica2DB(connection, domainName, user, domainId, contractId, suspensionReason, ereCode, ereEnd);
		} catch (Exception e) {
			// Not use here
		}
	}

	public static void createCertifica2DB(Connection connection, String domainName, String user, Integer domainId, Integer contractId, String suspensionCode, String ereCode, java.util.Date ereEnd) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = getCertifica2Info(dslContext, domainName, user, contractId, suspensionCode, ereCode, ereEnd);
		saveCertifica2Info(dslContext, domainName, user, domainId, contractId, certifica2Info.getSuspensionCode(), ereCode, ereEnd);
	}
	
	public static byte[] createCertEnterprisePDF(String domainName, String user, Integer contractId, String suspensionReasonCode, String suspensionReason, String ereCode, java.util.Date ereEnd) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			if (AonStringUtils.isBlank(suspensionReasonCode))
				suspensionReasonCode = getsuspensionReasonCodeDB(dslContext, contractId);

			com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = getCertifica2Info(dslContext, domainName, user, contractId, suspensionReasonCode, ereCode, ereEnd);
			
			// Set suspensionCode and reason
			certifica2Info.setSuspensionCode(suspensionReasonCode);
			certifica2Info.setSuspension(suspensionReason);
			
			Map<String, String> fieldsMap;
			
			if (AonStringUtils.equalsIgnoreCase(certifica2Info.getRegime(), "0163"))
				fieldsMap = createCertifica2AgrarianPDFFieldsMap(certifica2Info);
			else
				fieldsMap = createCertifica2PDFFieldsMap(certifica2Info);
			
			if (AonStringUtils.equalsIgnoreCase(certifica2Info.getRegime(), "0163"))
				return CertificaFill.exportCertEnterpriseAgrarianPDF(fieldsMap);
			else
				return CertificaFill.exportCertEnterprisePDF(fieldsMap);
		} catch (Exception e) {
			return new byte[0];
		}
	}
	
	private static Map<String, String> createCertifica2PDFFieldsMap(Certifica2Info certifica2Info) {
		Map<String, String> fieldMap = new HashMap<>();
		
		fieldMap.put("01", certifica2Info.getRepresentativeName() + " " + certifica2Info.getRepresentativeSurname() + " con DNI o NIE " + certifica2Info.getRepresentativeDocument());
		fieldMap.put("02", certifica2Info.getRepresentativeWork());
		fieldMap.put("03", certifica2Info.getEnterpriseName());
		fieldMap.put("04", certifica2Info.getRegime());
		fieldMap.put("05", "REGIMEN GENERAL");
		fieldMap.put("06", certifica2Info.getCompleteCCC());
		fieldMap.put("07", certifica2Info.getAddress());
		fieldMap.put("08", certifica2Info.getCity());
		fieldMap.put("09", certifica2Info.getZip());
		fieldMap.put("010", certifica2Info.getGeozone());
		fieldMap.put("011", certifica2Info.getCnaeCode());
		fieldMap.put("012", certifica2Info.getCnae());
		fieldMap.put("014", certifica2Info.getName() + " " + certifica2Info.getSurname() + (AonStringUtils.isBlank(certifica2Info.getSecondSurname()) ? "" : " " + certifica2Info.getSecondSurname()));
		fieldMap.put("015", certifica2Info.getDocument());
		fieldMap.put("016", certifica2Info.getSSNumber());
		fieldMap.put("017", certifica2Info.getQuoteGroup());
		fieldMap.put("018", certifica2Info.getContractType());
		fieldMap.put("019", certifica2Info.getContractDuration() + " dias");
		fieldMap.put("051", certifica2Info.getCnoCode());
		fieldMap.put("052", certifica2Info.getCno());
		fieldMap.put("055", dateFormat.format(certifica2Info.getStartDate()));
		fieldMap.put("056", certifica2Info.getSuspensionCode());
		fieldMap.put("057", certifica2Info.getSuspension());
		fieldMap.put("058", dayDateFormat.format(certifica2Info.getEndDate()));
		fieldMap.put("059", monthDateFormat.format(certifica2Info.getEndDate()));
		fieldMap.put("060", yearDateFormat.format(certifica2Info.getEndDate()));
		
		if(AonStringUtils.isNotBlank(certifica2Info.getErteCode())) {
			fieldMap.put("061", dayDateFormat.format(certifica2Info.getErteEnd()));
			fieldMap.put("062", monthDateFormat.format(certifica2Info.getErteEnd()));
			fieldMap.put("063", yearDateFormat.format(certifica2Info.getErteEnd()));
			
			fieldMap.put("064", certifica2Info.getErteCode());
			fieldMap.put("642", certifica2Info.getErteCoef());
		}
		
		Double totalDays = 0.00;
		Double totalCgc = 0.00;
		Double totalCgp = 0.00;
		
		Integer idx = 138;
		for(Map<String, String> quoteData : certifica2Info.getQuoteDataList()) {
			fieldMap.put(checkIdx(idx), quoteData.get("anioCtz"));
			idx++;
			fieldMap.put(checkIdx(idx), quoteData.get("monthCtz"));
			idx++;
			Double days = Double.parseDouble(quoteData.get("daysCtz"));
			totalDays += days;
			fieldMap.put(checkIdx(idx), quoteData.get("daysCtz"));
			idx++;
			Double bccc = Double.parseDouble(quoteData.get("bccc"));
			totalCgc += bccc;
			fieldMap.put(checkIdx(idx), quoteData.get("bccc"));
			idx++;
			Double bcd = Double.parseDouble(quoteData.get("bcd"));
			totalCgp += bcd;
			fieldMap.put(checkIdx(idx), quoteData.get("bcd"));
			idx++;
			idx++;
			
		}
		
		fieldMap.put("0186", certifica2Info.getSettleQuoteDays().toString());
		fieldMap.put("0187", certifica2Info.getBaseCgc().toString());
		fieldMap.put("0188", certifica2Info.getBaseUnemployment().toString());
		

		totalDays += certifica2Info.getSettleQuoteDays();
		totalCgc += certifica2Info.getBaseCgc();
		totalCgp += certifica2Info.getBaseUnemployment();
		
		fieldMap.put("0190", totalDays.toString());
		fieldMap.put("0191", totalCgc.toString());
		fieldMap.put("0192", totalCgp.toString());
		
		fieldMap.put("01021", certifica2Info.getGeozone());
		
		java.util.Date date = new java.util.Date();
		fieldMap.put("01022", dayDateFormat.format(date));
		fieldMap.put("01023", monthStrDateFormat.format(date));
		fieldMap.put("01024", yearDateFormat.format(date).substring(2, 4));
		
		return fieldMap;
	}
	
	private static Map<String, String> createCertifica2AgrarianPDFFieldsMap(Certifica2Info certifica2Info) {
		Map<String, String> fieldMap = new HashMap<>();
		
		fieldMap.put("03", certifica2Info.getRepresentativeName() + " " + certifica2Info.getRepresentativeSurname() + " con DNI o NIE " + certifica2Info.getRepresentativeDocument());
		fieldMap.put("04", certifica2Info.getRepresentativeWork());
		fieldMap.put("05", certifica2Info.getEnterpriseName());
		fieldMap.put("06", certifica2Info.getCompleteCCC());
		fieldMap.put("07", certifica2Info.getAddress());
		fieldMap.put("08", certifica2Info.getCity());
		fieldMap.put("09", certifica2Info.getZip());
		fieldMap.put("010", certifica2Info.getGeozone());
		fieldMap.put("011", certifica2Info.getCnaeCode());
		fieldMap.put("012", certifica2Info.getCnae());
		fieldMap.put("015", certifica2Info.getName() + " " + certifica2Info.getSurname() + (AonStringUtils.isBlank(certifica2Info.getSecondSurname()) ? "" : " " + certifica2Info.getSecondSurname()));
		fieldMap.put("016", certifica2Info.getDocument());
		fieldMap.put("017", certifica2Info.getSSNumber());
		fieldMap.put("018", certifica2Info.getContractType());
		fieldMap.put("019", certifica2Info.getContractDuration() + " dias");
		fieldMap.put("020", certifica2Info.getCnoCode());
		fieldMap.put("021", certifica2Info.getCno());
		fieldMap.put("025", dateFormat.format(certifica2Info.getStartDate()));
		fieldMap.put("026", certifica2Info.getSuspensionCode());
		fieldMap.put("028", certifica2Info.getSuspension());
		fieldMap.put("029", dateFormat.format(certifica2Info.getEndDate()));
		
		Double totalDays = 0.00;
		Double totalCgp = 0.00;
		
		Integer idx = 37;
		for(Map<String, String> quoteData : certifica2Info.getQuoteDataList()) {
			fieldMap.put(checkIdx(idx), quoteData.get("anioCtz"));
			idx++;
			fieldMap.put(checkIdx(idx), quoteData.get("monthCtz"));
			idx++;
			fieldMap.put(checkIdx(idx), certifica2Info.getQuoteGroup());
			idx++;
			
			Double days = Double.parseDouble(quoteData.get("daysCtz"));
			totalDays += days;
			
			if(AonStringUtils.isNotBlank(certifica2Info.getMdCtz()) && AonStringUtils.equalsIgnoreCase(certifica2Info.getMdCtz(), "2")) {
				idx++;
				fieldMap.put(checkIdx(idx), quoteData.get("daysCtz"));
				idx++;
			} else {
				fieldMap.put(checkIdx(idx), quoteData.get("daysCtz"));
				idx++;
				idx++;
			}
			
			Double bcd = Double.parseDouble(quoteData.get("bcd"));
			totalCgp += bcd;
			fieldMap.put(checkIdx(idx), quoteData.get("bcd"));
			idx++;
			idx++;
			
		}
		
		fieldMap.put("095", certifica2Info.getQuoteGroup());
		if(AonStringUtils.isNotBlank(certifica2Info.getMdCtz()) && AonStringUtils.equalsIgnoreCase(certifica2Info.getMdCtz(), "2"))
			fieldMap.put("094", certifica2Info.getSettleQuoteDays().toString());
		else fieldMap.put("093", certifica2Info.getSettleQuoteDays().toString());
		fieldMap.put("097", certifica2Info.getBaseUnemployment().toString());
		
		totalDays += certifica2Info.getSettleQuoteDays();
		totalCgp += certifica2Info.getBaseUnemployment();
		
		if(AonStringUtils.isNotBlank(certifica2Info.getMdCtz()) && AonStringUtils.equalsIgnoreCase(certifica2Info.getMdCtz(), "2"))
			fieldMap.put("099", totalDays.toString());
		else fieldMap.put("098", totalDays.toString());
		
		fieldMap.put("0102", totalCgp.toString());
		
		fieldMap.put("101", certifica2Info.getGeozone());
		
		java.util.Date date = new java.util.Date();
		fieldMap.put("102", dayDateFormat.format(date));
		fieldMap.put("103", monthStrDateFormat.format(date));
		fieldMap.put("104", yearDateFormat.format(date).substring(2, 4));
		
		return fieldMap;
	}

	private static String checkIdx(Integer idx) {
		String idxStr = idx.toString();
		return idxStr.length() == 3 ? AonStringUtils.leftPad(idxStr, 4, '0') : AonStringUtils.leftPad(idxStr, 3, '0');
	}

	// ----------------------------------------------------- getCertific@2Info

	public static com.esferalia.aon.gwt.payroll.shared.Certifica2Info getCertifica2Info(Connection connection,
			String domainName, String user, Integer contractId, String suspensionReasonCode, String ereCode, Date ereEnd) throws IllegalArgumentException {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		return getCertifica2Info(dslContext, domainName, user, contractId, suspensionReasonCode, ereCode, ereEnd);
	}

	private static com.esferalia.aon.gwt.payroll.shared.Certifica2Info getCertifica2Info(DSLContext dslContext,
			String domainName, String user, Integer contractId, String suspensionReasonCode, String ereCode, java.util.Date ereEnd) throws IllegalArgumentException {

		com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = new com.esferalia.aon.gwt.payroll.shared.Certifica2Info();

		// Contract/Employee Data

		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();

		Integer enterpriseCCCId = contractRecord.get(CONTRACT.ENTERPRISE_CCC);
		Integer enterpriseActivityId = contractRecord.get(CONTRACT.ENTERPRISE_ACTIVITY);
		
		getContractData(dslContext, certifica2Info, contractRecord, suspensionReasonCode, ereCode, ereEnd);

		// Representative Data

		Integer domainId = contractRecord.get(CONTRACT.DOMAIN);
		DomainRecord domainRecord = dslContext.selectFrom(DOMAIN).where(DOMAIN.ID.eq(domainId)).fetchOne();
		getReprensentativeData(dslContext, certifica2Info, domainId, domainRecord.getParent(), contractRecord);

		// Enterprise Data

		Integer registryId = getEnterpriseData(dslContext, certifica2Info, enterpriseCCCId, enterpriseActivityId, contractId);
		RegistryAddress address = AON.get(domainName, domainId, user, ((RegistryAddressFilter) f -> f.getRegistryProperty().eq(registryId)));
		certifica2Info.setAddress(normalizeString(address.getAddress()));
		certifica2Info.setCity(normalizeString(address.getCity()));
		certifica2Info.setZip(address.getZip());
		certifica2Info.setGeozone(normalizeString(address.getGeozoneName()));

		// Certifica2 Holidays

		getCertifica2Holidays(dslContext, certifica2Info, contractId);
		
		// Certifica2 Periods

		Date seniorityDate = contractRecord.get(CONTRACT.SENIORITY_DATE);
		getCertifica2Periods(dslContext, certifica2Info, seniorityDate, contractId);

		return certifica2Info;
	}

	private static void getContractData(DSLContext dslContext,
			com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, Record contractRecord,
			String suspensionReasonCode, String ereCode, java.util.Date ereEnd) throws IllegalArgumentException {

		Integer contractId = contractRecord.get(CONTRACT.ID);
		Integer personId = contractRecord.get(CONTRACT.PERSON);

		//Date startDate = null != contractRecord.get(CONTRACT.SENIORITY_DATE) ? contractRecord.get(CONTRACT.SENIORITY_DATE) : contractRecord.get(CONTRACT.START_DATE);
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);

		Result<Record> ereRecords = null;
		
		if (null == endDate) {
			ereRecords = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.in(ContextVariable.ERE_FACTORS_NAMES))
					.orderBy(CONTRACT_DATA.START_DATE)
					.fetch();
			
			if(null == ereRecords || ereRecords.isEmpty())
				throw new IllegalArgumentException("No existe fecha fin para este contrato");
			
			endDate = ereRecords.get(0).get(CONTRACT_DATA.START_DATE);
		}

		int contractDuration = getDaysBetween(startDate, endDate);

		Record personRecord = dslContext.select().from(PERSON).where(PERSON.REGISTRY.eq(personId)).fetchOne();

		String name = personRecord.get(PERSON.NAME);
		String surName = personRecord.get(PERSON.FIRST_SURNAME);
		String secondSurName = personRecord.get(PERSON.SECOND_SURNAME);
		String ssNum = personRecord.get(PERSON.SOCIAL_SECURITY_NUM);

		if (AonStringUtils.isBlank(ssNum))
			throw new IllegalArgumentException("No existe numero de la Seguridad Social para esta persona");

		Record employeeRegistry = dslContext.select().from(REGISTRY).where(REGISTRY.ID.eq(personId)).fetchOne();

		String dni = employeeRegistry.get(REGISTRY.DOCUMENT);

		if (AonStringUtils.isBlank(dni))
			throw new IllegalArgumentException("No existe documento de identidad para esta persona");

		List<String> contractTypeList = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("TC2"))
				.orderBy(CONTRACT_DATA.START_DATE.desc()).fetch(CONTRACT_DATA.EXPRESSION);

		String contractType = contractTypeList.isEmpty() ? null : contractTypeList.get(0);

		String tc2 = normalizeString(contractType);

		List<String> quoteGroupTypeList = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION"))
				.orderBy(CONTRACT_DATA.START_DATE.desc()).fetch(CONTRACT_DATA.EXPRESSION);

		if (quoteGroupTypeList.isEmpty())
			throw new IllegalArgumentException("No existe grupo de cotizacion para este contrato");

		String quoteGroupType = quoteGroupTypeList.get(0);

		String quoteGroup = normalizeString(quoteGroupType);

		List<String> cnoTypeList = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("CNO"))
				.orderBy(CONTRACT_DATA.ID.desc()).fetch(CONTRACT_DATA.EXPRESSION);

		String cnoCode = null;
		String cno = null;

		if (!cnoTypeList.isEmpty()) {
			cnoCode = normalizeString(cnoTypeList.get(0));
			cno = dslContext.select(CNO.TITLE).from(CNO).where(CNO.CODE.eq(cnoCode)).fetchOne(CNO.TITLE);
		}
		
		// certifica2Info
		certifica2Info.setDocument(dni);
		certifica2Info.setSSNumber(ssNum);
		certifica2Info.setName(normalizeString(name));
		certifica2Info.setSurname(normalizeString(surName));
		certifica2Info.setSecondSurname(normalizeString(secondSurName));
		certifica2Info.setContractType(tc2);
		certifica2Info.setQuoteGroup(quoteGroup);
		certifica2Info.setContractDuration(contractDuration);
		certifica2Info.setProfesionalCategory(cnoCode);
		certifica2Info.setCnoCode(cnoCode);
		certifica2Info.setCno(cno);
		certifica2Info.setSuspensionCode(suspensionReasonCode);
		
		if(AonStringUtils.equalsIgnoreCase(tc2, "300"))
			startDate = null != contractRecord.get(CONTRACT.SENIORITY_DATE) ? contractRecord.get(CONTRACT.SENIORITY_DATE) : contractRecord.get(CONTRACT.START_DATE);
		
		if(null != ereRecords) {
			// Get last ere date for ereEndDate
			Date ereStartDate = ereRecords.get(0).get(CONTRACT_DATA.START_DATE);
			Date ereEndDate = ereRecords.get(ereRecords.size() - 1).get(CONTRACT_DATA.END_DATE);
			String ereCoef = ereRecords.get(0).get(CONTRACT_DATA.EXPRESSION);
			Double parseEreCoef = Double.parseDouble(ereCoef) * 100;
			
			certifica2Info.setStartDate(startDate);
			certifica2Info.setEndDate(ereStartDate);
			certifica2Info.setErteCoef(parseEreCoef.intValue() + "");
			certifica2Info.setErteEnd(null == ereEndDate ? ereEnd : ereEndDate);
			certifica2Info.setErteCode(ereCode);
			
			certifica2Info.setSuspensionCode("17");
		} else {
			certifica2Info.setStartDate(startDate);
			certifica2Info.setEndDate(endDate);
		}

	}

	private static Integer getEnterpriseData(DSLContext dslContext,
			com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, Integer enterpriseCCCId,
			Integer enterpriseActivityId, Integer contractId) {

		Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.ID.eq(enterpriseCCCId)).fetchOne();

		String regime = parseSSRegime(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE));
		String ccc = enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC);
		String completeCCC = regime + ccc;

		Record enterpriseRegistryRecord = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY)
						.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseActivityId))
						.fetchOne(ENTERPRISE_ACTIVITY.ENTERPRISE)))
				.fetchOne();

		Integer registryId = enterpriseRegistryRecord.get(REGISTRY.ID);
		String enterpriseCIF = enterpriseRegistryRecord.get(REGISTRY.DOCUMENT);
		String enterpriseName = enterpriseRegistryRecord.get(REGISTRY.NAME);
		
		certifica2Info.setRegime(regime);
		
		if(AonStringUtils.equalsIgnoreCase(regime, "0163")) {
			Result<Record> mdCtzRecords = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.eq("MODELO_COTIZACION_AGRARIO"))
					.orderBy(CONTRACT_DATA.ID.desc()).fetch();
			if(mdCtzRecords.isNotEmpty())
				certifica2Info.setMdCtz(mdCtzRecords.get(0).get(CONTRACT_DATA.EXPRESSION));
		}
		
		Integer cnaeCodeInt = dslContext.select(ENTERPRISE_ACTIVITY.CNAE2009).from(ENTERPRISE_ACTIVITY)
			.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseActivityId))
			.fetchOne(ENTERPRISE_ACTIVITY.CNAE2009);
		
		String cnaeCode = null;
		String cnae = null;
		
		if(null != cnaeCodeInt) {
			cnaeCode = cnaeCodeInt.toString();
			cnae = dslContext.select(CNAE2009.TITLE).from(CNAE2009).where(CNAE2009.CODE.eq(cnaeCode)).fetchOne(CNAE2009.TITLE);
			if(AonStringUtils.isBlank(cnae)) cnae = dslContext.select(CNAE2009.TITLE).from(CNAE2009).where(CNAE2009.ID.eq(cnaeCodeInt)).fetchOne(CNAE2009.TITLE);
		}
			
		
		certifica2Info.setCcc(ccc);
		certifica2Info.setCompleteCCC(completeCCC);
		certifica2Info.setEnterpriseDocument(enterpriseCIF);
		certifica2Info.setEnterpriseName(normalizeString(enterpriseName));
		
		certifica2Info.setCnae(normalizeString(cnae));
		certifica2Info.setCnaeCode(cnaeCode);
		
		return registryId;

	}

	private static void getReprensentativeData(DSLContext dslContext,
			com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, Integer domainId,  Integer parentDomainId,
			Record contractRecord) {

		Integer contractId = contractRecord.get(CONTRACT.ID);
		String staffFullname = dslContext.select(CONTRACT_INFO.EXPRESSION).from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("I_ENTERPRISE_DIR_STAFF_NAME")
						.or(CONTRACT_INFO.NAME.eq("T_ENTERPRISE_DIR_STAFF_NAME"))
						.or(CONTRACT_INFO.NAME.eq("L_ENTERPRISE_DIR_STAFF_NAME"))
						.or(CONTRACT_INFO.NAME.eq("P_ENTERPRISE_DIR_STAFF_NAME")))
				.fetchOne(CONTRACT_INFO.EXPRESSION);

		String representativeDocument = "";
		String representativeName = "";
		String representativeSurname = "";
		String representativeCharge = "";

		if (AonStringUtils.isNotBlank(staffFullname)) {
			if (staffFullname.contains(",")) {
				representativeName = staffFullname.split(",")[1].trim();
				representativeSurname = staffFullname.split(",")[0].trim();
			} else
				representativeName = staffFullname.trim();
		} else {
			Result<Record> representativeRecords = dslContext.select().from(CONTRACT_INFO)
					.where(CONTRACT_INFO.NAME.in("I_ENTERPRISE_DIR_STAFF_NAME", "T_ENTERPRISE_DIR_STAFF_NAME", "L_ENTERPRISE_DIR_STAFF_NAME", "P_ENTERPRISE_DIR_STAFF_NAME"))
					.and(CONTRACT_INFO.CONTRACT.isNull())
					.and(CONTRACT_INFO.DOMAIN.eq(domainId).or(CONTRACT_INFO.DOMAIN.eq(parentDomainId)))
					.orderBy(CONTRACT_INFO.START_DATE.desc())
					.fetch();
			
			if(!representativeRecords.isEmpty() && AonStringUtils.isNotBlank(representativeRecords.get(0).get(CONTRACT_INFO.EXPRESSION))) {
				String representative = representativeRecords.get(0).get(CONTRACT_INFO.EXPRESSION);
				if (representative.contains(",")) {
					representativeName = representative.split(",")[1].trim();
					representativeSurname = representative.split(",")[0].trim();
				} else
					representativeName = representative.trim();
			} else {
				Integer enterpriseRegisty = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
						.where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY);

				Result<Record> staffRecords = dslContext.select().from(RDIR_STAFF)
						.where(RDIR_STAFF.REGISTRY.eq(enterpriseRegisty))
						.and(RDIR_STAFF.REPRESENTATIVE_LABOR.eq((byte)1))
						.fetch();

				// Hay representante de empresa
				if (staffRecords.isNotEmpty()) {
					// Cogemos el primer representate
					Record staffRecord = staffRecords.get(0);
					representativeDocument = staffRecord.get(RDIR_STAFF.DOCUMENT);
					representativeCharge = staffRecord.get(RDIR_STAFF.CHARGE_DESCRIPTION);
					String fullName = staffRecord.get(RDIR_STAFF.NAME);

					if (fullName.contains(",")) {
						representativeName = fullName.split(",")[1].trim();
						representativeSurname = fullName.split(",")[0].trim();
					} else
						representativeName = fullName.trim();

					// No hay representante
				} else {
					Record enterpriseRegistry = dslContext.select().from(REGISTRY).where(REGISTRY.ID.eq(enterpriseRegisty))
							.fetchOne();

					representativeDocument = enterpriseRegistry.get(REGISTRY.DOCUMENT);
					representativeName = enterpriseRegistry.get(REGISTRY.NAME);
				}
			}
		}

		// Get from contract
		String staffDocument = dslContext.select(CONTRACT_INFO.EXPRESSION).from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("I_ENTERPRISE_DIR_STAFF_NIF")
						.or(CONTRACT_INFO.NAME.eq("T_ENTERPRISE_DIR_STAFF_NIF"))
						.or(CONTRACT_INFO.NAME.eq("L_ENTERPRISE_DIR_STAFF_NIF"))
						.or(CONTRACT_INFO.NAME.eq("P_ENTERPRISE_DIR_STAFF_NIF")))
				.fetchOne(CONTRACT_INFO.EXPRESSION);

		if (AonStringUtils.isNotBlank(staffDocument))
			representativeDocument = staffDocument;
		

		// Get from domain
		List<String> staffDocuments = dslContext.select(CONTRACT_INFO.EXPRESSION).from(CONTRACT_INFO)
				.where(CONTRACT_INFO.NAME.eq("I_ENTERPRISE_DIR_STAFF_NIF")
						.or(CONTRACT_INFO.NAME.eq("T_ENTERPRISE_DIR_STAFF_NIF"))
						.or(CONTRACT_INFO.NAME.eq("L_ENTERPRISE_DIR_STAFF_NIF"))
						.or(CONTRACT_INFO.NAME.eq("P_ENTERPRISE_DIR_STAFF_NIF")))
				.and(CONTRACT_INFO.CONTRACT.isNull())
				.and(CONTRACT_INFO.DOMAIN.eq(domainId).or(CONTRACT_INFO.DOMAIN.eq(parentDomainId)))
				.orderBy(CONTRACT_INFO.START_DATE.desc())
				.fetch(CONTRACT_INFO.EXPRESSION);

		if (AonStringUtils.isBlank(representativeDocument))
			if(staffDocuments.isEmpty()) throw new IllegalArgumentException("No existe documento del representante de la empresa");
			else representativeDocument = staffDocuments.get(0);
		
		// Get from contract
		String staffCharge = dslContext.select(CONTRACT_INFO.EXPRESSION).from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("I_ENTERPRISE_DIR_STAFF_CHARGE")
						.or(CONTRACT_INFO.NAME.eq("T_ENTERPRISE_DIR_STAFF_CHARGE"))
						.or(CONTRACT_INFO.NAME.eq("L_ENTERPRISE_DIR_STAFF_CHARGE"))
						.or(CONTRACT_INFO.NAME.eq("P_ENTERPRISE_DIR_STAFF_CHARGE")))
				.fetchOne(CONTRACT_INFO.EXPRESSION);

		if (AonStringUtils.isNotBlank(staffCharge))
			representativeCharge = staffCharge;
		
		// Get from domain
		List<String> staffCharges = dslContext.select(CONTRACT_INFO.EXPRESSION).from(CONTRACT_INFO)
				.where(CONTRACT_INFO.NAME.eq("I_ENTERPRISE_DIR_STAFF_CHARGE")
						.or(CONTRACT_INFO.NAME.eq("T_ENTERPRISE_DIR_STAFF_CHARGE"))
						.or(CONTRACT_INFO.NAME.eq("L_ENTERPRISE_DIR_STAFF_CHARGE"))
						.or(CONTRACT_INFO.NAME.eq("P_ENTERPRISE_DIR_STAFF_CHARGE")))
				.and(CONTRACT_INFO.CONTRACT.isNull())
				.and(CONTRACT_INFO.DOMAIN.eq(domainId).or(CONTRACT_INFO.DOMAIN.eq(parentDomainId)))
				.orderBy(CONTRACT_INFO.START_DATE.desc())
				.fetch(CONTRACT_INFO.EXPRESSION);

		if (!staffCharges.isEmpty())
			representativeCharge = staffCharges.get(0);

		certifica2Info.setRepresentativeDocument(representativeDocument);
		certifica2Info.setRepresentativeName(normalizeString(representativeName));
		certifica2Info.setRepresentativeSurname(normalizeString(representativeSurname));
		certifica2Info.setRepresentativeWork(normalizeString(representativeCharge));

	}

	private static void getCertifica2Periods(DSLContext dslContext,
			com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, Date seniorityDate, Integer contractId) {

		List<Certifica2Period> certifica2List = new ArrayList<>();

		// Los dias de vacaciones no disfrutadas ya consumen cupo (son posteriores a la baja)
		Integer maxDays = certifica2Info.getSettleQuoteDays();

		boolean generalRegime = !AonStringUtils.equalsIgnoreCase(certifica2Info.getRegime(), "0163");

		java.util.Date filterDateJava = DateUtils.copyDateOnly(certifica2Info.getEndDate());
		filterDateJava = DateUtils.addDays2Date(filterDateJava, -180);
		Date filterDate = parseDateToSQL(filterDateJava);

		if (null != seniorityDate)
			filterDate = filterDate.before(seniorityDate) ? seniorityDate : filterDate;

		Date windowEndDate = parseDateToSQL(certifica2Info.getEndDate());

		// Bases adicionales (atrasos + preaviso) indexadas por anio-mes.
		// Solo regimen general de momento.
		Map<String, Double[]> extraBases = generalRegime
				? getExtraBases(dslContext, contractId, filterDate, windowEndDate)
				: new HashMap<>();

		// Un mes solo puede recibir la base extra una vez, aunque tenga varias nominas
		List<String> consumedMonths = new ArrayList<>();

		Result<Record> salariesRecords = dslContext.select().from(SALARY)
				.where(SALARY.SOCIAL_SECURITY_NUMBER.eq(certifica2Info.getSSNumber()))
				.and(SALARY.CCC.eq(certifica2Info.getCcc()))
				.and(SALARY.TYPE.eq(SalaryType.SALARY.value()))
				.and(SALARY.END_DATE.ge(filterDate))
				.and(SALARY.END_DATE.le(windowEndDate))
				.and(SALARY.CONTRACT.eq(contractId))
				.orderBy(SALARY.END_DATE.desc())
				.fetch();

		// Using for agrarian only
		Integer contractDuration = 0;

		for (Record salary : salariesRecords) {
			if (maxDays >= 180)
				break;

			Integer salaryId = salary.get(SALARY.ID);

			Date salaryStartDate = salary.get(SALARY.START_DATE);
			Date salaryEndDate = salary.get(SALARY.END_DATE);

			int salaryDaysBetween = 0;
			if (!generalRegime) {
				if (AonStringUtils.isNotBlank(certifica2Info.getMdCtz())
						&& AonStringUtils.equalsIgnoreCase(certifica2Info.getMdCtz(), "2")) {
					salaryDaysBetween = getAgrarianDays(dslContext, salaryId);
					contractDuration += salaryDaysBetween;
				} else {
					salaryDaysBetween = getDaysBetween(salaryStartDate, salaryEndDate);
					contractDuration += salaryDaysBetween;
				}
			} else
				salaryDaysBetween = getDaysBetween(salaryStartDate, salaryEndDate);

			Double baseCGC = salary.get(SALARY.CGC_BASE);
			Double baseCGP = salary.get(SALARY.CGP_BASE);

			// Atrasos y preaviso: se suman INTEGROS al mes, aunque la nomina sea parcial
			String monthKey = monthKeyFormat.format(salaryStartDate);
			if (!consumedMonths.contains(monthKey)) {
				Double[] extra = extraBases.get(monthKey);
				if (null != extra) {
					baseCGC += extra[0];
					baseCGP += extra[1];
					consumedMonths.add(monthKey);
				}
			}

			maxDays = addCertifica2Period(certifica2List, maxDays, salaryStartDate, salaryDaysBetween, baseCGC, baseCGP);
		}

		List<Map<String, String>> quoteDataList = new ArrayList<>();

		certifica2List.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));

		for (Certifica2Period certifica2 : certifica2List) {
			Map<String, String> quoteData = new HashMap<>();

			quoteData.put("anioCtz", certifica2.getYear());
			quoteData.put("monthCtz", certifica2.getMonth());
			quoteData.put("daysCtz", certifica2.getQuotedDays() + "");
			quoteData.put("bccc", round(certifica2.getBase_cgc(), 2) + "");
			quoteData.put("bcd", round(certifica2.getBase_unemployment(), 2) + "");

			if (!checkQuoteData(quoteDataList, quoteData))
				quoteDataList.add(quoteData);
		}

		// Contract duration for agrarian only
		if (contractDuration != 0)
			certifica2Info.setContractDuration(contractDuration);

		certifica2Info.setQuoteDataList(quoteDataList);
	}
	
	/**
	 * Bases adicionales que hay que sumar a las de la nomina, indexadas por anio-mes (yyyyMM):
	 *   - Atrasos (SalaryType.DELAY) cuyo charge_date cae dentro de la ventana de 180 dias.
	 *   - Preaviso: los BASE_CGC/BASE_CGP del finiquito contenidos en el rango de DIAS_PREAVISO.
	 * Ninguna de las dos aporta dias cotizados, solo base.
	 */
	private static Map<String, Double[]> getExtraBases(DSLContext dslContext, Integer contractId,
			Date windowStart, Date windowEnd) {

		Map<String, Double[]> extraBases = new HashMap<>();

		// ---------------- Atrasos ----------------
		List<Integer> delayIds = dslContext.select(SALARY.ID).from(SALARY)
				.where(SALARY.TYPE.eq(SalaryType.DELAY.value()))
				.and(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.CHARGE_DATE.ge(windowStart))
				.and(SALARY.CHARGE_DATE.le(windowEnd))
				.fetch(SALARY.ID);

		if (!delayIds.isEmpty()) {
			Result<Record> delayBaseRecords = dslContext.select().from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.in(delayIds))
					.and(SALARY_DATA.NAME.in("BASE_CGC", "BASE_CGP"))
					.fetch();

			for (Record delayBaseRecord : delayBaseRecords)
				addExtraBase(extraBases, delayBaseRecord);
		}

		// ---------------- Preaviso ----------------
		Result<Record> settlementRecords = getSettlementRecords(dslContext, contractId);

		if (settlementRecords.isEmpty())
			return extraBases;

		Integer settlementId = settlementRecords.get(0).get(SALARY.ID);

		Result<Record> preavisoRecords = dslContext.select().from(SALARY_DATA)
				.where(SALARY_DATA.SALARY.eq(settlementId))
				.and(SALARY_DATA.NAME.eq("DIAS_PREAVISO")).fetch();

		if (preavisoRecords.isEmpty())
			return extraBases;

		// Los tramos de vacaciones no disfrutadas caen dentro del rango de preaviso:
		// hay que excluirlos o se contarian dos veces (ya van en la casilla de vacaciones cotizadas)
		List<String> holidayRanges = new ArrayList<>();

		Result<Record> holidaysRecords = dslContext.select().from(SALARY_DATA)
				.where(SALARY_DATA.SALARY.eq(settlementId))
				.and(SALARY_DATA.NAME.eq("DIAS_VACACIONES_NO_DISFRUTADOS")).fetch();

		for (Record holidaysRecord : holidaysRecords)
			holidayRanges.add(getRangeKey(holidaysRecord.get(SALARY_DATA.START_DATE),
					holidaysRecord.get(SALARY_DATA.END_DATE)));

		for (Record preavisoRecord : preavisoRecords) {
			Date preavisoStart = preavisoRecord.get(SALARY_DATA.START_DATE);
			Date preavisoEnd = preavisoRecord.get(SALARY_DATA.END_DATE);

			if (null == preavisoStart || null == preavisoEnd)
				continue;

			Result<Record> preavisoBaseRecords = dslContext.select().from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(settlementId))
					.and(SALARY_DATA.NAME.in("BASE_CGC", "BASE_CGP"))
					.and(SALARY_DATA.START_DATE.ge(preavisoStart))
					.and(SALARY_DATA.END_DATE.le(preavisoEnd))
					.fetch();

			for (Record preavisoBaseRecord : preavisoBaseRecords) {
				if (holidayRanges.contains(getRangeKey(preavisoBaseRecord.get(SALARY_DATA.START_DATE),
						preavisoBaseRecord.get(SALARY_DATA.END_DATE))))
					continue;

				addExtraBase(extraBases, preavisoBaseRecord);
			}
		}

		return extraBases;
	}

	private static void addExtraBase(Map<String, Double[]> extraBases, Record baseRecord) {
		Date baseStartDate = baseRecord.get(SALARY_DATA.START_DATE);

		if (null == baseStartDate)
			return;

		String monthKey = monthKeyFormat.format(baseStartDate);

		Double[] bases = extraBases.get(monthKey);
		if (null == bases) {
			bases = new Double[] { 0.00, 0.00 };
			extraBases.put(monthKey, bases);
		}

		Double value = parseDouble(baseRecord.get(SALARY_DATA.EXPRESSION));

		if (AonStringUtils.equalsIgnoreCase(baseRecord.get(SALARY_DATA.NAME), "BASE_CGC"))
			bases[0] += value;
		else
			bases[1] += value;
	}

	/**
	 * Anade un tramo al listado aplicando el prorrateo si se supera el tope de 180 dias.
	 */
	private static Integer addCertifica2Period(List<Certifica2Period> certifica2List, Integer maxDays,
			Date periodDate, int days, Double baseCGC, Double baseCGP) {

		if (days <= 0)
			return maxDays;

		if (null == baseCGC) baseCGC = 0.00;
		if (null == baseCGP) baseCGP = 0.00;

		int quotedDays = days;
		Double periodCGC = baseCGC;
		Double periodCGP = baseCGP;

		if (maxDays + days > 180) {
			quotedDays = days - (maxDays + days - 180);

			if (quotedDays <= 0)
				return maxDays + days;

			periodCGC = baseCGC / 30 * quotedDays;
			periodCGP = baseCGP / 30 * quotedDays;
		}

		Certifica2Period certifica2Period = new Certifica2Period(yearDateFormat.format(periodDate),
				monthDateFormat.format(periodDate), quotedDays, periodCGC, periodCGP);
		certifica2Period.setDate(periodDate);

		certifica2List.add(certifica2Period);

		return maxDays + days;
	}

	/**
	 * Suma de los salary_data con ese nombre y ese tramo exacto de fechas, o null si no hay ninguno.
	 */
	private static Double getSalaryDataBase(DSLContext dslContext, Integer salaryId, String name,
			Date startDate, Date endDate) {

		List<String> values = dslContext.select(SALARY_DATA.EXPRESSION).from(SALARY_DATA)
				.where(SALARY_DATA.SALARY.eq(salaryId))
				.and(SALARY_DATA.NAME.eq(name))
				.and(null == startDate ? DSL.trueCondition() : SALARY_DATA.START_DATE.eq(startDate))
				.and(null == endDate ? DSL.trueCondition() : SALARY_DATA.END_DATE.eq(endDate))
				.fetch(SALARY_DATA.EXPRESSION);

		if (values.isEmpty())
			return null;

		Double sum = 0.00;
		for (String value : values)
			sum += parseDouble(value);

		return sum;
	}

	private static Result<Record> getSettlementRecords(DSLContext dslContext, Integer contractId) {
		return dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.TYPE.eq(SalaryType.SETTLE.value()))
				.orderBy(SALARY.END_DATE.desc()).fetch();
	}

	private static String getRangeKey(Date startDate, Date endDate) {
		return (null == startDate ? "" : fullDateFormat.format(startDate)) + "|"
				+ (null == endDate ? "" : fullDateFormat.format(endDate));
	}

	private static Double parseDouble(String value) {
		if (AonStringUtils.isBlank(value))
			return 0.00;

		try {
			return Double.parseDouble(value.trim().replace(",", "."));
		} catch (NumberFormatException e) {
			return 0.00;
		}
	}

	private static boolean checkQuoteData(List<Map<String, String>> quoteDataList, Map<String, String> quoteData) {
		Optional<Map<String, String>> quoteDataOpt = quoteDataList.stream().filter(map -> AonStringUtils.equalsIgnoreCase(map.get("anioCtz"), quoteData.get("anioCtz")) && AonStringUtils.equalsIgnoreCase(map.get("monthCtz"), quoteData.get("monthCtz"))).findFirst();
		
		if(!quoteDataOpt.isPresent()) return false;
		
		Integer daysCtz = Integer.parseInt(quoteDataOpt.get().get("daysCtz"));
		daysCtz += Integer.parseInt(quoteData.get("daysCtz"));
		
		Double bccc = Double.parseDouble(quoteDataOpt.get().get("bccc"));
		bccc +=  Double.parseDouble(quoteData.get("bccc"));
		
		Double bcd = Double.parseDouble(quoteDataOpt.get().get("bcd"));
		bcd +=  Double.parseDouble(quoteData.get("bcd"));
		
		quoteDataOpt.get().put("daysCtz", daysCtz + "");
		quoteDataOpt.get().put("bccc", round(bccc, 2) + "");
		quoteDataOpt.get().put("bcd", round(bcd, 2) + "");
		
		return true;
	}

	private static void getCertifica2Holidays(DSLContext dslContext,
			com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, Integer contractId) {

		Result<Record> settlementRecords = getSettlementRecords(dslContext, contractId);

		if (settlementRecords.isEmpty()) {
			certifica2Info.setSettleQuoteDays(0);
			certifica2Info.setBaseCgc(0.00);
			certifica2Info.setBaseUnemployment(0.00);
			return;
		}

		Integer settlementId = settlementRecords.get(0).get(SALARY.ID);

		// Vacaciones no disfrutadas: necesitamos el registro completo (fechas), no solo la expresion
		Result<Record> holidaysRecords = dslContext.select().from(SALARY_DATA)
				.where(SALARY_DATA.SALARY.eq(settlementId))
				.and(SALARY_DATA.NAME.eq("DIAS_VACACIONES_NO_DISFRUTADOS"))
				.orderBy(SALARY_DATA.START_DATE).fetch();

		Record holidaysPaymentRecord = dslContext.select().from(SALARY_PAYMENT)
				.where(SALARY_PAYMENT.SALARY.eq(settlementId)).and(SALARY_PAYMENT.TYPE.eq((byte) 6)).fetchOne();

		Integer holidayDays = 0;
		Double baseCGC = 0.00;
		Double baseCGP = 0.00;
		boolean baseFound = false;

		for (Record holidaysRecord : holidaysRecords) {
			holidayDays += parseDouble(holidaysRecord.get(SALARY_DATA.EXPRESSION)).intValue();

			if (null == holidaysPaymentRecord)
				continue;

			Date holidayStart = holidaysRecord.get(SALARY_DATA.START_DATE);
			Date holidayEnd = holidaysRecord.get(SALARY_DATA.END_DATE);

			// Bases del MISMO tramo de fechas que las vacaciones no disfrutadas
			Double cgc = getSalaryDataBase(dslContext, settlementId, "BASE_CGC", holidayStart, holidayEnd);
			Double cgp = getSalaryDataBase(dslContext, settlementId, "BASE_CGP", holidayStart, holidayEnd);

			if (null != cgc || null != cgp) {
				baseFound = true;
				baseCGC += (null == cgc ? 0.00 : cgc);
				baseCGP += (null == cgp ? 0.00 : cgp);
			}
		}

		// Fallback al comportamiento anterior si no hay salary_data de bases con esas fechas
		if (!baseFound && null != holidaysPaymentRecord) {
			baseCGC = settlementRecords.get(0).get(SALARY.CGC_BASE);
			baseCGP = settlementRecords.get(0).get(SALARY.CGP_BASE);
		}

		if (holidayDays == 0) {
			Date settleEndDate = settlementRecords.get(0).get(SALARY.END_DATE);
			Date contractEndDate = dslContext.select(CONTRACT.END_DATE).from(CONTRACT)
					.where(CONTRACT.ID.eq(contractId)).fetchOne(CONTRACT.END_DATE);

			if (null != contractEndDate)
				holidayDays = DateUtils.getDaysBetween(contractEndDate, settleEndDate);
		}

		certifica2Info.setSettleQuoteDays(holidayDays);
		certifica2Info.setBaseCgc(round(baseCGC, 2));
		certifica2Info.setBaseUnemployment(round(baseCGP, 2));
	}

	// ----------------------------------------------------- saveCertific@2Info to
	// DB

	private static void saveCertifica2Info(DSLContext dslContext, String domainName, String user, Integer domainId, Integer contractId,
			String suspensionReasonCode, String ereCode, java.util.Date ereEnd) throws IllegalArgumentException {
		if (AonStringUtils.isBlank(suspensionReasonCode))
			suspensionReasonCode = getsuspensionReasonCodeDB(dslContext, contractId);

		com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = getCertifica2Info(dslContext, domainName, user, contractId,
				suspensionReasonCode, ereCode, ereEnd);

		// ByteArrayOutputStream
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		getCertificateSepe(certifica2Info, out);

		// Enterprise ID
		Integer enterpriseId = dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY)
				.where(ENTERPRISE_ACTIVITY.ID.eq(dslContext.select(CONTRACT.ENTERPRISE_ACTIVITY).from(CONTRACT)
						.where(CONTRACT.ID.eq(contractId)).fetchOne(CONTRACT.ENTERPRISE_ACTIVITY)))
				.fetchOne(ENTERPRISE_ACTIVITY.ENTERPRISE);

		String enterpriseDocument = dslContext.select(REGISTRY.DOCUMENT).from(REGISTRY)
				.where(REGISTRY.ID.eq(enterpriseId)).fetchOne(REGISTRY.DOCUMENT);

		// --------------------------- INSERT CERTIFICA2_BATCH TABLE
		Certifica2BatchRecord cetifica2BatchRecord = dslContext.insertInto(CERTIFICA2_BATCH)
				.set(CERTIFICA2_BATCH.DOMAIN, domainId).set(CERTIFICA2_BATCH.ENTERPRISE, enterpriseId)
				.set(CERTIFICA2_BATCH.INCOME_FILE, (byte[]) null).set(CERTIFICA2_BATCH.OUTCOME_FILE, (byte[]) null)
				.returning(CERTIFICA2_BATCH.ID).fetchOne();

		Integer cetifica2BatchId = cetifica2BatchRecord.get(CERTIFICA2_BATCH.ID);

		// --------------------------- INSERT CERTIFICA2_BATCH_DETAIL TABLE
		dslContext.insertInto(CERTIFICA2_BATCH_DETAIL).set(CERTIFICA2_BATCH_DETAIL.DOMAIN, domainId)
				.set(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH, cetifica2BatchId)
				.set(CERTIFICA2_BATCH_DETAIL.CONTRACT, contractId)
				.set(CERTIFICA2_BATCH_DETAIL.SUSPENSION_CAUSE_CODE, suspensionReasonCode)
				.set(CERTIFICA2_BATCH_DETAIL.STATUS, (byte) 0).set(CERTIFICA2_BATCH_DETAIL.ERE_NUMBER, (String) null)
				.execute();

		// Generate SEPE_BATCH_ATTACH description
		java.util.Date date = new java.util.Date();
		String sepeBatchDescription = enterpriseDocument + simpleDateFormat.format(date);

		// --------------------------- INSERT SEPE_BATCH_ATTACH TABLE
		dslContext.insertInto(SEPE_BATCH_ATTACH).set(SEPE_BATCH_ATTACH.DOMAIN, domainId)
				.set(SEPE_BATCH_ATTACH.SOURCE_BATCH, cetifica2BatchId).set(SEPE_BATCH_ATTACH.SOURCE_TYPE, (byte) 0)
				.set(SEPE_BATCH_ATTACH.MIMETYPE, (byte) 5).set(SEPE_BATCH_ATTACH.DESCRIPTION, sepeBatchDescription)
				.set(SEPE_BATCH_ATTACH.DATA, out.toByteArray()).set(SEPE_BATCH_ATTACH.TYPE, (byte) 0)
				.set(SEPE_BATCH_ATTACH.ATTACH_DATE, new Date(date.getTime())).execute();
	}

	private static String getsuspensionReasonCodeDB(DSLContext dslContext, Integer contractId) {
		// Settlement Record
		Result<Record> settlementRecords = dslContext.select().from(SALARY).where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.TYPE.eq(SalaryType.SETTLE.value())).orderBy(SALARY.ID.desc()).fetch();

		Integer settlementId = settlementRecords.get(0).get(SALARY.ID);

		// Find Suspension Code in settlement salary_data
		Result<Record> suspensionCodeRecords = dslContext.select().from(SALARY_DATA)
				.where(SALARY_DATA.SALARY.eq(settlementId))
				.and(SALARY_DATA.NAME.eq("FIN").or(SALARY_DATA.NAME.eq("CAUSA_INDEMNIZACION"))).fetch();

		String suspensionReasonCode = "00";

		if (suspensionCodeRecords.isNotEmpty())
			suspensionReasonCode = getSuspensionReasonCode(suspensionCodeRecords.get(0).get(SALARY_DATA.EXPRESSION));

		return suspensionReasonCode;
	}

	private static void getCertificateSepe(com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info,
			ByteArrayOutputStream out) {

		// CertificadoEmpresa

		CertificadoEmpresa certificadoEmpresa = new CertificadoEmpresa();

		CUENTACOTIZACIONTYPE cuentaCotizacion = new CUENTACOTIZACIONTYPE();

		REPRESENTANTETYPE representanteType = new REPRESENTANTETYPE();
		representanteType.setCIFNIF(certifica2Info.getRepresentativeDocument());
		representanteType.setNombre(formatNameLenght(removeAccents(certifica2Info.getRepresentativeName())));
		representanteType.setApellido1(removeAccents(certifica2Info.getRepresentativeSurname()));
		if(AonStringUtils.isNotBlank(certifica2Info.getRepresentativeWork()))
			representanteType.setCargo(certifica2Info.getRepresentativeWork());

		EMPRESATYPE empresaType = new EMPRESATYPE();
		empresaType.setCIFNIF(certifica2Info.getEnterpriseDocument());
		empresaType.setCCC(certifica2Info.getCompleteCCC());

		TRABAJADORTYPE trabajadorType = new TRABAJADORTYPE();
		trabajadorType.setDNINIE(certifica2Info.getDocument());
		trabajadorType.setNombre(formatNameLenght(removeAccents(certifica2Info.getName())));
		trabajadorType.setApellido1(removeAccents(certifica2Info.getSurname()));
		trabajadorType.setNumSS(certifica2Info.getSSNumber());
		if(AonStringUtils.isNotBlank(certifica2Info.getRegime()) && !AonStringUtils.equalsIgnoreCase(certifica2Info.getRegime(), "0163"))
			trabajadorType.setGrupoCotizacion(certifica2Info.getQuoteGroup());
		trabajadorType.setTipoContrato(certifica2Info.getContractType());
		trabajadorType.setDuracionContrato(StringUtils.leftPad(certifica2Info.getContractDuration().toString(), 5, '0'));

		if (!StringUtils.isBlank(certifica2Info.getProfesionalCategory()))
			trabajadorType.setCodProfesion(StringUtils.rightPad(certifica2Info.getProfesionalCategory(), 7, '0'));

		trabajadorType.setFechaAltaEmpresa(fullDateFormat.format(certifica2Info.getStartDate()));
		trabajadorType.setCodCausaSuspension(certifica2Info.getSuspensionCode());
		trabajadorType.setFechaSuspensionExtincion(fullDateFormat.format(certifica2Info.getEndDate()));
		
		if(AonStringUtils.isNotBlank(certifica2Info.getErteCode())) {
			trabajadorType.setERE(StringUtils.leftPad(certifica2Info.getErteCode(), 9, '0'));
			if(!AonStringUtils.equalsIgnoreCase(certifica2Info.getErteCoef(), "100")) trabajadorType.setPorcentualReduccionERE(StringUtils.rightPad(certifica2Info.getErteCoef(), 4, '0'));
			trabajadorType.setFechaFinSuspension(fullDateFormat.format(certifica2Info.getErteEnd()));
		}
		
		trabajadorType.setDiasSalarioTramitacion("00000");

		if (AonStringUtils.equalsIgnoreCase(certifica2Info.getRegime(), "0163")) {
			if(AonStringUtils.isNotBlank(certifica2Info.getMdCtz()) && AonStringUtils.equalsIgnoreCase(certifica2Info.getMdCtz(), "2"))
				getDatosCotizacionREA(trabajadorType, certifica2Info);
			else
				getDatosCotizacion(trabajadorType, certifica2Info);
		} else
			getDatosCotizacion(trabajadorType, certifica2Info);

		if (AonStringUtils.equalsIgnoreCase(certifica2Info.getRegime(), "0163")) {
			DatosVacacionesCotizadasREA datosVacacionesCotizadasREA = new DatosVacacionesCotizadasREA();
			datosVacacionesCotizadasREA.setNumDiasCotizados(StringUtils.leftPad(certifica2Info.getSettleQuoteDays().toString(), 2, '0'));
			// Revisar num jornadas cotizadas
			datosVacacionesCotizadasREA.setNumJornadasCotizadas(StringUtils.leftPad("", 2, '0'));
			datosVacacionesCotizadasREA.setBaseCotizacionDesempleo(StringUtils.leftPad(format(certifica2Info.getBaseUnemployment()), 9, '0'));
			trabajadorType.setDatosVacacionesCotizadasREA(datosVacacionesCotizadasREA);
		} else {
			DatosVacacionesCotizadas datosVacacionesCotizadas = new DatosVacacionesCotizadas();
			datosVacacionesCotizadas.setNumDiasCotizados(StringUtils.leftPad(certifica2Info.getSettleQuoteDays().toString(), 3, '0'));
			datosVacacionesCotizadas.setBaseCotizacionContingenciasComunes(StringUtils.leftPad(format(certifica2Info.getBaseCgc()), 9, '0'));
			datosVacacionesCotizadas.setBaseCotizacionDesempleo(StringUtils.leftPad(format(certifica2Info.getBaseUnemployment()), 9, '0'));
			trabajadorType.setDatosVacacionesCotizadas(datosVacacionesCotizadas);
		}
		
		cuentaCotizacion.setDatosRepresentante(representanteType);
		cuentaCotizacion.setDatosEmpresa(empresaType);
		cuentaCotizacion.getDatosTrabajador().add(trabajadorType);

		certificadoEmpresa.getCuentaCotizacion().add(cuentaCotizacion);

		try {
			Utils.marshal(certificadoEmpresa, out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	private static void getDatosCotizacionREA(TRABAJADORTYPE trabajadorType,
			com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info) {
		for (Map<String, String> certifica2Map : certifica2Info.getQuoteDataList()) {
			COTIZACIONREATYPE cotizacionType = new COTIZACIONREATYPE();

			cotizacionType.setAno(certifica2Map.get("anioCtz"));
			cotizacionType.setMes(certifica2Map.get("monthCtz"));

			if (AonStringUtils.isBlank(certifica2Map.get("daysCtz")))
				cotizacionType.setNumJornadasCotizadas("00");
			else
				cotizacionType.setNumJornadasCotizadas(StringUtils.leftPad(certifica2Map.get("daysCtz"), 2, '0'));

			if (AonStringUtils.isBlank(certifica2Map.get("bcd")))
				cotizacionType.setBaseCotizacionDesempleo("000000000");
			else{
				Double bcd = Double.parseDouble(certifica2Map.get("bcd"));
				cotizacionType.setBaseCotizacionDesempleo(format(bcd));
			}

			trabajadorType.getDatosCotizacionREA().add(cotizacionType);
		}
	}

	private static void getDatosCotizacion(TRABAJADORTYPE trabajadorType,
			com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info) {
		for (Map<String, String> certifica2Map : certifica2Info.getQuoteDataList()) {
			COTIZACIONTYPE cotizacionType = new COTIZACIONTYPE();

			cotizacionType.setAno(certifica2Map.get("anioCtz"));
			cotizacionType.setMes(certifica2Map.get("monthCtz"));

			if (AonStringUtils.isBlank(certifica2Map.get("daysCtz")))
				cotizacionType.setNumDiasCotizados("000");
			else
				cotizacionType.setNumDiasCotizados(StringUtils.leftPad(certifica2Map.get("daysCtz"), 3, '0'));

			if (AonStringUtils.isBlank(certifica2Map.get("bccc")))
				cotizacionType.setBaseCotizacionContingenciasComunes("000000000");
			else {
				Double bccc = Double.parseDouble(certifica2Map.get("bccc"));
				cotizacionType.setBaseCotizacionContingenciasComunes(format(bccc));
			}

			if (AonStringUtils.isBlank(certifica2Map.get("bcd")))
				cotizacionType.setBaseCotizacionDesempleo("000000000");
			else {
				Double bcd = Double.parseDouble(certifica2Map.get("bcd"));
				cotizacionType.setBaseCotizacionDesempleo(format(bcd));
			}

			trabajadorType.getDatosCotizacion().add(cotizacionType);
		}
	}

	// ----------------------------------------------------- createCertificates
	// (Comunic@)

	public static Certificates createCertificates(Connection connection, String domainName, String user, Integer contractId, String suspensionCode, String ereCode, java.util.Date ereEnd) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = getCertifica2Info(dslContext, domainName, user, contractId,
				suspensionCode, ereCode, ereEnd);
		return createCertificates(certifica2Info);
	}

	private static Certificates createCertificates(com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info)
			throws IllegalArgumentException {
		CertificatesBuilder bd = new CertificatesBuilder();
		bd.setIpfManager(certifica2Info.getRepresentativeDocument())
			.setName(certifica2Info.getRepresentativeName())
			.setSurname(certifica2Info.getRepresentativeSurname())
			.setLastSurname("")
			.setCargo(certifica2Info.getRepresentativeWork())
			.setIpf(certifica2Info.getDocument())
			.setEmployeeName(certifica2Info.getName())
			.setEmployeeSurname(certifica2Info.getSurname())
			.setEmployeeSecondSurname(certifica2Info.getSecondSurname())
			.setNaf(certifica2Info.getSSNumber())
			.setRegimen(certifica2Info.getRegime())
			.setCtaCti(certifica2Info.getCcc())
			.setTypeContract(certifica2Info.getContractType())
			.setGz(certifica2Info.getQuoteGroup())
			.setDurationContract(certifica2Info.getContractDuration())
			.setTypeDuration(TypeDuration.DIAS)
			.setCatProfessional(certifica2Info.getProfesionalCategory())
			.setCauseSuspension(certifica2Info.getSuspensionCode())
			.setfAEd(certifica2Info.getStartDate())
			.setfSTd(certifica2Info.getEndDate())
			.setDaysCtzVc(certifica2Info.getSettleQuoteDays())
			.setBcccVc(certifica2Info.getBaseCgc())
			.setBcdVc(certifica2Info.getBaseUnemployment())
			.setEreCode(AonStringUtils.leftPad(certifica2Info.getErteCode(), 9, '0'))
			.setEreCoef(certifica2Info.getErteCoef())
			.setEreEnd(certifica2Info.getErteEnd());

		List<QuoteData> quoteDatas = new ArrayList<>();

		for (Map<String, String> certifica2Map : certifica2Info.getQuoteDataList()) {
			QuoteData quoteData = new QuoteData();

			quoteData.setAnio(Integer.parseInt(certifica2Map.get("anioCtz")));
			quoteData.setMonth(Integer.parseInt(certifica2Map.get("monthCtz")));

			if (AonStringUtils.isBlank(certifica2Map.get("daysCtz")))
				quoteData.setDays(0);
			else
				quoteData.setDays(Integer.parseInt(certifica2Map.get("daysCtz")));

			if (AonStringUtils.isBlank(certifica2Map.get("bccc")))
				quoteData.setBccc(0.00);
			else
				quoteData.setBccc(Double.parseDouble(certifica2Map.get("bccc")));

			if (AonStringUtils.isBlank(certifica2Map.get("bcd")))
				quoteData.setBcd(0.00);
			else
				quoteData.setBcd(Double.parseDouble(certifica2Map.get("bcd")));

			quoteDatas.add(quoteData);
		}

		bd.setQuoteData(quoteDatas);

		return bd.build();
	}

	// ----------------------------------------------------- Get days between
	// methods

	public static byte[] getCertitica2Data(String domainName, Integer contractId) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {

			DSLContext dslContext = DSL.using(connection, getDefaultSettings());

			List<Integer> cetifica2BatchRecords = dslContext.select(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH)
					.from(CERTIFICA2_BATCH_DETAIL).where(CERTIFICA2_BATCH_DETAIL.CONTRACT.eq(contractId))
					.orderBy(CERTIFICA2_BATCH_DETAIL.ID.desc()).fetch(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH);

			if (cetifica2BatchRecords.isEmpty())
				throw new IllegalArgumentException("No existe ningun archivo Certific@2 emitido previamente");

			Integer cetifica2BatchId = cetifica2BatchRecords.get(0);

			List<byte[]> sepeBatchAttachRecords = dslContext.select(SEPE_BATCH_ATTACH.DATA).from(SEPE_BATCH_ATTACH)
					.where(SEPE_BATCH_ATTACH.SOURCE_BATCH.eq(cetifica2BatchId)).orderBy(SEPE_BATCH_ATTACH.ID.desc())
					.fetch(SEPE_BATCH_ATTACH.DATA);

			if (sepeBatchAttachRecords.isEmpty())
				throw new IllegalArgumentException("No existe ningun archivo creado para Certific@2");

			return sepeBatchAttachRecords.get(0);

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// ----------------------------------------------------- Insert CNO

	public static void insertCNOToDB(Connection connection, Integer domainId, Integer contractId, String cno,
			java.util.Date startDate, java.util.Date endDate) {

		DSLContext dslContext = DSL.using(connection, getDefaultSettings());

		Result<Record> cnoRecords = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("CNO")).fetch();

		if (cnoRecords.isEmpty())
			dslContext.insertInto(CONTRACT_DATA).set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.CONTRACT, contractId).set(CONTRACT_DATA.NAME, "CNO")
					.set(CONTRACT_DATA.EXPRESSION, cno).set(CONTRACT_DATA.START_DATE, parseDateToSQL(startDate))
					.set(CONTRACT_DATA.END_DATE, null == endDate ? null : parseDateToSQL(endDate)).execute();
	}

	// ----------------------------------------------------- Get days between
	// methods

	private static int getAgrarianDays(DSLContext dslContext, Integer salaryId) {
		Result<Record> agrarianRecords = dslContext.select().from(SALARY_DATA)
				.where(SALARY_DATA.SALARY.eq(salaryId))
				.and(SALARY_DATA.NAME.eq("JORNADAS_REALES")
				)
				.fetch();

		int agrarian = 0;

		if (agrarianRecords.isEmpty())
			agrarianRecords = dslContext.select().from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(salaryId))
					.and(SALARY_DATA.NAME.eq("JORNADAS_REALES_TOTALES")
					)
					.fetch();
		
		if (agrarianRecords.isEmpty())
			return agrarian;

		for (Record agrarianRecord : agrarianRecords) {
			
			int itValue = 0;
			try {
				itValue = Integer.parseInt(agrarianRecord.get(SALARY_DATA.EXPRESSION));
			} catch (Exception e) {
				Double itValueDouble = Double.parseDouble(agrarianRecord.get(SALARY_DATA.EXPRESSION));
				itValue = itValueDouble.intValue();
			}
			
			agrarian += itValue;
		}
		return agrarian;
	}

	private static int getDaysBetween(Date startDate, Date endDate) {
		LocalDate startDateLocale = LocalDate.of(startDate.getYear() + 1900, startDate.getMonth() + 1, startDate.getDate());
        LocalDate endDateLocale = LocalDate.of(endDate.getYear() + 1900, endDate.getMonth() + 1, endDate.getDate());
        Long daysBetween = ChronoUnit.DAYS.between(startDateLocale, endDateLocale) + 1;
        return daysBetween.intValue();
	}

	// ----------------------------------------------------- Auxiliar methods

	private static String format(Double value) {
		return decimalFormat.format(value).replace(",", "").replace("\\.", "");
	}

	private static String getSuspensionReasonCode(String compensationReason) {
		switch (compensationReason) {
		case "UNFAIR":
			return "01";
		case "OBJECTIVE":
			return "02";
		case "WORK_END":
			return "11";
		case "TEMP_END":
			return "11";
		case "DEFINITE_END":
			return "11";
		case "CONDITIONS_CHANGE":
			return "21";
		default:
			return "00";
		}
	}

	private static Date parseDateToSQL(java.util.Date dateJava) {
		if (null == dateJava)
			return null;

		return new Date(dateJava.getTime());
	}

	private static String normalizeString(String value) {
		if( null != value && value.contains("\"") )
			value = value.split("\"")[1];
		
		return normalize(value);
	}
	
	private static String normalize(String value) {
		if(AonStringUtils.isBlank(value)) return value;
		
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                                       .replaceAll("\\p{M}", ""); 
        
        normalized = normalized.replaceAll("ñ", "n").replaceAll("Ñ", "N")
                               .replaceAll("[^a-zA-Z0-9 ]", "");
        
        return normalized;
	}

	private static String parseSSRegime(Byte ssRegime) {
		switch (ssRegime) {
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

	public static double round(Double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();
		if (value == null)
			return 0.00;

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
	
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
	
	private static String formatNameLenght(String input) {
		if(AonStringUtils.isBlank(input) || input.length() < 15) return input;
		else {
			input = input.replace(" DE LAS ", " ");
			input = input.replace(" DE LOS ", " ");
			input = input.replace(" DE ", " ");
			input = input.replace(" DEL ", " ");
			input = input.trim();
			
			return input.length() < 15 ? input : input.substring(0, 14);
		}
	}

}
