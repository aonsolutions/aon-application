package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.common.shared.HasDomain;
import com.esferalia.aon.gwt.common.shared.HasId;

public class Agreement implements Serializable, HasId<Integer>, HasDomain<Integer> {

	private int id;
	private Integer domain;
	private String description;

	private int redefined;
	private int employees;
	
	private boolean levelsWithoutCategories;
	private boolean hasContracts;

	@Override
	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean hasEmployees() {
		return employees > 0;
	}

	public void setEmployees(int employees) {
		this.employees = employees;
	}

	public boolean isRedefined() {
		return redefined > 0;
	}

	public void setRedefined(int redefined) {
		this.redefined = redefined;
	}
	
	
	public boolean hasLevelsWithoutCategories() {
		return levelsWithoutCategories;
	}

	public void setLevelsWithoutCategories(boolean levelWithoutCategories) {
		this.levelsWithoutCategories = levelWithoutCategories;
	}
	
	public void setHasContract(boolean hasContract) {
		this.hasContracts = hasContract;
	}
	
	public boolean getHasContract() {
		return this.hasContracts;
	}
	
	// ----------------------------------------------------------------------

	public boolean isSaved(){
		return id > 0;
	}

	public boolean canDelete(){
		return !isSaved();
	}
	


}
