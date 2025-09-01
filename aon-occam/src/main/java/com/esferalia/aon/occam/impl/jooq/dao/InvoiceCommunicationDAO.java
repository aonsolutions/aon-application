package com.esferalia.aon.occam.impl.jooq.dao;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceCommunicationTrackingDAO;

public class InvoiceCommunicationDAO {
	
	private InvoiceCommunicationDAO() {

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
		Attach requestAttach = AttachmentDAO.getDataAttachStream(ctx, f -> f.getSourceTypeProperty().eq(DataAttachSource.VERIFACTU.value())
				.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
				.and(f.getSourceBatchProperty().eq(dataRequest)), false)
				.findFirst().orElse(new Attach());
			
		JSONObject requestData = new JSONObject();
		requestData.put("domain_name", ctx.getDomainName());
		requestData.put("domain_id", ctx.getDomainId());
		requestData.put("id", requestAttach.getId());
		requestData.put("attach_type", AttachType.DATA.getName());
		String result = Base64.getEncoder().encodeToString(requestData.toString().getBytes(StandardCharsets.UTF_8));
		return "ms/api/file/" +  result;
	}
	
	private static String getVerifactuResponseUrl(AONContext ctx, Integer dataResponse) {
		Attach responseAttach = AttachmentDAO.getDataAttachStream(ctx, f -> f.getSourceTypeProperty().eq(DataAttachSource.VERIFACTU.value())
				.and(f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value())
					.or(f.getTypeProperty().eq(DataAttachType.RESPONSE_ERROR.value())))
				.and(f.getSourceBatchProperty().eq(dataResponse)), false).findFirst()
				.orElse(new Attach());
			
		JSONObject responseData = new JSONObject();
		responseData.put("domain_name", ctx.getDomainName());
		responseData.put("domain_id", ctx.getDomainId());
		responseData.put("id", responseAttach.getId());
		responseData.put("attach_type", AttachType.DATA.getName());
		String responseResult = Base64.getEncoder().encodeToString(responseData.toString().getBytes(StandardCharsets.UTF_8));
		return "ms/api/file/" +  responseResult;
	}
}




