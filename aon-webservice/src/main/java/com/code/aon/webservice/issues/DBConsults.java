package com.code.aon.webservice.issues;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.type.TagType;

public class DBConsults {
	public static DBConsults getInstance() {
		return new DBConsults();
	}
	
	public LinkedList<Task> getTaskList(Domain domain, String login, IssueFilter filter){
		return AON.getTaskList(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()), filter);
	}
	
	public Task getTask(Domain domain, String login, Integer id){
		return AON.getTask(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(id));
	}
	
	public Task getTaskWithNumber(Domain domain, String login, Integer number){
		return AON.getTask(domain.getName(), domain.getId(), login, f-> f.getNumberProperty().eq(number).and(f.getDomainProperty().eq(domain.getId())));
	}
	
	public Integer getTaskId(Domain domain, String login, Integer number){
		return AON.getTask(domain.getName(), domain.getId(), login, f-> f.getNumberProperty().eq(number).and(f.getDomainProperty().eq(domain.getId()))).getId();
	}

	public Integer getTagId(Domain domain, String login, String name, TagType tagType){
		return AON.getTag(domain.getName(), domain.getId(), login, f-> f.getNameProperty().eq(name).and(f.getDomainProperty().eq(domain.getId()))
				.and(f.getTypeProperty().eq(tagType.value()))).getId();
	}
	
	public Tag getTag(Domain domain, String login, String name, TagType tagType){
		return AON.getTag(domain.getName(), domain.getId(), login, f-> f.getNameProperty().eq(name).and(f.getDomainProperty().eq(domain.getId()))
				.and(f.getTypeProperty().eq(tagType.value())));
	}
	
	public User getUser(Domain domain, String login){
		return AON.getUser(domain.getName(), domain.getId(), login);
	}
	
	public void updateTaskDescription(Domain domain, String login, Task task){
		AON.updateTaskDescription(domain.getName(), domain.getId(),login, task);
	}
}
