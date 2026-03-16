package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.watson.util.AonCollectionUtils;
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

class VerifactuCanariasVentaAnuladaTest extends AbstractVerifactuTest {
	
	@Override protected Environment getEnvironment() { return VERIFACTU_CANARIAS_ENV; }

	private Invoice getTestInvoice() {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ANULADA
			.get( getEnvironment() )
			.setId(1);
		return invoice.setReferenceCode(VerifactuTestsUtils.referenceCode(invoice));
	}
	
	@Test
	void ventaAnuladaNoAct() throws InvoiceCommunicationException {
		List<Invoice> invoices = AonCollectionUtils.toList( getTestInvoice() );
		InvoiceCommunicatorContext  icc = getEnvironment().getInvoiceCommunicatorContext(invoices);
		VerifactuContext vc = new VerifactuContext(icc, getEnvironment().getEnablerData(icc.getConfig()))
			.setOperation(InvoiceCommunicationOperation.ANNULMENT);
		assertInvoice( vc );
	}
	
	private void assertInvoice( VerifactuContext vc) throws InvoiceCommunicationException {
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
	    Date expDate = i.getExpDate() != null ? i.getExpDate() : new Date();
	    assertEquals(VerifactuUtils.toString(expDate), idFactura.getFechaExpedicionFacturaAnulada() );
	    
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
	    assertEquals( vc.getCompany().getDocument() + "-" + getEnvironment().getDomainId() , sistemaInformatico.getNumeroInstalacion());
	    assertEquals( SiNoType.N , sistemaInformatico.getTipoUsoPosibleSoloVerifactu());
	    assertEquals( SiNoType.S , sistemaInformatico.getTipoUsoPosibleMultiOT());
	    assertEquals( SiNoType.S , sistemaInformatico.getIndicadorMultiplesOT());
				
	    assertNotNull( anul.getFechaHoraHusoGenRegistro() );
	    assertEquals( "01", anul.getTipoHuella() );
	    assertNotNull( anul.getHuella() );
	    assertNull( anul.getSignature() );
	    
	}
	
}
