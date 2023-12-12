package net.aonsolutions.aon.api.servlet.warehouse;

import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.SalesDetailJSON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.SalesDetailProperties;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@WebServlet(name = "AonApiSalesDetailsServlet", urlPatterns = {"/ms/api/salesDetails/*"})
public class SalesDetailsServlet extends AonApiHttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER  = Logger.getLogger(DeliveriesServlet.class.getName());

	public static final String SALES_DETAILS = "/";
	public static final String SALES_DETAIL = "/:id";
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	private void get(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);

            Object object = new AonRouting(api)
                    .addRoute(SALES_DETAILS, SalesDetailsServlet::getSalesDetails)
                    .addRoute(SALES_DETAIL, SalesDetailsServlet::getSalesDetail)
                    .apply();

            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }
	
	private static JSONArray getSalesDetails(AonApiData api) {
		return SalesDetailJSON.toJSON(
			AON.getSalesDetailStream(api.getDomain().getName(), api.getDomain().getId(),
					api.getUser().getLogin(), f -> salesDetailFilter(api, f)));
	}
	
	private static JSONObject getSalesDetail(AonApiData api) {
		SalesDetail salesDetail = AON.getSalesDetailStream(api.getDomain().getName(), api.getDomain().getId(),
				api.getUser().getLogin(), f -> salesDetailFilter(api, f)).findFirst().orElse(new SalesDetail());
		return SalesDetailJSON.toJSON(salesDetail);
	}
	
	
	private static Filter salesDetailFilter(AonApiData api, SalesDetailProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
	
		Integer id = JsonUtils.getInteger(api.getData(), IJsonNames.ID);
		if(id != null) {
			filter = filter.and(f.getIdProperty().eq(id));
		}
		
		Integer delivery = JsonUtils.getInteger(api.getData(), IJsonNames.DELIVERY);
		if(delivery != null) {
			Byte[] statuses = new Byte[] {
					SalesDetailStatus.PENDING.value(),
					SalesDetailStatus.PARTIAL_SETTLED.value()
			};
			filter = filter.and(f.getDeliveryProperty().eq(delivery))
					.and(f.getStatusProperty().in(statuses));
		}
		
		return filter;
	}
}
