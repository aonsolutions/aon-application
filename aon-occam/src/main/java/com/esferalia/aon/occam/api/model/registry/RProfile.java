package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public class RProfile implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String questionAlias;
	private String value;
	
	public RProfile() {}

	public String getQuestionAlias() {
		return questionAlias;
	}

	public RProfile setQuestionAlias(String questionAlias) {
		this.questionAlias = questionAlias;
		return this;
	}

	public String getValue() {
		return value;
	}

	public RProfile setValue(String value) {
		this.value = value;
		return this;
	}

}
