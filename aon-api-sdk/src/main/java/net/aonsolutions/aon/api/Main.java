package net.aonsolutions.aon.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.json.TaskHolderJSON;
import com.esferalia.aon.occam.api.json.TaskJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.Task;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import com.esferalia.aon.occam.api.model.task.TaskHolderType;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class Main {

	public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
		Auth auth = AonAuth.getAuth("admin@aonsolutions.org","artist-payroll-test.aonsolutions.org" );
		if (auth.getUuid() != null && auth.getUuid().length() > 0) {
			String token = AonToken.build(auth.getUuid(), AonDateUtils.addMonths(new Date(), 3));
			List<com.esferalia.aon.occam.api.model.AonCompany> companies = AonCompany.getCompanies(token, "hola");
			for (com.esferalia.aon.occam.api.model.AonCompany company : companies) {

				if (company.getDomain().getName().equals("artist-payroll-test.aonsolutions.org")) {

					User user = AonUser.getUser(company.getDomain().getName(), token);
					List<TaskHolder> taskHolderList = AonTask.getTaskHolders(company.getDomain().getName(),
							user.getLogin(), token);
					user.setTaskHolders(taskHolderList);

					if (user.getTaskHolders().isEmpty()) {

						TaskHolder newTaskHolder = (TaskHolder) new TaskHolder().setRegistry(user.getRegistry().getId())
								.setUserId(user.getId()).setType(TaskHolderType.INTERNAL)
								.setWorkgroups(user.getWorkgroups()).setDomain(company.getDomain());
						TaskHolder taskHolder = TaskHolderJSON.fromJSON(AonTask
								.newTaskHolder(company.getDomain().getName(), user.getLogin(), token, newTaskHolder));
						System.out.println(taskHolder.toString());
						taskHolderList = AonTask.getTaskHolders(company.getDomain().getName(),
								user.getLogin(), token);
						user.setTaskHolders(taskHolderList);
					}
					
					List<Workgroup> workgroupList = AonTask.getWorkgroups(company.getDomain().getName(), user.getLogin(), token);
					if(workgroupList.isEmpty()) {
						Workgroup workgroup = new Workgroup()
								.setDescription("GENERAL")
								.setDomain(company.getDomain().getId());
						AonTask.newWorkgroup(company.getDomain().getName(), user.getLogin(), token, workgroup);
						workgroupList = AonTask.getWorkgroups(company.getDomain().getName(), user.getLogin(), token);
					}
					System.out.println(workgroupList);
                        
					Task newTask = new Task()
							.setCreationDate(new Date())
							.setCreationUser(user.getLogin())
							.setDomain(company.getDomain())
							.setTitle("Testing the creation of a task")
							.setTaskHolder(user.getTaskHolders().get(0))
							.setSender(user.getTaskHolders().get(0))
							.setRegistry(user.getRegistry())
							.setStatus(TaskStatus.PENDING)
							.setWorkgroup(workgroupList.get(0));
					
				
						Task task = TaskJSON.fromJSON(AonTask.newTask(company.getDomain().getName(), user.getLogin(), newTask));
					    System.out.println(task.getId());
					    
					    TaskWorkflow taskWorkflow = new TaskWorkflow();
					    taskWorkflow.setCreationUser(user.getLogin())
					    .setCreationDate(new Date())
					    .setTask(task.getId())
					    .setTaskHolder(taskHolderList.get(0))
					    .setComment("Hola Mundo")
					    .setType(TaskWorkflowType.COMMENT)
					    .setEmail("admin@aonsolutions.org");
					    
					    AonTask.addTaskWorkflow(company.getDomain().getName(), user.getLogin(), taskWorkflow);
					      
					    
				}
			}
			// JSONObject domain = (JSONObject) domains.get(0);
			// Domain d = DomainJSON.fromJSON(domain);
			// String domainName = domain.getString("domain");
			// User user = AonUser.getUser(domainName, token);
//	       String userLogin = userData.getString("login");
//	       Integer userId = userData.getInt("id");
//	       Integer registryId = userData.getInt("registry");
//	       JSONArray taskHolders = new JSONArray(Aon.get(domainName, userLogin, token, "/ms/api/taskholder/list", Collections.EMPTY_MAP));
//	       if(taskHolders.isEmpty()) {
//	    	   TaskHolder newTaskHolder = 
//	    			   new TaskHolder()
//	    			   .setRegistry(registryId)
//	    			   .setUserId(userId)
//	    			   .setType(TaskHolderType.INTERNAL);
//	    	   AonTaskHolder.newTaskHolder(domainName, userLogin, token,  newTaskHolder);
//	    	   taskHolders = new JSONArray(Aon.get(domainName, userLogin, token, "/ms/api/taskholder/list", Collections.EMPTY_MAP));
//	       }

		} else
			throw new IllegalArgumentException();
	}

}
