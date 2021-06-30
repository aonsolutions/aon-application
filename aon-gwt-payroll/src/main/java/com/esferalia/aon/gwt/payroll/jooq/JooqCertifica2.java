package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Certifica2Batch.CERTIFICA2_BATCH;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.jooq.tables.records.Certifica2BatchRecord;
import com.esferalia.aon.payroll.sepe.certifica2.Certifica2;
import com.esferalia.aon.payroll.sepe.certifica2.Certifica2Info;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.COTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CUENTACOTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CertificadoEmpresa;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.EMPRESATYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE.DatosVacacionesCotizadas;
import com.esferalia.aon.watson.util.AonStringUtils;

import aon.sepe.objects.Certificates;
import aon.sepe.objects.Certificates.CertificatesBuilder;
import aon.sepe.objects.Certificates.TypeDuration;
import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class JooqCertifica2 {

	private static Settings SETTINGS = null;
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
	
	private static SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat monthDateFormat = new SimpleDateFormat("MM");
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static String generateCertifica2(Connection conn, Integer domainId, SalaryDraft salaryDraft) {
		return generateCertifica2DB(DSL.using(conn, getDefaultSettings()), domainId, salaryDraft);
	}

	private static String generateCertifica2DB(DSLContext dslContext, Integer domainId, SalaryDraft salaryDraft) {
		// ByteArrayOutputStream
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		// Contract ID
		Integer contractId = salaryDraft.getEmployee().getId();
		
		// Settlement Record
		Result<Record> settlementRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.TYPE.eq((byte)2))
				.orderBy(SALARY.ID.desc())
				.fetch();
		
		Integer settlementId = settlementRecords.get(0).get(SALARY.ID);
		
		// Find Suspension Code in settlement salary_data
		Result<Record> suspensionCodeRecords = dslContext.select().from(SALARY_DATA)
				.where(SALARY_DATA.SALARY.eq(settlementId))
				.and(SALARY_DATA.NAME.eq("FIN").or(SALARY_DATA.NAME.eq("CAUSA_INDEMNIZACION")))
				.fetch();
		
		String suspensionReasonCode = "00";
		
		if(suspensionCodeRecords.isNotEmpty())
			suspensionReasonCode = getSuspensionReasonCode(suspensionCodeRecords.get(0).get(SALARY_DATA.EXPRESSION));
		
		// --------------------------- GENERATE CERTIFIC@2
		String certifica2Message = Certifica2.getCertifica2(dslContext, contractId, suspensionReasonCode, out);
		
		if(!StringUtils.isBlank(certifica2Message))
			return "No se ha podido generar el fichero Certific@2: \n\n" + certifica2Message;
		
		// Enterprise ID
		List<Integer> enterprisesRecord = dslContext.select(REGISTRY.ID).from(REGISTRY)
				.where(REGISTRY.NAME.eq(salaryDraft.getEnterpriseName()))
				.fetch(REGISTRY.ID);
		
		// --------------------------- INSERT CERTIFICA2_BATCH TABLE
		Certifica2BatchRecord cetifica2_batchRecord = dslContext.insertInto(CERTIFICA2_BATCH)
			.set(CERTIFICA2_BATCH.DOMAIN, domainId)
			.set(CERTIFICA2_BATCH.ENTERPRISE, enterprisesRecord.get(0))
			.set(CERTIFICA2_BATCH.INCOME_FILE, (byte[])null)
			.set(CERTIFICA2_BATCH.OUTCOME_FILE, (byte[])null)
			.returning(CERTIFICA2_BATCH.ID)
			.fetchOne();
		 
		Integer cetifica2_batch_id = cetifica2_batchRecord.get(CERTIFICA2_BATCH.ID);
		
		// --------------------------- INSERT CERTIFICA2_BATCH_DETAIL TABLE
		dslContext.insertInto(CERTIFICA2_BATCH_DETAIL)
			.set(CERTIFICA2_BATCH_DETAIL.DOMAIN, domainId)
			.set(CERTIFICA2_BATCH_DETAIL.CERTIFICA2_BATCH, cetifica2_batch_id)
			.set(CERTIFICA2_BATCH_DETAIL.CONTRACT, contractId)
			.set(CERTIFICA2_BATCH_DETAIL.SUSPENSION_CAUSE_CODE, suspensionReasonCode)
			.set(CERTIFICA2_BATCH_DETAIL.STATUS, (byte)0)
			.set(CERTIFICA2_BATCH_DETAIL.ERE_NUMBER, (String)null)
			.execute();
		
		// Generate SEPE_BATCH_ATTACH description
		java.util.Date date = new java.util.Date();
		String sepe_batch_description = salaryDraft.getEnterpriseDocument() + simpleDateFormat.format(date);
		
		// --------------------------- INSERT SEPE_BATCH_ATTACH TABLE
		dslContext.insertInto(SEPE_BATCH_ATTACH)
			.set(SEPE_BATCH_ATTACH.DOMAIN, domainId)
			.set(SEPE_BATCH_ATTACH.SOURCE_BATCH, cetifica2_batch_id)
			.set(SEPE_BATCH_ATTACH.SOURCE_TYPE, (byte)0)
			.set(SEPE_BATCH_ATTACH.MIMETYPE, (byte)5)
			.set(SEPE_BATCH_ATTACH.DESCRIPTION, sepe_batch_description)
			.set(SEPE_BATCH_ATTACH.DATA, out.toByteArray())
			.set(SEPE_BATCH_ATTACH.TYPE, (byte)0)
			.set(SEPE_BATCH_ATTACH.ATTACH_DATE, new Date(date.getTime()))
			.execute();
		
		String employeeName = salaryDraft.getEmployee().getFullname();
		
		return "El fichero Certific@2 se ha generado correctamente. \n\n Para poder visualizarlo y comunicarlo, dirijase a: \n\n Contratos > " + employeeName + " > Mas > Certific@2";
	}
	
	private static String getSuspensionReasonCode (String compensation_reason) {
		switch (compensation_reason) {
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
	
	// ------------------------------------------- SEPE Comunication
	
	public static Certificates createCertificates(Connection conn, Integer domainId, Integer contractId, String suspensionReasonCode) throws IllegalArgumentException {
		return createCertificates(DSL.using(conn, getDefaultSettings()), domainId, contractId, suspensionReasonCode);
	}

	private static Certificates createCertificates(DSLContext dslContext, Integer domainId, Integer contractId, String suspensionReasonCode) throws IllegalArgumentException {
		
		// ---------------------------------------------------- Employee Data
		Record contractRecord = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Integer personId = contractRecord.get(CONTRACT.PERSON);
		Integer enterpriseCCCId = contractRecord.get(CONTRACT.ENTERPRISE_CCC);
		Integer enterpriseActivityId = contractRecord.get(CONTRACT.ENTERPRISE_ACTIVITY);
		
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		Long contractDuration = getDaysBetween(startDate, endDate);
		
		Record personRecord = dslContext.select().from(PERSON)
				.where(PERSON.REGISTRY.eq(personId))
				.fetchOne();
		
		String name = personRecord.get(PERSON.NAME);
		String surName = personRecord.get(PERSON.FIRST_SURNAME);
		String secondSurName = personRecord.get(PERSON.SECOND_SURNAME);
		String ssNum = personRecord.get(PERSON.SOCIAL_SECURITY_NUM);
		
		if(AonStringUtils.isBlank(ssNum))
			throw new IllegalArgumentException("No existe numero de la Seguridad Social para esta persona");
		
		Record employeeRegistry = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(personId))
				.fetchOne();
		
		String dni = employeeRegistry.get(REGISTRY.DOCUMENT);
		
		if(AonStringUtils.isBlank(dni))
			throw new IllegalArgumentException("No existe documento de identidad para esta persona");
		
		String contractType = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("TC2"))
				.fetchOne(CONTRACT_DATA.EXPRESSION);
		
		String tc2 = normalizeString(contractType);
		
		String quoteGroupType = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION"))
				.fetchOne(CONTRACT_DATA.EXPRESSION);
		
		if(AonStringUtils.isBlank(quoteGroupType))
			throw new IllegalArgumentException("No existe grupo de cotizacion para este contrato");
		
		String quoteGroup = normalizeString(quoteGroupType);
		
		String cnoType = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("CNO"))
				.fetchOne(CONTRACT_DATA.EXPRESSION);
		
		
		
		if(AonStringUtils.isBlank(cnoType))
			throw new IllegalArgumentException("No existe CNO para este contrato");
		
		String cno = normalizeString(cnoType);
		
		// ---------------------------------------------------- Enterprise Data
		
		Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.ID.eq(enterpriseCCCId))
				.fetchOne();
		
		String regime = parseSS_Regime(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE));
		String ccc = enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC);
		String completeCCC = regime + ccc;
		
		Record enterpriseRegistryRecord = dslContext.select().from(REGISTRY).where(REGISTRY.ID.eq(
					dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY)
						.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseActivityId))
						.fetchOne(ENTERPRISE_ACTIVITY.ENTERPRISE)
			)).fetchOne();
		
		String enterpriseCIF = enterpriseRegistryRecord.get(REGISTRY.DOCUMENT);
		
		// ---------------------------------------------------- Salaries Data
		
		List<Certifica2Info> certifica2List = new ArrayList<Certifica2Info>();
		Integer maxDays = 0;
		
		Result<Record> salariesRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.TYPE.eq((byte)0))
				.orderBy(SALARY.ID.desc())
				.fetch();
		
		for(Record salary : salariesRecords) {
			if(maxDays > 180)
				break;
			
			Integer salaryId = salary.get(SALARY.ID);
			
			Date salaryStartDate = salary.get(SALARY.START_DATE);
			Date salaryEndDate = salary.get(SALARY.END_DATE);
			Long salaryDaysBetween = getDaysBetween(salaryStartDate, salaryEndDate);
			
			List<String> baseCGCRecords = dslContext.select(SALARY_DATA.EXPRESSION).from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(salaryId))
					.and(SALARY_DATA.NAME.eq("BASE_CGC"))
					.fetch(SALARY_DATA.EXPRESSION);
			
			Double baseCGC = 0.00;
			
			// Using for, cause can be periods in the same Salary
			for(String baseCGCStr : baseCGCRecords) {
				baseCGC += Double.parseDouble(baseCGCStr);
			}
			
			List<String> baseCGPRecords = dslContext.select(SALARY_DATA.EXPRESSION).from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(salaryId))
					.and(SALARY_DATA.NAME.eq("BASE_CGP"))
					.fetch(SALARY_DATA.EXPRESSION);
			
			Double baseCGP = 0.00;
			
			// Using for, cause can be periods in the same Salary
			for(String baseCGPStr : baseCGPRecords) {
				baseCGP += Double.parseDouble(baseCGPStr);
			}
			
			// Initialize Certifica2Info
			Certifica2Info certifica2Info = null;
			
			// Ya has cumplido los 180 dias de registro
			if(maxDays + salaryDaysBetween > 180) {
				 Long restDays = salaryDaysBetween - (maxDays + salaryDaysBetween - 180);
				 
				 certifica2Info = new Certifica2Info(
							yearDateFormat.format(salaryStartDate), 
							monthDateFormat.format(salaryStartDate), 
							restDays.intValue(), 
							baseCGC / 30 * restDays.intValue(), 
							baseCGP / 30 * restDays.intValue());
				 
				 maxDays += salaryDaysBetween.intValue();
				 
			} else {
				certifica2Info = new Certifica2Info(
						yearDateFormat.format(salaryStartDate), 
						monthDateFormat.format(salaryStartDate), 
						salaryDaysBetween.intValue(), 
						baseCGC, 
						baseCGP);
				
				maxDays += salaryDaysBetween.intValue();
			}
			
			certifica2List.add(certifica2Info);
			
		}
		
		// Check Settle for unEnjoy Holidays
		Result<Record> settlementRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.TYPE.eq((byte)2))
				.orderBy(SALARY.ID.desc())
				.fetch();
		
		Integer settlementId = settlementRecords.get(0).get(SALARY.ID);
		
		Date chargeDate = settlementRecords.get(0).get(SALARY.CHARGE_DATE);
		Date settlementEndDate = settlementRecords.get(0).get(SALARY.END_DATE);
		Long settlementDaysBetween = getDaysBetween(chargeDate, settlementEndDate);
		
		Record holidaysRecord = dslContext.select().from(SALARY_PAYMENT)
				.where(SALARY_PAYMENT.SALARY.eq(settlementId))
				.and(SALARY_PAYMENT.TYPE.eq((byte)6))
				.fetchOne();
		
		Double baseCGC = holidaysRecord.get(SALARY_PAYMENT.AMOUNT);
		Double baseCGP = holidaysRecord.get(SALARY_PAYMENT.QUOTE);
		
		Certifica2Info settlementCertifica2Info = new Certifica2Info(
				null,
				null,
				settlementDaysBetween.intValue(), 
				baseCGC, 
				baseCGP);
		
		// ---------------------------------------------------- Create Certificates
		
		CertificatesBuilder bd  = new CertificatesBuilder();
		bd.setRegimen(regime)
			.setCtaCti(ccc)
			.setIpf(dni)
			.setIpfManager(enterpriseCIF)
			.setName(name)
			.setSurname(surName)
			.setLastSurname(secondSurName)
			.setTypeContract(tc2)
			.setGz(quoteGroup)
			.setDurationContract(contractDuration.intValue())
			.setTypeDuration(TypeDuration.DIAS)
			.setCatProfessional(cno)
			.setCauseSuspension(suspensionReasonCode)
			.setfAEd(startDate)
			.setfSTd(endDate)
			.setDaysCtzVc(settlementCertifica2Info.getQuotedDays())
			.setBcccVc(settlementCertifica2Info.getBase_cgc()+"")
			.setBcdVc(settlementCertifica2Info.getBase_unemployment()+"");
		
		List<Map<String, String>> dataCtz = new ArrayList<Map<String, String>>();
		
		certifica2List.sort(new Comparator<Certifica2Info>() {
			@Override
			public int compare(Certifica2Info o1, Certifica2Info o2) {
				return o2.getMonth().compareTo(o1.getMonth());
			}
		});
		
		for(Certifica2Info certifica2 : certifica2List) {
			Map<String, String> values = new HashMap<String, String>();
			
			values.put("anioCtz", certifica2.getYear());
			values.put("monthCtz", certifica2.getMonth());
			values.put("daysCtz", certifica2.getQuotedDays()+"");
			values.put("bccc", certifica2.getBase_cgc()+"");
			values.put("bcd",  certifica2.getBase_unemployment()+"");
			dataCtz.add(values);
		}
		
		bd.setDataCtz(dataCtz);
		
		Certificates certificates = bd.build();
		
		return certificates;
	}

	public static String getSuspensionReasonCode(Connection conn, Integer domainId, Integer contractId) throws IllegalArgumentException {
		return getSuspensionReasonCode(DSL.using(conn, getDefaultSettings()), domainId, contractId);
	}

	private static String getSuspensionReasonCode(DSLContext dslContext, Integer domainId, Integer contractId) throws IllegalArgumentException {
		// Settlement Record
		Result<Record> settlementRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.TYPE.eq((byte)2))
				.orderBy(SALARY.ID.desc())
				.fetch();
		
		if(settlementRecords.isEmpty())
			throw new IllegalArgumentException("No existe finiquito, por lo que no es posible comunicar Cetifica2");
		
		// Settlement Id
		Integer settlementId = settlementRecords.get(0).get(SALARY.ID);
		
		// Find Suspension Code in settlement salary_data
		Result<Record> suspensionCodeRecords = dslContext.select().from(SALARY_DATA)
				.where(SALARY_DATA.SALARY.eq(settlementId))
				.and(SALARY_DATA.NAME.eq("FIN").or(SALARY_DATA.NAME.eq("CAUSA_INDEMNIZACION")))
				.fetch();
		
		if(suspensionCodeRecords.isEmpty())
			throw new IllegalArgumentException("No existe causa de indemnizaci\u00F3n para poder generar Cetifica2");
		
		String suspensionReasonCode = "00";
		
		if(suspensionCodeRecords.isNotEmpty())
			suspensionReasonCode = getSuspensionReasonCode(suspensionCodeRecords.get(0).get(SALARY_DATA.EXPRESSION));
		
		return suspensionReasonCode;
	}
	
	// ----------------------------------------------------- Auxiliar Methods
	
	private static Long getDaysBetween(Date startDate, Date endDate) {
		java.util.Date salaryStartDateJava = new java.util.Date(startDate.getTime());
		java.util.Date salaryEndDateJava = new java.util.Date(endDate.getTime());
		return Duration.between(salaryStartDateJava.toInstant(), salaryEndDateJava.toInstant()).toDays() + 1;
	}

	private static String normalizeString(String contractType) {
		if(contractType.contains("\""))
			return contractType.split("\"")[1];
		return contractType;
	}

	private static String parseSS_Regime(Byte ss_regime) {
		switch (ss_regime) {
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
}
