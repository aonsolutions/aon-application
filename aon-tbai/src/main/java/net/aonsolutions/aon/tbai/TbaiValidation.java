package net.aonsolutions.aon.tbai;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import ticketbai.anulacion.AnulaTicketBai;
import ticketbai.anulacion.Cabecera;
import ticketbai.anulacion.CabeceraFacturaType;
import ticketbai.anulacion.Emisor;
import ticketbai.anulacion.EntidadDesarrolladoraType;
import ticketbai.anulacion.HuellaTBAI;
import ticketbai.anulacion.IDFactura;
import ticketbai.anulacion.IDOtro;
import ticketbai.anulacion.SoftwareFacturacionType;
import ticketbai.emision.CausaExencionType;
import ticketbai.emision.CausaNoSujetaType;
import ticketbai.emision.ClaveTipoFacturaType;
import ticketbai.emision.ClaveTipoRectificativaType;
import ticketbai.emision.ClavesType;
import ticketbai.emision.DatosFacturaType;
import ticketbai.emision.DesgloseFacturaType;
import ticketbai.emision.DesgloseTipoOperacionType;
import ticketbai.emision.Destinatarios;
import ticketbai.emision.DetalleExentaType;
import ticketbai.emision.DetalleIVAType;
import ticketbai.emision.DetalleNoExentaType;
import ticketbai.emision.DetalleNoSujeta;
import ticketbai.emision.DetallesFacturaType;
import ticketbai.emision.EncadenamientoFacturaAnteriorType;
import ticketbai.emision.Entrega;
import ticketbai.emision.ExentaType;
import ticketbai.emision.Factura;
import ticketbai.emision.FacturaRectificativaType;
import ticketbai.emision.FacturasRectificadasSustituidasType;
import ticketbai.emision.IDClaveType;
import ticketbai.emision.IDDestinatario;
import ticketbai.emision.IDDetalleFacturaType;
import ticketbai.emision.IDFacturaRectificadaSustituidaType;
import ticketbai.emision.ImporteRectificacionSustitutivaType;
import ticketbai.emision.NoExentaType;
import ticketbai.emision.NoSujetaType;
import ticketbai.emision.PrestacionServicios;
import ticketbai.emision.SiNoType;
import ticketbai.emision.SujetaType;
import ticketbai.emision.Sujetos;
import ticketbai.emision.TicketBai;
import ticketbai.emision.TipoDesgloseType;
import ticketbai.emision.TipoOperacionSujetaNoExentaType;
import ticketbai.zuzendu_alta.SubsanacionModificacionTicketBAI;

/**
 * Validaciones previas al envio de los ficheros de alta y de anulacion TicketBAI.
 *
 * Las validaciones y los codigos de error se corresponden con los documentos de
 * la DFA "Listado de validaciones y errores del fichero de ALTA TicketBAI"
 * version 13.0 (documentacion/tbai/Validaciones fichero de Alta
 * TicketBAI-v13.pdf) y "Listado de validaciones y errores del fichero de
 * ANULACION TicketBAI" version 4.0 (documentacion/tbai/Validaciones fichero de
 * Anulacion TicketBAI-v4.pdf), y con los esquemas ticketBaiV1-2-2.xsd y
 * Anula_ticketBaiV1-2-2.xsd.
 */
public class TbaiValidation {

	private static final String ID_VERSION_TBAI = "1.2";
	private static final String ID_VERSION_ZUZENDU = "1.0";
	private static final String FORMATO_FECHA = "dd-MM-yyyy";

	/** Longitudes maximas de los tipos simples del esquema de anulacion. */
	private static final int MAX_TEXTO_20 = 20;
	private static final int MAX_TEXTO_30 = 30;
	private static final int MAX_TEXTO_120 = 120;

	private static final Pattern PATRON_NIF =
		Pattern.compile("([a-zA-Z]\\d{7}[a-zA-Z])|(\\d{8}[a-zA-Z])|([a-zA-Z]\\d{8})");
	private static final Pattern PATRON_FECHA = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");

	/** Tipos de identificacion admitidos en el pais de residencia (IDTypeType). */
	private static final List<String> TIPOS_ID_OTRO = List.of("02", "03", "04", "05", "06");

	// ***** Constantes de las validaciones del fichero de alta

	/** Longitudes maximas de los tipos simples del esquema de alta. */
	private static final int MAX_TEXTO_100 = 100;
	private static final int MAX_TEXTO_250 = 250;

	/** Cardinalidades maximas de los bloques repetibles del esquema de alta. */
	private static final int MAX_DESTINATARIOS = 100;
	private static final int MAX_FACTURAS_RECTIFICADAS = 100;
	private static final int MAX_DETALLES_FACTURA = 1000;
	private static final int MAX_CLAVES = 3;
	private static final int MAX_DETALLES_EXENTA = 7;
	private static final int MAX_DETALLES_NO_EXENTA = 2;
	private static final int MAX_DETALLES_NO_SUJETA = 4;
	private static final int MAX_DETALLES_IVA = 12;

	private static final String FORMATO_HORA = "HH:mm:ss";

	private static final Pattern PATRON_HORA = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");

	/** Importe de 15 digitos (12 enteros y 2 decimales) con signo opcional. */
	private static final Pattern PATRON_IMPORTE = Pattern.compile("(\\+|-)?\\d{1,12}(\\.\\d{0,2})?");

	/** Importe de las lineas de detalle: 12 enteros y 8 decimales. */
	private static final Pattern PATRON_IMPORTE_DETALLE = Pattern.compile("(\\+|-)?\\d{1,12}(\\.\\d{0,8})?");

	/** Tipo impositivo o de recargo: 3 enteros y 2 decimales. */
	private static final Pattern PATRON_TIPO = Pattern.compile("\\d{1,3}(\\.\\d{0,2})?");

	/**
	 * Formato del NIF-IVA: el prefijo de dos letras del pais que lo ha asignado y
	 * el numero de identificacion, sin exceder los 20 caracteres del tipo
	 * TextMax20Type con el que se informa.
	 */
	private static final Pattern PATRON_NIF_IVA = Pattern.compile("[a-zA-Z]{2}[a-zA-Z0-9]{1,18}");

	/** Caracteres que no admiten la serie ni el numero de factura. */
	private static final String CARACTERES_PROHIBIDOS = "#%&'+\u00BF?";

	/**
	 * Las series que empiezan por DFA_ estan reservadas para las facturas que se
	 * obtienen con el programa FakturAraba de la DFA.
	 */
	private static final String SERIE_RESERVADA_DFA = "DFA_";

	/** Prefijo del identificador de los destinatarios de Irlanda del Norte. */
	private static final String PREFIJO_IRLANDA_NORTE = "XI";

	/** Letras iniciales de los NIF de personas fisicas que no son un DNI. */
	private static final String LETRAS_PERSONA_FISICA = "KLMXYZ";

	/** Personas juridicas que pueden usar las claves de recargo de equivalencia. */
	private static final String LETRAS_ENTIDAD_CLAVES_51_52 = "EJ";

	/** Entidades publicas, que son los destinatarios de la clave 14. */
	private static final String LETRAS_ENTIDAD_CLAVE_14 = "PQSV";

	/** Destinatarios que no obligan a informar el tipo impositivo ni la cuota. */
	private static final String LETRAS_SIN_TIPO_IMPOSITIVO = "RGQNW";

	/** Tipo de identificacion NIF-IVA del bloque IDOtro. */
	private static final String TIPO_ID_OTRO_NIF_IVA = "02";

	private static final String CLAVE_01 = "01";
	private static final String CLAVE_02 = "02";
	private static final String CLAVE_04 = "04";
	private static final String CLAVE_06 = "06";
	private static final String CLAVE_07 = "07";
	private static final String CLAVE_08 = "08";
	private static final String CLAVE_10 = "10";
	private static final String CLAVE_14 = "14";
	private static final String CLAVE_17 = "17";
	private static final String CLAVE_19 = "19";
	private static final String CLAVE_51 = "51";
	private static final String CLAVE_53 = "53";
	private static final String CLAVE_54 = "54";

	private static final String CAUSA_NO_SUJECION_OT = "OT";
	private static final String CAUSA_NO_SUJECION_RL = "RL";
	private static final String CAUSA_NO_SUJECION_VT = "VT";
	private static final String CAUSA_NO_SUJECION_IE = "IE";
	private static final String CAUSA_EXENCION_E5 = "E5";

	/** Claves de regimen de IVA admitidas por el esquema. */
	private static final Set<String> CLAVES_VALIDAS = Set.of("01", "02", "03", "04", "05", "06", "07",
		"08", "09", "10", "11", "12", "13", "14", "15", "17", "19", "51", "52", "53", "54");

	/**
	 * Compatibilidad de las claves de regimen de IVA (validacion 10-131): para cada
	 * primera clave, los valores que pueden tomar la segunda y la tercera. Las
	 * claves que no aparecen deben ir solas.
	 */
	private static final Map<String, Set<String>> CLAVES_COMPATIBLES = Map.ofEntries(
		Map.entry("01", Set.of("02")),
		Map.entry("03", Set.of("01")),
		Map.entry("05", Set.of("01", "06", "08", "11", "12", "13")),
		Map.entry("06", Set.of("11", "12", "13", "14", "15")),
		Map.entry("07", Set.of("01", "03", "05", "09", "11", "12", "13", "14", "15")),
		Map.entry("08", Set.of("01", "02")),
		Map.entry("11", Set.of("08", "15")),
		Map.entry("12", Set.of("08", "15")),
		Map.entry("13", Set.of("08", "15")),
		Map.entry("19", Set.of("01", "19", "51", "52")),
		Map.entry("51", Set.of("01", "19", "51", "52")),
		Map.entry("52", Set.of("01", "19", "51", "52")));

	/** Claves con las que la fecha de operacion puede ser posterior a la actual. */
	private static final Set<String> CLAVES_SIN_LIMITE_FECHA_OPERACION = Set.of("14", "15");

	/** Claves para las que no se comprueba el cuadre con las lineas de detalle. */
	private static final Set<String> CLAVES_SIN_CUADRE_DETALLES = Set.of("03", "05", "06", "09");

	/** Claves para las que no se comprueba el cuadre con los desgloses. */
	private static final Set<String> CLAVES_SIN_CUADRE_DESGLOSE = Set.of("03", "05", "06", "09", "17");

	/** Claves con las que se admite la inversion del sujeto pasivo. */
	private static final Set<String> CLAVES_INVERSION_SUJETO_PASIVO = Set.of("01", "04", "05", "06", "12");

	/** Claves de las operaciones en recargo de equivalencia o regimen simplificado. */
	private static final Set<String> CLAVES_RECARGO_EQUIVALENCIA = Set.of("51", "52");

	/** Claves con las que se admite informar la cuota de recargo de equivalencia. */
	private static final Set<String> CLAVES_CUOTA_RECARGO_EQUIVALENCIA = Set.of("01", "07", "52");

	/** Regimenes especiales que tributan por el margen de beneficio. */
	private static final Set<String> CLAVES_MARGEN_DE_BENEFICIO = Set.of("03", "09");

	/** Claves cuyo tipo impositivo debe ser el general. */
	private static final Set<String> CLAVES_TIPO_IMPOSITIVO_GENERAL = Set.of("11", "12", "13");

	/** Claves cuyo desglose no sujeto solo admite la causa VT (validacion 10-275). */
	private static final Set<String> CLAVES_CAUSA_NO_SUJECION_SOLO_VT = Set.of("02", "03", "04", "07");

	/** Claves cuyo desglose no sujeto no admite la causa IE (validacion 10-279). */
	private static final Set<String> CLAVES_CAUSA_NO_SUJECION_SIN_IE = Set.of("05", "09");

	/** Claves que admiten que solo haya desglose no sujeto con causa VT (10-278). */
	private static final Set<String> CLAVES_ADMITEN_SOLO_NO_SUJECION_VT = Set.of("01", "51", "52");

	/** Causas de no sujecion que exige la clave 08 (validacion 10-189). */
	private static final Set<String> CAUSAS_NO_SUJECION_CLAVE_08 = Set.of("RL", "IE");

	/** Causas de no sujecion que admite la clave 08 en solitario (validacion 10-190). */
	private static final Set<String> CAUSAS_NO_SUJECION_CLAVE_08_UNICA = Set.of("RL", "IE", "VT");

	/** Causas de no sujecion que admite la clave 19 en solitario (validacion 10-241). */
	private static final Set<String> CAUSAS_NO_SUJECION_CLAVE_19 = Set.of("OT", "VT");

	/** Causas de no sujecion que no admite la clave 54 (validacion 10-276). */
	private static final Set<String> CAUSAS_NO_SUJECION_CLAVE_54 = Set.of("OT", "RL");

	/** Causas de exencion incompatibles con la clave 01 (validacion 10-174). */
	private static final Set<String> CAUSAS_EXENCION_CLAVE_01 = Set.of("E2", "E3");

	/** Causas de exencion incompatibles con la clave 07 (validacion 10-188). */
	private static final Set<String> CAUSAS_EXENCION_CLAVE_07 = Set.of("E2", "E3", "E4", "E5");

	/** Codigos de pais de los estados miembros de la Union Europea. */
	private static final Set<String> PAISES_UE = Set.of("AT", "BE", "BG", "CY", "CZ", "DE", "DK", "EE",
		"ES", "FI", "FR", "GR", "HR", "HU", "IE", "IT", "LT", "LU", "LV", "MT", "NL", "PL", "PT", "RO",
		"SE", "SI", "SK");

	/** Tipo impositivo general del IVA, que exigen las claves 11, 12 y 13. */
	private static final double TIPO_IMPOSITIVO_GENERAL = 21.0;

	/** Margen con el que se comprueba que una cuota esta bien calculada. */
	private static final double TOLERANCIA_CUOTA = 0.01;

	/**
	 * Margen con el que se comprueba el cuadre del importe total de la factura con
	 * las lineas de detalle y con los desgloses. El documento de validaciones no
	 * publica el margen que aplica la DFA, por lo que solo se avisa de los
	 * descuadres que no pueden venir de un redondeo.
	 */
	private static final double TOLERANCIA_CUADRE = 1.0;

	/** Fecha desde la que se admiten las operaciones con la clave 17. */
	private static final Date FECHA_MINIMA_CLAVE_17 = AonDateUtils.parse("01-07-2021", FORMATO_FECHA);

	private TbaiValidation() {

	}

	private static class AnulacionContext {
		final AnulaTicketBai anulacion;
		final List<InvoiceCommunicationError> errors = new LinkedList<>();

		AnulacionContext(AnulaTicketBai anulacion) {
			this.anulacion = anulacion;
		}

		Cabecera getCabecera() {
			return anulacion.getCabecera();
		}

		IDFactura getIDFactura() {
			return anulacion.getIDFactura();
		}

		Emisor getEmisor() {
			return getIDFactura() == null ? null : getIDFactura().getEmisor();
		}

		CabeceraFacturaType getCabeceraFactura() {
			return getIDFactura() == null ? null : getIDFactura().getCabeceraFactura();
		}

		HuellaTBAI getHuella() {
			return anulacion.getHuellaTBAI();
		}

		SoftwareFacturacionType getSoftware() {
			return getHuella() == null ? null : getHuella().getSoftware();
		}

		EntidadDesarrolladoraType getEntidadDesarrolladora() {
			return getSoftware() == null ? null : getSoftware().getEntidadDesarrolladora();
		}

		IDOtro getIDOtro() {
			return getEntidadDesarrolladora() == null ? null : getEntidadDesarrolladora().getIDOtro();
		}

		/**
		 * No se repite el mismo error cuando varios campos incumplen la misma
		 * validacion, porque los errores no identifican el campo al que pertenecen.
		 */
		void addError(InvoiceCommunicationError error) {
			if (!errors.contains(error)) {
				errors.add(error);
			}
		}
	}

	// *********************************************************
	// ********** [VALIDACION ANULACION TICKETBAI] *************
	// *********************************************************

