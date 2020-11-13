package com.esferalia.aon.in.payroll.tgss.idc;

import static com.esferalia.aon.watson.util.AonStringUtils.remove;
import static com.esferalia.aon.watson.util.AonStringUtils.removeStart;
import static com.esferalia.aon.watson.util.AonStringUtils.trim;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.payroll.tgss.cra.StringUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class IdcParser {
	
	public static void parse( File file , IdcListener listener) throws IOException, UnknownPDFException {
		try (PDDocument doc = PDDocument.load(file)){
			parse(doc, listener);
		}
	}

 	public static void parse( InputStream is ,IdcListener listener) throws IOException , UnknownPDFException {
		try (PDDocument doc = PDDocument.load(is)){
			parse(doc, listener);
		}
	}
	
	public static void parse(PDDocument doc, IdcListener listener) throws IOException, UnknownPDFException {
       AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent()){
			throw new IOException("You do not have permission to extract text");
		}
		
		PDFTextStripper stripper= new PDFTextStripper();
		
		stripper.setSortByPosition(true);
		
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
//		System.out.println(text);
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			
			Matcher matcher = find(reader, EMPLOYEE_NAME);
			
			String fullName = matcher.group("name");
			
			matcher = find(reader, EMPLOYEE_NSS_TYPEDOC_DOC_GENDER_BIRTHDATE);
			
			String nss = matcher.group("province")+ matcher.group("nss");
			
			onEmployee(listener, fullName, nss);
			
			listener.onEmployeeOtherInfo(matcher.group("docType"), matcher.group("doc"), matcher.group("gender"), simpleDateFormat.parse(matcher.group("birthDate")));
			
			matcher = find(reader, ENTERPRISE_NAME_CCC_CIF);
			
			String socialReason = matcher.group("name");
			String enterpriseCCC = matcher.group("province") + matcher.group("ccc");
			String enterpriseCIF = matcher.group("cif");
			
			matcher = find(reader, ENTERPRISE_ACTIVITY_REGIME);
			String enterpriseActivityCode = matcher.group("code");
			String enterpriseActivityDescription = matcher.group("description");
			String enterpriseRegime = matcher.group("regime");
			
			matcher = find(reader, EMPLOYEE_PERIOD_START);
			Date startDate = simpleDateFormat.parse(matcher.group("start"));
			
			matcher = find(reader, CONTRACT_TYPE_START_END);
			listener.onContractType(matcher.group("contractType"));
			listener.onContractStart(simpleDateFormat.parse(matcher.group("start")));
			
			if(hasData(matcher.group("end"))) {
				try {
					listener.onContractEnd(simpleDateFormat.parse(matcher.group("end")));
				} catch (ParseException e) {
					listener.onContractEnd(null);
				}	
			}
			
			matcher = find(reader, CONTRACT_PARTIALCOEF_DATE_AGE);
			if(null != matcher.group("partialCoef")) listener.onContractPartialCoeficient(matcher.group("partialCoef"));
			
			matcher = find(reader, CONTRACT_QUOTEGROUP_INACTIVITY_COMPLETECCC);
			listener.onContractQuoteGroup(matcher.group("quoteGroup"));
			String enterpriseCompleteCCC = (matcher.group("completeCCC"));
			if(hasData(matcher.group("inactivity"))) listener.onContractInactivityType(matcher.group("inactivity"));
			
			onEnterprise(listener, socialReason, enterpriseCCC, enterpriseCIF, enterpriseActivityCode,
					enterpriseActivityDescription, enterpriseRegime, enterpriseCompleteCCC);
			
			matcher = find(reader, CONTRACT_OCUPATION);
			if(hasData(matcher.group("ocupation"))) listener.onContractOcupation(matcher.group("ocupation"));
			
			matcher = find(reader, CONTRACT_QUOTEMODALITY);
			if(hasData(matcher.group("quoteModality"))) listener.onContractAgrarianQuoteModality(matcher.group("quoteModality"));
			
			matcher = find(reader, CONTRACT_REALJOURNEY);
			if(hasData(matcher.group("realJourney"))) listener.onContractAgrarianRealJourney(matcher.group("realJourney"));
			if(hasData(matcher.group("realJourneyProvided"))) listener.onContractAgrarianRealJourneyProvided(matcher.group("realJourneyProvided"));
			
			matcher = find(reader, PECULIARITIES_HEADER);
			
			Date endDate = null;
			startDate = null;
			
			try {
				for ( Optional<Matcher> optional = attempt(reader, EMPLOYEE_QUOTE_PEC); 
					optional.isPresent() ; optional = attempt(reader, EMPLOYEE_QUOTE_PEC)){
					
					endDate = simpleDateFormat.parse(optional.get().group("end"));
					
					String code = optional.get().group("code");
					String description = optional.get().group("description");
					String portTipo = optional.get().group("tipo");
					String quota = optional.get().group("quota");
					Date start = simpleDateFormat.parse(optional.get().group("start"));
					Date end = simpleDateFormat.parse(optional.get().group("end"));
					
					if ( !start.equals(startDate) || !end.equals(endDate)) 
						listener.onEmployeePerido(nss, enterpriseCCC, start, end);
					onEmployeeQuotePEC(listener, nss, enterpriseCCC, code, description, portTipo, quota, start, end);
					
					startDate = start;
					endDate = end;
					
				}
			} catch ( ParseException e ) {
				throw new UnknownPDFException(e);					
			} 
			
			matcher = find(reader, TOTAL_CLV);
			matcher = find(reader, QUOTATION_TYPES);
			Double it = hasData(matcher.group("it")) ? Double.parseDouble(matcher.group("it").replace(",", ".")) : null;
			Double ims = hasData(matcher.group("ims")) ? Double.parseDouble(matcher.group("ims").replace(",", ".")) : null;
			Double unemployment = hasData(matcher.group("unemployment")) ? Double.parseDouble(matcher.group("unemployment").replace(",", ".")) : null;
			listener.onEmployeeQuoteTypes(it, ims, unemployment);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static void onEmployeeQuotePEC(IdcListener listener, String nss, String enterpriseCCC, String code,
			String description, String portTipo, String quota, Date start, Date end) {
		code = remove(code, " ");
		quota = remove(quota, " ");
		portTipo = remove(portTipo, " ");
		listener.onEmployeeQuotePEC(nss, enterpriseCCC, code, description, portTipo, quota, start, end);
	}

	private static void onEnterprise(IdcListener listener, String socialReason, String enterpriseCCC,
			String enterpriseCIF, String enterpriseActivityCode, String enterpriseActivityDescription,
			String enterpriseRegime, String enterpriseCompleteCCC) {
		socialReason = trim(socialReason);
		enterpriseCCC = remove(enterpriseCCC, " ");
		enterpriseCIF = remove(enterpriseCIF, " ");
		enterpriseCIF = removeStart( enterpriseCIF, "0");
		enterpriseActivityCode = remove(enterpriseActivityCode, " ");
		enterpriseActivityDescription = trim(enterpriseActivityDescription);
		enterpriseRegime = remove(enterpriseRegime, " ");
		enterpriseCompleteCCC = remove(enterpriseCompleteCCC, " ");
		
		listener.onEnterprise(socialReason, enterpriseCCC, enterpriseCIF, enterpriseActivityCode, enterpriseActivityDescription, enterpriseRegime, enterpriseCompleteCCC);
	}

	private static void onEmployee(IdcListener listener, String fullName, String nss) {
		nss = remove(nss, " ");
		fullName = trim(fullName);
		listener.onEmployee(nss, fullName);
	}
	
	private static boolean hasData(String data) {
		return !StringUtils.isBlank(data);
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

	private static Optional<Matcher> attempt( BufferedReader reader, Pattern pattern ) throws IOException {
		reader.mark(256);
		String line = reader.readLine() ; 
		Matcher matcher = pattern.matcher(line) ;
		if ( matcher.matches() )
			return Optional.of(matcher);
		
		reader.reset();
		
		return Optional.empty();
				
	}
	
	//NOMBRE Y APELLIDOS: JOSEFA LOPEZ VARO
	private static final Pattern EMPLOYEE_NAME = 
	Pattern.compile(
	"^NOMBRE\\s*Y\\s*APELLIDOS\\s*:\\s*(?<name>.+)$"
	, Pattern.CASE_INSENSITIVE);
		
	//NSS: 11 1058186657 DOC.IDENTIFICATIVO: D.N.I. NUM: 052300641K SEXO: MUJER NACIMIENTO: 30-04-1964
	private static final Pattern EMPLOYEE_NSS_TYPEDOC_DOC_GENDER_BIRTHDATE = 
	Pattern.compile(
			"^NSS\\s*:\\s*(?<province>[0-9]{2})\\s*(?<nss>[0-9]+)\\s*DOC.\\s*IDENTIFICATIVO\\s*:\\s*(?<docType>.*)NUM\\s*:\\s*(?<doc>.+)SEXO\\s*:\\s*(?<gender>.*)NACIMIENTO\\s*:\\s*(?<birthDate>[0-9]+-[0-9]+-[0-9]+)$"
			, Pattern.CASE_INSENSITIVE);
	
	//RAZÓN SOCIAL: SOUTHWEST GOLF S.L. CCC: 11 112501771 DNI/NIE/CIF: 9 0B85729648
	private static final Pattern ENTERPRISE_NAME_CCC_CIF = 
	Pattern.compile(
	"^RAZÓN\\s*SOCIAL\\s*:\\s*(?<name>.+)CCC\\s*:\\s*(?<province>[0-9]{2})\\s*(?<ccc>[0-9]+)\\s*DNI/NIE/CIF\\s*:\\s*(?<type>[0-9]{1})\\s*(?<cif>.+)$"
	, Pattern.CASE_INSENSITIVE);
	
	//ACTIVIDAD ECONOMICA: 9311 Gestión de instalaciones deportivas REGIMEN: REGIMEN GENERAL
	private static final Pattern ENTERPRISE_ACTIVITY_REGIME = 
	Pattern.compile(
	"^ACTIVIDAD\\s*ECONOMICA\\s*:\\s*(?<code>[0-9]+)\\s*(?<description>.*)REGIMEN\\s*:\\s*(?<regime>.*)$"
	, Pattern.CASE_INSENSITIVE);
	
	//ACTIVIDAD ECONOMICA: 9311 Gestión de instalaciones deportivas REGIMEN: REGIMEN GENERAL
	private static final Pattern EMPLOYEE_PERIOD_START = 
	Pattern.compile(
	"^PERIODO\\s*:\\s*DESDE\\s*(?<start>[0-9]+-[0-9]+-[0-9]+)\\s*.\\s*$"
	, Pattern.CASE_INSENSITIVE);
	
	//TIPO CONTRATO: 289 INDEFINIDO.TIEMPO PARCIAL.TRANSFORMACION ALTA: 01-05-2018 BAJA:  
	private static final Pattern CONTRACT_TYPE_START_END = 
	Pattern.compile(
	"^TIPO\\s*CONTRATO\\s*:\\s*(?<contractType>.+)ALTA\\s*:\\s*(?<start>[0-9]+-[0-9]+-[0-9]+)\\s*BAJA\\s*:\\s*(?<end>[0-9]+-[0-9]+-[0-9]+)*$"
	, Pattern.CASE_INSENSITIVE);
	
	//COEF.TIEMPO PARCIAL: 500 REDUCCIÓN JORNADA/COEFIC:  FECHA: 01-11-2019 EDAD: 55
	private static final Pattern CONTRACT_PARTIALCOEF_DATE_AGE = 
	Pattern.compile(
	"^COEF\\.\\s*TIEMPO\\s*PARCIAL\\s*:\\s*(?<partialCoef>.+)REDUCCIÓN\\s*JORNADA/COEFIC\\s*:\\s*FECHA\\s*:\\s*(?<date>[0-9]+-[0-9]+-[0-9]+)\\s*EDAD\\s*:\\s*(?<age>[0-9]+)$"
	, Pattern.CASE_INSENSITIVE);
	
	//GC/M*: 08 RELEVO:  TIPO DE INACTIVIDAD/COEFIC: T.ACT.PAR.PR.COVID19/300 C.C.C.: 0111 11 112501771
	private static final Pattern CONTRACT_QUOTEGROUP_INACTIVITY_COMPLETECCC =
	Pattern.compile(
	"^GC/M\\*:\\s*(?<quoteGroup>[0-9]{2})\\s*RELEVO\\s*:\\s*TIPO\\s*DE\\s*INACTIVIDAD/COEFIC\\s*:\\s*(?<inactivity>.*)C\\.C\\.C\\.:\\s*(?<completeCCC>[0-9]{4}\\s*[0-9]{2}\\s*[0-9]+)$"
	, Pattern.CASE_INSENSITIVE);
	
	//TRABAJADOR SUSTITUTO*:  OCUPACION*:   
	private static final Pattern CONTRACT_OCUPATION =
	Pattern.compile(
	"^TRABAJADOR\\s*SUSTITUTO\\*:\\s*(?<sustituteEmployee>[0-9]{2})*OCUPACION\\*\\s*:\\s*(?<ocupation>.*)$"
	, Pattern.CASE_INSENSITIVE);
	
	//MODALIDAD DE COTIZACIÓN:   DISCAPACIDAD -GRADO Y TIPO-
	private static final Pattern CONTRACT_QUOTEMODALITY =
	Pattern.compile(
	"^MODALIDAD\\s*DE\\s*COTIZACIÓN\\s*:\\s*(?<quoteModality>.*)*DISCAPACIDAD\\s*-GRADO\\s*Y\\s*TIPo-$"
	, Pattern.CASE_INSENSITIVE);

	
	//JORNADAS REALES REALIZADAS:  JORNADAS REALES PREVISTAS:  TIPO:
	private static final Pattern CONTRACT_REALJOURNEY =
	Pattern.compile(
	"^JORNADAS\\s*REALES\\s*REALIZADAS\\s*:\\s*(?<realJourney>.*)*JORNADAS\\s*REALES\\s*PREVISTAS\\s*:\\s*(?<realJourneyProvided>.*)\\s*TIPO:\\s*(?<disabilityType>.*)*$"
	, Pattern.CASE_INSENSITIVE);
	
	//TIPO DE PECULIARIDAD PORCENTAJE/TIPO CUANTÍA/MES FRACCIÓN DE CUOTA DESDE HASTA CLV
	private static final Pattern PECULIARITIES_HEADER =
	Pattern.compile(
	"^TIPO\\s*DE\\s*PECULIARIDAD\\s*PORCENTAJE/TIPO\\s*.*$"
	, Pattern.CASE_INSENSITIVE);

	//37 EXONE.ERE.F.MAY.COMP 85,00  01   CUOTA EMPRESARIAL 14-05-2020 31-05-2020 FD4
	private static final Pattern EMPLOYEE_QUOTE_PEC = 
	Pattern.compile(
	"^\\s*(?<code>[0-9]+)\\s+(?<description>.*)\\s+(?<tipo>[0-9,]+)\\s+(?<quota>[0-9]{2})([^0-9]+)\\s+(?<start>[0-9]+-[0-9]+-[0-9]+)\\s*(?<end>[0-9]+-[0-9]+-[0-9]+).*$"
	, Pattern.CASE_INSENSITIVE);
	
	//TOTAL CLV NFL
	private static final Pattern TOTAL_CLV =
	Pattern.compile(
	"^TOTAL\\s*CLV.*$"
	, Pattern.CASE_INSENSITIVE);
	
	//TIPOS DE COTIZACIÓN* CONTINGENCIAS PROFESIONALES: IT: 1,70 I.M.S.: 1,30 TOTAL: 3,00 DESEMPLEO: 7,05
	private static final Pattern QUOTATION_TYPES = 
	Pattern.compile(
	"^TIPOS\\s*DE\\s*COTIZACIÓN\\*\\s*CONTINGENCIAS\\s*PROFESIONALES:\\s*IT:\\s*(?<it>[0-9,]+)\\s*I\\.M\\.S\\.:\\s*(?<ims>[0-9,]+).*DESEMPLEO:\\s*(?<unemployment>[0-9,]+)$"
	, Pattern.CASE_INSENSITIVE);
			
}
