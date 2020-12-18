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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CCCLaboralLife {

	//PARSER HANDLE EXCEPTIONS
	public static Collection<Employee> parse(InputStream is, Employee.EmployeeBuilder builder) throws IOException, UnknownPDFException {
		try (PDDocument doc = PDDocument.load(is)) {return parser(doc,builder);}
	}

	//TOTAL DOCUMENT PARSER
	private static Collection<Employee> parser(PDDocument doc, Employee.EmployeeBuilder builder) throws IOException, UnknownPDFException {
		AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent()) throw new IOException("You do not have permission to extract text");
		PDFTextStripper stripper = new PDFTextStripper();
		stripper.setSortByPosition(true);
		ArrayList<Employee> employees = new ArrayList<>();

		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
			stripper.setStartPage(p);
			stripper.setEndPage(p);
			String text = stripper.getText(doc);
			if (AonStringUtils.isBlank(text)) continue;
			employees.addAll(parseInfo(new BufferedReader(new StringReader(text)),builder));
		}

		return employees;
	}

	//PARSE A PAGE INFO
	private static Collection<Employee> parseInfo(BufferedReader br, Employee.EmployeeBuilder builder) throws IOException, UnknownPDFException {

		Matcher ccc_cif = 	find(br, CCC_CIF);
		String regime = 	ccc_cif.group("regime");
		String province = 	ccc_cif.group("province");
		String ccc = 		ccc_cif.group("ccc");
		String cif = 		ccc_cif.group("cif");

		Matcher city_cp_period = 	find(br,CITY_PCODE_PERIOD);
		Date periodStart = 			parseDate(city_cp_period.group("periodStart"), "dd MM yyyy");
		Date periodEnd = 			parseDate(city_cp_period.group("periodEnd"), "dd MM yyyy");

		builder.setCtaCti(province + ccc).setRegime(regime);
		ArrayList<Employee> employees = new ArrayList<>();
		try{
			while(true){
				Matcher employeeData = 	find(br,NSS_IPF_NAME_SURNAME_CLV);
				String nss = 			employeeData.group("nss1") + employeeData.group("nss2");
				String ipf = 			employeeData.group("ipf1") + employeeData.group("ipf2");
				String name = 			employeeData.group("name");
				builder.setNss(nss).setIpf(ipf).setName(name);

				Optional<Matcher> data = attempt(br, GENERAL_INFO);
				builder.setFra(null)
						.setFea(null)
						.setFrb(null)
						.setFeb(null);

				while(data.isPresent()) {
					employeeData = data.get();

					String type = 			employeeData.group("type");
					Date startDate = 		null;
					Date effectDate = 		null;

					if(employeeData.group("startDate") != null)  	startDate = 		parseDate(employeeData.group("startDate"),"dd-MM-yyyy");
					if(employeeData.group("effectDate") != null)  	effectDate = 		parseDate(employeeData.group("effectDate"),"dd-MM-yyyy");

					String gc =				employeeData.group("gc");
					String tc = 			employeeData.group("tc");
					String ep = 			employeeData.group("ep");

					Float ctp = 			null;
					String ctp_str = 		employeeData.group("ctp");
					if(ctp_str != null){
						String[] ctp_arr = 	ctp_str.split(",");
						ctp = 				Float.parseFloat(ctp_arr[0] + "." + ctp_arr[1]);
					}

					String[] at_arr = 		employeeData.group("at").split(",");
					Float at = 				Float.parseFloat(at_arr[0] + "." + at_arr[1]);

					String[] ims_arr =		employeeData.group("ims").split(",");
					float ims = 			Float.parseFloat(ims_arr[0] + "." + ims_arr[1]);

					String[] total_arr =	employeeData.group("total").split(",");
					float total = 			Float.parseFloat(total_arr[0] + "." + total_arr[1]);

					int cotDays = 			Integer.parseInt(employeeData.group("cotDays"));

					if(ctp != null) 			builder.setCtp(ctp);
					if(type.equalsIgnoreCase("ALTA")) builder.setFra(startDate).setFea(effectDate);
					if(type.equalsIgnoreCase("BAJA")) builder.setFrb(startDate).setFeb(effectDate);

					builder.setSituation(type)
							.setGc(gc)
							.setTc(tc)
							.setEp(ep)
							.setAt(at)
							.setIms(ims)
							.setTotal(total)
							.setCotDays(cotDays);

					data = attempt(br, GENERAL_INFO);
				}
				Employee e = builder.build();
				employees.add(e);
			}
		}catch(UnknownPDFException ignored){}

		return	employees;
	}

	//PARSE A DATE WITH AN SPECIFIC FORMAT
	public static Date parseDate(String dateStr,String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Date formattedDate;

		try {
			formattedDate = dateFormatter.parse(dateStr);
			return formattedDate;
		} catch (ParseException e){return null;}
	}


	//[DEBUG] LOG
	public static String log(Object object){
		String log = "[LOG] >> ";
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



	//ATTEMP TO FIND A STRING THA MATCHES THE REGEXP PATTERN
	private static Optional<Matcher> attempt(BufferedReader reader, Pattern pattern ) throws IOException {
		reader.mark(256);
		String line = reader.readLine() ;
		Matcher matcher = pattern.matcher(line) ;
		if ( matcher.matches() ) return Optional.of(matcher);
		reader.reset();
		return Optional.empty();
	}


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

	//	01 0019805355 1 016271678Y ANA DIAZ PEREZ WM4
	private static final Pattern NSS_IPF_NAME_SURNAME_CLV = Pattern.compile(
			"(?<nss1>\\d{2})" +
					"\\s(?<nss2>\\d*)" +
					"\\s(?<ipf1>\\d*)" +
					"\\s(?<ipf2>\\d*\\p{Alpha})" +
					"\\s(?<name>.*)" +
					"\\s\\S{3}" +
					".*"
			, Pattern.CASE_INSENSITIVE);

	//	 BAJA 01-12-2014 01-12-2014 13-07-2018 13-07-2018 07 100  A 0,65 0,35 1,00 1321 9Z1
	private static final Pattern GENERAL_INFO = Pattern.compile(
			"\\s*(?<type>\\S+)" +
					"\\s(?<startDate>\\d{0,2}-\\d{0,2}-\\d{0,4})?" +
					"\\s(?<effectDate>\\d{0,2}-\\d{0,2}-\\d{0,4})?" +
					"\\s(?<realSitDate>\\d{0,2}-\\d{0,2}-\\d{0,4})?" +
					"\\s(?<effectSitDate>\\d{0,2}-\\d{0,2}-\\d{0,4})?" +
					"\\s(?<gc>\\d{2})?" +
					"\\s(?<tc>\\d{3})?" +
					"\\s(?<ctp>\\d+,\\d+)?" +
					"\\s(?<ep>\\p{Alpha})?" +
					"\\s(?<at>\\d,\\d{2})?" +
					"\\s(?<ims>\\d,\\d{2})?" +
					"\\s(?<total>\\d,\\d{2})?" +
					"\\s(?<cotDays>\\d*)?" +
					".*"
			, Pattern.CASE_INSENSITIVE);
}
