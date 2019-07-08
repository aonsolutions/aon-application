package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.AgrarianJourney;
import com.google.gwt.regexp.shared.RegExp;

public class JooqAgrarian {

	private static Settings SETTINGS = null;
	
	public static Map<Integer, List<AgrarianJourney>> getAgrarianJourney(java.util.Date startDate, java.util.Date endDate, String domain, Integer enterprise_ccc, Connection conn) {
		return getAgrarianJourneyoDB(startDate, endDate, domain, enterprise_ccc, DSL.using(conn, getDefaultSettings()));
	}
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	private static Map<Integer, List<AgrarianJourney>> getAgrarianJourneyoDB(java.util.Date  startDate, java.util.Date endDate, String domain, Integer enterprise_ccc, DSLContext dslContext) {
		
		Map<Integer, List<AgrarianJourney>> agrarianJourneyMap = new HashMap<>();
		
		Result<Record> agrarianJourney = dslContext.select().from(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.in(
				dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.ENTERPRISE_CCC.eq(enterprise_ccc))
					.fetch()	
			))
			.and(CONTRACT_DATA.NAME.eq("PEONADAS"))
			.and(CONTRACT_DATA.START_DATE.greaterOrEqual(new Date(startDate.getTime())))
			.and(CONTRACT_DATA.END_DATE.lessOrEqual(new Date(endDate.getTime())))
			.fetch();
		
		for (Record r : agrarianJourney){
			Result<Record> personRecords = dslContext.select().from(PERSON)
					.where(PERSON.REGISTRY.in(
							dslContext.select(CONTRACT.PERSON).from(CONTRACT)
								.where(CONTRACT.ID.eq(r.get(CONTRACT_DATA.CONTRACT)))
							))
					.fetch();
			if(!personRecords.isEmpty())
				addAgrarianJourney(
						agrarianJourneyMap, 
						r.get(CONTRACT_DATA.CONTRACT), 
						r.get(CONTRACT_DATA.START_DATE),
						r.get(CONTRACT_DATA.END_DATE),
						personRecords.get(0).get(PERSON.NAME),
						personRecords.get(0).get(PERSON.FIRST_SURNAME)
				);
			
			
			System.out.println(
					"Contract : " + r.get(CONTRACT_DATA.CONTRACT) +
					" PEONADAS -> StartDate : " + r.get(CONTRACT_DATA.START_DATE) +
					" EndDate : " + r.get(CONTRACT_DATA.END_DATE) +
					" Nombre : " + personRecords.get(0).get(PERSON.NAME) +
					" Apellido : " + personRecords.get(0).get(PERSON.FIRST_SURNAME)
			);
		}
		
		
		return agrarianJourneyMap;
	}

	private static void addAgrarianJourney(
			Map<Integer, List<AgrarianJourney>> agrarianJourneyMap, 
			Integer contractId, Date startDate, 
			Date endDate,
			String name,
			String firstSurname) {
		
		List<AgrarianJourney> listJournies = agrarianJourneyMap.get(contractId);
		if(null == listJournies){
			List<AgrarianJourney> newJournies = new ArrayList<>();
			AgrarianJourney journey = new AgrarianJourney(contractId, startDate, endDate);
			journey.setName(name);
			journey.setSurname(firstSurname);
			newJournies.add(journey);
			agrarianJourneyMap.put(contractId, newJournies);
		}else{
			AgrarianJourney journey = new AgrarianJourney(contractId, startDate, endDate);
			journey.setName(name);
			journey.setSurname(firstSurname);
			listJournies.add(journey);
			agrarianJourneyMap.put(contractId, listJournies);
		}
	}
	
	// ********************************************************************************************************************************************
	//													GENERATE JSON AGRARIAN
	// ********************************************************************************************************************************************

	@SuppressWarnings({ "unchecked" })
	public static JSONObject getAgrarianInfo(String _domainId, String domainName, String _enterpriseId, String enterpriseName,
			String _ccc, String _startDate, String _endDate, ArrayList<Integer> _selectedContracts) {
		
		java.util.Date startDate = new java.util.Date(Long.parseLong(_startDate));
		java.util.Date endDate = new java.util.Date(Long.parseLong(_endDate));
		
		JSONObject agrarianJSON = new JSONObject();
		
		DSLContext dslContext = null;
		Connection connection = null;
		
		try {
			
			connection = AonServletUtils.getConnection(domainName);
			dslContext = DSL.using(connection, getDefaultSettings());
			
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
			eti.put("payrollProvider", "498");		//Proveedor de nominas ESFERALIA NETWORKS, S.A.
			eti.put("fileName", null);
			eti.put("prorityCode", "N");
			agrarianJSON.put("ETI", eti);
			
			//EMP
			JSONObject emp = new JSONObject();
			emp.put("cccProvince", _ccc.substring(0, 2));
			emp.put("ccc", _ccc.substring(2, _ccc.length()));
			emp.put("cccRegimePrincipal", "");
			emp.put("cccProvincePrincipal", "00"/*_ccc.substring(0, 2)*/);
			emp.put("cccPrincipal", "0000000000000" /* _ccc.substring(2, _ccc.length())*/);
			agrarianJSON.put("EMP", emp);
			
			//RZS
			JSONObject rzsData = new JSONObject();
			rzsData.put("businessmanType", "2");
			rzsData.put("rzsName", removeAccents(enterpriseName));
			agrarianJSON.put("RZS", rzsData);
			
			//ETF
			JSONObject etf = new JSONObject();
			etf.put("authkey", authKey);
			etf.put("payrollProvider", "498");
			etf.put("fileName", null);
			etf.put("priorityCode", "N");
			agrarianJSON.put("ETF", etf);
			
			Result<Record> enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC).where(ENTERPRISE_CCC.CCC.eq(_ccc)).fetch();
			Integer enterpriseCCCId = enterpriseCCCRecord.get(0).get(ENTERPRISE_CCC.ID);
			
			//EMPLOYEES
			JSONArray emps = new JSONArray();
			Map<Integer, List<AgrarianJourney>> contractsJourney = getAgrarianJourneyoDB(startDate, endDate, domainName, enterpriseCCCId, dslContext);
			for(Integer contractId : _selectedContracts) {
				JSONObject empl = new JSONObject();
				JSONObject tra = getTRA(contractId, dslContext);
				empl.put("TRA", tra);
				JSONObject ayn = getAYN(contractId, dslContext);
				empl.put("AYN", ayn);
				//Para el caso de las jornadas agrarias esto es constante
				final String fab = "FABMJR000000000000 00000  000000 0000000000000000 0000   N 00000000   ";
				empl.put("FAB", fab);
				List<AgrarianJourney> journeis = contractsJourney.get(contractId);
				JSONObject dra = getDRA(startDate, endDate, journeis);
				empl.put("DRA", dra);
				emps.add(empl);
			}
			
			agrarianJSON.put("EMPS", emps);
			
			//CONFIG
			JSONObject conf = new JSONObject();
			conf.put("staticLines", "4");
			conf.put("employeeLines", "4");
			conf.put("numEmployees", _selectedContracts.size()+"");
			agrarianJSON.put("CONF", conf);
			
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (dslContext != null)
				dslContext.close();
		}

		
		return agrarianJSON;
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
			
		json.put("numAfiliacion", personRecord.get(PERSON.SOCIAL_SECURITY_NUM));
		
		Record registryRecord = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(
						dslContext.select(CONTRACT.PERSON).from(CONTRACT)
							.where(CONTRACT.ID.eq(contractId))
							.fetchOne(CONTRACT.PERSON)
				))
				.fetchOne();
		
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");
		String document = registryRecord.get(REGISTRY.DOCUMENT);
		
		json.put("documentType", dniPattern.test(document.toUpperCase()) ? 1 : 6);
