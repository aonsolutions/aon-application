package com.code.aon.webservice.github;
import java.io.IOException;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.code.aon.webservice.issues.DBConsults;
import com.code.aon.webservice.issues.Utils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.task.TaskSource;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.task.TaskTag;
import com.esferalia.aon.occam.api.model.type.TagType;

@SuppressWarnings("serial")
@WebServlet(name = "GithubServlet", urlPatterns = { "/github/*" })
public class GithubServlet extends HttpServlet{
	
	private static final DBConsults DB = DBConsults.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST METHOD");
		
		String line = "";
		String s = "";
		while((line = req.getReader().readLine()) != null)
			s = s + " " + line;
		s = Utils.checkString(s);
		if(s == null || s.equals("")) s = "{}";
		JSONObject json = new JSONObject(s);
		JSONObject sender = new JSONObject(json.get("sender"));
		JSONObject issue = new JSONObject(json.get("issue"));
		
		String login = sender.getString("sender");
		String domainName = req.getServerName();
		
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));
		if(GithubAction.ASSIGNED.getName().equals(json.get("action"))){
			JSONObject assignee = new JSONObject(json.get("assignee"));
			assigned(domain, login, issue, assignee);
		}
		else if(GithubAction.UNASSIGNED.getName().equals(json.get("action"))){
			JSONObject assignee = new JSONObject(json.get("assignee"));
			unassigned(domain, login, issue, assignee);
		}
		else if(GithubAction.OPENED.getName().equals(json.get("action")))
			opened(domain, login, issue);
		else if(GithubAction.REOPENED.getName().equals(json.get("action")))
			reopened(domain, login, issue);
		else if(GithubAction.CLOSED.getName().equals(json.get("action")))
			closed(domain, login, issue);
		else if(GithubAction.COMMENT_CREATED.getName().equals(json.get("action"))){
			JSONObject comment = new JSONObject(json.get("comment"));
			created(domain, login, issue, comment);
		}
		else if(GithubAction.COMMENT_DELETED.getName().equals(json.get("action"))){
			JSONObject comment = new JSONObject(json.get("comment"));
			deleted(domain, login, issue, comment);
		}
		else if(GithubAction.COMMENT_EDITED.getName().equals(json.get("action"))){
			JSONObject comment = new JSONObject(json.get("comment"));
			edited(domain, login, issue, comment);
		}
		else if(GithubAction.LABELED.getName().equals(json.get("action"))){
			JSONObject label = new JSONObject(json.get("label"));
			labeled(domain, login, issue, label);
		}
		else if(GithubAction.UNLABELED.getName().equals(json.get("action"))){
			JSONObject label = new JSONObject(json.get("label"));
			unlabeled(domain, login, issue, label);
		}
	}
	
	private void assigned(Domain domain, String login, JSONObject issue, JSONObject assignee) {
		Task task = AON.getTask(domain.getName(), domain.getId(), login, 
				f -> f.getSourceProperty().eq(TaskSource.GITHUB.value())
				.and(f.getSourceIdProperty().eq(issue.getInt("number")))
				.and(f.getDomainProperty().eq(domain.getId())))
			.setModificationUser(login).setModificationDate(Calendar.getInstance().getTime());
		Registry user = AON.getTaskMember(domain.getName(), domain.getId(), login, assignee.getString("login"));
		if(user.getId() != null)
			AON.updateTaskUser(domain.getName(), domain.getId(), login, task.setTaskHolder(user.getId()));
	}
	
	private void unassigned(Domain domain, String login, JSONObject issue, JSONObject assignee) {
		Task task = AON.getTask(domain.getName(), domain.getId(), login, 
				f -> f.getSourceProperty().eq(TaskSource.GITHUB.value())
				.and(f.getSourceIdProperty().eq(issue.getInt("number")))
				.and(f.getDomainProperty().eq(domain.getId())))
			.setModificationUser(login).setModificationDate(Calendar.getInstance().getTime());
		Registry user = AON.getTaskMember(domain.getName(), domain.getId(), login, assignee.getString("login"));
		if(user.getId() != null && user.getId().equals(task.getTaskHolder()))
			AON.updateTaskUser(domain.getName(), domain.getId(), login, task.setTaskHolder(null));
	}
	
	private void opened(Domain domain, String login, JSONObject issue) {
		Integer num = AON.getLastTaskNumber(domain.getName(), domain.getId(),login) != null ?
				AON.getLastTaskNumber(domain.getName(), domain.getId(),login) : 0;
		
		Task task = new Task()
			.setDescription(issue.getString("title"))
			.setComments(issue.getString("body"))
			.setDomain(domain.getId())
			.setNumber(num + 1)
			.setStartDate(Calendar.getInstance().getTime())
			.setDueDate(Calendar.getInstance().getTime())
			.setStatus(TaskStatus.PENDING.value())
			.setRegistry(null) 
			.setPercent((byte) 0) 
			.setPriority((byte) 0)
			.setSource(TaskSource.GITHUB.value())
			.setSourceId(issue.getInt("number"))
			.setCreationUser(login)
			.setCreationDate(Calendar.getInstance().getTime())
			.setModificationUser(login)
			.setModificationDate(Calendar.getInstance().getTime())
			;
		
		AON.createTask(domain.getName(), domain.getId(), login, task);
	}
	
	private void reopened(Domain domain, String login, JSONObject issue) {
		Task task = AON.getTask(domain.getName(), domain.getId(), login, 
				f -> f.getSourceProperty().eq(TaskSource.GITHUB.value())
				.and(f.getSourceIdProperty().eq(issue.getInt("number")))
				.and(f.getDomainProperty().eq(domain.getId())));
		task = task.setStatus(TaskStatus.PENDING.value()).setEndDate(null).setId(task.getId())
				.setModificationDate(Calendar.getInstance().getTime()).setModificationUser(login);
		if(!DB.isPrincipal(domain, login, task))
			task.setParent(null);
		else task.setParent(task.getId());
		AON.updateTaskParent(domain.getName(), domain.getId(), login, task);
		TaskEvent taskEvent = new TaskEvent().setCreationDate(Calendar.getInstance().getTime()).setDomain(domain.getId())
			.setEvent("reopened").setTask(task.getId()).setCreationUser(login);
		AON.createTaskEvent(domain.getName(), domain.getId(), login, taskEvent, task.getId());

	}
	
	private void closed(Domain domain, String login, JSONObject issue) {
		Task task = AON.getTask(domain.getName(), domain.getId(), login, 
				f -> f.getSourceProperty().eq(TaskSource.GITHUB.value())
				.and(f.getSourceIdProperty().eq(issue.getInt("number")))
				.and(f.getDomainProperty().eq(domain.getId())));
		task = task.setStatus(TaskStatus.FINISHED.value()).setEndDate(Calendar.getInstance().getTime()).setId(task.getId())
				.setModificationDate(Calendar.getInstance().getTime()).setModificationUser(login);
		TaskEvent taskEvent = new TaskEvent().setCreationDate(Calendar.getInstance().getTime()).setDomain(domain.getId())
				.setEvent("closed").setTask(task.getId()).setCreationUser(login);
		AON.createTaskEvent(domain.getName(), domain.getId(), login, taskEvent, task.getId());
	}
	
	private void created(Domain domain, String login, JSONObject issue, JSONObject comment) {
		Task task = AON.getTask(domain.getName(), domain.getId(), login, 
				f -> f.getSourceProperty().eq(TaskSource.GITHUB.value())
				.and(f.getSourceIdProperty().eq(issue.getInt("number")))
				.and(f.getDomainProperty().eq(domain.getId())));
		
		TaskComment tc = new TaskComment().setComment(comment.getString("body"))
				.setModificationDate(Calendar.getInstance().getTime())
				.setCreationDate(Calendar.getInstance().getTime())
				.setDomain(domain.getId())
				.setTask(task.getId())
				.setCreationUser(login)
				.setModificationUser(login);
		AON.createTaskComment(domain.getName(), domain.getId(), login, tc, task.getId());	
	}
	
	private void deleted(Domain domain, String login, JSONObject issue, JSONObject comment) {
		AON.deleteTaskComment(domain.getName(), domain.getId(), login, 
			f -> f.getSourceProperty().eq(TaskSource.GITHUB.ordinal())
			.and(f.getSourceIdProperty().eq(comment.getInt("id")))
			.and(f.getDomainProperty().eq(domain.getId())));
	}
	
	private void edited(Domain domain, String login, JSONObject issue, JSONObject comment) {
		TaskComment tc = AON.getTaskComment(domain.getName(), domain.getId(), login,
				f -> f.getSourceProperty().eq(TaskSource.GITHUB.ordinal())
				.and(f.getSourceIdProperty().eq(comment.getInt("id")))
				.and(f.getDomainProperty().eq(domain.getId())))
			.setComment(comment.getString("body")).setModificationDate(Calendar.getInstance().getTime())
			.setModificationUser(login);
		AON.updateTaskComment(domain.getName(), domain.getId(), login, tc);
	}
	
	private void labeled(Domain domain, String login, JSONObject issue, JSONObject label) {
		Task task = AON.getTask(domain.getName(), domain.getId(), login, 
				f -> f.getSourceProperty().eq(TaskSource.GITHUB.value())
				.and(f.getSourceIdProperty().eq(issue.getInt("number")))
				.and(f.getDomainProperty().eq(domain.getId())));
		Tag tag = AON.getTag(domain.getName(), domain.getId(), login, 
				f -> f.getTypeProperty().eq(TagType.TASK_LABEL.value())
				.and(f.getDomainProperty().eq(domain.getId()))
				.and(f.getNameProperty().eq(label.getString("name"))));
		if(tag.getId() == null){
			Tag t = new Tag().setColor(label.getString("color"))
					.setDomain(domain.getId())
					.setName(label.getString("name"))
					.setType(TagType.TASK_LABEL.value());
			tag = AON.insertTag(domain.getName(), domain.getId(), login, t);
		}
		TaskTag taskTag = new TaskTag().setDomain(domain.getId()).setTask(task.getId()).setTag(tag.getId());
		AON.createTaskTag(domain.getName(), domain.getId(), login, taskTag);
	}
	
	private void unlabeled(Domain domain, String login, JSONObject issue, JSONObject label) {
		Task task = AON.getTask(domain.getName(), domain.getId(), login, 
				f -> f.getSourceProperty().eq(TaskSource.GITHUB.value())
				.and(f.getSourceIdProperty().eq(issue.getInt("number")))
				.and(f.getDomainProperty().eq(domain.getId())));
		Tag tag = AON.getTag(domain.getName(), domain.getId(), login, 
				f -> f.getTypeProperty().eq(TagType.TASK_LABEL.value())
				.and(f.getDomainProperty().eq(domain.getId()))
				.and(f.getNameProperty().eq(label.getString("name"))));
		AON.deleteTaskTag(domain.getName(), domain.getId(), login, 
				f -> f.getTagProperty().eq(tag.getId()).and(f.getTaskProperty().eq(task.getId())));
	}
}
