package net.aonsolutions.aon.invoice.communication.visitor;
 
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatch;
import com.esferalia.aon.occam.api.model.finance.InvoiceBatchDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistoryMapValue;
import com.esferalia.aon.occam.api.model.finance.InvoiceData;
import com.esferalia.aon.occam.api.model.finance.InvoiceDataName;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationTracking;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicatorContext;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataRequestDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DataResponseDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.UserDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceCommunicationTrackingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.aon.invoice.communication.InvoiceCommunicator;
import net.aonsolutions.aon.verifactu.Environment;

class NoVerifactuInvoiceCommunicationSaveTest extends AbsInvoiceCommunicationSaveTest {

	@Override 
	protected Environment getEnvironment() { 
		return NO_VERIFACTU_ENV; 
	}

	protected Invoice save(Invoice invoice) throws InvoiceCommunicationException {
		Domain domain = DomainDAO.getDomain(getEnvironment().getCtx(), getEnvironment().getDomainId());
		User user = UserDAO.get(getEnvironment().getCtx(), getEnvironment().getDomainId(), getEnvironment().getUser())
			.orElseThrow(() -> new IllegalStateException("User not found: " + getEnvironment().getUser()));
		List<Invoice> invoices = AonCollectionUtils.toList(invoice);
		InvoiceCommunicatorContext cc = new InvoiceCommunicatorContext(domain, user, null, invoices);
		cc.setConfig(getEnvironment().configurationWithCertificate()).setCompany(getEnvironment().company());
		InvoiceCommunicator.acceptInvoice(cc);
		InvoiceCommunicationTracking tracking = assertInvoiceBatch(invoice);
		DataResponse response = assertDataResponse(cc, invoice, tracking);
		assertDataRequest(invoice, response);
		assertInvoiceData(invoice);
		assertInvoiceInfo(invoice);
		assertInvoiceCommunication(invoice);
		assertCommunicationHistory(invoice);
		return invoice;
	}

	private InvoiceCommunicationTracking assertInvoiceBatch(Invoice i) {
		Optional<InvoiceCommunicationTracking> oTracking = InvoiceCommunicationTrackingDAO.getNoVerifactuRegister(getEnvironment().getCtx(), i.getDomain(), i.getId());
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
		assertTrue(status == InvoiceCommunicationStatus.PENDING);
		InvoiceBatch invoiceBatch = tracking.getInvoiceBatch();
		assertNotNull(invoiceBatch);
		assertSame(InvoiceCommunicationType.NO_VERIFACTU, invoiceBatch.getType());
		assertSame(InvoiceCommunicationOperation.REGISTER, invoiceBatch.getOperation());
		assertEquals(invoiceBatch.getDomain(), i.getDomain());
		Integer dataResponse = invoiceBatch.getDataResponse();
		assertNotNull(dataResponse);
		assertTrue(dataResponse > 0);
		return tracking;
	}

