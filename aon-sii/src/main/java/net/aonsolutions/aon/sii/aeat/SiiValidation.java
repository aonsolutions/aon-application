package net.aonsolutions.aon.sii.aeat;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.CabeceraSiiBaja;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaExpedidaBCType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDFacturaRecibidaNombreBCType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.IDOtroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.RegistroSii.PeriodoLiquidacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasEmitidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.BajaLRFacturasRecibidas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaExpedidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaRecibidasType;
import net.aonsolutions.aon.sii.IDType;

/**
 * Validaciones previas al envio de los mensajes del SII.
 *
 * Las validaciones y los codigos de error se corresponden con el documento
 * "Validaciones y errores SII" version 1.1 de la AEAT
 * (documentacion/Validaciones_ErroresSII_v1.1.pdf).
 */
public class SiiValidation {

	private static final String ID_VERSION_SII = "1.1";
	private static final String FORMATO_FECHA = "dd-MM-yyyy";
	private static final String PREFIJO_NIF_MENOR = "K";

	/** Maximo de registros por peticion (SuministroLR.xsd: maxOccurs="10000"). */
	private static final int MAX_REGISTROS = 10000;
	private static final int MAX_NOMBRE_RAZON = 120;
	private static final int MAX_NUM_SERIE_FACTURA = 60;
	private static final int MAX_REF_EXTERNA = 60;
	private static final int MAX_ID_OTRO = 20;
	private static final int ANIOS_ANTIGUEDAD_MAXIMA = 20;
	private static final int PRIMER_EJERCICIO_TRIMESTRAL = 2018;

	private static final Pattern PATRON_EJERCICIO = Pattern.compile("\\d{4}");
	private static final Pattern PATRON_FECHA = Pattern.compile("\\d{2}-\\d{2}-\\d{4}");
	private static final Pattern PATRON_CARACTER_CONTROL = Pattern.compile("\\p{Cntrl}");

	private static final List<String> PERIODOS_MENSUALES =
		List.of("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");
	private static final List<String> PERIODOS_TRIMESTRALES = List.of("1T", "2T", "3T", "4T");

	/** Tipos de identificacion admitidos en el bloque IDOtro (02..07). */
	private static final List<String> ID_TYPES =
		Arrays.stream(IDType.values()).map(IDType::getName).collect(Collectors.toList());

	private SiiValidation() {

	}

	private static class BajaContext {
		final CabeceraSiiBaja cabecera;
		final List<?> registros;
		final List<InvoiceCommunicationError> errors = new LinkedList<>();

		BajaContext(CabeceraSiiBaja cabecera, List<?> registros) {
			this.cabecera = cabecera;
			this.registros = registros;
		}

		CabeceraSiiBaja getCabecera() {
			return cabecera;
		}

		PersonaFisicaJuridicaESType getTitular() {
			return getCabecera() == null ? null : getCabecera().getTitular();
		}

		String getTitularNif() {
			return getTitular() == null ? null : getTitular().getNIF();
		}

		/**
		 * Los errores no identifican el registro al que pertenecen, por lo que no
		 * se repite el mismo error cuando la peticion lleva varios registros.
		 */
		void addError(InvoiceCommunicationError error) {
			if (!errors.contains(error)) {
				errors.add(error);
			}
		}
	}

	// *********************************************************
	// ********* [VALIDACION BAJA FACTURAS EMITIDAS] ***********
	// *********************************************************

	/**
	 * Valida el mensaje de baja del libro registro de facturas expedidas.
	 *
	 * Se acumulan todos los errores encontrados (cabecera y registros) y, si hay
	 * alguno, se lanza una unica excepcion con todos ellos.
	 *
	 * @param baja mensaje de baja a validar
	 * @throws InvoiceCommunicationException si el mensaje no cumple alguna validacion
	 */
	public static void validateFacturasEmitidasBaja(BajaLRFacturasEmitidas baja) throws InvoiceCommunicationException {
		if (baja == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.SII_4102);
		}

		BajaContext v = new BajaContext(baja.getCabecera(), baja.getRegistroLRBajaExpedidas());
		validateCabecera(v);

		AonCollectionUtils.stream(baja.getRegistroLRBajaExpedidas())
			.filter(Objects::nonNull)
			.forEach(registro -> validateRegistroBajaExpedidas(v, registro));

