package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataRequestDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceCommunicationTrackingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;

class VerifactuCommunicationSaveTest extends AbstractVerifactuTest {

	@Test
	void venta_nacional_simpleAEATTest() {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(ctx, DOMAIN_ID);
		save(invoice);
	}
	
	@Test
	void venta_nacional_simplificadaAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA.get(ctx, DOMAIN_ID);
		save(invoice);
	}

	@Test
	void venta_nacional_simplificada_con_customer_sin_direccionAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA_CON_CUSTOMER_SIN_DIRECCION.get(ctx, DOMAIN_ID);
		save(invoice);
	}
	
	@Test
	void venta_nacional_suplidosAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SUPLIDOS.get(ctx, DOMAIN_ID);
		save(invoice);
	}
	
	@Test
	void venta_nacional_rectificativa_simpleAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get(ctx, DOMAIN_ID);
		save(invoice);
	}
	
	@Test
	void venta_nacional_rectificativa_simplificadaAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLIFICADA.get(ctx, DOMAIN_ID);
		save(invoice);
	}
	
	@Test
	void venta_ispAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ISP.get(ctx, DOMAIN_ID);
		save(invoice);
	}
	
	@Test
	void venta_nacional_reAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get(ctx, DOMAIN_ID);
		save(invoice);
	}
	
	@Test
	void venta_nacional_irpf_professionalAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_IRPF_PROFESSIONAL.get(ctx, DOMAIN_ID);
		save(invoice);
	}
	
	@Test
	void venta_intracomunitaria_serviciosAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_INTRACOMUNITARIA_SERVICIOS.get(ctx, DOMAIN_ID);
		save(invoice);
	}
	
	@Test
	void venta_intracomunitaria_no_serviciosAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_INTRACOMUNITARIA.get(ctx, DOMAIN_ID);
		save(invoice);
	}

	@Test
	void venta_extracomunitariaAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_EXTRACOMUNITARIA.get(ctx, DOMAIN_ID);
		save(invoice);
	}
	
	@Test
	void venta_extracomunitaria_servicioAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_EXTRACOMUNITARIA_SERVICIO.get(ctx, DOMAIN_ID);
		save(invoice);
	}

	@Test
	void venta_can_ceu_melAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_CAN_CEU_MEL.get(ctx, DOMAIN_ID);
		save(invoice);
	}

	@Test
	void venta_can_ceu_mel_servicioAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_CAN_CEU_MEL_SERVICIO.get(ctx, DOMAIN_ID);
		save(invoice);
	}

	@Test
	void venta_nacional_exenta_e1AEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_EXENTA_E1.get(ctx, DOMAIN_ID);
		save(invoice);
	}
	
	@Test
	void venta_anuladaAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ANULADA.get(ctx, DOMAIN_ID);
		save(invoice);
	}
	
	@Test
	void venta_nacional_simple_criterio_cajaAEATTest()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE_CRITERIO_CAJA.get(ctx, DOMAIN_ID);
		save(invoice);
	}

	private void save(Invoice invoice) {
		List<Invoice> invoices = new LinkedList<>();
		invoices.add( invoice );
		
		ctx.transaction(config -> {
			invoices.stream().forEach(i -> {
				InvoiceDAO.save(ctx, i);
			});
			VERIFACTU.accept(ctx, config(), company(), invoices);
			invoices.stream().forEach( i -> {
				InvoiceCommunicationTracking tracking = assertInvoiceBatch(i);
				DataResponse response = assertDataResponse(i, tracking);
				assertDataRequest(i, response);
				assertInvoiceData(i);
				assertInvoiceInfo(i);
			});
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
		assertTrue(invoiceBatch.getType() == InvoiceCommunicationType.VERIFACTU);
		assertTrue(invoiceBatch.getOperation() == InvoiceCommunicationOperation.REGISTER);
		assertTrue(invoiceBatch.getDomain().equals(i.getDomain()));
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
		assertTrue(dataResponse.getId().equals(dataResponseId));
		assertTrue(dataResponse.getDomain().equals(i.getDomain()));
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
		InvoiceInfo invoiceInfo = InvoiceInfoDAO.get(ctx, 
			f -> f.getDomainProperty().eq(i.getDomain())
			.and(f.getTypeProperty().eq(InvoiceCommunicationType.VERIFACTU.value())
			.and(f.getInvoiceProperty().eq(i.getId()))));
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
		assertTrue(dataRequest.getId().equals(responseDataRequest));
		assertTrue(dataRequest.getDomain().equals(i.getDomain()));
		assertTrue(dataRequest.getId() > 0);
		Attach request = AttachmentDAO.getDataAttachStream(ctx, f -> f.getDomainProperty().eq(i.getDomain())
				.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
				.and(f.getSourceTypeProperty().eq(DataAttachSource.VERIFACTU.value()))
				.and(f.getSourceBatchProperty().eq(dataRequest.getId())), true).findFirst().orElse(null);
		assertNotNull(request);
		assertNotNull(request.getData());
	}

	private void assertInvoiceData(Invoice invoice) {
		Optional<InvoiceData> oQRUrl = InvoiceDataDAO.get(ctx, invoice.getDomain(), invoice.getId(), InvoiceData.VERIFACTU_QR);
		assertNotNull(oQRUrl);
		assertTrue(oQRUrl.isPresent());
		InvoiceData QRUrl =  oQRUrl.get();
		assertEquals(invoice.getId(), QRUrl.getInvoice() );
		assertEquals(invoice.getDomain(), QRUrl.getDomain() );
		assertEquals(InvoiceData.VERIFACTU_QR, QRUrl.getName());
		assertNotNull(QRUrl.getValue());

		Optional<InvoiceData> oHuella = InvoiceDataDAO.get(ctx, invoice.getDomain(), invoice.getId(), InvoiceData.VERIFACTU_HUELLA);
		assertNotNull(oHuella);
		assertTrue(oHuella.isPresent());
		InvoiceData huella =  oHuella.get();
		assertEquals(invoice.getId(), huella.getInvoice() );
		assertEquals(invoice.getDomain(), huella.getDomain() );
		assertEquals(InvoiceData.VERIFACTU_HUELLA, huella.getName());
		assertNotNull(huella.getValue());
	}
	
}
