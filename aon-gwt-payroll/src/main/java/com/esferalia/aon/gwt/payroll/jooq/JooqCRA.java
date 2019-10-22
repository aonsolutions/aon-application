package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.CraBatch.CRA_BATCH;
import static com.esferalia.aon.jooq.tables.CraBatchDetail.CRA_BATCH_DETAIL;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Payment.Type;
import com.esferalia.aon.jooq.tables.records.CraBatchRecord;

public class JooqCRA {
	
	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	// ********************************************************************************************************************************************
	//													GENERATE JSON AGRARIAN
	// ********************************************************************************************************************************************

//	@SuppressWarnings({ "unchecked", "null" })
//	public static JSONObject getMainCRA(String _domainId, String domainName, String _enterpriseId, String _enterpriseName, 
//			String _ccc, long _startDate, long _endDate) {
//		
//		java.util.Date startDate = new java.util.Date(_startDate);
//		java.util.Date endDate = new java.util.Date(_endDate);
//		
//		Date startDateSQL = new Date(startDate.getTime());
//		Date endDateSQL = new Date(endDate.getTime());
//		
//		AONContext context = null;
//		JSONObject mainCRAJSON = new JSONObject();
//		
//		try {
//			DSLContext dslContext = AONContext.getAONContext(domainName, Integer.parseInt(_domainId),
//					AonServletUtils.getLoggedUser()).getDslContext();
//			
//			//GET AuthKey from DB
//			Record domainRecord = dslContext.select().from(DOMAIN)
//					.where(DOMAIN.ID.eq(Integer.parseInt(_domainId)))
//					.fetchOne();
//			
//			Integer parentDomainId = domainRecord.get(DOMAIN.PARENT);
//			
//			Record appParamRecord = dslContext.select().from(APP_PARAM)
//					.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY"))
//						.and(APP_PARAM.DOMAIN.eq(Integer.parseInt(_domainId)))
//					.fetchOne();
//			
//			String authKey = "";
//			if(null == appParamRecord || null == appParamRecord.get(APP_PARAM.VALUE)) {
//				appParamRecord = dslContext.select().from(APP_PARAM)
//						.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY"))
//							.and(APP_PARAM.DOMAIN.eq(parentDomainId))
//						.fetchOne();
//				if(null == appParamRecord || null == appParamRecord.get(APP_PARAM.VALUE)) 
//					authKey = "00000";
//				else
//					authKey = appParamRecord.get(APP_PARAM.VALUE);
//			} else
//				authKey = appParamRecord.get(APP_PARAM.VALUE);
//			
//			
//			//ETI
//			JSONObject eti = new JSONObject();
//			eti.put("authkey", authKey);
//			eti.put("fileName", null);
//			eti.put("prorityCode", "N");
//			
//			mainCRAJSON.put("ETI", eti);
//			
//			
//			//GET Salaries from DB (employees)
//			Result<Record> salaryRecords = dslContext.select().from(SALARY)
//					.where(SALARY.START_DATE.ge(startDateSQL))
//						.and(SALARY.END_DATE.le(endDateSQL))
//						.and(SALARY.ENTERPRISE_NAME.equalIgnoreCase(_enterpriseName))
//						.and(SALARY.CCC.eq(_ccc))
//					.fetch();
//			
//			//DDE
//			JSONObject dde = new JSONObject();
//			dde.put("cccRegime", parseSS_Regime(salaryRecords.get(0).get(SALARY.SS_REGIME)));
//			dde.put("ccc", salaryRecords.get(0).get(SALARY.CCC));
//			dde.put("year", startDateSQL.getYear());
//			dde.put("month", startDateSQL.getMonth());
//			
//			JSONArray trbs = new JSONArray();
//			
//			for(Record salary: salaryRecords) {
//				JSONObject trb = new JSONObject();
//				
//				trb.put("numAfilicion", salary.get(SALARY.SOCIAL_SECURITY_NUMBER));
//				
//				JSONArray cres = new JSONArray();
//				
//				//GET Salaries from DB (employees)
//				Result<Record> salaryPaymentRecords = dslContext.select().from(SALARY_PAYMENT)
//						.where(SALARY_PAYMENT.SALARY.eq(salary.get(SALARY.ID)))
//						.fetch();
//				
//				Type typeCRA = null;
//				
//				for (int i=0; i<salaryPaymentRecords.size(); i++) {
//					
//					JSONObject cre = new JSONObject();
//					Type craType = Payment.Type.values()[salaryPaymentRecords.get(i).get(SALARY_PAYMENT.TYPE)];
//					Double craAmount = salaryPaymentRecords.get(i).get(SALARY_PAYMENT.AMOUNT);
//					String craAmountStr = String.format( "%.2f", craAmount );
//					String amount = craAmountStr.split("[.]")[0] + craAmountStr.split("[.]")[1];
//					cre.put("concept", craType.getDescription().split(" ")[0]);
//					cre.put("include_exclude", craType.isBBCCIncluded() ? "I" : "E");
//					cre.put("amount", amount);
//					
//					
//					if(typeCRA == craType) {
//						cre.put("action", "C");
//					}else {
//						cre.put("action", " ");
//						typeCRA = craType;
//					}
//					
//					cres.add(cre);
//					
//				}
//				
//				trb.put("CRES", cres);
//				
//				trbs.add(trb);
//			}
//			
//			dde.put("TRBS", trbs);
//			
//			mainCRAJSON.put("DDE", dde);
//			
//			System.out.println(mainCRAJSON);
//			
//		} finally {
//			if (context != null)
//				context.close();
//		}
//		
//		
//		return mainCRAJSON;
//	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static JSONObject getMainCRAByCRA(String domainName, String _enterpriseId, String _enterpriseName, 
			String _ccc, long _startDate, long _endDate, Connection connection)  {
		
		java.util.Date startDate = new java.util.Date(_startDate);
		java.util.Date endDate = new java.util.Date(_endDate);
		
		//TODO: fix date new dates when payroll emit
		DateUtils.addDays2Date(endDate, 1);
		
		Date startDateSQL = new Date(startDate.getTime());
		Date endDateSQL = new Date(endDate.getTime());
		
		JSONObject mainCRAJSON = new JSONObject();
		
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
		//GET AuthKey from DB
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.NAME.eq(domainName))
				.fetchOne();
		
		Integer parentDomainId = domainRecord.get(DOMAIN.PARENT);
		Integer _domainId = domainRecord.get(DOMAIN.ID);
		
		Record appParamRecord = dslContext.select().from(APP_PARAM)
				.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY"))
					.and(APP_PARAM.DOMAIN.eq(_domainId))
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
//					.and(SALARY.ENTERPRISE_NAME.equalIgnoreCase(_enterpriseName))
					.and(SALARY.CCC.eq(_ccc))
					.and(SALARY.SS_REGIME.notEqual((byte)3))
				.fetch();
		
		//TODO: COMPROBAR ERROR DEL SALARY RECORDS EMPTY
		Result<Record> enterpriseCCCRecords = null;
		// ERRORS
		JSONArray errors = new JSONArray();
		if(salaryRecords.isEmpty()){
			JSONObject err = new JSONObject();
			err.put("ERR", "No hay ninguna nómina emitida para este periodo.");
			errors.add(err);
		}else {
		
			//DDE
			JSONObject dde = new JSONObject();
			enterpriseCCCRecords = dslContext.select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.CCC.eq(salaryRecords.get(0).get(SALARY.CCC)))
					.fetch();
			
			dde.put("cccRegime", parseSS_Regime(enterpriseCCCRecords.get(0).get(ENTERPRISE_CCC.TYPE)));
			dde.put("ccc", salaryRecords.get(0).get(SALARY.CCC));
			dde.put("year", startDateSQL.getYear());
			dde.put("month", startDateSQL.getMonth());
			
			JSONArray trbs = new JSONArray();
			
			for(Record salary: salaryRecords) {
				JSONObject trb = new JSONObject();
				
				trb.put("numAfilicion", salary.get(SALARY.SOCIAL_SECURITY_NUMBER));
				
				JSONArray cres = new JSONArray();
				
				//GET Salaries from DB (employees)
				Result<Record> salaryPaymentRecords = dslContext.select().from(SALARY_PAYMENT)
						.where(SALARY_PAYMENT.SALARY.eq(salary.get(SALARY.ID)))
						.fetch();
				
				Type typeCRA = (salaryPaymentRecords.size() == 0) ? null : Payment.Type.values()[salaryPaymentRecords.get(0).get(SALARY_PAYMENT.TYPE)];;
				Double craAmount = 0.00;
				JSONObject cre = new JSONObject();
				
				for (int i=0; i<salaryPaymentRecords.size(); i++) {
					
					Type craType = Payment.Type.values()[salaryPaymentRecords.get(i).get(SALARY_PAYMENT.TYPE)];
					
					if(typeCRA == craType) {
						craAmount += (salaryPaymentRecords.get(i).get(SALARY_PAYMENT.QUOTE) > 0) ? salaryPaymentRecords.get(i).get(SALARY_PAYMENT.QUOTE) : salaryPaymentRecords.get(i).get(SALARY_PAYMENT.AMOUNT);
						
						//Es la ultima iteracion
						if(i+1 == salaryPaymentRecords.size()) {
							String craAmountStr = String.format( "%.2f", craAmount );
							String amount = "";
							if(craAmountStr.contains(","))
								amount = craAmountStr.split(",")[0] + craAmountStr.split(",")[1];
							else
								amount = craAmountStr.split("[.]")[0] + craAmountStr.split("[.]")[1];
							// String amount = craAmountStr.split("[.]")[0] + craAmountStr.split("[.]")[1];
							
							if(craAmount > 0){
								cre.put("concept", craType.getDescription().split(" ")[0]);
								cre.put("include_exclude", craType.isBBCCIncluded() ? "I" : "E");
								cre.put("amount", amount);
								cre.put("action", " ");
								cres.add(cre);
							}
						}
						
						continue;
					}else {
						String craAmountStr = String.format( "%.2f", craAmount );
						String amount = "";
						if(craAmountStr.contains(","))
							amount = craAmountStr.split(",")[0] + craAmountStr.split(",")[1];
						else
							amount = craAmountStr.split("[.]")[0] + craAmountStr.split("[.]")[1];
						
						if(craAmount > 0){
							cre.put("concept", typeCRA.getDescription().split(" ")[0]);
							cre.put("include_exclude", typeCRA.isBBCCIncluded() ? "I" : "E");
							cre.put("amount", amount);
							cre.put("action", " ");
							cres.add(cre);
						}
						
						typeCRA = craType;
						cre = new JSONObject();
						craAmount = (salaryPaymentRecords.get(i).get(SALARY_PAYMENT.QUOTE) > 0) ? salaryPaymentRecords.get(i).get(SALARY_PAYMENT.QUOTE) : salaryPaymentRecords.get(i).get(SALARY_PAYMENT.AMOUNT);
						
						//Es la ultima iteracion
						if(i+1 == salaryPaymentRecords.size()) {
							craAmountStr = String.format( "%.2f", craAmount );
							if(craAmountStr.contains(","))
								amount = craAmountStr.split(",")[0] + craAmountStr.split(",")[1];
							else
								amount = craAmountStr.split("[.]")[0] + craAmountStr.split("[.]")[1];
							// amount = craAmountStr.split("[.]")[0] + craAmountStr.split("[.]")[1];
							
							if(craAmount > 0){
								cre.put("concept", craType.getDescription().split(" ")[0]);
								cre.put("include_exclude", craType.isBBCCIncluded() ? "I" : "E");
								cre.put("amount", amount);
								cre.put("action", " ");
								cres.add(cre);
							}
						}
					}
					
				}
				
				if(cres.size() > 0) {
					
					trb.put("CRES", cres);
					trbs.add(trb);
				
				}
				
				
			}
			
			dde.put("TRBS", trbs);
			
			mainCRAJSON.put("DDE", dde);
		}
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// 											NOMINA ATRASOS
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		//TODO: fix date new dates when payroll emit, needed?
		java.util.Date endDateFirst = DateUtils.copyDateOnly(endDate);
		DateUtils.deleteDays2Date(endDateFirst, 1);
		
		Date endDateFirstSQL = new Date(endDateFirst.getTime());
		endDateSQL = new Date(endDate.getTime());
		
		//GET Atrasos SALARY from DB (employees)
		salaryRecords = dslContext.select().from(SALARY)
				.where(SALARY.ENTERPRISE_NAME.equalIgnoreCase(_enterpriseName))
					.and(SALARY.CCC.eq(_ccc))
					.and(SALARY.TYPE.eq((byte)3))
					.and(SALARY.END_DATE.between(endDateFirstSQL, endDateSQL))
					.and(SALARY.SS_REGIME.notEqual((byte)3))
				.fetch();
		
		if(salaryRecords.isEmpty()){
			JSONObject err = new JSONObject();
			err.put("ERR1", "No hay ninguna nómina emitida para este periodo.");
			errors.add(err);
		}else {
		
			//DDEAS
			JSONArray ddeas = new JSONArray();
			
			for(Record salary: salaryRecords) {
				Integer salaryId = salary.get(SALARY.ID);
				
				Result<Record> salaryDatas = dslContext.select().from(SALARY_DATA)
						.where(SALARY_DATA.SALARY.eq(salaryId))
						.and(SALARY_DATA.NAME.eq("BASE_CGC"))
						.fetch();
				
				for(Record salatyData: salaryDatas){
					Double craAmount = Double.parseDouble(salatyData.get(SALARY_DATA.EXPRESSION));
					
					if(craAmount > 0){
						if(salatyData.get(SALARY_DATA.START_DATE).before(endDateSQL)){		
							JSONObject ddea = new JSONObject();
							enterpriseCCCRecords = dslContext.select().from(ENTERPRISE_CCC)
									.where(ENTERPRISE_CCC.CCC.eq(salary.get(SALARY.CCC)))
									.fetch();
							
							ddea.put("cccRegime", parseSS_Regime(enterpriseCCCRecords.get(0).get(ENTERPRISE_CCC.TYPE)));
							ddea.put("ccc", salary.get(SALARY.CCC));
							ddea.put("year", salatyData.get(SALARY_DATA.START_DATE).getYear() + 1900);
							ddea.put("month", salatyData.get(SALARY_DATA.START_DATE).getMonth() + 1);
						
							JSONArray trbsa = new JSONArray();
							JSONObject trba = new JSONObject();
						
							trba.put("numAfilicion", salary.get(SALARY.SOCIAL_SECURITY_NUMBER));
						
							JSONArray cres = new JSONArray();
							
							Type typeCRA = Payment.Type.values()[8];;
	//							Double craAmount = Double.parseDouble(salatyData.get(SALARY_DATA.EXPRESSION));
							JSONObject cre = new JSONObject();
							
							String craAmountStr = String.format( "%.2f", craAmount );
							String amount = "";
							if(craAmountStr.contains(","))
								amount = craAmountStr.split(",")[0] + craAmountStr.split(",")[1];
							else
								amount = craAmountStr.split("[.]")[0] + craAmountStr.split("[.]")[1];
							
							cre.put("concept", typeCRA.getDescription().split(" ")[0]);
							cre.put("include_exclude", typeCRA.isBBCCIncluded() ? "I" : "E");
							cre.put("amount", amount);
							cre.put("action", " ");
							cres.add(cre);
							
							trba.put("CRES", cres);
							trbsa.add(trba);
							ddea.put("TRBS", trbsa);
							ddeas.add(ddea);
						}
					}
				}
				
			}
			mainCRAJSON.put("DDEAS", parseDDEAS(ddeas));
		}
		
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// 											FINIQUITOS
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		//GET Finiquitos SALARY from DB (employees) se usa ISSUE_DATE por que el finiquito puede tener un startDate de hace mil..
		salaryRecords = dslContext.select().from(SALARY)
				.where(SALARY.ENTERPRISE_NAME.equalIgnoreCase(_enterpriseName))
					.and(SALARY.CCC.eq(_ccc))
					.and(SALARY.ISSUE_DATE.between(startDateSQL, endDateSQL)
							.or(SALARY.END_DATE.between(startDateSQL, endDateSQL)))
					.and(SALARY.TYPE.eq((byte)2))
					.and(SALARY.SS_REGIME.notEqual((byte)3))
				.fetch();
		
		if(!salaryRecords.isEmpty()){
			enterpriseCCCRecords = dslContext.select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.CCC.eq(salaryRecords.get(0).get(SALARY.CCC)))
					.fetch();
			
			//Finiq
			JSONObject finiq = new JSONObject();
			finiq.put("cccRegime", parseSS_Regime(enterpriseCCCRecords.get(0).get(ENTERPRISE_CCC.TYPE)));
			finiq.put("ccc", salaryRecords.get(0).get(SALARY.CCC));
			finiq.put("year", startDateSQL.getYear());
			finiq.put("month", startDateSQL.getMonth());
			JSONArray trbsf = new JSONArray();
			for(Record salary: salaryRecords) {
				Integer salaryId = salary.get(SALARY.ID);
				//Date finiqEndDate = salary.get(SALARY.END_DATE);
				
				Result<Record> salaryDatas = dslContext.select().from(SALARY_DATA)
						.where(SALARY_DATA.SALARY.eq(salaryId))
						.and(SALARY_DATA.NAME.eq("BASE_CGC"))
						.and(SALARY_DATA.START_DATE.between(startDateSQL, endDateSQL))
						.fetch();
				
				for(Record salatyData: salaryDatas){
					Double craAmount = Double.parseDouble(salatyData.get(SALARY_DATA.EXPRESSION));
					
					if(craAmount > 0){
						JSONObject trbf = new JSONObject();
						
						trbf.put("numAfilicion", salary.get(SALARY.SOCIAL_SECURITY_NUMBER));
					
						JSONArray cres = new JSONArray();
						
						Type typeCRA = Payment.Type.values()[6];;
//							Double craAmount = Double.parseDouble(salatyData.get(SALARY_DATA.EXPRESSION));
						JSONObject cre = new JSONObject();
						
						String craAmountStr = String.format( "%.2f", craAmount );
						String amount = "";
						if(craAmountStr.contains(","))
							amount = craAmountStr.split(",")[0] + craAmountStr.split(",")[1];
						else
							amount = craAmountStr.split("[.]")[0] + craAmountStr.split("[.]")[1];
						
						cre.put("concept", typeCRA.getDescription().split(" ")[0]);
						cre.put("include_exclude", typeCRA.isBBCCIncluded() ? "I" : "E");
						cre.put("amount", amount);
						cre.put("action", " ");
						cres.add(cre);
						
						trbf.put("CRES", cres);
						trbsf.add(trbf);
					}
				}
				finiq.put("TRBS", trbsf);
				
			}
			mainCRAJSON.put("FINIQ", finiq);
		}
		
		mainCRAJSON.put("ERRS", errors);
		
		System.out.println(mainCRAJSON);
		
		return mainCRAJSON;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private static JSONArray parseDDEAS(JSONArray ddeas) {
		JSONArray result = new JSONArray();
		
		ArrayList<Date> visitedDates = new ArrayList<>();
		
		JSONObject ddea = new JSONObject();
		for(int i = 0; i<ddeas.size(); i++){
			JSONObject ddeaFirst = (JSONObject) ddeas.get(i); 
			String month = ddeaFirst.get("month").toString();
			String year = ddeaFirst.get("year").toString();
			
			Date date = new Date(Integer.parseInt(year) - 1900, Integer.parseInt(month) - 1, 1);
			
			if(!visitedDates.contains(date)){
				ddea.put("cccRegime", ddeaFirst.get("cccRegime"));
				ddea.put("ccc", ddeaFirst.get("ccc"));
				ddea.put("month", ddeaFirst.get("month"));
				ddea.put("year", ddeaFirst.get("year"));
				
				JSONArray trbs = new JSONArray();
				JSONObject trb = (JSONObject) ((JSONArray) ddeaFirst.get("TRBS")).get(0);
				
				String ssNum = trb.get("numAfilicion").toString();
				trbs.add(trb);
				
				for(int j = 1; j<ddeas.size(); j++){
					JSONObject ddeaAux = (JSONObject) ddeas.get(j); 
					if(month.equals(ddeaAux.get("month").toString()) && year.equals(ddeaAux.get("year").toString()) 
							&& !visitedDates.contains(date)){
						JSONObject trbAux = (JSONObject) ((JSONArray) ddeaAux.get("TRBS")).get(0);
						if(!ssNum.equals(trbAux.get("numAfilicion").toString()))
							trbs.add(trbAux);
					}
				}
				ddea.put("TRBS", trbs);
				visitedDates.add(date);
				result.add(ddea);
				ddea = new JSONObject();
			}
			
		}
		
		return result;
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


	// ********************************************************************************************************************************************
	//													GET DOMAIN CRAs FOR LIST
	// ********************************************************************************************************************************************
	
	public static List<CRA> getDomainCRAs(String domainName, Connection conn) {
		return getDomainCRAsDB(domainName, DSL.using(conn, getDefaultSettings()));
	}
	
	private static List<CRA> getDomainCRAsDB(String domainName, DSLContext dslContext) {
		List<CRA> cras = new ArrayList<CRA>();
		
		Integer domainId = dslContext.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.NAME.eq(domainName))
				.fetchOne(DOMAIN.ID);
		
		Result<Record> craBatchRecords = dslContext.select().from(CRA_BATCH)
				.where(CRA_BATCH.DOMAIN.eq(domainId))
				.fetch();
		
		for(Record craBatch : craBatchRecords) {
			CRA cra = new CRA();
			
			cra.setCode(craBatch.get(CRA_BATCH.ID));
			cra.setStatus(craBatch.get(CRA_BATCH.STATUS));
			cra.setCreationDate(craBatch.get(CRA_BATCH.OUTCOME_FILE_DATE));
			cra.setType(craBatch.get(CRA_BATCH.COMMUNICATION_ID));
			
			 Result<Record> craBatchDetailRecord = dslContext.select().from(CRA_BATCH_DETAIL)
					.where(CRA_BATCH_DETAIL.CRA_BATCH.eq(craBatch.get(CRA_BATCH.ID)))
					.fetch();
			
			if(null != craBatchDetailRecord && !craBatchDetailRecord.isEmpty()){
			
				Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
						.where(ENTERPRISE_CCC.ID.eq(craBatchDetailRecord.get(0).get(CRA_BATCH_DETAIL.ENTERPRISE_CCC)))
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
	
	public static byte[] getDownloadMainCRA(String domainName, String _craBatchId) {
		Connection connection = null;
		
		try {
			connection = AonServletUtils.getConnection(domainName);
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			Record craBatchRecord = dslContext.select().from(CRA_BATCH)
					.where(CRA_BATCH.ID.eq(Integer.parseInt(_craBatchId)))
					.fetchOne();
			
			byte[] data = craBatchRecord.get(CRA_BATCH.OUTCOME_FILE);
			return data;
			
		}catch (SQLException e) {
			throw new RuntimeException(e);
		} 
	}

	public static String deleteMainCRA(Integer _craBatchId, Connection connection) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());

		dslContext.delete(CRA_BATCH_DETAIL)
			.where(CRA_BATCH_DETAIL.CRA_BATCH.eq(_craBatchId))
			.execute();
		
		dslContext.delete(CRA_BATCH)
			.where(CRA_BATCH.ID.eq(_craBatchId))
			.execute();
		
		return null;
	}

	public static String setMainCra(String domainName, String _cccId, String agrarianAFI, long _startDate, String craDocumentType, Connection connection) {
		DSLContext dslContext = DSL.using(connection, getDefaultSettings());
		
		java.util.Date startDate = new java.util.Date(_startDate);
		
		//GET AuthKey from DB
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.NAME.eq(domainName))
				.fetchOne();
		
		Integer _domainId = domainRecord.get(DOMAIN.ID);
		
		// RECTIFICATIVO
		if (craDocumentType.equals("R")) {
			Result<Record> craBatchRecords = dslContext.select().from(CRA_BATCH)
					.where(CRA_BATCH.ID.in(
							dslContext.select(CRA_BATCH_DETAIL.CRA_BATCH).from(CRA_BATCH_DETAIL)
								.where(CRA_BATCH_DETAIL.ENTERPRISE_CCC.eq(Integer.parseInt(_cccId)))
					)).and(CRA_BATCH.DOMAIN.eq(_domainId))
					.and(CRA_BATCH.DATE.eq(new Timestamp(startDate.getTime())))
					.fetch();
			
			byte[] data = null;
			ArrayList<Integer> oldCraBatchIds = new ArrayList<Integer>();
			for(Record r: craBatchRecords) {
				if(r.get(CRA_BATCH.COMMUNICATION_ID).equals("N")) {
					data = r.get(CRA_BATCH.OUTCOME_FILE);
					oldCraBatchIds.add(r.get(CRA_BATCH.ID));
				}else
					oldCraBatchIds.add(r.get(CRA_BATCH.ID));
			}
			
//				Integer oldCraBatchId = craBatchRecord.get(CRA_BATCH.ID);
//				byte[] data = craBatchRecord.get(CRA_BATCH.OUTCOME_FILE);
			
			String dataStr = new String(data);
			System.out.println();
			System.out.println("Lenght DataStr : " + dataStr.length());
			System.out.println(dataStr);
			System.out.println();
			
			String resultStr = "";
			String subStringAnalize = "";
			for(int i=0; i<dataStr.length(); i+=72) {
				subStringAnalize = dataStr.substring(i, i + 72);
				if(subStringAnalize.contains("CRE")) {
					String subStringAnalize1 = subStringAnalize.substring(0, 17);
					String deleteString = "B";
					String subStringAnalize2 = subStringAnalize.substring(18, 72);
					
					subStringAnalize = subStringAnalize1 + deleteString + subStringAnalize2;
				}
				resultStr += subStringAnalize;
			}
			System.out.println("Lenght ResultStr : " + resultStr.length());
			System.out.println(resultStr);
			String newETI = agrarianAFI.substring(0, 72);
			resultStr += parseCRAToRectificative(agrarianAFI);
			String newCRA = newETI + resultStr.substring(72, resultStr.length());
			System.out.println("RESULTADO FINAL");
			System.out.println(newCRA);
			
			CraBatchRecord rectificativeCRABatchRecord = dslContext.insertInto(CRA_BATCH)
					.set(CRA_BATCH.DOMAIN, _domainId)
					.set(CRA_BATCH.DATE, new Timestamp(startDate.getTime()))
					.set(CRA_BATCH.STATUS, (byte)1)
					.set(CRA_BATCH.COMMUNICATION_ID, craDocumentType)
					.set(CRA_BATCH.INCOME_FILE, (byte[])null)
					.set(CRA_BATCH.OUTCOME_FILE, newCRA.getBytes())
					.set(CRA_BATCH.OUTCOME_FILE_DATE, new Timestamp(startDate.getTime()))
					.returning(CRA_BATCH.ID)
					.fetchOne();
				
			Integer craBatchId = rectificativeCRABatchRecord.getId();
			
			dslContext.insertInto(CRA_BATCH_DETAIL)
				.set(CRA_BATCH_DETAIL.DOMAIN, _domainId)
				.set(CRA_BATCH_DETAIL.CRA_BATCH, craBatchId)
				.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, Integer.parseInt(_cccId))
				.execute();
			
			// DELETE OLD CRA
			dslContext.delete(CRA_BATCH_DETAIL).where(CRA_BATCH_DETAIL.CRA_BATCH.in(oldCraBatchIds)).execute();
			dslContext.delete(CRA_BATCH).where(CRA_BATCH.ID.in(oldCraBatchIds)).execute();
		}
		
		CraBatchRecord craBatchRecord = dslContext.insertInto(CRA_BATCH)
			.set(CRA_BATCH.DOMAIN, _domainId)
			.set(CRA_BATCH.DATE, new Timestamp(startDate.getTime()))
			.set(CRA_BATCH.STATUS, (byte)1)
			.set(CRA_BATCH.COMMUNICATION_ID, "N")
			.set(CRA_BATCH.INCOME_FILE, (byte[])null)
			.set(CRA_BATCH.OUTCOME_FILE, agrarianAFI.getBytes())
			.set(CRA_BATCH.OUTCOME_FILE_DATE, new Timestamp(startDate.getTime()))
			.returning(CRA_BATCH.ID)
			.fetchOne();
		
		Integer craBatchId = craBatchRecord.getId();
		
		dslContext.insertInto(CRA_BATCH_DETAIL)
			.set(CRA_BATCH_DETAIL.DOMAIN, _domainId)
			.set(CRA_BATCH_DETAIL.CRA_BATCH, craBatchId)
			.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, Integer.parseInt(_cccId))
			.execute();
		
		return null;
		
	}

	private static String parseCRAToRectificative(String cra) {
		String resultStr = "";
		String subStringAnalize = "";
		for(int i=0; i<cra.length(); i+=72) {
			subStringAnalize = cra.substring(i, i + 72);
			if(subStringAnalize.contains("ETI"))
				continue;
			else
				resultStr += subStringAnalize;
		}
		return resultStr;
	}
	
