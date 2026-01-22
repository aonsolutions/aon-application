package net.aonsolutions.aon.verifactu;

import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoRectificativaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CompletaSinDestinatarioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CountryType2;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaARType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDOtroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType.Destinatarios;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType.FacturasRectificadas;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SimplificadaCualificadaType;

enum ClaveTipoFactura {


    /**
     * FACTURA (ART. 6, 7.2 Y 7.3 DEL RD 1619/2012)
     * 
     */
    F_1("F1") {
    	@Override
    	void visit( ClaveTipoFacturaVisitor visitor ) {
			visitor.visitF_1();
    	}
    	
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
		void visit( ClaveTipoFacturaVisitor visitor ) {
			visitor.visitF_2();
		}
		
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
			alta.setFacturaSimplificadaArt7273(SimplificadaCualificadaType.N);
		}
	},

    /**
     * FACTURA RECTIFICATIVA (Art 80.1 y 80.2 y error fundado en derecho)
     * 
     */
    R_1("R1") {
		@Override
		void visit( ClaveTipoFacturaVisitor visitor ) {
			visitor.visitR_1();
		}
		
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
			rectified.setFechaExpedicionFactura( VerifactuUtils.toString(inv.getRectificationInvoiceDate()));
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
		void visit( ClaveTipoFacturaVisitor visitor ) {
			visitor.visitR_2();
		}
		
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
		void visit( ClaveTipoFacturaVisitor visitor ) {
			visitor.visitR_3();
		}

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
		void visit( ClaveTipoFacturaVisitor visitor ) {
			visitor.visitR_4();
		}
		
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
		void visit( ClaveTipoFacturaVisitor visitor ) {
			visitor.visitR_5();
		}

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
			alta.setFacturaSimplificadaArt7273(SimplificadaCualificadaType.N);
			alta.setTipoRectificativa(ClaveTipoRectificativaType.I);

			FacturasRectificadas frs = new FacturasRectificadas();

			IDFacturaARType rectified = new IDFacturaARType();
			rectified.setIDEmisorFactura(vc.getCompany().getDocument());
			rectified.setNumSerieFactura(inv.getRectificationInvoiceReference());
			rectified.setFechaExpedicionFactura( VerifactuUtils.toString(inv.getRectificationInvoiceDate()));

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
		void visit( ClaveTipoFacturaVisitor visitor ) {
			visitor.visitF_3();
		}

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
	abstract void visit( ClaveTipoFacturaVisitor visitor );

	public static void fill( VerifactuContext vc, Invoice inv, RegistroFacturacionAltaType alta ) throws InvoiceCommunicationException {
		LinkedList<ClaveTipoFactura> tipoFact = AonCollectionUtils.stream( values() )
			.filter( cr -> cr.accept( inv ))
			.collect(Collectors.toCollection(LinkedList::new));
		if (AonCollectionUtils.isEmpty(tipoFact)) throw new InvoiceCommunicationException( InvoiceCommunicationError.AON_9005 );
		if (AonCollectionUtils.size(tipoFact) > 1) throw new InvoiceCommunicationException( InvoiceCommunicationError.AON_9006 );
		tipoFact.get(0).filler(vc, inv, alta);
	}
	
	public static Destinatarios getDestinatarios(Invoice invoice) {
		PersonaFisicaJuridicaType destinatario = new PersonaFisicaJuridicaType();
		destinatario.setNombreRazon(invoice.getRegistryName());
		
		if((invoice.isNational() || invoice.isIsp() || invoice.isCanCeuMel())
				&& Country.ES.equals(invoice.getRegistryDocumentCountry())) {
			destinatario.setNIF(invoice.getRegistryDocument().replace(" ", ""));	
		} else {
			IDOtroType other = new IDOtroType();
			other.setCodigoPais(CountryType2.valueOf(invoice.getRegistryDocumentCountry().getAeatCode()));		
			other.setIDType(invoice.isIntracommunity() 
					? VerifactuIDType.NIF_IVA.getName()
					: VerifactuIDType.OTRO.getName());
			
			String doc = invoice.getRegistryDocument().replace(" ", "");
			if(AonStringUtils.notEquals(doc.substring(0,2), invoice.getRegistryDocumentCountry().getAeatCode())) {
				boolean isGrecia = Country.GR == invoice.getRegistryDocumentCountry();
				String countryDocument = isGrecia ? "EL" : invoice.getRegistryDocumentCountry().getAeatCode();
				doc = countryDocument + doc;
			}
			other.setID(doc);
			destinatario.setIDOtro(other);
		}
		
		Destinatarios destinatarios = new Destinatarios();
		destinatarios.getIDDestinatario().add(destinatario);
		return destinatarios;
	}
	
	static ClaveTipoFactura from(ClaveTipoFacturaType tipoFactura) {
		if (tipoFactura != null) {
			if (ClaveTipoFacturaType.F_1.equals(tipoFactura)) {
				return F_1;
			} else if (ClaveTipoFacturaType.F_2.equals(tipoFactura)) {
				return F_2;
			} else if (ClaveTipoFacturaType.R_1.equals(tipoFactura)) {
				return R_1;
			} else if (ClaveTipoFacturaType.R_2.equals(tipoFactura)) {
				return R_2;
			} else if (ClaveTipoFacturaType.R_3.equals(tipoFactura)) {
				return R_3;
			} else if (ClaveTipoFacturaType.R_4.equals(tipoFactura)) {
				return R_4;
			} else if (ClaveTipoFacturaType.R_5.equals(tipoFactura)) {
				return R_5;
			} else if (ClaveTipoFacturaType.F_3.equals(tipoFactura)) {
				return F_3;
			}
		}
		return null;
	}
	
	public static interface ClaveTipoFacturaVisitor {
		void visitF_1();
		void visitF_2();
		void visitR_1();
		void visitR_2();
		void visitR_3();
		void visitR_4();
		void visitR_5();
		void visitF_3();
	}

	
}