//		json.put("documentType", registryRecord.get(REGISTRY.DOCUMENT_TYPE) == 0 ? 1 : 6);
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
		
		json.put("firstSurname", personRecord.get(PERSON.FIRST_SURNAME));
		json.put("secondSurname", personRecord.get(PERSON.SECOND_SURNAME));
		json.put("name", personRecord.get(PERSON.NAME));
		
		return json;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static JSONObject getDRA(java.util.Date startDate, java.util.Date endDate, List<AgrarianJourney> journeis) {
		JSONObject json = new JSONObject();
		
		json.put("year", (startDate.getYear()+1900)+"");
		json.put("month", (startDate.getMonth()+1)+"");
		
		Integer numDays = 31;
		JSONArray days = new JSONArray();
		
		for(int i = 0; i < numDays; i++) {
			java.util.Date date = new java.util.Date(startDate.getYear(), startDate.getMonth(), i+1);
			if(checkDateAgraria(journeis, new Date(date.getTime())))
				days.add(i, "S");
			else
				days.add(i, " ");
		}
		
		json.put("days", days);
		
		return json;
	}
	
	private static boolean checkDateAgraria(List<AgrarianJourney> journiesList, Date date) {
		for(AgrarianJourney journey : journiesList){
			if((journey.getStartDate().before(date) || journey.getStartDate().equals(date)) &&
			   (journey.getEndDate().after(date) || journey.getEndDate().equals(date)))
			   return true;
		}
		return false;
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

}
