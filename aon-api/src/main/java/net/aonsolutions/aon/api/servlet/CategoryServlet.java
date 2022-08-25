package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.CategoryJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.Category;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonCategoryServlet", urlPatterns = {"/ms/api/category/*"})
public class CategoryServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(CategoryServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API CATEGORY SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getCategorys(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("AON API CATEGORY SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, saveCategory(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("AON API CATEGORY SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteCategory(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	private JSONArray getCategorys(AonApiData api) {
		return CategoryJSON.toJSON(
				AON_SOLUTIONS.getCategoryStream(api.getDomain(), api.getUser(), f-> f.getDomainProperty().eq(api.getDomain().getId()))
		);
	}

	
	private JSONObject saveCategory(AonApiData api) {
		Category category = CategoryJSON.fromJSON(api.getData());
		return CategoryJSON.toJSON(AON_SOLUTIONS.saveCategory(api.getDomain(), api.getUser(), category));
	}

	private JSONObject deleteCategory(AonApiData api) {
		Integer id = api.getData().optInt(IJsonNames.ID);
		AON_SOLUTIONS.deleteCategory(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}
	
}
