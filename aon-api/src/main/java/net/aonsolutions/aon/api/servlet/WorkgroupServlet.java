package net.aonsolutions.aon.api.servlet;

import java.util.logging.Logger;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.ProjectHolderJSON;
import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.json.WorkgroupJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.WorkgroupProperties;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.type.WorkgroupStatus;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonWorkgroupServlet", urlPatterns = {"/ms/api/workgroup/*"})
public class WorkgroupServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(WorkgroupServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API LOCATION SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/":
					response(req, resp, getWorkgroups(api));
					break;
				case "/projectsHolder":
					response(req, resp, getWorkgroupProjectHolders(api));
					break;
				case "/task":
					response(req, resp, getWorkgroupTasks(api));
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
		LOGGER.info("AON API LOCATION SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/":
					response(req, resp, saveWorkgroup(api));
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
		LOGGER.info("AON API LOCATION SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/":
					response(req, resp, deleteWorkgroup(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONArray getWorkgroupTasks(AonApiData api) {
		Domain domain = api.getDomain();
		Integer workgroup = api.getData().optInt(IJsonNames.WORKGROUP);
		
		return TaskJSON.toJSON(AON_SOLUTIONS.getTaskStream(domain, api.getUser(), f -> f.getDomainProperty().eq(domain.getId()).and(f.getWorkgroupProperty().eq(workgroup))));
	}
	
	private JSONArray getWorkgroupProjectHolders(AonApiData api) {
		Domain domain = api.getDomain();
		Integer workgroup = api.getData().optInt(IJsonNames.WORKGROUP);
		
		return ProjectHolderJSON.toJSON(AON.getProjectHolderStream(api.getDomain(), api.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(domain.getId()).and(f.getWorkgroupProperty().eq(workgroup)) 
		));
	}

	private JSONArray getWorkgroups(AonApiData api) {
		Domain domain = api.getDomain();
		Integer taskHolder = api.getData().optInt(IJsonNames.TASK_HOLDER);
		
		Stream<Workgroup> workgroupStream = taskHolder > 0 
				? 
					AON.getWorkgroupByTaskHolderStream(domain.getName(), domain.getId(), api.getUser().getLogin(), f->workgroupFilter(api,f), taskHolder) 
				: 
					AON.getWorkgroupStream(domain.getName(), domain.getId(), api.getUser().getLogin(), f->workgroupFilter(api,f) );
					
		return WorkgroupJSON.toJSON(workgroupStream);
	}
	
	private JSONObject saveWorkgroup(AonApiData api) {
		Domain domain = api.getDomain();
		Workgroup workgroup = WorkgroupJSON.fromJSON(api.getData());
		workgroup = AON.saveWorkgroup(domain.getName(), domain.getId(), api.getUser().getLogin(), workgroup);
		return WorkgroupJSON.toJSON(workgroup);
	}

	private JSONObject deleteWorkgroup(AonApiData api) {
		Domain domain = api.getDomain();
		AON.deleteWorkgroup(domain.getName(), domain.getId(), api.getUser().getLogin(), 
				api.getData().optInt(IJsonNames.ID));
		return new JSONObject();
	}
	
	private Filter workgroupFilter(AonApiData api, WorkgroupProperties f) {
		Domain domain = api.getDomain();
		String status  = api.getData().optString(IJsonNames.STATUS);
		Filter filter = f.getDomainProperty().eq(domain.getId());
		if(!status.isEmpty()) 
			filter = filter.and( f.getStatusProperty().eq( WorkgroupStatus.safeValueOf(status).value() ) );
		
		return filter;
	}
	
}
