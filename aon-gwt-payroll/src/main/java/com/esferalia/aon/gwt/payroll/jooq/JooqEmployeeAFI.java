package com.esferalia.aon.gwt.payroll.jooq;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Geozone.GEOZONE;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.Agreement.AGREEMENT;
import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map.Entry;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges.AFIChange;
import com.esferalia.aon.watson.util.AonStringUtils;

public class JooqEmployeeAFI {

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	public static String setEmployeeAFI(Connection connection, Integer contractId, AFIChanges afiChangesMap) {
		return setEmployeeAFIDB(DSL.using(connection, getDefaultSettings()), contractId, afiChangesMap);
	}
	
	public static AFIChanges getEmployeeAFI(Connection connection, Integer contractId) {
		return getEmployeeAFIDB(DSL.using(connection, getDefaultSettings()), contractId);
	}

	// ********************************************************************************************************************************************
	//													GENERATE JSON EMPLOYE AFI
	// ********************************************************************************************************************************************

	@SuppressWarnings({ "unchecked"})
	public static JSONObject getEmployeeAFIInfo(String _domainId, String _domainName, String _contractId, String _workplaceId, Boolean _isStartContract,
			Boolean _isEndContract, Boolean _isChangeContract, Boolean _isQuoteContract, Boolean _isOcupationContract, Boolean _isPartialityCoefContract) {
		
		Connection connection = null;
		
		JSONObject employeeAFIJSON = new JSONObject();
		try {
			connection = AonServletUtils.getConnection(_domainName);
			DSLContext dslContext = DSL.using(connection, getDefaultSettings());
			
			Record domainRecord = dslContext.select().from(DOMAIN)
					.where(DOMAIN.ID.eq(Integer.parseInt(_domainId)))
					.fetchOne();
			
			Integer parentDomainId = domainRecord.get(DOMAIN.PARENT);
			
			//AUTH_KEY
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
			employeeAFIJSON.put("ETI", eti);
			
			//EMP
			Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.ID.in(
							dslContext.select(CONTRACT.ENTERPRISE_CCC).from(CONTRACT)
								.where(CONTRACT.ID.eq(Integer.parseInt(_contractId)))
								.fetchOne()
								.get(CONTRACT.ENTERPRISE_CCC)
					)).fetchOne();
			
			Record geozoneCCC = dslContext.select().from(GEOZONE).where(GEOZONE.ID.eq(enterpriseCCCRecord.get(ENTERPRISE_CCC.GEOZONE))).fetchOne();
			
			Result<Record> enterpriseCCCPrincipalRecords = dslContext.select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY.eq(enterpriseCCCRecord.get(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY)))
					.and(ENTERPRISE_CCC.TYPE.eq((byte)0))
					.orderBy(ENTERPRISE_CCC.ID)
					.fetch();
			
			Record geozoneCCCPrincipal = null;
			
			if(enterpriseCCCPrincipalRecords.size() > 0)
				geozoneCCCPrincipal = dslContext.select().from(GEOZONE).where(GEOZONE.ID.eq(enterpriseCCCPrincipalRecords.get(0).get(ENTERPRISE_CCC.GEOZONE))).fetchOne();
			
