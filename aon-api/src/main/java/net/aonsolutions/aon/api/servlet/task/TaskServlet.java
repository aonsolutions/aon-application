package net.aonsolutions.aon.api.servlet.task;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.json.TaskWorkflowJSON;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;

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
				case "/workflow":
					response(req, resp,  getTaskWorkflow(api));
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
						.and( 
							workgroup > 0 ?  
							f.getWorkgroupProperty().eq(workgroup) :
							f.getWorkgroupProperty().isNotNull()
						),
						page, peerPage)
		);
	}
	
	private Object saveTask(AonApiData api) {
		Task task = TaskJSON.fromJSON(api.getData());
		task = AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), task);
		saveTaskWorkflow(api, task); //ADD WORKFLOW
		return TaskJSON.toJSON(task);
	}
	
	private Object getTaskWorkflow(AonApiData api) {
		Integer taskId = api.getParams().optInt("taskId");
		return TaskWorkflowJSON.toJSON(
				AON_SOLUTIONS.getTaskWorkflowStream(api.getDomain(), api.getUser(), 
				f->f.getTaskProperty().eq(taskId)) 
		);
	}
	
	private void saveTaskWorkflow(AonApiData api, Task task) {
//		Thread newThread = new Thread(() -> {
//				TaskWorkflow workflow = TaskWorkflowJSON.fromJSON(api.getData().opt("workflow"));
//				workflow.setTask(task.getId());
//				if(api.getData().getString("id").isEmpty()) { //open task
//					workflow.setType(TaskWorkflowType.OPEN);
//				} 
			task.getWorkflows().stream().forEach(workflow -> AON_SOLUTIONS.saveTaskWorkflow(api.getDomain(), api.getUser(), workflow.setTask(task.getId())));
//		});
//		newThread.start();
	}
}
