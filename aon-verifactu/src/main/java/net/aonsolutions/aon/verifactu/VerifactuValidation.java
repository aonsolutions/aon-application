package net.aonsolutions.aon.verifactu;

import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CalificacionOperacionType.N_1;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CalificacionOperacionType.N_2;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CalificacionOperacionType.S_1;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CalificacionOperacionType.S_2;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType.F_1;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType.F_2;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType.F_3;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType.R_1;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType.R_2;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType.R_3;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType.R_4;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType.R_5;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.OperacionExentaType.E_2;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.OperacionExentaType.E_3;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.OperacionExentaType.E_4;
import static https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.OperacionExentaType.E_5;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonObjectUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.consultalr.ConsultaFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CalificacionOperacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoRectificativaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CompletaSinDestinatarioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CountryType2;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CuponType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DetalleType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.GeneradoPorType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaARType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDOtroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.MacrodatoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.OperacionExentaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAnulacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SimplificadaCualificadaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SistemaInformaticoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SubsanacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.TercerosODestinatarioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;

public class VerifactuValidation {
	 
	private static final Date DATE_01_07_2022 = AonDateUtils.getDate(2022,6,1); 	// 1 de julio de 2022
	private static final Date DATE_31_12_2022 = AonDateUtils.getDate(2022,11,31); 	// 31 de diciembre de 2022
	private static final Date DATE_01_01_2023 = AonDateUtils.getDate(2023,0,1); 	// 1 de enero de 2023 
	private static final Date DATE_30_09_2024 = AonDateUtils.getDate(2024,8,30); 	// 30 de septiembre de 2024
	
	private static final Date DATE_01_10_2024 = AonDateUtils.getDate(2024,9,1); 	// 1 de octubre de 2024 
	private static final Date DATE_31_10_2024 = AonDateUtils.getDate(2024,9,31); 	// 31 de diciembre de 2024
	private static final Date DATE_31_12_2024 = AonDateUtils.getDate(2024,11,31); 	// 31 de diciembre de 2024
	
	private VerifactuValidation() {
		
	}
	
	private static class ValidatorContext {
		final RegFactuSistemaFacturacion verifactu;
		final RegistroFacturaType fraType;
		final Invoice invoice;

		ValidatorContext(RegFactuSistemaFacturacion message, RegistroFacturaType fraType, Invoice invoice) {
			this.verifactu = message;
			this.fraType = fraType;
			this.invoice = invoice;
		}
		
		Optional<RegistroFacturacionAltaType> getAlta() {
			return Optional.ofNullable( fraType.getRegistroAlta() );
		}
		Optional<RegistroFacturacionAnulacionType> getAnulacion() {
			return Optional.ofNullable( fraType.getRegistroAnulacion() );
		}
		void addError( InvoiceCommunicationError error) {
			this.invoice.addMessage( InvoiceErrorMessages.C050.err(InvoiceErrorKey.COMMUNICATION,error.getCode(),error.getMessage()));
		}
	}
	
	// *********************************************************
	// *************** [VALIDACION CABECERA] *******************
	// *********************************************************
	public static void validateHeader(ConsultaFactuSistemaFacturacionType message) throws InvoiceCommunicationException {
		try {
			CABECERA_OBLIGADO_EMISION_QUERY
				.accept(message);
		} catch( AonCoreException e) {
			if (e.getCause() instanceof InvoiceCommunicationException ice) {
				throw ice;
			}
			throw e;
		}
	}
	
	/**
	 * 1. ObligadoEmision
	 * 
	 * 	- El dato Cabecera >> ObligadoEmision >> NIF debe existir y ser válido.
	 */
	private static final Consumer<ConsultaFactuSistemaFacturacionType> CABECERA_OBLIGADO_EMISION_QUERY = v -> {
		if (AonStringUtils.isBlank(v.getCabecera().getObligadoEmision().getNIF())) {
			throw new AonCoreException(new InvoiceCommunicationException(InvoiceCommunicationError.VERIFACTU_4104));
		} else if (!AonDocumentUtil.isValid(v.getCabecera().getObligadoEmision().getNIF())) {
			throw new AonCoreException(new InvoiceCommunicationException(InvoiceCommunicationError.VERIFACTU_4116));
		}
	};
	
	// *********************************************************
	// *************** [VALIDACION CABECERA] *******************
	// *********************************************************
	public static void validateHeader(RegFactuSistemaFacturacion message) throws InvoiceCommunicationException {
		try {
			CABECERA_OBLIGADO_EMISION
				.andThen(CABECERA_REPRESENTANTE)
				.accept(message);
		} catch( AonCoreException e) {
			if (e.getCause() instanceof InvoiceCommunicationException ice) {
				throw ice;
			}
			throw e;
		}
	}
	
	/**
	 * 1. ObligadoEmision
	 * 
	 * 	- El dato Cabecera >> ObligadoEmision >> NIF debe existir y ser válido.
	 */
	private static final Consumer<RegFactuSistemaFacturacion> CABECERA_OBLIGADO_EMISION = v -> {
		if (AonStringUtils.isBlank(v.getCabecera().getObligadoEmision().getNIF())) {
			throw new AonCoreException(new InvoiceCommunicationException(InvoiceCommunicationError.VERIFACTU_4104));
		} else if (!AonDocumentUtil.isValid(v.getCabecera().getObligadoEmision().getNIF())) {
			throw new AonCoreException(new InvoiceCommunicationException(InvoiceCommunicationError.VERIFACTU_4116));
		}
	};
	
	/**
	 * 2. Representante
	 * 
	 * 	- El dato Cabecera >> Representante >> NIF  debe ser válido.
	 * 	- El NIF del representante/asesor del obligado a expedir (emitir) facturas asociado a la remisión debe estar identificado en la AEAT.
	 * 
	 */
	private static final Consumer<RegFactuSistemaFacturacion> CABECERA_REPRESENTANTE = v -> {
		if (v.getCabecera().getRepresentante() != null) {
			if (AonStringUtils.isBlank(v.getCabecera().getRepresentante().getNIF())) {
				throw new AonCoreException(new InvoiceCommunicationException(InvoiceCommunicationError.VERIFACTU_4105));
			} else if (!AonDocumentUtil.isValid(v.getCabecera().getRepresentante().getNIF())) {
				throw new AonCoreException(new InvoiceCommunicationException(InvoiceCommunicationError.VERIFACTU_4117));
			}
		}
	};
	
	
		/*

			3.FechaFinVeriFactu
				-Sólo se permite contenido en sistemas que emite facturas verificables.
				-La fecha debe tener el formato 31-12-20XX.
				-El año de la fecha deberá ser igual al año de la fecha del sistema de la AEAT, o al año anterior (para admitir casos excepcionales y puntuales que pudieran darse a finales de un año y comienzo del siguiente).
			4.Incidencia
				-Sólo se permite contenido en sistemas que emite facturas verificables.
			5.RefRequerimiento
				-Sólo se permite contenido en sistemas que emiten facturas no verificables.
				-Obligatorio en sistemas que emiten facturas no verificables.
				-La referencia del requerimiento deberá existir en la AEAT.
				 
		*/
	
	
	
	public static void validate(RegFactuSistemaFacturacion fras, RegistroFacturaType fraType,Invoice invoice ) {
		ValidatorContext v = new ValidatorContext(fras, fraType, invoice);
		v.getAlta().ifPresent( alta -> validateAlta(v,alta));
		v.getAnulacion().ifPresent( anulacion-> validateAnulacion(v, anulacion));
	}
	
	
	// *********************************************************
	// *************** [VALIDACION REGISTRO ALTA] **************
	// *********************************************************
	private static record AltaContext(ValidatorContext vc, RegistroFacturacionAltaType fra) {}
	public static void validateAlta(ValidatorContext vc, RegistroFacturacionAltaType alta) {
			ALTA_FRA_NIF
			.andThen(ALTA_FRA_FECHA)
			.andThen(ALTA_FRA_FECHA_EXP)
			.andThen(ALTA_FRA_NUM_SERIE)
			.andThen(ALTA_FRA_RECHAZO_PREVIO)
			.andThen(ALTA_FRA_TIPO_RECTIFICATIVA)
			.andThen(ALTA_FRA_AGRUPACION_RECTIFICADAS)
			.andThen(ALTA_FRA_AGRUPACION_RECTIFICADAS_IDS)
			.andThen(ALTA_FRA_AGRUPACION_SUSTITUIDAS)
			.andThen(ALTA_FRA_AGRUPACION_RECTIFICACION)
			.andThen(ALTA_FRA_FECHA_OPERACION)
			.andThen(ALTA_FRA_SIMPLIFICADA_ART_7273)
			.andThen(ALTA_FRA_SIN_IDENTIF_DESTINATARIO_ART61D)
			.andThen(ALTA_FRA_MACRODATO)
			.andThen(ALTA_FRA_EMITIDA_POR_TERCERO)
			.andThen(ALTA_FRA_AGRUPACIÓN_TERCERO)
			.andThen(ALTA_FRA_AGRUPACIÓN_DESTINATARIOS)
			.andThen(ALTA_FRA_CUPON)
			.andThen(ALTA_FRA_DESGLOSE)
			.andThen(ALTA_FRA_DESGLOSE_SUM_SIMPLIFICADAS)
			.andThen(ALTA_FRA_CUOTA_TOTAL)
			.andThen(ALTA_FRA_IMPORTE_TOTAL)
			.andThen(ALTA_FRA_HUELLA)
			.andThen(ALTA_FRA_SISTEMA_INFORMATICO_ID)
			.andThen(ALTA_FRA_SISTEMA_INFORMATICO_OTHERS)
		.accept(new AltaContext(vc,alta));
	}
	
