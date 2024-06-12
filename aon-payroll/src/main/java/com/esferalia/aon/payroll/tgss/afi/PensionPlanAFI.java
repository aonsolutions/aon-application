package com.esferalia.aon.payroll.tgss.afi;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.EnterpriseData.ENTERPRISE_DATA;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.watson.util.AonStringUtils;

public class PensionPlanAFI {
	
	private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
	
	// ********************************************************************************************************************************************
	//													GENERATE JSON AGRARIAN
	// ********************************************************************************************************************************************

	@SuppressWarnings({ "unchecked" })
	public static JSONObject getPensionPlanInfo(Connection connection, ArrayList<Integer> cccIdList, java.util.Date date) throws SQLException {
		
		// Get dslContext for given connection
		AONContext ctx = new AONContext(connection);
		DSLContext dslContext = ctx.getDslContext();
		
		// Given findingDate set start and end date
		Calendar startDate = Calendar.getInstance();
		startDate.setTimeInMillis(date.getTime());
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(date.getTime());
		endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Date startDateSQL = new Date(startDate.getTimeInMillis());
		Date endDateSQL = new Date(endDate.getTimeInMillis());
		
		// Create JSONObject Agrarian JSON
		JSONObject agrarianJSON = new JSONObject();
		
		// GET AuthKey from DB
		String authKey = getAuthKeyFromDomain(dslContext, cccIdList.get(0));
		String enterpriseName = getEnterpriseName(dslContext, cccIdList.get(0));
		String entidadGestora = getSSMutualFromDomain(dslContext, cccIdList.get(0));
		entidadGestora = AonStringUtils.leftPad(entidadGestora, 5, '0');
		
		//ETI
		JSONObject eti = new JSONObject();
		eti.put("authkey", authKey);			
		eti.put("payrollProvider", "498");		//Proveedor de nominas ESFERALIA NETWORKS, S.A.
		eti.put("fileName", null);
		eti.put("prorityCode", "N");
		agrarianJSON.put("ETI", eti);
		
		JSONArray enterprises = new JSONArray();
		Integer cccUsed = 0;
		Integer contractsUsed = 0;
		
		for(Integer cccId: cccIdList) {
			
			Record enterpriseCCC = dslContext.select().from(ENTERPRISE_CCC)
					.join(GEOZONE).on(GEOZONE.ID.eq(ENTERPRISE_CCC.GEOZONE)).where(ENTERPRISE_CCC.ID.eq(cccId)).fetchOne();
			
			String cccRegimeCode = getCCCRegimeCode(enterpriseCCC.get(ENTERPRISE_CCC.TYPE));
			String ccc =  parseCCC(enterpriseCCC.get(ENTERPRISE_CCC.CCC));
			String cccProvince = enterpriseCCC == null ? "00" : enterpriseCCC.get(GEOZONE.CODE);
			
			Result<Record> contracts = dslContext.select().from(CONTRACT)
					.join(SALARY).on(SALARY.CONTRACT.eq(CONTRACT.ID))
					.join(SALARY_PAYMENT).on(SALARY_PAYMENT.SALARY.eq(SALARY.ID))
					.where(CONTRACT.ENTERPRISE_CCC.eq(cccId))
					.and(SALARY_PAYMENT.PAYMENT_CONCEPT.eq("PPE"))
					.and(SALARY.START_DATE.ge(startDateSQL))
					.and(SALARY.END_DATE.le(endDateSQL))
					.fetch();
			
			if(contracts.isEmpty())
				continue;
			
			// The ccc we have contracts on
			cccUsed ++;
			
			JSONObject enterprise = new JSONObject();
			
			//EMP
			JSONObject emp = new JSONObject();
			emp.put("regime", cccRegimeCode);
			emp.put("cccProvince", cccProvince);
			emp.put("ccc", ccc);
			emp.put("cccRegimePrincipal", "");
			emp.put("cccProvincePrincipal", "00");
			emp.put("cccPrincipal", "0000000000000");
			enterprise.put("EMP", emp);
			
			//RZS
			JSONObject rzsData = new JSONObject();
			rzsData.put("businessmanType", "2");
			rzsData.put("rzsName", removeAccents(enterpriseName));
			enterprise.put("RZS", rzsData);
			
			//EMPLOYEES
			JSONArray emps = new JSONArray();
			
			contractsUsed += contracts.size();
			
			for(Record contract : contracts) {
				JSONObject empl = new JSONObject();
				JSONObject tra = getTRA(contract.get(CONTRACT.ID), dslContext);
				empl.put("TRA", tra);
				JSONObject ayn = getAYN(contract.get(CONTRACT.ID), dslContext);
				empl.put("AYN", ayn);
				//Para el caso de las jornadas agrarias esto es constante
				final String fab = "FABASA000000000000 00000  000000 0000000000000000 0000     0000       ";
				empl.put("FAB", fab);
				
				Double amount = contract.get(SALARY_PAYMENT.QUOTE);
				amount = roundToTwoDecimals(amount);
				final String odl = "ODL00000000000000      0000      " + AonStringUtils.leftPad(amount.toString().split("\\.")[0], 4, '0') + AonStringUtils.rightPad(AonStringUtils.substring(amount.toString().split("\\.")[1], 0, 2), 2, '0')  + entidadGestora + "000                       ";
				empl.put("ODL", odl);
				
				final String fct = "FCT43900000000000" + DATE_FORMAT.format(startDateSQL) + DATE_FORMAT.format(endDateSQL) + "0000000000000000                000  ";
				empl.put("FCT", fct);
				
				emps.add(empl);
			}
			
			enterprise.put("EMPS", emps);
			
			enterprises.add(enterprise);
			
		}
		
		//ENTERPRISES
		agrarianJSON.put("ACTIVITIES", enterprises);
		
		//ETF
		JSONObject etf = new JSONObject();
		etf.put("authkey", authKey);
		etf.put("payrollProvider", "498");
		etf.put("fileName", null);
		etf.put("priorityCode", "N");
		agrarianJSON.put("ETF", etf);
		
		//CONFIG
		JSONObject conf = new JSONObject();
		conf.put("staticLines", "2");
		conf.put("activityLines", "2");
		conf.put("employeeLines", "5");
		conf.put("numEmployees", contractsUsed+"");
		conf.put("numCCCs", cccUsed+"");
		agrarianJSON.put("CONF", conf);

		System.out.println(agrarianJSON);
		return agrarianJSON;
	}
	
