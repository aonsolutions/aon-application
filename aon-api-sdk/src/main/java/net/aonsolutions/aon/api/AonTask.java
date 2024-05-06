package net.aonsolutions.aon.api;

import static net.aonsolutions.aon.api.Aon.get;
import static net.aonsolutions.aon.api.Aon.postJSON;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.json.TaskWorkflowJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Workgroup;
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
	String userLogin = "admin";
	String domainName = "invofox-management-test.aonsolutions.org";
	String title = "TITULO TAREA";
	String workgroup = "WORKGROUP";
	String description = "DESCRIPCION TAREA.";
	
	JSONObject task = 
		AonTask.newTask(
		domainName, 
		userLogin, 
		new Task()
		.setTitle(title)
		.setDescription(description)
		.setWorkgroup(new Workgroup().setDescription(workgroup)));
	System.out.println(task.toString(1));
//	JSONArray taskWorkflows = AonTask.getTaskWorkflows(domainName, userLogin, 33950);
//	System.out.println(taskWorkflows.toString(1));
	
    }
    

}
