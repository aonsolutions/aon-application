package com.esferalia.aon.occam.api.model.scope;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;

@SuppressWarnings("serial")
public class UserScopeFull implements Serializable {

	private boolean selected;
	
	Integer id;
	Integer domain;
	User user;
	Scope scope;
	
	Date startDate;
	Date endDate;
	User owner;
	
	String createdBy;
	Date createdDate;
	String modifiedBy;
	Date modifiedDate;

	public UserScopeFull() { }

	public Integer getDomain() {
		return domain;
	}

	public UserScopeFull setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}

	public Integer getId() {
		return id;
	}

	public UserScopeFull setId(Integer id) {
		this.id = id;
		return this;
	}

	public User getUser() {
		return user;
	}

	public UserScopeFull setUser(User user) {
		this.user = user;
		return this;
	}

	public Scope getScope() {
		return scope;
	}

	public UserScopeFull setScope(Scope scope) {
		this.scope = scope;
		return this;
	}
	
	public Date getStartDate() {
		return startDate;
	}

	public UserScopeFull setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public UserScopeFull setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}

	public User getOwner() {
		return owner;
	}

	public UserScopeFull setOwner(User owner) {
		this.owner = owner;
		return this;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public UserScopeFull setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
		return this;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public UserScopeFull setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
		return this;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public UserScopeFull setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
		return this;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public UserScopeFull setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
		return this;
	}
	
	// ---------------------------------------------------------- 

	public boolean isSelected() {
		return selected;
	}
	public UserScopeFull setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	
}
