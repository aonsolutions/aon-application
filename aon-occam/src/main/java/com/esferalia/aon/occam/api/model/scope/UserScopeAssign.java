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
	
	private Integer sellerOwner;
	private Integer sellerNewOwner;
	
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

	public Integer getSellerOwner() {
		return sellerOwner;
	}

	public UserScopeAssign setSellerOwner(Integer sellerOwner) {
		this.sellerOwner = sellerOwner;
		return this;
	}

	public Integer getSellerNewOwner() {
		return sellerNewOwner;
	}

	public UserScopeAssign setSellerNewOwner(Integer sellerNewOwner) {
		this.sellerNewOwner = sellerNewOwner;
		return this;
	}
	
}
