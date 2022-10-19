package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.PayMethodJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonPaymethodServlet", urlPatterns = {"/ms/api/paymethods/*"})
public class PaymethodServlet extends AonApiHttpServlet {
	
	private static final Logger LOGGER  = Logger.getLogger(PaymethodServlet.class.getName());

	public static final String PAYMETHODS = "/";
    public static final String PAYMETHOD = "/:id";
	
    @Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
        get(req, resp);
	}

    private void get(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);

            Object object = new AonRouting(api)
                    .addRoute(PAYMETHODS, PaymethodServlet::getPaymethods)
                    .addRoute(PAYMETHOD, PaymethodServlet::getPaymethod)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }
	
	private static JSONArray getPaymethods(AonApiData api) {
		return PayMethodJSON.toJSON(AON.getPayMethods(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin()));
	}
	
    private static JSONObject getPaymethod(AonApiData api) {
        JSONObject vars = JsonUtils.getJSONObject(api.getData(), IJsonNames.VARIABLES);
        Integer paymethodId = JsonUtils.getInteger(vars, IJsonNames.ID);
        return PayMethodJSON.toJSON(AON.getPayMethod(api.getDomain().getName(),
                api.getDomain().getId(), api.getUser().getLogin(), f ->
            f.getIdProperty().eq(paymethodId)));        
    }
}
