package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.TagDB;

@Entity
@Table(name="tag")
@Heritable
public class Tag extends TagDB {

	private static final long serialVersionUID = 1L;
	
}