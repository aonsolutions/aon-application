package net.aonsolutions.aon.verifactu;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CalificacionOperacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DetalleType;
import net.aonsolutions.aon.verifactu.Invoice2Verifactu.TipoImpuesto;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuError;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuException;

enum ClaveRegimen {
	C01("01") {		// Operación de régimen general.
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return getVATRegime(vc, inv) == VATRegime.GENERAL 
				&& (inv.isNational() || inv.isIsp())
				&& !inv.isSurcharge() 
				&& !inv.isWithholdingFarmer()
				&& !inv.isVatAccrualPayment()
				&& !inv.isSalesOSS();
		}
		
		@Override
		protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws VerifactuException {
			DetalleType detalle = C01.getBasic( ib );
			detalle.setTipoRecargoEquivalencia(null);
			detalle.setCuotaRecargoEquivalencia(null);
			detalle.setOperacionExenta(null);
			detalle.setCalificacionOperacion(inv.isIsp() 
				? CalificacionOperacionType.S_2 
				: CalificacionOperacionType.S_1);
			return detalle;
		}
	}
	// Exportación.
	,C02("01") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return false;
		}
		@Override
		protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws VerifactuException {
			throw new VerifactuException(new UnsupportedOperationException("Not implented!"));
		}
	}
	// Operaciones a las que se aplique el régimen especial de bienes usados, objetos de arte, antigüedades y objetos de colección.
	,C03("02") {
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
			return false;
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
	// Recargo de equivalencia.
	,C18("18") {
		@Override
		protected boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) {
			return getVATRegime(vc, inv) == VATRegime.GENERAL 
				&& inv.isNational()
				&& inv.isSurcharge() 
				&& !inv.isWithholdingFarmer()
				&& !inv.isVatAccrualPayment()
				&& !inv.isSalesOSS();
		}
		
		@Override
		protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws VerifactuException {
			DetalleType detalle = C18.getBasic( ib );
			detalle.setTipoRecargoEquivalencia(Invoice2Verifactu.doubleToString(ib.getSurcharge()));
			detalle.setCuotaRecargoEquivalencia(Invoice2Verifactu.doubleToString(ib.getSurchargeQuota()));
			detalle.setOperacionExenta(null);
			detalle.setCalificacionOperacion( CalificacionOperacionType.S_1 );
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
		detalle.setBaseImponibleOimporteNoSujeto(Invoice2Verifactu.doubleToString(ib.getBase()));
		detalle.setBaseImponibleACoste(null);
		detalle.setTipoImpositivo(Invoice2Verifactu.doubleToString(ib.getPercentage()));
		detalle.setCuotaRepercutida(Invoice2Verifactu.doubleToString(ib.getQuota()));
		return detalle;
	}

	
	public static DetalleType get( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib ) throws VerifactuException {
		LinkedList<ClaveRegimen> claveRegimes = AonCollectionUtils.stream( values() )
			.filter( cr -> cr.accept(vc, inv, ib))
			.collect(Collectors.toCollection(LinkedList::new));
		if (AonCollectionUtils.isEmpty(claveRegimes)) throw new VerifactuException( VerifactuError.AON_9001 );
		if (AonCollectionUtils.size(claveRegimes) > 1) throw new VerifactuException( VerifactuError.AON_9002 );
		return claveRegimes.get(0).getDetalleType(vc, inv, ib);
	}
	
	protected DetalleType getDetalleType( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib) throws VerifactuException {
		throw new VerifactuException(new UnsupportedOperationException("Not implented!"));
	}
	
	protected abstract boolean accept( VerifactuContext vc, Invoice inv, InvoiceBreakdown ib);
}
