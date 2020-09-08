package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.Certifica2Batch.CERTIFICA2_BATCH;
import static com.esferalia.aon.jooq.tables.Certifica2BatchDetail.CERTIFICA2_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.jooq.tables.records.Certifica2BatchRecord;
import com.esferalia.aon.payroll.sepe.certifica2.Certifica2;

public class JooqCertifica2 {

	private static Settings SETTINGS = null;
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
	
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
		
		String suspensionReasonCode = getSuspensionReasonCode(suspensionCodeRecords.get(0).get(SALARY_DATA.EXPRESSION));
		
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

	
	
	
}
