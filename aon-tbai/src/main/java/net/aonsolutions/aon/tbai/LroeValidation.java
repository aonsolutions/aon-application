package net.aonsolutions.aon.tbai;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera140Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_anulacionpeticion_v1_0_0.LROEPF140IngresosConFacturaConSGAnulacionPeticion;
import net.aonsolutions.aon.tbai.utils.XMLUtils;
import ticketbai.anulacion.AnulaTicketBai;
import ticketbai.anulacion.CabeceraFacturaType;
import ticketbai.anulacion.Emisor;

/**
 * Validaciones previas al envio de los ficheros LROE.
 *
 * Las validaciones y los codigos de error se corresponden con el documento
 * "Validaciones y errores BATUZ - LROE" version 1.0.13 de la DFB
 * (documentacion/lroe/batuz_lroe_validaciones_errores_v1_0_13.pdf) y con los
 * esquemas publicados en https://www.batuz.eus/fitxategiak/batuz/LROE/esquemas.
 */
public class LroeValidation {

	private static final String MODELO_140 = "140";
	private static final String CAPITULO_1 = "1";
	private static final String SUBCAPITULO_1_1 = "1.1";
	private static final String ID_VERSION = "1.0";

	private static final String FORMATO_FECHA = "dd-MM-yyyy";

	/** Numero maximo de anulaciones que admite AnulacionesIngresosConSGType. */
	private static final int MAX_INGRESOS = 1000;

	/** Longitud maxima del tipo TextMax120Type del esquema batuz_TiposBasicos. */
	private static final int MAX_TEXTO_120 = 120;

	private static final Pattern PATRON_NIF =
		Pattern.compile("([a-zA-Z]\\d{7}[a-zA-Z])|(\\d{8}[a-zA-Z])|([a-zA-Z]\\d{8})");
	private static final Pattern PATRON_FECHA = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");

	private LroeValidation() {

	}

	private static class AnulacionConSGContext {
		final LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe;
		final List<InvoiceCommunicationError> errors = new LinkedList<>();

		/**
		 * Ficheros de anulacion TicketBAI de cada Ingreso, en el mismo orden en el
		 * que aparecen en la peticion. Las posiciones cuyo contenido no se ha podido
		 * deserializar quedan a null.
		 */
		final List<AnulaTicketBai> ticketBais = new ArrayList<>();

		AnulacionConSGContext(LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe) {
			this.lroe = lroe;
		}

		Cabecera140Type getCabecera() {
			return lroe.getCabecera();
		}

		NIFPersonaType getObligadoTributario() {
			return getCabecera() == null ? null : getCabecera().getObligadoTributario();
		}

		String getNifObligadoTributario() {
			return getObligadoTributario() == null ? null : getObligadoTributario().getNIF();
		}

		List<AnulacionFacturaConSGType> getIngresos() {
			return lroe.getIngresos() == null ? null : lroe.getIngresos().getIngreso();
		}

		Emisor getEmisor(AnulaTicketBai anulacion) {
			return anulacion == null || anulacion.getIDFactura() == null
				? null : anulacion.getIDFactura().getEmisor();
		}

		CabeceraFacturaType getCabeceraFactura(AnulaTicketBai anulacion) {
			return anulacion == null || anulacion.getIDFactura() == null
				? null : anulacion.getIDFactura().getCabeceraFactura();
		}

		/**
		 * No se repite el mismo error cuando varios campos o varios registros
		 * incumplen la misma validacion, porque los errores no identifican el campo
		 * ni el registro al que pertenecen.
		 */
		void addError(InvoiceCommunicationError error) {
			if (!errors.contains(error)) {
				errors.add(error);
			}
		}
	}

	// *****************************************************************
	// ********** [VALIDACION LROE_PF_140_1_1 ANULACION] ***************
	// *****************************************************************

