package net.aonsolutions.aon.api.servlet;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.WorkplaceJSON;
import com.esferalia.aon.occam.api.model.Workplace;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonWorkplaceServlet", urlPatterns = {"/ms/api/workplace/*"})
public class WorkplaceServlet extends AonApiHttpServlet{
	
	private static final Logger LOGGER  = Logger.getLogger(WorkplaceServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		get(req, resp);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
		put(req, resp);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		delete(req, resp);
	}
	
	private void get(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getWorkplaces(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private void put(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("AON API LOCATION SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/":
					response(req, resp, saveWorkplace(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("AON API LOCATION SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/":
					response(req, resp, deleteWorkplace(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private Object getWorkplaces(AonApiData api) {
		List<Workplace> workplaces = AON.getWorkplaceList(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f -> 
				f.getDomainProperty().eq(api.getDomain().getId()));
		return WorkplaceJSON.toJSON(workplaces);
	}
	
	private JSONObject saveWorkplace(AonApiData api) {
		Workplace workplace = WorkplaceJSON.fromJSON(api.getData());
		workplace = AON.saveWorkplace(api.getDomain(), api.getUser().getLogin(), workplace);
		return WorkplaceJSON.toJSON(workplace);
	}

	private JSONObject deleteWorkplace(AonApiData api) {
		// TODO delete workplace
		return new JSONObject();
	}
	
}
