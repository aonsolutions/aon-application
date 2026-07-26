package com.esferalia.aon.occam.api.model.scope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserScopeAssign implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private ArrayList<UserScopeFull> userScopes;
	private Integer sellerUserOwner;
	private Integer sellerUserNewOwner;
	private Date assginDate;
	
	public UserScopeAssign() {
		// TODO Auto-generated constructor stub
	}

	public List<UserScopeFull> getUserScopes() {
		return userScopes;
	}

	public UserScopeAssign setUserScopes(ArrayList<UserScopeFull> userScopes) {
		this.userScopes = userScopes;
		return this;
	}

	public Integer getSellerUserOwner() {
		return sellerUserOwner;
	}

	public UserScopeAssign setSellerUserOwner(Integer sellerUserOwner) {
		this.sellerUserOwner = sellerUserOwner;
		return this;
	}

	public Integer getSellerUserNewOwner() {
		return sellerUserNewOwner;
	}

	public UserScopeAssign setSellerUserNewOwner(Integer sellerUserNewOwner) {
		this.sellerUserNewOwner = sellerUserNewOwner;
		return this;
	}

	public Date getAssginDate() {
		return assginDate;
	}

	public UserScopeAssign setAssginDate(Date assginDate) {
		this.assginDate = assginDate;
		return this;
	}
	
	
	
}
