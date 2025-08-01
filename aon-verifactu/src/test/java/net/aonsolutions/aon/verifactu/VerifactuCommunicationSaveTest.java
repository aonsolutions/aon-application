package net.aonsolutions.aon.verifactu;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.DataRequest;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
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

import net.aonsolutions.aon.verifactu.exceptions.VerifactuException;

class VerifactuCommunicationSaveTest extends AbstractVerifactuTest {
		
	@Test
	void ventaNacionalSimpleSaveTest() throws VerifactuException {
		List<Invoice> invoices = new LinkedList<>();
		invoices.add( InvoiceTypes.Invoices.VENTA_NACIONAL_SIMPLE.get(ctx, DOMAIN_ID) );
		invoices.stream().forEach(i -> InvoiceDAO.save(ctx, i));
		VERIFACTU.accept(getOccam(), config(), company(), invoices, null);
		invoices.stream().forEach( invoice -> {
			InvoiceData qrUrl = InvoiceDataDAO.get(ctx, f -> f.getDomainProperty().eq(invoice.getDomain())
					.and(f.getInvoiceProperty().eq(invoice.getId()))
					.and(f.getNameProperty().eq("VERIFACTU_QR")));
			assertNotNull(qrUrl);
			assertNotNull(qrUrl.getValue());
			
			InvoiceData huella = InvoiceDataDAO.get(ctx, f -> f.getDomainProperty().eq(invoice.getDomain())
					.and(f.getInvoiceProperty().eq(invoice.getId()))
					.and(f.getNameProperty().eq("VERIFACTU_HUELLA")));
			assertNotNull(huella);
			assertNotNull(huella.getValue());		
					
			InvoiceInfo invoiceInfo = InvoiceInfoDAO.get(ctx, f -> f.getDomainProperty().eq(invoice.getDomain())
					.and(f.getTypeProperty().eq(InvoiceCommunicationType.VERIFACTU.value())
					.and(f.getInvoiceProperty().eq(invoice.getId()))));
			assertNotNull(invoiceInfo);
			
			assertTrue(invoiceInfo.getStatus() == InvoiceCommunicationStatus.ACCEPTED
					|| invoiceInfo.getStatus() == InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS);
	
			InvoiceCommunicationTracking tracking = InvoiceCommunicationTrackingDAO.get(ctx, f -> f.getDomainProperty().eq(invoice.getDomain())
					.and(f.getInvoiceProperty().eq(invoice.getId()))
					.and(f.getTypeProperty().eq(InvoiceCommunicationType.VERIFACTU.value()))
					.and(f.getOperationProperty().eq(InvoiceCommunicationOperation.REGISTER.value())));
			assertNotNull(tracking);
			assertNotNull(tracking.getInvoiceBatchDetail());
			assertTrue(tracking.getInvoiceBatchDetail().getStatus() == InvoiceCommunicationStatus.ACCEPTED
					|| tracking.getInvoiceBatchDetail().getStatus() == InvoiceCommunicationStatus.ACCEPTED_WITH_ERRORS);
			
			assertNotNull(tracking.getInvoiceBatch());
			assertNotNull(tracking.getInvoiceBatch().getDataResponse());
			
			DataResponse dataResponse = DataResponseDAO.get(ctx, f -> f.getDomainProperty().eq(invoice.getDomain())
					.and(f.getIdProperty().eq(tracking.getInvoiceBatch().getDataResponse())));
			assertNotNull(dataResponse);
			assertNotNull(dataResponse.getDataRequest());
	
			DataRequest dataRequest = DataRequestDAO.get(ctx, f -> f.getDomainProperty().eq(invoice.getDomain())
					.and(f.getIdProperty().eq(dataResponse.getDataRequest())));
			assertNotNull(dataRequest);
	
			Attach request = AttachmentDAO.getDataAttachStream(ctx, f -> f.getDomainProperty().eq(invoice.getDomain())
					.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
					.and(f.getSourceTypeProperty().eq(DataAttachSource.VERIFACTU.value()))
					.and(f.getSourceBatchProperty().eq(dataRequest.getId())), true).findFirst().orElse(null);
			assertNotNull(request);
			assertNotNull(request.getData());
			
			Attach response = AttachmentDAO.getDataAttachStream(ctx, f -> f.getDomainProperty().eq(invoice.getDomain())
					.and(f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value()))
					.and(f.getSourceTypeProperty().eq(DataAttachSource.VERIFACTU.value()))
					.and(f.getSourceBatchProperty().eq(dataResponse.getId())), true).findFirst().orElse(null);
			assertNotNull(response);
			assertNotNull(request.getData());
		});
	}
	
}
