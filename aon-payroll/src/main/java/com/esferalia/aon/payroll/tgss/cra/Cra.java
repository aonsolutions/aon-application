package com.esferalia.aon.payroll.tgss.cra;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Cra {
	
	// ********************************************************************************************************************************************
	//													CHECK EXIST PAYROLL
	// ********************************************************************************************************************************************
	
	public static boolean existAnySalary(List<String> cccList, long findingDate, Connection connection)  {
		DSLContext dslContext = new AONContext(connection).getDslContext();
		
		// Given findingDate set start and end date
		Calendar startDate = Calendar.getInstance();
		startDate.setTimeInMillis(findingDate);
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(findingDate);
		endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Date startDateSQL = new Date(startDate.getTimeInMillis());
		Date endDateSQL = new Date(endDate.getTimeInMillis());
		
		for(String ccc : cccList) {
			Record salaryRecord = dslContext.select().from(SALARY)
				.where(
						(SALARY.START_DATE.ge(startDateSQL).and(SALARY.END_DATE.le(endDateSQL))) // Nomina
						.or(SALARY.CHARGE_DATE.between(startDateSQL, endDateSQL)) // Finiquito
						.or(SALARY.ISSUE_DATE.between(startDateSQL, endDateSQL).or(SALARY.END_DATE.between(startDateSQL, endDateSQL))) // Atraso
				)
				.and(SALARY.CCC.eq(ccc))
				.and(
						SALARY.TYPE.eq((byte)0) // Nomina
						.or(SALARY.TYPE.eq((byte)2)) // Finiquito
						.or(SALARY.TYPE.eq((byte)3)) // Atraso
				)
				.and(SALARY.SS_REGIME.notEqual((byte)3))
				.and(SALARY.TOTAL_PAYMENT.gt(0.00))
				.limit(1)
				.fetchOne();
			
			return null != salaryRecord;
		}
		
		return false;
	}
	
	// ********************************************************************************************************************************************
	//													GENERATE JSON AGRARIAN
	// ********************************************************************************************************************************************
	
	@SuppressWarnings("unchecked")
	public static JSONObject getMainCRAByCRA(Integer domainId, Integer parentDomainId, Integer userId, List<String> cccList, long findingDate, String fileName, Connection connection) throws IllegalArgumentException {
		
		// Get dslContext for given connection
		DSLContext dslContext = new AONContext(connection).getDslContext();
		
		// Given findingDate set start and end date
		Calendar startDate = Calendar.getInstance();
		startDate.setTimeInMillis(findingDate);
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(findingDate);
		endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Date startDateSQL = new Date(startDate.getTimeInMillis());
		Date endDateSQL = new Date(endDate.getTimeInMillis());
		
		// Create JSONObject mainCra
		JSONObject mainCRAJSON = new JSONObject();
			
		// GET AuthKey from DB
		String authKey = getAuthKeyFromDomain(dslContext, domainId, parentDomainId);
		
		// ETI
		JSONObject eti = new JSONObject();
		eti.put("authkey", authKey);
		eti.put("fileName", fileName);
		eti.put("prorityCode", "N");
		
		mainCRAJSON.put("ETI", eti);
		
		// Prepare JSON CCCs
		JSONArray jsonCCCs = new JSONArray();
		
		// Domain Childs
		List<Integer> domainChilds = getDomainChilds(dslContext, domainId, userId); 
		
		cccList.forEach(ccc -> {
		
			// Prepare ERRORS
			JSONArray errors = new JSONArray();
			
			// Prepare CCCi
			JSONObject ccci = new JSONObject();
			
			// ----------- NOMINAS
			Result<Record> salaryRecords = dslContext.select().from(SALARY)
					.join(ENTERPRISE_CCC)
					.on(ENTERPRISE_CCC.CCC.eq(SALARY.CCC))
					.join(CONTRACT)
					.on(CONTRACT.ID.eq(SALARY.CONTRACT))
					.join(PERSON)
					.on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
					.where(SALARY.START_DATE.ge(startDateSQL))
					.and(SALARY.END_DATE.le(endDateSQL))
					.and(SALARY.CCC.eq(ccc))
					.and(SALARY.TYPE.eq((byte)0))
					.and(SALARY.SS_REGIME.notEqual((byte)3))
					.and(SALARY.TOTAL_PAYMENT.gt(0.00))
					.and(SALARY.DOMAIN.in(domainChilds))
					.fetch();
			
			if(salaryRecords.isEmpty()){
				
				JSONObject err = new JSONObject();
				err.put("ERR", "No hay ninguna nómina emitida para este periodo.");
				errors.add(err);
			
			} else {
				
				// Prepare DDE
				JSONObject dde = new JSONObject();
	
				dde.put("cccRegime", parseSSRegime(salaryRecords.get(0).get(ENTERPRISE_CCC.TYPE)));
				dde.put("ccc", ccc);
				dde.put("year", startDate.get(Calendar.YEAR));
				dde.put("month", startDate.get(Calendar.MONTH)+1);
				
				// Prepare TRBS
				JSONArray trbs = new JSONArray();
				
				salaryRecords.forEach(salaryRecord -> {
					// Prepare TRB
					JSONObject trb = new JSONObject();
					
					String ssn = salaryRecord.get(PERSON.SOCIAL_SECURITY_NUM);
					if(!checkSS(ssn)) throw new IllegalArgumentException("El trabajador " + salaryRecord.get(SALARY.EMPLOYEE_NAME) + " de la empresa " + salaryRecord.get(SALARY.ENTERPRISE_NAME) + " tiene un numero de la Seguridad Social err\u00f3neo");
					trb.put("numAfilicion", ssn);
					
					// Prepare CRES
					JSONArray cres = getCREsForSalaries(dslContext, salaryRecord);
					
					// If having CRES add to TRB and TRB to TRBS
					if(!cres.isEmpty()) {
						trb.put("CRES", cres);
						trbs.add(trb);
					}
					
				});
				
				// Adding TRBS to DDE
				dde.put("TRBS", trbs);
				
				// Adding DDE (Normal salaries) to MainCRAJSON 
				ccci.put("DDE", dde);
			}
			
			// ----------- ATRASOS
			salaryRecords = dslContext.select().from(SALARY)
					.join(ENTERPRISE_CCC)
					.on(ENTERPRISE_CCC.CCC.eq(SALARY.CCC))
					.join(CONTRACT)
					.on(CONTRACT.ID.eq(SALARY.CONTRACT))
					.join(PERSON)
					.on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
					.where(SALARY.CHARGE_DATE.between(startDateSQL, endDateSQL))
					.and(SALARY.CCC.eq(ccc))
					.and(SALARY.TYPE.eq((byte)3))
					.and(SALARY.SS_REGIME.notEqual((byte)3))
					.and(SALARY.DOMAIN.in(domainChilds))
					.fetch();
			
			if(salaryRecords.isEmpty()){
				
				JSONObject err = new JSONObject();
				err.put("ERR1", "No hay ningun atraso emitido para este periodo.");
				errors.add(err);
			
			} else {
				// Prepare DDEAS
				JSONArray ddeas = new JSONArray();
				
				salaryRecords.forEach(salaryRecord -> {
					// Get salaryId
					Integer salaryId = salaryRecord.get(SALARY.ID);
					
					// Get salaryPayment type of salary
					Byte salaryPaymentType = dslContext.select(SALARY_PAYMENT.TYPE).from(SALARY_PAYMENT)
							.where(SALARY_PAYMENT.SALARY.eq(salaryId))
							.limit(1)
							.fetchOne()
							.getValue(SALARY_PAYMENT.TYPE);
					
					// Get salaryData of salary
					Result<Record> salaryDatas = dslContext.select().from(SALARY_DATA)
							.where(SALARY_DATA.SALARY.eq(salaryId))
							.and(SALARY_DATA.NAME.eq("BASE_CGC"))
							.fetch();
					
					// SSNumber
					String ssn = salaryRecord.get(PERSON.SOCIAL_SECURITY_NUM);
					if(!checkSS(ssn)) throw new IllegalArgumentException("El trabajador " + salaryRecord.get(SALARY.EMPLOYEE_NAME) + " de la empresa " + salaryRecord.get(SALARY.ENTERPRISE_NAME) + " tiene un numero de la Seguridad Social err\u00f3neo");
					
					for(Record salaryData: salaryDatas){
						
						// Get craAmount
						Double craAmount = Double.parseDouble(salaryData.get(SALARY_DATA.EXPRESSION));
						
						if(craAmount > 0 && salaryData.get(SALARY_DATA.START_DATE).before(endDateSQL)){
								
							// Prepare DDEA
							JSONObject ddea = new JSONObject();
							
							ddea.put("cccRegime", parseSSRegime(salaryRecord.get(ENTERPRISE_CCC.TYPE)));
							ddea.put("ccc", ccc);
							
							// Instance Calendar with actual iteration salary_data startDate
							Calendar salaryDataStartDate = Calendar.getInstance();
							salaryDataStartDate.setTimeInMillis(salaryData.get(SALARY_DATA.START_DATE).getTime());
							
							ddea.put("year", salaryDataStartDate.get(Calendar.YEAR));
							ddea.put("month", salaryDataStartDate.get(Calendar.MONTH) + 1);
						
							// Prepare TRBSA
							JSONArray trbsa = new JSONArray();
							
							// Prepare TRBA
							JSONObject trba = new JSONObject();
						
							trba.put("numAfilicion", ssn);
						
							// Prepare CRES
							JSONArray cres = new JSONArray();
							
							// Get CRA type -> Same CRA type for all salaryData of a Salary
							PaymentType typeCRA = PaymentType.values()[salaryPaymentType];
							
							// Try to add Cre to Cres
							addCreToCres(craAmount, typeCRA, cres);
							
							// Add CRES to TRBA
							trba.put("CRES", cres);
							
							// Add TRBA to TRBAS
							trbsa.add(trba);
							
							// Add TRBAS to DDEA
							ddea.put("TRBS", trbsa);
							
							// Add DDEA to DDEAS
							ddeas.add(ddea);
							
						}
					}
				});
				
				// Adding DDEAS (Delay salaries) to MainCRAJSON, but first parseDDEAS to accumulate amount of same craType 
				ccci.put("DDEAS", parseDDEAS(ddeas));
			}
			
			// ----------- FINIQUITOS
			salaryRecords = dslContext.select().from(SALARY)
					.join(ENTERPRISE_CCC)
					.on(ENTERPRISE_CCC.CCC.eq(SALARY.CCC))
					.join(CONTRACT)
					.on(CONTRACT.ID.eq(SALARY.CONTRACT))
					.join(PERSON)
					.on(PERSON.REGISTRY.eq(CONTRACT.PERSON))
					.where(SALARY.CCC.eq(ccc))
					.and(SALARY.ISSUE_DATE.between(startDateSQL, endDateSQL))
					.and(SALARY.TYPE.eq((byte)2))
					.and(SALARY.SS_REGIME.notEqual((byte)3))
					.and(SALARY.TOTAL_PAYMENT.gt(0.00))
					.and(SALARY.DOMAIN.in(domainChilds))
					.fetch();
			
			if(salaryRecords.isEmpty()){
				
				JSONObject err = new JSONObject();
				err.put("ERR2", "No hay ningun finiquito emitido para este periodo.");
				errors.add(err);
			
			} else {
				// Prepare FINIQ
				JSONObject finiq = new JSONObject();
				
				finiq.put("cccRegime", parseSSRegime(salaryRecords.get(0).get(ENTERPRISE_CCC.TYPE)));
				finiq.put("ccc", ccc);
				finiq.put("year", startDate.get(Calendar.YEAR));
				finiq.put("month", startDate.get(Calendar.MONTH)+1);
				
				// Prepare TRBSF
				JSONArray trbsf = new JSONArray();
				
				for(Record salaryRecord: salaryRecords) {
					
					// SSNumber
					String ssn = salaryRecord.get(PERSON.SOCIAL_SECURITY_NUM);
					if(!checkSS(ssn)) throw new IllegalArgumentException("El trabajador " + salaryRecord.get(SALARY.EMPLOYEE_NAME) + " de la empresa " + salaryRecord.get(SALARY.ENTERPRISE_NAME) + " tiene un numero de la Seguridad Social err\u00f3neo");
					
					
					// Get salaryId
					Integer salaryId = salaryRecord.get(SALARY.ID);
					Date issueDate = salaryRecord.get(SALARY.ISSUE_DATE);
					
					// Get salaryDatas of Settelment salary
					Result<Record> salaryDatas = dslContext.select().from(SALARY_DATA)
							.where(SALARY_DATA.SALARY.eq(salaryId))
							.and(SALARY_DATA.NAME.eq("BASE_CGC"))
							.fetch();
					
					// Prepare TRBF
					JSONObject trbf = new JSONObject();
					
					trbf.put("numAfilicion", ssn);
					
					// Prepare CRES
					JSONArray cres = new JSONArray();
					
					Double craAmount = 0.00;
					
					for(Record salatyData: salaryDatas){
						
						// Get CRA amount
						craAmount += Double.parseDouble(salatyData.get(SALARY_PAYMENT.EXPRESSION));
						
					}
					
					if(craAmount > 0){
						
						// Get typeCRA ¿always 6?
						PaymentType typeCRA = PaymentType.values()[6];
						
						// Try to add Cre to Cres
						addCreToCres(craAmount, typeCRA, cres);
						
					}
					
					if((issueDate.after(startDateSQL) || issueDate.equals(startDateSQL)) && (issueDate.before(endDateSQL) || issueDate.equals(endDateSQL))) {
					
						Result<Record> salaryPayments = dslContext.select().from(SALARY_PAYMENT)
								.where(SALARY_PAYMENT.SALARY.eq(salaryId))
								.and(SALARY_PAYMENT.TYPE.ne((byte)6))
								.fetch();
						
						for(Record salaryPayment : salaryPayments) {
							PaymentType typeCRA = PaymentType.values()[salaryPayment.get(SALARY_PAYMENT.TYPE)];
							
							if(typeCRA.equals(PaymentType.CRA_0000))
								continue;
							
							Double amount = salaryPayment.get(SALARY_PAYMENT.AMOUNT);
							
							if(null != amount && 0.0 != amount) {
								// Try to add Cre to Cres
								addCreToCres(amount, typeCRA, cres);
							}
						}
					}
					
					if(!cres.isEmpty()) {
						// Add CRES to TRBF
						trbf.put("CRES", cres);
						
						// Add TRBF to TRBSF
						trbsf.add(trbf);
					}
					
				}
				
				if(!trbsf.isEmpty()){
					// Add TRBSF to FINIQ
					finiq.put("TRBS", trbsf);
					
					// Adding FINIQ (Settelment salaries) to MainCRAJSON  
					ccci.put("FINIQ", finiq);
				}
			}
			
			// Adding ERRORS to MainCRAJSON  
			ccci.put("ERRS", errors);
			
			jsonCCCs.add(ccci);
		});
		
		mainCRAJSON.put("CCCs", jsonCCCs);
		
		System.out.println(mainCRAJSON);
		
		return mainCRAJSON;
		
	}
	
	// ********************************************************************************************************************************************
	//													AUXILIAR METHODS
	// ********************************************************************************************************************************************
	
	private static JSONArray getCREsForSalaries(DSLContext dslContext, Record salaryRecord) {
		// Prepare CRES
		JSONArray cres = new JSONArray();
		
		// GET Salaries_Payment from Salary to get CRA type 
		Result<Record> salaryPaymentRecords = dslContext.select().from(SALARY_PAYMENT)
				.where(SALARY_PAYMENT.SALARY.eq(salaryRecord.get(SALARY.ID)))
				.orderBy(SALARY_PAYMENT.TYPE)
				.fetch();
		
		// Get first typeCRA to compare with to accumulate, first iteration will be true always
		Byte firstType = salaryPaymentRecords.isEmpty() ? null : salaryPaymentRecords.get(0).get(SALARY_PAYMENT.TYPE);
		PaymentType typeCRA = (salaryPaymentRecords.isEmpty() || null == firstType) ? null : PaymentType.values()[firstType];
		
		// Initialice craAmount for accumulation
		Double craAmount = 0.00;
		
		for(int i=0; i < salaryPaymentRecords.size(); i ++) {
		
			// FIx CRA_000
			PaymentType craType = null;
			
			Byte paymentType = salaryPaymentRecords.get(i).get(SALARY_PAYMENT.TYPE);
			String paymentConcept = salaryPaymentRecords.get(i).get(SALARY_PAYMENT.PAYMENT_CONCEPT);
			
			Double amount = salaryPaymentRecords.get(i).get(SALARY_PAYMENT.AMOUNT);
			Double quote = salaryPaymentRecords.get(i).get(SALARY_PAYMENT.QUOTE);
			
			if(null == paymentType || (paymentType == 0 && AonStringUtils.equalsIgnoreCase(paymentConcept, "MEJORA")))
				continue;
			if(paymentType == 0)
				craType = PaymentType.values()[1];
			else
				craType = PaymentType.values()[paymentType];
			
			if(typeCRA == craType) {
				
				// Accumulate craAmount (important if exists amount)
				// Mirar amount por que en quote a veces está la base y eso es mucho mas que la percepcion en si
				craAmount += amount > 0 ? amount : quote;
				
				// Last iteration
				if(i+1 == salaryPaymentRecords.size()) {
					
					// Try to add Cre to Cres
					addCreToCres(craAmount, craType, cres);
					
				}
				
			} else {
				
				// Try to add Cre to Cres
				addCreToCres(craAmount, typeCRA, cres);
				
				// Update typeCra
				typeCRA = craType;
				
				// Update craAmount
				craAmount = quote > 0 ? quote : amount;
				
				// Last iteration
				if(i+1 == salaryPaymentRecords.size()) {
					
					// Try to add Cre to Cres
					addCreToCres(craAmount, craType, cres);
				}
			}
		}
		
		return cres;
	}

	private static List<Integer> getDomainChilds(DSLContext dslContext, Integer domainId, Integer userId) {
		return dslContext.select(DOMAIN.ID).from(DOMAIN)
				.where(DOMAIN.ID.eq(domainId).or(DOMAIN.PARENT.eq(domainId)))
				.and(DOMAIN.SCOPE.in(
						dslContext.select(USER_SCOPE.SCOPE).from(USER_SCOPE)
							.where(USER_SCOPE.USER_ID.eq(userId))
							.fetch(USER_SCOPE.SCOPE))
				.or(DOMAIN.SCOPE.isNull()))
				.fetch(DOMAIN.ID);
	}

	private static boolean checkSS(String socialSecurity){
		if(AonStringUtils.isBlank(socialSecurity) || socialSecurity.length() != 12) return false;
		
		String provinceCode = socialSecurity.substring(0, 2);;
		String ssNumberWithoutCode = socialSecurity.substring(2, 10);
		String controlCode = socialSecurity.substring(10, 12);;
		
		try {
			long ssNumber = Long.parseLong(ssNumberWithoutCode);
			long province = Long.parseLong(provinceCode);
			
			if (ssNumber < 10000000) ssNumber = ssNumber + province * 10000000;
			else ssNumber = Long.parseLong(provinceCode + ssNumberWithoutCode);
				
			long calculateControlCode = ssNumber % 97;
			String calculateControlCodeStr = String.valueOf(calculateControlCode);
			
			if(calculateControlCodeStr.length() == 1)
				calculateControlCodeStr =  AonStringUtils.leftPad(calculateControlCodeStr, 2, '0');
			
			return AonStringUtils.equalsIgnoreCase(controlCode, calculateControlCodeStr);
		} catch (Exception e) {
			return false;
		}
	}

	private static String getAuthKeyFromDomain(DSLContext dslContext, Integer domainId, Integer parentDomainId) {
		Record appParamRecord = dslContext.select().from(APP_PARAM)
				.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY"))
				.and(APP_PARAM.DOMAIN.eq(domainId))
				.fetchOne();
		
		if(null != appParamRecord && AonStringUtils.isNotBlank(appParamRecord.get(APP_PARAM.VALUE)))
			return appParamRecord.get(APP_PARAM.VALUE);
		else {
			appParamRecord = dslContext.select().from(APP_PARAM)
					.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY"))
					.and(APP_PARAM.DOMAIN.eq(parentDomainId))
					.fetchOne();
			
			return null != appParamRecord && AonStringUtils.isNotBlank(appParamRecord.get(APP_PARAM.VALUE)) ? appParamRecord.get(APP_PARAM.VALUE) : "00000";
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void addCreToCres(Double craAmount, PaymentType craType, JSONArray cres) {
		// Prepare CRE
		JSONObject cre = new JSONObject();
		
		// Prepare craAmountStr
		String craAmountStr = String.format( "%.2f", craAmount );
		String amount = "";
		
		// (String) craAmountStr = xx,yy || xx.yy -> amount = xxyy
		if(craAmountStr.contains(","))
			amount = craAmountStr.split(",")[0] + craAmountStr.split(",")[1];
		else
			amount = craAmountStr.split("[.]")[0] + craAmountStr.split("[.]")[1];
		
		// Add CRE to CRES if craAmount > 0
		if(craAmount > 0.001){
			String includeExclude = "I";
			
			if(craType.isBBCCIncluded() && craType.isBBCCExcluded())
				includeExclude = "E";
			else if(craType.isBBCCIncluded())
				includeExclude = "I";
			else if(craType.isBBCCExcluded())
				includeExclude = "E";
			
			cre.put("concept", getCRADescription(craType));
			cre.put("include_exclude", includeExclude);
			cre.put("amount", amount);
			cre.put("action", " ");
			cres.add(cre);
		}
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static JSONArray parseDDEAS(JSONArray ddeas) {
		
		// Prepare returning JSONArray
		JSONArray result = new JSONArray();
		
		// ArrayList of date that already visisted
		ArrayList<Date> visitedDates = new ArrayList<>();
		
		// Prepare DDEA
		JSONObject ddea = new JSONObject();
		
		for(int i = 0; i<ddeas.size(); i++){
			
			// DDEA iteration object
			JSONObject ddeaFirst = (JSONObject) ddeas.get(i);
			
			String month = ddeaFirst.get("month").toString();
			String year = ddeaFirst.get("year").toString();
			
			// Date to iterate
			Date date = new Date(Integer.parseInt(year) - 1900, Integer.parseInt(month) - 1, 1);
			
			if(!visitedDates.contains(date)){
				
				// Initialice DDEA
				ddea.put("cccRegime", ddeaFirst.get("cccRegime"));
				ddea.put("ccc", ddeaFirst.get("ccc"));
				ddea.put("month", ddeaFirst.get("month"));
				ddea.put("year", ddeaFirst.get("year"));
				
				// Prepare new TRBS
				JSONArray trbs = new JSONArray();
				
				// Get TRB from DDEAFirst
				JSONObject trb = (JSONObject) ((JSONArray) ddeaFirst.get("TRBS")).get(0);
				
				// Add TRB to new TROBS
				trbs.add(trb);
				
				// Get SS Number to compare in order to accumulate amount
				String ssNum = trb.get("numAfilicion").toString();
				
				// Iterate DDEAS for trying to accumulate amount
				for(int j = 1; j<ddeas.size(); j++){
					
					// Prepare DDEA auxiliar to compare whith the iterable one
					JSONObject ddeaAux = (JSONObject) ddeas.get(j);
					
					if( month.equals(ddeaAux.get("month").toString()) && 
						year.equals(ddeaAux.get("year").toString()) && 
						!visitedDates.contains(date)){
						
						// Get TRB auxiliar to work with
						JSONObject trbAux = (JSONObject) ((JSONArray) ddeaAux.get("TRBS")).get(0);
						
						// If TRB auxiliar has different ssNum of iterable one, its different employee so simple add
						// If they have the same SS Number we have to look if they have the same CRA type to accumulate
						// or if they are different just add too
						if(!ssNum.equals(trbAux.get("numAfilicion").toString())) {
							trbs.add(trbAux);
						} else {
							tryAddCRE(trb, ((JSONObject)((JSONArray)trbAux.get("CRES")).get(0)));
						}
					}
					
				}
				
				// Visit de date
				visitedDates.add(date);
				
				// Add TRBS to DDEA
				ddea.put("TRBS", trbs);
				
				// Add DDEA to result
				result.add(ddea);
				
				// Initialice DDEA for a new iteration
				ddea = new JSONObject();
			}	
		}
		
		return result;
	}

	@SuppressWarnings("unchecked")
	private static void tryAddCRE(JSONObject trb, JSONObject cre) {
		
		// Get CRES of the input TRB
		JSONArray cres = (JSONArray) trb.get("CRES");
		
		// Get amount and concept of the cre we are going to accumulate or add
		String creAmount = cre.get("amount").toString();
		String creConcept = cre.get("concept").toString();
		
		for(int i=0; i < cres.size(); i++) {
			
			// Get amount and concept of the cre we are going to compare with
			String creAmountAux = ((JSONObject)cres.get(i)).get("amount").toString();
			String creConceptAux = ((JSONObject)cres.get(i)).get("concept").toString();
			
			if(!creAmount.equalsIgnoreCase(creAmountAux)) {
				
				// If they have the same concept accumulate it, otherwise add CRE to CRES of TRB given
				if(creConcept.equals(creConceptAux)) {
					Integer totalAmount = Integer.parseInt(creAmount) + Integer.parseInt(creAmountAux);
					((JSONObject)cres.get(i)).put("amount", totalAmount.toString());
				} else
					((JSONArray) trb.get("CRES")).add(cre);
				
			}
		}
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
	
	private static String getCRADescription(PaymentType craType) {
		return craType.toString().split("_")[1];
	}
	
}
