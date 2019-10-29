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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
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
	
	@SuppressWarnings("static-access")
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

		// create the parser
		CommandLineParser parser = new GnuParser();

		try {
			Class.forName(com.mysql.cj.jdbc.Driver.class.getName());
			
			// parse the command line arguments
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
			
			String ccc = cmd.getOptionValue(cccOpt.getLongOpt());
			Long findingDate = parseDate(cmd.getOptionValue(findingDateOpt.getLongOpt()));
			
			String agrarianAFI = MainCRAGenerator.generateMainCRA(getMainCRAByCRA(ccc, findingDate, connection));
			System.out.println(agrarianAFI);

		} catch (ParseException e) {
			// oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("CRA", options);

		}

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

	@SuppressWarnings("static-access")
	private static Option getCCCOption() {
		return OptionBuilder.withArgName("name").hasArg()
				.withLongOpt("ccc")
				.isRequired(true)
				.withDescription("CCC to find CRA.")
				.create("c");
	}

	@SuppressWarnings("static-access")
	private static Option getFindingDateOption() {
		return OptionBuilder.withArgName("name").hasArg()
				.withLongOpt("date")
				.isRequired(true)
				.withDescription("Date to find CRA 'yyyy-mm-dd'")
				.create("d");
	}
	
	// ********************************************************************************************************************************************
	//													GENERATE JSON AGRARIAN
	// ********************************************************************************************************************************************
	
	@SuppressWarnings("unchecked")
	public static JSONObject getMainCRAByCRA(String ccc, long findingDate, Connection connection)  {
		
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
		Date endDateSQL = new Date(endDate.getTimeInMillis());
		
		// Create JSONObject mainCra
		JSONObject mainCRAJSON = new JSONObject();
			
		//GET AuthKey from DB
		Record domainRecord = dslContext.select().from(DOMAIN)
				.where(DOMAIN.ID.in(
						dslContext.select(ENTERPRISE_CCC.DOMAIN).from(ENTERPRISE_CCC)
							.where(ENTERPRISE_CCC.CCC.eq(ccc))
				))
				.fetchOne();
		
		Integer parentDomainId = domainRecord.get(DOMAIN.PARENT);
		Integer _domainId = domainRecord.get(DOMAIN.ID);
		
		Record appParamRecord = dslContext.select().from(APP_PARAM)
				.where(APP_PARAM.NAME.eq("PAY_authorization_key_PAY"))
				.and(APP_PARAM.DOMAIN.eq(_domainId))
				.fetchOne();
		
		String authKey = "";
		
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
				.where(SALARY.CHARGE_DATE.between(startDateSQL, endDateSQL))
				.and(SALARY.CCC.eq(ccc))
				.and(SALARY.TYPE.eq((byte)0))
				.and(SALARY.SS_REGIME.notEqual((byte)3))
				.fetch();
		
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
			dde.put("year", startDate.get(Calendar.YEAR));
			dde.put("month", startDate.get(Calendar.MONTH));
			
			JSONArray trbs = new JSONArray();
			
			for(Record salary: salaryRecords) {
				JSONObject trb = new JSONObject();
				
				trb.put("numAfilicion", salary.get(SALARY.SOCIAL_SECURITY_NUMBER));
				
				JSONArray cres = new JSONArray();
				
				//GET Salaries from DB (employees)
				Result<Record> salaryPaymentRecords = dslContext.select().from(SALARY_PAYMENT)
						.where(SALARY_PAYMENT.SALARY.eq(salary.get(SALARY.ID)))
						.fetch();
				
				PaymentType typeCRA = (salaryPaymentRecords.size() == 0) ? null : PaymentType.values()[salaryPaymentRecords.get(0).get(SALARY_PAYMENT.TYPE)];;
				Double craAmount = 0.00;
				JSONObject cre = new JSONObject();
				
				for (int i=0; i<salaryPaymentRecords.size(); i++) {
					
					PaymentType craType = PaymentType.values()[salaryPaymentRecords.get(i).get(SALARY_PAYMENT.TYPE)];
					
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
							
							if(craAmount > 0){
								cre.put("concept", getCRADescription(craType));
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
							cre.put("concept", getCRADescription(typeCRA));
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
							
							if(craAmount > 0){
								cre.put("concept", getCRADescription(craType));
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
		
		//GET Atrasos SALARY from DB (employees)
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
		
			//DDEAS
			JSONArray ddeas = new JSONArray();
			
			for(Record salary: salaryRecords) {
				Integer salaryId = salary.get(SALARY.ID);
				
				Result<Record> salaryDatas = dslContext.select().from(SALARY_DATA)
						.where(SALARY_DATA.SALARY.eq(salaryId))
						.and(SALARY_DATA.NAME.eq("BASE_CGC"))
						.fetch();
				
				Byte salaryPaymentType = dslContext.select(SALARY_PAYMENT.TYPE).from(SALARY_PAYMENT)
						.where(SALARY_PAYMENT.SALARY.eq(salaryId))
						.limit(1)
						.fetchOne()
						.getValue(SALARY_PAYMENT.TYPE);
				
				for(Record salaryData: salaryDatas){
					Double craAmount = Double.parseDouble(salaryData.get(SALARY_DATA.EXPRESSION));
					
					if(craAmount > 0){
						if(salaryData.get(SALARY_DATA.START_DATE).before(endDateSQL)){		
							JSONObject ddea = new JSONObject();
							enterpriseCCCRecords = dslContext.select().from(ENTERPRISE_CCC)
									.where(ENTERPRISE_CCC.CCC.eq(salary.get(SALARY.CCC)))
									.fetch();
							
							ddea.put("cccRegime", parseSS_Regime(enterpriseCCCRecords.get(0).get(ENTERPRISE_CCC.TYPE)));
							ddea.put("ccc", salary.get(SALARY.CCC));
							
							Calendar salaryDataStartDate = Calendar.getInstance();
							salaryDataStartDate.setTimeInMillis(salaryData.get(SALARY_DATA.START_DATE).getTime());
							
							ddea.put("year", salaryDataStartDate.get(Calendar.YEAR));
							ddea.put("month", salaryDataStartDate.get(Calendar.MONTH) + 1);
						
							JSONArray trbsa = new JSONArray();
							JSONObject trba = new JSONObject();
						
							trba.put("numAfilicion", salary.get(SALARY.SOCIAL_SECURITY_NUMBER));
						
							JSONArray cres = new JSONArray();
							
							PaymentType typeCRA = PaymentType.values()[salaryPaymentType];
							JSONObject cre = new JSONObject();
							
							String craAmountStr = String.format( "%.2f", craAmount );
							String amount = "";
							if(craAmountStr.contains(","))
								amount = craAmountStr.split(",")[0] + craAmountStr.split(",")[1];
							else
								amount = craAmountStr.split("[.]")[0] + craAmountStr.split("[.]")[1];
							
							cre.put("concept", getCRADescription(typeCRA));
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
				.where(SALARY.CCC.eq(ccc))
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
			finiq.put("year", startDate.get(Calendar.YEAR));
			finiq.put("month", startDate.get(Calendar.MONTH));
			
			JSONArray trbsf = new JSONArray();
			for(Record salary: salaryRecords) {
				Integer salaryId = salary.get(SALARY.ID);
				
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
						
						PaymentType typeCRA = PaymentType.values()[6];
						JSONObject cre = new JSONObject();
						
						String craAmountStr = String.format( "%.2f", craAmount );
						String amount = "";
						
						if(craAmountStr.contains(","))
							amount = craAmountStr.split(",")[0] + craAmountStr.split(",")[1];
						else
							amount = craAmountStr.split("[.]")[0] + craAmountStr.split("[.]")[1];
						
						cre.put("concept", getCRADescription(typeCRA));
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
		
		//System.out.println(mainCRAJSON);
		
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
	
	private static String getCRADescription(PaymentType craType) {
		String type = craType.toString().split("_")[1];
		return type;
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
	
}
