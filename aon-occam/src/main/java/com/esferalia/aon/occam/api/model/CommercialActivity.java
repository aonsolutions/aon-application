package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CommercialActivity implements Serializable {
	
	Integer domain;
	Integer id;
	String name;
	Integer probability;
	Integer survey;
	
	public Integer getDomain() {
		return domain;
	}
	public CommercialActivity setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Integer getId() {
		return id;
	}
	public CommercialActivity setId(Integer id) {
		this.id = id;
		return this;
	}
	public String getName() {
		return name;
	}
	public CommercialActivity setName(String name) {
		this.name = name;
		return this;
	}
	public Integer getProbability() {
		return probability;
	}
	public CommercialActivity setProbability(Integer probability) {
		this.probability = probability;
		return this;
	}
	public Integer getSurvey() {
		return survey;
	}
	public CommercialActivity setSurvey(Integer survey) {
		this.survey = survey;
		return this;
	}
}
