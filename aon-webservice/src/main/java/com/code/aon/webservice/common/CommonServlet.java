package com.code.aon.webservice.common;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;

@SuppressWarnings("serial")
@WebServlet(name = "CommonServlet", urlPatterns = { "/common/*",
													 "/aon_gwt_aio/common/*"})
public class CommonServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CommonServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Common Servlet - GET METHOD");
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
				case MSG.WORKPLACE: // PRODUCT CATEGPRY
					object = getWorkplaceList(domain, userName);
					break;
				case MSG.MAIL_ACCOUNT: // PRODUCT CATEGPRY
					object = getMailAccountList(domain, userName);
					break;
				case MSG.SIGNATURE: // PRODUCT CATEGPRY
					object = getSignatureList(domain, userName);
					break;
				case "app_param": // PRODUCT CATEGPRY
					object = new JSONObject();
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
		LOGGER.info("Common Servlet - POST METHOD");
		JSONObject json = Utils.getRequestJSON(req);

		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){				
			Object object = new Object();
			if("app_param".equals(pathInfo[3])){
				if(pathInfo.length > 4){ 
					if(MSG.UPDATE.equals(pathInfo[4])){
						object = new JSONObject();
					} else if(MSG.DELETE.equals(pathInfo[4])){

					} 
				} else {
					object = insertAppParam(domain, userName, json);
				}	
			}
			
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}

    private JSONArray getWorkplaceList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getWorkplaceList(domain.getName(), domain.getId(), login, 
    			f -> f.getDomainProperty().eq(domain.getId()))
    	.stream().forEach(wp -> 
    		array.put(ToJSON.objectToJSON(wp.getId(), wp.getDescription())));
    	return array;
    }
    
    private JSONArray getMailAccountList(Domain domain, String login) {		
    	JSONArray array = new JSONArray();
    	if(domain.isEnableHeredity())
			AON.getMailAccountList(domain.getName(), domain.getId(), login, f -> (f.getUserIdProperty().isNull())
				.and(f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId()))))
			.stream().forEach(ma -> array.put(ToJSON.objectToJSON(ma.getId(), ma.getName())));
		else AON.getMailAccountList(domain.getName(), domain.getId(), login, f -> (f.getUserIdProperty().isNull())
				.and(f.getDomainProperty().eq(domain.getId())))
			.stream().forEach(ma -> array.put(ToJSON.objectToJSON(ma.getId(), ma.getName())));
    	return array;
	}
    
	public  JSONArray getSignatureList(Domain domain, String login){
		JSONArray array = new JSONArray();
		AON.getSignatureList(domain.getName(), domain.getId(), login,
				f -> f.getUserIdProperty().isNull().and(f.getDomainProperty().eq(domain.getId())))
		.stream().forEach(s -> array.put(ToJSON.objectToJSON(s.getId(), s.getName())));
		return array;
	}
  
	private JSONArray insertAppParam(Domain domain, String login, JSONObject json) {
		String parameter = json.getString("parameter");
		String value = json.getString("value");
		AON.insertApplicationParameter(domain.getName(), domain.getId(), login, parameter, value);
		return new JSONArray();
	}
}
