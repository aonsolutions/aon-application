package net.aonsolutions.aon.api.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.json.AmortizationTypeJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonAmortizationTypeServlet", urlPatterns = {"/ms/api/amortizationtype/*"})
public class AmortizationTypeServlet extends AonApiHttpServlet {

	private static final Logger LOGGER = Logger.getLogger(AmortizationTypeServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API AMORTIZATION TYPE SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getAmortizationTypes(api));
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
		LOGGER.info("AON API AMORTIZATION TYPE SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveAmortizationType(api));
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
		LOGGER.info("AON API AMORTIZATION TYPE SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteAmortizationType(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private JSONArray getAmortizationTypes(AonApiData api) throws Exception {
		JSONObject params = api.getData();
		AmortizationTypeParams amortizationTypeParams = new AmortizationTypeParams()
				.setDomain(api.getDomain().getId())
				.setDescription(params.optString(IJsonNames.DESCRIPTION, null));

		return AmortizationTypeJSON.toJSON(
				ACCOUNTING.getAmortizationTypeList(
						api.getDomain().getName(),
						api.getDomain().getId(),
						api.getUser().getLogin(),
						amortizationTypeParams));
	}

	private JSONObject saveAmortizationType(AonApiData api) throws Exception {
		JSONObject params = api.getData();

		AmortizationType amortizationType = AmortizationTypeJSON.fromJSON(params);
		amortizationType.setDomain(api.getDomain());

		ACCOUNTING.saveAmortizationType(
				api.getDomain().getName(),
				api.getDomain().getId(),
				api.getUser().getLogin(),
				amortizationType);

		return new JSONObject();
	}

	private JSONObject deleteAmortizationType(AonApiData api) throws Exception {
		JSONObject params = api.getData();
		Integer id = params.optInt(IJsonNames.ID);

		List<Integer> deleteIds = new ArrayList<>();
		deleteIds.add(id);

		ACCOUNTING.deleteAmortizationTypes(
				api.getDomain().getName(),
				api.getDomain().getId(),
				api.getUser().getLogin(),
				deleteIds);

		return new JSONObject();
	}
}
