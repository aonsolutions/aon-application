package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.esferalia.aon.occam.api.model.registry.QuestionType;

public class Question implements Serializable {

	private static final long serialVersionUID = -3274429313022170469L;
	
	private Integer id;
	private Integer domain;
	private Boolean active;
	private String text;
	private QuestionType type;
	private String argument;
	private String alias;
	
	private boolean survey;
	private List<String> surveyDescriptions;
	
	private boolean rprofile;
	private List<String> rprofileNames;
	
	private List<QuestionValue> values;

	public Integer getId() {
		return id;
	}

	public Question setId(Integer id) {
		this.id = id;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}

	public Question setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Boolean isActive() {
		return active == null ? false : active;
	}

	public Question setActive(Boolean active) {
		this.active = active;
		return this;
	}

	public String getText() {
		return text;
	}

	public Question setText(String text) {
		this.text = text;
		return this;
	}

	public QuestionType getType() {
		return type;
	}

	public Question setType(QuestionType type) {
		this.type = type;
		return this;
	}

	public String getArgument() {
		return argument;
	}

	public Question setArgument(String argument) {
		this.argument = argument;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public Question setAlias(String alias) {
		this.alias = alias;
		return this;
	}
	
	public boolean hasSurvey() {
		return survey;
	}

	public Question setHasSurvey(boolean survey) {
		this.survey = survey;
		return this;
	}

	public List<String> getSurveyDescriptions() {
		return null == surveyDescriptions ? Collections.emptyList() : surveyDescriptions;
	}

	public void setSurveyDescriptions(List<String> surveyDescriptions) {
		this.surveyDescriptions = surveyDescriptions;
	}

	public boolean hasRprofile() {
		return rprofile;
	}

	public Question setRprofile(boolean rprofile) {
		this.rprofile = rprofile;
		return this;
	}

	public List<String> getRprofileNames() {
		return null == rprofileNames ? Collections.emptyList() : rprofileNames;
	}

	public void setRprofileNames(List<String> rprofileNames) {
		this.rprofileNames = rprofileNames;
	}

	public List<QuestionValue> getValues() {
		return null == values || values.isEmpty() ? new ArrayList<>() : values;
	}

	public Question setValues(List<QuestionValue> values) {
		this.values = null == values || values.isEmpty() ? new ArrayList<>() : values;
		return this;
	}

	public void addValue(QuestionValue questionValue) {
		if(null == values || values.isEmpty()) this.values = new ArrayList<>();
		this.values.add(questionValue);
	}

	
}
