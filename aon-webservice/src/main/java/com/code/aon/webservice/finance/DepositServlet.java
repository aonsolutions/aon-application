package com.code.aon.webservice.finance;
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
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.DepositType;
import com.esferalia.aon.occam.api.model.type.AppParam;

@SuppressWarnings("serial")
@WebServlet(name = "DepositServletWebservice", urlPatterns = { "/deposit/*",
													 "/aon_gwt_aio/ms/deposit/*"})
public class DepositServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(DepositServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Deposit Servlet - GET METHOD");
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
				if("configuration".equals(pathInfo[3])) {
					object = getDepositConfiguration(domain, userName);
				}
				Utils.giveBack(req, resp, object, meta);
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Deposit Servlet - POST METHOD");	
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
				setDepositConfiguration(domain, userName, json);
			}
		}
	}
  
    private JSONArray getDepositConfiguration(Domain domain, String login){
    	JSONArray array = new JSONArray();

    	ApplicationParameter ap = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_MODEL_CFG_CCAA.getValue());
    	JSONObject json = new JSONObject();
    	if(ap.getValue() != null) {
    		json.put("operation", DepositType.values()[Integer.parseInt(ap.getValue())].getLabel());
    	} else json.put("operation", DepositType.ABREVIADO.getLabel());
    	
    	JSONArray options = new JSONArray();

    	JSONObject option1 = new JSONObject();
    	option1.put("id", 0);
    	option1.put("name", DepositType.ABREVIADO.getLabel());
    	options.put(option1);
    	
    	JSONObject option2 = new JSONObject();
    	option2.put("id", 1);
    	option2.put("name", DepositType.PYMES.getLabel());
    	options.put(option2);
    	
    	json.put("operation_option", options);
    	
    	array.put(json);
    	return array;
    }
    
    private void setDepositConfiguration(Domain domain, String login, JSONObject json){
    	if(json.opt("operation") != null) {
    		AON.insertApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_MODEL_CFG_CCAA,
    				Integer.toString(DepositType.valueOfLabel(json.getString("operation")).ordinal()));
    	}
    }
}
