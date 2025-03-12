package net.aonsolutions.aon.api.servlet;

import java.util.Optional;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.S3CategoryProperties;
import com.esferalia.aon.occam.api.model.S3Category;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "S3CategoryServlet", urlPatterns = {"/ms/api/s3category/*"})
public class S3CategoryServlet extends AonApiHttpServlet {
	
	private static final Logger LOGGER  = Logger.getLogger(S3CategoryServlet.class.getName());
	
	public static final String CATEGORY = "/";
	public static final String CATEGORY_COUNT = "/count";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		post(req, resp);
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
			switch(api.getPath()) {
			case CATEGORY:
				response(req, resp, getAction(api));
				break;
			case CATEGORY_COUNT:
				response(req, resp, count(api));
				break;
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			Object object = new AonRouting(api)
				.addRoute(CATEGORY, S3CategoryServlet::putAction)
				.apply();
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void post(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			Object object = new AonRouting(api)
				.addRoute(CATEGORY, S3CategoryServlet::postAction)
				.apply();
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			Object object = new AonRouting(api)
				.addRoute(CATEGORY, S3CategoryServlet::deleteAction)
				.apply();
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static Object getAction(AonApiData api) {
		if(api.getData().has(IJsonNames.ID)) {
			return getOne(api);			
		} else {
			return getList(api);	
		}
	}
	
	private static JSONObject getOne(AonApiData api) {
		JSONArray jsArray = new JSONArray();
		AON_SOLUTIONS.getS3CategoryStream(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(api.getData().getInt(IJsonNames.ID)), null, null).forEach(category -> {
					jsArray.put(s3CategoryToJson(category));
				});
		return jsArray.length() > 0 ? jsArray.getJSONObject(0) : new JSONObject();
	}
	
	private static JSONArray getList(AonApiData api) {
		JSONArray jsArray = new JSONArray();
		Optional<Integer> page = Optional.ofNullable(JsonUtils.getInteger(api.getData(), IJsonNames.PAGE));
		Optional<Integer> perPage = Optional.ofNullable(JsonUtils.getInteger(api.getData(), IJsonNames.PER_PAGE));
		AON_SOLUTIONS.getS3CategoryStream(api.getDomain(), api.getUser(), f -> generateFilters(api, f), page, perPage).forEach(category -> {
			jsArray.put(s3CategoryToJson(category));
		});
		return jsArray;
	}
	
	private static JSONObject count(AonApiData api) {
		return new JSONObject().put(IJsonNames.COUNT, AON_SOLUTIONS.countS3Category(api.getDomain(), api.getUser(), f -> generateFilters(api, f)));
	}
	
	private static JSONObject postAction(AonApiData api) {
		S3Category category = new S3Category()
				.setDescription(api.getData().getString(IJsonNames.NAME))
				.setName(api.getData().getString(IJsonNames.NAME))
				.setDomain(api.getDomain().getId())
				.setIsDeletable((byte) 1)
				.setIsVisible((byte) 1)
				.setParent(null)
				.setScope(api.getData().has(IJsonNames.SCOPE) ? api.getData().getInt(IJsonNames.SCOPE) : null);
		category = AON_SOLUTIONS.insertS3Category(api.getDomain(), api.getUser(), category);
		return s3CategoryToJson(category);
	}
	
	private static JSONObject putAction(AonApiData api) {
		S3Category category = new S3Category()
				.setId(api.getData().getInt(IJsonNames.ID))
				.setDescription(api.getData().getString(IJsonNames.NAME))
				.setName(api.getData().getString(IJsonNames.NAME))
				.setIsVisible(api.getData().has("is_visible") ? (byte) api.getData().getInt("is_visible") : 1)
				.setParent(null)
				.setScope(api.getData().getInt(IJsonNames.SCOPE));
		category = AON_SOLUTIONS.updateS3Category(api.getDomain(), api.getUser(), category);
		return s3CategoryToJson(category);
	}
	
	private static JSONObject deleteAction(AonApiData api) {
		AON_SOLUTIONS.deleteS3Category(api.getDomain(), api.getUser(), f -> f.getIdProperty().eq(api.getData().getInt(IJsonNames.ID)));
		return new JSONObject();
	}
	
	private static Filter generateFilters(AonApiData api, S3CategoryProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId()).or(f.getDomainProperty().eq(api.getDomain().getParentId()));
		JSONObject json = api.getData();
		if(json.has(IJsonNames.NAME))
			filter = filter.and(f.getNameProperty().eq("%" + json.getString(IJsonNames.NAME) + "%"));
		if(json.has(IJsonNames.DESCRIPTION))
			filter = filter.and(f.getDescriptionProperty().eq("%" + json.getString(IJsonNames.DESCRIPTION) + "%"));
		if(json.has(IJsonNames.PARENT)) {
			if(json.getString(IJsonNames.PARENT).equals("null")) {
				filter = filter.and(f.getParentProperty().isNull());
			} else {
				filter = filter.and(f.getParentProperty().eq(json.getInt(IJsonNames.PARENT)));
			}
		}
		return filter;
	}
	
	private static JSONObject s3CategoryToJson(S3Category category) {
		JSONObject json = new JSONObject();
		json.put(IJsonNames.ID, category.getId());
		json.put(IJsonNames.DOMAIN, category.getDomain());
		json.put(IJsonNames.DESCRIPTION, category.getDescription());
		json.put(IJsonNames.NAME, category.getName());
		json.put(IJsonNames.PARENT, category.getParent());
		json.put(IJsonNames.SCOPE, category.getScope());
		json.put("is_deletable", category.getIsDeletable());
		json.put("is_visible", category.getIsVisible());
		return json;
	}
}
