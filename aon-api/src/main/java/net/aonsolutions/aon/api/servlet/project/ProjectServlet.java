package net.aonsolutions.aon.api.servlet.project;

import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_EXT_TASK_HOLDER;
import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_EXT_WORKGROUP;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.AppParamJSON;
import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.ProjectJSON;
import com.esferalia.aon.occam.api.json.ProjectTypeJSON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.ProjectProperties;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.registry.Project;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiProjectServlet", urlPatterns = {"/ms/api/project/*"})
public class ProjectServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ProjectServlet.class.getName());
	private static final String ROOT = "/";
	private static final String OFFICE = "/office";
	private static final String TYPE = "/type";
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[GET /ms/api/project] AON API PROJECT SERVLET");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case ROOT:
					response(req, resp, getProjects(api));
					break;
				case OFFICE:
					response(req, resp, getOfficeProjects(api));
					break;
				case TYPE:
					response(req, resp, getProjectTypes(api));
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
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[POST /ms/api/project] AON API PROJECT SERVLET");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case ROOT:
					response(req, resp, saveProject(api));
					break;
				case TYPE:
					response(req, resp, saveProjectType(api));
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
		LOGGER.info("[DELETE /ms/api/project] AON API PROJECT SERVLET");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case ROOT:
					response(req, resp, deleteProject(api));
					break;
				case TYPE:
					response(req, resp, deleteProjectType(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	private JSONArray getOfficeProjects(AonApiData api) {
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		JSONArray arr = new JSONArray();
		AON.getDomainOfficeLinked(api.getDomain(), api.getUser().getLogin()).stream().forEach(domain -> {
			Customer customer = AON.getCustomer(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(company.getDocument())));
			JSONObject json = new JSONObject();
			json.put(IJsonNames.DOMAIN, DomainJSON.toJSON(domain));
			JSONArray projects = new JSONArray();
			json.put(IJsonNames.REGISTRY, RegistryJSON.toJSON(customer));
			AON.getProjectStream(domain, "", f -> f.getRegistryProperty().eq(customer.getId()).and(f.getProjectTypeProperty().isNotNull()))
				.forEach(project -> {
					ProjectHolder holder = AON.getProjectHolder(project.getDomain(), "", f -> f.getProjectProperty().eq(project.getId()).and(f.getEndDateProperty().isNull()));
					project.setProjectHolder(holder);
					projects.put(ProjectJSON.toJSON(project));	
				});
			json.put("appParams", getAppParams(domain));
			json.put(IJsonNames.PROJECTS, projects);
			arr.put(json);
		});
		return arr;
	}

	private JSONArray getProjects(AonApiData api) {
		JSONArray arr = new JSONArray();
		AON.getProjectStream(api.getDomain(), "", f -> projectFilter(api, f))
		.forEach(project -> {
			ProjectHolder holder = AON.getProjectHolder(project.getDomain(), "", f -> f.getProjectProperty().eq(project.getId()).and(f.getEndDateProperty().isNull()));
			project.setProjectHolder(holder);
			arr.put(ProjectJSON.toJSON(project));	
		});
		return arr;
	}
	
	private JSONArray getAppParams(Domain domain) {
		String[] names = new String[] {APP_REQUESTS_EXT_WORKGROUP.name(), APP_REQUESTS_EXT_TASK_HOLDER.name()};
		return AppParamJSON.toJSON(
			AON.getApplicationParameterStream(domain.getName(), domain.getId(), "",
				f-> f.getDomainProperty().eq(domain.getId()).and(f.getNameProperty().in(names))
			)
		);
	}
	
    private Filter projectFilter(AonApiData api, ProjectProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getProjectTypeProperty().isNotNull());
		
		if(api.getData().opt(IJsonNames.REGISTRY) != null) {
			Integer registry = JsonUtils.getInteger(api.getData(), IJsonNames.REGISTRY);
			filter = filter.and(f.getRegistryProperty().eq(registry));
		}

		return filter;
    }
    
    private Object saveProject(AonApiData api) {
    	JSONArray projects = api.getData().optJSONArray(IJsonNames.PROJECTS);
    	if(projects!=null) {
    		 List<Project> list = ProjectJSON.fromJSON(projects);
    		 list.forEach(project->{
    			 AON.saveProject(api.getDomain(), api.getUser(), project);
    		 });
    		 return ProjectJSON.toJSON(list);
    	} else {
        	return ProjectJSON.toJSON(AON.saveProject(api.getDomain(), api.getUser(), 
        			ProjectJSON.fromJSON(api.getData())));
    	}
    }
    
    
    
    private JSONObject deleteProject(AonApiData api) {
    	AON.deleteProject(api.getDomain(), api.getUser(), JsonUtils.getInteger(api.getData(), IJsonNames.ID)); 
    	return new JSONObject();
    }
    
    // ---------- PROJECT TYPE
    
    private JSONArray getProjectTypes(AonApiData api) {
    	return ProjectTypeJSON.toJSON(
    			AON.getProjectTypeStream(api.getDomain(), api.getUser(), f -> 
    			f.getDomainProperty().eq(api.getDomain().getId())));
	}
    
    private JSONObject saveProjectType(AonApiData api) {
    	return ProjectTypeJSON.toJSON(
    		AON.saveProjectType(api.getDomain(), api.getUser(), 
    			ProjectTypeJSON.fromJSON(api.getData())));
    }
    
    private JSONObject deleteProjectType(AonApiData api) {
    	AON.deleteProjectType(api.getDomain(), api.getUser(), 
    			JsonUtils.getInteger(api.getData(), IJsonNames.ID)); 
    	return new JSONObject();
    }
}
