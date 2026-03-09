package com.esferalia.aon.in.payroll.tgss.idc;

import static com.esferalia.aon.watson.util.AonStringUtils.remove;
import static com.esferalia.aon.watson.util.AonStringUtils.removeStart;
import static com.esferalia.aon.watson.util.AonStringUtils.trim;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.payroll.tgss.cra.StringUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public class IdcParser {

	public static void parse(File file, IdcParserListener listener) throws IOException, UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(file)) {
			parse(doc, listener);
		}
	}

	public static void parse(byte[] data, IdcParserListener idcListener) throws IOException, UnknownPDFException {
		try (InputStream is = new ByteArrayInputStream(data)) {
			parse(is, idcListener);
		}
	}

	public static void parse(InputStream is, IdcParserListener listener) throws IOException, UnknownPDFException {
		try (PDDocument doc = Loader.loadPDF(is.readAllBytes())) {
			parse(doc, listener);
		}
	}

	public static void parse(PDDocument doc, IdcParserListener listener) throws IOException, UnknownPDFException {
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

	public static void parse(String text, IdcParserListener listener) throws IOException, UnknownPDFException {
//		System.out.println(text);
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

			Matcher matcher = find(reader, EMPLOYEE_NAME);

			String fullName = matcher.group("name");

			matcher = find(reader, EMPLOYEE_NSS_TYPEDOC_DOC_GENDER_BIRTHDATE);

			String nss = AonStringUtils.leftPad(matcher.group("province"), 2, '0') + AonStringUtils.leftPad(matcher.group("nss"), 10, '0');

			onEmployee(listener, fullName, nss);

			listener.onEmployeeOtherInfo(matcher.group("docType"), matcher.group("doc"), matcher.group("gender"),
					simpleDateFormat.parse(matcher.group("birthDate")));

			matcher = find(reader, ENTERPRISE_NAME_CCC_CIF);

			String socialReason = matcher.group("name");
			String enterpriseCCC = AonStringUtils.leftPad(matcher.group("province"), 2, '0') + AonStringUtils.leftPad(matcher.group("ccc"), 9, '0');
			String enterpriseCIF = matcher.group("cif");

			matcher = find(reader, ENTERPRISE_ACTIVITY_REGIME);
			String enterpriseActivityCode = matcher.group("code");
			String enterpriseActivityDescription = matcher.group("description");
			String enterpriseRegime = matcher.group("regime");

			matcher = find(reader, EMPLOYEE_PERIOD_START);
			Date periodStart = simpleDateFormat.parse(matcher.group("start"));
			Date periodEnd = matcher.group("end") != null ? simpleDateFormat.parse(matcher.group("end")) : null;
			listener.onEmployeePerido(nss, enterpriseCCC, periodStart, periodEnd);

			matcher = find(reader, CONTRACT_TYPE_START_END);

			listener.onContractStart(simpleDateFormat.parse(matcher.group("start")));

			if (hasData(matcher.group("end"))) {
				try {
					listener.onContractEnd(simpleDateFormat.parse(matcher.group("end")));
				} catch (ParseException e) {
					listener.onContractEnd(null);
				}
			}

			if (hasData(matcher.group("contractType"))) {
				listener.onContractType(matcher.group("contractType"));
			}

			matcher = find(reader, RLCE_COTIZACION_ADIC);

			if (hasData(matcher.group("rlce")))
				listener.onRlce(matcher.group("rlce"));

			matcher = find(reader, CONTRACT_PARTIALCOEF_DATE_AGE);
			if (hasData(matcher.group("partialCoef"))) {
				listener.onContractPartialCoeficient(matcher.group("partialCoef"));
			}

			matcher = find(reader, CONTRACT_QUOTEGROUP_MONTHLY_INACTIVITY_COMPLETECCC);
			if (hasData(matcher.group("quoteGroup"))) {
				listener.onContractQuoteGroup(matcher.group("quoteGroup"));
			}

//			String enterpriseCompleteCCC = (matcher.group("completeCCC"));

			if (hasData(matcher.group("inactivity"))) {
				listener.onContractInactivityType(matcher.group("inactivity"));
			}

			String enterpriseCompleteCCC = createEnterpriseCompleteCCC(enterpriseRegime, enterpriseCCC);

			onEnterprise(listener, socialReason, enterpriseCCC, enterpriseCIF, enterpriseActivityCode,
					enterpriseActivityDescription, enterpriseRegime, enterpriseCompleteCCC);

			matcher = find(reader, CONTRACT_OCUPATION);
			if (hasData(matcher.group("ocupation"))) {
				listener.onContractOcupation(matcher.group("ocupation"));
			}

			matcher = find(reader, CONTRACT_TRL);
			if (hasData(matcher.group("trl"))) {
				listener.onEmployeeQuoteTRL(nss, enterpriseCCC, matcher.group("trl"), periodStart, periodEnd);
			}

			matcher = find(reader, CONTRACT_QUOTEMODALITY);
			if (hasData(matcher.group("quoteModality"))) {
				listener.onContractAgrarianQuoteModality(matcher.group("quoteModality"));
			}

			matcher = find(reader, CONTRACT_REALJOURNEY);
			if (hasData(matcher.group("realJourney"))) {
				listener.onContractAgrarianRealJourney(matcher.group("realJourney"));
			}
			if (hasData(matcher.group("realJourneyProvided"))) {
				listener.onContractAgrarianRealJourneyProvided(matcher.group("realJourneyProvided"));
			}

			matcher = find(reader, BENEFITS_LOSS_BY_EMPLOYEE);
			if (hasData(matcher.group("cause"))) {
				listener.onEmployeeBenefitsLoss(nss, enterpriseCCC, matcher.group("cause"), periodStart, periodEnd);
			}

			matcher = find(reader, SUSPENSION_HEADER);
			for (Optional<Matcher> optional = attempt(reader, TIPO_SUSPENSION_FECHAS); optional
					.isPresent(); optional = attempt(reader, TIPO_SUSPENSION_FECHAS)) {
				String suspensionType = optional.get().group("suspensionType").trim();
				Date from = simpleDateFormat.parse(optional.get().group("from"));
				Date to = null;
				if (optional.get().group("to") != null)
					to = simpleDateFormat.parse(optional.get().group("to"));

				listener.onEmployeeIT(suspensionType, from, to);
			}

			matcher = find(reader, PECULIARITIES_HEADER);

			Date endDate = null;
			Date startDate = null;

			try {
				for (Optional<Matcher> optional = attempt(reader, EMPLOYEE_QUOTE_PEC); optional
						.isPresent(); optional = attempt(reader, EMPLOYEE_QUOTE_PEC)) {

					// String end = optional.get().group("end");
					if (optional.get().group("end") != null)
						endDate = simpleDateFormat.parse(optional.get().group("end"));

					String code = optional.get().group("code");
					String description = optional.get().group("description");
					String portTipo = optional.get().group("tipo");
					String quota = optional.get().group("quota");
					Date start = simpleDateFormat.parse(optional.get().group("start"));

					Date end = null;
					if (optional.get().group("end") != null)
						end = simpleDateFormat.parse(optional.get().group("end"));

//					if ( AonUtils.notEquals(start,startDate) || AonUtils.notEquals(end, endDate) ) 
//						listener.onEmployeePerido(nss, enterpriseCCC, start, end);

					onEmployeeQuotePEC(listener, nss, enterpriseCCC, code, description, portTipo, quota, start, end);

					startDate = start;
					endDate = end;

				}
			} catch (ParseException e) {
				throw new UnknownPDFException(e);
			}

			matcher = find(reader, TOTAL_CLV);
			matcher = find(reader, QUOTATION_TYPES);
			Double it = hasData(matcher.group("it")) ? Double.parseDouble(matcher.group("it").replace(",", ".")) : null;
			Double ims = hasData(matcher.group("ims")) ? Double.parseDouble(matcher.group("ims").replace(",", "."))
					: null;
			Double unemployment = hasData(matcher.group("unemployment"))
					? Double.parseDouble(matcher.group("unemployment").replace(",", "."))
					: null;
			listener.onEmployeeQuoteTypes(it, ims, unemployment);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static String createEnterpriseCompleteCCC(String enterpriseRegime, String enterpriseCCC) {
		String regime = "0111";
		if (AonStringUtils.containsIgnoreCase(enterpriseRegime, "REGIMEN GENERAL"))
			regime = "0111";
		else if (AonStringUtils.containsIgnoreCase(enterpriseRegime, "HOGAR"))
			regime = "0138";
		else if (AonStringUtils.containsIgnoreCase(enterpriseRegime, "AGRARIO"))
			regime = "0163";
		else if (AonStringUtils.containsIgnoreCase(enterpriseRegime, "ARTISTA"))
			regime = "0112";
		return regime + enterpriseCCC;
	}

	private static void onEmployeeQuotePEC(IdcParserListener listener, String nss, String enterpriseCCC, String code,
			String description, String portTipo, String quota, Date start, Date end) {
		code = remove(code, " ");
		quota = remove(quota, " ");
		portTipo = remove(portTipo, " ");
		listener.onEmployeeQuotePEC(nss, enterpriseCCC, code, description, portTipo, quota, start, end);
	}

	private static void onEnterprise(IdcParserListener listener, String socialReason, String enterpriseCCC,
			String enterpriseCIF, String enterpriseActivityCode, String enterpriseActivityDescription,
			String enterpriseRegime, String enterpriseCompleteCCC) {
		socialReason = trim(socialReason);
		enterpriseCCC = remove(enterpriseCCC, " ");
		enterpriseCIF = remove(enterpriseCIF, " ");
		enterpriseCIF = removeStart(enterpriseCIF, "0");
		enterpriseActivityCode = remove(enterpriseActivityCode, " ");
		enterpriseActivityDescription = trim(enterpriseActivityDescription);
		enterpriseRegime = remove(enterpriseRegime, " ");
		enterpriseCompleteCCC = remove(enterpriseCompleteCCC, " ");

		listener.onEnterprise(socialReason, enterpriseCCC, enterpriseCIF, enterpriseActivityCode,
				enterpriseActivityDescription, enterpriseRegime, enterpriseCompleteCCC);
	}

	private static void onEmployee(IdcParserListener listener, String fullName, String nss) {
		nss = remove(nss, " ");
		fullName = trim(fullName);
		listener.onEmployee(nss, fullName);
	}

	private static boolean hasData(String data) {
		return !StringUtils.isBlank(data);
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

	protected static Double parseDouble(String string) throws ParseException {
		return AonNumberUtils.toDouble(AonStringUtils.replace(string, ",", "."));
	}

	protected static Date parseDate(String string) throws ParseException {
		return new SimpleDateFormat("dd-MM-yyyy").parse(string);
	}

	// NOMBRE Y APELLIDOS: JOSEFA LOPEZ VARO
	protected static final Pattern EMPLOYEE_NAME = Pattern.compile("^NOMBRE\\s*Y\\s*APELLIDOS\\s*:\\s*(?<name>.+)$",
			Pattern.CASE_INSENSITIVE);

	// NSS: 11 1058186657 DOC.IDENTIFICATIVO: D.N.I. NUM: 052300641K SEXO: MUJER
	// NACIMIENTO: 30-04-1964
	protected static final Pattern EMPLOYEE_NSS_TYPEDOC_DOC_GENDER_BIRTHDATE = Pattern.compile(
			"^NSS\\s*:\\s*(?<province>\\d{1,2})\\s+(?<nss>\\d{8,12})\\s+DOC\\.?\\s*IDENTIFICATIVO\\s*:\\s*(?<docType>[\\w\\.\\s]+?)\\s+NUM\\s*:\\s*(?<doc>[A-Z0-9]+)\\s+SEXO\\s*:\\s*(?<gender>[A-ZÁÉÍÓÚÜÑa-záéíóúüñ]+)\\s+NACIMIENTO\\s*:\\s*(?<birthDate>\\d{2}-\\d{2}-\\d{4})$",
			Pattern.CASE_INSENSITIVE);

	// RAZÓN SOCIAL: SOUTHWEST GOLF S.L. CCC: 11 112501771 DNI/NIE/CIF: 9 0B85729648
	protected static final Pattern ENTERPRISE_NAME_CCC_CIF = Pattern.compile(
			"^RAZÓN\\s*SOCIAL\\s*:\\s*(?<name>.+?)\\s+CCC\\s*:\\s*(?<province>\\d{1,2})\\s+(?<ccc>\\d+)\\s+DNI/NIE/CIF\\s*:\\s*(?<type>\\d)\\s+(?<cif>[A-Z0-9]+)$",
			Pattern.CASE_INSENSITIVE);

	// ACTIVIDAD ECONOMICA: 9311 Gestión de instalaciones deportivas REGIMEN:
	// REGIMEN GENERAL
	protected static final Pattern ENTERPRISE_ACTIVITY_REGIME = Pattern.compile(
			"^ACTIVIDAD\\s*ECONOMICA\\s*:\\s*(?<code>[0-9]+)\\s*(?<description>.*)REGIMEN\\s*:\\s*(?<regime>.*)$",
			Pattern.CASE_INSENSITIVE);

	// ACTIVIDAD ECONOMICA: 9311 Gestión de instalaciones deportivas REGIMEN:
	// REGIMEN GENERAL
	protected static final Pattern EMPLOYEE_PERIOD_START = Pattern.compile(
			"^PERIODO\\s*:\\s*DESDE\\s*(?<start>[0-9]+-[0-9]+-[0-9]+)(\\s*HASTA\\s*(?<end>[0-9]+-[0-9]+-[0-9]+))?.*$",
			Pattern.CASE_INSENSITIVE);

	// TIPO CONTRATO: 289 INDEFINIDO.TIEMPO PARCIAL.TRANSFORMACION ALTA: 01-05-2018
	// BAJA:
	protected static final Pattern CONTRACT_TYPE_START_END = Pattern.compile(
			"^T(\\.|IPO)\\s*CONTRATO\\s*:\\s*(?<contractType>[0-9]*)(?<contractDescription>.*)ALTA\\s*:\\s*(?<start>[0-9]+-[0-9]+-[0-9]+)\\s*BAJA\\s*:\\s*(?<end>[0-9]+-[0-9]+-[0-9]+)*$",
			Pattern.CASE_INSENSITIVE);

	// R.L.C.E.: PRACT. NO LAB. EMP COTIZACIÓN ADICIONAL:
	protected static final Pattern RLCE_COTIZACION_ADIC = Pattern
			.compile("^R\\.L\\.C\\.E\\.\\s*:\\s*(?<rlce>.*)COTIZACIÓN.*$", Pattern.CASE_INSENSITIVE);

	// COEF.TIEMPO PARCIAL: 500 REDUCCIÓN JORNADA/COEFIC: FECHA: 01-11-2019 EDAD: 55
	protected static final Pattern CONTRACT_PARTIALCOEF_DATE_AGE = Pattern.compile(
			"^COEF\\.\\s*TIEMPO\\s*PARCIAL\\s*:\\s*(?<partialCoef>\\d{1,3})?\\s*REDUCCIÓN\\s*JORNADA/COEFIC\\s*:\\s*FECHA\\s*:\\s*(?<date>\\d{2}-\\d{2}-\\d{4})?\\s*EDAD\\s*:\\s*(?<age>\\d+)?$"
//	"^COEF\\.\\s*TIEMPO\\s*PARCIAL\\s*:\\s*(?<partialCoef>[0-9]{3})?.*REDUCCIÓN\\s*JORNADA/COEFIC\\s*:.*FECHA\\s*:\\s*(?<date>[0-9]+-[0-9]+-[0-9]+)\\s*EDAD\\s*:\\s*(?<age>[0-9]+)?$"
//	"^COEF\\.\\s*TIEMPO\\s*PARCIAL\\s*:\\s*(?<partialCoef>[0-9]{3})?.*REDUCCIÓN\\s*JORNADA/COEFIC\\s*:\\s*FECHA\\s*:\\s*(?<date>[0-9]+-[0-9]+-[0-9]+)\\s*EDAD\\s*:\\s*(?<age>[0-9]+)?$"
			, Pattern.CASE_INSENSITIVE);

	// GC/M*: 08 RELEVO: TIPO DE INACTIVIDAD/COEFIC: T.ACT.PAR.PR.COVID19/300
	// C.C.C.: 0111 11 112501771
	protected static final Pattern CONTRACT_QUOTEGROUP_MONTHLY_INACTIVITY_COMPLETECCC = Pattern.compile(
			"^GC/M\\*:\\s*(?<quoteGroup>\\d{2})(?:/(?<monthly>\\S))?\\s*RELEVO:\\s*(?<relevo>\\S*)\\s*TIPO\\s+DE\\s+INACTIVIDAD/COEFIC:\\s*(?<inactivity>.*?)\\s*C\\.C\\.C\\.\\s*:\\s*(?<completeCCC>[\\d\\s]+)?$"
// "^GC/M\\*:\\s*(?<quoteGroup>[0-9]{2})/?(?<monthly>.)?\\S*\\s*RELEVO\\s*:\\s*\\S*\\s*TIPO\\s*DE\\s*INACTIVIDAD/COEFIC\\s*:\\s*(?<inactivity>.*)C\\.C\\.C\\.:\\s*(?<completeCCC>[0-9]{4}\\s*[0-9]{2}\\s*[0-9]+)?$"
//	"^GC/M\\*:\\s*(?<quoteGroup>[0-9]{2})/?(?<monthly>.)?\\S*\\s*RELEVO\\s*:\\s*TIPO\\s*DE\\s*INACTIVIDAD\\/COEFIC\\s*:\\s*(?<inactivity>.*)C\\.C\\.C\\.:\\s*(?<completeCCC>[0-9]{4}\\s*[0-9]{2}\\s*[0-9]+)?$"
			, Pattern.CASE_INSENSITIVE);

	// TRABAJADOR SUSTITUTO*: OCUPACION*:
	protected static final Pattern CONTRACT_OCUPATION = Pattern.compile(
			"^TRABAJADOR\\s*SUSTITUTO\\*:\\s*(?<sustituteEmployee>.*)OCUPACION\\*\\s*:\\s*(?<ocupation>([a-z](?!\\.TRAB))?).*$",
			Pattern.CASE_INSENSITIVE);

	// COLECTIVO S/EXCLUSIÓN EN COTIZACIÓN: PROGRAMAS DE FORMACION:
	protected static final Pattern CONTRACT_TRL = Pattern.compile(
			"^COLECTIVO\\s*S/EXCLUSIÓN\\s*EN\\s*COTIZACIÓN\\s*:\\s*(?<trl>.*?)\\s*FECHA.*$", Pattern.CASE_INSENSITIVE);

	// MODALIDAD DE COTIZACIÓN: DISCAPACIDAD -GRADO Y TIPO-
	protected static final Pattern CONTRACT_QUOTEMODALITY = Pattern.compile(
			"^MODALIDAD\\s*DE\\s*COTIZACIÓN\\s*:\\s*(?<quoteModality>.*)*DISCAPACIDAD\\s*-GRADO\\s*Y\\s*TIPo-$",
			Pattern.CASE_INSENSITIVE);

	// JORNADAS REALES REALIZADAS: JORNADAS REALES PREVISTAS: TIPO:
	protected static final Pattern CONTRACT_REALJOURNEY = Pattern.compile(
			"^JORNADAS\\s*REALES\\s*REALIZADAS\\s*:\\s*(?<realJourney>.*)*JORNADAS\\s*REALES\\s*PREVISTAS\\s*:\\s*(?<realJourneyProvided>.*)\\s*TIPO:\\s*(?<disabilityType>.*)*$",
			Pattern.CASE_INSENSITIVE);

	// SUSPENSIÓN POR SITUACIONES QUE PUEDEN DAR ORIGEN A PRESTACIONES POR CORTA
	// DURACIÓN DEL SISTEMA DE LA SEGURIDAD SOCIAL (Tipo de suspensión/Desde/Hasta)
	protected static final Pattern SUSPENSION_HEADER = Pattern
			.compile("^SUSPENSI.N\\s*POR\\s*SITUACIONES\\s*QUE\\s*PUEDEN\\s*DAR\\s*.*$", Pattern.CASE_INSENSITIVE);

	// P. DELEG.-ACCIDENTE DE TRABAJO 04-04-2024 17-04-2024
	protected static final Pattern TIPO_SUSPENSION_FECHAS = Pattern.compile(
			"^(P\\.\\s*DELEG\\.\\-)?(?<suspensionType>.*?)\\s*(?<from>\\d{2}\\-\\d{2}\\-\\d{4})\\s*(?<to>\\d{2}\\-\\d{2}\\-\\d{4})?.*$",
			Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ);

	// TIPO DE PECULIARIDAD PORCENTAJE/TIPO CUANTÍA/MES FRACCIÓN DE CUOTA DESDE
	// HASTA CLV
	protected static final Pattern PECULIARITIES_HEADER = Pattern
			.compile("^TIPO\\s*DE\\s*PECULIARIDAD\\s*PORCENTAJE/TIPO\\s*.*$", Pattern.CASE_INSENSITIVE);

	// 37 EXONE.ERE.F.MAY.COMP 85,00 01 CUOTA EMPRESARIAL 14-05-2020 31-05-2020 FD4
	protected static final Pattern EMPLOYEE_QUOTE_PEC = Pattern.compile(
			"^\\s*(?<code>[0-9]+)\\s+(?<description>.*)\\s+(?<tipo>[0-9,]+)\\s+(?<quota>[0-9]{2})([^0-9]+)\\s+(?<start>[0-9]+-[0-9]+-[0-9]+)\\s*(?<end>[0-9]+-[0-9]+-[0-9]+)?.*$",
			Pattern.CASE_INSENSITIVE);

	// TOTAL CLV NFL
	protected static final Pattern TOTAL_CLV = Pattern.compile("^TOTAL\\s*CLV.*$", Pattern.CASE_INSENSITIVE);

	// TIPOS DE COTIZACIÓN* CONTINGENCIAS PROFESIONALES: IT: 1,70 I.M.S.: 1,30
	// TOTAL: 3,00 DESEMPLEO: 7,05
	protected static final Pattern QUOTATION_TYPES = Pattern.compile(
			"^TIPOS\\s*DE\\s*COTIZACIÓN\\*\\s*CONTINGENCIAS\\s*PROFESIONALES:\\s*IT:\\s*(?<it>[0-9,]+)?\\s*I\\.M\\.S\\.:\\s*(?<ims>[0-9,]+)?.*DESEMPLEO:\\s*(?<unemployment>[0-9,]+)?(EXCLUIDO)?$",
			Pattern.CASE_INSENSITIVE);

	// POR TRABAJADOR:CAUSA:ALTA 3 MESES PREVIOS CONTRATO INDEFINIDO
	protected static final Pattern BENEFITS_LOSS_BY_EMPLOYEE = Pattern
			.compile("^POR\\s*TRABAJADOR\\s*:\\s*CAUSA\\s*:\\s*(?<cause>.*)$", Pattern.CASE_INSENSITIVE);

}
