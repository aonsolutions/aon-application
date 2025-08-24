package com.esferalia.aon.occam.api.model.project;

import java.io.Serializable;

public class TasItem implements Serializable {

	private static final long serialVersionUID = -5470203751484710521L;

	private Integer domain;
	
	private String publicCode;
	private String privateCode;
	private String description;
	
	private String modelName;
	private String makeName;
	
	public TasItem() {
		super();
	}

	public Integer getDomain() {
		return domain;
	}

	public TasItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public String getPublicCode() {
		return publicCode;
	}

	public TasItem setPublicCode(String publicCode) {
		this.publicCode = publicCode;
		return this;
	}

	public String getPrivateCode() {
		return privateCode;
	}

	public TasItem setPrivateCode(String privateCode) {
		this.privateCode = privateCode;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public TasItem setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getModelName() {
		return modelName;
	}

	public TasItem setModelName(String modelName) {
		this.modelName = modelName;
		return this;
	}

	public String getMakeName() {
		return makeName;
	}

	public TasItem setMakeName(String makeName) {
		this.makeName = makeName;
		return this;
	}
	
}
