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

import java.sql.Connection;
import java.sql.SQLException;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;

public class JooqEmployeeAFI {

	private static Settings SETTINGS = null;
	
	protected static Settings getDefaultSettings() {
		if (SETTINGS == null) {
			SETTINGS = new Settings();
			SETTINGS.setRenderSchema(false);
		}
		return SETTINGS;
	}
	
	// ********************************************************************************************************************************************
	//													GENERATE JSON EMPLOYE AFI
	// ********************************************************************************************************************************************

	@SuppressWarnings({ "unchecked", "null" })
	public static JSONObject getEmployeeAFIInfo(String _domainId, String _domainName, String _contractId, String _workplaceId, Boolean _isStartContract,
			Boolean _isEndContract, Boolean _isChangeContract, Boolean _isQuoteContract, Boolean _isOcupationContract) {
		
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
			
			Record geozoneCCCPrincipal = dslContext.select().from(GEOZONE).where(GEOZONE.ID.eq(enterpriseCCCPrincipalRecords.get(0).get(ENTERPRISE_CCC.GEOZONE))).fetchOne();
			
			Record enterpriseRegistryRecord = dslContext.select().from(REGISTRY).where(REGISTRY.ID.in(
						dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY)
							.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseCCCRecord.get(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY)))
							.fetchOne().get(ENTERPRISE_ACTIVITY.ENTERPRISE)
					)).fetchOne();
			
			JSONObject emp = new JSONObject();
			emp.put("cccRegime", getCCCRegimeCode(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE)));
			emp.put("cccProvince", geozoneCCC.get(GEOZONE.CODE));
			emp.put("ccc", enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC));
			emp.put("identType", getIndetType(enterpriseRegistryRecord.get(REGISTRY.DOCUMENT_TYPE)));
			emp.put("country", "011");
			emp.put("ident", enterpriseRegistryRecord.get(REGISTRY.DOCUMENT));
			emp.put("cccRegimePrincipal", "0111");
			emp.put("cccProvincePrincipal", geozoneCCCPrincipal);
			emp.put("cccPrincipal", enterpriseCCCPrincipalRecords.get(0).get(ENTERPRISE_CCC.CCC));
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
				employeeAFIJSON.put("SDC", getSDC(Integer.parseInt(_contractId), dslContext));
			}else if(_isEndContract) {
				contSeg++;
				employeeAFIJSON.put("EDC", getEDC(Integer.parseInt(_contractId), dslContext));
			}else if(_isChangeContract) {
				contSeg++;
				employeeAFIJSON.put("CHC", getCHC(Integer.parseInt(_contractId), dslContext));
			}else if(_isQuoteContract) {
				contSeg++;
				employeeAFIJSON.put("QGC", getQGC(Integer.parseInt(_contractId), dslContext));
			}else if(_isOcupationContract) {
				contSeg++;
				employeeAFIJSON.put("OCC", getOCC(Integer.parseInt(_contractId), dslContext));
			}
			
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
		Record contractDataQuoteRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION")).fetchOne();
		Record contractDataTC2Record = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("TC2")).fetchOne();
		Byte gender = dslContext.select(PERSON.GENDER).from(PERSON).where(PERSON.REGISTRY.in(
				dslContext.select(CONTRACT.PERSON).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne().get(CONTRACT.PERSON)
				)).fetchOne().get(PERSON.GENDER);
		
		//FAB
		fab.put("action", "MA");
		fab.put("situation", "1");
		fab.put("day", contractRecord.get(CONTRACT.START_DATE).getDate());
		fab.put("month", (contractRecord.get(CONTRACT.START_DATE).getMonth()+1));
		fab.put("year", (contractRecord.get(CONTRACT.START_DATE).getYear()+1900));
		fab.put("quoteGroup", contractDataQuoteRecord.get(CONTRACT_DATA.EXPRESSION));
		fab.put("tc2", contractDataTC2Record.get(CONTRACT_DATA.EXPRESSION));
		fab.put("gender", gender);
		
		//OTD
		//TODO: Falta el codigo del convenio colectivo
		otd.put("convCollective", "XXXXXXXXXXXXXX");
		
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
		Record contractDataQuoteRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION")).fetchOne();
		Record contractDataTC2Record = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("TC2")).fetchOne();
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
		//AvERIGUAR A TRAVES DEL FINIQUITO
		fab.put("situation", getCausaDespido(salaryDataRecords.get(0).get(SALARY_DATA.EXPRESSION)));
		fab.put("day", contractRecord.get(CONTRACT.END_DATE).getDate());
		fab.put("month", (contractRecord.get(CONTRACT.END_DATE).getMonth()+1));
		fab.put("year", (contractRecord.get(CONTRACT.END_DATE).getYear()+1900));
		fab.put("quoteGroup", contractDataQuoteRecord.get(CONTRACT_DATA.EXPRESSION));
		fab.put("tc2", contractDataTC2Record.get(CONTRACT_DATA.EXPRESSION));
		fab.put("gender", gender);
		
		//DAM
		//TODO: todo reservado
		
		json.put("FAB", fab);
		json.put("DAM", dam);
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject getCHC(int contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		JSONObject fab = new JSONObject();
		JSONObject dam = new JSONObject();
		
		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		Record contractDataQuoteRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION")).fetchOne();
		Record contractDataTC2Record = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("TC2")).fetchOne();
		Byte gender = dslContext.select(PERSON.GENDER).from(PERSON).where(PERSON.REGISTRY.in(
				dslContext.select(CONTRACT.PERSON).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne().get(CONTRACT.PERSON)
				)).fetchOne().get(PERSON.GENDER);
		
		//FAB
		fab.put("action", "MC");
		fab.put("situation", "");
		fab.put("day", contractDataTC2Record.get(CONTRACT_DATA.START_DATE).getDate());
		fab.put("month", (contractDataTC2Record.get(CONTRACT_DATA.START_DATE).getMonth()+1));
		fab.put("year", (contractDataTC2Record.get(CONTRACT_DATA.START_DATE).getYear()+1900));
		fab.put("quoteGroup", contractDataQuoteRecord.get(CONTRACT_DATA.EXPRESSION));
		fab.put("tc2", contractDataTC2Record.get(CONTRACT_DATA.EXPRESSION));
		fab.put("gender", gender);
		
		//OTD
		//TODO: codigo convenio colection
		
		json.put("FAB", fab);
		json.put("DAM", dam);
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject getQGC(int contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		JSONObject fab = new JSONObject();
		JSONObject dam = new JSONObject();
		
		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		Record contractDataQuoteRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION")).fetchOne();
		Record contractDataTC2Record = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("TC2")).fetchOne();
		Byte gender = dslContext.select(PERSON.GENDER).from(PERSON).where(PERSON.REGISTRY.in(
				dslContext.select(CONTRACT.PERSON).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne().get(CONTRACT.PERSON)
				)).fetchOne().get(PERSON.GENDER);
		
		//FAB
		fab.put("action", "MG");
		fab.put("situation", "");
		fab.put("day", contractDataQuoteRecord.get(CONTRACT_DATA.START_DATE).getDate());
		fab.put("month", (contractDataQuoteRecord.get(CONTRACT_DATA.START_DATE).getMonth()+1));
		fab.put("year", (contractDataQuoteRecord.get(CONTRACT_DATA.START_DATE).getYear()+1900));
		fab.put("quoteGroup", contractDataQuoteRecord.get(CONTRACT_DATA.EXPRESSION));
		fab.put("tc2", contractDataTC2Record.get(CONTRACT_DATA.EXPRESSION));
		fab.put("gender", gender);
		
		//OTD
		//TODO: codigo convenio colection
		
		json.put("FAB", fab);
		json.put("DAM", dam);
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	private static JSONObject getOCC(int contractId, DSLContext dslContext) {
		JSONObject json = new JSONObject();
		JSONObject fab = new JSONObject();
		JSONObject dam = new JSONObject();
		
		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		Record contractDataQuoteRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION")).fetchOne();
		Record contractDataOcupationRecord = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("OCUPACION")).fetchOne();
		Record contractDataTC2Record = dslContext.select().from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("TC2")).fetchOne();
		Byte gender = dslContext.select(PERSON.GENDER).from(PERSON).where(PERSON.REGISTRY.in(
				dslContext.select(CONTRACT.PERSON).from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne().get(CONTRACT.PERSON)
				)).fetchOne().get(PERSON.GENDER);
		
		//FAB
		fab.put("action", "MT");
		fab.put("situation", "");
		fab.put("day", contractRecord.get(CONTRACT.START_DATE).getDate());
		fab.put("month", (contractRecord.get(CONTRACT.START_DATE).getMonth()+1));
		fab.put("year", (contractRecord.get(CONTRACT.START_DATE).getYear()+1900));
		fab.put("quoteGroup", contractDataQuoteRecord.get(CONTRACT_DATA.EXPRESSION));
		fab.put("tc2", contractDataTC2Record.get(CONTRACT_DATA.EXPRESSION));
		fab.put("gender", gender);
		
		//DAM
		dam.put("ocupation", contractDataOcupationRecord.get(CONTRACT_DATA.EXPRESSION));
		
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

	private static String getCausaDespido(String causaDespido) {

		switch (causaDespido) {
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

}
