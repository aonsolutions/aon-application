package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.WorkGroupDB;

@Entity
@Table(name="workgroup")
public class WorkGroup extends WorkGroupDB {
	
	private static final long serialVersionUID = 1L;
	
} 
