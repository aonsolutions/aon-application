package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.AttachJSON;
import com.esferalia.aon.occam.api.json.CategoryJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.type.CategoryType;

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
		JSONObject params = api.getData();
		CategoryType type = CategoryType.safeValueOf(params.optString(IJsonNames.TYPE));
		
		return CategoryJSON.toJSON(
			AON_SOLUTIONS.getCategoryStream(api.getDomain(), api.getUser(), 
				f-> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getTypeProperty().eq(type.value()))
			)
		);
	}

	private JSONObject saveCategory(AonApiData api) {
		JSONObject params = api.getData();
		
		validateSave(params);
		
		Category category = CategoryJSON.fromJSON(params);
		
		Integer rattachId = saveAttach(api, category.getRattach());

		category.setRattach(rattachId);

		return CategoryJSON.toJSON(AON_SOLUTIONS.saveCategory(api.getDomain(), api.getUser(), category));
	}

	private JSONObject deleteCategory(AonApiData api) {
		Integer id = api.getData().optInt(IJsonNames.ID);
		AON_SOLUTIONS.deleteCategory(api.getDomain(), api.getUser(), id);
		return new JSONObject();
	}
	
	private void validateSave(JSONObject params) {
		if(params.isNull(IJsonNames.DESCRIPTION)) {
			throw new AonApiException("Descripci\u00f3n requerido");
		} else if(params.isNull(IJsonNames.NAME)) {
			throw new AonApiException("Nombre requerido");
		} else if(params.isNull(IJsonNames.TYPE)) {
			throw new AonApiException("Tipo requerido");
		}
	}
	
	
	private Integer saveAttach(AonApiData api, Integer rattach) {
		Domain domain = api.getDomain();
		String login  = api.getUser().getLogin();
		JSONObject params = api.getData();
		
		JSONObject rattachJson = params.optJSONObject(IJsonNames.ATTACH);
	
		if(params.isNull(IJsonNames.ATTACH) && rattach!=null) {
			rattach = null;
		} else if(rattachJson!= null && !rattachJson.isNull(IJsonNames.CONTENT)) {
			Attach attach = AttachJSON.fromJSON(rattachJson);
			if(attach!=null && !attach.isEmpty()) {
				Company company = AON.getCompany(domain.getName(), domain.getId(), login, f->f.getDomainProperty().eq(domain.getId()));
				if(company.getId()!=null) {
					attach.setAttachModule(company.getId());
					
					String description = attach.getDescription();
					if(description.length()>64) {
						description = description.substring(0,64);
					}
							
					attach.setDescription(description);
					
					rattach = AON.insertAttach(domain.getName(), domain.getId(), login, attach);
				}
			}
		}
		
		return rattach;
	}
}
