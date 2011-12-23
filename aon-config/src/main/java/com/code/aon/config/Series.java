package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.SeriesDB;

@Entity
@Table(name="series")
public class Series extends SeriesDB {
	
	private static final long serialVersionUID = 1L;
	
} 
