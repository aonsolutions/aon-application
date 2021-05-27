package com.esferalia.aon.payroll.tgss.afi;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDatabaseOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDbPasswordOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDbUserOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getHostNameOption;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;

public class AgrarianAFI {
	
	// ********************************************************************************************************************************************
	//																MAIN
	// ********************************************************************************************************************************************
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		//@formatter:off
		Option hostName = getHostNameOption();
		Option user = getDbUserOption();
		Option password = getDbPasswordOption();
		Option database = getDatabaseOption();
		Option cccOpt = getCCCOption();
		Option findingDateOpt = getFindingDateOption();
		Option contractsOpt = getContractsOption();

		Options options = new Options()
				.addOption(hostName)
				.addOption(user)
				.addOption(password)
				.addOption(database)
				.addOption(cccOpt)
				.addOption(findingDateOpt)
				.addOption(contractsOpt)
				;	
		//@formatter:on

		// Create the parser
		CommandLineParser parser = new DefaultParser();

		try {
			Class.forName(com.mysql.cj.jdbc.Driver.class.getName());
			
			// Parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			Connection connection = DriverManager.getConnection(
					String.format(
							"jdbc:mysql://%s:%d/%s",
							cmd.getOptionValue(hostName.getLongOpt(), "127.0.0.1"),
							3306, 
							cmd.getOptionValue(database.getLongOpt())
					),
					cmd.getOptionValue(user.getLongOpt()),
					cmd.getOptionValue(password.getLongOpt()));
			
			
			String cccs = cmd.getOptionValue(cccOpt.getLongOpt());
			ArrayList<String> cccList = parseCCCsToArray(cccs);
			
			Long findingDate = parseDate(cmd.getOptionValue(findingDateOpt.getLongOpt()));
			
			String contracts = cmd.getOptionValue(contractsOpt.getLongOpt());
			ArrayList<Integer> contractsList = parseContractsToArray(contracts);
			
