package com.code.aon.google.apis;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.google.apis.jooq.DBTask;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.security.User;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.google.api.services.tasks.model.Tasks;

/**
 * @author aibanez
 */
public class TaskUtils {
	
	/**
	 * @class Search, utilidades para la búsqueda y ordenacion de listas 
	 * de tasklist y task.
	 * @author aibanez
	 */
	public static class Search{
		

		public static Integer searchProject(Project project, TaskLists taskLists , int n){
			// COMPARA EL NOMBRE DEL PROYECTO DE LA BD CON EL TITULO DE LA LISTA DE TAREAS DE GOOGLE
			int centro;
			int inf = 0;
			int sup = n - 1;
			
			while (n!=0 && inf <= sup) {
				centro = (sup + inf) / 2;
				if ( taskLists.getItems().get(centro).getTitle().equals(project.getName())){
					return centro;
				} else if ( taskLists.getItems().get(centro).getTitle().compareTo(project.getName())>0){
					sup = centro - 1;
				} else{
					inf = centro + 1;
				}
			}
			
			return -1;
		}
		
		public static Integer searchTask(com.esferalia.aon.occam.api.model.OldTask taskBD, Tasks tasks, int n){
			// COMPARA LA DESCRIPCIÓN DE LA TAREA DE LA BD CON EL TITULO DE LA TAREA DE GOOGLE
			int centro;
			int inf = 0;
			int sup = n - 1;
			
			while (n!=0 && inf <= sup) {
				centro = (sup + inf) / 2;
				if ( tasks.getItems().get(centro).getTitle().equals(taskBD.getDescription())){
					return centro;
				} else if ( tasks.getItems().get(centro).getTitle().compareTo(taskBD.getDescription())>0){
					sup = centro - 1;
				} else{
					inf = centro + 1;
				}
			}
			return -1;
		}
		
		private static Tasks tasks;
		private static TaskLists tasklists;
		private static int number;

		public static TaskLists taskListsort(TaskLists taskLists){
			tasklists = taskLists;
			number = taskLists.getItems().size();
			quicksortTaskList(0,number - 1);
			return tasklists;
		}
		
		static void quicksortTaskList(int low, int high) {
			int i = low, j = high;
			String pivot = tasklists.getItems().get(low + (high - low) / 2).getTitle();
			while (i <= j) {
				while ( tasklists.getItems().get(i).getTitle().compareTo(pivot)<0) {
					i++;
				}
				while (tasklists.getItems().get(j).getTitle().compareTo(pivot)>0) {
					j--;
				}
				if (i <= j) {
					exchangeTaskList(i, j);
					i++;
					j--;
				}
			}
			if (low < j)
				quicksortTaskList(low, j);
			if (i < high)
				quicksortTaskList(i, high);
		}
		
		static void exchangeTaskList(int i, int j) {
			TaskList aux = tasklists.getItems().get(i);
			tasklists.getItems().set(i, tasklists.getItems().get(j));
			tasklists.getItems().set(j, aux);
		}
		
		public static Tasks tasksort(Tasks tasks2){
			tasks = tasks2;
			number = tasks2.getItems().size();
			if(number !=0)
				quicksortTask(0,number - 1);
			return tasks;
		}
		
		static void quicksortTask(int low, int high) {
			int i = low, j = high;
			String pivot = tasks.getItems().get(low + (high - low) / 2).getTitle();
			while (i <= j) {
				while ( tasks.getItems().get(i).getTitle().compareTo(pivot)<0) {
					i++;
				}
				while (tasks.getItems().get(j).getTitle().compareTo(pivot)>0) {
					j--;
				}
				if (i <= j) {
					exchangeTask(i, j);
					i++;
					j--;
				}
			}
			if (low < j)
				quicksortTask(low, j);
			if (i < high)
				quicksortTask(i, high);
		}
		
		static void exchangeTask(int i, int j) {
			Task aux = tasks.getItems().get(i);
			tasks.getItems().set(i, tasks.getItems().get(j));
			tasks.getItems().set(j, aux);
		}

			
		
	}
	
	
	//private static Credential credential;	
	//private static com.google.api.services.tasks.Tasks client;
	
	/**
	 * 
	 * @param req
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void initialize(HttpServletRequest req) throws IOException,
	ServletException {
		//credential = newFlow().loadCredential(getPrincipalShortName(req));/**HttpServletRequest req**/
		//client = new com.google.api.services.tasks.Tasks.Builder(
		//		getHttpTransport(), getJsonFactory(), credential)
		//		.setApplicationName("AON SOLUTIONS").build();
		