	public static double roundToTwoDecimals(Double value) {
		if(null == value) return 0.00;
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
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
	
	private static String parseCCC(String ccc) {
		if(ccc.length() == 11) {
			return ccc.substring(2);
		}else
			return ccc;
		
	}
	
	private static String getEnterpriseName(DSLContext dslContext, Integer cccId) {
		Record registryRecord = dslContext.select().from(REGISTRY)
			.where(REGISTRY.ID.in(
				dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY)
					.where(ENTERPRISE_ACTIVITY.ID.in(
						dslContext.select(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY).from(ENTERPRISE_CCC)
							.where(ENTERPRISE_CCC.ID.eq(cccId))
					))
			)).fetchOne();
		
		return null == registryRecord ? null : registryRecord.get(REGISTRY.NAME);
	}

	private static String getAuthKeyFromDomain(DSLContext dslContext, Integer cccId) {
		
		// Prepare aunthKey
		String authKey = "";
		
		Result<Record> domainRecords = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.in(
						dslContext.select(ENTERPRISE_CCC.DOMAIN).from(ENTERPRISE_CCC)
							.where(ENTERPRISE_CCC.ID.eq(cccId))
				))
				.fetch();
		
		Integer parentDomainId = domainRecords.get(0).get(DOMAIN.PARENT);
		Integer domainId = domainRecords.get(0).get(DOMAIN.ID);
		
		Record enterpriseDataRecord = dslContext.select().from(ENTERPRISE_DATA)
				.where(ENTERPRISE_DATA.NAME.eq("PAY_authorization_key_PAY"))
				.and(ENTERPRISE_DATA.DOMAIN.eq(domainId))
				.fetchOne();
		
