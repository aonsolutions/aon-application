package net.aonsolutions.aon.verifactu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceDataName;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataRequestDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceCommunicationDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceCommunicationTrackingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.util.AonCollectionUtils;

class SifCommunicationCancelTest extends AbstractVerifactuTest {

	@Override protected Environment getEnvironment() { return SIF_ENV; }

	@Test
	void venta_nacional_simple_test() {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(getEnvironment());
		saveAndCancel(invoice);
	}

	@Test
	void venta_nacional_simplificada_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA.get(getEnvironment());
		saveAndCancel(invoice);
	}

	@Test
	void venta_nacional_simplificada_con_customer_sin_direccion_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA_CON_CUSTOMER_SIN_DIRECCION.get(getEnvironment());
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_nacional_suplidos_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SUPLIDOS.get(getEnvironment());
		saveAndCancel(invoice);
	}

	@Test
	void venta_nacional_rectificativa_simple_test()  {
		Invoice invoice1 = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(getEnvironment());
		invoice1 = save(invoice1);

		Invoice invoice2 = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLE.get(getEnvironment());
		invoice2.setRectificationInvoice(invoice1.getId());
		invoice2.setRectificationInvoiceDate(invoice1.getIssueDate());
		invoice2.setRectificationInvoiceNumber(invoice1.getNumber());
		invoice2.setRectificationInvoiceSeries(invoice1.getSeries());
		invoice2.setRectificationInvoiceReference(invoice1.getReferenceCode());
		saveAndCancel(invoice2);
	}

	@Test
	void venta_nacional_rectificativa_simplificada_test()  {
		Invoice invoice1 = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLIFICADA.get(getEnvironment());
		invoice1 = save(invoice1);
		
		Invoice invoice2 = InvoiceTypes.Invoices.VENTA_NACIONAL_RECTIFICATIVA_SIMPLIFICADA.get(getEnvironment());
		invoice2.setRectificationInvoice(invoice1.getId());
		invoice2.setRectificationInvoiceDate(invoice1.getIssueDate());
		invoice2.setRectificationInvoiceNumber(invoice1.getNumber());
		invoice2.setRectificationInvoiceSeries(invoice1.getSeries());
		invoice2.setRectificationInvoiceReference(invoice1.getReferenceCode());
		saveAndCancel(invoice2);
	}

	@Test
	void venta_isp_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ISP.get(getEnvironment());
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_nacional_re_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_RE.get(getEnvironment());
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_nacional_irpf_professional_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_IRPF_PROFESSIONAL.get(getEnvironment());
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_intracomunitaria_servicios_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_INTRACOMUNITARIA_SERVICIOS.get(getEnvironment());
		saveAndCancel(invoice);
	}
 	

	@Test
	void venta_intracomunitaria_no_servicios_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_INTRACOMUNITARIA.get(getEnvironment());
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_extracomunitaria_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_EXTRACOMUNITARIA.get(getEnvironment());
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_extracomunitaria_servicio_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_EXTRACOMUNITARIA_SERVICIO.get(getEnvironment());
		saveAndCancel(invoice);
	}

	@Test
	void venta_can_ceu_mel_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_CAN_CEU_MEL.get(getEnvironment());
		saveAndCancel(invoice);
	}

	@Test
	void venta_can_ceu_mel_servicio_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_CAN_CEU_MEL_SERVICIO.get(getEnvironment());
		saveAndCancel(invoice);
	}

	@Test
	void venta_nacional_exenta_e1_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_EXENTA_E1.get(getEnvironment());
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_anulada_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_ANULADA.get(getEnvironment());
		saveAndCancel(invoice);
	}
	
	@Test
	void venta_nacional_simple_criterio_caja_test()  {
		Invoice invoice = InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE_CRITERIO_CAJA.get(getEnvironment());
		saveAndCancel(invoice);
	}
	
	private Invoice saveAndCancel(Invoice invoice) {
		invoice.setSeries(VerifactuTestsUtils.series(getEnvironment().getCtx(), invoice.isRectifier()));
		invoice = save(invoice);
		cancel(invoice);
		return invoice;
	}
	private Invoice save(Invoice invoice) {
		Invoice i =  getEnvironment().getCtx().getDslContext().transactionResult(config -> {
			List<Invoice> invoices = AonCollectionUtils.toList(invoice);
			InvoiceCommunicatorContext  icc = getEnvironment().getInvoiceCommunicatorContextWithCertificate(invoices);
			Invoice inv = InvoiceDAO.save(getEnvironment().getCtx(), invoice);
			invoices = AonCollectionUtils.toList(inv);
			VerifactuContext vc = SIF.accept(getEnvironment().getCtx(), icc, getEnvironment().getEnablerData(icc.getConfig()), PHASE_LISTENER);
			assertNotNull(vc);
			assertNull(vc.getResponse());
			return inv;
		});
		InvoiceCommunicationTracking tracking = assertInvoiceBatch(i);
		DataResponse response = assertDataResponse(i, tracking);
		assertDataRequest(i, response);
		assertInvoiceData(i);
		assertInvoiceInfo(i);
		assertInvoiceCommunication(i);
		assertCommunicationHistory(i);
		return i;
	}
	
	private Invoice cancel(Invoice invoice) {
		return getEnvironment().getCtx().getDslContext().transactionResult(config -> {
			List<Invoice> invoices = AonCollectionUtils.toList(invoice);
			InvoiceCommunicatorContext  icc = getEnvironment().getInvoiceCommunicatorContextWithCertificate(invoices);
			SIF.cancel(getEnvironment().getCtx(), icc, getEnvironment().getEnablerData(icc.getConfig()));
			assertCanceledDataResponse( icc, invoice );
			return invoice;
		});
	}
		
	private InvoiceCommunicationTracking assertInvoiceBatch(Invoice i) {
		Optional<InvoiceCommunicationTracking> oTracking = InvoiceCommunicationTrackingDAO.getSifRegister(getEnvironment().getCtx(), i.getDomain(), i.getId());
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
		assertTrue(status == InvoiceCommunicationStatus.ACCEPTED);
		InvoiceBatch invoiceBatch = tracking.getInvoiceBatch();
		assertNotNull(invoiceBatch);
		assertSame(InvoiceCommunicationType.SIF, invoiceBatch.getType());
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
		Optional<DataResponse> optDataResponse = DataResponseDAO.get(getEnvironment().getCtx(), dataResponseId);
		assertNotNull(optDataResponse);
		assertTrue(optDataResponse.isPresent());
		DataResponse dataResponse = optDataResponse.get();
		assertNotNull(dataResponse);
		assertEquals(dataResponse.getId(),dataResponseId);
		assertEquals(dataResponse.getDomain(),i.getDomain());
		assertTrue(dataResponse.getId() > 0);
		assertNotNull(dataResponse.getDataRequest());
		
		Attach response = AttachmentDAO.getDataAttachStream(getEnvironment().getCtx(), f -> 
				f.getDomainProperty().eq(i.getDomain())
				.and(f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value()))
				.and(f.getSourceTypeProperty().eq(DataAttachSource.SIF.value()))
				.and(f.getSourceBatchProperty().eq(dataResponse.getId())), true)
			.findFirst()
			.orElse(null);
		assertNotNull(response);
		assertNull(response.getData());
		return dataResponse;
	}
	
	private void assertInvoiceInfo(Invoice i) {
		Optional<InvoiceInfo> invoiceInfoOpt = InvoiceInfoDAO.get(getEnvironment().getCtx(), i.getId(), InvoiceCommunicationType.SIF);
		assertNotNull(invoiceInfoOpt);
		assertTrue(invoiceInfoOpt.isPresent());
		InvoiceInfo invoiceInfo = invoiceInfoOpt.get();
		assertNotNull(invoiceInfo);
		assertNotNull(invoiceInfo.getId());
		assertNotNull(invoiceInfo.getDomain());
		assertEquals(i.getId(), invoiceInfo.getInvoice());
		assertEquals(i.getDomain(), invoiceInfo.getDomain());
		assertTrue(invoiceInfo.getStatus() == InvoiceCommunicationStatus.ACCEPTED);
	}

	private void assertDataRequest(Invoice i, DataResponse response) {
		assertNotNull(response);
		Integer responseDataRequest = response.getDataRequest();
		assertNotNull(responseDataRequest);
		DataRequest dataRequest = DataRequestDAO.get(getEnvironment().getCtx(), f -> 
			f.getDomainProperty().eq(i.getDomain())
			.and(f.getIdProperty().eq(responseDataRequest)));
		assertNotNull(dataRequest);
		assertEquals(dataRequest.getId(),responseDataRequest);
		assertEquals(dataRequest.getDomain(),i.getDomain());
		assertTrue(dataRequest.getId() > 0);
		Attach request = AttachmentDAO.getDataAttachStream(getEnvironment().getCtx(), f -> 
				f.getDomainProperty().eq(i.getDomain())
				.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
				.and(f.getSourceTypeProperty().eq(DataAttachSource.SIF.value()))
				.and(f.getSourceBatchProperty().eq(dataRequest.getId())), true)
			.findFirst()
			.orElse(null);
		assertNotNull(request);
		assertNotNull(request.getData());
	}

	private void assertInvoiceData(Invoice invoice) {
		Optional<InvoiceData> oQRUrl = InvoiceDataDAO.get(getEnvironment().getCtx(), invoice.getDomain(), invoice.getId(), InvoiceDataName.VERIFACTU_QR);
		assertNotNull(oQRUrl);
		assertTrue(oQRUrl.isPresent());
		InvoiceData qRUrl =  oQRUrl.get();
		assertEquals(invoice.getId(), qRUrl.getInvoice() );
		assertEquals(invoice.getDomain(), qRUrl.getDomain() );
		assertEquals(InvoiceDataName.VERIFACTU_QR, qRUrl.getName());
		assertNotNull(qRUrl.getValue());

		Optional<InvoiceData> oHuella = InvoiceDataDAO.get(getEnvironment().getCtx(), invoice.getDomain(), invoice.getId(), InvoiceDataName.VERIFACTU_HUELLA);
		assertNotNull(oHuella);
		assertTrue(oHuella.isPresent());
		InvoiceData huella =  oHuella.get();
		assertEquals(invoice.getId(), huella.getInvoice() );
		assertEquals(invoice.getDomain(), huella.getDomain() );
		assertEquals(InvoiceDataName.VERIFACTU_HUELLA, huella.getName());
		assertNotNull(huella.getValue());
	}
	
	private void assertInvoiceCommunication(Invoice i) {
		Invoice inv = InvoiceDAO.getFullInvoice(getEnvironment().getCtx(), i.getId());
		assertNotNull(inv);
		assertNotNull(inv.getId());
		assertNotNull(inv.getCommunicationInfo());
		assertTrue(AonCollectionUtils.isNotEmpty( inv.getCommunicationInfo()));
		assertNotNull(inv.getSifInfo());
		assertTrue(inv.getSifInfo().isPresent());
		InvoiceInfo invoiceInfo = inv.getSifInfo().get();
		assertNotNull(invoiceInfo);
		assertNotNull(invoiceInfo.getId());
		assertNotNull(invoiceInfo.getDomain());
		assertEquals(i.getId(), invoiceInfo.getInvoice());
		assertEquals(i.getDomain(), invoiceInfo.getDomain());
		assertTrue(invoiceInfo.getStatus() == InvoiceCommunicationStatus.ACCEPTED);
	}
	
	private void assertCommunicationHistory(Invoice i) {
		List<InvoiceCommunicationHistory> history = InvoiceCommunicationDAO.getHistory(getEnvironment().getCtx(), i.getId(), null );
		assertNotNull(history);
		assertTrue(AonCollectionUtils.isNotEmpty( history ));
		assertEquals(1, history.size());
		InvoiceCommunicationHistory h = history.get(0);
		assertEquals(i.getId(), h.getInvoiceId());
		assertEquals(getEnvironment().getUser(), h.getCreationUser());
		assertTrue(h.getOperation() == InvoiceCommunicationOperation.REGISTER);
		assertTrue(h.getStatus() == InvoiceCommunicationStatus.ACCEPTED);
		assertNotNull(h.getDate() );
		assertNotNull(h.getRequestUrl());
		assertNotNull(h.getResponseUrl());
		assertNull(h.getResponseMessages());
		assertNull(h.getResponseData());
	}

	private DataResponse assertCanceledDataResponse(InvoiceCommunicatorContext cc, Invoice invoice) {
		assertNotNull(cc);
		assertNotNull(cc.getDataResponse());
		Integer dataResponseId = cc.getDataResponse().getId();
		assertNotNull(dataResponseId);
		assertTrue(dataResponseId > 0);
		
		Optional<DataResponse> optDataResponse = DataResponseDAO.get(getEnvironment().getCtx(), dataResponseId );
		assertNotNull(optDataResponse);
		assertTrue(optDataResponse.isPresent());
		DataResponse dataResponse = optDataResponse.get();
		assertNotNull(dataResponse);
		assertEquals(dataResponse.getId(),dataResponseId);
		assertEquals(dataResponse.getDomain(), invoice.getDomain());
		assertTrue(dataResponse.getId() > 0);
		assertNotNull(dataResponse.getDataRequest());
		
		Attach response = AttachmentDAO.getDataAttachStream(getEnvironment().getCtx(), f -> 
					 f.getDomainProperty().eq(invoice.getDomain())
				.and(f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value()))
				.and(f.getSourceTypeProperty().eq(DataAttachSource.SIF.value()))
				.and(f.getSourceBatchProperty().eq(dataResponse.getId())), true)
			.findFirst()
			.orElse(null);
		assertNotNull(response);
		assertNull(response.getData());
		return dataResponse;
	}

}