		if (!v.errors.isEmpty()) {
			throw new InvoiceCommunicationException(v.errors);
		}
	}

	// *********************************************************
	// ******** [VALIDACION BAJA FACTURAS RECIBIDAS] ***********
	// *********************************************************

	/**
	 * Valida el mensaje de baja del libro registro de facturas recibidas.
	 *
	 * Se acumulan todos los errores encontrados (cabecera y registros) y, si hay
	 * alguno, se lanza una unica excepcion con todos ellos.
	 *
	 * @param baja mensaje de baja a validar
	 * @throws InvoiceCommunicationException si el mensaje no cumple alguna validacion
	 */
	public static void validateFacturasRecibidasBaja(BajaLRFacturasRecibidas baja) throws InvoiceCommunicationException {
		if (baja == null) {
			throw new InvoiceCommunicationException(InvoiceCommunicationError.SII_4102);
		}

		BajaContext v = new BajaContext(baja.getCabecera(), baja.getRegistroLRBajaRecibidas());
		validateCabecera(v);

		AonCollectionUtils.stream(baja.getRegistroLRBajaRecibidas())
			.filter(Objects::nonNull)
			.forEach(registro -> validateRegistroBajaRecibidas(v, registro));

		if (!v.errors.isEmpty()) {
			throw new InvoiceCommunicationException(v.errors);
		}
	}

	// *********************************************************
	// ******************* [CABECERA Y ENVIO] ******************
	// *********************************************************

	/**
	 * La cabecera y el numero de registros se validan igual en los dos libros de
	 * registro.
	 *
	 * @param v contexto de validacion
	 */
	private static void validateCabecera(BajaContext v) {
		BAJA_CABECERA
			.andThen(BAJA_CABECERA_ID_VERSION_SII)
			.andThen(BAJA_CABECERA_TITULAR)
			.andThen(BAJA_CABECERA_TITULAR_NIF)
			.andThen(BAJA_CABECERA_TITULAR_NIF_REPRESENTANTE)
			.andThen(BAJA_CABECERA_TITULAR_MENOR)
			.andThen(BAJA_REGISTROS)
		.accept(v);
	}

	/**
	 * 1. Cabecera
	 *
	 * 	- El bloque Cabecera es obligatorio.
	 */
	private static final Consumer<BajaContext> BAJA_CABECERA = v -> {
		if (v.getCabecera() == null) {
			v.addError(InvoiceCommunicationError.SII_4102);
		}
	};

	/**
	 * 2. Cabecera >> IDVersionSii
	 *
	 * 	- Campo obligatorio.
	 * 	- El contenido debe ser una version admitida por el servicio ("1.1").
	 */
	private static final Consumer<BajaContext> BAJA_CABECERA_ID_VERSION_SII = v -> {
		if (v.getCabecera() == null) return;
		String version = v.getCabecera().getIDVersionSii();
		if (AonStringUtils.isBlank(version)) {
			v.addError(InvoiceCommunicationError.SII_4102);
		} else if (AonStringUtils.notEquals(ID_VERSION_SII, version)) {
			v.addError(InvoiceCommunicationError.SII_4100);
		}
	};

	/**
	 * 3. Cabecera >> Titular
	 *
	 * 	- El bloque Titular es obligatorio.
	 * 	- El campo NombreRazon es obligatorio y no puede exceder los 120 caracteres.
	 */
	private static final Consumer<BajaContext> BAJA_CABECERA_TITULAR = v -> {
		if (v.getCabecera() == null) return;
		if (v.getTitular() == null) {
			v.addError(InvoiceCommunicationError.SII_4102);
			return;
		}
		String nombreRazon = v.getTitular().getNombreRazon();
		if (AonStringUtils.isBlank(nombreRazon)) {
			v.addError(InvoiceCommunicationError.SII_4102);
		} else if (AonStringUtils.length(nombreRazon) > MAX_NOMBRE_RAZON) {
			v.addError(InvoiceCommunicationError.SII_3004);
		}
	};

	/**
	 * 4. Cabecera >> Titular >> NIF
	 *
	 * 	- El NIF asociado al titular del libro de registro debe estar identificado.
	 * 	- El NIF del titular debe tener un formato valido.
	 */
	private static final Consumer<BajaContext> BAJA_CABECERA_TITULAR_NIF = v -> {
		if (v.getTitular() == null) return;
		String nif = v.getTitularNif();
		if (AonStringUtils.isBlank(nif)) {
			v.addError(InvoiceCommunicationError.SII_4104);
		} else if (!AonDocumentUtil.isValid(nif)) {
			v.addError(InvoiceCommunicationError.SII_4122);
		}
	};

	/**
	 * 5. Cabecera >> Titular >> NIFRepresentante
	 *
	 * 	- El NIF del representante del titular del libro de registro, si se incluye,
	 * 	  debe estar identificado y tener un formato valido.
	 */
	private static final Consumer<BajaContext> BAJA_CABECERA_TITULAR_NIF_REPRESENTANTE = v -> {
		if (v.getTitular() == null) return;
		String nifRepresentante = v.getTitular().getNIFRepresentante();
		if (AonStringUtils.isNotBlank(nifRepresentante) && !AonDocumentUtil.isValid(nifRepresentante)) {
			v.addError(InvoiceCommunicationError.SII_4123);
		}
	};

	/**
	 * 6. Cabecera >> Titular >> NIF / NIFRepresentante
	 *
	 * 	- Si el NIF del titular del libro de registro es de un menor (empieza por K)
	 * 	  el campo NIFRepresentante es obligatorio y distinto del NIF del titular.
	 */
	private static final Consumer<BajaContext> BAJA_CABECERA_TITULAR_MENOR = v -> {
		if (v.getTitular() == null) return;
		String nif = v.getTitularNif();
		if (!AonStringUtils.startsWithIgnoreCase(nif, PREFIJO_NIF_MENOR)) return;

		String nifRepresentante = v.getTitular().getNIFRepresentante();
		if (AonStringUtils.isBlank(nifRepresentante)) {
			v.addError(InvoiceCommunicationError.SII_1122);
		} else if (AonStringUtils.equals(nif, nifRepresentante)) {
			v.addError(InvoiceCommunicationError.SII_1123);
		}
	};

	/**
	 * 7. RegistroLRBajaExpedidas / RegistroLRBajaRecibidas
	 *
	 * 	- Debe informarse al menos un registro.
	 * 	- No se puede superar el limite maximo de facturas a registrar (10.000).
	 */
	private static final Consumer<BajaContext> BAJA_REGISTROS = v -> {
		if (AonCollectionUtils.isEmpty(v.registros)) {
			v.addError(InvoiceCommunicationError.SII_4102);
		} else if (v.registros.size() > MAX_REGISTROS) {
			v.addError(InvoiceCommunicationError.SII_4117);
		}
	};

	// *********************************************************
	// *********** [VALIDACION REGISTRO DE LA BAJA] ************
	// *********************************************************

	/**
	 * Datos de un registro de baja comunes a los dos libros de registro. El
	 * emisor de la factura se valida aparte porque se identifica de forma
	 * distinta en cada libro.
	 */
	private static record RegistroContext(
		BajaContext vc,
		PeriodoLiquidacion periodoLiquidacion,
		boolean idFactura,
		String numSerieFacturaEmisor,
		String fechaExpedicionFacturaEmisor,
		String refExterna) {}

	private static void validateRegistroBajaExpedidas(BajaContext v, LRBajaExpedidasType registro) {
		IDFacturaExpedidaBCType idFactura = registro.getIDFactura();
		RegistroContext r = new RegistroContext(v,
			registro.getPeriodoLiquidacion(),
			idFactura != null,
			idFactura == null ? null : idFactura.getNumSerieFacturaEmisor(),
			idFactura == null ? null : idFactura.getFechaExpedicionFacturaEmisor(),
			registro.getRefExterna());

		validateRegistro(r, rc -> validateEmisorExpedida(rc.vc(), idFactura));
	}

	private static void validateRegistroBajaRecibidas(BajaContext v, LRBajaRecibidasType registro) {
		IDFacturaRecibidaNombreBCType idFactura = registro.getIDFactura();
		RegistroContext r = new RegistroContext(v,
			registro.getPeriodoLiquidacion(),
			idFactura != null,
			idFactura == null ? null : idFactura.getNumSerieFacturaEmisor(),
			idFactura == null ? null : idFactura.getFechaExpedicionFacturaEmisor(),
			registro.getRefExterna());

		validateRegistro(r, rc -> validateEmisorRecibida(rc.vc(), idFactura));
	}

	private static void validateRegistro(RegistroContext r, Consumer<RegistroContext> emisor) {
		BAJA_REGISTRO_PERIODO_LIQUIDACION
			.andThen(BAJA_REGISTRO_EJERCICIO)
			.andThen(BAJA_REGISTRO_PERIODO)
			.andThen(BAJA_REGISTRO_ID_FACTURA)
			.andThen(emisor)
			.andThen(BAJA_REGISTRO_NUM_SERIE_FACTURA)
			.andThen(BAJA_REGISTRO_FECHA_EXPEDICION)
			.andThen(BAJA_REGISTRO_REF_EXTERNA)
		.accept(r);
	}

	/**
	 * 1. Agrupacion PeriodoLiquidacion
	 *
	 * 	- El bloque PeriodoLiquidacion es obligatorio.
	 */
	private static final Consumer<RegistroContext> BAJA_REGISTRO_PERIODO_LIQUIDACION = r -> {
		if (r.periodoLiquidacion() == null) {
			r.vc().addError(InvoiceCommunicationError.SII_4102);
		}
	};

	/**
	 * 2. Agrupacion PeriodoLiquidacion >> Ejercicio
	 *
	 * 	- Campo obligatorio con formato de cuatro digitos.
	 * 	- Debe ser el ejercicio actual o anteriores. En la baja no se puede informar
	 * 	  un ejercicio superior al actual porque no existen las claves de regimen
	 * 	  "14" y "15" que lo permiten en el alta.
	 */
	private static final Consumer<RegistroContext> BAJA_REGISTRO_EJERCICIO = r -> {
		PeriodoLiquidacion periodoLiquidacion = r.periodoLiquidacion();
		if (periodoLiquidacion == null) return;

		String ejercicio = periodoLiquidacion.getEjercicio();
		if (AonStringUtils.isBlank(ejercicio)) {
			r.vc().addError(InvoiceCommunicationError.SII_4102);
		} else if (!PATRON_EJERCICIO.matcher(ejercicio).matches()
				|| AonNumberUtils.toInteger(ejercicio) > AonDateUtils.getCurrentYear()) {
			r.vc().addError(InvoiceCommunicationError.SII_3015);
		}
	};

	/**
	 * 3. Agrupacion PeriodoLiquidacion >> Periodo
	 *
	 * 	- Campo obligatorio.
	 * 	- Solo se admiten periodos mensuales (01..12) y trimestrales (1T..4T).
	 * 	- Solo se permiten periodos trimestrales a partir del ejercicio 2018.
	 * 	- Si el ejercicio es el actual, el periodo debe ser inferior o igual al
	 * 	  periodo actual.
	 */
	private static final Consumer<RegistroContext> BAJA_REGISTRO_PERIODO = r -> {
		PeriodoLiquidacion periodoLiquidacion = r.periodoLiquidacion();
		if (periodoLiquidacion == null) return;

		String periodo = periodoLiquidacion.getPeriodo();
		if (AonStringUtils.isBlank(periodo)) {
			r.vc().addError(InvoiceCommunicationError.SII_4102);
			return;
		}

		boolean trimestral = PERIODOS_TRIMESTRALES.contains(periodo);
		if (!trimestral && !PERIODOS_MENSUALES.contains(periodo)) {
			r.vc().addError(InvoiceCommunicationError.SII_3017);
			return;
		}

		String ejercicio = periodoLiquidacion.getEjercicio();
		if (AonStringUtils.isBlank(ejercicio) || !PATRON_EJERCICIO.matcher(ejercicio).matches()) return;

		int anio = AonNumberUtils.toInteger(ejercicio);
		if (trimestral && anio < PRIMER_EJERCICIO_TRIMESTRAL) {
			r.vc().addError(InvoiceCommunicationError.SII_1211);
		}
		if (anio == AonDateUtils.getCurrentYear() && primerMesDelPeriodo(periodo) > mesActual()) {
			r.vc().addError(InvoiceCommunicationError.SII_3016);
		}
	};

	/**
	 * 4. Agrupacion IDFactura
	 *
	 * 	- El bloque IDFactura es obligatorio. La factura se identifica con los
	 * 	  mismos datos con los que se dio de alta.
	 */
	private static final Consumer<RegistroContext> BAJA_REGISTRO_ID_FACTURA = r -> {
		if (!r.idFactura()) {
			r.vc().addError(InvoiceCommunicationError.SII_4102);
		}
	};

	/**
	 * 5. Agrupacion IDFactura >> IDEmisorFactura >> NIF (facturas expedidas)
	 *
	 * 	- Campo obligatorio con formato de NIF valido.
	 * 	- El NIF debe ser el mismo que el NIF del titular del libro de registro.
	 *
	 * @param v contexto de validacion
	 * @param idFactura identificacion de la factura expedida
	 */
	private static void validateEmisorExpedida(BajaContext v, IDFacturaExpedidaBCType idFactura) {
		if (idFactura == null) return;

		String nif = idFactura.getIDEmisorFactura() == null ? null : idFactura.getIDEmisorFactura().getNIF();
		if (AonStringUtils.isBlank(nif)) {
			v.addError(InvoiceCommunicationError.SII_4102);
			return;
		}
		if (!AonDocumentUtil.isValid(nif)) {
			v.addError(InvoiceCommunicationError.SII_4111);
		}
		if (AonStringUtils.notEquals(v.getTitularNif(), nif)) {
			v.addError(InvoiceCommunicationError.SII_1112);
		}
	}

	/**
	 * 5. Agrupacion IDFactura >> IDEmisorFactura (facturas recibidas)
	 *
	 * 	- El bloque IDEmisorFactura es obligatorio.
	 * 	- El campo NombreRazon es obligatorio y no puede exceder los 120 caracteres.
	 * 	- El emisor es el proveedor, por lo que se identifica con su NIF o con el
	 * 	  bloque IDOtro, pero nunca con los dos a la vez.
	 * 	- El NIF, si es el que identifica al emisor, debe tener un formato valido.
	 *
	 * @param v contexto de validacion
	 * @param idFactura identificacion de la factura recibida
	 */
	private static void validateEmisorRecibida(BajaContext v, IDFacturaRecibidaNombreBCType idFactura) {
		if (idFactura == null) return;

		IDFacturaRecibidaNombreBCType.IDEmisorFactura emisor = idFactura.getIDEmisorFactura();
		if (emisor == null) {
			v.addError(InvoiceCommunicationError.SII_4102);
			return;
		}

		String nombreRazon = emisor.getNombreRazon();
		if (AonStringUtils.isBlank(nombreRazon)) {
			v.addError(InvoiceCommunicationError.SII_4102);
		} else if (AonStringUtils.length(nombreRazon) > MAX_NOMBRE_RAZON) {
			v.addError(InvoiceCommunicationError.SII_3004);
		}

		String nif = emisor.getNIF();
		IDOtroType idOtro = emisor.getIDOtro();
		boolean conNif = AonStringUtils.isNotBlank(nif);
		boolean conIdOtro = idOtro != null;
		if (conNif == conIdOtro) {
			v.addError(InvoiceCommunicationError.SII_4102);
			return;
		}

		if (conNif) {
			if (!AonDocumentUtil.isValid(nif)) {
				v.addError(InvoiceCommunicationError.SII_4111);
			}
		} else validateIdOtro(v, idOtro);
	}

	/**
	 * 6. Agrupacion IDFactura >> IDEmisorFactura >> IDOtro (facturas recibidas)
	 *
	 * 	- El campo IDType es obligatorio y debe ser uno de los admitidos (02..07).
	 * 	- El campo ID es obligatorio y no puede exceder los 20 caracteres.
	 * 	- El campo CodigoPais solo deja de ser obligatorio cuando el emisor se
	 * 	  identifica con un NIF-IVA.
	 *
	 * @param v contexto de validacion
	 * @param idOtro identificacion del emisor distinta del NIF
	 */
	private static void validateIdOtro(BajaContext v, IDOtroType idOtro) {
		String idType = idOtro.getIDType();
		if (AonStringUtils.isBlank(idType)) {
			v.addError(InvoiceCommunicationError.SII_4102);
		} else if (!ID_TYPES.contains(idType)) {
			v.addError(InvoiceCommunicationError.SII_1103);
		}

		String id = idOtro.getID();
		if (AonStringUtils.isBlank(id)) {
			v.addError(InvoiceCommunicationError.SII_4102);
		} else if (AonStringUtils.length(id) > MAX_ID_OTRO) {
			v.addError(InvoiceCommunicationError.SII_3004);
		}

		boolean nifIva = AonStringUtils.equals(IDType.NIF_IVA.getName(), idType);
		if (idOtro.getCodigoPais() == null && !nifIva) {
			v.addError(InvoiceCommunicationError.SII_1124);
		}
	}

	/**
	 * 7. Agrupacion IDFactura >> NumSerieFacturaEmisor
	 *
	 * 	- Campo obligatorio que no puede exceder los 60 caracteres.
	 * 	- No puede contener caracteres de control.
	 */
	private static final Consumer<RegistroContext> BAJA_REGISTRO_NUM_SERIE_FACTURA = r -> {
		if (!r.idFactura()) return;

		String numSerie = r.numSerieFacturaEmisor();
		if (AonStringUtils.isBlank(numSerie)) {
			r.vc().addError(InvoiceCommunicationError.SII_4102);
		} else if (AonStringUtils.length(numSerie) > MAX_NUM_SERIE_FACTURA) {
			r.vc().addError(InvoiceCommunicationError.SII_3004);
		} else if (PATRON_CARACTER_CONTROL.matcher(numSerie).find()) {
			r.vc().addError(InvoiceCommunicationError.SII_1105);
		}
	};

	/**
	 * 8. Agrupacion IDFactura >> FechaExpedicionFacturaEmisor
	 *
	 * 	- Campo obligatorio con formato dd-mm-yyyy.
	 * 	- La fecha de expedicion no podra ser superior a la fecha actual.
	 * 	- La fecha de expedicion no debe ser inferior a la fecha actual menos
	 * 	  veinte anios.
	 */
	private static final Consumer<RegistroContext> BAJA_REGISTRO_FECHA_EXPEDICION = r -> {
		if (!r.idFactura()) return;

		String fecha = r.fechaExpedicionFacturaEmisor();
		if (AonStringUtils.isBlank(fecha)) {
			r.vc().addError(InvoiceCommunicationError.SII_4102);
			return;
		}
		if (!PATRON_FECHA.matcher(fecha).matches()) {
			r.vc().addError(InvoiceCommunicationError.SII_4106);
			return;
		}

		Date expDate = AonDateUtils.parse(fecha, FORMATO_FECHA);
		if (expDate == null || AonStringUtils.notEquals(fecha, AonDateUtils.format(expDate, FORMATO_FECHA))) {
			r.vc().addError(InvoiceCommunicationError.SII_1106);
			return;
		}
		if (AonDateUtils.isAfter(expDate, AonDateUtils.today())) {
			r.vc().addError(InvoiceCommunicationError.SII_1125);
		}
		if (AonDateUtils.isBefore(expDate, AonDateUtils.addYears(AonDateUtils.today(), -ANIOS_ANTIGUEDAD_MAXIMA))) {
			r.vc().addError(InvoiceCommunicationError.SII_1196);
		}
	};

	/**
	 * 9. RefExterna
	 *
	 * 	- Campo opcional que no puede exceder los 60 caracteres.
	 */
	private static final Consumer<RegistroContext> BAJA_REGISTRO_REF_EXTERNA = r -> {
		String refExterna = r.refExterna();
		if (AonStringUtils.isNotBlank(refExterna) && AonStringUtils.length(refExterna) > MAX_REF_EXTERNA) {
			r.vc().addError(InvoiceCommunicationError.SII_1210);
		}
	};

	/**
	 * Devuelve el primer mes (1..12) del periodo de liquidacion indicado.
	 *
	 * @param periodo periodo mensual (01..12) o trimestral (1T..4T)
	 * @return primer mes del periodo
	 */
	private static int primerMesDelPeriodo(String periodo) {
		if (PERIODOS_TRIMESTRALES.contains(periodo)) {
			return ((AonNumberUtils.toInteger(periodo.substring(0, 1)) - 1) * 3) + 1;
		}
		return AonNumberUtils.toInteger(periodo);
	}

	/**
	 * Devuelve el mes actual (1..12).
	 *
	 * @return mes actual
	 */
	private static int mesActual() {
		return AonDateUtils.getMonth(AonDateUtils.today()) + 1;
	}
}

/*
	Validaciones de la baja que solo puede resolver la AEAT con la informacion
	de su libro de registro y que se reciben en la respuesta al envio:

	- 3001 El registro esta ya dado de baja.
	- 3002 No existe el registro.
	- 3010 El presentador no tiene los permisos necesarios para actualizar esta factura.
	- 3019 El ejercicio y el periodo indicados en la baja deben coincidir con el
	       ejercicio y el periodo del alta del registro y, si hubiera sido modificado,
	       con el indicado en la ultima modificacion (apartado "24. BAJA DE FACTURA"
	       de las facturas expedidas y "12. BAJA DE FACTURA" de las recibidas).
	- 3902 La factura especificada no pertenece al titular registrado en el sistema.
	- 4104 / 4123 El NIF del titular o del representante no esta identificado en el censo.
	- 4109 / 1116 / 1117 El NIF o el ID del emisor de la factura recibida no esta
	       identificado en el censo.
	- 4114 / 4130 / 4131 / 4132 / 4133 El titular del certificado debe ser el titular del
	       libro de registro, colaborador social, apoderado o sucesor.
*/
