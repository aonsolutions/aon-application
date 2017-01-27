package com.code.aon.webservice.common;
import java.io.IOException;
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
				case "workplace": // PRODUCT CATEGPRY
					object = getWorkplaceList(domain, userName);
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
	}

    private JSONArray getWorkplaceList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getWorkplaceList(domain.getName(), domain.getId(), login, 
    			f -> f.getDomainProperty().eq(domain.getId()))
    	.stream().forEach(wp -> 
    		array.put(ToJSON.objectToJSON(wp.getId(), wp.getDescription())));
    	return array;
    }
  
}
