package com.esferalia.aon.in.payroll.tgss.report;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.watson.util.AonStringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCCVidaLaboral {

	//PARSER HANDLE EXCEPTIONS
	public static void parse(InputStream is) throws IOException, UnknownPDFException {
		try (PDDocument doc = PDDocument.load(is)) {parser(doc);}
	}

	//TOTAL DOCUMENT PARSER
	private static void parser(PDDocument doc) throws IOException, UnknownPDFException {
		AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent()) throw new IOException("You do not have permission to extract text");
		PDFTextStripper stripper = new PDFTextStripper();
		stripper.setSortByPosition(true);

		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
			log("RUNING... " + p + "/" + doc.getNumberOfPages() + " PAGES.");
			log("-----------------------------------------------");
			stripper.setStartPage(p);
			stripper.setEndPage(p);
			String text = stripper.getText(doc);
			if (AonStringUtils.isBlank(text)) continue;
			parseInfo(new BufferedReader(new StringReader(text)));
			System.out.println("\n\n");
			//log(text);
		}
	}

	//PARSE A PAGE INFO
	private static void parseInfo(BufferedReader br) throws IOException, UnknownPDFException {
		log("Starting parser...");

		Matcher ccc_cif = find(br, CCC_CIF);
		log("regime: " + ccc_cif.group("regime"));
		log("province: " + ccc_cif.group("province"));
		log("ccc: " + ccc_cif.group("ccc"));
		log("cif: " + ccc_cif.group("cif"));

		Matcher city_cp_period = find(br,CITY_PCODE_PERIOD);
		log("period start: " + city_cp_period.group("periodStart") );
		log("period end: " + city_cp_period.group("periodEnd") );

		try {
			while(true){
				Matcher employeeData = find(br,NSS_IPF_NAME_SURNAME_CLV);
				log("nss: " + employeeData.group("nss1") + employeeData.group("nss2"));
				log("ipf: " + employeeData.group("ipf1") + employeeData.group("ipf2"));
				log("name: " + employeeData.group("name"));

				employeeData = find(br,GENERAL_INFO);
				log("type: " + employeeData.group("type"));
				log("start date: " + employeeData.group("startDate"));
				log("effect date: " + employeeData.group("effectDate"));
				log("real situation date: " + employeeData.group("realSitDate"));
				log("effect situation date: " + employeeData.group("effectSitDate"));
				log("gc: " + employeeData.group("gc"));
				log("tc: " + employeeData.group("tc"));
				log("ctp: " + employeeData.group("ctp"));
				log("ep: " + employeeData.group("ep"));
				log("at: " + employeeData.group("at"));
				log("it: " + employeeData.group("it"));
				log("total: " + employeeData.group("total"));
				log("cotDays: " + employeeData.group("cotDays"));

				System.out.println("------------------------------------------------");
			}

			/*
				TO_DO								PDF READER
				-----------------------------------------------
				1 - FIND EMPLOYEEE
				2-  WHILE (NEXT LINE DOES NOT MATCH WITH USER) MATCH WITCH DATA PATTERN
				3- IF EMPLOYEE NOT FOUND >> GO!
			 */
		}catch(UnknownPDFException e){log("Finishing process...");}

	}
	//[DEBUG] LOG
	public static String log(Object object){
		String log = "[LOG] " + new Date().toString().split(" ")[3] + " >> ";
		if(object == null)  log += "{empty}";
		else log += object.toString();
		System.out.println(log);
		return log;
	}


	//FIND A STRING THAT MATCHES THE REGEXP PATTERN
	private static Matcher find( BufferedReader reader, Pattern pattern ) throws IOException, UnknownPDFException {

		String line  ;
		while ( ( line = reader.readLine() ) != null  ) {
			Matcher matcher = pattern.matcher(line) ;
			if ( !matcher.matches() ) continue;
			return matcher;
		}
		throw new UnknownPDFException(String.format("Pattern: '%s' Not found" ,  pattern.pattern()) );
	}

	//FIND A STRING THAT MATCHES THE REGEXP PATTERN
	private static Matcher findBinary( BufferedReader reader, Pattern pattern, Pattern other ) throws IOException, UnknownPDFException {

		String line  ;
		while ( ( line = reader.readLine() ) != null  ) {
			Matcher matcher = pattern.matcher(line) ;
			if ( !matcher.matches() ){
				matcher = other.matcher(line) ;
				if(matcher.matches()) return matcher;
				continue;
			}
			return matcher;
		}
		throw new UnknownPDFException(String.format("Pattern: '%s' Not found" ,  pattern.pattern()) );
	}

	//ATTEMP TO FIND A STRING THA MATCHES THE REGEXP PATTERN
	private static Optional<Matcher> attempt(BufferedReader reader, Pattern pattern ) throws IOException {
		reader.mark(256);
		String line = reader.readLine() ;
		Matcher matcher = pattern.matcher(line) ;
		if ( matcher.matches() ) return Optional.of(matcher);
		reader.reset();
		return Optional.empty();
	}


	//CHECK IS A STRING MATCHES THE PATTERN
	private static Matcher check(Pattern pattern, String str) {
		Matcher matcher = pattern.matcher(str);
		matcher.matches();
		return matcher;
	}

	//	AON SOLUTIONS S.L.
	private static final Pattern ENTERPRISE =
			Pattern.compile("^\\s*(?<enterprise>.*)$"
					, Pattern.CASE_INSENSITIVE);

	//	0111 01 105360062 9 0B01487271
	private static final Pattern CCC_CIF =
			Pattern.compile("^\\s*(?<regime>[0-9]+)\\s+(?<province>[0-9]+)\\s+(?<ccc>[0-9]+)\\s+[0-9]\\s+(?<cif>[A-Z,0-9]+)\\s*$"
					, Pattern.CASE_INSENSITIVE);

	//	CL DUQUE DE WELLINGTON 52 B
	private static final Pattern ADDRESS =
			Pattern.compile(
					"\\s*(?<address>.*)\\s*"
					, Pattern.CASE_INSENSITIVE);

	//	VITORIA-GASTEIZ 01010 01 01 2015 / 23 10 2020
	private static final Pattern CITY_PCODE_PERIOD =
			Pattern.compile(
					"(?<city>\\S*)\\s*(?<pcode>\\S*)\\s*(?<periodStart>.*)/\\s(?<periodEnd>.*)"
					, Pattern.CASE_INSENSITIVE);

	//	6209  Otros servicios relacionados con las tec
	private static final Pattern CNAE =
			Pattern.compile(
					"\\s(?<cnaeCode>\\S*)\\s*(?<cnaeTitle>.*)"
					, Pattern.CASE_INSENSITIVE);

	//	0,80 0,70 1,50
	private static final Pattern AT_TIPES =
			Pattern.compile(
					"\\s(?<it>\\S*)\\s*(?<ims>\\S*)\\s*(?<total>\\S*)\\s*"
					, Pattern.CASE_INSENSITIVE);

	//	01 0019805355 1 016271678Y ANA DIAZ PEREZ WM4
	private static final Pattern NSS_IPF_NAME_SURNAME_CLV = Pattern.compile(
			"(?<nss1>\\d*)" +
					"\\s(?<nss2>\\d*)" +
					"\\s(?<ipf1>\\d*)" +
					"\\s(?<ipf2>\\d*\\p{Alpha})" +
					"\\s(?<name>.*)" +
					"\\s\\S{3}" +
					".*"
			, Pattern.CASE_INSENSITIVE);

	//	 BAJA 01-12-2014 01-12-2014 13-07-2018 13-07-2018 07 100  A 0,65 0,35 1,00 1321 9Z1
	private static final Pattern GENERAL_INFO = Pattern.compile(
			"\\s*(?<type>\\p{Alpha}*)" +
					"\\s(?<startDate>\\d{0,2}-\\d{0,2}-\\d{0,4})?" +
					"\\s(?<effectDate>\\d{0,2}-\\d{0,2}-\\d{0,4})?" +
					"\\s(?<realSitDate>\\d{0,2}-\\d{0,2}-\\d{0,4})?" +
					"\\s(?<effectSitDate>\\d{0,2}-\\d{0,2}-\\d{0,4})?" +
					"\\s(?<gc>\\d{2})?" +
					"\\s(?<tc>\\d{3})?" +
					"\\s(?<ctp>\\d+,\\d+)?" +
					"\\s(?<ep>\\p{Alpha})?" +
					"\\s(?<at>\\d,\\d{2})?" +
					"\\s(?<it>\\d,\\d{2})?" +
					"\\s(?<total>\\d,\\d{2})?" +
					"\\s(?<cotDays>\\d*)?" +
					".*"
			, Pattern.CASE_INSENSITIVE);


	public static void main(String[] args) {
		Matcher matcher = check(ENTERPRISE, "\tAON SOLUTIONS S.L.");
		System.out.println("entreprise : " + matcher.group("enterprise"));

		matcher = check(CCC_CIF, "	0111 01 105360062 9 0B01487271");
		System.out.println("regime : " + matcher.group("regime"));
		System.out.println("province : " + matcher.group("province"));
		System.out.println("ccc : " + matcher.group("ccc"));
		System.out.println("cif : " + matcher.group("cif"));

		matcher = check(ADDRESS, "\tCL DUQUE DE WELLINGTON 52 B");
		System.out.println("address: " + matcher.group("address"));

		matcher = check(CITY_PCODE_PERIOD, "\tVITORIA-GASTEIZ 01010 01 01 2015 / 23 10 2020");
		System.out.println("city: " + matcher.group("city"));
		System.out.println("postal code: " + matcher.group("pcode"));
		System.out.println("period start: " + matcher.group("periodStart"));
		System.out.println("period end: " + matcher.group("periodEnd"));

		matcher = check(CNAE, "\t6209  Otros servicios relacionados con las tec");
		System.out.println("cnae code: " + matcher.group("cnaeCode"));
		System.out.println("cnae title: " + matcher.group("cnaeTitle"));

		matcher = check(AT_TIPES, "\t0,80 0,70 1,50");
		System.out.println("it: " + matcher.group("it"));
		System.out.println("ims: " + matcher.group("ims"));
		System.out.println("total: " + matcher.group("total"));

		matcher = check(NSS_IPF_NAME_SURNAME_CLV, "\t01 0019805355 1 016271678Y ANA DIAZ PEREZ WM4");
		System.out.println("nss province: " + matcher.group("nss1"));
		System.out.println("nss code: " + matcher.group("nss2"));
		System.out.println("ipf start: " + matcher.group("ipf1"));
		System.out.println("ipf code: " + matcher.group("ipf2"));
		System.out.println("name: " + matcher.group("name"));

		matcher = check(GENERAL_INFO, "\t BAJA 02-10-2014 02-10-2014 15-08-2015 15-08-2015 07 502 0,500 A 0,65 0,35 1,00 159 3JF");
		System.out.println("type: " + matcher.group("type"));
		System.out.println("start date: " + matcher.group("startDate"));
		System.out.println("effect date: " + matcher.group("effectDate"));
		System.out.println("real situation date: " + matcher.group("realSitDate"));
		System.out.println("effect situation date: " + matcher.group("effectSitDate"));
		System.out.println("gc: " + matcher.group("gc"));
		System.out.println("tc: " + matcher.group("tc"));
		System.out.println("ctp: " + matcher.group("ctp"));
		System.out.println("ep/oc: " + matcher.group("ep"));
		System.out.println("at: " + matcher.group("at"));
		System.out.println("it: " + matcher.group("it"));
		System.out.println("total: " + matcher.group("total"));
		System.out.println("cot days: " + matcher.group("cotDays"));

	}
}
