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
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.CompanyProperties;

@SuppressWarnings("serial")
@WebServlet(name = "CompanyServlet32", urlPatterns = {
												   "/aon_gwt_aio/ms/company/*",
												   "/aon_gwt_commercial/ms/company/*",
												   "/aon_gwt_fiscal/ms/company/*"})
public class CompanyServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CompanyServlet.class.getName());

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
		object = getCompanyList(domain, userName, req.getParameterMap());
				
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, object, meta);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Company Servlet - DELETE METHOD");
		

		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){				
			JSONObject object = new JSONObject();
						
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
		LOGGER.info("Company Servlet - POST METHOD");

		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		JSONObject json = Utils.getRequestJSON(req);
		if(pathInfo.length > 3){				
			JSONObject object = new JSONObject();
			if("generateScope".equals(pathInfo[3])){
				Integer id = json.getInt("company");
				Company cp = AON.getCompany(domain.getName(), domain.getId(), userName, f -> f.getIdProperty().eq(id));
				cp.getDomain().setScope(json.getInt("scope"));
				AON.updateDomainScope(cp.getDomain().getName(), cp.getDomain().getId(), userName, cp.getDomain());
			} 
			
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}
	

	
    private JSONArray getCompanyList(Domain domain, String login, Map<String,String[]> map){
    	JSONArray array = new JSONArray();
    	AON.getCompanyStream(domain.getName(), domain.getId(), login, f-> companyFilter(domain, map, f))
    	.forEach(company -> {
    		array.put(ToJSON.companyToJSON(company));
    	});
    	return array;
    }
    
	
	public static Filter companyFilter(Domain domain, Map<String, String[]> filterMap, CompanyProperties f) {
		Filter filter = f.getDomainParentProperty().eq(domain.getId());

		if(filterMap.containsKey("description")){
    		String description = filterMap.get("description")[0];
    		filter = filter.and(f.getNameProperty().like("%"+ description + "%").or(f.getDocumentProperty().like("%" + description + "%"))
    				.or(f.getScopeDescriptionProperty().like("%" + description + "%")));
		}
		
		if(filterMap.containsKey("user")) {
			Integer userId = Integer.parseInt(filterMap.get("user")[0]);
			Integer[] array = { -1 };
			try {
				array = AON.getUserScopes(domain.getName(), domain.getId(), "", userId);
			} catch (Exception e) {}
			filter = filter.and(f.getScopeIdProperty().in(array));
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
