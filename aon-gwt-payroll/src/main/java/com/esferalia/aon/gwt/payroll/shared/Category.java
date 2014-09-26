package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;

import com.esferalia.aon.gwt.common.shared.HasId;

public class Category implements Serializable, HasId<Integer>  {
	
	private Integer id;
	private String level;
	private Integer levelId;
	private String description;
	private Agreement agreement;

	@Override
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getLevel() {
		return level;
	}
	
	public void setLevel(String level) {
		this.level = level;
	}
	
	public Integer getLevelId() {
		return levelId;
	}
	
	public void setLevelId(Integer levelId) {
		this.levelId = levelId;
	}
	
	public Agreement getAgreement() {
		return agreement;
	}
	
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
}
