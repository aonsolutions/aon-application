package net.aonsolutions.aon.verifactu;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.soap.SOAPException;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.VerifactuConfiguration;
import com.esferalia.aon.watson.server.AonDateUtils;
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
import net.aonsolutions.aon.verifactu.Invoice2Verifactu.TipoImpuesto;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuException;
import net.aonsolutions.aon.verifactu.utils.XMLUtils;

class VentaISPTest {
	// En el entorno de pruebas los XML de entrada se deben ejecutar en la siguente dirección:
	private String TEST_URL = "https://prewww1.aeat.es/wlpl/TIKE-CONT/ws/SistemaFacturacion/ValRegistroNoVerifactu";
	
	@Test
	void invoiceTest() throws VerifactuException, JAXBException, SOAPException, IOException {
		Company c = company();
		List<Invoice> invoices = new LinkedList<>();
		invoices.add( InvoiceTypes.VENTA_ISP() );
		
		VerifactuContext vc = new VerifactuContext()
			.setConfig( config() )
			.setCompany( c )
			.setInvoices(invoices);
		RegFactuSistemaFacturacion rfsf = Invoice2Verifactu.build(vc);
		assertNotNull( rfsf );
		
		// ------------------------ CabeceraType asserts
		CabeceraType cab = rfsf.getCabecera();
		assertNotNull( cab );
	    PersonaFisicaJuridicaESType obligadoEmision = cab.getObligadoEmision();
	    assertNotNull( obligadoEmision );
	    assertEquals(c.getDocument() , obligadoEmision.getNIF());
	    assertEquals(c.getName() , obligadoEmision.getNombreRazon());
	    PersonaFisicaJuridicaESType representante = cab.getRepresentante();
	    assertNull( representante );
	    CabeceraType.RemisionVoluntaria remisionVoluntaria = cab.getRemisionVoluntaria();
	    assertNull( remisionVoluntaria );
	    CabeceraType.RemisionRequerimiento remisionRequerimiento = cab.getRemisionRequerimiento();
	    assertNull( remisionRequerimiento );
	    // ------------------------ 

		// ------------------------ RegistroFacturaType asserts
	    List<RegistroFacturaType> facturas = rfsf.getRegistroFactura();
	    assertEquals(invoices.size() , facturas.size() );
	    Invoice i = invoices.get(0);
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
	    assertEquals(c.getDocument() , idFactura.getIDEmisorFactura() );
	    assertEquals(i.getReferenceCode() , idFactura.getNumSerieFactura() );
	    assertEquals(AonDateUtils.format(i.getExpDate(), Invoice2Verifactu.DATE_FORMAT ), idFactura.getFechaExpedicionFactura() );
	    
	    String refExterna = rfat.getRefExterna();
	    assertNotNull( refExterna );
	    assertEquals(AonNumberUtils.toString(i.getId()), refExterna );
	    
	    String nombreRazonEmisor = rfat.getNombreRazonEmisor();
	    assertNotNull( nombreRazonEmisor );
	    assertEquals(c.getName() , nombreRazonEmisor );
	    
	    SubsanacionType subsanacion = rfat.getSubsanacion();
	    assertNotNull( subsanacion );
	    assertEquals( SubsanacionType.N, subsanacion );
	    
	    RechazoPrevioType rechazoPrevio = rfat.getRechazoPrevio();
	    assertNotNull( rechazoPrevio );
	    assertEquals( RechazoPrevioType.N, rechazoPrevio );
	    
	    ClaveTipoFacturaType tipoFactura = rfat.getTipoFactura();
	    assertNotNull( tipoFactura );
	    assertEquals( ClaveTipoFacturaType.F_1, tipoFactura );
	    
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
	    assertEquals( AonDateUtils.format(i.getIssueDate(), Invoice2Verifactu.DATE_FORMAT ), fechaOperacion);
	    
	    String descripcionOperacion = rfat.getDescripcionOperacion();
	    assertNotNull( descripcionOperacion );
	    assertEquals( Invoice2Verifactu.NO_SERVICE_DESCRIPTION , descripcionOperacion );
	    
	    SimplificadaCualificadaType facturaSimplificadaArt7273 = rfat.getFacturaSimplificadaArt7273();
	    assertNotNull( facturaSimplificadaArt7273 );
	    assertEquals( SimplificadaCualificadaType.N , facturaSimplificadaArt7273 );

	    CompletaSinDestinatarioType facturaSinIdentifDestinatarioArt61D = rfat.getFacturaSinIdentifDestinatarioArt61D();
	    assertNotNull( facturaSinIdentifDestinatarioArt61D );
	    assertEquals( CompletaSinDestinatarioType.N , facturaSinIdentifDestinatarioArt61D );
	    
	    MacrodatoType macrodato = rfat.getMacrodato();
	    assertNotNull( macrodato );
	    assertEquals(MacrodatoType.N,macrodato);
	    
	    TercerosODestinatarioType emitidaPorTerceroODestinatario = rfat.getEmitidaPorTerceroODestinatario();
	    assertNull( emitidaPorTerceroODestinatario );
	    
	    PersonaFisicaJuridicaType tercero = rfat.getTercero();
	    assertNull( tercero );
	    
	    RegistroFacturacionAltaType.Destinatarios destinatarios = rfat.getDestinatarios();
	    assertNotNull( destinatarios );
	    List<PersonaFisicaJuridicaType> IDDestinatario = destinatarios.getIDDestinatario();
	    assertNotNull( IDDestinatario );
	    assertEquals( 1, IDDestinatario.size() );
	    PersonaFisicaJuridicaType destinatario = IDDestinatario.get(0);
	    assertNotNull( destinatario );
	    assertEquals( i.getRegistryDocument(), destinatario.getNIF() );
	    assertEquals( i.getRegistryName(), destinatario.getNombreRazon() );
	    assertNull( destinatario.getIDOtro() );
	    
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
	    assertEquals( ClaveRegimen.C01_ISP.getValue() , dt.getClaveRegimen() );
	    assertEquals( CalificacionOperacionType.S_2 , dt.getCalificacionOperacion() );
	    assertNull( dt.getOperacionExenta() );		
	    assertEquals( "21" , dt.getTipoImpositivo());
	    assertEquals( "100" , dt.getBaseImponibleOimporteNoSujeto());
	    assertNull( dt.getBaseImponibleACoste() );
	    assertEquals( "21" , dt.getCuotaRepercutida());
	    assertNull( dt.getTipoRecargoEquivalencia() );		
	    assertNull( dt.getCuotaRecargoEquivalencia() );
	    
	    assertEquals( "21" , rfat.getCuotaTotal());
	    assertEquals( "100" , rfat.getImporteTotal());
	    
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
	    assertEquals( vc.getCompany().getDocument() + "-1" , sistemaInformatico.getNumeroInstalacion());
	    assertEquals( SiNoType.N , sistemaInformatico.getTipoUsoPosibleSoloVerifactu());
	    assertEquals( SiNoType.S , sistemaInformatico.getTipoUsoPosibleMultiOT());
	    assertEquals( SiNoType.S , sistemaInformatico.getIndicadorMultiplesOT());
				
	    assertNotNull( rfat.getFechaHoraHusoGenRegistro() );
	    assertNull( rfat.getNumRegistroAcuerdoFacturacion() );
	    assertNull( rfat.getIdAcuerdoSistemaInformatico() );
	    assertEquals( "01", rfat.getTipoHuella() );
	    assertNotNull( rfat.getHuella() );
	    assertNull( rfat.getSignature() );
	    
	    if ( vc.getConfig().getCertificate() != null) {
	    	VerifactuResponse response = XMLUtils.post(
    			vc.getConfig().getCertificate()
    			, TEST_URL
    			, XMLUtils.soapMarshal(rfsf, RegFactuSistemaFacturacion.class));
	    	assertNotNull( response );
	    	assertFalse( response.isError() );
	    }
	    
	}
	
	private VerifactuConfiguration config() {
		return new VerifactuConfiguration()
			.setActive(true)
			.setTest(true)
			.setDefaultCertificate(null)
			.setCertificate(null)
			.setIncludeDate(null)
			.setRegistryDate(null)
		;
	}
	private Company company() {
		Company company = new Company();
		company.setDocument("11111111H");
		company.setName("Verifactu Test Company S.L.");
		company.setDomain(new Domain().setId(1));
		return company;
	}
	
}
