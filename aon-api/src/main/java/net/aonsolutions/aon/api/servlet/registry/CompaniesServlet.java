package net.aonsolutions.aon.api.servlet.registry;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.RegistryBankJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.type.MediaType;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;
import net.aonsolutions.aon.api.servlet.CompanyServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiCompaniesServlet", urlPatterns = {"/ms/api/companies/*"})
public class CompaniesServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(CompaniesServlet.class.getName());
	
	public static final String COMPANIES = "/";
	public static final String COMPANY = "/:id";
	public static final String COMPANY_EMAILS = "/:id/emails";
	public static final String COMPANY_BANKS = "/:id/banks";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(COMPANIES, CompanyServlet::getCompanies)
				.addRoute(COMPANY, CompanyServlet::getCompany)
				.addRoute(COMPANY_EMAILS, CompaniesServlet::getCompanyEmails)
				.addRoute(COMPANY_BANKS, CompaniesServlet::getCompanyBanks)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(COMPANIES, CompaniesServlet::saveCompany)
				.addRoute(COMPANY, CompaniesServlet::saveCompany)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getCompanies(AonApiData api) {
		// TODO
		return new JSONArray();
	}
	
	private static JSONObject getCompany(AonApiData api) {
		// TODO
		return new JSONObject();
	}
	
	private static JSONArray getCompanyEmails(AonApiData api) {
		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
		Integer companyId = JsonUtils.getInteger(vars, IJsonNames.ID);
		ArrayList<String> list = AON.getRegistryMediaStream(api.getDomain(), api.getUser(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId())
			.and(f.getRegistryProperty().eq(companyId))
			.and(f.getMediaProperty().eq(MediaType.EMAIL.value())))
		.map(r -> r.getValue()).collect(Collectors.toCollection(ArrayList::new));
		return new JSONArray(list);
	}
	
	private static JSONArray getCompanyBanks(AonApiData api) {
		JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
		Integer companyId = JsonUtils.getInteger(vars, IJsonNames.ID);
		return RegistryBankJSON.toJSON(AON.getRegistryBankStream(api.getDomain(), api.getUser().getLogin(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId())
			.and(f.getRegistryProperty().eq(companyId))));
	}	

	public static JSONObject saveCompany(AonApiData api) {
		//	TODO
		return new JSONObject();
	}
	
}
