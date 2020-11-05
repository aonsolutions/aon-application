package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;

@SuppressWarnings("serial")
@WebServlet(name = "AonCompanyServlet", urlPatterns = {"/ms/api/company/*"})
public class CompanyServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CompanyServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON COMPANY SERVLET - GET METHOD");
		String token = req.getHeader("session_id");

		JSONArray jsArray = new JSONArray();

		List<String> schemas = AONContext.getSchemas();
		String domainName = req.getHeader("domain_name");
		String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
		JSONObject json = new JSONObject();
		Object object = new Object();
		if(pathInfo  != null) {
			if("app".equalsIgnoreCase(pathInfo[1])) {
				Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
				json = getDomainApps(domain);
				object = json;
			}
		} else {
			for(String schema : schemas) {
				AON_SOLUTIONS.getCompanyStream(token, schema, null, null)
					.sorted((o1, o2) -> o1.getCompany().getName().compareTo(o2.getCompany().getName())).forEach(
							ac -> jsArray.put(ac.toJSON()));
			}
			object = jsArray;
		}
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, object, new JSONObject());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("COMPANY SERVLET - POST METHOD");
		String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
		JSONObject json = Utils.getRequestJSON(req);
		if(pathInfo != null) {
			if("app".equalsIgnoreCase(pathInfo[1])) {
				json = setDomainApp(json);
			} 
		}
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, json, new JSONObject());
	}
		
	private JSONObject getDomainApps(Domain domain){
		JSONObject json = new JSONObject();
		AON_SOLUTIONS.getDomainApp(domain.getName(), domain.getId(), "", f -> 
		f.getDomainProperty().eq(domain.getId())).forEach(domainApp -> {	
			if(domainApp.getApp() != null) {
				json.put(domainApp.getApp().name().toLowerCase(), domainApp.getActive());
			}
		});
		if(json.isEmpty()) {
			return oldModules(domain);
		}
		return json;
	}
	
	private JSONObject oldModules(Domain domain) {
		JSONObject json = new JSONObject();
		AON.getDomainModules(domain.getName(), domain.getId(), "").forEach(r -> {
			DomainApp dapp = new DomainApp()
					.setDomain(domain.getId())
					.setActive(true);
			if(Module.ACCOUNTING.equals(r)) {
				dapp.setApp(AonApp.ACCOUNTING);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp);
			} else if(Module.AON_FINANCE.equals(r)) {
				dapp.setApp(AonApp.INVOICE);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp);
			} else if(Module.CALL_CENTER.equals(r)) {
				dapp.setApp(AonApp.MESSENGER);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp);
			} else if(Module.CONTRATA.equals(r)) {
				
			} else if(Module.DOCUMENT.equals(r)) {
				dapp.setApp(AonApp.DOCUMENTAL);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp);
			} else if(Module.FISCAL.equals(r)) {
				dapp.setApp(AonApp.FISCAL);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp);
			} else if(Module.PAYROLL.equals(r)) {
				dapp.setApp(AonApp.PAYROLL);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp);
			}
			if(dapp.getApp() != null)
				json.put(dapp.getApp().name().toLowerCase(), dapp.getActive());

		});			
		return json;
	}
	
	private JSONObject setDomainApp(JSONObject json){
		// TODO ACTUALIZAR LA PARTE VIEJA!
		String domainName = json.getString("domain");
		String app = json.getString("app");
		Boolean active = json.getBoolean("active");
		AonApp aonApp = AonApp.safeValueOf(app);
		Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
		DomainApp domainApp = AON_SOLUTIONS.getDomainApp(domain.getName(), domain.getId(), "", f -> 
			f.getDomainProperty().eq(domain.getId()).and(f.getAppProperty().eq(aonApp.value()))).findFirst().orElse(null);
		if(domainApp == null) {
			domainApp = new DomainApp()
					.setDomain(domain.getId())
					.setApp(aonApp)
					.setActive(active);
			domainApp = AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", domainApp);
		} else {
			domainApp.setActive(active);
			domainApp = AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", domainApp);
		}
		
		return domainApp.toJSON();
	}
	
}
