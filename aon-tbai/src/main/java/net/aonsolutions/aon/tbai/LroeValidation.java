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

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.CountryEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaConSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaEmitidaSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionFacturaRecibidaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionGastoConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionIngresoSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesFacturasEmitidasSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesFacturasRecibidasType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesGastosConFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposanulacion.AnulacionesIngresosSinSGType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera140Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.Cabecera240Type;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.DocumentoType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaConEmisorType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDFacturaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.IDOtroType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_tiposcomplejos.NIFPersonaType;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_1_ingresos_confacturaconsg_anulacionpeticion_v1_0_0.LROEPF140IngresosConFacturaConSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_1_2_ingresos_confacturasinsg_anulacionpeticion_v1_0_0.LROEPF140IngresosConFacturaSinSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pf_140_2_1_gastos_confactura_anulacionpeticion_v1_0_0.LROEPF140GastosConFacturaAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_1_facturasemitidas_consg_anulacionpeticion_v1_0_0.LROEPJ240FacturasEmitidasConSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_1_2_facturasemitidas_sinsg_anulacionpeticion_v1_0_0.LROEPJ240FacturasEmitidasSinSGAnulacionPeticion;
import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.lroe_pj_240_2_facturasrecibidas_anulacionpeticion_v1_0_0.LROEPJ240FacturasRecibidasAnulacionPeticion;
import net.aonsolutions.aon.tbai.lroe.IDType;
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
	private static final String MODELO_240 = "240";
	private static final String CAPITULO_1 = "1";
	private static final String CAPITULO_2 = "2";
	private static final String SUBCAPITULO_1_1 = "1.1";
	private static final String SUBCAPITULO_1_2 = "1.2";
	private static final String SUBCAPITULO_2_1 = "2.1";
	private static final String ID_VERSION = "1.0";

	private static final String FORMATO_FECHA = "dd-MM-yyyy";

	/**
	 * Numero maximo de anulaciones que admiten AnulacionesIngresosConSGType y
	 * AnulacionesFacturasEmitidasConSGType.
	 */
	private static final int MAX_REGISTROS_CON_SG = 1000;

	/**
	 * Numero maximo de anulaciones que admiten AnulacionesIngresosSinSGType y
	 * AnulacionesFacturasEmitidasSinSGType.
	 */
	private static final int MAX_REGISTROS_SIN_SG = 10000;

	/**
	 * Numero maximo de anulaciones que admiten AnulacionesGastosConFacturaType y
	 * AnulacionesFacturasRecibidasType.
	 */
	private static final int MAX_REGISTROS_GASTOS = 10000;

	/** Longitud maxima del tipo TextMax20Type del esquema batuz_TiposBasicos. */
	private static final int MAX_TEXTO_20 = 20;

	/** Longitud maxima del tipo TextMax120Type del esquema batuz_TiposBasicos. */
	private static final int MAX_TEXTO_120 = 120;

	private static final Pattern PATRON_NIF =
		Pattern.compile("([a-zA-Z]\\d{7}[a-zA-Z])|(\\d{8}[a-zA-Z])|([a-zA-Z]\\d{8})");
	private static final Pattern PATRON_FECHA = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");

	/**
	 * Formato del NIF-IVA: el prefijo de dos letras del pais que lo ha asignado y
	 * el numero de identificacion, sin exceder los 20 caracteres del tipo
	 * TextMax20Type con el que se informa.
	 */
	private static final Pattern PATRON_NIF_IVA = Pattern.compile("[a-zA-Z]{2}[a-zA-Z0-9]{1,18}");

	/**
	 * Tipos de documento con los que se puede identificar al emisor de una factura
	 * recibida, que son los valores del tipo TipoDocumentoIdentificativoEnum del
	 * esquema batuz_Enumerados.
	 */
	private static final Set<String> ID_TYPES = Set.of(IDType.NIF_IVA.getName(),
		IDType.PASAPORTE.getName(), IDType.DOCUMENTO_OFICIAL_PAIS.getName(),
		IDType.CERTIFICADO_RESIDENCIA.getName(), IDType.OTRO.getName());

	private LroeValidation() {

	}

	/**
	 * Vista comun de las cabeceras Cabecera140Type (personas fisicas) y
	 * Cabecera240Type (personas juridicas), que declaran los mismos campos pero no
	 * comparten ningun tipo del que hereden.
	 */
	private record Cabecera(String modelo, String capitulo, String subcapitulo, OperacionEnum operacion,
			String version, int ejercicio, NIFPersonaType obligadoTributario) {

		static Cabecera of(Cabecera140Type cabecera) {
			return cabecera == null ? null
				: new Cabecera(cabecera.getModelo(), cabecera.getCapitulo(), cabecera.getSubcapitulo(),
					cabecera.getOperacion(), cabecera.getVersion(), cabecera.getEjercicio(),
					cabecera.getObligadoTributario());
		}

		static Cabecera of(Cabecera240Type cabecera) {
			return cabecera == null ? null
				: new Cabecera(cabecera.getModelo(), cabecera.getCapitulo(), cabecera.getSubcapitulo(),
					cabecera.getOperacion(), cabecera.getVersion(), cabecera.getEjercicio(),
					cabecera.getObligadoTributario());
		}
	}

	/**
	 * Datos con los que se identifica la factura que se anula.
	 *
	 * En los subcapitulos con software garante se leen del fichero TicketBAI y en
	 * los subcapitulos sin software garante del bloque IDIngreso o IDFactura, que
	 * no incluye el NIF del emisor.
	 *
	 * En el subcapitulo de gastos con factura se leen del bloque IDGasto, donde el
	 * emisor es el proveedor y se puede identificar con su NIF o, si no lo tiene,
	 * con el ID del bloque IDOtro.
	 */
	private record Factura(String idEmisor, String serie, String numero, String fecha) {

	}

	/**
	 * Datos de la peticion de anulacion que necesitan las validaciones comunes a
	 * todos los subcapitulos.
	 */
	private abstract static class AnulacionContext {
		/** Modelo que corresponde a la peticion que se valida: 140 o 240. */
		final String modeloEsperado;

		/** Capitulo que corresponde a la peticion que se valida: 1 o 2. */
		final String capituloEsperado;

		/** Subcapitulo que corresponde a la peticion que se valida: 1.1, 1.2 o 2.1. */
		final String subcapituloEsperado;

		final Cabecera cabecera;

		final List<InvoiceCommunicationError> errors = new LinkedList<>();

		/**
		 * Facturas que se anulan, en el mismo orden en el que aparecen en la peticion.
		 * Las posiciones cuyo contenido no se ha podido leer quedan a null.
		 */
		final List<Factura> facturas = new ArrayList<>();

		AnulacionContext(String modeloEsperado, String capituloEsperado, String subcapituloEsperado,
				Cabecera cabecera) {
			this.modeloEsperado = modeloEsperado;
			this.capituloEsperado = capituloEsperado;
			this.subcapituloEsperado = subcapituloEsperado;
			this.cabecera = cabecera;
		}

		/** Numero de anulaciones que incluye el cuerpo de la peticion. */
		abstract int getNumRegistros();

		/** Numero maximo de anulaciones que admite el esquema del subcapitulo. */
		abstract int getMaxRegistros();

		Cabecera getCabecera() {
			return cabecera;
		}

		NIFPersonaType getObligadoTributario() {
			return cabecera == null ? null : cabecera.obligadoTributario();
		}

		String getNifObligadoTributario() {
			return getObligadoTributario() == null ? null : getObligadoTributario().getNIF();
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

	/**
	 * Peticion de anulacion de los subcapitulos con software garante
	 * (LROE_PF_140_1_1 y LROE_PJ_240_1_1), en los que cada anulacion es un fichero
	 * de anulacion TicketBAI.
	 */
	private static class AnulacionConSGContext extends AnulacionContext {
		/**
		 * Anulaciones del cuerpo de la peticion: los Ingresos del subcapitulo
		 * LROE_PF_140_1_1 o las FacturasEmitidas del subcapitulo LROE_PJ_240_1_1.
		 */
		final List<AnulacionFacturaConSGType> registros;

		AnulacionConSGContext(String modeloEsperado, Cabecera cabecera,
				List<AnulacionFacturaConSGType> registros) {
			super(modeloEsperado, CAPITULO_1, SUBCAPITULO_1_1, cabecera);
			this.registros = registros;
		}

		@Override
		int getNumRegistros() {
			return AonCollectionUtils.size(registros);
		}

		@Override
		int getMaxRegistros() {
			return MAX_REGISTROS_CON_SG;
		}
	}

	/**
	 * Peticion de anulacion de los subcapitulos sin software garante
	 * (LROE_PF_140_1_2 y LROE_PJ_240_1_2), en los que cada anulacion identifica la
	 * factura con su serie, su numero y su fecha de expedicion.
	 */
	private static class AnulacionSinSGContext extends AnulacionContext {
		/**
		 * Identificadores de las facturas que se anulan: el IDIngreso de cada Ingreso
		 * del subcapitulo LROE_PF_140_1_2 o el IDFactura de cada FacturaEmitida del
		 * subcapitulo LROE_PJ_240_1_2.
		 */
		final List<IDFacturaType> registros;

		AnulacionSinSGContext(String modeloEsperado, Cabecera cabecera, List<IDFacturaType> registros) {
			super(modeloEsperado, CAPITULO_1, SUBCAPITULO_1_2, cabecera);
			this.registros = registros;
		}

		@Override
		int getNumRegistros() {
			return AonCollectionUtils.size(registros);
		}

		@Override
		int getMaxRegistros() {
			return MAX_REGISTROS_SIN_SG;
		}
	}

	/**
	 * Peticion de anulacion de los gastos con factura del modelo 140
	 * (LROE_PF_140_2_1) y de las facturas recibidas del modelo 240
	 * (LROE_PJ_240_2), en las que cada anulacion identifica la factura recibida con
	 * su serie, su numero, su fecha de expedicion y su emisor.
	 */
	private static class AnulacionGastosContext extends AnulacionContext {
		/**
		 * Identificadores de las facturas recibidas que se anulan: el IDGasto de cada
		 * Gasto del modelo 140 o el IDRecibida de cada FacturaRecibida del modelo 240.
		 */
		final List<IDFacturaConEmisorType> registros;

		AnulacionGastosContext(String modeloEsperado, String subcapituloEsperado, Cabecera cabecera,
				List<IDFacturaConEmisorType> registros) {
			super(modeloEsperado, CAPITULO_2, subcapituloEsperado, cabecera);
			this.registros = registros;
		}

		@Override
		int getNumRegistros() {
			return AonCollectionUtils.size(registros);
		}

		@Override
		int getMaxRegistros() {
			return MAX_REGISTROS_GASTOS;
		}
	}

	// *****************************************************************
	// ************ [VALIDACION ANULACION CON SG 140/240] **************
	// *****************************************************************

	/**
	 * Valida el fichero de anulacion del subcapitulo LROE_PF_140_1_1 (personas
	 * fisicas: ingresos con facturas emitidas con software garante).
	 *
	 * @param lroe fichero de anulacion a validar
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	public static void validateAnulacionConSG(LROEPF140IngresosConFacturaConSGAnulacionPeticion lroe)
			throws InvoiceCommunicationException {
		if (lroe == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.LROE_1000001);
		}

		validateAnulacionConSG(new AnulacionConSGContext(MODELO_140, Cabecera.of(lroe.getCabecera()),
			lroe.getIngresos() == null ? null : lroe.getIngresos().getIngreso()));
	}

	/**
	 * Valida el fichero de anulacion del subcapitulo LROE_PJ_240_1_1 (personas
	 * juridicas: facturas emitidas con software garante).
	 *
	 * @param lroe fichero de anulacion a validar
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	public static void validateAnulacionConSG(LROEPJ240FacturasEmitidasConSGAnulacionPeticion lroe)
			throws InvoiceCommunicationException {
		if (lroe == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.LROE_1000001);
		}

		validateAnulacionConSG(new AnulacionConSGContext(MODELO_240, Cabecera.of(lroe.getCabecera()),
			lroe.getFacturasEmitidas() == null ? null : lroe.getFacturasEmitidas().getFacturaEmitida()));
	}

	/**
	 * Valida la peticion de anulacion de un subcapitulo de facturas emitidas con
	 * software garante. Las validaciones son las mismas para el LROE_PF_140_1_1 y
	 * para el LROE_PJ_240_1_1, que solo se diferencian en el modelo de la cabecera
	 * y en el nombre del bloque que agrupa las anulaciones.
	 *
	 * Se acumulan todos los errores encontrados y, si hay alguno, se lanza una
	 * unica excepcion con todos ellos.
	 *
	 * El bloque Signature de cada fichero de anulacion TicketBAI no se valida
	 * porque se incorpora al fichero al firmarlo, despues de serializarlo.
	 *
	 * @param v contexto con los datos de la peticion que se valida
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	private static void validateAnulacionConSG(AnulacionConSGContext v)
			throws InvoiceCommunicationException {
		LroeValidation.<AnulacionConSGContext>primera(CABECERA)
			.andThen(CABECERA_MODELO)
			.andThen(CABECERA_CAPITULO)
			.andThen(CABECERA_SUBCAPITULO)
			.andThen(CABECERA_OPERACION)
			.andThen(CABECERA_VERSION)
			.andThen(CABECERA_EJERCICIO)
			.andThen(CABECERA_OBLIGADO_TRIBUTARIO)
			.andThen(CABECERA_OBLIGADO_TRIBUTARIO_NIF)
			.andThen(CABECERA_OBLIGADO_TRIBUTARIO_NOMBRE_RAZON_SOCIAL)
			.andThen(REGISTROS)
			.andThen(REGISTROS_ANULACION_TICKETBAI)
			.andThen(FACTURAS_EMISOR_NIF)
			.andThen(FACTURAS_FECHA_EXPEDICION)
			.andThen(FACTURAS_EJERCICIO)
			.andThen(FACTURAS_DUPLICADAS)
		.accept(v);

		throwErrors(v);
	}

	// *****************************************************************
	// ************ [VALIDACION ANULACION SIN SG 140/240] **************
	// *****************************************************************

	/**
	 * Valida el fichero de anulacion del subcapitulo LROE_PF_140_1_2 (personas
	 * fisicas: ingresos con facturas emitidas sin software garante).
	 *
	 * @param lroe fichero de anulacion a validar
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	public static void validateAnulacionSinSG(LROEPF140IngresosConFacturaSinSGAnulacionPeticion lroe)
			throws InvoiceCommunicationException {
		if (lroe == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.LROE_1000001);
		}

		validateAnulacionSinSG(new AnulacionSinSGContext(MODELO_140, Cabecera.of(lroe.getCabecera()),
			idIngresos(lroe.getIngresos())));
	}

	/**
	 * Valida el fichero de anulacion del subcapitulo LROE_PJ_240_1_2 (personas
	 * juridicas: facturas emitidas sin software garante).
	 *
	 * @param lroe fichero de anulacion a validar
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	public static void validateAnulacionSinSG(LROEPJ240FacturasEmitidasSinSGAnulacionPeticion lroe)
			throws InvoiceCommunicationException {
		if (lroe == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.LROE_1000001);
		}

		validateAnulacionSinSG(new AnulacionSinSGContext(MODELO_240, Cabecera.of(lroe.getCabecera()),
			idFacturas(lroe.getFacturasEmitidas())));
	}

	/**
	 * Valida la peticion de anulacion de un subcapitulo de facturas emitidas sin
	 * software garante. Las validaciones son las mismas para el LROE_PF_140_1_2 y
	 * para el LROE_PJ_240_1_2, que solo se diferencian en el modelo de la cabecera
	 * y en el nombre de los bloques que identifican las facturas que se anulan.
	 *
	 * Se acumulan todos los errores encontrados y, si hay alguno, se lanza una
	 * unica excepcion con todos ellos.
	 *
	 * @param v contexto con los datos de la peticion que se valida
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	private static void validateAnulacionSinSG(AnulacionSinSGContext v)
			throws InvoiceCommunicationException {
		LroeValidation.<AnulacionSinSGContext>primera(CABECERA)
			.andThen(CABECERA_MODELO)
			.andThen(CABECERA_CAPITULO)
			.andThen(CABECERA_SUBCAPITULO)
			.andThen(CABECERA_OPERACION)
			.andThen(CABECERA_VERSION)
			.andThen(CABECERA_EJERCICIO)
			.andThen(CABECERA_OBLIGADO_TRIBUTARIO)
			.andThen(CABECERA_OBLIGADO_TRIBUTARIO_NIF)
			.andThen(CABECERA_OBLIGADO_TRIBUTARIO_NOMBRE_RAZON_SOCIAL)
			.andThen(REGISTROS)
			.andThen(REGISTROS_ID_FACTURA)
			.andThen(FACTURAS_FECHA_EXPEDICION)
			.andThen(FACTURAS_EJERCICIO)
			.andThen(FACTURAS_DUPLICADAS)
		.accept(v);

		throwErrors(v);
	}

	// *****************************************************************
	// *********** [VALIDACION ANULACION GASTOS 140/240] ***************
	// *****************************************************************

	/**
	 * Valida el fichero de anulacion del subcapitulo LROE_PF_140_2_1 (personas
	 * fisicas: gastos con factura recibida).
	 *
	 * @param lroe fichero de anulacion a validar
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	public static void validateAnulacionGastos(LROEPF140GastosConFacturaAnulacionPeticion lroe)
			throws InvoiceCommunicationException {
		if (lroe == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.LROE_1000001);
		}

		validateAnulacionGastos(new AnulacionGastosContext(MODELO_140, SUBCAPITULO_2_1,
			Cabecera.of(lroe.getCabecera()), idGastos(lroe.getGastos())));
	}

	/**
	 * Valida el fichero de anulacion del capitulo LROE_PJ_240_2 (personas
	 * juridicas: facturas recibidas).
	 *
	 * @param lroe fichero de anulacion a validar
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	public static void validateAnulacionGastos(LROEPJ240FacturasRecibidasAnulacionPeticion lroe)
			throws InvoiceCommunicationException {
		if (lroe == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.LROE_1000001);
		}

		validateAnulacionGastos(new AnulacionGastosContext(MODELO_240, null,
			Cabecera.of(lroe.getCabecera()), idRecibidas(lroe.getFacturasRecibidas())));
	}

	/**
	 * Valida la peticion de anulacion de los gastos con factura del modelo 140 o de
	 * las facturas recibidas del modelo 240. Las validaciones son las mismas para el
	 * LROE_PF_140_2_1 y para el LROE_PJ_240_2, que solo se diferencian en el modelo
	 * y el subcapitulo de la cabecera y en el nombre de los bloques que identifican
	 * las facturas que se anulan.
	 *
	 * Se acumulan todos los errores encontrados y, si hay alguno, se lanza una
	 * unica excepcion con todos ellos.
	 *
	 * A diferencia de los subcapitulos de facturas emitidas, el emisor de la
	 * factura es el proveedor y no el obligado tributario, por lo que no se
	 * comprueba que los dos coincidan sino que el documento con el que se
	 * identifica tiene un formato correcto.
	 *
	 * Tampoco se comprueba que el ejercicio de la cabecera coincida con el anio de
	 * la fecha de expedicion de las facturas, porque el ejercicio de una factura
	 * recibida es el de su fecha de recepcion (ver LROE.getEjercicio), que no se
	 * informa en la anulacion.
	 *
	 * @param v contexto con los datos de la peticion que se valida
	 * @throws InvoiceCommunicationException si el fichero no cumple alguna validacion
	 */
	private static void validateAnulacionGastos(AnulacionGastosContext v)
			throws InvoiceCommunicationException {
		LroeValidation.<AnulacionGastosContext>primera(CABECERA)
			.andThen(CABECERA_MODELO)
			.andThen(CABECERA_CAPITULO)
			.andThen(CABECERA_SUBCAPITULO)
			.andThen(CABECERA_OPERACION)
			.andThen(CABECERA_VERSION)
			.andThen(CABECERA_EJERCICIO)
			.andThen(CABECERA_OBLIGADO_TRIBUTARIO)
			.andThen(CABECERA_OBLIGADO_TRIBUTARIO_NIF)
			.andThen(CABECERA_OBLIGADO_TRIBUTARIO_NOMBRE_RAZON_SOCIAL)
			.andThen(REGISTROS)
			.andThen(REGISTROS_ID_GASTO)
			.andThen(GASTOS_EMISOR_FACTURA_RECIBIDA)
			.andThen(FACTURAS_FECHA_EXPEDICION)
			.andThen(FACTURAS_DUPLICADAS)
		.accept(v);

		throwErrors(v);
	}

	/**
	 * Devuelve la primera validacion de una cadena adaptada al contexto concreto
	 * que se valida.
	 *
	 * Las validaciones comunes se declaran sobre {@link AnulacionContext}, por lo
	 * que se pueden encadenar con andThen sobre cualquiera de sus subclases pero no
	 * pueden iniciar la cadena.
	 */
	private static <T extends AnulacionContext> Consumer<T> primera(Consumer<AnulacionContext> validacion) {
		return validacion::accept;
	}

	/** Lanza una unica excepcion con todos los errores acumulados, si hay alguno. */
	private static void throwErrors(AnulacionContext v) throws InvoiceCommunicationException {
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
			v.addError(InvoiceCommunicationError.LROE_1000001);
		}
	};

	/**
	 * 2. Cabecera >> Modelo
	 *
	 * 	- Campo obligatorio.
	 * 	- El modelo 140 se corresponde con las personas fisicas y el 240 con las
	 * 	  personas juridicas.
	 */
	private static final Consumer<AnulacionContext> CABECERA_MODELO = v -> {
		if (v.getCabecera() == null) return;

		String modelo = v.getCabecera().modelo();
		if (AonStringUtils.isBlank(modelo)) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		} else if (AonStringUtils.notEquals(v.modeloEsperado, modelo)) {
			v.addError(InvoiceCommunicationError.LROE_1000020);
		}
	};

	/**
	 * 3. Cabecera >> Capitulo
	 *
	 * 	- Campo obligatorio.
	 * 	- Los ingresos y las facturas emitidas son el capitulo 1 de los dos modelos y
	 * 	  los gastos y las facturas recibidas el capitulo 2.
	 */
	private static final Consumer<AnulacionContext> CABECERA_CAPITULO = v -> {
		if (v.getCabecera() == null) return;

		String capitulo = v.getCabecera().capitulo();
		if (AonStringUtils.isBlank(capitulo)) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		} else if (AonStringUtils.notEquals(v.capituloEsperado, capitulo)) {
			v.addError(InvoiceCommunicationError.LROE_1000023);
		}
	};

	/**
	 * 4. Cabecera >> Subcapitulo
	 *
	 * 	- Las facturas emitidas con software garante son el subcapitulo 1.1, las
	 * 	  emitidas sin software garante el subcapitulo 1.2 y los gastos con factura
	 * 	  del modelo 140 el subcapitulo 2.1.
	 * 	- El capitulo 2 del modelo 240 (facturas recibidas) no se divide en
	 * 	  subcapitulos, por lo que el campo, que es opcional en el esquema, no se
	 * 	  puede informar.
	 */
	private static final Consumer<AnulacionContext> CABECERA_SUBCAPITULO = v -> {
		if (v.getCabecera() == null) return;

		String subcapitulo = v.getCabecera().subcapitulo();
		if (v.subcapituloEsperado == null) {
			if (AonStringUtils.isNotBlank(subcapitulo)) {
				v.addError(InvoiceCommunicationError.LROE_1000024);
			}
		} else if (AonStringUtils.isBlank(subcapitulo)) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		} else if (AonStringUtils.notEquals(v.subcapituloEsperado, subcapitulo)) {
			v.addError(InvoiceCommunicationError.LROE_1000024);
		}
	};

	/**
	 * 5. Cabecera >> Operacion
	 *
	 * 	- Campo obligatorio.
	 * 	- La unica operacion admitida en una peticion de anulacion es AN0.
	 */
	private static final Consumer<AnulacionContext> CABECERA_OPERACION = v -> {
		if (v.getCabecera() == null) return;

		OperacionEnum operacion = v.getCabecera().operacion();
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
	private static final Consumer<AnulacionContext> CABECERA_VERSION = v -> {
		if (v.getCabecera() == null) return;

		String version = v.getCabecera().version();
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
	private static final Consumer<AnulacionContext> CABECERA_EJERCICIO = v -> {
		if (v.getCabecera() == null) return;

		int ejercicio = v.getCabecera().ejercicio();
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
	private static final Consumer<AnulacionContext> CABECERA_OBLIGADO_TRIBUTARIO = v -> {
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
	private static final Consumer<AnulacionContext> CABECERA_OBLIGADO_TRIBUTARIO_NIF = v -> {
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
	private static final Consumer<AnulacionContext> CABECERA_OBLIGADO_TRIBUTARIO_NOMBRE_RAZON_SOCIAL = v -> {
		if (v.getObligadoTributario() == null) return;

		String nombreRazonSocial = v.getObligadoTributario().getApellidosNombreRazonSocial();
		if (AonStringUtils.isBlank(nombreRazonSocial)) {
			v.addError(InvoiceCommunicationError.LROE_2000000);
		} else if (AonStringUtils.length(nombreRazonSocial) > MAX_TEXTO_120) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		}
	};

	/**
	 * 11. Ingresos / Gastos (modelo 140) / FacturasEmitidas / FacturasRecibidas
	 *     (modelo 240)
	 *
	 * 	- El bloque que agrupa las anulaciones es obligatorio y debe contener al
	 * 	  menos una anulacion.
	 * 	- Los esquemas admiten como maximo 1.000 anulaciones en los subcapitulos con
	 * 	  software garante y 10.000 en los subcapitulos sin software garante y en los
	 * 	  de gastos con factura y facturas recibidas.
	 */
	private static final Consumer<AnulacionContext> REGISTROS = v -> {
		if (v.getNumRegistros() == 0 || v.getNumRegistros() > v.getMaxRegistros()) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		}
	};

	/**
	 * 12. Ingreso / FacturaEmitida >> AnulacionTicketBai (subcapitulos 1.1)
	 *
	 * 	- Campo obligatorio.
	 * 	- El XML del fichero TicketBAI debe cumplir el esquema de anulacion, por lo
	 * 	  que se deserializa y se valida con {@link TbaiValidation#validateAnulacion}.
	 *
	 * La factura que anula cada fichero se guarda en el contexto para las
	 * validaciones posteriores, que necesitan su contenido.
	 */
	private static final Consumer<AnulacionConSGContext> REGISTROS_ANULACION_TICKETBAI = v -> {
		if (v.getNumRegistros() == 0) return;

		for (AnulacionFacturaConSGType registro : v.registros) {
			byte[] anulacionTicketBai = registro == null ? null : registro.getAnulacionTicketBai();
			if (anulacionTicketBai == null || anulacionTicketBai.length == 0) {
				v.addError(InvoiceCommunicationError.LROE_2000000);
				v.facturas.add(null);
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
			v.facturas.add(factura(anulacion));
		}
	};

	/**
	 * 12. Ingreso >> IDIngreso (modelo 140) / FacturaEmitida >> IDFactura (modelo
	 *     240) de los subcapitulos 1.2
	 *
	 * 	- El bloque que identifica la factura que se anula es obligatorio.
	 * 	- El numero y la fecha de expedicion de la factura son obligatorios.
	 * 	- La serie es opcional y ni ella ni el numero pueden exceder los 20
	 * 	  caracteres.
	 *
	 * La factura que anula cada registro se guarda en el contexto para las
	 * validaciones posteriores, que necesitan su contenido.
	 */
	private static final Consumer<AnulacionSinSGContext> REGISTROS_ID_FACTURA = v -> {
		if (v.getNumRegistros() == 0) return;

		for (IDFacturaType idFactura : v.registros) {
			if (idFactura == null) {
				v.addError(InvoiceCommunicationError.LROE_1000001);
				v.facturas.add(null);
				continue;
			}

			if (AonStringUtils.isBlank(idFactura.getNumFactura())) {
				v.addError(InvoiceCommunicationError.LROE_2000000);
			} else if (AonStringUtils.length(idFactura.getNumFactura()) > MAX_TEXTO_20) {
				v.addError(InvoiceCommunicationError.LROE_1000001);
			}

			if (AonStringUtils.length(idFactura.getSerieFactura()) > MAX_TEXTO_20) {
				v.addError(InvoiceCommunicationError.LROE_1000001);
			}

			if (AonStringUtils.isBlank(idFactura.getFechaExpedicionFactura())) {
				v.addError(InvoiceCommunicationError.LROE_2000000);
			}

			v.facturas.add(new Factura(null, idFactura.getSerieFactura(), idFactura.getNumFactura(),
				idFactura.getFechaExpedicionFactura()));
		}
	};

	/**
	 * 12. Gasto >> IDGasto (subcapitulo 2.1) / FacturaRecibida >> IDRecibida
	 *     (capitulo 2 del modelo 240)
	 *
	 * 	- El bloque que identifica la factura recibida que se anula es obligatorio.
	 * 	- El numero y la fecha de expedicion de la factura son obligatorios.
	 * 	- La serie es opcional y ni ella ni el numero pueden exceder los 20
	 * 	  caracteres.
	 * 	- El bloque que identifica al emisor de la factura recibida es obligatorio.
	 *
	 * La factura que anula cada registro se guarda en el contexto para las
	 * validaciones posteriores, que necesitan su contenido.
	 */
	private static final Consumer<AnulacionGastosContext> REGISTROS_ID_GASTO = v -> {
		if (v.getNumRegistros() == 0) return;

		for (IDFacturaConEmisorType idGasto : v.registros) {
			if (idGasto == null) {
				v.addError(InvoiceCommunicationError.LROE_1000001);
				v.facturas.add(null);
				continue;
			}

			if (AonStringUtils.isBlank(idGasto.getNumFactura())) {
				v.addError(InvoiceCommunicationError.LROE_2000000);
			} else if (AonStringUtils.length(idGasto.getNumFactura()) > MAX_TEXTO_20) {
				v.addError(InvoiceCommunicationError.LROE_1000001);
			}

			if (AonStringUtils.length(idGasto.getSerieFactura()) > MAX_TEXTO_20) {
				v.addError(InvoiceCommunicationError.LROE_1000001);
			}

			if (AonStringUtils.isBlank(idGasto.getFechaExpedicionFactura())) {
				v.addError(InvoiceCommunicationError.LROE_2000000);
			}

			if (idGasto.getEmisorFacturaRecibida() == null) {
				v.addError(InvoiceCommunicationError.LROE_2000000);
			}

			v.facturas.add(new Factura(idEmisor(idGasto.getEmisorFacturaRecibida()),
				idGasto.getSerieFactura(), idGasto.getNumFactura(),
				idGasto.getFechaExpedicionFactura()));
		}
	};

	/**
	 * 13. IDGasto / IDRecibida >> EmisorFacturaRecibida
	 *
	 * 	- El emisor se identifica con su NIF o con el bloque IDOtro, pero nunca con
	 * 	  los dos a la vez.
	 * 	- El NIF, si es el que identifica al emisor, debe ser un documento valido con
	 * 	  el formato del tipo NIFType.
	 */
	private static final Consumer<AnulacionGastosContext> GASTOS_EMISOR_FACTURA_RECIBIDA = v -> {
		if (v.getNumRegistros() == 0) return;

		for (IDFacturaConEmisorType idGasto : v.registros) {
			DocumentoType emisor = idGasto == null ? null : idGasto.getEmisorFacturaRecibida();
			if (emisor == null) continue;

			String nif = emisor.getNIF();
			boolean conNif = AonStringUtils.isNotBlank(nif);
			boolean conIdOtro = emisor.getIDOtro() != null;
			if (conNif && conIdOtro) {
				v.addError(InvoiceCommunicationError.LROE_1000001);
				continue;
			}
			if (!conNif && !conIdOtro) {
				v.addError(InvoiceCommunicationError.LROE_2000000);
				continue;
			}

			if (conNif) {
				if (!PATRON_NIF.matcher(nif).matches() || !AonDocumentUtil.isValid(nif)) {
					v.addError(InvoiceCommunicationError.LROE_2000011);
				}
			} else validateIdOtro(v, emisor.getIDOtro());
		}
	};

	/**
	 * 14. IDGasto / IDRecibida >> EmisorFacturaRecibida >> IDOtro
	 *
	 * 	- El campo IDType es obligatorio y debe ser uno de los admitidos por el tipo
	 * 	  TipoDocumentoIdentificativoEnum (02..06).
	 * 	- El campo ID es obligatorio y no puede exceder los 20 caracteres.
	 * 	- El campo CodigoPais es obligatorio cuando el IDType es 03, 04, 05 o 06.
	 * 	- El ID, cuando el IDType es 02, debe tener el formato del NIF-IVA del pais.
	 *
	 * @param v contexto con los datos de la peticion que se valida
	 * @param idOtro identificacion del emisor distinta del NIF
	 */
	private static void validateIdOtro(AnulacionGastosContext v, IDOtroType idOtro) {
		String idType = idOtro.getIDType();
		if (AonStringUtils.isBlank(idType)) {
			v.addError(InvoiceCommunicationError.LROE_2000000);
		} else if (!ID_TYPES.contains(idType)) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		}

		String id = idOtro.getID();
		if (AonStringUtils.isBlank(id)) {
			v.addError(InvoiceCommunicationError.LROE_2000000);
		} else if (AonStringUtils.length(id) > MAX_TEXTO_20) {
			v.addError(InvoiceCommunicationError.LROE_1000001);
		}

		if (!AonStringUtils.equals(IDType.NIF_IVA.getName(), idType)) {
			if (idOtro.getCodigoPais() == null) {
				v.addError(InvoiceCommunicationError.LROE_2000012);
			}
		} else if (AonStringUtils.isNotBlank(id) && !esNifIva(idOtro.getCodigoPais(), id)) {
			v.addError(InvoiceCommunicationError.LROE_2000013);
		}
	}

	/**
	 * 15. AnulacionTicketBai >> IDFactura >> Emisor >> NIF (subcapitulos 1.1)
	 *
	 * 	- El NIF del emisor de la factura que se anula debe coincidir con el del
	 * 	  obligado tributario de la cabecera.
	 *
	 * Los subcapitulos sin software garante no informan del emisor de la factura,
	 * por lo que no se comprueba nada en ellos, y en el subcapitulo de gastos el
	 * emisor es el proveedor, por lo que no se puede exigir que coincida.
	 */
	private static final Consumer<AnulacionContext> FACTURAS_EMISOR_NIF = v -> {
		String nifObligadoTributario = v.getNifObligadoTributario();
		if (AonStringUtils.isBlank(nifObligadoTributario)) return;

		for (Factura factura : v.facturas) {
			if (factura == null || AonStringUtils.isBlank(factura.idEmisor())) continue;

			if (AonStringUtils.notEquals(nifObligadoTributario, factura.idEmisor())) {
				v.addError(InvoiceCommunicationError.LROE_2000002);
			}
		}
	};

	/**
	 * 16. FechaExpedicionFactura
	 *
	 * 	- Debe ser una fecha correcta con formato dd-mm-yyyy que no puede ser
	 * 	  posterior a la fecha actual.
	 */
	private static final Consumer<AnulacionContext> FACTURAS_FECHA_EXPEDICION = v -> {
		for (Factura factura : v.facturas) {
			if (factura == null || AonStringUtils.isBlank(factura.fecha())) continue;

			if (parseFechaExpedicion(factura.fecha()) == null) {
				v.addError(InvoiceCommunicationError.LROE_2000005);
			}
		}
	};

	/**
	 * 17. Cabecera >> Ejercicio / FechaExpedicionFactura
	 *
	 * 	- El ejercicio indicado en la cabecera debe coincidir con el del cuerpo, es
	 * 	  decir, con el anio de la fecha de expedicion de todas las facturas que se
	 * 	  anulan en la peticion.
	 */
	private static final Consumer<AnulacionContext> FACTURAS_EJERCICIO = v -> {
		if (v.getCabecera() == null || v.getCabecera().ejercicio() <= 0) return;

		int ejercicio = v.getCabecera().ejercicio();
		for (Factura factura : v.facturas) {
			if (factura == null) continue;

			Date fechaExpedicion = parseFechaExpedicion(factura.fecha());
			if (fechaExpedicion == null) continue;

			if (ejercicio != AonDateUtils.getYear(fechaExpedicion)) {
				v.addError(InvoiceCommunicationError.LROE_1000021);
			}
		}
	};

	/**
	 * 18. Ingreso / FacturaEmitida / Gasto / FacturaRecibida
	 *
	 * 	- La peticion no puede incluir dos anulaciones de la misma factura, que se
	 * 	  identifica con el documento del emisor, la serie, el numero y la fecha de
	 * 	  expedicion.
	 */
	private static final Consumer<AnulacionContext> FACTURAS_DUPLICADAS = v -> {
		Set<String> facturas = new HashSet<>();
		for (Factura factura : v.facturas) {
			if (factura == null) continue;

			String clave = AonStringUtils.trimToEmpty(factura.idEmisor())
				+ "|" + AonStringUtils.trimToEmpty(factura.serie())
				+ "|" + AonStringUtils.trimToEmpty(factura.numero())
				+ "|" + AonStringUtils.trimToEmpty(factura.fecha());

			if (!facturas.add(clave)) {
				v.addError(InvoiceCommunicationError.LROE_1000005);
			}
		}
	};

	/**
	 * Devuelve la factura que anula el fichero TicketBAI o null si el fichero no se
	 * ha podido leer o no identifica la factura.
	 */
	private static Factura factura(AnulaTicketBai anulacion) {
		if (anulacion == null || anulacion.getIDFactura() == null
				|| anulacion.getIDFactura().getCabeceraFactura() == null) {
			return null;
		}

		Emisor emisor = anulacion.getIDFactura().getEmisor();
		CabeceraFacturaType cabeceraFactura = anulacion.getIDFactura().getCabeceraFactura();
		return new Factura(emisor == null ? null : emisor.getNIF(), cabeceraFactura.getSerieFactura(),
			cabeceraFactura.getNumFactura(), cabeceraFactura.getFechaExpedicionFactura());
	}

	/**
	 * Devuelve el documento con el que se identifica al emisor de la factura
	 * recibida, que es su NIF o el ID del bloque IDOtro, o null si no se informa.
	 */
	private static String idEmisor(DocumentoType emisor) {
		if (emisor == null) return null;

		if (AonStringUtils.isNotBlank(emisor.getNIF())) return emisor.getNIF();
		return emisor.getIDOtro() == null ? null : emisor.getIDOtro().getID();
	}

	/**
	 * Comprueba si el identificador tiene el formato del NIF-IVA del pais indicado,
	 * es decir, si empieza por el prefijo con el que el pais asigna los NIF-IVA.
	 *
	 * El prefijo es el codigo ISO del pais excepto en Grecia, que asigna los
	 * NIF-IVA con el prefijo EL, y en Irlanda del Norte, que los asigna con el
	 * prefijo XI y comparte el codigo de pais del Reino Unido.
	 *
	 * Cuando no se informa el codigo de pais, que solo es obligatorio con los demas
	 * tipos de documento, unicamente se comprueba el formato del identificador.
	 */
	private static boolean esNifIva(CountryEnum codigoPais, String id) {
		if (!PATRON_NIF_IVA.matcher(id).matches()) return false;
		if (codigoPais == null) return true;

		String prefijo = id.substring(0, 2).toUpperCase();
		if (CountryEnum.GR.equals(codigoPais)) return "EL".equals(prefijo);
		if (CountryEnum.GB.equals(codigoPais)) return "GB".equals(prefijo) || "XI".equals(prefijo);
		return codigoPais.name().equals(prefijo);
	}

	/** Identificadores de las facturas recibidas de los gastos del subcapitulo LROE_PF_140_2_1. */
	private static List<IDFacturaConEmisorType> idGastos(AnulacionesGastosConFacturaType gastos) {
		if (gastos == null) return null;

		List<IDFacturaConEmisorType> registros = new ArrayList<>();
		for (AnulacionGastoConFacturaType gasto : gastos.getGasto()) {
			registros.add(gasto == null ? null : gasto.getIDGasto());
		}
		return registros;
	}

	/** Identificadores de las facturas recibidas del capitulo LROE_PJ_240_2. */
	private static List<IDFacturaConEmisorType> idRecibidas(AnulacionesFacturasRecibidasType facturas) {
		if (facturas == null) return null;

		List<IDFacturaConEmisorType> registros = new ArrayList<>();
		for (AnulacionFacturaRecibidaType facturaRecibida : facturas.getFacturaRecibida()) {
			registros.add(facturaRecibida == null ? null : facturaRecibida.getIDRecibida());
		}
		return registros;
	}

	/** Identificadores de las facturas de los ingresos del subcapitulo LROE_PF_140_1_2. */
	private static List<IDFacturaType> idIngresos(AnulacionesIngresosSinSGType ingresos) {
		if (ingresos == null) return null;

		List<IDFacturaType> registros = new ArrayList<>();
		for (AnulacionIngresoSinSGType ingreso : ingresos.getIngreso()) {
			registros.add(ingreso == null ? null : ingreso.getIDIngreso());
		}
		return registros;
	}

	/** Identificadores de las facturas emitidas del subcapitulo LROE_PJ_240_1_2. */
	private static List<IDFacturaType> idFacturas(AnulacionesFacturasEmitidasSinSGType facturasEmitidas) {
		if (facturasEmitidas == null) return null;

		List<IDFacturaType> registros = new ArrayList<>();
		for (AnulacionFacturaEmitidaSinSGType facturaEmitida : facturasEmitidas.getFacturaEmitida()) {
			registros.add(facturaEmitida == null ? null : facturaEmitida.getIDFactura());
		}
		return registros;
	}

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
	Validaciones de la anulacion de los subcapitulos LROE_PF_140_1_1,
	LROE_PF_140_1_2, LROE_PF_140_2_1, LROE_PJ_240_1_1, LROE_PJ_240_1_2 y del
	capitulo LROE_PJ_240_2 que solo puede resolver la DFB con la informacion de su
	sistema y que se reciben en la respuesta al envio:

	- B4_1000002 Todos los registros incluidos en la peticion son incorrectos.
	- B4_1000003 Error al descomprimir el fichero.
	- B4_1000004 Error tecnico.
	- B4_1000006 El Modelo no corresponde al tipo de persona.
	- B4_1000022 El NIF del interesado indicado en la cabecera del JSON de la
	             peticion no coincide con el del obligado tributario. Los dos se
	             construyen a partir de los mismos datos (ver LROE140.buildJSON y
	             LROE140.buildCabecera, LROE240.buildJSON y LROE240.buildCabecera),
	             por lo que no se puede incumplir aqui.
	- B4_1000026 Apartado erroneo. El apartado se envia en la cabecera del JSON.
	- B4_1000030 El nombre y apellidos o razon social del interesado indicado en la
	             cabecera del JSON no coincide con el del obligado tributario.
	- B4_2000004 El registro que se anula no existe en el sistema.
	- B4_2000006 El registro ya esta anulado.

	Solo en los subcapitulos con software garante:

	- B4_2000063 La licencia TicketBAI no esta registrada.
	- B4_2000064 El NIF de la entidad desarrolladora no corresponde con el de la licencia.
	- B4_2000065 El ID de la entidad desarrolladora no corresponde con el de la licencia.
	- B4_2000066 El nombre del software no corresponde con el de la licencia.
	- B4_2000070 La firma no cumple los requisitos de la politica de firma TicketBAI
	             (la firma se incorpora al fichero despues de serializarlo).
*/
