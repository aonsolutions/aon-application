package net.aonsolutions.aon.verifactu;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoRectificativaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CompletaSinDestinatarioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DesgloseRectificacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaARType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType.Destinatarios;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType.FacturasRectificadas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SimplificadaCualificadaType;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuError;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuException;

enum ClaveTipoFactura {


    /**
     * FACTURA (ART. 6, 7.2 Y 7.3 DEL RD 1619/2012)
     * 
     */
    F_1("F1") {
		@Override
		protected boolean accept( Invoice inv) {
			return inv.isSales()
				&& !inv.isSimplified()
				&& !inv.isRectifier();
		}

		@Override
		protected void filler(VerifactuContext vc, Invoice inv, RegistroFacturacionAltaType alta) {
			alta.setTipoFactura(ClaveTipoFacturaType.F_1);
			alta.setFacturaSinIdentifDestinatarioArt61D(CompletaSinDestinatarioType.N);
			alta.setFacturaSimplificadaArt7273(SimplificadaCualificadaType.N);
			alta.setDestinatarios(getDestinatarios(inv));
		}
	},

    /**
     * FACTURA SIMPLIFICADA Y FACTURAS SIN IDENTIFICACION DEL 
     * DESTINATARIO ART. 6.1.D) RD 1619/2012
     * 
     */
    F_2("F2") {
		@Override
		protected boolean accept( Invoice inv) {
			return inv.isSales()
				&& inv.isSimplified()
				&& !inv.isRectifier();
		}
		
		@Override
		protected void filler(VerifactuContext vc, Invoice inv, RegistroFacturacionAltaType alta) {
			alta.setTipoFactura(ClaveTipoFacturaType.F_2 );
			alta.setFacturaSinIdentifDestinatarioArt61D(CompletaSinDestinatarioType.S);
			alta.setFacturaSimplificadaArt7273(SimplificadaCualificadaType.S);
		}
	},

    /**
     * FACTURA RECTIFICATIVA (Art 80.1 y 80.2 y error fundado en derecho)
     * 
     */
    R_1("R1") {
		@Override
		protected boolean accept( Invoice inv) {
			return inv.isSales()
				&& !inv.isSimplified()
				&& inv.isRectifier();
		}
		
		@Override
		protected void filler(VerifactuContext vc, Invoice inv, RegistroFacturacionAltaType alta) {
			alta.setTipoFactura(ClaveTipoFacturaType.R_1);
			alta.setFacturaSinIdentifDestinatarioArt61D(CompletaSinDestinatarioType.N);
			alta.setFacturaSimplificadaArt7273(SimplificadaCualificadaType.N);
			alta.setTipoRectificativa(ClaveTipoRectificativaType.I); 
			FacturasRectificadas frs = new FacturasRectificadas();
			IDFacturaARType rectified = new IDFacturaARType();
			rectified.setIDEmisorFactura(vc.getCompany().getDocument());
			rectified.setNumSerieFactura(inv.getRectificationInvoiceReference());
			rectified.setFechaExpedicionFactura( Invoice2Verifactu.dateToString(inv.getRectificationInvoiceDate()));
			frs.getIDFacturaRectificada().add(rectified);
			alta.setFacturasRectificadas(frs);
			alta.setDestinatarios(getDestinatarios(inv));
		}
	},

    /**
     * FACTURA RECTIFICATIVA (Art. 80.3)
     * 
     */
    R_2("R2") {
		@Override
		protected boolean accept( Invoice inv) {
			return false;
		}
		
		@Override
		protected void filler(VerifactuContext vc, Invoice inv, RegistroFacturacionAltaType alta) {
			// Nothing
		}
	},

    /**
     * FACTURA RECTIFICATIVA (Art. 80.4)
     * 
     */
    R_3("R3") {
		@Override
		protected boolean accept( Invoice inv) {
			return false;
		}
		
		@Override
		protected void filler(VerifactuContext vc, Invoice inv, RegistroFacturacionAltaType alta) {
			// Nothing
		}
	},

    /**
     * FACTURA RECTIFICATIVA (Resto)
     * 
     */
    R_4("R4") {
		@Override
		protected boolean accept( Invoice inv) {
			return false;
		}
		
		@Override
		protected void filler(VerifactuContext vc, Invoice inv, RegistroFacturacionAltaType alta) {
			// Nothing
		}
	},

    /**
     * FACTURA RECTIFICATIVA EN FACTURAS SIMPLIFICADAS
     * 
     */
    R_5("R5") {
		@Override
		protected boolean accept( Invoice inv) {
			return inv.isSales()
				&& inv.isSimplified()
				&& inv.isRectifier();
		}
		
		@Override
		protected void filler(VerifactuContext vc, Invoice inv, RegistroFacturacionAltaType alta) {
			alta.setTipoFactura(ClaveTipoFacturaType.R_5 );
			alta.setFacturaSinIdentifDestinatarioArt61D(CompletaSinDestinatarioType.S);
			alta.setFacturaSimplificadaArt7273(SimplificadaCualificadaType.S);
			alta.setTipoRectificativa(ClaveTipoRectificativaType.I);

			FacturasRectificadas frs = new FacturasRectificadas();

			IDFacturaARType rectified = new IDFacturaARType();
			rectified.setIDEmisorFactura(vc.getCompany().getDocument());
			rectified.setNumSerieFactura(inv.getRectificationInvoiceReference());
			rectified.setFechaExpedicionFactura( Invoice2Verifactu.dateToString(inv.getRectificationInvoiceDate()));

			frs.getIDFacturaRectificada().add(rectified);
			alta.setFacturasRectificadas(frs);
		}
	},

    /**
     * FACTURA EMITIDA EN SUSTITUCION DE FACTURAS SIMPLIFICADAS FACTURADAS Y DECLARADAS
     * 
     */
    F_3("F3") {
		@Override
		protected boolean accept( Invoice inv) {
			return false;
		}
		
		@Override
		protected void filler(VerifactuContext vc, Invoice inv, RegistroFacturacionAltaType alta) {
			// Nothing
		}
	}
	;
    private final String value;

    private ClaveTipoFactura(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

	protected abstract boolean accept( Invoice inv );
	protected abstract void filler( VerifactuContext vc, Invoice inv, RegistroFacturacionAltaType alta);

	public static void fill( VerifactuContext vc, Invoice inv, RegistroFacturacionAltaType alta ) throws VerifactuException {
		LinkedList<ClaveTipoFactura> tipoFact = AonCollectionUtils.stream( values() )
			.filter( cr -> cr.accept( inv ))
			.collect(Collectors.toCollection(LinkedList::new));
		if (AonCollectionUtils.isEmpty(tipoFact)) throw new VerifactuException( VerifactuError.AON_9005 );
		if (AonCollectionUtils.size(tipoFact) > 1) throw new VerifactuException( VerifactuError.AON_9006 );
		tipoFact.get(0).filler(vc, inv, alta);
	}
	
	public static Destinatarios getDestinatarios(Invoice invoice) {
		PersonaFisicaJuridicaType destinatario = new PersonaFisicaJuridicaType();
		destinatario.setNIF(invoice.getRegistryDocument());
		destinatario.setNombreRazon(invoice.getRegistryName());
		
		
		// TODO ID OTRO
		
		Destinatarios destinatarios = new Destinatarios();
		destinatarios.getIDDestinatario().add(destinatario);
		return destinatarios;
	}
}