	/**
	 * Valida el fichero de anulacion TicketBAI.
	 *
	 * Se acumulan todos los errores encontrados y, si hay alguno, se lanza una
	 * unica excepcion con todos ellos.
	 *
	 * El bloque Signature no se valida porque se incorpora al fichero al firmarlo,
	 * despues de serializar el objeto.
	 *
	 * @param anulacion fichero de anulacion a validar
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	public static void validateAnulacion(AnulaTicketBai anulacion) throws InvoiceCommunicationException {
		if (anulacion == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.TBAI_002);
		}

		AnulacionContext v = new AnulacionContext(anulacion);
		CABECERA
			.andThen(CABECERA_ID_VERSION_TBAI)
			.andThen(ID_FACTURA)
			.andThen(EMISOR)
			.andThen(EMISOR_NIF)
			.andThen(EMISOR_NOMBRE_RAZON_SOCIAL)
			.andThen(CABECERA_FACTURA)
			.andThen(CABECERA_FACTURA_SERIE)
			.andThen(CABECERA_FACTURA_NUMERO)
			.andThen(CABECERA_FACTURA_FECHA_EXPEDICION)
			.andThen(HUELLA)
			.andThen(HUELLA_NUM_SERIE_DISPOSITIVO)
			.andThen(SOFTWARE)
			.andThen(SOFTWARE_LICENCIA_TBAI)
			.andThen(SOFTWARE_ENTIDAD_DESARROLLADORA)
			.andThen(SOFTWARE_ENTIDAD_DESARROLLADORA_NIF)
			.andThen(SOFTWARE_ENTIDAD_DESARROLLADORA_ID_OTRO)
			.andThen(SOFTWARE_NOMBRE)
			.andThen(SOFTWARE_VERSION)
		.accept(v);

		if (!v.errors.isEmpty()) {
			throw new InvoiceCommunicationException(v.errors);
		}
	}

	/**
	 * 1. Cabecera
	 *
	 * 	- El bloque Cabecera es obligatorio.
	 */
	private static final Consumer<AnulacionContext> CABECERA = v -> {
		if (v.getCabecera() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 2. Cabecera >> IDVersionTBAI
	 *
	 * 	- Campo obligatorio.
	 * 	- El unico valor admitido por el esquema de anulacion es "1.2".
	 */
	private static final Consumer<AnulacionContext> CABECERA_ID_VERSION_TBAI = v -> {
		if (v.getCabecera() == null) return;

		String version = v.getCabecera().getIDVersionTBAI();
		if (AonStringUtils.isBlank(version)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.notEquals(ID_VERSION_TBAI, version)) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 3. IDFactura
	 *
	 * 	- El bloque IDFactura es obligatorio. La factura se identifica con los
	 * 	  mismos datos con los que se dio de alta.
	 */
	private static final Consumer<AnulacionContext> ID_FACTURA = v -> {
		if (v.getIDFactura() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 4. IDFactura >> Emisor
	 *
	 * 	- El bloque Emisor es obligatorio.
	 */
	private static final Consumer<AnulacionContext> EMISOR = v -> {
		if (v.getIDFactura() == null) return;

		if (v.getEmisor() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 5. IDFactura >> Emisor >> NIF
	 *
	 * 	- Campo obligatorio, necesario para el calculo del identificador TicketBAI.
	 * 	- Secuencia de nueve digitos o letras con el formato del tipo NIFType.
	 * 	- El NIF debe ser un documento valido.
	 */
	private static final Consumer<AnulacionContext> EMISOR_NIF = v -> {
		if (v.getEmisor() == null) return;

		String nif = v.getEmisor().getNIF();
		if (AonStringUtils.isBlank(nif)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (!PATRON_NIF.matcher(nif).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		} else if (!AonDocumentUtil.isValid(nif)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		}
	};

	/**
	 * 6. IDFactura >> Emisor >> ApellidosNombreRazonSocial
	 *
	 * 	- Campo obligatorio que no puede exceder los 120 caracteres.
	 */
	private static final Consumer<AnulacionContext> EMISOR_NOMBRE_RAZON_SOCIAL = v -> {
		if (v.getEmisor() == null) return;

		String nombreRazonSocial = v.getEmisor().getApellidosNombreRazonSocial();
		if (AonStringUtils.isBlank(nombreRazonSocial)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.length(nombreRazonSocial) > MAX_TEXTO_120) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 7. IDFactura >> CabeceraFactura
	 *
	 * 	- El bloque CabeceraFactura es obligatorio.
	 */
	private static final Consumer<AnulacionContext> CABECERA_FACTURA = v -> {
		if (v.getIDFactura() == null) return;

		if (v.getCabeceraFactura() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 8. IDFactura >> CabeceraFactura >> SerieFactura
	 *
	 * 	- Campo opcional que no puede exceder los 20 caracteres.
	 */
	private static final Consumer<AnulacionContext> CABECERA_FACTURA_SERIE = v -> {
		if (v.getCabeceraFactura() == null) return;

		String serie = v.getCabeceraFactura().getSerieFactura();
		if (AonStringUtils.isNotBlank(serie) && AonStringUtils.length(serie) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 9. IDFactura >> CabeceraFactura >> NumFactura
	 *
	 * 	- Campo obligatorio, necesario para el calculo del identificador TicketBAI,
	 * 	  que no puede exceder los 20 caracteres.
	 */
	private static final Consumer<AnulacionContext> CABECERA_FACTURA_NUMERO = v -> {
		if (v.getCabeceraFactura() == null) return;

		String numero = v.getCabeceraFactura().getNumFactura();
		if (AonStringUtils.isBlank(numero)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.length(numero) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 10. IDFactura >> CabeceraFactura >> FechaExpedicionFactura
	 *
	 * 	- Campo obligatorio, necesario para el calculo del identificador TicketBAI,
	 * 	  con formato dd-mm-yyyy.
	 * 	- Debera ser una fecha correcta y no puede ser posterior a la fecha actual.
	 */
	private static final Consumer<AnulacionContext> CABECERA_FACTURA_FECHA_EXPEDICION = v -> {
		if (v.getCabeceraFactura() == null) return;

		String fecha = v.getCabeceraFactura().getFechaExpedicionFactura();
		if (AonStringUtils.isBlank(fecha)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
			return;
		}
		if (!PATRON_FECHA.matcher(fecha).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
			return;
		}

		Date expDate = AonDateUtils.parse(fecha, FORMATO_FECHA);
		if (expDate == null || AonStringUtils.notEquals(fecha, AonDateUtils.format(expDate, FORMATO_FECHA))) {
			v.addError(InvoiceCommunicationError.TBAI_004);
			return;
		}
		if (AonDateUtils.isAfter(expDate, AonDateUtils.today())) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		}
	};

	/**
	 * 11. HuellaTBAI
	 *
	 * 	- El bloque HuellaTBAI es obligatorio.
	 */
	private static final Consumer<AnulacionContext> HUELLA = v -> {
		if (v.getHuella() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 12. HuellaTBAI >> NumSerieDispositivo
	 *
	 * 	- Campo opcional que no puede exceder los 30 caracteres.
	 */
	private static final Consumer<AnulacionContext> HUELLA_NUM_SERIE_DISPOSITIVO = v -> {
		if (v.getHuella() == null) return;

		String numSerie = v.getHuella().getNumSerieDispositivo();
		if (AonStringUtils.isNotBlank(numSerie) && AonStringUtils.length(numSerie) > MAX_TEXTO_30) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 13. HuellaTBAI >> Software
	 *
	 * 	- El bloque Software es obligatorio.
	 */
	private static final Consumer<AnulacionContext> SOFTWARE = v -> {
		if (v.getHuella() == null) return;

		if (v.getSoftware() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 14. HuellaTBAI >> Software >> LicenciaTBAI
	 *
	 * 	- Campo obligatorio: se comprueba que la licencia TicketBAI exista.
	 * 	- No puede exceder los 20 caracteres.
	 */
	private static final Consumer<AnulacionContext> SOFTWARE_LICENCIA_TBAI = v -> {
		if (v.getSoftware() == null) return;

		String licencia = v.getSoftware().getLicenciaTBAI();
		if (AonStringUtils.isBlank(licencia)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.length(licencia) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 15. HuellaTBAI >> Software >> EntidadDesarrolladora
	 *
	 * 	- El bloque EntidadDesarrolladora es obligatorio y debe tener contenido:
	 * 	  se informa el NIF o, alternativamente, el bloque IDOtro.
	 */
	private static final Consumer<AnulacionContext> SOFTWARE_ENTIDAD_DESARROLLADORA = v -> {
		if (v.getSoftware() == null) return;

		EntidadDesarrolladoraType entidad = v.getEntidadDesarrolladora();
		if (entidad == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
			return;
		}
		if (AonStringUtils.isBlank(entidad.getNIF()) && entidad.getIDOtro() == null) {
			v.addError(InvoiceCommunicationError.TBAI_016);
		} else if (AonStringUtils.isNotBlank(entidad.getNIF()) && entidad.getIDOtro() != null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 16. HuellaTBAI >> Software >> EntidadDesarrolladora >> NIF
	 *
	 * 	- Si se informa el NIF de la entidad desarrolladora debe ser una secuencia
	 * 	  de nueve digitos o letras con el formato del tipo NIFType y corresponder
	 * 	  con el NIF registrado en la lista de software TicketBAI.
	 */
	private static final Consumer<AnulacionContext> SOFTWARE_ENTIDAD_DESARROLLADORA_NIF = v -> {
		EntidadDesarrolladoraType entidad = v.getEntidadDesarrolladora();
		if (entidad == null || AonStringUtils.isBlank(entidad.getNIF())) return;

		String nif = entidad.getNIF();
		if (!PATRON_NIF.matcher(nif).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		} else if (!AonDocumentUtil.isValid(nif)) {
			v.addError(InvoiceCommunicationError.TBAI_011);
		}
	};

	/**
	 * 17. HuellaTBAI >> Software >> EntidadDesarrolladora >> IDOtro
	 *
	 * 	- El campo IDType es obligatorio y debe ser uno de los tipos de
	 * 	  identificacion admitidos (02, 03, 04, 05 o 06).
	 * 	- El campo ID es obligatorio y no puede exceder los 20 caracteres.
	 */
	private static final Consumer<AnulacionContext> SOFTWARE_ENTIDAD_DESARROLLADORA_ID_OTRO = v -> {
		IDOtro idOtro = v.getIDOtro();
		if (idOtro == null) return;

		if (AonStringUtils.isBlank(idOtro.getIDType())) {
			v.addError(InvoiceCommunicationError.TBAI_016);
		} else if (!TIPOS_ID_OTRO.contains(idOtro.getIDType())) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}

		if (AonStringUtils.isBlank(idOtro.getID())) {
			v.addError(InvoiceCommunicationError.TBAI_016);
		} else if (AonStringUtils.length(idOtro.getID()) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 18. HuellaTBAI >> Software >> Nombre
	 *
	 * 	- Campo obligatorio que no puede exceder los 120 caracteres.
	 * 	- Debe coincidir con el nombre registrado en la lista de software TicketBAI.
	 */
	private static final Consumer<AnulacionContext> SOFTWARE_NOMBRE = v -> {
		if (v.getSoftware() == null) return;

		String nombre = v.getSoftware().getNombre();
		if (AonStringUtils.isBlank(nombre)) {
			v.addError(InvoiceCommunicationError.TBAI_011);
		} else if (AonStringUtils.length(nombre) > MAX_TEXTO_120) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 19. HuellaTBAI >> Software >> Version
	 *
	 * 	- Campo obligatorio que no puede exceder los 20 caracteres.
	 */
	private static final Consumer<AnulacionContext> SOFTWARE_VERSION = v -> {
		if (v.getSoftware() == null) return;

		String version = v.getSoftware().getVersion();
		if (AonStringUtils.isBlank(version)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.length(version) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	// *********************************************************
	// ************* [VALIDACION ALTA TICKETBAI] ***************
	// *********************************************************

	/**
	 * Datos del fichero de alta que necesitan las validaciones.
	 *
	 * Los accesos a los bloques anidados devuelven null (o una lista vacia) cuando
	 * falta alguno de sus padres, de modo que cada validacion solo tiene que
	 * comprobar el bloque que le corresponde.
	 */
	private static class EmisionContext {
		final TicketBai emision;
		final List<InvoiceCommunicationError> errors = new LinkedList<>();

		EmisionContext(TicketBai emision) {
			this.emision = emision;
		}

		// ***** Cabecera y sujetos

		ticketbai.emision.Cabecera getCabecera() {
			return emision.getCabecera();
		}

		Sujetos getSujetos() {
			return emision.getSujetos();
		}

		ticketbai.emision.Emisor getEmisor() {
			return getSujetos() == null ? null : getSujetos().getEmisor();
		}

		Destinatarios getDestinatarios() {
			return getSujetos() == null ? null : getSujetos().getDestinatarios();
		}

		List<IDDestinatario> getIDDestinatarios() {
			return getDestinatarios() == null
				? List.of()
				: getDestinatarios().getIDDestinatario().stream().filter(Objects::nonNull).toList();
		}

		boolean hayDestinatarioNacional() {
			return getIDDestinatarios().stream().anyMatch(d -> AonStringUtils.isNotBlank(d.getNIF()));
		}

		boolean hayDestinatarioExtranjero() {
			return getIDDestinatarios().stream().anyMatch(d -> d.getIDOtro() != null);
		}

		/**
		 * Los destinatarios de Irlanda del Norte se informan con el codigo de pais de
		 * Reino Unido y el identificador con el prefijo XI, porque el esquema no admite
		 * XI como codigo de pais.
		 */
		boolean hayDestinatarioIrlandaDelNorte() {
			return getIDDestinatarios().stream()
				.map(IDDestinatario::getIDOtro)
				.filter(Objects::nonNull)
				.anyMatch(id -> AonStringUtils.trimToEmpty(id.getID()).toUpperCase()
					.startsWith(PREFIJO_IRLANDA_NORTE));
		}

		// ***** Factura

		Factura getFactura() {
			return emision.getFactura();
		}

		ticketbai.emision.CabeceraFacturaType getCabeceraFactura() {
			return getFactura() == null ? null : getFactura().getCabeceraFactura();
		}

		FacturaRectificativaType getFacturaRectificativa() {
			return getCabeceraFactura() == null ? null : getCabeceraFactura().getFacturaRectificativa();
		}

		ImporteRectificacionSustitutivaType getImporteRectificacion() {
			return getFacturaRectificativa() == null
				? null
				: getFacturaRectificativa().getImporteRectificacionSustitutiva();
		}

		FacturasRectificadasSustituidasType getFacturasRectificadasSustituidas() {
			return getCabeceraFactura() == null ? null : getCabeceraFactura().getFacturasRectificadasSustituidas();
		}

		List<IDFacturaRectificadaSustituidaType> getFacturasRectificadas() {
			return getFacturasRectificadasSustituidas() == null
				? List.of()
				: getFacturasRectificadasSustituidas().getIDFacturaRectificadaSustituida().stream()
					.filter(Objects::nonNull).toList();
		}

		boolean isSimplificada() {
			return getCabeceraFactura() != null && SiNoType.S == getCabeceraFactura().getFacturaSimplificada();
		}

		boolean isSustitutivaDeSimplificada() {
			return getCabeceraFactura() != null
				&& SiNoType.S == getCabeceraFactura().getFacturaEmitidaSustitucionSimplificada();
		}

		boolean isRectificativa() {
			return getFacturaRectificativa() != null;
		}

		boolean isRectificativaPorSustitucion() {
			return isRectificativa() && ClaveTipoRectificativaType.S == getFacturaRectificativa().getTipo();
		}

		// ***** Datos de la factura

		DatosFacturaType getDatosFactura() {
			return getFactura() == null ? null : getFactura().getDatosFactura();
		}

		DetallesFacturaType getDetallesFactura() {
			return getDatosFactura() == null ? null : getDatosFactura().getDetallesFactura();
		}

		List<IDDetalleFacturaType> getDetalles() {
			return getDetallesFactura() == null
				? List.of()
				: getDetallesFactura().getIDDetalleFactura().stream().filter(Objects::nonNull).toList();
		}

		ClavesType getClavesFactura() {
			return getDatosFactura() == null ? null : getDatosFactura().getClaves();
		}

		/** Claves de regimen de IVA informadas, en el orden en el que se declaran. */
		List<String> getClaves() {
			return getClavesFactura() == null
				? List.of()
				: getClavesFactura().getIDClave().stream()
					.filter(Objects::nonNull)
					.map(IDClaveType::getClaveRegimenIvaOpTrascendencia)
					.filter(AonStringUtils::isNotBlank)
					.toList();
		}

		Set<String> getClavesDistintas() {
			return new LinkedHashSet<>(getClaves());
		}

		boolean tieneClave(String clave) {
			return getClaves().contains(clave);
		}

		boolean tieneAlgunaClave(Set<String> claves) {
			return getClaves().stream().anyMatch(claves::contains);
		}

		boolean esUnicaClave(String clave) {
			Set<String> claves = getClavesDistintas();
			return claves.size() == 1 && claves.contains(clave);
		}

		boolean esUnicaClaveDe(Set<String> claves) {
			Set<String> distintas = getClavesDistintas();
			return distintas.size() == 1 && claves.containsAll(distintas);
		}

		boolean hayClaveFueraDe(Set<String> claves) {
			return getClaves().stream().anyMatch(c -> !claves.contains(c));
		}

		// ***** Desglose

		TipoDesgloseType getTipoDesglose() {
			return getFactura() == null ? null : getFactura().getTipoDesglose();
		}

		DesgloseFacturaType getDesgloseFactura() {
			return getTipoDesglose() == null ? null : getTipoDesglose().getDesgloseFactura();
		}

		DesgloseTipoOperacionType getDesgloseTipoOperacion() {
			return getTipoDesglose() == null ? null : getTipoDesglose().getDesgloseTipoOperacion();
		}

		PrestacionServicios getPrestacionServicios() {
			return getDesgloseTipoOperacion() == null ? null : getDesgloseTipoOperacion().getPrestacionServicios();
		}

		Entrega getEntrega() {
			return getDesgloseTipoOperacion() == null ? null : getDesgloseTipoOperacion().getEntrega();
		}

		/** Bloques Sujeta informados en cualquiera de los tipos de desglose. */
		List<SujetaType> getSujetas() {
			List<SujetaType> sujetas = new LinkedList<>();
			if (getDesgloseFactura() != null && getDesgloseFactura().getSujeta() != null) {
				sujetas.add(getDesgloseFactura().getSujeta());
			}
			if (getPrestacionServicios() != null && getPrestacionServicios().getSujeta() != null) {
				sujetas.add(getPrestacionServicios().getSujeta());
			}
			if (getEntrega() != null && getEntrega().getSujeta() != null) {
				sujetas.add(getEntrega().getSujeta());
			}
			return sujetas;
		}

		/** Bloques NoSujeta informados en cualquiera de los tipos de desglose. */
		List<NoSujetaType> getNoSujetas() {
			List<NoSujetaType> noSujetas = new LinkedList<>();
			if (getDesgloseFactura() != null && getDesgloseFactura().getNoSujeta() != null) {
				noSujetas.add(getDesgloseFactura().getNoSujeta());
			}
			if (getPrestacionServicios() != null && getPrestacionServicios().getNoSujeta() != null) {
				noSujetas.add(getPrestacionServicios().getNoSujeta());
			}
			if (getEntrega() != null && getEntrega().getNoSujeta() != null) {
				noSujetas.add(getEntrega().getNoSujeta());
			}
			return noSujetas;
		}

		List<ExentaType> getExentas() {
			return getSujetas().stream().map(SujetaType::getExenta).filter(Objects::nonNull).toList();
		}

		List<NoExentaType> getNoExentas() {
			return getSujetas().stream().map(SujetaType::getNoExenta).filter(Objects::nonNull).toList();
		}

		List<DetalleExentaType> getDetallesExenta() {
			return getExentas().stream()
				.flatMap(e -> e.getDetalleExenta().stream())
				.filter(Objects::nonNull)
				.toList();
		}

		List<DetalleNoExentaType> getDetallesNoExenta() {
			return getNoExentas().stream()
				.flatMap(e -> e.getDetalleNoExenta().stream())
				.filter(Objects::nonNull)
				.toList();
		}

		List<DetalleIVAType> getDetallesIVA() {
			return getDetallesNoExenta().stream()
				.map(DetalleNoExentaType::getDesgloseIVA)
				.filter(Objects::nonNull)
				.flatMap(d -> d.getDetalleIVA().stream())
				.filter(Objects::nonNull)
				.toList();
		}

		List<DetalleNoSujeta> getDetallesNoSujeta() {
			return getNoSujetas().stream()
				.flatMap(n -> n.getDetalleNoSujeta().stream())
				.filter(Objects::nonNull)
				.toList();
		}

		boolean hayDesgloseExento() {
			return !getDetallesExenta().isEmpty();
		}

		boolean hayDesgloseNoExento() {
			return !getDetallesNoExenta().isEmpty();
		}

		boolean hayDesgloseSujeto() {
			return hayDesgloseExento() || hayDesgloseNoExento();
		}

		boolean hayDesgloseNoSujeto() {
			return !getDetallesNoSujeta().isEmpty();
		}

		/** Causas de no sujecion informadas en cualquiera de los tipos de desglose. */
		Set<String> getCausasNoSujecion() {
			return getDetallesNoSujeta().stream()
				.map(DetalleNoSujeta::getCausa)
				.filter(Objects::nonNull)
				.map(CausaNoSujetaType::value)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		}

		/** Causas de exencion informadas en cualquiera de los tipos de desglose. */
		Set<String> getCausasExencion() {
			return getDetallesExenta().stream()
				.map(DetalleExentaType::getCausaExencion)
				.filter(Objects::nonNull)
				.map(CausaExencionType::value)
				.collect(Collectors.toCollection(LinkedHashSet::new));
		}

		boolean hayInversionSujetoPasivo() {
			return getDetallesNoExenta().stream()
				.anyMatch(d -> TipoOperacionSujetaNoExentaType.S_2 == d.getTipoNoExenta());
		}

		boolean haySinInversionSujetoPasivo() {
			return getDetallesNoExenta().stream()
				.anyMatch(d -> TipoOperacionSujetaNoExentaType.S_1 == d.getTipoNoExenta());
		}

		// ***** Huella TicketBAI

		ticketbai.emision.HuellaTBAI getHuella() {
			return emision.getHuellaTBAI();
		}

		EncadenamientoFacturaAnteriorType getEncadenamiento() {
			return getHuella() == null ? null : getHuella().getEncadenamientoFacturaAnterior();
		}

		ticketbai.emision.SoftwareFacturacionType getSoftwareEmision() {
			return getHuella() == null ? null : getHuella().getSoftware();
		}

		ticketbai.emision.EntidadDesarrolladoraType getEntidadDesarrolladoraEmision() {
			return getSoftwareEmision() == null ? null : getSoftwareEmision().getEntidadDesarrolladora();
		}

		ticketbai.emision.IDOtro getIDOtroEmision() {
			return getEntidadDesarrolladoraEmision() == null ? null : getEntidadDesarrolladoraEmision().getIDOtro();
		}

		/**
		 * No se repite el mismo error cuando varios campos incumplen la misma
		 * validacion, porque los errores no identifican el campo al que pertenecen.
		 */
		void addError(InvoiceCommunicationError error) {
			if (!errors.contains(error)) {
				errors.add(error);
			}
		}
	}

	/**
	 * Valida el fichero de alta TicketBAI.
	 *
	 * Se acumulan todos los errores encontrados y, si hay alguno, se lanza una
	 * unica excepcion con todos ellos.
	 *
	 * Las validaciones y los codigos de error se corresponden con el documento
	 * "Listado de validaciones y errores del fichero de ALTA TicketBAI" version
	 * 13.0 de la DFA (documentacion/tbai/Validaciones fichero de Alta
	 * TicketBAI-v13.pdf) y con el esquema ticketBaiV1-2-2.xsd:
	 *
	 * 	- Validaciones de recepcion (001 a 006): provocan el rechazo del fichero.
	 * 	- Validaciones de consolidacion (016, 029 y 010-XXX): el fichero se recibe
	 * 	  con avisos, salvo que el emisor sea contribuyente del SII, en cuyo caso
	 * 	  tambien se rechaza.
	 * 	- Otras validaciones de requisitos TicketBAI (007 a 015 y 998): avisos que
	 * 	  se comunican de forma sincrona o asincrona.
	 *
	 * Solo se comprueba lo que se puede resolver con el contenido del fichero. Las
	 * validaciones que necesitan la informacion del sistema de la DFA (facturas ya
	 * registradas, encadenamiento, censo, licencia de software o certificados) se
	 * reciben en la respuesta al envio (ver el comentario del final del fichero).
	 *
	 * El bloque Signature no se valida porque se incorpora al fichero al firmarlo,
	 * despues de serializar el objeto.
	 *
	 * @param emision fichero de alta a validar
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	public static void validateEmision(TicketBai emision) throws InvoiceCommunicationException {
		if (emision == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.TBAI_002);
		}

		EmisionContext v = new EmisionContext(emision);
		List.<Consumer<EmisionContext>>of(
			ALTA_CABECERA,
			ALTA_CABECERA_ID_VERSION_TBAI,
			ALTA_SUJETOS,
			ALTA_EMISOR,
			ALTA_EMISOR_NIF,
			ALTA_EMISOR_NOMBRE_RAZON_SOCIAL,
			ALTA_DESTINATARIOS,
			ALTA_DESTINATARIOS_NACIONALES_Y_EXTRANJEROS,
			ALTA_DESTINATARIOS_RECTIFICATIVA,
			ALTA_DESTINATARIOS_CLAVE_10,
			ALTA_DESTINATARIOS_CLAVE_14,
			ALTA_DESTINATARIO_IDENTIFICACION,
			ALTA_DESTINATARIO_NOMBRE_RAZON_SOCIAL,
			ALTA_DESTINATARIO_DOMICILIO,
			ALTA_FACTURA,
			ALTA_CABECERA_FACTURA,
			ALTA_CABECERA_FACTURA_SERIE,
			ALTA_CABECERA_FACTURA_NUMERO,
			ALTA_CABECERA_FACTURA_FECHA_EXPEDICION,
			ALTA_CABECERA_FACTURA_HORA_EXPEDICION,
			ALTA_FACTURA_SIMPLIFICADA,
			ALTA_FACTURA_RECTIFICATIVA,
			ALTA_IMPORTE_RECTIFICACION_SUSTITUTIVA,
			ALTA_FACTURAS_RECTIFICADAS_SUSTITUIDAS,
			ALTA_FACTURAS_RECTIFICADAS_SUSTITUIDAS_DATOS,
			ALTA_DATOS_FACTURA,
			ALTA_DATOS_FACTURA_FECHA_OPERACION,
			ALTA_DATOS_FACTURA_DESCRIPCION,
			ALTA_DETALLES_FACTURA,
			ALTA_DETALLES_FACTURA_DATOS,
			ALTA_IMPORTE_TOTAL_FACTURA,
			ALTA_RETENCION_SOPORTADA,
			ALTA_BASE_IMPONIBLE_A_COSTE,
			ALTA_CLAVES,
			ALTA_CLAVES_COMPATIBLES,
			ALTA_CLAVES_RECARGO_Y_PERSONA_JURIDICA,
			ALTA_TIPO_DESGLOSE,
			ALTA_DESGLOSE_SUJETA,
			ALTA_DESGLOSE_REPETIDOS,
			ALTA_DESGLOSE_CLAVE_02,
			ALTA_DESGLOSE_CLAVES_04_Y_08,
			ALTA_DESGLOSE_CLAVE_10,
			ALTA_DESGLOSE_CLAVE_17,
			ALTA_DESGLOSE_CLAVE_19,
			ALTA_DESGLOSE_CLAVE_53,
			ALTA_DESGLOSE_CLAVE_54,
			ALTA_DESGLOSE_NO_SUJETA_CAUSAS,
			ALTA_DESGLOSE_CLAVES_11_12_13,
			ALTA_DESGLOSE_EXENTA,
			ALTA_DESGLOSE_NO_EXENTA,
			ALTA_DETALLE_IVA,
			ALTA_DETALLE_IVA_INVERSION_SUJETO_PASIVO,
			ALTA_DETALLE_IVA_RECARGO_EQUIVALENCIA,
			ALTA_DETALLE_IVA_TIPO_Y_CUOTA,
			ALTA_CUADRE_IMPORTE_TOTAL_Y_DETALLES,
			ALTA_CUADRE_IMPORTE_TOTAL_Y_DESGLOSE,
			ALTA_CUADRE_DETALLES_Y_DESGLOSE,
			ALTA_HUELLA,
			ALTA_HUELLA_ENCADENAMIENTO,
			ALTA_HUELLA_NUM_SERIE_DISPOSITIVO,
			ALTA_SOFTWARE,
			ALTA_SOFTWARE_LICENCIA_TBAI,
			ALTA_SOFTWARE_ENTIDAD_DESARROLLADORA,
			ALTA_SOFTWARE_ENTIDAD_DESARROLLADORA_NIF,
			ALTA_SOFTWARE_ENTIDAD_DESARROLLADORA_ID_OTRO,
			ALTA_SOFTWARE_NOMBRE,
			ALTA_SOFTWARE_VERSION
		).forEach(validacion -> validacion.accept(v));

		if (!v.errors.isEmpty()) {
			throw new InvoiceCommunicationException(v.errors);
		}
	}

	// *********************************************************
	// ******* [UTILIDADES DE LAS VALIDACIONES DE ALTA] ********
	// *********************************************************

	/** Fecha valida con el formato dd-mm-yyyy, o null si no lo es. */
	private static Date fecha(String valor) {
		if (AonStringUtils.isBlank(valor) || !PATRON_FECHA.matcher(valor).matches()) {
			return null;
		}
		Date fecha = AonDateUtils.parse(valor, FORMATO_FECHA);
		return fecha != null && AonStringUtils.equals(valor, AonDateUtils.format(fecha, FORMATO_FECHA))
			? fecha
			: null;
	}

	/** Importe informado con el formato del esquema, o null si no lo es. */
	private static Double importe(String valor) {
		if (AonStringUtils.isBlank(valor)) {
			return null;
		}
		try {
			return Double.valueOf(valor);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static double importeOCero(String valor) {
		Double importe = importe(valor);
		return importe == null ? 0.0 : importe;
	}

	/**
	 * Un importe o un tipo se considera informado cuando tiene valor y no es cero,
	 * porque las validaciones que exigen que un campo no venga informado admiten
	 * que se informe a cero.
	 */
	private static boolean informado(String valor) {
		return AonStringUtils.isNotBlank(valor) && AonMathUtils.isNotZero(importeOCero(valor));
	}

	/** Caracteres que no admiten la serie ni el numero de factura. */
	private static boolean tieneCaracteresProhibidos(String valor) {
		return AonStringUtils.isNotBlank(valor)
			&& valor.chars().anyMatch(c -> CARACTERES_PROHIBIDOS.indexOf(c) >= 0);
	}

	private static boolean distintoSigno(double primero, double segundo) {
		return (primero > 0 && segundo < 0) || (primero < 0 && segundo > 0);
	}

	/** Suma de los importes totales de las lineas de detalle. */
	private static double sumaDetalles(EmisionContext v) {
		return AonMathUtils.round(v.getDetalles().stream()
			.mapToDouble(d -> importeOCero(d.getImporteTotal()))
			.sum());
	}

	/** Suma de las bases, cuotas y recargos de todos los desgloses. */
	private static double sumaDesglose(EmisionContext v) {
		double noExento = v.getDetallesIVA().stream()
			.mapToDouble(d -> importeOCero(d.getBaseImponible()) + importeOCero(d.getCuotaImpuesto())
				+ importeOCero(d.getCuotaRecargoEquivalencia()))
			.sum();
		double exento = v.getDetallesExenta().stream()
			.mapToDouble(d -> importeOCero(d.getBaseImponible()))
			.sum();
		double noSujeto = v.getDetallesNoSujeta().stream()
			.mapToDouble(d -> importeOCero(d.getImporte()))
			.sum();
		return AonMathUtils.round(noExento + exento + noSujeto);
	}

	/**
	 * El emisor es persona fisica cuando su NIF empieza por un digito (DNI) o por
	 * la letra de los NIF de personas fisicas; el resto de letras identifican a
	 * personas juridicas y entidades.
	 */
	private static boolean esPersonaFisica(String nif) {
		if (AonStringUtils.isBlank(nif)) {
			return false;
		}
		char inicial = Character.toUpperCase(nif.charAt(0));
		return Character.isDigit(inicial) || LETRAS_PERSONA_FISICA.indexOf(inicial) >= 0;
	}

	/** Importe de una linea de detalle, con 12 enteros y 8 decimales. */
	private static void validaImporteDetalle(EmisionContext v, String valor, boolean obligatorio) {
		if (AonStringUtils.isBlank(valor)) {
			if (obligatorio) {
				v.addError(InvoiceCommunicationError.TBAI_004);
			}
		} else if (!PATRON_IMPORTE_DETALLE.matcher(valor).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	}

	/** Los bloques repetibles del esquema admiten entre una y un maximo de lineas. */
	private static boolean cardinalidadValida(int lineas, int maximo) {
		return lineas >= 1 && lineas <= maximo;
	}

	private static boolean hayRepetidos(Stream<?> valores) {
		List<?> lista = valores.filter(Objects::nonNull).toList();
		return lista.size() != new HashSet<>(lista).size();
	}

	/** Lineas de desglose de IVA de un bloque Sujeta. */
	private static List<DetalleIVAType> detallesIVA(SujetaType sujeta) {
		if (sujeta == null || sujeta.getNoExenta() == null) {
			return List.of();
		}
		return sujeta.getNoExenta().getDetalleNoExenta().stream()
			.filter(Objects::nonNull)
			.map(DetalleNoExentaType::getDesgloseIVA)
			.filter(Objects::nonNull)
			.flatMap(desglose -> desglose.getDetalleIVA().stream())
			.filter(Objects::nonNull)
			.toList();
	}

	/** Lineas de desglose exento de un bloque Sujeta. */
	private static List<DetalleExentaType> detallesExenta(SujetaType sujeta) {
		if (sujeta == null || sujeta.getExenta() == null) {
			return List.of();
		}
		return sujeta.getExenta().getDetalleExenta().stream().filter(Objects::nonNull).toList();
	}

	/**
	 * Lineas de desglose de IVA de las operaciones sujetas y no exentas con o sin
	 * inversion del sujeto pasivo, porque las validaciones del tipo impositivo y de
	 * la cuota dependen de ello.
	 */
	private static List<DetalleIVAType> detallesIVA(EmisionContext v, TipoOperacionSujetaNoExentaType tipoNoExenta) {
		return v.getDetallesNoExenta().stream()
			.filter(detalle -> tipoNoExenta == detalle.getTipoNoExenta())
			.map(DetalleNoExentaType::getDesgloseIVA)
			.filter(Objects::nonNull)
			.flatMap(desglose -> desglose.getDetalleIVA().stream())
			.filter(Objects::nonNull)
			.toList();
	}

	/**
	 * El destinatario es intracomunitario cuando se identifica con un NIF-IVA de un
	 * pais de la Union Europea o es de Irlanda del Norte, que se informa con el
	 * codigo de pais de Reino Unido y el prefijo XI en el identificador porque el
	 * esquema no admite XI como codigo de pais.
	 */
	private static boolean hayDestinatarioIntracomunitario(EmisionContext v) {
		return v.getIDDestinatarios().stream()
			.map(IDDestinatario::getIDOtro)
			.filter(Objects::nonNull)
			.anyMatch(idOtro -> (idOtro.getCodigoPais() != null
					&& PAISES_UE.contains(idOtro.getCodigoPais().value()))
				|| AonStringUtils.trimToEmpty(idOtro.getID()).toUpperCase().startsWith(PREFIJO_IRLANDA_NORTE));
	}

	/**
	 * Los destinatarios cuyo NIF empieza por R, G, Q, N o W no obligan a informar
	 * el tipo impositivo ni la cuota del impuesto (validaciones 10-265 y 10-266).
	 */
	private static boolean hayDestinatarioSinTipoImpositivo(EmisionContext v) {
		return v.getIDDestinatarios().stream()
			.map(IDDestinatario::getNIF)
			.filter(AonStringUtils::isNotBlank)
			.anyMatch(nif -> LETRAS_SIN_TIPO_IMPOSITIVO.indexOf(Character.toUpperCase(nif.charAt(0))) >= 0);
	}

	// *********************************************************
	// ************* [ALTA: CABECERA Y SUJETOS] ****************
	// *********************************************************

	/**
	 * Cabecera
	 *
	 * 	- El bloque Cabecera es obligatorio.
	 */
	private static final Consumer<EmisionContext> ALTA_CABECERA = v -> {
		if (v.getCabecera() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * Cabecera >> IDVersionTBAI
	 *
	 * 	- Campo obligatorio.
	 * 	- El unico valor admitido por el esquema de alta es "1.2".
	 */
	private static final Consumer<EmisionContext> ALTA_CABECERA_ID_VERSION_TBAI = v -> {
		if (v.getCabecera() == null) return;

		String version = v.getCabecera().getIDVersionTBAI();
		if (AonStringUtils.isBlank(version)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.notEquals(ID_VERSION_TBAI, version)) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * Sujetos
	 *
	 * 	- El bloque Sujetos es obligatorio.
	 */
	private static final Consumer<EmisionContext> ALTA_SUJETOS = v -> {
		if (v.getSujetos() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * Sujetos >> Emisor
	 *
	 * 	- El bloque Emisor es obligatorio.
	 */
	private static final Consumer<EmisionContext> ALTA_EMISOR = v -> {
		if (v.getSujetos() == null) return;

		if (v.getEmisor() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * Sujetos >> Emisor >> NIF (validacion 004)
	 *
	 * 	- Campo obligatorio, necesario para el calculo del identificador TicketBAI.
	 * 	- Secuencia de nueve digitos o letras con el formato del tipo NIFType.
	 * 	- Se comprueba que sea un NIF correcto.
	 */
	private static final Consumer<EmisionContext> ALTA_EMISOR_NIF = v -> {
		if (v.getEmisor() == null) return;

		String nif = v.getEmisor().getNIF();
		if (AonStringUtils.isBlank(nif)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (!PATRON_NIF.matcher(nif).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		} else if (!AonDocumentUtil.isValid(nif)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		}
	};

	/**
	 * Sujetos >> Emisor >> ApellidosNombreRazonSocial
	 *
	 * 	- Campo obligatorio que no puede exceder los 120 caracteres.
	 */
	private static final Consumer<EmisionContext> ALTA_EMISOR_NOMBRE_RAZON_SOCIAL = v -> {
		if (v.getEmisor() == null) return;

		String nombreRazonSocial = v.getEmisor().getApellidosNombreRazonSocial();
		if (AonStringUtils.isBlank(nombreRazonSocial)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.length(nombreRazonSocial) > MAX_TEXTO_120) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * Sujetos >> Destinatarios (validaciones 10-51, 10-52 y 10-53)
	 *
	 * 	- 10-51: si la factura es completa (FacturaSimplificada distinto de S) debe
	 * 	  indicarse al menos un destinatario.
	 * 	- 10-53: si VariosDestinatarios es S debe indicarse al menos uno de ellos.
	 * 	- 10-52: si VariosDestinatarios es N no pueden indicarse varios.
	 * 	- El esquema exige entre 1 y 100 destinatarios cuando se informa el bloque.
	 */
	private static final Consumer<EmisionContext> ALTA_DESTINATARIOS = v -> {
		if (v.getSujetos() == null) return;

		List<IDDestinatario> destinatarios = v.getIDDestinatarios();
		if (destinatarios.isEmpty()) {
			if (v.getDestinatarios() != null) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			if (!v.isSimplificada()) {
				v.addError(InvoiceCommunicationError.TBAI_010_051);
			}
			if (SiNoType.S == v.getSujetos().getVariosDestinatarios()) {
				v.addError(InvoiceCommunicationError.TBAI_010_053);
			}
			return;
		}
		if (destinatarios.size() > MAX_DESTINATARIOS) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
		if (destinatarios.size() > 1 && SiNoType.N == v.getSujetos().getVariosDestinatarios()) {
			v.addError(InvoiceCommunicationError.TBAI_010_052);
		}
	};

	/**
	 * Sujetos >> Destinatarios (validacion 10-57)
	 *
	 * 	- Si se indican varios destinatarios no esta permitido mezclar destinatarios
	 * 	  nacionales y extranjeros.
	 */
	private static final Consumer<EmisionContext> ALTA_DESTINATARIOS_NACIONALES_Y_EXTRANJEROS = v -> {
		if (v.hayDestinatarioNacional() && v.hayDestinatarioExtranjero()) {
			v.addError(InvoiceCommunicationError.TBAI_010_057);
		}
	};

	/**
	 * Sujetos >> Destinatarios (validacion 10-166)
	 *
	 * 	- Si es una factura rectificativa con un codigo distinto de R5 tiene que
	 * 	  haber destinatario.
	 */
	private static final Consumer<EmisionContext> ALTA_DESTINATARIOS_RECTIFICATIVA = v -> {
		if (!v.isRectificativa() || ClaveTipoFacturaType.R_5 == v.getFacturaRectificativa().getCodigo()) return;

		if (v.getIDDestinatarios().isEmpty()) {
			v.addError(InvoiceCommunicationError.TBAI_010_166);
		}
	};

	/**
	 * Sujetos >> Destinatarios (validacion 10-192)
	 *
	 * 	- Si la clave de regimen de IVA es 10 tiene que haber al menos un
	 * 	  destinatario nacional.
	 */
	private static final Consumer<EmisionContext> ALTA_DESTINATARIOS_CLAVE_10 = v -> {
		if (v.tieneClave(CLAVE_10) && !v.hayDestinatarioNacional()) {
			v.addError(InvoiceCommunicationError.TBAI_010_192);
		}
	};

	/**
	 * Sujetos >> Destinatarios (validacion 10-195)
	 *
	 * 	- Si la clave de regimen de IVA es 14 tiene que haber al menos un
	 * 	  destinatario nacional cuyo NIF empiece por P, Q, S o V.
	 */
	private static final Consumer<EmisionContext> ALTA_DESTINATARIOS_CLAVE_14 = v -> {
		if (!v.tieneClave(CLAVE_14)) return;

		boolean hayEntidadPublica = v.getIDDestinatarios().stream()
			.map(IDDestinatario::getNIF)
			.filter(AonStringUtils::isNotBlank)
			.anyMatch(nif -> LETRAS_ENTIDAD_CLAVE_14.indexOf(Character.toUpperCase(nif.charAt(0))) >= 0);
		if (!hayEntidadPublica) {
			v.addError(InvoiceCommunicationError.TBAI_010_195);
		}
	};

	/**
	 * Sujetos >> Destinatarios >> IDDestinatario (validaciones 10-68, 10-54, 10-55
	 * y 10-248)
	 *
	 * 	- El destinatario se identifica con el NIF, si es nacional, o con el bloque
	 * 	  IDOtro, si no lo es.
	 * 	- 10-68: si el NIF del destinatario es nacional se valida que sea correcto.
	 * 	- 10-54: si es un identificador extranjero se comprueba que el pais sea valido.
	 * 	- 10-248: si el destinatario no es nacional se comprueba que se ha informado
	 * 	  la identificacion.
	 * 	- 10-55: si la identificacion es de tipo NIF-IVA se comprueba que es correcta.
	 */
	private static final Consumer<EmisionContext> ALTA_DESTINATARIO_IDENTIFICACION = v -> {
		for (IDDestinatario destinatario : v.getIDDestinatarios()) {
			boolean hayNif = AonStringUtils.isNotBlank(destinatario.getNIF());
			ticketbai.emision.IDOtro idOtro = destinatario.getIDOtro();
			if (hayNif == (idOtro != null)) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			if (hayNif) {
				String nif = destinatario.getNIF();
				if (!PATRON_NIF.matcher(nif).matches()) {
					v.addError(InvoiceCommunicationError.TBAI_002);
				} else if (!AonDocumentUtil.isValid(nif)) {
					v.addError(InvoiceCommunicationError.TBAI_010_068);
				}
			}
			if (idOtro == null) {
				continue;
			}
			if (idOtro.getCodigoPais() == null) {
				v.addError(InvoiceCommunicationError.TBAI_010_054);
			}
			if (AonStringUtils.isBlank(idOtro.getIDType())) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			} else if (!TIPOS_ID_OTRO.contains(idOtro.getIDType())) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			if (AonStringUtils.isBlank(idOtro.getID())) {
				v.addError(InvoiceCommunicationError.TBAI_010_248);
			} else if (AonStringUtils.length(idOtro.getID()) > MAX_TEXTO_20) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			} else if (TIPO_ID_OTRO_NIF_IVA.equals(idOtro.getIDType())
					&& !PATRON_NIF_IVA.matcher(idOtro.getID()).matches()) {
				v.addError(InvoiceCommunicationError.TBAI_010_055);
			}
		}
	};

	/**
	 * Sujetos >> Destinatarios >> IDDestinatario >> ApellidosNombreRazonSocial
	 * (validacion 004)
	 *
	 * 	- Campo obligatorio que no puede exceder los 120 caracteres.
	 */
	private static final Consumer<EmisionContext> ALTA_DESTINATARIO_NOMBRE_RAZON_SOCIAL = v -> {
		for (IDDestinatario destinatario : v.getIDDestinatarios()) {
			String nombreRazonSocial = destinatario.getApellidosNombreRazonSocial();
			if (AonStringUtils.isBlank(nombreRazonSocial)) {
				v.addError(InvoiceCommunicationError.TBAI_004);
			} else if (AonStringUtils.length(nombreRazonSocial) > MAX_TEXTO_120) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
		}
	};

	/**
	 * Sujetos >> Destinatarios >> IDDestinatario (validaciones 016-024 y 016-025)
	 *
	 * 	- Si hay destinatarios es obligatorio indicar el domicilio y el codigo
	 * 	  postal, que son campos opcionales para el esquema.
	 */
	private static final Consumer<EmisionContext> ALTA_DESTINATARIO_DOMICILIO = v -> {
		for (IDDestinatario destinatario : v.getIDDestinatarios()) {
			if (AonStringUtils.isBlank(destinatario.getDireccion())) {
				v.addError(InvoiceCommunicationError.TBAI_016_024);
			} else if (AonStringUtils.length(destinatario.getDireccion()) > MAX_TEXTO_250) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			if (AonStringUtils.isBlank(destinatario.getCodigoPostal())) {
				v.addError(InvoiceCommunicationError.TBAI_016_025);
			} else if (AonStringUtils.length(destinatario.getCodigoPostal()) > MAX_TEXTO_20) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
		}
	};

	// *********************************************************
	// ************** [ALTA: CABECERA DE FACTURA] **************
	// *********************************************************

	/**
	 * Factura
	 *
	 * 	- El bloque Factura es obligatorio.
	 */
	private static final Consumer<EmisionContext> ALTA_FACTURA = v -> {
		if (v.getFactura() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * Factura >> CabeceraFactura
	 *
	 * 	- El bloque CabeceraFactura es obligatorio.
	 */
	private static final Consumer<EmisionContext> ALTA_CABECERA_FACTURA = v -> {
		if (v.getFactura() == null) return;

		if (v.getCabeceraFactura() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * Factura >> CabeceraFactura >> SerieFactura (validaciones 004 y 10-124)
	 *
	 * 	- Campo opcional que no puede exceder los 20 caracteres.
	 * 	- Si se informa se comprueba que no esta en blanco, que no tiene los
	 * 	  caracteres # % & ' + ? ni el de apertura de interrogacion y que no
	 * 	  empieza por DFA_, porque esas series estan reservadas para las facturas
	 * 	  del programa FakturAraba de la DFA.
	 * 	- 10-124: en las facturas rectificativas es obligatorio indicar la serie.
	 */
	private static final Consumer<EmisionContext> ALTA_CABECERA_FACTURA_SERIE = v -> {
		if (v.getCabeceraFactura() == null) return;

		String serie = v.getCabeceraFactura().getSerieFactura();
		if (v.isRectificativa() && AonStringUtils.isBlank(serie)) {
			v.addError(InvoiceCommunicationError.TBAI_010_124);
		}
		if (serie == null) return;

		if (AonStringUtils.isBlank(serie)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
			return;
		}
		if (AonStringUtils.length(serie) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
		if (tieneCaracteresProhibidos(serie)
				|| serie.toUpperCase().startsWith(SERIE_RESERVADA_DFA)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		}
	};

	/**
	 * Factura >> CabeceraFactura >> NumFactura (validacion 004)
	 *
	 * 	- Campo obligatorio, necesario para el calculo del identificador TicketBAI,
	 * 	  que no puede exceder los 20 caracteres.
	 * 	- No se admiten los caracteres # % & ' + ? ni el de apertura de interrogacion.
	 */
	private static final Consumer<EmisionContext> ALTA_CABECERA_FACTURA_NUMERO = v -> {
		if (v.getCabeceraFactura() == null) return;

		String numero = v.getCabeceraFactura().getNumFactura();
		if (AonStringUtils.isBlank(numero)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
			return;
		}
		if (AonStringUtils.length(numero) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
		if (tieneCaracteresProhibidos(numero)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		}
	};

	/**
	 * Factura >> CabeceraFactura >> FechaExpedicionFactura (validaciones 004 y
	 * 10-239)
	 *
	 * 	- Campo obligatorio, necesario para el calculo del identificador TicketBAI,
	 * 	  con formato dd-mm-yyyy.
	 * 	- Debera ser una fecha correcta y no puede ser posterior a la fecha actual.
	 * 	- 10-239: si la clave de regimen de IVA es 17 la fecha de expedicion no
	 * 	  puede ser anterior al 1 de julio de 2021.
	 */
	private static final Consumer<EmisionContext> ALTA_CABECERA_FACTURA_FECHA_EXPEDICION = v -> {
		if (v.getCabeceraFactura() == null) return;

		String valor = v.getCabeceraFactura().getFechaExpedicionFactura();
		if (AonStringUtils.isBlank(valor)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
			return;
		}
		if (!PATRON_FECHA.matcher(valor).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
			return;
		}
		Date expedicion = fecha(valor);
		if (expedicion == null) {
			v.addError(InvoiceCommunicationError.TBAI_004);
			return;
		}
		if (AonDateUtils.isAfter(expedicion, AonDateUtils.today())) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		}
		if (v.tieneClave(CLAVE_17) && AonDateUtils.isBefore(expedicion, FECHA_MINIMA_CLAVE_17)) {
			v.addError(InvoiceCommunicationError.TBAI_010_239);
		}
	};

	/**
	 * Factura >> CabeceraFactura >> HoraExpedicionFactura (validacion 004)
	 *
	 * 	- Campo obligatorio con formato hh:mm:ss, que debe ser una hora correcta.
	 */
	private static final Consumer<EmisionContext> ALTA_CABECERA_FACTURA_HORA_EXPEDICION = v -> {
		if (v.getCabeceraFactura() == null) return;

		String hora = v.getCabeceraFactura().getHoraExpedicionFactura();
		if (AonStringUtils.isBlank(hora)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
			return;
		}
		if (!PATRON_HORA.matcher(hora).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
			return;
		}
		Date valor = AonDateUtils.parse(hora, FORMATO_HORA);
		if (valor == null || AonStringUtils.notEquals(hora, AonDateUtils.format(valor, FORMATO_HORA))) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		}
	};

	/**
	 * Factura >> CabeceraFactura (validaciones 10-159, 10-249 y 10-185)
	 *
	 * 	- 10-159: si FacturaEmitidaSustitucionSimplificada es S la factura tiene que
	 * 	  ser completa (FacturaSimplificada igual a N).
	 * 	- 10-249: una factura no puede ser sustitutiva de una simplificada y
	 * 	  rectificativa a la vez.
	 * 	- 10-185: si la clave de regimen de IVA es 06 la factura no puede ser
	 * 	  simplificada, ni sustitutiva de simplificada, ni rectificativa de codigo R5.
	 */
	private static final Consumer<EmisionContext> ALTA_FACTURA_SIMPLIFICADA = v -> {
		if (v.getCabeceraFactura() == null) return;

		if (v.isSustitutivaDeSimplificada() && v.isSimplificada()) {
			v.addError(InvoiceCommunicationError.TBAI_010_159);
		}
		if (v.isSustitutivaDeSimplificada() && v.isRectificativa()) {
			v.addError(InvoiceCommunicationError.TBAI_010_249);
		}
		boolean rectificativaR5 = v.isRectificativa()
			&& ClaveTipoFacturaType.R_5 == v.getFacturaRectificativa().getCodigo();
		if (v.tieneClave(CLAVE_06)
				&& (v.isSimplificada() || v.isSustitutivaDeSimplificada() || rectificativaR5)) {
			v.addError(InvoiceCommunicationError.TBAI_010_185);
		}
	};

	/**
	 * Factura >> CabeceraFactura >> FacturaRectificativa (validacion 10-244)
	 *
	 * 	- El codigo y el tipo de la factura rectificativa son obligatorios.
	 * 	- 10-244: si la clave de regimen de IVA es 10 la factura no puede ser
	 * 	  rectificativa.
	 */
	private static final Consumer<EmisionContext> ALTA_FACTURA_RECTIFICATIVA = v -> {
		if (!v.isRectificativa()) return;

		FacturaRectificativaType rectificativa = v.getFacturaRectificativa();
		if (rectificativa.getCodigo() == null || rectificativa.getTipo() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
		if (v.tieneClave(CLAVE_10)) {
			v.addError(InvoiceCommunicationError.TBAI_010_244);
		}
	};

	/**
	 * Factura >> CabeceraFactura >> FacturaRectificativa >>
	 * ImporteRectificacionSustitutiva (validaciones 10-125, 10-126, 10-127 y 10-141)
	 *
	 * 	- La base y la cuota rectificadas son obligatorias cuando se informa el
	 * 	  bloque; la cuota de recargo rectificada es opcional.
	 * 	- 10-125, 10-126 y 10-127: los importes rectificados solo pueden indicarse
	 * 	  cuando la factura es rectificativa por sustitucion (Tipo igual a S).
	 * 	- 10-141: no hay que indicarlos cuando la factura es sustitutiva de una
	 * 	  simplificada.
	 */
	private static final Consumer<EmisionContext> ALTA_IMPORTE_RECTIFICACION_SUSTITUTIVA = v -> {
		ImporteRectificacionSustitutivaType importes = v.getImporteRectificacion();
		if (importes == null) return;

		boolean hayBase = AonStringUtils.isNotBlank(importes.getBaseRectificada());
		boolean hayCuota = AonStringUtils.isNotBlank(importes.getCuotaRectificada());
		boolean hayCuotaRecargo = AonStringUtils.isNotBlank(importes.getCuotaRecargoRectificada());

		if (!hayBase || !hayCuota) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
		if ((hayBase && !PATRON_IMPORTE.matcher(importes.getBaseRectificada()).matches())
				|| (hayCuota && !PATRON_IMPORTE.matcher(importes.getCuotaRectificada()).matches())
				|| (hayCuotaRecargo
					&& !PATRON_IMPORTE.matcher(importes.getCuotaRecargoRectificada()).matches())) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
		if (!v.isRectificativaPorSustitucion()) {
			if (hayBase) {
				v.addError(InvoiceCommunicationError.TBAI_010_125);
			}
			if (hayCuota) {
				v.addError(InvoiceCommunicationError.TBAI_010_126);
			}
			if (hayCuotaRecargo) {
				v.addError(InvoiceCommunicationError.TBAI_010_127);
			}
		}
		if (v.isSustitutivaDeSimplificada() && (hayBase || hayCuota || hayCuotaRecargo)) {
			v.addError(InvoiceCommunicationError.TBAI_010_141);
		}
	};

	/**
	 * Factura >> CabeceraFactura >> FacturasRectificadasSustituidas (validaciones
	 * 10-142, 10-235 y 10-172)
	 *
	 * 	- 10-142: cuando se emite factura en sustitucion de una simplificada deben
	 * 	  indicarse las facturas sustituidas.
	 * 	- 10-235: cuando se emite factura rectificativa deben indicarse las facturas
	 * 	  rectificadas.
	 * 	- 10-172: si la factura no es sustitutiva ni rectificativa no puede haber
	 * 	  contenido en el bloque.
	 * 	- El esquema exige entre 1 y 100 facturas cuando se informa el bloque.
	 */
	private static final Consumer<EmisionContext> ALTA_FACTURAS_RECTIFICADAS_SUSTITUIDAS = v -> {
		if (v.getCabeceraFactura() == null) return;

		List<IDFacturaRectificadaSustituidaType> facturas = v.getFacturasRectificadas();
		if (facturas.isEmpty()) {
			if (v.getFacturasRectificadasSustituidas() != null) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			if (v.isSustitutivaDeSimplificada()) {
				v.addError(InvoiceCommunicationError.TBAI_010_142);
			}
			if (v.isRectificativa()) {
				v.addError(InvoiceCommunicationError.TBAI_010_235);
			}
			return;
		}
		if (facturas.size() > MAX_FACTURAS_RECTIFICADAS) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
		if (!v.isSustitutivaDeSimplificada() && !v.isRectificativa()) {
			v.addError(InvoiceCommunicationError.TBAI_010_172);
		}
	};

	/**
	 * Factura >> CabeceraFactura >> FacturasRectificadasSustituidas >>
	 * IDFacturaRectificadaSustituida (validaciones 004, 10-67, 10-163, 10-69 y 10-70)
	 *
	 * 	- 10-67: la serie, si se informa, debe ser diferente de blanco.
	 * 	- 10-163: la serie no puede contener los caracteres # % & ' + ? ni el de
	 * 	  apertura de interrogacion.
	 * 	- 10-69: el numero de factura no puede contener esos mismos caracteres.
	 * 	- 10-70: la fecha de expedicion debe ser valida.
	 */
	private static final Consumer<EmisionContext> ALTA_FACTURAS_RECTIFICADAS_SUSTITUIDAS_DATOS = v -> {
		for (IDFacturaRectificadaSustituidaType factura : v.getFacturasRectificadas()) {
			String serie = factura.getSerieFactura();
			if (serie != null && AonStringUtils.isBlank(serie)) {
				v.addError(InvoiceCommunicationError.TBAI_010_067);
			} else if (AonStringUtils.length(serie) > MAX_TEXTO_20) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			} else if (tieneCaracteresProhibidos(serie)) {
				v.addError(InvoiceCommunicationError.TBAI_010_163);
			}

			String numero = factura.getNumFactura();
			if (AonStringUtils.isBlank(numero)) {
				v.addError(InvoiceCommunicationError.TBAI_004);
			} else if (AonStringUtils.length(numero) > MAX_TEXTO_20) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			} else if (tieneCaracteresProhibidos(numero)) {
				v.addError(InvoiceCommunicationError.TBAI_010_069);
			}

			String valor = factura.getFechaExpedicionFactura();
			if (AonStringUtils.isBlank(valor)) {
				v.addError(InvoiceCommunicationError.TBAI_004);
			} else if (!PATRON_FECHA.matcher(valor).matches()) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			} else {
				Date expedicion = fecha(valor);
				if (expedicion == null || AonDateUtils.isAfter(expedicion, AonDateUtils.today())) {
					v.addError(InvoiceCommunicationError.TBAI_010_070);
				}
			}
		}
	};

	// *********************************************************
	// *************** [ALTA: DATOS DE FACTURA] ****************
	// *********************************************************

	/**
	 * Factura >> DatosFactura
	 *
	 * 	- El bloque DatosFactura es obligatorio.
	 */
	private static final Consumer<EmisionContext> ALTA_DATOS_FACTURA = v -> {
		if (v.getFactura() == null) return;

		if (v.getDatosFactura() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * Factura >> DatosFactura >> FechaOperacion (validaciones 004, 10-59, 10-90,
	 * 10-194 y 10-238)
	 *
	 * 	- Campo opcional que, si se informa, debe ser una fecha correcta.
	 * 	- 10-59: la fecha de operacion no puede ser posterior a la fecha actual,
	 * 	  salvo con las claves de regimen de IVA 14 o 15.
	 * 	- 10-90: la fecha de operacion no puede ser posterior a la de expedicion,
	 * 	  salvo con las claves 14 o 15.
	 * 	- 10-194: con la clave 14 la fecha de operacion es obligatoria y debe ser
	 * 	  posterior a la fecha de expedicion.
	 * 	- 10-238: con la clave 17 la fecha de operacion no puede ser anterior al 1
	 * 	  de julio de 2021.
	 *
	 * La comprobacion de que la fecha de operacion no sea "demasiado antigua" que
	 * describe la validacion 10-59 no se hace aqui porque el documento no publica
	 * el limite que aplica la DFA.
	 */
	private static final Consumer<EmisionContext> ALTA_DATOS_FACTURA_FECHA_OPERACION = v -> {
		if (v.getDatosFactura() == null) return;

		String valor = v.getDatosFactura().getFechaOperacion();
		Date expedicion = v.getCabeceraFactura() == null
			? null
			: fecha(v.getCabeceraFactura().getFechaExpedicionFactura());

		if (AonStringUtils.isBlank(valor)) {
			if (v.tieneClave(CLAVE_14)) {
				v.addError(InvoiceCommunicationError.TBAI_010_194);
			}
			return;
		}
		if (!PATRON_FECHA.matcher(valor).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
			return;
		}
		Date operacion = fecha(valor);
		if (operacion == null) {
			v.addError(InvoiceCommunicationError.TBAI_004);
			return;
		}
		if (!v.tieneAlgunaClave(CLAVES_SIN_LIMITE_FECHA_OPERACION)) {
			if (AonDateUtils.isAfter(operacion, AonDateUtils.today())) {
				v.addError(InvoiceCommunicationError.TBAI_010_059);
			}
			if (expedicion != null && AonDateUtils.isAfter(operacion, expedicion)) {
				v.addError(InvoiceCommunicationError.TBAI_010_090);
			}
		}
		if (v.tieneClave(CLAVE_14) && expedicion != null && !AonDateUtils.isAfter(operacion, expedicion)) {
			v.addError(InvoiceCommunicationError.TBAI_010_194);
		}
		if (v.tieneClave(CLAVE_17) && AonDateUtils.isBefore(operacion, FECHA_MINIMA_CLAVE_17)) {
			v.addError(InvoiceCommunicationError.TBAI_010_238);
		}
	};

	/**
	 * Factura >> DatosFactura >> DescripcionFactura
	 *
	 * 	- Campo obligatorio que no puede exceder los 250 caracteres.
	 */
	private static final Consumer<EmisionContext> ALTA_DATOS_FACTURA_DESCRIPCION = v -> {
		if (v.getDatosFactura() == null) return;

		String descripcion = v.getDatosFactura().getDescripcionFactura();
		if (AonStringUtils.isBlank(descripcion)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.length(descripcion) > MAX_TEXTO_250) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * Factura >> DatosFactura >> DetallesFactura (validacion 003)
	 *
	 * 	- Es obligatorio poner las lineas de detalle de la factura.
	 * 	- El esquema admite como maximo 1000 lineas.
	 */
	private static final Consumer<EmisionContext> ALTA_DETALLES_FACTURA = v -> {
		if (v.getDatosFactura() == null) return;

		List<IDDetalleFacturaType> detalles = v.getDetalles();
		if (detalles.isEmpty()) {
			v.addError(InvoiceCommunicationError.TBAI_003);
		} else if (detalles.size() > MAX_DETALLES_FACTURA) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * Factura >> DatosFactura >> DetallesFactura >> IDDetalleFactura
	 *
	 * 	- La descripcion del detalle es obligatoria y no puede exceder los 250
	 * 	  caracteres.
	 * 	- La cantidad, el importe unitario y el importe total son obligatorios, con
	 * 	  el formato de importe de 12 enteros y 8 decimales; el descuento es opcional.
	 */
	private static final Consumer<EmisionContext> ALTA_DETALLES_FACTURA_DATOS = v -> {
		for (IDDetalleFacturaType detalle : v.getDetalles()) {
			String descripcion = detalle.getDescripcionDetalle();
			if (AonStringUtils.isBlank(descripcion)) {
				v.addError(InvoiceCommunicationError.TBAI_004);
			} else if (AonStringUtils.length(descripcion) > MAX_TEXTO_250) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			validaImporteDetalle(v, detalle.getCantidad(), true);
			validaImporteDetalle(v, detalle.getImporteUnitario(), true);
			validaImporteDetalle(v, detalle.getImporteTotal(), true);
			validaImporteDetalle(v, detalle.getDescuento(), false);
		}
	};

	/**
	 * Factura >> DatosFactura >> ImporteTotalFactura
	 *
	 * 	- Campo obligatorio, necesario para el codigo QR, con el formato de importe
	 * 	  de 12 enteros y 2 decimales.
	 */
	private static final Consumer<EmisionContext> ALTA_IMPORTE_TOTAL_FACTURA = v -> {
		if (v.getDatosFactura() == null) return;

		String total = v.getDatosFactura().getImporteTotalFactura();
		if (AonStringUtils.isBlank(total)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (!PATRON_IMPORTE.matcher(total).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * Factura >> DatosFactura >> RetencionSoportada
	 *
	 * 	- Campo opcional con el formato de importe de 12 enteros y 2 decimales.
	 */
	private static final Consumer<EmisionContext> ALTA_RETENCION_SOPORTADA = v -> {
		if (v.getDatosFactura() == null) return;

		String retencion = v.getDatosFactura().getRetencionSoportada();
		if (AonStringUtils.isNotBlank(retencion) && !PATRON_IMPORTE.matcher(retencion).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * Factura >> DatosFactura >> BaseImponibleACoste (validaciones 10-129 y 10-130)
	 *
	 * 	- 10-129: solo puede informarse cuando alguna de las claves de regimen de
	 * 	  IVA es 06 (regimen especial de grupo de entidades en IVA).
	 * 	- 10-130: es obligatorio informarla cuando alguna de las claves es 06.
	 */
	private static final Consumer<EmisionContext> ALTA_BASE_IMPONIBLE_A_COSTE = v -> {
		if (v.getDatosFactura() == null) return;

		String base = v.getDatosFactura().getBaseImponibleACoste();
		if (AonStringUtils.isNotBlank(base) && !PATRON_IMPORTE.matcher(base).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
		if (AonStringUtils.isNotBlank(base) && !v.tieneClave(CLAVE_06)) {
			v.addError(InvoiceCommunicationError.TBAI_010_129);
		}
		if (AonStringUtils.isBlank(base) && v.tieneClave(CLAVE_06)) {
			v.addError(InvoiceCommunicationError.TBAI_010_130);
		}
	};

	/**
	 * Factura >> DatosFactura >> Claves
	 *
	 * 	- El bloque Claves es obligatorio y admite entre 1 y 3 claves de regimen de
	 * 	  IVA y operaciones con trascendencia tributaria.
	 * 	- Cada clave debe ser uno de los valores admitidos por el esquema.
	 */
	private static final Consumer<EmisionContext> ALTA_CLAVES = v -> {
		if (v.getDatosFactura() == null) return;

		if (v.getClavesFactura() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
			return;
		}
		List<IDClaveType> idClaves = v.getClavesFactura().getIDClave().stream()
			.filter(Objects::nonNull)
			.toList();
		if (idClaves.isEmpty() || idClaves.size() > MAX_CLAVES) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
		for (IDClaveType idClave : idClaves) {
			String clave = idClave.getClaveRegimenIvaOpTrascendencia();
			if (AonStringUtils.isBlank(clave)) {
				v.addError(InvoiceCommunicationError.TBAI_004);
			} else if (!CLAVES_VALIDAS.contains(clave)) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
		}
	};

	/**
	 * Factura >> DatosFactura >> Claves (validacion 10-131)
	 *
	 * 	- Se comprueba que las claves de regimen de IVA indicadas sean compatibles
	 * 	  entre si, segun la tabla de compatibilidades del documento de validaciones
	 * 	  (ver CLAVES_COMPATIBLES).
	 * 	- La clave 53 es incompatible con el resto y debe ir sola.
	 */
	private static final Consumer<EmisionContext> ALTA_CLAVES_COMPATIBLES = v -> {
		List<String> claves = v.getClaves();
		if (claves.size() < 2) return;

		if (claves.contains(CLAVE_53)) {
			v.addError(InvoiceCommunicationError.TBAI_010_131);
			return;
		}
		String primera = claves.get(0);
		Set<String> compatibles = CLAVES_COMPATIBLES.getOrDefault(primera, Set.of());
		for (String clave : claves.subList(1, claves.size())) {
			if (!clave.equals(primera) && !compatibles.contains(clave)) {
				v.addError(InvoiceCommunicationError.TBAI_010_131);
			}
		}
	};

	/**
	 * Factura >> DatosFactura >> Claves (validacion 10-132)
	 *
	 * 	- Las claves 51 y 52 solo pueden utilizarse cuando el emisor es una persona
	 * 	  fisica o una persona juridica cuyo NIF empieza por E o por J.
	 */
	private static final Consumer<EmisionContext> ALTA_CLAVES_RECARGO_Y_PERSONA_JURIDICA = v -> {
		if (!v.tieneAlgunaClave(CLAVES_RECARGO_EQUIVALENCIA)) return;

		String nif = v.getEmisor() == null ? null : v.getEmisor().getNIF();
		if (AonStringUtils.isBlank(nif) || esPersonaFisica(nif)) return;

		if (LETRAS_ENTIDAD_CLAVES_51_52.indexOf(Character.toUpperCase(nif.charAt(0))) < 0) {
			v.addError(InvoiceCommunicationError.TBAI_010_132);
		}
	};

	// *********************************************************
	// ****************** [ALTA: DESGLOSES] ********************
	// *********************************************************

	/**
	 * Factura >> TipoDesglose (validaciones 004, 10-61, 10-62, 10-63, 10-64, 10-65
	 * y 10-107)
	 *
	 * 	- El bloque TipoDesglose es obligatorio y el esquema solo admite uno de los
	 * 	  dos tipos: a nivel de factura o a nivel de operacion.
	 * 	- 004: la factura debe contener algun dato en el desglose.
	 * 	- 10-63: si el desglose es a nivel de factura debe existir Sujeta o NoSujeta.
	 * 	- 10-62: si el desglose es a nivel de operacion debe haber informacion en
	 * 	  PrestacionServicios, en Entrega o en ambos.
	 * 	- 10-64 y 10-65: cada uno de esos bloques debe tener Sujeta o NoSujeta.
	 * 	- 10-61: cuando el NIF del destinatario es extranjero el desglose debe ser a
	 * 	  nivel de operacion.
	 * 	- 10-107: para los destinatarios de Irlanda del Norte el desglose debe ser a
	 * 	  nivel de operacion y tener datos en Entrega.
	 */
	private static final Consumer<EmisionContext> ALTA_TIPO_DESGLOSE = v -> {
		if (v.getFactura() == null) return;

		if (v.getTipoDesglose() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
			return;
		}
		DesgloseFacturaType desgloseFactura = v.getDesgloseFactura();
		DesgloseTipoOperacionType desgloseOperacion = v.getDesgloseTipoOperacion();
		if ((desgloseFactura == null) == (desgloseOperacion == null)) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
		if (!v.hayDesgloseSujeto() && !v.hayDesgloseNoSujeto()) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		}
		if (desgloseFactura != null
				&& desgloseFactura.getSujeta() == null && desgloseFactura.getNoSujeta() == null) {
			v.addError(InvoiceCommunicationError.TBAI_010_063);
		}
		if (desgloseOperacion != null
				&& v.getPrestacionServicios() == null && v.getEntrega() == null) {
			v.addError(InvoiceCommunicationError.TBAI_010_062);
		}
		if (v.getPrestacionServicios() != null && v.getPrestacionServicios().getSujeta() == null
				&& v.getPrestacionServicios().getNoSujeta() == null) {
			v.addError(InvoiceCommunicationError.TBAI_010_064);
		}
		if (v.getEntrega() != null
				&& v.getEntrega().getSujeta() == null && v.getEntrega().getNoSujeta() == null) {
			v.addError(InvoiceCommunicationError.TBAI_010_065);
		}
		if (v.hayDestinatarioExtranjero() && desgloseOperacion == null) {
			v.addError(InvoiceCommunicationError.TBAI_010_061);
		}
		if (v.hayDestinatarioIrlandaDelNorte() && (v.getEntrega() == null
				|| (v.getEntrega().getSujeta() == null && v.getEntrega().getNoSujeta() == null))) {
			v.addError(InvoiceCommunicationError.TBAI_010_107);
		}
	};

	/**
	 * Factura >> TipoDesglose >> Sujeta (validacion 10-66)
	 *
	 * 	- Si la operacion es sujeta en cualquiera de los bloques de desglose debe
	 * 	  existir el bloque Exenta, el bloque NoExenta o ambos.
	 * 	- El esquema limita el numero de detalles de cada bloque: 7 exentos, 2 no
	 * 	  exentos, 4 no sujetos y 12 lineas de desglose de IVA.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_SUJETA = v -> {
		for (SujetaType sujeta : v.getSujetas()) {
			if (sujeta.getExenta() == null && sujeta.getNoExenta() == null) {
				v.addError(InvoiceCommunicationError.TBAI_010_066);
			}
			if (sujeta.getExenta() != null
					&& !cardinalidadValida(sujeta.getExenta().getDetalleExenta().size(), MAX_DETALLES_EXENTA)) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			if (sujeta.getNoExenta() != null && !cardinalidadValida(
					sujeta.getNoExenta().getDetalleNoExenta().size(), MAX_DETALLES_NO_EXENTA)) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
		}
		for (NoSujetaType noSujeta : v.getNoSujetas()) {
			if (!cardinalidadValida(noSujeta.getDetalleNoSujeta().size(), MAX_DETALLES_NO_SUJETA)) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
		}
		for (DetalleNoExentaType detalle : v.getDetallesNoExenta()) {
			if (detalle.getDesgloseIVA() != null
					&& !cardinalidadValida(detalle.getDesgloseIVA().getDetalleIVA().size(), MAX_DETALLES_IVA)) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
		}
	};

	/**
	 * Factura >> TipoDesglose (validaciones 10-253, 10-255, 10-256 y 10-257)
	 *
	 * 	- 10-253: los valores de Causa de un bloque NoSujeta no pueden repetirse.
	 * 	- 10-255: los valores de CausaExencion de un bloque Exenta no pueden repetirse.
	 * 	- 10-256: los valores de TipoNoExenta de un bloque NoExenta no pueden repetirse.
	 * 	- 10-257: con TipoNoExenta igual a S1 no puede repetirse el trio formado por
	 * 	  el tipo impositivo, el tipo de recargo de equivalencia y la marca de
	 * 	  operacion en recargo de equivalencia o regimen simplificado.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_REPETIDOS = v -> {
		for (NoSujetaType noSujeta : v.getNoSujetas()) {
			if (hayRepetidos(noSujeta.getDetalleNoSujeta().stream()
					.filter(Objects::nonNull)
					.map(DetalleNoSujeta::getCausa))) {
				v.addError(InvoiceCommunicationError.TBAI_010_253);
			}
		}
		for (ExentaType exenta : v.getExentas()) {
			if (hayRepetidos(exenta.getDetalleExenta().stream()
					.filter(Objects::nonNull)
					.map(DetalleExentaType::getCausaExencion))) {
				v.addError(InvoiceCommunicationError.TBAI_010_255);
			}
		}
		for (NoExentaType noExenta : v.getNoExentas()) {
			if (hayRepetidos(noExenta.getDetalleNoExenta().stream()
					.filter(Objects::nonNull)
					.map(DetalleNoExentaType::getTipoNoExenta))) {
				v.addError(InvoiceCommunicationError.TBAI_010_256);
			}
		}
		for (DetalleNoExentaType detalle : v.getDetallesNoExenta()) {
			if (TipoOperacionSujetaNoExentaType.S_1 != detalle.getTipoNoExenta()
					|| detalle.getDesgloseIVA() == null) {
				continue;
			}
			if (hayRepetidos(detalle.getDesgloseIVA().getDetalleIVA().stream()
					.filter(Objects::nonNull)
					.map(d -> AonStringUtils.trimToEmpty(d.getTipoImpositivo()) + "|"
						+ AonStringUtils.trimToEmpty(d.getTipoRecargoEquivalencia()) + "|"
						+ (d.getOperacionEnRecargoDeEquivalenciaORegimenSimplificado() == null
							? "" : d.getOperacionEnRecargoDeEquivalenciaORegimenSimplificado().value())))) {
				v.addError(InvoiceCommunicationError.TBAI_010_257);
			}
		}
	};

	/**
	 * Factura >> TipoDesglose (validaciones 10-180, 10-181, 10-85, 10-86, 10-87 y
	 * 10-88)
	 *
	 * 	- 10-180: si la unica clave de regimen de IVA es 02 (exportacion) no puede
	 * 	  haber un desglose diferente de sujeto y exento.
	 * 	- 10-181: si alguna de las claves es 02 debe haber un desglose sujeto y exento.
	 * 	- 10-85 a 10-88: con la clave 02 no debe informarse el tipo impositivo, la
	 * 	  cuota del impuesto, el tipo de recargo ni la cuota de recargo.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_CLAVE_02 = v -> {
		if (!v.tieneClave(CLAVE_02)) return;

		if (v.esUnicaClave(CLAVE_02) && (v.hayDesgloseNoExento() || v.hayDesgloseNoSujeto())) {
			v.addError(InvoiceCommunicationError.TBAI_010_180);
		}
		if (!v.hayDesgloseExento()) {
			v.addError(InvoiceCommunicationError.TBAI_010_181);
		}
		for (DetalleIVAType detalle : v.getDetallesIVA()) {
			if (informado(detalle.getTipoImpositivo())) {
				v.addError(InvoiceCommunicationError.TBAI_010_085);
			}
			if (informado(detalle.getCuotaImpuesto())) {
				v.addError(InvoiceCommunicationError.TBAI_010_086);
			}
			if (informado(detalle.getTipoRecargoEquivalencia())) {
				v.addError(InvoiceCommunicationError.TBAI_010_087);
			}
			if (informado(detalle.getCuotaRecargoEquivalencia())) {
				v.addError(InvoiceCommunicationError.TBAI_010_088);
			}
		}
	};

	/**
	 * Factura >> TipoDesglose (validaciones 10-184, 10-304, 10-189 y 10-190)
	 *
	 * 	- 10-184: con la clave de regimen de IVA 04 no puede haber informacion en el
	 * 	  desglose sujeto y no exento sin inversion del sujeto pasivo (TipoNoExenta
	 * 	  igual a S1).
	 * 	- 10-304: lo mismo con la clave 08.
	 * 	- 10-189: si alguna de las claves es 08 debe haber un desglose no sujeto con
	 * 	  causa RL o IE.
	 * 	- 10-190: si la unica clave es 08 solo puede haber desglose no sujeto con
	 * 	  causa RL, IE o VT.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_CLAVES_04_Y_08 = v -> {
		if (v.tieneClave(CLAVE_04) && v.haySinInversionSujetoPasivo()) {
			v.addError(InvoiceCommunicationError.TBAI_010_184);
		}
		if (!v.tieneClave(CLAVE_08)) return;

		if (v.haySinInversionSujetoPasivo()) {
			v.addError(InvoiceCommunicationError.TBAI_010_304);
		}
		if (v.getCausasNoSujecion().stream().noneMatch(CAUSAS_NO_SUJECION_CLAVE_08::contains)) {
			v.addError(InvoiceCommunicationError.TBAI_010_189);
		}
		if (v.esUnicaClave(CLAVE_08) && (v.hayDesgloseSujeto()
				|| !CAUSAS_NO_SUJECION_CLAVE_08_UNICA.containsAll(v.getCausasNoSujecion()))) {
			v.addError(InvoiceCommunicationError.TBAI_010_190);
		}
	};

	/**
	 * Factura >> TipoDesglose (validaciones 10-245 y 10-191)
	 *
	 * 	- 10-245: si la unica clave de regimen de IVA es 10 no puede haber
	 * 	  informacion en el desglose sujeto.
	 * 	- 10-191: si la unica clave es 10 solo puede haber desglose no sujeto con
	 * 	  causa OT y con importe distinto de cero.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_CLAVE_10 = v -> {
		if (!v.esUnicaClave(CLAVE_10)) return;

		if (v.hayDesgloseSujeto()) {
			v.addError(InvoiceCommunicationError.TBAI_010_245);
		}
		boolean noSujetaCorrecta = v.hayDesgloseNoSujeto() && v.getDetallesNoSujeta().stream()
			.allMatch(d -> CausaNoSujetaType.OT == d.getCausa() && informado(d.getImporte()));
		if (!noSujetaCorrecta) {
			v.addError(InvoiceCommunicationError.TBAI_010_191);
		}
	};

	/**
	 * Factura >> TipoDesglose (validacion 10-240)
	 *
	 * 	- Si la unica clave de regimen de IVA es 17 no puede haber informacion en el
	 * 	  desglose sujeto y exento ni en el no sujeto con causa distinta de RL.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_CLAVE_17 = v -> {
		if (!v.esUnicaClave(CLAVE_17)) return;

		if (v.hayDesgloseExento()
				|| v.getCausasNoSujecion().stream().anyMatch(c -> !CAUSA_NO_SUJECION_RL.equals(c))) {
			v.addError(InvoiceCommunicationError.TBAI_010_240);
		}
	};

	/**
	 * Factura >> TipoDesglose (validacion 10-241)
	 *
	 * 	- Si alguna de las claves de regimen de IVA es 19 debe haber al menos una
	 * 	  linea de desglose no sujeta con causa OT.
	 * 	- Si la unica clave es 19 solo puede haber desglose no sujeto con causa OT
	 * 	  o VT.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_CLAVE_19 = v -> {
		if (!v.tieneClave(CLAVE_19)) return;

		if (!v.getCausasNoSujecion().contains(CAUSA_NO_SUJECION_OT)) {
			v.addError(InvoiceCommunicationError.TBAI_010_241);
		}
		if (v.esUnicaClave(CLAVE_19) && (v.hayDesgloseSujeto()
				|| !CAUSAS_NO_SUJECION_CLAVE_19.containsAll(v.getCausasNoSujecion()))) {
			v.addError(InvoiceCommunicationError.TBAI_010_241);
		}
	};

	/**
	 * Factura >> TipoDesglose (validacion 10-133)
	 *
	 * 	- Si la clave de regimen de IVA es 53 el desglose solo podra ser no sujeto y
	 * 	  con causa de no sujecion OT.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_CLAVE_53 = v -> {
		if (!v.tieneClave(CLAVE_53)) return;

		if (v.hayDesgloseSujeto() || !v.hayDesgloseNoSujeto()
				|| v.getCausasNoSujecion().stream().anyMatch(c -> !CAUSA_NO_SUJECION_OT.equals(c))) {
			v.addError(InvoiceCommunicationError.TBAI_010_133);
		}
	};

	/**
	 * Factura >> TipoDesglose (validaciones 10-276 y 10-277)
	 *
	 * 	- 10-277: con la clave de regimen de IVA 54 no puede haber informacion en
	 * 	  los desgloses sujetos.
	 * 	- 10-276: si la unica clave es 54 no puede haber desglose no sujeto con
	 * 	  causa OT o RL.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_CLAVE_54 = v -> {
		if (!v.tieneClave(CLAVE_54)) return;

		if (v.hayDesgloseSujeto()) {
			v.addError(InvoiceCommunicationError.TBAI_010_277);
		}
		if (v.esUnicaClave(CLAVE_54)
				&& v.getCausasNoSujecion().stream().anyMatch(CAUSAS_NO_SUJECION_CLAVE_54::contains)) {
			v.addError(InvoiceCommunicationError.TBAI_010_276);
		}
	};

	/**
	 * Factura >> TipoDesglose >> NoSujeta (validaciones 10-275, 10-279 y 10-278)
	 *
	 * 	- 10-275: con las claves de regimen de IVA 02, 03, 04 o 07 el desglose no
	 * 	  sujeto solo puede tener causa de no sujecion VT.
	 * 	- 10-279: con las claves 05 o 09 el desglose no sujeto no puede tener causa
	 * 	  de no sujecion IE.
	 * 	- 10-278: si ninguna de las claves es 01, 51 o 52 no puede haber unicamente
	 * 	  desglose no sujeto con causa VT.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_NO_SUJETA_CAUSAS = v -> {
		if (!v.hayDesgloseNoSujeto()) return;

		Set<String> causas = v.getCausasNoSujecion();
		if (v.tieneAlgunaClave(CLAVES_CAUSA_NO_SUJECION_SOLO_VT)
				&& causas.stream().anyMatch(c -> !CAUSA_NO_SUJECION_VT.equals(c))) {
			v.addError(InvoiceCommunicationError.TBAI_010_275);
		}
		if (v.tieneAlgunaClave(CLAVES_CAUSA_NO_SUJECION_SIN_IE) && causas.contains(CAUSA_NO_SUJECION_IE)) {
			v.addError(InvoiceCommunicationError.TBAI_010_279);
		}
		if (!v.tieneAlgunaClave(CLAVES_ADMITEN_SOLO_NO_SUJECION_VT) && !v.hayDesgloseSujeto()
				&& causas.equals(Set.of(CAUSA_NO_SUJECION_VT))) {
			v.addError(InvoiceCommunicationError.TBAI_010_278);
		}
	};

	/**
	 * Factura >> TipoDesglose (validacion 10-193)
	 *
	 * 	- Si la unica clave de regimen de IVA es 11, 12 o 13 el tipo impositivo del
	 * 	  desglose sujeto y no exento debe ser el 21%.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_CLAVES_11_12_13 = v -> {
		if (!v.esUnicaClaveDe(CLAVES_TIPO_IMPOSITIVO_GENERAL)) return;

		for (DetalleIVAType detalle : detallesIVA(v, TipoOperacionSujetaNoExentaType.S_1)) {
			Double tipo = importe(detalle.getTipoImpositivo());
			if (tipo == null || AonMathUtils.notEquals(tipo, TIPO_IMPOSITIVO_GENERAL)) {
				v.addError(InvoiceCommunicationError.TBAI_010_193);
			}
		}
	};

	/**
	 * Factura >> TipoDesglose >> Sujeta >> Exenta (validaciones 10-168, 10-173,
	 * 10-174 y 10-188)
	 *
	 * 	- La causa de exencion y la base imponible son obligatorias en cada linea.
	 * 	- 10-168: si la causa de exencion es E5 el destinatario tiene que ser
	 * 	  intracomunitario.
	 * 	- 10-173: si el desglose es por prestacion de servicios la causa de exencion
	 * 	  no puede ser E5.
	 * 	- 10-174: si la unica clave de regimen de IVA es 01 la causa de exencion no
	 * 	  puede ser E2 ni E3.
	 * 	- 10-188: si la unica clave es 07 la causa de exencion no puede ser E2, E3,
	 * 	  E4 ni E5.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_EXENTA = v -> {
		for (DetalleExentaType detalle : v.getDetallesExenta()) {
			if (detalle.getCausaExencion() == null) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			if (AonStringUtils.isBlank(detalle.getBaseImponible())) {
				v.addError(InvoiceCommunicationError.TBAI_004);
			} else if (!PATRON_IMPORTE.matcher(detalle.getBaseImponible()).matches()) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
		}
		Set<String> causas = v.getCausasExencion();
		if (causas.isEmpty()) return;

		if (causas.contains(CAUSA_EXENCION_E5) && !hayDestinatarioIntracomunitario(v)) {
			v.addError(InvoiceCommunicationError.TBAI_010_168);
		}
		boolean e5EnPrestacionServicios = v.getPrestacionServicios() != null
			&& detallesExenta(v.getPrestacionServicios().getSujeta()).stream()
				.anyMatch(d -> CausaExencionType.E_5 == d.getCausaExencion());
		if (e5EnPrestacionServicios) {
			v.addError(InvoiceCommunicationError.TBAI_010_173);
		}
		if (v.esUnicaClave(CLAVE_01) && causas.stream().anyMatch(CAUSAS_EXENCION_CLAVE_01::contains)) {
			v.addError(InvoiceCommunicationError.TBAI_010_174);
		}
		if (v.esUnicaClave(CLAVE_07) && causas.stream().anyMatch(CAUSAS_EXENCION_CLAVE_07::contains)) {
			v.addError(InvoiceCommunicationError.TBAI_010_188);
		}
	};

	/**
	 * Factura >> TipoDesglose >> Sujeta >> NoExenta (validaciones 10-82 y 10-83)
	 *
	 * 	- El tipo de no exencion y el bloque DesgloseIVA son obligatorios en cada
	 * 	  linea.
	 * 	- 10-82: TipoNoExenta igual a S2 (inversion del sujeto pasivo) solo puede
	 * 	  utilizarse con las claves de regimen de IVA 01, 04, 05, 06 y 12.
	 * 	- 10-83: con inversion del sujeto pasivo la factura no puede ser simplificada.
	 */
	private static final Consumer<EmisionContext> ALTA_DESGLOSE_NO_EXENTA = v -> {
		for (DetalleNoExentaType detalle : v.getDetallesNoExenta()) {
			if (detalle.getTipoNoExenta() == null || detalle.getDesgloseIVA() == null) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
		}
		if (!v.hayInversionSujetoPasivo()) return;

		if (v.hayClaveFueraDe(CLAVES_INVERSION_SUJETO_PASIVO)) {
			v.addError(InvoiceCommunicationError.TBAI_010_082);
		}
		if (v.isSimplificada()) {
			v.addError(InvoiceCommunicationError.TBAI_010_083);
		}
	};

	/**
	 * Factura >> TipoDesglose >> Sujeta >> NoExenta >> DesgloseIVA >> DetalleIVA
	 * (validaciones 10-177 y 10-74)
	 *
	 * 	- La base imponible es obligatoria y el resto de campos, si se informan,
	 * 	  deben cumplir el formato del esquema.
	 * 	- 10-177: la cuota del impuesto y la base imponible tienen que tener el
	 * 	  mismo signo.
	 * 	- 10-74: se comprueba que la cuota del impuesto este bien calculada a partir
	 * 	  de la base imponible y del tipo impositivo. Que falte la cuota lo avisa la
	 * 	  validacion 10-266. Con la clave de regimen de IVA
	 * 	  06 la cuota se calcula a partir de la base imponible a coste, que no se
	 * 	  puede repartir entre las lineas del desglose, por lo que no se comprueba
	 * 	  (validacion 10-179).
	 * 	- 10-178: con la unica clave 06 la cuota del impuesto y la base imponible a
	 * 	  coste deben tener el mismo signo.
	 */
	private static final Consumer<EmisionContext> ALTA_DETALLE_IVA = v -> {
		for (DetalleIVAType detalle : v.getDetallesIVA()) {
			if (AonStringUtils.isBlank(detalle.getBaseImponible())) {
				v.addError(InvoiceCommunicationError.TBAI_004);
			} else if (!PATRON_IMPORTE.matcher(detalle.getBaseImponible()).matches()) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			if (AonStringUtils.isNotBlank(detalle.getCuotaImpuesto())
					&& !PATRON_IMPORTE.matcher(detalle.getCuotaImpuesto()).matches()) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			if (AonStringUtils.isNotBlank(detalle.getCuotaRecargoEquivalencia())
					&& !PATRON_IMPORTE.matcher(detalle.getCuotaRecargoEquivalencia()).matches()) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			if (AonStringUtils.isNotBlank(detalle.getTipoImpositivo())
					&& !PATRON_TIPO.matcher(detalle.getTipoImpositivo()).matches()) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			if (AonStringUtils.isNotBlank(detalle.getTipoRecargoEquivalencia())
					&& !PATRON_TIPO.matcher(detalle.getTipoRecargoEquivalencia()).matches()) {
				v.addError(InvoiceCommunicationError.TBAI_002);
			}
			if (distintoSigno(importeOCero(detalle.getBaseImponible()),
					importeOCero(detalle.getCuotaImpuesto()))) {
				v.addError(InvoiceCommunicationError.TBAI_010_177);
			}
		}
		String baseACoste = v.getDatosFactura() == null ? null : v.getDatosFactura().getBaseImponibleACoste();
		for (DetalleIVAType detalle : detallesIVA(v, TipoOperacionSujetaNoExentaType.S_1)) {
			double base = importeOCero(detalle.getBaseImponible());
			double cuota = importeOCero(detalle.getCuotaImpuesto());
			double tipo = importeOCero(detalle.getTipoImpositivo());
			if (!v.tieneClave(CLAVE_06) && informado(detalle.getTipoImpositivo())
					&& AonStringUtils.isNotBlank(detalle.getCuotaImpuesto())
					&& Math.abs(cuota - AonMathUtils.round(base * tipo / 100)) > TOLERANCIA_CUOTA) {
				v.addError(InvoiceCommunicationError.TBAI_010_074);
			}
			if (v.esUnicaClave(CLAVE_06) && distintoSigno(cuota, importeOCero(baseACoste))) {
				v.addError(InvoiceCommunicationError.TBAI_010_178);
			}
		}
	};

	/**
	 * Factura >> TipoDesglose >> Sujeta >> NoExenta >> DesgloseIVA >> DetalleIVA
	 * (validaciones 10-135, 10-136, 10-137, 10-138 y 10-155)
	 *
	 * 	- Con TipoNoExenta igual a S2 (inversion del sujeto pasivo) no puede haber
	 * 	  tipo impositivo, cuota del impuesto, tipo de recargo de equivalencia ni
	 * 	  cuota de recargo de equivalencia.
	 * 	- 10-155: tampoco puede marcarse la operacion en recargo de equivalencia o
	 * 	  regimen simplificado.
	 *
	 * Se admite que esos campos se informen a cero, que es como se comunican las
	 * facturas con inversion del sujeto pasivo (ver Invoice2tbai).
	 */
	private static final Consumer<EmisionContext> ALTA_DETALLE_IVA_INVERSION_SUJETO_PASIVO = v -> {
		for (DetalleIVAType detalle : detallesIVA(v, TipoOperacionSujetaNoExentaType.S_2)) {
			if (informado(detalle.getTipoImpositivo())) {
				v.addError(InvoiceCommunicationError.TBAI_010_135);
			}
			if (informado(detalle.getCuotaImpuesto())) {
				v.addError(InvoiceCommunicationError.TBAI_010_136);
			}
			if (informado(detalle.getTipoRecargoEquivalencia())) {
				v.addError(InvoiceCommunicationError.TBAI_010_137);
			}
			if (informado(detalle.getCuotaRecargoEquivalencia())) {
				v.addError(InvoiceCommunicationError.TBAI_010_138);
			}
			if (SiNoType.S == detalle.getOperacionEnRecargoDeEquivalenciaORegimenSimplificado()) {
				v.addError(InvoiceCommunicationError.TBAI_010_155);
			}
		}
	};

	/**
	 * Factura >> TipoDesglose >> Sujeta >> NoExenta >> DesgloseIVA >> DetalleIVA
	 * (validaciones 10-84, 10-196, 10-209, 10-210, 10-285, 10-284, 10-167, 10-140
	 * y 10-77)
	 *
	 * 	- 10-84: si la operacion esta en recargo de equivalencia o regimen
	 * 	  simplificado la clave de regimen de IVA tiene que ser 51 o 52.
	 * 	- 10-196: si alguna de las claves es 51 o 52 al menos una linea de desglose
	 * 	  debe estar marcada como operacion en recargo de equivalencia o regimen
	 * 	  simplificado.
	 * 	- 10-209 y 10-210: si la unica clave es 51 el tipo y la cuota de recargo de
	 * 	  equivalencia no deben venir informados o deben ser cero.
	 * 	- 10-285: si alguna de las claves es distinta de 01, 07 y 52 no puede haber
	 * 	  cuota de recargo de equivalencia.
	 * 	- 10-284: si el desglose es por prestacion de servicios no puede haber cuota
	 * 	  de recargo de equivalencia.
	 * 	- 10-167: si la cuota de recargo de equivalencia tiene valor tiene que haber
	 * 	  un destinatario nacional.
	 * 	- 10-140: la base del impuesto y la cuota de recargo de equivalencia no
	 * 	  pueden tener distinto signo.
	 * 	- 10-77: se comprueba que la cuota de recargo de equivalencia este bien
	 * 	  calculada.
	 */
	private static final Consumer<EmisionContext> ALTA_DETALLE_IVA_RECARGO_EQUIVALENCIA = v -> {
		List<DetalleIVAType> detalles = v.getDetallesIVA();
		boolean hayRecargo = detalles.stream()
			.anyMatch(d -> SiNoType.S == d.getOperacionEnRecargoDeEquivalenciaORegimenSimplificado());
		boolean hayCuotaRecargo = detalles.stream()
			.anyMatch(d -> informado(d.getCuotaRecargoEquivalencia()));

		if (hayRecargo && !v.tieneAlgunaClave(CLAVES_RECARGO_EQUIVALENCIA)) {
			v.addError(InvoiceCommunicationError.TBAI_010_084);
		}
		if (!hayRecargo && v.tieneAlgunaClave(CLAVES_RECARGO_EQUIVALENCIA)) {
			v.addError(InvoiceCommunicationError.TBAI_010_196);
		}
		if (v.esUnicaClave(CLAVE_51)) {
			if (detalles.stream().anyMatch(d -> informado(d.getTipoRecargoEquivalencia()))) {
				v.addError(InvoiceCommunicationError.TBAI_010_209);
			}
			if (hayCuotaRecargo) {
				v.addError(InvoiceCommunicationError.TBAI_010_210);
			}
		}
		if (hayCuotaRecargo) {
			if (v.hayClaveFueraDe(CLAVES_CUOTA_RECARGO_EQUIVALENCIA)) {
				v.addError(InvoiceCommunicationError.TBAI_010_285);
			}
			if (!v.hayDestinatarioNacional()) {
				v.addError(InvoiceCommunicationError.TBAI_010_167);
			}
			if (v.getPrestacionServicios() != null
					&& detallesIVA(v.getPrestacionServicios().getSujeta()).stream()
						.anyMatch(d -> informado(d.getCuotaRecargoEquivalencia()))) {
				v.addError(InvoiceCommunicationError.TBAI_010_284);
			}
		}
		for (DetalleIVAType detalle : detalles) {
			if (distintoSigno(importeOCero(detalle.getBaseImponible()),
					importeOCero(detalle.getCuotaRecargoEquivalencia()))) {
				v.addError(InvoiceCommunicationError.TBAI_010_140);
			}
			if (informado(detalle.getTipoRecargoEquivalencia())
					&& Math.abs(importeOCero(detalle.getCuotaRecargoEquivalencia())
						- AonMathUtils.round(importeOCero(detalle.getBaseImponible())
							* importeOCero(detalle.getTipoRecargoEquivalencia()) / 100)) > TOLERANCIA_CUOTA) {
				v.addError(InvoiceCommunicationError.TBAI_010_077);
			}
		}
	};

	/**
	 * Factura >> TipoDesglose >> Sujeta >> NoExenta >> DesgloseIVA >> DetalleIVA
	 * (validaciones 10-265, 10-266, 10-267, 10-268, 10-269, 10-270, 10-271, 10-272
	 * y 10-217)
	 *
	 * Todas se aplican a las lineas sin inversion del sujeto pasivo (TipoNoExenta
	 * igual a S1) y sustituyen a las validaciones 10-175, 10-176, 10-213, 10-214,
	 * 10-215, 10-216, 10-218 y 10-219, que dejaron de incluir la clave 05 para las
	 * fechas de operacion a partir del 01/04/2023:
	 *
	 * 	- 10-265 y 10-266: el tipo impositivo y la cuota del impuesto deben tener
	 * 	  valor si la clave de regimen de IVA es distinta de 03 y 09, salvo que el
	 * 	  destinatario comience por R, G, Q, N o W. Un tipo impositivo cero no se
	 * 	  considera error.
	 * 	- 10-267 y 10-268: con la unica clave 03 o 09 y base imponible cero el tipo
	 * 	  impositivo y la cuota tienen que ser vacios o cero.
	 * 	- 10-269 y 10-270: con la unica clave 03 o 09 y base imponible distinta de
	 * 	  cero el tipo impositivo y la cuota tienen que ser distintos de cero.
	 * 	- 10-271 y 10-272: si alguna clave es 03 o 09 y alguna otra es distinta, el
	 * 	  tipo impositivo y la cuota tienen que estar informados los dos o ninguno.
	 * 	- 10-217: en ese mismo caso, si no hay desglose exento ni no sujeto, debe
	 * 	  haber al menos un tipo impositivo con valor.
	 */
	private static final Consumer<EmisionContext> ALTA_DETALLE_IVA_TIPO_Y_CUOTA = v -> {
		List<DetalleIVAType> detalles = detallesIVA(v, TipoOperacionSujetaNoExentaType.S_1);
		if (detalles.isEmpty()) return;

		boolean margenDeBeneficio = v.tieneAlgunaClave(CLAVES_MARGEN_DE_BENEFICIO);
		boolean unicaMargenDeBeneficio = v.esUnicaClaveDe(CLAVES_MARGEN_DE_BENEFICIO);
		boolean margenYOtras = margenDeBeneficio && v.hayClaveFueraDe(CLAVES_MARGEN_DE_BENEFICIO);

		for (DetalleIVAType detalle : detalles) {
			boolean hayTipo = AonStringUtils.isNotBlank(detalle.getTipoImpositivo());
			boolean hayCuota = AonStringUtils.isNotBlank(detalle.getCuotaImpuesto());
			boolean hayBase = informado(detalle.getBaseImponible());

			if (!margenDeBeneficio && !hayDestinatarioSinTipoImpositivo(v)) {
				if (!hayTipo) {
					v.addError(InvoiceCommunicationError.TBAI_010_265);
				}
				if (!hayCuota) {
					v.addError(InvoiceCommunicationError.TBAI_010_266);
				}
			}
			if (unicaMargenDeBeneficio) {
				if (!hayBase && informado(detalle.getTipoImpositivo())) {
					v.addError(InvoiceCommunicationError.TBAI_010_267);
				}
				if (!hayBase && informado(detalle.getCuotaImpuesto())) {
					v.addError(InvoiceCommunicationError.TBAI_010_268);
				}
				if (hayBase && !informado(detalle.getTipoImpositivo())) {
					v.addError(InvoiceCommunicationError.TBAI_010_269);
				}
				if (hayBase && !informado(detalle.getCuotaImpuesto())) {
					v.addError(InvoiceCommunicationError.TBAI_010_270);
				}
			}
			if (margenYOtras) {
				if (!informado(detalle.getTipoImpositivo()) && informado(detalle.getCuotaImpuesto())) {
					v.addError(InvoiceCommunicationError.TBAI_010_271);
				}
				if (informado(detalle.getTipoImpositivo()) && !informado(detalle.getCuotaImpuesto())) {
					v.addError(InvoiceCommunicationError.TBAI_010_272);
				}
			}
		}
		if (margenYOtras && !v.hayDesgloseExento() && !v.hayDesgloseNoSujeto()
				&& detalles.stream().noneMatch(d -> informado(d.getTipoImpositivo()))) {
			v.addError(InvoiceCommunicationError.TBAI_010_217);
		}
	};

	// *********************************************************
	// ***************** [ALTA: CUADRE DE IMPORTES] ************
	// *********************************************************

	/**
	 * Factura >> DatosFactura (validacion 10-72)
	 *
	 * 	- Se comprueba que cuadra la informacion indicada en las lineas de detalle
	 * 	  con el importe total de la factura, salvo para las claves de regimen de
	 * 	  IVA 03, 05, 06 y 09.
	 */
	private static final Consumer<EmisionContext> ALTA_CUADRE_IMPORTE_TOTAL_Y_DETALLES = v -> {
		if (v.getDatosFactura() == null || v.getDetalles().isEmpty()
				|| v.tieneAlgunaClave(CLAVES_SIN_CUADRE_DETALLES)) {
			return;
		}
		Double total = importe(v.getDatosFactura().getImporteTotalFactura());
		if (total != null && Math.abs(total - sumaDetalles(v)) > TOLERANCIA_CUADRE) {
			v.addError(InvoiceCommunicationError.TBAI_010_072);
		}
	};

	/**
	 * Factura >> DatosFactura (validacion 10-79)
	 *
	 * 	- Se comprueba que cuadra la informacion indicada en los desgloses con el
	 * 	  importe total de la factura, salvo para las claves de regimen de IVA 03,
	 * 	  05, 06, 09 y 17.
	 */
	private static final Consumer<EmisionContext> ALTA_CUADRE_IMPORTE_TOTAL_Y_DESGLOSE = v -> {
		if (v.getDatosFactura() == null || v.tieneAlgunaClave(CLAVES_SIN_CUADRE_DESGLOSE)) return;

		Double total = importe(v.getDatosFactura().getImporteTotalFactura());
		if (total != null && Math.abs(total - sumaDesglose(v)) > TOLERANCIA_CUADRE) {
			v.addError(InvoiceCommunicationError.TBAI_010_079);
		}
	};

	/**
	 * Factura >> DatosFactura (validacion 10-78)
	 *
	 * 	- Se comprueba que la informacion indicada en las lineas de detalle cuadra
	 * 	  con la indicada en los desgloses, salvo para las claves de regimen de IVA
	 * 	  03, 05, 06, 09 y 17.
	 */
	private static final Consumer<EmisionContext> ALTA_CUADRE_DETALLES_Y_DESGLOSE = v -> {
		if (v.getDetalles().isEmpty() || v.tieneAlgunaClave(CLAVES_SIN_CUADRE_DESGLOSE)) return;

		if (Math.abs(sumaDetalles(v) - sumaDesglose(v)) > TOLERANCIA_CUADRE) {
			v.addError(InvoiceCommunicationError.TBAI_010_078);
		}
	};

	// *********************************************************
	// ***************** [ALTA: HUELLA TICKETBAI] **************
	// *********************************************************

	/**
	 * HuellaTBAI
	 *
	 * 	- El bloque HuellaTBAI es obligatorio.
	 */
	private static final Consumer<EmisionContext> ALTA_HUELLA = v -> {
		if (v.getHuella() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * HuellaTBAI >> EncadenamientoFacturaAnterior (validaciones 004 y 016-017)
	 *
	 * 	- Bloque opcional, que no se informa en la primera factura de la cadena.
	 * 	- El numero y la fecha de expedicion de la factura anterior son obligatorios
	 * 	  y la fecha debe ser correcta y no posterior a la fecha actual.
	 * 	- 016-017: la firma de la factura anterior debe contener 100 caracteres.
	 *
	 * El encadenamiento con la factura anterior lo comprueba la DFA con la
	 * informacion de su sistema (validacion 009).
	 */
	private static final Consumer<EmisionContext> ALTA_HUELLA_ENCADENAMIENTO = v -> {
		EncadenamientoFacturaAnteriorType encadenamiento = v.getEncadenamiento();
		if (encadenamiento == null) return;

		if (AonStringUtils.length(encadenamiento.getSerieFacturaAnterior()) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
		if (AonStringUtils.isBlank(encadenamiento.getNumFacturaAnterior())) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.length(encadenamiento.getNumFacturaAnterior()) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}

		String valor = encadenamiento.getFechaExpedicionFacturaAnterior();
		if (AonStringUtils.isBlank(valor)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (!PATRON_FECHA.matcher(valor).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		} else {
			Date anterior = fecha(valor);
			if (anterior == null || AonDateUtils.isAfter(anterior, AonDateUtils.today())) {
				v.addError(InvoiceCommunicationError.TBAI_004);
			}
		}

		String firma = encadenamiento.getSignatureValueFirmaFacturaAnterior();
		if (AonStringUtils.isBlank(firma)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.length(firma) > MAX_TEXTO_100) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		} else if (AonStringUtils.length(firma) != MAX_TEXTO_100) {
			v.addError(InvoiceCommunicationError.TBAI_016_017);
		}
	};

	/**
	 * HuellaTBAI >> NumSerieDispositivo
	 *
	 * 	- Campo opcional que no puede exceder los 30 caracteres.
	 */
	private static final Consumer<EmisionContext> ALTA_HUELLA_NUM_SERIE_DISPOSITIVO = v -> {
		if (v.getHuella() == null) return;

		String numSerie = v.getHuella().getNumSerieDispositivo();
		if (AonStringUtils.isNotBlank(numSerie) && AonStringUtils.length(numSerie) > MAX_TEXTO_30) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * HuellaTBAI >> Software
	 *
	 * 	- El bloque Software es obligatorio.
	 */
	private static final Consumer<EmisionContext> ALTA_SOFTWARE = v -> {
		if (v.getHuella() == null) return;

		if (v.getSoftwareEmision() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * HuellaTBAI >> Software >> LicenciaTBAI (validacion 004)
	 *
	 * 	- Campo obligatorio: se comprueba que la licencia TicketBAI exista.
	 * 	- No puede exceder los 20 caracteres.
	 */
	private static final Consumer<EmisionContext> ALTA_SOFTWARE_LICENCIA_TBAI = v -> {
		if (v.getSoftwareEmision() == null) return;

		String licencia = v.getSoftwareEmision().getLicenciaTBAI();
		if (AonStringUtils.isBlank(licencia)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.length(licencia) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * HuellaTBAI >> Software >> EntidadDesarrolladora (validacion 016)
	 *
	 * 	- El bloque EntidadDesarrolladora es obligatorio y debe tener contenido: se
	 * 	  informa el NIF o, alternativamente, el bloque IDOtro.
	 */
	private static final Consumer<EmisionContext> ALTA_SOFTWARE_ENTIDAD_DESARROLLADORA = v -> {
		if (v.getSoftwareEmision() == null) return;

		ticketbai.emision.EntidadDesarrolladoraType entidad = v.getEntidadDesarrolladoraEmision();
		if (entidad == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
			return;
		}
		if (AonStringUtils.isBlank(entidad.getNIF()) && entidad.getIDOtro() == null) {
			v.addError(InvoiceCommunicationError.TBAI_016);
		} else if (AonStringUtils.isNotBlank(entidad.getNIF()) && entidad.getIDOtro() != null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * HuellaTBAI >> Software >> EntidadDesarrolladora >> NIF (validacion 011)
	 *
	 * 	- Si se informa el NIF de la entidad desarrolladora debe ser una secuencia
	 * 	  de nueve digitos o letras con el formato del tipo NIFType y corresponder
	 * 	  con el NIF registrado en la lista de software TicketBAI.
	 */
	private static final Consumer<EmisionContext> ALTA_SOFTWARE_ENTIDAD_DESARROLLADORA_NIF = v -> {
		ticketbai.emision.EntidadDesarrolladoraType entidad = v.getEntidadDesarrolladoraEmision();
		if (entidad == null || AonStringUtils.isBlank(entidad.getNIF())) return;

		String nif = entidad.getNIF();
		if (!PATRON_NIF.matcher(nif).matches()) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		} else if (!AonDocumentUtil.isValid(nif)) {
			v.addError(InvoiceCommunicationError.TBAI_011);
		}
	};

	/**
	 * HuellaTBAI >> Software >> EntidadDesarrolladora >> IDOtro (validacion 016)
	 *
	 * 	- El campo IDType es obligatorio y debe ser uno de los tipos de
	 * 	  identificacion admitidos (02, 03, 04, 05 o 06).
	 * 	- El campo ID es obligatorio y no puede exceder los 20 caracteres.
	 */
	private static final Consumer<EmisionContext> ALTA_SOFTWARE_ENTIDAD_DESARROLLADORA_ID_OTRO = v -> {
		ticketbai.emision.IDOtro idOtro = v.getIDOtroEmision();
		if (idOtro == null) return;

		if (AonStringUtils.isBlank(idOtro.getIDType())) {
			v.addError(InvoiceCommunicationError.TBAI_016);
		} else if (!TIPOS_ID_OTRO.contains(idOtro.getIDType())) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}

		if (AonStringUtils.isBlank(idOtro.getID())) {
			v.addError(InvoiceCommunicationError.TBAI_016);
		} else if (AonStringUtils.length(idOtro.getID()) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * HuellaTBAI >> Software >> Nombre (validacion 011)
	 *
	 * 	- Campo obligatorio que no puede exceder los 120 caracteres.
	 * 	- Debe coincidir con el nombre registrado en la lista de software TicketBAI.
	 */
	private static final Consumer<EmisionContext> ALTA_SOFTWARE_NOMBRE = v -> {
		if (v.getSoftwareEmision() == null) return;

		String nombre = v.getSoftwareEmision().getNombre();
		if (AonStringUtils.isBlank(nombre)) {
			v.addError(InvoiceCommunicationError.TBAI_011);
		} else if (AonStringUtils.length(nombre) > MAX_TEXTO_120) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * HuellaTBAI >> Software >> Version (validacion 004)
	 *
	 * 	- Campo obligatorio que no puede exceder los 20 caracteres.
	 */
	private static final Consumer<EmisionContext> ALTA_SOFTWARE_VERSION = v -> {
		if (v.getSoftwareEmision() == null) return;

		String version = v.getSoftwareEmision().getVersion();
		if (AonStringUtils.isBlank(version)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.length(version) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	// *********************************************************
	// ***** [VALIDACION SUBSANACION/MODIFICACION TICKETBAI] ***
	// *********************************************************

	/**
	 * Valida el fichero de subsanacion o modificacion (zuzendu) TicketBAI.
	 *
	 * El zuzendu repite el contenido del alta con los datos corregidos, por lo que
	 * los bloques Sujetos, Factura y HuellaTBAI son los del esquema de alta y se
	 * validan con sus mismas validaciones. Ademas se comprueban la cabecera propia
	 * del esquema de subsanacion/modificacion y la firma del alta de la factura
	 * que se corrige.
	 *
	 * Se acumulan todos los errores encontrados y, si hay alguno, se lanza una
	 * unica excepcion con todos ellos.
	 *
	 * El bloque Signature no se valida porque se incorpora al fichero al firmarlo,
	 * despues de serializar el objeto.
	 *
	 * @param zuzendu fichero de subsanacion o modificacion a validar
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	public static void validateZuzendu(SubsanacionModificacionTicketBAI zuzendu) throws InvoiceCommunicationException {
		if (zuzendu == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.TBAI_002);
		}

		ZuzenduContext v = new ZuzenduContext(zuzendu);
		ZUZENDU_CABECERA
			.andThen(ZUZENDU_CABECERA_ID_VERSION)
			.andThen(ZUZENDU_CABECERA_ACCION)
			.andThen(ZUZENDU_SIGNATURE_VALUE_FIRMA_FACTURA)
		.accept(v);

		try {
			validateEmision(asEmision(zuzendu));
		} catch (InvoiceCommunicationException e) {
			e.getMessages().forEach(v::addError);
		}

		if (!v.errors.isEmpty()) {
			throw new InvoiceCommunicationException(v.errors);
		}
	}

	/**
	 * Fichero de alta equivalente al zuzendu, con el que se reutilizan las
	 * validaciones del alta. La cabecera es la del esquema de alta porque la del
	 * zuzendu es distinta y se valida aparte.
	 */
	private static TicketBai asEmision(SubsanacionModificacionTicketBAI zuzendu) {
		ticketbai.emision.Cabecera cabecera = new ticketbai.emision.Cabecera();
		cabecera.setIDVersionTBAI(ID_VERSION_TBAI);

		TicketBai emision = new TicketBai();
		emision.setCabecera(cabecera);
		emision.setSujetos(zuzendu.getSujetos());
		emision.setFactura(zuzendu.getFactura());
		emision.setHuellaTBAI(zuzendu.getHuellaTBAI());
		return emision;
	}

	private static class ZuzenduContext {
		final SubsanacionModificacionTicketBAI zuzendu;
		final List<InvoiceCommunicationError> errors = new LinkedList<>();

		ZuzenduContext(SubsanacionModificacionTicketBAI zuzendu) {
			this.zuzendu = zuzendu;
		}

		ticketbai.zuzendu_alta.Cabecera getCabecera() {
			return zuzendu.getCabecera();
		}

		/**
		 * No se repite el mismo error cuando varios campos incumplen la misma
		 * validacion, porque los errores no identifican el campo al que pertenecen.
		 */
		void addError(InvoiceCommunicationError error) {
			if (!errors.contains(error)) {
				errors.add(error);
			}
		}
	}

	/**
	 * 1. Cabecera
	 *
	 * 	- El bloque Cabecera es obligatorio.
	 */
	private static final Consumer<ZuzenduContext> ZUZENDU_CABECERA = v -> {
		if (v.getCabecera() == null) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 2. Cabecera >> IDVersion
	 *
	 * 	- Campo obligatorio.
	 * 	- El unico valor admitido por el esquema de subsanacion/modificacion es "1.0".
	 */
	private static final Consumer<ZuzenduContext> ZUZENDU_CABECERA_ID_VERSION = v -> {
		if (v.getCabecera() == null) return;

		String version = v.getCabecera().getIDVersion();
		if (AonStringUtils.isBlank(version)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.notEquals(ID_VERSION_ZUZENDU, version)) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};

	/**
	 * 3. Cabecera >> Accion
	 *
	 * 	- Campo obligatorio: subsanacion de un fichero de alta rechazado o
	 * 	  modificacion de un fichero de alta ya registrado.
	 */
	private static final Consumer<ZuzenduContext> ZUZENDU_CABECERA_ACCION = v -> {
		if (v.getCabecera() == null) return;

		if (v.getCabecera().getAccion() == null) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		}
	};

	/**
	 * 4. SignatureValueFirmaFactura
	 *
	 * 	- Campo obligatorio: identifica la factura que se corrige con los 100
	 * 	  primeros caracteres de la firma de su fichero de alta.
	 */
	private static final Consumer<ZuzenduContext> ZUZENDU_SIGNATURE_VALUE_FIRMA_FACTURA = v -> {
		String firma = v.zuzendu.getSignatureValueFirmaFactura();
		if (AonStringUtils.isBlank(firma)) {
			v.addError(InvoiceCommunicationError.TBAI_004);
		} else if (AonStringUtils.length(firma) > MAX_TEXTO_100) {
			v.addError(InvoiceCommunicationError.TBAI_002);
		}
	};
}

/*
	Validaciones de la anulacion que solo puede resolver la DFA con la informacion
	de su sistema y que se reciben en la respuesta al envio (o de forma asincrona
	en la aplicacion web "Consulta de facturas"):

	- 001 El certificado utilizado para el envio no es valido (revocado o no homologado).
	- 006 El servicio de recepcion no esta disponible.
	- 018 El fichero de alta que se anula no existe en el sistema.
	- 019 El fichero de alta ya ha sido anulado previamente.
	- 024 La factura a anular ya ha sido sustituida o rectificada por sustitucion.
	- 007 / 012 / 013 Avisos sobre la identidad y la validez del certificado remitente.
	- 008 / 014 Avisos sobre la verificacion de la firma y la validez del certificado
	      firmante (la firma se incorpora al fichero despues de serializarlo).
	- 011 Aviso: los datos de software no coinciden con los del alta-inscripcion.
	- 998 Aviso: el certificado solo se ha podido validar en cache caducada.
	- 999 Error en el sistema.
*/

/*
	Validaciones del alta que solo puede resolver la DFA con la informacion de su
	sistema y que se reciben en la respuesta al envio (o de forma asincrona en la
	aplicacion web "Consulta de facturas enviadas"):

	- 001 El certificado utilizado para el envio no es valido (revocado o no homologado).
	- 005 El fichero de alta ya esta registrado en el sistema.
	- 006 El servicio de recepcion no esta disponible.
	- 029 Aviso: existe otra factura del mismo emisor con la misma serie y numero
	      en el mismo ano de expedicion (validacion 029-237).
	- 10-80 / 10-81 / 10-160 Avisos sobre las facturas rectificadas o sustituidas:
	      que existan en el sistema, que sean simplificadas cuando se emite factura
	      en sustitucion de una simplificada y que no hayan sido anuladas,
	      sustituidas o rectificadas por sustitucion previamente.
	- 016-283 Aviso: la fecha y hora de recepcion no debe superar en mas de 24 horas
	      la fecha y hora de expedicion de la factura.
	- 007 / 012 / 013 / 998 Avisos sobre la identidad y la validez del certificado
	      remitente y sobre el registro del dispositivo de facturacion.
	- 008 / 014 Avisos sobre la verificacion de la firma y la validez del certificado
	      firmante (la firma se incorpora al fichero despues de serializarlo).
	- 009 Aviso: posible error de encadenamiento con la factura anterior.
	- 011 Aviso: los datos de software no coinciden con los del alta-inscripcion.
	- 015 Aviso: el NIF del emisor no consta en el fichero central de contribuyentes
	      o, si es persona fisica, no esta censado en el IAE.
	- 999 Error en el sistema.

	Tampoco se comprueban las validaciones cuyos valores admitidos no publica el
	documento: 10-73 y 10-75 (tipo impositivo y tipo de recargo de equivalencia
	validos), el limite de antiguedad de la fecha de operacion de la validacion
	10-59 y la validacion 10-179, que exige calcular la cuota del impuesto a partir
	de la base imponible a coste, que se informa a nivel de factura y no se puede
	repartir entre las lineas del desglose.
*/
