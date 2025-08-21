package net.aonsolutions.aon.verifactu;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.VATExemptionCause;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CalificacionOperacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DetalleType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.OperacionExentaType;

enum ClaveRegimen {
	/**
	 * OPERACIÓN DE RÉGIMEN GENERAL. NACIONALES
	 */
	C01_NATIONAL("01") {	
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return inv.isSales()
				&& getVATRegime(vc, inv) == VATRegime.GENERAL 
				&& inv.isNational()
				&& !inv.isSurcharge() 
				&& !inv.isWithholdingFarmer()
				&& !inv.isVatAccrualPayment()
				&& !inv.isSalesOSS()
				&& !ib.isPrepayment()
				&& VatDeductionType.safeSujetoNoExento(ib.getVatDeductionType())
			;
		}
		
		@Override
		protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws InvoiceCommunicationException {
			DetalleType detalle = C01_NATIONAL.getBasicWithVat( ib );
			detalle.setCalificacionOperacion(CalificacionOperacionType.S_1);
			return detalle;
		}
	},
	
	/**
	 * OPERACIÓN DE RÉGIMEN GENERAL. ISP
	 */
	C01_ISP("01") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return getVATRegime(vc, inv) == VATRegime.GENERAL 
				&& inv.isIsp()
				&& !inv.isSurcharge() 
				&& !inv.isWithholdingFarmer()
				&& !inv.isVatAccrualPayment()
				&& !inv.isSalesOSS()
				&& !ib.isPrepayment()
				&& VatDeductionType.safeSujetoNoExento(ib.getVatDeductionType())
			;
		}
		
		@Override
		protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws InvoiceCommunicationException {
			DetalleType detalle = C01_ISP.getBasicWithVat( ib );
			detalle.setCalificacionOperacion(CalificacionOperacionType.S_2);
			return detalle;
		}
	},
	
	/**
	 *  OPERACIÓN DE RÉGIMEN GENERAL. PRESTACION SERVICIO INTRACOMUNITARIO
	 */
	C01_INTRACOMMUNITY_SERVICE("01") {	
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return getVATRegime(vc, inv) == VATRegime.GENERAL 
				&& inv.isIntracommunity()
				&& inv.isService()
				&& !inv.isSurcharge() 
				&& !inv.isWithholdingFarmer()
				&& !inv.isVatAccrualPayment()
				&& !inv.isSalesOSS()
				&& !ib.isPrepayment()
				&& VatDeductionType.safeSujetoNoExento(ib.getVatDeductionType())
			;
		}
		
		@Override
		protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws InvoiceCommunicationException {
			DetalleType detalle = C01_INTRACOMMUNITY_SERVICE.getBasic( ib );
			detalle.setCalificacionOperacion(CalificacionOperacionType.N_2);
			return detalle;
		}
	},
	
	/**
	 * OPERACIÓN DE RÉGIMEN GENERAL. SUPLIDOS.
	 */
	C01_PREPAYMENT("01") {	
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return getVATRegime(vc, inv) == VATRegime.GENERAL 
				&& inv.isNational()
				&& !inv.isSurcharge() 
				&& !inv.isWithholdingFarmer()
				&& !inv.isVatAccrualPayment()
				&& !inv.isSalesOSS()
				&& ib.isPrepayment()
				&& VatDeductionType.safeNoSujeto(ib.getVatDeductionType())
			;
		}
		
		@Override
		protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws InvoiceCommunicationException {
			DetalleType detalle = C01_PREPAYMENT.getBasic(ib);
			detalle.setCalificacionOperacion(CalificacionOperacionType.N_1);
			return detalle;
		}
	},
	
	/**
	 *	OPERACIÓN DE RÉGIMEN GENERAL. EXENTA E1.
	 *
	 * Art. 20 - Exenciones interiores (en España)
	 * Ciertas actividades no llevan IVA, como:
	 * 	- Educación
	 * 	- Sanidad
	 * 	- Alquiler de vivienda
	 * 	- Servicios financieros y seguros
	 * 	- Actividades sin ánimo de lucro
	 */
	C01_EXENTA_E1("01") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return inv.isNational()
				&& inv.isExempt()				
				&& !inv.isSurcharge() 
				&& !inv.isWithholdingFarmer()
				&& !inv.isVatAccrualPayment()
				&& !inv.isSalesOSS()
				&& !ib.isPrepayment()
				&& VatDeductionType.safeSujetoExento(ib.getVatDeductionType())
				&& ib.getVatExemptionCause() == VATExemptionCause.E1
			;
		}
		
		@Override
		protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws InvoiceCommunicationException {
			DetalleType detalle = C01_EXENTA_E1.getBasic( ib );
			detalle.setOperacionExenta(OperacionExentaType.E_1);
			return detalle;
		}
	},
    /**
     * Art. 25 - Entregas intracomunitarias
     * 	Vender a empresas en otros países de la UE también está exento de IVA, 
     * 	si el comprador tiene NIF-IVA intracomunitario y se prueba el envío.	
     */
	C01_EXENTA_E5("01") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return inv.isSales()
				&& inv.isIntracommunity()
				&& !inv.isService()
				&& !inv.isSurcharge()
				&& !inv.isWithholdingFarmer()
				&& !inv.isVatAccrualPayment()
				&& !inv.isSalesOSS()
				&& !ib.isPrepayment()
