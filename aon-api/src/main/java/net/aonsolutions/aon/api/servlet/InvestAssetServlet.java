package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;
import java.util.stream.Stream;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.InvestAssetJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.InvestAsset;
import com.esferalia.aon.occam.api.model.Properties.InvestAssetProperties;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiInvestAssetServlet", urlPatterns = {"/ms/api/invest/*"})
public class InvestAssetServlet extends AonApiHttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(InvestAssetServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVEST ASSET SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, getInvestAsset(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVEST ASSET SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveInvestAsset(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVEST ASSET SERVLET - PUT METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveInvestAsset(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVEST ASSET SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req);
		
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteInvestAsset(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getInvestAsset(AonApiData api) {
		Stream<InvestAsset> stream = AON.getInvestAssetStream(api.getDomain(), api.getUser(), f -> 
				investAssetFilter(api, f));
		return InvestAssetJSON.toJSON(stream);
	}
	
	private JSONObject saveInvestAsset(AonApiData api) {
		InvestAsset investAsset = InvestAssetJSON.fromJSON(api.getData());
		investAsset = AON.saveInvestAsset(api.getDomain(), api.getUser(), investAsset);
		return InvestAssetJSON.toJSON(investAsset);
	}
	
	private JSONObject deleteInvestAsset(AonApiData api) {
		Integer id = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		AON.deleteInvestAsset(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}
	
	private Filter investAssetFilter(AonApiData api, InvestAssetProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());

		if(!AonStringUtils.isBlank(api.getData().optString(IJsonNames.VALUE))) {
			String value = api.getData().optString(IJsonNames.VALUE);
			Filter valueFilter = f.getDescriptionProperty().like("%" + value + "%");
			filter = filter.and(valueFilter);
		}
		
		return filter;
	}
}