			Record enterpriseRegistryRecord = dslContext.select().from(REGISTRY).where(REGISTRY.ID.in(
						dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY)
							.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseCCCRecord.get(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY)))
							.fetchOne().get(ENTERPRISE_ACTIVITY.ENTERPRISE)
					)).fetchOne();
			
			JSONObject emp = new JSONObject();
			emp.put("cccRegime", getCCCRegimeCode(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE)));
			emp.put("cccProvince", geozoneCCC.get(GEOZONE.CODE));
			emp.put("ccc", parseCCC(enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC)));
			emp.put("identType", getIndetType(enterpriseRegistryRecord.get(REGISTRY.DOCUMENT_TYPE)));
			emp.put("country", "011");
			emp.put("ident", enterpriseRegistryRecord.get(REGISTRY.DOCUMENT));
			emp.put("cccRegimePrincipal", "0111");
			emp.put("cccProvincePrincipal", geozoneCCCPrincipal == null ? "00" : geozoneCCCPrincipal.get(GEOZONE.CODE));
			emp.put("cccPrincipal", parseCCC(enterpriseCCCPrincipalRecords.size() == 0 ? "000000000" : enterpriseCCCPrincipalRecords.get(0).get(ENTERPRISE_CCC.CCC)));
			employeeAFIJSON.put("EMP", emp);
			
			//RZS
			JSONObject rzsData = new JSONObject();
			rzsData.put("businessmanType", "2");
			rzsData.put("rzsName", removeAccents(enterpriseRegistryRecord.get(REGISTRY.NAME)));
			employeeAFIJSON.put("RZS", rzsData);
			
			//TRA
			employeeAFIJSON.put("TRA", getTRA(Integer.parseInt(_contractId), dslContext));
			
			//AYN
			employeeAFIJSON.put("AYN", getAYN(Integer.parseInt(_contractId), dslContext));
			
			Integer contSeg = 0;
			if(_isStartContract) {
				contSeg++;
				employeeAFIJSON.put("MA", getSDC(Integer.parseInt(_contractId), dslContext));
			}
			if(_isEndContract) {
				contSeg++;
				employeeAFIJSON.put("MB", getEDC(Integer.parseInt(_contractId), dslContext));
			}
			
			// Movimientos Contrato
			contSeg++;
			employeeAFIJSON.put("MC", getMC(Integer.parseInt(_contractId), dslContext));
			
			//ETF
			JSONObject etf = new JSONObject();
			etf.put("authkey", authKey);
			etf.put("payrollProvider", "498");
			etf.put("fileName", null);
			etf.put("priorityCode", "N");
			employeeAFIJSON.put("ETF", etf);
			
			//CONFIG
			JSONObject conf = new JSONObject();
			conf.put("staticLines", "4");
			conf.put("employeeLines", "4");
			conf.put("numEmployees", contSeg+"");
			employeeAFIJSON.put("CONF", conf);
			
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		System.out.println(employeeAFIJSON);
		return employeeAFIJSON;
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
		
		json.put("documentType", registryRecord.get(REGISTRY.DOCUMENT_TYPE) == 0 ? 1 : 0);
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
	
	@SuppressWarnings("unchecked")
	private static JSONObject getSDC(int contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		JSONObject fab = new JSONObject();
		JSONObject otd = new JSONObject();
		
		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		Result<Record> contractDataQuoteRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION")).orderBy(CONTRACT_DATA.ID.desc()).fetch();
		Result<Record> contractDataTC2Record = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("TC2")).orderBy(CONTRACT_DATA.ID.desc()).fetch();
		Byte gender = dslContext.select(PERSON.GENDER).from(PERSON).where(PERSON.REGISTRY.in(
				dslContext.select(CONTRACT.PERSON).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne().get(CONTRACT.PERSON)
				)).fetchOne().get(PERSON.GENDER);
		
		//FAB
		fab.put("action", "MA");
		fab.put("situation", "1");
		fab.put("day", contractRecord.get(CONTRACT.START_DATE).getDate());
		fab.put("month", (contractRecord.get(CONTRACT.START_DATE).getMonth()+1));
		fab.put("year", (contractRecord.get(CONTRACT.START_DATE).getYear()+1900));
		fab.put("quoteGroup", parseContractData(contractDataQuoteRecord.get(0).get(CONTRACT_DATA.EXPRESSION)));
		fab.put("tc2", parseContractData(contractDataTC2Record.get(0).get(CONTRACT_DATA.EXPRESSION)));
		fab.put("gender", gender+1);
		
		//OTD
		//TODO: Falta el codigo del convenio colectivo
		String agreementColective = dslContext.select(AGREEMENT.SS_NUMBER).from(AGREEMENT).where(AGREEMENT.ID.in(
				dslContext.select(AGREEMENT_LEVEL.AGREEMENT).from(AGREEMENT_LEVEL).where(AGREEMENT_LEVEL.ID.in(
						dslContext.select(CONTRACT.AGREEMENT_LEVEL).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne().get(CONTRACT.AGREEMENT_LEVEL)
				)).fetchOne().get(AGREEMENT_LEVEL.AGREEMENT)
				)).fetchOne().get(AGREEMENT.SS_NUMBER);
