package com.code.aon.config;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.PayMethodDB;

@Entity
@Table(name="pay_method")
public class PayMethod extends PayMethodDB {
	
	private static final long serialVersionUID = 1L;
	
} 
