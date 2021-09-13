package net.aonsolutions.aon.api.model.mail;

import java.util.LinkedList;
import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.watson.server.AonDateUtils;
import java.util.Date;

public class TaskMail {
	String number;
	String date;
	String url; 
	String title;
	String logo;
	LinkedList<TaskWorkflow> workflows;
	
	public TaskMail() {}

	public String getNumber() {
		return number;
	}

	public TaskMail setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getDate() {
		return date;
	}

	public TaskMail setDate(Date date) {
		this.date = AonDateUtils.format(date, "dd/MM/yyyy");
		return this;
	}

	public String getUrl() {
		return url;
	}

	public TaskMail setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public String getTitle() {
		return title;
	}

	public TaskMail setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public LinkedList<WorkflowMail> getWorkflows() {
		LinkedList<WorkflowMail> workflowsList = new LinkedList<WorkflowMail>();
		
		for(TaskWorkflow workflow: workflows) {
			WorkflowMail wemail = new WorkflowMail();
			wemail.setName(workflow.getTaskHolder().getName());
			wemail.setDate(workflow.getCreationDate());
			wemail.setMessage(workflow.getComment());
			workflowsList.add(wemail);
		}
	
		return workflowsList;
	}

	public TaskMail setWorkflows(LinkedList<TaskWorkflow> workflows) {
		this.workflows = workflows;
		return this;
	}
	
	public String getLogo() {
		return logo;
	}

	public TaskMail setLogo(String logo) {
		this.logo = logo;
		return this;
	}
}
