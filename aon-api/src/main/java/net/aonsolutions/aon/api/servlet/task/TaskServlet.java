package net.aonsolutions.aon.api.servlet.task;

import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.model.task.Task;
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
		return TaskJSON.toJSON(
				AON_SOLUTIONS.getTaskStream(
						api.getDomain(), api.getUser(), 
						f-> f.getDomainProperty().eq(api.getDomain().getId())
				)
		);
	}
	
	private Object saveTask(AonApiData api) {
		Task task = TaskJSON.fromJSON(api.getData());
		addWorkflow(api, task); //ADD WORKFLOW
		return TaskJSON.toJSON(AON_SOLUTIONS.saveTask(api.getDomain(), api.getUser(), task));
	}
	
	private void addWorkflow(AonApiData api, Task task) {
		String comment = api.getData().getString("comment");
		System.out.println(comment);
	}
}