			JSONObject agrarianJSON = getAgrarianInfo(cccList, findingDate, contractsList, connection);
			String agrarianAFI = MainAgrarianAFIGenerator.generateAgrarianAFI(agrarianJSON);
			System.out.println(agrarianAFI);

		} catch (ParseException e) {
			
			// Oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// Automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("CRA", options);

		}
	}
	
	private static Option getCCCOption() {
		return Option.builder("c")
				.longOpt("cccs")
				.desc("CCCs to find Agrarian AFI. 'ccc1, ccc2, ccc3 ...'")
				.required()
				.argName("name")
				.hasArg()
				.build();
	}
	
	private static Option getFindingDateOption() {
		return Option.builder("d")
				.longOpt("date")
				.desc("Date to find Agrarian AFI 'yyyy-mm-dd'")
				.required()
				.argName("name")
				.hasArg()
				.build();
	}
	
	private static Option getContractsOption() {
		return Option.builder("C")
				.longOpt("contracts")
				.desc("Contracts to find Agrarian AFI. 'contract1, contract2, contract3 ...'")
				.required()
				.argName("name")
				.hasArg()
				.build();
	}
	
	private static ArrayList<String> parseCCCsToArray(String cccs) {
		ArrayList<String> cccList = new ArrayList<String>();
		
		if(cccs.contains(",")) {
			String[] cccsArr = cccs.split(", ");
			for( int i = 0; i < cccsArr.length; i++) {
				cccList.add(cccsArr[i]);
			}
		}else { //Only one ccc
			cccList.add(cccs);
		}
		return cccList;
	}

	private static Long parseDate(String dateStr) {
		Integer year = Integer.parseInt(dateStr.split("-")[0]);
		Integer month = Integer.parseInt(dateStr.split("-")[1]) - 1;
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		
		return cal.getTimeInMillis();
	}
	
	private static ArrayList<Integer> parseContractsToArray(String contracts) {
		ArrayList<Integer> contractsList = new ArrayList<Integer>();
		
		if(contracts.contains(",")) {
			String[] cccsArr = contracts.split(", ");
			for( int i = 0; i < cccsArr.length; i++) {
				contractsList.add(Integer.parseInt(cccsArr[i]));
			}
		}else { //Only one contract
			contractsList.add(Integer.parseInt(contracts));
		}
		return contractsList;
	}
	
	// ********************************************************************************************************************************************
	//													GENERATE JSON AGRARIAN
	// ********************************************************************************************************************************************

	@SuppressWarnings({ "unchecked" })
	public static JSONObject getAgrarianInfo(ArrayList<String> cccList, Long findingDate, ArrayList<Integer> selectedContracts, Connection connection) throws SQLException {
		
		// Get dslContext for given connection
		AONContext ctx = new AONContext(connection);
		DSLContext dslContext = ctx.getDslContext();
		
		// Given findingDate set start and end date
		Calendar startDate = Calendar.getInstance();
		startDate.setTimeInMillis(findingDate);
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(findingDate);
		endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
		
		Date startDateSQL = new Date(startDate.getTimeInMillis());
		
		// Create JSONObject Agrarian JSON
		JSONObject agrarianJSON = new JSONObject();
		
		try {
			
			// GET AuthKey from DB
			String authKey = getAuthKeyFromDomain(dslContext, cccList.get(0));
			String enterpriseName = getEnterpriseName(dslContext, cccList.get(0));
			
			//ETI
			JSONObject eti = new JSONObject();
			eti.put("authkey", authKey);			
			eti.put("payrollProvider", "498");		//Proveedor de nominas ESFERALIA NETWORKS, S.A.
			eti.put("fileName", null);
			eti.put("prorityCode", "N");
			agrarianJSON.put("ETI", eti);
			
			JSONArray enterprises = new JSONArray();
			Integer cccUsed = 0;
			
			for(String ccc: cccList) {
				
				Map<Integer, List<AgrarianJourney>> contractsJourney = getAgrarianJourneyoDB(findingDate, ccc, dslContext);
				
				// Check if selected contracts are in the contractsJourney of ccc finding
				if(checkContractinJourney(selectedContracts, contractsJourney)) {
					
					// The ccc we have contracts on
					cccUsed ++;
					
					JSONObject enterprise = new JSONObject();
					
					//EMP
					JSONObject emp = new JSONObject();
					emp.put("cccProvince", ccc.substring(0, 2));
					emp.put("ccc", ccc.substring(2, ccc.length()));
					emp.put("cccRegimePrincipal", "");
					emp.put("cccProvincePrincipal", "00"/*_ccc.substring(0, 2)*/);
					emp.put("cccPrincipal", "0000000000000" /* _ccc.substring(2, _ccc.length())*/);
					enterprise.put("EMP", emp);
					
					//RZS
					JSONObject rzsData = new JSONObject();
					rzsData.put("businessmanType", "2");
					rzsData.put("rzsName", removeAccents(enterpriseName));
					enterprise.put("RZS", rzsData);
					
					
					
					//EMPLOYEES
					JSONArray emps = new JSONArray();
					
					for(Integer contractId : selectedContracts) {
						List<AgrarianJourney> journeis = contractsJourney.get(contractId);
						if(null != journeis) {
							JSONObject empl = new JSONObject();
							JSONObject tra = getTRA(contractId, dslContext);
							empl.put("TRA", tra);
							JSONObject ayn = getAYN(contractId, dslContext);
							empl.put("AYN", ayn);
							//Para el caso de las jornadas agrarias esto es constante
							final String fab = "FABMJR000000000000 00000  000000 0000000000000000 0000   N 00000000   ";
							empl.put("FAB", fab);
							JSONObject dra = getDRA(startDateSQL, journeis);
							empl.put("DRA", dra);
							emps.add(empl);
						}
					}
					
					enterprise.put("EMPS", emps);
					
					enterprises.add(enterprise);
				}
			
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
			conf.put("employeeLines", "4");
			conf.put("numEmployees", selectedContracts.size()+"");
			conf.put("numCCCs", cccUsed+"");
			agrarianJSON.put("CONF", conf);
			
		
		} finally {
			if (dslContext != null)
				dslContext.close();
		}

		System.out.println(agrarianJSON);
		return agrarianJSON;
	}
		
	private static boolean checkContractinJourney(ArrayList<Integer> selectedContracts,
			Map<Integer, List<AgrarianJourney>> contractsJourney) {
		
		for(Integer contractId : selectedContracts) {
			if(contractsJourney.containsKey(contractId))
				return true;
		}
		return false;
	}

	private static String getEnterpriseName(DSLContext dslContext, String ccc) {
		Record registryRecord = dslContext.select().from(REGISTRY)
			.where(REGISTRY.ID.in(
				dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY)
					.where(ENTERPRISE_ACTIVITY.ID.in(
						dslContext.select(ENTERPRISE_CCC.ENTERPRISE_ACTIVITY).from(ENTERPRISE_CCC)
							.where(ENTERPRISE_CCC.CCC.eq(ccc))
					))
			)).fetchOne();
		
		return null == registryRecord ? null : registryRecord.get(REGISTRY.NAME);
	}

	private static String getAuthKeyFromDomain(DSLContext dslContext, String ccc) {
		
		// Prepare aunthKey
		String authKey = "";
		
		Result<Record> domainRecords = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.in(
						dslContext.select(ENTERPRISE_CCC.DOMAIN).from(ENTERPRISE_CCC)
							.where(ENTERPRISE_CCC.CCC.eq(ccc))
				))
				.fetch();
		
		Integer parentDomainId = domainRecords.get(0).get(DOMAIN.PARENT);
		Integer domainId = domainRecords.get(0).get(DOMAIN.ID);
		
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
		
		json.put("firstSurname", fisrtSurname);
		json.put("secondSurname", secondSurname);
		json.put("name", name);
		
		return json;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static JSONObject getDRA(Date startDate, List<AgrarianJourney> journeis) {
		JSONObject json = new JSONObject();
		
		json.put("year", (startDate.getYear()+1900)+"");
		json.put("month", (startDate.getMonth()+1)+"");
		
		Calendar checkDate = Calendar.getInstance();
		checkDate.setTimeInMillis(startDate.getTime());
		checkDate.set(Calendar.HOUR_OF_DAY, checkDate.getActualMinimum(Calendar.HOUR_OF_DAY));
		checkDate.set(Calendar.MINUTE, checkDate.getActualMinimum(Calendar.MINUTE));
		checkDate.set(Calendar.SECOND, checkDate.getActualMinimum(Calendar.SECOND));
		checkDate.set(Calendar.MILLISECOND, checkDate.getActualMinimum(Calendar.MILLISECOND));
		
		Integer numDays = 31; //checkDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		JSONArray days = new JSONArray();
		
		for(int i = 0; i < numDays; i++) {
			checkDate.set(Calendar.DAY_OF_MONTH, i+1);
			
			if(checkDateAgraria(journeis, checkDate.getTime()))
				days.add(i, "S");
			else
				days.add(i, " ");
		}
		
		json.put("days", days);
		
		return json;
	}
	
	private static boolean checkDateAgraria(List<AgrarianJourney> journiesList, java.util.Date date) {
		for(AgrarianJourney journey : journiesList){
			Calendar startDate = Calendar.getInstance();
			startDate.setTimeInMillis(journey.getStartDate().getTime());
			startDate.set(Calendar.HOUR_OF_DAY, startDate.getActualMinimum(Calendar.HOUR_OF_DAY));
			startDate.set(Calendar.MINUTE, startDate.getActualMinimum(Calendar.MINUTE));
			startDate.set(Calendar.SECOND, startDate.getActualMinimum(Calendar.SECOND));
			startDate.set(Calendar.MILLISECOND, startDate.getActualMinimum(Calendar.MILLISECOND));
			
			Calendar endDate = Calendar.getInstance();
			endDate.setTimeInMillis(journey.getEndDate().getTime());
			endDate.set(Calendar.HOUR_OF_DAY, endDate.getActualMinimum(Calendar.HOUR_OF_DAY));
			endDate.set(Calendar.MINUTE, endDate.getActualMinimum(Calendar.MINUTE));
			endDate.set(Calendar.SECOND, endDate.getActualMinimum(Calendar.SECOND));
			endDate.set(Calendar.MILLISECOND, endDate.getActualMinimum(Calendar.MILLISECOND));
			
			if((startDate.getTime().before(date) || startDate.getTime().equals(date)) &&
			   (endDate.getTime().after(date) || endDate.getTime().equals(date)))
				
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
	
	private static Map<Integer, List<AgrarianJourney>> getAgrarianJourneyoDB(long findingDate, String cccList, DSLContext dslContext) {
		
		Map<Integer, List<AgrarianJourney>> agrarianJourneyMap = new HashMap<>();
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTimeInMillis(findingDate);
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		startDate.set(Calendar.HOUR_OF_DAY, startDate.getActualMinimum(Calendar.HOUR_OF_DAY));
		startDate.set(Calendar.MINUTE, startDate.getActualMinimum(Calendar.MINUTE));
		startDate.set(Calendar.SECOND, startDate.getActualMinimum(Calendar.SECOND));
		startDate.set(Calendar.MILLISECOND, startDate.getActualMinimum(Calendar.MILLISECOND));
		
		Calendar endDate = Calendar.getInstance();
		endDate.setTimeInMillis(findingDate);
		endDate.set(Calendar.DAY_OF_MONTH, endDate.getActualMaximum(Calendar.DAY_OF_MONTH));
		endDate.set(Calendar.HOUR_OF_DAY, endDate.getActualMinimum(Calendar.HOUR_OF_DAY));
		endDate.set(Calendar.MINUTE, endDate.getActualMinimum(Calendar.MINUTE));
		endDate.set(Calendar.SECOND, endDate.getActualMinimum(Calendar.SECOND));
		endDate.set(Calendar.MILLISECOND, endDate.getActualMinimum(Calendar.MILLISECOND));
		
		Result<Record> agrarianJourney = dslContext.select().from(CONTRACT_DATA)
			.where(CONTRACT_DATA.CONTRACT.in(
				dslContext.select(CONTRACT.ID).from(CONTRACT)
					.where(CONTRACT.ENTERPRISE_CCC.in(
							dslContext.select(ENTERPRISE_CCC.ID).from(ENTERPRISE_CCC).where(ENTERPRISE_CCC.CCC.in(cccList))
					))
					.fetch()	
			))
			.and(CONTRACT_DATA.NAME.eq("JORNADAS_REALES"))
			.and(CONTRACT_DATA.START_DATE.greaterOrEqual(new Date(startDate.getTimeInMillis())))
			.and(CONTRACT_DATA.END_DATE.lessOrEqual(new Date(endDate.getTimeInMillis())))
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
			
			
//			System.out.println(
//					"Contract : " + r.get(CONTRACT_DATA.CONTRACT) +
//					" JORNADAS -> StartDate : " + r.get(CONTRACT_DATA.START_DATE) +
//					" EndDate : " + r.get(CONTRACT_DATA.END_DATE) +
//					" Nombre : " + personRecords.get(0).get(PERSON.NAME) +
//					" Apellido : " + personRecords.get(0).get(PERSON.FIRST_SURNAME)
//			);
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
}
