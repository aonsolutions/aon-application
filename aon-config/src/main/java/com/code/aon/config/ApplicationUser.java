package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ApplicationUserDB;

@Entity
@Table(name="application_user")
public class ApplicationUser extends ApplicationUserDB {
	
	private static final long serialVersionUID = 1L;

	public ApplicationUser() {
		setActive(true);
	}

}