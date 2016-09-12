package com.code.aon.webservice.issues;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Task;
import com.esferalia.aon.occam.api.model.task.IssueFilter;
import com.esferalia.aon.occam.api.model.type.TagType;

public class DBConsults {
	
	public static LinkedList<Task> getTaskList(String domainName, Integer domainId, String login, IssueFilter filter){
		return AON.getTaskList(domainName, domainId, login, f -> f.getDomainProperty().eq(domainId), filter);
	}
	
	public static Task getTask(String domainName, Integer domainId, String login, Integer id){
		return AON.getTask(domainName, domainId, login, f -> f.getIdProperty().eq(id));
	}
	
	public static Task getTaskWithNumber(String domainName, Integer domainId, String login, Integer number){
		return AON.getTask(domainName, domainId, login, f-> f.getNumberProperty().eq(number).and(f.getDomainProperty().eq(domainId)));
	}
	
	public static Integer getTaskId(String domainName, Integer domainId, String login, Integer number){
		return AON.getTask(domainName, domainId, login, f-> f.getNumberProperty().eq(number).and(f.getDomainProperty().eq(domainId))).getId();
	}

	public static Integer getTagId(String domainName, Integer domainId, String login, String name, TagType tagType){
		return AON.getTag(domainName, domainId, login, f-> f.getNameProperty().eq(name).and(f.getDomainProperty().eq(domainId))
				.and(f.getTypeProperty().eq(tagType.value()))).getId();
	}
}
