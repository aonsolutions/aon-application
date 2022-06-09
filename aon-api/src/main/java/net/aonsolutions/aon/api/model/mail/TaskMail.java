package net.aonsolutions.aon.api.model.mail;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.task.TaskWorkflow;
import com.esferalia.aon.occam.api.model.task.TaskWorkflowType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class TaskMail {
	String number;
	String date;
	String url; 
	String title;
	String logo;
	String description;
	String companyName;
	String domainName;
	String contact;
	String taskHolderName;
	String note;
	Boolean showEvaluation;
	TaskWorkflowType type;
	List<TaskWorkflow> workflows;
	
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
		this.date = AonDateUtils.format(date, "dd/MM/yyyy HH:mm");
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
	
	public TaskMail setType(TaskWorkflowType type) {
		this.type = type;
		return this;
	}
	
	public String getTypeText() {
		String txt = "";
		switch (type) {
			case OPEN:
				txt = "Abierto";
			break;
			case CLOSE:
				txt = "Cerrado";
			break;
			default:
			break;
		}
		return txt;
	}
	
	public String getDescription() {
		return description;
	}

	public TaskMail setDescription(String description) {
		this.description = description;
		return this;
	}
	
	public String getCompanyName() {
		return companyName;
	}

	public TaskMail setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}
	
	public TaskMail setNote(String note) {
		this.note = note;
		return this;
	}
	
	public String getNote() {
		return note;
	}

	public String getDomainName() {
		return domainName;
	}

	public TaskMail setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	public String getContact() {
		return contact;
	}

	public TaskMail setContact(String contact) {
		this.contact = contact;
		return this;
	}
	
	public String getTaskHolderName() {
		return taskHolderName;
	}

	public TaskMail setTaskHolderName(String taskHolderName) {
		this.taskHolderName = taskHolderName;
		return this;
	}
	
	public Boolean getShowEvaluation() {
		return showEvaluation!=null && showEvaluation;
	}

	public TaskMail setShowEvaluation(Boolean showEvaluation) {
		this.showEvaluation = showEvaluation;
		return this;
	}
	
	public TaskMail setWorkflows(List<TaskWorkflow> workflows) {
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
	
	public List<WorkflowMail> getWorkflows() {
		LinkedList<WorkflowMail> workflowsList = new LinkedList<>();
		
		for(TaskWorkflow workflow: workflows) {
			WorkflowMail wemail = new WorkflowMail();
			wemail.setName(workflow.getTaskHolder().getId()!=null ? workflow.getTaskHolder().getName() : workflow.getEmail());
			wemail.setDate(workflow.getModificationDate()!=null ? workflow.getModificationDate() : workflow.getCreationDate() );
			wemail.setMessage(workflow.getComment());
			workflowsList.add(wemail);
		}
	
		return workflowsList;
	}

}
