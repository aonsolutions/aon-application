package solutions.aon.circe;

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

public class SRLDueParser {
	public static void main(String[] args) throws Exception {
		try (FileInputStream dueSRL = new FileInputStream("/home/ndiaz/Descargas/Due-sociedades.pdf")) {
			parse(dueSRL, new SRLDueParserListener() {
//				public void onRegistroEntradaYPAE(String registroEntrada, String pae) {
//					System.out.println(registroEntrada);
//					System.out.println(pae);
//					System.out.println(" ");
//				}
//				
//				public void onEmpresaActividad(Date inicioActividad, Date cierreEjercicio) {
//					System.out.println(inicioActividad);
//					System.out.println(cierreEjercicio);
//					System.out.println(" ");
//				}
//				
//				public void onEmpresaDatosJuridicos(String duracionPersonaJuridica, String denominacionSocial, float capitalSocial,
//						boolean acreditaCapitalSocial, boolean estatutosTipo) {
//					System.out.println(duracionPersonaJuridica);
//					System.out.println(denominacionSocial);
//					System.out.println(capitalSocial);
//					System.out.println(acreditaCapitalSocial);
//					System.out.println(estatutosTipo);
//					System.out.println(" ");
//
//				}
//				
//				public void onPersonasTrabajadoras(int numPersonasTrabajadoras) {
//					System.out.println(numPersonasTrabajadoras);
//					System.out.println(" ");
//				}
			});
		}
	}

	public static void parse(File file, SRLDueParserListener listener) throws Exception {
		try (PDDocument doc = Loader.loadPDF(file)) {
			parse(doc, listener);
		}
	}

	public static void parse(byte[] data, SRLDueParserListener dueListener) throws Exception {
		try (InputStream is = new ByteArrayInputStream(data)) {
			parse(is, dueListener);
		}
	}

	public static void parse(InputStream is, SRLDueParserListener listener) throws Exception {
		try (PDDocument doc = Loader.loadPDF(is)) {
			parse(doc, listener);
		}
	}

