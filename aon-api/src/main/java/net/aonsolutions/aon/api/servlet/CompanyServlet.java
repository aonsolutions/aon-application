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
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Module;
import com.esferalia.aon.occam.api.model.aonsolutions.AonApp;
import com.esferalia.aon.occam.api.model.aonsolutions.AonDomainUserRoles;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainApp;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;

import net.aonsolutions.aon.api.json.AonAddressJSON;
import net.aonsolutions.aon.api.json.AonRegistryJSON;

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
			case "/one":
				response(req, resp, getCompany());
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
			case "/media":
				response(req, resp, getMedia());
				break;
			case "/address":
				response(req, resp, getMainAddress());
				break;	
			case "/banks":
				response(req, resp, getBanks());
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
	
	private JSONObject getCompany() {
		Company company = AON.getCompany(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
			f.getDomainProperty().eq(getDomain().getId()));
		RAddress ra = AON.getRAddressStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
			f.getRegistryProperty().eq(company.getId()).and(f.getTypeProperty().eq((byte)0))).findFirst().orElse(new RAddress());
		company.setMainAddress(ra);
		return AonRegistryJSON.toJSON(company);
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
	
	private JSONObject getMedia() {
		Integer registryId = getData().opt("company") != null 
			? getData().optInt("company") 
			: AON.getCompany(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
				f.getDomainProperty().eq(getDomain().getId())).getId();
		JSONObject json = new JSONObject();
		AON.getRMediaStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
			f.getDomainProperty().eq(getDomain().getId()).and(f.getRegistryProperty().eq(registryId)))
		.forEach(rm -> {
			json.put(rm.getMedia().name().toLowerCase(), rm.getValue());
		});
		return json;
	}
	
	private JSONObject getMainAddress() {
		Integer registryId = getData().opt("company") != null 
			? getData().optInt("company") 
			: AON.getCompany(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
					f.getDomainProperty().eq(getDomain().getId())).getId();
		RAddress ra = AON.getRAddressStream(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
			f.getRegistryProperty().eq(registryId).and(f.getTypeProperty().eq((byte)0))).findFirst().orElse(new RAddress());
		return AonAddressJSON.toJSON(ra);
	}
	
	private JSONArray oldModules(Domain domain) {
		JSONArray array = new JSONArray();
		AON.getDomainModules(domain.getName(), domain.getId(), "").forEach(r -> {
			DomainApp dapp = new DomainApp()
					.setDomain(domain.getId())
					.setActive(true);
			if(Module.ACCOUNTING.equals(r)) {
				dapp.setApp(AonApp.ACCOUNTING);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp, false);
			} else if(Module.AON_FINANCE.equals(r)) {
				dapp.setApp(AonApp.INVOICE);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp, false);
			} else if(Module.CALL_CENTER.equals(r)) {
				dapp.setApp(AonApp.MESSENGER);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp, false);
			} else if(Module.COMUNICA.equals(r)) {
				
			} else if(Module.DOCUMENT.equals(r)) {
				dapp.setApp(AonApp.DOCUMENTAL);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp, false);
			} else if(Module.FISCAL.equals(r)) {
				dapp.setApp(AonApp.FISCAL);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp, false);
			} else if(Module.PAYROLL.equals(r)) {
				dapp.setApp(AonApp.PAYROLL);
				AON_SOLUTIONS.saveDomainApp(domain.getName(), domain.getId(), "", dapp, false);
			}
			if(dapp.getApp() != null && dapp.getActive())
				array.put(dapp.getApp().name().toLowerCase());

		});			
		return array;
	}
	
	private JSONObject setDomainApp(){
		JSONArray array = getData().optJSONArray("apps");
		LinkedList<AonApp> apps = new LinkedList<>();
		for(int i = 0; i < array.length(); i++) {
			AonApp app = AonApp.safeValueOf(array.optString(i));
			if(app != null) {
				apps.add(AonApp.safeValueOf(array.optString(i)));
			}
		}
		LinkedList<DomainApp> activeDomainApps = new LinkedList<DomainApp>();
		
		for (AonApp aonApp : AonApp.values()) {
			DomainApp domainApp = AON_SOLUTIONS.getDomainApp(getDomain().getName(), getDomain().getId(), getUser().getLogin(), f -> 
			f.getDomainProperty().eq(getDomain().getId()).and(f.getAppProperty().eq(aonApp.value()))).findFirst().orElse(new DomainApp());
			if(apps.contains(aonApp)) {
				 activeDomainApps.add(domainApp
						 .setDomain(getDomain().getId())
						 .setApp(aonApp)
						 .setActive(true));
			} else if(!domainApp.isEmpty()) {
				AON_SOLUTIONS.saveDomainApp(getDomain().getName(), getDomain().getId(), getUser().getLogin(), domainApp.setActive(false), true);
			}
		}

		for (DomainApp domainApp : activeDomainApps) {
			AON_SOLUTIONS.saveDomainApp(getDomain().getName(), getDomain().getId(), getUser().getLogin(), domainApp, true);
		}

		return new JSONObject();
	}
	
	private JSONArray getBanks() {
		JSONArray array = new JSONArray();
		Domain domain = getDomain();
		String login = getUser().getLogin();
		Company company = AON.getCompany(domain.getName(),domain.getId(), login, f-> f.getDomainProperty().eq(getDomain().getId()));
		
		LinkedList<RegistryBank> registryBanks = AON.getRegistryBanks(domain.getName(), domain.getId(), login, company.getId());
		
		registryBanks.stream().forEach(rb->{
			JSONObject json = new JSONObject();
			json.put("id", rb.getId());
			json.put("bankAccount", rb.getBankAccount());
			json.put("bic", rb.getBic());
			json.put("suffix", rb.getSuffix());
			json.put("alias", rb.getAlias());
			json.put("account", rb.getAccount());
			json.put("accountCode", rb.getAccountCode());
			json.put("accountDescription", rb.getAccountDescription());
			array.put(json);
		});
		
		return array;
	}
	
}
