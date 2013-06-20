package com.code.aon.account;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.AccountDB;

@Entity
@Table(name="account")
@Heritable
public class Account extends AccountDB {
	
	private static final long serialVersionUID = 1L;
	
	public Account() {
		setActive(true);
	}
	
	@Transient
	public String getFullDescription() {
		return (getCode() + " " + getDescription());
	}
	
} 