//				&& VatDeductionType.safeSujetoExento(ib.getVatDeductionType())
//				&& ib.getVatExemptionCause() == VATExemptionCause.E5
			;
		}
		
		@Override
		protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws InvoiceCommunicationException {
			DetalleType detalle = C01_EXENTA_E5.getBasic(ib);
			detalle.setOperacionExenta(OperacionExentaType.E_5);
			return detalle;
		}
	},

	// Exportación.
	C02("02") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return inv.isExtracommunity() || inv.isCanCeuMel();
		}
		@Override
		protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws InvoiceCommunicationException {
			DetalleType detalle = C02.getBasic(ib);
			detalle.setOperacionExenta(OperacionExentaType.E_2);
			return detalle;
		}
	}
	// Operaciones a las que se aplique el régimen especial de bienes usados, objetos de arte, antigüedades y objetos de colección.
	,C03("03") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}
	// Régimen especial del oro de inversión.
	,C04("04") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}
	// Régimen especial de las agencias de viajes.
	,C05("05") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}	
	// Régimen especial grupo de entidades en IVA (Nivel Avanzado)
	,C06("06") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}
	// Régimen especial del criterio de caja.
	,C07("07") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return inv.isSales()
				&& getVATRegime(vc, inv) == VATRegime.GENERAL 
				&& inv.isNational()
				&& inv.isVatAccrualPayment()
				&& !ib.isPrepayment()
				&& VatDeductionType.safeSujetoNoExento(ib.getVatDeductionType())
			;
		}
		
		@Override
		protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws InvoiceCommunicationException {
			DetalleType detalle = C07.getBasicWithVat( ib );
			detalle.setCalificacionOperacion(CalificacionOperacionType.S_1);
			return detalle;
		}
	}
	// Operaciones sujetas al IPSI/IGIC (Impuesto sobre la Producción, los Servicios y la Importación/Impuesto General Indirecto Canario).
	,C08("08") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}
	// Facturación de las prestaciones de servicios de agencias de viaje que actúan como mediadoras en nombre y por cuenta ajena (D.A.4.ª RD1619/2012)
	,C09("09") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}
	// Cobros por cuenta de terceros de honorarios profesionales o de derechos derivados de la propiedad industrial, de autor u otros por cuenta de sus socios, asociados o colegiados efectuados por sociedades, asociaciones, colegios profesionales u otras entidades que realicen estas funciones de cobro.
	,C10("10") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}
	// Operaciones de arrendamiento de local de negocio.
	,C11("11") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}
	// Factura con IVA pendiente de devengo en certificaciones de obra cuyo destinatario sea una Administración Pública.
	,C14("14") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}
	// Factura con IVA pendiente de devengo en operaciones de tracto sucesivo.
	,C15("15") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}
	// Operación acogida a alguno de los regímenes previstos en el capítulo XI del título IX (OSS e IOSS)
	,C17("17") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}
	
	,/**
	 * OPERACIÓN DE RÉGIMEN DE RECARGO DE EQUIVALENCIA.
	 */
	C18("18") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return getVATRegime(vc, inv) == VATRegime.GENERAL 
				&& inv.isNational()
				&& inv.isSurcharge() 
				&& !inv.isWithholdingFarmer()
				&& !inv.isVatAccrualPayment()
				&& !inv.isSalesOSS()
				&& !ib.isPrepayment()
				&& VatDeductionType.safeSujetoNoExento(ib.getVatDeductionType())
			;
		}
		
		@Override
		protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws InvoiceCommunicationException {
			DetalleType detalle = C18.getBasicWithVat( ib );
			detalle.setCalificacionOperacion(CalificacionOperacionType.S_1);
			detalle.setTipoRecargoEquivalencia(VerifactuUtils.toString(ib.getSurcharge()));
			detalle.setCuotaRecargoEquivalencia(VerifactuUtils.toString(ib.getSurchargeQuota()));
			return detalle;
		}
		
	}
	// Operaciones de actividades incluidas en el Régimen Especial de Agricultura, Ganadería y Pesca (REAGYP)
	,C19("19") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}
	// Régimen simplificado
	,C20("20") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
	}
	;
	
	private String value;
	private ClaveRegimen(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	private static VATRegime getVATRegime(VerifactuContext vc, Invoice inv) {
		return inv.optActivity()
			.map(a -> a.getId())
			.flatMap( vc::getActivity )
			.map(a -> a.getVatRegime())
			.orElse(VATRegime.GENERAL)
		;
	}
	
	private DetalleType getBasic( InvoiceBreakdown ib ) {
		DetalleType detalle = new DetalleType();
		detalle.setImpuesto(TipoImpuesto.IVA.getValue());
		detalle.setClaveRegimen( this.getValue() );
		detalle.setBaseImponibleOimporteNoSujeto(VerifactuUtils.toString(ib.getBase()));
		return detalle;
	}
	
	private DetalleType getBasicWithVat( InvoiceBreakdown ib ) {
		DetalleType detalle = this.getBasic(ib);
		detalle.setBaseImponibleACoste(null);
		detalle.setTipoImpositivo(VerifactuUtils.toString(ib.getPercentage()));
		detalle.setCuotaRepercutida(VerifactuUtils.toString(ib.getQuota()));
		return detalle;
	}

	
	public static DetalleType get( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib ) throws InvoiceCommunicationException {
		LinkedList<ClaveRegimen> claveRegimes = AonCollectionUtils.stream( values() )
			.filter( cr -> cr.accept(vc, inv, ib))
			.collect(Collectors.toCollection(LinkedList::new));
		if (AonCollectionUtils.isEmpty(claveRegimes)) throw new InvoiceCommunicationException( InvoiceCommunicationError.AON_9001 );
		if (AonCollectionUtils.size(claveRegimes) > 1) throw new InvoiceCommunicationException( InvoiceCommunicationError.AON_9002 );
		return claveRegimes.get(0).getDetalleType(vc, inv, ib);
	}
	
	protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws InvoiceCommunicationException {
		throw new InvoiceCommunicationException(new UnsupportedOperationException("Not implented!"));
	}
	
	protected abstract boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib);
}
