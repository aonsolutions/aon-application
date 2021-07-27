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
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskAttach;
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
	
	private Object getTasks(AonApiData api) {
		Integer page = api.getParams().optInt("page");
		Integer peerPage = api.getParams().optInt("peerPage");
		Integer workgroup = api.getParams().optInt("workgroup");
		String status = api.getParams().optString("status");
		
		return TaskJSON.toJSON(
				AON_SOLUTIONS.getTaskList(api.getDomain(), api.getUser(),  
						f-> 
						f.getDomainProperty().eq(api.getDomain().getId())
						.and(f.getStatusProperty().eq(TaskStatus.safeValueOf(status).value()))
						.or(
								status.equalsIgnoreCase("pending") ?
								f.getStatusProperty().eq(TaskStatus.IN_PROGRESS.value()) :
								f.getStatusProperty().eq(TaskStatus.safeValueOf(status).value()) 
						)
						.and( 
							workgroup > 0 ?  
							f.getWorkgroupProperty().eq(workgroup) :
							f.getWorkgroupProperty().isNotNull()
						),
						page, peerPage)
		);
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
}
