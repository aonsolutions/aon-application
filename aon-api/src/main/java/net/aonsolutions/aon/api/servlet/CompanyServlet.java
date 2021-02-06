package net.aonsolutions.aon.api.servlet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonDomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;

@SuppressWarnings("serial")
@WebServlet(name = "AonCompanyServlet", urlPatterns = {"/ms/api/company/*"})
public class CompanyServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CompanyServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API COMPANY SERVLET - GET METHOD");
		try {
			super.doGet(req, resp);

			switch (getPath()) {
			case "/":
				response(req, resp, getCompanies());
				break;
			case "/app":
				response(req, resp, getDomainApps());
				break;
			case "/approles":
				response(req, resp, getDomainUserRoles());
				break;
			case "/notice":
				response(req, resp, getNotices());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API COMPANY SERVLET - POST METHOD");
		try {
			super.doPost(req, resp);

			switch (getPath()) {
			case "/app":
				response(req, resp, setDomainApp());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
		
	private JSONArray getCompanies() {
		List<String> schemas = AONContext.getSchemas();
		JSONArray jsArray = new JSONArray();
		for(String schema : schemas) {
			AON_SOLUTIONS.getCompanyStream(getToken(), schema, null, null)
				.sorted((o1, o2) -> o1.getCompany().getName().compareTo(o2.getCompany().getName())).forEach(
						ac -> jsArray.put(ac.toJSON()));
		}
		return jsArray;
	}
	
	private JSONObject getDomainUserRoles() {
		DomainUserRoles dur = SECURITY.getDomainUserRoles(getDomain(), getUser().getLogin(), getUser().getId());
		AonDomainUserRoles adur = new AonDomainUserRoles(dur);
		return adur.toJSON();
	}
	
	private JSONArray getDomainApps(){
		JSONArray array = new JSONArray();
		LinkedList<AonApp> list = new LinkedList<>();
		DomainUserRoles dur = SECURITY.getDomainUserRoles(getDomain(), getUser().getLogin(), getUser().getId());
		dur.getParentDomainApps().stream().forEach(app -> list.add(app));
		dur.getDomainApps().stream().forEach(app -> {
			if(!list.contains(app)) list.add(app);
		});
		
		list.stream().forEach(app -> {	
			array.put(app.name().toLowerCase());
		});
		if(array.isEmpty()) {
			return oldModules(getDomain());
		}
		return array;
	}
	
	private JSONObject getNotices() {
		return AON.getRawdocUserData(getDomain().getName(), getDomain().getId(), getUser().getLogin()).toJSON();
	}
	
	private JSONArray oldModules(Domain domain) {
		JSONArray array = new JSONArray();
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
			} else if(Module.COMUNICA.equals(r)) {
				
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
			if(dapp.getApp() != null && dapp.getActive())
				array.put(dapp.getApp().name().toLowerCase());

		});			
		return array;
	}
	
	private JSONObject setDomainApp(){
		// TODO ACTUALIZAR LA PARTE VIEJA!
		String domainName = getData().getString("domain");
		String app = getData().getString("app");
		Boolean active = getData().getBoolean("active");
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
