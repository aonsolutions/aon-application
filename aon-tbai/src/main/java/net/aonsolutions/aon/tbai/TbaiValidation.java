package net.aonsolutions.aon.tbai;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
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

/**
 * Validaciones previas al envio del fichero de anulacion TicketBAI.
 *
 * Las validaciones y los codigos de error se corresponden con el documento
 * "Listado de validaciones y errores del fichero de ANULACION TicketBAI"
 * version 4.0 de la DFA
 * (documentacion/Validaciones fichero de Anulacion TicketBAI-v4.pdf) y con el
 * esquema Anula_ticketBaiV1-2-2.xsd.
 */
public class TbaiValidation {

	private static final String ID_VERSION_TBAI = "1.2";
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
