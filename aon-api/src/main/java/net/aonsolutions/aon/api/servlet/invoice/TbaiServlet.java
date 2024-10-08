package net.aonsolutions.aon.api.servlet.invoice;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.invoice.TbaiConfigurationJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

import https.www_batuz_eus.fitxategiak.batuz.lroe.esquemas.batuz_enumerados.OperacionEnum;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.tbai.LroeData;
import net.aonsolutions.aon.tbai.TBAIInformation;
import net.aonsolutions.aon.tbai.TbaiData;
import net.aonsolutions.aon.tbai.lroe.LROEInformation;

@SuppressWarnings("serial")
@WebServlet(name = "AonTbaiServlet", urlPatterns = {"/ms/api/tbai/*"})
public class TbaiServlet extends AonApiHttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(TbaiServlet.class.getName());

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
				response(req, resp, getTbaiHistory(api));
				break;
			case "/configuration":
				response(req, resp, getTbaiConfiguration(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject getTbaiConfiguration(AonApiData api) {
		return TbaiConfigurationJSON.toJSON(AON.getTbaiConfiguration(api.getDomain(), api.getUser()));
	}
	
	private JSONArray getTbaiHistory(AonApiData api) {
		Integer invoice = JsonUtils.getInteger(api.getData(), IJsonNames.INVOICE);
		TbaiConfiguration tbaiConfiguration = AON.getTbaiConfiguration(api.getDomain(), api.getUser().getLogin());
		if(tbaiConfiguration.isBizkaia()) {	
			LROEInformation lroeInfo = LroeData.get(api.getDomain(), api.getUser(), invoice, InvoiceType.SALES);		
			return lroeInfo2JSON(lroeInfo);
		} else {
			TBAIInformation tbaiInfo = TbaiData.getInstance(tbaiConfiguration).get(api.getDomain(), api.getUser(), invoice);
			return tbaiInfo2JSON(tbaiInfo);
		}
	}
	
	public JSONArray tbaiInfo2JSON(TBAIInformation tbaiInfo) {
		JSONArray arr = new JSONArray();
		tbaiInfo.getRequests().stream().forEach(r -> {
			JSONObject json = new JSONObject();
			json.put(IJsonNames.DATE, r.getDataResponse().getResponseDate());
			json.put("operation", r.getOperacion());
			json.put("requestUrl", r.getRequestUrl());
			json.put("responseUrl", r.getResponseUrl());
			json.put("ok", r.getResponse().isOk());
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