	private DataResponse assertDataResponse(InvoiceCommunicatorContext cc, Invoice invoice, InvoiceCommunicationTracking tracking) {
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
		assertEquals(dataResponse.getDomain(), invoice.getDomain());
		assertTrue(dataResponse.getId() > 0);
		assertNotNull(dataResponse.getDataRequest());
		
		Attach response = AttachmentDAO.getDataAttachStream(getEnvironment().getCtx(), f -> f.getDomainProperty().eq(invoice.getDomain())
			.and(f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value()))
			.and(f.getSourceTypeProperty().eq(DataAttachSource.NO_VERIFACTU.value()))
			.and(f.getSourceBatchProperty().eq(dataResponse.getId())), true).findFirst().orElse(null);
		assertNotNull(response);
		assertNull(response.getData());
		return dataResponse;
	}
	
	private void assertInvoiceInfo(Invoice i) {
		Optional<InvoiceInfo> invoiceInfoOpt = InvoiceInfoDAO.get(getEnvironment().getCtx(), i.getId(), InvoiceCommunicationType.NO_VERIFACTU);
		assertNotNull(invoiceInfoOpt);
		assertTrue(invoiceInfoOpt.isPresent());
		InvoiceInfo invoiceInfo = invoiceInfoOpt.get();
		assertNotNull(invoiceInfo);
		assertNotNull(invoiceInfo.getId());
		assertNotNull(invoiceInfo.getDomain());
		assertEquals(i.getId(), invoiceInfo.getInvoice());
		assertEquals(i.getDomain(), invoiceInfo.getDomain());
		assertTrue(invoiceInfo.getStatus() == InvoiceCommunicationStatus.PENDING);
	}
	
	private void assertInvoiceCommunication(Invoice i) {
		Invoice inv = InvoiceDAO.getFullInvoice(getEnvironment().getCtx(), i.getId());
		assertNotNull(inv);
		assertNotNull(inv.getId());
		assertNotNull(inv.getCommunicationInfo());
		assertTrue(AonCollectionUtils.isNotEmpty( inv.getCommunicationInfo()));
		assertNotNull(inv.getNoVerifactuInfo());
		assertTrue(inv.getNoVerifactuInfo().isPresent());
		InvoiceInfo invoiceInfo = inv.getNoVerifactuInfo().get();
		assertNotNull(invoiceInfo);
		assertNotNull(invoiceInfo.getId());
		assertNotNull(invoiceInfo.getDomain());
		assertEquals(i.getId(), invoiceInfo.getInvoice());
		assertEquals(i.getDomain(), invoiceInfo.getDomain());
		assertTrue(invoiceInfo.getStatus() == InvoiceCommunicationStatus.PENDING);
	}
	
	private void assertCommunicationHistory(Invoice i) throws InvoiceCommunicationException {
		Map<InvoiceCommunicationType, InvoiceCommunicationHistoryMapValue> map = InvoiceCommunicator.history(getEnvironment().getOccam(), i.getId());
		assertNotNull(map);
		assertTrue(AonCollectionUtils.isNotEmpty( map ));
		AonCollectionUtils.stream(map)
			.forEach(e -> {
				assertNotNull(e.getKey());		
				assertNotNull(e.getValue());
				InvoiceCommunicationHistoryMapValue v = e.getValue();
				assertNotNull(v.getInfo());
				InvoiceInfo info = v.getInfo();
				assertEquals(e.getKey(), info.getType());
				assertNotNull(v.getHistory());
				List<InvoiceCommunicationHistory> history = v.getHistory();
				assertNotNull(history);
				assertTrue(AonCollectionUtils.isNotEmpty( history ));
				assertEquals(1, history.size());
				InvoiceCommunicationHistory h = history.get(0);
				assertEquals(i.getId(), h.getInvoiceId());
				assertEquals(e.getKey(), h.getType());
				assertEquals(getEnvironment().getUser(), h.getCreationUser());
				assertTrue(h.getOperation() == InvoiceCommunicationOperation.REGISTER);
				assertTrue(h.getStatus() == InvoiceCommunicationStatus.PENDING);
				assertNotNull(h.getDate() );
				assertNotNull(h.getRequestUrl());
				assertNull(h.getResponseData());
			});
		
	}

	private void assertDataRequest(Invoice i, DataResponse response) {
		assertNotNull(response);
		Integer responseDataRequest = response.getDataRequest();
		assertNotNull(responseDataRequest);
		DataRequest dataRequest = DataRequestDAO.get(getEnvironment().getCtx(), f -> f.getDomainProperty().eq(i.getDomain())
			.and(f.getIdProperty().eq(responseDataRequest)));
		assertNotNull(dataRequest);
		assertEquals(dataRequest.getId(), responseDataRequest);
		assertEquals(dataRequest.getDomain(), i.getDomain());
		assertTrue(dataRequest.getId() > 0);
		Attach request = AttachmentDAO.getDataAttachStream(getEnvironment().getCtx(), f -> f.getDomainProperty().eq(i.getDomain())
				.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
				.and(f.getSourceTypeProperty().eq(DataAttachSource.NO_VERIFACTU.value()))
				.and(f.getSourceBatchProperty().eq(dataRequest.getId())), true).findFirst().orElse(null);
		assertNotNull(request);
		assertNotNull(request.getData());
	}

	private void assertInvoiceData(Invoice invoice) {
		Optional<InvoiceData> oQRUrl = InvoiceDataDAO.get(getEnvironment().getCtx(), invoice.getDomain(), invoice.getId(), InvoiceDataName.VERIFACTU_QR);
		assertNotNull(oQRUrl);
		assertTrue(oQRUrl.isPresent());
		InvoiceData qrUrl =  oQRUrl.get();
		assertEquals(invoice.getId(), qrUrl.getInvoice() );
		assertEquals(invoice.getDomain(), qrUrl.getDomain() );
		assertEquals(InvoiceDataName.VERIFACTU_QR, qrUrl.getName());
		assertNotNull(qrUrl.getValue());

		Optional<InvoiceData> oHuella = InvoiceDataDAO.get(getEnvironment().getCtx(), invoice.getDomain(), invoice.getId(), InvoiceDataName.VERIFACTU_HUELLA);
		assertNotNull(oHuella);
		assertTrue(oHuella.isPresent());
		InvoiceData huella =  oHuella.get();
		assertEquals(invoice.getId(), huella.getInvoice() );
		assertEquals(invoice.getDomain(), huella.getDomain() );
		assertEquals(InvoiceDataName.VERIFACTU_HUELLA, huella.getName());
		assertNotNull(huella.getValue());
	}
	
}
