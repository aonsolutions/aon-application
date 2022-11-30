package com.esferalia.aon.in.payroll.tgss.idc;

import static com.esferalia.aon.watson.util.AonStringUtils.endsWithAny;
import static com.esferalia.aon.watson.util.AonStringUtils.remove;
import static com.esferalia.aon.watson.util.AonStringUtils.removeEnd;
import static com.esferalia.aon.watson.util.AonStringUtils.removeStart;
import static com.esferalia.aon.watson.util.AonStringUtils.trim;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import com.esferalia.aon.in.payroll.pdf.SalaryPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class IdcplcccParser {

	public static void parse( File file , IdcParserListener listener) throws IOException, UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(file))
		{
			parse(doc, listener);
		}
	}

	public static void parse( InputStream is ,IdcParserListener listener) throws IOException , UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(is))
		{
			parse(doc, listener);
		}
	}
	
	public static void parse(PDDocument doc, IdcParserListener listener) throws IOException, UnknownPDFException {
       AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent())
		{
			throw new IOException("You do not have permission to extract text");
		}
		
		PDFTextStripper stripper= new PDFTextStripper();
		
		stripper.setSortByPosition(true);
		
		SalaryPDFTemplate template = null;
		
		Parser parser = IdcplcccParser::parseFirstPage;
		
		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
            // Set the page interval to extract. 
			// If we don't, then all pages would be extracted.
			stripper.setStartPage(p);
			stripper.setEndPage(p);
			
			String text = stripper.getText(doc);
			if ( AonStringUtils.isBlank(text) ) 
				continue;
			parser = parser.parse(text, listener);	
		}
						
			
	}
		
	private static Parser parseFirstPage(String text, IdcParserListener listener) throws IOException, UnknownPDFException {
		//System.out.println(text);
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			Matcher matcher = find(reader, ENTERPRISE_NAME_CCC_CIF_REGIME);
			
			String socialReason = matcher.group("name");
			String enterpriseCCC = matcher.group("province") + matcher.group("ccc");
			String enterpriseCIF = matcher.group("cif");
			String enterpriseRegime = matcher.group("regime");
			
			matcher = find(reader, ENTERPRISE_ACTIVITY);
			String enterpriseActivityCode = matcher.group("code");
			String enterpriseActivityDescription = matcher.group("description");
			
			onEnterprise(listener, socialReason, enterpriseCCC, enterpriseCIF, enterpriseRegime, enterpriseActivityCode,
					enterpriseActivityDescription);

			matcher = find(reader, MAIN_PERIOD);
			String month = matcher.group("month");
			String year = matcher.group("year");
			try {
				Date date = new SimpleDateFormat("MMMM-yyyy", new Locale("es", "ES")).parse(month+"-"+year);
				listener.onPeriod(date);
			} catch (ParseException e) {
				throw new UnknownPDFException(e);
			}
			
			String employeeeNss = null;
			String employeeName = null;
			
			while ( true ) {
				try {
					matcher = find(reader, EMPLOYEE_NSS_NAME, tryAuthorized(listener));
					employeeeNss = matcher.group("province") + matcher.group("nss");
					employeeName  = matcher.group("name");
					onEmployee(listener, employeeeNss, employeeName);
					
					parseEmployeePeriods(listener, reader, enterpriseCCC, employeeeNss);
					
					if ( attempt(reader, HOLIDAYS).isPresent() )
						return getHolidayspageParser(enterpriseCCC, employeeeNss);

				} catch ( UnknownPDFException e ) {
					// No more Employees
					break;
				} catch ( ParseException e ) {
					throw new UnknownPDFException(e);					
				} 
			}
			return getNextpageParser(enterpriseCCC, employeeeNss);
		}
	}

	protected static void parseEmployeePeriods(IdcParserListener listener, BufferedReader reader, String enterpriseCCC,
			String employeeeNss) throws IOException, UnknownPDFException, ParseException {
		Optional<Matcher> optional = attempt(reader, EMPLOYEE_PERIOD_QUOTE);
		while ( optional.isPresent() ) {
			Matcher matcher = optional.get();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date startDate = simpleDateFormat.parse(matcher.group("start"));
			Date endDate = simpleDateFormat.parse(matcher.group("end"));
			onEmployeePeriod(listener, enterpriseCCC, employeeeNss, startDate, endDate);
			String group = matcher.group("group");
			onEmployeeQuoteGroup(listener, group);
			boolean monthly = matcher.group("monthly") != null;
			parseEmployeePeriodPECs(reader, listener, employeeeNss, enterpriseCCC, startDate, endDate);
			onEmployeeQuoteGroup(listener, group, monthly);
			optional = attempt(reader, EMPLOYEE_PERIOD_QUOTE);
		}
		
	}

	protected static Parser getNextpageParser(String enterpriseCCC, String employeeeNss) {
		return (text, listener ) ->  parseNextPage(text, listener, enterpriseCCC, employeeeNss);
	}
	
	protected static Parser getHolidayspageParser(String enterpriseCCC, String employeeeNss) {
		return (text, listener ) ->  getHolidayspageParser(enterpriseCCC, employeeeNss);
	}

	private static Parser parseNextPage(String text, IdcParserListener listener, String enterpriseCCC, String employeeeNss ) throws IOException, UnknownPDFException {

		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {

			find(reader, ENTERPRISE_NAME_CCC_CIF_REGIME);
			find(reader, ENTERPRISE_ACTIVITY);
			find(reader, MAIN_PERIOD);
			
			find(reader, PEC_TYPE);
			
			Optional<Matcher> optional = attempt(reader, EMPLOYEE_NSS_NAME);
			if ( optional.isPresent() ) {
				Matcher matcher = optional.get();
				employeeeNss = matcher.group("province") + matcher.group("nss");
				String employeeName  = matcher.group("name");
				onEmployee(listener, employeeeNss, employeeName);
			}

			parseEmployeePeriods(listener, reader, enterpriseCCC, employeeeNss);
			
			while ( true ) {
				try {
					Matcher matcher = find(reader, EMPLOYEE_NSS_NAME, tryAuthorized(listener));
					employeeeNss = matcher.group("province") + matcher.group("nss");
					String employeeName  = matcher.group("name");
					onEmployee(listener, employeeeNss, employeeName);
					
					parseEmployeePeriods(listener, reader, enterpriseCCC, employeeeNss);
					
					if ( attempt(reader, HOLIDAYS).isPresent() )
						return getHolidayspageParser(enterpriseCCC, employeeeNss);
					
					
				} catch ( UnknownPDFException e ) {
					// No more Employees
					break;
				} catch ( ParseException e ) {
					throw new UnknownPDFException(e);					
				} 
			}
			
			return getNextpageParser(enterpriseCCC, employeeeNss);
		} catch ( ParseException e ) {
			throw new UnknownPDFException(e);					
		} 
	}
	
	private static Consumer<String> tryAuthorized (IdcParserListener listener) {
		return (String line) -> tryAuthorized(listener, line);
	}

	private static void tryAuthorized (IdcParserListener listener, String line) {
		Matcher matcher = AUTHORIZED.matcher(line);
		if ( matcher.matches() ) {
			String authorizedName = matcher.group("name");
			String authorizedNumber = matcher.group("number");
			onAuthorized(listener, authorizedNumber, authorizedName );
		}
	}
	
	
	private static void parseEmployeePeriodPECs(BufferedReader reader, IdcParserListener listener, String naf, String ccc, Date start, Date end) throws IOException {
		
		attempt(reader, EMPLOYEE_QUOTE_PEC).ifPresentOrElse(
		(m) -> {
			do {
				String code = m.group("code");
				String description = m.group("description");
				String tipo = m.group("tipo");
				String quota = m.group("quota");
				
				onEmployeeQuotePEC(listener, 
						ccc, 
						naf, 
						start, 
						end, 
						code, 
						description,
						tipo, 
						quota);
				try {
					m = attempt(reader, EMPLOYEE_QUOTE_PEC).orElse(null);
				} catch (IOException e) {
					m = null;
				}
			} while ( m != null );
		},
		() -> {
			try {
				attempt(reader, EMPLOYEE_NO_QUOTE_PEC);
			} catch (IOException e) {
			}
			listener.onNoEmployeeQuotePEC(naf, ccc, start, end);
		}
		);	
	}
	



	private static interface Parser {
		Parser parse(String text, IdcParserListener listener) throws IOException, UnknownPDFException ;
	}

