package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.ScopeDB;

@Entity
@Table(name="scope")
public class Scope extends ScopeDB {
	
	private static final long serialVersionUID = 1L;
	
} 
