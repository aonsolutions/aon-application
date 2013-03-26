package com.code.aon.admin;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ApplicationUserProfileDB;

@Entity
@Table(name="application_user_profile")
public class ApplicationUserProfile extends ApplicationUserProfileDB {
	
	private static final long serialVersionUID = 1L;

}