package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ApplicationDB;

@Entity
@Table(name="application")
public class Application extends ApplicationDB {
	
	private static final long serialVersionUID = 1L;
	
} 
