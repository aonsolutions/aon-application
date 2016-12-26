package com.code.aon.webservice.project;

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
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "ProjectServlet", urlPatterns = { "/project/*",
													 "/aon_gwt_aio/project/*"})
public class ProjectServlet extends HttpServlet{
			
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
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
				case "commercial":
					if(pathInfo.length > 4){
						if (pathInfo[4].equals("registry")) {
							if(pathInfo.length > 5)
								if(AonStringUtils.isNumeric(pathInfo[5]))
									object = getProjectList(domain, userName, Integer.parseInt(pathInfo[5]));
								else object = new JSONObject();
						} 
					}
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

	private JSONArray getProjectList(Domain domain, String login, Integer registryId){
    	JSONArray array = new JSONArray();
		AON.getProjectCommercialStream(domain.getName(), domain.getId(), login, f -> f.getTargetProperty().eq(registryId)
			.and(f.getDomainProperty().eq(domain.getId()))).forEach(pc -> {
				Registry registry = new Registry().setId(pc.getRegistryId()).setName(pc.getRegistryName());
				Registry seller = AON.getRegistry(domain.getName(), domain.getId(), login, pc.getSeller());
				JSONObject json = ToJSON.projectCommercialToJSON(pc, registry, seller);
		    	JSONArray ar = new JSONArray();
		    	AON.getCommercialTrackingStream(domain.getName(), domain.getId(), login, f2 -> f2.getProjectCommercialProperty().eq(pc.getId()))
		    		.forEach(ct -> ar.put(ToJSON.commercialTrackingToJSON(ct,  AON.getRegistry(domain.getName(), domain.getId(), login, ct.getSeller()))));
				json.put("commercial_tracking", ar);
				array.put(json);
			});
		return array;
	}

}
