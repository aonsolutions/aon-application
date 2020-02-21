package com.code.aon.webservice.common;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
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
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.ScopeProperties;
import com.esferalia.aon.occam.api.model.security.UserScope;

@SuppressWarnings("serial")
@WebServlet(name = "ScopeServlet32", urlPatterns = {"/scope/*",
												   "/aon_gwt_aio/ms/scope/*",
												   "/aon_gwt_commercial/ms/scope/*",
												   "/aon_gwt_fiscal/ms/scope/*"})
public class ScopeServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ScopeServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Scope Servlet - GET METHOD");

		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Integer domainId = Integer.parseInt(req.getParameter(MSG.DOMAIN));
		Domain domain = AON.getDomain(domainName, domainId, userName);
		Object object = new Object();
		JSONObject meta = new JSONObject();
		if(pathInfo.length>3) {
			switch (pathInfo[3]) {
			case "user":
				object = getUsersScopeList(domain, userName, Integer.parseInt(pathInfo[4]));
				break;
			case "company":
				object = getCompaniesScopeList(domain, userName, Integer.parseInt(pathInfo[4]));
				break;
			default :
				break;
			}
		} else {
			object = getScopeList(domain, userName, req.getParameterMap());
		}
		
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, object, meta);
	}
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Scope Servlet - DELETE METHOD");
		JSONObject json = Utils.getRequestJSON(req);

		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){				
			JSONObject object = new JSONObject();
			if("user".equals(pathInfo[3])){
				deleteUserScope(domain, userName, json);	
			} else if("company".equals(pathInfo[3])){
				deleteCompanyScope(domain, userName, json);
			}
			
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Scope Servlet - POST METHOD");
		JSONObject json = Utils.getRequestJSON(req);

		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){				
			JSONObject object = new JSONObject();
			if("user".equals(pathInfo[3])){
				userScope(domain, userName, json);	
			} else if("company".equals(pathInfo[3])){
			//	companyScope(domain, userName, json);
			}
			
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}
	
	private void userScope(Domain domain, String login, JSONObject json) {
		for(Integer i = 0 ; i < json.getJSONArray("users").length(); i++) {
			UserScope us = AON.getUserScope(domain.getName(), domain.getId(), login, 
					json.getJSONArray("users").getInt(i),
					json.getInt("scope"));
			if(us == null) {
				us = new UserScope()
						.setDomain(domain.getId())
						.setScope(json.getInt("scope"))
						.setUserId(json.getJSONArray("users").getInt(i));
				AON.insertUserScope(domain.getName(), domain.getId(), login, us);
			}
		}
	}
	
	private void deleteUserScope(Domain domain, String login, JSONObject json) {
		AON.deleteUserScope(domain.getName(), domain.getId(),login,
				json.getInt("user"), json.getInt("scope"));
	}
	
	private void deleteCompanyScope(Domain domain, String login, JSONObject json) {
		Domain d = AON.getDomain(domain.getName(), domain.getId(), login, 
				f -> f.getIdProperty().eq(json.getInt("company")));
		d.setScope(null);
		AON.updateDomainScope(d.getName(), d.getId(), login, d);
	}
	
	
    private JSONArray getScopeList(Domain domain, String login, Map<String,String[]> map){
    	JSONArray array = new JSONArray();
    	AON.getScopeStream(domain.getName(), domain.getId(), login, f-> scopeFilter(domain, map, f))
    	.forEach(scope -> {
    		array.put(ToJSON.objectToJSON(scope.getId(), scope.getDescription()));
    	});
    	
    	return array;
    }
    
    private JSONArray getUsersScopeList(Domain domain, String login, Integer scope){
    	JSONArray array = new JSONArray();
    	AON.getUsersByScope(domain.getName(), domain.getId(), login, scope)
    	.forEach(user -> {
    		array.put(ToJSON.userToJSON(user));
    	});
    	return array;
    }
    
    private JSONArray getCompaniesScopeList(Domain domain, String login, Integer scope){
    	JSONArray array = new JSONArray();
    	AON.getCompaniesByScope(domain.getName(), domain.getId(), login, scope)
    	.forEach(d -> {
    		array.put(ToJSON.objectToJSON(d.getId(), d.getDescription()));
    	});
    	return array;
    }
	
	public static Filter scopeFilter(Domain domain, Map<String, String[]> filterMap, ScopeProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());

		if(filterMap.containsKey("description")){
    		String description = filterMap.get("description")[0];
    		filter = filter.and(f.getDescriptionProperty().like("%"+ description + "%"));
		}
		
		if(filterMap.containsKey("per_page")){
			String per_page = filterMap.get("per_page")[0];
			Integer perPage = Integer.parseInt(per_page);
			filter.perPage(perPage);
		}
		if(filterMap.containsKey("page")){
			String page_str = filterMap.get("page")[0];
			Integer page = Integer.parseInt(page_str);
			filter.page(page);
		}
		return filter;
	}
    
}
