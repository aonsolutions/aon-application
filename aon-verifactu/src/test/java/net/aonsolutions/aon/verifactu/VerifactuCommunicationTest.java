package net.aonsolutions.aon.verifactu;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
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
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceCommunicationTrackingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceDataDAO;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;

import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.respuestasuministro.RespuestaRegFactuSistemaFacturacionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.tike.cont.ws.suministrolr.RegFactuSistemaFacturacion;
import net.aonsolutions.aon.verifactu.utils.XMLUtils;

class VerifactuCommunicationTest extends AbstractVerifactuTest {
		
	@Test
	@Disabled
	void communication() throws Exception {
		VerifactuContext vc = new VerifactuContext()
			.setConfig(config())
			.setCompany(company())
			.setInvoices(InvoiceTypes.getAll(ctx))
			.setBlockchain(null);
		vc.setRequest(Invoice2Verifactu.build(vc) );
		String requestStr = XMLUtils.soapMarshal(
			vc.getRequest(), 
			RegFactuSistemaFacturacion.class);
		
		vc.setResponse( XMLUtils.post(
			vc.getConfig().getCertificate(), 
			VerifactuUri.getUrlEmision(true), 
			requestStr));
	
		assertNotNull(vc.getResponse());
		assertEquals(vc.getResponse().isError(), false);
		
		RespuestaRegFactuSistemaFacturacionType respuesta = (RespuestaRegFactuSistemaFacturacionType) 
				XMLUtils.soapUnmarshal(RespuestaRegFactuSistemaFacturacionType.class, vc.getResponse().getResponse());

		if (!respuesta.getRespuestaLinea().isEmpty()) {
			respuesta.getRespuestaLinea().stream().forEach(r -> {
				assertEquals(r.getEstadoRegistro().equals("ParcialmenteCorrecto")
						? "ParcialmenteCorrecto" : "Correcto", r.getEstadoRegistro());
			});
		}
	}
	
	@Test
	@Disabled
	void communicationAndSave() throws Exception {
		List<Invoice> invoices = InvoiceTypes.getAll(ctx).stream().map(i -> AON.acceptInvoice(ctx, i)).toList();	
		VERIFACTU.accept(config(), company(), invoices, null, "user");
		invoices.stream().forEach(invoice -> assertInvoiceCommunication(ctx, invoice));
		
	}
		
	private void assertInvoiceCommunication(AONContext ctx, Invoice invoice) {
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
		assertEquals(InvoiceCommunicationStatus.ACCEPTED, invoiceInfo.getStatus());

		InvoiceCommunicationTracking tracking = InvoiceCommunicationTrackingDAO.get(ctx, f -> f.getDomainProperty().eq(invoice.getDomain())
				.and(f.getInvoiceProperty().eq(invoice.getId()))
				.and(f.getTypeProperty().eq(InvoiceCommunicationType.VERIFACTU.value()))
				.and(f.getOperationProperty().eq(InvoiceCommunicationOperation.REGISTER.value())));
		assertNotNull(tracking);
		assertNotNull(tracking.getInvoiceBatchDetail());
		assertEquals(InvoiceCommunicationStatus.ACCEPTED, tracking.getInvoiceBatchDetail().getStatus());
		
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
	}
	
}
