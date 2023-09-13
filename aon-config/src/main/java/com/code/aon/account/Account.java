package com.code.aon.account;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.AccountDB;

@Entity
@Table(name="account")
@Heritable
public class Account extends AccountDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public Account() {
		setActive(true);
	}
	
	@Transient
	public String getFullDescription() {
		return (getCode() + " " + getDescription());
	}
	
} 
