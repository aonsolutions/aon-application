package com.code.aon.webservice.sii;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;

@SuppressWarnings("serial")
@WebServlet(name = "SiiServletS11", urlPatterns = { "/s11/*",
													 "/aon_gwt_aio/ms/s11/*"})
public class SiiServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(SiiServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Sii Servlet - GET METHOD");
		String accessToken = req.getParameter(MSG.ACCESS_TOKEN);
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[2];
		String domainName = pathInfo[1];
		String md5 = Utils.getMd5(userName+domainName);
		
		if(accessToken.equals(md5)){
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
			if(pathInfo.length > 3){
				Object object = new Object();
				JSONObject meta = new JSONObject();
				switch (pathInfo[3]) {
				case "invoiceHistory":
					object = getSiiInvoiceHistory(domain, userName,  Integer.parseInt(req.getParameter("id")));
					break;
				case "history": // INVOICE
					Integer page = Integer.parseInt(req.getParameter("page"));
					Integer perPage = Integer.parseInt(req.getParameter("per_page"));
					object = getSiiHistory(domain, userName, page, perPage);
					break;
				case "historyDetail":
					object = getSiiHistoryDetail(domain, userName, Integer.parseInt(req.getParameter("id")));
					break;
				case "configuration":
					object = getSiiConfiguration(domain, userName);
					break;
				default:
					break;
				}
				Utils.giveBack(req, resp, object, meta);
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Sii Servlet - POST METHOD");	
		JSONObject json = Utils.getRequestJSON(req);
		
		String scheme = req.getParameter("scheme");
		String[] pathInfo = req.getPathInfo().split("/");
		String userName = pathInfo[2];
		String domainName = pathInfo[1]; 
		String url = Utils.getUrl(scheme, domainName, req.getRequestURL().toString().contains("aon-aio"));
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){
			Object object = new Object();
			if("configuration".equals(pathInfo[3])) {
				setSiiConfiguration(domain, userName, json);
			}
		}
	}
    
    private JSONArray getSiiHistory(Domain domain, String login, Integer page, Integer perPage){
    	JSONArray array = new JSONArray();
    	
    	AON.getDataResponseStream(domain.getName(), domain.getId(), login, DataResponseSource.SII, f -> 
    		f.getDomainProperty().eq(domain.getId())
    		.and(f.getSourceProperty().eq(DataResponseSource.SII.value()))
    		.page(page).perPage(perPage))
    	.forEach(r -> {
    		array.put(ToJSON.dataResponseToJSON(r));
    	});
    	return array;
    }
    
    private JSONArray getSiiInvoiceHistory(Domain domain, String login, Integer id){
    	DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), login, DataResponseSource.SII_INVOICE, f -> f.getSourceIdProperty().eq(id));
    
    	JSONArray array = new JSONArray();
    	if(dr != null && dr.getId() != null) {
    		
    		AON.getAttachStream(domain.getName(), domain.getId(), login, f -> 
    			f.getDomainProperty().eq(domain.getId())
    			.and(f.getSourceTypeProperty().eq(DataAttachSource.SII.value()))
    			.and(f.getSourceBatchProperty().eq(dr.getId())), AttachType.DATA, false)
    		.forEach(r -> {
    			array.put(ToJSON.objectToJSON(r.getId(), r.getDescription(), r.getCreationDate()));
    		});
    	}
    	return array;
    }

    private JSONArray getSiiHistoryDetail(Domain domain, String login, Integer id){
    	JSONArray array = new JSONArray();
    	Integer[] ids = AON.getDataResponseStream(domain.getName(), domain.getId(), login, DataResponseSource.SII_INVOICE, f -> 
    		f.getDomainProperty().eq(domain.getId())
    		.and(f.getSourceProperty().eq(DataResponseSource.SII_INVOICE.value()))
    		.and(f.getDetailVariableProperty().eq("send"))
    		.and(f.getDetailValueProperty().eq(id.toString())))
    	.map(m -> m.getSourceId()).toArray(Integer[]::new);
    	
    	AON.getInvoiceStream(domain.getName(), domain.getId(), login, f-> f.getIdProperty().in(ids))
    	.forEach(r -> {
    		array.put(ToJSON.invoiceToJSON(r));
    	});
    	return array;
    }
    
    private JSONArray getSiiConfiguration(Domain domain, String login){
    	JSONArray array = new JSONArray();

    	ApplicationParameter ap = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_MODEL_CFG_SII.getValue());
    	JSONObject json = new JSONObject();
    	if(ap.getValue() != null && "R".equals(ap.getValue())) {
    		json.put("operation_date", "Fecha Auditoria");
    	} else json.put("operation_date", "Fecha IVA");
    	
    	JSONArray options = new JSONArray();

    	JSONObject option1 = new JSONObject();
    	option1.put("id", 0);
    	option1.put("name", "Fecha IVA");
    	options.put(option1);
    	
    	JSONObject option2 = new JSONObject();
    	option2.put("id", 1);
    	option2.put("name", "Fecha Auditoria");
    	options.put(option2);
    	
    	json.put("operation_date_option", options);

    	ApplicationParameter ap2 = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.SII_INCLUDE_DATE.getValue());
    	if(ap2 != null && ap2.getId() != null) {
    		json.put("sii_date", ap2.getValue());
    	} else {
    		ApplicationParameter param= AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_DEFAULT_ADMINISTRATION);
    		Administration administration = param.getValue() != null ? Administration.values()[Integer.parseInt(param.getValue())] : Administration.COMMON_TERRITORY;
    		String defaultDate = administration.equals(Administration.COMMON_TERRITORY) ? "2017-07-01" : "2018-01-01";
    		AON.insertApplicationParameter(domain.getName(), domain.getId(), login, AppParam.SII_INCLUDE_DATE, defaultDate);
    		json.put("sii_date", defaultDate);
    	}
    	array.put(json);
    	return array;
    }
    
    private void setSiiConfiguration(Domain domain, String login, JSONObject json){
    	json.getString("sii_date");
    	if(json.opt("operation_date") != null) {
    		AON.insertApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_MODEL_CFG_SII, "Fecha Registro".equals(json.getString("operation_date")) ? "R" : "I");
    	}
    	if(json.opt("sii_date") != null) {
    		AON.insertApplicationParameter(domain.getName(), domain.getId(), login, AppParam.SII_INCLUDE_DATE, json.getString("sii_date"));
    	}
    }
}
