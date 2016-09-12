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
import com.esferalia.aon.occam.api.model.type.AonUrlApi;


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
	private String createdAt;
	private String updatedAt;
	private User assignee;
	private User user;
	private LinkedList<Label> labels;
	private Integer comments;
	private Label type;
	private Label priority;
	
	private User workgroup;
	
	public Issue() {
	
	}
	
	public Issue(Task task, com.esferalia.aon.occam.api.model.security.User creator, Registry assignee, LinkedList<Label> labels,
			Label type, Label priority, Integer comments, Domain domain, String userName, Workgroup workgroup) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		this.id = task.getId();
		this.title = task.getDescription();
		this.url = AonUrlApi.AONTEST.getUrl() + "repos/" + userName + "/" + domain.getName() + "/issues/"+task.getNumber();
		this.repositoryUrl = AonUrlApi.AONTEST.getUrl() + "repos/" + userName + "/" + domain.getName();
		this.number =  task.getNumber();
		this.labelsUrl = AonUrlApi.AONTEST.getUrl() + "repos/" + userName + "/" + domain.getName() + "/issues/"+task.getNumber() + "/labels";
		this.commentsUrl = AonUrlApi.AONTEST.getUrl() + "repos/" + userName + "/" + domain.getName() + "/issues/"+task.getNumber() + "/comments";
		this.eventsUrl = AonUrlApi.AONTEST.getUrl() + "repos/" + userName + "/" + domain.getName() + "/issues/"+task.getNumber() + "/events";
		this.state = TaskStatus.values()[task.getStatus()].getName();
		this.body = task.getComments();
		this.closedAt = task.getEndDate() != null ? dateFormat.format(task.getEndDate()) : "";
		this.createdAt = dateFormat.format(task.getStartDate());
		this.updatedAt = task.getUpdateDate() != null ? dateFormat.format(task.getUpdateDate()): "";
		this.assignee = new User().setId(task.getRegistry()).setLogin(assignee.getName());
		this.user = new User().setId(task.getTaskHolder()).setLogin(creator.getName());
		this.labels = labels;
		this.comments = comments;
		this.type = type;
		this.priority = priority;
		this.workgroup = new User().setId(workgroup.getId()).setLogin(workgroup.getDescription());
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
		json.put("created_at", getCreatedAt());
		json.put("updated_at", getUpdatedAt());
		json.put("assignee", getAssignee().toJSON());		
		json.put("user", getUser().toJSON());

		JSONArray arrayLabels = new JSONArray();
		getLabels().stream().map(label -> label.toJSON()).forEach(s -> arrayLabels.put(s));
		json.put("labels",arrayLabels);

		json.put("comments", getComments());
		json.put("type", getType().toJSON());
		json.put("priority", getPriority().toJSON());
		
		json.put("workgroup",getWorkgroup().toJSON());
				
		return json;
	}	
}
