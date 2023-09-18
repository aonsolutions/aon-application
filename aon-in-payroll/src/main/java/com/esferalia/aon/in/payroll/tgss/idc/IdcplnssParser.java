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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import com.esferalia.aon.in.payroll.pdf.SalaryPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.payroll.calculator.sql.SQLContractExtraCalculatorContext.DateFormatException;
import com.esferalia.aon.watson.util.AonStringUtils;


public class IdcplnssParser {

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
		
		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
            // Set the page interval to extract. 
			// If we don't, then all pages would be extracted.
			stripper.setStartPage(p);
			stripper.setEndPage(p);
			
			String text = stripper.getText(doc);
			if ( AonStringUtils.isBlank(text) ) 
				continue;
			
			parse(text, listener);	
		}
						
			
	}
		
	public static void parse(String text, IdcParserListener listener) throws IOException, UnknownPDFException {
		//System.out.println(text);
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			Matcher matcher = find(reader, EMPLOYEE_NAME_NSS_NIF);
			
			String employeeCif = matcher.group("cif");
			String employeeName = matcher.group("name");
			String employeeNss = matcher.group("province") + matcher.group("nss");
			
			matcher = find(reader, ENTERPRISE_NAME_CCC_CIF_REGIME);
			
			String enterpriseRegime = matcher.group("regime");
			String enterpriseName = matcher.group("name");
			String enterpriseCCC = matcher.group("province") + matcher.group("ccc");
			
			onEnterprise(listener, enterpriseName, enterpriseCCC, enterpriseRegime);
			
			onEmployee(listener, employeeNss, employeeName);
			
			matcher = find(reader, LIQUIDATION_PERIOD);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
			Date fromDate = simpleDateFormat.parse(matcher.group("month"));
			//Date endDate = simpleDateFormat.parse(matcher.group("to"));	
			listener.onPeriod(fromDate);
			
			simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", new Locale("es", "ES"));
			matcher = ateempt(reader, EMPLOYEE_PERIOD);
			while ( matcher != null ) {
				Date startDate = simpleDateFormat.parse(matcher.group("start"));
				Date endDate = simpleDateFormat.parse(matcher.group("end"));				
				onEmployeePeriod(listener, enterpriseCCC, employeeNss, startDate, endDate);
				
				String group = matcher.group("group");
				onEmployeeQuoteGroup(listener, group);

				boolean monthly = matcher.group("monthly") != null;
				onEmployeeQuoteGroup(listener, group, monthly );
				
				
				matcher = tryy(reader, EMPLOYEE_PEC);
				while ( matcher != null ) {
					String code = matcher.group("code");
					String description = matcher.group("description");
					String tipo = matcher.group("tipo");
					String quota = matcher.group("quota");
					String colective = matcher.group("colective"); 
					String law = matcher.group("law");
										
					onEmployeeQuotePEC(listener, enterpriseCCC, employeeNss, startDate, endDate, code, description,
							tipo, quota, colective);
					
					matcher = tryy(reader, EMPLOYEE_PEC);
				}
				
				matcher = tryy(reader, WITHOUT_PEC);
				if ( matcher != null )
					onNoEmployeeQuotePEC(listener, employeeNss, enterpriseCCC, startDate, endDate);
				
				
				matcher = ateempt(reader, EMPLOYEE_PERIOD);
			}
			
		}
		catch (ParseException e) {
			throw new UnknownPDFException(e);					
		}
	}

	private static void onNoEmployeeQuotePEC(IdcParserListener listener, String ssNum, String ccc, Date start, Date end) {
		listener.onNoEmployeeQuotePEC(ssNum, ccc, start, end);
	}


	private static void onEmployeeQuotePEC(IdcParserListener listener, String enterpriseCCC, String employeeeNss,
		Date startDate, Date endDate, String code, String description, String tipo, String quota, String colective) {
        	code = remove(code, " ");
        	tipo = remove(tipo, " ");
        	quota = remove(quota, " ");
        	colective = remove(colective, " ");
        	listener.onEmployeeQuotePEC(
        			employeeeNss,
        			enterpriseCCC,
        			code, 
        			description,
        			tipo, 
        			quota,
        			colective,
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
			String enterpriseRegime ) {
		socialReason = trim(socialReason);
		enterpriseCCC = remove(enterpriseCCC, " ");
		enterpriseRegime = trim(enterpriseRegime);
		listener.onEnterprise(socialReason, enterpriseCCC, null, null, null, enterpriseRegime, null);
	}
	
	private static Matcher find( BufferedReader reader, Pattern pattern ) throws IOException, UnknownPDFException {
		
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
//			System.out.println(line);
			Matcher matcher = pattern.matcher(line) ;
			if ( !matcher.matches() ) {
				continue;
			}
			
			return matcher;
		}
		
		throw new UnknownPDFException(String.format("Pattern: '%s' Not found" ,  pattern.pattern()) );
				
	}	

	private static Matcher ateempt( BufferedReader reader, Pattern pattern ) throws IOException, UnknownPDFException {
		
		String line  ; 
		while ( ( line = reader.readLine() ) != null  ) {
			Matcher matcher = pattern.matcher(line) ;
			if ( !matcher.matches() ) {
				continue;
			}
			
			return matcher;
		}
		
		return null;
				
	}	
	
	private static Matcher tryy( BufferedReader reader, Pattern pattern ) throws IOException {
		reader.mark(256);
		String line = reader.readLine() ; 
		Matcher matcher = pattern.matcher(line) ;
		if ( matcher.matches() )
			return matcher;
		
		reader.reset();
		
		return null;
				
	}	
	
	//NOMBRE Y APELLIDOS: RAUL TREPIANA ZARATE	NÚMERO SEGURIDAD SOCIAL: 01 1005185924 DOC.IDENTIFICATIVO: 1 NÚMERO: 044679529M SEXO: VARON NACIMIENTO: 03/05/1975
	private static final Pattern EMPLOYEE_NAME_NSS_NIF = 
	Pattern.compile(
			"^NOMBRE\\s*Y\\s*APELLIDOS\\s*:\\s*(?<name>.+)NÚMERO\\s*SEGURIDAD\\s*SOCIAL\\s*:\\s*(?<province>[0-9]{2})\\s*(?<nss>[0-9]+)\\s*DOC.IDENTIFICATIVO\\s*:\\s*([0-9])\\s*NÚMERO\\s*:\\s*(?<cif>.+)SEXO.*$"
			, Pattern.CASE_INSENSITIVE);
	
	//RAZÓN SOCIAL: AON SOLUTIONS S.L.
	private static final Pattern ENTERPRISE_NAME_CCC_CIF_REGIME = 
	Pattern.compile(
	"^RAZÓN\\s*SOCIAL\\s*:\\s*(?<name>.+)C\\.C\\.C\\.\\s*:\\s*(?<province>[0-9]{2})\\s*(?<ccc>[0-9]+)\\s*DNI/NIE/CIF\\s*:\\s*(?<cif>.+)RÉGIMEN\\s*:\\s*(?<regime>.*)$"
	, Pattern.CASE_INSENSITIVE);
	
	//PERIODO DE LIQUIDACIÓN: DICIEMBRE 2020
	private static final Pattern LIQUIDATION_PERIOD = 
	Pattern.compile(
	"^PERIODO\\s*DE\\s*LIQUIDACIÓN:\\s*(?<month>[A-Z]+\\s*[0-9]+).*$"
	, Pattern.CASE_INSENSITIVE);
	
	//1   01-12-2020        31-12-2020	
	private static final Pattern EMPLOYEE_PERIOD = 
	Pattern.compile(
	"^\\s*(?<index>[0-9]+)\\s*(?<start>[0-9]+-[0-9]+-[0-9]+)\\s*(?<end>[0-9]+-[0-9]+-[0-9]+)\\s+(?<group>[0-9]+)(/(?<monthly>S))?.*$"
	, Pattern.CASE_INSENSITIVE);

	//03-07-2020 31-07-2020   37 EXONE.ERE.F.MAY.COMP 60,00  01 CUOTA EMPRESARIAL 4608 EX.FM37CV<50.789R 0222 RDL 24/2020      H1B
	private static final Pattern EMPLOYEE_PEC = 
	Pattern.compile(
	"^\\s*(?<code>[0-9]+)\\s+(?<description>.*)\\s+(?<tipo>[0-9,]+)\\s+(?<quota>[0-9]{2})([^0-9]+)\\s+(?<colective>[0-9]{4})(.*)\\s+(?<law>[0-9]{4}.*).*$"
	, Pattern.CASE_INSENSITIVE);
	
	//SIN PECULIARIDADES DE COTIZACION
	private static final Pattern WITHOUT_PEC = 
	Pattern.compile(
	"^.*SIN\\s*PECULIARIDADES\\s*DE\\s*COTIZACION.*$"
	, Pattern.CASE_INSENSITIVE);
}
