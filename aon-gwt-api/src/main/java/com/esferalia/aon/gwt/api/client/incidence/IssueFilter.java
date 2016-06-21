package com.esferalia.aon.gwt.api.client.incidence;

public class IssueFilter {
	String title;
	
	String milestone;
	String state;
	String assignee;
	String creator;
	String mentioned;
	String labels;
	String sort;
	String direction;
	String since;
	
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
	public String getAssignee() {
		return assignee;
	}
	public IssueFilter setAssignee(String assignee) {
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
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
