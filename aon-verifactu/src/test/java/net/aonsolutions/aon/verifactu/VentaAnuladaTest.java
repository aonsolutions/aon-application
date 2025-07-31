package net.aonsolutions.aon.verifactu;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationOperation;
import com.esferalia.aon.watson.util.AonNumberUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.CabeceraType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.GeneradoPorType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaExpedidaBajaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaESType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PersonaFisicaJuridicaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.PrimerRegistroCadenaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioAnulacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAltaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAnulacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RegistroFacturacionAnulacionType.Encadenamiento;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SiNoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SinRegistroPrevioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SistemaInformaticoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegistroFacturaType;
import net.aonsolutions.aon.verifactu.exceptions.VerifactuException;

class VentaAnuladaTest extends AbstractVerifactuTest {
	
	private Invoice getTestInvoice() {
		return InvoiceTypes.Invoices.VENTA_ANULADA
			.get( ctx )
			.setId(1);
	}
	
	@Test
	void ventaAnuladaNoAct() throws VerifactuException {
		Company c = company();
		List<Invoice> invoices = new LinkedList<>();
		invoices.add( getTestInvoice() );
		VerifactuContext vc = new VerifactuContext()
			.setConfig( config() )
			.setCompany( c )
			.setInvoices(invoices)
			.setOperation(InvoiceCommunicationOperation.ANNULMENT);
		assertInvoice( vc );
	}
	
	private void assertInvoice( VerifactuContext vc ) throws VerifactuException {
		RegFactuSistemaFacturacion rfsf = Invoice2Verifactu.build(vc);
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
	    assertEquals(vc.getInvoices().size() , facturas.size() );
	    Invoice i = vc.getInvoices().get(0);
	    assertNotNull( i );
	    RegistroFacturaType rft = facturas.get(0);
	    assertNotNull( rft );
	    RegistroFacturacionAltaType alta = rft.getRegistroAlta();
	    assertNull( alta );
	    
	    RegistroFacturacionAnulacionType anul = rft.getRegistroAnulacion();
	    assertNotNull( anul );
	    
	    
	    String idVersion = anul.getIDVersion();
	    assertNotNull( idVersion );
	    assertEquals(Invoice2Verifactu.VERSION , idVersion );
	    
	    IDFacturaExpedidaBajaType idFactura = anul.getIDFactura();
	    assertNotNull( idFactura );
	    assertEquals(vc.getCompany().getDocument() , idFactura.getIDEmisorFacturaAnulada() );
	    assertEquals(i.getReferenceCode() , idFactura.getNumSerieFacturaAnulada() );
	    assertEquals(VerifactuUtils.toString(i.getExpDate() ), idFactura.getFechaExpedicionFacturaAnulada() );
	    
	    String refExterna = anul.getRefExterna();
	    assertNotNull( refExterna );
	    assertEquals(AonNumberUtils.toString(i.getId()), refExterna );
	    
	    SinRegistroPrevioType sinRegistroPrevio = anul.getSinRegistroPrevio();
	    assertNotNull( sinRegistroPrevio );
	    assertEquals( SinRegistroPrevioType.N, sinRegistroPrevio );

	    RechazoPrevioAnulacionType rechazoPrevio = anul.getRechazoPrevio();
	    assertNotNull( rechazoPrevio );
	    assertEquals( RechazoPrevioAnulacionType.N, rechazoPrevio );

	    GeneradoPorType generadoPor = anul.getGeneradoPor();
	    assertNull( generadoPor );
	    
	    PersonaFisicaJuridicaType generador = anul.getGenerador();
	    assertNull( generador );
	    
	    Encadenamiento encadenamiento = anul.getEncadenamiento();
	    assertNotNull( encadenamiento );
	    assertEquals( PrimerRegistroCadenaType.S , encadenamiento.getPrimerRegistro());
	    assertNull( encadenamiento.getRegistroAnterior() );
	    
	    SistemaInformaticoType sistemaInformatico = anul.getSistemaInformatico();
	    assertNotNull( sistemaInformatico );
	    assertEquals( "B01487271" , sistemaInformatico.getNIF());
	    assertEquals( "AON SOLUTIONS SL" , sistemaInformatico.getNombreRazon());
	    assertEquals( "01" , sistemaInformatico.getIdSistemaInformatico());
	    assertEquals( "aonSolutions" , sistemaInformatico.getNombreSistemaInformatico());
	    assertEquals( "9.23" , sistemaInformatico.getVersion());
	    assertEquals( vc.getCompany().getDocument() + "-" + DOMAIN_ID , sistemaInformatico.getNumeroInstalacion());
	    assertEquals( SiNoType.N , sistemaInformatico.getTipoUsoPosibleSoloVerifactu());
	    assertEquals( SiNoType.S , sistemaInformatico.getTipoUsoPosibleMultiOT());
	    assertEquals( SiNoType.S , sistemaInformatico.getIndicadorMultiplesOT());
				
	    assertNotNull( anul.getFechaHoraHusoGenRegistro() );
	    assertEquals( "01", anul.getTipoHuella() );
	    assertNotNull( anul.getHuella() );
	    assertNull( anul.getSignature() );
	    
	}
	
}
