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
		try (FileInputStream dueAutonomo = new FileInputStream("/home/ndiaz/Descargas/Due-autonomo.pdf")) {
			parse(dueAutonomo, null);
		}
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

			System.out.println(text);
			parse(text, listener);
		}
	}

	public static void parse(String text, DueParserListener listener) throws Exception {
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

			Matcher matcher = find(reader, REGISTRO_ENTRADA_PAE);
			String registroEntrada = matcher.group("registro");
			String pae = matcher.group("pae");
			
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
//
//	private static void onEmployeeQuotePEC(DueParserListener listener, String nss, String enterpriseCCC, String code,
//			String description, String portTipo, String quota, Date start, Date end) {
//		code = remove(code, " ");
//		quota = remove(quota, " ");
//		portTipo = remove(portTipo, " ");
//		listener.onEmployeeQuotePEC(nss, enterpriseCCC, code, description, portTipo, quota, start, end);
//	}
//
//	private static void onEnterprise(DueParserListener listener, String socialReason, String enterpriseCCC,
//			String enterpriseCIF, String enterpriseActivityCode, String enterpriseActivityDescription,
//			String enterpriseRegime, String enterpriseCompleteCCC) {
//		socialReason = trim(socialReason);
//		enterpriseCCC = remove(enterpriseCCC, " ");
//		enterpriseCIF = remove(enterpriseCIF, " ");
//		enterpriseCIF = removeStart(enterpriseCIF, "0");
//		enterpriseActivityCode = remove(enterpriseActivityCode, " ");
//		enterpriseActivityDescription = trim(enterpriseActivityDescription);
//		enterpriseRegime = remove(enterpriseRegime, " ");
//		enterpriseCompleteCCC = remove(enterpriseCompleteCCC, " ");
//
//		listener.onEnterprise(socialReason, enterpriseCCC, enterpriseCIF, enterpriseActivityCode,
//				enterpriseActivityDescription, enterpriseRegime, enterpriseCompleteCCC);
//	}
//
//	private static void onEmployee(DueParserListener listener, String fullName, String nss) {
//		nss = remove(nss, " ");
//		fullName = trim(fullName);
//		listener.onEmployee(nss, fullName);
//	}

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

	protected static Double parseDouble(String string) {
		return AonNumberUtils.toDouble(AonStringUtils.replace(string, ",", "."));
	}

	protected static Date parseDate(String string) throws ParseException {
		return new SimpleDateFormat("dd-MM-yyyy").parse(string);
	}

	// Registro de Entrada: PAE: AYUDA-T PYMES
	protected static final Pattern REGISTRO_ENTRADA_PAE = Pattern.compile(
			"^REGISTRO\\s*DE\\s*ENTRADA\\s*:\\s*(?<registro>.*)PAE\\s*:\\s*(?<pae>.*)$", Pattern.CASE_INSENSITIVE);

	// Doc. identidad: 46857352G Nombre: Luis Fernando Apellidos: Exposito De La
	// Fuente
	protected static final Pattern DOC_NOMBRE_APELLIDO = Pattern.compile(
			"^DOC\\.\\s*IDENTIDAD\\s*:\\s*(?<doc>.*)\\s*NOMBRE\\s*:\\s*(?<nombre>.*)\\s*APELLIDOS\\s*:\\s*(?<apellidos>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Nacionalidad: ESPAÑA Sexo: Varon Fecha de nacimiento: 20/09/1982
	protected static final Pattern NAIONALIDAD_SEXO_FECHA_NACIMIENTO = Pattern.compile(
			"^NACIONALIDAD\\s*:\\s*(?<nacionalidad>.*)SEXO\\s*:\\s*(?<sexo>.*)FECHA\\s*DE\\s*NACIMIENTO\\s*:\\s*(?<fechaNacimiento>.*)$",
			Pattern.CASE_INSENSITIVE);

	// S.S.Nº(NSS/NAF): 281169930272 Estado Civil: SOLTERO
	protected static final Pattern NSS_ESTADO_CIVIL = Pattern.compile(
			"^S\\.S\\.N\\º\\s*\\(NSS\\/NAF\\)\\s*:\\s*(?<nss>.*)\\s*ESTADO\\s*CIVIL\\s*:\\s*(?<estadocivil>.*)$",
			Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

	// Domicilio Residencia: CALLE OCHO DE MARZO, 4, Portal 2, piso 2, Puerta C,
	protected static final Pattern DOMICILIO = Pattern.compile("^DOMICILIO\\\\s*.*:\\\\s*(?<domicilio>.*)$",
			Pattern.CASE_INSENSITIVE);

	// 28523, RIVAS-VACIAMADRID, MADRID, MADRID, ESPAÑA,
	protected static final Pattern DOMICILIO_CP = Pattern.compile(
			"^(?<cp>.*)\\,\\s*(?<municipio>.*)\\,\\s*(?<provincia>.*)\\,\\s*(?<comunidad>.*)\\,\\s*(?<pais>.*)\\,.*$",
			Pattern.CASE_INSENSITIVE);

	// Teléfono: 650625500 E-Mail: fexposito.ef@icloud.com
	protected static final Pattern TELEFONO_EMAIL = Pattern
			.compile("^TEL.FONO\\s*:\\s*(?<telefono>.*)\\s*E\\-MAIL\\s*:\\s*(?<mail>.*)$", Pattern.CASE_INSENSITIVE);

	// Prefijo: 34 Teléfono: 650625500 E-Mail: fexposito.ef@icloud.com
	protected static final Pattern PREFIJO_TELEFONO_EMAIL = Pattern.compile(
			"^PREFIJO\\s*:\\s*(?<prefijo>.*)TEL.FONO\\s*:\\s*(?<telefono>.*)E\\-MAIL\\s*:\\s*(?<mail>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Deseo recibir información institucional de la DGIPYME.: No
	protected static final Pattern RECIBIR_INFORMACION = Pattern.compile(
			"^DESEO\\s*RECIBIR\\s*INFORMACI.N\\s*INSTITUCIONAL\\s*DE\\s*LA\\s*DGIPYME\\s*\\.:\\s*(?<informacion>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Inicio de Actividad: 01/04/2024
	protected static final Pattern INICIO_ACTIVIDAD = Pattern
			.compile("^INICIO\\s*DE\\s*ACTIVIDAD\\s*.\\s*(?<inicioActividad>.*)$", Pattern.CASE_INSENSITIVE);

	// Número de Personas
	protected static final Pattern NUM_PERSONAS = Pattern.compile("^N.MERO\\s*DE\\s*PERSONAS.*$",
			Pattern.CASE_INSENSITIVE);

	// Trabajadoras
	protected static final Pattern TRABAJADORAS = Pattern.compile("^TRABAJADORAS.*$", Pattern.CASE_INSENSITIVE);

	// :0
	protected static final Pattern NUM_PERSONAS_TRABAJADORAS = Pattern.compile("^:\\s*(?<trabajadores>\\d*)$",
			Pattern.CASE_INSENSITIVE);

	// Superficie Total: 1,00
	protected static final Pattern SUPERFICIE_TOTAL = Pattern.compile("^SUPERFICIE\\s*TOTAL\\s*:\\s*(?<superficie>.*)$",
			Pattern.CASE_INSENSITIVE);

	// CLASIFICACIÓN NACIONAL ACTIVIDADES ECONÓMICAS (CNAE)
	protected static final Pattern CNAE = Pattern.compile("^CLASIFICACI.N\\s*.*$", Pattern.CASE_INSENSITIVE);

	// IMPUESTO ACTIVIDAD ECONÓMICA (IAE)
	protected static final Pattern IAE = Pattern.compile("^IMPUESTO\\s*ACTIVIDAD.*$", Pattern.CASE_INSENSITIVE);

	// Actividad Principal: 8559 - Otra educacion n. c. o. p.
	protected static final Pattern ACTIVIDAD_PRINCIPAL = Pattern
			.compile("^ACTIVIDAD\\s*PRINCIPAL\\s*:\\s*(?<actividadPrincipal>.*)$", Pattern.CASE_INSENSITIVE);

	// Epígrafe AE: 2*826 - PERSONAL DOCENTE ENSEÑANZAS DIVERSAS
	protected static final Pattern EPIGRAFE_AE = Pattern.compile("^EP.GRAFE\\s*AE\\s*:\\s*(?<epigrafeAE>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Tipo Actividad: A05 - PROFESIONALES
	protected static final Pattern TIPO_ACTIVIDAD = Pattern.compile("^TIPO\\s*ACTIVIDAD\\s*:\\s*(?<tipoActividad>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Provincia: MADRID Municipio: MADRID Fecha Inicio: 01/04/2024
	protected static final Pattern PROVINCIA_MUNICIPIO_FECHA_INICO = Pattern.compile(
			"^PROVINCIA\\s*:\\s*(?<provincia>.*)MUNICIPIO\\s*:\\s*(?<municipio>.*)FECHA\\s*INICIO\\s*:\\s*(?<fechaInicio>.*)$",
			Pattern.CASE_INSENSITIVE);

	// 502 Comunicación de inicio de actividad. Entregas de bienes o prestaciones de
	// servicios previa o simultánea a la adquisición Sí 01/04/2024
	// 504 Comunicación de inicio de actividad. Entregas de bienes o prestaciones de
	// servicios posterior a adquisición de bienes o No
	protected static final Pattern DECLARACION_CENSAL = Pattern.compile("^\\d{3}.*(?<respuesta>sí|no)\\s*(?<fecha>.*)$",
			Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ | Pattern.UNICODE_CASE);

	// Tipo: Trabajador Autónomo
	protected static final Pattern TIPO_TRABAJADOR = Pattern.compile("^TIPO\\s*:\\s*(?<tipo>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Fecha Real Alta: 01/04/2024
	protected static final Pattern FECHA_REAL_ALTA = Pattern
			.compile("^FECHA\\s*REAL\\s*ALTA\\s*:\\s*(?<fechaRealAlta>.*)$", Pattern.CASE_INSENSITIVE);

	// Régimen: RETA TRL: NO Subgrupo: N/A
	protected static final Pattern REGIMEN_TRL_SUBGRUPO = Pattern.compile(
			"^R.GIMEN\\s*:\\s*(?<regimen>.*)TRL\\s*:\\s*(?<trl>.*)SUBGRUPO\\s*:\\s*(?<subgrupo>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Grupo: 0521- RÉGIMEN ESPECIAL DE TRABAJADORES POR CUENTA PROPIA O AUTÓNOMOS.
	protected static final Pattern GRUPO = Pattern.compile("^GRUPO\\s*:\\s*(?<grupo>.*)$", Pattern.CASE_INSENSITIVE);

	// Base Cotización: 900,00 Rendimientos netos anuales en promedio mensual:
	// 400,00
	protected static final Pattern BASE_COTIZACION_RENDIMINETOS = Pattern.compile(
			"^BASE\\s*COTIZACI.N\\s*:\\s*(?<baseCotizacion>.*)RENDIMIENTOS\\s*NETOS\\s*.*:\\s*(?<rendiminetosNetos>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Mutua de IT: IBERMUTUA
	protected static final Pattern MUTUA_IT = Pattern.compile("^\\s*MUTUA\\s*.*:\\s*(?<mutuaIT>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Acogerse a Contingencias Profesionales: Sí
	protected static final Pattern CONTINGENCIAS_PROFESIONALES = Pattern
			.compile("^.*CONTINGENCIAS\\s*.*:\\s*(?<contingencasProfesionales>.*)$", Pattern.CASE_INSENSITIVE);

	// Acogerse a Cese de Actividad: No
	protected static final Pattern CESE_ACTIVIDAD = Pattern.compile(
			"^ACOGERSE\\s*A\\s*CESE\\s*DE\\s*ACTIVIDAD\\s*:\\s*(?<ceseActividad>.*)$", Pattern.CASE_INSENSITIVE);

	// Reducciones: Tarifa plana "cuota reducida por inicio de actividad artículo 38
	// ter"
	protected static final Pattern REDUCCIONES = Pattern.compile("^REDUCCIONES\\s*:\\s*(?<reducciones>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Opcion CA FP: No
	protected static final Pattern OPCION_CA_FP = Pattern.compile("^OPCION\\s*CA\\s*FP\\s*:\\s*(?<opcionCAFP>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Cuenta: 0073 0100 54 0496630843
	protected static final Pattern CUENTA = Pattern.compile("^CUENTA\\s*:\\s*(?<cuenta>.*)$", Pattern.CASE_INSENSITIVE);

}
