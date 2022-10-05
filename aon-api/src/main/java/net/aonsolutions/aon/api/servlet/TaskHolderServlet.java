package net.aonsolutions.aon.api.servlet;

import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.TaskHolderJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonTaskHolderServlet", urlPatterns = {"/ms/api/taskholder/*"})
public class TaskHolderServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(TaskHolderServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TASKHOLDER SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, getTaskHolder(api));
				break;
			case "/list":
				response(req, resp, getTaskHolders(api));
				break;
			case "/enterprise":
				response(req, resp, getTaskHoldersEnterprise(api));
				break;
			case "/workgroup":
				response(req, resp, getTaskHoldersWorkGroup(api));
				break;
			case "/user":
				response(req, resp, getTaskHoldersUser(api));
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
		LOGGER.info("AON API TASKHOLDER SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
			case "/":
				response(req, resp, setTaskHolder(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	

	private Object getTaskHoldersUser(AonApiData api) throws Exception {
		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		LinkedList<TaskHolder> taskHolders = AON_SOLUTIONS.getTaskHolders(aonToken);
		JSONArray array = new JSONArray();
		taskHolders.stream()
		.forEach(th -> {
			JSONObject json = new JSONObject();
			json.put("id", th.getId());
			json.put("name", th.getName());
			json.put("company", th.getDomain().getDescription());
			json.put("domain_id", th.getDomain().getId());
			json.put("domain_name", th.getDomain().getName());
			array.put(json);
		});
		return array;
	}	

	private JSONArray getTaskHoldersEnterprise(AonApiData api) {
		Domain domain = api.getDomain();
		JSONArray array = new JSONArray();
		AON.getTaskHolderStream(domain.getName(), domain.getId(), api.getUser().getLogin(), 
				f -> f.getDomainProperty().eq(domain.getId()).and(f.getActiveProperty().eq((byte)1))
		)
		.forEach(th->{
			JSONObject json = new JSONObject();
			json.put("id", th.getId());
			json.put("name", th.getName());
			json.put("alias", th.getAlias());
			json.put("document", th.getDocument());
			json.put("company", th.getDomain().getDescription());
			json.put("domain_id", th.getDomain().getId());
			json.put("domain_name", th.getDomain().getName());
			array.put(json);
		});
		return array;
	}
	
	private JSONArray getTaskHolders(AonApiData api) {
		Domain domain = api.getDomain();
		return TaskHolderJSON.toJSON(AON.getTaskHolderStream(domain.getName(), domain.getId(), api.getUser().getLogin(), 
				f-> filter(api, f)
		));
	}
	
	private JSONObject getTaskHolder(AonApiData api) {
		Domain domain = api.getDomain();
		return TaskHolderJSON.toJSON(
			AON.getTaskHolder(domain.getName(), domain.getId(), api.getUser().getLogin(), 
				f->	api.getData().opt(IJsonNames.ID)!=null ? 
				    filter(api, f) :
				    f.getDomainProperty().eq(domain.getId()).and(f.getUserIdProperty().eq(api.getUser().getId()))
			)
		);
	}
	
	private JSONArray getTaskHoldersWorkGroup(AonApiData api) {
		Domain domain     = api.getDomain();
		Integer workgroup = api.getData().optInt(IJsonNames.WORKGROUP);
		Integer active    = api.getData().optInt(IJsonNames.ACTIVE);
		return TaskHolderJSON.toJSON(AON.getTaskHolderWorkgroupStream(domain, api.getUser(), 
				f->f.getUserIdProperty().isNotNull()
				.and(f.getActiveProperty().eq(active.byteValue()))
				.and(f.getDomainProperty().eq(api.getDomain().getId())), 
				workgroup));
	}
	
	private JSONObject setTaskHolder(AonApiData api) {
		Domain domain = api.getDomain();
		TaskHolder th = TaskHolderJSON.fromJSON(api.getData());

		Optional<TaskHolder> opt = Optional.empty();
		if(th.getUserId()!=null && th.getId()==null) {
			opt = AON.getTaskHolderStream(domain.getName(), domain.getId(), api.getUser().getLogin(), 
					f-> f.getDomainProperty().eq(domain.getId()).and(f.getUserIdProperty().eq(th.getUserId())))
			.findFirst();	
		}
		
		TaskHolder taskHolder = opt.isPresent() ? opt.get().setActive(th.isActive()) : th;
	
		return TaskHolderJSON.toJSON( 
				AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), taskHolder)
		);
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
