package net.aonsolutions.aon.api;

import static net.aonsolutions.aon.api.Aon.get;
import static net.aonsolutions.aon.api.Aon.postJSON;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.TaskHolderJSON;
import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.json.TaskWorkflowJSON;
import com.esferalia.aon.occam.api.json.WorkgroupJSON;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;

public class AonTask {

	public static JSONObject newTask(String domainName, String userLogin, Task task)
			throws URISyntaxException, IOException, InterruptedException {
		JSONObject jsonObject = TaskJSON.toJSON(task);
		return postJSON(domainName, userLogin, "/ms/api/task", jsonObject);
	}

	public static JSONObject addTaskWorkflow(String domainName, String userLogin, TaskWorkflow taskWorkflow)
			throws URISyntaxException, IOException, InterruptedException {
		JSONObject jsonObject = TaskWorkflowJSON.toJSON(taskWorkflow);
		System.out.println(jsonObject.toString());
		return postJSON(domainName, userLogin, "/ms/api/task/workflow", jsonObject);
	}

	public static JSONArray getTaskWorkflows(String domainName, String userLogin, Integer taskId)
			throws URISyntaxException, IOException, InterruptedException {
		String response = get(domainName, userLogin, "/ms/api/task/workflow",
				Collections.singletonMap(IJsonNames.TASK, taskId));
		return new JSONArray(response);
	}

	public static JSONObject newTaskHolder(String domainName, String userLogin, String token, TaskHolder taskHolder)
			throws URISyntaxException, IOException, InterruptedException {
		JSONObject jsonObject = TaskHolderJSON.toJSON(taskHolder);
		return postJSON(domainName, userLogin, token, "/ms/api/taskholder", jsonObject);
	}

	public static List<TaskHolder> getTaskHolders(String domainName, String userLogin, String token)
			throws URISyntaxException, IOException, InterruptedException {
		String response = get(domainName, userLogin, token, "/ms/api/taskholder/list", Collections.EMPTY_MAP);
		JSONArray responseJSON = new JSONArray(response);
		return TaskHolderJSON.fromJSON(responseJSON);
	}

	public static List<Workgroup> getWorkgroups(String domainName, String userLogin, String token)
			throws URISyntaxException, IOException, InterruptedException {
		String response = get(domainName, userLogin, token, "/ms/api/workgroup/", Collections.EMPTY_MAP);
		JSONArray responseJSON = new JSONArray(response);
		return WorkgroupJSON.fromJSON(responseJSON);
	}
	
	public static JSONObject newWorkgroup(String domainName, String userLogin, String token, Workgroup workgroup)
			throws URISyntaxException, IOException, InterruptedException {
		JSONObject jsonObject = WorkgroupJSON.toJSON(workgroup);
		return postJSON(domainName, userLogin, token, "/ms/api/workgroup", jsonObject);
	}

}
