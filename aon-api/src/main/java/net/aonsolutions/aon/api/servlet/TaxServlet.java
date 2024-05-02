package net.aonsolutions.aon.api.servlet;
import java.util.LinkedList;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.product.Tax;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "TaxServlet", urlPatterns = {"/ms/api/tax/*"})
public class TaxServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(ExampleServlet.class.getName());
	
	public static final String TAX = "/";
	
	public static final String WITHHOLDING = "/withholding";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
				.addRoute(TAX, TaxServlet::getActionTax)
				.addRoute(WITHHOLDING, TaxServlet::getActionWithHolding)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getActionTax(AonApiData api) {
		return getTaxes(api, (byte) 1);
	}
	
	private static JSONArray getActionWithHolding(AonApiData api) {
		return getTaxes(api, (byte) 2);
	}
	
	private static JSONArray getTaxes(AonApiData api, byte typeTax) {
		JSONArray json = new JSONArray();
		LinkedList<Tax> taxes = AON.getTaxList(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), 
				f -> f.getTaxTypeProperty().eq(typeTax).and(f.getDomainProperty().eq(api.getDomain().getId()).or(f.getDomainProperty().eq(api.getDomain().getParentId()))));
		for(int i = 0; i < taxes.size(); i++) {
			if(typeTax == (byte) 1) json.put(taxToJson(taxes.get(i))); else json.put(withHoldingToJson(taxes.get(i)));
		}
		return json;
	}
	
	private static JSONObject taxToJson(Tax tax) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.ID, tax.getId());
		json.put(IJsonNames.PERCENTAGE, tax.getPercentage());
		json.put(IJsonNames.NAME, tax.getName());
		json.put(IJsonNames.SURCHARGE, tax.getSurcharge());
		return json;
	}
	
	private static JSONObject withHoldingToJson(Tax tax) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.ID, tax.getId());
		json.put(IJsonNames.PERCENTAGE, tax.getPercentage());
		json.put(IJsonNames.WITHHOLDING_TYPE, tax.getWithholdingType());
		json.put(IJsonNames.DESCRIPTION, tax.getWithholdingType().getDescription());
		json.put(IJsonNames.NAME, tax.getWithholdingType().getAbbreviatedDescription());
		json.put(IJsonNames.TITLE, tax.getName());
		return json;
	}
}
