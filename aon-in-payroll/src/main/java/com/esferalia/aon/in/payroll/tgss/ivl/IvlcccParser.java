package com.esferalia.aon.in.payroll.tgss.ivl;

import static com.esferalia.aon.watson.util.AonStringUtils.replace;
import static com.esferalia.aon.watson.util.AonStringUtils.trim;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.watson.util.AonStringUtils;

class IvlcccParser {

	public static void parse( File file , IvlParserListener listener) throws IOException, UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(file))
		{
			parse(doc, listener);
		}
	}

	public static void parse( InputStream is ,IvlParserListener listener) throws IOException , UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(is.readAllBytes()))
		{
			parse(doc, listener);
		}
	}
	
	public static void parse(PDDocument doc, IvlParserListener listener) throws IOException, UnknownPDFException {
	       AccessPermission ap = doc.getCurrentAccessPermission();
	       if (!ap.canExtractContent()) {
		   throw new IOException("You do not have permission to extract text");
	       }

	       PDFTextStripper stripper = new PDFTextStripper();
	       stripper.setSortByPosition(true);

	       for (int p = 1; p <= doc.getNumberOfPages(); p++) {
		   // Set the page interval to extract.
		   // If we don't, then all pages would be extracted.
		   stripper.setStartPage(p);
		   stripper.setEndPage(p);

		   String text = stripper.getText(doc);
		   if (AonStringUtils.isBlank(text))
		       continue;

		   parse(text, listener);
	       }
	}
	
	public static void parse(String text, IvlParserListener listener) throws IOException, UnknownPDFException {
	    //System.out.println(text);
	    try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
		{
			find(reader, ENTERPRISE_NAME_CCC_CIF_LABEL);
        		Matcher matcher = find(reader, ENTERPRISE_NAME_CCC_CIF);
        		String enterpriseName = getString(matcher,"enterpriseName");
        		String cccRegime = getString(matcher,"cccRegime");
        		String cccProvince = getString(matcher,"cccProvince");
        		String cccNumber = getString(matcher,"cccNumber");
        		String docType = getString(matcher,"docType");
        		String docNumber = getString(matcher,"docNumber");

        		find(reader, ENTERPRISE_ADDRESS_CITY_CP_LABEL);
        		matcher = find(reader, ENTERPRISE_ADDRESS);
        		String enterpriseAddress = getString(matcher,"enterpriseAddress");
        		matcher = find(reader, ENTERPRISE_CITY_CP);
        		String enterpriseCity = getString(matcher,"enterpriseCity");
        		String enterpriseCP = getString(matcher,"enterpriseCP");
			
        		find(reader, ENTERPRISE_CNAE_IT_IMS_LABEL);
        		matcher = find(reader, ENTERPRISE_CNAE);
        		String enterpriseCNAENumber = getString(matcher,"enterpriseCNAENumber");
        		String enterpriseCNAEDescription = getString(matcher,"enterpriseCNAEDescription");

        		listener.onEnterprise(enterpriseName, cccRegime, cccProvince, cccNumber, docType, docNumber, enterpriseAddress, enterpriseCity, enterpriseCP, enterpriseCNAENumber, enterpriseCNAEDescription);
		}
		
		{
        		for( Matcher matcher = look(reader, EMPLOYEE_NAF_NIF_NAME); matcher != null; matcher = look(reader, EMPLOYEE_NAF_NIF_NAME) ) {

        		    String nafProvince = getString(matcher,"nafProvince");
        		    String nafNumber = getString(matcher,"nafNumber");
        		    String docType = getString(matcher,"docType");
        		    String docNumber = getString(matcher,"docNumber");
        		    String employeeName = trim(replace(getString(matcher,"employeeName"),"---",""));
        		    listener.onEmployee(nafProvince, nafNumber, docType, docNumber, employeeName);
        		    
        		    for ( Matcher startEndMatcher = attempt(reader, EMPLOYEE_START_END_GC_TC); startEndMatcher != null; startEndMatcher = attempt(reader, EMPLOYEE_START_END_GC_TC) ) {
                		    Date realStartDate = getDate(startEndMatcher,"realStartDate");
                		    Date efectiveStartDate = getDate(startEndMatcher,"efectiveStartDate");
                		    Date realEndDate = getDate(startEndMatcher,"realEndDate");
                		    Date efectiveEndDate = getDate(startEndMatcher,"efectiveEndDate");
                		    String quoteGroup = getString(startEndMatcher,"quoteGroup");
                		    String monthly = getString(startEndMatcher,"monthly");
                		    String tc2 = getString(startEndMatcher,"tc2");
                		    
                		    Double partialFactor = getDouble(startEndMatcher, "partialFactor");

                		    Double it = getDouble(startEndMatcher, "it");
                		    Double ims = getDouble(startEndMatcher, "ims");
                		    
                		    listener.onEmployeeContract(realStartDate, efectiveStartDate, realEndDate, efectiveEndDate, quoteGroup, monthly, tc2, partialFactor, it , ims, null);
        			
        		    }
        		    
        		    
        		    
        		}
		}

	    }
	}	
	
	//TODO: Refactor and move to a new class? 
	
	private static String getString(Matcher matcher, String group) {
	    return AonStringUtils.trim(matcher.group(group));	    
	}
	
	private static Date getDate(Matcher matcher, String group) throws UnknownPDFException  {
	    String string = getString(matcher, group);
	    if ( AonStringUtils.isBlank(string)) {
		return null;
	    }
	    try {
		return new SimpleDateFormat("dd-MM-yyyy").parse(string);
	    } catch (ParseException e) {
		throw new UnknownPDFException(e);
	    }
	}

	private static Double getDouble(Matcher matcher, String group) throws UnknownPDFException  {
	    String string = getString(matcher, group);
	    if ( AonStringUtils.isBlank(string)) {
		return null;
	    }
	    try {
		DecimalFormat decimalFormat = new DecimalFormat();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		symbols.setGroupingSeparator('.');
		decimalFormat.setDecimalFormatSymbols(symbols);
		
		return decimalFormat.parse(string).doubleValue();
	    } catch (ParseException e) {
		throw new UnknownPDFException(e);
	    }
	}

	private static String readLine(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		while ( AonStringUtils.isBlank(line) )
			line = reader.readLine();
		return line;
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

	private static Matcher look( BufferedReader reader, Pattern pattern ) throws IOException {
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

	private static Matcher attempt( BufferedReader reader, Pattern pattern ) throws IOException {
		reader.mark(256);
		String line = reader.readLine() ; 
		Matcher matcher = pattern.matcher(line) ;
		if ( matcher.matches() )
			return matcher;
		
		reader.reset();
		
		return null;
				
	}
	
	protected static final Pattern ENTERPRISE_NAME_CCC_CIF_LABEL = Pattern.compile(
		"^RAZÓN\\s*SOCIAL\\s*CÓDIGO\\s*CUENTA\\s*DE\\s*COTIZACIÓN\\s*EMPRESARIO\\s*$", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);	
	protected static final Pattern ENTERPRISE_NAME_CCC_CIF = Pattern.compile(
		"^(?<enterpriseName>.*)(?<cccRegime>\\d{4})\\s*(?<cccProvince>\\d{2})\\s*(?<cccNumber>\\d{9})\\s*(?<docType>\\d)\\s*0*((?<docNumber>[A-Z0-9]+))$", Pattern.CASE_INSENSITIVE);	
	
	protected static final Pattern ENTERPRISE_ADDRESS_CITY_CP_LABEL = Pattern.compile(
		"^DOMICILIO\\s*LOCALIDAD\\s*C\\.P\\.\\s*PERIODO\\s*SOLICITADO\\s*$", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);	
	protected static final Pattern ENTERPRISE_ADDRESS = Pattern.compile(
		"^(?<enterpriseAddress>.*)$", Pattern.CASE_INSENSITIVE);	
	protected static final Pattern ENTERPRISE_CITY_CP = Pattern.compile(
		"^(?<enterpriseCity>.*)(?<enterpriseCP>\\d{5}).*$", Pattern.CASE_INSENSITIVE);	
	
	protected static final Pattern ENTERPRISE_CNAE_IT_IMS_LABEL = Pattern.compile(
//		"^CNAE\\s*(?<city>.+)?\\s*TIPOS\\s*AT\\s*:\\s*IT\\s*IMS\\s*TOTAL\\s*$", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);	
		"^.*TIPOS\\s*AT\\s*:\\s*IT\\s*IMS\\s*TOTAL\\s*$", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);	
	protected static final Pattern ENTERPRISE_CNAE = Pattern.compile(
		"^(?<enterpriseCNAENumber>\\d+)\\s*(?<enterpriseCNAEDescription>.*)$", Pattern.CASE_INSENSITIVE);	
	
	protected static final Pattern EMPLOYEE_NAF_NIF_NAME= Pattern.compile(
		"^(?<nafProvince>\\d{2})\\s*(?<nafNumber>\\d+)\\s*(?<docType>\\d)\\s*0*(?<docNumber>[0-9A-Z]+)\\s*(?<employeeName>.*)\\s*(?<clv>[0-9A-Z]{3}).*$", Pattern.CASE_INSENSITIVE);	
	protected static final Pattern EMPLOYEE_START_END_GC_TC = Pattern.compile(
		"^\\s*(ALTA|BAJA)\\s*"
		+ "(?<realStartDate>\\d{2}-\\d{2}-\\d{4})\\s*(?<efectiveStartDate>\\d{2}-\\d{2}-\\d{4})\\s*"
		+ "((?<realEndDate>\\d{2}-\\d{2}-\\d{4})\\s*(?<efectiveEndDate>\\d{2}-\\d{2}-\\d{4})\\s*)?"
		+ "(?<quoteGroup>\\d{2})\\s*(?<monthly>/S)?\\s*"
		+ "(?<tc2>\\d{3})?\\s*"
		+ "(?<partialFactor>\\d+,\\d{3})?\\s*"
		+ "(?<ocupation>[A-Za-z])?\\s*"
		+ "((?<it>\\d+(,\\d+)?)\\s*(?<ims>\\d+(,\\d+)?)\\s*(?<total>\\d+(,\\d+)?)\\s*)?"
		+ "(?<quoteDays>\\d+)"
		+ ".*$"
		, Pattern.CASE_INSENSITIVE);
	
	public static void main(String[] args) {
	    Matcher matcher = EMPLOYEE_START_END_GC_TC.matcher(" ALTA 12-06-2023 12-06-2023   08 100 G 3,35 3,35 6,70 149 D7K");
	    if (!matcher.matches()) 
		throw new AssertionError();
	    matcher = EMPLOYEE_NAF_NIF_NAME.matcher("08 1330742864 6 0Y4553289Q CHARNJIT --- G56");
	    if (!matcher.matches()) 
		throw new AssertionError();
	    System.out.println(matcher.group("employeeName"));
	    matcher = EMPLOYEE_NAF_NIF_NAME.matcher("08 1330742864 6 0Y4553289Q CHARNJIT --- --- G56");
	    if (!matcher.matches()) 
		throw new AssertionError();
	    System.out.println(matcher.group("employeeName"));
	    
	}
}
