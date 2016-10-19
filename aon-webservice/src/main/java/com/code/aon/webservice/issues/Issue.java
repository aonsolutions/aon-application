package com.code.aon.webservice.issues;

import java.text.SimpleDateFormat;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.type.Priority;


public class Issue {
	
	private Integer id;
	private String title;
	private String url;
	private String repositoryUrl;
	private String labelsUrl;
	private String commentsUrl;
	private String eventsUrl;
	private Integer number;
	private String state;
	private String body;
	
	private String closedAt;
	private String closedAtDate;
	private String closedAtHour;
	
	private String createdAt;
	private String createdAtDate;
	private String createdAtHour;
	
	private String updatedAt;
	private String updatedAtDate;
	private String updatedAtHour;
	
	private User assignee;
	private User user;
	private LinkedList<Label> labels;
	private Integer comments;
	private Label type;
	private Label priority;
	
	private User workgroup;
	
	private User enterprise; 
	
	public Issue() {
	
	}
	
	public Issue(Task task, Registry assignee, LinkedList<Label> labels,
			Label type, Integer comments, Domain domain, String userName, Workgroup workgroup, Registry enterprise) {
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");

		String title = "";
		String[] str = task.getDescription().split(" ");
		for(Integer i = 0; i < str.length; i++){
			String s = str[i];
			while(s.length()>30){
				title = title + s.substring(0, 29)+ " ";
				s = s.substring(30);
			}
			title = title +  s + " ";
		}
		
		String url = "http://"+domain.getName()+ "/";
		//url = url + "aon-aio/";
		this.id = task.getId();
		this.title = title;
		this.url = url + "repos/" + userName + "/" + domain.getName() + "/issues/"+task.getNumber();
		this.repositoryUrl = url + "repos/" + userName + "/" + domain.getName();
		this.number =  task.getNumber();
		this.labelsUrl = url + "repos/" + userName + "/" + domain.getName() + "/issues/"+task.getNumber() + "/labels";
		this.commentsUrl = url + "repos/" + userName + "/" + domain.getName() + "/issues/"+task.getNumber() + "/comments";
		this.eventsUrl = url + "repos/" + userName + "/" + domain.getName() + "/issues/"+task.getNumber() + "/events";
		this.state = TaskStatus.values()[task.getStatus()].getGwtName();
		this.body = task.getComments();
		this.closedAt = task.getEndDate() != null ? dateTimeFormat.format(task.getEndDate()) : "";
		this.closedAtDate = task.getEndDate() != null ? dateFormat.format(task.getEndDate()) : "";
		this.closedAtHour = task.getEndDate() != null ? hourFormat.format(task.getEndDate()) : "";
		this.createdAt = dateTimeFormat.format(task.getStartDate());
		this.createdAtDate = dateFormat.format(task.getStartDate());
		this.createdAtHour = hourFormat.format(task.getStartDate());
		this.updatedAt = task.getModificationDate() != null ? dateTimeFormat.format(task.getModificationDate()): "";
		this.updatedAtDate = task.getModificationDate() != null ? dateFormat.format(task.getModificationDate()): "";
		this.updatedAtHour = task.getModificationDate() != null ? hourFormat.format(task.getModificationDate()): "";
		this.assignee = new User().setId(assignee.getId()).setLogin(assignee.getName());
		this.user = new User().setLogin(task.getCreationUser());
		this.labels = labels;
		this.comments = comments;
		this.type = type;
		Priority p = Priority.values()[task.getPriority()];
		this.priority = new Label().setId(p.ordinal()).setName(p.getName()).setColor(p.getColor().getColor());
		this.workgroup = new User().setId(workgroup.getId()).setLogin(workgroup.getDescription());
		this.enterprise = new User().setId(enterprise.getId()).setLogin(enterprise.getName());
	}
	