		//client=GoogleAuthorizationCodeCallbackServlet.tasks;
	}
	
	
	//------------------------------------------- TASKLIST
	
	/**
	 * 
	 * @param project
	 * @return
	 */
	public static TaskList newTaskList(Project project){
		TaskList taskList= new TaskList();
		
		taskList.setTitle(project.getName());
		DateTime date = new DateTime(new Date(), TimeZone.getTimeZone("UTC"));
		taskList.setUpdated(date);
		
		return taskList;
	}
	
	/**
	 * 
	 * @param taskList
	 * @return
	 * @throws IOException
	 */
	public static TaskList addTaskList(TaskList taskList,com.google.api.services.tasks.Tasks client) throws IOException{
		TaskList result = client.tasklists().insert(taskList).execute();
		return result;
	}
	
	/**
	 * 
	 * @param taskList
	 * @return
	 * @throws IOException
	 */
	public static TaskList removeTaskList(TaskList taskList, com.google.api.services.tasks.Tasks client) throws IOException{
		client.tasklists().delete(taskList.getId()).execute();
		return taskList;
	}
	
	/**
	 * 
	 * @param taskList
	 * @return
	 * @throws IOException
	 */
	public static TaskList updateTaskList(TaskList taskList,com.google.api.services.tasks.Tasks client) throws IOException{
		TaskList result = client.tasklists().update(taskList.getId(), taskList).execute();
		return result;
	}
	
	
	//------------------------------------------- TASK
	
	/**
	 * 
	 * @param task
	 * @return
	 */
	public static Task newTask(com.esferalia.aon.occam.api.model.OldTask task){
		Task task2= new Task();
		
		DateTime date = new DateTime(task.getDueDate(), TimeZone.getTimeZone("UTC"));// es posible que sea necesario el convertDate de CalendarUtils
		DateTime updated = new DateTime(new Date(), TimeZone.getTimeZone("UTC"));
		task2.setDue(date);
		task2.setNotes(task.getComments());
		task2.setTitle(task.getDescription());
		task2.setUpdated(updated);
		
		return task2;
	}
	
	/**
	 * 
	 * @param task
	 * @param taskList
	 * @return
	 * @throws IOException
	 */
	public static Task addTask(Task task, TaskList taskList,com.google.api.services.tasks.Tasks client) throws IOException{
		Task result = client.tasks().insert(taskList.getId() , task).execute();
		return result;
	}
	
	/**
	 * 
	 * @param task
	 * @param taskList
	 * @return
	 * @throws IOException
	 */
	public static Task removeTask(Task task, TaskList taskList,com.google.api.services.tasks.Tasks client) throws IOException{
		client.tasks().delete(taskList.getId(),task.getId()).execute();
		return task;
	}
	
	/**
	 * 
	 * @param task
	 * @param taskList
	 * @return
	 * @throws IOException
	 */
	public static Task updateTask(Task task, TaskList taskList, com.google.api.services.tasks.Tasks client) throws IOException{
		Task result = client.tasks().update(taskList.getId(), task.getId(), task).execute();
		return result;
	}
	
	
	//------------------------------------------- UTILS

	public static TaskList getTaskList(com.google.api.services.tasks.Tasks client,String id) throws IOException{
		
		return client.tasklists().get(id).execute();
	}
	
	/**
	 * 
	 * @throws IOException
	 */
	public static void synchronize(com.google.api.services.tasks.Tasks client) throws IOException{
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String username=AonUtil.getAuthPrincipal().getShortName();
		Domain domain = AON.getDomain(domainName, domainId, username);		
		User user = new User().setLogin(username);

		LinkedList<com.esferalia.aon.occam.api.model.OldTask> taskBDList = DBTask.getTask(domain, user);
		TaskList taskList = null;
		for(int i= 0; i<taskBDList.size();i++){
			Project project = DBTask.getProjectTask(domain, user, taskBDList.get(i));
			com.esferalia.aon.occam.api.model.OldTask taskBD=taskBDList.get(i);
			if (taskBD.getGtasklistId() == null){
				taskList = addTaskList(newTaskList(project),client);
				
				System.out.println("------------------new TaskList ----------------");
				System.out.println("ID: "+taskList.getId());
				System.out.println("Title: "+taskList.getTitle());
				DBTask.addTaskListId(domain, user, taskList.getId(),taskBD.getId());
			}
			else{
				taskList = getTaskList(client,taskBD.getGtasklistId());
			}
			
			if(taskBD.getGtaskId()== null){
				Task t=addTask(newTask(taskBD),taskList,client);

				System.out.println("------------------new Task ----------------");
				System.out.println("ID: "+t.getId());
				System.out.println("Title: "+t.getTitle());
				DBTask.addTaskId(domain, user, t.getId(), taskBD.getId());
				System.out.println("holaa");
			}
			else{
				updateTask(newTask(taskBD), taskList,client);
			}
			
			System.out.println("holaaa");
		}
	}
	
	
}
