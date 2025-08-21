package net.aonsolutions.aon.verifactu;

import java.util.Date;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorKey;
import com.esferalia.aon.occam.api.model.invoice.InvoiceErrorMessages;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SubsanacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;

public class VerifactuValidation {
	
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
//		Optional<RegistroFacturacionAnulacionType> getAnulacion() {
//			return Optional.ofNullable( fraType.getRegistroAnulacion() );
//		}
		void addError( InvoiceCommunicationError error) {
			this.invoice.addMessage( InvoiceErrorMessages.C050.err(InvoiceErrorKey.COMMUNICATION,error.getCode(),error.getMessage()));
		}
	}
	
	public static Invoice validate(RegFactuSistemaFacturacion fras, RegistroFacturaType fraType,Invoice invoice ) {
		ValidatorContext v = new ValidatorContext(fras, fraType, invoice);
		CHECK_CABECERA
			.accept(v);
		v.getAlta().ifPresent( alta -> {
			ALTA_FRA_NIF
				.andThen(ALTA_FRA_FECHA)
				.andThen(ALTA_FRA_FECHA_EXP)
				.andThen(ALTA_FRA_NUM_SERIE)
				.andThen(ALTA_FRA_RECHAZO_PREVIO)
				.andThen(ALTA_FRA_TIPO_RECTIFICATIVA)
			.accept(v, alta);
		});
		return invoice;
	}
	
	// *********************************************************
	// ************ [VALIDACION REGISTRO ANULACION] ************
	// *********************************************************
	/**
	 * El dato Cabecera >> ObligadoEmision >> NIF  en  debe existir.
	 */
	private static final Consumer<ValidatorContext> CHECK_CABECERA = v -> {
		if (AonStringUtils.isBlank(v.verifactu.getCabecera().getObligadoEmision().getNIF())) {
			v.addError(InvoiceCommunicationError.VERIFACTU_4104);
		} else if (!AonDocumentUtil.isValid(v.verifactu.getCabecera().getObligadoEmision().getNIF())) {
			v.addError(InvoiceCommunicationError.VERIFACTU_4109);
		}
	};
	
	// *********************************************************
	// *************** [VALIDACION REGISTRO ALTA] **************
	// *********************************************************
	/**
	 * 1. Agrupación IDFactura
	 * 
	 * 	 - El NIF del campo IDEmisorFactura debe ser el mismo que el del campo NIF
	 * 	   de la agrupación ObligadoEmision del bloque Cabecera.
	 */
	private static final BiConsumer<ValidatorContext,RegistroFacturacionAltaType> ALTA_FRA_NIF = (v,fra) -> {
		if (AonStringUtils.notEquals(
			v.verifactu.getCabecera().getObligadoEmision().getNIF(),
			fra.getIDFactura().getIDEmisorFactura())) {
			v.addError(InvoiceCommunicationError.VERIFACTU_1108);
		}
	};

	/**
	 * 1. Agrupación IDFactura
	 * 
	 * 	 - La FechaExpedicionFactura no podrá ser superior a la fecha actual.
	 */
	private static final BiConsumer<ValidatorContext,RegistroFacturacionAltaType> ALTA_FRA_FECHA = (v,fra) -> {
		Date expDate = VerifactuUtils.toDate(fra.getIDFactura().getFechaExpedicionFactura());
		if (expDate == null) {
			v.addError(InvoiceCommunicationError.VERIFACTU_1105);
		}
		if (AonDateUtils.isAfter(expDate, AonDateUtils.today())) {
			v.addError(InvoiceCommunicationError.VERIFACTU_1112);
		}
	};
	
	/*
	 * 1. Agrupación IDFactura
	 * 
	 * - La FechaExpedicionFactura no debe ser inferior a 28/10/2024 (fecha de entrada en vigor de la Orden Ministerial de VERI*FACTU).
	 */
	private static final BiConsumer<ValidatorContext,RegistroFacturacionAltaType> ALTA_FRA_FECHA_EXP = (v,fra) -> {
		Date expDate = VerifactuUtils.toDate(fra.getIDFactura().getFechaExpedicionFactura());
		if (!AonDateUtils.isAfter(expDate, AonDateUtils.getDate(27,9,2024))) {
			v.addError(InvoiceCommunicationError.VERIFACTU_1236);
		}
	};
	
	/*
	 * 1. Agrupación IDFactura
	 *
	 *	- NumSerieFactura solo puede contener caracteres ASCII del 32 a 126 (caracteres imprimibles)
	 */
	private static final BiConsumer<ValidatorContext,RegistroFacturacionAltaType> ALTA_FRA_NUM_SERIE = (v,fra) -> {
		String numSerie = fra.getIDFactura().getNumSerieFactura();
		if (!AonStringUtils.isAsciiPrintable(numSerie)) {
			v.addError(InvoiceCommunicationError.VERIFACTU_1130);
		}
	};
	
	/*
	 * 2. RechazoPrevio
	 */
	private static final BiConsumer<ValidatorContext,RegistroFacturacionAltaType> ALTA_FRA_RECHAZO_PREVIO = (v,fra) -> {
		// - Solo podrá incluirse el campo RechazoPrevio con valor "X" si se ha informado el campo Subsanacion y tiene el valor "S".
		if (fra.getRechazoPrevio() == RechazoPrevioType.X && fra.getSubsanacion() != SubsanacionType.S) {
			v.addError(InvoiceCommunicationError.VERIFACTU_1153);
		}
		// - No podrá informarse el campo RechazoPrevio con valor "S" si no se informa el campo Subsanación o éste tiene el valor "N"
		if (fra.getRechazoPrevio() == RechazoPrevioType.S && (fra.getSubsanacion() == null || fra.getSubsanacion() == SubsanacionType.N)) {
			v.addError(InvoiceCommunicationError.VERIFACTU_1161);
		}
	};
	
	/*
	 * 	3. TipoRectificativa
	 */
	private static final BiConsumer<ValidatorContext,RegistroFacturacionAltaType> ALTA_FRA_TIPO_RECTIFICATIVA = (v,fra) -> {
		if (fra.getTipoFactura() == null) {
			v.addError(InvoiceCommunicationError.VERIFACTU_1106);
		} else {
			if (AonStringUtils.in(fra.getTipoFactura().value(), "R1", "R2", "R3", "R4", "R5")) {
				// - Campo obligatorio si TipoFactura es igual a "R1", "R2", "R3", "R4" o "R5".
				if (fra.getTipoRectificativa() == null) {
					v.addError(InvoiceCommunicationError.VERIFACTU_1114);
				}
			} else {
				// - Solo podrá incluirse el campo TipoRectificativa si el valor del campo 
				//	 TipoFactura es igual a "R1", "R2", "R3", "R4" o "R5"
				if (fra.getTipoRectificativa() != null) {
					v.addError(InvoiceCommunicationError.VERIFACTU_1115);
				}
			}
		}
	};
}

