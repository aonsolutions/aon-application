package com.code.aon.webservice.issues;

import java.util.LinkedList;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter.TaskEventFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskFilter;
import com.esferalia.aon.occam.api.model.Filter.TaskTagFilter;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Signature;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.office.NotificationInfo;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.task.TaskComment;
import com.esferalia.aon.occam.api.model.task.TaskEvent;
import com.esferalia.aon.occam.api.model.task.TaskStatus;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.TagType;

public class DBConsults {
	
	public static DBConsults getInstance() {
		return new DBConsults();
	}
	
	public Domain getDomain(String domainName, String login){
		return AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));
	}
	
	public Domain getDomain(Domain domain, String login){
		return AON.getDomain(domain.getName(), domain.getId(), login);
	}
	
	//-------------------- TASK
	
	public Integer getTaskId(Domain domain, String login, Integer number){
		return AON.getTask(domain.getName(), domain.getId(), login, f-> f.getNumberProperty().eq(number)
				.and(f.getDomainProperty().eq(domain.getId()))).getId();
	}
	
	public Task getTask(Domain domain, String login, Integer id){
		return AON.getTask(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id));
	}
	
	public Task getTaskWithNumber(Domain domain, String login, Integer number){
		return AON.getTask(domain.getName(), domain.getId(), login, f-> f.getNumberProperty().eq(number)
				.and(f.getDomainProperty().eq(domain.getId())));
	}
	
	public Stream<Task> getTaskStream(Domain domain, String login, IssueFilter filter){
		return AON.getTaskStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()), filter);
	}
	
	public Stream<Task> getFaqTaskStream(Domain domain, String login,IssueFilter filter){
		return AON.getTaskStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getStatusProperty().eq(TaskStatus.FAQ.value())).and(f.getParentProperty().isNull()), filter);
	}
	
	public Stream<Task> getTaskStream(Domain domain, String login, IssueFilter filter, TaskFilter f){
		return AON.getTaskStream(domain.getName(), domain.getId(), login, f, filter);
	}
	
	public Stream<Task> getLightTaskStream(Domain domain, String login, IssueFilter filter, Integer act){
		return AON.getTaskStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getIdProperty().ne(act)), filter);
	}

	public Stream<Task> getDuplicateTaskStream(Domain domain, String login, Integer parent){
		return AON.getDuplicateTaskStream(domain.getName(), domain.getId(), login, parent);
	}
	
	public LinkedList<Task> getTaskList(Domain domain, String login, IssueFilter filter){
		return AON.getTaskList(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()), filter);
	}
	
	public void updateTaskDescription(Domain domain, String login, Task task){
		AON.updateTaskDescription(domain.getName(), domain.getId(),login, task);
	}
	
	public void updateTaskPriority(Domain domain, String login, Task task){
		AON.updateTaskPriority(domain.getName(), domain.getId(),login, task);
	}
	
	public Boolean isPrincipal(Domain domain, String login, Task task){
		if(task.getParent() != null && task.getParent().equals(task.getId()))
			return true;
		if(task.getParent() != null && !task.getId().equals(task.getParent()))
			return AON.isTaskParent(domain.getName(), domain.getId(), login, task.getId());
		return false;
	}
	
	//-------------------- TAG

	public Integer getTagId(Domain domain, String login, String name, TagType tagType){
		return AON.getTag(domain.getName(), domain.getId(), login, f-> f.getNameProperty().eq(name).and(f.getDomainProperty().eq(domain.getId()))
				.and(f.getTypeProperty().eq(tagType.value()))).getId();
	}
	
	public Tag getTag(Domain domain, String login, String name, TagType tagType){
		return AON.getTag(domain.getName(), domain.getId(), login, f-> f.getNameProperty().eq(name).and(f.getDomainProperty().eq(domain.getId()))
				.and(f.getTypeProperty().eq(tagType.value())));
	}
	
	public void deleteTag(Domain domain, String login, Tag tag){
		AON.deleteTag(domain.getName(), domain.getId(), login, tag); 
	}
	
	//-------------------- TASK_TAG

	public Stream<Tag> getTaskLabelStream(Domain domain, String login, TaskTagFilter filter){
		return AON.getTaskLabelStream(domain.getName(), domain.getId(), login, filter);
	}
	
	public void deleteTaskTag(Domain domain, String login, TaskTagFilter filter){
		AON.deleteTaskTag(domain.getName(), domain.getId(), login, filter);
	}
	
	//-------------------- USER
	
	public User getUser(Domain domain, String login){
		return AON.getUser(domain.getName(), domain.getId(), login);
	}
	
	//-------------------- TASK_EVENT

	public TaskEvent getLastTaskEvent(Domain domain, String login, Integer taskId){
		return AON.getLastTaskEvent(domain.getName(), domain.getId(), login, f -> f.getTaskProperty().eq(taskId));
	}
	
	public TaskEvent getLastTaskEvent(Domain domain, String login, TaskEventFilter filter){
		return AON.getLastTaskEvent(domain.getName(), domain.getId(), login, filter);
	}
	
	public TaskEvent getTaskEvent(Domain domain, String login, Integer taskEventId){
		return AON.getTaskEvent(domain.getName(), domain.getId(), login, taskEventId);
	}
	
	public Stream<TaskEvent> getTaskEventStream(Domain domain, String login, Integer taskId){
		return AON.getTaskEventStream(domain.getName(), domain.getId(), login, taskId);
	}
	
	public LinkedList<TaskEvent> getTaskEventList(Domain domain, String login, Integer taskId){
		return AON.getTaskEventList(domain.getName(), domain.getId(), login, taskId);
	}
	
	//-------------------- TASK_COMMENT

	public TaskComment getLastTaskComment(Domain domain, String login, Integer taskId){
		return AON.getLastTaskComment(domain.getName(), domain.getId(), login, taskId);
	}
	
	public Stream<TaskComment> getTaskCommentStream(Domain domain, String login, Integer taskId){
		return AON.getTaskCommentStream(domain.getName(), domain.getId(), login, taskId);
	}
	
	public LinkedList<TaskComment> getTaskCommentList(Domain domain, String login, Integer taskId){
		return AON.getTaskCommentList(domain.getName(), domain.getId(), login, taskId);
	}

	//-------------------- NOTIFICATION INFO
	
	public NotificationInfo getNotificationInfo(Domain domain, String login){
		return AON.getNotificationInfo(domain.getName(), domain.getId(), login);
	}
	
	public void  insertNotificationInfo(Domain domain, String login, String data, AppParam appParam){
		AON.insertNotificationInfo(domain.getName(), domain.getId(), login, data, appParam);
	}

	public LinkedList<MailAccount> getMailAccountList(Domain domain, String login) {		
		if(domain.isEnableHeredity())
			return AON.getMailAccountList(domain.getName(), domain.getId(), login, f -> (f.getUserIdProperty().isNull())
				.and(f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId()))));
		else return AON.getMailAccountList(domain.getName(), domain.getId(), login, f -> (f.getUserIdProperty().isNull())
				.and(f.getDomainProperty().eq(domain.getId())));
	}
	
	public MailAccount getMailAccount(Domain domain, String login, Integer mailAccountId) {
		return AON.getMailAccount(domain.getName(), domain.getId(), login, 
				f -> f.getIdProperty().eq(mailAccountId));
	}
	
	public  String getSignature(Domain domain, String login, Integer signatureId){
		return AON.getSignature(domain.getName(), domain.getId(), login, signatureId).getSignature();
	}
	
	public  LinkedList<Signature> getSignatureList(Domain domain, String login){
		return AON.getSignatureList(domain.getName(), domain.getId(), login,
				f -> f.getUserIdProperty().isNull().and(f.getDomainProperty().eq(domain.getId())));
	}
	
	
}
