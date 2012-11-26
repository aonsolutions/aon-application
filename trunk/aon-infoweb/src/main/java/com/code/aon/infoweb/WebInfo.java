package com.code.aon.infoweb;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.WebInfoDB;

@Entity
@Table(name="web_info")
public class WebInfo extends WebInfoDB {
	
	private static final long serialVersionUID = 1L;
	
}