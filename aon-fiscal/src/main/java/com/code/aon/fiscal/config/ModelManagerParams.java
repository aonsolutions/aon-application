package com.code.aon.fiscal.config;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.config.enumeration.Administration;

public class ModelManagerParams implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private int masterDomain;
	
	private int year;
	private Model model;
	private Administration administration;
	
	private Integer domainId;
	private String domainName;
	private String document;
	private String name;
	private boolean showOnlyConfiguratedModels;
	
	public ModelManagerParams(int masterDomain) {
		this.masterDomain = masterDomain;
	}
	
	public int getMasterDomain() {
		return masterDomain;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public Administration getAdministration() {
		return administration;
	}
	public void setAdministration(Administration administration) {
		this.administration = administration;
	}
	public Integer getDomainId() {
		return domainId;
	}
	public void setDomainId(Integer domainId) {
		this.domainId = domainId;
	}
	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public boolean isShowOnlyConfiguratedModels() {
		return showOnlyConfiguratedModels;
	}

	public void setShowOnlyConfiguratedModels(boolean showOnlyConfiguratedModels) {
		this.showOnlyConfiguratedModels = showOnlyConfiguratedModels;
	}
	
}