	public Integer getId() {
		return id;
	}
	public Issue setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getTitle() {
		return title;
	}
	public Issue setTitle(String title) {
		this.title = title;
		return this;
	}
	public String getUrl() {
		return url;
	}
	public Issue setUrl(String url) {
		this.url = url;
		return this;
	}
	public String getRepositoryUrl() {
		return repositoryUrl;
	}
	public Issue setRepositoryUrl(String repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
		return this;
	}
	public String getLabelsUrl() {
		return labelsUrl;
	}
	public Issue setLabelsUrl(String labelsUrl) {
		this.labelsUrl = labelsUrl;
		return this;
	}
	public String getCommentsUrl() {
		return commentsUrl;
	}
	public Issue setCommentsUrl(String commentsUrl) {
		this.commentsUrl = commentsUrl;
		return this;
	}
	public String getEventsUrl() {
		return eventsUrl;
	}
	public Issue setEventsUrl(String eventsUrl) {
		this.eventsUrl = eventsUrl;
		return this;
	}
	public Integer getNumber() {
		return number;
	}
	public Issue setNumber(Integer number) {
		this.number = number;
		return this;
	}
	public String getState() {
		return state;
	}
	public Issue setState(String state) {
		this.state = state;
		return this;
	}
	public String getBody() {
		return body;
	}
	public Issue setBody(String body) {
		this.body = body;
		return this;
	}
	public String getClosedAt() {
		return closedAt;
	}
	public Issue setClosedAt(String closedAt) {
		this.closedAt = closedAt;
		return this;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public Issue setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
		return this;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public Issue setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
		return this;
	}
	public User getAssignee() {
		return assignee;
	}
	public Issue setAssignee(User assignee) {
		this.assignee = assignee;
		return this;
	}
	public User getUser() {
		return user;
	}
	public Issue setUser(User user) {
		this.user = user;
	return this;
	}
	public LinkedList<Label> getLabels() {
		return labels;
	}
	public Issue setLabels(LinkedList<Label> labels) {
		this.labels = labels;
		return this;
	}
	public Integer getComments() {
		return comments;
	}
	public Issue setComments(Integer comments) {
		this.comments = comments;
		return this;
	}
	public Label getType() {
		return type;
	}
	public Issue setType(Label type) {
		this.type = type;
		return this;
	}
	public Label getPriority() {
		return priority;
	}
	public Issue setPriority(Label priority) {
		this.priority = priority;
		return this;
	}
	
	public User getWorkgroup() {
		return workgroup;
	}

	public Issue setWorkgroup(User workgroup) {
		this.workgroup = workgroup;
		return this;
	}

	public User getEnterprise() {
		return enterprise;
	}

	public Issue setEnterprise(User enterprise) {
		this.enterprise = enterprise;
		return this;
	}

	public String getClosedAtDate() {
		return closedAtDate;
	}

	public Issue setClosedAtDate(String closedAtDate) {
		this.closedAtDate = closedAtDate;
		return this;
	}

	public String getClosedAtHour() {
		return closedAtHour;
	}

	public Issue setClosedAtHour(String closedAtHour) {
		this.closedAtHour = closedAtHour;
		return this;
	}

	public String getCreatedAtDate() {
		return createdAtDate;
	}

	public Issue setCreatedAtDate(String createdAtDate) {
		this.createdAtDate = createdAtDate;
		return this;
	}

	public String getCreatedAtHour() {
		return createdAtHour;
	}

	public Issue setCreatedAtHour(String createdAtHour) {
		this.createdAtHour = createdAtHour;
		return this;
	}

	public String getUpdatedAtDate() {
		return updatedAtDate;
	}

	public Issue setUpdatedAtDate(String updatedAtDate) {
		this.updatedAtDate = updatedAtDate;
		return this;
	}

	public String getUpdatedAtHour() {
		return updatedAtHour;
	}

	public Issue setUpdatedAtHour(String updatedAtHour) {
		this.updatedAtHour = updatedAtHour;
		return this;
	}
	
	public Boolean isClosed() {
		return getState().equals(TaskStatus.FINISHED.getGwtName());
	}
	
	public Boolean isOpen() {
		return getState().equals(TaskStatus.IN_PROGRESS.getGwtName())
			|| getState().equals(TaskStatus.PENDING.getGwtName());
	}

	public Boolean isDeleted() {
		return getState().equals(TaskStatus.DELETED.getGwtName());
	}
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		//if(getNumber() == null) return json;
		json.put("id", getId());
		json.put("title", getTitle());
		json.put("url", getUrl());
		json.put("repository_url", getRepositoryUrl());
		json.put("labels_url", getLabelsUrl());
		json.put("comments_url", getCommentsUrl());
		json.put("events_url", getEventsUrl());
		json.put("number", getNumber());
		json.put("state", getState());
		json.put("body", getBody());
		json.put("closed_at", getClosedAt());
		json.put("closed_at_date", getClosedAtDate());
		json.put("closed_at_hour", getClosedAtHour());
		json.put("created_at", getCreatedAt());
		json.put("created_at_date", getCreatedAtDate());
		json.put("created_at_hour", getCreatedAtHour());
		json.put("updated_at", getUpdatedAt());
		json.put("updated_at_date", getUpdatedAtDate());
		json.put("updated_at_hour", getUpdatedAtHour());
		json.put("assignee", getAssignee().toJSON());		
		json.put("user", getUser().toJSON());

		JSONArray arrayLabels = new JSONArray();
		getLabels().stream().map(label -> label.toJSON()).forEach(s -> arrayLabels.put(s));
		json.put("labels",arrayLabels);

		json.put("comments", getComments());
		json.put("type", getType().toJSON());
		json.put("priority", getPriority().toJSON());
		
		json.put("workgroup",getWorkgroup().toJSON());
		json.put("enterprise", getEnterprise().toJSON());
				
		json.put("is_deleted", isDeleted());
		json.put("is_open", isOpen());
		json.put("is_closed", isClosed());
		
		return json;
	}	
}
