package net.aonsolutions.aon.api.servlet.project;

import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_EXT_TASK_HOLDER;
import static net.aonsolutions.aon.api.servlet.task.AppParamsRequest.APP_REQUESTS_EXT_WORKGROUP;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.ActivityTypeJSON;
import com.esferalia.aon.occam.api.json.AppParamJSON;
import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.ProjectHolderJSON;
import com.esferalia.aon.occam.api.json.ProjectJSON;
import com.esferalia.aon.occam.api.json.ProjectTypeJSON;
import com.esferalia.aon.occam.api.json.RegistryJSON;
import com.esferalia.aon.occam.api.model.ActivityType;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.ActivityTypeProperties;
import com.esferalia.aon.occam.api.model.Properties.ProjectHolderProperties;
import com.esferalia.aon.occam.api.model.Properties.ProjectProperties;
import com.esferalia.aon.occam.api.model.project.ProjectActivity;
import com.esferalia.aon.occam.api.model.project.ProjectHolder;
import com.esferalia.aon.occam.api.model.project.ProjectType;
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
	private static final String ACTIVITY = "/activity";
	private static final String ACTIVITY_TYPE = "/activity-type";
	
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
				.addRoute(ACTIVITY, ProjectsServlet::getActivitiesType)
				.addRoute(ACTIVITY_TYPE, ProjectsServlet::getActivitiesType)
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
				.addRoute(HOLDER, ProjectsServlet::saveProjectHolder)
				.addRoute(TYPE, ProjectsServlet::saveProjectType)
				.addRoute(ACTIVITY_TYPE, ProjectsServlet::saveActivityType)
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
				.addRoute(ACTIVITY_TYPE, ProjectsServlet::deleteActivityType)
				.apply();
			
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private static JSONArray getProjects(AonApiData api) {
	
		List<Project> projects = AON.getProjectStream(api.getDomain(), "", f -> projectFilter(api, f)).collect(Collectors.toList());

		setHoldersByProjects(api, projects);
		
		setActivityByProjects(api, projects);
		
		return ProjectJSON.toJSON(projects);
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
        	return ProjectJSON.toJSON(
        		AON.saveProject(api.getDomain(), api.getUser(), ProjectJSON.fromJSON(api.getData()))
        	);
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
    			f.getDomainProperty().eq(api.getDomain().getId()))
    	);
	}
    
    private static JSONObject saveProjectType(AonApiData api) {
    	ProjectType projectType = ProjectTypeJSON.fromJSON(api.getData());
    	return ProjectTypeJSON.toJSON(
    		AON.saveProjectType(api.getDomain(), api.getUser(), projectType)
    	);
    }
    
    private static JSONObject deleteProjectType(AonApiData api) {
    	AON.deleteProjectType(api.getDomain(), api.getUser(), JsonUtils.getInteger(api.getData(), IJsonNames.ID)); 
    	return new JSONObject();
    }
    
    
    // ---------- ACTIVITY TYPE
    private static JSONObject saveActivityType(AonApiData api) {
    	return ActivityTypeJSON.toJSON(AON.saveActivityType(api.getDomain(), api.getUser(), ActivityTypeJSON.fromJSON(api.getData())));
    }
 
    private static JSONObject deleteActivityType(AonApiData api) {
	  ActivityType type = ActivityTypeJSON.fromJSON(api.getData());
	  AON.deleteActivityType(api.getDomain(), api.getUser(), type.getId());
	  return new JSONObject();
    }
    
    private static Object saveProjectHolder(AonApiData api) {
        return saveProjectHolder(api, ProjectHolderJSON.fromJSON(api.getData()));
    }
    
    private static Object saveProjectHolder(AonApiData api, ProjectHolder holder) {
        return ProjectHolderJSON.toJSON(
        		AON.saveProjectHolder(api.getDomain(), api.getUser(), holder)
        );
    }
   
    private static Object deleteHolder(AonApiData api) {
        ProjectHolder holder = ProjectHolderJSON.fromJSON(api.getData());
        AON.deleteProjectHolder(api.getDomain(), api.getUser(), holder.getId());
        return new JSONObject();
    }
    
	private static JSONArray getHolders(AonApiData api) {
		return ProjectHolderJSON.toJSON(AON.getProjectHolderStream(api.getDomain(), api.getUser().getLogin(), 
				f ->projectHolderFilter(api, f) 
		));
	}
	
	private static JSONArray getActivitiesType(AonApiData api) {
		return ActivityTypeJSON.toJSON(
    	    	AON.getActivityTypeStream(api.getDomain(), api.getUser(), f-> activityTypeFilter(api, f))
    	);
	}
    
	private static void setHoldersByProjects(AonApiData api, List<Project>projects) {
		Timestamp ts = Timestamp.from(Instant.now());
		
	    Integer[] projectIds  = projects.stream().map(Project::getId).toArray(Integer[]::new);
	    
		if(projectIds!=null && projectIds.length>0) {
			List<ProjectHolder> holders = AON.getProjectHolderList(api.getDomain(), api.getUser(),  
					f-> f.getProjectProperty().in(projectIds)
					.and(f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(ts)))
			);
			
			projects.forEach(project->
				holders.stream()
				.filter(t-> t.getProject().equals(project.getId()))
				.forEach(project::addProjectHolder)
			);
		}
	}
	
	private static void setActivityByProjects(AonApiData api, List<Project>projects) {
	    Integer[] projectIds  = projects.stream().map(Project::getId).toArray(Integer[]::new);
	    
		if(projectIds!=null && projectIds.length>0) {
			
			List<ProjectActivity> activities = AON.getProjectActivityStream(api.getDomain(), api.getUser(),  
					f-> f.getProjectProperty().in(projectIds)
			).collect(Collectors.toList());
			
			projects.forEach(project->
				activities.stream()
				.filter(t-> t.getProject().equals(project.getId()))
				.forEach(project::addProjectActivity)
			);
		}
	}
	
    private static Filter projectFilter(AonApiData api, ProjectProperties f) {
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId())
				.and(f.getProjectTypeProperty().isNotNull());
		
		Integer registry    = api.getData().optInt(IJsonNames.REGISTRY);
		String  search      = api.getData().optString(IJsonNames.SEARCH);
		Integer projectType = api.getData().optInt(IJsonNames.PROJECT_TYPE);
		
		if(registry!=0) {
			filter = filter.and(f.getRegistryProperty().eq(registry));
		}
		
		if(projectType!=0) {
			filter = filter.and(f.getProjectTypeProperty().eq(projectType));
		}
		
		if(!search.isEmpty()) {

			Filter searchFilter = f.getNameProperty().like("%" + search + "%")
				.or(f.getAliasProperty().like("%" + search + "%"))
				.or(f.getRegistryNameProperty().like("%" + search + "%"))
				.or(f.getTypeDescriptionProperty().like("%" + search + "%"))
				;
			filter = filter.and(searchFilter);
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

   private static Filter activityTypeFilter(AonApiData api, ActivityTypeProperties f) {
  		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
  		JSONObject params = api.getData();

  		if(params.opt(IJsonNames.PROJECT_TYPE) != null) {
  			filter = filter.and(f.getProjectTypeProperty().eq(params.optInt(IJsonNames.PROJECT_TYPE)));
  		}
  		
  		return filter;
   }
}
