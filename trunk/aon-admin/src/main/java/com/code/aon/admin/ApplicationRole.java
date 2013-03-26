package com.code.aon.admin;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ApplicationRoleDB;

@Entity
@Table(name="application_role")
public class ApplicationRole extends ApplicationRoleDB {
	
	private static final long serialVersionUID = 1L;

}