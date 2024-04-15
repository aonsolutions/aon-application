package solutions.aon.circe;

import static com.esferalia.aon.watson.util.AonStringUtils.remove;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.NumberFormat;
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

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AutonomoDueParser {

	public static void main(String[] args) throws Exception {
//		try (FileInputStream dueAutonomo = new FileInputStream("/home/ndiaz/Descargas/Due-autonomos.pdf")) {
//			parse(dueAutonomo, new DueParserListener() {
//				public void onRegistroEntradaYPAE(String registroEntrada, String pae) {
//					System.out.println(registroEntrada);
//					System.out.println(pae);
//				}
//			});
//		}

		Pattern p = Pattern.compile(
				"^\\s*SEXO\\s*:\\s*(?<sexo>.*)\\s*TIPO\\s*R.GIMEN\\s*MATRIMONIAL\\s*:\\s*(?<tipoRegimen>.*)$",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = p.matcher("Sexo: Mujer Tipo Régimen Matrimonial: Gananciales");
		matcher.matches();
		System.out.println(matcher.group(0));
		System.out.println(matcher.group("sexo"));
		System.out.println(matcher.group("tipoRegimen"));
//		System.out.println(matcher.group("socioAdministrador"));

	}

	public static void parse(File file, AutonomoDueParserListener listener) throws Exception {
		try (PDDocument doc = Loader.loadPDF(file)) {
			parse(doc, listener);
		}
	}

	public static void parse(byte[] data, AutonomoDueParserListener dueListener) throws Exception {
		try (InputStream is = new ByteArrayInputStream(data)) {
			parse(is, dueListener);
		}
	}

	public static void parse(InputStream is, AutonomoDueParserListener listener) throws Exception {
		try (PDDocument doc = Loader.loadPDF(is)) {
			parse(doc, listener);
		}
	}

	public static void parse(PDDocument doc, AutonomoDueParserListener listener) throws Exception {
		AccessPermission ap = doc.getCurrentAccessPermission();
		if (!ap.canExtractContent()) {
			throw new IOException("You do not have permission to extract text");
		}

		PDFTextStripper stripper = new PDFTextStripper();

		stripper.setSortByPosition(true);

		StringBuilder textBuilder = new StringBuilder();
		for (int p = 1; p <= doc.getNumberOfPages(); p++) {
			// Set the page interval to extract.
			// If we don't, then all pages would be extracted.
			stripper.setStartPage(p);
			stripper.setEndPage(p);

			String text = stripper.getText(doc);
			if (AonStringUtils.isBlank(text))
				continue;
			textBuilder.append(text);

//			System.out.println(text);
		}
		parse(textBuilder.toString(), listener);
	}

	public static void parse(String text, AutonomoDueParserListener listener)
			throws IOException, UnknownPDFException, ParseException {
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			NumberFormat currency = NumberFormat
					.getNumberInstance(new Locale.Builder().setLanguageTag("es").setRegion("ES").build());

			Matcher matcher = find(reader, REGISTRO_ENTRADA_PAE);
			String registroEntrada = matcher.group("registro");
			String pae = matcher.group("pae");
			listener.onRegistroEntradaYPAE(registroEntrada, pae);

			matcher = find(reader, DOC_NOMBRE_APELLIDO);
			String docIdentidad = matcher.group("doc");
			String nombre = matcher.group("nombre");
			String apellidos = matcher.group("apellidos");
			matcher = find(reader, NAIONALIDAD_SEXO_FECHA_NACIMIENTO);
			String nacionalidad = matcher.group("nacionalidad");
			String sexo = matcher.group("sexo");
			String fechaNaciminetoString = matcher.group("fechaNacimiento");
			Date fechaNacimiento = simpleDateFormat.parse(fechaNaciminetoString);
			matcher = find(reader, NSS_ESTADO_CIVIL);
			String nss = matcher.group("nss");
			String estadoCivil = matcher.group("estadoCivil");
			listener.onDatosPersonales(docIdentidad, nombre, apellidos, nacionalidad, sexo, fechaNacimiento, nss,
					estadoCivil);

			matcher = find(reader, DOMICILIO);
			String domicilio = matcher.group("domicilio");
			matcher = find(reader, DOMICILIO_CP);
			String cp = matcher.group("cp");
			String municipio = matcher.group("municipio");
			String provincia = matcher.group("provincia");
			String comunidad = matcher.group("comunidad");
			String pais = matcher.group("pais");
			String domicilioResidencia = domicilio + cp + " " + municipio + " " + provincia + " " + comunidad + " "
					+ pais;
			String domicilioFiscal = domicilio + cp + " " + municipio + " " + provincia + " " + comunidad + " " + pais;
			String domicilioNotificaciones = domicilio + cp + " " + municipio + " " + provincia + " " + comunidad + " "
					+ pais;
			String domicilioActividad = domicilio + cp + " " + municipio + " " + provincia + " " + comunidad + " "
					+ pais;
			listener.onDomicilios(domicilioResidencia, domicilioFiscal, domicilioNotificaciones);

			matcher = find(reader, TELEFONO_EMAIL);
			String telefonoTGSS = matcher.group("telefono");
			String emailTGSS = matcher.group("mail");
			listener.onNotificacionTGSS(telefonoTGSS, emailTGSS);

			matcher = find(reader, PREFIJO_TELEFONO_EMAIL);
			String prefijoString = matcher.group("prefijo");
			String prefijo = remove(prefijoString, " ");
			String telefonoAEAT = matcher.group("telefono");
			String emailAEAT = matcher.group("mail");
			listener.onNotificacionAEAT(prefijo, telefonoAEAT, emailAEAT);

			matcher = find(reader, RECIBIR_INFORMACION);
			String comunicacion = matcher.group("informacion");
			listener.onComunicaciones(comunicacion);

			matcher = find(reader, INICIO_ACTIVIDAD);
			String inicioActividadString = matcher.group("inicioActividad");
			Date inicioActividad = simpleDateFormat.parse(inicioActividadString);
			matcher = find(reader, NUM_PERSONAS);
			matcher = find(reader, TRABAJADORAS);
			matcher = find(reader, NUM_PERSONAS_TRABAJADORAS);
			String numTrabajadorasString = matcher.group("trabajadores");
			int numTrabajadoras = Integer.parseInt(numTrabajadorasString);
			listener.onActividades(inicioActividad, numTrabajadoras);

			matcher = find(reader, SUPERFICIE_TOTAL);
			String superficieString = matcher.group("superficie");
			float superficie = currency.parse(superficieString).floatValue();
			listener.onCentroActividad(superficie, domicilioActividad);

			matcher = find(reader, CNAE);
			String cnae = matcher.group(0);
			listener.onCNAE(cnae);

			matcher = find(reader, IAE);
			String iae = matcher.group(0);
			listener.onIAE(iae);

			matcher = find(reader, EPIGRAFE_AE);
			String epigrafeAE = matcher.group("epigrafeAE");
			matcher = find(reader, TIPO_ACTIVIDAD);
			String tipoActividad = matcher.group("tipoActividad");
			matcher = find(reader, PROVINCIA_MUNICIPIO_FECHA_INICO);
			String provinciaAE = matcher.group("provincia");
			String municipioAE = matcher.group("municipio");
			String fechaInicioAEString = matcher.group("fechaInicio");
			Date fechaInicioAE = simpleDateFormat.parse(fechaInicioAEString);
			listener.onLugarFueraDelLocal(epigrafeAE, tipoActividad, provinciaAE, municipioAE, fechaInicioAE);

			while (not(reader, SEGURIDAD_SOCIAL)) {
				matcher = find(reader, DECLARACION_CENSAL);
				String codigoString = matcher.group("codigo");
				int codigo = Integer.parseInt(codigoString);
				String respuesta = matcher.group("respuesta");
				String fechaString = matcher.group("fecha");
				if (hasData(fechaString)) {
					Date fechaDC = simpleDateFormat.parse(fechaString);
					listener.onDeclaracionCensal(codigo, respuesta, fechaDC);
				} else {
					listener.onDeclaracionCensal(codigo, respuesta, null);
				}

			}

			matcher = find(reader, TIPO_TRABAJADOR);
			String tipo = matcher.group("tipo");
			matcher = find(reader, FECHA_REAL_ALTA);
			String fechaSSString = matcher.group("fechaRealAlta");
			Date fechaSS = simpleDateFormat.parse(fechaSSString);
			listener.onSeguridadSocial(tipo, fechaSS);

			matcher = find(reader, REGIMEN_TRL_SUBGRUPO);
			String regimen = matcher.group("regimen");
			String trl = matcher.group("trl");
			String subgrupo = matcher.group("subgrupo");
			matcher = find(reader, GRUPO);
			String grupo = matcher.group("grupo");
			listener.onRegimenDeEncuadramiento(regimen, trl, subgrupo, grupo);

			matcher = find(reader, BASE_COTIZACION_RENDIMINETOS);
			String baseCotizacionString = matcher.group("baseCotizacion");
			float baseCotizacion = currency.parse(baseCotizacionString).floatValue();
			String rendimientoNetoString = matcher.group("rendimientoNeto");
			float rendimientoNeto = currency.parse(rendimientoNetoString).floatValue();
			listener.onBaseCotizacion(baseCotizacion, rendimientoNeto);

			matcher = find(reader, MUTUA_IT);
			String mutuaIT = matcher.group("mutuaIT");
			listener.onIncapacidadTemporal(mutuaIT);

			matcher = find(reader, CONTINGENCIAS_PROFESIONALES);
			String contingenciaProfesional = matcher.group("contingencaProfesional");
			matcher = find(reader, CESE_ACTIVIDAD);
			String ceseActividad = matcher.group("ceseActividad");
			listener.onCobertura(contingenciaProfesional, ceseActividad);

			matcher = find(reader, REDUCCIONES);
			String reduccion = matcher.group("reduccion");
			listener.onReduccion(reduccion);

			matcher = find(reader, OPCION_CA_FP);
			String opcionCAFP = matcher.group("opcionCAFP");
			listener.onOpcionCAFP(opcionCAFP);

			matcher = find(reader, CUENTA);
			String cuenta = matcher.group("cuenta");
			listener.onCuenta(cuenta);

		}
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

	private static boolean not(BufferedReader reader, Pattern pattern) throws IOException {
		return attempt(reader, pattern).isEmpty();
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
			"^\\s*S.*NSS.*NAF.*:\\s*(?<nss>.*)\\s*ESTADO\\s*CIVIL\\s*:\\s*(?<estadoCivil>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Domicilio Residencia: CALLE OCHO DE MARZO, 4, Portal 2, piso 2, Puerta C,
	protected static final Pattern DOMICILIO = Pattern.compile("^DOMICILIO\\s*.*:\\s*(?<domicilio>.*)$",
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
	protected static final Pattern NUM_PERSONAS = Pattern.compile("^\\s*N.MERO\\s*DE\\s*PERSONAS.*$",
			Pattern.CASE_INSENSITIVE);

	// Trabajadoras
	protected static final Pattern TRABAJADORAS = Pattern.compile("^\\s*TRABAJADORAS.*$", Pattern.CASE_INSENSITIVE);

	// :0
	protected static final Pattern NUM_PERSONAS_TRABAJADORAS = Pattern.compile("^\\s*:\\s*(?<trabajadores>\\d*)$",
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
	protected static final Pattern DECLARACION_CENSAL = Pattern.compile(
			"^(?<codigo>\\d{3})\\s*.*(?<respuesta>sí|no)\\s*(?<fecha>.*)$",
			Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ | Pattern.UNICODE_CASE);

	// SEGURIDAD SOCIAL
	protected static final Pattern SEGURIDAD_SOCIAL = Pattern.compile("^SEGURIDAD\\s*SOCIAL\\s*$",
			Pattern.CASE_INSENSITIVE);

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
	protected static final Pattern GRUPO = Pattern.compile("^GRUPO\\s*:\\s*(?<grupo>\\d{4}).*$",
			Pattern.CASE_INSENSITIVE);

	// Base Cotización: 900,00 Rendimientos netos anuales en promedio mensual:
	// 400,00
	protected static final Pattern BASE_COTIZACION_RENDIMINETOS = Pattern.compile(
			"^BASE\\s*COTIZACI.N\\s*:\\s*(?<baseCotizacion>.*)RENDIMIENTOS\\s*NETOS\\s*.*:\\s*(?<rendimientoNeto>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Mutua de IT: IBERMUTUA
	protected static final Pattern MUTUA_IT = Pattern.compile("^\\s*MUTUA\\s*.*:\\s*(?<mutuaIT>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Acogerse a Contingencias Profesionales: Sí
	protected static final Pattern CONTINGENCIAS_PROFESIONALES = Pattern
			.compile("^.*CONTINGENCIAS\\s*.*:\\s*(?<contingencaProfesional>.*)$", Pattern.CASE_INSENSITIVE);

	// Acogerse a Cese de Actividad: No
	protected static final Pattern CESE_ACTIVIDAD = Pattern.compile(
			"^ACOGERSE\\s*A\\s*CESE\\s*DE\\s*ACTIVIDAD\\s*:\\s*(?<ceseActividad>.*)$", Pattern.CASE_INSENSITIVE);

	// Reducciones: Tarifa plana "cuota reducida por inicio de actividad artículo 38
	// ter"
	protected static final Pattern REDUCCIONES = Pattern.compile("^REDUCCIONES\\s*:\\s*(?<reduccion>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Opcion CA FP: No
	protected static final Pattern OPCION_CA_FP = Pattern.compile("^OPCION\\s*CA\\s*FP\\s*:\\s*(?<opcionCAFP>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Cuenta: 0073 0100 54 0496630843
	protected static final Pattern CUENTA = Pattern.compile("^CNAE\\\\s*:\\\\s*(?<cnae>\\\\d{4}).*$",
			Pattern.CASE_INSENSITIVE);

}
