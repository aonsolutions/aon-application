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

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import com.esferalia.aon.in.payroll.pdf.SalaryPDFTemplate;
import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.watson.util.AonStringUtils;


public class IdcplcccParser {

	public static void parse( File file , IdcListener listener) throws IOException, UnknownPDFException {
		try (PDDocument doc = PDDocument.load(file))
		{
			parse(doc, listener);
		}
	}

	public static void parse( InputStream is ,IdcListener listener) throws IOException , UnknownPDFException {
		try (PDDocument doc = PDDocument.load(is))
		{
			parse(doc, listener);
		}
	}
	
	public static void parse(PDDocument doc, IdcListener listener) throws IOException, UnknownPDFException {
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
		
	public static void parse(String text, IdcListener listener) throws IOException, UnknownPDFException {
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
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			while ( true ) {
				try {
					matcher = find(reader, EMPLOYEE_NSS_NAME);
					String employeeeNss = matcher.group("province") + matcher.group("nss");
					String employeeName  = matcher.group("name");
					onEmployee(listener, employeeeNss, employeeName);
					matcher = find(reader, EMPLOYEE_PERIOD_QUOTE);
					Date startDate = simpleDateFormat.parse(matcher.group("start"));
					Date endDate = simpleDateFormat.parse(matcher.group("end"));
					onEmployeePeriod(listener, enterpriseCCC, employeeeNss, startDate, endDate);
					String group = matcher.group("group");
					onEmployeeQuoteGroup(listener, group);
					
					for ( Optional<Matcher> optional = attempt(reader, EMPLOYEE_QUOTE_PEC); 
						optional.isPresent() ; optional = attempt(reader, EMPLOYEE_QUOTE_PEC)) {
						
						String code = optional.get().group("code");
						String description = optional.get().group("description");
						String tipo = optional.get().group("tipo");
						String quota = optional.get().group("quota");
						String colective = optional.get().group("colective"); 
						String law = optional.get().group("law");
						
//						onEmployeeQuotePEC(listener, code, description, tipo, quota, colective, law);
						
						onEmployeeQuotePEC(listener, enterpriseCCC, employeeeNss, startDate, endDate, code, description,
								tipo, quota);
					}
				
				} catch ( UnknownPDFException e ) {
					// No more Employees
					break;
				} catch ( ParseException e ) {
					throw new UnknownPDFException(e);					
				} 
			}
		}
	}

//	private static void onEmployeeQuotePEC(IdcListener listener, String code, String description, String tipo,
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

	private static void onEmployeeQuotePEC(IdcListener listener, String enterpriseCCC, String employeeeNss,
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

	private static void onEmployeeQuoteGroup(IdcListener listener, String group) {
		group = trim(group);
		listener.onEmployeeQuoteGroup(group);
	}

	private static void onEmployeePeriod(IdcListener listener, String enterpriseCCC, String employeeeNss,
			Date startDate, Date endDate) {
		listener.onEmployeePerido(employeeeNss, enterpriseCCC, startDate, endDate);
	}

	private static void onEmployee(IdcListener listener, String nss, String name) {
		nss = remove(nss, " ");
		
		name  = trim(name);
		while (endsWithAny(name, "-"))
			name = removeEnd(name, "-");
		name  = trim(name);
		
		listener.onEmployee(nss, name);
	}

	private static void onEnterprise(IdcListener listener, String socialReason, String enterpriseCCC,
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

	private static Optional<Matcher> attempt( BufferedReader reader, Pattern pattern ) throws IOException, UnknownPDFException {
		reader.mark(256);
		String line = reader.readLine() ; 
		//System.out.println(line);
		Matcher matcher = pattern.matcher(line) ;
		if ( matcher.matches() )
			return Optional.of(matcher);
		
		reader.reset();
		
		return Optional.empty();
				
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
	"^\\s*(?<index>[0-9]+)\\s*(?<start>[0-9]+-[0-9]+-[0-9]+)\\s*(?<end>[0-9]+-[0-9]+-[0-9]+)\\s+(?<group>[0-9]+).*$"
	, Pattern.CASE_INSENSITIVE);

	private static final Pattern EMPLOYEE_QUOTE_PEC = 
	Pattern.compile(
	"^\\s*(?<code>[0-9]+)\\s+(?<description>.*)\\s+(?<tipo>[0-9,]+)\\s+(?<quota>[0-9]{2})([^0-9]+)\\s+(?<colective>[0-9]{4})([^0-9]+)\\s+(?<law>[0-9]{4}[^0-9]+).*$"
	, Pattern.CASE_INSENSITIVE);

}
