package net.aonsolutions.aon.api.servlet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.AppParamJSON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "ApplicationParameterServlet", urlPatterns = {"/ms/api/application-parameter/*"})
public class ApplicationParameterServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ApplicationParameterServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON APLICATION-PARAMETER SERVLET GET");
		try {		
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("AON APLICATION-PARAMETER - POST");
		try {		
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/":
					response(req, resp, saveApplicationParameter(api));
				break;
				case "/all":
					response(req, resp, getApplicationParameters(api));
				break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON APLICATION-PARAMETER - DELETE METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteApplicationParameter(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray saveApplicationParameter(AonApiData api) {
		JSONArray arr = new JSONArray();
		Domain domain = api.getDomain();
		String login = api.getUser().getLogin();
		
		LinkedList<ApplicationParameter> appParams = AppParamJSON.fromJSON(api.getData().optJSONArray(IJsonNames.PARAMS));
		for (ApplicationParameter param : appParams) {

			 ApplicationParameter exists = AON.getApplicationParameter(domain.getName(), domain.getId(), login, param.getName());
			 if(exists.getId()!=null) {
				 if(param.getValue()!=null) {
					 
					 exists.setValue(param.getValue());
					 System.out.println("--------UPDATE APP PARAMS "+param.getName()+"-------------");

					 AON.updateApplicationParameter(domain.getName(), domain.getId(), api.getUser().getLogin(), exists, 
								f->f.getDomainProperty().eq(exists.getDomain()).and(f.getNameProperty().eq(exists.getName()))
					);
				 } else {
					 System.out.println("--------DELETE APP PARAMS "+param.getName()+"-------------");

					 AON.deleteApplicationParameter(domain.getName(), domain.getId(), api.getUser().getLogin(),
							 f-> f.getDomainProperty().eq(domain.getId()).and(f.getNameProperty().eq(param.getName()))
					 );
				 }
			 } else if(param.getValue()!=null) {
				 System.out.println("--------SAVE APP PARAMS "+param.getName()+"-------------");

				 AON.insertApplicationParameter(domain.getName(), domain.getId(), api.getUser().getLogin(), param);
			 }
		}
		return arr;
	}
	
	private JSONArray getApplicationParameters(AonApiData api) {
		JSONObject params = api.getData();
		JSONArray jsonArray = params.optJSONArray(IJsonNames.PARAMS);

		if(jsonArray!=null && !jsonArray.isEmpty()) {
			 List<String> listNames = new ArrayList<>();
			 
			 for (int i = 0; i < jsonArray.length(); i++) {
				 listNames.add(jsonArray.optString(i));
			 }
			 
			 List<ApplicationParameter> appParams = getAppParamsList(api, listNames);
		     if(!appParams.isEmpty()) {
		    	return AppParamJSON.toJSON(appParams);
		     }
		}

		return new JSONArray();
	}
	
	private JSONObject deleteApplicationParameter(AonApiData api) {
		Domain domain = api.getDomain();
		Integer id = api.getData().getInt(IJsonNames.ID);
		
		AON.deleteApplicationParameter(domain.getName(), domain.getId(), api.getUser().getLogin(),
				f -> f.getDomainProperty().eq(domain.getId()).and(f.getIdProperty().eq(id)));

		return new JSONObject();
	}
	
	private List<ApplicationParameter> getAppParamsList(AonApiData api, List<String> listNames) {
		Domain domain = api.getDomain();
		JSONObject params = api.getData();
		boolean parent = params.optBoolean(IJsonNames.PARENT);
		List<ApplicationParameter> list = new ArrayList<>();
		if(!listNames.isEmpty()) {
		    String[] names = listNames.toArray(String[]::new);
			
			AON.getApplicationParameterStream(
				api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),
				f-> f.getDomainProperty().eq(parent ? domain.getParentId() : domain.getId()).and(f.getNameProperty().in(names))
			)
			.forEach(list::add);
		}
		return list;
	}
}
