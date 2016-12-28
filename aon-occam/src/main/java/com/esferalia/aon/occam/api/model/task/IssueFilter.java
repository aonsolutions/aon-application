package com.esferalia.aon.occam.api.model.task;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

@SuppressWarnings("serial")
public class IssueFilter implements Serializable, Cloneable{
	String title;
	
	String milestone;
	String state;
	String creator;
	String mentioned;
	String labels;
	String sort;
	String direction;
	String since;
	
	String priority;
	String type;
	String enterprise;
	
	String dateDiff;
	Date from;
	Date to;
	
	String mine;
	
	Integer page = 1;
	Integer perPage = 30;
	
	
	LinkedList<Integer> assignee;
	LinkedList<Integer> workgroup;
	
	public IssueFilter() {
		this.page = 1;
		this.perPage = 30;
	}
	
	public String getMilestone() {
		return milestone;
	}
	public IssueFilter setMilestone(String milestone) {
		this.milestone = milestone;
		return this;
	}
	public String getState() {
		return state;
	}
	public IssueFilter setState(String state) {
		this.state = state;
		return this;
	}
	public LinkedList<Integer> getAssignee() {
		return assignee;
	}
	public IssueFilter setAssignee(LinkedList<Integer> assignee) {
		this.assignee = assignee;
		return this;
	}
	public String getCreator() {
		return creator;
	}
	public IssueFilter setCreator(String creator) {
		this.creator = creator;
		return this;
	}
	public String getMentioned() {
		return mentioned;
	}
	public IssueFilter setMentioned(String mentioned) {
		this.mentioned = mentioned;
		return this;
	}
	public String getLabels() {
		return labels;
	}
	public IssueFilter setLabels(String labels) {
		this.labels = labels;
		return this;
	}
	public String getSort() {
		return sort;
	}
	public IssueFilter setSort(String sort) {
		this.sort = sort;
		return this;
	}
	public String getDirection() {
		return direction;
	}
	public IssueFilter setDirection(String direction) {
		this.direction = direction;
		return this;
	}
	public String getSince() {
		return since;
	}
	public IssueFilter setSince(String since) {
		this.since = since;
		return this;
	}
	public String getTitle() {
		return title;
	}
	public IssueFilter setTitle(String title) {
		this.title = title;
		return this;
	}
	public Integer getPage() {
		return page;
	}
	public IssueFilter setPage(Integer page) {
		this.page = page;
		return this;
	}
	public Integer getPerPage() {
		return perPage;
	}
	public IssueFilter setPerPage(Integer perPage) {
		this.perPage = perPage;
		return this;
	}

	public String getPriority() {
		return priority;
	}

	public IssueFilter setPriority(String priority) {
		this.priority = priority;
		return this;
	}
	
	public String getType() {
		return type;
	}

	public IssueFilter setType(String type) {
		this.type = type;
		return this;
	}
	
	public String getEnterprise() {
		return enterprise;
	}

	public IssueFilter setEnterprise(String enterprise) {
		this.enterprise = enterprise;
		return this;
	}
	
	public String getDateDiff() {
		return dateDiff;
	}

	public IssueFilter setDateDiff(String dateDiff) {
		this.dateDiff = dateDiff;
		return this;
	}

	public LinkedList<Integer> getWorkgroup() {
		return workgroup;
	}

	public IssueFilter setWorkgroup(LinkedList<Integer> workgroup) {
		this.workgroup = workgroup;
		return this;
	}
	
	public String getMine() {
		return mine;
	}

	public IssueFilter setMine(String mine) {
		this.mine = mine;
		return this;
	}

	public Date getFrom() {
		return from;
	}

	public IssueFilter setFrom(Date from) {
		this.from = from;
		return this;
	}

	public Date getTo() {
		return to;
	}

	public IssueFilter setTo(Date to) {
		this.to = to;
		return this;
	}
	
}
