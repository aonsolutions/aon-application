package net.aonsolutions.aon.api;

import static net.aonsolutions.aon.api.Aon.AON_API_URL;
import static net.aonsolutions.aon.api.Aon.DOMAIN_LOGIN;
import static net.aonsolutions.aon.api.Aon.DOMAIN_NAME;
import static net.aonsolutions.aon.api.Aon.PREDEFINED_TOKEN;
import static net.aonsolutions.aon.api.Aon.SESSION_ID;
import static net.aonsolutions.aon.api.Aon.get;
import static net.aonsolutions.aon.api.Aon.postJSON;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.json.TaskWorkflowJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;

public class AonTask {
    
    public static JSONObject newTask(String domainName, String userLogin,  Task task) throws URISyntaxException, IOException, InterruptedException {
	JSONObject jsonObject = TaskJSON.toJSON(task);
	return postJSON(domainName, userLogin, "/ms/api/task", jsonObject);
    }

    public static JSONObject addTaskWorkflow(String domainName, String userLogin,  TaskWorkflow taskWorkflow) throws URISyntaxException, IOException, InterruptedException {
	JSONObject jsonObject = TaskWorkflowJSON.toJSON(taskWorkflow);
	System.out.println(jsonObject.toString());
	return postJSON(domainName, userLogin, "/ms/api/task/workflow", jsonObject);
    }
    
    public static JSONArray getTaskWorkflows(String domainName, String userLogin, Integer taskId ) throws URISyntaxException, IOException, InterruptedException {
	String response = get(domainName, userLogin, "/ms/api/task/workflow", Collections.singletonMap(IJsonNames.TASK, taskId));
	return new JSONArray(response);
    }

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
	String userLogin = "jgarcia";
	String domainName = "translogia.aonsolutions.org";
	String title = "TITULO TAREA";
	String workgroup = "WORKGROUP";
	String description = "DESCRIPCION TAREA.";
	
//	AonTask.newTask(
//		domainName, 
//		userLogin, 
//		new Task()
//		.setTitle(title)
//		.setDescription(description)
//		.setWorkgroup(new Workgroup().setDescription(workgroup)));
	
	JSONArray taskWorkflows = AonTask.getTaskWorkflows(domainName, userLogin, 33950);
	System.out.println(taskWorkflows.toString(1));
	
    }
    

}
