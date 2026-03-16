package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CabeceraType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CalificacionOperacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoFacturaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.ClaveTipoRectificativaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CompletaSinDestinatarioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CuponType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DesgloseRectificacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DesgloseType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.DetalleType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.MacrodatoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PrimerRegistroCadenaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SiNoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SimplificadaCualificadaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SistemaInformaticoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SubsanacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.TercerosODestinatarioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;

class VerifactuVentaNacionalSimplificadaConClienteTest extends AbstractVerifactuTest {
	
	@Override protected Environment getEnvironment() { return VERIFACTU_ENV; }

	private Invoice getTestInvoice() {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA_CON_CUSTOMER_SIN_DIRECCION
			.get( getEnvironment() )
			.setId(1);
		return invoice.setReferenceCode(VerifactuTestsUtils.referenceCode(invoice));
	}
	
	@Test
	void ventaNoActTest() throws InvoiceCommunicationException {
		List<Invoice> invoices = AonCollectionUtils.toList( getTestInvoice() );
		invoices.add( getTestInvoice() );
		InvoiceCommunicatorContext  icc = getEnvironment().getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc, getEnvironment().getEnablerData(icc.getConfig()));
		assertInvoice( vc );
	}
		
	@Test
	void ventaActGeneralTest() throws InvoiceCommunicationException {
		List<Invoice> invoices = new LinkedList<>();
		Invoice invoice = getTestInvoice();
		invoice.setActivity(InvoiceTypes.getActivityGeneral(getEnvironment().getCtx(), getEnvironment().getDomainId()));
		invoices.add( invoice );
		InvoiceCommunicatorContext  icc = getEnvironment().getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc, getEnvironment().getEnablerData(icc.getConfig()));
		assertInvoice( vc );
	}
	
	@Test
	void ventaFacesTest() throws InvoiceCommunicationException {
		Invoice facesInvoice = VerifactuTestsUtils.toFacesInvoice( getTestInvoice() );
		List<Invoice> invoices = AonCollectionUtils.toList( facesInvoice );
		InvoiceCommunicatorContext  icc = getEnvironment().getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc, getEnvironment().getEnablerData(icc.getConfig()));
		assertInvoice( vc );
	}

	private void assertInvoice( VerifactuContext vc ) throws InvoiceCommunicationException {
		RegFactuSistemaFacturacion rfsf = Invoice2Verifactu.build(getEnvironment().getCtx(), InvoiceCommunicationType.VERIFACTU,vc, EMPTY_VERIFACTU_PHASE_LISTENER);
		assertNotNull( rfsf );
		
		// ------------------------ CabeceraType asserts
		CabeceraType cab = rfsf.getCabecera();
		assertNotNull( cab );
	    PersonaFisicaJuridicaESType obligadoEmision = cab.getObligadoEmision();
	    assertNotNull( obligadoEmision );
	    assertEquals(vc.getCompany().getDocument() , obligadoEmision.getNIF());
	    assertEquals(vc.getCompany().getName() , obligadoEmision.getNombreRazon());
	    PersonaFisicaJuridicaESType representante = cab.getRepresentante();
	    assertNull( representante );
	    CabeceraType.RemisionVoluntaria remisionVoluntaria = cab.getRemisionVoluntaria();
	    assertNull( remisionVoluntaria );
	    CabeceraType.RemisionRequerimiento remisionRequerimiento = cab.getRemisionRequerimiento();
	    assertNull( remisionRequerimiento );
	    // ------------------------ 

		// ------------------------ RegistroFacturaType asserts
	    List<RegistroFacturaType> facturas = rfsf.getRegistroFactura();
	    assertEquals(vc.invoiceCount() , facturas.size() );
	    Invoice i = vc.invoiceStream().findFirst().orElse(null);
	    assertNotNull( i );
	    RegistroFacturaType rft = facturas.get(0);
	    assertNotNull( rft );
	    RegistroFacturacionAltaType rfat = rft.getRegistroAlta();
	    assertNotNull( rfat );
	    
	    String idVersion = rfat.getIDVersion();
	    assertNotNull( idVersion );
	    assertEquals(Invoice2Verifactu.VERSION , idVersion );
	    
	    IDFacturaExpedidaType idFactura = rfat.getIDFactura();
	    assertNotNull( idFactura );
	    assertEquals(vc.getCompany().getDocument() , idFactura.getIDEmisorFactura() );
	    assertEquals(i.getReferenceCode() , idFactura.getNumSerieFactura() );
	    Date expDate = i.getExpDate() != null ? i.getExpDate() : new Date();
	    assertEquals(VerifactuUtils.toString(expDate), idFactura.getFechaExpedicionFactura() );
	    
	    String refExterna = rfat.getRefExterna();
	    assertNotNull( refExterna );
	    assertEquals(AonNumberUtils.toString(i.getId()), refExterna );
	    
	    String nombreRazonEmisor = rfat.getNombreRazonEmisor();
	    assertNotNull( nombreRazonEmisor );
	    assertEquals(vc.getCompany().getName() , nombreRazonEmisor );
	    
	    SubsanacionType subsanacion = rfat.getSubsanacion();
	    assertNotNull( subsanacion );
	    assertEquals( SubsanacionType.N, subsanacion );
	    
	    RechazoPrevioType rechazoPrevio = rfat.getRechazoPrevio();
	    assertNotNull( rechazoPrevio );
	    assertEquals( RechazoPrevioType.N, rechazoPrevio );
	    
	    ClaveTipoFacturaType tipoFactura = rfat.getTipoFactura();
	    assertNotNull( tipoFactura );
	    assertEquals( ClaveTipoFacturaType.F_2, tipoFactura );
	    
	    ClaveTipoRectificativaType tipoRectificativa = rfat.getTipoRectificativa();
	    assertNull( tipoRectificativa );
	    
	    RegistroFacturacionAltaType.FacturasRectificadas facturasRectificadas = rfat.getFacturasRectificadas();
	    assertNull( facturasRectificadas );
	    
	    RegistroFacturacionAltaType.FacturasSustituidas facturasSustituidas = rfat.getFacturasSustituidas();
	    assertNull( facturasSustituidas );
	    
	    DesgloseRectificacionType importeRectificacion = rfat.getImporteRectificacion();
	    assertNull( importeRectificacion );
	    
	    String fechaOperacion = rfat.getFechaOperacion();
	    assertNotNull( fechaOperacion );
	    assertEquals( VerifactuUtils.toString(i.getIssueDate() ), fechaOperacion);
	    
	    String descripcionOperacion = rfat.getDescripcionOperacion();
	    assertNotNull( descripcionOperacion );
	    assertEquals( Invoice2Verifactu.NO_SERVICE_DESCRIPTION , descripcionOperacion );
	    
	    SimplificadaCualificadaType facturaSimplificadaArt7273 = rfat.getFacturaSimplificadaArt7273();
	    assertNotNull( facturaSimplificadaArt7273 );
	    assertEquals( SimplificadaCualificadaType.N , facturaSimplificadaArt7273 );

	    CompletaSinDestinatarioType facturaSinIdentifDestinatarioArt61D = rfat.getFacturaSinIdentifDestinatarioArt61D();
	    assertNotNull( facturaSinIdentifDestinatarioArt61D );
	    assertEquals( CompletaSinDestinatarioType.S , facturaSinIdentifDestinatarioArt61D );
	    
	    MacrodatoType macrodato = rfat.getMacrodato();
	    assertNotNull( macrodato );
	    assertEquals(MacrodatoType.N,macrodato);
	    
	    TercerosODestinatarioType emitidaPorTerceroODestinatario = rfat.getEmitidaPorTerceroODestinatario();
	    assertNull( emitidaPorTerceroODestinatario );
	    
	    PersonaFisicaJuridicaType tercero = rfat.getTercero();
	    assertNull( tercero );
	    
	    RegistroFacturacionAltaType.Destinatarios destinatarios = rfat.getDestinatarios();
	    assertNull( destinatarios );
	    
	    CuponType cupon = rfat.getCupon();
	    assertNotNull( cupon );
	    assertEquals(CuponType.N,cupon);

	    DesgloseType desglose = rfat.getDesglose();
	    assertNotNull( desglose );
	    List<DetalleType> listaDesglose = desglose.getDetalleDesglose();
	    assertNotNull( listaDesglose );
	    assertEquals( 1, listaDesglose.size() );
	    DetalleType dt = listaDesglose.get(0);
	    assertNotNull( dt );
	    assertEquals( TipoImpuesto.IVA.getValue() , dt.getImpuesto() );
	    assertEquals( ClaveRegimen.C01_NATIONAL.getValue() , dt.getClaveRegimen() );
	    assertEquals( CalificacionOperacionType.S_1 , dt.getCalificacionOperacion() );
	    assertNull( dt.getOperacionExenta() );		
	    assertEquals( "21" , dt.getTipoImpositivo());
	    assertEquals( "100" , dt.getBaseImponibleOimporteNoSujeto());
	    assertNull( dt.getBaseImponibleACoste() );
	    assertEquals( "21" , dt.getCuotaRepercutida());
	    assertNull( dt.getTipoRecargoEquivalencia() );		
	    assertNull( dt.getCuotaRecargoEquivalencia() );
	    
	    assertEquals( "21" , rfat.getCuotaTotal());
	    assertEquals( "121" , rfat.getImporteTotal());
	    
	    RegistroFacturacionAltaType.Encadenamiento encadenamiento = rfat.getEncadenamiento();
	    assertNotNull( encadenamiento );
	    assertEquals( PrimerRegistroCadenaType.S , encadenamiento.getPrimerRegistro());
	    assertNull( encadenamiento.getRegistroAnterior() );
	    
	    SistemaInformaticoType sistemaInformatico = rfat.getSistemaInformatico();
	    assertNotNull( sistemaInformatico );
	    assertEquals( "B01487271" , sistemaInformatico.getNIF());
	    assertEquals( "AON SOLUTIONS SL" , sistemaInformatico.getNombreRazon());
	    assertEquals( "01" , sistemaInformatico.getIdSistemaInformatico());
	    assertEquals( "aonSolutions" , sistemaInformatico.getNombreSistemaInformatico());
	    assertEquals( "9.23" , sistemaInformatico.getVersion());
	    assertEquals( vc.getCompany().getDocument() + "-" + getEnvironment().getDomainId() , sistemaInformatico.getNumeroInstalacion());
	    assertEquals( SiNoType.N , sistemaInformatico.getTipoUsoPosibleSoloVerifactu());
	    assertEquals( SiNoType.S , sistemaInformatico.getTipoUsoPosibleMultiOT());
	    assertEquals( SiNoType.S , sistemaInformatico.getIndicadorMultiplesOT());
				
	    assertNotNull( rfat.getFechaHoraHusoGenRegistro() );
	    assertNull( rfat.getNumRegistroAcuerdoFacturacion() );
	    assertNull( rfat.getIdAcuerdoSistemaInformatico() );
	    assertEquals( "01", rfat.getTipoHuella() );
	    assertNotNull( rfat.getHuella() );
	    assertNull( rfat.getSignature() );
	    
	}
	
	
}
