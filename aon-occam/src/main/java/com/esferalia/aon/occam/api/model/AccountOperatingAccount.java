package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.AccountOperatingReport.AccountOperatingStatementType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class AccountOperatingAccount implements Serializable {

	private static final long serialVersionUID = -3524946313690250917L;

	private AccountOperatingStatementType type; 
	private Integer id;
	private String code;
	private String description;
	
	public AccountOperatingStatementType getType() {
		return type;
	}
	public AccountOperatingAccount setType(AccountOperatingStatementType type) {
		this.type = type;
		return this;
	}
	
	public Integer getId() {
		return id;
	}
	public AccountOperatingAccount setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public String getCode() {
		return code;
	}
	public AccountOperatingAccount setCode(String code) {
		this.code = code;
		return this;
	}

	public String getDescription() {
		return description;
	}
	public AccountOperatingAccount setDescription(String description) {
		this.description = description;
		return this;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
	    hash = 31 * hash + (getType() == null ? 0 : getType().hashCode());
	    hash = 31 * hash + (getId() == null ? 0 : getId().hashCode());
	    hash = 31 * hash + (getCode() == null ? 0 : getCode().hashCode());
	    hash = 31 * hash + (getDescription() == null ? 0 : getDescription().hashCode());
	    return hash;		
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof AccountOperatingAccount)) return false;
		AccountOperatingAccount other = (AccountOperatingAccount) obj;
		return type  == other.getType()
			&& AonNumberUtils.equals(getId(), other.getId())
			&& AonStringUtils.equals(getCode(), other.getCode())
//			&& AonStringUtils.equals(getDescription(), other.getDescription())
				;
	}
	
}
