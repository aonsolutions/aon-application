package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.List;

import com.esferalia.aon.occam.api.model.security.Scope;

public class MarketingCampaign implements Serializable {

	private static final long serialVersionUID = -3274429313022170469L;
	
	private Integer id;
	private Integer domain;
	private boolean active;
	private String description;
	private Scope scope;
	private Double budget;
	
	private List<MarketingAction> actions;
	
	public Integer getId() {
		return id;
	}

	public MarketingCampaign setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public MarketingCampaign setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Boolean isActive() {
		return active;
	}

	public MarketingCampaign setActive(Boolean active) {
		this.active = active;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public MarketingCampaign setDescription(String description) {
		this.description = description;
		return this;
	}

	public Scope getScope() {
		return scope;
	}

	public MarketingCampaign setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	
	public Double getBudget() {
		return budget == null ? 0.00 : budget;
	}
	
	public MarketingCampaign setBudget(Double budget) {
		this.budget = budget;
		return this;
	}

	public void addAction(MarketingAction action) {
		actions.add(action);
	}

	public void setActions(List<MarketingAction> marketingActions) {
		actions = marketingActions;
	}
	
	public List<MarketingAction> getActions() {
		return actions;
	}
	
}