		if(null != enterpriseDataRecord && AonStringUtils.isNotBlank(enterpriseDataRecord.get(ENTERPRISE_DATA.EXPRESSION)))
			return enterpriseDataRecord.get(ENTERPRISE_DATA.EXPRESSION);
		else {
			Record appParamRecord = dslContext.select().from(APP_PARAM)
					.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY"))
					.and(APP_PARAM.DOMAIN.eq(domainId))
					.fetchOne();
			
			if(null == appParamRecord || null == appParamRecord.get(APP_PARAM.VALUE)) {
				
				// Find Authkey from parent
				appParamRecord = dslContext.select().from(APP_PARAM)
						.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY"))
						.and(APP_PARAM.DOMAIN.eq(parentDomainId))
						.fetchOne();
				
				if(null == appParamRecord || null == appParamRecord.get(APP_PARAM.VALUE)) 
					authKey = "00000";
				else
					authKey = appParamRecord.get(APP_PARAM.VALUE);
				
			} else {
				authKey = appParamRecord.get(APP_PARAM.VALUE);
			}
			
			return authKey;
		}
		
	}
	
	private static String getSSMutualFromDomain(DSLContext dslContext, Integer cccId) {
		Result<Record> domainRecords = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.in(
						dslContext.select(ENTERPRISE_CCC.DOMAIN).from(ENTERPRISE_CCC)
							.where(ENTERPRISE_CCC.ID.eq(cccId))
				))
				.fetch();
		
		Integer parentDomainId = domainRecords.get(0).get(DOMAIN.PARENT);
		Integer domainId = domainRecords.get(0).get(DOMAIN.ID);
		
		Record enterpriseDataRecord = dslContext.select().from(ENTERPRISE_DATA)
				.where(ENTERPRISE_DATA.NAME.eq("PAY_ss_pension_plan_mutual_PAY"))
				.and(ENTERPRISE_DATA.DOMAIN.eq(domainId))
				.fetchOne();
		
		if(null != enterpriseDataRecord && AonStringUtils.isNotBlank(enterpriseDataRecord.get(ENTERPRISE_DATA.EXPRESSION)))
			return enterpriseDataRecord.get(ENTERPRISE_DATA.EXPRESSION);
		else return  "00000";
		
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject getTRA(Integer contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		
		Record personRecord = dslContext.select().from(PERSON)
				.where(PERSON.REGISTRY.eq(
						dslContext.select(CONTRACT.PERSON).from(CONTRACT)
							.where(CONTRACT.ID.eq(contractId))
							.fetchOne(CONTRACT.PERSON)
				))
				.fetchOne();
		
		if(AonStringUtils.isBlank(personRecord.get(PERSON.SOCIAL_SECURITY_NUM)))
			throw new IllegalArgumentException(personRecord.get(PERSON.NAME) + " no tiene numero de Seguridad Social registrado");
			
		json.put("numAfiliacion", personRecord.get(PERSON.SOCIAL_SECURITY_NUM));
		
		Record registryRecord = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(
						dslContext.select(CONTRACT.PERSON).from(CONTRACT)
							.where(CONTRACT.ID.eq(contractId))
							.fetchOne(CONTRACT.PERSON)
				))
				.fetchOne();
		
		
		Pattern dniPattern = Pattern.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");
		String document = registryRecord.get(REGISTRY.DOCUMENT);
		Matcher matcher = dniPattern.matcher(document.toUpperCase());
		
		json.put("documentType", matcher.matches() ? 1 : 6);
		json.put("documentCountry", /*registryRecord.get(REGISTRY.DOCUMENT_COUNTRY)*/ "");		//¿Es opcional?
		json.put("document", registryRecord.get(REGISTRY.DOCUMENT));
		json.put("nationality", /*registryRecord.get(REGISTRY.NATIONALITY)*/ "724");			//¿Es opcional?
		
		return json;
	}
	
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
		
		String fisrtSurname = personRecord.get(PERSON.FIRST_SURNAME);
		String secondSurname = personRecord.get(PERSON.SECOND_SURNAME);
		String name = personRecord.get(PERSON.NAME);
		
		if(null != fisrtSurname) 
			fisrtSurname = Normalizer
	        .normalize(fisrtSurname, Normalizer.Form.NFD)
	        .replaceAll("[^\\p{ASCII}]", "");
		
		if(null != secondSurname) 
			secondSurname = Normalizer
	        .normalize(secondSurname, Normalizer.Form.NFD)
	        .replaceAll("[^\\p{ASCII}]", "");
		
		if(null != name) 
			name = Normalizer
	        .normalize(name, Normalizer.Form.NFD)
	        .replaceAll("[^\\p{ASCII}]", "");
		
		json.put("firstSurname", AonStringUtils.isBlank(fisrtSurname) ? "" : fisrtSurname);
		json.put("secondSurname", AonStringUtils.isBlank(secondSurname) ? "" : secondSurname);
		json.put("name", AonStringUtils.isBlank(name) ? "" : name);
		
		return json;
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
	            .replace("ú", "u")
	            .replace("\u00D1", "N")
	            .replace("\u00F1", "n");
	}
}
