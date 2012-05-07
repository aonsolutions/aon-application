package com.code.aon.admin;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ProfileDB;

@Entity
@Table(name="profile")
public class Profile extends ProfileDB {
	
	private static final long serialVersionUID = 1L;

}