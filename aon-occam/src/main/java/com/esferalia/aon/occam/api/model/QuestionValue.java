package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;

public class QuestionValue implements Serializable {

	private static final long serialVersionUID = -3274429313022170469L;
	
	private Integer id;
	private Integer domain;
	private Question question;
	private String valueText;
	private Double valueNumber;
	private Date valueDate;
	
	private boolean deleted = false;
	
	public Integer getId() {
		return id;
	}
	public QuestionValue setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public QuestionValue setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public Question getQuestion() {
		return question;
	}
	public QuestionValue setQuestion(Question question) {
		this.question = null == question ? new Question() : question;
		return this;
	}
	public String getValueText() {
		return valueText;
	}
	public QuestionValue setValueText(String valueText) {
		this.valueText = valueText;
		return this;
	}
	public Double getValueNumber() {
		return valueNumber;
	}
	public QuestionValue setValueNumber(Double valueNumber) {
		this.valueNumber = valueNumber;
		return this;
	}
	public Date getValueDate() {
		return valueDate;
	}
	public QuestionValue setValueDate(Date valueDate) {
		this.valueDate = valueDate;
		return this;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
}
