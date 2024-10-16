package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Certifica2Batch.CERTIFICA2_BATCH;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Cnae2009.CNAE2009;
import static com.esferalia.aon.jooq.tables.Cno.CNO;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.ContractInfo.CONTRACT_INFO;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.esferalia.aon.jooq.tables.records.SalaryDataRecord;
import com.esferalia.aon.jooq.tables.records.SalaryRecord;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddressFilter;
import com.esferalia.aon.occam.api.model.registry.RegistryAddress;
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
	
	private static SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat monthDateFormat = new SimpleDateFormat("MM");
	private static SimpleDateFormat monthStrDateFormat = new SimpleDateFormat("MMMM");
	private static SimpleDateFormat dayDateFormat = new SimpleDateFormat("dd");

	private static DecimalFormat decimalFormat = new DecimalFormat("0000000.00");

	// -----------------------------------------------------
	// createCertifica2DB@2Info

	public static void createCertifica2DBServlet(String domainName, String user, Integer contractId, String suspensionReason, String ereCode) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			createCertifica2DB(connection, domainName, user, domainId, contractId, suspensionReason, ereCode);
		} catch (Exception e) {
			// Not use here
		}
	}

	public static void createCertifica2DB(Connection connection, String domainName, String user, Integer domainId, Integer contractId, String suspensionCode, String ereCode) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = getCertifica2Info(dslContext, domainName, user, contractId, suspensionCode, ereCode);
		saveCertifica2Info(dslContext, domainName, user, domainId, contractId, certifica2Info.getSuspensionCode(), ereCode);
	}
	
	public static byte[] createCertEnterprisePDF(String domainName, String user, Integer contractId, String suspensionReasonCode, String suspensionReason, String ereCode) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			if (AonStringUtils.isBlank(suspensionReasonCode))
				suspensionReasonCode = getsuspensionReasonCodeDB(dslContext, contractId);

			com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = getCertifica2Info(dslContext, domainName, user, contractId, suspensionReasonCode, ereCode);
			
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
		fieldMap.put("014", certifica2Info.getName() + " " + certifica2Info.getSurname());
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
		fieldMap.put("015", certifica2Info.getName() + " " + certifica2Info.getSurname());
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
			String domainName, String user, Integer contractId, String suspensionReasonCode, String ereCode) throws IllegalArgumentException {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		return getCertifica2Info(dslContext, domainName, user, contractId, suspensionReasonCode, ereCode);
	}

	private static com.esferalia.aon.gwt.payroll.shared.Certifica2Info getCertifica2Info(DSLContext dslContext,
			String domainName, String user, Integer contractId, String suspensionReasonCode, String ereCode) throws IllegalArgumentException {

		com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = new com.esferalia.aon.gwt.payroll.shared.Certifica2Info();

		// Contract/Employee Data

		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();

		Integer enterpriseCCCId = contractRecord.get(CONTRACT.ENTERPRISE_CCC);
		Integer enterpriseActivityId = contractRecord.get(CONTRACT.ENTERPRISE_ACTIVITY);
		
		getContractData(dslContext, certifica2Info, contractRecord, suspensionReasonCode, ereCode);

		// Representative Data

		Integer domainId = contractRecord.get(CONTRACT.DOMAIN);
		getReprensentativeData(dslContext, certifica2Info, domainId, contractRecord);

		// Enterprise Data

		Integer registryId = getEnterpriseData(dslContext, certifica2Info, enterpriseCCCId, enterpriseActivityId, contractId);
		RegistryAddress address = AON.get(domainName, domainId, user, ((RegistryAddressFilter) f -> f.getRegistryProperty().eq(registryId)));
		certifica2Info.setAddress(address.getAddress());
		certifica2Info.setCity(address.getCity());
		certifica2Info.setZip(address.getZip());
		certifica2Info.setGeozone(address.getGeozoneName());

		// Certifica2 Periods

		Date seniorityDate = contractRecord.get(CONTRACT.SENIORITY_DATE);
		getCertifica2Periods(dslContext, certifica2Info, seniorityDate, contractId);

		// Certifica2 Holidays

		getCertifica2Holidays(dslContext, certifica2Info, contractId);

		return certifica2Info;
	}

	private static void getContractData(DSLContext dslContext,
			com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, Record contractRecord,
			String suspensionReasonCode, String ereCode) throws IllegalArgumentException {

		Integer contractId = contractRecord.get(CONTRACT.ID);
		Integer personId = contractRecord.get(CONTRACT.PERSON);

		Date startDate = null != contractRecord.get(CONTRACT.SENIORITY_DATE) ? contractRecord.get(CONTRACT.SENIORITY_DATE) : contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);

		Result<Record> ereRecords = null;
		
		if (null == endDate) {
			Date currentDate = new Date(new java.util.Date().getTime());
			Date previusCurrentDate = new Date(DateUtils.addDays2Date(currentDate, -15).getTime());
			
			ereRecords = dslContext.select().from(CONTRACT_DATA)
					.where(CONTRACT_DATA.CONTRACT.eq(contractId))
					.and(CONTRACT_DATA.NAME.eq("COEFICIENTE_ERE"))
					.and(CONTRACT_DATA.START_DATE.ge(previusCurrentDate))
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
		certifica2Info.setName(name);
		certifica2Info.setSurname(surName);
		certifica2Info.setSecondSurname(secondSurName);
		certifica2Info.setContractType(tc2);
		certifica2Info.setQuoteGroup(quoteGroup);
		certifica2Info.setContractDuration(contractDuration);
		certifica2Info.setProfesionalCategory(cnoCode);
		certifica2Info.setCnoCode(cnoCode);
		certifica2Info.setCno(cno);
		certifica2Info.setSuspensionCode(suspensionReasonCode);
		
		if(null != ereRecords) {
			// Get last ere date for ereEndDate
			Date ereStartDate = ereRecords.get(0).get(CONTRACT_DATA.START_DATE);
			Date ereEndDate = ereRecords.get(ereRecords.size() - 1).get(CONTRACT_DATA.END_DATE);
			String ereCoef = ereRecords.get(0).get(CONTRACT_DATA.EXPRESSION);
			Double parseEreCoef = Double.parseDouble(ereCoef) * 100;
			
			certifica2Info.setStartDate(startDate);
			certifica2Info.setEndDate(ereStartDate);
			certifica2Info.setErteCoef(parseEreCoef.intValue() + "");
			certifica2Info.setErteEnd(ereEndDate);
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
		certifica2Info.setEnterpriseName(enterpriseName);
		
		certifica2Info.setCnae(cnae);
		certifica2Info.setCnaeCode(cnaeCode);
		
		return registryId;

	}

	private static void getReprensentativeData(DSLContext dslContext,
			com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, Integer domainId,
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

		String staffDocument = dslContext.select(CONTRACT_INFO.EXPRESSION).from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("I_ENTERPRISE_DIR_STAFF_NIF")
						.or(CONTRACT_INFO.NAME.eq("T_ENTERPRISE_DIR_STAFF_NIF"))
						.or(CONTRACT_INFO.NAME.eq("L_ENTERPRISE_DIR_STAFF_NIF"))
						.or(CONTRACT_INFO.NAME.eq("P_ENTERPRISE_DIR_STAFF_NIF")))
				.fetchOne(CONTRACT_INFO.EXPRESSION);

		if (AonStringUtils.isNotBlank(staffDocument))
			representativeDocument = staffDocument;
		
		String staffCharge = dslContext.select(CONTRACT_INFO.EXPRESSION).from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("I_ENTERPRISE_DIR_STAFF_CHARGE")
						.or(CONTRACT_INFO.NAME.eq("T_ENTERPRISE_DIR_STAFF_CHARGE"))
						.or(CONTRACT_INFO.NAME.eq("L_ENTERPRISE_DIR_STAFF_CHARGE"))
						.or(CONTRACT_INFO.NAME.eq("P_ENTERPRISE_DIR_STAFF_CHARGE")))
				.fetchOne(CONTRACT_INFO.EXPRESSION);

		if (AonStringUtils.isNotBlank(staffCharge))
			representativeCharge = staffCharge;

		certifica2Info.setRepresentativeDocument(representativeDocument);
		certifica2Info.setRepresentativeName(representativeName);
		certifica2Info.setRepresentativeSurname(representativeSurname);
		certifica2Info.setRepresentativeWork(representativeCharge);

	}

	private static void getCertifica2Periods(DSLContext dslContext,
			com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, Date seniorityDate, Integer contractId) {

		List<Certifica2Period> certifica2List = new ArrayList<>();
		Integer maxDays = 0;

		java.util.Date filterDateJava = DateUtils.copyDateOnly(certifica2Info.getEndDate());
		filterDateJava = DateUtils.addDays2Date(filterDateJava, -180);
		Date filterDate = parseDateToSQL(filterDateJava);

		if (null != seniorityDate)
			filterDate = filterDate.before(seniorityDate) ? seniorityDate : filterDate;

		// TODO: ¿que casos hay que hacerlo por ssNumber en vez de contractId?
		
		Result<Record> salariesRecords = dslContext.select().from(SALARY)
				.where(SALARY.SOCIAL_SECURITY_NUMBER.eq(certifica2Info.getSSNumber()))
				.and(SALARY.CCC.eq(certifica2Info.getCcc()))
				.and(SALARY.TYPE.eq((byte) 0))
				.and(SALARY.END_DATE.ge(filterDate))
				.and(SALARY.END_DATE.le(parseDateToSQL(certifica2Info.getEndDate())))
				.and(SALARY.CONTRACT.eq(contractId))
				.orderBy(SALARY.END_DATE.desc())
				.fetch();

		// Using for agrarian only
		Integer contractDuration = 0;

		for (Record salary : salariesRecords) {
			if (maxDays > 180)
				break;

			Integer salaryId = salary.get(SALARY.ID);

			Date salaryStartDate = salary.get(SALARY.START_DATE);
			Date salaryEndDate = salary.get(SALARY.END_DATE);

			int salaryDaysBetween = 0;
			if (AonStringUtils.equalsIgnoreCase(certifica2Info.getRegime(), "0163")) {
				if(AonStringUtils.isNotBlank(certifica2Info.getMdCtz()) && AonStringUtils.equalsIgnoreCase(certifica2Info.getMdCtz(), "2")) {
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
			
			baseCGC += checkCGCDelaySalary(dslContext, salaryStartDate, contractId);
			baseCGP += checkCGPDelaySalary(dslContext, salaryStartDate, contractId);

			// Initialize Certifica2Info
			Certifica2Period certifica2Period = null;

			// Ya has cumplido los 180 dias de registro
			if (maxDays + salaryDaysBetween > 180) {
				int restDays = salaryDaysBetween - (maxDays + salaryDaysBetween - 180);
				if(0 == restDays) continue;

				certifica2Period = new Certifica2Period(yearDateFormat.format(salaryStartDate),
						monthDateFormat.format(salaryStartDate), restDays,
						baseCGC / 30 * restDays, baseCGP / 30 * restDays);
				
				certifica2Period.setDate(salaryStartDate);

				maxDays += salaryDaysBetween;

			} else {
				certifica2Period = new Certifica2Period(yearDateFormat.format(salaryStartDate),
						monthDateFormat.format(salaryStartDate), salaryDaysBetween, baseCGC, baseCGP);
				
				certifica2Period.setDate(salaryStartDate);

				maxDays += salaryDaysBetween;
			}

			certifica2List.add(certifica2Period);

		}

		List<Map<String, String>> quoteDataList = new ArrayList<>();

		certifica2List.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));

		for (Certifica2Period certifica2 : certifica2List) {
			Map<String, String> quoteData = new HashMap<>();

			quoteData.put("anioCtz", certifica2.getYear());
			quoteData.put("monthCtz", certifica2.getMonth());
			quoteData.put("daysCtz", certifica2.getQuotedDays() + "");
			quoteData.put("bccc", round(certifica2.getBase_cgc(), 2) + "");
			quoteData.put("bcd", round(certifica2.getBase_unemployment(), 2) + "");
			
			if(!checkQuoteData(quoteDataList, quoteData)) quoteDataList.add(quoteData);
			
		}

		// Contract duration for agrarian only
		if (contractDuration != 0)
			certifica2Info.setContractDuration(contractDuration);

		certifica2Info.setQuoteDataList(quoteDataList);
	}

	private static Double checkCGCDelaySalary(DSLContext dslContext, Date salaryStartDate, Integer contractId) {
		Result<SalaryRecord> delaySalaries = dslContext.selectFrom(SALARY)
			.where(SALARY.TYPE.eq((byte)3))
			.and(SALARY.CONTRACT.eq(contractId))
			.and(SALARY.START_DATE.le(salaryStartDate))
			.and(SALARY.END_DATE.ge(salaryStartDate))
			.fetch();
		
		if(delaySalaries.isEmpty()) return 0.00;
		
		Result<SalaryDataRecord> delaySalaryDatas = dslContext.selectFrom(SALARY_DATA)
			.where(SALARY_DATA.SALARY.in(delaySalaries.stream().map(delaySalary -> delaySalary.getId()).collect(Collectors.toList())))
			.and(SALARY_DATA.NAME.eq("BASE_CGC"))
			.and(SALARY_DATA.START_DATE.le(salaryStartDate))
			.and(SALARY_DATA.END_DATE.ge(salaryStartDate))
			.fetch();
		
		if(delaySalaryDatas.isEmpty()) return 0.00;
		
		double cgcDelaySum = delaySalaryDatas.stream().mapToDouble(delaySalaryData -> Double.parseDouble(delaySalaryData.getExpression())).sum();
		
		return cgcDelaySum;
	}

	private static Double checkCGPDelaySalary(DSLContext dslContext, Date salaryStartDate, Integer contractId) {
		Result<SalaryRecord> delaySalaries = dslContext.selectFrom(SALARY)
			.where(SALARY.TYPE.eq((byte)3))
			.and(SALARY.CONTRACT.eq(contractId))
			.and(SALARY.START_DATE.le(salaryStartDate))
			.and(SALARY.END_DATE.ge(salaryStartDate))
			.fetch();
		
		if(delaySalaries.isEmpty()) return 0.00;
		
		Result<SalaryDataRecord> delaySalaryDatas = dslContext.selectFrom(SALARY_DATA)
			.where(SALARY_DATA.SALARY.in(delaySalaries.stream().map(delaySalary -> delaySalary.getId()).collect(Collectors.toList())))
			.and(SALARY_DATA.NAME.eq("BASE_CGP"))
			.and(SALARY_DATA.START_DATE.le(salaryStartDate))
			.and(SALARY_DATA.END_DATE.ge(salaryStartDate))
			.fetch();
		
		if(delaySalaryDatas.isEmpty()) return 0.00;
		
		double cgcDelaySum = delaySalaryDatas.stream().mapToDouble(delaySalaryData -> Double.parseDouble(delaySalaryData.getExpression())).sum();
		
		return cgcDelaySum;
		
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

		// Check Settle for unEnjoy Holidays
		Result<Record> settlementRecords = dslContext.select().from(SALARY).where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.TYPE.eq((byte) 2)).orderBy(SALARY.END_DATE.desc()).fetch();

		Certifica2Period settlementCertifica2Info = null;

		if (settlementRecords.isEmpty())
			settlementCertifica2Info = new Certifica2Period(null, null, 0, 0.00, 0.00);
		else {
			Integer settlementId = settlementRecords.get(0).get(SALARY.ID);

			List<String> holidaysList = dslContext.select(SALARY_DATA.EXPRESSION).from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(settlementId))
					.and(SALARY_DATA.NAME.eq("DIAS_VACACIONES_NO_DISFRUTADOS")).fetch(SALARY_DATA.EXPRESSION);

			Integer holidayDays = 0;

			for (String holidays : holidaysList)
				holidayDays += (int) (AonStringUtils.isBlank(holidays) ? 0 : Double.parseDouble(holidays));

			Record holidaysRecord = dslContext.select().from(SALARY_PAYMENT)
					.where(SALARY_PAYMENT.SALARY.eq(settlementId)).and(SALARY_PAYMENT.TYPE.eq((byte) 6)).fetchOne();

			Double baseCGC = 0.00;
			Double baseCGP = 0.00;

			if (null != holidaysRecord) {
				baseCGC = settlementRecords.get(0).get(SALARY.CGC_BASE);
				baseCGP = settlementRecords.get(0).get(SALARY.CGP_BASE);
			}
			
			if(holidayDays == 0) {
				Date settleEndDate = settlementRecords.get(0).get(SALARY.END_DATE);
				Date contractEndDate = dslContext.select(CONTRACT.END_DATE).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne(CONTRACT.END_DATE);
				
				if(null != contractEndDate)
					holidayDays = DateUtils.getDaysBetween(contractEndDate, settleEndDate);
				
			}

			settlementCertifica2Info = new Certifica2Period(null, null, holidayDays, baseCGC, baseCGP);
		}

		certifica2Info.setSettleQuoteDays(settlementCertifica2Info.getQuotedDays());
		certifica2Info.setBaseCgc(round(settlementCertifica2Info.getBase_cgc(), 2));
		certifica2Info.setBaseUnemployment(round(settlementCertifica2Info.getBase_unemployment(), 2));

	}

	// ----------------------------------------------------- saveCertific@2Info to
	// DB

	private static void saveCertifica2Info(DSLContext dslContext, String domainName, String user, Integer domainId, Integer contractId,
			String suspensionReasonCode, String ereCode) throws IllegalArgumentException {
		if (AonStringUtils.isBlank(suspensionReasonCode))
			suspensionReasonCode = getsuspensionReasonCodeDB(dslContext, contractId);

		com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = getCertifica2Info(dslContext, domainName, user, contractId,
				suspensionReasonCode, ereCode);

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
				.and(SALARY.TYPE.eq((byte) 2)).orderBy(SALARY.ID.desc()).fetch();

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

	public static Certificates createCertificates(Connection connection, String domainName, String user, Integer contractId, String suspensionCode, String ereCode) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = getCertifica2Info(dslContext, domainName, user, contractId,
				suspensionCode, ereCode);
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
		Result<Record> agrarianRecords = dslContext.select().from(SALARY_DATA).where(SALARY_DATA.SALARY.eq(salaryId))
				.and(SALARY_DATA.NAME.eq("JORNADAS_REALES")).fetch();

		int agrarian = 0;

		if (agrarianRecords.isEmpty())
			return agrarian;

		for (Record agrarianRecord : agrarianRecords) {
			int itValue = Integer.parseInt(agrarianRecord.get(SALARY_DATA.EXPRESSION));
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
		return null != value && value.contains("\"") ? value.split("\"")[1] : value;
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