//		otd.put("convCollective", "XXXXXXXXXXXXXX");
		otd.put("convCollective",  AonStringUtils.isBlank(agreementColective) ? "00000000000000" : agreementColective);
		
		json.put("FAB", fab);
		json.put("OTD", otd);
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject getEDC(int contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		JSONObject fab = new JSONObject();
		JSONObject dam = new JSONObject();
		
		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		Result<Record> contractDataQuoteRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION")).orderBy(CONTRACT_DATA.ID.desc()).fetch();
		Result<Record> contractDataTC2Record = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("TC2")).orderBy(CONTRACT_DATA.ID.desc()).fetch();
		Byte gender = dslContext.select(PERSON.GENDER).from(PERSON).where(PERSON.REGISTRY.in(
				dslContext.select(CONTRACT.PERSON).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne().get(CONTRACT.PERSON)
				)).fetchOne().get(PERSON.GENDER);
		
		Result<Record> salaryDataRecords = dslContext.select().from(SALARY_DATA)
			.where(SALARY_DATA.NAME.eq("CAUSA_INDEMNIZACION"))
			.and(SALARY_DATA.SALARY.in(
					dslContext.select(SALARY.ID).from(SALARY).where(SALARY.TYPE.eq((byte)2)).and(SALARY.CONTRACT.eq(contractId)).fetch().getValues(SALARY.ID))
			).fetch();
		
		//FAB
		fab.put("action", "MB");
		//AVERIGUAR A TRAVES DEL FINIQUITO
		fab.put("situation", getCausaDespido(salaryDataRecords));
		fab.put("day", contractRecord.get(CONTRACT.END_DATE).getDate());
		fab.put("month", (contractRecord.get(CONTRACT.END_DATE).getMonth()+1));
		fab.put("year", (contractRecord.get(CONTRACT.END_DATE).getYear()+1900));
		fab.put("quoteGroup", parseContractData(contractDataQuoteRecord.get(0).get(CONTRACT_DATA.EXPRESSION)));
		fab.put("tc2", parseContractData(contractDataTC2Record.get(0).get(CONTRACT_DATA.EXPRESSION)));
		fab.put("gender", gender);
		
		//DAM
		//TODO: todo reservado
		
		json.put("FAB", fab);
		json.put("DAM", dam);
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject getMC(int contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		JSONObject fab = new JSONObject();
		JSONObject dam = new JSONObject();
		
		ArrayList<Date> dates = new ArrayList<Date>();
		
		Result<Record> contractDataQuoteRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION")).orderBy(CONTRACT_DATA.START_DATE.desc()).fetch();
		Result<Record> contractDataTC2Record = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("TC2")).orderBy(CONTRACT_DATA.START_DATE.desc()).fetch();
		Byte gender = dslContext.select(PERSON.GENDER).from(PERSON).where(PERSON.REGISTRY.in(
				dslContext.select(CONTRACT.PERSON).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne().get(CONTRACT.PERSON)
				)).fetchOne().get(PERSON.GENDER);
		Result<Record> contractDataPCRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("COEFICIENTE_PARCIALIDAD")).orderBy(CONTRACT_DATA.START_DATE.desc()).fetch();
		Result<Record> contractDataOcupationRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("OCUPACION")).orderBy(CONTRACT_DATA.ID.desc()).fetch();
		
		dates.add(contractDataQuoteRecord.get(0).get(CONTRACT_DATA.START_DATE));
		dates.add(contractDataTC2Record.get(0).get(CONTRACT_DATA.START_DATE));
		dates.add(contractDataPCRecord.get(0).get(CONTRACT_DATA.START_DATE));
		dates.add(contractDataOcupationRecord.get(0).get(CONTRACT_DATA.START_DATE));
		dates.sort(new Comparator<Date>() {
			@Override
			public int compare(Date o1, Date o2) {
				return o1.compareTo(o2);
			}
		});
		
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
		String date = formatDate.format(dates.get(dates.size()-1));
		
		//FAB
		fab.put("action", "MC");
		fab.put("situation", "");
		fab.put("day", date.split("/")[0]);
		fab.put("month", date.split("/")[1]);
		fab.put("year", date.split("/")[2]);
		fab.put("quoteGroup", parseContractData(contractDataQuoteRecord.get(0).get(CONTRACT_DATA.EXPRESSION)));
		fab.put("tc2", parseContractData(contractDataTC2Record.get(0).get(CONTRACT_DATA.EXPRESSION)));
		fab.put("partialityCoef", parseContractData(contractDataPCRecord.get(0).get(CONTRACT_DATA.EXPRESSION)));
		fab.put("gender", gender);
		
		//DAM
		dam.put("ocupation", parseContractData(contractDataOcupationRecord.get(0).get(CONTRACT_DATA.EXPRESSION)));		
		
		//OTD
		//TODO: codigo convenio colection
		
		json.put("FAB", fab);
		json.put("DAM", dam);
		
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
	            .replace("ú", "u");
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
	
	private static Object getIndetType(Byte identType) {
		switch (identType) {
		case 0:
			return "1";
		case 1:
			return "9";
		case 2:
			return "6";
		default:
			return "L";
		}
	}

	private static String getCausaDespido(Result<Record> records) {

		if(records.isEmpty())
			return "99";
		
		switch (records.get(0).get(SALARY_DATA.EXPRESSION)) {
		case "UNFAIR":
			return "93";
		case "TEMP_END":
			return "93";
		case "WORK_END": //FIN_OBRA
			return "93";
		case "DEFINITE_END":
			return "93";
		case "OBJECTIVE":
			return "91";
		default: //"CONDITIONS_CHANGE":
			return "99";
		}
	}
	
	private static String parseContractData( String data ) {
		if(null != data)
			data = data.replace(".", "");
		if(null != data && data.contains("\""))
			return data.split("\"")[1];
		else
			return data;
	}
	
	private static String parseCCC(String ccc) {
		if(ccc.length() == 11) {
			return ccc.substring(2);
		}else
			return ccc;
		
	}
	
	// ***************************************************************************************************************************************
	// ***************************************************************************************************************************************
	// ***************************************************************************************************************************************
	
	private static String setEmployeeAFIDB(DSLContext dslContext, Integer contractId, AFIChanges afiChangesMap) {
		
		//Delete existing info
		ArrayList<String> contractDataVars = new ArrayList<String>();
		contractDataVars.add("TC2");
		contractDataVars.add("GRUPO_COTIZACION");
		contractDataVars.add("OCUPACION");
		contractDataVars.add("COEFICIENTE_PARCIALIDAD");
		
		dslContext.delete(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.eq(contractId))
			.and(CONTRACT_DATA.NAME.in(contractDataVars))
			.execute();
		
		//Get contract startDate and endDate
		Record contractRecord = dslContext.select().from(CONTRACT)
					.where(CONTRACT.ID.eq(contractId))
					.fetchOne();
		
		Date contractEndDate = contractRecord.get(CONTRACT.END_DATE);
		Integer domainId = contractRecord.get(CONTRACT.DOMAIN);
		
		ArrayList<java.util.Date> dateList = new ArrayList<java.util.Date>();
		dateList.addAll(afiChangesMap.getAFIChanges().keySet());
		
		//Insert info : OPCION 1
		Integer cont = 1;
		
		for(Entry<java.util.Date, ArrayList<AFIChange>> entry : afiChangesMap.getAFIChanges().entrySet()) {
			Date startDate = new Date(entry.getKey().getTime());
			Date endDate;
			if(cont < dateList.size()) {
				endDate = new Date(DateUtils.copyDateOnly(dateList.get(cont)).getTime());
				DateUtils.addDays2Date(endDate, -1);
			}else
				endDate = contractEndDate;
			
			for(AFIChange afiChange: afiChangesMap.getAFIChanges().get(entry.getKey())) {
				if(null != afiChange.getValue())
					if(AonStringUtils.equals(afiChange.getName(), "COEFICIENTE_PARCIALIDAD"))
						dslContext.insertInto(CONTRACT_DATA)
							.set(CONTRACT_DATA.DOMAIN, domainId)
							.set(CONTRACT_DATA.CONTRACT, contractId)
							.set(CONTRACT_DATA.NAME, afiChange.getName())
							.set(CONTRACT_DATA.EXPRESSION, afiChange.getValue().contains("\"") ? AonStringUtils.replace(afiChange.getValue(), "\"", "") : afiChange.getValue())
							.set(CONTRACT_DATA.START_DATE, startDate)
							.set(CONTRACT_DATA.END_DATE, endDate)
							.execute();
					else
						dslContext.insertInto(CONTRACT_DATA)
							.set(CONTRACT_DATA.DOMAIN, domainId)
							.set(CONTRACT_DATA.CONTRACT, contractId)
							.set(CONTRACT_DATA.NAME, afiChange.getName())
							.set(CONTRACT_DATA.EXPRESSION, afiChange.getValue().contains("\"") ? afiChange.getValue() : "\"" + afiChange.getValue() + "\"")
							.set(CONTRACT_DATA.START_DATE, startDate)
							.set(CONTRACT_DATA.END_DATE, endDate)
							.execute();
			}
			
			cont++;
		}
		
		return null;
	}

	private static AFIChanges getEmployeeAFIDB(DSLContext dslContext, Integer contractId) {
		AFIChanges afiChanges = new AFIChanges();
		
		ArrayList<String> contractDataVars = new ArrayList<String>();
		contractDataVars.add("TC2");
		contractDataVars.add("GRUPO_COTIZACION");
		contractDataVars.add("OCUPACION");
		contractDataVars.add("COEFICIENTE_PARCIALIDAD");
		
		Result<Record> contractDataRecords = dslContext.select().from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.in(contractDataVars))
				.orderBy(CONTRACT_DATA.START_DATE.asc())
				.fetch();
		
		if(!contractDataRecords.isEmpty()) {
			ArrayList<AFIChange> afiChangeList = new ArrayList<>();
			Date dateAux = contractDataRecords.get(0).get(CONTRACT_DATA.START_DATE);
			
			for(Record r : contractDataRecords) {
				if(r.get(CONTRACT_DATA.START_DATE).equals(dateAux)) {
					afiChangeList.add(new AFIChange(r.get(CONTRACT_DATA.NAME), r.get(CONTRACT_DATA.EXPRESSION)));
				}else {
					java.util.Date javaDate = new Date(dateAux.getTime());
					afiChanges.getAFIChanges().put(javaDate, afiChangeList);
					dateAux = r.get(CONTRACT_DATA.START_DATE);
					afiChangeList = new ArrayList<>();
					afiChangeList.add(new AFIChange(r.get(CONTRACT_DATA.NAME), r.get(CONTRACT_DATA.EXPRESSION)));
				}
			}
			
			if(!afiChangeList.isEmpty()) {
				java.util.Date javaDate = new Date(dateAux.getTime());
				afiChanges.getAFIChanges().put(javaDate, afiChangeList);
			}
			
		}
		
		return afiChanges;
	}

}
