package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Certifica2Batch.CERTIFICA2_BATCH;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Certifica2Info.Certifica2Period;
import com.esferalia.aon.jooq.tables.records.Certifica2BatchRecord;
import com.esferalia.aon.payroll.tgss.cra.StringUtils;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.COTIZACIONREATYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.COTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CUENTACOTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CertificadoEmpresa;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.EMPRESATYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.REPRESENTANTETYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE.DatosVacacionesCotizadas;
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

	private static SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat monthDateFormat = new SimpleDateFormat("MM");

	private static DecimalFormat decimalFormat = new DecimalFormat("######0.00");
	
	// ----------------------------------------------------- createCertifica2DB@2Info
	
	public static void createCertifica2DBServlet(String domainName, Integer contractId) {
		try(Connection connection = AonServletUtils.getConnection(domainName)){
			Integer domainId = AonServletUtils.getDomainID(domainName);
			createCertifica2DB(connection, domainId, contractId, null);
		} catch (Exception e) {
			// Not use here
		}
	}
	
	public static void createCertifica2DB(Connection connection, Integer domainId, Integer contractId, String suspensionCode) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = getCertifica2Info(dslContext, contractId, suspensionCode);
		saveCertifica2Info(dslContext, domainId, contractId, certifica2Info.getSuspensionCode());
	}

	// ----------------------------------------------------- getCertific@2Info

	public static com.esferalia.aon.gwt.payroll.shared.Certifica2Info getCertifica2Info(Connection connection,
			Integer contractId, String suspensionReasonCode) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		return getCertifica2Info(dslContext, contractId, suspensionReasonCode);
	}
	
	private static com.esferalia.aon.gwt.payroll.shared.Certifica2Info getCertifica2Info(DSLContext dslContext,
			Integer contractId, String suspensionReasonCode) throws IllegalArgumentException {

		com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = new com.esferalia.aon.gwt.payroll.shared.Certifica2Info();

		// Contract/Employee Data

		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();

		Integer enterpriseCCCId = contractRecord.get(CONTRACT.ENTERPRISE_CCC);
		Integer enterpriseActivityId = contractRecord.get(CONTRACT.ENTERPRISE_ACTIVITY);

		getContractData(dslContext, certifica2Info, contractRecord, suspensionReasonCode);

		// Representative Data

		Integer domainId = contractRecord.get(CONTRACT.DOMAIN);
		getReprensentativeData(dslContext, certifica2Info, domainId, contractRecord);

		// Enterprise Data

		getEnterpriseData(dslContext, certifica2Info, enterpriseCCCId, enterpriseActivityId);

		// Certifica2 Periods

		Date seniorityDate = contractRecord.get(CONTRACT.SENIORITY_DATE);
		getCertifica2Periods(dslContext, certifica2Info, seniorityDate);

		// Certifica2 Holidays

		getCertifica2Holidays(dslContext, certifica2Info, contractId);

		
		return certifica2Info;
	}

	private static void getContractData(DSLContext dslContext, com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, 
			Record contractRecord, String suspensionReasonCode) throws IllegalArgumentException {

		Integer contractId = contractRecord.get(CONTRACT.ID);
		Integer personId = contractRecord.get(CONTRACT.PERSON);

		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);

		if (null == endDate) throw new IllegalArgumentException("No existe fecha fin para este contrato");

		Long contractDuration = getDaysBetween(startDate, endDate);

		Record personRecord = dslContext.select().from(PERSON).where(PERSON.REGISTRY.eq(personId)).fetchOne();

		String name = personRecord.get(PERSON.NAME);
		String surName = personRecord.get(PERSON.FIRST_SURNAME);
		String secondSurName = personRecord.get(PERSON.SECOND_SURNAME);
		String ssNum = personRecord.get(PERSON.SOCIAL_SECURITY_NUM);

		if (AonStringUtils.isBlank(ssNum)) throw new IllegalArgumentException("No existe numero de la Seguridad Social para esta persona");

		Record employeeRegistry = dslContext.select().from(REGISTRY).where(REGISTRY.ID.eq(personId)).fetchOne();

		String dni = employeeRegistry.get(REGISTRY.DOCUMENT);

		if (AonStringUtils.isBlank(dni)) throw new IllegalArgumentException("No existe documento de identidad para esta persona");

		List<String> contractTypeList = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("TC2"))
				.orderBy(CONTRACT_DATA.START_DATE.desc()).fetch(CONTRACT_DATA.EXPRESSION);

		String contractType = contractTypeList.isEmpty() ? null : contractTypeList.get(0);

		String tc2 = normalizeString(contractType);

		List<String> quoteGroupTypeList = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION"))
				.orderBy(CONTRACT_DATA.START_DATE.desc()).fetch(CONTRACT_DATA.EXPRESSION);

		if (quoteGroupTypeList.isEmpty()) throw new IllegalArgumentException("No existe grupo de cotizacion para este contrato");

		String quoteGroupType = quoteGroupTypeList.get(0);

		String quoteGroup = normalizeString(quoteGroupType);

		List<String> cnoTypeList = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("CNO"))
				.orderBy(CONTRACT_DATA.ID.desc())
				.fetch(CONTRACT_DATA.EXPRESSION);

		String cno = null;

		if (!cnoTypeList.isEmpty())
			cno = normalizeString(cnoTypeList.get(0));

		certifica2Info.setDocument(dni);
		certifica2Info.setSSNumber(ssNum);
		certifica2Info.setName(name);
		certifica2Info.setSurname(surName);
		certifica2Info.setSecondSurname(secondSurName);
		certifica2Info.setContractType(tc2);
		certifica2Info.setQuoteGroup(quoteGroup);
		certifica2Info.setContractDuration(contractDuration.intValue());
		certifica2Info.setProfesionalCategory(cno);
		certifica2Info.setSuspensionCode(suspensionReasonCode);
		certifica2Info.setStartDate(startDate);
		certifica2Info.setEndDate(endDate);
	}

	private static void getEnterpriseData(DSLContext dslContext, com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, 
			Integer enterpriseCCCId, Integer enterpriseActivityId) {

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

		String enterpriseCIF = enterpriseRegistryRecord.get(REGISTRY.DOCUMENT);

		certifica2Info.setRegime(regime);
		certifica2Info.setCcc(ccc);
		certifica2Info.setCompleteCCC(completeCCC);
		certifica2Info.setEnterpriseDocument(enterpriseCIF);

	}

	private static void getReprensentativeData(DSLContext dslContext, com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, 
			Integer domainId, Record contractRecord) {
		
		Integer contractId = contractRecord.get(CONTRACT.ID);
		String staffFullname = dslContext.select(CONTRACT_INFO.EXPRESSION)
				.from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("I_ENTERPRISE_DIR_STAFF_NAME")
					.or(CONTRACT_INFO.NAME.eq("T_ENTERPRISE_DIR_STAFF_NAME"))
					.or(CONTRACT_INFO.NAME.eq("L_ENTERPRISE_DIR_STAFF_NAME"))
					.or(CONTRACT_INFO.NAME.eq("P_ENTERPRISE_DIR_STAFF_NAME"))
				).fetchOne(CONTRACT_INFO.EXPRESSION);
		
		String representativeDocument = "";
		String representativeName = "";
		String representativeSurname = "";
		
		if(AonStringUtils.isNotBlank(staffFullname)) {
			if (staffFullname.contains(",")) {
				representativeName = staffFullname.split(",")[1].trim();
				representativeSurname = staffFullname.split(",")[0].trim();
			} else
				representativeName = staffFullname.trim();
		} else {
			Integer enterpriseRegisty = dslContext.select(ENTERPRISE.REGISTRY).from(ENTERPRISE)
					.where(ENTERPRISE.DOMAIN.eq(domainId)).fetchOne(ENTERPRISE.REGISTRY);

			Result<Record> staffRecords = dslContext.select().from(RDIR_STAFF)
					.where(RDIR_STAFF.REGISTRY.eq(enterpriseRegisty)).fetch();

			// Hay representante de empresa
			if (staffRecords.isNotEmpty()) {
				// Cogemos el primer representate
				Record staffRecord = staffRecords.get(0);
				representativeDocument = staffRecord.get(RDIR_STAFF.DOCUMENT);
				String fullName = staffRecord.get(RDIR_STAFF.NAME);

				if (fullName.contains(",")) {
					representativeName = fullName.split(",")[1].trim();
					representativeSurname = fullName.split(",")[0].trim();
				} else
					representativeName = fullName.trim();

				// No hay representante
			} else {
				Record enterpriseRegistry = dslContext.select().from(REGISTRY)
						.where(REGISTRY.ID.eq(enterpriseRegisty))
						.fetchOne();
				
				representativeDocument = enterpriseRegistry.get(REGISTRY.DOCUMENT);
				representativeName = enterpriseRegistry.get(REGISTRY.NAME);
			}
		}
		
		String staffDocument = dslContext.select(CONTRACT_INFO.EXPRESSION)
				.from(CONTRACT_INFO)
				.where(CONTRACT_INFO.CONTRACT.eq(contractId))
				.and(CONTRACT_INFO.NAME.eq("I_ENTERPRISE_DIR_STAFF_NIF")
					.or(CONTRACT_INFO.NAME.eq("T_ENTERPRISE_DIR_STAFF_NIF"))
					.or(CONTRACT_INFO.NAME.eq("L_ENTERPRISE_DIR_STAFF_NIF"))
					.or(CONTRACT_INFO.NAME.eq("P_ENTERPRISE_DIR_STAFF_NIF"))
				).fetchOne(CONTRACT_INFO.EXPRESSION);
		
		if(AonStringUtils.isNotBlank(staffDocument))
			representativeDocument = staffDocument;

		certifica2Info.setRepresentativeDocument(representativeDocument);
		certifica2Info.setRepresentativeName(representativeName);
		certifica2Info.setRepresentativeSurname(representativeSurname);

	}

	private static void getCertifica2Periods(DSLContext dslContext, com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, 
			Date seniorityDate) {

		List<Certifica2Period> certifica2List = new ArrayList<>();
		Integer maxDays = 0;

		java.util.Date filterDateJava = DateUtils.copyDateOnly(certifica2Info.getEndDate());
		filterDateJava = DateUtils.addDays2Date(filterDateJava, -180);
		Date filterDate = parseDateToSQL(filterDateJava);

		if (null != seniorityDate)
			filterDate = filterDate.before(seniorityDate) ? seniorityDate : filterDate;

		Result<Record> salariesRecords = dslContext.select().from(SALARY).where(SALARY.SOCIAL_SECURITY_NUMBER.eq(certifica2Info.getSSNumber()))
				.and(SALARY.CCC.eq(certifica2Info.getCcc())).and(SALARY.TYPE.eq((byte) 0)).and(SALARY.END_DATE.ge(filterDate))
				.and(SALARY.END_DATE.le(parseDateToSQL(certifica2Info.getEndDate()))).orderBy(SALARY.END_DATE.desc()).fetch();

		// Using for agrarian only
		Integer contractDuration = 0;
		
		for (Record salary : salariesRecords) {
			if (maxDays > 180)
				break;

			Integer salaryId = salary.get(SALARY.ID);

			Date salaryStartDate = salary.get(SALARY.START_DATE);
			Date salaryEndDate = salary.get(SALARY.END_DATE);

			Long salaryDaysBetween = null;
			if (AonStringUtils.equalsIgnoreCase(certifica2Info.getRegime(), "0163")) {
				salaryDaysBetween = getAgrarianDays(dslContext, salaryId);
				contractDuration += salaryDaysBetween.intValue();
			} else
				salaryDaysBetween = getDaysBetween(salaryStartDate, salaryEndDate);

			List<String> baseCGCRecords = dslContext.select(SALARY_DATA.EXPRESSION).from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(salaryId)).and(SALARY_DATA.NAME.eq("BASE_CGC"))
					.fetch(SALARY_DATA.EXPRESSION);

			Double baseCGC = 0.00;

			// Using for, cause can be periods in the same Salary
			for (String baseCGCStr : baseCGCRecords) {
				baseCGC += Double.parseDouble(baseCGCStr);
			}

			List<String> baseCGPRecords = dslContext.select(SALARY_DATA.EXPRESSION).from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(salaryId)).and(SALARY_DATA.NAME.eq("BASE_CGP"))
					.fetch(SALARY_DATA.EXPRESSION);

			Double baseCGP = 0.00;

			// Using for, cause can be periods in the same Salary
			for (String baseCGPStr : baseCGPRecords) {
				baseCGP += Double.parseDouble(baseCGPStr);
			}

			// Initialize Certifica2Info
			Certifica2Period certifica2Period = null;

			// Ya has cumplido los 180 dias de registro
			if (maxDays + salaryDaysBetween > 180) {
				Long restDays = salaryDaysBetween - (maxDays + salaryDaysBetween - 180);

				certifica2Period = new Certifica2Period(yearDateFormat.format(salaryStartDate),
						monthDateFormat.format(salaryStartDate), restDays.intValue(),
						baseCGC / 30 * restDays.intValue(), baseCGP / 30 * restDays.intValue());

				maxDays += salaryDaysBetween.intValue();

			} else {
				certifica2Period = new Certifica2Period(yearDateFormat.format(salaryStartDate),
						monthDateFormat.format(salaryStartDate), salaryDaysBetween.intValue(), baseCGC, baseCGP);

				maxDays += salaryDaysBetween.intValue();
			}

			certifica2List.add(certifica2Period);

		}

		List<Map<String, String>> quoteDataList = new ArrayList<>();

		certifica2List.sort((o1, o2) -> o2.getMonth().compareTo(o1.getMonth()));

		for (Certifica2Period certifica2 : certifica2List) {
			Map<String, String> quoteData = new HashMap<>();

			quoteData.put("anioCtz", certifica2.getYear());
			quoteData.put("monthCtz", certifica2.getMonth());
			quoteData.put("daysCtz", certifica2.getQuotedDays() + "");
			quoteData.put("bccc", round(certifica2.getBase_cgc(), 2) + "");
			quoteData.put("bcd", round(certifica2.getBase_unemployment(), 2) + "");

			quoteDataList.add(quoteData);
		}
		
		// Contract duration for agrarian only
		if(contractDuration != 0)
			certifica2Info.setContractDuration(contractDuration);

		certifica2Info.setQuoteDataList(quoteDataList);
	}

	private static void getCertifica2Holidays(DSLContext dslContext, com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info, 
			Integer contractId) {
		
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
			
			for(String holidays : holidaysList)
				holidayDays += (int) (AonStringUtils.isBlank(holidays) ? 0 : Double.parseDouble(holidays));

			Record holidaysRecord = dslContext.select().from(SALARY_PAYMENT)
					.where(SALARY_PAYMENT.SALARY.eq(settlementId)).and(SALARY_PAYMENT.TYPE.eq((byte) 6)).fetchOne();

			Double baseCGC = 0.00;
			Double baseCGP = 0.00;

			if (null != holidaysRecord) {
				baseCGC = holidaysRecord.get(SALARY_PAYMENT.AMOUNT);
				baseCGP = holidaysRecord.get(SALARY_PAYMENT.QUOTE);
			}

			settlementCertifica2Info = new Certifica2Period(null, null, holidayDays, baseCGC, baseCGP);
		}

		certifica2Info.setSettleQuoteDays(settlementCertifica2Info.getQuotedDays());
		certifica2Info.setBaseCgc(round(settlementCertifica2Info.getBase_cgc(), 2));
		certifica2Info.setBaseUnemployment(round(settlementCertifica2Info.getBase_unemployment(), 2));

	}

	// ----------------------------------------------------- saveCertific@2Info to DB
	
	private static void saveCertifica2Info(DSLContext dslContext, Integer domainId, Integer contractId, String suspensionReasonCode) throws IllegalArgumentException {
		if(AonStringUtils.isBlank(suspensionReasonCode))
			suspensionReasonCode = getsuspensionReasonCodeDB(dslContext, contractId);
		
		com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = getCertifica2Info(dslContext, contractId, suspensionReasonCode);
		
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
		Result<Record> settlementRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.TYPE.eq((byte) 2))
				.orderBy(SALARY.ID.desc())
				.fetch();

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
		representanteType.setNombre(certifica2Info.getRepresentativeName());
		representanteType.setApellido1(certifica2Info.getRepresentativeSurname());

		EMPRESATYPE empresaType = new EMPRESATYPE();
		empresaType.setCIFNIF(certifica2Info.getEnterpriseDocument());
		empresaType.setCCC(certifica2Info.getCompleteCCC());

		TRABAJADORTYPE trabajadorType = new TRABAJADORTYPE();
		trabajadorType.setDNINIE(certifica2Info.getDocument());
		trabajadorType.setNombre(certifica2Info.getName());
		trabajadorType.setApellido1(certifica2Info.getSurname());
		trabajadorType.setNumSS(certifica2Info.getSSNumber());
		trabajadorType.setGrupoCotizacion(certifica2Info.getQuoteGroup());
		trabajadorType.setTipoContrato(certifica2Info.getContractType());
		trabajadorType
				.setDuracionContrato(StringUtils.leftPad(certifica2Info.getContractDuration().toString(), 5, '0'));

		if (!StringUtils.isBlank(certifica2Info.getProfesionalCategory()))
			trabajadorType.setCodProfesion(StringUtils.rightPad(certifica2Info.getProfesionalCategory(), 7, '0'));

		trabajadorType.setFechaAltaEmpresa(fullDateFormat.format(certifica2Info.getStartDate()));
		trabajadorType.setCodCausaSuspension(certifica2Info.getSuspensionCode());
		trabajadorType.setFechaSuspensionExtincion(fullDateFormat.format(certifica2Info.getEndDate()));
		trabajadorType.setDiasSalarioTramitacion("00000");
		
		if(AonStringUtils.equalsIgnoreCase(certifica2Info.getRegime(), "0163"))
			getDatosCotizacionREA(trabajadorType, certifica2Info);
		else
			getDatosCotizacion(trabajadorType, certifica2Info);

		DatosVacacionesCotizadas datosVacacionesCotizadas = new DatosVacacionesCotizadas();
		datosVacacionesCotizadas.setNumDiasCotizados(StringUtils.leftPad(certifica2Info.getSettleQuoteDays().toString(), 3, '0'));
		datosVacacionesCotizadas.setBaseCotizacionContingenciasComunes(StringUtils.leftPad(format(certifica2Info.getBaseCgc()), 9, '0'));
		datosVacacionesCotizadas.setBaseCotizacionDesempleo(StringUtils.leftPad(format(certifica2Info.getBaseUnemployment()), 9, '0'));
		trabajadorType.setDatosVacacionesCotizadas(datosVacacionesCotizadas);

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
			else
				cotizacionType.setBaseCotizacionDesempleo(StringUtils.leftPad(format(certifica2Map.get("bcd")), 9, '0'));

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
			else
				cotizacionType.setBaseCotizacionContingenciasComunes(
						StringUtils.leftPad(format(certifica2Map.get("bccc")), 9, '0'));

			if (AonStringUtils.isBlank(certifica2Map.get("bcd")))
				cotizacionType.setBaseCotizacionDesempleo("000000000");
			else
				cotizacionType
						.setBaseCotizacionDesempleo(StringUtils.leftPad(format(certifica2Map.get("bcd")), 9, '0'));

			trabajadorType.getDatosCotizacion().add(cotizacionType);
		}
	}

	// ----------------------------------------------------- createCertificates (Comunic@)
	
	public static Certificates createCertificates(Connection connection, Integer contractId, String suspensionCode) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info = getCertifica2Info(dslContext, contractId, suspensionCode);
		return createCertificates(certifica2Info);
	}
	
	private static Certificates createCertificates(com.esferalia.aon.gwt.payroll.shared.Certifica2Info certifica2Info) throws IllegalArgumentException {
		CertificatesBuilder bd = new CertificatesBuilder();
		bd.setIpfManager(certifica2Info.getRepresentativeDocument())
			.setName(certifica2Info.getRepresentativeName())
			.setSurname(certifica2Info.getRepresentativeSurname())
			.setLastSurname("")
			.setIpf(certifica2Info.getDocument())
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
			.setBcdVc(certifica2Info.getBaseUnemployment());

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
	
	// ----------------------------------------------------- Get days between methods
	
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
	
	public static void insertCNOToDB(Connection connection, Integer domainId, Integer contractId, String cno, java.util.Date startDate, java.util.Date endDate) {
		
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		Result<Record> cnoRecords = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("CNO")).fetch();
		
		if(cnoRecords.isEmpty())
			dslContext.insertInto(CONTRACT_DATA)
					.set(CONTRACT_DATA.DOMAIN, domainId)
					.set(CONTRACT_DATA.CONTRACT, contractId)
					.set(CONTRACT_DATA.NAME, "CNO")
					.set(CONTRACT_DATA.EXPRESSION, cno)
					.set(CONTRACT_DATA.START_DATE, parseDateToSQL(startDate))
					.set(CONTRACT_DATA.END_DATE, null == endDate ? null : parseDateToSQL(endDate))
					.execute();
	}

	
	// ----------------------------------------------------- Get days between methods
	
	private static Long getAgrarianDays(DSLContext dslContext, Integer salaryId) {
		Result<Record> agrarianRecords = dslContext.select().from(SALARY_DATA).where(SALARY_DATA.SALARY.eq(salaryId))
				.and(SALARY_DATA.NAME.eq("JORNADAS_REALES")).fetch();
		if (agrarianRecords.isEmpty())
			return null;
		Long agrarian = (long) 0;
		for (Record agrarianRecord : agrarianRecords) {
			Long itValue = Long.parseLong(agrarianRecord.get(SALARY_DATA.EXPRESSION));
			agrarian += itValue;
		}
		return agrarian;
	}
	
	private static Long getDaysBetween(Date startDate, Date endDate) {
		java.util.Date salaryStartDateJava = new java.util.Date(startDate.getTime());
		java.util.Date salaryEndDateJava = new java.util.Date(endDate.getTime());
		return Duration.between(salaryStartDateJava.toInstant(), salaryEndDateJava.toInstant()).toDays() + 1;
	}
	
	// ----------------------------------------------------- Auxiliar methods

	private static String format(Double value) {
		return decimalFormat.format(value).replace(",", "").replace("\\.", "");
	}

	private static String format(String value) {
		return value.replaceAll("[,.]", "");
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

}
