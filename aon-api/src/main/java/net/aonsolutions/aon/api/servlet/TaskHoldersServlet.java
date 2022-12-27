package net.aonsolutions.aon.api.servlet;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.TaskHolderJSON;
import com.esferalia.aon.occam.api.json.WorkgroupJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderProperties;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.registry.RegistriesServlet;
import net.aonsolutions.aon.api.servlet.registry.RegistryServlet;

@SuppressWarnings("serial")
@WebServlet(name = "AonTaskHoldersServlet", urlPatterns = {"/ms/api/taskholders/*"})
public class TaskHoldersServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(TaskHoldersServlet.class.getName());
	
	public static final String TASK_HOLDERS = "/";
	public static final String TASK_HOLDER = "/:id";
	public static final String TASK_HOLDER_EMAILS = "/:id/emails";
	public static final String TASK_HOLDER_PHONES = "/:id/phones";
	
	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        get(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        get(req, resp);
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) {
        put(req, resp);
    }
	
    
    private void get(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req);
            
            Object object = new AonRouting(api)
                .addRoute(TASK_HOLDERS, TaskHoldersServlet::getTaskHolders)
                .addRoute(TASK_HOLDER, TaskHoldersServlet::getTaskHolder)
                .addRoute(TASK_HOLDER_EMAILS, RegistriesServlet::getRegistryEmails)
                .addRoute(TASK_HOLDER_PHONES, RegistriesServlet::getRegistryPhones)
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
                .addRoute(TASK_HOLDERS, TaskHoldersServlet::saveTaskHolder)
                .addRoute(TASK_HOLDER, TaskHoldersServlet::saveTaskHolder)
                .apply();
            
            response(req, resp, object);
        } catch (Exception e) {
            error(req, resp, e);
        }
    }
    
    private static JSONArray getTaskHolders(AonApiData api) {
        return TaskHolderJSON.toJSON(
                AON.getTaskHolderStream(api.getDomain(), api.getUser(), 
                        f-> filter(api, f)
        ));
    }
    
    private static JSONObject getTaskHolder(AonApiData api) {
        return TaskHolderJSON.toJSON(
                AON.getTaskHolder(api.getDomain(), api.getUser(), f-> filter(api, f)
        ));
    }
    
    private static JSONObject saveTaskHolder(AonApiData api) {
        Domain domain = api.getDomain();
        TaskHolder th = TaskHolderJSON.fromJSON(api.getData());

        Optional<TaskHolder> opt = Optional.empty();
        if(th.getUserId()!=null && th.getId()==null) {
            opt = AON.getTaskHolderStream(domain.getName(), domain.getId(), api.getUser().getLogin(), 
                    f-> f.getDomainProperty().eq(domain.getId())
                    .and(f.getUserIdProperty().eq(th.getUserId())))
            .findFirst();   
        }
        
        TaskHolder taskHolder = AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(),  
                opt.isPresent() ? opt.get().setActive(th.isActive()) : th
        );
        
        RegistryServlet.saveRegistryAdditionalInfo(api, taskHolder.getId(), taskHolder.getDomain().getId());
        
        saveTaskHolderWorkgroup(api, taskHolder);
        
        return TaskHolderJSON.toJSON(taskHolder);
    }
    
    private static void saveTaskHolderWorkgroup(AonApiData api, TaskHolder taskHolder) {
        JSONArray workgroups = api.getData().optJSONArray(IJsonNames.WORKGROUPS);
        Integer userId = taskHolder.getUserId();
        User user = AON.getUser(api.getDomain(), api.getUser().getLogin(), f-> f.getIdProperty().eq(userId));

        if(!workgroups.isEmpty()) {
            List<Workgroup> list = WorkgroupJSON.fromJSON(workgroups)
                .stream()
                .filter(w->w.getId()!=null)
                .collect(Collectors.toList());
            
            if(!list.isEmpty()) {
                user.setWorkgroups(list);
                AON.saveUserWorkgroups(api.getDomain(), api.getUser().getLogin(), user);    
            }
        }
    }

	public static Filter filter(AonApiData api, TaskHolderProperties f) {
		JSONObject params  = api.getData();

		Integer id = params.optInt(IJsonNames.ID);
		String search = params.optString(IJsonNames.SEARCH);
		
		
		Filter filter  = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(id!=0) {
			filter = filter.and(f.getIdProperty().eq(id));
		} 
		
		if(params.opt(IJsonNames.ACTIVE)!=null) {
			filter = filter.and(f.getActiveProperty().eq( (byte)(params.optBoolean(IJsonNames.ACTIVE) ? 1 : 0)) );
		}
		
		if(!search.isEmpty()) {
			Filter searchFilter = f.getNameProperty().like("%" + search + "%")
					.or(f.getDocumentProperty().like("%" + search + "%"))
					.or(f.getAliasProperty().like("%" + search + "%"));
			filter = filter.and(searchFilter);
		}
		
		return filter;
	}	
}
