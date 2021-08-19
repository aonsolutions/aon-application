package net.aonsolutions.aon.api.servlet.task;

import java.util.Base64;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.TaskAttachJSON;
import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.json.TaskWorkflowJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.type.MimeType;
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
				case "/one":
					response(req, resp,  getTask(api));
					break;
				case "/workflow":
					response(req, resp,  getTaskWorkflow(api));
					break;
				case "/attach":
					response(req, resp,  getTasksAttach(api));
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
					response(req, resp, saveTask(api));
				break;
				case "/attach":
					response(req, resp,  saveTaskAttach(api));
					break;
				case "/workflow":
					response(req, resp,  saveTaskWorkflow(api));
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
	public void doDelete(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API TASK SERVLET - DELETE METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
			case "/":
				response(req, resp, deleteTask(api));
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Object getTasks(AonApiData api) {
		Integer page = api.getParams().optInt("page");
		Integer perPage = api.getParams().optInt("perPage");
		return TaskJSON.toJSON(
				AON_SOLUTIONS.getTaskStream(api.getDomain(), api.getUser(), f -> taskFilter(api, f), page, perPage));
	}
	
	private Filter taskFilter(AonApiData api, TaskProperties f) {
		Integer workgroup = api.getParams().optInt("workgroup");
		String status = api.getParams().optString("status");
		String source = api.getParams().optString("source");
		Integer taskHolder = api.getParams().optInt("task_holder");
		Integer sender = api.getParams().optInt("sender");
		String search = api.getParams().optString("search");
		
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		if("pending".equalsIgnoreCase(status)) {
			filter = filter.and(f.getStatusProperty().eq(TaskStatus.IN_PROGRESS.value()).or(f.getStatusProperty().eq(TaskStatus.PENDING.value())));
		} else 
			filter = filter.and(f.getStatusProperty().eq(TaskStatus.safeValueOf(status).value()));
		
		if(workgroup != null && workgroup > 0) 
			filter = filter.and(f.getWorkgroupProperty().eq(workgroup));

		if(taskHolder != null && taskHolder!=0) 
			filter = filter.and(f.getTaskHolderProperty().eq(taskHolder));

		if(sender != null && sender!=0) 
			filter = filter.and(f.getSenderProperty().eq(sender));

		if(!source.isEmpty()) 
			filter = filter.and(f.getSourceProperty().eq(TaskSource.safeValueOf(source).value()));
		
//		if(!search.isEmpty()) 
			
			
		return filter;
	}
	
	private Object getTask(AonApiData api) {
		Integer taskId = api.getParams().optInt("id");
		return TaskJSON.toJSON( AON_SOLUTIONS.getTask(api.getDomain(), api.getUser(), f-> f.getIdProperty().eq(taskId)));
	}
	
	private Object saveTask(AonApiData api) {
		Task task = TaskJSON.fromJSON(api.getData());
		task = AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), task);
		if(task.getWorkflows().size() > 0) {
			saveAllTaskWorkflow(api, task); //ADD WORKFLOW
		}
		return TaskJSON.toJSON(task);
	}
	
	private Object getTaskWorkflow(AonApiData api) {
		Integer taskId = api.getParams().optInt("taskId");
		return TaskWorkflowJSON.toJSON(
				AON_SOLUTIONS.getTaskWorkflowStream(api.getDomain(), api.getUser(), 
				f->f.getTaskProperty().eq(taskId)) 
		);
	}
	
	private void saveAllTaskWorkflow(AonApiData api, Task task) {
		task.getWorkflows().stream().forEach(workflow -> AON_SOLUTIONS.saveTaskWorkflow(api.getDomain(), api.getUser(), workflow.setTask(task.getId())));
	}
	
	private JSONObject saveTaskWorkflow(AonApiData api) {
		return TaskWorkflowJSON.toJSON(AON_SOLUTIONS.saveTaskWorkflow(api.getDomain(), api.getUser(), TaskWorkflowJSON.fromJSON(api.getData())));
	}
	
	private Object getTasksAttach(AonApiData api) {
		Integer taskId = api.getParams().optInt("taskId");
		return TaskAttachJSON.toJSON( AON_SOLUTIONS.getTaskAttachList(api.getDomain(), api.getUser(), f-> f.getTaskProperty().eq(taskId)));
	}
	
	private JSONObject saveTaskAttach(AonApiData api) {
		Domain domain = api.getDomain();
		Integer taskId = api.getData().optInt("taskId");
		if(api.getData().opt("file")!= null) { 
			
			JSONObject file  = api.getData().optJSONObject("file");
			String base64 = file.optString("content");
			String contentType = file.optString("contentType");
			byte[] fileData = Base64.getDecoder().decode(base64);
			TaskAttach taskAttach = new TaskAttach()
			.setDomain(domain.getId())
			.setTask(taskId)
			.setData(fileData)
			.setMimetype(MimeType.get(contentType));
			
			return TaskAttachJSON.toJSON(AON_SOLUTIONS.saveTaskAttach(domain, api.getUser(), taskAttach));
		}
		return new JSONObject();
	}
	
	private JSONObject deleteTask(AonApiData api) {
		Integer taskId = api.getData().optInt("taskId");
		AON_SOLUTIONS.deleteTask(api.getDomain(), api.getUser(), taskId);
		return new JSONObject();
	}
}