	/**
	 * 1. Agrupación IDFactura
	 * 
	 * 	 - El NIF del campo IDEmisorFactura debe ser el mismo que el del campo NIF
	 * 	   de la agrupación ObligadoEmision del bloque Cabecera.
	 */
	private static final Consumer<AltaContext> ALTA_FRA_NIF = a -> {
		if (AonStringUtils.notEquals(
			a.vc.verifactu.getCabecera().getObligadoEmision().getNIF(),
			a.fra.getIDFactura().getIDEmisorFactura())) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1108);
		}
	};

	/**
	 * 1. Agrupación IDFactura
	 * 
	 * 	 - La FechaExpedicionFactura no podrá ser superior a la fecha actual.
	 */
	private static final Consumer<AltaContext> ALTA_FRA_FECHA = a -> {
		Date expDate = VerifactuUtils.toDate(a.fra.getIDFactura().getFechaExpedicionFactura());
		if (expDate == null) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1105);
		}
		if (AonDateUtils.isAfter(expDate, AonDateUtils.today())) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1112);
		}
	};
	
	/*
	 * 1. Agrupación IDFactura
	 * 
	 * - La FechaExpedicionFactura no debe ser inferior a 28/10/2024 (fecha de entrada en vigor de la Orden Ministerial de VERI*FACTU).
	 */
	private static final Consumer<AltaContext> ALTA_FRA_FECHA_EXP = a -> {
		String fecha = a.fra.getIDFactura().getFechaExpedicionFactura();
		if (AonStringUtils.isNotBlank(fecha)) {
			Date expDate = VerifactuUtils.toDate(fecha);
			Date limit = AonDateUtils.getDate(2024,9,28);
			if (expDate.before(limit) ) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1152);
			}
		}
	};
	
	/*
	 * 1. Agrupación IDFactura
	 *
	 *	- NumSerieFactura solo puede contener caracteres ASCII del 32 a 126 (caracteres imprimibles)
	 */
	private static final Consumer<AltaContext> ALTA_FRA_NUM_SERIE = a -> {
		String numSerie = a.fra.getIDFactura().getNumSerieFactura();
		if (AonStringUtils.isBlank(numSerie)) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1104);
		} else if (!AonStringUtils.isAsciiPrintable(numSerie)) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1130);
		} else if (AonStringUtils.containsAny(numSerie, '"','\'','<','>','=')) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1130);
		}
		
		
	};
	
	/*
	 * 2. RechazoPrevio
	 */
	private static final Consumer<AltaContext> ALTA_FRA_RECHAZO_PREVIO = a -> {
		// - Solo podrá incluirse el campo RechazoPrevio con valor "X" si se ha informado el campo Subsanacion y tiene el valor "S".
		if (a.fra.getRechazoPrevio() == RechazoPrevioType.X && a.fra.getSubsanacion() != SubsanacionType.S) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1153);
		}
		// - No podrá informarse el campo RechazoPrevio con valor "S" si no se informa el campo Subsanación o éste tiene el valor "N"
		if (a.fra.getRechazoPrevio() == RechazoPrevioType.S && (a.fra.getSubsanacion() == null || a.fra.getSubsanacion() == SubsanacionType.N)) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1161);
		}
	};
	
	/*
	 * 	3. TipoRectificativa
	 */
	private static final Consumer<AltaContext> ALTA_FRA_TIPO_RECTIFICATIVA = a -> {
		if (a.fra.getTipoFactura() == null) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1106);
		} else {
			if (AonEnumUtils.in(a.fra.getTipoFactura(), R_1, R_2, R_3, R_4, R_5)) {
				// - Campo obligatorio si TipoFactura es igual a "R1", "R2", "R3", "R4" o "R5".
				if (a.fra.getTipoRectificativa() == null) {
					a.vc.addError(InvoiceCommunicationError.VERIFACTU_1114);
				}
			} else {
				// - Solo podrá incluirse el campo TipoRectificativa si el valor del campo 
				//	 TipoFactura es igual a "R1", "R2", "R3", "R4" o "R5"
				if (a.fra.getTipoRectificativa() != null) {
					a.vc.addError(InvoiceCommunicationError.VERIFACTU_1115);
				}
			}
		}
	};
	
	/*
	 * 4. Agrupación FacturasRectificadas
	*/
	private static final Consumer<AltaContext> ALTA_FRA_AGRUPACION_RECTIFICADAS = a -> {
		// - Sólo podrá incluirse esta agrupación (no es obligatoria) si TipoFactura es igual a "R1", "R2","R3", "R4" o "R5". 
		if (a.fra.getFacturasRectificadas() != null && !AonEnumUtils.in(a.fra.getTipoFactura(), R_1, R_2, R_3, R_4, R_5)) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1117);
		}
	};
	private static final Consumer<AltaContext> ALTA_FRA_AGRUPACION_RECTIFICADAS_IDS = a -> {
		if (AonEnumUtils.in(a.fra.getTipoFactura(), R_1, R_2, R_3, R_4, R_5)) {
			RegistroFacturacionAltaType.FacturasRectificadas fras = a.fra.getFacturasRectificadas();
			if (fras == null || AonCollectionUtils.isEmpty(fras.getIDFacturaRectificada())) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_4102);
			} else {
				for ( IDFacturaARType id : fras.getIDFacturaRectificada()) {
					if (id.getIDEmisorFactura() == null
					 || id.getFechaExpedicionFactura() == null
					 || id.getNumSerieFactura() == null) {
						a.vc.addError(InvoiceCommunicationError.VERIFACTU_4102);
					}
				}
			}
		}
	};
	
	/*
	 * 5. Agrupación FacturasSustituidas
	*/
	private static final Consumer<AltaContext> ALTA_FRA_AGRUPACION_SUSTITUIDAS = a -> {
		// - Sólo podrá incluirse esta agrupación (no es obligatoria) cuando el campo TipoFactura="F3". 
		if (a.fra.getFacturasSustituidas() != null && a.fra.getTipoFactura() != F_3) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1116);
		}
	};
	
	/*
	 * 6. Agrupación ImporteRectificacion
	*/
	private static final Consumer<AltaContext> ALTA_FRA_AGRUPACION_RECTIFICACION = a -> {
		// - Obligatorio si TipoRectificativa = "S".
		if (a.fra.getImporteRectificacion() == null && a.fra.getTipoRectificativa() == ClaveTipoRectificativaType.S) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1118);
		}
		// - Sólo deberá incluirse esta agrupación si el campo TipoRectificativa = "S".
		if (a.fra.getImporteRectificacion() != null && a.fra.getTipoRectificativa() != ClaveTipoRectificativaType.S) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1119);
		}
	};
	
	/*
	 * 7. FechaOperacion
	*/
	private static final Consumer<AltaContext> ALTA_FRA_FECHA_OPERACION = a -> {
		// - La FechaOperacion no debe ser inferior a la fecha actual menos veinte años y no debe ser superior al año siguiente de la fecha actual.
		Date today = new Date();
		Date fechaOperacion = VerifactuUtils.toDate(a.fra.getFechaOperacion());
		if (AonStringUtils.isNotBlank(a.fra.getFechaOperacion())) {
			Date minDate = AonDateUtils.addYears(today, -20);
			if (fechaOperacion.before(minDate)) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1134);	
			}
			Date maxDate = AonDateUtils.addYears(today, 1);
			if (fechaOperacion.after(maxDate)) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1125);
			}
		}
	};

	/*
	 * 8. FacturaSimplificadaArt7273
	*/
	private static final Consumer<AltaContext> ALTA_FRA_SIMPLIFICADA_ART_7273 = a -> {
		// - El campo FacturaSimplificadaArticulos7273 solo acepta valores N o S.
		if ( a.fra.getFacturaSimplificadaArt7273() == null) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1136);
		}
		//	- Sólo se podrá rellenar con "S" si TipoFactura="F1" o "F3" o "R1" o "R2" o "R3" o "R4".
		if ( a.fra.getFacturaSimplificadaArt7273() == SimplificadaCualificadaType.S
			&& AonEnumUtils.notIn(a.fra.getTipoFactura(), F_1, F_3, R_1, R_2, R_3, R_4)) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1183);
		}
	};

	/*
	 * 9. FacturaSinIdentifDestinatarioArt61d
	*/
	private static final Consumer<AltaContext> ALTA_FRA_SIN_IDENTIF_DESTINATARIO_ART61D = a -> {
		// El campo FacturaSinIdentifDestinatarioArt61d solo acepta valores S o N.
		if (a.fra.getFacturaSinIdentifDestinatarioArt61D() == null) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1184);
		}
		// - Sólo se podrá rellenar con "S" si TipoFactura="F2" o "R5".
		if (a.fra.getFacturaSinIdentifDestinatarioArt61D() == CompletaSinDestinatarioType.S
			&& AonEnumUtils.notIn(a.fra.getTipoFactura(), F_2, R_5)) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1185);
		}
	};

	/*
	 * 10. Macrodato
	*/
	private static final Consumer<AltaContext> ALTA_FRA_MACRODATO = a -> {
		double d = VerifactuUtils.todouble( a.fra.getImporteTotal());
		// El campo Macrodato solo acepta valores N o S.
		if (a.fra.getMacrodato() == null) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1137);
		}
		// - El campo Macrodato solo debe ser informado con valor S si el valor de ImporteTotal es igual o superior a +-100.000.000
		if ( AonMathUtils.absRounded(d) < 100000000.00
		  && a.fra.getMacrodato() == MacrodatoType.S )  {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1138);
		}
	};
	
	/*
	 * 11. EmitidaPorTerceroODestinatario
	*/
	private static final Consumer<AltaContext> ALTA_FRA_EMITIDA_POR_TERCERO = a -> {
		// "El campo EmitidaPorTerceroODestinatario solo acepta valores T o D."),
		if ( a.fra.getEmitidaPorTerceroODestinatario() == null) {
			// a.vc.addError(InvoiceCommunicationError.VERIFACTU_1151);
			if ( a.fra.getTercero() != null) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1155);
			}
		} else {
			// - Si es igual a "T", el bloque Tercero será de cumplimentación obligatoria.
			if (a.fra.getEmitidaPorTerceroODestinatario() == TercerosODestinatarioType.T) {
				if ( a.fra.getTercero() == null ) {
					a.vc.addError(InvoiceCommunicationError.VERIFACTU_1186);
				}
			} else {
				//- Solo podrá cumplimentarse si EmitidaPorTerceroODestinatario es "T".
				if ( a.fra.getTercero() != null ) {
					a.vc.addError(InvoiceCommunicationError.VERIFACTU_1187);
				}
			}
		}
	};

	/*
	 * 12. Agrupación Tercero
	*/
	private static final Consumer<AltaContext> ALTA_FRA_AGRUPACIÓN_TERCERO = a -> {
		PersonaFisicaJuridicaType tercero = a.fra.getTercero();
		if ( a.fra.getTercero() != null ) {
			//- Si se identifica mediante NIF, el NIF debe estar identificado y ser distinto 
			//  del NIF del campo IDEmisorFactura de la agrupación IDFactura.
			if (AonStringUtils.isNotBlank(tercero.getNIF())) {
				if ( AonStringUtils.equals(tercero.getNIF(), a.fra.getIDFactura().getIDEmisorFactura())) {
					a.vc.addError(InvoiceCommunicationError.VERIFACTU_1188);
				}
				if ( tercero.getIDOtro() != null) {
					//- Si se cumplimenta NIF, no deberá existir la agrupación IDOtro y viceversa, pero es 
					//		obligatorio que se cumplimente uno de los dos.
					a.vc.addError(InvoiceCommunicationError.VERIFACTU_1211);
				}
			} else if ( tercero.getIDOtro() != null) {
				checkIDOtroNo07( a.vc, a.fra, tercero.getIDOtro() );
			}
		}
	};
	
	/*
	 * 13. Agrupación Destinatarios
	*/
	private static final Consumer<AltaContext> ALTA_FRA_AGRUPACIÓN_DESTINATARIOS = a -> {
		if (AonEnumUtils.in(a.fra.getTipoFactura(), F_2, R_5)) {
			// - Si TipoFactura es "F2" o "R5", la agrupación Destinatarios no puede estar cumplimentada.
			if (a.fra.getDestinatarios() != null && AonCollectionUtils.isNotEmpty(a.fra.getDestinatarios().getIDDestinatario())) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1190);
			}
		} else {
			// - Si TipoFactura es "F1", "F3", "R1", "R2", "R3" o "R4", la agrupación Destinatarios tiene que estar cumplimentada, con al menos un destinatario.
			if (a.fra.getDestinatarios() == null || AonCollectionUtils.isEmpty(a.fra.getDestinatarios().getIDDestinatario())) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1189);
			} else {
				for ( PersonaFisicaJuridicaType destinatario : a.fra.getDestinatarios().getIDDestinatario() ) {
					// - Si se cumplimenta NIF, no deberá existir la agrupación IDOtro y viceversa, pero es 
					// 	 obligatorio que se cumplimente uno de los dos.
					if ((AonStringUtils.isNotBlank(destinatario.getNIF()) && destinatario.getIDOtro() != null ) 
					 || (AonStringUtils.isBlank(destinatario.getNIF()) && destinatario.getIDOtro() == null )) {
						a.vc.addError(InvoiceCommunicationError.VERIFACTU_1239);
					} else if (AonStringUtils.isNotBlank(destinatario.getNIF())) {
						if (!AonDocumentUtil.isValid(destinatario.getNIF())) {
							a.vc.addError(InvoiceCommunicationError.VERIFACTU_1123);	
						}
					} else if (destinatario.getIDOtro() != null) {
						IDOtroType idOtro = destinatario.getIDOtro();
						// - Si el campo IDType = "07" (No censado), el campo CodigoPais debe ser "ES".					
						if (AonStringUtils.equals("07", idOtro.getIDType())) {
							if (!isSpain(idOtro.getCodigoPais())) {
								a.vc.addError(InvoiceCommunicationError.VERIFACTU_1126);
							}
						} else if (AonStringUtils.equals("02", idOtro.getIDType())) {
							// - Si el campo IDType = "02" (NIF-IVA), no será exigible el campo CodigoPais.
							// - Cuando uno o varios destinatarios se identifiquen a través de la agrupación IDOtro e IDType sea "02", se validará que el campo identificador se ajuste a la estructura de NIF-IVA de alguno de los Estados Miembros y debe estar identificado. Ver nota (1).
							checkIDOtro02(a.vc, idOtro);
						} else {
							if (idOtro.getCodigoPais() == null) {
								//- Si el campo IDType = "02" (NIF-IVA), no será exigible el campo CodigoPais.
								a.vc.addError(InvoiceCommunicationError.VERIFACTU_1111);
							} else {
								// - Cuando uno o varios destinatarios se identifiquen a través de la agrupación IDOtro 
								//   y CodigoPais sea "ES", se validará que el campo IDType sea "03" o "07".
								if (AonStringUtils.equals(idOtro.getIDType(),"03") && !isSpain(idOtro.getCodigoPais())) {
									a.vc.addError(InvoiceCommunicationError.VERIFACTU_1126);
								}
							}
						}
					}
					
				}
			}
		}
	};

	/*
	 * 14. Cupón
	*/
	private static final Consumer<AltaContext> ALTA_FRA_CUPON = a -> {
		// - Sólo se podrá rellenar con "S" (no es obligatorio) si TipoFactura = "R5" o "R1"
		if (a.fra.getCupon() == CuponType.S && AonEnumUtils.notIn(a.fra.getTipoFactura(), R_1, R_5)) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1157);
		}
	};

	
	private static record DegloseContext(ValidatorContext v, RegistroFacturacionAltaType fra, DetalleType det) {}
	
	/*
	 * 15. Agrupación Desglose / DetalleDesglose.
	*/
	/*
	 * 15.1 TipoImpositivo
	*/

	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_TIPOIMPOSITIVO = ctx -> {
		if (ctx.det.getCalificacionOperacion() == S_1) {
			//	Si CalificacionOperacion es S1 y BaseImponibleACoste no est\u00E1 cumplimentada,
			//	TipoImpositivo y CuotaRepercutida son obligatorios.
			if (ctx.det.getBaseImponibleACoste() == null
				&& ( AonStringUtils.isBlank(ctx.det.getTipoImpositivo()) || AonStringUtils.isBlank(ctx.det.getCuotaRepercutida())) ) {
				ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1208);
			} else if (isIVA(ctx.det.getImpuesto())) { 
				// - Si Impuesto = "01" (IVA) o no se cumplimenta (considerándose "01" - IVA) y CalificacionOperacion = "S1":
				Date fechaOperacion = VerifactuUtils.toDate(ctx.fra.getFechaOperacion());
				String tipo = ctx.det.getTipoImpositivo();
				// - Solo se permiten TipoImpositivo = 0; 2; 4; 5; 7,5; 10 y 21 (valores que indican el tanto por ciento).
				if (AonStringUtils.notIn(tipo, "0","2","4","5","7.5","10","21")) {
					ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1124);
				} else if (AonStringUtils.equals(tipo,"2")) {
					// - Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se 
					//	informa FechaOperacion) >= 1 de octubre de 2024 y <= 31 de diciembre de 2024 se admitirá el TipoImpositivo = 2
					if (!AonDateUtils.isBetween(fechaOperacion, DATE_01_10_2024,DATE_31_12_2024)) {
						ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1235);	
					}
				} else if (AonStringUtils.equals(tipo,"5")) {
					// - Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se 
					//	informa FechaOperacion) >= 1 de julio de 2022 y <= 30 de septiembre de 2024 se admitirá TipoImpositivo = 5.
					if (!AonDateUtils.isBetween(fechaOperacion, DATE_01_07_2022,DATE_30_09_2024)) {
						ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1194);	
					}
				} else if (AonStringUtils.equals(tipo,"7.5")) {
					// - Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se 
					//	informa FechaOperacion) >= 1 de octubre de 2024 y <= 31 de diciembre de 2024 se admitirá el TipoImpositivo = 7,5.	
					if (!AonDateUtils.isBetween(fechaOperacion, DATE_01_10_2024,DATE_31_12_2024)) {
						ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1235);	
					}
				}
			}
		}
	};
	
	/*
	 * 15.2 BaseImponibleACoste
	*/
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_BASE_IMPONIBLE_A_COSTE = ctx -> {
		if (AonStringUtils.isNotBlank(ctx.det.getBaseImponibleACoste())) {
			// - El campo BaseImponibleACoste solo puede estar cumplimentado si la 
			// 		ClaveRegimen es = "06" o Impuesto = "02" (IPSI) o Impuesto = "05" (Otros).
			if (AonStringUtils.notEquals(ctx.det.getClaveRegimen(),"06")
			  && AonStringUtils.notIn(ctx.det.getImpuesto(), "02", "05")) {
				ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1257);
			}
		}
	};

	/*
	 * 15.3 TipoRecargoEquivalencia
	*/
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_TIPO_RECARGO_EQUIVALENCIA = ctx -> {
		if (isIVA(ctx.det.getImpuesto())) {
			String reTipo = ctx.det.getTipoRecargoEquivalencia();
			String reQuota = ctx.det.getCuotaRecargoEquivalencia();
			String clave = ctx.det.getClaveRegimen();
			if ( AonStringUtils.equals(ClaveRegimen.C18.getValue(), clave) ) {
				if (reTipo == null || reQuota == null) {
					//	Si el impuesto es IVA(01) o vacio y se asigna ClaveRegimen a 18 es obligatorio informar 
					//	TipoRecargoEquivalencia y CuotaRecargoEquivalencia.
					ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1280);
				} else if (ctx.det.getCalificacionOperacion() != S_1) {
					// Solo se puede cumplimentar TipoRecargoEquivalencia y CuotaRecargoEquivalencia cuando 
					// CalificacionOperacion es 'S1'"),
					ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1281);
				} else {
					String tipo = ctx.det.getTipoImpositivo();
					Date fechaOperacion = VerifactuUtils.toDate(ctx.fra.getFechaOperacion());
					// -- Solo se permiten TipoRecargoEquivalencia = 0; 0,26; 0,5; 0,62; 1; 1,4; 1,75; 5,2 (valores que indican el tanto por ciento).
					if (AonStringUtils.notIn(reTipo, "0", "0.26", "0.5", "0.62", "1", "1.4", "1.75", "5.2")) {
						ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1127);
					} else if (AonStringUtils.equals(reTipo,"0")) {
						// - Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se informa FechaOperacion) 
						//	es mayor o igual que 1 de enero de 2023 y menor o igual que 30 de septiembre de 2024, 
						//	solo se admitirá TipoRecargoEquivalencia = 0.
						if (!AonDateUtils.isBetween(fechaOperacion, DATE_01_01_2023,DATE_30_09_2024)) {
							ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1165);	
						}
					} else if (AonStringUtils.equals(reTipo,"0.26")) {
						//- Si TipoImpositivo es 2 sólo se admitirá TipoRecargoEquivalencia = 0,26.
						//	Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se informa 
						//  FechaOperacion) es mayor o igual que 1 de octubre de 2024 y menor o igual que 31 de diciembre de 2024 
						//  se admitirá el TipoRecargoEquivalencia = 0,26. 
						if (AonStringUtils.equals(tipo,"2")) {
							if (!AonDateUtils.isBetween(fechaOperacion, DATE_01_10_2024,DATE_31_12_2024)) {
								ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1166);
							}
						} else if (AonStringUtils.equals(tipo,"0")) {
							// Si el TipoImpositivo es 0%, desde el 1 de octubre del 2024, s\u00F3lo se admite TipoRecargoEquivalencia 0,26.
							if (AonDateUtils.isBefore(fechaOperacion, DATE_01_10_2024 )) {
								ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1170);
							}
						} else {
							ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1127);
						}
					} else if (AonStringUtils.equals(reTipo,"0.5")) {
						// - Si tipo impositivo es 5:
						if (AonStringUtils.equals(tipo,"5")) {
							//	Si el TipoImpositivo es 5% solo se admite TipoRecargoEquivalencia 0,5 si Fecha 
							//	Operacion (Fecha Expedicion Factura si no se informa FechaOperacion) es mayor o 
							//	igual que el 1 de julio de 2022 y el 31 de diciembre de 2022.
							if(!AonDateUtils.isBetween(fechaOperacion, DATE_01_07_2022,DATE_31_12_2022)) {
								ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1167);
							}
						} else if (AonStringUtils.notEquals(tipo,"4")) {
							// - Si TipoImpositivo es 4 sólo se admitirá TipoRecargoEquivalencia = 0,5.
							ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1164);
						}
					} else if (AonStringUtils.equals(reTipo,"0.62")) {
						//- Si tipo impositivo es 5:
						//	Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se informa 
						//	FechaOperacion) es mayor o igual que 1 de enero de 2023 y menor o igual que 30 de septiembre de 2024, 
						//	solo se admitirá TipoRecargoEquivalencia = 0,62.
						if (AonStringUtils.notEquals(tipo,"5")
							|| !AonDateUtils.isBetween(fechaOperacion, DATE_01_01_2023,DATE_30_09_2024)) {
							ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1168);
						}
					} else if (AonStringUtils.equals(reTipo,"1")) {
						// - Si TipoImpositivo es 7.5 sólo se admitirá TipoRecargoEquivalencia = 1.	
						if (AonStringUtils.notEquals(tipo,"7.5")
							|| !AonDateUtils.isBetween(fechaOperacion, DATE_01_10_2024,DATE_31_10_2024)) {
							ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1169);
						}
					} else if (AonStringUtils.equals(reTipo,"1.4")) {
						// - Si TipoImpositivo es 10 sólo se admitirá TipoRecargoEquivalencia = 1,4.	
						if (AonStringUtils.notEquals(tipo,"10")) {
							ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1163);
						}
					} else if (AonStringUtils.equals(reTipo,"1.75")) {
						// - Si TipoImpositivo es 21 sólo se admitirán TipoRecargoEquivalencia = 5,2 ó 1,75.
						if (AonStringUtils.notEquals(tipo,"21")) {
							ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1162);
						}
					} else if (AonStringUtils.equals(reTipo,"5.2")) {
						// - Si TipoImpositivo es 21 sólo se admitirán TipoRecargoEquivalencia = 5,2 ó 1,75.
						if (AonStringUtils.notEquals(tipo,"21")) {
							ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1162);
						}
					}
				}
			} else {
				//	Si el impuesto es IVA(01) o vacio, solo se podr\u00E1 informar TipoRecargoEquivalencia 
				//	y CuotaRecargoEquivalencia si ClaveRegimen es 18.
				if (reTipo != null || reQuota != null) {
					ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1279);
				}
			}
		}
	};
	
	/*
	* 	15.4 CalificacionOperacion
	*
	*/
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_CALIFICACION_OPERACION = ctx -> {
		// - Si CalificacionOperacion es "S2", 
		if (ctx.det.getCalificacionOperacion() == S_2) {
			// - Si CalificacionOperacion es "S2", TipoFactura solo puede ser "F1", "F3", "R1", "R2", "R3" y "R4".
			if (!AonEnumUtils.in( ctx.fra.getTipoFactura(), F_1, F_3, R_1, R_2, R_3, R_4) ) {
				ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1197);
			} else {
				//	- Cuando CalificacionOperacion sea "S2":
				//		- TipoImpositivo = 0. (No se admite que vaya vacío o que el campo no exista).
				//		- CuotaRepercutida = 0. (No se admite que vaya vacío o que el campo no	exista ). 
				if ( AonStringUtils.notEquals("0",ctx.det.getTipoImpositivo()) || AonStringUtils.notEquals("0",ctx.det.getCuotaRepercutida())) {
					ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1198);
				}
			}
		} else if (AonEnumUtils.in( ctx.det.getCalificacionOperacion(), N_1, N_2) && isIVA( ctx.det.getImpuesto()) ) {
			// - Si CalificacionOperacion es = "N1/N2" e Impuesto = "01" (IVA) o no se cumplimenta (considerándose "01" - IVA), 
			//  	no se puede informar ninguno de estos campos: TipoImpositivo, CuotaRepercutida, TipoRecargoEquivalencia, CuotaRecargoEquivalencia.
			if ( AonStringUtils.isNotBlank(ctx.det.getTipoImpositivo()) || AonStringUtils.isNotBlank(ctx.det.getCuotaRepercutida()) 
			  || ctx.det.getTipoRecargoEquivalencia() != null || ctx.det.getCuotaRecargoEquivalencia() != null) {
				ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1237);	
			}
		}
	};

	/*
	* 	15.5 OperacionExenta.
		- Si Impuesto = "01" (IVA) o no se cumplimenta (considerándose "01" - IVA), el valor de OperacionExenta deberá estar contenido en lista L10.
		- Si Impuesto = "03" (IGIC), el valor de OperacionExenta deberá estar contenido en lista L10 y adicionalmente podrá contener los valores "E7" y "E8".
	*/
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_OPERACION_EXENTA = ctx -> {
		OperacionExentaType opExenta = ctx.det.getOperacionExenta();
		CalificacionOperacionType calOperacion = ctx.det.getCalificacionOperacion();
		// OperacionExenta o CalificacionOperacion no pueden ser ambos informados ya que son excluyentes entre si.
		if ( opExenta != null && calOperacion != null) {
			ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1196);
			
		// Al menos uno de los dos campos OperacionExenta o CalificacionOperacion deben estar informados.
		} else if ( opExenta == null && calOperacion == null) {
			ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1195);
			
		// - Si el campo OperacionExenta está cumplimentado no se pueden informar ninguno de estos campos: TipoImpositivo, CuotaRepercutida, TipoRecargoEquivalencia y CuotaRecargoEquivalencia.
		} else if ( opExenta != null 
			&& ( AonStringUtils.isNotBlank(ctx.det.getTipoImpositivo()) || AonStringUtils.isNotBlank(ctx.det.getCuotaRepercutida()) 
			  || ctx.det.getTipoRecargoEquivalencia() != null || ctx.det.getCuotaRecargoEquivalencia() != null)) {
			ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1238);

		// - Si Impuesto = "01" (IVA), "03" (IGIC) o no se cumplimenta (considerándose "01" - IVA), y ClaveRegimen es 
		//   igual a "01", no pueden marcarse los valores de OperacionExenta "E2"	y "E3"	
		} else if (isIVAoIGIC(ctx.det.getImpuesto()) 
			&& AonStringUtils.equals(ctx.det.getClaveRegimen(),"01") 
			&& AonEnumUtils.in( opExenta , E_2, E_3)) {
				ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1199);
		} 
	};
	
	/*
	* 	15.6 ClaveRegimen
	*	- Si Impuesto = "01" (IVA) o no se cumplimenta (considerándose "01" - IVA), el valor de ClaveRegimen deberá estar cumplimentado y 
	*	  contenido en lista L8A. 
	*	- Si Impuesto = "03" (IGIC), el valor de ClaveRegimen deberá estar cumplimentado y contenido en lista L8B. Adicionalmente, 
	*	  puede contener el valor "20" (Operaciones sujetas al IPSI).
	*/
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_CLAVE_REGIMEN = ctx -> {
		// - Solo podrá incluirse este campo si Impuesto = "01" (IVA), "03" (IGIC) o no se cumplimenta (considerándose "01" - IVA) y será obligatorio. 
		if ( isIVAoIGIC(ctx.det.getImpuesto()) ) {
			if ( AonStringUtils.isBlank(ctx.det.getClaveRegimen())) {
				ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1245);	
			} else if (ClaveRegimen.isNotValid( ctx.det.getClaveRegimen() )) {
				ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1246);
			}
		} else {
			if ( AonStringUtils.isNotBlank(ctx.det.getClaveRegimen())) {
				ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1260);	
			}
		}
	};
	
	/*
	 *	15.6.1 ClaveRegimen 02. Exportación.
	 * 		- Si Impuesto = "01" (IVA), "03" (IGIC) o no se cumplimenta (considerándose "01" - IVA), si clave de 
	 * 		  ClaveRegimen es igual a "02", solo puede estar cumplimentado OperacionExenta.
	 */
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_02_EXPORTACION = ctx -> {
		if ( AonStringUtils.equals("02",ctx.det.getClaveRegimen())
			&& isIVAoIGIC(ctx.det.getImpuesto())
			&& ctx.det.getOperacionExenta() == null ) {
			ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1182);
		}
	};
	/*
	 * 	15.6.2 ClaveRegimen 03. REBU.
	 *		- Si Impuesto = "01" (IVA), "03" (IGIC) o no se cumplimenta (considerándose "01" - IVA), cuando 
	 *		  ClaveRegimen sea igual a "03", si se cumplimenta CalificacionOperacion, este campo solo puede 
	 *		  contener el valor "S1".
	 *
	 * 		Aclaración: ClaveRegimen "03" es compatible con operación exenta, ya que el artículo 137. Dos. 5ª de la 
	 * 		Ley 37/1992, de 28 de diciembre, del Impuesto sobre el Valor Añadido contempla expresamente la posibilidad 
	 * 		de aplicar exenciones en REBU (Régimen especial de bienes usados, objetos de arte, antigüedades y 
	 * 		objetos de colección).
	 */
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_03_REBU = ctx -> {
		if ( AonStringUtils.equals("03",ctx.det.getClaveRegimen())
			&& isIVAoIGIC(ctx.det.getImpuesto())
			&& ctx.det.getCalificacionOperacion() != S_1 ) {
			ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1200);
		}
	};
	
	/*
	 *	15.6.3 ClaveRegimen 04. Operaciones con oro de inversión. 
	 *		- Si Impuesto = "01" (IVA), "03" (IGIC) o no se cumplimenta (considerándose "01" - IVA), si 
	 *		  clave de ClaveRegimen es igual a "04", CalificacionOperacion solo puede ser "S2", o bien OperacionExenta. 
	 * 
	*/
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_04_ORO = ctx -> {
		if ( AonStringUtils.equals("04",ctx.det.getClaveRegimen())
			&& isIVAoIGIC(ctx.det.getImpuesto())
			&& (ctx.det.getOperacionExenta() != null || ctx.det.getCalificacionOperacion() != S_2 )) {
			ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1201);
		}
	};
	
	/*
	 *	15.6.4 ClaveRegimen 06. Grupo de entidades nivel avanzado.
	 *		- Si Impuesto = "01" (IVA), "03" (IGIC) o no se cumplimenta (considerándose "01" - IVA):
	 *		- Si ClaveRegimen es igual a "06":
	 *			 - Se validará que TipoFactura sea distinto de "F2", "F3", "R5".
	 *			 - Campo BaseImponibleACoste deberá estar cumplimentado.
	 */
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_06_GRUPO = ctx -> {
		if ( AonStringUtils.equals("06",ctx.det.getClaveRegimen())
			&& isIVAoIGIC(ctx.det.getImpuesto())
			&& (AonEnumUtils.in(ctx.fra.getTipoFactura(),F_2,F_3,R_5) || ctx.det.getBaseImponibleACoste() == null )) {
			ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1202);
		}
	};
	
	/*
	 *	15.6.5 ClaveRegimen 07. Criterio de caja.
	 *	- Si Impuesto = "01" (IVA), "03" (IGIC) o no se cumplimenta (considerándose "01" - IVA):
	 *	- Si ClaveRegimen = "07":
	 *		- CalificacionOperacion no puede ser "S2", "N1", "N2".
	 *		- OperacionExenta no puede ser "E2", "E3", "E4" y "E5".	 
	 */
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_07_CRITERIO_DE_CAJA = ctx -> {
		if ( AonStringUtils.equals("07",ctx.det.getClaveRegimen())
			&& isIVAoIGIC(ctx.det.getImpuesto())
			&& (AonEnumUtils.in(ctx.det.getCalificacionOperacion(), S_2, N_1, N_2) 
			 || AonEnumUtils.in(ctx.det.getOperacionExenta(), E_2, E_3, E_4, E_5))) {
			ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1203);
		}
	};
	
	/*
	 *	15.6.6 ClaveRegimen 08. 
	 *		- Si Impuesto = "01" (IVA), "03" (IGIC) o no se cumplimenta (considerándose "01" - IVA):
	 *			- Si ClaveRegimen = "08", CalificacionOperacion tiene que ser "N2" y siempre debe ir relleno. 
	 */
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_08 = ctx -> {
		if ( AonStringUtils.equals("08",ctx.det.getClaveRegimen())
			&& isIVAoIGIC(ctx.det.getImpuesto())
			&& ctx.det.getCalificacionOperacion() != N_2) {
			ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1252);
		}
	};
	
	/*
	 *	15.6.7 ClaveRegimen 10. Cobro por cuenta de terceros.
	 *	- Si Impuesto = "01" (IVA), "03" (IGIC) o no se cumplimenta (considerándose "01" - IVA):
	 *		 - Si existe una ClaveRegimen "10":
	 *			- CalificacionOperacion tiene que ser "N1" y siempre debe ir relleno.
	 *			- TipoFactura tiene que ser "F1".
	 *			- Todos los destinatarios tienen que estar identificado mediante NIF.
	 */
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_10 = ctx -> {
		if ( AonStringUtils.equals("10",ctx.det.getClaveRegimen()) 
			&& isIVAoIGIC(ctx.det.getImpuesto()) 
			&& (ctx.det.getCalificacionOperacion() != N_1
			 || ctx.fra.getTipoFactura() != F_1
			 || ctx.fra.getDestinatarios() == null
			 || AonCollectionUtils.stream(ctx.fra.getDestinatarios().getIDDestinatario()).anyMatch( d -> AonStringUtils.isBlank(d.getNIF())))) {
			ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1205);
		}
	};

	/*
	 *	15.6.8 ClaveRegimen 11. Arrendamiento de local de negocio
	 *		- Si Impuesto = "01" (IVA) o no se cumplimenta (considerándose "01" - IVA), 
	 *		cuando ClaveRegimen sea "11", únicamente se admitirá el TipoImpositivo = 21.	 
	 */
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_11 = ctx -> {
		if ( AonStringUtils.equals("11",ctx.det.getClaveRegimen()) 
			&& isIVAoIGIC(ctx.det.getImpuesto()) 
			&& AonStringUtils.notEquals("21",ctx.det.getTipoImpositivo())) {
				ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1206);
		}
	};
	
	
	/*
	 *	15.6.9 ClaveRegimen 14. IVA pendiente AAPP.
	 *		- Si Impuesto = "01" (IVA), "03" (IGIC) o no se cumplimenta (considerándose "01" - IVA):
	 *		- Si existe una ClaveRegimen igual a "14":
	 *			- FechaOperacion, campo de cumplimentación obligatoria y posterior a fecha de expedición.
	 *			- Todos los destinatarios tienen que estar identificados mediante NIF y comenzar por "P","Q","S" o "V".
	 *			- TipoFactura: se validará que tipo de factura sea "F1", "R1", "R2", "R3" o "R4".
	 *
	 */
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_14 = ctx -> {
		if ( AonStringUtils.equals("14",ctx.det.getClaveRegimen()) 
			&& isIVAoIGIC(ctx.det.getImpuesto())){
			Date ope = VerifactuUtils.toDate(ctx.fra.getFechaOperacion());
			Date exp = VerifactuUtils.toDate(ctx.fra.getIDFactura().getFechaExpedicionFactura());
			if (AonStringUtils.isBlank(ctx.fra.getFechaOperacion()) || !AonDateUtils.isAfter(ope,exp)) {
				ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1147);
			} else if ( AonEnumUtils.notIn(ctx.fra.getTipoFactura(), F_1, R_1, R_2, R_3, R_4) ) {
				ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1148);
			} else if ( ctx.fra.getDestinatarios() == null
				 || AonCollectionUtils.stream(ctx.fra.getDestinatarios().getIDDestinatario()).anyMatch( d -> AonStringUtils.isBlank(d.getNIF())) ) {
				ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1149);
			}
		}
	};
	
	/*
	 *	15.6.10 ClaveRegimen 20 (IGIC). Operaciones sujetas al IPSI.
	 *		 - Si Impuesto = "03" (IGIC), el valor de ClaveRegimen debe estar contenido en la 
	 *			lista L8B y adicionalmente puede contener el valor "20" (Operaciones sujetas al IPSI).
	 */
	
			
	/*
	 *	15.6.11 ClaveRegimen 21 (IGIC). Régimen simplificado.
	 *		 - Si Impuesto = "03" (IGIC), el valor de ClaveRegimen debe estar contenido en la lista L8B 
	 *		y adicionalmente puede contener el valor "21" (Régimen simplificado).
	 */

	/*
	 *	15.7 CuotaRepercutida. 
	 *		-Si CalificacionOperacion es "S1" y BaseImponibleACoste no está cumplimentada, se validará que:
	 *			- TipoImpositivo: campo obligatorio.
	 *			- CuotaRepercutida: campo obligatorio y deberá validarse (excepto si TipoRectificativa = "I" o TipoFactura "R2", "R3") que:
	 *				- CuotaRepercutida y BaseImponibleOimporteNoSujeto deben tener el mismo signo.
	 *				- [CuotaRepercutida] = ([BaseImponibleOimporteNoSujeto] * TipoImpositivo) / 100 +/- 10,00 euros
	 *		-Si CalificacionOperacion es "S1" y BaseImponibleACoste está cumplimentada, se validará que:
	 *			- TipoImpositivo: campo obligatorio.
	 * 			- CuotaRepercutida: campo obligatorio y deberá validarse (excepto si TipoRectificativa = "I" o TipoFactura "R2", "R3") que:
	 *				- CuotaRepercutida y BaseImponibleACoste deben tener el mismo signo.
	 *				- [CuotaRepercutida] = ([BaseImponibleACoste] * TipoImpositivo) / 100 +/- 10,00 euros.
	 * 
	 */
	private static final Consumer<DegloseContext> ALTA_FRA_DESGLOSE_CUOTA_REPERCUTIDA = ctx -> {
		if (ctx.det.getCalificacionOperacion() != S_1 
			&& AonStringUtils.isNotBlank(ctx.det.getCuotaRepercutida())
			&& AonStringUtils.notEquals("0",ctx.det.getCuotaRepercutida())) {
			// La CuotaRepercutida solo podr\u00E1 ser distinta de 0 si CalificacionOperacion es S1.
			ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1207);
		} else 
		if (ctx.det.getCalificacionOperacion() == S_1) {
			if (AonStringUtils.isBlank(ctx.det.getTipoImpositivo()) || AonStringUtils.isBlank(ctx.det.getCuotaRepercutida())) {
				// Error VERIFACTU_1208 añadido en ALTA_FRA_DESGLOSE_TIPOIMPOSITIVO 
			} else if ( ctx.fra.getTipoRectificativa() != ClaveTipoRectificativaType.I || AonEnumUtils.notIn(ctx.fra.getTipoFactura(), R_2, R_3))  { 
				if (AonStringUtils.isBlank(ctx.det.getBaseImponibleACoste())) {
					if ( notSameSign(ctx.det.getCuotaRepercutida(),ctx.det.getBaseImponibleOimporteNoSujeto()) ) {
						ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1143);
					} else if (notValidGap(ctx.det.getBaseImponibleOimporteNoSujeto(),ctx.det.getTipoImpositivo(),ctx.det.getCuotaRepercutida())) {
						ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1142);
					}
				} else {
					if ( notSameSign(ctx.det.getCuotaRepercutida(),ctx.det.getBaseImponibleACoste()) ) {
						ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1140);
					} else if (notValidGap(ctx.det.getBaseImponibleACoste(),ctx.det.getTipoImpositivo(),ctx.det.getCuotaRepercutida())) {
						ctx.v.addError(InvoiceCommunicationError.VERIFACTU_1144);
					}
				}
			} 
		}
	};
	
	/*
	 * 	15.8 Validaciones adicionales en el caso de facturas simplificadas.
	 * 		- Cuando TipoFactura sea "F2", se validará que sumatorio (BaseImponibleOimporteNoSujeto + CuotaRepercutida) 
	 * 			de todas las líneas de detalle no sea superior a 3.000,00 euros. Se admitirá un error de + 10,00 euros.
	 * 
	 * 		  	Esta validación no se aplicará cuando exista acuerdo de facturación, es decir, cuando el campo 
	 * 			NumRegistroAcuerdoFacturacion esté cumplimentado. Esta validación tampoco se aplicará cuando el 
	 * 			campo FacturaSinIdentifDestinatarioArticulo61d = "S".
	 * 
	 */
	private static final Consumer<AltaContext> ALTA_FRA_DESGLOSE_SUM_SIMPLIFICADAS = a -> {
		if ( a.fra.getTipoFactura() == F_2
		 && (a.fra.getNumRegistroAcuerdoFacturacion() == null || a.fra.getFacturaSinIdentifDestinatarioArt61D() != CompletaSinDestinatarioType.S)) {
			double sum = 0.0;
			for ( DetalleType det : a.fra.getDesglose().getDetalleDesglose() ) {
				double base = VerifactuUtils.todouble(det.getBaseImponibleOimporteNoSujeto());
				double quot = VerifactuUtils.todouble(det.getCuotaRepercutida());
				sum = AonMathUtils.round(sum + base + quot);
			}
			sum = AonMathUtils.absRounded(sum);
			if (sum > 3010.0) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1150);
			}
		}
	};
	
	/*
	 * 	16. CuotaTotal
	 * 		- Se validará que sea igual a sumatorio (CuotaRepercutida + CuotaRecargoEquivalencia) de todas 
	 * 		  las líneas de detalle de desglose. En caso contrario se devolverá un aviso de error (no 
	 * 		  generará rechazo), admitiéndose un margen de error de +/- 10,00 euros.
	 * 
	 */
	private static final Consumer<AltaContext> ALTA_FRA_CUOTA_TOTAL = a -> {
		double qTotal = VerifactuUtils.todouble(a.fra.getCuotaTotal());
		double sum = 0.0;
		for ( DetalleType det : a.fra.getDesglose().getDetalleDesglose() ) {
			double q1 = VerifactuUtils.todouble(det.getCuotaRepercutida());
			double q2 = VerifactuUtils.todouble(det.getCuotaRecargoEquivalencia());
			sum = AonMathUtils.round(sum + q1 + q2);
		}
		double gap = AonMathUtils.absRounded(qTotal - sum);
		if (gap > 10.0) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1216);
		}
	};
	
	/*
	 * 	17. ImporteTotal
	 * 		-Se validará que sea igual a sumatorio (BaseImponibleOimporteNoSujeto + CuotaRepercutida + CuotaRecargoEquivalencia) 
	 * 		 de todas las líneas de detalle de desglose. En caso contrario se devolverá un aviso de error (no generará rechazo), 
	 * 		 admitiéndose un margen de error de +/- 10,00 euros.
	 * 
	 * 		-Esta validación no se aplicará cuando ClaveRegimen sea "03", "05", "06", "08" o "09".
	 */
	private static final Consumer<AltaContext> ALTA_FRA_IMPORTE_TOTAL = a -> {
		if (AonCollectionUtils.stream(a.fra.getDesglose().getDetalleDesglose())
				.noneMatch( d -> AonStringUtils.in(d.getClaveRegimen(), "03", "05", "06", "08", "09"))) { 
		
			double total = VerifactuUtils.todouble(a.fra.getImporteTotal());
			double sum = 0.0;
			for ( DetalleType det : a.fra.getDesglose().getDetalleDesglose() ) {
			
				double b  = VerifactuUtils.todouble(det.getBaseImponibleOimporteNoSujeto());
				double q1 = VerifactuUtils.todouble(det.getCuotaRepercutida());
				double q2 = VerifactuUtils.todouble(det.getCuotaRecargoEquivalencia());
				sum = AonMathUtils.round(sum + b + q1 + q2);
			}
			double gap = AonMathUtils.absRounded(total - sum);
			if (gap > 10.0) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1210);
			}
		}
	};
	
	/*
	 * 15. Agrupación Desglose / DetalleDesglose.
	 * 
	*/
	private static final Consumer<AltaContext> ALTA_FRA_DESGLOSE = a -> {
			List<DetalleType> list = AonObjectUtils.ifNotNullGet( a.fra.getDesglose(), d -> d.getDetalleDesglose());
			AonCollectionUtils.stream( list )
				.map(det -> new DegloseContext(a.vc, a.fra, det) )
				.forEach( c -> 
					ALTA_FRA_DESGLOSE_TIPOIMPOSITIVO
						.andThen(ALTA_FRA_DESGLOSE_BASE_IMPONIBLE_A_COSTE)
						.andThen(ALTA_FRA_DESGLOSE_TIPO_RECARGO_EQUIVALENCIA)
						.andThen(ALTA_FRA_DESGLOSE_CALIFICACION_OPERACION)
						.andThen(ALTA_FRA_DESGLOSE_OPERACION_EXENTA)
						.andThen(ALTA_FRA_DESGLOSE_CLAVE_REGIMEN)
						.andThen(ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_02_EXPORTACION)
						.andThen(ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_03_REBU)
						.andThen(ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_04_ORO)
						.andThen(ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_06_GRUPO)
						.andThen(ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_07_CRITERIO_DE_CAJA)
						.andThen(ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_08)
						.andThen(ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_10)
						.andThen(ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_11)
						.andThen(ALTA_FRA_DESGLOSE_CLAVE_REGIMEN_14)
						.andThen(ALTA_FRA_DESGLOSE_CUOTA_REPERCUTIDA)
						.accept(c)
					);
	};
	

	/*
	 * 18. Huella (del registro anterior)
	 * 	- Se validará que la huella del encadenamiento del registro anterior cumpla el formato de 
	 * 		salida del algoritmo SHA-256, siendo de 64 caracteres en hexadecimal y en mayúsculas. 
	 * 		En caso contrario se devolverá un aviso de error (no generará rechazo).
	 * 
	*/
	private static final Pattern SHA256_PATTERN = Pattern.compile("^[A-F0-9]{64}$");
	private static final Consumer<AltaContext> ALTA_FRA_HUELLA = a -> {
		String huella = a.fra.getHuella();
	    if (huella == null || !SHA256_PATTERN.matcher(huella).matches()) {
	    	a.vc.addError(InvoiceCommunicationError.VERIFACTU_2000);
	    }
	};
	
	/*
	 * 	19. Agrupación SistemaInformatico
	 * 		- Si se cumplimenta NIF, no deberá existir la agrupación IDOtro y viceversa, pero es 
	 * 			obligatorio que se cumplimente uno de los dos.
	 * 		- Si el campo IDType = "02" (NIF-IVA), no será exigible el campo CodigoPais.
	 * 		- Cuando la persona o entidad productora del sistema informático se identifique a través 
	 * 			de la agrupación IDOtro e IDType sea "02", se validará que el campo identificador se ajuste 
	 * 			a la estructura de NIF-IVA de alguno de los Estados Miembros y debe estar identificado. Ver nota (1).
	 * 		- Si se identifica a través de la agrupación IDOtro y CodigoPais sea "ES", se validará 
	 * 			que el campo IDType sea "03".
	 * 		- No se admite el tipo de identificación IDType "07" ("No censado").
	 */

	private static final Consumer<AltaContext> ALTA_FRA_SISTEMA_INFORMATICO_ID = a -> {
		SistemaInformaticoType sis = a.fra.getSistemaInformatico();
		checkSistemaInformatico( a.vc, sis );
	};
	
	/* 	19. Agrupación SistemaInformatico
	 * 		- El campo IdSistemaInformatico deberá tener rellenas siempre las dos posiciones, cada una de las
	 * 			cuales deberá ser una letra mayúscula, excepto la Ñ, o un dígito numérico.
	 * 		- El campo NombreSistemaInformatico es obligatorio y debe tener contenido.
	 * 		- El campo TipoUsoPosibleSoloVerifactu es obligatorio y debe tener contenido.
	 * 		- El campo TipoUsoPosibleMultiOT es obligatorio y debe tener contenido.
	 */
	private static final Pattern PATRON = Pattern.compile("^[A-MO-Z0-9]{2}$");
	private static final Consumer<AltaContext> ALTA_FRA_SISTEMA_INFORMATICO_OTHERS = a -> {
		SistemaInformaticoType sis = a.fra.getSistemaInformatico();
		if (sis != null) {
			if (sis.getIdSistemaInformatico() == null || !PATRON.matcher(sis.getIdSistemaInformatico()).matches()) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1177);
			}
			if (AonStringUtils.isBlank(sis.getNombreSistemaInformatico())) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1220);
			}
			if (sis.getTipoUsoPosibleSoloVerifactu() == null ) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1212);
			}
			if (sis.getTipoUsoPosibleMultiOT() == null ) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1213);
			}
		}
	};
		
	/*	
		
	20. FechaHoraHusoGenRegistro
		- Se validará que la FechaHoraHusoGenRegistro sea menor o igual que la fecha del sistema de la AEAT, admitiéndose 
			un margen de error. En caso de superar el umbral, se devolverá un aviso de error (no generará rechazo).
	
	21. NumRegistroAcuerdoFacturacion
		- Si se informa, debe existir el NumRegistroAcuerdoFacturacion en la AEAT.
	
	22. IdAcuerdoSistemaInformatico
		- Si se informa, debe existir el IdAcuerdoSistemaInformatico en la AEAT. 
	
	23. Huella
		- Se validará que la huella o «hash» generado sea acorde a las especificaciones y formato detallados en el 
			documento "Especificaciones técnicas para generación de la huella o hash de los registros de facturación" 
			publicado en Sede Electrónica de la AEAT. En caso contrario, se devolverá un aviso de error (no generará rechazo).			

	 */
	
	// *********************************************************
	// ********************* [UTIL] ****************************
	// *********************************************************

	private static void checkIDOtroNo07(ValidatorContext vc, RegistroFacturacionAltaType fra, IDOtroType idOtro) {
		if (AonStringUtils.equals("07", idOtro.getIDType())) {
			//- No se admite el tipo de identificación IDType "07" (No censado). 
			vc.addError(InvoiceCommunicationError.VERIFACTU_1222);
		} else if (AonStringUtils.equals("02", idOtro.getIDType())) {
				checkIDOtro02(vc, idOtro);
		} else {
			if (idOtro.getCodigoPais() == null) {
				//- Si el campo IDType = "02" (NIF-IVA), no será exigible el campo CodigoPais.
				vc.addError(InvoiceCommunicationError.VERIFACTU_1111);
			} else {
				//- Si se identifica a través de la agrupación IDOtro y CodigoPais sea "ES", se validará 
				//		que el campo IDType sea "03".
				if (isSpain(idOtro.getCodigoPais()) && AonStringUtils.notEquals(idOtro.getIDType(),"03")) {
					vc.addError(InvoiceCommunicationError.VERIFACTU_1126);
				}
			}
		}
	}
	
	private static void checkSistemaInformatico(ValidatorContext vc, SistemaInformaticoType sis) {
		
		if (sis == null) {
			vc.addError(InvoiceCommunicationError.VERIFACTU_1179);
		} else {
			if (AonStringUtils.isNotBlank(sis.getNIF()) && sis.getIDOtro() != null) {
				vc.addError(InvoiceCommunicationError.VERIFACTU_1223);
			} else if (AonStringUtils.isBlank(sis.getNIF()) && sis.getIDOtro() == null) {
				vc.addError(InvoiceCommunicationError.VERIFACTU_1223);
			} else if (AonStringUtils.isNotBlank(sis.getNIF())) {
				if (!AonDocumentUtil.isValid(sis.getNIF())) {
					vc.addError(InvoiceCommunicationError.VERIFACTU_1123);	
				}
			} else if (sis.getIDOtro() != null) {
				IDOtroType idOtro = sis.getIDOtro();
				if (AonStringUtils.equals("07", idOtro.getIDType())) {
					vc.addError(InvoiceCommunicationError.VERIFACTU_1221);
				} else if (AonStringUtils.equals("02", idOtro.getIDType())) {
					if (idOtro.getCodigoPais() != null) {
						if (AonDocumentUtil.isValidComunitaryCountry(idOtro.getCodigoPais().value())) {
							if (!AonDocumentUtil.isValidComunitaryCode(idOtro.getCodigoPais().value(), idOtro.getID())) {
								vc.addError(InvoiceCommunicationError.VERIFACTU_1221);						
							} else {
								String prefix = AonStringUtils.substring(idOtro.getID(), 0, 2);
								if (AonStringUtils.notEquals(prefix, idOtro.getCodigoPais().value())) {
									vc.addError(InvoiceCommunicationError.VERIFACTU_1221);			
								}
							}
						} else {
							vc.addError(InvoiceCommunicationError.VERIFACTU_1221);
						}
					}
				} else {
					if (idOtro.getCodigoPais() == null) {
						//- Si el campo IDType = "02" (NIF-IVA), no será exigible el campo CodigoPais.
						vc.addError(InvoiceCommunicationError.VERIFACTU_1221);
					} else {
						//- Si se identifica a través de la agrupación IDOtro y CodigoPais sea "ES", se validará 
						//		que el campo IDType sea "03".
						if (isSpain(idOtro.getCodigoPais()) && AonStringUtils.notEquals(idOtro.getIDType(),"03")) {
							vc.addError(InvoiceCommunicationError.VERIFACTU_1221);
						}
					}
				}
				
			}
		}
	}

	private static boolean notValidGap(String baseS, String tipoS, String cuotaS) {
		double base = VerifactuUtils.todouble(baseS);
		double tipo = VerifactuUtils.todouble(tipoS);
		double cuota = VerifactuUtils.todouble(cuotaS);
		double q = AonMathUtils.round(base * tipo / 100);
		double gap = AonMathUtils.absRounded(cuota - q);
		return gap > 10.0;
	}

	private static boolean notSameSign(String v1, String v2) {
		double d1 = VerifactuUtils.todouble(v1);
		double d2 = VerifactuUtils.todouble(v2);
		return AonMathUtils.isNotZero(d1)
			&& AonMathUtils.isNotZero(d2)
			&& AonMathUtils.isNegative(d1) != AonMathUtils.isNegative(d2);
	}

	private static boolean isIVA(String impuesto) {
		return AonStringUtils.isBlank(impuesto) || AonStringUtils.equals(impuesto, "01"); 
	}
	private static boolean isIGIC(String impuesto) {
		return AonStringUtils.equals(impuesto, "03"); 
	}
	private static boolean isIVAoIGIC(String impuesto) {
		return isIVA(impuesto) || isIGIC(impuesto); 
	}

	private static boolean isSpain(CountryType2 country) {
		return country != null && AonStringUtils.equals("ES", country.value());
	}
	
	private static void checkIDOtro02(ValidatorContext vc, IDOtroType idOtro) {
		if (idOtro.getCodigoPais() != null) {
			if (AonDocumentUtil.isValidComunitaryCountry(idOtro.getCodigoPais().value())) {
				// - El campo CodigoPais indicado no coincide con los dos primeros d\u00EDgitos del identificador.
				// Si es Grecia, el campo CodigoPais es diferente a los dos primeros d\u00EDgitos del identificador.
				String prefix = AonStringUtils.substring(idOtro.getID(), 0, 2);
				if (AonStringUtils.notEquals(prefix, idOtro.getCodigoPais().value()) 
					&& !idOtro.getCodigoPais().equals(CountryType2.GR)) {
					vc.addError(InvoiceCommunicationError.VERIFACTU_1122);			
				} else {
					//- Cuando el tercero se identifique a través de la agrupación IDOtro e IDType sea "02", 
					//		se validará que el campo identificador ID se ajuste a la estructura de NIF-IVA de alguno 
					//		de los Estados Miembros y debe estar identificado. Ver nota (1).
					String doc = AonStringUtils.substring(idOtro.getID(), 2);
					if (!AonDocumentUtil.isValidComunitaryCode(idOtro.getCodigoPais().value(), doc )) {
						vc.addError(InvoiceCommunicationError.VERIFACTU_1222);						
					}
				}
			} else {
				// - El valor del campo CodigoPais es incorrecto.
				vc.addError(InvoiceCommunicationError.VERIFACTU_1101);
			}
		}
	}
	
	// *********************************************************
	// *********** [VALIDACION REGISTRO ANULACION] *************
	// *********************************************************
	private static record AnulacionContext(ValidatorContext vc, RegistroFacturacionAnulacionType fra) {}
	public static void validateAnulacion(ValidatorContext vc, RegistroFacturacionAnulacionType anulacion) {
		ANULACION_FRA_NIF
			.andThen(ANULACION_FRA_GENERADO_POR)
			.andThen(ANULACION_FRA_GENERADOR)
			.andThen(ANULACION_FRA_HUELLA)
			.andThen(ANULACION_FRA_SISTEMA_INFORMATICO_ID)
		.accept(new AnulacionContext(vc,anulacion));
	}
	
	/**
	 * 1. Agrupación IDFactura
	 * 
	 * 	 - El NIF del campo IDEmisorFacturaAnulada debe ser el mismo que el del campo 
	 * 	   NIF de la agrupación ObligadoEmision del bloque Cabecera.
	 */
	private static final Consumer<AnulacionContext> ANULACION_FRA_NIF = a -> {
		if (AonStringUtils.notEquals(
			a.vc.verifactu.getCabecera().getObligadoEmision().getNIF(),
			a.fra.getIDFactura().getIDEmisorFacturaAnulada())) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1108);
		}
	};
	
	/**
	 * 2. GeneradoPor
	 * 
	 * 	 - Si se informa este campo, deberá informarse la agrupación Generador.
	 */
	private static final Consumer<AnulacionContext> ANULACION_FRA_GENERADO_POR = a -> {
		if (a.fra.getGeneradoPor() != null && a.fra.getGenerador() == null) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1224);
		}
	};
	
	/**
	 * 3. Agrupación Generador
	 * 
	 * 	 
	 */
	private static final Consumer<AnulacionContext> ANULACION_FRA_GENERADOR = a -> {
		// - Si se informa esta agrupación, debe haberse informado el campo GeneradoPor.
		if (a.fra.getGenerador() != null && a.fra.getGeneradoPor() == null) {
			a.vc.addError(InvoiceCommunicationError.VERIFACTU_1224);
		}
		if (a.fra.getGenerador() != null) {
			PersonaFisicaJuridicaType generador = a.fra.getGenerador();
			
			// - Si se identifica mediante NIF, el NIF debe estar identificado y ser distinto 
			//   del campo NIF de la agrupación ObligadoEmisión del bloque Cabecera.
			if (AonStringUtils.isNotBlank(generador.getNIF()) 
				&& AonStringUtils.equals(a.vc.verifactu.getCabecera().getObligadoEmision().getNIF(),generador.getNIF())) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1259);
			}
			
			// - Si se identifica mediante NIF, el NIF debe ser válido
			if (AonStringUtils.isNotBlank(generador.getNIF()) && !AonDocumentUtil.isValid(generador.getNIF())) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1258);	
			}
			
			// - Si se cumplimenta NIF, no deberá existir la agrupación IDOtro y viceversa, pero es 
			// 	 obligatorio que se cumplimente uno de los dos.
			if ((AonStringUtils.isNotBlank(generador.getNIF()) && generador.getIDOtro() != null ) 
			 || (AonStringUtils.isBlank(generador.getNIF()) && generador.getIDOtro() == null )) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1228);
			} 
			
			// - Si el valor de GeneradoPor es igual a "E", debe estar relleno el campo NIF en el generador.
			if (a.fra.getGeneradoPor() == GeneradoPorType.E && AonStringUtils.isBlank(generador.getNIF())) {
				a.vc.addError(InvoiceCommunicationError.VERIFACTU_1227);
			}
			if (generador.getIDOtro() != null) {
				IDOtroType idOtro = generador.getIDOtro();
				
				if (AonStringUtils.equals("02", idOtro.getIDType())) {
					// - Si el campo IDType = "02" (NIF-IVA), no será exigible el campo CodigoPais.
					// - Cuando uno o varios destinatarios se identifiquen a través de la agrupación IDOtro e IDType sea "02", se validará que el campo identificador se ajuste a la estructura de NIF-IVA de alguno de los Estados Miembros y debe estar identificado. Ver nota (1).
					checkIDOtro02(a.vc, idOtro);
				} if (a.fra.getGeneradoPor() == GeneradoPorType.D) {
					//- Si el valor de GeneradoPor es igual a "D", cuando el Generador se identifique a través del 
					//  bloque IDOtro y CodigoPais sea "ES", se validará que el campo IDType sea "03" o "07".
					if (AonStringUtils.in(idOtro.getIDType(),"03","07") && !isSpain(idOtro.getCodigoPais())) {
						a.vc.addError(InvoiceCommunicationError.VERIFACTU_1230);
					} else if (AonStringUtils.equals("02", idOtro.getIDType())) {
						// - Si el campo IDType = "02" (NIF-IVA), no será exigible el campo CodigoPais.
						// - Cuando uno o varios destinatarios se identifiquen a través de la agrupación IDOtro e IDType sea "02", se validará que el campo identificador se ajuste a la estructura de NIF-IVA de alguno de los Estados Miembros y debe estar identificado. Ver nota (1).
						checkIDOtro02(a.vc, idOtro);
					}
				} else if (a.fra.getGeneradoPor() == GeneradoPorType.T) {
					//- Si el valor del campo GeneradoPor es igual a "T":
					//	o Si se identifica a través de la agrupación IDOtro y CodigoPais sea "ES", se validará que el campo IDType sea "03".
					if (AonStringUtils.equals(idOtro.getIDType(),"03") && !isSpain(idOtro.getCodigoPais())) {
						a.vc.addError(InvoiceCommunicationError.VERIFACTU_1231);
					} else if (AonStringUtils.equals("07", idOtro.getIDType())) {
						// - No se admite el tipo de identificación IDType "07" ("No censado").					
						a.vc.addError(InvoiceCommunicationError.VERIFACTU_1229);
					}
				}
				
			}
		}
	};
	
	/**
	 * 4. Huella (del registro anterior)
	 *	- Se validará que la huella del encadenamiento del registro anterior cumpla el formato de salida del 
	 *    algoritmo SHA-256, siendo de 64 caracteres en hexadecimal y en mayúsculas. En caso contrario se 
	 *    devolverá un aviso de error (no generará rechazo). 
	 */
	private static final Consumer<AnulacionContext> ANULACION_FRA_HUELLA = a -> {
		String huella = a.fra.getHuella();
	    if (huella == null || !SHA256_PATTERN.matcher(huella).matches()) {
	    	a.vc.addError(InvoiceCommunicationError.VERIFACTU_2000);
	    }
	};
	
	/**
	 * 5. Agrupación SistemaInformatico
	 *	- Se validará que la huella del encadenamiento del registro anterior cumpla el formato de salida del 
	 *    algoritmo SHA-256, siendo de 64 caracteres en hexadecimal y en mayúsculas. En caso contrario se 
	 *    devolverá un aviso de error (no generará rechazo). 
	 */
	private static final Consumer<AnulacionContext> ANULACION_FRA_SISTEMA_INFORMATICO_ID = a -> {
		SistemaInformaticoType sis = a.fra.getSistemaInformatico();
		checkSistemaInformatico( a.vc, sis );
	};
}

/*
	6. FechaHoraHusoGenRegistro
		- Se validará que la FechaHoraHusoGenRegistro sea menor o igual que la fecha del sistema de la AEAT, admitiéndose un margen de error. En caso de superar el umbral, se devolverá un aviso de error (no generará rechazo).
	7. Huella
		-Se validará que la huella o «hash» generado sea acorde a las especificaciones y formato detallados en el documento "Especificaciones técnicas para generación de la huella o «hash» de los registros de facturación" publicado en Sede Electrónica de la AEAT. En caso contrario, se devolverá un aviso de error (no generará rechazo).
*/ 
