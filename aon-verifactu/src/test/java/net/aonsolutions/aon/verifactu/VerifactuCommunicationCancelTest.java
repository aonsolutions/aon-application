package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceDataName;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationError;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationPhaseListener;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataRequestDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceCommunicationTrackingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.EstadoRegistroType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaRegFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.IDFacturaExpedidaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.OperacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.RechazoPrevioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.SinRegistroPrevioType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministroinformacion.TipoOperacionType;

class VerifactuCommunicationCancelTest extends AbstractVerifactuTest {
	
	private static InvoiceCommunicationPhaseListener PHASE_LISTENER = new InvoiceCommunicationPhaseListener() {
		@Override
		public void beforeAll(AONContext ctx, InvoiceCommunicatorContext icc) throws InvoiceCommunicationException {
			// Nothing
		}
		@Override
		public void beforeInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) {
			// Nothing
		}
		@Override
		public void afterRightInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) {
			// Nothing
		}
		
		@Override
		public void afterWrongInvoice(AONContext ctx, InvoiceCommunicatorContext icc, Invoice invoice) throws InvoiceCommunicationException{
			// Nothing
		}
		
		@Override
		public void afterAll(AONContext ctx, InvoiceCommunicatorContext icc) throws InvoiceCommunicationException {
			if (icc.isFailOnWrongValidation() 
			 && icc.invoiceStream().filter( Invoice::hasMessages ).anyMatch( Invoice::hasERRMessages )) {
				// TRACE _-- borrar
				icc.invoiceStream()
					.filter( Invoice::hasMessages )
					.flatMap( Invoice::messageStream )
					.forEach( m -> System.out.println( m.getLevel() + " " + m.getCode() + " - " + m.getMessage() ));
				// ----------------
				throw new InvoiceCommunicationException(InvoiceCommunicationError.AON_0024);
			}
		}
	};

	@Test
	void venta_nacional_simple_test() {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}

	@Test
	void venta_nacional_simplificada_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}

	@Test
	void venta_nacional_simplificada_con_customer_sin_direccion_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA_CON_CUSTOMER_SIN_DIRECCION.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_nacional_suplidos_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SUPLIDOS.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}

	@Test
	void venta_nacional_rectificativa_simple_test()  {
		Invoice invoice1 = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(ctx, DOMAIN_ID);
		invoice1 = save(invoice1);

		Invoice invoice2 = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get(ctx, DOMAIN_ID);
		invoice2.setRectificationInvoice(invoice1.getId());
		invoice2.setRectificationInvoiceDate(invoice1.getIssueDate());
		invoice2.setRectificationInvoiceNumber(invoice1.getNumber());
		invoice2.setRectificationInvoiceSeries(invoice1.getSeries());
		invoice2.setRectificationInvoiceReference(invoice1.getReferenceCode());
		saveAndCancel(invoice2);
	}

	@Test
	void venta_nacional_rectificativa_simplificada_test()  {
		Invoice invoice1 = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA.get(ctx, DOMAIN_ID);
		invoice1 = save(invoice1);
		
		Invoice invoice2 = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLIFICADA.get(ctx, DOMAIN_ID);
		invoice2.setRectificationInvoice(invoice1.getId());
		invoice2.setRectificationInvoiceDate(invoice1.getIssueDate());
		invoice2.setRectificationInvoiceNumber(invoice1.getNumber());
		invoice2.setRectificationInvoiceSeries(invoice1.getSeries());
		invoice2.setRectificationInvoiceReference(invoice1.getReferenceCode());
		saveAndCancel(invoice2);
	}

	@Test
	void venta_isp_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ISP.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_nacional_re_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_nacional_irpf_professional_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_IRPF_PROFESSIONAL.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_intracomunitaria_servicios_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_INTRACOMUNITARIA_SERVICIOS.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}
 	

	@Test
	void venta_intracomunitaria_no_servicios_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_INTRACOMUNITARIA.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_extracomunitaria_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_EXTRACOMUNITARIA.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_extracomunitaria_servicio_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_EXTRACOMUNITARIA_SERVICIO.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}

	@Test
	void venta_can_ceu_mel_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_CAN_CEU_MEL.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}

	@Test
	void venta_can_ceu_mel_servicio_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_CAN_CEU_MEL_SERVICIO.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}

	@Test
	void venta_nacional_exenta_e1_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_EXENTA_E1.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_anulada_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ANULADA.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_nacional_simple_criterio_caja_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE_CRITERIO_CAJA.get(ctx, DOMAIN_ID);
		saveAndCancel(invoice);
	}

	private Invoice saveAndCancel(Invoice invoice) {
		invoice.setSeries(VerifactuTestsUtils.series(ctx, invoice.isRectifier(), true));
		invoice = save(invoice);
		cancel(invoice);
		return invoice;
	}

	private Invoice save(Invoice invoice) {
		return ctx.getDslContext().transactionResult(config -> {
			List<Invoice> invoices = AonCollectionUtils.toList(invoice);
			InvoiceCommunicatorContext  icc = getInvoiceCommunicatorContext(invoices);
			icc.setConfig(configWithCertificate());
			Invoice inv = InvoiceDAO.save(ctx, invoice);
			invoices = AonCollectionUtils.toList(inv);
			VerifactuContext vc = VERIFACTU.accept(ctx, icc, PHASE_LISTENER);
			System.out.println("*****");
			AonIOUtils.write(vc.getResponse().getBytes(), System.out );
			System.out.println();
			System.out.println("*****");
			System.out.println( vc.getResponse().getBytes() );
			vc.invoiceStream()			
				.forEach( i -> {
					InvoiceCommunicationTracking tracking = assertInvoiceBatch(i);
					DataResponse response = assertDataResponse(i, tracking);
					assertDataRequest(i, response);
					assertInvoiceData(i);
					assertInvoiceInfo(i);
			});
			
			icc.setDataResponse(null);	// Reset previous response
			return inv;
		});
	}
	
	private Invoice cancel(Invoice invoice) {
		return ctx.getDslContext().transactionResult(config -> {
			List<Invoice> invoices = AonCollectionUtils.toList(invoice);
			InvoiceCommunicatorContext  icc = getInvoiceCommunicatorContext(invoices);
			icc.setConfig(configWithCertificate());
			VERIFACTU.cancel(ctx, icc);
			assertCanceledDataResponse( icc, invoice );
			return invoice;
		});
	}
		
	

	private InvoiceCommunicationTracking assertInvoiceBatch(Invoice i) {
		Optional<InvoiceCommunicationTracking> oTracking = InvoiceCommunicationTrackingDAO.getVerifactuRegister(ctx, i.getDomain(), i.getId());
		assertNotNull(oTracking);
		assertTrue(oTracking.isPresent());
		InvoiceCommunicationTracking tracking = oTracking.get();
		assertNotNull(tracking);
		InvoiceBatchDetail invoiceBatchDetail = tracking.getInvoiceBatchDetail();
		assertNotNull(invoiceBatchDetail);
		assertEquals(invoiceBatchDetail.getDomain(),i.getDomain());
		assertEquals(invoiceBatchDetail.getInvoice(),i.getId());
		InvoiceCommunicationStatus status = invoiceBatchDetail.getStatus();
		assertNotNull(status);
		assertTrue(status == InvoiceCommunicationStatus.ACCEPTED || status == InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS);
		InvoiceBatch invoiceBatch = tracking.getInvoiceBatch();
		assertNotNull(invoiceBatch);
		assertSame(InvoiceCommunicationType.VERIFACTU, invoiceBatch.getType());
		assertSame(InvoiceCommunicationOperation.REGISTER, invoiceBatch.getOperation());
		assertEquals(invoiceBatch.getDomain(),i.getDomain());
		Integer dataResponse = invoiceBatch.getDataResponse();
		assertNotNull(dataResponse);
		assertTrue(dataResponse > 0);
		return tracking;
	}

	private DataResponse assertDataResponse(Invoice i, InvoiceCommunicationTracking tracking) {
		assertNotNull(tracking);
		assertNotNull(tracking.getInvoiceBatch());
		Integer dataResponseId = tracking.getInvoiceBatch().getDataResponse();
		assertNotNull(dataResponseId);
		Optional<DataResponse> optDataResponse = DataResponseDAO.get(ctx, dataResponseId);
		assertNotNull(optDataResponse);
		assertTrue(optDataResponse.isPresent());
		DataResponse dataResponse = optDataResponse.get();
		assertNotNull(dataResponse);
		assertEquals(dataResponse.getId(),dataResponseId);
		assertEquals(dataResponse.getDomain(),i.getDomain());
		assertTrue(dataResponse.getId() > 0);
		assertNotNull(dataResponse.getDataRequest());
		
		Attach response = AttachmentDAO.getDataAttachStream(ctx, f -> f.getDomainProperty().eq(i.getDomain())
			.and(f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value()))
			.and(f.getSourceTypeProperty().eq(DataAttachSource.VERIFACTU.value()))
			.and(f.getSourceBatchProperty().eq(dataResponse.getId())), true).findFirst().orElse(null);
		assertNotNull(response);
		assertNotNull(response.getData());
		return dataResponse;
	}
	
	private void assertInvoiceInfo(Invoice i) {
		Optional<InvoiceInfo> invoiceInfoOpt = InvoiceInfoDAO.get(ctx, i.getId(), InvoiceCommunicationType.VERIFACTU);
		assertNotNull(invoiceInfoOpt);
		assertTrue(invoiceInfoOpt.isPresent());
		InvoiceInfo invoiceInfo = invoiceInfoOpt.get();
		assertNotNull(invoiceInfo);
		assertNotNull(invoiceInfo.getId());
		assertNotNull(invoiceInfo.getDomain());
		assertEquals(i.getId(), invoiceInfo.getInvoice());
		assertEquals(i.getDomain(), invoiceInfo.getDomain());
		assertTrue(invoiceInfo.getStatus() == InvoiceCommunicationStatus.ACCEPTED
				|| invoiceInfo.getStatus() == InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS);
	}

	private void assertDataRequest(Invoice i, DataResponse response) {
		assertNotNull(response);
		Integer responseDataRequest = response.getDataRequest();
		assertNotNull(responseDataRequest);
		DataRequest dataRequest = DataRequestDAO.get(ctx, f -> f.getDomainProperty().eq(i.getDomain())
			.and(f.getIdProperty().eq(responseDataRequest)));
		assertNotNull(dataRequest);
		assertEquals(dataRequest.getId(),responseDataRequest);
		assertEquals(dataRequest.getDomain(),i.getDomain());
		assertTrue(dataRequest.getId() > 0);
		Attach request = AttachmentDAO.getDataAttachStream(ctx, f -> f.getDomainProperty().eq(i.getDomain())
				.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
				.and(f.getSourceTypeProperty().eq(DataAttachSource.VERIFACTU.value()))
				.and(f.getSourceBatchProperty().eq(dataRequest.getId())), true).findFirst().orElse(null);
		assertNotNull(request);
		assertNotNull(request.getData());
	}

	private void assertInvoiceData(Invoice invoice) {
		Optional<InvoiceData> oQRUrl = InvoiceDataDAO.get(ctx, invoice.getDomain(), invoice.getId(), InvoiceDataName.VERIFACTU_QR);
		assertNotNull(oQRUrl);
		assertTrue(oQRUrl.isPresent());
		InvoiceData qRUrl =  oQRUrl.get();
		assertEquals(invoice.getId(), qRUrl.getInvoice() );
		assertEquals(invoice.getDomain(), qRUrl.getDomain() );
		assertEquals(InvoiceDataName.VERIFACTU_QR, qRUrl.getName());
		assertNotNull(qRUrl.getValue());

		Optional<InvoiceData> oHuella = InvoiceDataDAO.get(ctx, invoice.getDomain(), invoice.getId(), InvoiceDataName.VERIFACTU_HUELLA);
		assertNotNull(oHuella);
		assertTrue(oHuella.isPresent());
		InvoiceData huella =  oHuella.get();
		assertEquals(invoice.getId(), huella.getInvoice() );
		assertEquals(invoice.getDomain(), huella.getDomain() );
		assertEquals(InvoiceDataName.VERIFACTU_HUELLA, huella.getName());
		assertNotNull(huella.getValue());
	}
	
	private DataResponse assertCanceledDataResponse(InvoiceCommunicatorContext cc, Invoice invoice) {
		assertNotNull(cc);
		assertNotNull(cc.getDataResponse());
		Integer dataResponseId = cc.getDataResponse().getId();
		assertNotNull(dataResponseId);
		assertTrue(dataResponseId > 0);
		
		Optional<DataResponse> optDataResponse = DataResponseDAO.get(ctx, dataResponseId );
		assertNotNull(optDataResponse);
		assertTrue(optDataResponse.isPresent());
		DataResponse dataResponse = optDataResponse.get();
		assertNotNull(dataResponse);
		assertEquals(dataResponse.getId(),dataResponseId);
		assertEquals(dataResponse.getDomain(), invoice.getDomain());
		assertTrue(dataResponse.getId() > 0);
		assertNotNull(dataResponse.getDataRequest());
		
		Attach response = AttachmentDAO.getDataAttachStream(ctx, f -> 
					 f.getDomainProperty().eq(invoice.getDomain())
				.and(f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value()))
				.and(f.getSourceTypeProperty().eq(DataAttachSource.VERIFACTU.value()))
				.and(f.getSourceBatchProperty().eq(dataResponse.getId())), true)
			.findFirst()
			.orElse(null);
		assertNotNull(response);
		assertNotNull(response.getData());
		assertCanceledVerifactuResponse(cc, invoice, response.getData());
		return dataResponse;
	}


	private void assertCanceledVerifactuResponse(InvoiceCommunicatorContext cc, Invoice invoice, byte[] data) {
		try {
			InputStream is = new ByteArrayInputStream(data);
			SOAPMessage response = MessageFactory.newInstance().createMessage(null, is);
			SOAPBody soapBody = response.getSOAPBody();
			Document bodyDoc = soapBody.extractContentAsDocument();
	        NodeList faults = bodyDoc.getElementsByTagNameNS("http://schemas.xmlsoap.org/soap/envelope/", "Fault");
	        if (faults.getLength() > 0) {
	            Element faultElem = (Element) faults.item(0);
	            String faultString = faultElem.getElementsByTagName("faultstring").item(0).getTextContent();
	            fail( faultString ); 
	        } else {	        
	        	JAXBContext jc = JAXBContext.newInstance(RespuestaRegFactuSistemaFacturacionType.class.getPackage().getName());
	        	Unmarshaller um = jc.createUnmarshaller();
	        	JAXBElement<RespuestaRegFactuSistemaFacturacionType> o = um.unmarshal(bodyDoc, RespuestaRegFactuSistemaFacturacionType.class);
	        	RespuestaRegFactuSistemaFacturacionType resp = o.getValue();
	        	
	    		assertNotNull(resp);
	    		assertNotNull(resp.getRespuestaLinea());
	    		assertFalse(resp.getRespuestaLinea().isEmpty());
	    		assertEquals(1, resp.getRespuestaLinea().size());
	    		RespuestaExpedidaType ret = resp.getRespuestaLinea().get(0);
	    		
	    		IDFacturaExpedidaType idFactura = ret.getIDFactura();
	    	    assertNotNull( idFactura );
	    	    assertEquals(cc.getCompany().getDocument() , idFactura.getIDEmisorFactura() );
	    	    assertEquals(invoice.getReferenceCode() , idFactura.getNumSerieFactura() );
	    	    assertEquals(VerifactuUtils.toString(invoice.getExpDate() ), idFactura.getFechaExpedicionFactura() );
	    		
	    		OperacionType operacion = ret.getOperacion();
	    		assertNotNull(operacion);
	    		assertNotNull(operacion.getTipoOperacion());
	    		assertEquals(TipoOperacionType.ANULACION, operacion.getTipoOperacion());
	    		assertNull(operacion.getSubsanacion());
	    		assertNotNull(operacion.getRechazoPrevio());
	    		assertEquals(RechazoPrevioType.N, operacion.getRechazoPrevio());
	    		assertNotNull(operacion.getSinRegistroPrevio());
	    		assertEquals(SinRegistroPrevioType.N, operacion.getSinRegistroPrevio());
	    		
	    		assertNotNull(ret.getRefExterna());
	    		String stringId = AonNumberUtils.toString(invoice.getId());
				assertEquals( stringId , ret.getRefExterna());
	        	
	    		EstadoRegistroType estado = ret.getEstadoRegistro();
	    		assertNotNull(estado);
	    		
	    		if (estado == EstadoRegistroType.ACEPTADO_CON_ERRORES) {
	    			BigInteger codigoErrorRegistro = ret.getCodigoErrorRegistro();
	    			String descripcionErrorRegistro = ret.getDescripcionErrorRegistro();
	    			
	    			System.out.println(  estado + " (" + codigoErrorRegistro + ") " + descripcionErrorRegistro );
	    			
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
			
		} catch (IOException | SOAPException | JAXBException e) {
			fail(e);
		}
	}
}
