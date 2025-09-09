package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.DataAttach.DATA_ATTACH;
import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.InvoiceBatch.INVOICE_BATCH;
import static com.esferalia.aon.jooq.tables.InvoiceBatchDetail.INVOICE_BATCH_DETAIL;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONObject;

import com.esferalia.aon.jooq.tables.DataAttach;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceCommunicationTrackingDAO;

public class InvoiceCommunicationDAO {
	
	private static final DataAttach DATA_ATTACH_REQUEST = DATA_ATTACH.as("data_attach_request");
	private static final DataAttach DATA_ATTACH_RESPONSE = DATA_ATTACH.as("data_attach_response");
	
	private InvoiceCommunicationDAO() {

	}
	public static List<InvoiceCommunicationHistory> getHistory(AONContext ctx, Integer invoiceId, Function<InvoiceCommunicationHistory, List<String>> messagesExtractor) {
		return ctx.getDslContext().select(
				INVOICE_BATCH.DATE,
				INVOICE_BATCH.OPERATION,
				INVOICE_BATCH.CREATION_DATE,
				INVOICE_BATCH.CREATION_USER,
				INVOICE_BATCH_DETAIL.STATUS,
				DATA_RESPONSE.DATA_REQUEST,
				DATA_RESPONSE.ID,
				DATA_ATTACH_REQUEST.ID,
				DATA_ATTACH_RESPONSE.ID,
				DATA_ATTACH_RESPONSE.DATA
			)
			.from(INVOICE_BATCH_DETAIL)
			.join(INVOICE_BATCH).on(INVOICE_BATCH.ID.eq(INVOICE_BATCH_DETAIL.INVOICE_BATCH)
				.and(INVOICE_BATCH.TYPE.eq(InvoiceCommunicationType.VERIFACTU.value())))
			.join(DATA_RESPONSE).on(DATA_RESPONSE.ID.eq(INVOICE_BATCH.DATA_RESPONSE))
			.leftOuterJoin(DATA_ATTACH_REQUEST).on(DATA_ATTACH_REQUEST.SOURCE_ID.eq(DATA_RESPONSE.ID)
				.and(DATA_ATTACH_REQUEST.SOURCE.eq(DataAttachSource.VERIFACTU.value())
				.and(DATA_ATTACH_REQUEST.TYPE.eq(DataAttachType.REQUEST.value())))
			)
			.leftOuterJoin(DATA_ATTACH_RESPONSE).on(DATA_ATTACH_RESPONSE.SOURCE_ID.eq(DATA_RESPONSE.ID)
				.and(DATA_ATTACH_RESPONSE.SOURCE.eq(DataAttachSource.VERIFACTU.value()))
				.and(DATA_ATTACH_RESPONSE.TYPE.in(DataAttachType.RESPONSE_OK.value(), DataAttachType.RESPONSE_ERROR.value()))
			)
			.where( INVOICE_BATCH_DETAIL.INVOICE.eq(invoiceId))
			.fetch()
			.stream()
			.map( r -> new InvoiceCommunicationHistory()
				.setInvoiceId(invoiceId)
				.setDate(r.getValue(INVOICE_BATCH.DATE))
				.setCreationUser(r.getValue(INVOICE_BATCH.CREATION_USER))
				.setOperation(InvoiceCommunicationOperation.safeValueOf(r.getValue(INVOICE_BATCH.OPERATION)))
				.setStatus(InvoiceCommunicationStatus.safeValueOf(r.getValue(INVOICE_BATCH_DETAIL.STATUS)))
				.setRequestUrl(getAttachUrl(ctx.getDomainName(), ctx.getDomainId(), r.getValue(DATA_ATTACH_REQUEST.ID)))
				.setResponseUrl(getAttachUrl(ctx.getDomainName(), ctx.getDomainId(), r.getValue(DATA_ATTACH_RESPONSE.ID)))
				.setResponseData(r.getValue(DATA_ATTACH_RESPONSE.DATA))
			)
			.map( h  -> (messagesExtractor != null)?h.setResponseMessages( messagesExtractor.apply(h) ):h )
			.collect(Collectors.toCollection(LinkedList::new))
		;
	}
	
	public static List<InvoiceCommunicationHistory> getHistory(AONContext ctx, Integer invoice) {
		InvoiceCommunicationConfiguration config = InvoiceCommunicationConfigurationDAO.get(ctx, ctx.getDomainId());
		if(config.isVerifactu()) {
			return InvoiceCommunicationTrackingDAO.getStream(ctx, f -> f.getInvoiceProperty().eq(invoice)
					.and(f.getTypeProperty().eq(InvoiceCommunicationType.VERIFACTU.value())))
				.map(tracking -> {
					DataResponse dr = DataResponseDAO.get(ctx, f -> 
						f.getDomainProperty().eq(ctx.getDomainId())
						.and(f.getIdProperty().eq(tracking.getInvoiceBatch().getDataResponse())));
					
					return new InvoiceCommunicationHistory()
						.setDate(tracking.getInvoiceBatch().getDate())
						.setOperation(tracking.getInvoiceBatch().getOperation())
						.setStatus(tracking.getInvoiceBatchDetail().getStatus())
						.setRequestUrl(getVerifactuRequestUrl(ctx, dr.getDataRequest()))
						.setResponseUrl(getVerifactuResponseUrl(ctx, dr.getId()));
				}).toList();
		} else return new LinkedList<>();
	}
	
	private static String getVerifactuRequestUrl(AONContext ctx, Integer dataRequest) {
		Attach requestAttach = AttachmentDAO.getDataAttachStream(ctx
			, f -> f.getSourceTypeProperty().eq(DataAttachSource.VERIFACTU.value())
				.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
				.and(f.getSourceBatchProperty().eq(dataRequest)), false)
			.findFirst()
			.orElse(new Attach());
		return getAttachUrl(ctx.getDomainName(), ctx.getDomainId(), requestAttach.getId());
	}

	private static String getVerifactuResponseUrl(AONContext ctx, Integer dataResponse) {
		Attach responseAttach = AttachmentDAO.getDataAttachStream(ctx
			, f -> f.getSourceTypeProperty().eq(DataAttachSource.VERIFACTU.value())
				.and(f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value())
					.or(f.getTypeProperty().eq(DataAttachType.RESPONSE_ERROR.value())))
				.and(f.getSourceBatchProperty().eq(dataResponse)), false)
			.findFirst()
			.orElse(new Attach());
		return getAttachUrl(ctx.getDomainName(), ctx.getDomainId(), responseAttach.getId());
	}
	
	private static String getAttachUrl(String domainName,Integer domainId, Integer attachId) {
		JSONObject attachData = new JSONObject();
		attachData.put("domain_name", domainName);
		attachData.put("domain_id", domainId);
		attachData.put("id", attachId);
		attachData.put("attach_type", AttachType.DATA.getName());
		String result = Base64.getEncoder().encodeToString(attachData.toString().getBytes(StandardCharsets.UTF_8));
		return "ms/api/file/" +  result;
	}
	
}




