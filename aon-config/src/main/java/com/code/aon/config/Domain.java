package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.DomainDB;

@Entity
@Table(name="domain")
public class Domain extends DomainDB {
	
	private static final long serialVersionUID = 1L;
	
} 
