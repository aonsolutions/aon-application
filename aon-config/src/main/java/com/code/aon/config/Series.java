package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.SeriesDB;

@Entity
@Table(name="series")
@Heritable
public class Series extends SeriesDB implements IScopable{
	
	private static final long serialVersionUID = 1L;
	
} 
