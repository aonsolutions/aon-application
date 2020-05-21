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
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.security.UserScope;

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
			if("deleteScope".equals(pathInfo[3])){
				Integer id = Integer.parseInt(pathInfo[4]);
				Company cp = AON.getCompany(domain.getName(), domain.getId(), userName, f -> f.getIdProperty().eq(id));
				Domain d = AON.getDomain(domainName, cp.getDomain(), userName);
				final Scope scope = AON.getScope(domain.getName(), domain.getId(), userName, d.getScope());
				d.setScope(null);
				AON.updateDomainScope(d.getName(), cp.getDomain(), userName, d);

				Domain dom = AON.getDomain(domain.getName(), domain.getId(), userName, f -> f.getScopeProperty().eq(scope.getId()));
				if(dom == null || dom.getId() == null) {
					AON.deleteUserScope(domain.getName(), domain.getId(), userName, scope.getId());					
					AON.deleteScope(domain.getName(), domain.getId(), userName, scope.getId());
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
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Company Servlet - POST METHOD");

		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		User user = AON.getUser(domain.getName(), domain.getId(), userName);
		if(pathInfo.length > 3){				
			JSONObject object = new JSONObject();
			if("generateScope".equals(pathInfo[3])){
				Integer id = Integer.parseInt(pathInfo[4]);
				Company cp = AON.getCompany(domain.getName(), domain.getId(), userName, f -> f.getIdProperty().eq(id));
				Domain d = AON.getDomain(domainName, cp.getDomain(), userName);
				if(cp.getDocument() != null) {
					Scope scope = AON.getScopeStream(domain.getName(), domain.getId(), userName, 
						f -> f.getDomainProperty().eq(domain.getId()).and(f.getDescriptionProperty().eq(cp.getDocument()))).findFirst().orElse(null);
					if(scope == null) {
						scope = AON.insertScope(domain.getName(), domain.getId(), userName, new Scope()
								.setDomain(domain.getId())
								.setDescription(cp.getDocument()));
					}
					UserScope us = AON.getUserScope(domain.getName(), domain.getId(), user.getLogin(), user.getId(), scope.getId());
					if(us == null || us.getId() == null) {
						AON.insertUserScope(domain.getName(),domain.getId(), user.getLogin(), new UserScope()
								.setDomain(domain.getId())
								.setScope(scope.getId())
								.setUserId(user.getId()));
					}
					d.setScope(scope.getId());
					AON.updateDomainScope(d.getName(), cp.getDomain(), userName, d);
					AON.getWorkplaceList(d.getName(), d.getId(), userName, f -> f.getDomainProperty().eq(d.getId()))
						.stream().forEach(wp -> AON.updateWorkplace(d.getName(), d.getId(), userName, wp.setScope(d.getScope())));
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
			Integer[] array = AON.getUserScopes(domain.getName(), domain.getId(), "", userId);
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
