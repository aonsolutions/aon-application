package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.common.shared.HasDomain;
import com.esferalia.aon.gwt.common.shared.HasId;
import com.esferalia.aon.gwt.payroll.shared.Agreement.Level;

public class Agreement implements Serializable, HasId<Integer>, HasDomain<Integer> {

	
	public static class Level implements Serializable, HasId<Integer>, HasDomain<Integer> {
		
		private Integer id;
		private Integer domain;
		private String description;
	
		@Override
		public Integer getId() {
			return id;
		}
	
		public void setId(Integer id) {
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
	
		@Override
		public int hashCode() {
			return id;
		}
	
		@Override
		public boolean equals(Object obj) {
			return obj instanceof Level && id.equals(((Level) obj).id);
		}
	
	}

	private int id;
	private Integer domain;
	private String description;

	private int redefined;
	private int employees;
	
	private boolean levelsWithoutCategories;
	private boolean hasContracts;

	private Set<Level> levels;
	private Map<Integer, Set<String>> categories;
	
	
	
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
	
	public Set<Level> getLevels() {
		return levels != null ? levels : Collections.<Level>emptySet();
	}

	public void setLevels(Set<Level> levels) {
		this.levels = levels;
	}
	
	public Map<Integer, Set<String>> getCategoriesMap() {
		return categories != null ? categories : Collections.<Integer, Set<String>>emptyMap();
	}

	public void setCategoriesMap(Map<Integer, Set<String>> categories) {
		this.categories = categories;
	}

	// ----------------------------------------------------------------------

	public boolean isSaved(){
		return id > 0;
	}

	public boolean canDelete(){
		return !isSaved();
	}
	


}
