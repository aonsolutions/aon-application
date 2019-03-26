package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.CraBatch.CRA_BATCH;
import static com.esferalia.aon.jooq.tables.CraBatchDetail.CRA_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.InsertResultStep;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.jooq.tables.records.CraBatchRecord;
import com.esferalia.aon.occam.api.AONContext;

public class JooqCRA {
	
	// ********************************************************************************************************************************************
	//													GENERATE JSON AGRARIAN
	// ********************************************************************************************************************************************

	@SuppressWarnings({ "unchecked", "null" })
	public static JSONObject getMainCRA(String _domainId, String domainName, String _enterpriseId, String _enterpriseName, 
			String _ccc, String _startDate, String _endDate) {
		
		java.util.Date startDate = new java.util.Date(Long.parseLong(_startDate));
		java.util.Date endDate = new java.util.Date(Long.parseLong(_endDate));
		
		Date startDateSQL = new Date(startDate.getTime());
		Date endDateSQL = new Date(endDate.getTime());
		
		AONContext context = null;
		JSONObject mainCRAJSON = new JSONObject();
		
		try {
			DSLContext dslContext = AONContext.getAONContext(domainName, Integer.parseInt(_domainId),
					AonServletUtils.getLoggedUser()).getDslContext();
			
			//GET AuthKey from DB
			Record domainRecord = dslContext.select().from(DOMAIN)
					.where(DOMAIN.ID.eq(Integer.parseInt(_domainId)))
					.fetchOne();
			
			Integer parentDomainId = domainRecord.get(DOMAIN.PARENT);
			
			Record appParamRecord = dslContext.select().from(APP_PARAM)
					.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY"))
						.and(APP_PARAM.DOMAIN.eq(Integer.parseInt(_domainId)))
					.fetchOne();
			
			String authKey = "";
			if(null == appParamRecord || null == appParamRecord.get(APP_PARAM.VALUE)) {
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
			
			
			//ETI
			JSONObject eti = new JSONObject();
			eti.put("authkey", authKey);
			eti.put("fileName", null);
			eti.put("prorityCode", "N");
			
			mainCRAJSON.put("ETI", eti);
			
			
			//GET Salaries from DB (employees)
			Result<Record> salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.START_DATE.ge(startDateSQL))
						.and(SALARY.END_DATE.le(endDateSQL))
						.and(SALARY.ENTERPRISE_NAME.equalIgnoreCase(_enterpriseName))
						.and(SALARY.CCC.eq(_ccc))
					.fetch();
			
			//DDE
			JSONObject dde = new JSONObject();
			dde.put("cccRegime", parseSS_Regime(salaryRecords.get(0).get(SALARY.SS_REGIME)));
			dde.put("ccc", salaryRecords.get(0).get(SALARY.CCC));
			
			JSONArray trbs = new JSONArray();
			
			for(Record salary: salaryRecords) {
				JSONObject trb = new JSONObject();
				
				trb.put("numAfilicion", salary.get(SALARY.SOCIAL_SECURITY_NUMBER));
				
				JSONArray cres = new JSONArray();
				
				//GET Salaries from DB (employees)
				Result<Record> salaryPaymentRecords = dslContext.select().from(SALARY_PAYMENT)
						.where(SALARY_PAYMENT.SALARY.eq(salary.get(SALARY.ID)))
						.fetch();
				
				Type typeCRA = null;
				
				for (int i=0; i<salaryPaymentRecords.size(); i++) {
					
					JSONObject cre = new JSONObject();
					Type craType = Payment.Type.values()[salaryPaymentRecords.get(i).get(SALARY_PAYMENT.TYPE)];
					Double craAmount = salaryPaymentRecords.get(i).get(SALARY_PAYMENT.AMOUNT);
					String craAmountStr = String.format( "%.2f", craAmount );
					String amount = craAmountStr.split("[.]")[0] + craAmountStr.split("[.]")[1];
					cre.put("concept", craType.getDescription().split(" ")[0]);
					cre.put("include_exclude", craType.isBBCCIncluded() ? "I" : "E");
					cre.put("amount", amount);
					
					
					if(typeCRA == craType) {
						cre.put("action", "C");
					}else {
						cre.put("action", " ");
						typeCRA = craType;
					}
					
					cres.add(cre);
					
				}
				
				trb.put("CRES", cres);
				
				trbs.add(trb);
			}
			
			dde.put("TRBS", trbs);
			
			mainCRAJSON.put("DDE", dde);
			
			System.out.println(mainCRAJSON);
			
		} finally {
			if (context != null)
				context.close();
		}
		
		
		return mainCRAJSON;
	}

	private static String parseSS_Regime(Byte ss_regime) {
		switch (ss_regime) {
		case 3:
			return "0163";
		default:
			return "0111";
		}
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    long factor = (long) Math.pow(10, places);
	    value = value * factor;
	    long tmp = Math.round(value);
	    return (double) tmp / factor;
	}

	
	
//	private static String removeAccents(String cadena) {
//	    return cadena.replace("Á", "A")
//	            .replace("É", "E")
//	            .replace("Í", "I")
//	            .replace("Ó", "O")
//	            .replace("Ú", "U")
//	            .replace("á", "a")
//	            .replace("é", "e")
//	            .replace("í", "i")
//	            .replace("ó", "o")
//	            .replace("ú", "u");
//	}


	// ********************************************************************************************************************************************
	//													GET DOMAIN CRAs FOR LIST
	// ********************************************************************************************************************************************
	
	private static Settings SETTINGS = null;
	
