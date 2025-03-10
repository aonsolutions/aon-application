package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.json.AccountJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountProperties;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.ewok.IConstants;

@SuppressWarnings("serial")
@WebServlet(name = "AccountsServlet", urlPatterns = {"/ms/api/accounts/*"})
public class AccountsServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(AccountsServlet.class.getName());
	
	public static final String ACCOUNTS = "/";
	public static final String ACCOUNT = "/:id";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
		
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(ACCOUNTS, AccountsServlet::getAccounts)
				.addRoute(ACCOUNT, AccountsServlet::getAccount)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getAccounts(AonApiData api) {
		JSONArray array = new JSONArray();
		ACCOUNTING.getAccounts(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> accountFilter(f, api.getDomain(), api.getData()))
		.forEach(acc -> array.put(AccountJSON.toJSON(acc)));
		return array;
	}
	
	private static JSONObject getAccount(AonApiData api) {
		JSONObject pathJSON = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
		Integer accountId = JsonUtils.getInteger(pathJSON, IJsonNames.ID);
		
		Account acc = ACCOUNTING.getAccount(api.getDomain().getName(), api.getDomain().getId(),api.getUser().getLogin(), accountId);
		return AccountJSON.toJSON(acc); 
	}
	
	public static Filter accountFilter(AccountProperties f, Domain domain, JSONObject json) {
		Integer[] domains = { domain.getId(), domain.getParentId() };
		Filter filter =  f.getDomainProperty().in(domains);
		
		String code = JsonUtils.getString(json, IJsonNames.CODE);
		
    	if(code!=null)
    		filter = filter.and(f.getCodeProperty().like(code+"%")); 
    	
		return filter;
    }
	
	
	
}
