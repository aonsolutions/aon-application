package net.aonsolutions.aon.api.servlet.task;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.registry.RegistryBank;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskEvaluation;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@SuppressWarnings("serial")
@WebServlet(name = "TaskEvaluationServlet", urlPatterns = {"/ms/api/task-evaluation/*"})
public class TaskEvaluationServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(TaskEvaluationServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON TASK-EVALUATION SERVLET GET");
		try {		
			AonApiData api = initialize(req, false);
			switch (api.getPath()) {
				case "/":
					responseHtml(req, resp, saveTaskEvaluation(api));
					break;
				case "/rbank":
					responseHtml(req, resp, saveRBank(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		doGet(req, resp);
	}

	private String saveTaskEvaluation(AonApiData api) {
		Domain domain = api.getDomain();
		JSONObject params = api.getData();
		Integer id = params.optInt(IJsonNames.TASK);
		Integer evaluation = params.optInt("evaluation");
		String message = "";
		
		Task task = AON_SOLUTIONS.getTask(domain, new User(), f-> f.getIdProperty().eq(id));
		
		if(task.getId()==null) {
			throw new AonApiException(AonApiError.EMPTY_DATA.getMessage());
		}
		
		TaskWorkflow workflow = new TaskWorkflow()
		.setTask(task.getId())
		.setType(TaskWorkflowType.EVALUATION)
		;
		
		if( task.getEvaluation()!=null) {
			message = "Ya ha calificado!";
		} else {
			message = "Gracias por su calificaci\u00f3n!";
			
			task.setEvaluation(TaskEvaluation.safeValueOf(evaluation));
			AON_SOLUTIONS.saveTask(api.getDomain(), new User(), task);
			TaskUtils.onNotification(api, workflow);
		}
		
		return "<div style='font-family: Arial;background-color: #e3f2fd; width: 100%; height: 100%; position: absolute; right: 0; top: 0; left: 0; bottom: 0;'>\n" + 
				"    <div style='position: relative; display:flex; justify-content:center; align-items:center;height: 100%; flex-direction: column;'>\n" + 
				"        <div>\n" + 
				"            <h1>"+message+"</h1>\n" + 
				"        </div>\n" + 
				"        <div>\n" + 
				"          <!-- back button-->\n" + 
				"            <button style='\n" + 
				"                background-image: linear-gradient(to right, #00d2ff 0%, #3a7bd5  51%, #00d2ff  100%);\n" + 
				"                margin: 10px;\n" + 
				"                padding: 15px 45px;\n" + 
				"                text-align: center;\n" + 
				"                text-transform: uppercase;\n" + 
				"                transition: 0.5s;\n" + 
				"                background-size: 200% auto;\n" + 
				"                color: white;            \n" + 
				"                box-shadow: 0 0 20px #eee;\n" + 
				"                border-radius: 10px;\n" + 
				"                display: block;\n" + 
				"                border:none;\n" + 
				"                cursor: pointer;\n" + 
				"                font-weight: bold;\n" + 
				"             ' onclick=\"window.location.href='https://"+domain.getName()+"'\">Inicio</button>\n" + 
				"        </div>\n" + 
				"    </div>\n" + 
				"</div>         ";
	}
	
	
	private String saveRBank(AonApiData api) {
		Domain domain     = api.getDomain();
		JSONObject params = api.getData();
		Integer id  = params.optInt("rbank");

		String message = "Parametros requeridos";
		
		if(id!=0) {
			RegistryBank rbank = AON.getRegistryBank(domain, "", f-> f.getIdProperty().eq(id));
			if(rbank.getId()==null) {
				throw new AonApiException(AonApiError.EMPTY_DATA.getMessage());
			}
		
			message  = "Banco vinculado!";
		}
		
		return "<div style='font-family: Arial;background-color: #e3f2fd; width: 100%; height: 100%; position: absolute; right: 0; top: 0; left: 0; bottom: 0;'>\n" + 
				"    <div style='position: relative; display:flex; justify-content:center; align-items:center;height: 100%; flex-direction: column;'>\n" + 
				"        <div>\n" + 
				"            <h1>"+message+"</h1>\n" + 
				"        </div>\n" + 
				"        <div>\n" + 
				"            <h6>"+params.toString()+"</h6>\n" + 
				"        </div>\n" + 
				"        <div>\n" + 
				"          <!-- back button-->\n" + 
				"            <button style='\n" + 
				"                background-image: linear-gradient(to right, #00d2ff 0%, #3a7bd5  51%, #00d2ff  100%);\n" + 
				"                margin: 10px;\n" + 
				"                padding: 15px 45px;\n" + 
				"                text-align: center;\n" + 
				"                text-transform: uppercase;\n" + 
				"                transition: 0.5s;\n" + 
				"                background-size: 200% auto;\n" + 
				"                color: white;            \n" + 
				"                box-shadow: 0 0 20px #eee;\n" + 
				"                border-radius: 10px;\n" + 
				"                display: block;\n" + 
				"                border:none;\n" + 
				"                cursor: pointer;\n" + 
				"                font-weight: bold;\n" + 
				"             ' onclick=\"window.close();window.opener.parent.postMessage(\"exit\", \"*\");\">Cerrar</button>\n" + 
				"        </div>\n" + 
				"    </div>\n" + 
				"</div>         ";
	}
}
