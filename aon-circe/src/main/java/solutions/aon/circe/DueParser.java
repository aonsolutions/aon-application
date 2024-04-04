package solutions.aon.circe;

import static com.esferalia.aon.watson.util.AonStringUtils.remove;
import static com.esferalia.aon.watson.util.AonStringUtils.removeStart;
import static com.esferalia.aon.watson.util.AonStringUtils.trim;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
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

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class DueParser {

	public static void main(String[] args) throws Exception {
		FileInputStream dueAutonomo = new FileInputStream("/home/ndiaz/Descargas/Due-autonomo.pdf");
	}

	public static void parse(File file, DueParserListener listener) throws Exception {
		try (PDDocument doc = Loader.loadPDF(file)) {
			parse(doc, listener);
		}
	}

	public static void parse(byte[] data, DueParserListener dueListener) throws Exception {
		try (InputStream is = new ByteArrayInputStream(data)) {
			parse(is, dueListener);
		}
	}

	public static void parse(InputStream is, DueParserListener listener) throws Exception {
		try (PDDocument doc = Loader.loadPDF(is)) {
			parse(doc, listener);
		}
	}

	public static void parse(PDDocument doc, DueParserListener listener) throws Exception {
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

//			parse(text, listener);
		}
	}

//	public static void parse(String text, DueParserListener listener) throws Exception {
////			System.out.println(text);
//		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
//			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
//
//			Matcher matcher = find(reader, EMPLOYEE_NAME);
//
//			String fullName = matcher.group("name");
//
//			matcher = find(reader, EMPLOYEE_NSS_TYPEDOC_DOC_GENDER_BIRTHDATE);
//
//			String nss = matcher.group("province") + matcher.group("nss");
//
//			onEmployee(listener, fullName, nss);
//
//			listener.onEmployeeOtherInfo(matcher.group("docType"), matcher.group("doc"), matcher.group("gender"),
//					simpleDateFormat.parse(matcher.group("birthDate")));
//
//			matcher = find(reader, ENTERPRISE_NAME_CCC_CIF);
//
//			String socialReason = matcher.group("name");
//			String enterpriseCCC = matcher.group("province") + matcher.group("ccc");
//			String enterpriseCIF = matcher.group("cif");
//
//			matcher = find(reader, ENTERPRISE_ACTIVITY_REGIME);
//			String enterpriseActivityCode = matcher.group("code");
//			String enterpriseActivityDescription = matcher.group("description");
//			String enterpriseRegime = matcher.group("regime");
//
//			matcher = find(reader, EMPLOYEE_PERIOD_START);
//			Date periodStart = simpleDateFormat.parse(matcher.group("start"));
//			Date periodEnd = matcher.group("end") != null ? simpleDateFormat.parse(matcher.group("end")) : null;
//			listener.onEmployeePerido(nss, enterpriseCCC, periodStart, periodEnd);
//
//			matcher = find(reader, CONTRACT_TYPE_START_END);
//
//			listener.onContractStart(simpleDateFormat.parse(matcher.group("start")));
//
//			if (hasData(matcher.group("end"))) {
//				try {
//					listener.onContractEnd(simpleDateFormat.parse(matcher.group("end")));
//				} catch (ParseException e) {
//					listener.onContractEnd(null);
//				}
//			}
//
//			if (hasData(matcher.group("contractType"))) {
//				listener.onContractType(matcher.group("contractType"));
//			}
//
//			matcher = find(reader, RLCE_COTIZACION_ADIC);
//
//			if (hasData(matcher.group("rlce")))
//				listener.onRlce(matcher.group("rlce"));
//
//			matcher = find(reader, CONTRACT_PARTIALCOEF_DATE_AGE);
//			if (hasData(matcher.group("partialCoef"))) {
//				listener.onContractPartialCoeficient(matcher.group("partialCoef"));
//			}
//
//			matcher = find(reader, CONTRACT_QUOTEGROUP_MONTHLY_INACTIVITY_COMPLETECCC);
//			if (hasData(matcher.group("quoteGroup"))) {
//				listener.onContractQuoteGroup(matcher.group("quoteGroup"));
//			}
//
////				String enterpriseCompleteCCC = (matcher.group("completeCCC"));
//
//			if (hasData(matcher.group("inactivity"))) {
//				listener.onContractInactivityType(matcher.group("inactivity"));
//			}
//
//			String enterpriseCompleteCCC = createEnterpriseCompleteCCC(enterpriseRegime, enterpriseCCC);
//
//			onEnterprise(listener, socialReason, enterpriseCCC, enterpriseCIF, enterpriseActivityCode,
//					enterpriseActivityDescription, enterpriseRegime, enterpriseCompleteCCC);
//
//			matcher = find(reader, CONTRACT_OCUPATION);
//			if (hasData(matcher.group("ocupation"))) {
//				listener.onContractOcupation(matcher.group("ocupation"));
//			}
//
//			matcher = find(reader, CONTRACT_TRL);
//			if (hasData(matcher.group("trl"))) {
//				listener.onEmployeeQuoteTRL(nss, enterpriseCCC, matcher.group("trl"), periodStart, periodEnd);
//			}
//
//			matcher = find(reader, CONTRACT_QUOTEMODALITY);
//			if (hasData(matcher.group("quoteModality"))) {
//				listener.onContractAgrarianQuoteModality(matcher.group("quoteModality"));
//			}
//
//			matcher = find(reader, CONTRACT_REALJOURNEY);
//			if (hasData(matcher.group("realJourney"))) {
//				listener.onContractAgrarianRealJourney(matcher.group("realJourney"));
//			}
//			if (hasData(matcher.group("realJourneyProvided"))) {
//				listener.onContractAgrarianRealJourneyProvided(matcher.group("realJourneyProvided"));
//			}
//
//			matcher = find(reader, BENEFITS_LOSS_BY_EMPLOYEE);
//			if (hasData(matcher.group("cause"))) {
//				listener.onEmployeeBenefitsLoss(nss, enterpriseCCC, matcher.group("cause"), periodStart, periodEnd);
//			}
//
//			matcher = find(reader, PECULIARITIES_HEADER);
//
//			Date endDate = null;
//			Date startDate = null;
//
//			try {
//				for (Optional<Matcher> optional = attempt(reader, EMPLOYEE_QUOTE_PEC); optional
//						.isPresent(); optional = attempt(reader, EMPLOYEE_QUOTE_PEC)) {
//
//					// String end = optional.get().group("end");
//					if (optional.get().group("end") != null)
//						endDate = simpleDateFormat.parse(optional.get().group("end"));
//
//					String code = optional.get().group("code");
//					String description = optional.get().group("description");
//					String portTipo = optional.get().group("tipo");
//					String quota = optional.get().group("quota");
//					Date start = simpleDateFormat.parse(optional.get().group("start"));
//
//					Date end = null;
//					if (optional.get().group("end") != null)
//						end = simpleDateFormat.parse(optional.get().group("end"));
//
////						if ( AonUtils.notEquals(start,startDate) || AonUtils.notEquals(end, endDate) ) 
////							listener.onEmployeePerido(nss, enterpriseCCC, start, end);
//
//					onEmployeeQuotePEC(listener, nss, enterpriseCCC, code, description, portTipo, quota, start, end);
//
//					startDate = start;
//					endDate = end;
//
//				}
//			} catch (ParseException e) {
//				throw new UnknownPDFException(e);
//			}
//
//			matcher = find(reader, TOTAL_CLV);
//			matcher = find(reader, QUOTATION_TYPES);
//			Double it = hasData(matcher.group("it")) ? Double.parseDouble(matcher.group("it").replace(",", ".")) : null;
//			Double ims = hasData(matcher.group("ims")) ? Double.parseDouble(matcher.group("ims").replace(",", "."))
//					: null;
//			Double unemployment = hasData(matcher.group("unemployment"))
//					? Double.parseDouble(matcher.group("unemployment").replace(",", "."))
//					: null;
//			listener.onEmployeeQuoteTypes(it, ims, unemployment);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//	}

	private static void onEmployeeQuotePEC(DueParserListener listener, String nss, String enterpriseCCC, String code,
			String description, String portTipo, String quota, Date start, Date end) {
		code = remove(code, " ");
		quota = remove(quota, " ");
		portTipo = remove(portTipo, " ");
		listener.onEmployeeQuotePEC(nss, enterpriseCCC, code, description, portTipo, quota, start, end);
	}

	private static void onEnterprise(DueParserListener listener, String socialReason, String enterpriseCCC,
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

	private static void onEmployee(DueParserListener listener, String fullName, String nss) {
		nss = remove(nss, " ");
		fullName = trim(fullName);
		listener.onEmployee(nss, fullName);
	}

	private static boolean hasData(String data) {
		return !AonStringUtils.isBlank(data);
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

	// Registro de Entrada: PAE: AYUDA-T PYMES
	protected static final Pattern REGISTRO_ENTRADA_PAE = Pattern
			.compile("^REGISTRO\\s*DE\\s*ENTRADA\\s*:\\s*PAE\\s*:\\s*(?<pae>.+)$", Pattern.CASE_INSENSITIVE);

	// Doc. identidad: 46857352G Nombre: Luis Fernando Apellidos: Exposito De La Fuente
	protected static final Pattern DOC_NOMBRE_APELLIDO = Pattern.compile(
			"^DOC\\.\\s*IDENTIDAD\\s*:\\s*(?<doc>.*)\\s*NOMBRE\\s*:\\s*(<nombre>.*)\\s*APELLIDOS\\s*:\\s*(<apellidos>.*$",
			Pattern.CASE_INSENSITIVE);
	
	//Nacionalidad: ESPAÑA  Sexo: Varon  Fecha de nacimineto: 20/09/1982
	protected static final Pattern NAIONALIDAD_SEXO_FECHA_NACIMIENTO = Pattern.compile("^NACIONALIDAD\\s*:\\s*(?<nacionalidad>.*)SEXO\\s*:\\s*(?<sexo>.*)FECHA\\s*DE\\s*NACIMIENTO\\s*:\\s*(?<FechNacimiento>.*)$", Pattern.CASE_INSENSITIVE);
	
	
	//S.S.Nº(NSS/NAF): 281169930272  Estado Civil: SOLTERO
	protected static final Pattern NSS_ESTADO_CIVIL = Pattern.compile("^S\\.S\\.N\\º\\s*\\(NSS\\/NAF\\)\\s*:\\s*(?<nss>.*)\\s*ESTADO\\s*CIVL\\s*:\\s*(?<estadocivil>.*)$", Pattern.CASE_INSENSITIVE);

	
}