//	public static String setMainCra(String _domainId, String domainName, String _cccId, String agrarianAFI, long _startDate, String craDocumentType) {
//		AONContext context = null;
//		
//		java.util.Date startDate = new java.util.Date(_startDate);
//		
//		try {
//			DSLContext dslContext = AONContext.getAONContext(domainName, Integer.parseInt(_domainId),
//					AonServletUtils.getLoggedUser()).getDslContext();
//			
//			// RECTIFICATIVO
//			if (craDocumentType.equals("R")) {
//				Record craBatchRecord = dslContext.select().from(CRA_BATCH)
//						.where(CRA_BATCH.ID.in(
//								dslContext.select(CRA_BATCH_DETAIL.CRA_BATCH).from(CRA_BATCH_DETAIL)
//									.where(CRA_BATCH_DETAIL.ENTERPRISE_CCC.eq(Integer.parseInt(_cccId)))
//						)).and(CRA_BATCH.DOMAIN.eq(Integer.parseInt(_domainId)))
//						.and(CRA_BATCH.DATE.eq(new Timestamp(startDate.getTime())))
//						.fetchOne();
//				
//				Integer oldCraBatchId = craBatchRecord.get(CRA_BATCH.ID);
//				byte[] data = craBatchRecord.get(CRA_BATCH.OUTCOME_FILE);
//				
//				String dataStr = new String(data);
//				System.out.println();
//				System.out.println("Lenght DataStr : " + dataStr.length());
//				System.out.println(dataStr);
//				System.out.println();
//				
//				String resultStr = "";
//				String subStringAnalize = "";
//				for(int i=0; i<dataStr.length(); i+=72) {
//					subStringAnalize = dataStr.substring(i, i + 72);
//					if(subStringAnalize.contains("CRE")) {
//						String subStringAnalize1 = subStringAnalize.substring(0, 17);
//						String deleteString = "B";
//						String subStringAnalize2 = subStringAnalize.substring(18, 72);
//						
//						subStringAnalize = subStringAnalize1 + deleteString + subStringAnalize2;
//					}
//					resultStr += subStringAnalize;
//				}
//				System.out.println("Lenght ResultStr : " + resultStr.length());
//				System.out.println(resultStr);
//				
//				CraBatchRecord rectificativeCRABatchRecord = dslContext.insertInto(CRA_BATCH)
//						.set(CRA_BATCH.DOMAIN, Integer.parseInt(_domainId))
//						.set(CRA_BATCH.DATE, new Timestamp(startDate.getTime()))
//						.set(CRA_BATCH.STATUS, (byte)1)
//						.set(CRA_BATCH.COMMUNICATION_ID, craDocumentType)
//						.set(CRA_BATCH.INCOME_FILE, (byte[])null)
//						.set(CRA_BATCH.OUTCOME_FILE, resultStr.getBytes())
//						.set(CRA_BATCH.OUTCOME_FILE_DATE, new Timestamp(startDate.getTime()))
//						.returning(CRA_BATCH.ID)
//						.fetchOne();
//					
//				Integer craBatchId = rectificativeCRABatchRecord.getId();
//				
//				dslContext.insertInto(CRA_BATCH_DETAIL)
//					.set(CRA_BATCH_DETAIL.DOMAIN, Integer.parseInt(_domainId))
//					.set(CRA_BATCH_DETAIL.CRA_BATCH, craBatchId)
//					.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, Integer.parseInt(_cccId))
//					.execute();
//				
//				// DELETE OLD CRA
//				dslContext.delete(CRA_BATCH_DETAIL).where(CRA_BATCH_DETAIL.CRA_BATCH.eq(oldCraBatchId)).execute();
//				dslContext.delete(CRA_BATCH).where(CRA_BATCH.ID.eq(oldCraBatchId)).execute();
//			}
//			
//			CraBatchRecord craBatchRecord = dslContext.insertInto(CRA_BATCH)
//				.set(CRA_BATCH.DOMAIN, Integer.parseInt(_domainId))
//				.set(CRA_BATCH.DATE, new Timestamp(startDate.getTime()))
//				.set(CRA_BATCH.STATUS, (byte)1)
//				.set(CRA_BATCH.COMMUNICATION_ID, "N")
//				.set(CRA_BATCH.INCOME_FILE, (byte[])null)
//				.set(CRA_BATCH.OUTCOME_FILE, agrarianAFI.getBytes())
//				.set(CRA_BATCH.OUTCOME_FILE_DATE, new Timestamp(startDate.getTime()))
//				.returning(CRA_BATCH.ID)
//				.fetchOne();
//			
//			Integer craBatchId = craBatchRecord.getId();
//			
//			dslContext.insertInto(CRA_BATCH_DETAIL)
//				.set(CRA_BATCH_DETAIL.DOMAIN, Integer.parseInt(_domainId))
//				.set(CRA_BATCH_DETAIL.CRA_BATCH, craBatchId)
//				.set(CRA_BATCH_DETAIL.ENTERPRISE_CCC, Integer.parseInt(_cccId))
//				.execute();
//			
//		}finally {
//			if (context != null)
//				context.close();
//		}
//		
//		return null;
//		
//	}
}
