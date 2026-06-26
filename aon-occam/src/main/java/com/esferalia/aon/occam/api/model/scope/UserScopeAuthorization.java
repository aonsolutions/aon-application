package com.esferalia.aon.occam.api.model.scope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserScopeAuthorization implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<UserScopeFull> userScopes;
	private Integer authorizationId;
	private Date startDate;
	private Date endDate;
	
	public UserScopeAuthorization() {
		// TODO Auto-generated constructor stub
	}

	public List<UserScopeFull> getUserScopes() {
		return userScopes;
	}

	public UserScopeAuthorization setUserScopes(ArrayList<UserScopeFull> userScopes) {
		this.userScopes = userScopes;
		return this;
	}

	public Integer getAuthorizationId() {
		return authorizationId;
	}

	public UserScopeAuthorization setAuthorizationId(Integer authorizationId) {
		this.authorizationId = authorizationId;
		return this;
	}

	public Date getStartDate() {
		return startDate;
	}

	public UserScopeAuthorization setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}

	public Date getEndDate() {
		return endDate;
	}

	public UserScopeAuthorization setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	
	
}
