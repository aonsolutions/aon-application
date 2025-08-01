package net.aonsolutions.aon.verifactu;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAnulacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuError;

public class VerifactuValidation {
	
	private VerifactuValidation() {
		
	}
	
//	private static record AltaContext( 
//		RegFactuSistemaFacturacion fras, 
//		RegistroFacturacionAltaType fra,
//		List<VerifactuError> errors) 
//	{}
//	
//	private static record AnulacionContext( 
//		RegFactuSistemaFacturacion fras, 
//		RegistroFacturacionAnulacionType fra, 
//		List<VerifactuError> errors) 
//	{}
//	
//	public List<VerifactuError> validate(RegFactuSistemaFacturacion fras) {
//		LinkedList<VerifactuError> allErrors = new LinkedList<>();
//		for (RegistroFacturaType fraType : fras.getRegistroFactura()) {
//			if (fraType.getRegistroAlta() != null) {
//				allErrors.addAll( validateAlta( fras, fraType.getRegistroAlta()));
//			}
//			if (fraType.getRegistroAnulacion() != null) {
//				allErrors.addAll( validateAnulacion( fras, fraType.getRegistroAnulacion()));
//			}
//		}
//		return allErrors;
//	}
//	
//	// *********************************************************
//	// ************ [VALIDACION REGISTRO ANULACION] ************
//	// *********************************************************
//	private List<VerifactuError> validateAnulacion(RegFactuSistemaFacturacion fras, RegistroFacturacionAnulacionType fra) {
//		AnulacionContext ctx = new AnulacionContext(fras, fra, new LinkedList<>());
//		return ctx.errors;
//	}
//
//	// *********************************************************
//	// *************** [VALIDACION REGISTRO ALTA] **************
//	// *********************************************************
//	private List<VerifactuError> validateAlta(RegFactuSistemaFacturacion fras, RegistroFacturacionAltaType fra) {
//		AltaContext ctx = new AltaContext(fras, fra, new LinkedList<>());
//		CHECK_CABECERA
//			.andThen(ALTA_FRA_NIF)
//			.andThen(ALTA_FRA_FECHA)
//			.accept(ctx);
//		
//		return ctx.errors;
//	}
//	
//	/**
//	 * El dato Cabecera >> ObligadoEmision >> NIF  en  debe existir.
//	 */
//	private static final Consumer<AltaContext> CHECK_CABECERA = new Consumer<>() {
//		@Override
//		public void accept(AltaContext ctx) {
//			if (ctx.fras.getCabecera().getObligadoEmision().getNIF() == null) {
//				ctx.errors.add(VerifactuError.VERIFACTU_4104);
//			} else if (!AonDocumentUtil.isValid(ctx.fras.getCabecera().getObligadoEmision().getNIF())) {
//				ctx.errors.add(VerifactuError.VERIFACTU_4109);
//			}
//		}
//	};
//	
//	/**
//	 * 1. Agrupación IDFactura
//	 * 
//	 * 	 - El NIF del campo IDEmisorFactura debe ser el mismo que el del campo NIF
//	 * 	   de la agrupación ObligadoEmision del bloque Cabecera.
//	 */
//	private static final Consumer<AltaContext> ALTA_FRA_NIF = ctx -> {
//		if (AonStringUtils.notEquals(
//			ctx.fras.getCabecera().getObligadoEmision().getNIF(),
//			ctx.fra.getIDFactura().getIDEmisorFactura())) {
//			ctx.errors.add(VerifactuError.VERIFACTU_1108);
//		}
//	};
//
//	/**
//	 * 1. Agrupación IDFactura
//	 * 
//	 * 	 - La FechaExpedicionFactura no podrá ser superior a la fecha actual.
//	 */
//	private static final Consumer<AltaContext> ALTA_FRA_FECHA = ctx -> {
//		Date expDate = VerifactuUtils.toDate(ctx.fra.getIDFactura().getFechaExpedicionFactura());
//		if (expDate == null) {
//			ctx.errors.add(VerifactuError.VERIFACTU_1105);
//		}
//		if (AonDateUtils.isAfter(expDate, AonDateUtils.today())) {
//			ctx.errors.add(VerifactuError.VERIFACTU_1112);
//		}
//	};
}



/*

1. Agrupación IDFactura
				- El NIF del campo IDEmisorFactura debe ser el mismo que el del campo NIF de la agrupación ObligadoEmision del bloque Cabecera.
				- La FechaExpedicionFactura no podrá ser superior a la fecha actual.
	- Si Impuesto = "01" (IVA), "03" (IGIC) o no se cumplimenta (considerándose "01" - IVA), la FechaExpedicionFactura solo puede ser anterior a la FechaOperacion, si ClaveRegimen="14" o "15".
	- La FechaExpedicionFactura no debe ser inferior a 28/10/2024 (fecha de entrada en vigor de la Orden Ministerial de VERI*FACTU).
	- NumSerieFactura solo puede contener caracteres ASCII del 32 a 126 (caracteres imprimibles)

2. RechazoPrevio
	- Solo podrá incluirse el campo RechazoPrevio con valor "X" si se ha informado el campo Subsanacion y tiene el valor "S".
	- No podrá informarse el campo RechazoPrevio con valor "S" si no se informa el campo Subsanación o éste tiene el valor "N"

3. TipoRectificativa
	- Solo podrá incluirse este campo si el valor del campo TipoFactura es igual a "R1", "R2", "R3", "R4" o "R5"
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
