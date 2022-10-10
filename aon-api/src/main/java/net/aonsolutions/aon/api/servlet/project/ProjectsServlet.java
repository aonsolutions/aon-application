package net.aonsolutions.aon.api.servlet.project;

import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_EXT_TASK_HOLDER;
import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_EXT_WORKGROUP;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.AppParamJSON;
import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.ProjectHolderJSON;
import com.esferalia.aon.occam.api.json.ProjectJSON;
import com.esferalia.aon.occam.api.json.ProjectTypeJSON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.ProjectHolderProperties;
import com.esferalia.aon.occam.api.model.Properties.ProjectProperties;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.registry.Project;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.api.servlet.AonRouting;

@SuppressWarnings("serial")
@WebServlet(name = "AonApiProjectServlet", urlPatterns = {"/ms/api/projects/*"})
public class ProjectsServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ProjectsServlet.class.getName());
	private static final String ROOT = "/";
	private static final String OFFICE = "/office";
	private static final String TYPE = "/type";
	private static final String HOLDERS = "/:id/holders";
	private static final String HOLDER = "/holder";
	
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
			
			Object object = new AonRouting(api)
				.addRoute(ROOT, ProjectsServlet::getProjects)
				.addRoute(OFFICE, ProjectsServlet::getOfficeProjects)
				.addRoute(TYPE, ProjectsServlet::getProjectTypes)
				.addRoute(HOLDERS, ProjectsServlet::getHolders)
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
				.addRoute(ROOT, ProjectsServlet::saveProject)
				.addRoute(HOLDER, ProjectsServlet::saveHolder)
				.addRoute(TYPE, ProjectsServlet::saveProjectType)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private void put(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			
			Object object = new AonRouting(api)
//				.addRoute(HOLDER, ProjectsServlet::)
//				.addRoute(CUSTOMER, ProjectsServlet::saveCustomer)
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
				.addRoute(ROOT, ProjectsServlet::deleteProject)
				.addRoute(TYPE, ProjectsServlet::deleteProjectType)
				.addRoute(HOLDER, ProjectsServlet::deleteHolder)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getProjects(AonApiData api) {
		JSONArray arr = new JSONArray();
		AON.getProjectStream(api.getDomain(), "", f -> projectFilter(api, f))
		.forEach(project -> {
			ProjectHolder holder = AON.getProjectHolder(project.getDomain(), "", 
				f -> f.getProjectProperty().eq(project.getId()).and(f.getEndDateProperty().isNull())
			);
			project.setProjectHolder(holder);
			arr.put(ProjectJSON.toJSON(project));	
		});
		return arr;
	}
	
	private static JSONArray getOfficeProjects(AonApiData api) {
		Company company = AON.getCompanyForDomain(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin());
		JSONArray arr = new JSONArray();
		AON.getDomainOfficeLinked(api.getDomain(), api.getUser().getLogin())
		.stream()
		.forEach(domain -> {
			Customer customer = AON.getCustomer(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()).and(f.getDocumentProperty().eq(company.getDocument())));
			
			JSONObject json = new JSONObject();
			JSONArray projects = new JSONArray();
			
			json.put(IJsonNames.DOMAIN, DomainJSON.toJSON(domain));	
			json.put(IJsonNames.REGISTRY, RegistryJSON.toJSON(customer));
			json.put("appParams", getAppParams(domain));
			
			AON.getProjectStream(domain, "", f -> f.getRegistryProperty().eq(customer.getId()).and(f.getProjectTypeProperty().isNotNull()))
			.forEach(project -> {
				ProjectHolder holder = AON.getProjectHolder(project.getDomain(), "", f -> f.getProjectProperty().eq(project.getId()).and(f.getEndDateProperty().isNull()));
				project.setProjectHolder(holder);
				projects.put(ProjectJSON.toJSON(project));	
			});
			
			json.put(IJsonNames.PROJECTS, projects);
			arr.put(json);
		});
		return arr;
	}

	private static JSONArray getAppParams(Domain domain) {
		String[] names = new String[] {APP_REQUESTS_EXT_WORKGROUP.name(), APP_REQUESTS_EXT_TASK_HOLDER.name()};
		return AppParamJSON.toJSON(
			AON.getApplicationParameterStream(domain.getName(), domain.getId(), "",
				f-> f.getDomainProperty().eq(domain.getId()).and(f.getNameProperty().in(names))
			)
		);
	}

    private static Object saveProject(AonApiData api) {
    	if(api.getData().opt(IJsonNames.PROJECTS)!=null) {
    		return saveProjects(api);
    	} else {
        	return ProjectJSON.toJSON(AON.saveProject(api.getDomain(), api.getUser(), 
        			ProjectJSON.fromJSON(api.getData())));
    	}
    }
    
    private static JSONArray saveProjects(AonApiData api) {
    	JSONArray projects = api.getData().optJSONArray(IJsonNames.PROJECTS);
		 List<Project> list = ProjectJSON.fromJSON(projects);
		 list.forEach(project->{
			 AON.saveProject(api.getDomain(), api.getUser(), project);
		 });
		 return ProjectJSON.toJSON(list);
    }
    
    private static JSONObject deleteProject(AonApiData api) {
    	AON.deleteProject(api.getDomain(), api.getUser(), JsonUtils.getInteger(api.getData(), IJsonNames.ID)); 
    	return new JSONObject();
    }
    
    // ---------- PROJECT TYPE
    
    private static JSONArray getProjectTypes(AonApiData api) {
    	return ProjectTypeJSON.toJSON(
    			AON.getProjectTypeStream(api.getDomain(), api.getUser(), f -> 
    			f.getDomainProperty().eq(api.getDomain().getId())));
	}
    
    private static JSONObject saveProjectType(AonApiData api) {
    	return ProjectTypeJSON.toJSON(
    		AON.saveProjectType(api.getDomain(), api.getUser(), 
    			ProjectTypeJSON.fromJSON(api.getData())));
    }
    
    private static JSONObject deleteProjectType(AonApiData api) {
    	AON.deleteProjectType(api.getDomain(), api.getUser(), 
    			JsonUtils.getInteger(api.getData(), IJsonNames.ID)); 
    	return new JSONObject();
    }
    
    private static Object saveHolder(AonApiData api) {
        return ProjectHolderJSON.toJSON(
        		AON.saveProjectHolder(api.getDomain(), api.getUser(), ProjectHolderJSON.fromJSON(api.getData()))
        );
    }
   
    private static Object deleteHolder(AonApiData api) {
        ProjectHolder holder = ProjectHolderJSON.fromJSON(api.getData());
        AON.deleteProjectHolder(api.getDomain(), api.getUser(), holder.getId());
        return new JSONObject();
    }
	private static JSONArray getHolders(AonApiData api) {
	
		Stream<ProjectHolder> holders = AON.getProjectHolderStream(api.getDomain(), api.getUser().getLogin(), 
			f ->projectHolderFilter(api, f) 
		);
	
		return ProjectHolderJSON.toJSON(holders);
	}
	
	
    private static Filter projectFilter(AonApiData api, ProjectProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getProjectTypeProperty().isNotNull());
		
		if(api.getData().opt(IJsonNames.REGISTRY) != null) {
			Integer registry = JsonUtils.getInteger(api.getData(), IJsonNames.REGISTRY);
			filter = filter.and(f.getRegistryProperty().eq(registry));
		}

		return filter;
    }
    
    private static Filter projectHolderFilter(AonApiData api, ProjectHolderProperties f) {
  		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
  		JSONObject params = api.getData();
  		boolean active = params.optBoolean(IJsonNames.ACTIVE);
  		Timestamp ts = Timestamp.from(Instant.now());

  
  		if(params.opt(IJsonNames.PROJECT) != null) {
  			Integer project = params.optInt(IJsonNames.PROJECT);
  			filter = filter.and(f.getProjectProperty().eq(project));
  		}
  		
		if(active) {
			filter = filter.and(f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(ts)));
		} else {
			filter = filter.and(f.getEndDateProperty().isNotNull().and(f.getEndDateProperty().le(ts)));
		}

  		return filter;
   }
}
