package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.project.ProjectActivity;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.occam.api.model.security.User;

public class MarketingActionTarget extends Target implements Serializable {
	
	private static final long serialVersionUID = -3274429313022170469L;
	
	private Integer actionTargetId;
	private Integer domain;
	private MarketingAction marketingAction;
	private Byte actionTargetStatus;
	
	private Project project;
	private ProjectActivity projectActivity;
	
	private boolean customer = false;
	
	private Integer surveyResponse;
	private String comments;
	private User user;
	
	private boolean deleted = false;
	
	public MarketingActionTarget copy(Target target) {
		this.setScope(target.getScope());
		this.setStatus(target.getStatus());
		this.setAdvertising(target.getAdvertising());
		return super.copy(target, this);
	}

	public Integer getActionTargetId() {
		return actionTargetId;
	}

	public MarketingActionTarget setActionTargetId(Integer actionTargetId) {
		this.actionTargetId = actionTargetId;
		return this;
	}

	public Integer getActionTargetDomain() {
		return domain;
	}

	public MarketingActionTarget setActionTargetDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public MarketingAction getMarketingAction() {
		return marketingAction;
	}

	public MarketingActionTarget setMarketingAction(MarketingAction marketingAction) {
		this.marketingAction = marketingAction;
		return this;
	}

	public Byte getActionTargetStatus() {
		return actionTargetStatus;
	}

	public MarketingActionTarget setActionTargetStatus(Byte actionTargetStatus) {
		this.actionTargetStatus = actionTargetStatus;
		return this;
	}

	public Integer getSurveyResponse() {
		return surveyResponse;
	}

	public MarketingActionTarget setSurveyResponse(Integer surveyResponse) {
		this.surveyResponse = surveyResponse;
		return this;
	}

	public String getComments() {
		return comments;
	}

	public MarketingActionTarget setComments(String comments) {
		this.comments = comments;
		return this;
	}

	public User getUser() {
		return user;
	}

	public MarketingActionTarget setUser(User user) {
		this.user = user;
		return this;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public MarketingActionTarget setDeleted(boolean deleted) {
		this.deleted = deleted;
		return this;
	}

	public Boolean hasProjectCommercial() {
		return project != null;
	}
	
	public MarketingActionTarget setProject(Project project) {
		this.project = project;
		return this;
	}
	
	public Project getProject() {
		return project;
	}
	
	public MarketingActionTarget setProjectActivity(ProjectActivity projectActivity) {
		this.projectActivity = projectActivity;
		return this;
	}
	
	public ProjectActivity getProjectActivity() {
		return projectActivity;
	}
	
	public MarketingActionTarget setCustomer(boolean customer) {
		this.customer = customer;
		return this;
	}
	
	public boolean isCustomer() {
		return customer;
	}
	
}
