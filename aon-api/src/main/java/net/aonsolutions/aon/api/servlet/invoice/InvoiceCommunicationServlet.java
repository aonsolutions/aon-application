package net.aonsolutions.aon.api.servlet.invoice;
import java.util.Map;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistory;
import com.esferalia.aon.occam.api.model.finance.InvoiceCommunicationHistoryMapValue;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationOperation;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationStatus;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.InvoiceInfoDAO;
import com.esferalia.aon.watson.util.AonCollectionUtils;

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

@WebServlet(name = "AonInvoiceCommunicationServlet", urlPatterns = {"/ms/api/communication/*"})
public class InvoiceCommunicationServlet extends AonApiHttpServlet{

	private static final long serialVersionUID = -5555314923648911908L;
	
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
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject getInvoiceCommunicationHistory(AonApiData api) {
		try {
			Integer invoiceId = JsonUtils.getInteger(api.getData(), IJsonNames.INVOICE);
			InvoiceCommunicationConfiguration config = AON.getInvoiceCommunicationConfiguration(api.getOccam());
			Occam occam = api.getOccam();
			try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
				Map<InvoiceCommunicationType, InvoiceCommunicationHistoryMapValue> h = InvoiceCommunicator.history(ctx, occam.getDomain(), invoiceId);
				if ( config.isTbai()) {
					if(config.isBizkaia()) {	
						LROEInformation lroeInfo = LroeData.get(occam, invoiceId, InvoiceType.SALES);		
						addLROE(ctx, h, lroeInfo, occam.getDomain(), invoiceId);
					} else {
						TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(occam);
						TBAIInformation tbaiInfo = TbaiData.getInstance(tbaiConfiguration).get(occam, invoiceId);
						addTBAI(ctx, h, tbaiInfo, occam.getDomain(), invoiceId);
					}			
				}  
				return InvoiceCommunicator.historyToJSON(h).orElse(new JSONObject());
			}
		} catch (Exception e) {
			throw new AonApiException( e.getMessage(), e );
		}
	}

	private void addLROE(AONContext ctx, Map<InvoiceCommunicationType, InvoiceCommunicationHistoryMapValue> h, LROEInformation lroeInfo, Integer domainId, Integer invoiceId) {
		if ( lroeInfo == null ) return;
		if ( lroeInfo.getChapter1() == null ) return;
		AonCollectionUtils.stream(lroeInfo.getChapter1().getRequests())
			.forEach(r -> {
				InvoiceCommunicationStatus status = r.getResponse().isOk() ? InvoiceCommunicationStatus.ACCEPTED : InvoiceCommunicationStatus.WRONG;
				InvoiceCommunicationHistoryMapValue lroe = h.computeIfAbsent(InvoiceCommunicationType.LROE, 
					k -> {
						InvoiceInfo info = new InvoiceInfo()
							.setInvoice(domainId)
							.setInvoice(invoiceId)
							.setType(k)
							.setStatus(status)
							.setCreationUser(r.getDataResponse().getCreationUser())
							.setCreationDate(r.getDataResponse().getResponseDate());
						InvoiceInfoDAO.InvoiceInfoURLFiller.build(ctx, info);
						return new InvoiceCommunicationHistoryMapValue().setInfo( info);
					}
				);
				lroe.add(new InvoiceCommunicationHistory()
					.setInvoiceId(invoiceId)
					.setDate(r.getDataResponse().getResponseDate())
					.setCreationUser(r.getDataResponse().getCreationUser())
					.setType( InvoiceCommunicationType.LROE )
					.setOperation(operationEnumtoAonOperation(r.getInfo().getOperacion()))
					.setStatus(status)
					.setRequestUrl(r.getRequestUrl())
					.setResponseUrl(r.getResponseUrl())
				);
		});		
	}
	
	private InvoiceCommunicationOperation operationEnumtoAonOperation(OperacionEnum operation) {
		if(OperacionEnum.M_00.equals(operation) || OperacionEnum.M_01.equals(operation) ) return InvoiceCommunicationOperation.MODIFICATION;
		else if(OperacionEnum.AN_0.equals(operation)) return InvoiceCommunicationOperation.ANNULMENT;
		else if(OperacionEnum.C_00.equals(operation)) return InvoiceCommunicationOperation.CONSULTATION;
		else return InvoiceCommunicationOperation.REGISTER;
	}

	
	private void addTBAI(AONContext ctx, Map<InvoiceCommunicationType, InvoiceCommunicationHistoryMapValue> h, TBAIInformation tbaiInfo, Integer domainId, Integer invoiceId) {
		if ( tbaiInfo == null ) return;
		AonCollectionUtils.stream(tbaiInfo.getRequests())
			.forEach(r -> {
				InvoiceCommunicationStatus status = r.getResponse().isOk() ? InvoiceCommunicationStatus.ACCEPTED : InvoiceCommunicationStatus.WRONG;
				InvoiceCommunicationHistoryMapValue lroe = h.computeIfAbsent(InvoiceCommunicationType.TBAI, 
					k -> {
						InvoiceInfo info = new InvoiceInfo()
							.setDomain(domainId)	
							.setInvoice(invoiceId)
							.setType(k)
							.setStatus(status)
							.setCreationUser(r.getDataResponse().getCreationUser())
							.setCreationDate(r.getDataResponse().getResponseDate());
						InvoiceInfoDAO.InvoiceInfoURLFiller.build(ctx, info);
						return new InvoiceCommunicationHistoryMapValue().setInfo( info);
					}
				);
				lroe.add(new InvoiceCommunicationHistory()
					.setInvoiceId(invoiceId)
					.setDate(r.getDataResponse().getResponseDate())
					.setCreationUser(r.getDataResponse().getCreationUser())
					.setType( InvoiceCommunicationType.TBAI )
					.setOperation( InvoiceCommunicationOperation.REGISTER)
					.setStatus(status)
					.setRequestUrl(r.getRequestUrl())
					.setResponseUrl(r.getResponseUrl())
				);
		});		
	}
	
	// ***********************************************************
	// *************************** [OLD] *************************
	// ***********************************************************
