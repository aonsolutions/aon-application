package com.esferalia.aon.payroll.sepe.certifica2;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.EnterpriseActivity.ENTERPRISE_ACTIVITY;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.SalaryData.SALARY_DATA;
import static com.esferalia.aon.jooq.tables.SalaryPayment.SALARY_PAYMENT;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDatabaseOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDbPasswordOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getDbUserOption;
import static com.esferalia.aon.payroll.tgss.creta.Bases.getHostNameOption;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.JAXBException;

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
import com.esferalia.aon.payroll.tgss.cra.StringUtils;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.COTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CUENTACOTIZACIONTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.CertificadoEmpresa;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.EMPRESATYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE;
import com.esferalia.aon.sepe.api.certificados.certificadoEmpresa.TRABAJADORTYPE.DatosVacacionesCotizadas;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;

public class Certifica2 {
	
	private static SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy");
	private static SimpleDateFormat monthDateFormat = new SimpleDateFormat("MM");

	public static void main(String[] args) {
		//@formatter:off
		Option hostName = getHostNameOption();
		Option user = getDbUserOption();
		Option password = getDbPasswordOption();
		Option database = getDatabaseOption();
		Option contract = getContractOption();
		Option reasonCode = getReasonCodeOption();

		Options options = new Options()
				.addOption(hostName)
				.addOption(user)
				.addOption(password)
				.addOption(database)
				.addOption(contract)
				.addOption(reasonCode)
				;	
		//@formatter:on

		// Create the parser
		CommandLineParser parser = new DefaultParser();
		Connection connection = null;
		try {
			Class.forName(com.mysql.cj.jdbc.Driver.class.getName());
			
			// Parse the command line arguments
			CommandLine cmd = parser.parse(options, args);

			connection = DriverManager.getConnection(
					String.format(
							"jdbc:mysql://%s:%d/%s",
							cmd.getOptionValue(hostName.getLongOpt(), "127.0.0.1"),
							3306, 
							cmd.getOptionValue(database.getLongOpt())
					),
					cmd.getOptionValue(user.getLongOpt()),
					cmd.getOptionValue(password.getLongOpt()));
			
			// Contract ID
			Integer contractId = Integer.parseInt(cmd.getOptionValue(contract.getLongOpt()));
			String suspensionReasonCode = cmd.getOptionValue(reasonCode.getLongOpt());
			
			// Get dslContext for given connection
			AONContext aonContext = new AONContext(connection);
			DSLContext dslContext = aonContext.getDslContext();
			
			getCertifica2(dslContext, contractId, suspensionReasonCode, System.out);
			
//			JSONObject certifica2JSON = getCertifica2JSON(dslContext, contractId, suspensionReasonCode);
//			System.out.println(certifica2JSON.toString());
			
		} catch (ParseException | ClassNotFoundException | SQLException e) {
			// Oops, something went wrong
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			// Automatically generate the help statement
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("Certific@2", options);
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	// ----------------------------------------------------- Args Methods
	
	private static Option getContractOption() {
		return Option.builder("c")
				.longOpt("contract")
				.desc("Contract id")
				.required()
				.hasArg()
				.build();
	}
	
	private static Option getReasonCodeOption() {
		return Option.builder("r")
				.longOpt("reasonCode")
				.desc("Suspension reason code")
				.required()
				.hasArg()
				.build();
	}
	
	// ---------------------------------------------------------------------------------------------------------- 
	// ----------------------------------------------------- GetCertifica2JSON
	// ----------------------------------------------------------------------------------------------------------
	
	public static String getCertifica2(DSLContext dslContext, Integer contractId, String suspensionReasonCode, OutputStream os) {
		// ---------------------------------------------------- Employee Data
		
		Record contractRecord = dslContext.select().from(CONTRACT)
				.where(CONTRACT.ID.eq(contractId))
				.fetchOne();
		
		Integer personId = contractRecord.get(CONTRACT.PERSON);
		Integer enterpriseCCCId = contractRecord.get(CONTRACT.ENTERPRISE_CCC);
		Integer enterpriseActivityId = contractRecord.get(CONTRACT.ENTERPRISE_ACTIVITY);
		
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		Long contractDuration = getDaysBetween(startDate, endDate);
		
		Record personRecord = dslContext.select().from(PERSON)
				.where(PERSON.REGISTRY.eq(personId))
				.fetchOne();
		
		String name = personRecord.get(PERSON.NAME);
		String surName = personRecord.get(PERSON.FIRST_SURNAME);
		String ssNum = personRecord.get(PERSON.SOCIAL_SECURITY_NUM);
		
		if(checkIfNotExist(ssNum))
			return "No existe numero de la Seguridad Social para esta persona";
		
		Record employeeRegistry = dslContext.select().from(REGISTRY)
				.where(REGISTRY.ID.eq(personId))
				.fetchOne();
		
		String dni = employeeRegistry.get(REGISTRY.DOCUMENT);
		
		if(checkIfNotExist(dni))
			return "No existe documento de identidad para esta persona";
		
		String contractType = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("TC2"))
				.fetchOne(CONTRACT_DATA.EXPRESSION);
		
		String tc2 = normalizeString(contractType);
		
		String quoteGroupType = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION"))
				.fetchOne(CONTRACT_DATA.EXPRESSION);
		
		if(checkIfNotExist(quoteGroupType))
			return "No existe grupo de cotizacion para este contrato";
		
		String quoteGroup = normalizeString(quoteGroupType);
		
		String cnoType = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA)
				.where(CONTRACT_DATA.CONTRACT.eq(contractId))
				.and(CONTRACT_DATA.NAME.eq("CNO"))
				.fetchOne(CONTRACT_DATA.EXPRESSION);
		
		if(checkIfNotExist(cnoType))
			return "No existe CNO para este contrato";
		
		String cno = normalizeString(cnoType);
		
		// ---------------------------------------------------- Enterprise Data
		
		Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC)
				.where(ENTERPRISE_CCC.ID.eq(enterpriseCCCId))
				.fetchOne();
		
