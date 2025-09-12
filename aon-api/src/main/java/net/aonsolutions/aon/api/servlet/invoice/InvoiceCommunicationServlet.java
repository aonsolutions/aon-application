package net.aonsolutions.aon.api.servlet.invoice;
import java.util.logging.Logger;
import java.util.stream.Collector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.JsonUtils.JSONArrayCollector;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationException;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.invoice.communication.InvoiceCommunicator;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.TBAIInformation;
import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.tbai.lroe.LROEInformation;

@SuppressWarnings("serial")
@WebServlet(name = "AonInvoiceCommunicationServlet", urlPatterns = {"/ms/api/communication/*"})
public class InvoiceCommunicationServlet extends AonApiHttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(InvoiceCommunicationServlet.class.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/history":
				response(req, resp, getInvoiceCommunicationHistory(api));
				break;
			case "/configuration":
				response(req, resp, getInvoiceCommunicationConfiguration(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject getInvoiceCommunicationConfiguration(AonApiData api) {
		AON.getInvoiceCommunicationConfiguration(api.getOccam());
		//TODO InvoiceCommunicationConfigurationJSON
		return new JSONObject();
	}
	
	private JSONArray getInvoiceCommunicationHistory(AonApiData api) {
		Integer invoice = JsonUtils.getInteger(api.getData(), IJsonNames.INVOICE);
		InvoiceCommunicationConfiguration config = AON.getInvoiceCommunicationConfiguration(api.getOccam());
		if (config.isVerifactu()) {
			try {
				Occam occam = api.getOccam();
				return AonCollectionUtils.stream( InvoiceCommunicator.history(occam, invoice) )
					.map( h -> new JSONObject() 
						.put(IJsonNames.DATE, h.getDate())
						.put(IJsonNames.CREATION_USER, h.getCreationUser())
						.put(IJsonNames.OPERATION, h.getOperation().getDescription())
						.put(IJsonNames.REQUEST_URL, h.getRequestUrl())
						.put(IJsonNames.RESPONSE_URL, h.getResponseUrl())
						.put(IJsonNames.RESPONSE_MESSAGES, 
							AonCollectionUtils.stream(h.getResponseMessages())
							.filter(AonStringUtils::isNotBlank)
							.collect(JSONArrayCollector.toJSONArray())
							)
						.put(IJsonNames.STATUS, h.getStatus().name())
					)
					.collect( JSONArray::new, JSONArray::put, JSONArray::putAll )
				;
			} catch (InvoiceCommunicationException e) {
				// Nothingis shown
			}
		} else  if(config.isTbai()) {
			TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(api.getDomain(), api.getUser().getLogin());
			if(tbaiConfiguration.isBizkaia()) {	
				LROEInformation lroeInfo = LroeData.get(api.getDomain(), api.getUser(), invoice, InvoiceType.SALES);		
				return lroeInfo2JSON(lroeInfo);
			} else {
				TBAIInformation tbaiInfo = TbaiData.getInstance(tbaiConfiguration).get(api.getDomain(), api.getUser(), invoice);
				return tbaiInfo2JSON(tbaiInfo);
			}			
		}  
//		} else if (config.isVerifactu()) {
//			JSONArray arr = new JSONArray();
//			AON.getInvoiceCommunicationTrackingStream(api.getOccam(), f -> f.getInvoiceProperty().eq(invoice)
//					.and(f.getTypeProperty().eq(InvoiceCommunicationType.VERIFACTU.value())))
//			.forEach(tracking -> {
//				DataResponse dr = AON.getDataResponse(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
//					f.getDomainProperty().eq(api.getDomain().getId())
//					.and(f.getIdProperty().eq(tracking.getInvoiceBatch().getDataResponse())));
//				
//				JSONObject json = new JSONObject();
//				json.put(IJsonNames.DATE, tracking.getInvoiceBatch().getDate());
//				json.put("operation", tracking.getInvoiceBatch().getOperation().getDescription());
//				json.put("requestUrl", getVerifactuRequestUrl(api, dr.getDataRequest()));
//				json.put("responseUrl", getVerifactuResponseUrl(api, dr.getId()));
//				json.put("status", tracking.getInvoiceBatchDetail().getStatus().name());
//				arr.put(json);
//			});
//			return arr;			
		return new JSONArray();
	}
	
//	private String getVerifactuRequestUrl(AonApiData api, Integer dataRequest) {
//		Attach requestAttach = AON.getAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getSourceTypeProperty().eq(DataAttachSource.VERIFACTU.value())
//				.and(f.getTypeProperty().eq(DataAttachType.REQUEST.value()))
//				.and(f.getSourceBatchProperty().eq(dataRequest)), AttachType.DATA, false);
//			
//		JSONObject requestData = new JSONObject();
//		requestData.put("domain_name", api.getDomain().getName());
//		requestData.put("domain_id", api.getDomain().getId());
//		requestData.put("id", requestAttach.getId());
//		requestData.put("attach_type", AttachType.DATA.getName());
//		String result = Base64.getEncoder().encodeToString(requestData.toString().getBytes(StandardCharsets.UTF_8));
//		return "ms/api/file/" +  result;
//	}
//	
//	private String getVerifactuResponseUrl(AonApiData api, Integer dataResponse) {
//		Attach responseAttach = AON.getAttach(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> f.getSourceTypeProperty().eq(DataAttachSource.VERIFACTU.value())
//				.and(f.getTypeProperty().eq(DataAttachType.RESPONSE_OK.value())
//					.or(f.getTypeProperty().eq(DataAttachType.RESPONSE_ERROR.value())))
//				.and(f.getSourceBatchProperty().eq(dataResponse)), AttachType.DATA, false);
//			
//		JSONObject responseData = new JSONObject();
//		responseData.put("domain_name", api.getDomain().getName());
//		responseData.put("domain_id", api.getDomain().getId());
//		responseData.put("id", responseAttach.getId());
//		responseData.put("attach_type", AttachType.DATA.getName());
//		String responseResult = Base64.getEncoder().encodeToString(responseData.toString().getBytes(StandardCharsets.UTF_8));
//		return "ms/api/file/" +  responseResult;
//	}
	
	public JSONArray tbaiInfo2JSON(TBAIInformation tbaiInfo) {
		JSONArray arr = new JSONArray();
		tbaiInfo.getRequests().stream().forEach(r -> {
			JSONObject json = new JSONObject();
			json.put(IJsonNames.DATE, r.getDataResponse().getResponseDate());
			json.put("operation", r.getOperacion());
			json.put("requestUrl", r.getRequestUrl());
			json.put("responseUrl", r.getResponseUrl());
			json.put("ok", r.getResponse().isOk());
			json.put("status", r.getResponse().isOk() ? InvoiceCommunicationStatus.ACCEPTED : InvoiceCommunicationStatus.WRONG);
			arr.put(json);
		});		
		return arr;
	}
	
	public JSONArray lroeInfo2JSON(LROEInformation tbaiInfo) {
		JSONArray arr = new JSONArray();
		tbaiInfo.getChapter1().getRequests().stream().forEach(r -> {
			JSONObject json = new JSONObject();
			json.put(IJsonNames.DATE, r.getDataResponse().getResponseDate());
			json.put("operation", getLroeOperation(r.getInfo().getOperacion()));
			json.put("requestUrl", r.getRequestUrl());
			json.put("responseUrl", r.getResponseUrl());
			json.put("ok", r.getResponse().isOk());
			json.put("status", r.getResponse().isOk() ? InvoiceCommunicationStatus.ACCEPTED : InvoiceCommunicationStatus.WRONG);
			arr.put(json);
		});		
		return arr;
	}
	
	public String getLroeOperation(OperacionEnum operation) {
		if(OperacionEnum.M_00.equals(operation) || OperacionEnum.M_01.equals(operation) ) return "Modificación";
		else if(OperacionEnum.AN_0.equals(operation)) return "Anulación";
		else if(OperacionEnum.C_00.equals(operation)) return "Consulta";
		else return "Alta";
	}
}
