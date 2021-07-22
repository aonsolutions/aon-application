package net.aonsolutions.aon.api.servlet;

import java.util.LinkedList;
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
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

import net.aonsolutions.aon.api.ewok.AonApiData;
@SuppressWarnings("serial")
@WebServlet(name = "AonTaskHolderServlet", urlPatterns = {"/ms/api/taskholder/*"})
public class TaskHolderServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(TaskHolderServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TASKHOLDER SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					response(req, resp, getTaskHolder(api));
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
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("AON API TASKHOLDER SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					response(req, resp, setTaskHolder(api));
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Object getTaskHoldersUser(AonApiData api) throws Exception {
		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		LinkedList<TaskHolder> taskHolders = AON_SOLUTIONS.getTaskHolders(aonToken);
		JSONArray array = new JSONArray();
		taskHolders.stream().forEach(th -> {
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
		AON.getTaskHolderStream(domain.getName(), domain.getId(), "", f -> f.getDomainProperty().eq(domain.getId()))
		.forEach(th->{
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
	
	private JSONObject getTaskHolder(AonApiData api) {
		Domain domain = api.getDomain();
		return TaskHolderJSON.toJSON(
				AON.getTaskHolder(domain.getName(), domain.getId(), api.getUser().getLogin(), 
						f->f.getDomainProperty().eq(domain.getId()).and(f.getUserIdProperty().eq(api.getUser().getId())))
		);
	}
	
	private JSONArray getTaskHoldersWorkGroup(AonApiData api) {
		Domain domain = api.getDomain();
		JSONArray array = new JSONArray();
		Integer workgroupId = api.getParams().optInt("workgroupId");
		AON.getTaskHolderWorkgroupStream(domain, api.getUser(), f->f.getDomainProperty().eq(domain.getId()), workgroupId)
		.forEach(th->{
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
	
	private JSONObject setTaskHolder(AonApiData api) {
		TaskHolder th = TaskHolderJSON.fromJSON(api.getData());
		th = AON.save(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), th);
		return TaskHolderJSON.toJSON(th);
	}
	
}