//	private JSONArray tbaiInfo2JSON(TBAIInformation tbaiInfo) {
//		JSONArray arr = new JSONArray();
//		tbaiInfo.getRequests().stream().forEach(r -> {
//			JSONObject json = new JSONObject();
//			json.put(IJsonNames.DATE, r.getDataResponse().getResponseDate());
//			json.put("operation", r.getOperacion());
//			json.put("requestUrl", r.getRequestUrl());
//			json.put("responseUrl", r.getResponseUrl());
//			json.put("ok", r.getResponse().isOk());
//			json.put("status", r.getResponse().isOk() ? InvoiceCommunicationStatus.ACCEPTED : InvoiceCommunicationStatus.WRONG);
//			arr.put(json);
//		});		
//		return arr;
//	}
//	
//	private JSONArray lroeInfo2JSON(LROEInformation tbaiInfo) {
//		JSONArray arr = new JSONArray();
//		tbaiInfo.getChapter1().getRequests().stream().forEach(r -> {
//			JSONObject json = new JSONObject();
//			json.put(IJsonNames.DATE, r.getDataResponse().getResponseDate());
//			json.put("operation", getLroeOperation(r.getInfo().getOperacion()));
//			json.put("requestUrl", r.getRequestUrl());
//			json.put("responseUrl", r.getResponseUrl());
//			json.put("ok", r.getResponse().isOk());
//			json.put("status", r.getResponse().isOk() ? InvoiceCommunicationStatus.ACCEPTED : InvoiceCommunicationStatus.WRONG);
//			arr.put(json);
//		});		
//		return arr;
//	}
//	
//	private String getLroeOperation(OperacionEnum operation) {
//		if(OperacionEnum.M_00.equals(operation) || OperacionEnum.M_01.equals(operation) ) return "Modificación";
//		else if(OperacionEnum.AN_0.equals(operation)) return "Anulación";
//		else if(OperacionEnum.C_00.equals(operation)) return "Consulta";
//		else return "Alta";
//	}
}
