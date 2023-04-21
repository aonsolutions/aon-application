package com.esferalia.aon.in.payroll.ivl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.platform.commons.function.Try;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.payroll.tgss.cra.StringUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class IvlCccParser implements IvlParserListener{

	public static void parse(File file, IvlParserListener listener) throws IOException, UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(file)) {
			parse(doc, listener);
		}
	}

	public static void parse(byte[] data, IvlParserListener ivlListener) throws IOException, UnknownPDFException {
		try (InputStream is = new ByteArrayInputStream(data)) {
			parse(is, ivlListener);

		}
	} 

	public static void parse(InputStream is, IvlParserListener listener) throws IOException, UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(is)) {
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
		// Contador de paginas
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


	private static Matcher find(BufferedReader reader, Pattern pattern) throws IOException, UnknownPDFException {
		String line;
		while ((line = reader.readLine()) != null) {
			if (AonStringUtils.isBlank(line))
				continue;
			line = AonStringUtils.trim(line);
			Matcher matcher = pattern.matcher(line);
			if (!matcher.matches()) {
				continue;
			}
			return matcher;
		}
		throw new UnknownPDFException(String.format("Pattern: '%s' Not found", pattern.pattern()));
	}



	private static Optional<Matcher> attempt(BufferedReader reader, Pattern pattern) throws IOException {
		reader.mark(256);
		String line = reader.readLine();
		Matcher matcher = pattern.matcher(line);
		if (matcher.matches())
			return Optional.of(matcher);

		reader.reset();

		return Optional.empty();

	}

	public static void parse(String text, IvlParserListener listener) throws IOException, UnknownPDFException {

		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

			Matcher matcher = find(reader, ENTERPRISE_NAME_CCC_CIF);

			matcher = find(reader, GET_ENTERPRISE_NAME_CCC_CIF);

			// RAZONSOCIAL
			String socialReason = matcher.group("socialReason");
			// CODIGO CUENTA DE COTIZACION
			String ccc = matcher.group("ccc1");
			String regime = ccc.substring(0,4);
			// NIF EMPRESARIO
			String nif = matcher.group("ccc3");

			String fullCcc = ccc + nif;

			matcher = find(reader, ENTERPRISE_DOM_LOCAL_CP_SOL_PERIOD);

			matcher = find(reader, GET_ENTERPRISE_DOM);
			// DOMICILIO
			String address = matcher.group("address");
//			System.out.println(home);

//			matcher = find(reader, GET_LOCAL_CP_SOL_PERIOD);
			matcher = find(reader, GET_LOCALIDAD);
			// LOCALIDAD
			String city = matcher.group("city");
//			System.out.println(city);
			// CP
			String cp = matcher.group("cp");
//			System.out.println(cp);
			// DESDE -- PERIODO SOLICITADO
			String from = matcher.group("from");
//			System.out.println(from);
			// HASTA -- PERIODO SOLICITADO
			String to = matcher.group("to");
//			System.out.println(to);

			matcher = find(reader, CNAE_AT_TYPES);

			matcher = find(reader, GET_CNAE);
			// NUMERO CNAE
			String economicActivityCode = matcher.group("cnaeNumber");
			// TEXTO CNAE
			String economicActivityDescription = matcher.group("cnaeText");
			
			listener.onEnterprise(socialReason, ccc, regime, nif, economicActivityCode, economicActivityDescription, fullCcc);
//			System.out.println(socialReason);
			listener.onEnterpriseAddress(city, address, cp, economicActivityCode);

			listener.onEnterprisePeriod(from, to);
			matcher = find(reader, GET_AT_TYPES);

			// TIPOS AT
			// IT
			String it = matcher.group("it");
			// IMS
			String ims = matcher.group("ims");
			// TOTAL
			String total = matcher.group("total");

			listener.onAtTypes(it, ims, total);

			// HASTA AQUI PRIMERA PARTE DEL PDF

			matcher = find(reader, AFFILIATION_NUMBER_SITUATION);

			matcher = find(reader, CLV);

			matcher = find(reader, SITUATION_REALDATE_EFFECTDATE);

			while (true) {

				try {

					matcher = find(reader, GET_AFFI_NUMBER_IDENT_DOC_NAME_CLV);

					String nss = matcher.group("prov") + matcher.group("numSeg");

					String docType = matcher.group("ident");
					String docNum = matcher.group("numIdent");
					String name = matcher.group("fullName");
					String cl = matcher.group("clv");

					listener.onEmployee(nss, docType, docNum, name);

					Pattern vac = GET_VAC_RETRIB_NO;

					for (Optional<Matcher> optional = attempt(reader, vac);

							optional.isPresent(); optional = attempt(reader, vac)) {

						if (optional.get().group("situation") != null) {
							String situation = optional.get().group("situation");
//							System.out.println(situation);
//								Date start = simpleDateFormat.parse(optional.get().group("start"));
							String start = optional.get().group("start");
//								Date effect = simpleDateFormat.parse(optional.get().group("effect"));
							String effect = optional.get().group("effect");
//								Date startSit = simpleDateFormat.parse(optional.get().group("startSit"));
							String startSit = optional.get().group("startSit");
//								Date effectSit = simpleDateFormat.parse(optional.get().group("effectSit"));
							String effectSit = optional.get().group("effectSit");

							String gc = optional.get().group("gc");
							String tc = optional.get().group("tc");
//							System.out.println(tc + "DEBE SER UN NUMERO");
							String ctp = optional.get().group("ctp");
//							System.out.println(ctp + "prueba");
							String ep = optional.get().group("ep");
//							System.out.println(ep );
							it = optional.get().group("it");
							ims = optional.get().group("ims");
							total = optional.get().group("total");
							String cotDays = optional.get().group("diasCot");
							String clv = optional.get().group("clv");

							listener.onEmployeeSituation(situation, start, effect, startSit, effectSit, gc, tc,ctp, ep, it, ims,
									total, cotDays, clv);

						}
					}

				} catch (UnknownPDFException e) {
					// No more Employees
					break;
				}

			}

		}
	}

	protected static Date parseDate(String string) throws ParseException {
		return new SimpleDateFormat("dd-MM-yyyy").parse(string);
	}

	private static boolean hasData(String data) {
		return !StringUtils.isBlank(data);
	}
	
	
	
	
	
	
	//PATTERNS

	protected static final Pattern ENTERPRISE_NAME_CCC_CIF = Pattern
			.compile("^s*RAZÓN\\s*SOCIAL\\s*CÓDIGO\\s*CUENTA\\s*DE\\s*COTIZACIÓN\\s*EMPRESARIO$"

					, Pattern.CASE_INSENSITIVE);

	// SACA RAZON SOCIAL, CCC, NIF EMPRESARIO
	protected static final Pattern GET_ENTERPRISE_NAME_CCC_CIF = Pattern.compile(
			"\\s*(?<socialReason>.+[a-z])\\s*.\\s*(?<ccc1>.+[0-9]{4})\\s*(?<ccc2>.+[0-9]{2})\\s*(?<ccc3>.+[a-z][0-9].+)",
			Pattern.CASE_INSENSITIVE);

	protected static final Pattern ENTERPRISE_DOM_LOCAL_CP_SOL_PERIOD = Pattern
			.compile("^s*DOMICILIO\\s*LOCALIDAD\\s*C.\\s*P.\\s*PERIODO\\s*SOLICITADO$", Pattern.CASE_INSENSITIVE);

	// SACA DOMICILIO
	protected static final Pattern GET_ENTERPRISE_DOM = Pattern.compile("\\s*(?<address>.+)$",
			Pattern.CASE_INSENSITIVE);

	// SACA CIUDAD , CP, PERIODO SOLICITADO
	protected static final Pattern GET_LOCALIDAD = Pattern.compile(
			"^(?<city>.*)(?<cp>[0-9]{4})\\s*(?<from>[0-9]{2}\\s*[0-9]{2}\\s*[0-9]{4})\\s*/\\s*(?<to>[0-9]{2}\\s*[0-9]{2}\\s*[0-9]{4}).*$",
			Pattern.CASE_INSENSITIVE);

	protected static final Pattern GET_START_PERIOD = Pattern.compile(
			"\\s*(?<startPeriod>[0-9]{2}\\s*(<?month>)[0-9]{2}\\s*(<?year>)[0-9]{4})", Pattern.CASE_INSENSITIVE);
	protected static final Pattern GET_END_PERIOD = Pattern.compile("\\s*(?<endPeriod>[0-9]+-[0-9]+-[0-9]+)",
			Pattern.CASE_INSENSITIVE);

	// patternmasEspecifi

	protected static final Pattern CNAE_AT_TYPES = Pattern.compile("^s*CNAE\\s*TIPOS\\s*AT:\\s*IT\\s*IMS\\s*TOTAL$",
			Pattern.CASE_INSENSITIVE);

	protected static final Pattern GET_CNAE = Pattern.compile("\\s*(?<cnaeNumber>[0-9]+)\\s*(?<cnaeText>[a-z].+)",
			Pattern.CASE_INSENSITIVE);

	protected static final Pattern GET_AT_TYPES = Pattern
			.compile("\\s*(?<it>[-,0-9]+)\\s*(?<ims>[-,0-9]+)\\s*s*(?<total>[-,0-9]+)", Pattern.CASE_INSENSITIVE);

	protected static final Pattern AFFILIATION_NUMBER_SITUATION = Pattern.compile(
			"^s*NÚMERO\\s*\\DE\\s*AFILIACION\\s*DOCUMENTO\\s*IDENTIFICATIVO\\s*NOMBRE\\s*Y\\s*APELLIDOS$",
			Pattern.CASE_INSENSITIVE);

	protected static final Pattern CLV = Pattern.compile("^s*CLV$", Pattern.CASE_INSENSITIVE);

	protected static final Pattern SITUATION_REALDATE_EFFECTDATE = Pattern.compile(
			"^s*SITUACIÓN\\s*F.REAL\\s*ALTA\\s*F.EFECTO\\s*ALTA\\s*F.REAL\\s*SIT.\\s*F.EFECTO\\s*SIT.\\s*G.C/M T.C.\\s*C.T.P.\\s*EP/OC\\s*TIPOS AT:\\s*IT\\s*IMS\\s*TOTAL\\s*DIAS\\s*COT.$",
			Pattern.CASE_INSENSITIVE);
	protected static final Pattern GET_AFFI_NUMBER_IDENT_DOC_NAME_CLV = Pattern.compile(
			"\\s*(?<prov>[0-9]{2}+)\\s*(?<numSeg>[0-9]{10})\\s*(?<ident>[0-9]{1})\\s*(?<numIdent>[0-9]+[a-z]+)\\s*(?<fullName>.*[^0-9])\\s*(?<clv>[a-z\0-9]{3}.+)",
			Pattern.CASE_INSENSITIVE);

	protected static final Pattern GET_VAC_RETRIB_NO = Pattern.compile(
			"\\s*(?<situation>.*[a-z][^0-9])\\s*(?<start>[0-9]+-[0-9]+-[0-9]+)*\\s*(?<effect>[0-9]+-[0-9]+-[0-9]+)\\s*(?<startSit>[0-9]+-[0-9]+-[0-9]+)*\\s*(?<effectSit>[0-9]+-[0-9]+-[0-9]+)*\\s*(?<gc>[0-9]{2})*\\s*(?<tc>[0-9]{3})\\s*(?<ctp>[0-9]{1}.[0-9]{3})*\\s*(?<ep>[a-z]{1})*\\s*(?<it>[-,0-9]+)\\s*(?<ims>[-,0-9]+)\\s*(?<total>[-,0-9]+)\\s*(?<diasCot>[0-9]+)\\s*(?<clv>.+)",
			Pattern.CASE_INSENSITIVE);

}