// *********************************************************
// ************ [VALIDACION REGISTRO ANULACION] ************
// *********************************************************


/*

3. TipoRectificativa
	
	- Campo obligatorio si TipoFactura es igual a "R1", "R2", "R3", "R4" o "R5".

4. Agrupación FacturasRectificadas
	- El NIF del campo IDEmisorFactura debe estar identificado.
	- Sólo podrá incluirse esta agrupación (no es obligatoria) si TipoFactura es igual a "R1", "R2","R3", "R4" o "R5". 

5. Agrupación FacturasSustituidas
	- El NIF del campo IDEmisorFactura debe estar identificado.
	- Sólo podrá incluirse esta agrupación (no es obligatoria) cuando el campo TipoFactura="F3".

6. Agrupación ImporteRectificacion
	- Sólo deberá incluirse esta agrupación si el campo TipoRectificativa = "S".
	- Obligatorio si TipoRectificativa = "S".

7. FechaOperacion
	- La FechaOperacion no debe ser inferior a la fecha actual menos veinte años y no debe ser superior al año siguiente de la fecha actual.
	- Si Impuesto = "01" (IVA), "03" (IGIC) o no se cumplimenta (considerándose "01" - IVA), el campo FechaOperacion solo podrá ser superior a la fecha actual, si ClaveRegimen= "14" o "15".

8. FacturaSimplificadaArt7273
	- Sólo se podrá rellenar con "S" si TipoFactura="F1" o "F3" o "R1" o "R2" o "R3" o "R4".

9. FacturaSinIdentifDestinatarioArt61d
	- Sólo se podrá rellenar con "S" si TipoFactura="F2" o "R5".

10. Macrodato
	- Campo obligatorio si ImporteTotal >= |100.000.000,00| (valor absoluto).

11. EmitidaPorTerceroODestinatario
	- Si es igual a "T", el bloque Tercero será de cumplimentación obligatoria.
	- Si es igual a "D", el bloque Destinatarios será de cumplimentación obligatoria.

12. Agrupación Tercero
	- Solo podrá cumplimentarse si EmitidaPorTerceroODestinatario es "T".
	- Si se identifica mediante NIF, el NIF debe estar identificado y ser distinto del NIF del campo IDEmisorFactura de la agrupación IDFactura.
	- Si se cumplimenta NIF, no deberá existir la agrupación IDOtro y viceversa, pero es obligatorio que se cumplimente uno de los dos.
	- Si el campo IDType = "02" (NIF-IVA), no será exigible el campo CodigoPais.
	- Cuando el tercero se identifique a través de la agrupación IDOtro e IDType sea "02", se validará que el campo identificador ID se ajuste a la estructura de NIF-IVA de alguno de los Estados Miembros y debe estar identificado. Ver nota (1).
	- Si se identifica a través de la agrupación IDOtro y CodigoPais sea "ES", se validará que el campo IDType sea "03".
	- No se admite el tipo de identificación IDType "07" (No censado). 

13. Agrupación Destinatarios
	- Si TipoFactura es "F1", "F3", "R1", "R2", "R3" o "R4", la agrupación Destinatarios tiene que estar cumplimentada, con al menos un destinatario.
	- Si TipoFactura es "F2" o "R5", la agrupación Destinatarios no puede estar cumplimentada.
	- Si se cumplimenta NIF, no deberá existir la agrupación IDOtro y viceversa, pero es obligatorio que se cumplimente uno de los dos.
	- Si el campo IDType = "02" (NIF-IVA), no será exigible el campo CodigoPais.
	- Si el campo IDType = "07" (No censado), el campo CodigoPais debe ser "ES".
	- Cuando uno o varios destinatarios se identifiquen a través de la agrupación IDOtro e IDType sea "02", se validará que el campo identificador se ajuste a la estructura de NIF-IVA de alguno de los Estados Miembros y debe estar identificado. Ver nota (1).
	- Cuando uno o varios destinatarios se identifiquen a través de la agrupación IDOtro y CodigoPais sea "ES", se validará que el campo IDType sea "03" o "07". 
	- Cuando se identifique a través del bloque "IDOtro" y IDType sea "02", se validará que TipoFactura sea "F1", "F3", "R1", "R2", "R3" ó "R4".

14. Cupon
	- Sólo se podrá rellenar con "S" (no es obligatorio) si TipoFactura = "R5" o "R1"


15. Agrupación Desglose / DetalleDesglose.

	15.1 TipoImpositivo
	- Si Impuesto = "01" (IVA) o no se cumplimenta (considerándose "01" - IVA) y CalificacionOperacion = "S1":
		- Solo se permiten TipoImpositivo = 0; 2; 4; 5; 7,5; 10 y 21 (valores que indican el tanto por ciento).
		- Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se informa FechaOperacion) >= 1 de julio de 2022 y <= 30 de septiembre de 2024 se admitirá TipoImpositivo = 5.
		- Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se informa FechaOperacion) >= 1 de octubre de 2024 y <= 31 de diciembre de 2024 se admitirá el TipoImpositivo = 2
		- Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se informa FechaOperacion) >= 1 de octubre de 2024 y <= 31 de diciembre de 2024 se admitirá el TipoImpositivo = 7,5.	

	15.2 BaseImponibleACoste
	- El campo BaseImponibleACoste solo puede estar cumplimentado si la ClaveRegimen es = "06" o Impuesto = "02" (IPSI) o Impuesto = "05" (Otros).

	15.3 TipoRecargoEquivalencia
	- Si Impuesto = "01" (IVA) o no se cumplimenta (considerándose "01" - IVA) y CalificacionOperacion = "S1":
		- Solo se permiten TipoRecargoEquivalencia = 0; 0,26; 0,5; 0,62; 1; 1,4; 1,75; 5,2 (valores que indican el tanto por ciento).
		- Si TipoImpositivo es 21 sólo se admitirán TipoRecargoEquivalencia = 5,2 ó 1,75.
		- Si TipoImpositivo es 10 sólo se admitirá TipoRecargoEquivalencia = 1,4.
		- Si TipoImpositivo es 7,5 sólo se admitirá TipoRecargoEquivalencia = 1.
			Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se informa FechaOperacion) es mayor o igual que 1 de octubre de 2024 y menor o igual que 31 de diciembre de 2024 se admitirá el TipoRecargoEquivalencia = 1.
		- Si tipo impositivo es 5:
			Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se informa FechaOperacion) es igual o inferior al 31 de diciembre de 2022, solo se admitirá TipoRecargoEquivalencia = 0,5.
			Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se informa FechaOperacion) es mayor o igual que 1 de enero de 2023 y menor o igual que 30 de septiembre de 2024, solo se admitirá TipoRecargoEquivalencia = 0,62.
		- Si TipoImpositivo es 4 sólo se admitirá TipoRecargoEquivalencia = 0,5.
		- Si TipoImpositivo es 2 sólo se admitirá TipoRecargoEquivalencia = 0,26.
			Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se informa FechaOperacion) es mayor o igual que 1 de octubre de 2024 y menor o igual que 31 de diciembre de 2024 se admitirá el TipoRecargoEquivalencia = 0,26.
		- Si tipo impositivo es 0:
 			Si FechaOperacion (FechaExpedicionFactura de la agrupación IDFactura si no se informa FechaOperacion) es mayor o igual que 1 de enero de 2023 y menor o igual que 30 de septiembre de 2024, solo se admitirá TipoRecargoEquivalencia = 0.	


 */
