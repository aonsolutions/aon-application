package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.xml.soap.SOAPMessage;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.EstadoRegistroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaRegFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.OperacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SubsanacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.TipoOperacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;

class VerifactuCommunicationTest extends AbstractVerifactuTest {
		
	@Override protected Environment getEnvironment() { return VERIFACTU_ENV; }

	@Test
	void venta_nacional_simpleAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}

	@Test
	void venta_nacional_simplificadaAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	@Test
	void venta_nacional_simplificada_con_customer_sin_direccionAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA_CON_CUSTOMER_SIN_DIRECCION.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	@Test
	void venta_nacional_suplidosAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SUPLIDOS.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	@Test
	void venta_nacional_rectificativa_simpleAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	@Test
	void venta_nacional_rectificativa_simplificadaAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLIFICADA.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	@Test
	void venta_ispAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ISP.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	@Test
	void venta_nacional_reAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	@Test
	void venta_nacional_irpf_professionalAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_IRPF_PROFESSIONAL.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	@Test
	void venta_intracomunitaria_serviciosAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_INTRACOMUNITARIA_SERVICIOS.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	@Test
	void venta_intracomunitaria_no_serviciosAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_INTRACOMUNITARIA.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}

	@Test
	void venta_extracomunitariaAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_EXTRACOMUNITARIA.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}

	@Test
	void venta_extracomunitaria_servicioAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_EXTRACOMUNITARIA_SERVICIO.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}

	@Test
	void venta_can_ceu_melAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_CAN_CEU_MEL.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}

	@Test
	void venta_can_ceu_mel_servicioAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_CAN_CEU_MEL_SERVICIO.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}

	@Test
	void venta_nacional_exenta_e1AEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_EXENTA_E1.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	@Test
	void venta_anuladaAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ANULADA.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	@Test
	void venta_nacional_simple_criterio_cajaAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE_CRITERIO_CAJA.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	@Test
	void venta_nacional_cliente_no_censadoAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_CLIENTE_NO_CENSADO.get(getEnvironment()).setId(1);
		RespuestaExpedidaType ret = communicateInvalid(invoice);
		assertNotNull(ret);
		
		BigInteger codigoErrorRegistro = ret.getCodigoErrorRegistro();
		assertNotNull(codigoErrorRegistro);
		assertTrue(BigInteger.valueOf(1110).equals(codigoErrorRegistro)
				|| BigInteger.valueOf(1239).equals(codigoErrorRegistro));
		
		String descripcionErrorRegistro = ret.getDescripcionErrorRegistro();
		assertNotNull(descripcionErrorRegistro);
		assertTrue(AonStringUtils.startsWith(descripcionErrorRegistro, "Error en el bloque Destinatario.. El NIF no está identificado en el censo de la AEAT"));
	}
	
	@Test
	void venta_nacional_cliente_cedillaAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_CLIENTE_CEDILLA.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}
	
	
	@Test
	void venta_nacional_cliente_apostrofeAEATTest() throws InvoiceCommunicationException {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_CLIENTE_APOSTOFRE.get(getEnvironment()).setId(1);
		communicateValid(invoice);
	}


	private void communicateValid(Invoice invoice) throws InvoiceCommunicationException {
		invoice.setSeries(VerifactuTestsUtils.series(getEnvironment().getCtx(), invoice.isRectifier()));
		invoice.setNumber(VerifactuTestsUtils.number());
		invoice.setReferenceCode(VerifactuTestsUtils.referenceCode(invoice));
		RespuestaExpedidaType ret = communicateCommon(invoice);
		EstadoRegistroType estado = ret.getEstadoRegistro();
		assertNotNull(estado);
		
		if (estado == EstadoRegistroType.ACEPTADO_CON_ERRORES) {
			BigInteger codigoErrorRegistro = ret.getCodigoErrorRegistro();
			String descripcionErrorRegistro = ret.getDescripcionErrorRegistro();
			
			assertNotNull(codigoErrorRegistro);
			assertEquals( BigInteger.valueOf(2007) , codigoErrorRegistro);
			
			assertNotNull(descripcionErrorRegistro);
			assertTrue(AonStringUtils.startsWith(descripcionErrorRegistro, "No debe informarse como primer registro"));
		} else if (estado == EstadoRegistroType.CORRECTO) {
			BigInteger codigoErrorRegistro = ret.getCodigoErrorRegistro();
			assertNull(codigoErrorRegistro);
			String descripcionErrorRegistro = ret.getDescripcionErrorRegistro();
			assertNull(descripcionErrorRegistro);
		} else {
			fail( "Estado no correcto ["+estado+"] "
				+ " (" + ret.getCodigoErrorRegistro() + ") "
				+ ret.getDescripcionErrorRegistro() );
		}
		
		assertNull(ret.getRegistroDuplicado());
	}
	
	private RespuestaExpedidaType communicateInvalid(Invoice invoice) throws InvoiceCommunicationException {
		invoice.setSeries(VerifactuTestsUtils.series(getEnvironment().getCtx(), invoice.isRectifier()));
		invoice.setNumber(VerifactuTestsUtils.number());
		invoice.setReferenceCode(VerifactuTestsUtils.referenceCode(invoice));
		RespuestaExpedidaType ret = communicateCommon(invoice);
		EstadoRegistroType estado = ret.getEstadoRegistro();
		assertNotNull(estado);
		
		if (estado != EstadoRegistroType.INCORRECTO) {
			fail( "Estado Correcto");
		}
		assertNull(ret.getRegistroDuplicado());
		return ret;
	}
	
	private VerifactuContext doCommunicate(Invoice invoice) throws InvoiceCommunicationException {
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext  icc = getEnvironment().getInvoiceCommunicatorContextWithCertificate(invoices);
		VerifactuContext vc = new VerifactuContext(icc, getEnvironment().getEnablerData(icc.getConfig()));
		RegFactuSistemaFacturacion request = Invoice2Verifactu.build(getEnvironment().getCtx(), InvoiceCommunicationType.VERIFACTU,vc, EMPTY_VERIFACTU_PHASE_LISTENER);
		vc.setRequest( request );
		Document document = VerifactuXMLUtils.toDocument(request, RegFactuSistemaFacturacion.class);
		vc.setRequestBytes(VerifactuXMLUtils.toBytes(document));
		SOAPMessage requestMessage = VerifactuXMLUtils.soapMarshal(document);
		vc.setResponse( VerifactuXMLUtils.post(vc.getConfig().getCertificate(), VerifactuUri.getUrlEmision(true),requestMessage));

//		vc.setRequest(Invoice2Verifactu.build(vc) );
//		SOAPMessage request = VerifactuXMLUtils.soapMarshal(
//			vc.getRequest(), 
//			RegFactuSistemaFacturacion.class);
//		vc.setResponse( VerifactuXMLUtils.post(vc.getConfig().getCertificate(), VerifactuUri.getUrlEmision(true), request));
	
		assertNotNull(vc.getResponse());
		return vc;
	}
	private RespuestaExpedidaType communicateCommon(Invoice invoice) throws InvoiceCommunicationException {
		VerifactuContext vc = doCommunicate(invoice);
		
		RespuestaRegFactuSistemaFacturacionType resp = vc.getResponse().getResponse();
		assertNotNull(resp);
		assertNotNull(resp.getRespuestaLinea());
		assertFalse(resp.getRespuestaLinea().isEmpty());
		assertEquals(1, resp.getRespuestaLinea().size());
		RespuestaExpedidaType ret = resp.getRespuestaLinea().get(0);
		
		IDFacturaExpedidaType idFactura = ret.getIDFactura();
	    assertNotNull( idFactura );
	    assertEquals(vc.getCompany().getDocument() , idFactura.getIDEmisorFactura() );
	    assertEquals(invoice.getReferenceCode() , idFactura.getNumSerieFactura() );
	    Date expDate = invoice.getExpDate() != null ? invoice.getExpDate() : new Date();
	    assertEquals(VerifactuUtils.toString(expDate), idFactura.getFechaExpedicionFactura() );
		
		OperacionType operacion = ret.getOperacion();
		assertNotNull(operacion);
		assertNotNull(operacion.getTipoOperacion());
		assertEquals(TipoOperacionType.ALTA, operacion.getTipoOperacion());
		assertNotNull(operacion.getSubsanacion());
		assertEquals(SubsanacionType.N, operacion.getSubsanacion());
		assertNotNull(operacion.getRechazoPrevio());
		assertEquals(RechazoPrevioType.N, operacion.getRechazoPrevio());
		assertNull(operacion.getSinRegistroPrevio());
		
		assertNotNull(ret.getRefExterna());
		assertEquals("1", ret.getRefExterna());
		return ret;
	}
	
}
