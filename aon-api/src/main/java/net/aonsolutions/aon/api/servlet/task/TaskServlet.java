package net.aonsolutions.aon.api.servlet.task;

import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "TaskServlet", urlPatterns = {"/ms/api/task/*"})
public class TaskServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(TaskServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON TASK SERVLET GET");
		try {		
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					response(req, resp,  getTasks(api));
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("AON TASK SERVLET POST");
		try {		
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					response(req, resp, new JSONObject());
				break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}

	private Object getTasks(AonApiData api) {
		Domain domain = api.getDomain();
		JSONArray array = new JSONArray();
		AON.getTaskStream(domain.getName(), domain.getId(), "",
				f-> f.getDomainProperty().eq(domain.getId())
//				f->f.getIdProperty().isNotNull()
		)
		.forEach(task->{
			JSONObject json = new JSONObject();
			json.put("id", task.getId());
			json.put("domain", task.getDomain());
			json.put("description", task.getDescription());
			json.put("priority", task.getPriority());
			json.put("status", task.getStatus());
			json.put("percent", task.getPercent());
			json.put("taskHolder", task.getTaskHolder());
			json.put("workgroup", task.getWorkgroup());
			json.put("source", task.getSource());
			json.put("sourceId", task.getSourceId());
			json.put("project", task.getProject());
			json.put("registry", task.getRegistry());
			json.put("activityType", task.getActivityType());
			json.put("sender", task.getSender());
			json.put("comments", task.getComments());
			json.put("repeatPeriod", task.getRepeatPeriod());
			json.put("gtaskId", task.getGtaskId());
			json.put("gtasklistId", task.getGtasklistId());
			json.put("number", task.getNumber());
			json.put("creationUser", task.getCreationUser());
			json.put("number", task.getNumber());
			json.put("modificationUser", task.getModificationUser());
			json.put("parent", task.getParent());
			json.put("creationDate", task.getCreationDate()!=null ? task.getCreationDate().getTime() : null);
			json.put("modificationDate", task.getModificationDate()!=null ? task.getModificationDate().getTime() : null);
			json.put("startDate", task.getStartDate()!=null ? task.getStartDate().getTime() : null);
			json.put("endDate",  task.getEndDate()!=null ? task.getEndDate().getTime() : null);
			json.put("dueDate", task.getDueDate()!=null ? task.getDueDate().getTime(): null);
			json.put("dueDate", task.getDueDate()!=null ? task.getDueDate().getTime(): null);
			array.put(json);
		});
		return array;
	}
}