		String completeCCC = parseSS_Regime(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE)) + enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC);
		
		Record enterpriseRegistryRecord = dslContext.select().from(REGISTRY).where(REGISTRY.ID.eq(
					dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY)
						.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseActivityId))
						.fetchOne(ENTERPRISE_ACTIVITY.ENTERPRISE)
			)).fetchOne();
		
		String enterpriseCIF = enterpriseRegistryRecord.get(REGISTRY.DOCUMENT);
		
		// ---------------------------------------------------- Salaries Data
		
		List<Certifica2Info> certifica2List = new ArrayList<Certifica2Info>();
		Integer maxDays = 0;
		
		Result<Record> salariesRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.TYPE.eq((byte)0))
				.orderBy(SALARY.ID.desc())
				.fetch();
		
		for(Record salary : salariesRecords) {
			if(maxDays > 180)
				break;
			
			Integer salaryId = salary.get(SALARY.ID);
			
			Date salaryStartDate = salary.get(SALARY.START_DATE);
			Date salaryEndDate = salary.get(SALARY.END_DATE);
			Long salaryDaysBetween = getDaysBetween(salaryStartDate, salaryEndDate);
			
			List<String> baseCGCRecords = dslContext.select(SALARY_DATA.EXPRESSION).from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(salaryId))
					.and(SALARY_DATA.NAME.eq("BASE_CGC"))
					.fetch(SALARY_DATA.EXPRESSION);
			
			Double baseCGC = 0.00;
			
			// Using for, cause can be periods in the same Salary
			for(String baseCGCStr : baseCGCRecords) {
				baseCGC += Double.parseDouble(baseCGCStr);
			}
			
			List<String> baseCGPRecords = dslContext.select(SALARY_DATA.EXPRESSION).from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(salaryId))
					.and(SALARY_DATA.NAME.eq("BASE_CGP"))
					.fetch(SALARY_DATA.EXPRESSION);
			
			Double baseCGP = 0.00;
			
			// Using for, cause can be periods in the same Salary
			for(String baseCGPStr : baseCGPRecords) {
				baseCGP += Double.parseDouble(baseCGPStr);
			}
			
			// Initialize Certifica2Info
			Certifica2Info certifica2Info = null;
			
			// Ya has cumplido los 180 dias de registro
			if(maxDays + salaryDaysBetween > 180) {
				 Long restDays = salaryDaysBetween - (maxDays + salaryDaysBetween - 180);
				 
				 certifica2Info = new Certifica2Info(
							yearDateFormat.format(salaryStartDate), 
							monthDateFormat.format(salaryStartDate), 
							restDays.intValue(), 
							baseCGC / 30 * restDays.intValue(), 
							baseCGP / 30 * restDays.intValue());
				 
				 maxDays += salaryDaysBetween.intValue();
				 
			} else {
				certifica2Info = new Certifica2Info(
						yearDateFormat.format(salaryStartDate), 
						monthDateFormat.format(salaryStartDate), 
						salaryDaysBetween.intValue(), 
						baseCGC, 
						baseCGP);
				
				maxDays += salaryDaysBetween.intValue();
			}
			
			certifica2List.add(certifica2Info);
			
		}
		
		// Check Settle for unEnjoy Holidays
		Result<Record> settlementRecords = dslContext.select().from(SALARY)
				.where(SALARY.CONTRACT.eq(contractId))
				.and(SALARY.TYPE.eq((byte)2))
				.orderBy(SALARY.ID.desc())
				.fetch();
		
		Integer settlementId = settlementRecords.get(0).get(SALARY.ID);
		
		Date chargeDate = settlementRecords.get(0).get(SALARY.CHARGE_DATE);
		Date settlementEndDate = settlementRecords.get(0).get(SALARY.END_DATE);
		Long settlementDaysBetween = getDaysBetween(chargeDate, settlementEndDate);
		
		Record holidaysRecord = dslContext.select().from(SALARY_PAYMENT)
				.where(SALARY_PAYMENT.SALARY.eq(settlementId))
				.and(SALARY_PAYMENT.TYPE.eq((byte)6))
				.fetchOne();
		
		Double baseCGC = holidaysRecord.get(SALARY_PAYMENT.AMOUNT);
		Double baseCGP = holidaysRecord.get(SALARY_PAYMENT.QUOTE);
		
		Certifica2Info settlementCertifica2Info = new Certifica2Info(
				null,
				null,
				settlementDaysBetween.intValue(), 
				baseCGC, 
				baseCGP);
		
		// ---------------------------------------------------- Create CertificadoEmpresa
		
		CertificadoEmpresa certificadoEmpresa = new CertificadoEmpresa();
		
		CUENTACOTIZACIONTYPE cuentaCotizacion = new CUENTACOTIZACIONTYPE();
		
		EMPRESATYPE empresaType = new EMPRESATYPE();
		empresaType.setCIFNIF(enterpriseCIF);
		empresaType.setCCC(completeCCC);
		
		TRABAJADORTYPE trabajadorType = new TRABAJADORTYPE();
		trabajadorType.setDNINIE(dni);
		trabajadorType.setNombre(name);
		trabajadorType.setApellido1(surName);
		trabajadorType.setNumSS(ssNum);
		trabajadorType.setGrupoCotizacion(quoteGroup);
		trabajadorType.setTipoContrato(tc2);
		trabajadorType.setDuracionContrato(StringUtils.leftPad(contractDuration.toString(), 5, '0'));
		trabajadorType.setCodProfesion(StringUtils.rightPad(cno, 7, '0'));
		trabajadorType.setFechaAltaEmpresa(fullDateFormat.format(startDate));
		trabajadorType.setCodCausaSuspension(suspensionReasonCode);
		trabajadorType.setFechaSuspensionExtincion(fullDateFormat.format(endDate));
		trabajadorType.setDiasSalarioTramitacion("00000");
		
		certifica2List.sort(new Comparator<Certifica2Info>() {
			@Override
			public int compare(Certifica2Info o1, Certifica2Info o2) {
				return o2.getMonth().compareTo(o1.getMonth());
			}
		});
		
		for(Certifica2Info certifica2 : certifica2List) {
			COTIZACIONTYPE cotizacionType = new COTIZACIONTYPE();
			
			cotizacionType.setAno(certifica2.getYear());
			cotizacionType.setMes(certifica2.getMonth());
			cotizacionType.setNumDiasCotizados(StringUtils.leftPad(certifica2.getQuotedDays().toString(), 3, '0'));
			cotizacionType.setBaseCotizacionContingenciasComunes(StringUtils.leftPad(String.format("%.2f", certifica2.getBase_cgc()).replaceAll(",", ""), 9, '0'));
			cotizacionType.setBaseCotizacionDesempleo(StringUtils.leftPad(String.format("%.2f", certifica2.getBase_unemployment()).replaceAll(",", ""), 9, '0'));
			
			trabajadorType.getDatosCotizacion().add(cotizacionType);
		}
		
		DatosVacacionesCotizadas datosVacacionesCotizadas = new DatosVacacionesCotizadas();
		datosVacacionesCotizadas.setNumDiasCotizados(StringUtils.leftPad(settlementCertifica2Info.getQuotedDays().toString(), 3, '0'));
		datosVacacionesCotizadas.setBaseCotizacionContingenciasComunes(StringUtils.leftPad(String.format("%.2f", settlementCertifica2Info.getBase_cgc()).replaceAll(",", ""), 9, '0'));
		datosVacacionesCotizadas.setBaseCotizacionDesempleo(StringUtils.leftPad(String.format("%.2f", settlementCertifica2Info.getBase_unemployment()).replaceAll(",", ""), 9, '0'));
		trabajadorType.setDatosVacacionesCotizadas(datosVacacionesCotizadas);
		
		cuentaCotizacion.setDatosEmpresa(empresaType);
		cuentaCotizacion.getDatosTrabajador().add(trabajadorType);
		
		certificadoEmpresa.getCuentaCotizacion().add(cuentaCotizacion);
		
		try {
			Utils.marshal(certificadoEmpresa, os);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	private static JSONObject getCertifica2JSON(DSLContext dslContext, Integer contractId, String suspensionReasonCode) {
		
		// ---------------------------------------------------- Employee Data
		
		Record contractRecord = dslContext.select().from(CONTRACT).where(CONTRACT.ID.eq(contractId)).fetchOne();
		
		Integer personId = contractRecord.get(CONTRACT.PERSON);
		Integer enterpriseCCCId = contractRecord.get(CONTRACT.ENTERPRISE_CCC);
		Integer enterpriseActivityId = contractRecord.get(CONTRACT.ENTERPRISE_ACTIVITY);
		
		Date startDate = contractRecord.get(CONTRACT.START_DATE);
		Date endDate = contractRecord.get(CONTRACT.END_DATE);
		java.util.Date startDateJava = new java.util.Date(startDate.getTime());
		java.util.Date endDateJava = new java.util.Date(endDate.getTime());
		Long contractDuration = Duration.between(startDateJava.toInstant(), endDateJava.toInstant()).toDays() + 1;
		String contractDurationStr = contractDuration + "";
		
		Record personRecord = dslContext.select().from(PERSON).where(PERSON.REGISTRY.eq(personId)).fetchOne();
		
		String name = personRecord.get(PERSON.NAME);
		String surName = personRecord.get(PERSON.FIRST_SURNAME);
		String ssNum = personRecord.get(PERSON.SOCIAL_SECURITY_NUM);
		
		Record employeeRegistry = dslContext.select().from(REGISTRY).where(REGISTRY.ID.eq(personId)).fetchOne();
		
		String dni = employeeRegistry.get(REGISTRY.DOCUMENT);
		
		String contractType = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("TC2")).fetchOne(CONTRACT_DATA.EXPRESSION);
		
		String tc2 = normalizeString(contractType);
		
		String quoteGroupType = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("GRUPO_COTIZACION")).fetchOne(CONTRACT_DATA.EXPRESSION);
		
		String quoteGroup = normalizeString(quoteGroupType);
		
		String cnoType = dslContext.select(CONTRACT_DATA.EXPRESSION).from(CONTRACT_DATA).where(CONTRACT_DATA.CONTRACT.eq(contractId)).and(CONTRACT_DATA.NAME.eq("CNO")).fetchOne(CONTRACT_DATA.EXPRESSION);
		
		String cno = normalizeString(cnoType);
		
		// ---------------------------------------------------- Enterprise Data
		
		Record enterpriseCCCRecord = dslContext.select().from(ENTERPRISE_CCC).where(ENTERPRISE_CCC.ID.eq(enterpriseCCCId)).fetchOne();
		
		String completeCCC = parseSS_Regime(enterpriseCCCRecord.get(ENTERPRISE_CCC.TYPE)) + enterpriseCCCRecord.get(ENTERPRISE_CCC.CCC);
		
		Record enterpriseRegistryRecord = dslContext.select().from(REGISTRY).where(REGISTRY.ID.eq(
					dslContext.select(ENTERPRISE_ACTIVITY.ENTERPRISE).from(ENTERPRISE_ACTIVITY)
						.where(ENTERPRISE_ACTIVITY.ID.eq(enterpriseActivityId))
						.fetchOne(ENTERPRISE_ACTIVITY.ENTERPRISE)
			)).fetchOne();
		
		String enterpriseCIF = enterpriseRegistryRecord.get(REGISTRY.DOCUMENT);
		
		// ---------------------------------------------------- Salaries Data
		
		//TODO : check salaries
		
		List<Certifica2Info> certifica2List = new ArrayList<Certifica2Info>();
		Integer maxDays = 0;
		
		Result<Record> salariesRecords = dslContext.select().from(SALARY).where(SALARY.CONTRACT.eq(contractId)).and(SALARY.TYPE.eq((byte)0)).orderBy(SALARY.ID.desc()).fetch();
		
		for(Record salary : salariesRecords) {
			if(maxDays > 180)
				break;
			
			Integer salaryId = salary.get(SALARY.ID);
			
			Date salaryStartDate = salary.get(SALARY.START_DATE);
			Date salaryEndDate = salary.get(SALARY.END_DATE);
			java.util.Date salaryStartDateJava = new java.util.Date(salaryStartDate.getTime());
			java.util.Date salaryEndDateJava = new java.util.Date(salaryEndDate.getTime());
			Long salaryDaysBetween = Duration.between(salaryStartDateJava.toInstant(), salaryEndDateJava.toInstant()).toDays() + 1;
			
			List<String> baseCGCRecords = dslContext.select(SALARY_DATA.EXPRESSION).from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(salaryId))
					.and(SALARY_DATA.NAME.eq("BASE_CGC"))
					.fetch(SALARY_DATA.EXPRESSION);
			
			Double baseCGC = 0.00;
			
			for(String baseCGCStr : baseCGCRecords) {
				baseCGC += Double.parseDouble(baseCGCStr);
			}
			
			List<String> baseCGPRecords = dslContext.select(SALARY_DATA.EXPRESSION).from(SALARY_DATA)
					.where(SALARY_DATA.SALARY.eq(salaryId))
					.and(SALARY_DATA.NAME.eq("BASE_CGP"))
					.fetch(SALARY_DATA.EXPRESSION);
			
			Double baseCGP = 0.00;
			
			for(String baseCGPStr : baseCGPRecords) {
				baseCGP += Double.parseDouble(baseCGPStr);
			}
			
			Certifica2Info certifica2Info = null;
			
			// Ya has cumplido los 180 dias de registro
			if(maxDays + salaryDaysBetween > 180) {
				 Long restDays = salaryDaysBetween - (maxDays + salaryDaysBetween - 180);
				 
				 certifica2Info = new Certifica2Info(
							yearDateFormat.format(salaryStartDate), 
							monthDateFormat.format(salaryStartDate), 
							restDays.intValue(), 
							baseCGC / 30 * restDays.intValue(), 
							baseCGP / 30 * restDays.intValue());
				 
				 maxDays += salaryDaysBetween.intValue();
				 
			} else {
				certifica2Info = new Certifica2Info(
						yearDateFormat.format(salaryStartDate), 
						monthDateFormat.format(salaryStartDate), 
						salaryDaysBetween.intValue(), 
						baseCGC, 
						baseCGP);
				
				maxDays += salaryDaysBetween.intValue();
			}
			
			certifica2List.add(certifica2Info);
			
		}
		
		// Check Settle for unEnjoy Holidays
		
		Result<Record> settlementRecords = dslContext.select().from(SALARY).where(SALARY.CONTRACT.eq(contractId)).and(SALARY.TYPE.eq((byte)2)).orderBy(SALARY.ID.desc()).fetch();
		
		Integer settlementId = settlementRecords.get(0).get(SALARY.ID);
		
		Date chargeDate = settlementRecords.get(0).get(SALARY.CHARGE_DATE);
		Date settlementEndDate = settlementRecords.get(0).get(SALARY.END_DATE);
		java.util.Date chargeDateJava = new java.util.Date(chargeDate.getTime());
		java.util.Date settlementEndDateJava = new java.util.Date(settlementEndDate.getTime());
		Long settlementDaysBetween = Duration.between(chargeDateJava.toInstant(), settlementEndDateJava.toInstant()).toDays();
		
		Record holidaysRecord = dslContext.select().from(SALARY_PAYMENT).where(SALARY_PAYMENT.SALARY.eq(settlementId)).and(SALARY_PAYMENT.TYPE.eq((byte)6)).fetchOne();
		
		Double baseCGC = holidaysRecord.get(SALARY_PAYMENT.AMOUNT);
		Double baseCGP = holidaysRecord.get(SALARY_PAYMENT.QUOTE);
		
		Certifica2Info settlementCertifica2Info = new Certifica2Info(
				null,
				null,
				settlementDaysBetween.intValue(), 
				baseCGC, 
				baseCGP);
		
		// ---------------------------------------------------- Create JSON
		
		JSONObject certifica2JSON = new JSONObject();
		
		certifica2JSON.put("enterprise_cif", enterpriseCIF);
		certifica2JSON.put("enterprise_ccc", completeCCC);
		
		certifica2JSON.put("dni", dni);
		certifica2JSON.put("name", name);
		certifica2JSON.put("sur_name", surName);
		certifica2JSON.put("ss_num", ssNum);
		certifica2JSON.put("quote_group", quoteGroup);
		certifica2JSON.put("contract_type", tc2);
		certifica2JSON.put("contract_duration", StringUtils.leftPad(contractDurationStr, 5, '0'));
		certifica2JSON.put("cno", StringUtils.rightPad(cno, 7, '0'));
		certifica2JSON.put("start_date", fullDateFormat.format(startDate));
		certifica2JSON.put("suspension_code", suspensionReasonCode);
		certifica2JSON.put("end_date", fullDateFormat.format(endDate));
		certifica2JSON.put("dayly_salary", "00000");
		
		certifica2List.sort(new Comparator<Certifica2Info>() {
			@Override
			public int compare(Certifica2Info o1, Certifica2Info o2) {
				return o2.getMonth().compareTo(o1.getMonth());
			}
		});
		
		JSONArray quotesInfoList = new JSONArray();
		
		for(Certifica2Info certifica2 : certifica2List) {
			JSONObject salaryOjt = new JSONObject();
			
			salaryOjt.put("year", certifica2.getYear());
			salaryOjt.put("month", certifica2.getMonth());
			salaryOjt.put("days", StringUtils.leftPad(certifica2.getQuotedDays().toString(), 3, '0'));
			salaryOjt.put("base_cgc", StringUtils.leftPad(String.format("%.2f", certifica2.getBase_cgc()).replaceAll(",", ""), 9, '0'));
			salaryOjt.put("base_cgp", StringUtils.leftPad(String.format("%.2f", certifica2.getBase_unemployment()).replaceAll(",", ""), 9, '0'));
			
			quotesInfoList.add(salaryOjt);
		}
		
		certifica2JSON.put("quoteInfoList", quotesInfoList);
		
		JSONObject settlementOjt = new JSONObject();
		
		settlementOjt.put("days", StringUtils.leftPad(settlementCertifica2Info.getQuotedDays().toString(), 3, '0'));
		settlementOjt.put("base_cgc",  StringUtils.leftPad(String.format("%.2f", settlementCertifica2Info.getBase_cgc()).replaceAll(",", ""), 9, '0'));
		settlementOjt.put("base_cgp", StringUtils.leftPad(String.format("%.2f", settlementCertifica2Info.getBase_unemployment()).replaceAll(",", ""), 9, '0'));
		certifica2JSON.put("holidaysInfo", settlementOjt);
		
		return certifica2JSON;
	}
	
	// ----------------------------------------------------- Auxiliar Methods
	
	private static Boolean checkIfNotExist(String data) {
		return (null == data || StringUtils.isBlank(data)) ? true : false;
	}
	
	private static Long getDaysBetween(Date startDate, Date endDate) {
		java.util.Date salaryStartDateJava = new java.util.Date(startDate.getTime());
		java.util.Date salaryEndDateJava = new java.util.Date(endDate.getTime());
		return Duration.between(salaryStartDateJava.toInstant(), salaryEndDateJava.toInstant()).toDays() + 1;
	}

	private static String normalizeString(String contractType) {
		if(contractType.contains("\""))
			return contractType.split("\"")[1];
		return contractType;
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

}