	/**
	 * Valida el fichero de anulacion del subcapitulo LROE_PF_140_1_1 (ingresos y
	 * facturas emitidas con software garante).
	 *
	 * Se acumulan todos los errores encontrados y, si hay alguno, se lanza una
	 * unica excepcion con todos ellos.
	 *
	 * El bloque Signature de cada fichero de anulacion TicketBAI no se valida
	 * porque se incorpora al fichero al firmarlo, despues de serializarlo.
	 *
	 * @param lroe fichero de anulacion a validar
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	public static void validateAnulacionConSG(LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe)
			throws InvoiceCommunicationException {
		if (lroe == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.LROE_1000001);
		}

		AnulacionConSGContext v = new AnulacionConSGContext(lroe);
		CABECERA
			.andThen(CABECERA_MODELO)
			.andThen(CABECERA_CAPITULO)
			.andThen(CABECERA_SUBCAPITULO)
			.andThen(CABECERA_OPERACION)
			.andThen(CABECERA_VERSION)
			.andThen(CABECERA_EJERCICIO)
			.andThen(CABECERA_OBLIGADO_TRIBUTARIO)
			.andThen(CABECERA_OBLIGADO_TRIBUTARIO_NIF)
			.andThen(CABECERA_OBLIGADO_TRIBUTARIO_NOMBRE_RAZON_SOCIAL)
			.andThen(INGRESOS)
			.andThen(INGRESOS_ANULACION_TICKETBAI)
			.andThen(INGRESOS_EMISOR_NIF)
			.andThen(INGRESOS_FECHA_EXPEDICION_FACTURA)
			.andThen(INGRESOS_EJERCICIO)
			.andThen(INGRESOS_DUPLICADOS)
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
	private static final Consumer<AnulacionConSGContext> CABECERA = v -> {
		if (v.getCabecera() == null) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		}
	};

	/**
	 * 2. Cabecera >> Modelo
	 *
	 * 	- Campo obligatorio.
	 * 	- El unico modelo admitido por el esquema es el 140, que se corresponde con
	 * 	  las personas fisicas.
	 */
	private static final Consumer<AnulacionConSGContext> CABECERA_MODELO = v -> {
		if (v.getCabecera() == null) return;

		String modelo = v.getCabecera().getModelo();
		if (AonStringUtils.isBlank(modelo)) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		} else if (AonStringUtils.notEquals(MODELO_140, modelo)) {
			v.addError(InvoiceCommunicationError.LROE_1000020);
		}
	};

	/**
	 * 3. Cabecera >> Capitulo
	 *
	 * 	- Campo obligatorio.
	 * 	- Los ingresos y las facturas emitidas son el capitulo 1 del modelo 140.
	 */
	private static final Consumer<AnulacionConSGContext> CABECERA_CAPITULO = v -> {
		if (v.getCabecera() == null) return;

		String capitulo = v.getCabecera().getCapitulo();
		if (AonStringUtils.isBlank(capitulo)) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		} else if (AonStringUtils.notEquals(CAPITULO_1, capitulo)) {
			v.addError(InvoiceCommunicationError.LROE_1000023);
		}
	};

	/**
	 * 4. Cabecera >> Subcapitulo
	 *
	 * 	- Campo obligatorio en el modelo 140.
	 * 	- Los ingresos con facturas emitidas con software garante son el
	 * 	  subcapitulo 1.1.
	 */
	private static final Consumer<AnulacionConSGContext> CABECERA_SUBCAPITULO = v -> {
		if (v.getCabecera() == null) return;

		String subcapitulo = v.getCabecera().getSubcapitulo();
		if (AonStringUtils.isBlank(subcapitulo)) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		} else if (AonStringUtils.notEquals(SUBCAPITULO_1_1, subcapitulo)) {
			v.addError(InvoiceCommunicationError.LROE_1000024);
		}
	};

	/**
	 * 5. Cabecera >> Operacion
	 *
	 * 	- Campo obligatorio.
	 * 	- La unica operacion admitida en una peticion de anulacion es AN0.
	 */
	private static final Consumer<AnulacionConSGContext> CABECERA_OPERACION = v -> {
		if (v.getCabecera() == null) return;

		OperacionEnum operacion = v.getCabecera().getOperacion();
		if (operacion == null) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		} else if (!OperacionEnum.AN_0.equals(operacion)) {
			v.addError(InvoiceCommunicationError.LROE_1000025);
		}
	};

	/**
	 * 6. Cabecera >> Version
	 *
	 * 	- Campo obligatorio.
	 * 	- El unico valor admitido por el esquema (IDVersionEnum) es "1.0".
	 */
	private static final Consumer<AnulacionConSGContext> CABECERA_VERSION = v -> {
		if (v.getCabecera() == null) return;

		String version = v.getCabecera().getVersion();
		if (AonStringUtils.isBlank(version)) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		} else if (AonStringUtils.notEquals(ID_VERSION, version)) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		}
	};

	/**
	 * 7. Cabecera >> Ejercicio
	 *
	 * 	- Campo obligatorio con formato de anio (YearType).
	 * 	- No puede ser posterior al ejercicio en curso.
	 */
	private static final Consumer<AnulacionConSGContext> CABECERA_EJERCICIO = v -> {
		if (v.getCabecera() == null) return;

		int ejercicio = v.getCabecera().getEjercicio();
		if (ejercicio <= 0) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		} else if (ejercicio > AonDateUtils.getYear(AonDateUtils.today())) {
			v.addError(InvoiceCommunicationError.LROE_1000021);
		}
	};

	/**
	 * 8. Cabecera >> ObligadoTributario
	 *
	 * 	- El bloque ObligadoTributario es obligatorio.
	 */
	private static final Consumer<AnulacionConSGContext> CABECERA_OBLIGADO_TRIBUTARIO = v -> {
		if (v.getCabecera() == null) return;

		if (v.getObligadoTributario() == null) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		}
	};

	/**
	 * 9. Cabecera >> ObligadoTributario >> NIF
	 *
	 * 	- Campo obligatorio.
	 * 	- Secuencia de nueve digitos o letras con el formato del tipo NIFType.
	 * 	- El NIF debe ser un documento valido.
	 */
	private static final Consumer<AnulacionConSGContext> CABECERA_OBLIGADO_TRIBUTARIO_NIF = v -> {
		if (v.getObligadoTributario() == null) return;

		String nif = v.getNifObligadoTributario();
		if (AonStringUtils.isBlank(nif)) {
			v.addError(InvoiceCommunicationError.LROE_2000000);
		} else if (!PATRON_NIF.matcher(nif).matches() || !AonDocumentUtil.isValid(nif)) {
			v.addError(InvoiceCommunicationError.LROE_2000011);
		}
	};

	/**
	 * 10. Cabecera >> ObligadoTributario >> ApellidosNombreRazonSocial
	 *
	 * 	- Campo obligatorio que no puede exceder los 120 caracteres.
	 */
	private static final Consumer<AnulacionConSGContext> CABECERA_OBLIGADO_TRIBUTARIO_NOMBRE_RAZON_SOCIAL = v -> {
		if (v.getObligadoTributario() == null) return;

		String nombreRazonSocial = v.getObligadoTributario().getApellidosNombreRazonSocial();
		if (AonStringUtils.isBlank(nombreRazonSocial)) {
			v.addError(InvoiceCommunicationError.LROE_2000000);
		} else if (AonStringUtils.length(nombreRazonSocial) > MAX_TEXTO_120) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		}
	};

	/**
	 * 11. Ingresos
	 *
	 * 	- El bloque Ingresos es obligatorio y debe contener entre 1 y 1.000
	 * 	  anulaciones.
	 */
	private static final Consumer<AnulacionConSGContext> INGRESOS = v -> {
		List<AnulacionFacturaConSGType> ingresos = v.getIngresos();
		if (AonCollectionUtils.isEmpty(ingresos)) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		} else if (AonCollectionUtils.size(ingresos) > MAX_INGRESOS) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		}
	};

	/**
	 * 12. Ingresos >> Ingreso >> AnulacionTicketBai
	 *
	 * 	- Campo obligatorio.
	 * 	- El XML del fichero TicketBAI debe cumplir el esquema de anulacion, por lo
	 * 	  que se deserializa y se valida con {@link TbaiValidation#validateAnulacion}.
	 *
	 * Los ficheros deserializados se guardan en el contexto para las validaciones
	 * posteriores, que necesitan su contenido.
	 */
	private static final Consumer<AnulacionConSGContext> INGRESOS_ANULACION_TICKETBAI = v -> {
		List<AnulacionFacturaConSGType> ingresos = v.getIngresos();
		if (AonCollectionUtils.isEmpty(ingresos)) return;

		for (AnulacionFacturaConSGType ingreso : ingresos) {
			byte[] anulacionTicketBai = ingreso == null ? null : ingreso.getAnulacionTicketBai();
			if (anulacionTicketBai == null || anulacionTicketBai.length == 0) {
				v.addError(InvoiceCommunicationError.LROE_2000000);
				v.ticketBais.add(null);
				continue;
			}

			AnulaTicketBai anulacion = null;
			try {
				anulacion = (AnulaTicketBai) XMLUtils.unmarshal(anulacionTicketBai, AnulaTicketBai.class);
				TbaiValidation.validateAnulacion(anulacion);
			} catch (JAXBException | ClassCastException e) {
				anulacion = null;
				v.addError(InvoiceCommunicationError.LROE_2000001);
			} catch (InvoiceCommunicationException e) {
				v.addError(InvoiceCommunicationError.LROE_2000001);
				e.getMessages().forEach(v::addError);
			}
			v.ticketBais.add(anulacion);
		}
	};

	/**
	 * 13. Ingresos >> Ingreso >> AnulacionTicketBai >> IDFactura >> Emisor >> NIF
	 *
	 * 	- El NIF del emisor de la factura que se anula debe coincidir con el del
	 * 	  obligado tributario de la cabecera.
	 */
	private static final Consumer<AnulacionConSGContext> INGRESOS_EMISOR_NIF = v -> {
		String nifObligadoTributario = v.getNifObligadoTributario();
		if (AonStringUtils.isBlank(nifObligadoTributario)) return;

		for (AnulaTicketBai anulacion : v.ticketBais) {
			Emisor emisor = v.getEmisor(anulacion);
			if (emisor == null || AonStringUtils.isBlank(emisor.getNIF())) continue;

			if (AonStringUtils.notEquals(nifObligadoTributario, emisor.getNIF())) {
				v.addError(InvoiceCommunicationError.LROE_2000002);
			}
		}
	};

	/**
	 * 14. Ingresos >> Ingreso >> AnulacionTicketBai >> IDFactura >> CabeceraFactura
	 * 	   >> FechaExpedicionFactura
	 *
	 * 	- Debe ser una fecha correcta con formato dd-mm-yyyy que no puede ser
	 * 	  posterior a la fecha actual.
	 */
	private static final Consumer<AnulacionConSGContext> INGRESOS_FECHA_EXPEDICION_FACTURA = v -> {
		for (AnulaTicketBai anulacion : v.ticketBais) {
			CabeceraFacturaType cabeceraFactura = v.getCabeceraFactura(anulacion);
			if (cabeceraFactura == null) continue;

			if (parseFechaExpedicion(cabeceraFactura.getFechaExpedicionFactura()) == null) {
				v.addError(InvoiceCommunicationError.LROE_2000005);
			}
		}
	};

	/**
	 * 15. Cabecera >> Ejercicio / Ingresos >> Ingreso >> AnulacionTicketBai
	 *
	 * 	- El ejercicio indicado en la cabecera debe coincidir con el del cuerpo, es
	 * 	  decir, con el anio de la fecha de expedicion de todas las facturas que se
	 * 	  anulan en la peticion.
	 */
	private static final Consumer<AnulacionConSGContext> INGRESOS_EJERCICIO = v -> {
		if (v.getCabecera() == null || v.getCabecera().getEjercicio() <= 0) return;

		int ejercicio = v.getCabecera().getEjercicio();
		for (AnulaTicketBai anulacion : v.ticketBais) {
			CabeceraFacturaType cabeceraFactura = v.getCabeceraFactura(anulacion);
			if (cabeceraFactura == null) continue;

			Date fechaExpedicion = parseFechaExpedicion(cabeceraFactura.getFechaExpedicionFactura());
			if (fechaExpedicion == null) continue;

			if (ejercicio != AonDateUtils.getYear(fechaExpedicion)) {
				v.addError(InvoiceCommunicationError.LROE_1000021);
			}
		}
	};

	/**
	 * 16. Ingresos >> Ingreso
	 *
	 * 	- La peticion no puede incluir dos anulaciones de la misma factura, que se
	 * 	  identifica con el NIF del emisor, la serie, el numero y la fecha de
	 * 	  expedicion.
	 */
	private static final Consumer<AnulacionConSGContext> INGRESOS_DUPLICADOS = v -> {
		Set<String> facturas = new HashSet<>();
		for (AnulaTicketBai anulacion : v.ticketBais) {
			Emisor emisor = v.getEmisor(anulacion);
			CabeceraFacturaType cabeceraFactura = v.getCabeceraFactura(anulacion);
			if (emisor == null || cabeceraFactura == null) continue;

			String factura = AonStringUtils.trimToEmpty(emisor.getNIF())
				+ "|" + AonStringUtils.trimToEmpty(cabeceraFactura.getSerieFactura())
				+ "|" + AonStringUtils.trimToEmpty(cabeceraFactura.getNumFactura())
				+ "|" + AonStringUtils.trimToEmpty(cabeceraFactura.getFechaExpedicionFactura());

			if (!facturas.add(factura)) {
				v.addError(InvoiceCommunicationError.LROE_1000005);
			}
		}
	};

	/**
	 * Devuelve la fecha de expedicion de la factura o null si no es una fecha
	 * correcta con formato dd-mm-yyyy o es posterior a la fecha actual.
	 */
	private static Date parseFechaExpedicion(String fecha) {
		if (AonStringUtils.isBlank(fecha) || !PATRON_FECHA.matcher(fecha).matches()) {
			return null;
		}

		Date fechaExpedicion = AonDateUtils.parse(fecha, FORMATO_FECHA);
		if (fechaExpedicion == null
				|| AonStringUtils.notEquals(fecha, AonDateUtils.format(fechaExpedicion, FORMATO_FECHA))
				|| AonDateUtils.isAfter(fechaExpedicion, AonDateUtils.today())) {
			return null;
		}
		return fechaExpedicion;
	}
}

