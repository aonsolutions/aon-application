package com.code.aon.account;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.esferalia.aon.entity.master.AccountDB;

@Entity
@Table(name="account")
public class Account extends AccountDB {
	
	private static final long serialVersionUID = 1L;
	
	@Transient
	public String getFullDescription() {
		return (getCode() + " " + getDescription());
	}
	
} 
