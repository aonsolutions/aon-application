package com.esferalia.aon.payroll.tgss.cra;

import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDatabaseOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDbPasswordOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDbUserOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getHostNameOption;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import com.esferalia.aon.salary.enumeration.PaymentType;

public class Cra {

	// ********************************************************************************************************************************************
	//																MAIN
	// ********************************************************************************************************************************************
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException{

		//@formatter:off
		Option hostName = getHostNameOption();
		Option user = getDbUserOption();
		Option password = getDbPasswordOption();
		Option database = getDatabaseOption();
		Option cccOpt = getCCCOption();
		Option findingDateOpt = getFindingDateOption();

		Options options = new Options()
				.addOption(hostName)
				.addOption(user)
				.addOption(password)
				.addOption(database)
				.addOption(cccOpt)
				.addOption(findingDateOpt)
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
			List<String> cccList = parseCCCsToArray(cccs);
			
			Long findingDate = parseDate(cmd.getOptionValue(findingDateOpt.getLongOpt()));
			
			String agrarianAFI = MainCRAGenerator.generateMainCRA(getMainCRAByCRA(cccList, findingDate, connection));
			System.out.println(agrarianAFI);

		} catch (ParseException e) {
			// Oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// Automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("CRA", options);

		}

	}

	private static List<String> parseCCCsToArray(String cccs) {
		List<String> cccList = new ArrayList<String>();
		
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

	private static Option getCCCOption() {
		return Option.builder("c")
				.longOpt("cccs")
				.desc("CCCs to find CRA. 'ccc1, ccc2, ccc3 ...'")
				.required()
				.argName("name")
				.hasArg()
				.build();
	}

	private static Option getFindingDateOption() {
		return Option.builder("d")
				.longOpt("date")
				.desc("Date to find CRA 'yyyy-mm-dd'")
				.required()
				.argName("name")
				.hasArg()
				.build();
	}
	
	// ********************************************************************************************************************************************
	//													GENERATE JSON AGRARIAN
	// ********************************************************************************************************************************************
	
	@SuppressWarnings("unchecked")
	public static JSONObject getMainCRAByCRA(List<String> cccList, long findingDate, Connection connection)  {
		
		// Get dslContext for given connection
		AONContext ctx = new AONContext(connection);
		DSLContext dslContext = ctx.getDslContext();
		
		//TEMP CCC
		//String ccc = cccList.get(0);
		
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
		String authKey = getAuthKeyFromDomain(dslContext, cccList.get(0));
		
		// ETI
		JSONObject eti = new JSONObject();
		eti.put("authkey", authKey);
		eti.put("fileName", null);
		eti.put("prorityCode", "N");
		
		mainCRAJSON.put("ETI", eti);
		
		// Prepare JSON CCCs
		JSONArray jsonCCCs = new JSONArray();
		
		for(int k=0; k<cccList.size(); k++) {
			
			String ccc = cccList.get(k);
		
			// GET Salaries from DB (employees)
			Result<Record> salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.CHARGE_DATE.between(startDateSQL, endDateSQL))
					.and(SALARY.CCC.eq(ccc))
					.and(SALARY.TYPE.eq((byte)0))
					.and(SALARY.SS_REGIME.notEqual((byte)3))
					.fetch();
			
			// EnterpriseCCCRecord
			Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
					.where(ENTERPRISE_CCC.CCC.eq(ccc))
					.fetchOne();
			
			// Prepare ERRORS
			JSONArray errors = new JSONArray();
			
			// Prepare CCCi
			JSONObject ccci = new JSONObject();
			
			if(salaryRecords.isEmpty()){
				
				JSONObject err = new JSONObject();
				err.put("ERR", "No hay ninguna nómina emitida para este periodo.");
				errors.add(err);
			
			}else {
			
				// Prepare DDE
				JSONObject dde = new JSONObject();
	
				dde.put("cccRegime", parseSS_Regime(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE)));
				dde.put("ccc", ccc);
				dde.put("year", startDate.get(Calendar.YEAR));
				dde.put("month", startDate.get(Calendar.MONTH)+1);
				
				// Prepare TRBS
				JSONArray trbs = new JSONArray();
				
				for(Record salary: salaryRecords) {
					// Prepare TRB
					JSONObject trb = new JSONObject();
					
					trb.put("numAfilicion", salary.get(SALARY.SOCIAL_SECURITY_NUMBER));
					
					// Prepare CRES
					JSONArray cres = new JSONArray();
					
					// GET Salaries_Payment from Salary to get CRA type 
					Result<Record> salaryPaymentRecords = dslContext.select().from(SALARY_PAYMENT)
							.where(SALARY_PAYMENT.SALARY.eq(salary.get(SALARY.ID)))
							.orderBy(SALARY_PAYMENT.TYPE)
							.fetch();
					
					// Get first typeCRA to compare with to accumulate, first iteration will be true always
					PaymentType typeCRA = (salaryPaymentRecords.size() == 0) ? null : PaymentType.values()[salaryPaymentRecords.get(0).get(SALARY_PAYMENT.TYPE)];;
					
					// Initialice craAmount for accumulation
					Double craAmount = 0.00;
					
					for (int i=0; i<salaryPaymentRecords.size(); i++) {
						
						PaymentType craType = PaymentType.values()[salaryPaymentRecords.get(i).get(SALARY_PAYMENT.TYPE)];
						
						if(typeCRA == craType) {
							
							// Accumulate craAmount
							craAmount += (salaryPaymentRecords.get(i).get(SALARY_PAYMENT.QUOTE) > 0) ? salaryPaymentRecords.get(i).get(SALARY_PAYMENT.QUOTE) : salaryPaymentRecords.get(i).get(SALARY_PAYMENT.AMOUNT);
							
							// Last iteration
							if(i+1 == salaryPaymentRecords.size()) {
								
								// Try to add Cre to Cres
								addCreToCres(craAmount, craType, cres);
								
							}
							
							continue;
							
						} else {
							
							// Try to add Cre to Cres
							addCreToCres(craAmount, typeCRA, cres);
							
							// Update typeCra
							typeCRA = craType;
							
							// Update craAmount
							craAmount = (salaryPaymentRecords.get(i).get(SALARY_PAYMENT.QUOTE) > 0) ? salaryPaymentRecords.get(i).get(SALARY_PAYMENT.QUOTE) : salaryPaymentRecords.get(i).get(SALARY_PAYMENT.AMOUNT);
							
							// Last iteration
							if(i+1 == salaryPaymentRecords.size()) {
								
								// Try to add Cre to Cres
								addCreToCres(craAmount, craType, cres);
							}
						}
						
					}
					
					// If having CRES add to TRB and TRB to TRBS
					if(cres.size() > 0) {
						
						trb.put("CRES", cres);
						trbs.add(trb);
					
					}
						
				}
				
				// Adding TRBS to DDE
				dde.put("TRBS", trbs);
				
				// Adding DDE (Normal salaries) to MainCRAJSON 
				ccci.put("DDE", dde);
			}
			
			// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			// 											NOMINA ATRASOS
			// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			// GET Atrasos SALARY from DB (employees)
			salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.CHARGE_DATE.between(startDateSQL, endDateSQL))
					.and(SALARY.CCC.eq(ccc))
					.and(SALARY.TYPE.eq((byte)3))
					.and(SALARY.SS_REGIME.notEqual((byte)3))
					.fetch();
			
			if(salaryRecords.isEmpty()){
				
				JSONObject err = new JSONObject();
				err.put("ERR1", "No hay ninguna nómina emitida para este periodo.");
				errors.add(err);
			
			}else {
			
				// Prepare DDEAS
				JSONArray ddeas = new JSONArray();
				
				for(Record salary: salaryRecords) {
					
					// Get salaryId
					Integer salaryId = salary.get(SALARY.ID);
					
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
					
					for(Record salaryData: salaryDatas){
						
						// Get craAmount
						Double craAmount = Double.parseDouble(salaryData.get(SALARY_DATA.EXPRESSION));
						
						if(craAmount > 0){
							
							if(salaryData.get(SALARY_DATA.START_DATE).before(endDateSQL)){
								
								// Prepare DDEA
								JSONObject ddea = new JSONObject();
								
								ddea.put("cccRegime", parseSS_Regime(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE)));
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
							
								trba.put("numAfilicion", salary.get(SALARY.SOCIAL_SECURITY_NUMBER));
							
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
					}	
				}
				
				// Adding DDEAS (Delay salaries) to MainCRAJSON, but first parseDDEAS to accumulate amount of same craType 
				ccci.put("DDEAS", parseDDEAS(ddeas));
			}
			
			// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			// 											FINIQUITOS
			// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
			
			//GET Settlements SALARY from DB (employees) use ISSUE_DATE cause settlement can have OLD startDate...
			salaryRecords = dslContext.select().from(SALARY)
					.where(SALARY.CCC.eq(ccc))
						.and(SALARY.ISSUE_DATE.between(startDateSQL, endDateSQL)
								.or(SALARY.END_DATE.between(startDateSQL, endDateSQL)))
						.and(SALARY.TYPE.eq((byte)2))
						.and(SALARY.SS_REGIME.notEqual((byte)3))
					.fetch();
			
			if(!salaryRecords.isEmpty()){
				
				// Prepare FINIQ
				JSONObject finiq = new JSONObject();
				
				finiq.put("cccRegime", parseSS_Regime(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE)));
				finiq.put("ccc", ccc);
				finiq.put("year", startDate.get(Calendar.YEAR));
				finiq.put("month", startDate.get(Calendar.MONTH)+1);
				
				// Prepare TRBSF
				JSONArray trbsf = new JSONArray();
				
				for(Record salary: salaryRecords) {
					
					// Get salaryId
					Integer salaryId = salary.get(SALARY.ID);
					
					// Get salaryDatas of Settelment salary
					Result<Record> salaryDatas = dslContext.select().from(SALARY_DATA)
							.where(SALARY_DATA.SALARY.eq(salaryId))
							.and(SALARY_DATA.NAME.eq("BASE_CGC"))
							.and(SALARY_DATA.START_DATE.between(startDateSQL, endDateSQL))
							.fetch();
					
					// Prepare TRBF
					JSONObject trbf = new JSONObject();
					
					trbf.put("numAfilicion", salary.get(SALARY.SOCIAL_SECURITY_NUMBER));
					
					// Prepare CRES
					JSONArray cres = new JSONArray();
					
					for(Record salatyData: salaryDatas){
						
						// Get CRA amount
						Double craAmount = Double.parseDouble(salatyData.get(SALARY_DATA.EXPRESSION));
						
						if(craAmount > 0){
							
							// Get typeCRA ¿always 6?
							PaymentType typeCRA = PaymentType.values()[6];
							
							// Try to add Cre to Cres
							addCreToCres(craAmount, typeCRA, cres);
							
						}
					}
					
					Result<Record> salaryPayments = dslContext.select().from(SALARY_PAYMENT)
							.where(SALARY_PAYMENT.SALARY.eq(salaryId))
							.and(SALARY_PAYMENT.TYPE.ne((byte)6))
							.fetch();
					
					for(Record salaryPayment : salaryPayments) {
						// Get typeCRA ¿always 6?
						PaymentType typeCRA = PaymentType.values()[salaryPayment.get(SALARY_PAYMENT.TYPE)];
						
						// Try to add Cre to Cres
						addCreToCres(salaryPayment.get(SALARY_PAYMENT.AMOUNT), typeCRA, cres);
					}
					
					// Add CRES to TRBF
					trbf.put("CRES", cres);
					
					// Add TRBF to TRBSF
					trbsf.add(trbf);
					
				}
				
				// Add TRBSF to FINIQ
				finiq.put("TRBS", trbsf);
				
				// Adding FINIQ (Settelment salaries) to MainCRAJSON  
				ccci.put("FINIQ", finiq);
			}
			
			// Adding ERRORS to MainCRAJSON  
			ccci.put("ERRS", errors);
			
			jsonCCCs.add(ccci);
		}
		
		mainCRAJSON.put("CCCs", jsonCCCs);
		
		System.out.println(mainCRAJSON);
		
		return mainCRAJSON;
	}
	
	// ********************************************************************************************************************************************
	//													AUXILIAR METHODS
	// ********************************************************************************************************************************************
	
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
		if(craAmount > 0){
			cre.put("concept", getCRADescription(craType));
			cre.put("include_exclude", craType.isBBCCIncluded() ? "I" : "E");
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
			
			if(creAmount != creAmountAux) {
				
				// If they have the same concept accumulate it, otherwise add CRE to CRES of TRB given
				if(creConcept.equals(creConceptAux)) {
					Integer totalAmount = Integer.parseInt(creAmount) + Integer.parseInt(creAmountAux);
					((JSONObject)cres.get(i)).put("amount", totalAmount.toString());
				} else
					((JSONArray) trb.get("CRES")).add(cre);
				
			}
		}
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
	
	private static String getCRADescription(PaymentType craType) {
		String type = craType.toString().split("_")[1];
		return type;
	}
	
}