/*
	Validaciones de la anulacion del subcapitulo LROE_PF_140_1_1 que solo puede
	resolver la DFB con la informacion de su sistema y que se reciben en la
	respuesta al envio:

	- B4_1000002 Todos los registros incluidos en la peticion son incorrectos.
	- B4_1000003 Error al descomprimir el fichero.
	- B4_1000004 Error tecnico.
	- B4_1000006 El Modelo no corresponde al tipo de persona.
	- B4_1000022 El NIF del interesado indicado en la cabecera del JSON de la
	             peticion no coincide con el del obligado tributario. Los dos se
	             construyen a partir de los mismos datos (ver LROE140.buildJSON y
	             LROE140.buildCabecera), por lo que no se puede incumplir aqui.
	- B4_1000026 Apartado erroneo. El apartado se envia en la cabecera del JSON.
	- B4_1000030 El nombre y apellidos o razon social del interesado indicado en la
	             cabecera del JSON no coincide con el del obligado tributario.
	- B4_2000004 El registro que se anula no existe en el sistema.
	- B4_2000006 El registro ya esta anulado.
	- B4_2000063 La licencia TicketBAI no esta registrada.
	- B4_2000064 El NIF de la entidad desarrolladora no corresponde con el de la licencia.
	- B4_2000065 El ID de la entidad desarrolladora no corresponde con el de la licencia.
	- B4_2000066 El nombre del software no corresponde con el de la licencia.
	- B4_2000070 La firma no cumple los requisitos de la politica de firma TicketBAI
	             (la firma se incorpora al fichero despues de serializarlo).
*/