	public static List<CRA> getDomainCRAs(String domain, Connection conn) {
		return getDomainCRAsDB(domain, DSL.using(conn, getDefaultSettings()));
	}

	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static List<CRA> getDomainCRAsDB(String domain, DSLContext dslContext) {
		List<CRA> cras = new ArrayList<CRA>();
		
		Integer domainId = dslContext.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.NAME.eq(domain))
				.fetchOne(DOMAIN.ID);
		
		Result<Record> craBatchRecords = dslContext.select().from(CRA_BATCH)
				.where(CRA_BATCH.DOMAIN.eq(domainId))
				.fetch();
		
		for(Record craBatch : craBatchRecords) {
			CRA cra = new CRA();
			
			cra.setCode(craBatch.get(CRA_BATCH.ID));
			cra.setStatus(craBatch.get(CRA_BATCH.STATUS));
			cra.setCreationDate(craBatch.get(CRA_BATCH.OUTCOME_FILE_DATE));
			
			Record craBatchDetailRecord = dslContext.select().from(CRA_BATCH_DETAIL)
					.where(CRA_BATCH_DETAIL.CRA_BATCH.eq(craBatch.get(CRA_BATCH.ID)))
					.fetchOne();
			
			Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.ID.eq(craBatchDetailRecord.get(CRA_BATCH_DETAIL.ENTERPRISE_CCC)))
					.fetchOne();
			
			cra.setCcc(getCCCType(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE)) + enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC));
			cra.setCccType(getCCCTypeName(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE)));
			
			String geozone = dslContext.select(GEOZONE.NAME).from(GEOZONE)
					.where(GEOZONE.ID.eq(enterpriseCCCRecord.get(ENTERPRISE_CCC.GEOZONE)))
					.fetchOne(GEOZONE.NAME);
			
			cra.setCccProvince(geozone);
			
			Record enterpriseActivityRecord = dslContext.select().from(ENTERPRISE_ACTIVITY)
					.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseCCCRecord.get(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY)))
					.fetchOne();
			
			cra.setActivityName(enterpriseActivityRecord.get(ENTERPRISE_ACTIVITY.DESCRIPTION));
			
			cras.add(cra);
		}
		
		
		return cras;
	}

	private static String getCCCType(Byte cccType) {
		switch (cccType) {
		case 3:
			return "0163";
		default:
			return "0111";
		}
	}
	
	private static String getCCCTypeName(Byte cccType) {
		switch (cccType) {
		case 3:
			return "Agraria";
		default:
			return "Pricipal";
		}
	}

	// ********************************************************************************************************************************************
	//													GENERATE JSON CRA
	// ********************************************************************************************************************************************
	
	public static byte[] getDownloadMainCRA(String _domainId, String domainName, String _craBatchId) {
		AONContext context = null;
		
		try {
			DSLContext dslContext = AONContext.getAONContext(domainName, Integer.parseInt(_domainId),
					AonServletUtils.getLoggedUser()).getDslContext();
			
			Record craBatchRecord = dslContext.select().from(CRA_BATCH)
					.where(CRA_BATCH.ID.eq(Integer.parseInt(_craBatchId)))
					.fetchOne();
			
			byte[] data = craBatchRecord.get(CRA_BATCH.OUTCOME_FILE);
			return data;
			
		}finally {
			if (context != null)
				context.close();
		}
	}

	public static void deleteMainCRA(String _domainId, String domainName, String _craBatchId) {
		AONContext context = null;
		
		try {
			DSLContext dslContext = AONContext.getAONContext(domainName, Integer.parseInt(_domainId),
					AonServletUtils.getLoggedUser()).getDslContext();
			
			dslContext.delete(CRA_BATCH_DETAIL)
				.where(CRA_BATCH_DETAIL.CRA_BATCH.eq(Integer.parseInt(_craBatchId)))
				.execute();
			
			dslContext.deleteFrom(CRA_BATCH)
				.where(CRA_BATCH.ID.eq(Integer.parseInt(_craBatchId)))
				.execute();
			
		}finally {
			if (context != null)
				context.close();
		}
	}

	public static void setMainCra(String _domainId, String domainName, String _cccId, String agrarianAFI, String _startDate) {
		AONContext context = null;
		
		java.util.Date startDate = new java.util.Date(Long.parseLong(_startDate));
		
		try {
			DSLContext dslContext = AONContext.getAONContext(domainName, Integer.parseInt(_domainId),
					AonServletUtils.getLoggedUser()).getDslContext();
			
			CraBatchRecord craBatchRecord = dslContext.insertInto(CRA_BATCH)
				.set(CRA_BATCH.DOMAIN, Integer.parseInt(_domainId))
				.set(CRA_BATCH.DATE, new Timestamp(startDate.getTime()))
				.set(CRA_BATCH.STATUS, (byte)1)
				.set(CRA_BATCH.INCOME_FILE, (byte[])null)
				.set(CRA_BATCH.OUTCOME_FILE, agrarianAFI.getBytes())
				.set(CRA_BATCH.OUTCOME_FILE_DATE, new Timestamp(startDate.getTime()))
				.returning(CRA_BATCH.ID)
				.fetchOne();
			
			Integer craBatchId = craBatchRecord.getId();
			
			dslContext.insertInto(CRA_BATCH_DETAIL)
				.set(CRA_BATCH_DETAIL.DOMAIN, Integer.parseInt(_domainId))
				.set(CRA_BATCH_DETAIL.CRA_BATCH, craBatchId)
				.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, Integer.parseInt(_cccId))
				.execute();
			
		}finally {
			if (context != null)
				context.close();
		}
		
	}
}