	public static void parse(PDDocument doc, SRLDueParserListener listener) throws Exception {
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

	public static void parse(String text, SRLDueParserListener listener)
			throws IOException, UnknownPDFException, ParseException {
		try (BufferedReader reader = new BufferedReader(new StringReader(text))) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat simpleDateFormatWhithoutYear = new SimpleDateFormat("dd/MM");
			NumberFormat currency = NumberFormat
					.getNumberInstance(new Locale.Builder().setLanguageTag("es").setRegion("ES").build());
			
			Matcher matcher = find(reader, REGISTRO_ENTRADA_PAE_DATOS);
			String registroEntrada = matcher.group("registro");
			String pae = matcher.group("pae");
			listener.onRegistroEntradaYPAE(registroEntrada, pae);
			
			matcher = find(reader, INICIO_ACTIVIDAD_CIERRRE_EJERCICIO);
			String inicioActividad = matcher.group("inicioActividad");
			Date fechaInicioActividad = simpleDateFormat.parse(inicioActividad);
			String cierreEjercicio = matcher.group("cierreEjercicio");
			Date fechaCierreEjercicio = simpleDateFormatWhithoutYear.parse(cierreEjercicio);
			listener.onEmpresaActividad(fechaInicioActividad, fechaCierreEjercicio);
			
			matcher = find(reader, DURACION_PERSONA_JURIDICA);
			String duracionPersonaJuridica = matcher.group("duracion");
			matcher = find(reader, DENOMINACION_SOCIAL);
			String denominacionSocial = matcher.group("denominacionSocial");
			matcher = find(reader, CAPITAL_SOCIAL);
			String capitalSocialString = matcher.group("capitalSocial");
			float capitalSocial = currency.parse(capitalSocialString).floatValue();
			String acreditaCapitalSocialString = matcher.group("acreditaCapitalSocial");
			boolean acreditaCapitalSocial = false;
			if(acreditaCapitalSocialString.contentEquals("Si")) {
				acreditaCapitalSocial = true;
			}
			matcher = find(reader, ESTATUS_TIPO);
			String estatutosTipoString = matcher.group("acogeEstatutosTipo");
			boolean estatutosTipo = false;
			if(estatutosTipoString == "Sí") {
				estatutosTipo = true;
			}
			System.out.println(estatutosTipo);
//			listener.onEmpresaDatosJuridicos(duracionPersonaJuridica, denominacionSocial, capitalSocial, acreditaCapitalSocial,estatutosTipo );
			
			matcher = find(reader, NUM_PERSONAS_TRABAJADORAS);
			String personasTrabajadoras = matcher.group("numPersonasTrabajadoras");
			int numPersonasTrabajadoras = Integer.parseInt(personasTrabajadoras);
			listener.onPersonasTrabajadoras(numPersonasTrabajadoras);
			
			
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

	// REGISTRO ENTRADA Y PAE
	protected static final Pattern REGISTRO_ENTRADA_PAE = Pattern.compile("^REGISTRO\\s*ENTRADA\\s*Y\\s*PAE$",
			Pattern.CASE_INSENSITIVE);

	// Registro de Entrada: 202400061360293 PAE: AYUDA-T PYMES
	protected static final Pattern REGISTRO_ENTRADA_PAE_DATOS = Pattern.compile(
			"^REGISTRO\\s*DE\\s*ENTRADA\\s*:\\s*(?<registro>.*)PAE\\s*:\\s*(?<pae>.*)$", Pattern.CASE_INSENSITIVE);

	// EMPRESA
	protected static final Pattern EMPRESA = Pattern.compile("^EMPRESA$", Pattern.CASE_INSENSITIVE);

	// ACTIVIDAD
	protected static final Pattern ACTIVIDAD = Pattern.compile("^ACTIVIDAD$", Pattern.CASE_INSENSITIVE);

	// Inicio de Actividad: 26/04/2024 Cierre de Ejercicio: 31/12
	protected static final Pattern INICIO_ACTIVIDAD_CIERRRE_EJERCICIO = Pattern.compile(
			"^INICIO\\s*DE\\s*ACTIVIDAD\\s*:\\s*(?<inicioActividad>.*)CIERRE\\s*DE\\s*EJERCICIO\\s*:\\s*(?<cierreEjercicio>.*)$",
			Pattern.CASE_INSENSITIVE);

	// DATOS JURÍDICOS
	protected static final Pattern DATOS_JURIDICOS = Pattern.compile("^DATOS\\s*JURIDICOS$", Pattern.CASE_INSENSITIVE);

	// Duración Persona Jurídica: INDEFINIDA
	protected static final Pattern DURACION_PERSONA_JURIDICA = Pattern
			.compile("^DURACI.N\\s*PERSONA\\s*JUR.DICA\\s*:\\s*(?<duracion>.*)$", Pattern.CASE_INSENSITIVE);

	// Denominación Social: LA PLATANERA STUDIO S.L.
	protected static final Pattern DENOMINACION_SOCIAL = Pattern
			.compile("^DENOMINACI.N\\s*SOCIAL\\s*:\\s*(?<denominacionSocial>.*)$", Pattern.CASE_INSENSITIVE);

	// Capital Social: 3000,00 Acredita Capital Social: Sí
	protected static final Pattern CAPITAL_SOCIAL = Pattern.compile(
			"^CAPITAL\\s*SOCIAL\\s*:\\s*(?<capitalSocial>.*)\\s*ACREDITA\\s*CAPITAL\\s*SOCIAL\\s*:\\s*(?<acreditaCapitalSocial>.*)$",
			Pattern.CASE_INSENSITIVE);

	// OBJETO SOCIAL
	protected static final Pattern OBJETO_SOCIAL = Pattern.compile("^OBJETO\\s*SOCAIL$", Pattern.CASE_INSENSITIVE);

	// ¿Se acoge a estatutos tipo?: Sí
	protected static final Pattern ESTATUS_TIPO = Pattern.compile(
			"^\\¿SE\\s*ACOGE\\s*A\\s*ESTATUTOS\\s*TIPO\\?\\s*:\\s*(?<acogeEstatutosTipo>.*)$",
			Pattern.CASE_INSENSITIVE);

	// PERSONAS TRABAJADORAS
	protected static final Pattern PERSONAS_TRABAJADORAS = Pattern.compile("^PERSONAS\\s*TRABAJADORAS$",
			Pattern.CASE_INSENSITIVE);

	// Número de Personas Trabajadoras: 1
	protected static final Pattern NUM_PERSONAS_TRABAJADORAS = Pattern.compile(
			"^N.MERO\\s*DE\\s*PERSONAS\\s*TRABAJADORAS\\s*:\\s*(?<numPersonasTrabajadoras>.*)$",
			Pattern.CASE_INSENSITIVE);

	// DOMICILIOS
	protected static final Pattern DOMICILIOS = Pattern.compile("^DOMICILIOS$", Pattern.CASE_INSENSITIVE);

	// Domicilio Social: PASEO ABEL MATUTES JUAN, 311, 16-20,
	protected static final Pattern DOMICILIO = Pattern.compile("^(DIRECCI.N|CENTRO[^:]*|DOMICILIO[^:]*)\\s*:\\s*(?<domicilio>.*)$",
			Pattern.CASE_INSENSITIVE);

	// 07800, EIVISSA, ILLES BALEARS, ISLAS BALEARES, ESPAÑA,
	protected static final Pattern DOMICILIO_CP = Pattern.compile(
			"^.*(?<cp>\\d{5})\\,\\s*(?<municipio>.*)\\,\\s*(?<provincia>.*)\\,\\s*(?<comunidad>.*)\\,\\s*(?<pais>.*)\\,.*$",
			Pattern.CASE_INSENSITIVE);

	// COMUNICACIONES Y NOTIFICACIONES
	protected static final Pattern COMUNICACION_NOTIFICACION = Pattern.compile("^COMUNICACIONES\\s*NOTIFICACI.NES$",
			Pattern.CASE_INSENSITIVE);

	// NOTIFICACIÓN TGSS
	protected static final Pattern NOTIFICACION_TGSS = Pattern.compile("^NOTIFICACI.N\\s*TGSS$",
			Pattern.CASE_INSENSITIVE);

	// Teléfono: 669587726 E-Mail: paulatortosa@icloud.com
	protected static final Pattern TELEFONO_EMAIL = Pattern
			.compile("^TEL.FONO\\s*:\\s*(?<telefono>.*)\\s*E\\-MAIL\\s*:\\s*(?<mail>.*)$", Pattern.CASE_INSENSITIVE);

	// Medio Notificación Domicilio: Correo
	protected static final Pattern MEDIO_NOTIFICACION = Pattern.compile(
			"^MEDIO\\s*NOTIFICACI.N\\s*DOMICILIO\\s*:\\s*(?<medioNotificacionDomicilio>.*)$", Pattern.CASE_INSENSITIVE);

	// NOTIFICACIÓN AEAT
	protected static final Pattern NOTIFICACION_AEAT = Pattern.compile("^NOTIFICACI.N\\s*AEAT$",
			Pattern.CASE_INSENSITIVE);

	// Prefijo: 34 Teléfono: 669587726 E-Mail: paulatortosa@icloud.com
	protected static final Pattern PREFIJO_TELEFONO_EMAIL = Pattern.compile(
			"^PREFIJO\\s*:\\s*(?<prefijo>.*)TEL.FONO\\s*:\\s*(?<telefono>.*)E\\-MAIL\\s*:\\s*(?<mail>.*)$",
			Pattern.CASE_INSENSITIVE);

	// MEDIO NOTIFICACIÓN SOCIEDAD
	protected static final Pattern MEDIO_NOTIFICACION_SOCIEDAD = Pattern.compile("^MEDIO\\s*NOTIFICACI.N\\s*SOCIEDAD$",
			Pattern.CASE_INSENSITIVE);

	// COMUNICACIONES DE LA DIRECCIÓN GENERAL DE INDUSTRIA Y PYME
	protected static final Pattern COMUNICACIONES = Pattern.compile(
			"^COMUNICACIONES\\s*DE\\s*LA\\s*DIRECCI.N\\s*GENERAL\\s*DE\\s*INDUSTRIA\\s*Y\\s*PYME$",
			Pattern.CASE_INSENSITIVE);

	// Deseo recibir información institucional de la DGIPYME.: No
	protected static final Pattern RECIBIR_INFORMACION = Pattern.compile(
			"^DESEO\\s*RECIBIR\\s*INFORMACI.N\\s*INSTITUCIONAL\\s*DE\\s*LA\\s*DGIPYME.*:\\s*(?<recibirInformacion>.*)$",
			Pattern.CASE_INSENSITIVE);

	// SOCIO/A
	protected static final Pattern SOCIO = Pattern.compile("^SOCIO\\/A$", Pattern.CASE_INSENSITIVE);

	// Doc. Identidad: 53652201D Nombre: PAULA Apellidos: TORTOSA GRAS
	protected static final Pattern DOC_NOMBRE_APELLIDO = Pattern.compile(
			"^DOC\\.\\s*IDENTIDAD\\s*:\\s*(?<doc>.*)\\s*NOMBRE\\s*:\\s*(?<nombre>.*)\\s*APELLIDOS\\s*:\\s*(?<apellidos>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Nacionalidad: ESPAÑA Sexo: Varón Fecha de nacimiento: 10/07/1996
	protected static final Pattern NAIONALIDAD_SEXO_FECHA_NACIMIENTO = Pattern.compile(
			"^NACIONALIDAD\\s*:\\s*(?<nacionalidad>.*)SEXO\\s*:\\s*(?<sexo>.*)FECHA\\s*DE\\s*NACIMIENTO\\s*:\\s*(?<fechaNacimiento>.*)$",
			Pattern.CASE_INSENSITIVE);

	// S.S. Nº (NSS/NAF): 08/1249029155 Estado Civil: SOLTERO
	protected static final Pattern NSS_ESTADO_CIVIL = Pattern.compile(
			"^\\s*S.*NSS.*NAF.*:\\s*(?<nss>.*)\\s*ESTADO\\s*CIVIL\\s*:\\s*(?<estadoCivil>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Socio/a Trabajador/a: Sí Presentador ITP/AJD: Sí Socio/a Administrador: Sí
	protected static final Pattern SOCIO_PRESENTADOR = Pattern.compile(
			"^SOCIO.A\\s*TRABAJADOR.A\\s*:\\s*(?<socioTrabajador>.*)\\s*PRESENTADOR\\s*ITP\\/AJD\\s*:\\s*(?<presentador>.*)\\s*SOCIO.A\\s*ADMINISTRADOR\\s*:\\s*(?<socioAdministrador>.*)$",
			Pattern.CASE_INSENSITIVE);

	// ¿Ejerce funciones de Dirección y Gerencia?: Sí
	protected static final Pattern FUNCIONES_DIRECCION_GERENCIA = Pattern.compile(
			"^\\¿EJERCE\\s*FUNCIONES\\s*DE\\s*DIRECCI.N\\s*Y\\s*GERENCIA\\?\\s*:\\s*(?<funcionesDireccionGerencia>.*)$",
			Pattern.CASE_INSENSITIVE);

	// APORTACION
	protected static final Pattern APORTACION = Pattern.compile("^APORTACI.N$", Pattern.CASE_INSENSITIVE);

	// Aportación Dineraria: 0,00 Aportación no Dineraria: 1500,00
	// Descripción:telefono
	protected static final Pattern APORTACION_DESCRIPCION = Pattern.compile(
			"^APORTACI.N\\s*DINERARIA\\s*:\\s*(?<aportacionDineraria>.*)APORTACI.N\\s*NO\\s*DINERARIA\\s*:\\s*(?<aportacionNoDineraria>.*)DESCRIPCI.N\\s*:\\s*(?<descripcion>.*)",
			Pattern.CASE_INSENSITIVE);

	// CENTRO DE ACTIVIDAD
	protected static final Pattern CENTRO_DE_ACTIVIDAD = Pattern.compile("^CENTRO\\s*DE\\s*ACTIVIDAD$",
			Pattern.CASE_INSENSITIVE);

	// Nombre Centro Actividad: LA PLATANERA STUDIO SL
	protected static final Pattern NOMBRE_CENTRO_ACTIVIDAD = Pattern
			.compile("^NOMBRE\\s*CENTRO\\s*ACTIVIDAD\\s*:\\s*(?<nombreCentroActividad>.*)$", Pattern.CASE_INSENSITIVE);

	// Superficie Total: 1,00
	protected static final Pattern SUPERFICIE_TOTAL = Pattern.compile("^SUPERFICIE\\s*TOTAL\\s*:\\s*(?<superficie>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Local Principal de la Empresa: Sí Domicilio de Actividad TGSS: Sí
	protected static final Pattern LOCAL_PRINCIPAL = Pattern.compile(
			"^LOCAL\\s*PRINCIPAL\\s*DE\\s*LA\\s*EMPRESA\\s*:\\s*(?<localPrincipal>.*)DOMICILIO\\s*DE\\s*ACTIVIDAD\\s*TGSS\\s*:\\s*(?<domicilioActvidadTGSS>.*)$",
			Pattern.CASE_INSENSITIVE);

	// ACTIVIDADES
	protected static final Pattern ACTIVIDADES = Pattern.compile("^ACTIVIDADES$", Pattern.CASE_INSENSITIVE);

	// CLASIFICACIÓN NACIONAL ACTIVIDADES ECONÓMICAS (CNAE)
	protected static final Pattern CNAE = Pattern.compile(
			"^CALSIFICACI.N\\s*NACIONAL\\s*ACTIVIDADES\\s*ECON.MICAS\\s*\\(CNAE\\)$", Pattern.CASE_INSENSITIVE);

	// Actividad Principal: 7311 - Agencias de publicidad
	protected static final Pattern ACTIVIDAD_PRINCIPAL = Pattern
			.compile("^ACTIVIDAD\\s*PRINCIPAL\\s*:\\s*(?<actividadPrincipal>\\d{4})\\s*\\-.*$", Pattern.CASE_INSENSITIVE);

	// Otras Actividades: 7021 - Relaciones públicas y comunicación
	protected static final Pattern OTRA_ACTIVIDAD = Pattern
			.compile("^OTRAS\\s*ACTIVIDADES\\s*:\\s*(?<otrasActividades>\\d{4}})\\s*\\-.*$", Pattern.CASE_INSENSITIVE);

	// IMPUESTO ACTIVIDAD ECONÓMICA (IAE)
	protected static final Pattern IAE = Pattern.compile("^IMPUESTO\\s*ACTIVIDAD\\s*ECON.MICA\\s*\\(IAE\\)$",
			Pattern.CASE_INSENSITIVE);

	// LUGARES DE DESARROLLO DE LA ACTIVIDAD FUERA DEL LOCAL
	protected static final Pattern LUGARES_DE_DESARROLLO = Pattern.compile("^$", Pattern.CASE_INSENSITIVE);

	// Epígrafe AE: 1*844 - SERVICIOS PUBLICIDAD, RELACIONES PUBLIC.
	protected static final Pattern EPIGRAFE_AE = Pattern.compile("^EP.GRAFE\\s*AE\\s*:\\s*(?<epigrafeAE>[\\d\\*]{5})\\s*\\-.*$",
			Pattern.CASE_INSENSITIVE);

	// Tipo Actividad: A03 - RESTO DE ACTIVIDADES EMPRESARIALES
	protected static final Pattern TIPO_ACTIVIDAD = Pattern.compile("^TIPO\\s*ACTIVIDAD\\s*:\\s*(?<tipoActividad>\\w{3})\\s*\\-.*$",
			Pattern.CASE_INSENSITIVE);

	// Provincia: ILLES BALEARS Municipio: EIVISSA Fecha Inicio: 26/04/2024
	protected static final Pattern PROVINCIA_MUNICIPIO_FECHA_INICO = Pattern.compile(
			"^PROVINCIA\\s*:\\s*(?<provincia>.*)MUNICIPIO\\s*:\\s*(?<municipio>.*)FECHA\\s*INICIO\\s*:\\s*(?<fechaInicio>.*)$",
			Pattern.CASE_INSENSITIVE);

	// DECLARACIÓN CENSAL
	protected static final Pattern DECLARACION_CENSAL = Pattern.compile("^DECLARACI.N\\s*CENSAL$",
			Pattern.CASE_INSENSITIVE);

	// 502 Comunicación de inicio de actividad. Entregas de bienes o prestaciones de
	// servicios previa o simultánea a la adquisición Sí 01/04/2024
	// 504 Comunicación de inicio de actividad. Entregas de bienes o prestaciones de
	// servicios posterior a adquisición de bienes o No
	protected static final Pattern DECLARACION_CENSAL_DATOS = Pattern.compile(
			"^(?<codigo>\\d{3})\\s*.*(?<respuesta>sí|no)\\s*(?<fecha>.*)$",
			Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ | Pattern.UNICODE_CASE);

	// REPRESENTANTE
	protected static final Pattern REPRESENTANTE = Pattern.compile("^REPRESENTANTE$", Pattern.CASE_INSENSITIVE);

	// Causa de la representación: Legal Clave: Personas Jurídicas residentes o
	// constituidas en España
	protected static final Pattern CAUSA_REPRESENTACION = Pattern.compile(
			"^CAUSA\\s*DE\\s*LA\\s*REPRESENTACI.N\\s*:\\s*(?<causaRepresentacion>.*)CLAVE\\s*:\\s*(?<clave>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Tipo de representación: Individual Título de la representación: Documento
	// público
	protected static final Pattern TIPO__TITULO_REPRESENTACION = Pattern.compile(
			"^TIPO\\s*DE\\s*REPRESENTACI.N\\s*:\\s*(?<tipoRepresentacion>.*)T.TULO\\s*DE\\s*LA\\s*REPRESENTACI.N\\s*:\\s*(?<tituloRepresentacion>.*)$",
			Pattern.CASE_INSENSITIVE);

	// TRABAJADOR/A POR CUENTA PROPIA
	protected static final Pattern TRABAJADOR_CUENTA_PROPIA = Pattern
			.compile("^TRABAJADOR\\/A\\s*POR\\s*CUENTA\\s*PROPIA$", Pattern.CASE_INSENSITIVE);

	// ALTA DE PERSONA TRABAJADORA
	protected static final Pattern ALTA_PERSONA_TRABAJADORA = Pattern.compile("^ALTA\\s*DE\\s*PERSONA\\s*TRABAJADORA$",
			Pattern.CASE_INSENSITIVE);

	// Fecha de Alta: 26/04/2024 ¿Ejerce funciones de Dirección y Gerencia?: Sí
	protected static final Pattern FECHA_ALTA_DIRECCION_GERENCIA = Pattern.compile(
			"^FECHA\\s*DE\\s*ALTA\\s*:\\s*(?<fechaAlta>.*)\\¿EJERCE\\s*FUNCIONES\\s*DE\\s*DIRECCI.N\\s*Y\\s*GERENCIA\\s*\\?\\s*:\\s*(?<funcionesDireccionGerencia>.*)$",
			Pattern.CASE_INSENSITIVE);

	// CENTRO DE TRABAJO
	protected static final Pattern CENTRO_TRABAJO = Pattern.compile("^CENTRO\\s*DE\\s*TRABAJO$",
			Pattern.CASE_INSENSITIVE);

	// RÉGIMEN DE ENCUADRAMIENTO
	protected static final Pattern REGIMEN_ENCUADRAMIENTO = Pattern.compile("^R.GIMEN\\s*DE\\s*ENCUADRAMIENTO$",
			Pattern.CASE_INSENSITIVE);

	// Régimen: RETA TRL: NO Subgrupo: N/A
	protected static final Pattern REGIMEN_SUBGRUPO = Pattern.compile(
			"^R.GIMEN\\s*:\\s*(?<regimen>.*)TRL\\s*:\\s*(?<trl>.*)SUBGRUPO\\s*:\\s*(?<subgrupo>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Grupo: 0521- RÉGIMEN ESPECIAL DE TRABAJADORES POR CUENTA PROPIA O AUTÓNOMOS.
	protected static final Pattern GRUPO = Pattern.compile("^GRUPO\\s*:\\s*(?<grupo>\\d{4}).*$",
			Pattern.CASE_INSENSITIVE);

	// CNAE: 7021 - Relaciones públicas y comunicación
	protected static final Pattern CNAE_DATOS = Pattern.compile("^CNAE\\s*:\\s*(?<cnae>\\d{4}).*$",
			Pattern.CASE_INSENSITIVE);

	// 7311 - Agencias de publicidad
	protected static final Pattern CNAE_SECOND = Pattern.compile("^$", Pattern.CASE_INSENSITIVE);

	// Mutua de IT: FREMAP
	protected static final Pattern MUTUA_IT = Pattern.compile("^\\s*MUTUA\\s*.*:\\s*(?<mutuaIT>.*)$",
			Pattern.CASE_INSENSITIVE);

	// ¿Ha tramitado ya su alta en RETA?: No ¿Contingencias AT / EP?: Sí Acogerse a
	// Cese de Actividad: Sí
	protected static final Pattern TRAMITAR_ALTA_CONTINGENCIAS = Pattern.compile(
			"^\\¿HA\\s*TRAMITADO\\s*.*:\\s*(?<altaReta>.*)\\¿CONTINGENCIAS\\s*AT\\s*\\/\\s*EP\\s*\\?\\s*:\\s*(?<contingencias>.*)ACOGERSE\\s*A\\s*CESE\\s*DE\\s*ACTIVIDAD\\s*:\\s*(?<ceseActividad>.*)$",
			Pattern.CASE_INSENSITIVE);

	// Fecha Real Alta: 26/04/2024
	protected static final Pattern FECHA_REAL_ALTA = Pattern
			.compile("^FECHA\\s*REAL\\s*ALTA\\s*:\\s*(?<fechaRealAlta>.*)$", Pattern.CASE_INSENSITIVE);

	// Opcion CA FP: Sí
	protected static final Pattern OPCION_CA_FP = Pattern.compile("^OPCION\\s*CA\\s*FP\\s*:\\s*(?<opcionCAFP>.*)$",
			Pattern.CASE_INSENSITIVE);

	// BASE COTIZACIÓN
	protected static final Pattern BASE_COTIZACION = Pattern.compile("^BASE\\s*COTIZACI.N$", Pattern.CASE_INSENSITIVE);

	// Base Cotización: 950,98 Rendimientos netos anuales en promedio mensual:
	// 166,70
	protected static final Pattern BASE_COTIZACION_RENDIMIENTOS = Pattern.compile(
			"^BASE\\s*COTIZACI.N\\s*:\\s*(?<baseCotizacion>.*)RENDIMIENTOS\\s*NETOS\\s*ANUALES\\s*EN\\s*PROMEDIO\\s*MENSUAL\\s*:\\s*(?<rendimientoNeto>.*)$",
			Pattern.CASE_INSENSITIVE);

	// DATOS PARA LA DOMICILIACIÓN DE PAGO DE CUOTAS
	protected static final Pattern DATOS_DOMICILIACION = Pattern.compile("^DATOS\\s*PARA\\s*LA\\s*DOMICILIACI.N\\s*DE\\s+PAGO\\s*DE\\s*CUOTAS$", Pattern.CASE_INSENSITIVE);

	// Cuenta: 2100 0293 24 0200443673
	protected static final Pattern CUENTA = Pattern.compile("^CUENTA\\s*:\\s*(?<cuenta>.*)$", Pattern.CASE_INSENSITIVE);

	// Observaciones TGSS: ES71
	protected static final Pattern OBSERVACIONES_TGSS = Pattern.compile("^OBSERVACIONES\\s*TGSS\\s*:\\s*(?<observacionesTGSS>.*)$", Pattern.CASE_INSENSITIVE);

	// CITA NOTARIAL
	protected static final Pattern CITA_NOTARIAL = Pattern.compile("^CITA\\s*NOTARIAL$", Pattern.CASE_INSENSITIVE);

	// Persona Cita: PAULA TORTOSA GRAS Teléfono: 669587726
	protected static final Pattern PERSONA_CITA_TELEFONO = Pattern.compile("^PERSONA\\s*CITA\\s*:\\s*(?<personaCita>.*)TEL.FONO\\s*:\\s*(?<telefono>.*)$", Pattern.CASE_INSENSITIVE);

	// Fecha: 05/04/2024 Hora: 10:30
	protected static final Pattern FECHA_HORA = Pattern.compile("^FECHA\\s*:\\s*(?<fecha>.*)HORA\\s*:\\s*(?<hora>.*)$", Pattern.CASE_INSENSITIVE);

	// DATOS NOTARÍA
	protected static final Pattern DATOS_NOTARIA = Pattern.compile("^DATOS\\s*NOTAR.A$", Pattern.CASE_INSENSITIVE);

	// Nombre: Juan Apellidos: Acero Simón
	protected static final Pattern NOMBRE_APELLIDOS = Pattern.compile("^NOMBRE\\s*:\\s*(?<nombre>.*)APELLIDOS\\s*:\\s*(?<apellido>.*)$", Pattern.CASE_INSENSITIVE);

}