//	private static void onEmployeeQuotePEC(IdcParserListener listener, String code, String description, String tipo,
//			String quota, String colective, String law) {
//		code = remove(code, " ");
//		tipo = remove(tipo, " ");
//		quota = remove(quota, " ");
//		listener.onEmployeeQuotePEC(
//				code, 
//				description,
//				tipo, 
//				quota,
//				colective, 
//				law);
//	}

	private static void onEmployeeQuotePEC(IdcParserListener listener, String enterpriseCCC, String employeeeNss,
			Date startDate, Date endDate, String code, String description, String tipo, String quota) {
		code = remove(code, " ");
		tipo = remove(tipo, " ");
		quota = remove(quota, " ");
		listener.onEmployeeQuotePEC(
				employeeeNss,
				enterpriseCCC,
				code, 
				description,
				tipo, 
				quota,
				startDate,
				endDate);
	}

	private static void onEmployeeQuoteGroup(IdcParserListener listener, String group) {
		group = trim(group);
		listener.onEmployeeQuoteGroup(group);
	}

	private static void onEmployeeQuoteGroup(IdcParserListener listener, String group, boolean monthly) {
		group = trim(group);
		listener.onEmployeeQuoteGroup(group, monthly);
	}

	private static void onEmployeePeriod(IdcParserListener listener, String enterpriseCCC, String employeeeNss,
			Date startDate, Date endDate) {
		listener.onEmployeePerido(employeeeNss, enterpriseCCC, startDate, endDate);
	}

	private static void onEmployee(IdcParserListener listener, String nss, String name) {
		nss = remove(nss, " ");
		
		name  = trim(name);
		while (endsWithAny(name, "-"))
			name = removeEnd(name, "-");
		name  = trim(name);
		
		listener.onEmployee(nss, name);
	}

	private static void onEnterprise(IdcParserListener listener, String socialReason, String enterpriseCCC,
			String enterpriseCIF, String enterpriseRegime, String enterpriseActivityCode,
			String enterpriseActivityDescription) {
		socialReason = trim(socialReason);
		enterpriseCCC = remove(enterpriseCCC, " ");
		enterpriseCIF = remove(enterpriseCIF, " ");
		enterpriseCIF = removeStart(enterpriseCIF, "0");
		enterpriseActivityCode = remove(enterpriseActivityCode, " ");
		enterpriseActivityDescription = trim(enterpriseActivityDescription);
		enterpriseRegime = trim(enterpriseRegime);
		listener.onEnterprise(socialReason, enterpriseCCC, enterpriseCIF, enterpriseActivityCode, enterpriseActivityDescription, enterpriseRegime, null);
	}
	
	private static void onAuthorized(IdcParserListener listener, String authorizedNumber, String authorizedName) {
		String name = trim(authorizedName);
		Integer number = AonNumberUtils.toInteger(authorizedNumber);
		listener.onAuthorized(number, name);
	}

	private static Matcher find( BufferedReader reader, Pattern pattern ) throws IOException, UnknownPDFException {
		
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
			Matcher matcher = pattern.matcher(line) ;
			if ( !matcher.matches() ) {
				continue;
			}
			
			return matcher;
		}
		
		throw new UnknownPDFException(String.format("Pattern: '%s' Not found" ,  pattern.pattern()) );
				
	}	
	
	private static Matcher find( BufferedReader reader, Pattern pattern, Consumer<String> onUnknow ) throws IOException, UnknownPDFException {
		
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
			Matcher matcher = pattern.matcher(line) ;
			if ( !matcher.matches() ) {
				onUnknow.accept(line);
				continue;
			}
			
			return matcher;
		}
		
		throw new UnknownPDFException(String.format("Pattern: '%s' Not found" ,  pattern.pattern()) );
				
	}	

	private static Optional<Matcher> attempt( BufferedReader reader, Pattern pattern ) throws IOException {
		reader.mark(256);
		String line = readLine(reader) ; 
		Matcher matcher = pattern.matcher(line) ;
		if ( matcher.matches() )
			return Optional.of(matcher);
		
		reader.reset();
		
		return Optional.empty();
				
	}		

	private static String readLine(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		while ( AonStringUtils.isBlank(line) )
			line = reader.readLine();
		return line;
	}

	//RAZÓN SOCIAL: AON SOLUTIONS S.L. C.C.C.: 01 105360062 DNI/NIE/CIF: 0B01487271 RÉGIMEN: REGIMEN GENERAL
	private static final Pattern ENTERPRISE_NAME_CCC_CIF_REGIME = 
	Pattern.compile(
	"^RAZÓN\\s*SOCIAL\\s*:\\s*(?<name>.+)C\\.C\\.C\\.\\s*:\\s*(?<province>[0-9]{2})\\s*(?<ccc>[0-9]+)\\s*DNI/NIE/CIF\\s*:\\s*(?<cif>.+)RÉGIMEN\\s*:\\s*(?<regime>.*)$"
	, Pattern.CASE_INSENSITIVE);
	
	//ACT. ECONÓMICA: 6209  Otros servicios rela 
	private static final Pattern ENTERPRISE_ACTIVITY = 
	Pattern.compile(
	"^ACT.\\s*ECONÓMICA\\s*:\\s*(?<code>[0-9]+)\\s*(?<description>.*)$"
	, Pattern.CASE_INSENSITIVE);
	
	
	//CONVENIO COLECTIVO
	//   
	//99001355011983
	
	//PERIODO DE LIQUIDACIÓN:    OCTUBRE 2020 
	private static final Pattern PEC_TYPE = 
	Pattern.compile(
	"^\\s*TIPO DE PECULIARIDAD.*$"
	, Pattern.CASE_INSENSITIVE);

	//PERIODO DE LIQUIDACIÓN:    OCTUBRE 2020 
	private static final Pattern MAIN_PERIOD = 
	Pattern.compile(
	"^PERIODO\\s*DE\\s*LIQUIDACIÓN\\s*:\\s*(?<month>[A-Z]+)\\s*(?<year>[0-9]+)\\s*$"
	, Pattern.CASE_INSENSITIVE);
	
	
	//01 1011187190 ANDER IBAÑEZ DE GAUNA NAVAZO            CBJ
    //      1       01-10-2020    31-10-2020      02 0,80 0,70 1,50 7,05  J7Q
	//                SIN PECULIARIDADES DE COTIZACION            IHG
	private static final Pattern EMPLOYEE_NSS_NAME = 
	Pattern.compile(
	"^(?<province>[0-9]+)\\s*(?<nss>[0-9]+)\\s*(?<name>.*)\\s*(?<clv>[^\\s]{3})$"
	, Pattern.CASE_INSENSITIVE);
	
	private static final Pattern EMPLOYEE_PERIOD_QUOTE = 
	Pattern.compile(
	"^\\s*(?<index>[0-9]+)\\s*(?<start>[0-9]+-[0-9]+-[0-9]+)\\s*(?<end>[0-9]+-[0-9]+-[0-9]+)\\s+(?<group>[0-9]+)(/(?<monthly>S))?.*$"
	, Pattern.CASE_INSENSITIVE);

	private static final Pattern EMPLOYEE_QUOTE_PEC = 
	Pattern.compile(
	"^\\s*(?<code>[0-9]+)\\s+(?<description>.*)\\s+(?<tipo>[0-9,]+)\\s+(?<quota>[0-9]{2})([^0-9]+)\\s+(?<colective>[0-9]{4})([^0-9]+)\\s+(?<law>[0-9]{4}[^0-9]+).*$"
	, Pattern.CASE_INSENSITIVE);

	private static final Pattern EMPLOYEE_NO_QUOTE_PEC = 
			Pattern.compile(
			"^\\s*SIN\\s*PECULIARIDADES\\s*DE\\s*COTIZACION.*$"
			, Pattern.CASE_INSENSITIVE);
	
	//---- VACACIONES RETRIBUIDAS Y NO DISFRUTADAS ----
	private static final Pattern HOLIDAYS = 
			Pattern.compile(
			"^.*VACACIONES\\s*RETRIBUIDAS\\s*Y\\s*NO\\s*DISFRUTADAS.*$"
			, Pattern.CASE_INSENSITIVE);
	
	//De conformidad con los términos de la autorización número 88233, concedida en fecha a PABLO PRIETO VIZUETE ...
	private static final Pattern AUTHORIZED = 
			Pattern.compile(
			"^\\s*De\\s*conformidad\\s*con\\s*los\\s*términos\\s*de\\s*la\\s*autorización\\s*número\\s*(?<number>\\d+)\\s*,\\s*concedida\\s*en\\s*fecha\\s*a\\s*(?<name>.*)\\s*por.*$"
			, Pattern.CASE_INSENSITIVE);
	
}
