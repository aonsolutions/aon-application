package com.code.aon.webservice.common;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.issues.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;

@SuppressWarnings("serial")
@WebServlet(name = "CommonServlet", urlPatterns = { "/common/*",
													 "/aon_gwt_aio/common/*"})
public class CommonServlet extends HttpServlet{
		
	String h = "http://";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
		if(req.getServerPort() == 80) h = "http://";
		else if(req.getServerPort() == 443) h = "https://";
		String accessToken = req.getParameter("access_token");
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
				String js = req.getParameter("callback");
				if(js != null){
					resp.setContentType("application/javascript; charset=utf-8");     
					PrintWriter out = resp.getWriter();
					out.print(js + "({" +"\"meta\":"+ meta +", \"data\":" + object +"});");
					out.flush();
				} else {
					resp.setContentType("application/json");     
					PrintWriter out = resp.getWriter();
					out.print(object);
					out.flush();
				}
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST METHOD");
	}

    private JSONArray getWorkplaceList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getWorkplaceList(domain.getName(), domain.getId(), login, 
    			f -> f.getDomainProperty().eq(domain.getId()))
    	.stream().forEach(wp -> {
    		JSONObject json = new JSONObject();
    		json.put("id", wp.getId());
    		json.put("name", wp.getDescription());
    		array.put(json);
    	});
    	return array;
    }
  
}
